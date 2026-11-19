package br.com.fiap.trem.controller;

import br.com.fiap.trem.bo.EstacaoBO;
import br.com.fiap.trem.model.Estacao;
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
@RequestMapping("/estacoes")
public class EstacaoController {

    private final EstacaoBO bo;

    public EstacaoController(EstacaoBO bo) {
        this.bo = bo;
    }

    @GetMapping
    public List<Estacao> listar() throws SQLException {
        return bo.listar();
    }

    @GetMapping("/{codigo}")
    public Estacao buscar(@PathVariable("codigo") int codigo) throws SQLException {
        return bo.buscar(codigo);
    }

    @PostMapping
    public ResponseEntity<Estacao> cadastrar(@RequestBody Estacao corpo) throws SQLException {
        Estacao criado = bo.cadastrar(corpo);
        URI local = URI.create("/estacoes/" + criado.getCodigo());
        return ResponseEntity.created(local).body(criado);
    }

    @PutMapping("/{codigo}")
    public Estacao atualizar(@PathVariable("codigo") int codigo, @RequestBody Estacao corpo) throws SQLException {
        return bo.atualizar(codigo, corpo);
    }

    @DeleteMapping("/{codigo}")
    public ResponseEntity<Void> excluir(@PathVariable("codigo") int codigo) throws SQLException {
        bo.excluir(codigo);
        return ResponseEntity.noContent().build();
    }
}
