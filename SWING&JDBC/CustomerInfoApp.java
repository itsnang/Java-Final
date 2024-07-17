import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CustomerInfoApp extends JFrame implements ActionListener {
    private JLabel idLabel, lastNameLabel, firstNameLabel, phoneLabel;
    private JButton previousButton, nextButton;
    private ResultSet resultSet;

    public CustomerInfoApp() {
        initializeUI();
        loadCustomerData();
    }

    private void initializeUI() {
        setTitle("Customer");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = createGridBagConstraints();

        addLabelAndField(gbc, 0, "ID:", idLabel = new JLabel());
        addLabelAndField(gbc, 1, "Last Name:", lastNameLabel = new JLabel());
        addLabelAndField(gbc, 2, "First Name:", firstNameLabel = new JLabel());
        addLabelAndField(gbc, 3, "Phone:", phoneLabel = new JLabel());

        add(createButtonPanel(), createButtonPanelConstraints(gbc));
    }

    private GridBagConstraints createGridBagConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        return gbc;
    }

    private void addLabelAndField(GridBagConstraints gbc, int yPos, String labelText, JLabel label) {
        gbc.gridx = 0;
        gbc.gridy = yPos;
        add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        add(label, gbc);
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        previousButton = createButton("Previous", false);
        nextButton = createButton("Next", true);

        buttonPanel.add(previousButton);
        buttonPanel.add(nextButton);

        return buttonPanel;
    }

    private JButton createButton(String text, boolean enabled) {
        JButton button = new JButton(text);
        button.addActionListener(this);
        button.setEnabled(enabled);
        return button;
    }

    private GridBagConstraints createButtonPanelConstraints(GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        return gbc;
    }

    private void loadCustomerData() {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            resultSet = statement.executeQuery("SELECT * FROM customer");
            if (resultSet.next()) {
                displayCustomerData();
            } else {
                nextButton.setEnabled(false);
            }
        } catch (SQLException e) {
            showErrorDialog("Error loading customer data");
        }
    }

    private void displayCustomerData() {
        try {
            idLabel.setText(String.valueOf(resultSet.getInt("customer_id")));
            lastNameLabel.setText(resultSet.getString("customer_last_name"));
            firstNameLabel.setText(resultSet.getString("customer_first_name"));
            phoneLabel.setText(resultSet.getString("customer_phone"));

            previousButton.setEnabled(!resultSet.isFirst());
            nextButton.setEnabled(!resultSet.isLast());
        } catch (SQLException e) {
            showErrorDialog("Error displaying customer data");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == previousButton && resultSet.previous()) {
                displayCustomerData();
            } else if (e.getSource() == nextButton && resultSet.next()) {
                displayCustomerData();
            }
        } catch (SQLException ex) {
            showErrorDialog("Error navigating customer data");
        }
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CustomerInfoApp app = new CustomerInfoApp();
            app.setVisible(true);
        });
    }
}
