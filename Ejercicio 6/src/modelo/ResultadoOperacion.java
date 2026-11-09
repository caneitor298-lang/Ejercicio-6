package modelo;

public class ResultadoOperacion {

    private final EstadoResultado estado;
    private final String mensaje;
    private final String contenido;

    public ResultadoOperacion(EstadoResultado estado, String mensaje, String contenido) {
        this.estado = estado;
        this.mensaje = mensaje;
        this.contenido = contenido;
    }

    public EstadoResultado getEstado() {
        return estado;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getContenido() {
        return contenido;
    }

    @Override
    public String toString() {
        return "Estado: " + estado
                + "\nMensaje: " + mensaje
                + "\nContenido: " + contenido;
    }
}
