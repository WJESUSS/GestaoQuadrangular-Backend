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

    private static final String LOCATION = "location";
    private static final String KEY_STATUS = "status";
    private static final String KEY_TEMPLATE = "template";

    private final RegistroWebhookRepository repository;
    private final BloqueioService bloqueioService; // <-- novo
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
                    salvarRegistro(s, KEY_STATUS);
                }
            }

            JsonNode messages = value.path("messages");
            if (messages.isArray()) {
                for (JsonNode m : messages) {
                    String remetente = m.path("from").asText();

                    // Ignora completamente mensagens de números bloqueados.
                    if (bloqueioService.isBloqueado(remetente)) {
                        log.info("Mensagem de número bloqueado ignorada: {}", remetente);
                        continue;
                    }

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

        if (KEY_STATUS.equals(tipo)) {
            preencherStatus(node, r);
        } else {
            preencherMensagem(node, r);
        }

        r.setPayload(node.toString());
        repository.save(r);
        log.info("Webhook {} registrado: status={}, para={}", tipo, r.getStatus(), r.getNumeroDestino());
    }

    private void preencherStatus(JsonNode node, RegistroWebhook r) {
        r.setIdMensagem(node.path("id").asText());
        r.setStatus(node.path(KEY_STATUS).asText());
        r.setNumeroDestino(node.path("recipient_id").asText());
    }

    private void preencherMensagem(JsonNode node, RegistroWebhook r) {
        r.setIdMensagem(node.path("id").asText());
        r.setNumeroDestino(node.path("from").asText());
        r.setStatus("recebida");

        String tipoMensagem = node.path("type").asText();
        r.setTipoMensagem(tipoMensagem);
        r.setTextoMensagem(extrairTextoMensagem(node, tipoMensagem, r.getNumeroDestino()));
    }

    private String extrairTextoMensagem(JsonNode node, String tipoMensagem, String remetente) {
        return switch (tipoMensagem) {
            case "text" -> extrairTexto(node, remetente);
            case KEY_TEMPLATE -> extrairTemplate(node, remetente);
            case "image" -> extrairMedia(node, "image", "Imagem");
            case "video" -> extrairMedia(node, "video", "Vídeo");
            case "document" -> extrairMedia(node, "document", "Documento");
            case "audio" -> "[Áudio]";
            case "sticker" -> "[Sticker]";
            case LOCATION -> extrairLocalizacao(node);
            case "contacts" -> extrairContato(node);
            case "reaction" -> extrairReacao(node);
            default -> {
                log.warn("Tipo não tratado: {}", tipoMensagem);
                yield "[Tipo: " + tipoMensagem + "]";
            }
        };
    }

    private String extrairTexto(JsonNode node, String remetente) {
        String texto = node.path("text").path("body").asText("");
        log.info("Texto recebido de {}: {}", remetente, texto);
        return texto;
    }

    private String extrairTemplate(JsonNode node, String remetente) {
        String nomeTemplate = node.path(KEY_TEMPLATE).path("name").asText("");
        List<String> params = new ArrayList<>();

        JsonNode components = node.path(KEY_TEMPLATE).path("components");
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
        log.info("Template recebido de {}: {}", remetente, texto);
        return texto;
    }

    private String extrairMedia(JsonNode node, String campo, String label) {
        String caption = node.path(campo).path("caption").asText("");
        return "[" + label + "]" + (caption.isBlank() ? "" : " " + caption);
    }

    private String extrairLocalizacao(JsonNode node) {
        String name = node.path(LOCATION).path("name").asText("");
        String address = node.path(LOCATION).path("address").asText("");
        String result = "[Localização]";
        if (!name.isBlank()) result += " " + name;
        if (!address.isBlank()) result += " — " + address;
        return result;
    }

    private String extrairContato(JsonNode node) {
        JsonNode contacts = node.path("contacts");
        String nome = contacts.isArray() && !contacts.isEmpty()
                ? contacts.get(0).path("name").path("formatted_name").asText("")
                : "";
        return "[Contato]" + (nome.isBlank() ? "" : " " + nome);
    }

    private String extrairReacao(JsonNode node) {
        String emoji = node.path("reaction").path("emoji").asText("");
        return "[Reação] " + emoji;
    }

}