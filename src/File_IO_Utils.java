import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class File_IO_Utils extends BankApplication{

	private static final long serialVersionUID = 1L;

	private static void openFileRead()
	   {
		fc = new JFileChooser();
		int returnVal = fc.showOpenDialog(null);
		 
     if (returnVal == JFileChooser.APPROVE_OPTION) {
			if (fc.getSelectedFile().length() > 0L) { 
				File file = fc.getSelectedFile();
				table.clear();
				
			}
			else {
				System.out.println("Empty File");
				
			}
     } else {
             }
			
		      try // open file
		      {
		    	  if(fc.getSelectedFile()!=null) {
		    		  input = new RandomAccessFile( fc.getSelectedFile(), "r" );
		    		  
		    	  }
	
		      } // end try
		      catch ( IOException ioException )
		      {
		    	  JOptionPane.showMessageDialog(null, "File Does Not Exist.");
		      } // end catch
	   } // end method openFile
		
	static void readFile(){
		openFileRead();
	    readRecords();
	    closeFile();	
	    
	}
	 
	static void writeFile(){
		openFileWrite();
		saveToFile();
		closeFile();
	}
	
	static void saveFileAs(){
		saveToFileAs();
		saveToFile();	
		closeFile();
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
	


}
