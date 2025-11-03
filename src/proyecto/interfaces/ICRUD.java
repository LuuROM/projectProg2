package proyecto.interfaces;

import java.util.List;
import proyecto.excepciones.DatosInvalidosException;

// Requisito: Interfaces que implementen polimorfismo
public interface ICRUD<T> {

    // CRUD Básico
    public void agregar(T entidad) throws DatosInvalidosException, Exception;
    void modificar(T entidad) throws DatosInvalidosException, Exception;
    void eliminar(int id) throws DatosInvalidosException, Exception;
    
    // Búsqueda y Filtrado
    List<T> buscarTodo() throws Exception;
    T buscar(int id) throws DatosInvalidosException, Exception;

}