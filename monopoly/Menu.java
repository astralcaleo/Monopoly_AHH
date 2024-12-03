/*MODIFICACIONES PENDIENTES
 * - Movimientos avanzados
 * - Estadísticas juego / jugador
 * - Mandar a la cárcel si saca 3 dados dobles
 * - POR DETERMINAR
 */

 // scanner cerrar ???????

package monopoly;

import java.util.ArrayList;
import java.util.Arrays;

public class Menu {

    private Juego juego;
    private final ConsolaNormal consola; //final para que la referencia del atributo no pueda cambiar una vez que se le haya asignado un valor

    public Menu(ConsolaNormal consola){
        this.consola = consola;
        this.juego = new Juego(consola,this);
        iniciarPartida();
        while(juego.isFin()){
            juego.setFturno(false);
            juego.Turnos();
        }
        // this.scanner.close(); ?????????????????????????????????????????
    }

    // Método para inciar una partida: crea los jugadores y avatares.
    private void iniciarPartida() {
        consola.imprimir("Bienvenido al MONOPOLY ETSE by Antón Expósito, Helena Franco & Hugo Gilsanz\n");
        int jr;
        String js = "0";
        while(!js.equals("2") && !js.equals("3") && !js.equals("4") && !js.equals("5") && !js.equals("6")){
            js = consola.leer("\nIntroduce el número de jugadores para empezar la partida (entre 2 y 6): ");
            if(!js.equals("2") && !js.equals("3") && !js.equals("4") && !js.equals("5") && !js.equals("6")){
                consola.imprimir("Introduce un número válido de jugadores.");
            }
        }
        
        jr = Integer.parseInt(js);

        String comando; 
        while(jr != 0){
            consola.imprimir("\nJugadores restantes: " + jr);
            comando = consola.leer("Para añadir un jugador escriba: crear jugador \"NOMBRE\" \"TIPO\"\n");
            String[] partes = comando.split(" ");
            ArrayList<String> tipos = new ArrayList<>(Arrays.asList("sombrero", "coche", "esfinge", "pelota"));
            if (partes.length == 4 && partes[0].equals("crear") && partes[1].equals("jugador") && tipos.contains(partes[3])){
                juego.crearJugador(partes[2],partes[3]);
                --jr;
            } else {
                consola.imprimir("\nFormato incorrecto\n");
                if(!tipos.contains(partes[3])){consola.imprimir("Tipos de jugador permitidos: coche, esfinge, pelota y sombrero\n");}
            }
        }
        consola.imprimir("\nJugadores suficientes.\nComienza la partida...\n");

        for (int i = 0; i < jr; i++) {
            if (juego.getTratos().size() <= i) {
                juego.getTratos().add(new ArrayList<Trato>());  
            }
        }

        juego.verTablero();
    }

    @Override
    public String toString(){
        StringBuilder output = new StringBuilder();
        output.append("\nMENÚ\n\n");
        output.append("jugador\n");
        output.append("lanzar dados\n");
        output.append("cambiar modo\n");
        output.append("listar jugadores\n");
        output.append("listar avatares\n");
        output.append("describir jugador \"NOMBRE\"\n");
        output.append("describir avatar \"ID\"\n");
        output.append("describir \"CASILLA\"\n");
        output.append("estadisticas jugador \"NOMBRE\"\n");
        output.append("estadisticas juego\n");
        output.append("listar enventa\n");
        output.append("comprar " + juego.getAvatares().get(juego.getTurno()).getLugar().getNombre() + "\n");
        output.append("hipotecar " + juego.getAvatares().get(juego.getTurno()).getLugar().getNombre() + "\n");
        output.append("deshipotecar " + juego.getAvatares().get(juego.getTurno()).getLugar().getNombre() + "\n");
        output.append("edificar \"TIPOEDIFICIO\"\n");
        output.append("vender \"TIPOEDIFICIO\"" + juego.getAvatares().get(juego.getTurno()).getLugar().getNombre() + " \"CANTIDAD\"\n");
        output.append("listar edificios\n");
        output.append("listar edificios \"COLORGRUPO\"\n");
        output.append("trato \"NOMBREJUGADOR\": cambiar (\"____, ____\") \n");
        output.append("info tipos tratos\n");
        output.append("aceptar \"IDTRATO\"\n");
        output.append("declararse en bancarrota\n");
        output.append("salir carcel\n");
        output.append("ver tablero\n");
        output.append("acabar turno\n");
        return output.toString();
    }
}