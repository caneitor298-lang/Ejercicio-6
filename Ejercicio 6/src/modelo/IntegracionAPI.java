package modelo;

import java.util.Map;

// Lista con Polimorfismo
public interface IntegracionAPI
        extends Transaccional, Documentable, Auditable, Comparable<IntegracionAPI> {

    String getId();

    String getNombre();

    String getEndpoint();

    boolean isConectada();

    double getLatenciaPromedioMs();

    Map<String, String> getConfiguracion();

    String consultarEstado();
}
