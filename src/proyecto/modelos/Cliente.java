package proyecto.modelos;

import proyecto.dao.UsuarioDao;

public class Cliente extends Usuario {
    private String direccion;
    private String telefono;
    private String dni;
    private CarritoDeCompra carrito;
    private UsuarioDao usuarioDao;

    public Cliente(String direccion, String telefono, String dni,
        String nombre, String apellido, String email, int id, CarritoDeCompra carrito) {
        super(nombre, apellido, email, id);
        this.direccion = direccion;
        this.telefono = telefono;
        this.dni = dni;
        this.carrito = carrito != null ? carrito : new CarritoDeCompra();
    }

    // Getters y Setters
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public CarritoDeCompra getCarrito() { return carrito; }
    public void setCarrito(CarritoDeCompra carrito) { this.carrito = carrito; }
    
    

    // Métodos de carrito
    public void agregarProducto(Producto p, int cantidad) { 
        carrito.agregarProducto(p, cantidad); 
    }
    public void eliminarProducto(Producto p, int cantidad) { 
        carrito.eliminarProducto(p,cantidad);
    }
    
    public void mostrarInfo() {
        System.out.println("================================");
        System.out.println("Cliente ID: " + getId());
        System.out.println("Nombre: " + getNombre());
        System.out.println("Apellido: " + getApellido());
        System.out.println("Dirección: " + getDireccion());
        System.out.println("Teléfono: " + getTelefono());
        System.out.println("DNI: " + getDni());
        System.out.println("Tipo: Común"); 
        System.out.println("================================");
    }
   
}
