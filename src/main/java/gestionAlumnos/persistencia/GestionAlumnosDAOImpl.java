
package gestionAlumnos.persistencia;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import gestionAlumnos.entidades.Alumno;
import gestionAlumnos.entidades.AlumnoImpl;
import gestionAlumnos.entidades.NotaImpl;
import gestionAlumnos.entidades.Profesor;
import gestionAlumnos.entidades.ProfesorImpl;
import gestionAlumnos.entidades.Usuario;
import gestionAlumnos.entidades.UsuarioImpl;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class GestionAlumnosDAOImpl implements GestionAlumnosDAO{
    
    static DBCursor cursor = null;
    DB db;
    MongoClient mongoClient;
    
    //método para la conexión con la base de datos
    @Override
    public DB conexion(){
        try {
        
            // Conectar al servidor MongoDB
            mongoClient = new MongoClient("localhost", 27017);

            // Conectar a la base de datos
            db = mongoClient.getDB("gestionAlumnos");
            

        } catch (UnknownHostException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        
        return db;
    }
    
    //método que comprueba al entrar en la aplicación, si hay un profesor en la base de datos
    //en caso de que no, crea uno
    @Override
    public void comprobarProfesor(){
        db = conexion();
        //Obtencion coleccion "profesor"
        DBCollection collection = db.getCollection("profesor");
            
        // Obtenemos todos los documentos de la coleccion
        cursor = collection.find();
        
        //insertamos un documento en la colección profesor solo si no hay ninguno
        if(cursor.count()<=0){
            BasicDBObject profesorDB = new BasicDBObject();
            profesorDB.append("nombre","Alberto");
            profesorDB.append("apellidos","Gonzalez Rivas");
            //Insertar documento profesorDB en la coleccion "profesor"
            collection.insert(profesorDB);
        }
        
        if(cursor != null)
            cursor.close();
        
    }
    
    //método que comprueba al entrar en la aplicación, si hay un usuario en la base de datos
    //en caso de que no, crea usuarios
    @Override
    public void comprobarUsuario(){
        db = conexion();
        //Obtencion coleccion "usuario"
        DBCollection collection = db.getCollection("usuario");
            
        // Obtenemos todos los documentos de la coleccion
        cursor = collection.find();
        
        //insertamos documentos en la colección usuario solo si no hay ninguno
        if(cursor.count()<=0){
            BasicDBObject usuarioDB1 = new BasicDBObject();
            usuarioDB1.append("usuario","josema");
            usuarioDB1.append("password","josema1234");
            
            BasicDBObject usuarioDB2 = new BasicDBObject();
            usuarioDB2.append("usuario","alberto");
            usuarioDB2.append("password","alberto1234");
            //Insertar documento profesorDB en la coleccion "usuario"
            collection.insert(usuarioDB1);
            collection.insert(usuarioDB2);
        }
        
        if(cursor != null)
            cursor.close();
        
    }
    
    //método que recibe los parámetros de login y comprueba si coinciden en la base de datos
    @Override
    public boolean comprobarLogin(String usuario, String password){
        boolean enc = false;
        // Conectar a la base de datos
        db = conexion();
        //Obtencion coleccion "usuario"
        DBCollection collectionUsuario = db.getCollection("usuario");
        
        // Obtenemos todos los documentos de la coleccion usuario
        cursor = collectionUsuario.find();
        //Recorrido de todos los elementos de la coleccion usuario
        int i = 0;
        DBObject usuarios;
        while (cursor.hasNext() && !enc) {
            i++;
            usuarios = cursor.next();
            Usuario user = new UsuarioImpl((String)usuarios.get("usuario"),(String)usuarios.get("password"));
            //Si usuario y contraseña de un documuento coincide con los parametros de entrada, se deja de buscar
            if(user.getUsuario().equals(usuario) && user.getPassword().equals(password)){
                enc = true;
            }
        }
        if(cursor != null)
            cursor.close();
        
        return enc;
    }

    //método DAO de consulta de todos los alumnos de la colección alumno de la base de datos
    @Override
    public List<Alumno> listaAlumnos(){
        
        List<Alumno>listadoAlumnos = new ArrayList<>();
        // Conectar a la base de datos
        db = conexion();
        //Obtencion coleccion "alumno"
        DBCollection collectionAlumno = db.getCollection("alumno");
        // Obtenemos todos los documentos de la coleccion alumno
        cursor = collectionAlumno.find();
        //Recorrido de todos los elementos de la coleccion alumno
        int i = 0;
        DBObject alumnos;
        while (cursor.hasNext()) {
            i++;
            alumnos = cursor.next();
            //llama al método que se encarga de convertir en entidades java los objetos de mongodb
            Alumno alu = obtenerAlumno(alumnos);
            listadoAlumnos.add(alu);
        }
        if(cursor != null)
            cursor.close();
        
        return listadoAlumnos;
    }
    
    //método DAO que inserta un alumno en la base de datos
    @Override
    public void insertarAlumno(Alumno alumno){
        
        // Conectar a la base de datos
        db = conexion();
        
        //Crea documento nota
        BasicDBObject notaDB = new BasicDBObject();
        notaDB.append("notaEb",Float.toString(alumno.getNota().getNotaEb()));
        notaDB.append("notaEpd",Float.toString(alumno.getNota().getNotaEpd()));
        notaDB.append("notaFinal",Float.toString(alumno.getNota().getNotaFinal()));
        //Obtencion coleccion "nota"
        DBCollection collectionNota = db.getCollection("nota");
        //Insertar documento notaDB en la coleccion "nota"
        collectionNota.insert(notaDB);
        
        //Crea documento profesor
        BasicDBObject profesorDB = new BasicDBObject();
        profesorDB.append("nombre",alumno.getProfesor().getNombre());
        profesorDB.append("apellidos",alumno.getProfesor().getApellidos());
        //Obtencion coleccion "profesor"
        DBCollection collectionProfesor = db.getCollection("profesor");
        //Insertar documento profesorDB en la coleccion "profesor"
        collectionProfesor.insert(profesorDB);
        
        //Crea documento alumno, que incluye los documentos profesor y nota
        BasicDBObject alumnoDB = new BasicDBObject();
        alumnoDB.append("nombre",alumno.getNombre());
        alumnoDB.append("apellidos",alumno.getApellidos());
        alumnoDB.append("profesor",profesorDB);
        alumnoDB.append("nota", notaDB);
        //Obtencion coleccion "alumno"
        DBCollection collectionAlumno = db.getCollection("alumno");
        //Insertar documento alumnoDB en la coleccion "alumno"
        collectionAlumno.insert(alumnoDB);
    }
    
    //método DAO que modifica un alumno en la base de datos
    //recibe dos alumnos, uno con los datos nuevos y otro con los antiguos
    @Override
    public void modificarAlumno(Alumno newAlumno, Alumno oldAlumno){
        
        // Conectar a la base de datos
        db = conexion();
        
        //Obtencion coleccion "alumno"
        DBCollection collectionAlumno = db.getCollection("alumno");
        
        //crear documento nota del alumno
        BasicDBObject notaDB = new BasicDBObject();
        notaDB.append("notaEb",Float.toString(newAlumno.getNota().getNotaEb()));
        notaDB.append("notaEpd",Float.toString(newAlumno.getNota().getNotaEpd()));
        notaDB.append("notaFinal",Float.toString(newAlumno.getNota().getNotaFinal()));
        //Obtencion coleccion "nota"
        DBCollection collectionNota = db.getCollection("nota");
        //Insertar documento notaDB en la coleccion "nota"
        collectionNota.insert(notaDB);
         
        //Actualizacion del documento Alumno indicado
        //creamos un objeto con los nuevos datos del alumno a modificar
        BasicDBObject alumno = new BasicDBObject();
        alumno.append("$set", new BasicDBObject().append("nombre", newAlumno.getNombre()).append("apellidos", newAlumno.getApellidos()).append("nota", notaDB));
        //esto nos busca en la base de datos el alumno antiguo a modificar y actualizar
        BasicDBObject query = new BasicDBObject();
        query.put("nombre", oldAlumno.getNombre());
        query.put("apellidos", oldAlumno.getApellidos());
        collectionAlumno.update(query, alumno);
        
        //Finalmente eliminamos el objeto nota del alumno anterior de la base de datos
        collectionNota.remove(new BasicDBObject().append("notaEb", Float.toString(oldAlumno.getNota().getNotaEb())).append("notaEpd", Float.toString(oldAlumno.getNota().getNotaEpd())));
    }
    
    //método DAO que permite borrar un alumno de la base de datos
    @Override
    public void borrarAlumno(Alumno alumno){
        // Conectar a la base de datos
        db = conexion();
        
        //Obtencion coleccion "alumno"
        DBCollection collectionAlumno = db.getCollection("alumno");
        
        //Obtencion coleccion "nota"
        DBCollection collectionNota = db.getCollection("nota");
        
        //Borramos el alumno de la coleccion
        collectionAlumno.remove(new BasicDBObject().append("nombre", alumno.getNombre()).append("apellidos", alumno.getApellidos()));
        
        //Tambine borramos el documento nota
        collectionNota.remove(new BasicDBObject().append("notaEb", Float.toString(alumno.getNota().getNotaEb())).append("notaEpd", Float.toString(alumno.getNota().getNotaEpd())));
        
    }
    
    //Obtiene un objeto Alumno de la base de datos MongoDB
    @Override
    public Alumno obtenerAlumno(DBObject alumnoDB){
        //primero extraemos los atributos String
        String nombre = (String)alumnoDB.get("nombre");
        String apellidos = (String)alumnoDB.get("apellidos");
        //extraemos el profesor, que al ser una coleccion dentro de otra, tenemos que tratarlo de la forma siguiente:
        DBObject profesorDB = (DBObject)alumnoDB.get("profesor");
        Profesor profesor = new ProfesorImpl((String)profesorDB.get("nombre"),(String)profesorDB.get("apellidos"));
        //extraemos la nota, que al ser una coleccion dentro de otra, tenemos que tratarlo de la forma siguiente:
        DBObject notaDB = (DBObject)alumnoDB.get("nota");
        NotaImpl nota = new NotaImpl(Float.parseFloat((String)notaDB.get("notaEb")),Float.parseFloat((String)notaDB.get("notaEpd")),Float.parseFloat((String)notaDB.get("notaFinal")));
        Alumno alu = new AlumnoImpl(nombre,apellidos,profesor,nota);
        return alu;
    }
    
    
    
}