package modelo;

import java.math.BigDecimal;

//El motor va a trabajar con diversas interfaces sin importar la clase concreta del proveedor.
public class MotorCheckout {

    // Overloading: un nombre con distintas firmas y parámetros de tipo interfaz.
    public ResultadoOperacion procesar(Transaccional objetivo, BigDecimal monto) {
        if (objetivo == null) {
            return new ResultadoOperacion(EstadoResultado.ERROR,
                    "No se indicó una integración para el cobro.", "");
        }
        return objetivo.cobrar(monto);
    }

    public ResultadoOperacion procesar(Transaccional objetivo,
                                       String idOperacion, BigDecimal monto) {
        if (objetivo == null) {
            return new ResultadoOperacion(EstadoResultado.ERROR,
                    "No se indicó una integración para el reintegro.", "");
        }
        return objetivo.reintegrar(idOperacion, monto);
    }

    public ResultadoOperacion procesar(Documentable objetivo, String datos) {
        if (objetivo == null) {
            return new ResultadoOperacion(EstadoResultado.ERROR,
                    "No se indicó una integración para generar el documento.", "");
        }
        return objetivo.generarDocumento(datos);
    }

    public void registrar(Auditable objetivo, String evento) {
        if (objetivo != null) {
            objetivo.registrarEvento(evento);
        }
    }
}
