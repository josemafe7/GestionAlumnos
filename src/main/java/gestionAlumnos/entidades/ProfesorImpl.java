
package gestionAlumnos.entidades;


public class ProfesorImpl implements Profesor{
    
    //atributo para el nombre del profesor
    private String nombre;
    
    //atributo para el apellido del profesor
    private String apellidos;

    public ProfesorImpl(String nombre, String apellidos) {
        this.nombre = nombre;
        this.apellidos = apellidos;
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    @Override
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String getApellidos() {
        return apellidos;
    }

    @Override
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }
    
    
    
}
