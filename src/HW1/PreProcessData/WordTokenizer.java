package PreProcessData;

import java.util.ArrayList;

/**
 * This is for INFSCI 2140 in 2017
 * 
 * TextTokenizer can split a sequence of text into individual word tokens.
 */
public class WordTokenizer {
	//you can add essential private methods or variables
	
	private int iterator;
	private ArrayList<String> words = new ArrayList<>(); 
	private int len;
	
	// YOU MUST IMPLEMENT THIS METHOD
	public WordTokenizer( String texts ) {
		// this constructor will tokenize the input texts
		// please remove all punctuations
		
		this.iterator = 0;
		String[] word = texts.split("[^a-zA-Z0-9-.']");
		
		for(int i = 0; i< word.length;i++){
			if(word[i].trim() != null && !word[i].isEmpty()){
//				if word contain '-', replace that char to space.
				if(word[i].indexOf('-') != -1){
					word[i] = word[i].replaceAll("-"," ");
				}
//				if word contain 'n't', remove that part.
				if(word[i].indexOf('\'') != -1){
					word[i] = word[i].replaceAll("n't","");
					if(word[i].indexOf('\'') == word[i].length()-2) word[i] = word[i].replaceAll("'s","");
					else word[i] = word[i].replaceAll("'"," ").trim(); 
				}
				
				if(word[i].indexOf('.') != -1){
					word[i] = word[i].replaceAll(".","");
				}
				this.words.add(word[i]);
			}
		}
		
		this.len = words.size();
	}
	
	// YOU MUST IMPLEMENT THIS METHOD
	public String nextWord() {
		// read and return the next word of the document
		// or return null if it is the end of the document
		
		String cur;
		
		while(iterator < len){
			cur = words.get(iterator);
			iterator++;
			return cur;
		}
		
		return null;
	}
	
}
