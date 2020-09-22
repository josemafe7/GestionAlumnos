/*
Trabajo Proyecto Gestión de Alumnos

*/

/*
@author: José_Manuel_Fernández_Labrador
*/


package gestionAlumnos;

import javax.servlet.annotation.WebServlet;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.WrappedSession;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import gestionAlumnos.entidades.Alumno;
import gestionAlumnos.entidades.AlumnoImpl;
import gestionAlumnos.entidades.Nota;
import gestionAlumnos.entidades.NotaImpl;
import gestionAlumnos.persistencia.GestionAlumnosDAO;
import gestionAlumnos.persistencia.GestionAlumnosDAOImpl;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * This UI is the application entry point. A UI may either represent a browser window 
 * (or tab) or some part of a html page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be 
 * overridden to add component to the user interface and initialize non-component functionality.
 */

//Clase principal de UI que administra las distintas vistas y los atributos globales
@Theme("tests-valo-facebook") //Theme Valo para añadir estilo al sitio
public class SitioWeb extends UI implements Broadcaster.BroadcastListener{

    //Nos permite navegar por distintas vistas
    Navigator navigator;
    
    //vista de login
    protected static final String ACCESO = "acceso";
    //vista principal del usuario
    protected static final String PORTAL = "portal";
    
    //el objeto de una sesion
    protected static WrappedSession sesion;
    
    //objeto para la persistencia de la aplicación
    GestionAlumnosDAO alumnosDAO = new GestionAlumnosDAOImpl();
    
    //lista para mantener en memoria la entidad Asignatura
    List<Alumno> listadoAlumnos = new ArrayList<>();
    
    @Override
    protected void init(VaadinRequest vaadinRequest) {
        
        //inserta un profesor en la base de datos en caso que no haya ninguno
        alumnosDAO.comprobarProfesor();
        
        //inserta usuarios en la base de datos en caso que no haya ninguno
        alumnosDAO.comprobarUsuario();
        
        navigator = new Navigator(this, this);
        
        sesion = getSession().getSession();
        navigator.addView(ACCESO, new Acceso()); 
        navigator.addView(PORTAL, new Portal());
        
        navigator.navigateTo(ACCESO);
    }

    //método obligatorio a implementar la clase para el Push Server
    @Override
    public void receiveBroadcast(String message) {
    }

