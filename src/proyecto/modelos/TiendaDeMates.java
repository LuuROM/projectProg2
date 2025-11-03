
package proyecto.modelos;

import java.time.LocalDate;
import java.util.Date;
import proyecto.dao.*;


public class TiendaDeMates {

    private String nombre;
    private String razonSocial;
    private String direccion;
    private static LocalDate fechaDeCreacion = LocalDate.of(2025, 11, 1);
    private Administrador administrador;
    private static ProductoDAO productoDao = new ProductoDAO();

    public TiendaDeMates(String nombre, String razonSocial, String direccion, Administrador administrador) {
        this.nombre = nombre;
        this.razonSocial = razonSocial;
        this.direccion = direccion; 
        this.administrador = administrador;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Administrador getAdministrador() {
        return administrador;
    }

    public void setAdministrador(Administrador administrador) {
        this.administrador = administrador;
    }
    
    //Metodos
    public void agregarProducto(Producto p) throws Exception {
        productoDao.agregar(p);
    }
    
    ////////Colocar excepciones para el caso donde el producto no existe
    public void eliminarProducto(int id) throws Exception {
        productoDao.eliminar(id);
    }
    
    public void modificar(Producto p) throws Exception {
        productoDao.modificar(p);
    }
    
    public void modificar(int id, double nuevoPrecio) throws Exception {
        productoDao.modificar(id, nuevoPrecio);
    }
    
}