package br.com.fiap.trem.bo;

import br.com.fiap.trem.dao.OperadoraDAO;
import br.com.fiap.trem.exception.RecursoNaoEncontradoException;
import br.com.fiap.trem.exception.RegraNegocioException;
import br.com.fiap.trem.model.Operadora;
import br.com.fiap.trem.util.Validador;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class OperadoraBO {

    private final OperadoraDAO dao;

    public OperadoraBO(OperadoraDAO dao) {
        this.dao = dao;
    }

    public List<Operadora> listar() throws SQLException {
        return dao.listar();
    }

    public Operadora buscar(int codigo) throws SQLException {
        Operadora x = dao.buscarPorId(codigo);
        if (x == null) {
            throw new RecursoNaoEncontradoException("Operadora com codigo " + codigo + " nao encontrado(a)");
        }
        return x;
    }

    public Operadora cadastrar(Operadora x) throws SQLException {
        x.setCodigo(null);
        validar(x);
        x.setCodigo(dao.inserir(x));
        return x;
    }

    public Operadora atualizar(int codigo, Operadora x) throws SQLException {
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

    private void validar(Operadora x) throws SQLException {
        x.setNome(Validador.limpar(x.getNome()));
        x.setCnpj(Validador.limpar(x.getCnpj()));

        List<String> erros = new ArrayList<>();
        Validador.texto(erros, "nome", x.getNome(), true, 100);
        Validador.digitos(erros, "cnpj", x.getCnpj(), false, 14);


        if (!erros.isEmpty()) {
            throw new RegraNegocioException(erros);
        }
    }
}
