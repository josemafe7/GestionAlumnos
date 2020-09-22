
package gestionAlumnos.entidades;

public class AlumnoImpl implements Alumno{
    
    //atributo para el nombre del alumno
    private String nombre;
    
    //atributo para el apellido del alumno
    private String apellidos;
    
    //atributo para el profesor que imparte las clases al alumno
    private Profesor profesor;
    
    //atributo para las notas del alumno
    private Nota nota;

    public AlumnoImpl(String nombre, String apellidos,Nota nota) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.profesor = new ProfesorImpl("Alberto","Gonzalez Rivas");
        this.nota = nota;
    }
    
    public AlumnoImpl(String nombre, String apellidos,Profesor profesor,Nota nota) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.profesor = profesor;
        this.nota = nota;
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

    @Override
    public Profesor getProfesor() {
        return profesor;
    }

    @Override
    public void setProfesor(Profesor profesor) {
        this.profesor = profesor;
    }

    @Override
    public Nota getNota() {
        return nota;
    }

    @Override
    public void setNota(Nota nota) {
        this.nota = nota;
    }
    
}
