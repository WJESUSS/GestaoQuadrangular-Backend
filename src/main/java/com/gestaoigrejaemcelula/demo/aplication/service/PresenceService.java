package com.gestaoigrejaemcelula.demo.aplication.service;

import com.gestaoigrejaemcelula.demo.aplication.dto.UsuarioOnlineDTO;
import com.gestaoigrejaemcelula.demo.domain.entity.Usuario;
import com.gestaoigrejaemcelula.demo.domain.repository.UsuarioRepository;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class PresenceService {

    private final Cache<Long, Instant> presencaCache = Caffeine.newBuilder()
            .maximumSize(200)
            .expireAfterWrite(90, TimeUnit.SECONDS)
            .build();

    private final UsuarioRepository usuarioRepository;

    public PresenceService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public void registrarHeartbeat(Long usuarioId) {
        presencaCache.put(usuarioId, Instant.now());
    }

    public List<UsuarioOnlineDTO> listarOnline() {
        List<Long> ids = new ArrayList<>(presencaCache.asMap().keySet());

        if (ids.isEmpty()) {
            return List.of();
        }

        List<Usuario> usuarios = usuarioRepository.findAllById(ids);

        return usuarios.stream()
                .filter(Usuario::isAtivo)
                .map(u -> new UsuarioOnlineDTO(u, presencaCache.getIfPresent(u.getId())))
                .toList();
    }

    public int contarOnline() {
        return presencaCache.asMap().size();
    }
}
