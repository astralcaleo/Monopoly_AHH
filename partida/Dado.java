package partida;


public class Dado {
    //El dado solo tiene un atributo en nuestro caso: su valor.
    private int valor;

    //Metodo para simular lanzamiento de un dado: devolverá un valor aleatorio entre 1 y 6.
    public int hacerTirada() {
        this.valor = (int)(Math.random() * 6) + 1;
        return this.valor;
    }

    //GETTER
    /*Método para obtener el valor del dado*/
    public int getValor() {
        return this.valor;
    }

    //SETTER
    /*Método para modificar el valor del dado*/
    public void setValor(int valor) {
        this.valor = valor;
    }
}
