package com.libmgrsys;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Vector;

/**
 * Class representing the client tab in the admin menu, consists of a table with all the clients, sort and filter methods as well
 * as add and edit functions. Also contains the method of selecting a client for a transaction.
 */
public class ClientList extends JPanel
{
    //Database connection
    Connection connection;

    //Search interface-----------------------------------------------------------------------------------------------------------
    JButton btnSearch;
    //Search fields
    JLabel lSearchID;
    JTextField fSearchID;
    JLabel lSearchFN;
    JTextField fSearchFN;
    JLabel lSearchLN;
    JTextField fSearchLN;
    JLabel lSearchBD;
    DatePicker fSearchBD;

    //Table Components-----------------------------------------------------------------------------------------------------------
    JScrollPane scrollPane;
    JTable table;
    Vector<String> columnNames;
    Vector<Vector<Object>> rows;
    //Query components
    ResultSet resultSet;
    String queryString = "SELECT * FROM CLIENT_LIST";

    //Btn to select a client for the transaction creation
    JButton btnTransactionSelect;
    //The IDs of the selected client
    Integer selectedClientID = 0;

    //Add and Edit buttons
    JButton btnAddClient;
    JButton btnEditClient;

    boolean admin = true;

    /**
     * Constructor, the connection must be specifically created with the DatabaseHelper.connect() method.
     * @param connection MySQL Connection
     * @see DatabaseHelper
     * @see DatabaseHelper#connect()
     */
    public ClientList(Connection connection)
    {
        this.connection = connection;

        this.btnTransactionSelect = new JButton("Select for transaction");
        this.btnTransactionSelect.addActionListener(this::btnTransactionSelectActionPerformed);

        this.btnAddClient = new JButton("Add");
        this.btnAddClient.addActionListener(this::btnAddClientActionPerformed);

        this.btnEditClient = new JButton("Edit");
        this.btnEditClient.addActionListener(this::btnEditClientActionPerformed);

        initTable();
        initSearchInterface();

        initLayout();

        updateButtons();
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
                if (column == 0 || column == 4 || column == 5)
                {
                    return Integer.class;
                }
                else if (column == 3)
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
        //All the rest will have the same size bounds
        for (int i = 1; i < columnCount - 2; i++)
        {
            column = columnModel.getColumn(i);
            column.setPreferredWidth(150);
            column.setMinWidth(100);
        }
        column = columnModel.getColumn(columnCount - 3);
        column.setPreferredWidth(100);
        column.setMinWidth(100);
        column.setMaxWidth(150);
        column = columnModel.getColumn(columnCount - 2);
        column.setPreferredWidth(100);
        column.setMinWidth(100);
        column.setMaxWidth(150);
        column = columnModel.getColumn(columnCount - 1);
        column.setPreferredWidth(100);
        column.setMinWidth(100);
        column.setMaxWidth(150);
    }

