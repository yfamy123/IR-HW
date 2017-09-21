package PreProcessData;
import Classes.Stemmer;

/**
 * This is for INFSCI 2140 in 2017
 * 
 */
public class WordNormalizer {
	//you can add essential private methods or variables
	
	// YOU MUST IMPLEMENT THIS METHOD
	public String lowercase(String chars ) {
		//transform the uppercase characters in the word to lowercase
		char[] c = chars.toCharArray();
		
		for(int i = 0; i < c.length; i++){
			c[i] = Character.toLowerCase(c[i]);
		}
		
		return String.valueOf(c);
	}
	
	public String stem(String chars)
	{
		//use the stemmer in Classes package to do the stemming on input word, and return the stemmed word
		
		String str="";
		
		Stemmer stem = new Stemmer();
		char[] c = chars.toCharArray();
		stem.add(c,c.length);
		stem.stem();
		
		str = stem.toString();
		return str;
	}
	
}
