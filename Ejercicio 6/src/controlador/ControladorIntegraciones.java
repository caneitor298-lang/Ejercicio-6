package controlador;

import java.util.Optional;
import modelo.CatalogoIntegraciones;
import modelo.IntegracionAPI;
import vista.VistaConsola;

// Coordina las opciones del menú entre el catálogo y la vista. 
public class ControladorIntegraciones {

    private CatalogoIntegraciones catalogo;
    private VistaConsola vista;

    public ControladorIntegraciones(CatalogoIntegraciones catalogo, VistaConsola vista) {
        this.catalogo = catalogo;
        this.vista = vista;
    }

    public void iniciar() {
        vista.mostrarMensaje("Prototipo académico: APIs y operaciones simuladas.");
        vista.mostrarMensaje("Integraciones cargadas: " + catalogo.getCantidad());
        boolean continuar = true;
        while (continuar) {
            vista.mostrarMenu();
            switch (vista.leerOpcion()) {
                case 1:
                    listar();
                    break;
                case 2:
                    buscar();
                    break;
                case 3:
                    ordenar();
                    break;
                case 4:
                    continuar = false;
                    vista.mostrarMensaje("Programa finalizado.");
                    break;
                default:
                    vista.mostrarMensaje("Opción inválida. Escribe un número del 1 al 4.");
            }
        }
    }

    private void listar() {
        vista.mostrarMensaje("\nID      | Nombre                   | Conexión      | Latencia");
        // Se recorre la lista del modelo, sin copiarla a la vista.
        catalogo.recorrer(vista::mostrarIntegracion);
        vista.mostrarMensaje("Total: " + catalogo.getCantidad());
    }

    private void buscar() {
        String id = vista.leerId();
        Optional<IntegracionAPI> resultado = catalogo.buscarPorId(id);
        if (resultado.isEmpty()) {
            vista.mostrarMensaje("No se encontró una API con ese identificador.");
            return;
        }
        IntegracionAPI api = resultado.get();
        vista.mostrarIntegracion(api);
        vista.mostrarMensaje("Endpoint: " + api.getEndpoint());
        vista.mostrarMensaje("Estado del servicio: " + api.consultarEstado());
        vista.mostrarConfiguracion(api.getConfiguracion());
    }

    private void ordenar() {
        catalogo.ordenarPorLatencia();
        vista.mostrarMensaje("Catálogo ordenado por latencia de menor a mayor.");
        listar();
    }
}
