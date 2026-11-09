package vista;

import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import modelo.IntegracionAPI;

// Se encarga de mostrar información y leer entradas, sin ordenar ni buscar. 
public class VistaConsola {

    private Scanner entrada;

    public VistaConsola() {
        entrada = new Scanner(System.in, "UTF-8");
    }

    public void mostrarMenu() {
        System.out.println("\n=== ADMINISTRACIÓN DE INTEGRACIONES ===");
        System.out.println("1. Listar integraciones y su conexión");
        System.out.println("2. Buscar API por identificador");
        System.out.println("3. Ordenar catálogo por latencia");
        System.out.println("4. Salir");
        System.out.print("Selecciona una opción: ");
    }

    public int leerOpcion() {
        if (!entrada.hasNextLine()) {
            // Si termina la entrada, salimos para evitar un ciclo infinito.
            return 4;
        }
        String texto = entrada.nextLine().trim();
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    public String leerId() {
        System.out.print("Identificador de la API (ejemplo: TAR001): ");
        return entrada.hasNextLine() ? entrada.nextLine().trim() : "";
    }

    public void mostrarIntegracion(IntegracionAPI api) {
        System.out.printf(Locale.ROOT, "%-7s | %-24s | %-13s | %8.2f ms%n",
                api.getId(), api.getNombre(),
                api.isConectada() ? "Conectada" : "Desconectada",
                api.getLatenciaPromedioMs());
    }

    public void mostrarConfiguracion(Map<String, String> datos) {
        System.out.println("Parámetros de configuración:");
        for (Map.Entry<String, String> parametro : datos.entrySet()) {
            String clave = parametro.getKey();
            String claveNormalizada = clave.toLowerCase(Locale.ROOT);
            boolean secreto = claveNormalizada.contains("credencial")
                    || claveNormalizada.contains("token")
                    || claveNormalizada.contains("password")
                    || claveNormalizada.contains("secret")
                    || claveNormalizada.contains("apikey");
            System.out.println("  " + clave + " = "
                    + (secreto ? "[OCULTO]" : parametro.getValue()));
        }
    }

    public void mostrarMensaje(String mensaje) {
        System.out.println(mensaje);
    }
}
