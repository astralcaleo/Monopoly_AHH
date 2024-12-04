package monopoly.cartas;

import java.util.ArrayList;
import java.util.Collections;

public class CartaCajaComunidad extends Carta{
    // Atributos
    private static ArrayList<Carta> cartasCaja;   // Colección de cartas obtenibles en las casillas tipo Comunidad

    // Constructores
    public CartaCajaComunidad() {
        super();
        if(cartasCaja == null){accion();}
    }

    public CartaCajaComunidad(String nombre, String accion) {
        super(nombre, accion);       // Llama al constructor de la clase padre Carta
    }

    // Métodos de CartaCajaComunidad
    // Método que permite barajar las cartas de Caja de Comunidad
    public Carta barajarCaja(int numero) {
        Collections.shuffle(cartasCaja);
        //return cartasCaja.get(numero - 1);
        return cartasCaja.get(numero -1);
    }

    // Métodos heredados
    // Método abstracto que inicializa las barajas añadiendo las cartas correspondientes de Suerte y Comunidad
    @Override
    public void accion(){
        cartasCaja = new ArrayList<>();
        cartasCaja.add(new CartaCajaComunidad("caja1", "Paga 500000€ por un fin de semana en un balneario de 5 estrellas."));
        cartasCaja.add(new CartaCajaComunidad("caja2", "Te investigan por fraude de identidad. Ve a la Cárcel. Ve directamente sin pasar por la casilla de Salida y sin cobrar la cantidad habitual."));
        cartasCaja.add(new CartaCajaComunidad("caja3", "Colócate en la casilla de Salida. Cobra la cantidad habitual."));
        cartasCaja.add(new CartaCajaComunidad("caja4", "Tu compañía de Internet obtiene beneficios. Recibe 2000000€."));
        cartasCaja.add(new CartaCajaComunidad("caja5", "Paga 1000000€ por invitar a todos tus amigos a un viaje a Solar14."));
        cartasCaja.add(new CartaCajaComunidad("caja6", "Alquilas a tus compañeros una villa en Solar7 durante una semana. Paga 200000€ a cada jugador."));
    }
}
