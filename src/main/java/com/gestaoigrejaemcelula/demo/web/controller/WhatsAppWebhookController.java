package com.gestaoigrejaemcelula.demo.web.controller;

import com.gestaoigrejaemcelula.demo.aplication.service.WhatsAppWebhookService;
import com.gestaoigrejaemcelula.demo.domain.entity.RegistroWebhook;
import com.gestaoigrejaemcelula.demo.domain.repository.RegistroWebhookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/webhook/whatsapp")
@RequiredArgsConstructor
public class WhatsAppWebhookController {

    private final WhatsAppWebhookService webhookService;
    private final RegistroWebhookRepository registroRepository;

    @Value("${whatsapp.webhook.verify-token:meu_token_seguro}")
    private String verifyToken;

    @GetMapping
    public ResponseEntity<String> verificar(
            @RequestParam(name = "hub.mode", required = false) String mode,
            @RequestParam(name = "hub.verify_token", required = false) String token,
            @RequestParam(name = "hub.challenge", required = false) String challenge) {

        if ("subscribe".equals(mode) && verifyToken.equals(token) && challenge != null) {
            log.info("Webhook verificado com sucesso!");
            return ResponseEntity.ok(challenge);
        }

        if (mode == null && token == null && challenge == null) {
            return ResponseEntity.ok("Webhook ativo");
        }

        return ResponseEntity.status(403).body("Token inválido");
    }

    @PostMapping
    public ResponseEntity<Void> receber(@RequestBody String payload) {
        log.info("Webhook recebido: {} bytes", payload.length());
        webhookService.processarPayload(payload);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/registros")
    public ResponseEntity<Page<RegistroWebhook>> listarRegistros(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(registroRepository.findAllByOrderByRecebidoEmDesc(pageable));
    }
}
