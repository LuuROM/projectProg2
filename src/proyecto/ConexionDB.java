
package proyecto;
import java.sql.*;


public class ConexionDB {
    private static final String conexion_base_datos = "jdbc:sqlite:TiendaLunic.db";
    private static ConexionDB instancia ;
    private Connection con;

    public ConexionDB() {
        try {
            con = DriverManager.getConnection(conexion_base_datos);
            crearTablas();
            System.out.println("Conexion exitosa.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public Connection getCon() {
        return con;
    }

    public void setCon(Connection con) {
        this.con = con;
    }
    
    public static ConexionDB getInstancia() {
        if(instancia == null){
            instancia = new ConexionDB();   
        } 
            return instancia;
    }
    
    public void closeConnection(){
        if(con != null){
            try {
                con.close();
                con = null;
                instancia= null;
                System.out.println("La conexión se ha cerrado con éxito.");
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }
    
    private void crearTablas() throws SQLException {
        try (Statement st = con.createStatement()) {
            //Tabla Usuarios (Para UsuarioDAO)
            String sql_usuarios = "CREATE TABLE IF NOT EXISTS Usuarios ("
                    + "usuario_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "nombre TEXT NOT NULL,"
                    + "apellido TEXT,"
                    + "email TEXT UNIQUE NOT NULL,"
                    + "direccion TEXT,"
                    + "telefono TEXT UNIQUE NOT NULL,"
                    + "dni TEXT UNIQUE NOT NULL,"
                    + "contrasenia TEXT,"
                    + "vip INTEGER DEFAULT 0"
                    + ");";
            st.execute(sql_usuarios);
            
            // Tabla Productos (incluye stock)
            String sql_productos = "CREATE TABLE IF NOT EXISTS Productos ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "nombre TEXT NOT NULL,"
                    + "material TEXT,"
                    + "color TEXT,"
                    + "precio REAL NOT NULL,"
                    + "stock INTEGER NOT NULL"
                    + ");";
            st.execute(sql_productos);
            
            //Tabla Pedidos
             String sql_pedidos = "CREATE TABLE IF NOT EXISTS Pedidos ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "usuario_id INTEGER NOT NULL,"
                    + "usuario_email TEXT NOT NULL," 
                    + "fecha_pedido TEXT NOT NULL,"
                    + "total REAL NOT NULL,"
                    + "metodo_pago TEXT,"
                    + "estado TEXT NOT NULL,"
                    + "FOREIGN KEY(usuario_id) REFERENCES Usuarios(usuario_id)"
                    + ");";
            st.execute(sql_pedidos);
            
            //Tabla intermedia (muchos a muchos)
            String sql_pedido_producto = "CREATE TABLE IF NOT EXISTS pedido_producto ("
                   + "pedido_id INTEGER,"
                   + "producto_id INTEGER,"
                   + "cantidad INTEGER,"
                   + "PRIMARY KEY(pedido_id, producto_id),"
                   + "FOREIGN KEY(pedido_id) REFERENCES Pedidos(id),"
                   + "FOREIGN KEY(producto_id) REFERENCES Producto(id)"
                   + ");";
            st.execute(sql_pedido_producto);
        }
    }
}
