package modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;


public class CatalogoIntegraciones {

    private List<IntegracionAPI> integraciones;

    public CatalogoIntegraciones() {
        integraciones = new ArrayList<>();
    }

    public boolean agregar(IntegracionAPI api) {
        if (api == null || buscarPorId(api.getId()).isPresent()) {
            return false;
        }
        integraciones.add(api);
        return true;
    }

    public Optional<IntegracionAPI> buscarPorId(String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }
        for (IntegracionAPI api : integraciones) {
            if (api.getId().equals(id.trim())) {
                return Optional.of(api);
            }
        }
        return Optional.empty();
    }

    public void recorrer(Consumer<IntegracionAPI> accion) {
        Objects.requireNonNull(accion, "La acción es obligatoria.");
        for (IntegracionAPI api : integraciones) {
            accion.accept(api);
        }
    }

    public void ordenarPorLatencia() {
        // Collections.sort utiliza compareTo y ordena esta misma lista.
        Collections.sort(integraciones);
    }

    public int getCantidad() {
        return integraciones.size();
    }
}
