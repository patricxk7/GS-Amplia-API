package br.com.fiap.trem.dao;

import br.com.fiap.trem.model.SituacaoTrem;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class SituacaoTremDAO {

    private static final String SQL_LISTAR =
            "SELECT STA_TREM, NME_SITUACAO_TREM, DSC_SITUACAO_TREM FROM SITUACAO_TREM ORDER BY STA_TREM";
    private static final String SQL_BUSCAR =
            "SELECT STA_TREM, NME_SITUACAO_TREM, DSC_SITUACAO_TREM FROM SITUACAO_TREM WHERE STA_TREM = ?";
    private static final String SQL_INSERIR =
            "INSERT INTO SITUACAO_TREM (STA_TREM, NME_SITUACAO_TREM, DSC_SITUACAO_TREM) VALUES (?, ?, ?)";
    private static final String SQL_ATUALIZAR =
            "UPDATE SITUACAO_TREM SET NME_SITUACAO_TREM = ?, DSC_SITUACAO_TREM = ? WHERE STA_TREM = ?";
    private static final String SQL_EXCLUIR =
            "DELETE FROM SITUACAO_TREM WHERE STA_TREM = ?";

    private final DataSource dataSource;

    public SituacaoTremDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<SituacaoTrem> listar() throws SQLException {
        List<SituacaoTrem> lista = new ArrayList<>();
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public SituacaoTrem buscarPorId(String codigo) throws SQLException {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }
        return null;
    }

    public void inserir(SituacaoTrem x) throws SQLException {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_INSERIR)) {
            ps.setString(1, x.getCodigo());
            ps.setString(2, x.getNome());
            ps.setString(3, x.getDescricao());
            ps.executeUpdate();
        }
    }

    public boolean atualizar(SituacaoTrem x) throws SQLException {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_ATUALIZAR)) {
            ps.setString(1, x.getNome());
            ps.setString(2, x.getDescricao());
            ps.setString(3, x.getCodigo());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean excluir(String codigo) throws SQLException {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_EXCLUIR)) {
            ps.setString(1, codigo);
            return ps.executeUpdate() > 0;
        }
    }

    private SituacaoTrem mapear(ResultSet rs) throws SQLException {
        SituacaoTrem x = new SituacaoTrem();
        x.setCodigo(rs.getString("STA_TREM"));
        x.setNome(rs.getString("NME_SITUACAO_TREM"));
        x.setDescricao(rs.getString("DSC_SITUACAO_TREM"));
        return x;
    }
}
