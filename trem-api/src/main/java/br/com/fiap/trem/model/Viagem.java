package br.com.fiap.trem.model;

import java.time.LocalDateTime;

public class Viagem {

    private Integer codigo;
    private LocalDateTime dataHoraPartida;
    private LocalDateTime dataHoraChegada;
    private String situacao;
    private Integer codigoTrem;

    public Viagem() {
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public LocalDateTime getDataHoraPartida() {
        return dataHoraPartida;
    }

    public void setDataHoraPartida(LocalDateTime dataHoraPartida) {
        this.dataHoraPartida = dataHoraPartida;
    }

    public LocalDateTime getDataHoraChegada() {
        return dataHoraChegada;
    }

    public void setDataHoraChegada(LocalDateTime dataHoraChegada) {
        this.dataHoraChegada = dataHoraChegada;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public Integer getCodigoTrem() {
        return codigoTrem;
    }

    public void setCodigoTrem(Integer codigoTrem) {
        this.codigoTrem = codigoTrem;
    }
}
