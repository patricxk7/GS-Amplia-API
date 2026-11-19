package br.com.fiap.trem.bo;

import br.com.fiap.trem.dao.LinhaDAO;
import br.com.fiap.trem.dao.OperadoraDAO;
import br.com.fiap.trem.exception.RecursoNaoEncontradoException;
import br.com.fiap.trem.exception.RegraNegocioException;
import br.com.fiap.trem.model.Linha;
import br.com.fiap.trem.util.Validador;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class LinhaBO {

    private final LinhaDAO dao;
    private final OperadoraDAO operadoraDAO;

    public LinhaBO(LinhaDAO dao, OperadoraDAO operadoraDAO) {
        this.dao = dao;
        this.operadoraDAO = operadoraDAO;
    }

    public List<Linha> listar() throws SQLException {
        return dao.listar();
    }

    public Linha buscar(int codigo) throws SQLException {
        Linha x = dao.buscarPorId(codigo);
        if (x == null) {
            throw new RecursoNaoEncontradoException("Linha com codigo " + codigo + " nao encontrado(a)");
        }
        return x;
    }

    public Linha cadastrar(Linha x) throws SQLException {
        x.setCodigo(null);
        validar(x);
        x.setCodigo(dao.inserir(x));
        return x;
    }

    public Linha atualizar(int codigo, Linha x) throws SQLException {
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

    private void validar(Linha x) throws SQLException {
        x.setNome(Validador.limpar(x.getNome()));
        x.setCor(Validador.limpar(x.getCor()));
        x.setSituacao(Validador.maiuscula(x.getSituacao()));
        x.setTipoModal(Validador.maiuscula(x.getTipoModal()));

        List<String> erros = new ArrayList<>();
        Validador.texto(erros, "nome", x.getNome(), true, 100);
        Validador.texto(erros, "cor", x.getCor(), false, 30);
        Validador.codigo(erros, "situacao", x.getSituacao(), true, "AI");
        Validador.codigo(erros, "tipoModal", x.getTipoModal(), true, null);

        if (x.getCodigoOperadora() != null && operadoraDAO.buscarPorId(x.getCodigoOperadora()) == null) {
            erros.add("codigoOperadora " + x.getCodigoOperadora() + " nao existe");
        }

        if (!erros.isEmpty()) {
            throw new RegraNegocioException(erros);
        }
    }
}
