package br.com.fiap.trem.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Bilhete {

    private Integer codigo;
    private BigDecimal valor;
    private LocalDateTime dataCompra;
    private String situacao;
    private Integer codigoViagem;
    private Integer codigoPassageiro;
    private Integer codigoEstacaoOrigem;
    private Integer codigoEstacaoDestino;

    public Bilhete() {
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public LocalDateTime getDataCompra() {
        return dataCompra;
    }

    public void setDataCompra(LocalDateTime dataCompra) {
        this.dataCompra = dataCompra;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public Integer getCodigoViagem() {
        return codigoViagem;
    }

    public void setCodigoViagem(Integer codigoViagem) {
        this.codigoViagem = codigoViagem;
    }

    public Integer getCodigoPassageiro() {
        return codigoPassageiro;
    }

    public void setCodigoPassageiro(Integer codigoPassageiro) {
        this.codigoPassageiro = codigoPassageiro;
    }

    public Integer getCodigoEstacaoOrigem() {
        return codigoEstacaoOrigem;
    }

    public void setCodigoEstacaoOrigem(Integer codigoEstacaoOrigem) {
        this.codigoEstacaoOrigem = codigoEstacaoOrigem;
    }

    public Integer getCodigoEstacaoDestino() {
        return codigoEstacaoDestino;
    }

    public void setCodigoEstacaoDestino(Integer codigoEstacaoDestino) {
        this.codigoEstacaoDestino = codigoEstacaoDestino;
    }
}
