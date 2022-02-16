package com.libmgrsys;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;

/**
 * Class representing the transaction creation tab. used to create new transaction using selections from other tabs.
 */
public class TransactionCreator extends JPanel
{
    Connection connection;

    //Table Components-----------------------------------------------------------------------------------------------------------
    JScrollPane scrollPane;
    JTable table;
    Vector<String> columnNames;
    Vector<Vector<Object>> rows;
    //Query components
    ResultSet resultSet;
    String queryString = "SELECT `ID`, `title`, `author`, `genre` FROM INVENTORY WHERE `ID`=NULL";
    Set<Integer> bookIDs = new HashSet<>();

    JButton btnRemove;

    //Form Components------------------------------------------------------------------------------------------------------------
    Integer clientID = 0;
    JLabel lFirstName;
    JTextField fFirstName;
    JLabel lLastName;
    JTextField fLastName;
    JLabel lStartDate;
    JTextField fStartDate;
    JLabel lReturnWeeks;
    JTextField fReturnWeeks;
    int weeks = 0;
    JButton btnPlus;
    JButton btnMinus;
    JLabel lReturnDate;
    JTextField fReturnDate;
    boolean overdue = false;
    int currentBooks = 0;
    int maxBooks = 0;
    JLabel lWarning;
    boolean check = false;

    //Add and Edit buttons
    JButton btnConfirm;
    JButton btnReset;

    /**
     * Constructor, the connection must be specifically created with the DatabaseHelper.connect() method.
     * @param connection MySQL Connection
     * @see DatabaseHelper
     * @see DatabaseHelper#connect()
     */
    public TransactionCreator(Connection connection)
    {
        this.connection = connection;

        this.btnRemove = new JButton("Remove selection");
        this.btnRemove.addActionListener(this::btnRemoveActionPerformed);

        initTransactionList();
        initTransactionForm();

        initLayout();

        updateButtons();
        updateWarning();
    }

    /**
     * Initializes the table containing the selected books and all its functions.
     */
    private void initTransactionList()
    {
        this.resultSet = DatabaseHelper.createResultSet(this.connection, this.queryString);

        this.table = new JTable();
        generateTableModel();
        adjustColumnModel();
        //Adjust table settings
        this.table.getTableHeader().setReorderingAllowed(false);
        this.table.setAutoCreateRowSorter(true);
        this.table.setRowSelectionAllowed(true);

        this.table.getSelectionModel().addListSelectionListener(evt -> updateButtons());

        //Create a scroll pane and add the table to it
        this.scrollPane = new JScrollPane(table);
    }

    /**
     * Creates a table model with the data from the result set then applies it to the table. The result set is generated using
     * the IDs provided by the Inventory tab using the getLendIDs() method in the main menu class. The model checks weather the
     * first column is an Integer in order for the sorting by ID to work. Also, we make cells non-editable. This method is also
     * used to regenerate the table data when performing an update.
     * @see Inventory
     * @see Inventory#getLendIDs()
     */
    private void generateTableModel()
    {
        //Extract data from result set
        this.columnNames = DatabaseHelper.getColumnNames(this.resultSet);
        this.rows = DatabaseHelper.getRows(this.resultSet);
        //Generate table model
        TableModel tableModel = new DefaultTableModel(this.rows , this.columnNames)
        {
            @SuppressWarnings("rawtypes")
            @Override
            public Class getColumnClass(int column)
            {
                if (column == 0)
                {
                    return Integer.class;
                }
                else
                {
                    return String.class;
                }
            }

            @Override
            public boolean isCellEditable(int row, int column)
            {
                return false;
            }
        };
        //Set table model
        this.table.setModel(tableModel);
    }

    /**
     * Adjusts the column sizes for the table. This method is also used to readjust the column sizes after an update.
     */
    private void adjustColumnModel()
    {
        //Get the column model of the table
        TableColumnModel columnModel = this.table.getColumnModel();
        //Get the number of columns
        int columnCount = this.table.getColumnCount();
        //First column will be smaller since it's the ID column
        TableColumn column = columnModel.getColumn(0);
        column.setPreferredWidth(50);
        column.setMinWidth(50);
        column.setMaxWidth(50);
        //All the rest will have the same size bounds
        for (int i = 1; i < columnCount; i++)
        {
            column = columnModel.getColumn(i);
            column.setPreferredWidth(100);
            column.setMinWidth(50);
        }
    }

