package monopoly.cartas;

import java.util.ArrayList;
import java.util.Collections;

public class CartaSuerte extends Carta{
    // Atributos
    private static ArrayList<Carta> cartasSuerte;   // Colección de cartas obtenibles en las casillas tipo Suerte

    // Constructores
    public CartaSuerte() {
        super();
        if(cartasSuerte == null){inicializarCartas();}
    }

    public CartaSuerte(String nombre, String accion) {
        super(nombre, accion);       // Llama al constructor de la clase padre Carta
    }

    // Métodos de CartaSuerte
    // Método que permite barajar las cartas de Suerte
    public Carta barajarSuerte(int numero) {
        Collections.shuffle(cartasSuerte);
        return cartasSuerte.get(numero - 1);
    }

    // Métodos heredados
    // Método abstracto que inicializa las barajas añadiendo las cartas correspondientes de Suerte y Comunidad
    @Override
    public void inicializarCartas(){
        cartasSuerte = new ArrayList<>();
        cartasSuerte.add(new CartaSuerte("suerte1", "Ve al Transportes1 y coge un avión. Si pasas por la casilla de Salida, cobra la cantidad habitual."));
        cartasSuerte.add(new CartaSuerte("suerte2", "Decides hacer un viaje de placer. Avanza hasta Solar15 directamente, sin pasar por la casilla de Salida y sin cobrar la cantidad habitual."));
        cartasSuerte.add(new CartaSuerte("suerte3", "Vendes tu billete de avión para Solar17 en una subasta por Internet. Cobra 500000€"));
        cartasSuerte.add(new CartaSuerte("suerte4", "Ve a Solar3. Si pasas por la casilla de Salida, cobra la cantidad habitual."));
        cartasSuerte.add(new CartaSuerte("suerte5", "Los acreedores te persiguen por impago. Ve a la Cárcel. Ve directamente sin pasar por la casilla de Salida y sin cobrar la cantidad habitual."));
        cartasSuerte.add(new CartaSuerte("suerte6", "¡Has ganado el bote de la lotería! Recibe 1000000€."));
    }
}
