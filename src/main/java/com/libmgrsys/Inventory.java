package com.libmgrsys;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.swing.*;
import javax.swing.table.*;

/**
 * Class representing the Inventory panel a table containing all the books in store with sorting and filtering methods and buttons
 * to edit books or add new ones. Also contains the button to choose books for a transaction
 */
public class Inventory extends JPanel
{
    //Database connection
    Connection connection;

    //Search interface---------------------------------------------------------------------------------------------------------------------------
    JButton btnSearch;
    //Search fields
    JLabel lSearchID;
    JTextField fSearchID;
    JButton btnSyntax1;
    JLabel lSearchTitle;
    JTextField fSearchTitle;
    JButton btnSyntax2;
    JLabel lSearchAuthor;
    JTextField fSearchAuthor;
    JButton btnSyntax3;
    JLabel lSearchGenre;
    JTextField fSearchGenre;
    //Status search options
    JLabel lSearchStatus;
    ButtonGroup StatusGroup;
    JRadioButton rbStatus1;
    JRadioButton rbStatus2;
    JRadioButton rbStatus3;

    //Table Components---------------------------------------------------------------------------------------------------------------------------
    JScrollPane scrollPane;
    JTable table;
    Vector<String> columnNames;
    Vector<Vector<Object>> rows;
    //Query components
    ResultSet resultSet;
    String queryString = "SELECT * FROM INVENTORY";

    //Btn to add selected books to the transaction creation
    JButton btnTransactionAdd;
    //The IDs of the added books
    Set<Integer> lendIDs = new HashSet<>();

    //Add and Edit buttons
    JButton btnAddBook;
    JButton btnEditBook;

