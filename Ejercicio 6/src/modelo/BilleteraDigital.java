package modelo;

import java.math.BigDecimal;

// Adaptador de una billetera digital
public class BilleteraDigital extends PasarelaPago {

    private String moneda;

    public BilleteraDigital(String id, String nombre, String endpoint,
                            String credencial, String moneda) {
        super(id, nombre, endpoint, credencial);
        if (moneda == null || moneda.isBlank()) {
            throw new IllegalArgumentException("La moneda es obligatoria.");
        }
        this.moneda = moneda;
        configurar("moneda", moneda);
    }

    @Override
    public String consultarEstado() {
        return "Billetera en " + moneda + ": "
                + (isConectada() ? "disponible" : "sin conexión");
    }

    @Override
    public ResultadoOperacion cobrar(BigDecimal monto) {
        return simularCobro(monto, "billetera " + moneda);
    }

    @Override
    public ResultadoOperacion reintegrar(String idOperacion, BigDecimal monto) {
        return simularReintegro(idOperacion, monto, "billetera " + moneda);
    }
}
