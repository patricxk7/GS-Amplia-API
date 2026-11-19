package br.com.fiap.trem.exception;

import java.util.List;

public class RegraNegocioException extends RuntimeException {

    private final List<String> erros;

    public RegraNegocioException(List<String> erros) {
        super("Dados invalidos");
        this.erros = erros;
    }

    public List<String> getErros() {
        return erros;
    }
}
