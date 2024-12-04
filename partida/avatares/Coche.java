package partida.avatares;

import monopoly.*;
import monopoly.casillas.Casilla;
import monopoly.casillas.propiedades.*;
import partida.Jugador;

import java.util.ArrayList;

public class Coche extends Avatar {

    public Coche(Jugador jugador, Casilla lugar, ArrayList<Avatar> avCreados, Juego juego) {
        super("coche",jugador, lugar, avCreados, juego);
    }

    public void mover(int dado1, int dado2, Tablero tablero, Jugador banca) {
        int desplazamiento = dado1 + dado2;
    
        if (desplazamiento > 4) {
            // Avanza y realiza tiradas adicionales según las normas
            avanzarCoche(dado1, dado2, tablero,banca);
        } else {
            // Retrocede y aplica la penalización de 2 turnos
            retrocederCoche(dado1,dado2, tablero,banca);
        }
    }
    
    private void avanzarCoche(int dado1, int dado2, Tablero tablero, Jugador banca) {
        boolean compraRealizada = false;
        int tiradasExtra = 0;
        boolean ultimaTirada = false;
    
        do {
            int desplazamiento = dado1 + dado2;
            int posicionActual = this.getLugar().getPosicion();
            int nuevaPosicion = (posicionActual + desplazamiento) % 40;
            Casilla destino = tablero.encontrar_casilla(nuevaPosicion);
    
            getJuego().getConsola().imprimir("El avatar " + this.getID() + " avanza " + desplazamiento + " posiciones, desde " + this.getLugar().getNombre() + " hasta " + destino.getNombre());
    
            // Mover el avatar a la nueva posición
            this.moverAvatar(tablero.getPosiciones(), desplazamiento);
            this.setLugar(destino);
    
            // Evaluar la casilla
            destino.evaluarCasilla(this.getJugador(), desplazamiento, tablero, juego.getTurno(), juego);

            if((this.getLugar() instanceof Propiedad) && !compraRealizada) {
                getJuego().verTablero();
                System.out.println("\ncontinuar ");
                System.out.println("comprar " + this.getLugar().getNombre());
                System.out.println("hipotecar " + this.getLugar().getNombre());
                System.out.println("deshipotecar " + this.getLugar().getNombre());
                System.out.println("edificar \"TIPOEDIFICIO\"");
                System.out.println("vender \"TIPOEDIFICIO\"" + this.getLugar().getNombre() + " \"CANTIDAD\"\n");
    
                String comando = getJuego().getConsola().leer("Introduce comando:");
                String[] partes = comando.split(" ");
                while (comando.isEmpty()) {comando = getJuego().getConsola().leer(" ");}
                getJuego().analizarComando(comando);
                if(partes[0].equals("comprar")) compraRealizada = true;
    
            } else if((this.getLugar() instanceof Solar)){
                getJuego().verTablero();
                System.out.println("\ncontinuar ");
                System.out.println("hipotecar " + this.getLugar().getNombre());
                System.out.println("deshipotecar " + this.getLugar().getNombre());
                System.out.println("edificar \"TIPOEDIFICIO\"");
                System.out.println("vender \"TIPOEDIFICIO\"" + this.getLugar().getNombre() + " \"CANTIDAD\"\n");
                String comando = getJuego().getConsola().leer("Introduce comando:");
                while (comando.isEmpty()) {comando = getJuego().getConsola().leer(" ");}
                getJuego().analizarComando(comando);
            }

            // Comprobar si es la última tirada o si debe continuar
            if (tiradasExtra == 3 || desplazamiento <= 4) {
                ultimaTirada = true;
    
                // En la última tirada, evaluar dados dobles
                if (dado1 == dado2) {
                    getJuego().getConsola().imprimir("Dado doble en la última tirada, el avatar " + this.getID() + " puede lanzar una tirada extra.");
                    // Generar una tirada adicional si aplica
                    int[] nuevosDados = lanzarDados(); // Método para lanzar los dados
                    dado1 = nuevosDados[0];
                    dado2 = nuevosDados[1];
                    desplazamiento = dado1 + dado2;
    
                    // Comprobar si los nuevos dados son dobles (solo se permite una tirada extra)
                    if (dado1 == dado2) {
                        getJuego().getConsola().imprimir("En la tirada extra, se obtuvieron dados dobles nuevamente, pero no se permite continuar lanzando.");
                    }
                }
                break;
            }
    
            // Generar una nueva tirada si corresponde
            int[] nuevosDados = lanzarDados(); // Método para lanzar los dados
            dado1 = nuevosDados[0];
            dado2 = nuevosDados[1];
            tiradasExtra++;
    
        } while (!ultimaTirada && dado1 + dado2 > 4 && tiradasExtra < 4);
    }
    
    private void retrocederCoche(int dado1, int dado2, Tablero tablero, Jugador banca) {
        int desplazamiento = dado1 + dado2;
        int posicionActual = this.getLugar().getPosicion();
        int nuevaPosicion = ((posicionActual - desplazamiento) % 40 + 40) % 40;
        Casilla destino = tablero.encontrar_casilla(nuevaPosicion);
    
        getJuego().getConsola().imprimir("El avatar " + this.getID() + " retrocede " + desplazamiento + " posiciones, desde " + this.getLugar().getNombre() + " hasta " + destino.getNombre());
    
        // Mover el avatar a la nueva posición
        this.moverAvatar(tablero.getPosiciones(), -desplazamiento);
        this.setLugar(destino);
    
        // Evaluar la casilla
        destino.evaluarCasilla(this.getJugador(), desplazamiento, tablero, juego.getTurno(), juego);
    
        // Penalización: establecer 2 turnos bloqueados
        this.setTurnosBloqueados(2);
        getJuego().getConsola().imprimir("El avatar " + this.getID() + " no podrá lanzar los dados durante los próximos 2 turnos.");
    
        // No se permite lanzamiento adicional con dados dobles
        if (dado1 == dado2) {
            getJuego().getConsola().imprimir("Dados dobles durante el retroceso, pero no se permite lanzar nuevamente.");
        }
    }
    
    private int[] lanzarDados() {
        getJuego().getConsola().imprimir("Valor dados: ");
        int dado1 = getJuego().getConsola().leer();
        int dado2 = getJuego().getConsola().leer();
        return new int[]{dado1, dado2};
    }
    
    

    

}