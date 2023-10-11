public class Identificador extends ComponenteLexico {
    private String lexema;

    public Identificador(String lexema,String etiqueta) {
        super(etiqueta);
    }

    public String getLexema() {
        return lexema;
    }

    public String toString() {
        return lexema.toString();
    }
}