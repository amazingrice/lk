import java.util.ArrayList;
import java.util.List;

public class SimpleJavaParser {

    public class TokenTypes {
        public static final int KLASSE        = 1;
        public static final int TYP           = 2;
        public static final int WENN          = 14;
        public static final int SONST         = 15;
        public static final int FUER          = 16;  // for
        public static final int SOLANGE       = 17;  // while
        public static final int RUECKGABE     = 18;  // return
        public static final int NEU           = 19;  // new

        public static final int BLOCKAUF      = 3;   // {
        public static final int BLOCKZU       = 4;   // }
        public static final int SEMIKOLON     = 5;   // ;
        public static final int KLAMMERAUF    = 6;   // (
        public static final int KLAMMERZU     = 7;   // )
        public static final int ZUWEISUNGSOP  = 8;   // =
        public static final int VERGLEICHSOP  = 9;   // ==, <, >, <=, >=, !=
        public static final int ARITHMETICOP  = 10;  // +, -, *, /
        public static final int KOMMA         = 13;  // ,

        public static final int ZAHL          = 11;  // e.g., 42, 3.14
        public static final int NAME          = 12;  // Variable oder funktionen names
        
        public static final int UNBEKANNT     = 0;   // Unknown character
    }

    // --- 1. TOKENS ---
    enum TokenType {
        TYP, NAME, ZAHL,
        KLAMMER_AUF, KLAMMER_ZU,
        BLOCK_AUF, BLOCK_ZU,
        ZUWEISUNGS_OP, SEMIKOLON, PUNKT, EOF
    }

    static class Token {
        TokenType type;
        String value;

        Token(TokenType type, String value) {
            this.type = type;
            this.value = value;
        }

        @Override
        public String toString() {
            return type.name() + "(" + value + ")";
        }
    }

    // --- 2. DER SCANNER (LEXER) ---
    // Ersetzt die alte Hilfskonstruktion durch echte Zeichenerkennung
    public static List<Token> tokenize(String input) {
        List<Token> tokens = new ArrayList<>();
        if (input == null || input.trim().isEmpty()) return tokens;

        int i = 0;
        while (i < input.length()) {
            char ch = input.charAt(i);

            // 1. Whitespaces (Leerzeichen, Tabs, Umbrüche) überspringen
            if (Character.isWhitespace(ch)) {
                i++;
                continue;
            }

            // 2. Einzeldarstellungen / Symbole scannen
            if (ch == '(') {
                tokens.add(new Token(TokenType.KLAMMER_AUF, "("));
                i++;
            } else if (ch == ')') {
                tokens.add(new Token(TokenType.KLAMMER_ZU, ")"));
                i++;
            } else if (ch == '{') {
                tokens.add(new Token(TokenType.BLOCK_AUF, "{"));
                i++;
            } else if (ch == '}') {
                tokens.add(new Token(TokenType.BLOCK_ZU, "}"));
                i++;
            } else if (ch == '=') {
                tokens.add(new Token(TokenType.ZUWEISUNGS_OP, "="));
                i++;
            } else if (ch == '.') {
                tokens.add(new Token(TokenType.PUNKT, "."));
                i++;
            } else if (ch == ';') {
                tokens.add(new Token(TokenType.SEMIKOLON, ";"));
                i++;
            } 
            // 3. Zahlen (Ziffernfolgen) scannen
            else if (Character.isDigit(ch)) {
                StringBuilder sb = new StringBuilder();
                while (i < input.length() && Character.isDigit(input.charAt(i))) {
                    sb.append(input.charAt(i));
                    i++;
                }
                tokens.add(new Token(TokenType.ZAHL, sb.toString()));
            } 
            // 4. Identifier (Namen) und Datentypen (Keywords) scannen
            else if (Character.isLetter(ch) || ch == '_') {
                StringBuilder sb = new StringBuilder();
                while (i < input.length() && (Character.isLetterOrDigit(input.charAt(i)) || input.charAt(i) == '_')) {
                    sb.append(input.charAt(i));
                    i++;
                }
                String val = sb.toString();

                // Hier definierst du, welche Wörter als TYP eingestuft werden
                if (val.equals("int") || val.equals("void") || val.equals("double") || val.equals("float") || val.equals("String")) {
                    tokens.add(new Token(TokenType.TYP, val));
                } else {
                    tokens.add(new Token(TokenType.NAME, val));
                }
            } else {
                throw new RuntimeException("Lexer-Fehler: Unbekanntes Zeichen gefunden -> '" + ch + "'");
            }
        }

        // Am Ende immer ein EOF (End of File) anhängen
        tokens.add(new Token(TokenType.EOF, ""));
        return tokens;
    }

