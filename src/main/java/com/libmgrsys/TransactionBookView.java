package com.libmgrsys;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

/**
 * A window that shows all the books related to a transaction in a table, with the ability to mark them as returned or not returned.
 */
public class TransactionBookView extends JDialog
{
    //Database connection
    Connection connection;

    //The parent transaction list window
    TransactionList parent;

    JPanel mainPanel;

    //Table Components-----------------------------------------------------------------------------------------------------------
    JScrollPane scrollPane;
    JTable table;
    Vector<String> columnNames;
    Vector<Vector<Object>> rows;
    //Query components
    ResultSet resultSet;
    String queryString;
    int transactionID;
    boolean returned;

    //Buttons
    JLabel lChangeStatus;
    JButton btnReturned;
    JButton btnNotReturned;

    /**
     * Constructor, the connection must be specifically created with the DatabaseHelper.connect() method, the parent is the
     * TransactionList that created the object.
     * @param connection MySQL Connection
     * @param parent the parent TransactionList
     * @param row the row in the transaction list table from which to get the ID of the transaction
     * @see DatabaseHelper
     * @see DatabaseHelper#connect()
     * @see TransactionList
     */
    public TransactionBookView(Connection connection, TransactionList parent, int row)
    {
        this.connection = connection;

        this.parent = parent;

        this.transactionID = (Integer) parent.getTransactionAt(row).get(0);
        this.queryString = "SELECT `ID`, `Title`, `Author`, `Genre`, `Status` FROM TRANSACTION_BOOKS_VIEW WHERE `transaction_id`=" + this.transactionID;
        this.returned = parent.getTransactionAt(row).get(6).equals("Returned");

        initTable();

        this.mainPanel = new JPanel();

        this.lChangeStatus = new JLabel("Mark as:");

        this.btnReturned = new JButton("Returned");
        this.btnReturned.addActionListener(this::btnReturnedActionPerformed);

        this.btnNotReturned = new JButton("Not Returned");
        this.btnNotReturned.addActionListener(this::btnNotReturnedActionPerformed);

        GroupLayout layout = new GroupLayout(this.mainPanel);
        this.mainPanel.setLayout(layout);

        layout.setAutoCreateContainerGaps(true);
        layout.setAutoCreateGaps(true);

        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(this.scrollPane, -1, 800, -1)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(this.lChangeStatus)
                                .addComponent(this.btnReturned)
                                .addGap(5)
                                .addComponent(this.btnNotReturned)))
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(this.scrollPane, -1, 150, -1)
                .addGap(10)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(this.lChangeStatus)
                        .addComponent(this.btnReturned)
                        .addComponent(this.btnNotReturned))
        );

        this.add(mainPanel);

        updateButtons();

        this.setTitle("Books for the selected transaction");
        this.setResizable(true);
        this.setModalityType(ModalityType.DOCUMENT_MODAL);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    /**
     * Used by the mark as returned button, changes the status of the selected books to returned.
     */
    private void btnReturnedActionPerformed(ActionEvent actionEvent)
    {
        int[] selectedRows = this.table.getSelectedRows();

        if (selectedRows.length == 0)
        {
            JOptionPane.showMessageDialog(null, "No books have been selected");
        }
        else
        {
            changeStatus(1, selectedRows);
            updateTable();
            checkTransactionStatus();
        }
    }

    /**
     * Used by the mark as not returned button, changes the status of the selected books to not returned.
     */
    private void btnNotReturnedActionPerformed(ActionEvent actionEvent)
    {
        int[] selectedRows = this.table.getSelectedRows();

        if (selectedRows.length == 0)
        {
            JOptionPane.showMessageDialog(null, "No books have been selected");
        }
        else
        {
            changeStatus(0, selectedRows);
            updateTable();
            checkTransactionStatus();
        }
    }

    /**
     * Initializes the table and all its functions then loads the initial data onto it.
     */
    private void initTable()
    {
        //Generate initial data
        this.resultSet = DatabaseHelper.createResultSet(this.connection, this.queryString);

        //Create table and its components
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
     * Creates a table model with the data from the result set then applies it to the table. The model also fixes column classes
     * in order for auto sorting to work. Also, we make cells non-editable. This method is also used to regenerate the table data
     * when performing an update.
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
        column.setPreferredWidth(60);
        column.setMinWidth(60);
        column.setMaxWidth(60);
        //All the rest will have the same size bounds
        for (int i = 1; i < columnCount - 1; i++)
        {
            column = columnModel.getColumn(i);
            column.setPreferredWidth(150);
            column.setMinWidth(100);
        }
        //Last column will also be smaller bcs it's just the status
        column = columnModel.getColumn(columnCount - 1);
        column.setPreferredWidth(100);
        column.setMinWidth(100);
        column.setMaxWidth(150);
    }

    /**
     * Updates the date in the table, used by the search function and in case the database changes.
     */
    public void updateTable()
    {
        //Generate a new result set
        this.resultSet = DatabaseHelper.createResultSet(this.connection, this.queryString);
        //Regenerate the table
        generateTableModel();
        adjustColumnModel();
    }

    /**
     * Used by the selection listener in the table, deactivates/activates buttons based on selection.
     */
    private void updateButtons()
    {
        this.btnReturned.setEnabled(this.table.getSelectedRows().length != 0);
        this.btnNotReturned.setEnabled(this.table.getSelectedRows().length != 0);
    }

    /**
     * Changes the status of books in the database based on the selected rows from the table.
     * @param status the new status
     * @param selectedRows selected rows in the table from where to get the id of the books to change
     */
    private void changeStatus(int status, int[] selectedRows)
    {
        StringJoiner stringJoiner = new StringJoiner(",", "(", ")");

        for (int row : selectedRows)
        {
            stringJoiner.add(table.getValueAt(row, 0).toString());
        }

        String SQL = "UPDATE BOOKS SET `available`=" + status + " WHERE `book_id` IN " + stringJoiner;

        try
        {
            PreparedStatement preparedStatement = this.connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);

            if (preparedStatement.executeUpdate() <= 0)
            {
                JOptionPane.showMessageDialog(null, "Something went wrong, status was not changed");
            }

            System.out.println(preparedStatement);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Checks if all the books have been returned or not, and updates the status of the transaction based on that.
     */
    private void checkTransactionStatus()
    {
        boolean allReturned = true;

        for (Vector<Object> row : rows)
        {
            if (!row.get(4).equals("Returned"))
            {
                allReturned = false;
                break;
            }
        }

        if (this.returned != allReturned)
        {
            changeTransactionStatus(allReturned);
            parent.updateTable();

            if (allReturned)
            {
                JOptionPane.showMessageDialog(null, "All books returned, transaction status updated");
            }
            else
            {
                JOptionPane.showMessageDialog(null, "Some books marked as not returned, transaction status updated");
            }

            this.returned = allReturned;
        }
    }

    /**
     * Changes the status of the transaction for which this window was created using the transactionId atribute.
     * @param status the new status
     */
    private void changeTransactionStatus(boolean status)
    {
        String SQL = "UPDATE TRANSACTIONS SET `returned`=? WHERE `transaction_id`=?";

        try
        {
            PreparedStatement preparedStatement = this.connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setString(1, Integer.toString((status) ? 1 : 0));
            preparedStatement.setString(2, Integer.toString(this.transactionID));

            if (preparedStatement.executeUpdate() <= 0)
            {
                JOptionPane.showMessageDialog(null, "Something went wrong, transaction status was not changed");
            }

            System.out.println(preparedStatement);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
