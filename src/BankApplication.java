import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import net.miginfocom.swing.MigLayout;

public class BankApplication extends JFrame {
	

	private static final long serialVersionUID = 1L;
	ArrayList<BankAccount> accountList = new ArrayList<BankAccount>();
	static HashMap<Integer, BankAccount> table = new HashMap<Integer, BankAccount>();
	private final static int TABLE_SIZE = 29;
	JMenuBar menuBar;
	private static RandomAccessFile input;
	private static RandomAccessFile output;
	JMenu navigateMenu, recordsMenu, transactionsMenu, fileMenu, exitMenu;
	JMenuItem nextItem, prevItem, firstItem, lastItem, findByAccount, findBySurname, listAll, closeApp, createItem, modifyItem, deleteItem, setOverdraft, setInterest, open, save, saveAs, deposit, withdraw, calcInterest; 
	JButton firstItemButton, lastItemButton, nextItemButton, prevItemButton;
	JLabel accountIDLabel, accountNumberLabel, firstNameLabel, surnameLabel, accountTypeLabel, balanceLabel, overdraftLabel;
	JTextField accountIDTextField, accountNumberTextField, firstNameTextField, surnameTextField, accountTypeTextField, balanceTextField, overdraftTextField;
	static JFileChooser fc;
	JTable jTable;
	double interestRate;
	int currentItem = 0;
	boolean openValues;
	static String fileToSaveAs = "";

	
	private BankApplication() {	
		super("Bank Application");	
		initComponents();
	}

