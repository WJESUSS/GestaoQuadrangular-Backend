package com.gestaoigrejaemcelula.demo.aplication.service;

import com.gestaoigrejaemcelula.demo.domain.entity.NumeroBloqueado;
import com.gestaoigrejaemcelula.demo.domain.repository.NumeroBloqueadoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Bloqueio de números que mandam mensagem via WhatsApp.
 *
 * Estratégia em duas camadas:
 *  1) Blocklist LOCAL (banco próprio) — efeito imediato, sempre funciona,
 *     é o que o WhatsAppWebhookService consulta antes de salvar/processar.
 *  2) Bloqueio na META (Cloud API) — "bônus": só funciona se o número tiver
 *     mandado mensagem nas últimas 24h. Se falhar, não tem problema: o
 *     bloqueio local já garante que vocês não vão mais processar nada dele.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BloqueioService {

    private final NumeroBloqueadoRepository repository;
    private final RestTemplate restTemplate = new RestTemplate();

    // Configure no application.properties (opcional — se deixar em branco,
    // o serviço simplesmente não tenta bloquear na Meta, só localmente):
    //   whatsapp.api.phone-number-id=...
    //   whatsapp.api.access-token=...
    //   whatsapp.api.version=v23.0
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

    public NumeroBloqueado bloquear(String numero, String motivo) {
        String numeroNormalizado = normalizar(numero);

        NumeroBloqueado nb = repository.findByNumero(numeroNormalizado)
                .orElseGet(NumeroBloqueado::new);
        nb.setNumero(numeroNormalizado);
        nb.setMotivo(motivo);
        NumeroBloqueado salvo = repository.save(nb);

        tentarChamarMeta(numeroNormalizado, HttpMethod.POST, "bloquear");

        return salvo;
    }

    @Transactional
    public void desbloquear(String numero) {
        String numeroNormalizado = normalizar(numero);
        repository.deleteByNumero(numeroNormalizado);
        tentarChamarMeta(numeroNormalizado, HttpMethod.DELETE, "desbloquear");
    }

    public Page<NumeroBloqueado> listar(Pageable pageable) {
        return repository.findAllByOrderByBloqueadoEmDesc(pageable);
    }

    private void tentarChamarMeta(String numero, HttpMethod method, String acao) {
        if (phoneNumberId.isBlank() || accessToken.isBlank()) {
            log.info("whatsapp.api.phone-number-id/access-token não configurados — " +
                    "bloqueio feito só localmente para {}.", numero);
            return;
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
            log.info("Número {} também {} na Meta com sucesso.", numero, acao.equals("bloquear") ? "bloqueado" : "desbloqueado");
        } catch (RestClientException e) {
            // Não interrompe o fluxo: o bloqueio/desbloqueio LOCAL já foi salvo.
            // A causa mais comum de falha aqui é a janela de 24h sem mensagem do número.
            log.warn("Não foi possível {} {} na Meta (bloqueio local mantido). Causa provável: " +
                    "número fora da janela de 24h. Erro: {}", acao, numero, e.getMessage());
        }
    }

    private String normalizar(String numero) {
        return numero == null ? null : numero.replaceAll("\\D", "");
    }
}