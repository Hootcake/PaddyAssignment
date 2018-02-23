import java.util.Map;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
public class NavigationUtils extends BankApplication{

	private static final long serialVersionUID = 1L;
	
	public static void withdraw() {
		String accNum = JOptionPane.showInputDialog("Account number to withdraw from: ");
		boolean found = false;
		for (Map.Entry<Integer, BankAccount> entry : BankApplication.table.entrySet()) {
			if(accNum.equals(entry.getValue().getAccountNumber().trim())){
				found = true;
				double toWithdraw = (Double.parseDouble(JOptionPane.showInputDialog("Account found, Enter Amount to Withdraw: ")));
				if(toWithdraw > 0) {
				if(entry.getValue().getAccountType().trim().equals("Current")){
					if(toWithdraw > entry.getValue().getBalance() + entry.getValue().getOverdraft())
						JOptionPane.showMessageDialog(null, "Transaction exceeds overdraft limit");
					else{
						entry.getValue().setBalance(entry.getValue().getBalance() - toWithdraw);
						displayDetails(entry.getKey());
					}
				}
				else if(entry.getValue().getAccountType().trim().equals("Deposit")){
					if(toWithdraw <= entry.getValue().getBalance() && toWithdraw > 0){
						entry.getValue().setBalance(entry.getValue().getBalance()-toWithdraw);
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
	
	static void deposit() {
		final String accNum = JOptionPane.showInputDialog("Account number to deposit into: ");
		boolean found = false;
		for (Map.Entry<Integer, BankAccount> entry : table.entrySet()) {
			if(accNum.equals(entry.getValue().getAccountNumber().trim())){
				found = true;
				double toDeposit = (Double.parseDouble(JOptionPane.showInputDialog("Account found, Enter Amount to Withdraw: ")));
				if(toDeposit > 0) {
					entry.getValue().setBalance(entry.getValue().getBalance() + toDeposit);
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

	
	static void calcInterest() {
		for (Map.Entry<Integer, BankAccount> entry : table.entrySet()) {
			if(entry.getValue().getAccountType().equals("Deposit")){
				entry.getValue().setBalance(entry.getValue().getBalance()*(1+(interestRate/100)));
				JOptionPane.showMessageDialog(null, "Balances Updated");
				displayDetails(entry.getKey());
			}
		}
	}
	
	static void findByAccount() {
		String accNum = JOptionPane.showInputDialog("Search for account number: ");
		boolean found = false;
	
		 for (Map.Entry<Integer, BankAccount> entry : table.entrySet()) {
			   
			 if(accNum.equals(entry.getValue().getAccountNumber().trim())){
				 found = true;
				 setCurrentEntry(entry);								 
			 }		
		 }
		 if(found)
			 JOptionPane.showMessageDialog(null, "Account number " + accNum + " found.");
		 else
			 JOptionPane.showMessageDialog(null, "Account number " + accNum + " not found.");
	}
	
	 static void setInterest() {
		 String interestRateStr = JOptionPane.showInputDialog("Enter Interest Rate: (do not type the % sign)");
		 double rate = Double.parseDouble(interestRateStr);
		 if(interestRateStr!=null && rate >= 0 && rate <= 100)
			 interestRate = Double.parseDouble(interestRateStr);
		 else
			 JOptionPane.showMessageDialog(null, "Please enter a valid percentage");
	}
	 
		static void setOverdraft() {
			if(table.get(currentItem).getAccountType().trim().equals("Current")){
				String newOverdraftStr = JOptionPane.showInputDialog(null, "Enter new Overdraft", JOptionPane.OK_CANCEL_OPTION);
				overdraftTextField.setText(newOverdraftStr);
				table.get(currentItem).setOverdraft(Double.parseDouble(newOverdraftStr));
			}
			else
				JOptionPane.showMessageDialog(null, "Overdraft only applies to Current Accounts");
		}
		

		static void findBySurname() {
			String sName = JOptionPane.showInputDialog("Search for surname: ");
			boolean found = false;
			 for (Map.Entry<Integer, BankAccount> entry : table.entrySet()) {
				 if(sName.equalsIgnoreCase((entry.getValue().getSurname().trim()))){
					 found = true;
					 setCurrentEntry(entry);
				 }
			 }		
			 if(found)
				 JOptionPane.showMessageDialog(null, "Surname  " + sName + " found.");
			 else
				 JOptionPane.showMessageDialog(null, "Surname " + sName + " not found.");
		}
		
		static void listAll() {
			JFrame frame = new JFrame("Accounts");

	        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
	        frame.setVisible(true);		
		}
		
		static void deleteItem() {
			if(!table.isEmpty()) {
			table.remove(currentItem);
			JOptionPane.showMessageDialog(null, "Account Deleted");
			currentItem=0;
			while(!table.containsKey(currentItem)){
				currentItem++;
			}
			displayDetails(currentItem);
			}
			else
				JOptionPane.showMessageDialog(null, "No accounts present");
		}

}
