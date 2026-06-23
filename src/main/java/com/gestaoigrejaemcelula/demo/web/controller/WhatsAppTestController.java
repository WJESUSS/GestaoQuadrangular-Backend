package com.gestaoigrejaemcelula.demo.web.controller;

import com.gestaoigrejaemcelula.demo.aplication.service.WhatsAppService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/whatsapp/teste")
@RequiredArgsConstructor
public class WhatsAppTestController {

    private final WhatsAppService whatsAppService;

    public record EnviarRequest(
            @NotBlank String telefone,
            String template,
            String idioma,
            String[] parametros
    ) {}

    @PostMapping("/enviar")
    public ResponseEntity<Map<String, Object>> enviarTeste(@Valid @RequestBody EnviarRequest req) {
        String tmpl = req.template() != null ? req.template() : "lembrete_relatorio_celula";
        String lang = req.idioma() != null ? req.idioma() : "pt_BR";
        String[] params = req.parametros() != null ? req.parametros() : new String[]{"Teste"};

        whatsAppService.enviarTemplate(req.telefone(), tmpl, lang, params);
        return ResponseEntity.ok(Map.of(
                "mensagem", "WhatsApp enviado para " + req.telefone(),
                "template", tmpl,
                "idioma", lang,
                "parametros", (Object) params
        ));
    }
}