    /**
     * Initializes the search interface at the top of the panel.
     */
    private void initSearchInterface()
    {
        //Search button
        this.btnSearch = new JButton("Search");
        this.btnSearch.addActionListener(this::btnSearchActionPerformed);
        //Search fields
        this.lSearchID = new JLabel("ID:");
        this.fSearchID = new JTextField();
        this.lSearchFN = new JLabel("First Name:");
        this.fSearchFN = new JTextField();
        this.lSearchLN = new JLabel("Last Name:");
        this.fSearchLN = new JTextField();
        this.lSearchBD = new JLabel("Birth Date:");
        //Date picker
        this.fSearchBD = new DatePicker();
        DatePickerSettings settings = new DatePickerSettings();
        settings.setFormatForDatesCommonEra(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));
        this.fSearchBD.setSettings(settings);
    }

    /**
     * Sets up the layout of the client window.
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
                                .addComponent(this.lSearchFN, 66, 66, 66)
                                .addComponent(this.fSearchFN, 50, 150, -1)
                                .addGap(5)
                                .addComponent(this.lSearchLN, 65, 65, 65)
                                .addComponent(this.fSearchLN, 50, 150, -1)
                                .addGap(5)
                                .addComponent(this.lSearchBD, 63, 63, 63)
                                .addComponent(this.fSearchBD, 50, 150, -1)
                                .addGap(10)
                                .addComponent(this.btnSearch, 80, 80, 80))
                        .addComponent(this.scrollPane)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(this.btnTransactionSelect)
                                .addGap(5)
                                .addComponent(this.btnAddClient)
                                .addGap(5)
                                .addComponent(this.btnEditClient)))
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(this.lSearchID, 25, 25, 25)
                        .addComponent(this.fSearchID, 25, 25, 25)
                        .addComponent(this.lSearchFN, 25, 25, 25)
                        .addComponent(this.fSearchFN, 25, 25, 25)
                        .addComponent(this.lSearchLN, 25, 25, 25)
                        .addComponent(this.fSearchLN, 25, 25, 25)
                        .addComponent(this.lSearchBD, 25, 25, 25)
                        .addComponent(this.fSearchBD, 25, 25, 25)
                        .addComponent(this.btnSearch))
                .addGap(10)
                .addComponent(this.scrollPane)
                .addGap(10)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(this.btnTransactionSelect)
                        .addComponent(this.btnAddClient)
                        .addComponent(this.btnEditClient))
        );
    }

    /**
     * Used by the search button, builds a query string based on the search interface then performs a search and updates the data
     * in the table.
     */
    private void btnSearchActionPerformed(ActionEvent evt)
    {
        String searchString = " WHERE";

        //Value to check if string has been started or not in order to add AND before the next search field and to check if
        //any fields have been filled
        boolean search = false;

        if (!this.fSearchID.getText().isEmpty())
        {
            search = true;

            searchString += " `" + this.columnNames.get(0) + "` LIKE \"" + this.fSearchID.getText() + "\"";
        }

        if (!this.fSearchFN.getText().isEmpty())
        {
            if (search)
            {
                searchString +=  " AND";
            }
            else
            {
                search = true;
            }

            searchString += " `" + this.columnNames.get(1) + "` LIKE \"" + this.fSearchFN.getText() + "\"";
        }

        if (!this.fSearchLN.getText().isEmpty())
        {
            if (search)
            {
                searchString +=  " AND";
            }
            else
            {
                search = true;
            }

            searchString += " `" + this.columnNames.get(2) + "` LIKE \"" + this.fSearchLN.getText() + "\"";
        }

        if (!this.fSearchBD.getText().isEmpty())
        {
            if (search)
            {
                searchString +=  " AND";
            }
            else
            {
                search = true;
            }

            searchString += " `" + this.columnNames.get(3) + "` LIKE \"" + this.fSearchBD.getText() + "\"";
        }

        if (search)
        {
            this.queryString = "SELECT * FROM CLIENT_LIST" + searchString;
        }
        else
        {
            this.queryString = "SELECT * FROM CLIENT_LIST";
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
     * Used by the transaction select button, Selects a client for the transaction creation.
     */
    private void btnTransactionSelectActionPerformed(ActionEvent evt)
    {
        if (this.table.getSelectedRows().length == 0)
        {
            JOptionPane.showMessageDialog(null, "No client selected");
        }
        else if (this.table.getSelectedRows().length > 1)
        {
            JOptionPane.showMessageDialog(null, "Only select one client");
        }
        else
        {
            this.selectedClientID = (Integer) this.table.getValueAt(this.table.getSelectedRow(), 0);

            System.out.println("Selected client with ID=" + this.selectedClientID);

            JOptionPane.showMessageDialog(null, "Client selected successfully");
        }
    }

    public int getSelectedClientID()
    {
        return this.selectedClientID;
    }

    /**
     * Updates the state of buttons, if they are enabled or not. Used by the ListSelectionListener in the table to automatically
     * update button states based on selections.
     */
    private void updateButtons()
    {
        if (this.table.getSelectedRows().length != 1)
        {
            this.btnTransactionSelect.setEnabled(false);

            if (admin)
            {
                this.btnEditClient.setEnabled(false);
            }
        }
        else
        {
            this.btnTransactionSelect.setEnabled(true);

            if (admin)
            {
                this.btnEditClient.setEnabled(true);
            }
        }
    }

    /**
     * Used by the client add button. Creates a ClientEdit window for the selected client.
     * @see ClientAdd
     */
    private void btnAddClientActionPerformed(ActionEvent evt)
    {
        new ClientAdd(connection, this);
    }

    /**
     * Used by the client add button. Creates a ClientAdd window for the selected client.
     * @see ClientEdit
     */
    private void btnEditClientActionPerformed(ActionEvent evt)
    {
        int[] selectedRows = this.table.getSelectedRows();

        if (selectedRows.length == 0)
        {
            JOptionPane.showMessageDialog(null, "No clients have been selected");
        }
        else
        {
            for (int row : selectedRows)
            {
                new ClientEdit(connection, this, row);
            }
        }
    }

    /**
     * Gets the data from the selected client.
     * @return Vector containing the data of the client
     */
    public Vector<Object> getClientAt(int row)
    {
        Vector<Object> client = new Vector<>();

        int columnCount = this.table.getColumnCount();

        for (int i = 0; i < columnCount; i++)
        {
            client.add(this.table.getValueAt(row, i));
        }

        return client;
    }
}
