package modelo;

import java.math.BigDecimal;
import java.util.UUID;

// Base de las pasarelas. Las subclases deben sobrescribir los métodos financieros. 
public abstract class PasarelaPago extends IntegracionExterna {

    protected String credencial;

    protected PasarelaPago(String id, String nombre, String endpoint, String credencial) {
        super(id, nombre, endpoint);
        if (credencial == null || credencial.isBlank()) {
            throw new IllegalArgumentException("La credencial es obligatoria.");
        }
        this.credencial = credencial;
        configurar("tipo", "Pasarela de pago");
        configurar("credencial", credencial);
    }

    @Override
    public abstract ResultadoOperacion cobrar(BigDecimal monto);

    @Override
    public abstract ResultadoOperacion reintegrar(String idOperacion, BigDecimal monto);

    // concentran las validaciones compartidas de las pasarelas.
    protected ResultadoOperacion simularCobro(BigDecimal monto, String canal) {
        ResultadoOperacion error = validarMonto(monto);
        if (error != null) {
            return error;
        }
        String identificador = getId() + "-COBRO-" + UUID.randomUUID();
        registrarEvento("Cobro simulado por " + monto.toPlainString() + " mediante " + canal);
        return new ResultadoOperacion(EstadoResultado.EXITO,
                "Cobro simulado de " + monto.setScale(2).toPlainString()
                        + " mediante " + canal + ".", identificador);
    }

    protected ResultadoOperacion simularReintegro(String idOperacion,
                                                 BigDecimal monto, String canal) {
        if (idOperacion == null || idOperacion.isBlank()) {
            return new ResultadoOperacion(EstadoResultado.ERROR,
                    "Debes indicar el ID de la operación original.", "");
        }
        ResultadoOperacion error = validarMonto(monto);
        if (error != null) {
            return error;
        }
        registrarEvento("Reintegro simulado de la operación " + idOperacion.trim());
        return new ResultadoOperacion(EstadoResultado.EXITO,
                "Reintegro simulado de " + monto.setScale(2).toPlainString()
                        + " mediante " + canal + ". Operación: " + idOperacion.trim(),
                getId() + "-REINTEGRO-" + UUID.randomUUID());
    }

    private ResultadoOperacion validarMonto(BigDecimal monto) {
        if (monto == null || monto.signum() <= 0
                || monto.stripTrailingZeros().scale() > 2) {
            return new ResultadoOperacion(EstadoResultado.ERROR,
                    "El monto debe ser positivo y tener hasta dos decimales.", "");
        }
        if (!isConectada()) {
            return new ResultadoOperacion(EstadoResultado.ERROR,
                    "La pasarela está desconectada.", "");
        }
        return null;
    }
}
