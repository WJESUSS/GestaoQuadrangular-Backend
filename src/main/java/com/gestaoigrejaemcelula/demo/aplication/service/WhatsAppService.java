package com.gestaoigrejaemcelula.demo.aplication.service;

import com.fasterxml.jackson.databind.ObjectMapper;
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

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public void enviarTemplate(String numeroDestino, String templateName, String idiomaCode, String... parametros) {
        if (token == null || token.isBlank() || phoneNumberId == null || phoneNumberId.isBlank()) {
            log.warn("WhatsApp não configurado: token ou phone-number-id vazios. Mensagem não enviada para {}", numeroDestino);
            return;
        }

        String numeroLimpo = numeroDestino.replaceAll("\\D", "");
        String url = "https://graph.facebook.com/" + version + "/" + phoneNumberId + "/messages";

        try {
            Map<String, Object> body = new HashMap<>();
            body.put("messaging_product", "whatsapp");
            body.put("to", numeroLimpo);
            body.put("type", "template");

            Map<String, Object> template = new HashMap<>();
            template.put("name", templateName);

            Map<String, String> language = new HashMap<>();
            language.put("code", idiomaCode);
            template.put("language", language);

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

                List<Map<String, Object>> components = new ArrayList<>();
                components.add(component);
                template.put("components", components);
            }

            body.put("template", template);

            String json = objectMapper.writeValueAsString(body);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                log.info("WhatsApp template '{}' enviado com sucesso para {}", templateName, numeroLimpo);
            } else {
                log.error("Erro ao enviar WhatsApp para {}: status={}, resposta={}", numeroLimpo, response.statusCode(), response.body());
            }
        } catch (Exception e) {
            log.error("Exceção ao enviar WhatsApp para {}: {}", numeroLimpo, e.getMessage(), e);
        }
    }
}
