package proyecto.modelos;


public class Administrador extends Usuario {
    private int contrasenia;
    private TiendaDeMates tienda; 

    //Constructor

    public Administrador(int contrasenia, TiendaDeMates tienda, String nombre, String apellido, String email, int id) {
        super(nombre, apellido, email, id);
        this.contrasenia = contrasenia;
        this.tienda = tienda;
    }

    public int getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(int contrasenia) {
        this.contrasenia = contrasenia;
    }

    public TiendaDeMates getTienda() {
        return tienda;
    }

    public void setTienda(TiendaDeMates tienda) {
        this.tienda = tienda;
    }
    
    //Metodos
    public void agregarProducto(Producto p) throws Exception {
        tienda.agregarProducto(p);
    }
    
    public void eliminarProducto(int id) throws Exception {
        tienda.eliminarProducto(id);
    }
    
    public void modificar(Producto p) throws Exception {
        tienda.modificar(p);
    }
    
    public void modificar(int id, double nuevoPrecio) throws Exception {
        tienda.modificar(id, nuevoPrecio);
    }
}
