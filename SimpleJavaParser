import java.util.ArrayList;
import java.util.List;


public class SimpleJavaParser {


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
           return type.name();
       }
   }


   //(String zu Tokens) von kofi ersetzen lassen bitte
   public static List<Token> tokenize(String input) {
       List<Token> tokens = new ArrayList<>();
       if (input == null || input.trim().isEmpty()) return tokens;


       //space als snipp
       String[] words = input.split("\\s+");


       for (String word : words) {
           switch (word.toLowerCase()) {
               case "typ":                tokens.add(new Token(TokenType.TYP, word)); break;
               case "name":               tokens.add(new Token(TokenType.NAME, word)); break;
               case "klammerauf":         tokens.add(new Token(TokenType.KLAMMER_AUF, word)); break;
               case "klammerzu":          tokens.add(new Token(TokenType.KLAMMER_ZU, word)); break;
               case "blockauf":           tokens.add(new Token(TokenType.BLOCK_AUF, word)); break;
               case "blockzu":            tokens.add(new Token(TokenType.BLOCK_ZU, word)); break;
               case "zuweisungsoperator": tokens.add(new Token(TokenType.ZUWEISUNGS_OP, word)); break;
               case "zahl":               tokens.add(new Token(TokenType.ZAHL, word)); break;
               case "punktoperator":      tokens.add(new Token(TokenType.PUNKT, word)); break;
               case "semikolon":          tokens.add(new Token(TokenType.SEMIKOLON, word)); break;
               default:
                   throw new RuntimeException("Lexer-Fehler: Unbekanntes Wort im String gefunden -> '" + word + "'");
           }
       }


       // Am Ende immer ein EOF (End of File) anhängen
       tokens.add(new Token(TokenType.EOF, ""));
       return tokens;
   }


   // --- 3. DER PARSER ---


   //lege nimm die finale liste und gehe wort für wort durch
   // bis dinge gefunden sind und abgehakt werden können
   // bezug auf regeln in der grammatik
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
       //was wird jetzt erwartet?
       private void consume(TokenType expected) {
           if (current().type == expected) {
               pos++;
           } else {
               throw new RuntimeException("Syntaxfehler an Position " + pos +
                       ": Erwartet " + expected + ", aber " + current().type + " gefunden.");
           }
       }
       //ist das aktuelle wort das was wir suchen?
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
       // block öffnen
       public void parseBlock() {
           System.out.println("Parse: Block");
           consume(TokenType.BLOCK_AUF);


           //block schließen
           while (!match(TokenType.BLOCK_ZU) && !match(TokenType.EOF)) {
               parseZuweisung();
           }


           consume(TokenType.BLOCK_ZU);
       }


       public void parseZuweisung() {
           System.out.println("Parse: Zuweisung");


           //optionaler typ
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


   //hier um andere regeln erweitern bitttteeee (;


   // Test datei hier schnitstelle einbauen für simple logig
   public static void main(String[] args) {
       // Dein exakter String aus der Anforderung
       String input = "typ name klammerauf klammerzu blockauf " +
               "typ name zuweisungsoperator zahl punktoperator zahl semikolon " +
               "typ name zuweisungsoperator zahl punktoperator zahl semikolon " +
               "blockzu";
       //ersetzen mit ausgabe an user ui
       System.out.println("Eingabe-String:\n" + input + "\n");


       try {
           // 1. Lexer wandelt String in Tokens um ==> code ersetzen kofi??
           List<Token> tokenStream = tokenize(input);
           System.out.println("Erzeugte Tokens: " + tokenStream + "\n");


           // 2. Parser prüft die Grammatik
           Parser parser = new Parser(tokenStream);
           parser.parseMethode();


           System.out.println("\nErfolg! Der String entspricht der Grammatik.");
       } catch (Exception e) {
           System.err.println("\nFehler: " + e.getMessage());
       }
   }
}
//           __
//          /  \  .___
//         /    \/\  /\
//,       /     |  \/  \
//        \  MW | WM   /
//      ==|_    |     _|==
//          \../_\.../
//            \###/

