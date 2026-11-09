package app;

import java.math.BigDecimal;
import controlador.ControladorIntegraciones;
import modelo.CargaInicial;
import modelo.CatalogoIntegraciones;
import modelo.EstadoResultado;
import modelo.IntegracionAPI;
import modelo.MotorCheckout;
import modelo.ResultadoOperacion;
import vista.VistaConsola;


public class Aplicacion {

    public static void main(String[] args) {
        CatalogoIntegraciones catalogo = new CatalogoIntegraciones();
        CargaInicial.cargarEn(catalogo);

        if (args.length > 0 && "--demo".equals(args[0])) {
            demostrarCheckout(catalogo);
            return;
        }

        VistaConsola vista = new VistaConsola();
        ControladorIntegraciones controlador = new ControladorIntegraciones(catalogo, vista);
        controlador.iniciar();
    }

    private static void demostrarCheckout(CatalogoIntegraciones catalogo) {
        VistaConsola vista = new VistaConsola();
        MotorCheckout motor = new MotorCheckout();
        IntegracionAPI tarjeta = catalogo.buscarPorId("TAR001").orElseThrow();
        IntegracionAPI banco = catalogo.buscarPorId("BAN001").orElseThrow();
        IntegracionAPI billetera = catalogo.buscarPorId("BIL001").orElseThrow();
        IntegracionAPI logistica = catalogo.buscarPorId("LOG001").orElseThrow();
        IntegracionAPI fel = catalogo.buscarPorId("FEL001").orElseThrow();
        IntegracionAPI desconectada = catalogo.buscarPorId("BAN002").orElseThrow();

        vista.mostrarMensaje("=== DEMOSTRACIÓN DE CHECKOUT (SIMULACIÓN) ===");
        ResultadoOperacion cobro = motor.procesar(tarjeta, new BigDecimal("150.00"));
        vista.mostrarMensaje("\n1. Cobro con tarjeta\n" + cobro);
        if (cobro.getEstado() == EstadoResultado.EXITO) {
            vista.mostrarMensaje("\n2. Reintegro con tarjeta\n"
                    + motor.procesar(tarjeta, cobro.getContenido(), new BigDecimal("25.00")));
        }
        vista.mostrarMensaje("\n3. Cobro por transferencia\n"
                + motor.procesar(banco, new BigDecimal("75.50")));
        vista.mostrarMensaje("\n4. Cobro con billetera\n"
                + motor.procesar(billetera, new BigDecimal("40.00")));
        vista.mostrarMensaje("\n5. Reintegro a logística: debe ser IGNORADA\n"
                + motor.procesar(logistica, "OPERACION-DEMO", new BigDecimal("25.00")));
        vista.mostrarMensaje("\n6. El proceso continúa y genera una guía\n"
                + motor.procesar(logistica, "Destino: Guatemala; peso: 2 kg"));
        vista.mostrarMensaje("\n7. Generación simulada de factura FEL\n"
                + motor.procesar(fel, "Cliente de ejemplo; total: GTQ 150.00"));
        vista.mostrarMensaje("\n8. Documento en una pasarela: debe ser IGNORADA\n"
                + motor.procesar(tarjeta, "Documento de ejemplo"));
        vista.mostrarMensaje("\n9. Cobro con pasarela desconectada: debe ser ERROR\n"
                + motor.procesar(desconectada, new BigDecimal("10.00")));
        motor.registrar(logistica, "Checkout de demostración finalizado");
        vista.mostrarMensaje("\nDemostración finalizada. No se realizaron llamadas a APIs reales.");
    }
}
