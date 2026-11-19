package br.com.fiap.trem.controller;

import br.com.fiap.trem.bo.TipoModalBO;
import br.com.fiap.trem.model.TipoModal;
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
@RequestMapping("/tipos-modais")
public class TipoModalController {

    private final TipoModalBO bo;

    public TipoModalController(TipoModalBO bo) {
        this.bo = bo;
    }

    @GetMapping
    public List<TipoModal> listar() throws SQLException {
        return bo.listar();
    }

    @GetMapping("/{codigo}")
    public TipoModal buscar(@PathVariable("codigo") String codigo) throws SQLException {
        return bo.buscar(codigo);
    }

    @PostMapping
    public ResponseEntity<TipoModal> cadastrar(@RequestBody TipoModal corpo) throws SQLException {
        TipoModal criado = bo.cadastrar(corpo);
        URI local = URI.create("/tipos-modais/" + criado.getCodigo());
        return ResponseEntity.created(local).body(criado);
    }

    @PutMapping("/{codigo}")
    public TipoModal atualizar(@PathVariable("codigo") String codigo, @RequestBody TipoModal corpo) throws SQLException {
        return bo.atualizar(codigo, corpo);
    }

    @DeleteMapping("/{codigo}")
    public ResponseEntity<Void> excluir(@PathVariable("codigo") String codigo) throws SQLException {
        bo.excluir(codigo);
        return ResponseEntity.noContent().build();
    }
}
