package br.com.fiap.trem.dao;

import br.com.fiap.trem.model.TipoModal;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TipoModalDAO {

    private static final String SQL_LISTAR =
            "SELECT TPO_MODAL, NME_TIPO_MODAL, DSC_TIPO_MODAL FROM TIPO_MODAL ORDER BY TPO_MODAL";
    private static final String SQL_BUSCAR =
            "SELECT TPO_MODAL, NME_TIPO_MODAL, DSC_TIPO_MODAL FROM TIPO_MODAL WHERE TPO_MODAL = ?";
    private static final String SQL_INSERIR =
            "INSERT INTO TIPO_MODAL (TPO_MODAL, NME_TIPO_MODAL, DSC_TIPO_MODAL) VALUES (?, ?, ?)";
    private static final String SQL_ATUALIZAR =
            "UPDATE TIPO_MODAL SET NME_TIPO_MODAL = ?, DSC_TIPO_MODAL = ? WHERE TPO_MODAL = ?";
    private static final String SQL_EXCLUIR =
            "DELETE FROM TIPO_MODAL WHERE TPO_MODAL = ?";

    private final DataSource dataSource;

    public TipoModalDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<TipoModal> listar() throws SQLException {
        List<TipoModal> lista = new ArrayList<>();
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public TipoModal buscarPorId(String codigo) throws SQLException {
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

    public void inserir(TipoModal x) throws SQLException {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_INSERIR)) {
            ps.setString(1, x.getCodigo());
            ps.setString(2, x.getNome());
            ps.setString(3, x.getDescricao());
            ps.executeUpdate();
        }
    }

    public boolean atualizar(TipoModal x) throws SQLException {
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

    private TipoModal mapear(ResultSet rs) throws SQLException {
        TipoModal x = new TipoModal();
        x.setCodigo(rs.getString("TPO_MODAL"));
        x.setNome(rs.getString("NME_TIPO_MODAL"));
        x.setDescricao(rs.getString("DSC_TIPO_MODAL"));
        return x;
    }
}