    /**
     * Creates the transaction form that contains the transaction data.
     */
    private void initTransactionForm()
    {
        this.lFirstName = new JLabel("First Name:");
        this.fFirstName = new JTextField();
        this.fFirstName.setEditable(false);
        this.lLastName = new JLabel("Last Name:");
        this.fLastName = new JTextField();
        this.fLastName.setEditable(false);
        this.lStartDate = new JLabel("Start Date:");
        this.fStartDate = new JTextField();
        this.fStartDate.setEditable(false);
        this.lReturnWeeks = new JLabel("Return in:");
        this.fReturnWeeks = new JTextField();
        this.fReturnWeeks.setHorizontalAlignment(JTextField.CENTER);
        this.fReturnWeeks.setEditable(false);
        this.btnMinus = new JButton("-");
        this.btnMinus.addActionListener(this::btnMinusActionPerformed);
        this.btnMinus.setEnabled(false);
        this.btnPlus = new JButton("+");
        this.btnPlus.addActionListener(this::btnPlusActionPerformed);
        this.btnPlus.setEnabled(false);
        this.lReturnDate = new JLabel("Return Date:");
        this.fReturnDate = new JTextField();
        this.fReturnDate.setEditable(false);
        this.lWarning = new JLabel("Warning!");
        Font warningFont = new Font("Arial", Font.BOLD, 16);
        this.lWarning.setFont(warningFont);
        this.lWarning.setForeground(Color.red);

        this.btnConfirm = new JButton("Confirm");
        this.btnConfirm.addActionListener(this::btnConfirmActionPerformed);
        this.btnReset = new JButton("Reset");
        this.btnReset.addActionListener(this::btnResetActionPerformed);
    }

    /**
     * Updates the data in the transaction book list, used mostly by the remove button.
     */
    public void updateTransactionList()
    {
        //Generate a new result set
        this.resultSet = DatabaseHelper.createResultSet(this.connection, this.queryString);
        //Regenerate the table
        generateTableModel();
        adjustColumnModel();
    }

    /**
     * Sets the book ID for the books that will appear in the transaction book list. Used together with Inventory.getLendIDs().
     * @see Inventory
     * @see Inventory#getLendIDs()
     */
    public void setBookIDs(Set<Integer> IDs)
    {
        this.bookIDs = IDs;
    }

    /**
     * Updates the query string after changing the book IDs.
     */
    public void updateQueryString()
    {
        if (this.bookIDs.size() == 0)
        {
            this.queryString = "SELECT `ID`, `title`, `author`, `genre` FROM INVENTORY WHERE `ID`=NULL";
        }
        else
        {
            ArrayList<String> arrayListIDs= new ArrayList<>();
            for (int id : this.bookIDs)
            {
                arrayListIDs.add(Integer.toString(id));
            }

            String IDList = "";

            IDList += "('";
            IDList += String.join("', '", arrayListIDs);
            IDList += "')";

            this.queryString = "SELECT `ID`, `title`, `author`, `genre` FROM INVENTORY WHERE `ID` IN " + IDList;
        }
    }

