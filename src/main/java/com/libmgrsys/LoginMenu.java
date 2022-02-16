package com.libmgrsys;

import java.awt.event.*;
import java.sql.*;
import java.util.Arrays;
import javax.swing.*;

/**
 * Class representing the login menu.
 */
public class LoginMenu extends JFrame
{
	Connection connection;
	
	JPanel panel1;
	JLabel luser;
	JTextField fuser;
	JLabel lpass;
	JPasswordField fpass;
	JButton btnLogin;

	/**
	 * Basic constructor.
	 */
	public LoginMenu()
	{
		connection = DatabaseHelper.connect();
		initComponents();
	}

	/**
	 * Initializes the frame components.
	 */
	private void initComponents()
	{
		panel1 = new JPanel();
		
		luser = new JLabel("Username");
		fuser = new JTextField();
		
		lpass = new JLabel("Password");
		fpass = new JPasswordField();
		
		btnLogin = new JButton("Login");
		btnLogin.addActionListener(this::btnLoginActionPerformed);
		
		panel1.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		GroupLayout layout = new GroupLayout(panel1);
		panel1.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addGroup(layout.createSequentialGroup()
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
										.addComponent(luser, 70, 70, 70)
										.addComponent(lpass, 70, 70, 70))
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
										.addComponent(fuser, 200, 200, 200)
										.addComponent(fpass, 200, 200, 200)))
						.addComponent(btnLogin, 100, 100, 100))
		);
		
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(luser, 25, 25, 25)
						.addComponent(fuser, 25, 25, 25))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(lpass, 25, 25, 25)
						.addComponent(fpass, 25, 25, 25))
				.addComponent(btnLogin, 25, 25, 25)
		);
		
		add(panel1);
		
		setTitle("Login");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setResizable(false);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	/**
	 * Performs a query on the database to check for users and if one exists it opens its respective menu.
	 */
	private void btnLoginActionPerformed(ActionEvent evt)
	{
		String username = fuser.getText();
		String password = String.valueOf(fpass.getPassword());
         
        if (username.equals(""))
        {
            JOptionPane.showMessageDialog(null, "Please enter username");
        } 
        else if (password.equals(""))
        {
            JOptionPane.showMessageDialog(null, "Please enter password");
        }
        else
        {
        	System.out.println("Querying users table...");
            try
            {
            	Statement stmt = connection.createStatement();
            	stmt.executeUpdate("USE LIBRARY_DB");
            	String st = ("SELECT * FROM USERS WHERE USERNAME='"+username+"' AND PASSWORD='"+password+"'");
            	ResultSet rs = stmt.executeQuery(st);
            	
            	if (!rs.next())
            	{
            		System.out.print("No user found");
            		JOptionPane.showMessageDialog(null, "Wrong Username/Password!");
            	}
            	else
            	{
            		dispose();
            		
                	System.out.println("User found, opening master menu");
            		
					new MasterMenu(connection);
            	}
            }
            catch (Exception ex)
            {
                 ex.printStackTrace();
            }
        }
	}
}
