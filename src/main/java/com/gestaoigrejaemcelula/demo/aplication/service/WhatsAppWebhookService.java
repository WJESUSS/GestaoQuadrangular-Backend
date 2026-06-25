package com.gestaoigrejaemcelula.demo.aplication.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestaoigrejaemcelula.demo.domain.entity.RegistroWebhook;
import com.gestaoigrejaemcelula.demo.domain.repository.RegistroWebhookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WhatsAppWebhookService {

    private final RegistroWebhookRepository repository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void processarPayload(String payloadJson) {
        try {
            JsonNode root = objectMapper.readTree(payloadJson);

            JsonNode entry = root.path("entry").get(0);
            if (entry == null) return;

            JsonNode changes = entry.path("changes").get(0);
            if (changes == null) return;

            JsonNode value = changes.path("value");

            JsonNode statuses = value.path("statuses");
            if (statuses.isArray()) {
                for (JsonNode s : statuses) {
                    salvarRegistro(s, "status");
                }
            }

            JsonNode messages = value.path("messages");
            if (messages.isArray()) {
                for (JsonNode m : messages) {
                    salvarRegistro(m, "mensagem");
                }
            }

        } catch (Exception e) {
            log.error("Erro ao processar webhook WhatsApp: {}", e.getMessage(), e);
        }
    }

    private void salvarRegistro(JsonNode node, String tipo) {
        RegistroWebhook r = new RegistroWebhook();
        r.setTipoEvento(tipo);

        if ("status".equals(tipo)) {
            r.setIdMensagem(node.path("id").asText());
            r.setStatus(node.path("status").asText());
            r.setNumeroDestino(node.path("recipient_id").asText());

        } else {
            r.setIdMensagem(node.path("id").asText());
            r.setNumeroDestino(node.path("from").asText());
            r.setStatus("recebida");

            String tipoMensagem = node.path("type").asText();
            r.setTipoMensagem(tipoMensagem);

            switch (tipoMensagem) {

                case "text" -> {
                    String texto = node.path("text").path("body").asText("");
                    r.setTextoMensagem(texto);
                    log.info("Texto recebido de {}: {}", r.getNumeroDestino(), texto);
                }

                case "template" -> {
                    // nome do template
                    String nomeTemplate = node.path("template").path("name").asText("");

                    // extrai parâmetros do componente body
                    List<String> params = new ArrayList<>();
                    JsonNode components = node.path("template").path("components");
                    for (JsonNode comp : components) {
                        if ("body".equalsIgnoreCase(comp.path("type").asText())) {
                            for (JsonNode param : comp.path("parameters")) {
                                String val = param.path("text").asText("");
                                if (!val.isBlank()) params.add(val);
                            }
                        }
                    }

                    String texto = "[Template: " + nomeTemplate + "]";
                    if (!params.isEmpty()) texto += " | " + String.join(", ", params);
                    r.setTextoMensagem(texto);
                    log.info("Template recebido de {}: {}", r.getNumeroDestino(), texto);
                }

                case "image" -> {
                    String caption = node.path("image").path("caption").asText("");
                    r.setTextoMensagem("[Imagem]" + (caption.isBlank() ? "" : " " + caption));
                }

                case "audio" -> {
                    r.setTextoMensagem("[Áudio]");
                }

                case "video" -> {
                    String caption = node.path("video").path("caption").asText("");
                    r.setTextoMensagem("[Vídeo]" + (caption.isBlank() ? "" : " " + caption));
                }

                case "document" -> {
                    String filename = node.path("document").path("filename").asText("");
                    r.setTextoMensagem("[Documento]" + (filename.isBlank() ? "" : " " + filename));
                }

                case "sticker" -> r.setTextoMensagem("[Sticker]");

                case "location" -> {
                    String name = node.path("location").path("name").asText("");
                    String address = node.path("location").path("address").asText("");
                    r.setTextoMensagem("[Localização]" + (name.isBlank() ? "" : " " + name + (address.isBlank() ? "" : " — " + address)));
                }

                case "contacts" -> {
                    JsonNode contacts = node.path("contacts");
                    String nome = contacts.isArray() && contacts.size() > 0
                            ? contacts.get(0).path("name").path("formatted_name").asText("")
                            : "";
                    r.setTextoMensagem("[Contato]" + (nome.isBlank() ? "" : " " + nome));
                }

                case "reaction" -> {
                    String emoji = node.path("reaction").path("emoji").asText("");
                    r.setTextoMensagem("[Reação] " + emoji);
                }

                default -> {
                    r.setTextoMensagem("[Tipo: " + tipoMensagem + "]");
                    log.warn("Tipo não tratado: {}", tipoMensagem);
                }
            }
        }

        r.setPayload(node.toString());
        repository.save(r);
        log.info("Webhook {} registrado: status={}, para={}", tipo, r.getStatus(), r.getNumeroDestino());
    }

}