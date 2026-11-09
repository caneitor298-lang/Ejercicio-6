package modelo;

import java.math.BigDecimal;

// Contrato financiero con una respuesta segura para operaciones no disponibles. 
public interface Transaccional {

    default ResultadoOperacion cobrar(BigDecimal monto) {
        return new ResultadoOperacion(
                EstadoResultado.IGNORADA,
                "Esta integración no permite realizar cobros.",
                ""
        );
    }

    default ResultadoOperacion reintegrar(String idOperacion, BigDecimal monto) {
        return new ResultadoOperacion(
                EstadoResultado.IGNORADA,
                "Esta integración no permite realizar reintegros.",
                ""
        );
    }
}
