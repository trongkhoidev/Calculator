import java.awt.*;
import javax.swing.*;

public class HistoryPanel extends JPanel {
    private final JTextArea historyArea;
    private final CalculatorLogic calculator;
    private final JButton clearButton;

    public HistoryPanel(CalculatorLogic calculator) {
        this.calculator = calculator;
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        // Tạo khu vực hiển thị lịch sử
        historyArea = new JTextArea();
        historyArea.setEditable(false);
        historyArea.setFont(new Font("Digital-7", Font.PLAIN, 18));
        historyArea.setLineWrap(true);
        historyArea.setWrapStyleWord(true);
        historyArea.setBackground(Color.BLACK);
        historyArea.setForeground(Color.WHITE);
        historyArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tạo ScrollPane cho khu vực lịch sử
        JScrollPane scrollPane = new JScrollPane(historyArea);
        scrollPane.setBackground(Color.BLACK);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        // Tạo panel chứa nút Clear History
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

        // Timer để cập nhật lịch sử mỗi giây
        Timer timer = new Timer(1000, e -> updateHistory());
        timer.start();
    }

    private void updateHistory() {
        StringBuilder sb = new StringBuilder();
        for (String calc : calculator.getHistory()) {
            sb.append(calc).append("\n");
        }
        historyArea.setText(sb.toString());
    }

    private void clearHistory() {
        calculator.getHistory().clear();
        updateHistory();
    }
} 