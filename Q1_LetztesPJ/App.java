import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.awt.Desktop;
import java.net.URI;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Scanner und Parser App");
            frame.setSize(1200, 600);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout(15, 15));
            frame.getContentPane().setBackground(Color.pink);
            ((JPanel) frame.getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JPanel centerPanel = new JPanel(new GridLayout(1, 3, 15, 0));
            centerPanel.setBackground(Color.pink);

            // Spalte 1: Quelltext
            JPanel scannerPanel = new JPanel(new BorderLayout(0, 5));
            scannerPanel.setBackground(Color.pink);
            JTextArea scannerArea = new JTextArea("int main() { x = 10; }");
            scannerArea.setLineWrap(true);
            scannerArea.setWrapStyleWord(true);
            JButton scanButton = new JButton("Code Scannen");
            scannerPanel.add(new JLabel("Quelltext:"), BorderLayout.NORTH);
            scannerPanel.add(new JScrollPane(scannerArea), BorderLayout.CENTER);
            scannerPanel.add(scanButton, BorderLayout.SOUTH);

            // Spalte 2: Tokens
            JPanel parserPanel = new JPanel(new BorderLayout(0, 5));
            parserPanel.setBackground(Color.pink);
            JTextArea parserArea = new JTextArea();
            parserArea.setLineWrap(true);
            parserArea.setWrapStyleWord(true);
            JButton parseButton = new JButton("Tokens Parsen");
            parserPanel.add(new JLabel("Tokens:"), BorderLayout.NORTH);
            parserPanel.add(new JScrollPane(parserArea), BorderLayout.CENTER);
            parserPanel.add(parseButton, BorderLayout.SOUTH);

            // Spalte 3: Log
            JPanel ausgabePanel = new JPanel(new BorderLayout(0, 5));
            ausgabePanel.setBackground(Color.pink);
            JTextArea ausgabeArea = new JTextArea();
            ausgabeArea.setEditable(false);
            ausgabeArea.setBackground(new Color(240, 240, 240));
            JButton dontclickButton = new JButton("Don't click me ;)");
            ausgabePanel.add(new JScrollPane(ausgabeArea), BorderLayout.CENTER);
            ausgabePanel.add(dontclickButton, BorderLayout.SOUTH);
            
            centerPanel.add(scannerPanel);
            centerPanel.add(parserPanel);
            centerPanel.add(ausgabePanel);
            frame.add(centerPanel, BorderLayout.CENTER);

            // Connection: Scanner
            scanButton.addActionListener(e -> {
                try {
                    List<Token> tokens = Scanner.tokenize(scannerArea.getText());
                    parserArea.setText(tokens.toString());
                    ausgabeArea.setText("--- SCANNER ERFOLGREICH ---\nTokens wurden geladen.");
                } catch (Exception ex) {
                    ausgabeArea.setText("Scanner-Fehler: " + ex.getMessage());
                }
            });

            // Connection: Parser
            parseButton.addActionListener(e -> {
                String input = parserArea.getText();
                // Note: This logic assumes tokens are in list string format [TOKEN(text), ...]
                // If you want to use raw text, you'd need to re-parse the string.
                StringBuilder log = new StringBuilder();
                try {
                    List<Token> tokenStream = Scanner.tokenize(scannerArea.getText());
                    log.append("Erkannte Tokens:\n").append(tokenStream).append("\n\n");
                    new Parser(tokenStream, log).parseMethode();
                    log.append("Erfolg!");
                } catch (Exception ex) {
                    log.append("Fehler: ").append(ex.getMessage());
                }
                ausgabeArea.setText(log.toString());
            });
            
            dontclickButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                try {
                    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                        Desktop.getDesktop().browse(new URI("https://www.youtube.com/watch?v=dQw4w9WgXcQ"));
                    } else {
                        System.out.println("Desktop browsing is not supported on this platform.");
                    }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                }
            }
            });

            frame.setVisible(true);
        });
    }
}
