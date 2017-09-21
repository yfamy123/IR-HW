package PreProcessData;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import Classes.Path;

public class StopWordRemover {
	//you can add essential private methods or variables
	
	private BufferedReader reader;
	private FileInputStream file;
	private Set<String> stopword;
	
	public StopWordRemover( ) {
		// load and store the stop words from the fileinputstream with appropriate data structure
		// that you believe is suitable for matching stop words.
		// address of stopword.txt should be Path.StopwordDir
		
		try{
			this.file = new FileInputStream(Path.StopwordDir);
			this.reader = new BufferedReader(new InputStreamReader(file));
			System.out.println("StopWord file is opened successfully!");
			
			this.stopword = new HashSet<>();
			
			loadWord();
			
		}catch (IOException e)
        {
            System.out.println("File input error!");
        }	
	}
	
	public void loadWord(){
		
		String line;
		try {
			line = reader.readLine();
			
			while(line != null){
				
				stopword.add(line);
				line = reader.readLine();
				
			}
			
			System.out.println("StopWord is loaded successfully!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// YOU MUST IMPLEMENT THIS METHOD
	public boolean isStopword(String word ) {
		// return true if the input word is a stopword, or false if not
		
		return stopword.contains(word);
	}
}
