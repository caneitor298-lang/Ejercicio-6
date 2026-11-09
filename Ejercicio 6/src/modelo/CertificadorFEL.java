package modelo;

import java.util.UUID;


public class CertificadorFEL extends IntegracionExterna {

    private String nitEmisor;

    public CertificadorFEL(String id, String nombre, String endpoint, String nitEmisor) {
        super(id, nombre, endpoint);
        if (nitEmisor == null || nitEmisor.isBlank()) {
            throw new IllegalArgumentException("El NIT del emisor es obligatorio.");
        }
        this.nitEmisor = nitEmisor;
        configurar("tipo", "Certificador FEL");
        configurar("nitEmisor", nitEmisor);
    }

    @Override
    public String consultarEstado() {
        return "Certificador FEL para " + nitEmisor + ": "
                + (isConectada() ? "disponible" : "sin conexión");
    }

    @Override
    public ResultadoOperacion generarDocumento(String datos) {
        if (!isConectada()) {
            return new ResultadoOperacion(EstadoResultado.ERROR,
                    "El certificador FEL está desconectado.", "");
        }
        if (datos == null || datos.isBlank()) {
            return new ResultadoOperacion(EstadoResultado.ERROR,
                    "Debes indicar los datos de la factura.", "");
        }
        String xml = "<facturaSimulada id=\"" + UUID.randomUUID() + "\">"
                + "<nitEmisor>" + escaparXml(nitEmisor) + "</nitEmisor>"
                + "<datos>" + escaparXml(datos) + "</datos></facturaSimulada>";
        registrarEvento("Generación simulada de factura FEL");
        return new ResultadoOperacion(EstadoResultado.EXITO,
                "XML de factura simulado generado; sin validez fiscal.", xml);
    }

    private String escaparXml(String texto) {
        return texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&apos;");
    }
}
