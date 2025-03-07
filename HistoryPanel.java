import java.awt.*;
import javax.swing.*;

/**
 * HistoryPanel class represents the history view of the calculator.
 * It displays a list of previous calculations and provides functionality to clear the history.
 */
public class HistoryPanel extends JPanel {
    private final JTextArea historyArea;      // Area to display calculation history
    private final CalculatorLogic calculator; // Reference to calculator logic
    private final JButton clearButton;        // Button to clear history

    /**
     * Constructor initializes the history panel and sets up the UI components
     * @param calculator Reference to the calculator logic
     */
    public HistoryPanel(CalculatorLogic calculator) {
        this.calculator = calculator;
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        // Create history display area
        historyArea = new JTextArea();
        historyArea.setEditable(false);
        historyArea.setFont(new Font("Digital-7", Font.PLAIN, 18));
        historyArea.setLineWrap(true);
        historyArea.setWrapStyleWord(true);
        historyArea.setBackground(Color.BLACK);
        historyArea.setForeground(Color.WHITE);
        historyArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create scroll pane for history area
        JScrollPane scrollPane = new JScrollPane(historyArea);
        scrollPane.setBackground(Color.BLACK);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        // Create panel for Clear History button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.BLACK);
        
        clearButton = new JButton("Clear History");
        clearButton.setFont(new Font("Arial", Font.BOLD, 14));
        clearButton.setBackground(new Color(165, 165, 165));
        clearButton.setForeground(Color.BLACK);
        clearButton.addActionListener(e -> clearHistory());
        clearButton.setFocusPainted(false);
        
        buttonPanel.add(clearButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Set up timer for automatic history updates
        Timer timer = new Timer(1000, e -> updateHistory());
        timer.start();
    }

    /**
     * Updates the history display area with current calculation history
     */
    private void updateHistory() {
        StringBuilder sb = new StringBuilder();
        for (String calc : calculator.getHistory()) {
            sb.append(calc).append("\n");
        }
        historyArea.setText(sb.toString());
    }

    /**
     * Clears all calculation history
     */
    private void clearHistory() {
        calculator.getHistory().clear();
        updateHistory();
    }
} 