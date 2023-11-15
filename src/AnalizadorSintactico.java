
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
        while (componenteLexico.getEtiqueta().equals("coma")) {
            compara("coma");
            declaracion();
        }
        compara("punto_y_coma");
    }

    public void declaracion() {
        tipo();
        identificadores();
    }

    public void tipo() {
        if (componenteLexico.getEtiqueta().equals("int") || componenteLexico.getEtiqueta().equals("float")) {
            tipo = componenteLexico.getEtiqueta();
            componenteLexico = lexico.getComponenteLexico();
        } else {
            System.out.println("Error: Se esperaba int o float");
        }
    }

    public void identificadores() {
        if (componenteLexico.getEtiqueta().equals("id")) {
            simbolos.put(componenteLexico.getEtiqueta(), tipo);
            componenteLexico = lexico.getComponenteLexico();
            masIdentificadores();
        } else {
            System.out.println("Error: Se esperaba un identificador");
        }
    }

    public void masIdentificadores() {
        while (componenteLexico.getEtiqueta().equals("coma")) {
            compara("coma");
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