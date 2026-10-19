import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class GUI extends JFrame {

    private JPanel panel;
    private JTextArea textArea;
    private JButton btnGeneruj;
    private JProgressBar progressBar;
    private JMenuItem mntmZapisz;

    public GUI() {
        setTitle("INF.04 - Generator Raportów");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setContentPane(panel);

        JMenuBar menuBar = new JMenuBar();
        JMenu menuPlik = new JMenu("Plik");

        mntmZapisz = new JMenuItem("Zapisz Raport");
        mntmZapisz.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        mntmZapisz.addActionListener(e -> zapiszRaport());

        JMenuItem mntmWyjscie = new JMenuItem("Wyjście");
        mntmWyjscie.addActionListener(e -> System.exit(0));

        menuPlik.add(mntmZapisz);
        menuPlik.addSeparator();
        menuPlik.add(mntmWyjscie);
        menuBar.add(menuPlik);
        setJMenuBar(menuBar);

        progressBar.setValue(0);
        progressBar.setStringPainted(true);

        btnGeneruj.addActionListener(e -> uruchomGenerowanieWTle());
    }

    private void uruchomGenerowanieWTle() {
        btnGeneruj.setEnabled(false);
        textArea.setText("");
        progressBar.setValue(0);

        SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                for (int i = 1; i <= 10; i++) {
                    Thread.sleep(300);

                    int postep = i * 10;
                    String wiersz = "Wygenerowano wiersz raportu nr " + i + "\n";

                    publish(wiersz);
                    setProgress(postep);
                }
                return null;
            }

            @Override
            protected void process(List<String> chunks) {
                for (String text : chunks) {
                    textArea.append(text);
                }
                progressBar.setValue(getProgress());
            }

            @Override
            protected void done() {
                btnGeneruj.setEnabled(true);
                progressBar.setValue(100);
            }
        };

        worker.execute();
    }

    private void zapiszRaport() {
        String tekstRaportu = textArea.getText();

        if (tekstRaportu == null || tekstRaportu.trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Pole raportu jest puste! Brak danych do zapisania.",
                    "Ostrzeżenie",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Zapisz Raport");

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File plikDoZapisu = fileChooser.getSelectedFile();

            if (!plikDoZapisu.getName().toLowerCase().endsWith(".txt")) {
                plikDoZapisu = new File(plikDoZapisu.getAbsolutePath() + ".txt");
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(plikDoZapisu))) {
                writer.write(tekstRaportu);
                JOptionPane.showMessageDialog(
                        this,
                        "Raport został pomyślnie zapisany do pliku:\n" + plikDoZapisu.getName(),
                        "Sukces",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Wystąpił błąd podczas zapisu pliku:\n" + ex.getMessage(),
                        "Błąd",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GUI().setVisible(true);
        });
    }
}