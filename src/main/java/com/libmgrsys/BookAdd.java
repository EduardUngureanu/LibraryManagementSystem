package com.libmgrsys;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

/**
 * Class representing the book add window, used for adding new books to the database. Derived from BookForm.
 * @see BookForm
 */
public class BookAdd extends BookForm
{
    /**
     * Constructor, the connection must be specifically created with the DatabaseHelper.connect() method, the parent is the
     * ClientList that created the object.
     * @param connection MySQL Connection
     * @param parent the parent Inventory
     * @see DatabaseHelper
     * @see DatabaseHelper#connect()
     * @see ClientList
     * @see BookForm#BookForm(Connection, Inventory)
     */
    public BookAdd(Connection connection, Inventory parent)
    {
        super(connection, parent);

        GroupLayout layout = new GroupLayout(this.mainPanel);
        this.mainPanel.setLayout(layout);

        layout.setAutoCreateContainerGaps(true);
        layout.setAutoCreateGaps(true);

        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(lTitle, 60, 60, 60)
                                        .addComponent(lAuthor, 60, 60, 60)
                                        .addComponent(lGenre, 60, 60, 60))
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(fTitle, 250, 250, 250)
                                        .addComponent(fAuthor, 250, 250, 250)
                                        .addComponent(fGenre, 250, 250, 250)))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(btnConfirm, 100, 100, 100)
                                .addGap(40)
                                .addComponent(btnCancel, 100, 100, 100)))
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
                .addGap(20)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(this.btnConfirm, 25, 25, 25)
                        .addComponent(this.btnCancel, 25, 25, 25))
        );

        this.add(this.mainPanel);

        this.setTitle("Add a book to the database");
        this.setResizable(false);
        this.setModalityType(ModalityType.APPLICATION_MODAL);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    /**
     * Overwrites the abstract method in the parent class to add a book to the database then updates the table in the
     * Inventory. Checks weather the fields are filled and then calls the addClient() method.
     * @see BookAdd#addBook(String, String, String)
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
            addBook(title, author, genre);
            this.parent.updateTable();
        }
    }

    /**
     * Method to create a new book entry in the database.
     */
    private void addBook(String title, String author, String genre)
    {
        String SQL = "INSERT INTO BOOKS (`title`, `author`, `genre`) VALUES(?, ?, ?)";

        try
        {
            PreparedStatement preparedStatement = this.connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setString(1, title);
            preparedStatement.setString(2, author);
            preparedStatement.setString(3, genre);

            System.out.println(preparedStatement);

            System.out.println("Executing: " + SQL);

            if (preparedStatement.executeUpdate() > 0)
            {
                JOptionPane.showMessageDialog(null, "Book added successfully");
            }
            else
            {
                JOptionPane.showMessageDialog(null, "Something went wrong, book was not added");
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
