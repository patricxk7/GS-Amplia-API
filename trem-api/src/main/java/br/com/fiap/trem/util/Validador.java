package br.com.fiap.trem.util;

import java.util.List;

/** Regras de validacao reutilizadas pelas classes BO. */
public final class Validador {

    private Validador() {
    }

    /** Remove espacos das pontas; texto vazio vira null. */
    public static String limpar(String valor) {
        if (valor == null) {
            return null;
        }
        String t = valor.trim();
        return t.isEmpty() ? null : t;
    }

    /** Igual a limpar, mas em maiusculas (usado nos codigos de 1 caractere). */
    public static String maiuscula(String valor) {
        String t = limpar(valor);
        return t == null ? null : t.toUpperCase();
    }

    public static void texto(List<String> erros, String campo, String valor, boolean obrigatorio, int max) {
        if (valor == null) {
            if (obrigatorio) {
                erros.add(campo + " e obrigatorio");
            }
            return;
        }
        if (valor.length() > max) {
            erros.add(campo + " deve ter no maximo " + max + " caracteres");
        }
    }

    public static void digitos(List<String> erros, String campo, String valor, boolean obrigatorio, int tamanho) {
        if (valor == null) {
            if (obrigatorio) {
                erros.add(campo + " e obrigatorio");
            }
            return;
        }
        if (!valor.matches("\\d{" + tamanho + "}")) {
            erros.add(campo + " deve ter exatamente " + tamanho + " digitos numericos");
        }
    }

    public static void email(List<String> erros, String campo, String valor, int max) {
        if (valor == null) {
            return;
        }
        if (valor.length() > max || !valor.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            erros.add(campo + " invalido");
        }
    }

    /** Valida codigos de 1 caractere. Se validos for null, so confere o tamanho. */
    public static void codigo(List<String> erros, String campo, String valor, boolean obrigatorio, String validos) {
        if (valor == null) {
            if (obrigatorio) {
                erros.add(campo + " e obrigatorio");
            }
            return;
        }
        if (valor.length() != 1) {
            erros.add(campo + " deve ter exatamente 1 caractere");
        } else if (validos != null && !validos.contains(valor)) {
            erros.add(campo + " invalido. Valores aceitos: " + String.join(", ", validos.split("")));
        }
    }

    public static void obrigatorio(List<String> erros, String campo, Object valor) {
        if (valor == null) {
            erros.add(campo + " e obrigatorio");
        }
    }

    public static void positivo(List<String> erros, String campo, Number valor) {
        if (valor != null && valor.doubleValue() <= 0) {
            erros.add(campo + " deve ser maior que zero");
        }
    }
}
