package br.com.fiap.trem.dao;

import br.com.fiap.trem.model.Linha;
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
public class LinhaDAO {

       private static final String SQL_LISTAR =
            "SELECT CDG_LINHA, NME_LINHA, COR_LINHA, STA_SITUACAO, TPO_MODAL, CDG_OPERADORA FROM LINHA ORDER BY CDG_LINHA";
    private static final String SQL_BUSCAR =
            "SELECT CDG_LINHA, NME_LINHA, COR_LINHA, STA_SITUACAO, TPO_MODAL, CDG_OPERADORA FROM LINHA WHERE CDG_LINHA = ?";
    private static final String SQL_INSERIR =
            "INSERT INTO LINHA (NME_LINHA, COR_LINHA, STA_SITUACAO, TPO_MODAL, CDG_OPERADORA) VALUES (?, ?, ?, ?, ?)";
    private static final String SQL_ATUALIZAR =
            "UPDATE LINHA SET NME_LINHA = ?, COR_LINHA = ?, STA_SITUACAO = ?, TPO_MODAL = ?, CDG_OPERADORA = ? WHERE CDG_LINHA = ?";
    private static final String SQL_EXCLUIR =
            "DELETE FROM LINHA WHERE CDG_LINHA = ?";

    private final DataSource dataSource;

    public LinhaDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Linha> listar() throws SQLException {
        List<Linha> lista = new ArrayList<>();
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public Linha buscarPorId(int codigo) throws SQLException {
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

    public int inserir(Linha x) throws SQLException {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_INSERIR, new String[] {"CDG_LINHA"})) {
            preencher(ps, x);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Nao foi possivel obter o codigo gerado para Linha");
    }

    public boolean atualizar(Linha x) throws SQLException {
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

    private int preencher(PreparedStatement ps, Linha x) throws SQLException {
        int i = 1;
        ps.setString(i++, x.getNome());
        ps.setString(i++, x.getCor());
        ps.setString(i++, x.getSituacao());
        ps.setString(i++, x.getTipoModal());
        JdbcUtil.setInteger(ps, i++, x.getCodigoOperadora());
        return i;
    }

    private Linha mapear(ResultSet rs) throws SQLException {
        Linha x = new Linha();
        x.setCodigo(JdbcUtil.getInteger(rs, "CDG_LINHA"));
        x.setNome(rs.getString("NME_LINHA"));
        x.setCor(rs.getString("COR_LINHA"));
        x.setSituacao(rs.getString("STA_SITUACAO"));
        x.setTipoModal(rs.getString("TPO_MODAL"));
        x.setCodigoOperadora(JdbcUtil.getInteger(rs, "CDG_OPERADORA"));
        return x;
    }
}
