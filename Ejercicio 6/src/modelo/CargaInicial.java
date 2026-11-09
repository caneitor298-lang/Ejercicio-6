package modelo;

/** Construye diez conexiones ficticias de cinco tipos sin crear otra lista. */
public class CargaInicial {

    public static void cargarEn(CatalogoIntegraciones catalogo) {
        registrar(catalogo, new PagoTarjeta("TAR001", "Tarjeta Uno",
                "https://tarjeta-uno.example/api", "demo-tarjeta-1", "Visa"), 245.0);
        registrar(catalogo, new PagoTarjeta("TAR002", "Tarjeta Dos",
                "https://tarjeta-dos.example/api", "demo-tarjeta-2", "Mastercard"), 310.5);

        registrar(catalogo, new TransferenciaBancaria("BAN001", "Banco Uno",
                "https://banco-uno.example/api", "demo-banco-1", "Banco de ejemplo A"), 180.0);
        TransferenciaBancaria bancoDos = new TransferenciaBancaria("BAN002", "Banco Dos",
                "https://banco-dos.example/api", "demo-banco-2", "Banco de ejemplo B");
        bancoDos.setConectada(false);
        registrar(catalogo, bancoDos, 420.0);

        registrar(catalogo, new BilleteraDigital("BIL001", "Billetera Uno",
                "https://billetera-uno.example/api", "demo-billetera-1", "GTQ"), 95.0);
        BilleteraDigital billeteraDos = new BilleteraDigital("BIL002", "Billetera Dos",
                "https://billetera-dos.example/api", "demo-billetera-2", "USD");
        billeteraDos.setConectada(false);
        registrar(catalogo, billeteraDos, 165.0);

        registrar(catalogo, new ProveedorLogistico("LOG001", "Envíos Nacionales",
                "https://envios-nacionales.example/api", "Nacional"), 280.0);
        ProveedorLogistico ultimaMilla = new ProveedorLogistico("LOG002", "Última Milla",
                "https://ultima-milla.example/api", "Ciudad de Guatemala");
        ultimaMilla.configurar("documentosHabilitados", "false");
        registrar(catalogo, ultimaMilla, 135.0);

        registrar(catalogo, new CertificadorFEL("FEL001", "Certificador Uno",
                "https://fel-uno.example/api", "DEMO-NIT-1"), 190.0);
        registrar(catalogo, new CertificadorFEL("FEL002", "Certificador Dos",
                "https://fel-dos.example/api", "DEMO-NIT-2"), 110.0);
    }

    private static void registrar(CatalogoIntegraciones catalogo,
                                  IntegracionExterna api, double latencia) {
        api.setLatenciaPromedioMs(latencia);
        api.configurar("ambiente", "Simulación");
        api.configurar("timeoutMs", "5000");
        api.configurar("versionAPI", "v1");
        if (!catalogo.agregar(api)) {
            throw new IllegalStateException("ID duplicado en la carga inicial: " + api.getId());
        }
    }
}
