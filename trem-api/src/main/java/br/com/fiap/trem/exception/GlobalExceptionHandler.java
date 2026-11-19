package br.com.fiap.trem.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/** Camada de tratamento de excecoes: converte erros em respostas HTTP padronizadas. */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResposta> naoEncontrado(RecursoNaoEncontradoException ex) {
        return montar(HttpStatus.NOT_FOUND, ex.getMessage(), null);
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErroResposta> regraNegocio(RegraNegocioException ex) {
        return montar(HttpStatus.BAD_REQUEST, ex.getMessage(), ex.getErros());
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ErroResposta> erroBanco(SQLException ex) {
        switch (ex.getErrorCode()) {
            case 1: // ORA-00001: unique constraint
                return montar(HttpStatus.CONFLICT,
                        "Ja existe um registro com este valor unico (ex.: CPF, e-mail, CNPJ ou nome)", null);
            case 2292: // ORA-02292: child record found
                return montar(HttpStatus.CONFLICT,
                        "O registro possui outros registros dependentes e nao pode ser excluido", null);
            case 2291: // ORA-02291: parent key not found
                return montar(HttpStatus.BAD_REQUEST,
                        "Referencia invalida: o registro relacionado nao existe ou o codigo informado nao e valido", null);
            case 1400: // ORA-01400: cannot insert NULL
            case 1407: // ORA-01407: cannot update to NULL
                return montar(HttpStatus.BAD_REQUEST, "Um campo obrigatorio nao foi informado", null);
            case 12899: // ORA-12899: value too large for column
                return montar(HttpStatus.BAD_REQUEST, "Valor maior do que o tamanho permitido para a coluna", null);
            default:
                log.error("Erro ao acessar o banco de dados", ex);
                return montar(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao acessar o banco de dados", null);
        }
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResposta> corpoInvalido(HttpMessageNotReadableException ex) {
        return montar(HttpStatus.BAD_REQUEST,
                "Corpo da requisicao invalido. Verifique o JSON e o formato das datas "
                        + "(yyyy-MM-dd ou yyyy-MM-ddTHH:mm:ss)", null);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErroResposta> parametroInvalido(MethodArgumentTypeMismatchException ex) {
        return montar(HttpStatus.BAD_REQUEST, "Parametro invalido: " + ex.getName(), null);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErroResposta> metodoNaoPermitido(HttpRequestMethodNotSupportedException ex) {
        return montar(HttpStatus.METHOD_NOT_ALLOWED, "Metodo HTTP nao permitido para este endereco", null);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErroResposta> enderecoInexistente(NoResourceFoundException ex) {
        return montar(HttpStatus.NOT_FOUND, "Endereco nao encontrado", null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResposta> erroInesperado(Exception ex) {
        log.error("Erro inesperado", ex);
        return montar(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor", null);
    }

    private ResponseEntity<ErroResposta> montar(HttpStatus status, String mensagem, List<String> detalhes) {
        ErroResposta corpo = new ErroResposta(
                LocalDateTime.now(), status.value(), status.getReasonPhrase(), mensagem, detalhes);
        return ResponseEntity.status(status).body(corpo);
    }
}
