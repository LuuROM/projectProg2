package proyecto.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import proyecto.ConexionDB;
import proyecto.excepciones.DatosInvalidosException;
import proyecto.interfaces.ICRUD;
import proyecto.modelos.Producto;

// Implementa el contrato ICRUD, especializado en Producto
public class ProductoDAO implements ICRUD<Producto> {

    private Connection conexion;
    private static final String TABLE_NAME = "Productos";

    public ProductoDAO() {
        // Obtiene la única conexión activa (Singleton)
        this.conexion = ConexionDB.getinstancia().getCon();
    }
    
    // Método traductor: Convierte una fila de la DB (ResultSet) a un objeto Producto de Java.
    //ResultSet es un contenedor de datos java es como una tabla virtual que contiene todas las filas de datos
    //que devolvio despues de ejecutar una consulta SELECT  y rs es su abreviatura
    private Producto mapearProducto(ResultSet rs) throws SQLException {
        // El orden de los argumentos debe coincidir con el constructor de Producto
        return new Producto(
            rs.getString("material"), 
            rs.getString("color"), 
            rs.getString("nombre"), 
            rs.getInt("stock"), 
            rs.getInt("id"), 
            rs.getDouble("precio")
        );
    }
    
    
    // Métodos CRUD básicos (Plantillas)
    @Override
    public void agregar(Producto producto) throws DatosInvalidosException, Exception {
        String sql = "INSERT INTO " + TABLE_NAME + " (nombre, material, color, precio, stock) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setString(1, producto.getNombre());
            pstmt.setString(2, producto.getMaterial());
            pstmt.setString(3, producto.getColor());
            pstmt.setDouble(4, producto.getPrecio());
            pstmt.setInt(5, producto.getStock());
            pstmt.executeUpdate();
            System.out.println("Producto agregado correctamente.");
        } catch (SQLException e) {
            throw new Exception("Error al agregar producto: " + e.getMessage());
        }
    }
    
    //Modificar el stock de un producto
   @Override
    public void modificar(Producto producto) throws DatosInvalidosException, Exception {
        String sql = "UPDATE " + TABLE_NAME + " SET stock = ? WHERE id = ?";
        try (PreparedStatement st = conexion.prepareStatement(sql)) {
            st.setInt(1, producto.getStock());
            st.setInt(2, producto.getId());
            if (st.executeUpdate() == 0) {
                throw new DatosInvalidosException("No se encontró ningún producto con ID " + producto.getId());
            }
        } catch (SQLException e) {
            throw new Exception("Error al modificar stock: " + e.getMessage());
        }
}

    
    @Override
    public void eliminar(int id) throws DatosInvalidosException, Exception {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE id=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() == 0) {
                throw new DatosInvalidosException("No se encontró ningún producto con ID " + id);
            }
            System.out.println("Producto eliminado correctamente.");
        } catch (SQLException e) {
             throw new Exception("Error al eliminar producto: " + e.getMessage());
        }
    }

    // Métodos de Sobrecarga y Lectura
   @Override
    public Producto buscar(int id) throws DatosInvalidosException, Exception {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE id=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearProducto(rs);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al buscar producto: " + e.getMessage());
        }
        return null;
    }
    
    @Override
    public List<Producto> buscarTodo() throws Exception {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_NAME;//pide todas las columnas de la tabla producto
        try (Statement stmt = conexion.createStatement();//crea un objeto para ejecutar la consulta
             ResultSet rs = stmt.executeQuery(sql)) {//Ejecuta la consulta y le pide a la base que devuelva un resultado
            while (rs.next()) {//se inicia un bucle para recorrer cada fila y rs.next() mueve el cursor a la siguientee fila
                productos.add(mapearProducto(rs));//a mapear se le pasa la fila buscada para convertir la fila de sql a un obj Producto ,
                //luego la agrega ese obj recien creado a la lista producto
            }
        } catch (SQLException e) {
            throw new Exception("Error al buscar todos los productos: " + e.getMessage());
        }
        return productos;
    }
    
    // Lógica Específica del Administrador
    
    //Modificar precio de un producto
    public void modificar(int idProducto, double nuevoPrecio) throws DatosInvalidosException, Exception {
        String sql = "UPDATE " + TABLE_NAME + " SET precio = ? WHERE id = ?";
        try (PreparedStatement st = conexion.prepareStatement(sql)) {
            st.setDouble(1, nuevoPrecio);
            st.setInt(2, idProducto);
            
            if (st.executeUpdate() == 0) {
                throw new DatosInvalidosException("No se encontró ningún producto con ID " + idProducto);
            }
        } catch (SQLException e) {
            throw new Exception("Error al modificar precio: " + e.getMessage());
        }
    }
}