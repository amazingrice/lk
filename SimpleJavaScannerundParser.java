import java.util.ArrayList;
import java.util.List;

public class SimpleJavaParser {

    // --- TOKENS & TYPEN ---
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

    // --- 1. DER SCANNER (LEXER) ---
    public static List<Token> tokenize(String input) {
        List<Token> tokens = new ArrayList<>();
        if (input == null || input.trim().isEmpty()) return tokens;

        int i = 0;
        while (i < input.length()) {
            char ch = input.charAt(i);

            // Whitespaces überspringen
            if (Character.isWhitespace(ch)) {
                i++;
                continue;
            }

            // Symbole erkennen
            if (ch == '(') { tokens.add(new Token(TokenType.KLAMMER_AUF, "(")); i++; }
            else if (ch == ')') { tokens.add(new Token(TokenType.KLAMMER_ZU, ")")); i++; }
            else if (ch == '{') { tokens.add(new Token(TokenType.BLOCK_AUF, "{")); i++; }
            else if (ch == '}') { tokens.add(new Token(TokenType.BLOCK_ZU, "}")); i++; }
            else if (ch == '=') { tokens.add(new Token(TokenType.ZUWEISUNGS_OP, "=")); i++; }
            else if (ch == '.') { tokens.add(new Token(TokenType.PUNKT, ".")); i++; }
            else if (ch == ';') { tokens.add(new Token(TokenType.SEMIKOLON, ";")); i++; } 
            
            // Zahlen scannen
            else if (Character.isDigit(ch)) {
                StringBuilder sb = new StringBuilder();
                while (i < input.length() && Character.isDigit(input.charAt(i))) {
                    sb.append(input.charAt(i));
                    i++;
                }
                tokens.add(new Token(TokenType.ZAHL, sb.toString()));
            } 
            // Wörter (Typen / Namen) scannen
            else if (Character.isLetter(ch) || ch == '_') {
                StringBuilder sb = new StringBuilder();
                while (i < input.length() && (Character.isLetterOrDigit(input.charAt(i)) || input.charAt(i) == '_')) {
                    sb.append(input.charAt(i));
                    i++;
                }
                String val = sb.toString();

                // Erkennung von Datentypen
                if (val.equals("int") || val.equals("void") || val.equals("double") || val.equals("float") || val.equals("String") || val.equals("typ")) {
                    tokens.add(new Token(TokenType.TYP, val));
                } else {
                    tokens.add(new Token(TokenType.NAME, val));
                }
            } else {
                throw new RuntimeException("Lexer-Fehler: Unbekanntes Zeichen -> '" + ch + "'");
            }
        }

        tokens.add(new Token(TokenType.EOF, ""));
        return tokens;
    }

    // --- 2. DER PARSER ---
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
        // Test 1: Mit echtem Java-Code-Stil
        String inputEcht = "void meinCode() { int x = 42.5; double y = 10.2; }";
        
        System.out.println("Starte Parsing...");
        try {
            List<Token> tokenStream = tokenize(inputEcht);
            System.out.println("Erzeugte Tokens: " + tokenStream + "\n");

            Parser parser = new Parser(tokenStream);
            parser.parseMethode();

            System.out.println("\nErfolg! Der Code ist syntaktisch korrekt.");
        } catch (Exception e) {
            System.err.println("\nFehler: " + e.getMessage());
        }
    }
}
