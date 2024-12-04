package monopoly.excepciones;

public class MovimientoInvalidoException extends JugadorException {
    public MovimientoInvalidoException(String mensaje) {
        super(mensaje);
    }
}