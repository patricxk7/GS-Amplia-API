package br.com.fiap.trem.bo;

import br.com.fiap.trem.dao.PassageiroDAO;
import br.com.fiap.trem.exception.RecursoNaoEncontradoException;
import br.com.fiap.trem.exception.RegraNegocioException;
import br.com.fiap.trem.model.Passageiro;
import br.com.fiap.trem.util.Validador;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class PassageiroBO {

    private final PassageiroDAO dao;

    public PassageiroBO(PassageiroDAO dao) {
        this.dao = dao;
    }

    public List<Passageiro> listar() throws SQLException {
        return dao.listar();
    }

    public Passageiro buscar(int codigo) throws SQLException {
        Passageiro x = dao.buscarPorId(codigo);
        if (x == null) {
            throw new RecursoNaoEncontradoException("Passageiro com codigo " + codigo + " nao encontrado(a)");
        }
        return x;
    }

    public Passageiro cadastrar(Passageiro x) throws SQLException {
        x.setCodigo(null);
        validar(x);
        x.setCodigo(dao.inserir(x));
        return x;
    }

    public Passageiro atualizar(int codigo, Passageiro x) throws SQLException {
        buscar(codigo);
        x.setCodigo(codigo);
        validar(x);
        dao.atualizar(x);
        return x;
    }

    public void excluir(int codigo) throws SQLException {
        buscar(codigo);
        dao.excluir(codigo);
    }

    private void validar(Passageiro x) throws SQLException {
        x.setNome(Validador.limpar(x.getNome()));
        x.setCpf(Validador.limpar(x.getCpf()));
        x.setEmail(Validador.limpar(x.getEmail()));

        List<String> erros = new ArrayList<>();
        Validador.texto(erros, "nome", x.getNome(), true, 100);
        Validador.digitos(erros, "cpf", x.getCpf(), true, 11);
        Validador.email(erros, "email", x.getEmail(), 100);
        if (x.getDataNascimento() != null && x.getDataNascimento().isAfter(LocalDate.now())) {
            erros.add("dataNascimento nao pode ser uma data futura");
        }


        if (!erros.isEmpty()) {
            throw new RegraNegocioException(erros);
        }
    }
}
