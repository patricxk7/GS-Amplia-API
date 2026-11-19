package br.com.fiap.trem.dao;

import br.com.fiap.trem.model.Passageiro;
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
public class PassageiroDAO {

    private static final String SQL_LISTAR =
            "SELECT CDG_PASSAGEIRO, NME_PASSAGEIRO, NMR_CPF, DSC_EMAIL, DTA_NASCIMENTO FROM PASSAGEIRO ORDER BY CDG_PASSAGEIRO";
    private static final String SQL_BUSCAR =
            "SELECT CDG_PASSAGEIRO, NME_PASSAGEIRO, NMR_CPF, DSC_EMAIL, DTA_NASCIMENTO FROM PASSAGEIRO WHERE CDG_PASSAGEIRO = ?";
    private static final String SQL_INSERIR =
            "INSERT INTO PASSAGEIRO (NME_PASSAGEIRO, NMR_CPF, DSC_EMAIL, DTA_NASCIMENTO) VALUES (?, ?, ?, ?)";
    private static final String SQL_ATUALIZAR =
            "UPDATE PASSAGEIRO SET NME_PASSAGEIRO = ?, NMR_CPF = ?, DSC_EMAIL = ?, DTA_NASCIMENTO = ? WHERE CDG_PASSAGEIRO = ?";
    private static final String SQL_EXCLUIR =
            "DELETE FROM PASSAGEIRO WHERE CDG_PASSAGEIRO = ?";

    private final DataSource dataSource;

    public PassageiroDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Passageiro> listar() throws SQLException {
        List<Passageiro> lista = new ArrayList<>();
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public Passageiro buscarPorId(int codigo) throws SQLException {
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

    public int inserir(Passageiro x) throws SQLException {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_INSERIR, new String[] {"CDG_PASSAGEIRO"})) {
            preencher(ps, x);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Nao foi possivel obter o codigo gerado para Passageiro");
    }

    public boolean atualizar(Passageiro x) throws SQLException {
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

    private int preencher(PreparedStatement ps, Passageiro x) throws SQLException {
        int i = 1;
        ps.setString(i++, x.getNome());
        ps.setString(i++, x.getCpf());
        ps.setString(i++, x.getEmail());
        JdbcUtil.setLocalDate(ps, i++, x.getDataNascimento());
        return i;
    }

    private Passageiro mapear(ResultSet rs) throws SQLException {
        Passageiro x = new Passageiro();
        x.setCodigo(JdbcUtil.getInteger(rs, "CDG_PASSAGEIRO"));
        x.setNome(rs.getString("NME_PASSAGEIRO"));
        x.setCpf(rs.getString("NMR_CPF"));
        x.setEmail(rs.getString("DSC_EMAIL"));
        x.setDataNascimento(JdbcUtil.getLocalDate(rs, "DTA_NASCIMENTO"));
        return x;
    }
}
