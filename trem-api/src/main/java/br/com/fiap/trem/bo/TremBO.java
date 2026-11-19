package br.com.fiap.trem.bo;

import br.com.fiap.trem.dao.TremDAO;
import br.com.fiap.trem.dao.LinhaDAO;
import br.com.fiap.trem.exception.RecursoNaoEncontradoException;
import br.com.fiap.trem.exception.RegraNegocioException;
import br.com.fiap.trem.model.Trem;
import br.com.fiap.trem.util.Validador;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class TremBO {

    private final TremDAO dao;
    private final LinhaDAO linhaDAO;

    public TremBO(TremDAO dao, LinhaDAO linhaDAO) {
        this.dao = dao;
        this.linhaDAO = linhaDAO;
    }

    public List<Trem> listar() throws SQLException {
        return dao.listar();
    }

    public Trem buscar(int codigo) throws SQLException {
        Trem x = dao.buscarPorId(codigo);
        if (x == null) {
            throw new RecursoNaoEncontradoException("Trem com codigo " + codigo + " nao encontrado(a)");
        }
        return x;
    }

    public Trem cadastrar(Trem x) throws SQLException {
        x.setCodigo(null);
        validar(x);
        x.setCodigo(dao.inserir(x));
        return x;
    }

    public Trem atualizar(int codigo, Trem x) throws SQLException {
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

    private void validar(Trem x) throws SQLException {
        x.setModelo(Validador.limpar(x.getModelo()));
        x.setSituacao(Validador.maiuscula(x.getSituacao()));

        List<String> erros = new ArrayList<>();
        Validador.texto(erros, "modelo", x.getModelo(), false, 50);
        Validador.obrigatorio(erros, "capacidade", x.getCapacidade());
        Validador.positivo(erros, "capacidade", x.getCapacidade());
        Validador.codigo(erros, "situacao", x.getSituacao(), true, null);
        Validador.obrigatorio(erros, "codigoLinha", x.getCodigoLinha());

        if (x.getCodigoLinha() != null && linhaDAO.buscarPorId(x.getCodigoLinha()) == null) {
            erros.add("codigoLinha " + x.getCodigoLinha() + " nao existe");
        }

        if (!erros.isEmpty()) {
            throw new RegraNegocioException(erros);
        }
    }
}
