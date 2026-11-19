package br.com.fiap.trem.controller;

import br.com.fiap.trem.bo.OperadoraBO;
import br.com.fiap.trem.model.Operadora;
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
@RequestMapping("/operadoras")
public class OperadoraController {

    private final OperadoraBO bo;

    public OperadoraController(OperadoraBO bo) {
        this.bo = bo;
    }

    @GetMapping
    public List<Operadora> listar() throws SQLException {
        return bo.listar();
    }

    @GetMapping("/{codigo}")
    public Operadora buscar(@PathVariable("codigo") int codigo) throws SQLException {
        return bo.buscar(codigo);
    }

    @PostMapping
    public ResponseEntity<Operadora> cadastrar(@RequestBody Operadora corpo) throws SQLException {
        Operadora criado = bo.cadastrar(corpo);
        URI local = URI.create("/operadoras/" + criado.getCodigo());
        return ResponseEntity.created(local).body(criado);
    }

    @PutMapping("/{codigo}")
    public Operadora atualizar(@PathVariable("codigo") int codigo, @RequestBody Operadora corpo) throws SQLException {
        return bo.atualizar(codigo, corpo);
    }

    @DeleteMapping("/{codigo}")
    public ResponseEntity<Void> excluir(@PathVariable("codigo") int codigo) throws SQLException {
        bo.excluir(codigo);
        return ResponseEntity.noContent().build();
    }
}
