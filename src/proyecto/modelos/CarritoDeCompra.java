package proyecto.modelos;

import java.util.ArrayList;
import java.util.List;

public class CarritoDeCompra {
    
    private List<Producto> productos = new ArrayList<>();

    public CarritoDeCompra() {
    } 
    // Metodos
    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }
    
    public void agregarProducto(Producto p, int cantidad) {
        Producto nuevoP = new Producto(p.getMaterial(), p.getColor(), p.getNombre(), cantidad, p.getId(), p.getPrecio());
        productos.add(nuevoP);
    }

    public void eliminarProducto(Producto p, int cantidad) {
        productos.remove(p);
    }
    
    public void mostrarProductos() {
        if (productos.isEmpty()) {
            System.out.println("El carrito está vacío.");
            return;
        }
        System.out.println("=== Productos en el Carrito ===");
        for (Producto p : productos) {
            System.out.println("- " + p.getNombre() + " " + p.getColor() + " de " + p.getMaterial() + " (" + p.getStock() + "x" + p.getPrecio() + ")"); 
        }
        System.out.println("=============================");
    }
    
    public double calcularTotal() {
        double total = 0;
        for (Producto p : productos) {
            total += p.getPrecio()*p.getStock();
        }
        return total;
    }
    
    // Se renombra para mayor claridad
    public void vaciarCarrito() {
        productos.clear();
    }
    
    public boolean estaVacio() {
        return productos.isEmpty();
    }
}