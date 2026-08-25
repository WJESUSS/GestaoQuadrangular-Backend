package com.gestaoigrejaemcelula.demo.web.handler;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(404, "Não encontrado", ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException ex) {
        String message = ex.getMessage();

        if (message != null && message.contains("Relatório em atraso")) {
            log.info("Tentativa de envio de relatório em atraso: {}", message);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(400, "Relatório em atraso", message, "REPORT_LATE"));
        }

        if (message != null && message.contains("Data futura")) {
            log.info("Tentativa de envio de relatório com data futura: {}", message);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(400, "Data futura não permitida", message, "FUTURE_DATE"));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(400, "Requisição inválida", ex.getMessage()));
    }

    // ✅ ESSENCIAL: aqui é onde o errorCode "DUPLICATE_REPORT" é gerado.
    // Sem isso, o frontend nunca recebe o código e cai no alerta genérico.
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleConflict(IllegalStateException ex) {
        String message = ex.getMessage();
        String userFriendlyMessage = message;

        if (message != null && message.contains("Já existe um relatório")) {
            userFriendlyMessage = "Um relatório já foi enviado para esta data. Acesse o Histórico para editar.";

            log.info("Tentativa de duplicata de relatório: {}", message);

            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse(
                            409,
                            "Relatório duplicado",
                            userFriendlyMessage,
                            "DUPLICATE_REPORT"
                    ));
        }

        if (message != null && message.contains("membro") && message.contains("ausente")) {
            userFriendlyMessage = "Todos os membros ausentes devem ter uma justificativa. Verifique antes de enviar.";

            log.info("Membro ausente sem justificativa: {}", message);

            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse(
                            409,
                            "Justificativa faltante",
                            userFriendlyMessage,
                            "MISSING_JUSTIFICATION"
                    ));
        }

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(409, "Conflito", userFriendlyMessage));
    }

    @ExceptionHandler({AccessDeniedException.class, SecurityException.class})
    public ResponseEntity<ErrorResponse> handleForbidden(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(403, "Acesso negado", "Você não tem permissão para realizar esta ação"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("Erro de validação");

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ErrorResponse(422, "Erro de validação", msg));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        log.warn("Exceção em tempo de execução: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(409, "Conflito", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        log.error("Erro interno não tratado", ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(500, "Erro interno do servidor", "Ocorreu um erro inesperado. Por favor, tente novamente mais tarde."));
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleIntegrityViolation(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("Não é possível excluir este membro pois existem registros vinculados (relatórios de discipulado ou histórico de status).");
    }
    public static class ErrorResponse {
        private int status;
        private String title;
        private String message;
        private String errorCode;

        public ErrorResponse(int status, String title, String message) {
            this.status = status;
            this.title = title;
            this.message = message;
            this.errorCode = null;
        }

        public ErrorResponse(int status, String title, String message, String errorCode) {
            this.status = status;
            this.title = title;
            this.message = message;
            this.errorCode = errorCode;
        }

        public int getStatus() { return status; }
        public String getTitle() { return title; }
        public String getMessage() { return message; }
        public String getErrorCode() { return errorCode; }

        public void setStatus(int status) { this.status = status; }
        public void setTitle(String title) { this.title = title; }
        public void setMessage(String message) { this.message = message; }
        public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
    }
}