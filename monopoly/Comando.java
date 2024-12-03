package monopoly;
import partida.*;

public interface Comando {

    public void turnoJugador();
    public void listarJugadores();
    public void listarAvatares();
    public void lanzarDados();
    public void cambiarModo();
    public void acabarTurno();
    public void salirCarcel();
    public void descJugador(String[] nombre);
    public void descAvatar(String nombre);
    public void descCasilla(String nombre);
    public void estadisticasjugador(String nombre);
    public void listarVenta();
    public void comprar(String nombre);
    public void hipotecar(String nombre);
    public void deshipotecar(String nombre);
    public void bancarrota(Jugador solicitante, Jugador demandante);
    public void edificarCasilla(String edificio);
    public void listarEdificios();
    public void listarEdificiosGrupo(String grupo);
    public void venderEdificios(String tipoedificio, String nombre, String cantidad);
    public void infotratos();
    public void estadisticasjuego();
    public void verTablero();
    public void trato(String jugador,String inter1, String inter2);
    public void trato(String jugador,String inter1, String inter2, String inter3,String inter4);
    public void aceptarTrato(String idtrato);
    public void eliminarTrato(String idtrato);
    public void lanzarDados(int valor1, int valor2);
    public void lanzarDadosEsp(int valor1, int valor2);

}