    @WebServlet(urlPatterns = "/*", name = "SitioWeb", asyncSupported = true)
    @VaadinServletConfiguration(ui = SitioWeb.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
    
    //Clase para la vista de Login
    public class Acceso extends VerticalLayout implements View {
      
        public Acceso() {
            getPage().setTitle("Acceso Profesor");
            
            Label titulo = new Label("ACCESO PROFESOR");

            //campo para introducir el nombre de usuario
            TextField usuario = new TextField();
            usuario.setCaption("Nombre de usuario");
            
            //campo para introducir la contraseña de usuario
            PasswordField password = new PasswordField();
            usuario.setCaption("Contraseña");
            
            VerticalLayout loginError = new VerticalLayout();

            //boton para acceder a la pagina principal de usuario profesor
            Button iniciarSesion = new Button("Iniciar Sesión", (Button.ClickEvent event) -> {
                loginError.removeAllComponents();
                //Comprobamos usuario y contraseña en la base de datos, si no es correcto, muestra error
                if(alumnosDAO.comprobarLogin(usuario.getValue(), password.getValue())){
                    //crea una sesion            
                    sesion.setAttribute("usuario",usuario.getValue());

                    //generamos la fecha y hora actual 
                    Calendar calendario = Calendar.getInstance();
                    int anyo = calendario.get(Calendar.YEAR);
                    int mes = calendario.get(Calendar.MONTH);
                    int dia = calendario.get(Calendar.DAY_OF_MONTH);
                    int hora = calendario.get(Calendar.HOUR_OF_DAY);
                    int minuto = calendario.get(Calendar.MINUTE);
                    String log = ""+anyo+"-"+mes+"-"+dia+" "+hora+":"+minuto+" - "+usuario.getValue();

                    //envía el Push Server la cadena con fecha, hora y usuario
                    Broadcaster.broadcast(log);

                    //vacía el campo de login de usuario
                    usuario.setValue("");

                    //accede a la vista de la página principal del usuario
                    navigator.navigateTo(PORTAL);
                }else{
                    Label error = new Label("<p style='color: red;'>Usuario y/contraseña incorrectos</p>",ContentMode.HTML);
                    loginError.addComponents(error);
                }
            });
            
            addComponents(titulo,usuario,password,loginError,iniciarSesion);
        }

        @Override
        public void enter(ViewChangeListener.ViewChangeEvent event) {
            
        }

    }
    
    //Clase de la vista principal de usuario profesor
    public class Portal extends VerticalLayout implements View {
    
        
        public Portal() {
          
            
            getPage().setTitle("Portal Alumnos Matriculados");
            Label titulo = new Label("PORTAL ALUMNOS MATRICULADOS");
            
            //Permite cerrar sesión
            Button cerrarSesion = new Button("Cerrar Sesión", (Button.ClickEvent event) -> {
                //Elimina el atributo de la sesión actual y vuelve a la vista de login
                sesion.removeAttribute("usuario");
                navigator.navigateTo(ACCESO);
                
                //**IMPORTANTE: destruir una sesión con invalidate() hace que sea necesario
                //cargar la página para volver a entrar con otro usuario, y considero
                //que eso no es la funcionalidad que se pide, puesto que se debe probar
                //que entren varios usuarios
                //sesion.invalidate();
            });
            addComponents(titulo,cerrarSesion);
            
            //Creamos el layout para la tabla donde vamos a mostrar los alumnos
            VerticalLayout mostrar = new VerticalLayout();
            mostrar.setMargin(true);
            mostrar.setSpacing(true);
            //tabla alumnos, con 6 columnas como podemos ver
            Table tablaAlumnos = new Table("ALUMNOS");
            tablaAlumnos.addContainerProperty("Nombre", String.class, null);
            tablaAlumnos.addContainerProperty("Apellidos", String.class, null);
            tablaAlumnos.addContainerProperty("Profesor", String.class, null);
            tablaAlumnos.addContainerProperty("Nota EB", Float.class, null);
            tablaAlumnos.addContainerProperty("Nota EPD", Float.class, null);
            tablaAlumnos.addContainerProperty("NOTA FINAL", Float.class, null);

            //consultamos a través del DAO todos los alumnos de la base de datos, y lo mostramos en la tabla
            listadoAlumnos = alumnosDAO.listaAlumnos();
            for(int i=0; i<listadoAlumnos.size(); i++){
                    tablaAlumnos.addItem(new Object[]{listadoAlumnos.get(i).getNombre(), listadoAlumnos.get(i).getApellidos(), listadoAlumnos.get(i).getProfesor().getNombre()+" "+listadoAlumnos.get(i).getProfesor().getApellidos(), listadoAlumnos.get(i).getNota().getNotaEb(), listadoAlumnos.get(i).getNota().getNotaEpd(), listadoAlumnos.get(i).getNota().getNotaFinal()}, i);
            }
            tablaAlumnos.setPageLength(tablaAlumnos.size());
            tablaAlumnos.setSelectable(true);
            tablaAlumnos.setImmediate(true);
            mostrar.addComponents(tablaAlumnos);
            mostrar.setComponentAlignment(tablaAlumnos, Alignment.MIDDLE_CENTER);
          
            //Creamos un layout para los formularios de operaciones de alumnos
            //estara formado por 3 layout de formularios para añadir, editar y borrar
            HorizontalLayout formularios = new HorizontalLayout();
            addComponent(formularios);
            Label aAs = new Label("Registrar Alumno");
            //Formulario para registrar un alumno
            FormLayout form1 = new FormLayout();
            form1.setMargin(true);
            form1.setSpacing(true);
    
            TextField nombre = new TextField();
            nombre.setCaption("Nombre del alumno:");
            
            TextField apellidos = new TextField();
            apellidos.setCaption("Apellidos del alumno:");
            
            TextField notaEb = new TextField();
            notaEb.setCaption("Nota EB del alumno:");
            
            TextField notaEpd = new TextField();
            notaEpd.setCaption("Nota EPD del alumno:");
            
            VerticalLayout errorRegLay = new VerticalLayout();
            
            //Registra un alumno según los datos introducidos en el formulario
            Button insertar = new Button("Registrar Alumno", (Button.ClickEvent event) -> {
                errorRegLay.removeAllComponents();
                if(!nombre.getValue().isEmpty() && !apellidos.getValue().isEmpty() && !notaEb.getValue().isEmpty() && !notaEpd.getValue().isEmpty() && Float.parseFloat(notaEb.getValue())>=0 && Float.parseFloat(notaEb.getValue())<=10 && Float.parseFloat(notaEpd.getValue())>=0 && Float.parseFloat(notaEpd.getValue())<=10){
                    //Crea un objeto de la entidad nota
                    Nota nota = new NotaImpl(Float.parseFloat(notaEb.getValue()),Float.parseFloat(notaEpd.getValue()));
                    //Crea un objeto de la entidad alumno
                    Alumno alumno = new AlumnoImpl(nombre.getValue(),apellidos.getValue(),nota);
                    //Insertamos el alumno en la base de datos con la persistencia
                    alumnosDAO.insertarAlumno(alumno);
                    //Borramos la tabla y cargamos la lista de asignaturas
                    tablaAlumnos.removeAllItems();
                    //consultamos a través del DAO todos los alumnos de la base de datos, y lo mostramos en la tabla
                    listadoAlumnos = alumnosDAO.listaAlumnos();
                    for(int i=0; i<listadoAlumnos.size(); i++){
                        tablaAlumnos.addItem(new Object[]{listadoAlumnos.get(i).getNombre(), listadoAlumnos.get(i).getApellidos(), listadoAlumnos.get(i).getProfesor().getNombre()+" "+listadoAlumnos.get(i).getProfesor().getApellidos(), listadoAlumnos.get(i).getNota().getNotaEb(), listadoAlumnos.get(i).getNota().getNotaEpd(), listadoAlumnos.get(i).getNota().getNotaFinal()}, i);
                    }
                    tablaAlumnos.setPageLength(tablaAlumnos.size());
                    tablaAlumnos.setSelectable(true);
                    tablaAlumnos.setImmediate(true);
                    mostrar.addComponents(tablaAlumnos);
                    mostrar.setComponentAlignment(tablaAlumnos, Alignment.MIDDLE_CENTER);
                    nombre.setValue("");
                    apellidos.setValue("");
                    notaEb.setValue("");
                    notaEpd.setValue("");
                }
                else{
                    Label errorReg = new Label("<p style='color: red;'>Error de formato de datos introducidos</p>",ContentMode.HTML);
                    errorRegLay.addComponents(errorReg);
                }
            });
            form1.addComponents(aAs,nombre,apellidos,notaEb,notaEpd,insertar,errorRegLay);
            formularios.addComponent(form1);
            
            //Formulario para editar un alumno
            FormLayout form2 = new FormLayout();
            form2.setMargin(true);
            form2.setSpacing(true);
            Label eAs = new Label("Editar Alumno");
            TextField numEditar = new TextField();
            numEditar.setCaption("Introduce el numero de fila a editar: ");
            TextField nomAlu = new TextField();
            nomAlu.setCaption("Nombre Alumno: ");
            TextField apeAlu = new TextField();
            apeAlu.setCaption("Apellidos Alumno: ");
            TextField notaEbAlu = new TextField();
            notaEbAlu.setCaption("Nota EB Alumno: ");
            TextField notaEpdAlu = new TextField();
            notaEpdAlu.setCaption("Nota EPD Alumno: ");
            
            VerticalLayout errorEdLay = new VerticalLayout();
            
            //Según el número de fila introducido en el campo del formulario (le restamos 1 al valor introducido
            //porque las filas empiezan el índice en 0, y el usuario para editar la primera fila introduce un 1)
            //editamos el usuario correspondiente en la lista
            Button editar = new Button("Editar Alumno", (Button.ClickEvent event) -> {
                errorEdLay.removeAllComponents();
                if(tablaAlumnos.size()>=1){
                    if(!numEditar.getValue().isEmpty() && Integer.parseInt(numEditar.getValue())>=1 && Integer.parseInt(numEditar.getValue())<=tablaAlumnos.size() && !nomAlu.getValue().isEmpty() && !apeAlu.getValue().isEmpty() && !notaEbAlu.getValue().isEmpty() && !notaEpdAlu.getValue().isEmpty() && Float.parseFloat(notaEbAlu.getValue())>=0 && Float.parseFloat(notaEbAlu.getValue())<=10 && Float.parseFloat(notaEpdAlu.getValue())>=0 && Float.parseFloat(notaEpdAlu.getValue())<=10){
                        //Crea un objeto de la entidad nota
                        Nota nota = new NotaImpl(Float.parseFloat(notaEbAlu.getValue()),Float.parseFloat(notaEpdAlu.getValue()));
                        //Crea un objeto de la entidad alumno
                        Alumno alumno = new AlumnoImpl(nomAlu.getValue(),apeAlu.getValue(),nota);
                        //llamamos al metodo DAO y le enviamos el nuevo alumno introducido, y el antiguo correspondiente a esa fila
                        alumnosDAO.modificarAlumno(alumno,listadoAlumnos.get(Integer.parseInt(numEditar.getValue())-1));
                        //limpiamos la tabla antes de refrescarla
                        tablaAlumnos.removeAllItems();
                        //consultamos a través del DAO todos los alumnos de la base de datos, y lo mostramos en la tabla
                        listadoAlumnos = alumnosDAO.listaAlumnos();
                        for(int i=0; i<listadoAlumnos.size(); i++){
                            tablaAlumnos.addItem(new Object[]{listadoAlumnos.get(i).getNombre(), listadoAlumnos.get(i).getApellidos(), listadoAlumnos.get(i).getProfesor().getNombre()+" "+listadoAlumnos.get(i).getProfesor().getApellidos(), listadoAlumnos.get(i).getNota().getNotaEb(), listadoAlumnos.get(i).getNota().getNotaEpd(), listadoAlumnos.get(i).getNota().getNotaFinal()}, i);
                        }
                        tablaAlumnos.setPageLength(tablaAlumnos.size());
                        tablaAlumnos.setSelectable(true);
                        tablaAlumnos.setImmediate(true);
                        mostrar.addComponents(tablaAlumnos);
                        mostrar.setComponentAlignment(tablaAlumnos, Alignment.MIDDLE_CENTER);
                        numEditar.setValue("");
                        nomAlu.setValue("");
                        apeAlu.setValue("");
                        notaEbAlu.setValue("");
                        notaEpdAlu.setValue("");
                    }else{
                        Label errorEd = new Label("<p style='color: red;'>Error de formato de datos introducidos</p>",ContentMode.HTML);
                        errorEdLay.addComponents(errorEd);
                    }
                }else{
                    Label errorEd = new Label("<p style='color: red;'>Error, no hay alumnos registrados</p>",ContentMode.HTML);
                    errorEdLay.addComponents(errorEd);
                }
            });
            form2.addComponents(eAs,numEditar,nomAlu,apeAlu,notaEbAlu,notaEpdAlu,editar,errorEdLay);
            formularios.addComponent(form2);
            
            //Formulario para borrar un alumno
            FormLayout form3 = new FormLayout();
            form3.setMargin(true);
            form3.setSpacing(true);
            Label bAs = new Label("Borrar Alumno");
            TextField numBorrar = new TextField();
            numBorrar.setCaption("Introduce el numero de fila a borrar: ");
            
            VerticalLayout errorBoLay = new VerticalLayout();
            
            //Misma forma que el editar anterior
            Button borrar = new Button("Borrar Alumno", (Button.ClickEvent event) -> {
                errorBoLay.removeAllComponents();
                if(tablaAlumnos.size()>=1){
                    if(!numBorrar.getValue().isEmpty() && Integer.parseInt(numBorrar.getValue())>=1 && Integer.parseInt(numBorrar.getValue())<=tablaAlumnos.size()){
                        //enviamos el objeto alumno de la fila seleccionada
                        alumnosDAO.borrarAlumno(listadoAlumnos.get(Integer.parseInt(numBorrar.getValue())-1));
                        //limpiamos la tabla antes de resfrescar
                        tablaAlumnos.removeAllItems();
                        //consultamos a través del DAO todos los alumnos de la base de datos, y lo mostramos en la tabla
                        listadoAlumnos = alumnosDAO.listaAlumnos();
                        for(int i=0; i<listadoAlumnos.size(); i++){
                            tablaAlumnos.addItem(new Object[]{listadoAlumnos.get(i).getNombre(), listadoAlumnos.get(i).getApellidos(), listadoAlumnos.get(i).getProfesor().getNombre()+" "+listadoAlumnos.get(i).getProfesor().getApellidos(), listadoAlumnos.get(i).getNota().getNotaEb(), listadoAlumnos.get(i).getNota().getNotaEpd(), listadoAlumnos.get(i).getNota().getNotaFinal()}, i);
                        }
                        tablaAlumnos.setPageLength(tablaAlumnos.size());
                        tablaAlumnos.setSelectable(true);
                        tablaAlumnos.setImmediate(true);
                        mostrar.addComponents(tablaAlumnos);
                        mostrar.setComponentAlignment(tablaAlumnos, Alignment.MIDDLE_CENTER);
                        numBorrar.setValue("");
                    }else{
                        Label errorBo = new Label("<p style='color: red;'>Error de formato de datos introducidos</p>",ContentMode.HTML);
                        errorBoLay.addComponents(errorBo);
                    }
                }else{
                    Label errorBo = new Label("<p style='color: red;'>Error, no hay alumnos registrados</p>",ContentMode.HTML);
                    errorBoLay.addComponents(errorBo);
                }
            });
            form3.addComponents(bAs,numBorrar,borrar,errorBoLay);
            formularios.addComponent(form3);
            
            

            addComponent(mostrar);
        }

        //Permite una notificación con bienvenida al usuario
        @Override
        public void enter(ViewChangeListener.ViewChangeEvent event) {
            Notification.show("Bienvenido "+(String)sesion.getAttribute("usuario"),Notification.Type.TRAY_NOTIFICATION);
        }
        
    }
}
