package com.libmgrsys;

import java.awt.event.*;
import java.awt.*;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;
import java.util.Vector;

import javax.swing.*;
import javax.swing.table.*;

//import com.mysql.cj.x.protobuf.MysqlxCrud.Column;
//import com.mysql.cj.x.protobuf.MysqlxCrud.DataModel;
//
//import net.proteanit.sql.DbUtils;

@Deprecated
public class InventoryAlpha extends JFrame
{
	JPanel panel1;
	
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
	
	JLabel lSearchStatus;
	ButtonGroup StatusGroup;
	JRadioButton rbStatus1;
	JRadioButton rbStatus2;
	JRadioButton rbStatus3;
	
	JButton btnSearch;
	
	JScrollPane scrollPane;
	JTable table;

	JButton btnListAdd;
	JButton btnListView;
	Set<Integer> lendIDs = new HashSet<>();
	
	String defaultString = "SELECT * FROM INVENTORY";
	String queryString;
	Vector<String> columnNames;
	Vector<Vector<Object>> rows;
	TableColumnModel columnModel;
	TableModel tableModel;
	
	Connection con;
	ResultSet rs;
	
	public InventoryAlpha()
	{
		this.con = DatabaseHelper.connect();
		this.rs = DatabaseHelper.createResultSet(this.con, this.defaultString);

		this.columnNames = DatabaseHelper.getColumnNames(this.rs);
		this.rows = DatabaseHelper.getRows(this.rs);
		
		initComponents();
	}
	
