import java.awt.*;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;
import javax.swing.*;

// OOP: this class holds the data and the BMI logic
class BMIRecord {
    private final String name;
    private final String sex;
    private final double weight;      // kg
    private final double heightCm;    // cm
    private final LocalDate birthDate;

    public BMIRecord(String name, String sex, LocalDate birthDate, double weight, double heightCm) {
        this.name = name;
        this.sex = sex;
        this.birthDate = birthDate;
        this.weight = weight;
        this.heightCm = heightCm;
    }

    public int getAge() {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    public double getBmi() {
        double heightM = heightCm / 100;
        return weight / (heightM * heightM);
    }

    public String getCategory() {
        double bmi = getBmi();
        if (bmi < 18.5) return "Underweight";
        else if (bmi < 25) return "Normal";
        else if (bmi < 30) return "Overweight";
        else return "Obese";
    }

    // Age and birthdate are shown inside the Category line
    public String getResultText() {
        return String.format("Name     : %s\n", name)
             + String.format("Sex      : %s\n", sex)
             + String.format("Weight   : %.2f kg\n", weight)
             + String.format("Height   : %.2f cm\n", heightCm)
             + String.format("BMI      : %.2f\n", getBmi())
             + String.format("Category : %s (Age: %d, Birthdate: %s)",
                             getCategory(), getAge(), birthDate);
    }
}

// OOP: this class is the GUI window
public class BMIcalculator extends JFrame {
    private final JTextField nameField = new JTextField();
    private final JTextField birthField = new JTextField();
    private final JComboBox<String> sexBox = new JComboBox<>(new String[]{"Male", "Female"});
    private final JTextField weightField = new JTextField();
    private final JTextField heightField = new JTextField();
    private final JLabel banner = new JLabel(" ", SwingConstants.CENTER);
    private final JTextArea resultArea = new JTextArea(7, 30);

    public BMIcalculator() {
        setTitle("BMI Calculator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Title
        JLabel title = new JLabel("BMI Calculator", SwingConstants.CENTER);
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        // Input panel
        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 8, 8));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Birthdate (YYYY-MM-DD):"));
        inputPanel.add(birthField);
        inputPanel.add(new JLabel("Sex:"));
        inputPanel.add(sexBox);
        inputPanel.add(new JLabel("Weight (kg):"));
        inputPanel.add(weightField);
        inputPanel.add(new JLabel("Height (cm):"));
        inputPanel.add(heightField);

        // Buttons
        JButton calcButton = new JButton("Calculate");
        JButton clearButton = new JButton("Clear");
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 8, 0));
        buttonPanel.add(calcButton);
        buttonPanel.add(clearButton);
        inputPanel.add(new JLabel());
        inputPanel.add(buttonPanel);

        // Colored banner + result text
        banner.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        banner.setForeground(Color.WHITE);
        banner.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        resultArea.setEditable(false);
        resultArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        resultArea.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        JPanel resultPanel = new JPanel(new BorderLayout(0, 8));
        resultPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));
        resultPanel.add(banner, BorderLayout.NORTH);
        resultPanel.add(new JScrollPane(resultArea), BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(title, BorderLayout.NORTH);
        topPanel.add(inputPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(resultPanel, BorderLayout.CENTER);

        calcButton.addActionListener(e -> calculate());
        clearButton.addActionListener(e -> clearAll());
        getRootPane().setDefaultButton(calcButton);   // Enter key = Calculate

        pack();
        setLocationRelativeTo(null);
    }

    private void showBanner(String text, Color color) {
        banner.setText(text);
        banner.setBackground(color);
        banner.setOpaque(true);
        banner.repaint();
    }

    private void hideBanner() {
        banner.setText(" ");
        banner.setOpaque(false);
        banner.repaint();
    }

    private Color colorFor(String category) {
        switch (category) {
            case "Underweight": return new Color(52, 152, 219);   // blue
            case "Normal":      return new Color(39, 174, 96);    // green
            case "Overweight":  return new Color(243, 156, 18);   // orange
            default:            return new Color(192, 57, 43);    // red
        }
    }

    private void showError(String message) {
        hideBanner();
        resultArea.setText(message);
    }

    private void clearAll() {
        nameField.setText("");
        birthField.setText("");
        sexBox.setSelectedIndex(0);
        weightField.setText("");
        heightField.setText("");
        resultArea.setText("");
        hideBanner();
        nameField.requestFocus();
    }

    private void calculate() {
        try {
            String name = nameField.getText().trim();
            String sex = (String) sexBox.getSelectedItem();
            LocalDate birthDate = LocalDate.parse(birthField.getText().trim());
            double weight = Double.parseDouble(weightField.getText().trim());
            double height = Double.parseDouble(heightField.getText().trim());

            if (name.isEmpty()) {
                showError("Please enter a name.");
                return;
            }
            if (birthDate.isAfter(LocalDate.now())) {
                showError("Birthdate cannot be in the future.");
                return;
            }
            if (weight <= 0 || height <= 0) {
                showError("Weight and height must be greater than 0.");
                return;
            }

            BMIRecord record = new BMIRecord(name, sex, birthDate, weight, height);
            showBanner(String.format("BMI %.2f  -  %s", record.getBmi(), record.getCategory()),
                       colorFor(record.getCategory()));
            resultArea.setText(record.getResultText());
        } catch (DateTimeParseException ex) {
            showError("Birthdate must look like 2000-05-31.");
        } catch (NumberFormatException ex) {
            showError("Please enter valid numbers for weight and height.");
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // fall back to the default look
        }
        SwingUtilities.invokeLater(() -> new BMIcalculator().setVisible(true));
    }
}