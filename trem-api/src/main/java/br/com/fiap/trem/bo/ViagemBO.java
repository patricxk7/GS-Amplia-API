package br.com.fiap.trem.bo;

import br.com.fiap.trem.dao.ViagemDAO;
import br.com.fiap.trem.dao.TremDAO;
import br.com.fiap.trem.exception.RecursoNaoEncontradoException;
import br.com.fiap.trem.exception.RegraNegocioException;
import br.com.fiap.trem.model.Viagem;
import br.com.fiap.trem.util.Validador;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ViagemBO {

    private final ViagemDAO dao;
    private final TremDAO tremDAO;

    public ViagemBO(ViagemDAO dao, TremDAO tremDAO) {
        this.dao = dao;
        this.tremDAO = tremDAO;
    }

    public List<Viagem> listar() throws SQLException {
        return dao.listar();
    }

    public Viagem buscar(int codigo) throws SQLException {
        Viagem x = dao.buscarPorId(codigo);
        if (x == null) {
            throw new RecursoNaoEncontradoException("Viagem com codigo " + codigo + " nao encontrado(a)");
        }
        return x;
    }

    public Viagem cadastrar(Viagem x) throws SQLException {
        x.setCodigo(null);
        validar(x);
        x.setCodigo(dao.inserir(x));
        return x;
    }

    public Viagem atualizar(int codigo, Viagem x) throws SQLException {
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

    private void validar(Viagem x) throws SQLException {
        x.setSituacao(Validador.maiuscula(x.getSituacao()));

        List<String> erros = new ArrayList<>();
        Validador.obrigatorio(erros, "dataHoraPartida", x.getDataHoraPartida());
        Validador.codigo(erros, "situacao", x.getSituacao(), true, "AFC");
        Validador.obrigatorio(erros, "codigoTrem", x.getCodigoTrem());
        if (x.getDataHoraPartida() != null && x.getDataHoraChegada() != null
                && x.getDataHoraChegada().isBefore(x.getDataHoraPartida())) {
            erros.add("dataHoraChegada nao pode ser anterior a dataHoraPartida");
        }

        if (x.getCodigoTrem() != null && tremDAO.buscarPorId(x.getCodigoTrem()) == null) {
            erros.add("codigoTrem " + x.getCodigoTrem() + " nao existe");
        }

        if (!erros.isEmpty()) {
            throw new RegraNegocioException(erros);
        }
    }
}
