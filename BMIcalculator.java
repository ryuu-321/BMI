import java.awt.*;
import javax.swing.*;

// ===== CLASS 1: Person (holds the data) =====
// Encapsulation: fields are private and final, accessed only through getters.
class Person {
    private final String name;
    private final double weightKg;
    private final double heightCm;

    public Person(String name, double weightKg, double heightCm) {
        this.name = name;
        this.weightKg = weightKg;
        this.heightCm = heightCm;
    }

    public String getName()     { return name; }
    public double getWeightKg() { return weightKg; }
    public double getHeightCm() { return heightCm; }
}

// ===== CLASS 2: BmiLogic (does the computing) =====
class BmiLogic {
    private final Person person;

    public BmiLogic(Person person) {
        this.person = person;
    }

    // BMI = weight / (height in meters)^2
    public double calculateBmi() {
        double heightM = person.getHeightCm() / 100;
        return person.getWeightKg() / (heightM * heightM);
    }

    public String getCategory() {
        double bmi = calculateBmi();
        if (bmi < 18.5) return "Underweight";
        else if (bmi < 25) return "Normal";
        else if (bmi < 30) return "Overweight";
        else return "Obese";
    }
}

// ===== CLASS 3: BmiFrame (the GUI window) =====
// Inheritance: BmiFrame extends JFrame, so it IS a window.
class BmiFrame extends JFrame {
    // Colors and fonts used for the design
    private static final Color PRIMARY = new Color(0x1F6F8B);
    private static final Color BACKGROUND = new Color(0xF4F7FA);
    private static final Color MUTED = new Color(0x667085);
    private static final Font BODY = new Font(Font.SANS_SERIF, Font.PLAIN, 15);
    private static final Font BOLD = new Font(Font.SANS_SERIF, Font.BOLD, 15);

    // INPUT components
    private final JTextField nameField   = new JTextField(14);
    private final JTextField weightField = new JTextField(14);
    private final JTextField heightField = new JTextField(14);

    // OUTPUT components
    private final JLabel errorLabel    = new JLabel("Error: enter a weight and height above 0.");
    private final JLabel nameValue     = new JLabel();
    private final JLabel weightValue   = new JLabel();
    private final JLabel heightValue   = new JLabel();
    private final JLabel bmiValue      = new JLabel();
    private final JLabel categoryValue = new JLabel();
    private final JPanel resultCard    = new JPanel(new BorderLayout(0, 10));

    public BmiFrame() {
        super("BMI Calculator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // ----- Header -----
        JLabel title = new JLabel("BMI Calculator");
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        header.setBackground(PRIMARY);
        header.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));
        header.add(title);

        // ----- Form (INPUT) -----
        JPanel form = new JPanel(new GridLayout(3, 2, 12, 12));
        form.setOpaque(false);
        form.add(label("Name"));        form.add(styleField(nameField));
        form.add(label("Weight (kg)")); form.add(styleField(weightField));
        form.add(label("Height (cm)")); form.add(styleField(heightField));
        form.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ----- Button -----
        JButton button = new JButton("Calculate");
        button.setFont(BOLD);
        button.setBackground(PRIMARY);
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        button.setPreferredSize(new Dimension(100, 42));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.addActionListener(e -> onCalculate()); // click -> run onCalculate()

        // ----- Error message (hidden at first) -----
        errorLabel.setFont(BODY);
        errorLabel.setForeground(new Color(0xC9482B));
        errorLabel.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        errorLabel.setVisible(false);

        // ----- Result card (OUTPUT, hidden at first) -----
        JLabel resultTitle = new JLabel("BMI RESULT");
        resultTitle.setFont(BOLD);
        resultTitle.setForeground(PRIMARY);

        JPanel grid = new JPanel(new GridLayout(5, 2, 12, 8));
        grid.setOpaque(false);
        addRow(grid, "Name", nameValue);
        addRow(grid, "Weight", weightValue);
        addRow(grid, "Height", heightValue);
        addRow(grid, "BMI", bmiValue);
        addRow(grid, "Category", categoryValue);
        bmiValue.setFont(BOLD);
        categoryValue.setFont(BOLD);

        resultCard.setBackground(Color.WHITE);
        resultCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xD9E0E7)),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)));
        resultCard.add(resultTitle, BorderLayout.NORTH);
        resultCard.add(grid, BorderLayout.CENTER);
        resultCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        resultCard.setVisible(false);

        // ----- Put everything together -----
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(BACKGROUND);
        body.setBorder(BorderFactory.createEmptyBorder(22, 24, 24, 24));
        body.add(form);
        body.add(Box.createVerticalStrut(18));
        body.add(button);
        body.add(errorLabel);
        body.add(Box.createVerticalStrut(16));
        body.add(resultCard);

        JPanel root = new JPanel(new BorderLayout());
        root.add(header, BorderLayout.NORTH);
        root.add(body, BorderLayout.CENTER);

        setContentPane(root);
        getRootPane().setDefaultButton(button); // Enter key also calculates
        pack();
        setMinimumSize(new Dimension(380, getHeight()));
        setLocationRelativeTo(null);
    }

    // Runs when the button is clicked: INPUT -> PROCESS -> OUTPUT
    private void onCalculate() {
        try {
            // INPUT
            String name = nameField.getText().trim();
            double weight = Double.parseDouble(weightField.getText().trim());
            double height = Double.parseDouble(heightField.getText().trim());
            if (weight <= 0 || height <= 0) throw new NumberFormatException();

            // PROCESS (create objects)
            Person person = new Person(name, weight, height);
            BmiLogic calc = new BmiLogic(person);
            String category = calc.getCategory();

            // OUTPUT
            nameValue.setText(name.isEmpty() ? "-" : name);
            weightValue.setText(String.format("%.2f kg", weight));
            heightValue.setText(String.format("%.2f cm", height));
            bmiValue.setText(String.format("%.2f", calc.calculateBmi()));
            categoryValue.setText(category);
            categoryValue.setForeground(colorFor(category));

            errorLabel.setVisible(false);
            resultCard.setVisible(true);
        } catch (NumberFormatException ex) {
            resultCard.setVisible(false);
            errorLabel.setVisible(true);
        }
        pack();
    }

    // Each category has its own color
   private Color colorFor(String category) {
    return switch (category) {
        case "Underweight:" -> new Color(0x2F7FC1); // blue
        case "Normal:"      -> new Color(0x2E9A62); // green
        case "Overweight:"  -> new Color(0xD08A1E); // orange
        default            -> new Color(0xC9482B); // red
    };
}

    // ----- Small helper methods to keep the code short -----
    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(BODY);
        return l;
    }

    private JTextField styleField(JTextField field) {
        field.setFont(BODY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xC5CDD6)),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        return field;
    }

    private void addRow(JPanel grid, String labelText, JLabel value) {
        JLabel l = label(labelText);
        l.setForeground(MUTED);
        value.setFont(BODY);
        grid.add(l);
        grid.add(value);
    }
}

// ===== MAIN CLASS: starts the program =====
// The class name must match the file name: BMIcalculator.java
public class BMIcalculator {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BmiFrame().setVisible(true));
    }
}