    // --- 3. DER PARSER ---
    static class Parser {
        private final List<Token> tokens;
        private int pos = 0;

        public Parser(List<Token> tokens) {
            this.tokens = tokens;
        }

        private Token current() {
            if (pos >= tokens.size()) return new Token(TokenType.EOF, "");
            return tokens.get(pos);
        }

        private void consume(TokenType expected) {
            if (current().type == expected) {
                pos++;
            } else {
                throw new RuntimeException("Syntaxfehler an Position " + pos +
                        ": Erwartet " + expected + ", aber " + current().type + " gefunden.");
            }
        }

        private boolean match(TokenType type) {
            return current().type == type;
        }

        // --- GRAMMATIK-REGELN ---
        public void parseMethode() {
            System.out.println("Parse: Methode");
            consume(TokenType.TYP);
            consume(TokenType.NAME);
            consume(TokenType.KLAMMER_AUF);
            consume(TokenType.KLAMMER_ZU);
            parseBlock();
        }

        public void parseBlock() {
            System.out.println("Parse: Block");
            consume(TokenType.BLOCK_AUF);

            while (!match(TokenType.BLOCK_ZU) && !match(TokenType.EOF)) {
                parseZuweisung();
            }

            consume(TokenType.BLOCK_ZU);
        }

        public void parseZuweisung() {
            System.out.println("Parse: Zuweisung");

            if (match(TokenType.TYP)) {
                consume(TokenType.TYP);
            }

            consume(TokenType.NAME);
            consume(TokenType.ZUWEISUNGS_OP);
            parseAusdruck();
            consume(TokenType.SEMIKOLON);
        }

        public void parseAusdruck() {
            parseTerm();
        }

        public void parseTerm() {
            parseFaktor();
            if (match(TokenType.PUNKT)) {
                consume(TokenType.PUNKT);
                parseTerm();
            }
        }

        public void parseFaktor() {
            if (match(TokenType.NAME)) {
                consume(TokenType.NAME);
            } else if (match(TokenType.ZAHL)) {
                consume(TokenType.ZAHL);
            } else if (match(TokenType.KLAMMER_AUF)) {
                consume(TokenType.KLAMMER_AUF);
                parseAusdruck();
                consume(TokenType.KLAMMER_ZU);
            } else {
                throw new RuntimeException("Syntaxfehler im Faktor: Unerwartetes Token " + current().type);
            }
        }
    }

    // --- MAIN TESTUMGEBUNG ---
    public static void main(String[] args) {
        // Hier fütterst du das Programm nun mit echtem, strukturierten Code!
        // Hinweis zu deiner Grammatik: Fließkommazahlen werden über den Punkt-Operator ("42.5") zerlegt.
        String input = "void meinCode() { int x = 42.5; double y = 10.2; }";
        
        System.out.println("Eingabe-String:\n" + input + "\n");

        try {
            // 1. Lexer wandelt echten Code in Token-Objekte um
            List<Token> tokenStream = tokenize(input);
            System.out.println("Erzeugte Tokens: " + tokenStream + "\n");

            // 2. Parser prüft die syntaktische Korrektheit anhand der Grammatik
            Parser parser = new Parser(tokenStream);
            parser.parseMethode();

            System.out.println("\nErfolg! Der String entspricht der Grammatik.");
        } catch (Exception e) {
            System.err.println("\nFehler: " + e.getMessage());
        }
    }
}
