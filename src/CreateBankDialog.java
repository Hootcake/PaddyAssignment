
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import javax.swing.*;

import net.miginfocom.swing.MigLayout;

public class CreateBankDialog extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private final static int TABLE_SIZE = 29;
	Random rand = new Random();
	ArrayList<BankAccount> accountList;
	HashMap<Integer, BankAccount> table = new HashMap<Integer, BankAccount>();
	private JLabel accountNumberLabel, firstNameLabel, surnameLabel, 
	accountTypeLabel, balanceLabel, overdraftLabel; 
	private JPanel dataPanel = new JPanel(new MigLayout());
	private JTextField accountNumberTextField;
	private JTextField firstNameTextField, surnameTextField, accountTypeTextField, 
	balanceTextField, overdraftTextField;
	
	// Constructor code based on that for the Create and Edit dialog classes in the Shapes exercise.
	
	 CreateBankDialog(HashMap<Integer, BankAccount> accounts) {
		super("Add Bank Details");
		table = accounts;
		setupDialog();
		setLayout(new BorderLayout());
		
		String[] comboTypes = {"Current", "Deposit"};
		final JComboBox<String> comboBox = new JComboBox<String>(comboTypes);
		dataPanel.add(comboBox, "growx, pushx, wrap");
		add(dataPanel, BorderLayout.CENTER);
		
		JPanel buttonPanel = new JPanel(new FlowLayout());
		JButton addButton = new JButton("Add");
		JButton cancelButton = new JButton("Cancel");
		
		buttonPanel.add(addButton);
		buttonPanel.add(cancelButton);
		
		add(buttonPanel, BorderLayout.SOUTH);
		
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				handleAccount(comboBox.getSelectedItem().toString());
			}
		});
		
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		setSize(400,800);
		pack();
		setVisible(true);

	}
	
	public void handleAccount(String accType) {
		final String accountNumber = accountNumberTextField.getText();
		final String surname = surnameTextField.getText().trim();
		final String firstName = firstNameTextField.getText().trim();	
		if (accountNumber != null && accountNumber.length()==8 && Double.parseDouble(accountNumber) >= 0 && !surname.isEmpty() && !firstName.isEmpty() && accType != null) {
			try {
				boolean accNumTaken=false;	
				int randNumber = rand.nextInt(24) + 1;
				 for (Map.Entry<Integer, BankAccount> entry : table.entrySet()) {
					 while(randNumber == entry.getValue().getAccountID()){
						 randNumber = rand.nextInt(24)+1;
					 }		 
				 }
			 
					for (Map.Entry<Integer, BankAccount> entry : table.entrySet()) {					
						 if(entry.getValue().getAccountNumber().trim().equals(accountNumberTextField.getText())){
							 accNumTaken=true;	
						 }
					 }
				
				if(!accNumTaken) {
					BankAccount account = new BankAccount(randNumber, accountNumber, surname, firstName, accType, 0.0, 0.0);	
					int key = Integer.parseInt(account.getAccountNumber());
					int hash = (key%TABLE_SIZE);	
					while(table.containsKey(hash)){
						hash = hash+1;
					}
					table.put(hash, account);
				}
				else{
					JOptionPane.showMessageDialog(null, "Account Number must be unique");
				}
			}
			catch (Exception ex) {
				JOptionPane.showMessageDialog(null, "Number format exception");					
			}
		}
		else JOptionPane.showMessageDialog(null, "Please make sure all fields have values, and Account Number is a unique 8 digit number");
		dispose();
	}
	
	private void setupDialog() {
		accountNumberLabel = new JLabel("Account Number: ");
		accountNumberTextField = new JTextField(15);
		accountNumberTextField.setEditable(true);

		surnameLabel = new JLabel("Last Name: ");
		surnameTextField = new JTextField(15);
		surnameTextField.setEditable(true);
		
		firstNameLabel = new JLabel("First Name: ");
		firstNameTextField = new JTextField(15);
		firstNameTextField.setEditable(true);
		
		accountTypeLabel = new JLabel("Account Type: ");
		accountTypeTextField = new JTextField(5);
		accountTypeTextField.setEditable(true);
		
		balanceLabel = new JLabel("Balance: ");
		balanceTextField = new JTextField(10);
		balanceTextField.setText("0.0");
		balanceTextField.setEditable(false);
			
		overdraftLabel = new JLabel("Overdraft: ");
		overdraftTextField = new JTextField(10);
		overdraftTextField.setText("0.0");
		overdraftTextField.setEditable(false);
		
		dataPanel.add(accountNumberLabel, "growx, pushx");
		dataPanel.add(accountNumberTextField, "growx, pushx, wrap");
		dataPanel.add(firstNameLabel, "growx, pushx");
		dataPanel.add(firstNameTextField, "growx, pushx, wrap");
		dataPanel.add(surnameLabel, "growx, pushx");
		dataPanel.add(surnameTextField, "growx, pushx, wrap");
		dataPanel.add(overdraftLabel, "growx, pushx");
		dataPanel.add(overdraftTextField, "growx, pushx, wrap");
		dataPanel.add(balanceLabel, "growx, pushx");
		dataPanel.add(balanceTextField, "growx, pushx, wrap");
		dataPanel.add(accountTypeLabel, "growx, pushx");	
	}
}