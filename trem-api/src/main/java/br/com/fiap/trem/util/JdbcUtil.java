package br.com.fiap.trem.util;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Metodos auxiliares para tratar valores nulos e datas no JDBC. */
public final class JdbcUtil {

    private JdbcUtil() {
    }

    public static void setInteger(PreparedStatement ps, int indice, Integer valor) throws SQLException {
        if (valor == null) {
            ps.setNull(indice, Types.INTEGER);
        } else {
            ps.setInt(indice, valor);
        }
    }

    public static void setLocalDate(PreparedStatement ps, int indice, LocalDate valor) throws SQLException {
        if (valor == null) {
            ps.setNull(indice, Types.DATE);
        } else {
            ps.setDate(indice, Date.valueOf(valor));
        }
    }

    public static void setLocalDateTime(PreparedStatement ps, int indice, LocalDateTime valor) throws SQLException {
        if (valor == null) {
            ps.setNull(indice, Types.TIMESTAMP);
        } else {
            ps.setTimestamp(indice, Timestamp.valueOf(valor));
        }
    }

    public static Integer getInteger(ResultSet rs, String coluna) throws SQLException {
        int valor = rs.getInt(coluna);
        return rs.wasNull() ? null : valor;
    }

    public static LocalDate getLocalDate(ResultSet rs, String coluna) throws SQLException {
        Date data = rs.getDate(coluna);
        return data == null ? null : data.toLocalDate();
    }

    public static LocalDateTime getLocalDateTime(ResultSet rs, String coluna) throws SQLException {
        Timestamp data = rs.getTimestamp(coluna);
        return data == null ? null : data.toLocalDateTime();
    }
}
