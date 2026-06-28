package com.gestaoigrejaemcelula.demo.web.controller;

import com.gestaoigrejaemcelula.demo.domain.entity.RegistroWebhook;
import com.gestaoigrejaemcelula.demo.domain.repository.RegistroWebhookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Controller exclusivo para o painel admin.
 * Complementa o WhatsAppWebhookController sem tocar nele.
 */
@RestController
@RequestMapping("/webhook/whatsapp")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RegistroWebhookAdminController {

    private final RegistroWebhookRepository repository;

    // ------------------------------------------------------------------
    // GET /webhook/whatsapp/registros/filtrar
    // Parâmetros opcionais: tipoEvento, status, busca
    //
    // Separado do /registros original para não quebrar nada.
    // ------------------------------------------------------------------
    @GetMapping("/registros/filtrar")
    public ResponseEntity<Page<RegistroWebhook>> filtrar(
            @RequestParam(required = false) String tipoEvento,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String busca,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        String ft = (tipoEvento != null && !tipoEvento.isBlank()) ? tipoEvento : null;
        String fs = (status     != null && !status.isBlank())     ? status     : null;
        String fb = (busca      != null && !busca.isBlank())      ? busca      : null;

        return ResponseEntity.ok(repository.filtrar(ft, fs, fb, pageable));
    }

    // ------------------------------------------------------------------
    // GET /webhook/whatsapp/registros/metricas
    // Contagens para os cards do painel
    // ------------------------------------------------------------------
    @GetMapping("/registros/metricas")
    public ResponseEntity<Map<String, Long>> metricas() {
        LocalDateTime h24 = LocalDateTime.now().minusHours(24);

        return ResponseEntity.ok(Map.of(
                "total",      repository.count(),
                "mensagens",  repository.countByTipoEvento("mensagem"),
                "statusEvt",  repository.countByTipoEvento("status"),
                "recebidas",  repository.countByStatus("recebida"),
                "failed",     repository.countByStatus("failed"),
                "ultimas24h", repository.countByRecebidoEmAfter(h24)
        ));
    }
}