package com.gestaoigrejaemcelula.demo.web.controller;

import com.gestaoigrejaemcelula.demo.aplication.dto.UsuarioOnlineDTO;
import com.gestaoigrejaemcelula.demo.aplication.service.PresenceService;
import com.gestaoigrejaemcelula.demo.domain.entity.Usuario;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/presenca")
public class PresenceController {

    private final PresenceService presenceService;

    public PresenceController(PresenceService presenceService) {
        this.presenceService = presenceService;
    }

    @PostMapping("/heartbeat")
    public ResponseEntity<Map<String, Object>> heartbeat() {
        Long usuarioId = ((Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal()).getId();

        presenceService.registrarHeartbeat(usuarioId);

        return ResponseEntity.ok(Map.of(
                "online", presenceService.contarOnline()
        ));
    }

    @GetMapping("/online")
    public ResponseEntity<Map<String, Object>> listarOnline() {
        List<UsuarioOnlineDTO> online = presenceService.listarOnline();

        return ResponseEntity.ok(Map.of(
                "total", online.size(),
                "usuarios", online
        ));
    }
}
