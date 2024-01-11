
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
        analizaInicioFin();
        declaraciones();
        //new
    }

    public void declaraciones() {
        declaracion();
        while (componenteLexico.getEtiqueta().equals("comma")) {
            compara("comma");
            declaracion();
        }
        if(componenteLexico.getEtiqueta().equals("semicolon")){
            compara("semicolon");
            declaraciones();
        }

    }

    public void declaracion() {
        System.out.println("Declaracion");
        if(componenteLexico.getValor().equals("int") || componenteLexico.getValor().equals("float")) {
            tipo();
        }
        identificadores();
    }

    public void analizaInicioFin() {
        compara("void");
        compara("main");
        compara("open_brace");
    }

    public void identificadores() {

        if (componenteLexico.getEtiqueta().equals("id")) {
            if(!componenteLexico.getValor().equals("main") && !componenteLexico.getValor().equals("void")) {
                System.out.println("Identificador: " + componenteLexico.getValor());
                simbolos.put(componenteLexico.getValor(), tipo);  // Usamos getValor para obtener el nombre del identificador
                componenteLexico = lexico.getComponenteLexico();

                masIdentificadores();
            }
            else{
                componenteLexico = lexico.getComponenteLexico();
                masIdentificadores();
            }
        } else if(componenteLexico.getEtiqueta().equals("end_program")){
            return;
        } else if(componenteLexico.getEtiqueta().equals(("open_bracket"))){
            System.out.println("Entre a OpenBracket:");
            vector();
        }

        else {
            System.out.println("No soy un identificador" + componenteLexico.getEtiqueta());
            System.out.println("Error: Se esperaba un identificador");
        }
    }


    public void masIdentificadores() {
        while (componenteLexico.getValor().equals("comma")) {
            compara("comma");
            if (componenteLexico.getEtiqueta().equals("id")) {
                simbolos.put(componenteLexico.getValor(), tipo);
                componenteLexico = lexico.getComponenteLexico();
            } else {
                System.out.println("Error: Se esperaba un identificador");
            }
        }
    }

    public void compara(String token) {
        if (this.componenteLexico.getEtiqueta().equals(token)) {
            this.componenteLexico = this.lexico.getComponenteLexico();
        } else {
            System.out.println("Error: Expected " + token + ", but found " + this.componenteLexico.getEtiqueta());
            // Skip to the next token
            this.componenteLexico = this.lexico.getComponenteLexico();
            // Reset the tipo variable to avoid subsequent errors
            this.tipo = null;
        }
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

   /* public void tipo() {
        if(this.componenteLexico.getEtiqueta().equals("int")) {
            this.tipo = "int";
            compara("int");
        }else if(this.componenteLexico.getEtiqueta().equals("float")){
            this.tipo = "float";
            compara("float");
        }
    }*/

    public void vector() {
        if (componenteLexico.getEtiqueta().equals("open_bracket")) {
            componenteLexico = lexico.getComponenteLexico();
            System.out.println("Vector: " + componenteLexico.getEtiqueta());
            compara("open_bracket"); // compara con '['
            System.out.println("Tamaño: " + componenteLexico.getValor());
            if (componenteLexico.getEtiqueta().equals("num")) {
                tamaño = Integer.parseInt(componenteLexico.getValor());
                compara("num");
            } else {
                System.out.println("Error: Se esperaba un número para el tamaño del vector");
            }
            compara("close_bracket"); // compara con ']'

            // Actualiza la tabla de símbolos para el identificador actual como un vector
            simbolos.put(componenteLexico.getValor(), "array(" + tipo + ", " + tamaño + ")");
            componenteLexico = lexico.getComponenteLexico();
        } else {
            // No es un vector, no se hace nada
        }
    }


    /*public void compara(String token) {
        if(this.componenteLexico.getEtiqueta().equals(token)) {
            this.componenteLexico = this.lexico.getComponenteLexico();
        }else {
            System.out.println("Expected: " + token);
        }
    }*/
}