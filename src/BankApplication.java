import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;

public class BankApplication extends JFrame {
	private static final long serialVersionUID = 1L;
	public static HashMap<Integer, BankAccount> table = new HashMap<Integer, BankAccount>();
	public final static int TABLE_SIZE = 29;
	private JMenuBar menuBar;
	static RandomAccessFile input;
	static RandomAccessFile output;
	JMenu navigateMenu, recordsMenu, transactionsMenu, fileMenu, exitMenu;
	private JMenuItem nextItem, prevItem, firstItem, lastItem, findByAccount, findBySurname, listAll, closeApp, createItem, modifyItem, deleteItem, setOverdraft, setInterest, open, save, saveAs, deposit, withdraw, calcInterest; 
	private JButton firstItemButton, lastItemButton, nextItemButton, prevItemButton;
	private JLabel accountIDLabel, accountNumberLabel, firstNameLabel, surnameLabel, accountTypeLabel, balanceLabel, overdraftLabel;
	static JTextField accountIDTextField, accountNumberTextField, 
	firstNameTextField, surnameTextField, accountTypeTextField, balanceTextField, overdraftTextField;
	static JFileChooser fc;
	static JTable jTable;
	static double interestRate;
	public static int currentItem = 0;
	private boolean openValues;
	static String fileToSaveAs = "";

	
	BankApplication() {	
		super("Bank Application");	
		initComponents();
	}

	private void initComponents() {
		swingSetup();
    	setDefaultCloseOperation(EXIT_ON_CLOSE);

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
		
		setOverdraft.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				NavigationUtils.setOverdraft();
			}
		});
		
		deleteItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				NavigationUtils.deleteItem();
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
				NavigationUtils.setInterest();
			}
		});
		listAll.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				NavigationUtils.listAll();
			}
		});	
		open.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				open();
			}
		});	
		save.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				File_IO_Utils.writeFile();
			}
		});	
		saveAs.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				File_IO_Utils.saveFileAs();
			}
		});		
		closeApp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeApp();
			}
		});		
		findBySurname.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){	
				NavigationUtils.findBySurname();
			}
		});	
		findByAccount.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){	
				NavigationUtils.findByAccount();
			}
		});	
		deposit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				NavigationUtils.deposit();
			}
		});	
		withdraw.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				NavigationUtils.withdraw();
			}
		});
		calcInterest.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				NavigationUtils.calcInterest();
			}
		});		
	}

	
	static void setCurrentEntry(Map.Entry<Integer, BankAccount> entry) {
		 accountIDTextField.setText(entry.getValue().getAccountID()+"");
		 accountNumberTextField.setText(entry.getValue().getAccountNumber());
		 surnameTextField.setText(entry.getValue().getSurname());
		 firstNameTextField.setText(entry.getValue().getFirstName());
		 accountTypeTextField.setText(entry.getValue().getAccountType());
		 balanceTextField.setText(entry.getValue().getBalance()+"");
		 overdraftTextField.setText(entry.getValue().getOverdraft()+"");
	}

	private void closeApp() {
		int answer = JOptionPane.showConfirmDialog(BankApplication.this, "Do you want to save before quitting?");
		if (answer == JOptionPane.YES_OPTION) {
			File_IO_Utils.saveFileAs();
			dispose();
		}
		else if(answer == JOptionPane.NO_OPTION)
			dispose();
	}

	private void open() {
		File_IO_Utils.readFile();
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

	private void saveOpenValues(){		
		if (openValues){
			surnameTextField.setEditable(false);
			firstNameTextField.setEditable(false);
				
			table.get(currentItem).setSurname(surnameTextField.getText());
			table.get(currentItem).setFirstName(firstNameTextField.getText());
		}
	}	
	
	static void displayDetails(int currentItem) {	
				
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
		if(!table.isEmpty()) {
			if(table.size() == 1) {
				first();
			}else {
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
		}
	}
	
	private void prev() {
		if(!table.isEmpty())
		{
			if(table.size() == 1) {
				first();
			}else {
			ArrayList<Integer> keyList = new ArrayList<Integer>();
			int i=0;		
			while(i<TABLE_SIZE){
				i++;
				if(table.containsKey(i))
					keyList.add(i);
			}
			int minKey = Collections.min(keyList);
			saveOpenValues();
			if(currentItem>=minKey){
				currentItem--;
				while(!table.containsKey(currentItem)){
					currentItem--;
				}
			}
			displayDetails(currentItem);	
			}
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
    	
    	nextItem = new JMenuItem("Next Account");
    	prevItem = new JMenuItem("Previous Account");
    	firstItem = new JMenuItem("First Account");
    	lastItem = new JMenuItem("Last Account");
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