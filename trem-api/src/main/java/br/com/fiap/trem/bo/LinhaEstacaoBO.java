package br.com.fiap.trem.bo;

import br.com.fiap.trem.dao.LinhaEstacaoDAO;
import br.com.fiap.trem.dao.EstacaoDAO;
import br.com.fiap.trem.dao.LinhaDAO;
import br.com.fiap.trem.exception.RecursoNaoEncontradoException;
import br.com.fiap.trem.exception.RegraNegocioException;
import br.com.fiap.trem.model.LinhaEstacao;
import br.com.fiap.trem.util.Validador;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class LinhaEstacaoBO {

    private final LinhaEstacaoDAO dao;
    private final EstacaoDAO estacaoDAO;
    private final LinhaDAO linhaDAO;

    public LinhaEstacaoBO(LinhaEstacaoDAO dao, EstacaoDAO estacaoDAO, LinhaDAO linhaDAO) {
        this.dao = dao;
        this.estacaoDAO = estacaoDAO;
        this.linhaDAO = linhaDAO;
    }

    public List<LinhaEstacao> listar() throws SQLException {
        return dao.listar();
    }

    public LinhaEstacao buscar(int codigo) throws SQLException {
        LinhaEstacao x = dao.buscarPorId(codigo);
        if (x == null) {
            throw new RecursoNaoEncontradoException("LinhaEstacao com codigo " + codigo + " nao encontrado(a)");
        }
        return x;
    }

    public LinhaEstacao cadastrar(LinhaEstacao x) throws SQLException {
        x.setCodigo(null);
        validar(x);
        x.setCodigo(dao.inserir(x));
        return x;
    }

    public LinhaEstacao atualizar(int codigo, LinhaEstacao x) throws SQLException {
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

    private void validar(LinhaEstacao x) throws SQLException {

        List<String> erros = new ArrayList<>();
        Validador.obrigatorio(erros, "ordem", x.getOrdem());
        Validador.positivo(erros, "ordem", x.getOrdem());
        Validador.obrigatorio(erros, "codigoLinha", x.getCodigoLinha());
        Validador.obrigatorio(erros, "codigoEstacao", x.getCodigoEstacao());

        if (x.getCodigoLinha() != null && linhaDAO.buscarPorId(x.getCodigoLinha()) == null) {
            erros.add("codigoLinha " + x.getCodigoLinha() + " nao existe");
        }
        if (x.getCodigoEstacao() != null && estacaoDAO.buscarPorId(x.getCodigoEstacao()) == null) {
            erros.add("codigoEstacao " + x.getCodigoEstacao() + " nao existe");
        }

        if (!erros.isEmpty()) {
            throw new RegraNegocioException(erros);
        }
    }
}
