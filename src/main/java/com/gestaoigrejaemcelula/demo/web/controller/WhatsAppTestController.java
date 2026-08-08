package com.gestaoigrejaemcelula.demo.web.controller;

import com.gestaoigrejaemcelula.demo.aplication.service.LembreteWhatsAppScheduler;
import com.gestaoigrejaemcelula.demo.aplication.service.WhatsAppService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

@RestController
@RequestMapping("/whatsapp/teste")
@RequiredArgsConstructor
public class WhatsAppTestController {

    @Autowired
    private final WhatsAppService whatsAppService;

    @Autowired
    private final LembreteWhatsAppScheduler lembreteWhatsAppScheduler;

    public record EnviarRequest(
            @NotBlank String telefone,
            String template,
            String idioma,
            String[] parametros
    ) {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof EnviarRequest other)) return false;
            return java.util.Objects.equals(telefone, other.telefone)
                    && java.util.Objects.equals(template, other.template)
                    && java.util.Objects.equals(idioma, other.idioma)
                    && Arrays.equals(parametros, other.parametros);
        }

        @Override
        public int hashCode() {
            int result = java.util.Objects.hash(telefone, template, idioma);
            result = 31 * result + Arrays.hashCode(parametros);
            return result;
        }

        @Override
        public String toString() {
            return "EnviarRequest[telefone=%s, template=%s, idioma=%s, parametros=%s]"
                    .formatted(telefone, template, idioma, Arrays.toString(parametros));
        }
    }

    @PostMapping("/enviar")
    public ResponseEntity<Map<String, Object>> enviarTeste(@Valid @RequestBody EnviarRequest req) {
        String tmpl = req.template() != null ? req.template() : "lembrete_relatorio_celula";
        String lang = req.idioma() != null ? req.idioma() : "en";
        String[] params = req.parametros() != null ? req.parametros() : new String[]{"Teste"};

        whatsAppService.enviarTemplate(req.telefone(), tmpl, lang, params);
        return ResponseEntity.ok(Map.of(
                "mensagem", "WhatsApp enviado para " + req.telefone(),
                "template", tmpl,
                "idioma", lang,
                "parametros", (Object) params
        ));
    }

    @PostMapping("/aniversariantes")
    public ResponseEntity<Map<String, Object>> enviarAniversariantesParaPastores() {
        lembreteWhatsAppScheduler.lembrarAniversariantesDoDia();
        return ResponseEntity.ok(Map.of(
                "mensagem", "Fluxo de aniversariantes disparado para os pastores"
        ));
    }
}
