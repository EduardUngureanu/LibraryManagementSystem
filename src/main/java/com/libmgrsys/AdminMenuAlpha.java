package com.libmgrsys;

import java.awt.event.*;
import javax.swing.*;

/**
 * Class representing the admin menu.
 */
@Deprecated
public class AdminMenuAlpha extends JFrame
{
	JTabbedPane tabbedPane;
	JPanel panel1;
	JButton btnInventory;

	/**
	 * Basic constructor.
	 */
	public AdminMenuAlpha()
	{
		initComponents();
	}

	/**
	 * Initializes the components of the frame.
	 */
	private void initComponents()
	{
		tabbedPane = new JTabbedPane();
		panel1 = new JPanel();
		
		btnInventory = new JButton("Inventory");
		btnInventory.addActionListener(this::btnInventoryActionPerformed);
		
		panel1.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		GroupLayout layout = new GroupLayout(panel1);
		panel1.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addComponent(btnInventory, 150, 150, 150)
		);
		
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(btnInventory, 25, 25, 25)
		);
		
		add(panel1);
		
		setTitle("Admin Menu");
		setLocationRelativeTo(null);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setResizable(false);
		pack();
	}

	/**
	 * Opens an inventory window.
	 */
	protected void btnInventoryActionPerformed(ActionEvent evt)
	{
		JFrame inventory = new InventoryAlpha();
		inventory.setVisible(true);
	}
}
