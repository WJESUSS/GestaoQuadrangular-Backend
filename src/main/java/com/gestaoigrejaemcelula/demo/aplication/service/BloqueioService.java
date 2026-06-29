package com.gestaoigrejaemcelula.demo.aplication.service;

import com.gestaoigrejaemcelula.demo.domain.entity.NumeroBloqueado;
import com.gestaoigrejaemcelula.demo.domain.repository.NumeroBloqueadoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class BloqueioService {

    private final NumeroBloqueadoRepository repository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${whatsapp.api.phone-number-id:}")
    private String phoneNumberId;

    @Value("${whatsapp.api.access-token:}")
    private String accessToken;

    @Value("${whatsapp.api.version:v23.0}")
    private String apiVersion;

    public boolean isBloqueado(String numero) {
        if (numero == null || numero.isBlank()) return false;
        return repository.existsByNumero(normalizar(numero));
    }

    public BloqueioResultado bloquear(String numero, String motivo) {
        String num = normalizar(numero);
        NumeroBloqueado nb = repository.findByNumero(num).orElseGet(NumeroBloqueado::new);
        nb.setNumero(num);
        nb.setMotivo(motivo);
        repository.save(nb);

        boolean metaOk = tentarChamarMeta(num, HttpMethod.POST, "bloquear");
        return metaOk ? BloqueioResultado.sucesso() : BloqueioResultado.apenasLocal();
    }

    @Transactional
    public BloqueioResultado desbloquear(String numero) {
        String num = normalizar(numero);
        repository.deleteByNumero(num);

        boolean metaOk = tentarChamarMeta(num, HttpMethod.DELETE, "desbloquear");
        return metaOk ? BloqueioResultado.sucesso() : BloqueioResultado.apenasLocal();
    }

    public Page<NumeroBloqueado> listar(Pageable pageable) {
        return repository.findAllByOrderByBloqueadoEmDesc(pageable);
    }

    // Retorna true se a Meta respondeu OK, false se falhou
    private boolean tentarChamarMeta(String numero, HttpMethod method, String acao) {
        if (phoneNumberId.isBlank() || accessToken.isBlank()) {
            log.info("Credenciais Meta não configuradas — operação só local para {}.", numero);
            return false;
        }
        try {
            String url = "https://graph.facebook.com/" + apiVersion + "/" + phoneNumberId + "/block_users";

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = Map.of(
                    "messaging_product", "whatsapp",
                    "block_users", List.of(Map.of("user", numero))
            );

            restTemplate.exchange(url, method, new HttpEntity<>(body, headers), String.class);
            log.info("Número {} {} na Meta com sucesso.", numero, acao.equals("bloquear") ? "bloqueado" : "desbloqueado");
            return true;
        } catch (RestClientException e) {
            log.warn("Não foi possível {} {} na Meta. Causa provável: janela de 24h expirada. Erro: {}",
                    acao, numero, e.getMessage());
            return false;
        }
    }

    private String normalizar(String numero) {
        if (numero == null) return null;
        String digitos = numero.replaceAll("\\D", "");
        if (digitos.length() == 13 && digitos.startsWith("55")) {
            digitos = digitos.substring(0, 4) + digitos.substring(5);
        }
        return digitos;
    }
}