package proyecto.modelos;


public abstract class EntidadBase {
    int id; 
    
    public EntidadBase(int id) {
        this.id = id;
    }
    
    public EntidadBase() {
        this.id = 0;
    }

    public int getId() { 
        return id; 
    }
    public void setId(int id) { 
        this.id = id;
    }
}
