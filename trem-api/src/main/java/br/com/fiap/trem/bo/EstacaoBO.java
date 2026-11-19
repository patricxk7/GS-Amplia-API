package br.com.fiap.trem.bo;

import br.com.fiap.trem.dao.EstacaoDAO;
import br.com.fiap.trem.exception.RecursoNaoEncontradoException;
import br.com.fiap.trem.exception.RegraNegocioException;
import br.com.fiap.trem.model.Estacao;
import br.com.fiap.trem.util.Validador;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class EstacaoBO {

    private final EstacaoDAO dao;

    public EstacaoBO(EstacaoDAO dao) {
        this.dao = dao;
    }

    public List<Estacao> listar() throws SQLException {
        return dao.listar();
    }

    public Estacao buscar(int codigo) throws SQLException {
        Estacao x = dao.buscarPorId(codigo);
        if (x == null) {
            throw new RecursoNaoEncontradoException("Estacao com codigo " + codigo + " nao encontrado(a)");
        }
        return x;
    }

    public Estacao cadastrar(Estacao x) throws SQLException {
        x.setCodigo(null);
        validar(x);
        x.setCodigo(dao.inserir(x));
        return x;
    }

    public Estacao atualizar(int codigo, Estacao x) throws SQLException {
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

    private void validar(Estacao x) throws SQLException {
        x.setNome(Validador.limpar(x.getNome()));
        x.setEndereco(Validador.limpar(x.getEndereco()));
        x.setAcessibilidade(Validador.maiuscula(x.getAcessibilidade()));
        x.setSituacao(Validador.maiuscula(x.getSituacao()));

        List<String> erros = new ArrayList<>();
        Validador.texto(erros, "nome", x.getNome(), true, 100);
        Validador.texto(erros, "endereco", x.getEndereco(), false, 200);
        Validador.codigo(erros, "acessibilidade", x.getAcessibilidade(), true, "SN");
        Validador.codigo(erros, "situacao", x.getSituacao(), true, "AI");


        if (!erros.isEmpty()) {
            throw new RegraNegocioException(erros);
        }
    }
}
