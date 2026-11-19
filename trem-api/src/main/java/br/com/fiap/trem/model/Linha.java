package br.com.fiap.trem.model;

public class Linha {

    private Integer codigo;
    private String nome;
    private String cor;
    private String situacao;
    private String tipoModal;
    private Integer codigoOperadora;

    public Linha() {
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public String getTipoModal() {
        return tipoModal;
    }

    public void setTipoModal(String tipoModal) {
        this.tipoModal = tipoModal;
    }

    public Integer getCodigoOperadora() {
        return codigoOperadora;
    }

    public void setCodigoOperadora(Integer codigoOperadora) {
        this.codigoOperadora = codigoOperadora;
    }
}
