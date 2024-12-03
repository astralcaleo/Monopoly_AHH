package monopoly;

public class SaldoInsuficienteException extends JugadorException {
    public SaldoInsuficienteException(String mensaje) {
        super(mensaje);
    }
}