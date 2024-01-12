import java.nio.charset.Charset;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
public class Lexico {
    private PalabrasReservadas palabrasReservadas;
    private int posicion;
    private int lineas;
    private char caracter;
    private String programa;

    public Lexico(String programa) {
        this.posicion = 0;
        this.lineas = 1;
        this.palabrasReservadas = new PalabrasReservadas("Lexico.txt");
        this.programa = programa + (char) 0;
    }
    public Lexico(File ficheroEntrada, Charset utf8) {
        try {
            this.posicion = 0;
            this.lineas = 1;
            this.palabrasReservadas = new PalabrasReservadas("Lexico.txt");
            this.programa = "";

            Scanner scanner = new Scanner(ficheroEntrada, utf8);
            while (scanner.hasNextLine()) {
                this.programa += scanner.nextLine() + "\n";
            }
            this.programa += (char) 0;
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private char extraeCaracter() {
        return this.programa.charAt(this.posicion++);
    }

    private void devuelveCaracter() {
        this.posicion--;
    }

    private boolean extraeCaracter(char c) {
        if (this.posicion < this.programa.length() - 1) {
            this.caracter = extraeCaracter();
            if (c == this.caracter) {
                return true;
            } else {
                devuelveCaracter();
                return false;
            }
        } else {
            return false;
        }
    }

    public int getLineas() {
        return this.lineas;
    }

    public ComponenteLexico getComponenteLexico() {
        while (true) {
            this.caracter = extraeCaracter();

            // Ignorar comentarios
            if (this.caracter == '/') {
                if (extraeCaracter('*')) {
                    while (true) {
                        this.caracter = extraeCaracter();
                        if (this.caracter == '*' && extraeCaracter('/')) {
                            break;
                        } else if (this.caracter == 0) {
                            System.out.println("Error: Comentario no cerrado.");
                            return new ComponenteLexico("invalid_comment");
                        } else if (this.caracter == '\n') {
                            this.lineas++;
                        }
                    }
                } else if (extraeCaracter('/')) {
                    while (this.caracter != '\n' && this.caracter != 0) {
                        this.caracter = extraeCaracter();
                    }
                    if (this.caracter == '\n') {
                        this.lineas++;
                    }
                } else {
                    return new ComponenteLexico("divide");
                    //break;
                }
            } else if (this.caracter == 0) {
                return new ComponenteLexico("end_program");
            } else if (this.caracter == ' ' || (int) this.caracter == 9 || (int) this.caracter == 13) {
                continue;
            } else if ((int) this.caracter == 10) {
                this.lineas++;
            } else {
                break;
            }
        }

        if (Character.isDigit(this.caracter)) {
            String numero = "";
            do {
                numero = numero + this.caracter;
                this.caracter = extraeCaracter();
            } while (Character.isDigit(this.caracter));
            if (this.caracter != '.') {
                devuelveCaracter();
                return new ComponenteLexico("int", numero);
            }

            do {
                numero = numero + this.caracter;
                this.caracter = extraeCaracter();
            } while (Character.isDigit(this.caracter));
            devuelveCaracter();
            return new ComponenteLexico("float", numero);
        }

        if (Character.isLetter(this.caracter)) {
            String lexema = "";
            do {
                lexema = lexema + this.caracter;
                this.caracter = extraeCaracter();
            } while (Character.isLetterOrDigit(this.caracter));

            devuelveCaracter();
            if (this.palabrasReservadas.containsKey(lexema)) {
                return new ComponenteLexico(this.palabrasReservadas.getEtiqueta(lexema));
            } else {
                if (lexema.equals("void") || lexema.equals("main") ||lexema.equals ("while") || lexema.equals("print") || lexema.equals("if") || lexema.equals("else")) {
                    return new ComponenteLexico(lexema); // Tratar void y main como palabras reservadas
                } else if (lexema.equals("true") || lexema.equals("false")) {
                    return new ComponenteLexico("bool", lexema); // Tratar true y false como valores booleanos
                } else {
                    return new ComponenteLexico("id", lexema);
                }
            }

        }

        switch (this.caracter) {
            case '=':
                return new ComponenteLexico("assignment");
            case '<':
                return new ComponenteLexico("less_than");
            case '>':
                return new ComponenteLexico("greater_than");
            case '+':
                return new ComponenteLexico("add");
            case '-':
                return new ComponenteLexico("subtract");
            case '*':
                return new ComponenteLexico("multiply");
            case '/':
                return new ComponenteLexico("divide");
            case '%':
                return new ComponenteLexico("remainder");
            case ';':
                return new ComponenteLexico("semicolon");
            case '(':
                return new ComponenteLexico("open_parenthesis");
            case ')':
                return new ComponenteLexico("closed_parenthesis");
            case ',':
                return new ComponenteLexico("comma");
            case '{':
                return new ComponenteLexico("open_brace");
            case '}':
                return new ComponenteLexico("close_brace");
            case '[':
                return new ComponenteLexico("open_bracket");
            case ']':
                return new ComponenteLexico("closed_bracket");
            default:
                System.out.println("Invalid character: " + this.caracter);
                return new ComponenteLexico("invalid_char");
        }
    }
}

//"void main {int alex, b05, cec9, d; float x; int [105] v98;int alex;}";