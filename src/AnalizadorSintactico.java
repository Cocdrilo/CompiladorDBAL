import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

public class AnalizadorSintactico {

    private Lexico lexico;
    private ComponenteLexico componenteLexico;
    private Hashtable<String, String> simbolos;
    private String tipo;
    private int tamaño;

    public AnalizadorSintactico(Lexico lexico) {
        this.lexico = lexico;
        this.componenteLexico = this.lexico.getComponenteLexico();
        this.simbolos = new Hashtable<String, String>();
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
        if (componenteLexico.getEtiqueta().equals("semicolon")) {
            compara("semicolon");
            declaraciones();
        }
    }

    public void declaracion() {
        System.out.println("Declaracion");
        if (componenteLexico.getValor().equals("int") || componenteLexico.getValor().equals("float")) {
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
            String nombreIdentificador = componenteLexico.getValor();
            if (!nombreIdentificador.equals("main") && !nombreIdentificador.equals("void")) {
                if (simbolos.containsKey(nombreIdentificador)) {
                    System.out.println("Error: El identificador '" + nombreIdentificador + "' ya ha sido declarado.");
                } else {
                    System.out.println("Identificador: " + nombreIdentificador);
                    simbolos.put(nombreIdentificador, tipo);
                }
                componenteLexico = lexico.getComponenteLexico();
                masIdentificadores();
            } else {
                componenteLexico = lexico.getComponenteLexico();
                masIdentificadores();
            }
        } else if (componenteLexico.getEtiqueta().equals("end_program")) {
            return;
        } else if (componenteLexico.getEtiqueta().equals(("open_bracket"))) {
            System.out.println("Entre a OpenBracket:");
            vector();
        } else if (componenteLexico.getEtiqueta().equals("close_brace")) {
            System.out.println("Salgo de Encapsulamiento");
        } else {
            System.out.println("No soy un identificador" + componenteLexico.getEtiqueta());
        }
    }

    public void masIdentificadores() {
        while (componenteLexico.getValor().equals("comma")) {
            compara("comma");
            if (componenteLexico.getEtiqueta().equals("id")) {
                String nombreIdentificador = componenteLexico.getValor();
                if (simbolos.containsKey(nombreIdentificador)) {
                    System.out.println("Error: El identificador '" + nombreIdentificador + "' ya ha sido declarado.");
                } else {
                    simbolos.put(nombreIdentificador, tipo);
                }
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
            tipo = componenteLexico.getValor();
            componenteLexico = lexico.getComponenteLexico();
        } else {
            System.out.println(componenteLexico.getValor());
            System.out.println("Error: Se esperaba int o float");
        }
    }

    public String tablaSimbolos() {
        String simbolos = "";

        Set<Map.Entry<String, String>> s = this.simbolos.entrySet();
        if (s.isEmpty()) System.out.println("La tabla de simbolos esta vacia\n");
        for (Map.Entry<String, String> m : s) {
            simbolos = simbolos + "<'" + m.getKey() + "', " +
                    m.getValue() + "> \n";
        }

        return simbolos;
    }

    public void vector() {
        componenteLexico = lexico.getComponenteLexico();
        System.out.println("Vector: " + componenteLexico.getEtiqueta());
        if (componenteLexico.getEtiqueta().equals("int")) {
            tamaño = Integer.parseInt(componenteLexico.getValor());
            compara("int");
        } else {
            System.out.println("Error: Se esperaba un número para el tamaño del vector");
        }
        componenteLexico = lexico.getComponenteLexico();
        // Actualiza la tabla de símbolos para el identificador actual como un vector
        String nombreIdentificador = componenteLexico.getValor();
        if (simbolos.containsKey(nombreIdentificador)) {
            System.out.println("Error: El identificador '" + nombreIdentificador + "' ya ha sido declarado.");
        } else {
            simbolos.put(nombreIdentificador, "array(" + tipo + ", " + tamaño + ")");
        }
        componenteLexico = lexico.getComponenteLexico();
    }
}