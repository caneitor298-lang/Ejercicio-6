package modelo;

import java.math.BigDecimal;

//adaptador de transferencias bancarias como pasarela de pago
public class TransferenciaBancaria extends PasarelaPago {

    private String banco;

    public TransferenciaBancaria(String id, String nombre, String endpoint,
                                String credencial, String banco) {
        super(id, nombre, endpoint, credencial);
        if (banco == null || banco.isBlank()) {
            throw new IllegalArgumentException("El banco es obligatorio.");
        }
        this.banco = banco;
        configurar("banco", banco);
    }

    @Override
    public String consultarEstado() {
        return "Transferencias de " + banco + ": "
                + (isConectada() ? "disponibles" : "sin conexión");
    }

    @Override
    public ResultadoOperacion cobrar(BigDecimal monto) {
        return simularCobro(monto, "transferencia de " + banco);
    }

    @Override
    public ResultadoOperacion reintegrar(String idOperacion, BigDecimal monto) {
        return simularReintegro(idOperacion, monto, "transferencia de " + banco);
    }
}
