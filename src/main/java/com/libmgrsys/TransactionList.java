package com.libmgrsys;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Vector;

public class TransactionList extends JPanel
{
    //Database connection
    Connection connection;

    //Search interface-----------------------------------------------------------------------------------------------------------
    JButton btnSearch;
    //Search fields
    JLabel lSearchID;
    JTextField fSearchID;
    JLabel lSearchClientID;
    JTextField fSearchClientID;
    JLabel lSearchName;
    JTextField fSearchName;
    JLabel lSearchLend;
    DatePicker fSearchLendStart;
    DatePicker fSearchLendEnd;
    //Status search options
    JLabel lSearchStatus;
    ButtonGroup StatusGroup;
    JRadioButton rbStatus1;
    JRadioButton rbStatus2;
    JRadioButton rbStatus3;
    JRadioButton rbStatus4;

    //Table Components-----------------------------------------------------------------------------------------------------------
    JScrollPane scrollPane;
    JTable table;
    Vector<String> columnNames;
    Vector<Vector<Object>> rows;
    //Query components
    ResultSet resultSet;
    String queryString = "SELECT * FROM TRANSACTION_LIST";

    //The button to open the book view
    JButton btnBookView;

    /**
     * Constructor, the connection must be specifically created with the DatabaseHelper.connect() method.
     * @param connection MySQL Connection
     * @see DatabaseHelper
     * @see DatabaseHelper#connect()
     */
    public TransactionList(Connection connection)
    {
        this.connection = connection;

        this.btnBookView = new JButton("View Books");
        this.btnBookView.addActionListener(this::btnBookViewActionPerformed);

        initSearchInterface();
        initTable();

        initLayout();

        updateButtons();
    }

