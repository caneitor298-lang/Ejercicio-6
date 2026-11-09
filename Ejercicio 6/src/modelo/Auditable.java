package modelo;

/** Contrato para registrar eventos en una bitácora. */
public interface Auditable {

    void registrarEvento(String evento);
}
