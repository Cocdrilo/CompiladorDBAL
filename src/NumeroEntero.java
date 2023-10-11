public class NumeroEntero extends ComponenteLexico{
    private int valor;
    public NumeroEntero(int valor,String etiqueta) {
        super(etiqueta);
    }

    public int getValor() {
        return valor;
    }
    public String toString() {
        return Integer.toString(valor);
    }
}