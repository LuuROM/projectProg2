package proyecto.modelos;

public abstract class Usuario {
    private int id;
    private String nombre;
    private String apellido;
    private String email;

    public Usuario(String nombre, String apellido, String email, int id) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.id = id;
    }

    // Getters y Setters
  
    public int getId() { 
        return id; }
    public void setId(int id) { 
        this.id = id; }

    public String getNombre() { 
        return nombre; }
    public void setNombre(String nombre) { 
        this.nombre = nombre; }

    public String getApellido() {
        return apellido; }
    public void setApellido(String apellido) { 
        this.apellido = apellido; }

    public String getEmail() { 
        return email; }
    public void setEmail(String email) {
        this.email = email; }
}

