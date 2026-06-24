package com.gestaoigrejaemcelula.demo.aplication.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestaoigrejaemcelula.demo.domain.entity.RegistroWebhook;
import com.gestaoigrejaemcelula.demo.domain.repository.RegistroWebhookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
        }

        r.setPayload(node.toString());
        repository.save(r);

        log.info("Webhook {} registrado: status={}, para={}", tipo, r.getStatus(), r.getNumeroDestino());
    }
}
