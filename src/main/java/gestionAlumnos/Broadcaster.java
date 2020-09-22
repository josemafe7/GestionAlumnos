
package gestionAlumnos;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

//Clase auxiliar para hacer Push de una UI a otra UI distinta
//Para ello usa el patrón Singleton
public class Broadcaster {

    private static final List<BroadcastListener> listeners = new CopyOnWriteArrayList<BroadcastListener>();

    public static void register(BroadcastListener listener) {
        listeners.add(listener);
    }

    public static void unregister(BroadcastListener listener) {
        listeners.remove(listener);
    }

    public static void broadcast(final String message) {
        for (BroadcastListener listener : listeners) {
            listener.receiveBroadcast(message);
        }
    }

    //interfaz a implementar en las UI que realizan y reciben el push
    public interface BroadcastListener {

        public void receiveBroadcast(String message);
    }
}
