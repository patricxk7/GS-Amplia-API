package br.com.fiap.trem.dao;

import br.com.fiap.trem.model.Estacao;
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
public class EstacaoDAO {

    private static final String SQL_LISTAR =
            "SELECT CDG_ESTACAO, NME_ESTACAO, DSC_ENDERECO, STA_ACESSIBILIDADE, STA_SITUACAO FROM ESTACAO ORDER BY CDG_ESTACAO";
    private static final String SQL_BUSCAR =
            "SELECT CDG_ESTACAO, NME_ESTACAO, DSC_ENDERECO, STA_ACESSIBILIDADE, STA_SITUACAO FROM ESTACAO WHERE CDG_ESTACAO = ?";
    private static final String SQL_INSERIR =
            "INSERT INTO ESTACAO (NME_ESTACAO, DSC_ENDERECO, STA_ACESSIBILIDADE, STA_SITUACAO) VALUES (?, ?, ?, ?)";
    private static final String SQL_ATUALIZAR =
            "UPDATE ESTACAO SET NME_ESTACAO = ?, DSC_ENDERECO = ?, STA_ACESSIBILIDADE = ?, STA_SITUACAO = ? WHERE CDG_ESTACAO = ?";
    private static final String SQL_EXCLUIR =
            "DELETE FROM ESTACAO WHERE CDG_ESTACAO = ?";

    private final DataSource dataSource;

    public EstacaoDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Estacao> listar() throws SQLException {
        List<Estacao> lista = new ArrayList<>();
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public Estacao buscarPorId(int codigo) throws SQLException {
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

    public int inserir(Estacao x) throws SQLException {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_INSERIR, new String[] {"CDG_ESTACAO"})) {
            preencher(ps, x);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Nao foi possivel obter o codigo gerado para Estacao");
    }

    public boolean atualizar(Estacao x) throws SQLException {
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

    private int preencher(PreparedStatement ps, Estacao x) throws SQLException {
        int i = 1;
        ps.setString(i++, x.getNome());
        ps.setString(i++, x.getEndereco());
        ps.setString(i++, x.getAcessibilidade());
        ps.setString(i++, x.getSituacao());
        return i;
    }

    private Estacao mapear(ResultSet rs) throws SQLException {
        Estacao x = new Estacao();
        x.setCodigo(JdbcUtil.getInteger(rs, "CDG_ESTACAO"));
        x.setNome(rs.getString("NME_ESTACAO"));
        x.setEndereco(rs.getString("DSC_ENDERECO"));
        x.setAcessibilidade(rs.getString("STA_ACESSIBILIDADE"));
        x.setSituacao(rs.getString("STA_SITUACAO"));
        return x;
    }
}
