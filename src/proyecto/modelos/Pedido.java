
package proyecto.modelos;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// Hereda de EntidadBase
public class Pedido extends EntidadBase {
    
    private Usuario usuario; 
    private String metodoDePago;
    private List<Producto> productos = new ArrayList();
    private Date fechaPedido; 
    private SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
    private String fechaStr = sdf.format(this.getFechaPedido() != null ? this.getFechaPedido() : new Date());
    private double total;
    private String estado; 

    public Pedido(int id, Usuario usuario, List<Producto> productos, double total, String metodoDePago, String estado) {
        super(id); 
        this.usuario = usuario;
        this.productos = (productos != null) ? new ArrayList<>(productos) : new ArrayList<>(); 
        this.fechaPedido = new Date(); 
        this.total = total;
        this.metodoDePago = metodoDePago;
        this.estado = estado;
    }

    public Pedido(Usuario usuario, List<Producto> productos, double total, String metodoDePago, String estado) {
        this(0, usuario, productos, total, metodoDePago, estado);
    }
    
    // Requisito: Encapsulamiento
    public Usuario getUsuario() { 
        return usuario; 
    }
    public List<Producto> getProductos() { 
        return productos; 
    }
    public double getTotal() { 
        return total; 
    }
    public String getEstado() { 
        return estado; 
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Date getFechaPedido() {
        return fechaPedido;
    }

    public void setFechaPedido(Date fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMetodoDePago() {
        return metodoDePago;
    }

    public void setMetodoDePago(String metodoDePago) {
        this.metodoDePago = metodoDePago;
    }
    
    public void mostrarInfo() {
        System.out.println("================================");
        System.out.println("Pedido ID: " + getId());
        System.out.println("Productos: ");
        for (Producto p : productos) {
            System.out.println("- " + p.getNombre() + " de " + p.getMaterial() + " " + p.getColor() + " (" + p.getStock() + ")");;
        }
        System.out.println("Total: " + getTotal());
        System.out.println("Cliente: " + getUsuario().getNombre() + " " + getUsuario().getApellido());
        System.out.println("Método de pago: " + getMetodoDePago());
        System.out.println("Fecha: " + getFechaPedido());
        System.out.println("Estado: " + getEstado());
        System.out.println("================================");
    }
}