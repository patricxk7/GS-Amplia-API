package br.com.fiap.trem.model;

public class Trem {

    private Integer codigo;
    private String modelo;
    private Integer capacidade;
    private String situacao;
    private Integer codigoLinha;

    public Trem() {
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public Integer getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(Integer capacidade) {
        this.capacidade = capacidade;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public Integer getCodigoLinha() {
        return codigoLinha;
    }

    public void setCodigoLinha(Integer codigoLinha) {
        this.codigoLinha = codigoLinha;
    }
}
