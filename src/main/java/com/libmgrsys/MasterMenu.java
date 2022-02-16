package com.libmgrsys;

import java.sql.Connection;
import javax.swing.*;
import javax.swing.event.ChangeEvent;

/**
 * Class representing the whole menu, a frame containing a tabbed pane with all functions inside their own tabs.
 */
public class MasterMenu extends JFrame
{
    //Tabs
    int selected;
    JTabbedPane tabbedPane;
    Inventory inventory;
    TransactionCreator transactionCreator;
    ClientList clientList;
    TransactionList transactionList;

    //Database connection
    Connection connection;

    /**
     * Constructor, the connection must be specifically created with the DatabaseHelper.connect() method.
     * @param connection MySQL Connection
     * @see DatabaseHelper
     * @see DatabaseHelper#connect()
     */
    public MasterMenu(Connection connection)
    {
        this.connection = connection;

        initTabbedPane();

        this.setTitle("Admin Menu");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    /**
     * Initializes the tabbed pane components, aka all the submenus.
     */
    private void initTabbedPane()
    {
        this.inventory = new Inventory(this.connection);
        this.clientList = new ClientList(this.connection);
        this.transactionList = new TransactionList(this.connection);
        this.transactionCreator = new TransactionCreator(this.connection);
        this.transactionCreator.setBookIDs(this.inventory.getLendIDs());
        this.transactionCreator.setClientID(this.clientList.getSelectedClientID());


        this.tabbedPane = new JTabbedPane();
        this.tabbedPane.addTab("Transaction", this.transactionCreator);
        this.tabbedPane.addTab("Inventory", this.inventory);
        this.tabbedPane.addTab("Clients", this.clientList);
        this.tabbedPane.addTab("Transactions", this.transactionList);
        this.tabbedPane.addChangeListener(this::stateChanged);
        this.selected = 0;

        this.add(this.tabbedPane);
    }

    /**
     * Updates the data in all tabs, used by the ChangeListener in the tabbed pane so that data is updated every time you change
     * tabs.
     */
    private void stateChanged(ChangeEvent evt)
    {
        this.inventory.updateTable();
        this.clientList.updateTable();
        this.transactionList.updateTable();
        this.transactionCreator.setClientID(this.clientList.getSelectedClientID());
        this.transactionCreator.updateQueryString();
        this.transactionCreator.updateTransactionList();
        this.transactionCreator.updateFormData();
        this.transactionCreator.updateWarning();
    }
}
