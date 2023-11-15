
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

public class AnalizadorSintactico {

    private Lexico lexico;
    private ComponenteLexico componenteLexico;
    private Hashtable<String,String> simbolos;
    private String tipo;
    private int tamaño;

    public AnalizadorSintactico(Lexico lexico) {
        this.lexico = lexico;
        this.componenteLexico = this.lexico.getComponenteLexico();
        this.simbolos = new Hashtable<String,String>();
    }

    public void analisisSintactico() {
        declaraciones();
    }

    public void declaraciones() {
        declaracion();
        while (componenteLexico.getEtiqueta().equals("comma")) {
            compara("comma");
            declaracion();
        }
        compara("semicolon");
    }

    public void tipo() {
        if (componenteLexico.getValor().equals("int") || componenteLexico.getValor().equals("float")) {
            tipo = componenteLexico.getValor();  // Usamos getValor para obtener el tipo real ("int" o "float")
            componenteLexico = lexico.getComponenteLexico();
        } else {
            System.out.println(componenteLexico.getValor());
            System.out.println("Error: Se esperaba int o float");
        }
    }

    public void declaracion() {
        tipo();
        identificadores();
    }

    public void identificadores() {
        if (componenteLexico.getEtiqueta().equals("id")) {
            simbolos.put(componenteLexico.getValor(), tipo);  // Usamos getValor para obtener el nombre del identificador
            componenteLexico = lexico.getComponenteLexico();
            masIdentificadores();
        } else {
            System.out.println("Error: Se esperaba un identificador");
        }
    }


    public void masIdentificadores() {
        while (componenteLexico.getValor().equals("comma")) {
            compara("comma");
            if (componenteLexico.getEtiqueta().equals("id")) {
                simbolos.put(componenteLexico.getEtiqueta(), tipo);
                componenteLexico = lexico.getComponenteLexico();
            } else {
                System.out.println("Error: Se esperaba un identificador");
            }
        }
    }

    public void compara(String token) {
        if(this.componenteLexico.getEtiqueta().equals(token)) {
            this.componenteLexico = this.lexico.getComponenteLexico();
        }else {
            System.out.println(this.componenteLexico.getEtiqueta());
            System.out.println("Expected: " + token);
        }
    }

    public String tablaSimbolos() {
        String simbolos = "";

        Set<Map.Entry<String, String>> s = this.simbolos.entrySet();
        if(s.isEmpty()) System.out.println("La tabla de simbolos esta vacia\n");
        for(Map.Entry<String, String> m : s) {
            simbolos = simbolos + "<'" + m.getKey() + "', " +
                    m.getValue() + "> \n";
        }

        return simbolos;
    }
}