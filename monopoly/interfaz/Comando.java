package monopoly.interfaz;
import partida.*;

public interface Comando {

    public void turnoJugador();
    public void listarJugadores();
    public void listarAvatares();
    public void lanzarDados();
    public void cambiarModo();
    public void acabarTurno();
    public void salirCarcel() throws Exception;
    public void descJugador(String[] nombre) throws Exception;
    public void descAvatar(String nombre);
    public void descCasilla(String nombre);
    public void estadisticasjugador(String nombre) throws Exception;
    public void listarVenta();
    public void comprar(String nombre) throws Exception;
    public void hipotecar(String nombre) throws Exception;
    public void deshipotecar(String nombre) throws Exception;
    public void bancarrota(Jugador solicitante, Jugador demandante);
    public void edificarCasilla(String edificio) throws Exception;
    public void listarEdificios();
    public void listarEdificiosGrupo(String grupo);
    public void venderEdificios(String tipoedificio, String nombre, String cantidad);
    public void infotratos();
    public void estadisticasjuego();
    public void verTablero();
    public void trato(String jugador,String inter1, String inter2);
    public void trato(String jugador,String inter1, String inter2, String inter3,String inter4);
    public void tratos();
    public void aceptarTrato(String idtrato) throws Exception;
    public void eliminarTrato(String idtrato);
    public void lanzarDados(int valor1, int valor2) throws Exception;
    public void lanzarDadosEsp(int valor1, int valor2);

}