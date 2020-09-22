
package gestionAlumnos.entidades;

public class UsuarioImpl implements Usuario{
    
    //atributo para el nombre de usuario
    private String usuario;
    
    //atributo para la contraseña de usuario
    private String password;

    public UsuarioImpl(String usuario, String password) {
        this.usuario = usuario;
        this.password = password;
    }

    @Override
    public String getUsuario() {
        return usuario;
    }

    @Override
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }
    
    
}
