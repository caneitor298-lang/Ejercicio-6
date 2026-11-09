package modelo;

public interface Documentable {

    default ResultadoOperacion generarDocumento(String datos) {
        return new ResultadoOperacion(
                EstadoResultado.IGNORADA,
                "Esta integración no permite generar documentos.",
                ""
        );
    }
}
