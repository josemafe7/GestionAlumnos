package gestionAlumnos;

import gestionAlumnos.Broadcaster.BroadcastListener;
import com.vaadin.annotations.Push;
import javax.servlet.annotation.WebServlet;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Push
public class log extends UI implements BroadcastListener {

    //Aquí visualizamos el log, lo ponemos para que permita salto de línea
    final Label l = new Label("",Label.CONTENT_PREFORMATTED);

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        //Se registra como observador, para recibir los valores push
        Broadcaster.register(this); 
        final VerticalLayout layout = new VerticalLayout();
        layout.addComponent(l);
        setContent(layout);
    }

    @Override
    //Al implementar BroadcastListener, este metodo se ejecutará cada vez que se reciba un mensaje push
    public void receiveBroadcast(final String message) {
        access(new Runnable() {
            @Override
            public void run() {
                //En el label, introducimos el valor anterior, un salto de línea, y el mensaje recibido por push
                l.setValue(l.getValue()+"\n"+message);
            }
        });
    }

    @Override
    public void detach() {
        Broadcaster.unregister(this);
        super.detach();
    }

    @WebServlet(value = "/log/*", name = "log", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = log.class)
    public static class MyUIServlet extends VaadinServlet {
    }
}