    /**
     * Constructor, the connection must be specifically created with the DatabaseHelper.connect() method.
     * @param connection MySQL Connection
     * @see DatabaseHelper
     * @see DatabaseHelper#connect()
     */
    public Inventory(Connection connection)
    {
        this.connection = connection;

        this.btnTransactionAdd = new JButton("Add to transaction");
        this.btnTransactionAdd.addActionListener(this::btnTransactionAddActionPerformed);

        this.btnAddBook = new JButton("Add");
        this.btnAddBook.addActionListener(this::btnAddBookActionPerformed);

        this.btnEditBook = new JButton("Edit");
        this.btnEditBook.addActionListener(this::btnEditBookActionPerformed);

        initSearchInterface();
        initTable();

        initLayout();

        updateButtons();
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
        this.lSearchTitle = new JLabel("Title:");
        this.fSearchTitle = new JTextField();
        this.lSearchAuthor = new JLabel("Author:");
        this.fSearchAuthor = new JTextField();
        this.lSearchGenre = new JLabel("Genre:");
        this.fSearchGenre = new JTextField();
        //Status search options
        this.lSearchStatus = new JLabel("Status:");
        this.rbStatus1 = new JRadioButton("Both");
        this.rbStatus1.setSelected(true);
        this.rbStatus2 = new JRadioButton("Available");
        this.rbStatus3 = new JRadioButton("Not Available");
        this.StatusGroup = new ButtonGroup();
        this.StatusGroup.add(this.rbStatus1);
        this.StatusGroup.add(this.rbStatus2);
        this.StatusGroup.add(this.rbStatus3);
        //Syntax builder buttons
        Font syntaxFont = new Font("Arial", Font.PLAIN, 10);
        this.btnSyntax1 = new JButton("OR");
        this.btnSyntax1.setFont(syntaxFont);
        this.btnSyntax1.addActionListener(this::btnSyntaxActionPerformed);
        this.btnSyntax1.setActionCommand("1");
        this.btnSyntax2 = new JButton("OR");
        this.btnSyntax2.setFont(syntaxFont);
        this.btnSyntax2.addActionListener(this::btnSyntaxActionPerformed);
        this.btnSyntax2.setActionCommand("2");
        this.btnSyntax3 = new JButton("OR");
        this.btnSyntax3.setFont(syntaxFont);
        this.btnSyntax3.addActionListener(this::btnSyntaxActionPerformed);
        this.btnSyntax3.setActionCommand("3");
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
     * Creates a table model with the data from the result set then applies it to the table. The model also fixes column classes in order for
     * auto sorting to work. Also, we make cells non-editable. This method is also used to regenerate the table data when performing an update.
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
                                .addComponent(this.btnSyntax1, 57, 57, 57)
                                .addGap(5)
                                .addComponent(this.lSearchTitle, 30, 30, 30)
                                .addComponent(this.fSearchTitle, 50, 150, -1)
                                .addGap(5)
                                .addComponent(this.btnSyntax2, 57, 57, 57)
                                .addGap(5)
                                .addComponent(this.lSearchAuthor, 43, 43, 43)
                                .addComponent(this.fSearchAuthor, 50, 150, -1)
                                .addGap(5)
                                .addComponent(this.btnSyntax3, 57, 57, 57)
                                .addGap(5)
                                .addComponent(this.lSearchGenre, 40, 40, 40)
                                .addComponent(this.fSearchGenre, 50, 150, -1)
                                .addGap(5)
                                .addComponent(this.lSearchStatus, 41, 41, 41)
                                .addComponent(this.rbStatus1)
                                .addComponent(this.rbStatus2)
                                .addComponent(this.rbStatus3)
                                .addGap(10)
                                .addComponent(this.btnSearch, 80, 80, 80))
                        .addComponent(this.scrollPane)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(this.btnTransactionAdd)
                                .addGap(5)
                                .addComponent(this.btnAddBook)
                                .addGap(5)
                                .addComponent(this.btnEditBook)))
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(this.lSearchID, 25, 25, 25)
                        .addComponent(this.fSearchID, 25, 25, 25)
                        .addComponent(this.btnSyntax1, 15, 15, 15)
                        .addComponent(this.lSearchTitle, 25, 25, 25)
                        .addComponent(this.fSearchTitle, 25, 25, 25)
                        .addComponent(this.btnSyntax2, 15, 15, 15)
                        .addComponent(this.lSearchAuthor, 25, 25, 25)
                        .addComponent(this.fSearchAuthor, 25, 25, 25)
                        .addComponent(this.btnSyntax3, 15, 15, 15)
                        .addComponent(this.lSearchGenre, 25, 25, 25)
                        .addComponent(this.fSearchGenre, 25, 25, 25)
                        .addComponent(this.lSearchStatus, 25, 25, 25)
                        .addComponent(this.rbStatus1, 25, 25, 25)
                        .addComponent(this.rbStatus2, 25, 25, 25)
                        .addComponent(this.rbStatus3, 25, 25, 25)
                        .addComponent(this.btnSearch))
                .addGap(10)
                .addComponent(this.scrollPane)
                .addGap(10)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(this.btnTransactionAdd)
                        .addComponent(this.btnAddBook)
                        .addComponent(this.btnEditBook))
        );

        this.btnTransactionAdd.setAlignmentX(Component.RIGHT_ALIGNMENT);
    }

    /**
     * Used by the search button, builds a query string based on the search interface then performs a search and updates the data in the table.
     */
    private void btnSearchActionPerformed(ActionEvent evt)
    {
        String searchString = " WHERE";

        //Value to check if string has been started or not in order to add AND/OR before the next search field and to check if any fields have
        // been filled
        boolean search = false;

        if (!this.fSearchID.getText().isEmpty())
        {
            search = true;

            searchString += " `" + this.columnNames.get(0) + "` LIKE \"" + this.fSearchID.getText() + "\"";
        }

        if (!this.fSearchTitle.getText().isEmpty())
        {
            if (search)
            {
                searchString += " " + this.btnSyntax1.getText();
            }
            else
            {
                search = true;
            }

            searchString += " `" + this.columnNames.get(1) + "` LIKE \"" + this.fSearchTitle.getText() + "\"";
        }

        if (!this.fSearchAuthor.getText().isEmpty())
        {
            if (search)
            {
                searchString += " " + this.btnSyntax2.getText();
            }
            else
            {
                search = true;
            }

            searchString += " `" + this.columnNames.get(2) + "` LIKE \"" + this.fSearchAuthor.getText() + "\"";
        }

        if (!this.fSearchGenre.getText().isEmpty())
        {
            if (search)
            {
                searchString +=  " " + this.btnSyntax3.getText();
            }
            else
            {
                search = true;
            }

            searchString += " `" + this.columnNames.get(3) + "` LIKE \"" + this.fSearchGenre.getText() + "\"";
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

            searchString += " `" + this.columnNames.get(4) + "` LIKE 'Available'";
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

            searchString += " `" + columnNames.get(4) + "` LIKE 'Not Available'";
        }

        if (search)
        {
            this.queryString = "SELECT * FROM INVENTORY" + searchString;
        }
        else
        {
            this.queryString = "SELECT * FROM INVENTORY";
        }

        System.out.println("Executing: " + this.queryString);

        updateTable();
    }

    /**
     * Used by the syntax buttons, the method switches between AND/OR on the button whose respective action command was used.
     * @param evt ActionEvent with the ActionCommand being a value between {"1", "2", "3"} each specific to their respective button
     */
    private void btnSyntaxActionPerformed(@NotNull ActionEvent evt)
    {
        switch (evt.getActionCommand())
        {
            case "1" -> {
                if (this.btnSyntax1.getText().equals("OR"))
                {
                    this.btnSyntax1.setText("AND");
                }
                else
                {
                    this.btnSyntax1.setText("OR");
                }
            }
            case "2" -> {
                if (this.btnSyntax2.getText().equals("OR"))
                {
                    this.btnSyntax2.setText("AND");
                }
                else
                {
                    btnSyntax2.setText("OR");
                }
            }
            case "3" -> {
                if (this.btnSyntax3.getText().equals("OR"))
                {
                    this.btnSyntax3.setText("AND");
                }
                else
                {
                    this.btnSyntax3.setText("OR");
                }
            }
            default -> throw new IllegalArgumentException("Unexpected value: " + evt.getActionCommand());
        }
    }

    /**
     * Used by the transaction add button. Adds all the selected table entries to the transaction list. Since the transaction list is a set,
     * any duplicates will not be added.
     */
    private void btnTransactionAddActionPerformed(ActionEvent evt)
    {
        if (table.getSelectedRows().length == 0)
        {
            JOptionPane.showMessageDialog(null, "No books selected");
        }
        else
        {
            int[] selectedRows = table.getSelectedRows();
            boolean check = false;
            for (int i : selectedRows)
            {
                if (table.getValueAt(i, 4).equals("Available"))
                {
                    this.lendIDs.add((Integer) table.getValueAt(i, 0));
                }
                else
                {
                    check = true;
                }
            }
            if (check)
            {
                JOptionPane.showMessageDialog(null, "Some books were not added to the transaction list because they are not available");
            }
            else
            {
                JOptionPane.showMessageDialog(null, "Selected books have been added to the transaction list");
            }
        }
    }

    public Set<Integer> getLendIDs()
    {
        return this.lendIDs;
    }

    /**
     * Used by the book add button. Creates a BookAdd window.
     * @see BookAdd
     */
    private void btnAddBookActionPerformed(ActionEvent evt)
    {
        new BookAdd(connection, this);
    }

    /**
     * Used by the book edit button. Creates a BookEdit window for each book selected before clicking the button.
     * @see BookEdit
     */
    private void btnEditBookActionPerformed(ActionEvent evt)
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
                new BookEdit(connection, this, row);
            }
        }
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
     * Method that returns a vector containing the values of the book at the specified row.
     * @param row table row
     * @return Vector with book values
     */
    public Vector<Object> getBookAt(int row)
    {
        Vector<Object> book = new Vector<>();

        int columnCount = this.table.getColumnCount();

        for (int i = 0; i < columnCount; i++)
        {
            book.add(this.table.getValueAt(row, i));
        }

        return book;
    }

    /**
     * Updates the state of buttons, if they are enabled or not. Used by the ListSelectionListener in the table to automatically
     * update button states based on selections.
     */
    private void updateButtons()
    {
        this.btnTransactionAdd.setEnabled(this.table.getSelectedRows().length > 0);
        this.btnEditBook.setEnabled(this.table.getSelectedRows().length > 0);
    }
}