    private void initSearchInterface()
    {
        //Search button
        this.btnSearch = new JButton("Search");
        this.btnSearch.addActionListener(this::btnSearchActionPerformed);
        //Search fields
        this.lSearchID = new JLabel("ID:");
        this.fSearchID = new JTextField();
        this.lSearchClientID = new JLabel("Client ID:");
        this.fSearchClientID = new JTextField();
        this.lSearchName = new JLabel("Client Name:");
        this.fSearchName = new JTextField();
        this.lSearchLend = new JLabel("Lent between:");
        //Status search options
        this.lSearchStatus = new JLabel("Status:");
        this.rbStatus1 = new JRadioButton("All");
        this.rbStatus1.setSelected(true);
        this.rbStatus2 = new JRadioButton("Pending");
        this.rbStatus3 = new JRadioButton("Returned");
        this.rbStatus4 = new JRadioButton("Overdue");
        this.StatusGroup = new ButtonGroup();
        this.StatusGroup.add(this.rbStatus1);
        this.StatusGroup.add(this.rbStatus2);
        this.StatusGroup.add(this.rbStatus3);
        this.StatusGroup.add(this.rbStatus4);
        //Date pickers
        this.fSearchLendStart = new DatePicker();
        this.fSearchLendEnd = new DatePicker();
        DatePickerSettings settings = new DatePickerSettings();
        settings.setFormatForDatesCommonEra(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));
        this.fSearchLendStart.setSettings(settings);
        this.fSearchLendEnd.setSettings(settings.copySettings());
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
                if (column == 0 || column == 1 || column == 5)
                {
                    return Integer.class;
                }
                else if (column == 3 || column == 4)
                {
                    return Date.class;
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
        column = columnModel.getColumn(1);
        column.setPreferredWidth(60);
        column.setMinWidth(60);
        column.setMaxWidth(60);
        //All the rest will have the same size bounds
        for (int i = 2; i < columnCount - 2; i++)
        {
            column = columnModel.getColumn(i);
            column.setPreferredWidth(150);
            column.setMinWidth(100);
        }
        //Last column will also be smaller bcs it's just the status
        column = columnModel.getColumn(columnCount - 2);
        column.setPreferredWidth(100);
        column.setMinWidth(100);
        column.setMaxWidth(150);
        column = columnModel.getColumn(columnCount - 1);
        column.setPreferredWidth(150);
        column.setMinWidth(150);
        column.setMaxWidth(200);
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
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(this.lSearchID, 16, 16, 16)
                                .addComponent(this.fSearchID, 50, 50, 50)
                                .addGap(5)
                                .addComponent(this.lSearchClientID, 53, 53, 53)
                                .addComponent(this.fSearchClientID, 50, 50, 50)
                                .addGap(5)
                                .addComponent(this.lSearchName, 74, 74, 74)
                                .addComponent(this.fSearchName, 50, 150, -1)
                                .addGap(5)
                                .addComponent(this.lSearchLend, 83, 83, 83)
                                .addComponent(this.fSearchLendStart, 50, 150, -1)
                                .addGap(10)
                                .addComponent(this.fSearchLendEnd, 50, 150, -1)
                                .addGap(5)
                                .addComponent(this.lSearchStatus, 41, 41, 41)
                                .addComponent(this.rbStatus1)
                                .addComponent(this.rbStatus2)
                                .addComponent(this.rbStatus3)
                                .addComponent(this.rbStatus4)
                                .addGap(10)
                                .addComponent(this.btnSearch, 80, 80, 80))
                        .addComponent(this.scrollPane)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(this.btnBookView)))
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(this.lSearchID, 25, 25, 25)
                        .addComponent(this.fSearchID, 25, 25, 25)
                        .addComponent(this.lSearchClientID, 25, 25, 25)
                        .addComponent(this.fSearchClientID, 25, 25, 25)
                        .addComponent(this.lSearchName, 25, 25, 25)
                        .addComponent(this.fSearchName, 25, 25, 25)
                        .addComponent(this.lSearchLend, 25, 25, 25)
                        .addComponent(this.fSearchLendStart, 25, 25, 25)
                        .addComponent(this.fSearchLendEnd, 25, 25, 25)
                        .addComponent(this.lSearchStatus, 25, 25, 25)
                        .addComponent(this.rbStatus1, 25, 25, 25)
                        .addComponent(this.rbStatus2, 25, 25, 25)
                        .addComponent(this.rbStatus3, 25, 25, 25)
                        .addComponent(this.rbStatus4, 25, 25, 25)
                        .addComponent(this.btnSearch))
                .addGap(10)
                .addComponent(this.scrollPane)
                .addGap(10)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(this.btnBookView))
        );
    }

    /**
     * Used by the search button, builds a query string based on the search interface then performs a search and updates the data
     * in the table.
     */
    private void btnSearchActionPerformed(ActionEvent evt)
    {
        String searchString = " WHERE";

        //Value to check if string has been started or not in order to add AND/OR before the next search field and to check if
        //any fields have been filled
        boolean search = false;

        if (!this.fSearchID.getText().isEmpty())
        {
            search = true;

            searchString += " `" + this.columnNames.get(0) + "` LIKE \"" + this.fSearchID.getText() + "\"";
        }

        if (!this.fSearchClientID.getText().isEmpty())
        {
            if (search)
            {
                searchString += " AND";
            }
            else
            {
                search = true;
            }

            searchString += " `" + this.columnNames.get(1) + "` LIKE \"" + this.fSearchClientID.getText() + "\"";
        }

        if (!this.fSearchName.getText().isEmpty())
        {
            if (search)
            {
                searchString += " AND";
            }
            else
            {
                search = true;
            }

            StringBuilder nameSearch = new StringBuilder();
            for (String str : this.fSearchName.getText().split(" "))
            {
                nameSearch.append("(?=.*").append(str).append(")");
            }
            searchString += " `" + this.columnNames.get(2) + "` RLIKE \"" + nameSearch + "\"";
        }

        //Status options
        if (this.rbStatus2.isSelected())
        {
            if (search)
            {
                searchString +=  " AND";
            }
            else
            {
                search = true;
            }

            searchString += " `" + this.columnNames.get(6) + "` LIKE 'Pending'";
        }
        else if (this.rbStatus3.isSelected())
        {
            if (search)
            {
                searchString +=  " AND";
            }
            else
            {
                search = true;
            }

            searchString += " `" + columnNames.get(6) + "` LIKE 'Returned'";
        }
        else if (this.rbStatus4.isSelected())
        {
            if (search)
            {
                searchString +=  " AND";
            }
            else
            {
                search = true;
            }

            searchString += " `" + columnNames.get(6) + "` LIKE 'Overdue'";
        }

        if (search)
        {
            this.queryString = "SELECT * FROM TRANSACTION_LIST" + searchString;
        }
        else
        {
            this.queryString = "SELECT * FROM TRANSACTION_LIST";
        }

        System.out.println("Executing: " + this.queryString);

        updateTable();
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
     * Used by the book view button. Creates a TransactionBookView window for the selected transaction.
     * @see TransactionBookView
     */
    private void btnBookViewActionPerformed(ActionEvent evt)
    {
        int[] selectedRows = this.table.getSelectedRows();

        if (selectedRows.length == 0)
        {
            JOptionPane.showMessageDialog(null, "No books have been selected");
        }
        else
        {
            for (int row : selectedRows)
            {
                new TransactionBookView(connection, this, row);
            }
        }
    }

    /**
     * Method that returns a vector containing the values of the transaction at the specified row.
     * @param row table row
     * @return Vector with book values
     */
    public Vector<Object> getTransactionAt(int row)
    {
        Vector<Object> transaction = new Vector<>();

        int columnCount = this.table.getColumnCount();

        for (int i = 0; i < columnCount; i++)
        {
            transaction.add(this.table.getValueAt(row, i));
        }

        return transaction;
    }

    /**
     * Updates the state of buttons, if they are enabled or not. Used by the ListSelectionListener in the table to automatically
     * update button states based on selections.
     */
    private void updateButtons()
    {
        this.btnBookView.setEnabled(this.table.getSelectedRows().length != 0);
    }
}
