package com.libmgrsys;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

/**
 * Abstract class used as a base for ClientAdd and ClientEdit.
 * @see ClientAdd
 * @see ClientEdit
 */
public abstract class ClientForm extends JDialog
{
    //Database connection
    Connection connection;

    //The parent client list window
    ClientList parent;

    JPanel mainPanel;

    //Form components
    JLabel lFirstName;
    JTextField fFirstName;
    JLabel lLastName;
    JTextField fLastName;
    JLabel lBirthDate;
    DatePicker datePicker;
    JLabel lMaxBooks;
    JTextField fMaxBooks;

    //Action buttons
    JButton btnConfirm;
    JButton btnCancel;

    /**
     * Constructor, the connection must be specifically created with the DatabaseHelper.connect() method, the parent is the
     * ClientList that created the object.
     * @param connection MySQL Connection
     * @see DatabaseHelper
     * @see DatabaseHelper#connect()
     * @see ClientList
     */
    public ClientForm(Connection connection, ClientList parent)
    {
        this.connection = connection;

        this.parent = parent;

        this.mainPanel = new JPanel();

        this.lFirstName = new JLabel("First Name:");
        this.fFirstName = new JTextField();
        this.lLastName = new JLabel("Last Name:");
        this.fLastName = new JTextField();
        this.lBirthDate = new JLabel("Birth Date:");
        this.datePicker = new DatePicker();
        this.lMaxBooks = new JLabel("Max Books:");
        this.fMaxBooks = new JTextField();

        DatePickerSettings settings = new DatePickerSettings();
        settings.setFormatForDatesCommonEra(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));
        this.datePicker.setSettings(settings);

        this.btnConfirm = new JButton("Confirm");
        this.btnConfirm.addActionListener(this::btnConfirmActionPerformed);
        this.btnCancel = new JButton("Cancel");
        this.btnCancel.addActionListener(this::btnCancelActionPerformed);

        this.setResizable(false);
        this.setModalityType(ModalityType.APPLICATION_MODAL);
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
     * @see ClientAdd#btnConfirmActionPerformed(ActionEvent)
     * @see ClientEdit#btnConfirmActionPerformed(ActionEvent)
     */
    protected abstract void btnConfirmActionPerformed(ActionEvent evt);
}