    /**
     * Sets up the layout of the inventory window.
     */
    private void initLayout()
    {
        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);

        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(scrollPane)
                        .addComponent(this.btnRemove))
                .addGap(20)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(this.lFirstName, 100, 100, 100)
                                        .addComponent(this.lLastName, 100, 100, 100)
                                        .addComponent(this.lStartDate, 100, 100, 100)
                                        .addComponent(this.lReturnWeeks, 100, 100, 100)
                                        .addComponent(this.lReturnDate, 100, 100, 100))
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(this.fFirstName, 200, 200, 300)
                                        .addComponent(this.fLastName, 200, 200, 300)
                                        .addComponent(this.fStartDate, 200, 200, 300)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(this.fReturnWeeks, 100, 100, 100)
                                                .addGap(5)
                                                .addComponent(this.btnMinus)
                                                .addGap(5)
                                                .addComponent(this.btnPlus))
                                        .addComponent(this.fReturnDate, 200, 200, 300)))
                        .addComponent(this.lWarning)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(this.btnConfirm, 100, 100, 100)
                                .addGap(40)
                                .addComponent(this.btnReset, 100, 100, 100)))
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(this.scrollPane)
                                .addGap(10)
                                .addComponent(this.btnRemove))
                        .addGap(20)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(this.lFirstName, 25, 25, 25)
                                        .addComponent(this.fFirstName, 25, 25, 25))
                                .addGap(5)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(this.lLastName, 25, 25, 25)
                                        .addComponent(this.fLastName, 25, 25, 25))
                                .addGap(5)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(this.lStartDate, 25, 25, 25)
                                        .addComponent(this.fStartDate, 25, 25, 25))
                                .addGap(5)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(this.lReturnWeeks, 25, 25, 25)
                                        .addComponent(this.fReturnWeeks, 25, 25, 25)
                                        .addComponent(this.btnMinus, 25, 25, 25)
                                        .addComponent(this.btnPlus, 25, 25, 25))
                                .addGap(5)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(this.lReturnDate, 25, 25, 25)
                                        .addComponent(this.fReturnDate, 25, 25, 25))
                                .addGap(10)
                                .addComponent(this.lWarning)
                                .addGap(10)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(this.btnConfirm, 25, 25, 25)
                                        .addComponent(this.btnReset, 25, 25, 25))))
        );
    }

    /**
     * Used by the remove button, removes the selected books from the transaction book list.
     */
    private void btnRemoveActionPerformed(ActionEvent evt)
    {
        if (table.getSelectedRows().length == 0)
        {
            JOptionPane.showMessageDialog(null, "No books selected");
        }
        else
        {
            int[] selectedRows = table.getSelectedRows();

            for (int i : selectedRows)
            {
                this.bookIDs.remove((Integer) table.getValueAt(i, 0));
            }

            updateQueryString();
            updateTransactionList();
            updateWarning();
        }
    }

    /**
     * Updates the warning under the form.
     */
    public void updateWarning()
    {
        this.check = false;
        this.lWarning.setVisible(true);

        if (this.clientID == 0)
        {
            this.lWarning.setText("No client selected!");
        }
        else if (this.bookIDs.size() == 0)
        {
            this.lWarning.setText("No books selected!");
        }
        else if (this.overdue)
        {
            this.lWarning.setText("Client has overdue books!");
        }
        else if (this.currentBooks + this.table.getRowCount() > this.maxBooks)
        {
            this.lWarning.setText("Book limit exceeded by " + (this.currentBooks + this.table.getRowCount() - this.maxBooks) + "!");
        }
        else
        {
            this.lWarning.setText("");
            this.lWarning.setVisible(false);
            this.check = true;
        }

        this.btnConfirm.setEnabled(this.check);
    }

    /**
     * Updates the data in the form.
     */
    public void updateFormData()
    {
        if (this.clientID == 0)
        {
            this.fFirstName.setText("");
            this.fLastName.setText("");
            this.fStartDate.setText("");
            this.fReturnDate.setText("");
            this.fReturnWeeks.setText("");
            this.weeks = 0;
            this.btnMinus.setEnabled(false);
            this.btnPlus.setEnabled(false);
            this.maxBooks = 0;
            this.currentBooks = 0;
            this.overdue = false;
        }
        else
        {
            String SQL = "SELECT * from CLIENT_LIST WHERE ID=?";

            try
            {
                PreparedStatement preparedStatement = this.connection.prepareStatement(SQL);

                preparedStatement.setString(1, Integer.toString(clientID));

                ResultSet rs = preparedStatement.executeQuery();

                if (!rs.next())
                {
                    JOptionPane.showMessageDialog(null, "Something went wrong while getting the client data, try selecting a client again");
                    this.clientID = 0;
                    updateFormData();
                }
                else
                {
                    this.fFirstName.setText(rs.getString(2));
                    this.fLastName.setText(rs.getString(3));
                    this.fStartDate.setText(LocalDate.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
                    this.fReturnWeeks.setText("1 week");
                    this.weeks = 1;
                    this.btnMinus.setEnabled(false);
                    this.btnPlus.setEnabled(true);
                    this.fReturnDate.setText(LocalDate.now().plusDays(7L * weeks).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
                    this.maxBooks = rs.getInt(5);
                    this.currentBooks = rs.getInt(6);
                    this.overdue = (rs.getString(7).equals("Yes"));
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Sets client ID for the transaction form to generate data from. Used together with ClientList.getSelectedClientID().
     * @see ClientList
     * @see ClientList#getSelectedClientID()
     */
    public void setClientID(Integer ID)
    {
        this.clientID = ID;
    }

    /**
     * Updates the state of buttons, if they are enabled or not. Used by the ListSelectionListener in the table to automatically
     * update button states based on selections.
     */
    private void updateButtons()
    {
        this.btnRemove.setEnabled(this.table.getSelectedRows().length > 0);
    }

    /**
     * Used by the minus button in the form, subtracts a week from the return date, cannot go lower than 1 week.
     */
    private void btnMinusActionPerformed(ActionEvent evt)
    {
        if (weeks > 1)
        {
            weeks -= 1;
            btnPlus.setEnabled(true);

            if (weeks == 1)
            {
                btnMinus.setEnabled(false);
            }
        }

        this.fReturnWeeks.setText(weeks + " " + ((weeks > 1) ? "weeks" : "week"));
        this.fReturnDate.setText(LocalDate.now().plusDays(7L * weeks).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
    }

    /**
     * Used by the plus button in the form, adds a week to the return date, cannot go higher than 4 weeks.
     */
    private void btnPlusActionPerformed(ActionEvent evt)
    {
        if (weeks < 4)
        {
            weeks += 1;
            btnMinus.setEnabled(true);

            if (weeks == 4)
            {
                btnPlus.setEnabled(false);
            }
        }

        this.fReturnWeeks.setText(weeks + " " + ((weeks > 1) ? "weeks" : "week"));
        this.fReturnDate.setText(LocalDate.now().plusDays(7L * weeks).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
    }

    /**
     * Used by the reset button, clears all data in the window.
     */
    private void btnResetActionPerformed(ActionEvent evt)
    {
        clientID = 0;
        bookIDs.clear();

        updateQueryString();
        updateTransactionList();
        updateFormData();
        updateWarning();
    }

    /**
     * Used by the confirm button, creates a transaction, updates the status of the books related to the transaction and links them to the transaction.
     */
    private void btnConfirmActionPerformed(ActionEvent evt)
    {
        String SQL = "INSERT INTO TRANSACTIONS (`client_id`, `start_date`, `return_date`, `no_books`, `returned`) VALUES(?, ?, ?, ?, ?)";

        try
        {
            PreparedStatement preparedStatement = this.connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setString(1, Integer.toString(clientID));
            preparedStatement.setString(2, LocalDate.parse(fStartDate.getText(), DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)).format(DateTimeFormatter.ISO_LOCAL_DATE));
            preparedStatement.setString(3, LocalDate.parse(fReturnDate.getText(), DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)).format(DateTimeFormatter.ISO_LOCAL_DATE));
            preparedStatement.setString(4, Integer.toString(bookIDs.size()));
            preparedStatement.setString(5, Integer.toString(0));

            System.out.println("Executing: " + SQL);

            if (preparedStatement.executeUpdate() > 0)
            {
                ResultSet rs = preparedStatement.getGeneratedKeys();
                rs.next();
                int transactionID = rs.getInt(1);

                StringJoiner stringJoiner = new StringJoiner(", ");
                for (int id : bookIDs)
                {
                    stringJoiner.add("(" + transactionID + ", " + id + ")");
                }
                SQL = "INSERT INTO TRANSACTION_BOOKS (`transaction_id`, `book_id`) VALUES" + stringJoiner;

                preparedStatement = this.connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);

                System.out.println("Executing: " + SQL);

                if (preparedStatement.executeUpdate() > 0)
                {
                    ArrayList<String> arrayListIDs= new ArrayList<>();
                    for (int id : this.bookIDs)
                    {
                        arrayListIDs.add(Integer.toString(id));
                    }

                    String IDList = "";

                    IDList += "('";
                    IDList += String.join("', '", arrayListIDs);
                    IDList += "')";

                    SQL = "UPDATE BOOKS SET `available`=0 WHERE `book_id` IN " + IDList;

                    preparedStatement = this.connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);

                    System.out.println("Executing: " + SQL);

                    if (preparedStatement.executeUpdate() > 0)
                    {
                        JOptionPane.showMessageDialog(null, "Transaction successfully created");

                        bookIDs.clear();
                        updateQueryString();
                        updateTransactionList();
                        updateWarning();
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(null, "Something went wrong while updating books status");
                    }

                }
                else
                {
                    JOptionPane.showMessageDialog(null, "Something went wrong while linking the books to the transaction");
                }
            }
            else
            {
                JOptionPane.showMessageDialog(null, "Something went wrong while creating the transaction");
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
