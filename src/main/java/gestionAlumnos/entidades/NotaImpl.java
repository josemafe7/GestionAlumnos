
package gestionAlumnos.entidades;

public class NotaImpl implements Nota{
    
    //atributo para la nota de enseñanzas básicas
    private Float notaEb;
    
    //atributo para la nota de enseñanzas prácticas
    private Float notaEpd;
    
    //atributo para la nota final al hacer media de las anteriores
    private Float notaFinal;

    public NotaImpl(Float notaEb, Float notaEpd) {
        this.notaEb = notaEb;
        this.notaEpd = notaEpd;
        this.notaFinal = (notaEb + notaEpd) / 2;
    }
    
    public NotaImpl(Float notaEb, Float notaEpd, Float notaFinal) {
        this.notaEb = notaEb;
        this.notaEpd = notaEpd;
        this.notaFinal = notaFinal;
    }

    @Override
    public Float getNotaEb() {
        return notaEb;
    }

    @Override
    public void setNotaEb(Float notaEb) {
        this.notaEb = notaEb;
    }

    @Override
    public Float getNotaEpd() {
        return notaEpd;
    }

    @Override
    public void setNotaEpd(Float notaEpd) {
        this.notaEpd = notaEpd;
    }

    @Override
    public Float getNotaFinal() {
        return notaFinal;
    }

    @Override
    public void setNotaFinal(Float notaFinal) {
        this.notaFinal = notaFinal;
    }
    
    
}
