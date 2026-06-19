import java.util.*;

public class Scanner {
    private static int pos;
    private static String input;

    public static List<Token> tokenize(String source) {
        input = source; pos = 0;
        List<Token> tokens = new ArrayList<>();

        while (pos < input.length()) {
            char c = input.charAt(pos);
            if (Character.isWhitespace(c)) { pos++; continue; }

            // Strings "..."
            if (c == '"') { tokens.add(scanString()); continue; }
            
            // Zahlen
            if (Character.isDigit(c)) { tokens.add(scanNumber()); continue; }
            
            // Bezeichner & Keywords
            if (Character.isLetter(c)) { tokens.add(scanIdentifierOrKeyword()); continue; }

            // Symbole & Operatoren
            switch (c) {
                case '{': tokens.add(new Token(TokenType.BLOCK_AUF, "{")); pos++; break;
                case '}': tokens.add(new Token(TokenType.BLOCK_ZU, "}")); pos++; break;
                case '(': tokens.add(new Token(TokenType.KLAMMER_AUF, "(")); pos++; break;
                case ')': tokens.add(new Token(TokenType.KLAMMER_ZU, ")")); pos++; break;
                case '[': tokens.add(new Token(TokenType.ECKIGE_AUF, "[")); pos++; break; // Neu
                case ']': tokens.add(new Token(TokenType.ECKIGE_ZU, "]")); pos++; break; // Neu
                case ';': tokens.add(new Token(TokenType.SEMIKOLON, ";")); pos++; break;
                case ',': tokens.add(new Token(TokenType.KOMMA, ",")); pos++; break;
                case '.': tokens.add(new Token(TokenType.PUNKT, ".")); pos++; break;
                case '+': tokens.add(new Token(TokenType.ARITHMETIK_OP, "+")); pos++; break;
                case '-': tokens.add(new Token(TokenType.ARITHMETIK_OP, "-")); pos++; break;
                case '*': tokens.add(new Token(TokenType.ARITHMETIK_OP, "*")); pos++; break;
                case '/': tokens.add(new Token(TokenType.ARITHMETIK_OP, "/")); pos++; break;
                case '=':
                    if (peek() == '=') { tokens.add(new Token(TokenType.VERGLEICHS_OP, "==")); pos += 2; }
                    else { tokens.add(new Token(TokenType.ZUWEISUNGS_OP, "=")); pos++; }
                    break;
                case '!':
                    if (peek() == '=') { tokens.add(new Token(TokenType.VERGLEICHS_OP, "!=")); pos += 2; }
                    else { tokens.add(new Token(TokenType.NOT, "!")); pos++; }
                    break;
                default:
                    throw new RuntimeException("Unbekanntes Zeichen: " + c);
            }
        }
        tokens.add(new Token(TokenType.EOF, ""));
        return tokens;
    }

    private static char peek() { return (pos + 1 < input.length()) ? input.charAt(pos + 1) : '\0'; }

    private static Token scanString() {
        StringBuilder sb = new StringBuilder();
        pos++; // Öffnendes " überspringen
        while (pos < input.length() && input.charAt(pos) != '"') sb.append(input.charAt(pos++));
        pos++; // Schließendes " überspringen
        return new Token(TokenType.STRING_LITERAL, sb.toString());
    }

    private static Token scanNumber() {
        StringBuilder sb = new StringBuilder();
        while (pos < input.length() && Character.isDigit(input.charAt(pos))) sb.append(input.charAt(pos++));
        return new Token(TokenType.ZAHL, sb.toString());
    }

    private static Token scanIdentifierOrKeyword() {
        StringBuilder sb = new StringBuilder();
        while (pos < input.length() && Character.isLetterOrDigit(input.charAt(pos))) sb.append(input.charAt(pos++));
        String text = sb.toString();
        switch (text) {
            case "int": case "boolean": case "String": case "double": return new Token(TokenType.TYP, text);
            case "void": return new Token(TokenType.VOID, text);
            case "if": return new Token(TokenType.WENN, text);
            case "else": return new Token(TokenType.SONST, text);
            case "return": return new Token(TokenType.RUECKGABE, text);
            default: return new Token(TokenType.NAME, text);
        }
    }
}