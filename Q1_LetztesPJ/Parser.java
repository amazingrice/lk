import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int pos = 0;
    private final StringBuilder log;

    public Parser(List<Token> tokens, StringBuilder log) {
        this.tokens = tokens;
        this.log = log;
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

    // Hilfsmethode für den Blick nach vorne (Lookahead)
    private boolean peekMatch(int offset, TokenType type) {
        int targetPos = pos + offset;
        if (targetPos >= tokens.size()) return type == TokenType.EOF;
        return tokens.get(targetPos).type == type;
    }

    public void parseMethode() {
        log.append("Parse: Methode\n");
        
        // Akzeptiert TYP (int, String...) ODER VOID
        if (match(TokenType.TYP)) {
            consume(TokenType.TYP);
        } else {
            consume(TokenType.VOID);
        }
        
        consume(TokenType.NAME);
        consume(TokenType.KLAMMER_AUF);
        consume(TokenType.KLAMMER_ZU);
        parseBlock();
        
        // Falls noch eine Methode im Token-Stream folgt, parsen wir diese ebenfalls
        if (!match(TokenType.EOF)) {
            parseMethode();
        } else {
            consume(TokenType.EOF);
        }
    }

    public void parseBlock() {
        log.append("Parse: Block\n");
        consume(TokenType.BLOCK_AUF);
        while (!match(TokenType.BLOCK_ZU) && !match(TokenType.EOF)) {
            // Wenn es mit einem Typ beginnt ODER ein Name gefolgt von '=' ist, ist es eine Zuweisung
            if (match(TokenType.TYP) || (match(TokenType.NAME) && peekMatch(1, TokenType.ZUWEISUNGS_OP))) {
                parseZuweisung();
            } else {
                // Ansonsten ist es ein eigenständiger Ausdruck (z.B. ein Methodenaufruf)
                parseAusdruck();
                consume(TokenType.SEMIKOLON);
            }
        }
        consume(TokenType.BLOCK_ZU);
    }

    public void parseZuweisung() {
        log.append("Parse: Zuweisung\n");
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
        // Schleife erlaubt verkettete Punkte (System.out.println) ODER mathematische Operatoren (+)
        while (match(TokenType.PUNKT) || match(TokenType.ARITHMETIK_OP)) {
            if (match(TokenType.PUNKT)) {
                consume(TokenType.PUNKT);
            } else {
                consume(TokenType.ARITHMETIK_OP);
            }
            parseFaktor();
        }
    }

    public void parseFaktor() {
        if (match(TokenType.NAME)) {
            consume(TokenType.NAME);
            
            // Wenn nach dem Namen eine offene Klammer kommt, ist es ein Funktions-/Methodenaufruf
            if (match(TokenType.KLAMMER_AUF)) {
                consume(TokenType.KLAMMER_AUF);
                // Falls die Klammer nicht sofort schließt, parsen wir den Inhalt (die Parameter)
                if (!match(TokenType.KLAMMER_ZU)) {
                    parseAusdruck();
                }
                consume(TokenType.KLAMMER_ZU);
            }
            
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
