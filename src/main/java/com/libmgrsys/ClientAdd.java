package com.libmgrsys;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

/**
 * Class representing the client add window, used for adding new clients to the database. Derived from ClientForm.
 * @see ClientForm
 */
public class ClientAdd extends ClientForm
{
    /**
     * Constructor, the connection must be specifically created with the DatabaseHelper.connect() method, the parent is the
     * ClientList that created the object.
     * @param connection MySQL Connection
     * @param parent the parent ClientList
     * @see DatabaseHelper
     * @see DatabaseHelper#connect()
     * @see ClientList
     * @see ClientForm#ClientForm(Connection, ClientList)
     */
    public ClientAdd(Connection connection, ClientList parent)
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
                                        .addComponent(lFirstName, 70, 70, 70)
                                        .addComponent(lLastName, 70, 70, 70)
                                        .addComponent(lBirthDate, 70, 70, 70)
                                        .addComponent(lMaxBooks, 70, 70, 70))
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(fFirstName, 250, 250, 250)
                                        .addComponent(fLastName, 250, 250, 250)
                                        .addComponent(datePicker)
                                        .addComponent(fMaxBooks, 250, 250, 250)))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(btnConfirm, 100, 100, 100)
                                .addGap(40)
                                .addComponent(btnCancel, 100, 100, 100)))
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(this.lFirstName, 25, 25, 25)
                        .addComponent(this.fFirstName, 25, 25, 25))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(this.lLastName, 25, 25, 25)
                        .addComponent(this.fLastName, 25, 25, 25))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(this.lBirthDate, 25, 25, 25)
                        .addComponent(this.datePicker, 25, 25, 25))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(this.lMaxBooks, 25, 25, 25)
                        .addComponent(this.fMaxBooks, 25, 25, 25))
                .addGap(20)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(this.btnConfirm)
                        .addComponent(this.btnCancel))
        );

        this.add(this.mainPanel);

        this.setTitle("Add a client to the database");
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    /**
     * Overwrites the abstract method in the parent class to add a client to the database then updates the table in the 
     * ClientList. Checks weather the fields are filled and then calls the addClient() method.
     * @see ClientAdd#addClient(String, String, String, String) 
     * @see ClientList
     * @see ClientList#updateTable()
     */
    protected void btnConfirmActionPerformed(ActionEvent evt)
    {
        String firstName = this.fFirstName.getText();
        String lastName = this.fLastName.getText();
        String birthDate = this.datePicker.getText();
        String maxBooks = this.fMaxBooks.getText();

        if (firstName.equals(""))
        {
            JOptionPane.showMessageDialog(null, "Please enter a first name");
        }
        else if (lastName.equals(""))
        {
            JOptionPane.showMessageDialog(null, "Please enter a last name");
        }
        else if (birthDate.equals(""))
        {
            JOptionPane.showMessageDialog(null, "Please enter a birth date");
        }
        else if (maxBooks.equals(""))
        {
            JOptionPane.showMessageDialog(null, "Please enter a max amount of books");
        }
        else
        {
            addClient(firstName, lastName, birthDate, maxBooks);
            this.parent.updateTable();
        }
    }

    /**
     * Method to create a new client entry in the database.
     * @param firstName String first name
     * @param lastName String last name
     * @param birthDate String date formatted for (yyyy-mm-dd)
     * @param maxBooks String max number of books the client can have
     */
    private void addClient(String firstName, String lastName, String birthDate, String maxBooks)
    {
        String SQL = "INSERT INTO CLIENTS (`first_name`, `last_name`, `birth_date`, `max_books`) VALUES(?, ?, ?, ?)";

        try
        {
            PreparedStatement preparedStatement = this.connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, birthDate);
            preparedStatement.setString(4, maxBooks);

            System.out.println("Executing: " + SQL);

            if (preparedStatement.executeUpdate() > 0)
            {
                JOptionPane.showMessageDialog(null, "Client added successfully");
            }
            else
            {
                JOptionPane.showMessageDialog(null, "Something went wrong, client was not added");
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
