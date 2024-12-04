package monopoly.interfaz;

public interface Consola{

    public void imprimir (String mensaje);

    public int leerEnIntervalo(String mensaje, int min, int max);

    public String leer (String mensaje);

    public int leer();
}