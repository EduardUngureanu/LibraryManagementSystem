package com.libmgrsys;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Vector;

/**
 * Class representing the book edit window, used for edit existing books. Derived from BookForm.
 * @see BookForm
 */
public class BookEdit extends BookForm
{
    JLabel lStatus;
    ButtonGroup StatusGroup;
    JRadioButton rbStatus1;
    JRadioButton rbStatus2;

    int ID;

    /**
     * Constructor, the connection must be specifically created with the DatabaseHelper.connect() method, the parent is the
     * ClientList that created the object.
     * @param connection MySQL Connection
     * @param parent the parent Inventory
     * @param row the row in the inventory window from which to get the book id
     * @see DatabaseHelper
     * @see DatabaseHelper#connect()
     * @see ClientList
     * @see ClientForm#ClientForm(Connection, ClientList)
     */
    public BookEdit(Connection connection, Inventory parent, int row)
    {
        super(connection, parent);

        this.lStatus = new JLabel("Status:");
        this.rbStatus1 = new JRadioButton("Available");
        this.rbStatus2 = new JRadioButton("Not Available");
        this.StatusGroup = new ButtonGroup();
        this.StatusGroup.add(this.rbStatus1);
        this.StatusGroup.add(this.rbStatus2);

        Vector<Object> book = (this.parent.getBookAt(row));
        this.fTitle.setText(book.get(1).toString());
        this.fAuthor.setText(book.get(2).toString());
        this.fGenre.setText(book.get(3).toString());
        if (book.get(4).equals("Available"))
        {
            this.rbStatus1.setSelected(true);
        }
        else
        {
            this.rbStatus2.setSelected(true);
        }

        this.ID = (Integer) book.get(0);

        GroupLayout layout = new GroupLayout(this.mainPanel);
        mainPanel.setLayout(layout);

        layout.setAutoCreateContainerGaps(true);
        layout.setAutoCreateGaps(true);

        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(this.lTitle, 60, 60, 60)
                                        .addComponent(this.lAuthor, 60, 60, 60)
                                        .addComponent(this.lGenre, 60, 60, 60)
                                        .addComponent(this.lStatus, 60, 60, 60))
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(this.fTitle, 250, 250, 250)
                                        .addComponent(this.fAuthor, 250, 250, 250)
                                        .addComponent(this.fGenre, 250, 250, 250)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(this.rbStatus1)
                                                .addComponent(this.rbStatus2))))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(this.btnConfirm, 100, 100, 100)
                                .addGap(40)
                                .addComponent(this.btnCancel, 100, 100, 100)))
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(this.lTitle, 25, 25, 25)
                        .addComponent(this.fTitle, 25, 25, 25))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(this.lAuthor, 25, 25, 25)
                        .addComponent(this.fAuthor, 25, 25, 25))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(this.lGenre, 25, 25, 25)
                        .addComponent(this.fGenre, 25, 25, 25))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(this.lStatus, 25, 25, 25)
                        .addComponent(this.rbStatus1)
                        .addComponent(this.rbStatus2))
                .addGap(20)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(this.btnConfirm, 25, 25, 25)
                        .addComponent(this.btnCancel, 25, 25, 25))
        );

        this.add(this.mainPanel);

        this.setTitle("Edit the selected book");
        this.setResizable(false);
        this.setModalityType(ModalityType.APPLICATION_MODAL);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    /**
     * Overwrites the abstract method in the parent class to edit a book from the database then updates the table in the
     * Inventory. Checks weather the fields are filled and then calls the editClient() method.
     * @see BookEdit#editBook(String, String, String, int)
     * @see Inventory
     * @see Inventory#updateTable()
     */
    protected void btnConfirmActionPerformed(ActionEvent evt)
    {
        String title = this.fTitle.getText();
        String author = this.fAuthor.getText();
        String genre = this.fGenre.getText();

        if (title.equals(""))
        {
            JOptionPane.showMessageDialog(null, "Please enter a title");
        }
        else if (author.equals(""))
        {
            JOptionPane.showMessageDialog(null, "Please enter an author");
        }
        else if (genre.equals(""))
        {
            JOptionPane.showMessageDialog(null, "Please enter a genre");
        }
        else
        {
            if (this.rbStatus1.isSelected())
            {
                editBook(title, author, genre, 1);
            }
            else
            {
                editBook(title, author, genre, 0);
            }
            this.parent.updateTable();
            this.dispose();
        }
    }

    /**
     * Method to edit an existing book entry in the database. Uses the private ID attribute created with the constructor using
     * the selected row from the Inventory.
     */
    private void editBook(String title, String author, String genre, int status)
    {
        String SQL = "UPDATE BOOKS SET `title`=?, `author`=?, `genre`=?, `available`=? WHERE (`book_id`=?)";

        try
        {
            PreparedStatement preparedStatement = this.connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setString(1, title);
            preparedStatement.setString(2, author);
            preparedStatement.setString(3, genre);
            preparedStatement.setString(4, Integer.toString(status));
            preparedStatement.setString(5, Integer.toString(this.ID));

            System.out.println("Executing: " + SQL);

            if (preparedStatement.executeUpdate() > 0)
            {
                JOptionPane.showMessageDialog(null, "Book edited successfully");
            }
            else
            {
                JOptionPane.showMessageDialog(null, "Something went wrong, book was not edited");
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
