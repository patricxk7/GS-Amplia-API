package br.com.fiap.trem.model;

public class LinhaEstacao {

    private Integer codigo;
    private Integer ordem;
    private Integer codigoLinha;
    private Integer codigoEstacao;

    public LinhaEstacao() {
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public Integer getCodigoLinha() {
        return codigoLinha;
    }

    public void setCodigoLinha(Integer codigoLinha) {
        this.codigoLinha = codigoLinha;
    }

    public Integer getCodigoEstacao() {
        return codigoEstacao;
    }

    public void setCodigoEstacao(Integer codigoEstacao) {
        this.codigoEstacao = codigoEstacao;
    }
}
