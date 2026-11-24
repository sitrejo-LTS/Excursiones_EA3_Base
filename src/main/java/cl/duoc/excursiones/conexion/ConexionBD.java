package cl.duoc.excursiones.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Reúne los datos de conexión a la base de datos y entrega conexiones.
 * Ninguna otra clase debe contener estos datos.
 */
public class ConexionBD {

    // =====================================================================
    // EA3: completa los datos de conexión de tu laboratorio.
    //
    //   Formato de URL según el motor:
    //     MySQL  -> jdbc:mysql://<servidor>:<puerto>/<base_de_datos>
    //     Oracle -> jdbc:oracle:thin:@<servidor>:<puerto>/<servicio>
    // =====================================================================
    private static final String URL = "jdbc:oracle:thin:@localhost:1521/FREE";
    private static final String USUARIO = "system";
    private static final String CLAVE = "123321";

    private ConexionBD() {
    }

    /**
     * @return una conexión abierta a la base de datos. Quien la utiliza debe cerrarla.
     * @throws SQLException si no es posible conectarse.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, CLAVE);
    }
}
