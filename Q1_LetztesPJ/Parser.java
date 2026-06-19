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

    public void parseMethode() {
        log.append("Parse: Methode\n");
        consume(TokenType.TYP);
        consume(TokenType.NAME);
        consume(TokenType.KLAMMER_AUF);
        consume(TokenType.KLAMMER_ZU);
        parseBlock();
        consume(TokenType.EOF);
    }

    public void parseBlock() {
        log.append("Parse: Block\n");
        consume(TokenType.BLOCK_AUF);
        while (!match(TokenType.BLOCK_ZU) && !match(TokenType.EOF)) {
            parseZuweisung();
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

    public void parseAusdruck() { parseTerm(); }

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