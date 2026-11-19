package br.com.fiap.trem.dao;

import br.com.fiap.trem.model.Trem;
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
public class TremDAO {

    private static final String SQL_LISTAR =
            "SELECT CDG_TREM, NME_MODELO, QTD_CAPACIDADE, SITUACAO_TREM_STA_TREM, LINHA_CDG_LINHA FROM TREM ORDER BY CDG_TREM";
    private static final String SQL_BUSCAR =
            "SELECT CDG_TREM, NME_MODELO, QTD_CAPACIDADE, SITUACAO_TREM_STA_TREM, LINHA_CDG_LINHA FROM TREM WHERE CDG_TREM = ?";
    private static final String SQL_INSERIR =
            "INSERT INTO TREM (NME_MODELO, QTD_CAPACIDADE, SITUACAO_TREM_STA_TREM, LINHA_CDG_LINHA) VALUES (?, ?, ?, ?)";
    private static final String SQL_ATUALIZAR =
            "UPDATE TREM SET NME_MODELO = ?, QTD_CAPACIDADE = ?, SITUACAO_TREM_STA_TREM = ?, LINHA_CDG_LINHA = ? WHERE CDG_TREM = ?";
    private static final String SQL_EXCLUIR =
            "DELETE FROM TREM WHERE CDG_TREM = ?";

    private final DataSource dataSource;

    public TremDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Trem> listar() throws SQLException {
        List<Trem> lista = new ArrayList<>();
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public Trem buscarPorId(int codigo) throws SQLException {
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

    public int inserir(Trem x) throws SQLException {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_INSERIR, new String[] {"CDG_TREM"})) {
            preencher(ps, x);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Nao foi possivel obter o codigo gerado para Trem");
    }

    public boolean atualizar(Trem x) throws SQLException {
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

    private int preencher(PreparedStatement ps, Trem x) throws SQLException {
        int i = 1;
        ps.setString(i++, x.getModelo());
        JdbcUtil.setInteger(ps, i++, x.getCapacidade());
        ps.setString(i++, x.getSituacao());
        JdbcUtil.setInteger(ps, i++, x.getCodigoLinha());
        return i;
    }

    private Trem mapear(ResultSet rs) throws SQLException {
        Trem x = new Trem();
        x.setCodigo(JdbcUtil.getInteger(rs, "CDG_TREM"));
        x.setModelo(rs.getString("NME_MODELO"));
        x.setCapacidade(JdbcUtil.getInteger(rs, "QTD_CAPACIDADE"));
        x.setSituacao(rs.getString("SITUACAO_TREM_STA_TREM"));
        x.setCodigoLinha(JdbcUtil.getInteger(rs, "LINHA_CDG_LINHA"));
        return x;
    }
}
