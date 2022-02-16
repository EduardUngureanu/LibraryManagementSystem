package com.libmgrsys;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;

/**
 * Abstract class for a book form which is used in the creation and editing of books in the inventory.
 * @see BookAdd
 * @see BookEdit
 */
abstract class BookForm extends JDialog
{
    //Database connection
    Connection connection;

    //The parent inventory window
    Inventory parent;

    JPanel mainPanel;

    //Form components
    JLabel lTitle;
    JTextField fTitle;
    JLabel lAuthor;
    JTextField fAuthor;
    JLabel lGenre;
    JTextField fGenre;

    //Action buttons
    JButton btnConfirm;
    JButton btnCancel;

    /**
     * Constructor, the connection must be specifically created with the DatabaseHelper.connect() method, the parent is the
     * Inventory that created the object.
     * @param connection MySQL Connection
     * @see DatabaseHelper
     * @see DatabaseHelper#connect()
     * @see Inventory
     */
    public BookForm(Connection connection, Inventory parent)
    {
        this.connection = connection;

        this.parent = parent;

        this.mainPanel = new JPanel();

        this.lTitle = new JLabel("Title:");
        this.fTitle = new JTextField();
        this.lAuthor = new JLabel("Author:");
        this.fAuthor = new JTextField();
        this.lGenre = new JLabel("Genre:");
        this.fGenre = new JTextField();

        this.btnConfirm = new JButton("Confirm");
        this.btnConfirm.addActionListener(this::btnConfirmActionPerformed);
        this.btnCancel = new JButton("Cancel");
        this.btnCancel.addActionListener(this::btnCancelActionPerformed);
    }

    /**
     * Button action to close the window.
     */
    protected void btnCancelActionPerformed(ActionEvent evt)
    {
        this.dispose();
    }

    /**
     * Button action to confirm the form values, used differently by the derived classes.
     * @see BookAdd#btnConfirmActionPerformed(ActionEvent)
     * @see BookEdit#btnConfirmActionPerformed(ActionEvent) 
     */
    protected abstract void btnConfirmActionPerformed(ActionEvent evt);
}
