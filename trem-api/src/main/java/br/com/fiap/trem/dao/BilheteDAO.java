package br.com.fiap.trem.dao;

import br.com.fiap.trem.model.Bilhete;
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
public class BilheteDAO {

    private static final String SQL_LISTAR =
            "SELECT CDG_BILHETE, VLR_BILHETE, DTA_COMPRA, STA_SITUACAO, VIAGEM_CDG_VIAGEM, PASSAGEIRO_CDG_PASSAGEIRO, ESTACAO_CDG_ESTACAO_ORIGEM, ESTACAO_CDG_ESTACAO_DESTINO FROM BILHETE ORDER BY CDG_BILHETE";
    private static final String SQL_BUSCAR =
            "SELECT CDG_BILHETE, VLR_BILHETE, DTA_COMPRA, STA_SITUACAO, VIAGEM_CDG_VIAGEM, PASSAGEIRO_CDG_PASSAGEIRO, ESTACAO_CDG_ESTACAO_ORIGEM, ESTACAO_CDG_ESTACAO_DESTINO FROM BILHETE WHERE CDG_BILHETE = ?";
    private static final String SQL_INSERIR =
            "INSERT INTO BILHETE (VLR_BILHETE, DTA_COMPRA, STA_SITUACAO, VIAGEM_CDG_VIAGEM, PASSAGEIRO_CDG_PASSAGEIRO, ESTACAO_CDG_ESTACAO_ORIGEM, ESTACAO_CDG_ESTACAO_DESTINO) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_ATUALIZAR =
            "UPDATE BILHETE SET VLR_BILHETE = ?, DTA_COMPRA = ?, STA_SITUACAO = ?, VIAGEM_CDG_VIAGEM = ?, PASSAGEIRO_CDG_PASSAGEIRO = ?, ESTACAO_CDG_ESTACAO_ORIGEM = ?, ESTACAO_CDG_ESTACAO_DESTINO = ? WHERE CDG_BILHETE = ?";
    private static final String SQL_EXCLUIR =
            "DELETE FROM BILHETE WHERE CDG_BILHETE = ?";

    private final DataSource dataSource;

    public BilheteDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Bilhete> listar() throws SQLException {
        List<Bilhete> lista = new ArrayList<>();
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public Bilhete buscarPorId(int codigo) throws SQLException {
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

    public int inserir(Bilhete x) throws SQLException {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_INSERIR, new String[] {"CDG_BILHETE"})) {
            preencher(ps, x);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Nao foi possivel obter o codigo gerado para Bilhete");
    }

    public boolean atualizar(Bilhete x) throws SQLException {
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

    private int preencher(PreparedStatement ps, Bilhete x) throws SQLException {
        int i = 1;
        ps.setBigDecimal(i++, x.getValor());
        JdbcUtil.setLocalDateTime(ps, i++, x.getDataCompra());
        ps.setString(i++, x.getSituacao());
        JdbcUtil.setInteger(ps, i++, x.getCodigoViagem());
        JdbcUtil.setInteger(ps, i++, x.getCodigoPassageiro());
        JdbcUtil.setInteger(ps, i++, x.getCodigoEstacaoOrigem());
        JdbcUtil.setInteger(ps, i++, x.getCodigoEstacaoDestino());
        return i;
    }

    private Bilhete mapear(ResultSet rs) throws SQLException {
        Bilhete x = new Bilhete();
        x.setCodigo(JdbcUtil.getInteger(rs, "CDG_BILHETE"));
        x.setValor(rs.getBigDecimal("VLR_BILHETE"));
        x.setDataCompra(JdbcUtil.getLocalDateTime(rs, "DTA_COMPRA"));
        x.setSituacao(rs.getString("STA_SITUACAO"));
        x.setCodigoViagem(JdbcUtil.getInteger(rs, "VIAGEM_CDG_VIAGEM"));
        x.setCodigoPassageiro(JdbcUtil.getInteger(rs, "PASSAGEIRO_CDG_PASSAGEIRO"));
        x.setCodigoEstacaoOrigem(JdbcUtil.getInteger(rs, "ESTACAO_CDG_ESTACAO_ORIGEM"));
        x.setCodigoEstacaoDestino(JdbcUtil.getInteger(rs, "ESTACAO_CDG_ESTACAO_DESTINO"));
        return x;
    }
}
