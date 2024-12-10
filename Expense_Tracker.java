import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ExpenseTracker {
  
    static class User implements Serializable {
        private String username;
        private String password;

        public User(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() {
            return username;
        }

        public boolean checkPassword(String inputPassword) {
            return this.password.equals(inputPassword);
        }
    }

    static class UserManager {
        private static final String USER_FILE = "users.ser";
        private Map<String, User> users;

        public UserManager() {
            users = loadUsers();
        }

        private Map<String, User> loadUsers() {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USER_FILE))) {
                return (Map<String, User>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                return new HashMap<>();
            }
        }

        private void saveUsers() {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USER_FILE))) {
                oos.writeObject(users);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public boolean registerUser(String username, String password) {
            if (users.containsKey(username)) {
                return false;
            }
            users.put(username, new User(username, password));
            saveUsers();
            return true;
        }

        public boolean authenticateUser(String username, String password) {
            User user = users.get(username);
            return user != null && user.checkPassword(password);
        }
    }

    static class Expense implements Serializable {
        private String category;
        private double amount;
        private String date;

        public Expense(String category, double amount, String date) {
            this.category = category;
            this.amount = amount;
            this.date = date;
        }

        public String getCategory() {
            return category;
        }

        public double getAmount() {
            return amount;
        }

        public String getDate() {
            return date;
        }

        @Override
        public String toString() {
            return category + " | " + amount + " | " + date;
        }
    }

    public static class ExpenseManager {
        private ArrayList<Expense> expenses;
        private double totalAmount;
        private double overallBudget;
        private Map<String, Double> categorySpending;
        
        private JFrame frame;
        private JTextField categoryInput;
        private JTextField amountInput;
        private JTextField dateInput;
        private JTable expensesTable;
        private JLabel totalAmountLabel;
        private JLabel overallBudgetStatusLabel;
        private DefaultTableModel expenseTableModel;
        private JDialog editDialog;
        private JTextField editCategoryField;
        private JTextField editAmountField;
        private JTextField editDateField;
        private JButton logoutButton;
        private LoginWindow loginWindow;

        private static final Color MAIN_BACKGROUND = new Color(255, 253, 248); // Soft cream
        private static final Color INPUT_PANEL_BACKGROUND = new Color(255, 240, 220); // Soft peach
        private static final Color STATUS_PANEL_BACKGROUND = new Color(230, 230, 250); // Lavender
        private static final Color TABLE_BACKGROUND = new Color(255, 255, 240); // Pale yellow
        private static final Color TABLE_HEADER_BACKGROUND = new Color(255, 218, 185); // Peach

        public ExpenseManager(LoginWindow loginWindow) {
            this.loginWindow = loginWindow;
            
            expenses = new ArrayList<>();
            totalAmount = 0.0;
            overallBudget = 0.0;
            categorySpending = new HashMap<>();
            
            frame = new JFrame("Expense Tracker");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLayout(new BorderLayout());
            frame.getContentPane().setBackground(MAIN_BACKGROUND);

            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBackground(MAIN_BACKGROUND);

            JPanel topPanel = new JPanel(new BorderLayout());
            logoutButton = new JButton("Logout");
            logoutButton.addActionListener(e -> logout());
            topPanel.add(logoutButton, BorderLayout.EAST);

            JPanel budgetPanel = createBudgetPanel();
            topPanel.add(budgetPanel, BorderLayout.CENTER);
            mainPanel.add(topPanel, BorderLayout.NORTH);

            JPanel expensesPanel = createExpensesPanel();
            mainPanel.add(expensesPanel, BorderLayout.CENTER);

            frame.add(mainPanel);
            frame.setVisible(true);

            loadData();
            updateBudgetStatus();
            refreshExpenseTable();
        }

        private void logout() {
     
            saveData();

            frame.dispose();

            SwingUtilities.invokeLater(() -> {
                new LoginWindow();
            });
        }

        private JPanel createBudgetPanel() {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            panel.setBackground(INPUT_PANEL_BACKGROUND);

            JLabel overallBudgetLabel = new JLabel("Set Budget: ");
            JTextField overallBudgetInput = new JTextField(10);
            JButton setOverallBudgetBtn = new JButton("Set Budget");

            panel.add(overallBudgetLabel);
            panel.add(overallBudgetInput);
            panel.add(setOverallBudgetBtn);

            setOverallBudgetBtn.addActionListener(e -> {
                try {
                    double amount = Double.parseDouble(overallBudgetInput.getText());
                    if (amount < 0) {
                        JOptionPane.showMessageDialog(frame, "Budget cannot be negative.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    overallBudget = amount;
                    updateBudgetStatus();
                    overallBudgetInput.setText("");
                    JOptionPane.showMessageDialog(frame, "Budget set to: " + amount);
                    saveData();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Please enter a valid amount.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            return panel;
        }

        private JPanel createExpensesPanel() {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBackground(MAIN_BACKGROUND);

            JPanel inputPanel = new JPanel(new GridBagLayout());
            inputPanel.setBackground(INPUT_PANEL_BACKGROUND);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            gbc.gridx = 0; gbc.gridy = 0;
            inputPanel.add(new JLabel("Category:"), gbc);
            
            gbc.gridx = 1; gbc.gridy = 0;
            inputPanel.add(new JLabel("Amount:"), gbc);
            
            gbc.gridx = 2; gbc.gridy = 0;
            inputPanel.add(new JLabel("Date:"), gbc);

            categoryInput = new JTextField(15);
            amountInput = new JTextField(15);
            dateInput = new JTextField("yyyy-mm-dd", 15);

            gbc.gridy = 1;
            gbc.gridx = 0;
            inputPanel.add(categoryInput, gbc);
            
            gbc.gridx = 1;
            inputPanel.add(amountInput, gbc);
            
            gbc.gridx = 2;
            inputPanel.add(dateInput, gbc);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            buttonPanel.setBackground(INPUT_PANEL_BACKGROUND);
            JButton addBtn = new JButton("Add Expense");
            JButton editBtn = new JButton("Edit Selected");
            JButton deleteBtn = new JButton("Delete Selected");
            JButton undoBtn = new JButton("Undo Last");

            buttonPanel.add(addBtn);
            buttonPanel.add(editBtn);
            buttonPanel.add(deleteBtn);
            buttonPanel.add(undoBtn);

            JPanel topPanel = new JPanel(new BorderLayout());
            topPanel.setBackground(MAIN_BACKGROUND);
            topPanel.add(inputPanel, BorderLayout.CENTER);
            topPanel.add(buttonPanel, BorderLayout.SOUTH);

            expenseTableModel = new DefaultTableModel(
                    new String[]{"Category", "Amount", "Date"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            expensesTable = new JTable(expenseTableModel);
            expensesTable.setBackground(TABLE_BACKGROUND);
            expensesTable.getTableHeader().setBackground(TABLE_HEADER_BACKGROUND);
            JScrollPane scrollPane = new JScrollPane(expensesTable);
            scrollPane.getViewport().setBackground(TABLE_BACKGROUND);

            JPanel statusPanel = new JPanel(new GridLayout(2, 1));
            statusPanel.setBackground(STATUS_PANEL_BACKGROUND);
            totalAmountLabel = new JLabel("Total Amount: 0.0");
            overallBudgetStatusLabel = new JLabel("Budget Status: No Budget Set");
            statusPanel.add(totalAmountLabel);
            statusPanel.add(overallBudgetStatusLabel);

            panel.add(topPanel, BorderLayout.NORTH);
            panel.add(scrollPane, BorderLayout.CENTER);
            panel.add(statusPanel, BorderLayout.SOUTH);

            addBtn.addActionListener(e -> addExpense());
            editBtn.addActionListener(e -> editSelectedExpense());
            deleteBtn.addActionListener(e -> deleteSelectedExpense());
            undoBtn.addActionListener(e -> undoLastExpense());

            return panel;
        }

        private void addExpense() {
            String category = categoryInput.getText().trim();
            String amountText = amountInput.getText();
            String date = dateInput.getText();
            try {
                if (category.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please enter a category.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                double amount = Double.parseDouble(amountText);
                if (date.isEmpty() || amount <= 0) {
                    JOptionPane.showMessageDialog(frame, "Please enter valid details.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Expense newExpense = new Expense(category, amount, date);
                expenses.add(newExpense);
                categorySpending.put(category, categorySpending.getOrDefault(category, 0.0) + amount);
                totalAmount += amount;
                totalAmountLabel.setText("Total Amount: " + totalAmount);
                
                addExpenseToTable(newExpense);
                updateBudgetStatus();
                saveData();
                categoryInput.setText("");
                amountInput.setText("");
                dateInput.setText("yyyy-mm-dd");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Please enter a valid amount.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void editSelectedExpense() {
            int selectedRow = expensesTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(frame, "Please select an expense to edit.",
                        "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Expense selectedExpense = expenses.get(selectedRow);
            editDialog = new JDialog(frame, "Edit Expense", true);
            editDialog.setLayout(new GridLayout(4, 2, 5, 5));

            editCategoryField = new JTextField(selectedExpense.getCategory());
            editAmountField = new JTextField(String.valueOf(selectedExpense.getAmount()));
            editDateField = new JTextField(selectedExpense.getDate());

            editDialog.add(new JLabel("Category:"));
            editDialog.add(editCategoryField);
            editDialog.add(new JLabel("Amount:"));
            editDialog.add(editAmountField);
            editDialog.add(new JLabel("Date:"));
            editDialog.add(editDateField);

            JButton saveBtn = new JButton("Save");
            JButton cancelBtn = new JButton("Cancel");
            saveBtn.addActionListener(e -> saveEditedExpense(selectedRow, selectedExpense));
            cancelBtn.addActionListener(e -> editDialog.dispose());

            editDialog.add(saveBtn);
            editDialog.add(cancelBtn);

            editDialog.pack();
            editDialog.setLocationRelativeTo(frame);
            editDialog.setVisible(true);
        }

        private void saveEditedExpense(int selectedRow, Expense oldExpense) {
            try {
                String newCategory = editCategoryField.getText().trim();
                double newAmount = Double.parseDouble(editAmountField.getText().trim());
                String newDate = editDateField.getText().trim();

                if (newCategory.isEmpty() || newDate.isEmpty() || newAmount <= 0) {
                    JOptionPane.showMessageDialog(editDialog, "Please enter valid details.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                categorySpending.put(oldExpense.getCategory(),
                        categorySpending.getOrDefault(oldExpense.getCategory(), 0.0) - oldExpense.getAmount());
                categorySpending.put(newCategory,
                        categorySpending.getOrDefault(newCategory, 0.0) + newAmount);

                totalAmount = totalAmount - oldExpense.getAmount() + newAmount;
                totalAmountLabel.setText("Total Amount: " + totalAmount);

                Expense newExpense = new Expense(newCategory, newAmount, newDate);
                expenses.set(selectedRow, newExpense);

                refreshExpenseTable();
                updateBudgetStatus();
                saveData();
                editDialog.dispose();
                JOptionPane.showMessageDialog(frame, "Expense updated successfully!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(editDialog, "Please enter a valid amount.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void addExpenseToTable(Expense expense) {
            expenseTableModel.addRow(new Object[]{
                    expense.getCategory(),
                    expense.getAmount(),
                    expense.getDate()
            });
        }

        private void deleteSelectedExpense() {
            int selectedRow = expensesTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(frame, "Please select an expense to delete.",
                        "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Expense expense = expenses.get(selectedRow);
            expenses.remove(expense);
            totalAmount -= expense.getAmount();
            totalAmountLabel.setText("Total Amount: " + totalAmount);
            categorySpending.put(expense.getCategory(),
                    categorySpending.getOrDefault(expense.getCategory(), 0.0) - expense.getAmount());
            refreshExpenseTable();
            updateBudgetStatus();
            saveData();
            JOptionPane.showMessageDialog(frame, "Expense deleted successfully!");
        }

        private void updateBudgetStatus() {
            if (overallBudget == 0) {
                overallBudgetStatusLabel.setText("Budget Status: No Budget Set");
            } else {
                double remaining = overallBudget - totalAmount;
                String status;
                if (remaining < 0) {
                    status = "OVER BUDGET by " + String.format("%.2f", -remaining);
                } else if (remaining < (overallBudget * 0.1)) {
                    status = "NEAR LIMIT (Remaining: " + String.format("%.2f", remaining) + ")";
                } else {
                    status = "Within Budget (Remaining: " + String.format("%.2f", remaining) + ")";
                }
                overallBudgetStatusLabel.setText("Budget Status: " + status);
            }
        }

        private void refreshExpenseTable() {
            expenseTableModel.setRowCount(0);
            for (Expense exp : expenses) {
                addExpenseToTable(exp);
            }
        }

        private void saveData() {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("expense_data.ser"))) {
                oos.writeObject(expenses);
                oos.writeDouble(overallBudget);
                oos.writeObject(categorySpending);
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error saving data: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void loadData() {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("expense_data.ser"))) {
                expenses = (ArrayList<Expense>) ois.readObject();
                overallBudget = ois.readDouble();
                categorySpending = (Map<String, Double>) ois.readObject();
                
                for (Expense exp : expenses) {
                    totalAmount += exp.getAmount();
                }
                totalAmountLabel.setText("Total Amount: " + totalAmount);
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("No previous data found.");
            }
        }

        private void undoLastExpense() {
            if (expenses.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "No expenses to undo.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Expense lastExpense = expenses.remove(expenses.size() - 1);
            totalAmount -= lastExpense.getAmount();
            totalAmountLabel.setText("Total Amount: " + totalAmount);

            categorySpending.put(lastExpense.getCategory(),
                    categorySpending.getOrDefault(lastExpense.getCategory(), 0.0) - lastExpense.getAmount());

            refreshExpenseTable();
            updateBudgetStatus();
            saveData();
            JOptionPane.showMessageDialog(frame, "Last expense undone successfully!");
        }
    }

    // Login Window
    static class LoginWindow {
        private JFrame loginFrame;
        private UserManager userManager;

        public LoginWindow() {
            userManager = new UserManager();
            initLoginWindow();
        }

        private void initLoginWindow() {
            loginFrame = new JFrame("Expense Tracker - Login");
            loginFrame.setSize(400, 300);
            loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            loginFrame.setLayout(new BorderLayout(10, 10));

            JPanel loginPanel = new JPanel(new GridBagLayout());
            loginPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);

            JLabel usernameLabel = new JLabel("Username:");
            JTextField usernameField = new JTextField(15);
            JLabel passwordLabel = new JLabel("Password:");
            JPasswordField passwordField = new JPasswordField(15);

            JButton loginButton = new JButton("Login");
            JButton registerButton = new JButton("Register");

            gbc.gridx = 0;
            gbc.gridy = 0;
            loginPanel.add(usernameLabel, gbc);

            gbc.gridx = 1;
            loginPanel.add(usernameField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 1;
            loginPanel.add(passwordLabel, gbc);

            gbc.gridx = 1;
            loginPanel.add(passwordField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.gridwidth = 2;
            gbc.anchor = GridBagConstraints.CENTER;
            loginPanel.add(loginButton, gbc);

            gbc.gridy = 3;
            loginPanel.add(registerButton, gbc);

                  
            loginButton.addActionListener(e -> {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword());

                if (userManager.authenticateUser(username, password)) {
                    loginFrame.dispose();
                    SwingUtilities.invokeLater(() -> new ExpenseManager(this));
                } else {
                    JOptionPane.showMessageDialog(loginFrame, 
                        "Invalid username or password", 
                        "Login Failed", 
                        JOptionPane.ERROR_MESSAGE);
                }
            });

            registerButton.addActionListener(e -> {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword());

                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(loginFrame, 
                        "Username and password cannot be empty", 
                        "Registration Failed", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (userManager.registerUser(username, password)) {
                    JOptionPane.showMessageDialog(loginFrame, 
                        "User registered successfully", 
                        "Registration", 
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(loginFrame, 
                        "Username already exists", 
                        "Registration Failed", 
                        JOptionPane.ERROR_MESSAGE);
                }
            });

            loginFrame.add(loginPanel, BorderLayout.CENTER);
            loginFrame.setLocationRelativeTo(null);
            loginFrame.setVisible(true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginWindow::new);
    }
}