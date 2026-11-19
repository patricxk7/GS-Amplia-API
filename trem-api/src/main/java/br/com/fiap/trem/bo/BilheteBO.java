package br.com.fiap.trem.bo;

import br.com.fiap.trem.dao.BilheteDAO;
import br.com.fiap.trem.dao.EstacaoDAO;
import br.com.fiap.trem.dao.PassageiroDAO;
import br.com.fiap.trem.dao.ViagemDAO;
import br.com.fiap.trem.exception.RecursoNaoEncontradoException;
import br.com.fiap.trem.exception.RegraNegocioException;
import br.com.fiap.trem.model.Bilhete;
import br.com.fiap.trem.util.Validador;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class BilheteBO {

    private final BilheteDAO dao;
    private final EstacaoDAO estacaoDAO;
    private final PassageiroDAO passageiroDAO;
    private final ViagemDAO viagemDAO;

    public BilheteBO(BilheteDAO dao, EstacaoDAO estacaoDAO, PassageiroDAO passageiroDAO, ViagemDAO viagemDAO) {
        this.dao = dao;
        this.estacaoDAO = estacaoDAO;
        this.passageiroDAO = passageiroDAO;
        this.viagemDAO = viagemDAO;
    }

    public List<Bilhete> listar() throws SQLException {
        return dao.listar();
    }

    public Bilhete buscar(int codigo) throws SQLException {
        Bilhete x = dao.buscarPorId(codigo);
        if (x == null) {
            throw new RecursoNaoEncontradoException("Bilhete com codigo " + codigo + " nao encontrado(a)");
        }
        return x;
    }

    public Bilhete cadastrar(Bilhete x) throws SQLException {
        x.setCodigo(null);
        validar(x);
        x.setCodigo(dao.inserir(x));
        return x;
    }

    public Bilhete atualizar(int codigo, Bilhete x) throws SQLException {
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

    private void validar(Bilhete x) throws SQLException {
        x.setSituacao(Validador.maiuscula(x.getSituacao()));

        List<String> erros = new ArrayList<>();
        Validador.obrigatorio(erros, "valor", x.getValor());
        Validador.positivo(erros, "valor", x.getValor());
        Validador.obrigatorio(erros, "dataCompra", x.getDataCompra());
        Validador.codigo(erros, "situacao", x.getSituacao(), true, "AU");
        Validador.obrigatorio(erros, "codigoViagem", x.getCodigoViagem());
        Validador.obrigatorio(erros, "codigoPassageiro", x.getCodigoPassageiro());
        Validador.obrigatorio(erros, "codigoEstacaoOrigem", x.getCodigoEstacaoOrigem());
        Validador.obrigatorio(erros, "codigoEstacaoDestino", x.getCodigoEstacaoDestino());
        if (x.getCodigoEstacaoOrigem() != null && x.getCodigoEstacaoOrigem().equals(x.getCodigoEstacaoDestino())) {
            erros.add("estacao de origem e estacao de destino devem ser diferentes");
        }

        if (x.getCodigoViagem() != null && viagemDAO.buscarPorId(x.getCodigoViagem()) == null) {
            erros.add("codigoViagem " + x.getCodigoViagem() + " nao existe");
        }
        if (x.getCodigoPassageiro() != null && passageiroDAO.buscarPorId(x.getCodigoPassageiro()) == null) {
            erros.add("codigoPassageiro " + x.getCodigoPassageiro() + " nao existe");
        }
        if (x.getCodigoEstacaoOrigem() != null && estacaoDAO.buscarPorId(x.getCodigoEstacaoOrigem()) == null) {
            erros.add("codigoEstacaoOrigem " + x.getCodigoEstacaoOrigem() + " nao existe");
        }
        if (x.getCodigoEstacaoDestino() != null && estacaoDAO.buscarPorId(x.getCodigoEstacaoDestino()) == null) {
            erros.add("codigoEstacaoDestino " + x.getCodigoEstacaoDestino() + " nao existe");
        }

        if (!erros.isEmpty()) {
            throw new RegraNegocioException(erros);
        }
    }
}
