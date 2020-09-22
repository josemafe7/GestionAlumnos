
package gestionAlumnos.entidades;

public interface Alumno {
    
    public String getNombre();

    public void setNombre(String nombre);

    public String getApellidos();

    public void setApellidos(String apellidos);

    public Profesor getProfesor();
    
    public void setProfesor(Profesor profesor);

    public Nota getNota();

    public void setNota(Nota nota);

}
