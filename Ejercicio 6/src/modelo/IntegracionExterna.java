package modelo;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

//Ordenamiento de los APIs
public abstract class IntegracionExterna implements IntegracionAPI {

    private final String id;
    private String nombre;
    private String endpoint;
    private boolean conectada;
    private double latenciaPromedioMs;
    private Map<String, String> configuracion;

    protected IntegracionExterna(String id, String nombre, String endpoint) {
        if (id == null || id.isBlank() || nombre == null || nombre.isBlank()
                || endpoint == null || endpoint.isBlank()) {
            throw new IllegalArgumentException("El ID, nombre y endpoint son obligatorios.");
        }
        this.id = id.trim();
        this.nombre = nombre.trim();
        this.endpoint = endpoint.trim();
        this.conectada = true;
        this.latenciaPromedioMs = 0;
        this.configuracion = new LinkedHashMap<>();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    @Override
    public String getEndpoint() {
        return endpoint;
    }

    @Override
    public boolean isConectada() {
        return conectada;
    }

    @Override
    public double getLatenciaPromedioMs() {
        return latenciaPromedioMs;
    }

    @Override
    public Map<String, String> getConfiguracion() {
        // La copia protege los parámetros internos de cambios desde otras clases.
        return Collections.unmodifiableMap(new LinkedHashMap<>(configuracion));
    }

    public void setConectada(boolean valor) {
        conectada = valor;
    }

    public void setLatenciaPromedioMs(double valor) {
        if (!Double.isFinite(valor) || valor < 0) {
            throw new IllegalArgumentException("La latencia debe ser finita y no negativa.");
        }
        latenciaPromedioMs = valor;
    }

    public void configurar(String clave, String valor) {
        if (clave == null || clave.isBlank() || valor == null) {
            throw new IllegalArgumentException("El parámetro debe tener clave y valor.");
        }
        configuracion.put(clave.trim(), valor);
    }

    @Override
    public void registrarEvento(String evento) {
        // java.util.logging escribe la bitácora; no se crea otra lista de APIs.
        Logger.getLogger("IntegracionesAPI").info("[" + id + "] " + evento);
    }

    @Override
    public int compareTo(IntegracionAPI otra) {
        Objects.requireNonNull(otra, "La integración no puede ser nula.");
        int comparacion = Double.compare(latenciaPromedioMs, otra.getLatenciaPromedioMs());
        return comparacion != 0 ? comparacion : id.compareTo(otra.getId());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof IntegracionExterna)) {
            return false;
        }
        IntegracionExterna otra = (IntegracionExterna) obj;
        return id.equals(otra.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return id + " | " + nombre + " | " + (conectada ? "Conectada" : "Desconectada")
                + " | " + latenciaPromedioMs + " ms";
    }

    @Override
    public abstract String consultarEstado();
}
