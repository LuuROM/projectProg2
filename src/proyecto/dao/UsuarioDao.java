package proyecto.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import proyecto.ConexionDB;
import proyecto.excepciones.DatosInvalidosException;
import proyecto.interfaces.ICRUD;
import proyecto.modelos.*;

public class UsuarioDao implements ICRUD<Usuario> {

    private static final String TABLE_NAME = "Usuarios";
    private static Connection conexion;

    public UsuarioDao() {
        this.conexion = ConexionDB.getInstancia().getCon();
    }

    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        boolean esVip = rs.getInt("vip") == 1;

        if (esVip) {
            return new ClienteVip(
                rs.getString("direccion"),
                rs.getString("telefono"),
                rs.getString("dni"),
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getString("email"),
                rs.getInt("usuario_id"),
                new CarritoDeCompra(),
                rs.getString("contrasenia")
            );  
        } else {
            return new Cliente(
                rs.getString("direccion"),
                rs.getString("telefono"),
                rs.getString("dni"),
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getString("email"),
                rs.getInt("usuario_id"),
                new CarritoDeCompra()
            );
        }
    }

 
    //Metodos CRUD
    @Override
    public void agregar(Usuario usuario) throws DatosInvalidosException, Exception {
        if (usuario.getEmail() == null || usuario.getEmail().isEmpty()) {
            throw new DatosInvalidosException("El email del usuario es obligatorio");
        }

        Cliente cliente = (Cliente) usuario;

        // Chequear si ya existe email
        if (existeEmail(cliente.getEmail())) {
            throw new DatosInvalidosException("El email ya está registrado.");
        }

        String sql = "INSERT INTO " + TABLE_NAME + " (nombre, apellido, email, direccion, telefono, dni, contrasenia, vip) VALUES(?,?,?,?,?,?,?,?)";

        try (PreparedStatement pstmt = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, cliente.getNombre());
            pstmt.setString(2, cliente.getApellido());
            pstmt.setString(3, cliente.getEmail());
            pstmt.setString(4, cliente.getDireccion());
            pstmt.setString(5, cliente.getTelefono());
            pstmt.setString(6, cliente.getDni());
            if (usuario instanceof ClienteVip clienteVip) {
                pstmt.setString(7, clienteVip.getContrasenia());
            } else {
                pstmt.setString(7, null);
            }
            pstmt.setInt(8, usuario instanceof ClienteVip ? 1 : 0);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        cliente.setId(rs.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al agregar un usuario: " + e.getMessage());
        }
    }

    //Modificar direccion
    @Override
    public void modificar(Usuario usuario) throws DatosInvalidosException, Exception {
        int id = 0;
        String nuevaDireccion = "";
        if (usuario instanceof Cliente cliente) {
            id = cliente.getId();
            nuevaDireccion = cliente.getDireccion();
        } else if (usuario instanceof ClienteVip clienteVip) {
            id = clienteVip.getId();
            nuevaDireccion = clienteVip.getDireccion();
        }

        if (id <= 0) {
            throw new DatosInvalidosException("ID de usuario inválido");
        }

        String sql = "UPDATE " + TABLE_NAME + " SET direccion=? WHERE usuario_id=?";

        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setString(1, nuevaDireccion);
            pstmt.setInt(2, id);

            if (pstmt.executeUpdate() == 0) {
                throw new DatosInvalidosException("No se encontró el usuario con ID: " + id);
            }
        } catch (SQLException e) {
            throw new Exception("Error al modificar dirección: " + e.getMessage());
        }
    }
    
    //Modificar telefono
    public void modificar(Usuario usuario, String nuevoTelefono) throws DatosInvalidosException, Exception {
        int id = 0;
        if (usuario instanceof Cliente cliente) {
            id = cliente.getId();
        } else if (usuario instanceof ClienteVip clienteVip) {
            id = clienteVip.getId();
        }

        if (id <= 0) {
            throw new DatosInvalidosException("ID de usuario inválido");
        }

        String sql = "UPDATE " + TABLE_NAME + " SET telefono=? WHERE usuario_id=?";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setString(1, nuevoTelefono);
            pstmt.setInt(2, id);

            if (pstmt.executeUpdate() == 0) {
                throw new DatosInvalidosException("No se encontró el usuario con ID: " + id);
            }
        } catch (SQLException e) {
            throw new Exception("Error al modificar teléfono: " + e.getMessage());
        }
    }
 

    @Override
    public void eliminar(int id) throws DatosInvalidosException, Exception {
        if (id <= 0) {
            throw new DatosInvalidosException("ID de usuario inválido para eliminar");
        }

        String sql = "DELETE FROM " + TABLE_NAME + " WHERE usuario_id=?";

        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            if (pstmt.executeUpdate() == 0) {
                throw new DatosInvalidosException("No se encontró al usuario con ID: " + id);
            }
        } catch (SQLException e) {
            throw new Exception("Error al eliminar usuario: " + e.getMessage());
        }
    }

    @Override
    public Usuario buscar(int id) throws Exception {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE usuario_id=?";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapearUsuario(rs);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al buscar usuario por ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Usuario> buscarTodo() throws Exception {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_NAME;
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }
        } catch (SQLException e) {
            throw new Exception("Error al buscar todos los usuarios: " + e.getMessage());
        }
        return usuarios;
    }

    public Usuario buscarPorEmail(String email) throws Exception {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE email=?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearUsuario(rs);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al buscar usuario por email: " + e.getMessage());
        }
        return null;
    }
  
    
    public boolean existeDni(String dni) throws Exception {
        String sql = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE dni = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, dni);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new Exception("Error al verificar DNI: " + e.getMessage());
        }
    }

    public boolean existeEmail(String email) throws Exception {
        String sql = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE email=?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new Exception("Error al verificar email: " + e.getMessage());
        }
    }
}

