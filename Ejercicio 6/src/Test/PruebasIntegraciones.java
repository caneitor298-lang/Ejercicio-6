package pruebas;

import java.io.StringReader;
import java.math.BigDecimal;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import modelo.*;
import org.xml.sax.InputSource;

/** Pruebas de comportamiento sin bibliotecas externas; se ejecutan con probar.bat. */
public class PruebasIntegraciones {

    private static int comprobaciones;

    public static void main(String[] args) throws Exception {
        Logger.getLogger("IntegracionesAPI").setLevel(Level.OFF);
        CatalogoIntegraciones catalogo = new CatalogoIntegraciones();
        CargaInicial.cargarEn(catalogo);
        verificar(catalogo.getCantidad() == 10, "Carga de diez integraciones");

        int[] tipos = new int[5];
        catalogo.recorrer(api -> {
            if (api instanceof PagoTarjeta) tipos[0]++;
            if (api instanceof TransferenciaBancaria) tipos[1]++;
            if (api instanceof BilleteraDigital) tipos[2]++;
            if (api instanceof ProveedorLogistico) tipos[3]++;
            if (api instanceof CertificadorFEL) tipos[4]++;
        });
        for (int cantidad : tipos) {
            verificar(cantidad == 2, "Dos objetos de cada tipo");
        }

        verificar(catalogo.buscarPorId("NO-EXISTE").isEmpty(), "ID inexistente");
        verificar(catalogo.buscarPorId("").isEmpty(), "ID vacío");
        verificar(catalogo.buscarPorId(null).isEmpty(), "ID nulo");
        IntegracionAPI tarjeta = catalogo.buscarPorId(" TAR001 ").orElseThrow();
        verificar(tarjeta.getNombre().equals("Tarjeta Uno"), "Búsqueda con espacios");
        verificar(!catalogo.agregar(null), "Rechazo de integración nula");
        verificar(!catalogo.agregar(new ProveedorLogistico("TAR001", "Duplicada",
                "https://duplicada.example", "Nacional")), "Rechazo de ID duplicado");
        verificar(catalogo.getCantidad() == 10, "Duplicados no modifican cantidad");

        catalogo.ordenarPorLatencia();
        double[] anterior = {-1};
        int[] visitadas = {0};
        catalogo.recorrer(api -> {
            verificar(api.getLatenciaPromedioMs() >= anterior[0], "Orden ascendente");
            if (visitadas[0] == 0) {
                verificar(api.getId().equals("BIL001"), "Primera API: menor latencia");
            }
            anterior[0] = api.getLatenciaPromedioMs();
            visitadas[0]++;
        });
        verificar(visitadas[0] == 10, "Recorrido completo sin copiar catálogo");

        MotorCheckout motor = new MotorCheckout();
        ResultadoOperacion cobro = motor.procesar(tarjeta, new BigDecimal("100.00"));
        estado(cobro, EstadoResultado.EXITO, "Cobro con tarjeta");
        verificar(!cobro.getContenido().isBlank(), "Identificador de cobro generado");
        estado(motor.procesar(tarjeta, cobro.getContenido(), new BigDecimal("25.00")),
                EstadoResultado.EXITO, "Reintegro con tarjeta");
        estado(motor.procesar(catalogo.buscarPorId("BAN001").orElseThrow(),
                new BigDecimal("12.34")), EstadoResultado.EXITO, "Transferencia");
        estado(motor.procesar(catalogo.buscarPorId("BIL001").orElseThrow(),
                new BigDecimal("12.34")), EstadoResultado.EXITO, "Billetera");
        estado(motor.procesar(tarjeta, new BigDecimal("1.2300")),
                EstadoResultado.EXITO, "Decimales equivalentes a centavos");
        estado(motor.procesar(tarjeta, BigDecimal.ZERO), EstadoResultado.ERROR, "Monto cero");
        estado(motor.procesar(tarjeta, new BigDecimal("-1")), EstadoResultado.ERROR, "Monto negativo");
        estado(motor.procesar(tarjeta, (BigDecimal) null), EstadoResultado.ERROR, "Monto nulo");
        estado(motor.procesar(tarjeta, new BigDecimal("1.001")),
                EstadoResultado.ERROR, "Monto con fracciones de centavo");
        estado(motor.procesar(tarjeta, "", BigDecimal.ONE), EstadoResultado.ERROR, "ID de reintegro vacío");
        estado(motor.procesar(tarjeta, null, BigDecimal.ONE), EstadoResultado.ERROR, "ID de reintegro nulo");
        estado(motor.procesar(catalogo.buscarPorId("BAN002").orElseThrow(), BigDecimal.ONE),
                EstadoResultado.ERROR, "Pasarela desconectada");

        ProveedorLogistico logistica = (ProveedorLogistico) catalogo.buscarPorId("LOG001").orElseThrow();
        IntegracionAPI fel = catalogo.buscarPorId("FEL001").orElseThrow();
        estado(motor.procesar(logistica, "COMPRA-1", BigDecimal.ONE),
                EstadoResultado.IGNORADA, "Reintegro a logística sin excepción");
        estado(motor.procesar(logistica, BigDecimal.ONE),
                EstadoResultado.IGNORADA, "Cobro a logística sin excepción");
        estado(motor.procesar(fel, "COMPRA-1", BigDecimal.ONE),
                EstadoResultado.IGNORADA, "Reintegro a FEL sin excepción");
        estado(motor.procesar(tarjeta, "Documento"), EstadoResultado.IGNORADA, "Documento en pasarela");
        ProveedorLogistico soloConsulta = (ProveedorLogistico) catalogo.buscarPorId("LOG002").orElseThrow();
        estado(motor.procesar(soloConsulta, "Envío de ejemplo"),
                EstadoResultado.IGNORADA, "API configurada para solo consultas");
        verificar(soloConsulta.cotizar("Guatemala", 1).signum() > 0,
                "API de solo consulta permite cotizar");

        String datos = "Cliente A & B <prueba> \"datos\" 'ejemplo'";
        ResultadoOperacion guia = motor.procesar(logistica, datos);
        estado(guia, EstadoResultado.EXITO, "Checkout continúa después de IGNORADA");
        verificar(xmlDatos(guia).equals(datos), "XML de guía escapa caracteres especiales");
        ResultadoOperacion factura = motor.procesar(fel, datos);
        estado(factura, EstadoResultado.EXITO, "Documento FEL");
        verificar(xmlDatos(factura).equals(datos), "XML de factura escapa caracteres especiales");
        estado(motor.procesar(logistica, ""), EstadoResultado.ERROR, "Guía sin datos");
        estado(motor.procesar(fel, (String) null), EstadoResultado.ERROR, "Factura sin datos");
        verificar(logistica.cotizar("Guatemala", 2).compareTo(new BigDecimal("35.00")) == 0,
                "Cotización válida");
        verificar(logistica.cotizar("", 2).signum() == 0, "Cotización sin destino");
        verificar(logistica.cotizar("Guatemala", Double.NaN).signum() == 0, "Peso NaN");
        logistica.setConectada(false);
        estado(motor.procesar(logistica, datos), EstadoResultado.ERROR, "Logística desconectada");
        verificar(logistica.cotizar("Guatemala", 2).signum() == 0, "Cotización desconectada");
        logistica.setConectada(true);
        ((IntegracionExterna) fel).setConectada(false);
        estado(motor.procesar(fel, datos), EstadoResultado.ERROR, "FEL desconectada");

        estado(motor.procesar((Transaccional) null, BigDecimal.ONE),
                EstadoResultado.ERROR, "Objetivo de cobro nulo");
        estado(motor.procesar((Transaccional) null, "ID", BigDecimal.ONE),
                EstadoResultado.ERROR, "Objetivo de reintegro nulo");
        estado(motor.procesar((Documentable) null, datos),
                EstadoResultado.ERROR, "Objetivo de documento nulo");

        Map<String, String> configuracion = tarjeta.getConfiguracion();
        try {
            configuracion.put("intruso", "valor");
            throw new AssertionError("La configuración expuesta debe ser de solo lectura");
        } catch (UnsupportedOperationException esperado) {
            verificar(!tarjeta.getConfiguracion().containsKey("intruso"), "Configuración protegida");
        }
        ProveedorLogistico mismaId = new ProveedorLogistico("LOG001", "Otro nombre",
                "https://otra.example", "Nacional");
        verificar(logistica.equals(mismaId), "equals compara identificadores");
        verificar(logistica.hashCode() == mismaId.hashCode(), "hashCode consistente con equals");
        verificar(!logistica.equals(tarjeta), "Identificadores distintos no son iguales");
        verificar(tarjeta.toString().contains("TAR001"), "toString de integración");
        verificar(cobro.toString().contains("EXITO"), "toString de resultado");

        ProveedorLogistico startup = new ProveedorLogistico("NUE001", "Nueva startup",
                "https://startup.example", "Zona 16");
        startup.setLatenciaPromedioMs(95);
        verificar(catalogo.agregar(startup), "Extensión mediante una nueva instancia");
        verificar(startup.compareTo(catalogo.buscarPorId("BIL001").orElseThrow()) > 0,
                "Desempate por identificador");
        estado(motor.procesar(startup, "Cliente nuevo"), EstadoResultado.EXITO, "Nuevo proveedor genera guía");
        estado(motor.procesar(startup, "ID", BigDecimal.ONE),
                EstadoResultado.IGNORADA, "Nuevo proveedor usa el mismo motor");
        comprobarLatenciaInvalida(startup, -1);
        comprobarLatenciaInvalida(startup, Double.NaN);
        comprobarLatenciaInvalida(startup, Double.POSITIVE_INFINITY);
        System.out.println("OK: " + comprobaciones + " comprobaciones superadas.");
    }

    private static void verificar(boolean condicion, String caso) {
        if (!condicion) {
            throw new AssertionError("Falló: " + caso);
        }
        comprobaciones++;
    }

    private static void estado(ResultadoOperacion resultado, EstadoResultado esperado, String caso) {
        verificar(resultado.getEstado() == esperado, caso);
    }

    private static String xmlDatos(ResultadoOperacion resultado) throws Exception {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(new InputSource(new StringReader(resultado.getContenido())))
                .getElementsByTagName("datos").item(0).getTextContent();
    }

    private static void comprobarLatenciaInvalida(IntegracionExterna api, double valor) {
        double anterior = api.getLatenciaPromedioMs();
        try {
            api.setLatenciaPromedioMs(valor);
            throw new AssertionError("Se aceptó una latencia inválida");
        } catch (IllegalArgumentException esperado) {
            verificar(api.getLatenciaPromedioMs() == anterior, "Latencia inválida rechazada sin modificar el valor");
        }
    }
}
