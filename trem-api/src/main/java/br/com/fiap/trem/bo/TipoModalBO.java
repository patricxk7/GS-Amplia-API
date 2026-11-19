package br.com.fiap.trem.bo;

import br.com.fiap.trem.dao.TipoModalDAO;
import br.com.fiap.trem.exception.RecursoNaoEncontradoException;
import br.com.fiap.trem.exception.RegraNegocioException;
import br.com.fiap.trem.model.TipoModal;
import br.com.fiap.trem.util.Validador;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class TipoModalBO {

    private final TipoModalDAO dao;

    public TipoModalBO(TipoModalDAO dao) {
        this.dao = dao;
    }

    public List<TipoModal> listar() throws SQLException {
        return dao.listar();
    }

    public TipoModal buscar(String codigo) throws SQLException {
        TipoModal x = dao.buscarPorId(Validador.maiuscula(codigo));
        if (x == null) {
            throw new RecursoNaoEncontradoException("Tipo de modal com codigo " + codigo + " nao encontrado(a)");
        }
        return x;
    }

    public TipoModal cadastrar(TipoModal x) throws SQLException {
        validar(x);
        if (dao.buscarPorId(x.getCodigo()) != null) {
            throw new RegraNegocioException(List.of("ja existe um registro com o codigo " + x.getCodigo()));
        }
        dao.inserir(x);
        return x;
    }

    public TipoModal atualizar(String codigo, TipoModal x) throws SQLException {
        buscar(codigo);
        x.setCodigo(codigo);
        validar(x);
        dao.atualizar(x);
        return x;
    }

    public void excluir(String codigo) throws SQLException {
        buscar(codigo);
        dao.excluir(Validador.maiuscula(codigo));
    }

    private void validar(TipoModal x) {
        x.setCodigo(Validador.maiuscula(x.getCodigo()));
        x.setNome(Validador.limpar(x.getNome()));
        x.setDescricao(Validador.limpar(x.getDescricao()));

        List<String> erros = new ArrayList<>();
        Validador.codigo(erros, "codigo", x.getCodigo(), true, null);
        Validador.texto(erros, "nome", x.getNome(), true, 50);
        Validador.texto(erros, "descricao", x.getDescricao(), false, 200);

        if (!erros.isEmpty()) {
            throw new RegraNegocioException(erros);
        }
    }
}