	private void initComponents() {
		swingSetup();
    	setDefaultCloseOperation(EXIT_ON_CLOSE);
		setOverdraft.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				setOverdraft();
			}
		});
		ActionListener first = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				first();
			}
		};	
		ActionListener next = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				next();
			}
		};	
		ActionListener prev = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				prev();
			}
		};
		ActionListener last = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				last();
			}
		};
		
		nextItemButton.addActionListener(next);
		nextItem.addActionListener(next);
		
		prevItemButton.addActionListener(prev);
		prevItem.addActionListener(prev);

		firstItemButton.addActionListener(first);
		firstItem.addActionListener(first);

		lastItemButton.addActionListener(last);
		lastItem.addActionListener(last);

		deleteItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				deleteItem();
			}
		});
		
		createItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				new CreateBankDialog(table);		
			}
		});
		modifyItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				modifyItem();
			}
		});
		setInterest.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){			
				setInterest();
			}
		});
		listAll.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				listAll();
			}
		});	
		open.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				open();
			}
		});	
		save.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				writeFile();
			}
		});	
		saveAs.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				saveFileAs();
			}
		});		
		closeApp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeApp();
			}
		});		
		findBySurname.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){	
				findBySurname();
			}
		});	
		findByAccount.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){	
				findByAccount();
			}
		});	
		deposit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				deposit();
			}
		});	
		withdraw.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				withdraw();
			}
		});
		calcInterest.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				calcInterest();
			}
		});		
	}
	
	private void calcInterest() {
		// TODO Auto-generated method stub
		for (Map.Entry<Integer, BankAccount> entry : table.entrySet()) {
			if(entry.getValue().getAccountType().equals("Deposit")){
				double equation = 1 + ((interestRate)/100);
				entry.getValue().setBalance(entry.getValue().getBalance()*equation);
				//System.out.println(equation);
				JOptionPane.showMessageDialog(null, "Balances Updated");
				displayDetails(entry.getKey());
			}
		}
	}

	private void withdraw() {
		String accNum = JOptionPane.showInputDialog("Account number to withdraw from: ");
		boolean found = false;
		for (Map.Entry<Integer, BankAccount> entry : table.entrySet()) {
			if(accNum.equals(entry.getValue().getAccountNumber().trim())){
				found = true;
				String toWithdraw = JOptionPane.showInputDialog("Account found, Enter Amount to Withdraw: ");
				if(Double.parseDouble(toWithdraw) > 0) {
				if(entry.getValue().getAccountType().trim().equals("Current")){
					if(Double.parseDouble(toWithdraw) > entry.getValue().getBalance() + entry.getValue().getOverdraft())
						JOptionPane.showMessageDialog(null, "Transaction exceeds overdraft limit");
					else{
						entry.getValue().setBalance(entry.getValue().getBalance() - Double.parseDouble(toWithdraw));
						displayDetails(entry.getKey());
					}
				}
				else if(entry.getValue().getAccountType().trim().equals("Deposit")){
					if(Double.parseDouble(toWithdraw) <= entry.getValue().getBalance() && Double.parseDouble(toWithdraw) > 0){
						entry.getValue().setBalance(entry.getValue().getBalance()-Double.parseDouble(toWithdraw));
						displayDetails(entry.getKey());
					}
					else
						JOptionPane.showMessageDialog(null, "Insufficient funds.");
				}
			}
				else
					JOptionPane.showMessageDialog(null, "Please input a positive number");
			}
		}
		 if(!found) {
			 if(accNum != null)
				 JOptionPane.showMessageDialog(null, "Account number " + accNum + " not found.");

		 }
	}

	private void deposit() {
		final String accNum = JOptionPane.showInputDialog("Account number to deposit into: ");
		boolean found = false;
		for (Map.Entry<Integer, BankAccount> entry : table.entrySet()) {
			if(accNum.equals(entry.getValue().getAccountNumber().trim())){
				found = true;
				String toDeposit = JOptionPane.showInputDialog("Account found, Enter Amount to Deposit: ");
				if(Double.parseDouble(toDeposit) > 0) {
					entry.getValue().setBalance(entry.getValue().getBalance() + Double.parseDouble(toDeposit));
					displayDetails(entry.getKey());
				}
				else
					JOptionPane.showMessageDialog(null, "Enter a positive figure");
				//balanceTextField.setText(entry.getValue().getBalance()+"");
			}
		}
		if (!found)
			if(accNum != null)
				 JOptionPane.showMessageDialog(null, "Account number " + accNum + " not found.");
	}

	private void findByAccount() {
		String accNum = JOptionPane.showInputDialog("Search for account number: ");
		boolean found = false;
	
		 for (Map.Entry<Integer, BankAccount> entry : table.entrySet()) {
			   
			 if(accNum.equals(entry.getValue().getAccountNumber().trim())){
				 found = true;
				 accountIDTextField.setText(entry.getValue().getAccountID()+"");
				 accountNumberTextField.setText(entry.getValue().getAccountNumber());
				 surnameTextField.setText(entry.getValue().getSurname());
				 firstNameTextField.setText(entry.getValue().getFirstName());
				 accountTypeTextField.setText(entry.getValue().getAccountType());
				 balanceTextField.setText(entry.getValue().getBalance()+"");
				 overdraftTextField.setText(entry.getValue().getOverdraft()+"");						
				 
			 }			 
		 }
		 if(found)
			 JOptionPane.showMessageDialog(null, "Account number " + accNum + " found.");
		 else
			 JOptionPane.showMessageDialog(null, "Account number " + accNum + " not found.");
	}

	private void findBySurname() {
		String sName = JOptionPane.showInputDialog("Search for surname: ");
		boolean found = false;
		
		 for (Map.Entry<Integer, BankAccount> entry : table.entrySet()) {
			   
			 if(sName.equalsIgnoreCase((entry.getValue().getSurname().trim()))){
				 found = true;
				 accountIDTextField.setText(entry.getValue().getAccountID()+"");
				 accountNumberTextField.setText(entry.getValue().getAccountNumber());
				 surnameTextField.setText(entry.getValue().getSurname());
				 firstNameTextField.setText(entry.getValue().getFirstName());
				 accountTypeTextField.setText(entry.getValue().getAccountType());
				 balanceTextField.setText(entry.getValue().getBalance()+"");
				 overdraftTextField.setText(entry.getValue().getOverdraft()+"");
			 }
		 }		
		 if(found)
			 JOptionPane.showMessageDialog(null, "Surname  " + sName + " found.");
		 else
			 JOptionPane.showMessageDialog(null, "Surname " + sName + " not found.");
	}

	private void closeApp() {
		int answer = JOptionPane.showConfirmDialog(BankApplication.this, "Do you want to save before quitting?");
		if (answer == JOptionPane.YES_OPTION) {
			saveFileAs();
			dispose();
		}
		else if(answer == JOptionPane.NO_OPTION)
			dispose();
		else if(answer==0)
			;	
	}

	private void open() {
		readFile();
		currentItem=0;
		while(!table.containsKey(currentItem)){
			currentItem++;
		}
		displayDetails(currentItem);
	}

	private void modifyItem() {
		surnameTextField.setEditable(true);
		firstNameTextField.setEditable(true);			
		openValues = true;
	}

	private void listAll() {
		JFrame frame = new JFrame("Accounts");
	
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		String col[] = {"ID","Number","Name", "Account Type", "Balance", "Overdraft"};
		
		DefaultTableModel tableModel = new DefaultTableModel(col, 0);
		jTable = new JTable(tableModel);
		JScrollPane scrollPane = new JScrollPane(jTable);
		jTable.setAutoCreateRowSorter(true);
		
		for (Map.Entry<Integer, BankAccount> entry : table.entrySet()) {
		   
		    
		    Object[] objs = {entry.getValue().getAccountID(), entry.getValue().getAccountNumber(), 
		    				entry.getValue().getFirstName().trim() + " " + entry.getValue().getSurname().trim(), 
		    				entry.getValue().getAccountType(), entry.getValue().getBalance(), 
		    				entry.getValue().getOverdraft()};

		    tableModel.addRow(objs);
		}
		frame.setSize(600,500);
		frame.add(scrollPane);
		//frame.pack();
        frame.setVisible(true);		
	}

	private void setInterest() {
		 String interestRateStr = JOptionPane.showInputDialog("Enter Interest Rate: (do not type the % sign)");
		 double rate = Double.parseDouble(interestRateStr);
		 if(interestRateStr!=null && rate >= 0 && rate <= 100)
			 interestRate = Double.parseDouble(interestRateStr);
		 else
			 JOptionPane.showMessageDialog(null, "Please enter a valid percentage");
	}

	private void deleteItem() {
		
		table.remove(currentItem);
		JOptionPane.showMessageDialog(null, "Account Deleted");
		

		currentItem=0;
		while(!table.containsKey(currentItem)){
			currentItem++;
		}
		displayDetails(currentItem);

	}

	private void last() {
		if(!table.isEmpty()) {
			saveOpenValues();
			currentItem =29;					
			while(!table.containsKey(currentItem)){
				currentItem--;			
			}
			
			displayDetails(currentItem);
		}
	}

	private void prev() {
		if(!table.isEmpty()) {
			ArrayList<Integer> keyList = new ArrayList<Integer>();
			int i=0;		
			while(i<TABLE_SIZE){
				i++;
				if(table.containsKey(i))
					keyList.add(i);
			}
			int minKey = Collections.min(keyList);
			//System.out.println(minKey);
			
			if(currentItem>minKey){
				while(!table.containsKey(currentItem)){
					//System.out.println("Current: " + currentItem + ", min key: " + minKey);
					currentItem--;
				}
			}
			displayDetails(currentItem);				
		}
	}

	private void saveOpenValues(){		
		if (openValues){
			surnameTextField.setEditable(false);
			firstNameTextField.setEditable(false);
				
			table.get(currentItem).setSurname(surnameTextField.getText());
			table.get(currentItem).setFirstName(firstNameTextField.getText());
		}
	}	
	
	private void displayDetails(int currentItem) {	
				
		accountIDTextField.setText(table.get(currentItem).getAccountID()+"");
		accountNumberTextField.setText(table.get(currentItem).getAccountNumber());
		surnameTextField.setText(table.get(currentItem).getSurname());
		firstNameTextField.setText(table.get(currentItem).getFirstName());
		accountTypeTextField.setText(table.get(currentItem).getAccountType());
		balanceTextField.setText(table.get(currentItem).getBalance()+"");
		if(accountTypeTextField.getText().trim().equals("Current"))
			overdraftTextField.setText(table.get(currentItem).getOverdraft()+"");
		else
			overdraftTextField.setText("Only applies to current accs");
	
	}
	
	private static boolean openFileRead()
	   {
		fc = new JFileChooser();
		int returnVal = fc.showOpenDialog(null);
		 
        if (returnVal == JFileChooser.APPROVE_OPTION) {
			if (fc.getSelectedFile().length() > 0L) { 
				File file = fc.getSelectedFile();
				table.clear();
				return true;
			}
			else {
				System.out.println("Empty File");
				return false;
			}
        } else {
                }
			
		      try // open file
		      {
		    	  if(fc.getSelectedFile()!=null) {
		    		  input = new RandomAccessFile( fc.getSelectedFile(), "r" );
		    		  return true;
		    	  }
	
		      } // end try
		      catch ( IOException ioException )
		      {
		    	  JOptionPane.showMessageDialog(null, "File Does Not Exist.");
		    	  return false;
		      } // end catch
			return false;
	   } // end method openFile
		
	private static void openFileWrite(){
		if(fileToSaveAs!=""){
	      try // open file
	      {
	         output = new RandomAccessFile( fileToSaveAs, "rw" );
	         JOptionPane.showMessageDialog(null, "Accounts saved to " + fileToSaveAs);
	      } // end try
	      catch ( IOException ioException )
	      {
	    	  JOptionPane.showMessageDialog(null, "File does not exist.");
	      } // end catch
			}
		else
			saveToFileAs();
	   }
	
	private static void saveToFileAs()  {
		fc = new JFileChooser();
		 int returnVal = fc.showSaveDialog(null);
         if (returnVal == JFileChooser.APPROVE_OPTION) {
             File file = fc.getSelectedFile();
           
             fileToSaveAs = file.getName();
             JOptionPane.showMessageDialog(null, "Accounts saved to " + file.getName());
         } else {
             JOptionPane.showMessageDialog(null, "Save cancelled by user");
         }
     	    
	         try {
	        	 if(fc.getSelectedFile()==null){
	        		 JOptionPane.showMessageDialog(null, "Cancelled");
	        	 }
	        	 else
	        		 output = new RandomAccessFile(fc.getSelectedFile(), "rw" );
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	      
	      
	     
	   }
	
	private static void closeFile() 
	   {
	      try // close file and exit
	      {
	         if ( input != null )
	            input.close();
	      } // end try
	      catch ( IOException ioException )
	      {
	         
	    	  JOptionPane.showMessageDialog(null, "Error closing file.");//System.exit( 1 );
	      } // end catch
	   } // end method closeFile
	
	private static void readRecords(){
	      RandomAccessBankAccount record = new RandomAccessBankAccount();
	      try // read a record and display
	      {
	         while ( true ){
	            do {
	            	if(input!=null)
	            		record.read( input );
	            } while ( record.getAccountID() == 0 );    
	            
	            BankAccount ba = new BankAccount(record.getAccountID(), record.getAccountNumber(), record.getFirstName(),
	                    record.getSurname(), record.getAccountType(), record.getBalance(), record.getOverdraft());            
	            Integer key = Integer.valueOf(ba.getAccountNumber().trim());		
				int hash = (key%TABLE_SIZE);
				while(table.containsKey(hash)){
			
					hash = hash+1;
				}	
	            table.put(hash, ba);
	         } // end while
	      } // end try
	      catch ( EOFException eofException ) // close file
	      {
	         return; // end of file was reached
	      } // end catch
	      catch ( IOException ioException )
	      {
	    	  JOptionPane.showMessageDialog(null, "Error reading file.");
	         System.exit( 1 );
	      } // end catch
	   }
	
	private static void saveToFile(){
		  RandomAccessBankAccount record = new RandomAccessBankAccount();	
	      for (Map.Entry<Integer, BankAccount> entry : table.entrySet()) {
			   record.setAccountID(entry.getValue().getAccountID());
			   record.setAccountNumber(entry.getValue().getAccountNumber());
			   record.setFirstName(entry.getValue().getFirstName());
			   record.setSurname(entry.getValue().getSurname());
			   record.setAccountType(entry.getValue().getAccountType());
			   record.setBalance(entry.getValue().getBalance());
			   record.setOverdraft(entry.getValue().getOverdraft());
			   
			   if(output!=null){	   
			      try {
						record.write(output);
					} catch (IOException u) {
						u.printStackTrace();
					}
			   }		   
			}	  	      
	}

	private void setOverdraft() {
		if(table.get(currentItem).getAccountType().trim().equals("Current")){
			String newOverdraftStr = JOptionPane.showInputDialog(null, "Enter new Overdraft", JOptionPane.OK_CANCEL_OPTION);
			overdraftTextField.setText(newOverdraftStr);
			table.get(currentItem).setOverdraft(Double.parseDouble(newOverdraftStr));
		}
		else
			JOptionPane.showMessageDialog(null, "Overdraft only applies to Current Accounts");
	}
	
	private void first() {
		saveOpenValues();
		if(!table.isEmpty()) {
			currentItem=0;
			while(!table.containsKey(currentItem)){
				currentItem++;
			}
			displayDetails(currentItem);
		}
	}
	
	private void next() {
		ArrayList<Integer> keyList = new ArrayList<Integer>();
		int i=0;

		while(i<TABLE_SIZE){
			i++;
			if(table.containsKey(i))
				keyList.add(i);
		}
		
		int maxKey = Collections.max(keyList);
		saveOpenValues();	

			if(currentItem<maxKey){
				currentItem++;
				while(!table.containsKey(currentItem)){
					currentItem++;
				}
			}
			displayDetails(currentItem);
	}
	
	
	private static void writeFile(){
		openFileWrite();
		saveToFile();
		closeFile();
	}
	
	private static void saveFileAs(){
		saveToFileAs();
		saveToFile();	
		closeFile();
	}
	
	private static void readFile(){
	    if(openFileRead() == true) {
	    readRecords();
	    closeFile();	
	    }
	}
	
	public static void main(String[] args) {
		BankApplication ba = new BankApplication();
		ba.pack();
		ba.setVisible(true);
	}
	
	private void swingSetup() {
		setLayout(new BorderLayout());
		JPanel displayPanel = new JPanel(new MigLayout());
		
		accountIDLabel = new JLabel("Account ID: ");
		accountIDTextField = new JTextField(15);
		accountIDTextField.setEditable(false);
		
		displayPanel.add(accountIDLabel, "growx, pushx");
		displayPanel.add(accountIDTextField, "growx, pushx, wrap");
		
		accountNumberLabel = new JLabel("Account Number: ");
		accountNumberTextField = new JTextField(15);
		accountNumberTextField.setEditable(false);
		
		displayPanel.add(accountNumberLabel, "growx, pushx");
		displayPanel.add(accountNumberTextField, "growx, pushx, wrap");

		surnameLabel = new JLabel("Last Name: ");
		surnameTextField = new JTextField(15);
		surnameTextField.setEditable(false);
		
		displayPanel.add(surnameLabel, "growx, pushx");
		displayPanel.add(surnameTextField, "growx, pushx, wrap");

		firstNameLabel = new JLabel("First Name: ");
		firstNameTextField = new JTextField(15);
		firstNameTextField.setEditable(false);
		
		displayPanel.add(firstNameLabel, "growx, pushx");
		displayPanel.add(firstNameTextField, "growx, pushx, wrap");

		accountTypeLabel = new JLabel("Account Type: ");
		accountTypeTextField = new JTextField(5);
		accountTypeTextField.setEditable(false);
		
		displayPanel.add(accountTypeLabel, "growx, pushx");
		displayPanel.add(accountTypeTextField, "growx, pushx, wrap");

		balanceLabel = new JLabel("Balance: ");
		balanceTextField = new JTextField(10);
		balanceTextField.setEditable(false);
		
		displayPanel.add(balanceLabel, "growx, pushx");
		displayPanel.add(balanceTextField, "growx, pushx, wrap");
		
		overdraftLabel = new JLabel("Overdraft: ");
		overdraftTextField = new JTextField(10);
		overdraftTextField.setEditable(false);
		
		displayPanel.add(overdraftLabel, "growx, pushx");
		displayPanel.add(overdraftTextField, "growx, pushx, wrap");
		
		add(displayPanel, BorderLayout.CENTER);
		
		JPanel buttonPanel = new JPanel(new GridLayout(1, 4));

		nextItemButton = new JButton(new ImageIcon("next.png"));
		prevItemButton = new JButton(new ImageIcon("prev.png"));
		firstItemButton = new JButton(new ImageIcon("first.png"));
		lastItemButton = new JButton(new ImageIcon("last.png"));
		
		buttonPanel.add(firstItemButton);
		buttonPanel.add(prevItemButton);
		buttonPanel.add(nextItemButton);
		buttonPanel.add(lastItemButton);
		
		add(buttonPanel, BorderLayout.SOUTH);
		
		menuBar = new JMenuBar();
    	setJMenuBar(menuBar);
		
		navigateMenu = new JMenu("Navigate");
    	
    	nextItem = new JMenuItem("Next Item");
    	prevItem = new JMenuItem("Previous Item");
    	firstItem = new JMenuItem("First Item");
    	lastItem = new JMenuItem("Last Item");
    	findByAccount = new JMenuItem("Find by Account Number");
    	findBySurname = new JMenuItem("Find by Surname");
    	listAll = new JMenuItem("List All Records");
    	
    	navigateMenu.add(nextItem);
    	navigateMenu.add(prevItem);
    	navigateMenu.add(firstItem);
    	navigateMenu.add(lastItem);
    	navigateMenu.add(findByAccount);
    	navigateMenu.add(findBySurname);
    	navigateMenu.add(listAll);
    	
    	menuBar.add(navigateMenu);
    	
    	recordsMenu = new JMenu("Records");
    	
    	createItem = new JMenuItem("Create Record");
    	modifyItem = new JMenuItem("Modify Record");
    	deleteItem = new JMenuItem("Delete Record");
    	setOverdraft = new JMenuItem("Set Overdraft");
    	setInterest = new JMenuItem("Set Interest");
    	
    	recordsMenu.add(createItem);
    	recordsMenu.add(modifyItem);
    	recordsMenu.add(deleteItem);
    	recordsMenu.add(setOverdraft);
    	recordsMenu.add(setInterest);
    	
    	menuBar.add(recordsMenu);
    	
    	transactionsMenu = new JMenu("Transactions");
    	
    	deposit = new JMenuItem("Deposit");
    	withdraw = new JMenuItem("Withdraw");
    	calcInterest = new JMenuItem("Calculate Interest");
    	
    	transactionsMenu.add(deposit);
    	transactionsMenu.add(withdraw);
    	transactionsMenu.add(calcInterest);
    	
    	menuBar.add(transactionsMenu);
    	
    	fileMenu = new JMenu("File");
    	
    	open = new JMenuItem("Open File");
    	save = new JMenuItem("Save File");
    	saveAs = new JMenuItem("Save As");
    	
    	fileMenu.add(open);
    	fileMenu.add(save);
    	fileMenu.add(saveAs);
    	
    	menuBar.add(fileMenu);
    	
    	exitMenu = new JMenu("Exit");    	
    	closeApp = new JMenuItem("Close Application");   	
    	exitMenu.add(closeApp);	
    	menuBar.add(exitMenu);	
	}
	
}