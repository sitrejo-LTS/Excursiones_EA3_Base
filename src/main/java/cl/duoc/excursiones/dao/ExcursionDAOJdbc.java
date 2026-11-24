package cl.duoc.excursiones.dao;

import cl.duoc.excursiones.conexion.ConexionBD;
import cl.duoc.excursiones.model.Excursion;
import cl.duoc.excursiones.model.Kayak;
import cl.duoc.excursiones.model.Trekking;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;


public class ExcursionDAOJdbc implements ExcursionDAO {

    private static final int TIPO_TREKKING = 1;
    private static final int TIPO_KAYAK = 2;

    private static final String SQL_SELECT_TODOS =
            "SELECT id, id_tipo, codigo, participantes, distancia_km, "
                    + "desnivel_metros, guia_bilingue, seguro_incluido, duracion_horas "
                    + "FROM excursion ORDER BY id";

    private static final String SQL_INSERT =
            "INSERT INTO excursion (id_tipo, codigo, participantes, distancia_km, "
                    + "desnivel_metros, guia_bilingue, seguro_incluido, duracion_horas) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_UPDATE =
            "UPDATE excursion SET id_tipo = ?, codigo = ?, participantes = ?, distancia_km = ?, "
                    + "desnivel_metros = ?, guia_bilingue = ?, seguro_incluido = ?, duracion_horas = ? "
                    + "WHERE id = ?";

    private static final String SQL_DELETE = "DELETE FROM excursion WHERE id = ?";

    @Override
    public List<Excursion> obtenerTodos() throws PersistenciaException {
        List<Excursion> excursiones = new ArrayList<>();
        try (Connection conexion = ConexionBD.getConnection();
             PreparedStatement ps = conexion.prepareStatement(SQL_SELECT_TODOS);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                excursiones.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo obtener las excursiones: " + e.getMessage(), e);
        }
        return excursiones;
    }

    @Override
    public void insertar(Excursion excursion) throws PersistenciaException {
        try (Connection conexion = ConexionBD.getConnection();
             PreparedStatement ps = conexion.prepareStatement(SQL_INSERT)) {
            asignarDatos(ps, excursion);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo insertar la excursión: " + e.getMessage(), e);
        }
    }

    @Override
    public void actualizar(Excursion excursion) throws PersistenciaException {
        try (Connection conexion = ConexionBD.getConnection();
             PreparedStatement ps = conexion.prepareStatement(SQL_UPDATE)) {
            asignarDatos(ps, excursion);
            ps.setInt(9, excursion.getId());
            if (ps.executeUpdate() == 0) {
                throw new PersistenciaException("No se pudo actualizar la excursión: no existe una "
                        + "excursión con id " + excursion.getId() + ".");
            }
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo actualizar la excursión: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminar(int id) throws PersistenciaException {
        try (Connection conexion = ConexionBD.getConnection();
             PreparedStatement ps = conexion.prepareStatement(SQL_DELETE)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() == 0) {
                throw new PersistenciaException("No se pudo eliminar la excursión: no existe una "
                        + "excursión con id " + id + ".");
            }
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo eliminar la excursión: " + e.getMessage(), e);
        }
    }

    private void asignarDatos(PreparedStatement ps, Excursion excursion) throws SQLException {
        ps.setInt(1, excursion instanceof Trekking ? TIPO_TREKKING : TIPO_KAYAK);
        ps.setString(2, excursion.getCodigo());
        ps.setInt(3, excursion.getParticipantes());
        ps.setDouble(4, excursion.getDistanciaKm());

        if (excursion instanceof Trekking trekking) {
            ps.setDouble(5, trekking.getDesnivelMetros());
            ps.setBoolean(6, trekking.isGuiaBilingue());
            ps.setBoolean(7, trekking.isSeguroIncluido());
            ps.setNull(8, Types.DECIMAL);
        } else if (excursion instanceof Kayak kayak) {
            ps.setNull(5, Types.DECIMAL);
            ps.setNull(6, Types.BOOLEAN);
            ps.setNull(7, Types.BOOLEAN);
            ps.setDouble(8, kayak.getDuracionHoras());
        } else {
            throw new SQLException("Tipo de excursión no soportado: " + excursion.getTipo());
        }
    }

    private Excursion mapear(ResultSet rs) throws SQLException {
        int idTipo = rs.getInt("id_tipo");
        String codigo = rs.getString("codigo");
        int participantes = rs.getInt("participantes");
        double distanciaKm = rs.getDouble("distancia_km");

        Excursion excursion;
        if (idTipo == TIPO_TREKKING) {
            excursion = new Trekking(codigo, participantes, distanciaKm,
                    rs.getDouble("desnivel_metros"),
                    rs.getBoolean("guia_bilingue"),
                    rs.getBoolean("seguro_incluido"));
        } else if (idTipo == TIPO_KAYAK) {
            excursion = new Kayak(codigo, participantes, distanciaKm, rs.getDouble("duracion_horas"));
        } else {
            throw new SQLException("Tipo de excursión desconocido: " + idTipo);
        }
        excursion.setId(rs.getInt("id"));
        return excursion;
    }
}
