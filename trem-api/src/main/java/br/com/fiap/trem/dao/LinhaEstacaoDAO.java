package br.com.fiap.trem.dao;

import br.com.fiap.trem.model.LinhaEstacao;
import br.com.fiap.trem.util.JdbcUtil;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class LinhaEstacaoDAO {

    private static final String SQL_LISTAR =
            "SELECT CDG_LINHA_ESTACAO, ORD_LINHA_ESTACAO, LINHA_CDG_LINHA, ESTACAO_CDG_ESTACAO FROM LINHA_ESTACAO ORDER BY CDG_LINHA_ESTACAO";
    private static final String SQL_BUSCAR =
            "SELECT CDG_LINHA_ESTACAO, ORD_LINHA_ESTACAO, LINHA_CDG_LINHA, ESTACAO_CDG_ESTACAO FROM LINHA_ESTACAO WHERE CDG_LINHA_ESTACAO = ?";
    private static final String SQL_INSERIR =
            "INSERT INTO LINHA_ESTACAO (ORD_LINHA_ESTACAO, LINHA_CDG_LINHA, ESTACAO_CDG_ESTACAO) VALUES (?, ?, ?)";
    private static final String SQL_ATUALIZAR =
            "UPDATE LINHA_ESTACAO SET ORD_LINHA_ESTACAO = ?, LINHA_CDG_LINHA = ?, ESTACAO_CDG_ESTACAO = ? WHERE CDG_LINHA_ESTACAO = ?";
    private static final String SQL_EXCLUIR =
            "DELETE FROM LINHA_ESTACAO WHERE CDG_LINHA_ESTACAO = ?";

    private final DataSource dataSource;

    public LinhaEstacaoDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<LinhaEstacao> listar() throws SQLException {
        List<LinhaEstacao> lista = new ArrayList<>();
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public LinhaEstacao buscarPorId(int codigo) throws SQLException {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR)) {
            ps.setInt(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }
        return null;
    }

    public int inserir(LinhaEstacao x) throws SQLException {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_INSERIR, new String[] {"CDG_LINHA_ESTACAO"})) {
            preencher(ps, x);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Nao foi possivel obter o codigo gerado para LinhaEstacao");
    }

    public boolean atualizar(LinhaEstacao x) throws SQLException {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_ATUALIZAR)) {
            int proximo = preencher(ps, x);
            ps.setInt(proximo, x.getCodigo());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean excluir(int codigo) throws SQLException {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_EXCLUIR)) {
            ps.setInt(1, codigo);
            return ps.executeUpdate() > 0;
        }
    }

    private int preencher(PreparedStatement ps, LinhaEstacao x) throws SQLException {
        int i = 1;
        JdbcUtil.setInteger(ps, i++, x.getOrdem());
        JdbcUtil.setInteger(ps, i++, x.getCodigoLinha());
        JdbcUtil.setInteger(ps, i++, x.getCodigoEstacao());
        return i;
    }

    private LinhaEstacao mapear(ResultSet rs) throws SQLException {
        LinhaEstacao x = new LinhaEstacao();
        x.setCodigo(JdbcUtil.getInteger(rs, "CDG_LINHA_ESTACAO"));
        x.setOrdem(JdbcUtil.getInteger(rs, "ORD_LINHA_ESTACAO"));
        x.setCodigoLinha(JdbcUtil.getInteger(rs, "CDG_LINHA"));
        x.setCodigoEstacao(JdbcUtil.getInteger(rs, "CDG_ESTACAO"));
        return x;
    }
}
