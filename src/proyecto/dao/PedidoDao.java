
package proyecto.dao;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import proyecto.ConexionDB;
import proyecto.excepciones.DatosInvalidosException;
import proyecto.interfaces.ICRUD;
import proyecto.modelos.Pedido;
import proyecto.modelos.Producto;
import proyecto.modelos.Usuario;

public class PedidoDao implements ICRUD<Pedido> {
    private final Connection conexion;
    private static final String TABLE_NAME = "Pedidos";
    private final UsuarioDao usuarioDao;
  
    public PedidoDao(){
        this.conexion= ConexionDB.getinstancia().getCon();
        this.usuarioDao = new UsuarioDao();
    }
    
    private Pedido mapearPedido(ResultSet rs) throws SQLException, Exception {
        int usuarioId = rs.getInt("usuario_id");
        Usuario usuario = usuarioDao.buscar(usuarioId);
        if (usuario == null) {
            throw new Exception("Usuario con ID " + usuarioId + " no encontrado para el pedido " + rs.getInt("id"));
        }
        List<Producto> productos = obtenerProductosDelPedido(rs.getInt("id"));
        return new Pedido(
            rs.getInt("id"),
            usuario,
            productos, // lista de productos omitida (se puede llenar en otra consulta)
            rs.getDouble("total"),
            rs.getString("metodo_pago"),
            rs.getString("estado")
        );
    }

   //Implementacion de ICRUD
    @Override
    public void agregar(Pedido pedido)throws DatosInvalidosException, Exception{
        if (pedido.getTotal() <= 0){
            throw new DatosInvalidosException("El total del pedido deber ser positivo");
        }
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
        String fechaStr = sdf.format(pedido.getFechaPedido() != null ? pedido.getFechaPedido() : new Date());
      
        String sql = "INSERT INTO " + TABLE_NAME + " (usuario_id, usuario_email, fecha_pedido, total, metodo_pago, estado) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement st = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            st.setInt(1, pedido.getUsuario().getId());
            st.setString(2, pedido.getUsuario().getEmail());
            st.setString(3, fechaStr);
            st.setDouble(4, pedido.getTotal());
            st.setString(5, pedido.getMetodoDePago());
            st.setString(6, pedido.getEstado());
            
            int affectedRows = st.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = st.getGeneratedKeys()) {
                    if (rs.next()) {
                        pedido.setId(rs.getInt(1));
                    }
                }
            }
        } 
        
        String sqlProductos = "INSERT INTO pedido_producto (pedido_id, producto_id, cantidad) VALUES (?,?,?)";
        try (PreparedStatement stProd = conexion.prepareStatement(sqlProductos)) {
            for (Producto p : pedido.getProductos()) {
                stProd.setInt(1, pedido.getId());
                stProd.setInt(2, p.getId());
                stProd.setInt(3, p.getStock());
                stProd.addBatch();
            }
            stProd.executeBatch();
        } catch (SQLException e) {
            throw new Exception("Error al guardar los productos del pedido: " + e.getMessage());
        }
            System.out.println(" Pedido registrado con ID: " + pedido.getId());
    }
    
    @Override
    public void modificar(Pedido pedido) throws DatosInvalidosException,Exception{
        if (pedido.getId() <= 0){
            throw new DatosInvalidosException("ID del pedido invalido para modificar");
        }
        String sql = "UPDATE " + TABLE_NAME + " SET estado=? WHERE id=?";
    try (PreparedStatement st = conexion.prepareStatement(sql)){
        st.setString(1, pedido.getEstado());
        st.setInt(2, pedido.getId());
        int filasActualizadas = st.executeUpdate();
        if(filasActualizadas == 0){
            throw new DatosInvalidosException("No se encontro el pedido con id "+ pedido.getId());
            
        }
        System.out.println("Pedido ID "+ pedido.getId() + " modificado a estado:" + pedido.getEstado());
    } catch (SQLException e){
        throw new Exception("Error al modificar pedido ;" + e.getMessage());
    }
    
}
    @Override
    public void eliminar(int id) throws DatosInvalidosException,Exception{
        if (id <= 0){
            throw new DatosInvalidosException("ID del pedido invalido para eliminar");
        }
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE id =?";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)){
            pstmt.setInt(1, id);
            if(pstmt.executeUpdate() ==0){
                throw new DatosInvalidosException("No se encontro el pedido con ID"+ id);
            }
            System.out.println("Pedido con ID "+ id + " eliminado");
        } catch(SQLException e){
            throw new Exception("Error al eliminar pedido "+ e.getMessage());
        } 
    }
    
    @Override
    public Pedido buscar(int id) throws DatosInvalidosException,Exception {
        if (id <= 0){
            throw new DatosInvalidosException("Error al buscar el Pedido, indice negativo");
        }
       
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";
       
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setInt(1,id);
            try (ResultSet rs = pstmt.executeQuery()){
                if(rs.next()){
                    return  mapearPedido(rs);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al buscar pedido por ID"+ e.getMessage());
        }
        return null;
    }
    
    @Override
    public List<Pedido> buscarTodo() throws Exception{
        List<Pedido> pedidos = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_NAME;
        try (Statement stmt = conexion.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            try {
                pedidos.add(mapearPedido(rs));
            } catch (Exception e) {
                System.err.println("No se pudo mapear pedido con ID " + rs.getInt("id") + ": " + e.getMessage());
            }
        } catch (SQLException e) {
            throw new Exception("Error al buscar todos los usuarios: " + e.getMessage());
        } finally {
            return pedidos;
        }
    }
    
    // Método propio de PedidoDao
    private List<Producto> obtenerProductosDelPedido(int pedidoId) throws Exception {
    List<Producto> productos = new ArrayList<>();
    String sql = "SELECT p.*, pp.cantidad FROM Productos p " +
                 "JOIN pedido_producto pp ON p.id = pp.producto_id " +
                 "WHERE pp.pedido_id = ?";

    try (PreparedStatement st = conexion.prepareStatement(sql)) {
        st.setInt(1, pedidoId);
        try (ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                Producto prod = new Producto(
                    rs.getString("material"),
                    rs.getString("color"),
                    rs.getString("nombre"),
                    rs.getInt("cantidad"),
                    rs.getInt("id"),
                    rs.getDouble("precio")
                );
                productos.add(prod);
            }
        }
    }
    return productos;
}


}
