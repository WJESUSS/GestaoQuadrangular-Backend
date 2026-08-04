package com.gestaoigrejaemcelula.demo.aplication.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestaoigrejaemcelula.demo.domain.entity.RegistroWebhook;
import com.gestaoigrejaemcelula.demo.domain.repository.RegistroWebhookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class WhatsAppService {

    @Value("${whatsapp.api.token}")
    private String token;

    @Value("${whatsapp.api.phone-number-id}")
    private String phoneNumberId;

    @Value("${whatsapp.api.version:v23.0}")
    private String version;

    private final RegistroWebhookRepository registroRepository;

    private final BloqueioService bloqueioService; // ← adicione final
    private static final String TIPO_TEMPLATE = "template";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    public void enviarTemplate(String numeroDestino, String templateName, String idiomaCode, String... parametros) {
        if (token == null || token.isBlank() || phoneNumberId == null || phoneNumberId.isBlank()) {
            log.warn("WhatsApp não configurado. Mensagem não enviada para {}", numeroDestino);
            return;
        }

        String numeroLimpo = numeroDestino.replaceAll("\\D", "");
        String url = "https://graph.facebook.com/" + version + "/" + phoneNumberId + "/messages";

        try {
            // ── Monta o body ──────────────────────────────────────────
            Map<String, Object> body = new HashMap<>();
            body.put("messaging_product", "whatsapp");
            body.put("to", numeroLimpo);
            body.put("type", TIPO_TEMPLATE);

            Map<String, Object> template = new HashMap<>();
            template.put("name", templateName);

            Map<String, String> language = new HashMap<>();
            language.put("code", idiomaCode);
            template.put("language", language);

            if (bloqueioService.isBloqueado(numeroLimpo)) {
                log.info("Envio cancelado — número bloqueado: {}", numeroLimpo);
                return;
            }

            if (parametros != null && parametros.length > 0) {
                List<Map<String, Object>> parameters = new ArrayList<>();
                for (String param : parametros) {
                    Map<String, Object> paramMap = new HashMap<>();
                    paramMap.put("type", "text");
                    paramMap.put("text", param);
                    parameters.add(paramMap);
                }
                Map<String, Object> component = new HashMap<>();
                component.put("type", "body");
                component.put("parameters", parameters);
                template.put("components", List.of(component));
            }

            body.put(TIPO_TEMPLATE, template);
            String json = objectMapper.writeValueAsString(body);

            // ── Envia para a Meta ─────────────────────────────────────
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(5))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            // Assíncrono: não bloqueia a resposta do relatório esperando a Meta.
            // Timeout de 5s evita travar o request por ~10s quando a API demora/fica indisponível.
            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response ->
                            processarResposta(numeroLimpo, templateName, response, parametros, json))
                    .exceptionally(ex -> {
                        log.error("Exceção ao enviar para {}: {}", numeroLimpo, ex.getMessage(), ex);
                        return null;
                    });

        } catch (Exception e) {
            log.error("Exceção ao enviar para {}: {}", numeroLimpo, e.getMessage(), e);
        }
    }

    private void processarResposta(String numero, String templateName,
                                   HttpResponse<String> response,
                                   String[] parametros, String json) {
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            log.info("Template '{}' enviado para {}", templateName, numero);

            // ── Extrai o wamid da resposta ────────────────────────
            String wamid = null;
            try {
                JsonNode resp = objectMapper.readTree(response.body());
                wamid = resp.path("messages").get(0).path("id").asText(null);
            } catch (Exception ex) {
                log.warn("Não foi possível extrair wamid da resposta: {}", ex.getMessage());
            }

            // ── ✅ Salva o registro no banco ──────────────────────
            salvarRegistroEnvio(numero, templateName, wamid, parametros, json);

        } else {
            log.error("Erro ao enviar para {}: status={}, body={}", numero, response.statusCode(), response.body());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    private void salvarRegistroEnvio(String numero, String templateName, String wamid, String[] parametros, String payloadEnviado) {
        try {
            RegistroWebhook r = new RegistroWebhook();
            r.setTipoEvento("mensagem");
            r.setTipoMensagem(TIPO_TEMPLATE);
            r.setStatus("enviada");
            r.setNumeroDestino(numero);
            r.setIdMensagem(wamid != null ? wamid : "pendente-" + System.currentTimeMillis());

            // Texto legível: [Template: nome] | PARAM1, PARAM2
            String texto = "[Template: " + templateName + "]";
            if (parametros != null && parametros.length > 0) {
                texto += " | " + String.join(", ", parametros);
            }
            r.setTextoMensagem(texto);
            r.setPayload(payloadEnviado);

            registroRepository.save(r);
            log.info("Registro de envio salvo: template={}, para={}", templateName, numero);

        } catch (Exception e) {
            log.error("Erro ao salvar registro de envio: {}", e.getMessage(), e);
        }
    }
}