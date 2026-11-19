package br.com.fiap.trem.dao;

import br.com.fiap.trem.model.Viagem;
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
public class ViagemDAO {

        private static final String SQL_LISTAR =
            "SELECT CDG_VIAGEM, DTA_HORA_PARTIDA, DTA_HORA_CHEGADA, STA_SITUACAO, CDG_TREM FROM VIAGEM ORDER BY CDG_VIAGEM";
    private static final String SQL_BUSCAR =
            "SELECT CDG_VIAGEM, DTA_HORA_PARTIDA, DTA_HORA_CHEGADA, STA_SITUACAO, CDG_TREM FROM VIAGEM WHERE CDG_VIAGEM = ?";
    private static final String SQL_INSERIR =
            "INSERT INTO VIAGEM (DTA_HORA_PARTIDA, DTA_HORA_CHEGADA, STA_SITUACAO, CDG_TREM) VALUES (?, ?, ?, ?)";
    private static final String SQL_ATUALIZAR =
            "UPDATE VIAGEM SET DTA_HORA_PARTIDA = ?, DTA_HORA_CHEGADA = ?, STA_SITUACAO = ?, CDG_TREM = ? WHERE CDG_VIAGEM = ?";
    private static final String SQL_EXCLUIR =
            "DELETE FROM VIAGEM WHERE CDG_VIAGEM = ?";

    private final DataSource dataSource;

    public ViagemDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Viagem> listar() throws SQLException {
        List<Viagem> lista = new ArrayList<>();
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public Viagem buscarPorId(int codigo) throws SQLException {
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

    public int inserir(Viagem x) throws SQLException {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_INSERIR, new String[] {"CDG_VIAGEM"})) {
            preencher(ps, x);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Nao foi possivel obter o codigo gerado para Viagem");
    }

    public boolean atualizar(Viagem x) throws SQLException {
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

    private int preencher(PreparedStatement ps, Viagem x) throws SQLException {
        int i = 1;
        JdbcUtil.setLocalDateTime(ps, i++, x.getDataHoraPartida());
        JdbcUtil.setLocalDateTime(ps, i++, x.getDataHoraChegada());
        ps.setString(i++, x.getSituacao());
        JdbcUtil.setInteger(ps, i++, x.getCodigoTrem());
        return i;
    }

    private Viagem mapear(ResultSet rs) throws SQLException {
        Viagem x = new Viagem();
        x.setCodigo(JdbcUtil.getInteger(rs, "CDG_VIAGEM"));
        x.setDataHoraPartida(JdbcUtil.getLocalDateTime(rs, "DTA_HORA_PARTIDA"));
        x.setDataHoraChegada(JdbcUtil.getLocalDateTime(rs, "DTA_HORA_CHEGADA"));
        x.setSituacao(rs.getString("STA_SITUACAO"));
        x.setCodigoTrem(JdbcUtil.getInteger(rs, "CDG_TREM"));
        return x;
    }
}
