package br.com.fiap.trem.controller;

import br.com.fiap.trem.bo.PassageiroBO;
import br.com.fiap.trem.model.Passageiro;
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
@RequestMapping("/passageiros")
public class PassageiroController {

    private final PassageiroBO bo;

    public PassageiroController(PassageiroBO bo) {
        this.bo = bo;
    }

    @GetMapping
    public List<Passageiro> listar() throws SQLException {
        return bo.listar();
    }

    @GetMapping("/{codigo}")
    public Passageiro buscar(@PathVariable("codigo") int codigo) throws SQLException {
        return bo.buscar(codigo);
    }

    @PostMapping
    public ResponseEntity<Passageiro> cadastrar(@RequestBody Passageiro corpo) throws SQLException {
        Passageiro criado = bo.cadastrar(corpo);
        URI local = URI.create("/passageiros/" + criado.getCodigo());
        return ResponseEntity.created(local).body(criado);
    }

    @PutMapping("/{codigo}")
    public Passageiro atualizar(@PathVariable("codigo") int codigo, @RequestBody Passageiro corpo) throws SQLException {
        return bo.atualizar(codigo, corpo);
    }

    @DeleteMapping("/{codigo}")
    public ResponseEntity<Void> excluir(@PathVariable("codigo") int codigo) throws SQLException {
        bo.excluir(codigo);
        return ResponseEntity.noContent().build();
    }
}
