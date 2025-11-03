
package proyecto.modelos;

public class Producto extends EntidadBase {
    //Atributos
    private String material,color,nombre;
    private double precio;
    private int stock;
    //Consttructor
    public Producto(String material, String color, String nombre, int stock, int id, double precio) {
        super(id);
        this.material = material;
        this.color = color;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
    }
    //Getters and setters
    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }  
    //Metodos
    public void mostrarInfo() {
        System.out.println("================================");
        System.out.println("Producto ID: " + getId());
        System.out.println("Nombre: " + getNombre());
        System.out.println("Material: " + getMaterial());
        System.out.println("Color: " + getColor());
        System.out.println("Precio: $" + getPrecio());
        System.out.println("Stock: " + getStock());
        System.out.println("================================");
    }

}