	private void initComponents()
	{
		this.panel1 = new JPanel();
		
		//Search menu setup
		Font font = new Font("Arial", Font.PLAIN, 10);

		this.lSearchID = new JLabel("ID:");
		this.fSearchID = new JTextField();

		this.btnSyntax1 = new JButton("OR");
		this.btnSyntax1.setFont(font);
		this.btnSyntax1.addActionListener(this::btnSyntaxActionPerformed);
		this.btnSyntax1.setActionCommand("1");
		this.lSearchTitle = new JLabel("Title:");
		this.fSearchTitle = new JTextField();

		this.btnSyntax2 = new JButton("OR");
		this.btnSyntax2.setFont(font);
		this.btnSyntax2.addActionListener(this::btnSyntaxActionPerformed);
		this.btnSyntax2.setActionCommand("2");
		this.lSearchAuthor = new JLabel("Author:");
		this.fSearchAuthor = new JTextField();

		this.btnSyntax3 = new JButton("OR");
		this.btnSyntax3.setFont(font);
		this.btnSyntax3.addActionListener(this::btnSyntaxActionPerformed);
		this.btnSyntax3.setActionCommand("3");
		this.lSearchGenre = new JLabel("Genre:");
		this.fSearchGenre = new JTextField();

		this.lSearchStatus = new JLabel("Status:");
		this.rbStatus1 = new JRadioButton("Both");
		this.rbStatus1.setSelected(true);
		this.rbStatus2 = new JRadioButton("In Store");
		this.rbStatus3 = new JRadioButton("Lent");

		this.StatusGroup = new ButtonGroup();
		this.StatusGroup.add(this.rbStatus1);
		this.StatusGroup.add(this.rbStatus2);
		this.StatusGroup.add(this.rbStatus3);

		this.btnSearch = new JButton("Search");
		this.btnSearch.addActionListener(this::btnSearchActionPerformed);

		this.btnListAdd = new JButton("Add");
		this.btnListAdd.addActionListener(this::btnListAddActionPerformed);

		btnListView = new JButton("View List");
		//this.btnListAdd.addActionListener();
		
		//Creating table
		this.table = new JTable();
		
		initModel();
		this.table.setModel(this.tableModel);

		this.columnModel = this.table.getColumnModel();
		initColumnModel();

		this.table.getTableHeader().setReorderingAllowed(false);
		this.table.setAutoCreateRowSorter(true);
		this.table.setCellSelectionEnabled(true);

		this.scrollPane = new JScrollPane(table);
		
		//Setting up the layout
		GroupLayout layout = new GroupLayout(this.panel1);
		this.panel1.setLayout(layout);

		layout.setAutoCreateContainerGaps(true);
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup()
								.addComponent(this.lSearchID, 16, 16, 16)
								.addComponent(this.fSearchID, 40, 40, 40)
								.addGap(5)
								.addComponent(this.btnSyntax1, 57, 57, 57)
								.addGap(5)
								.addComponent(this.lSearchTitle, 30, 30, 30)
								.addComponent(this.fSearchTitle, 50, 150, 150)
								.addGap(5)
								.addComponent(this.btnSyntax2, 57, 57, 57)
								.addGap(5)
								.addComponent(this.lSearchAuthor, 43, 43, 43)
								.addComponent(this.fSearchAuthor, 50, 150, 150)
								.addGap(5)
								.addComponent(this.btnSyntax3, 57, 57, 57)
								.addGap(5)
								.addComponent(this.lSearchGenre, 40, 40, 40)
								.addComponent(this.fSearchGenre, 50, 150, 150)
								.addGap(5)
								.addComponent(this.lSearchStatus, 41, 41, 41)
								.addComponent(this.rbStatus1)
								.addComponent(this.rbStatus2)
								.addComponent(this.rbStatus3)
								.addGap(10)
								.addComponent(this.btnSearch, 80, 80, 80))
						.addComponent(this.scrollPane))
		);
		
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lSearchID, 20, 20, 20)
						.addComponent(this.fSearchID, 20, 20, 20)
						.addComponent(this.btnSyntax1, 15, 15, 15)
						.addComponent(this.lSearchTitle, 20, 20, 20)
						.addComponent(this.fSearchTitle, 20, 20, 20)
						.addComponent(this.btnSyntax2, 15, 15, 15)
						.addComponent(this.lSearchAuthor, 20, 20, 20)
						.addComponent(this.fSearchAuthor, 20, 20, 20)
						.addComponent(this.btnSyntax3, 15, 15, 15)
						.addComponent(this.lSearchGenre, 20, 20, 20)
						.addComponent(this.fSearchGenre, 20, 20, 20)
						.addComponent(this.lSearchStatus, 20, 20, 20)
						.addComponent(this.rbStatus1)
						.addComponent(this.rbStatus2)
						.addComponent(this.rbStatus3)
						.addComponent(this.btnSearch, 20, 20, 20))
				.addGap(10)
				.addComponent(this.scrollPane)
		);
		
		add(this.panel1);
		
		//Finalizing frame setup
		setTitle("Inventory");
		setLocationRelativeTo(null);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setResizable(true);
		pack();
	}
	
	private void initModel()
	{
		this.tableModel = new DefaultTableModel(this.rows ,this.columnNames)
        {
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
        };
	}
	
	private void initColumnModel()
	{
		int columnCount = this.table.getColumnCount();
		
		TableColumn column = this.columnModel.getColumn(0);
		column.setPreferredWidth(40);
		column.setMinWidth(40);
		column.setMaxWidth(40);
		
		for(int i = 1; i < columnCount; i++)
		{
			column = this.columnModel.getColumn(i);
			column.setPreferredWidth(200);
			column.setMinWidth(50);
		}
	}
	
	private void updateTable()
	{
		this.columnNames = DatabaseHelper.getColumnNames(rs);
		this.rows = DatabaseHelper.getRows(rs);
		
		initModel();
		this.table.setModel(this.tableModel);
		initColumnModel();
	}
	
	private void btnSearchActionPerformed(ActionEvent evt)
	{
		String tmpString = "";
		boolean search = false;
		
		if (!this.fSearchID.getText().isEmpty())
		{
			search = true;
			
			tmpString += " `" + this.columnNames.get(0) + "` LIKE '" + this.fSearchID.getText() + "'";
		}
		
		if (!this.fSearchTitle.getText().isEmpty())
		{
			if (search)
			{
				tmpString += " " + this.btnSyntax1.getText();
			}
			else
			{
				search = true;
			}
			
			tmpString += " `" + this.columnNames.get(1) + "` LIKE '" + this.fSearchTitle.getText() + "'";
		}
		
		if (!this.fSearchAuthor.getText().isEmpty())
		{
			if (search)
			{
				tmpString += " " + this.btnSyntax2.getText();
			}
			else
			{
				search = true;
			}
			
			tmpString += " `" + this.columnNames.get(2) + "` LIKE '" + this.fSearchAuthor.getText() + "'";
		}
		
		if (!this.fSearchGenre.getText().isEmpty())
		{
			if (search)
			{
				tmpString +=  " " + this.btnSyntax3.getText();
			}
			else
			{
				search = true;
			}
			
			tmpString += " `" + this.columnNames.get(3) + "` LIKE '" + this.fSearchGenre.getText() + "'";
		}
		
		if (this.rbStatus2.isSelected())
		{
			if (search)
			{
				tmpString +=  " AND";
			}
			else
			{
				search = true;
			}
			
			tmpString += " `" + this.columnNames.get(4) + "` LIKE 'In Store'";
		}
		else if (this.rbStatus3.isSelected())
		{
			if (search)
			{
				tmpString +=  " AND";
			}
			else
			{
				search = true;
			}
			
			tmpString += " `" + columnNames.get(4) + "` LIKE 'Lent'";
		}

		this.queryString = this.defaultString;
		
		if (search)
		{
			this.queryString = this.defaultString + " WHERE" + tmpString;
		}
		
		System.out.println("Exectuting query: " + this.queryString);

		this.rs = DatabaseHelper.createResultSet(this.con, this.queryString);
		
		updateTable();
	}
	
	private void btnSyntaxActionPerformed(ActionEvent evt)
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
	 * Action method for adding books from the inventory to the lending list.
	 */
	private void btnListAddActionPerformed(ActionEvent evt)
	{
		int[] selectedRows = table.getSelectedRows();
		for (int i : selectedRows)
		{
			this.lendIDs.add((Integer) table.getValueAt(i, 0));
		}
	}

	private void btnListViewActionPerformed(ActionEvent evt)
	{
		StringJoiner list = new StringJoiner(", ", "(", ")");

		for (int ID : lendIDs)
		{
			list.add(Integer.toString(ID));
		}

		String str = "SELECT * FROM INVENTORY WHERE ID IN (" + list.toString();
		ResultSet resultSet = DatabaseHelper.createResultSet(con, str.toString());

		JTable lendTable = new JTable();

		DefaultTableModel model = new DefaultTableModel(DatabaseHelper.getRows(resultSet), this.columnNames)
		{
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
		};

		lendTable.setModel(model);
		lendTable.setColumnModel(this.columnModel);
		lendTable.getTableHeader().setReorderingAllowed(false);
		lendTable.setAutoCreateRowSorter(true);

		JScrollPane tableContainer = new JScrollPane(lendTable);
		JPanel panel = new JPanel();
		BoxLayout layout = new BoxLayout(panel, BoxLayout.Y_AXIS);
		panel.setLayout(layout);

		panel.add(tableContainer);
	}
}
