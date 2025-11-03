package proyecto.modelos;


public class ClienteVip extends Cliente {
    private String contrasenia;
    private static final double DESCUENTO = 0.20;

    public ClienteVip(String direccion, String telefono, String dni, String nombre, String apellido, String email, int id, CarritoDeCompra carrito, String contrasenia) {
        super(direccion, telefono, dni, nombre, apellido, email, id, carrito);
        this.contrasenia = contrasenia;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    // Calcula total con 20% de descuento
    public double calcularTotalConDescuento(double total) {
        return total-(total*DESCUENTO);
    }
    
    @Override
    public void mostrarInfo() {
        System.out.println("================================");
        System.out.println("Cliente ID: " + getId());
        System.out.println("Nombre: " + getNombre());
        System.out.println("Apellido: " + getApellido());
        System.out.println("Dirección: " + getDireccion());
        System.out.println("Teléfono: " + getTelefono());
        System.out.println("DNI: " + getDni());
        System.out.println("Tipo: VIP"); 
        System.out.println("================================");
    }
}
