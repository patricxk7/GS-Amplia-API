package br.com.fiap.trem.dao;

import br.com.fiap.trem.model.Operadora;
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
public class OperadoraDAO {

    private static final String SQL_LISTAR =
            "SELECT CDG_OPERADORA, NME_OPERADORA, NMR_CNPJ FROM OPERADORA ORDER BY CDG_OPERADORA";
    private static final String SQL_BUSCAR =
            "SELECT CDG_OPERADORA, NME_OPERADORA, NMR_CNPJ FROM OPERADORA WHERE CDG_OPERADORA = ?";
    private static final String SQL_INSERIR =
            "INSERT INTO OPERADORA (NME_OPERADORA, NMR_CNPJ) VALUES (?, ?)";
    private static final String SQL_ATUALIZAR =
            "UPDATE OPERADORA SET NME_OPERADORA = ?, NMR_CNPJ = ? WHERE CDG_OPERADORA = ?";
    private static final String SQL_EXCLUIR =
            "DELETE FROM OPERADORA WHERE CDG_OPERADORA = ?";

    private final DataSource dataSource;

    public OperadoraDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Operadora> listar() throws SQLException {
        List<Operadora> lista = new ArrayList<>();
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public Operadora buscarPorId(int codigo) throws SQLException {
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

    public int inserir(Operadora x) throws SQLException {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_INSERIR, new String[] {"CDG_OPERADORA"})) {
            preencher(ps, x);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Nao foi possivel obter o codigo gerado para Operadora");
    }

    public boolean atualizar(Operadora x) throws SQLException {
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

    private int preencher(PreparedStatement ps, Operadora x) throws SQLException {
        int i = 1;
        ps.setString(i++, x.getNome());
        ps.setString(i++, x.getCnpj());
        return i;
    }

    private Operadora mapear(ResultSet rs) throws SQLException {
        Operadora x = new Operadora();
        x.setCodigo(JdbcUtil.getInteger(rs, "CDG_OPERADORA"));
        x.setNome(rs.getString("NME_OPERADORA"));
        x.setCnpj(rs.getString("NMR_CNPJ"));
        return x;
    }
}
