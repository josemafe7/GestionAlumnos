
package gestionAlumnos.persistencia;

import com.mongodb.DB;
import com.mongodb.DBObject;
import gestionAlumnos.entidades.Alumno;
import java.util.List;

public interface GestionAlumnosDAO {
    
    public DB conexion();
    
    public void comprobarProfesor();
    
    public void comprobarUsuario();
    
    public boolean comprobarLogin(String usuario, String password);

    public List<Alumno> listaAlumnos();
    
    public void insertarAlumno(Alumno alumno);
    
    public void modificarAlumno(Alumno newAlumno, Alumno oldAlumno);
    
    public void borrarAlumno(Alumno alumno);
    
    public Alumno obtenerAlumno(DBObject alumnoDB);   
}
