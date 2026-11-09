package modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

// Ofrece cotizaciones y guías, hereda las operaciones financieras ignoradas. 
public class ProveedorLogistico extends IntegracionExterna {

    private String cobertura;

    public ProveedorLogistico(String id, String nombre, String endpoint, String cobertura) {
        super(id, nombre, endpoint);
        if (cobertura == null || cobertura.isBlank()) {
            throw new IllegalArgumentException("La cobertura es obligatoria.");
        }
        this.cobertura = cobertura;
        configurar("tipo", "Proveedor logístico");
        configurar("cobertura", cobertura);
        configurar("documentosHabilitados", "true");
    }

    @Override
    public String consultarEstado() {
        return "Logística con cobertura " + cobertura + ": "
                + (isConectada() ? "disponible" : "sin conexión");
    }

    public BigDecimal cotizar(String destino, double pesoKg) {
        if (!isConectada() || destino == null || destino.isBlank()
                || !Double.isFinite(pesoKg) || pesoKg <= 0) {
            return BigDecimal.ZERO.setScale(2);
        }
        return new BigDecimal("25.00")
                .add(BigDecimal.valueOf(pesoKg).multiply(new BigDecimal("5.00")))
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public ResultadoOperacion generarDocumento(String datos) {
        // Algunas APIs logísticas solo ofrecen consultas y cotizaciones.
        if ("false".equalsIgnoreCase(getConfiguracion().get("documentosHabilitados"))) {
            return new ResultadoOperacion(EstadoResultado.IGNORADA,
                    "Esta API logística solo permite consultas y cotizaciones.", "");
        }
        if (!isConectada()) {
            return new ResultadoOperacion(EstadoResultado.ERROR,
                    "El proveedor logístico está desconectado.", "");
        }
        if (datos == null || datos.isBlank()) {
            return new ResultadoOperacion(EstadoResultado.ERROR,
                    "Debes indicar los datos del envío.", "");
        }
        String xml = "<guiaSimulada id=\"" + UUID.randomUUID() + "\">"
                + "<proveedor>" + escaparXml(getNombre()) + "</proveedor>"
                + "<datos>" + escaparXml(datos) + "</datos></guiaSimulada>";
        registrarEvento("Generación simulada de guía de envío");
        return new ResultadoOperacion(EstadoResultado.EXITO,
                "Guía de envío simulada generada.", xml);
    }

    private String escaparXml(String texto) {
        return texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&apos;");
    }
}
