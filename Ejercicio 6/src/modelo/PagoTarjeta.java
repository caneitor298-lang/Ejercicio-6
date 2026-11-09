package modelo;

import java.math.BigDecimal;

// Adaptador de una pasarela de tarjetas (las respuestas son simuladas)
public class PagoTarjeta extends PasarelaPago {

    private String red;

    public PagoTarjeta(String id, String nombre, String endpoint,
                       String credencial, String red) {
        super(id, nombre, endpoint, credencial);
        if (red == null || red.isBlank()) {
            throw new IllegalArgumentException("La red de tarjeta es obligatoria.");
        }
        this.red = red;
        configurar("red", red);
    }

    @Override
    public String consultarEstado() {
        return "Pasarela de tarjetas " + red + ": "
                + (isConectada() ? "disponible" : "sin conexión");
    }

    @Override
    public ResultadoOperacion cobrar(BigDecimal monto) {
        return simularCobro(monto, "tarjeta " + red);
    }

    @Override
    public ResultadoOperacion reintegrar(String idOperacion, BigDecimal monto) {
        return simularReintegro(idOperacion, monto, "tarjeta " + red);
    }
}
