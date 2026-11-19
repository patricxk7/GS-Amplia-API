package br.com.fiap.trem.controller;

import br.com.fiap.trem.bo.SituacaoTremBO;
import br.com.fiap.trem.model.SituacaoTrem;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/situacoes-trem")
public class SituacaoTremController {

    private final SituacaoTremBO bo;

    public SituacaoTremController(SituacaoTremBO bo) {
        this.bo = bo;
    }

    @GetMapping
    public List<SituacaoTrem> listar() throws SQLException {
        return bo.listar();
    }

    @GetMapping("/{codigo}")
    public SituacaoTrem buscar(@PathVariable("codigo") String codigo) throws SQLException {
        return bo.buscar(codigo);
    }

    @PostMapping
    public ResponseEntity<SituacaoTrem> cadastrar(@RequestBody SituacaoTrem corpo) throws SQLException {
        SituacaoTrem criado = bo.cadastrar(corpo);
        URI local = URI.create("/situacoes-trem/" + criado.getCodigo());
        return ResponseEntity.created(local).body(criado);
    }

    @PutMapping("/{codigo}")
    public SituacaoTrem atualizar(@PathVariable("codigo") String codigo, @RequestBody SituacaoTrem corpo) throws SQLException {
        return bo.atualizar(codigo, corpo);
    }

    @DeleteMapping("/{codigo}")
    public ResponseEntity<Void> excluir(@PathVariable("codigo") String codigo) throws SQLException {
        bo.excluir(codigo);
        return ResponseEntity.noContent().build();
    }
}
