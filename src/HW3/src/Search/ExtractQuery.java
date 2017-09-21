package Search;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import Classes.Path;
import Classes.Query;
import Classes.Stemmer;

public class ExtractQuery {
	
	BufferedReader topicReader;
	String curLine;
	Set<String> stopWords = new HashSet<>();
	int TopicID; 
	BufferedReader stopReader;
	
	public ExtractQuery() throws IOException {
		//you should extract the 4 queries from the Path.TopicDir
		//NT: the query content of each topic should be 1) tokenized, 2) to lowercase, 3) remove stop words, 4) stemming
		//NT: you can simply pick up title only for query, or you can also use title + description + narrative for the query content.
		
		this.topicReader = new BufferedReader(new InputStreamReader(new FileInputStream(Path.TopicDir)));
		
		this.stopReader = new BufferedReader(new InputStreamReader(new FileInputStream(Path.StopwordDir)));
		
		String Stopline = stopReader.readLine();
		
		while(Stopline != null){
			this.stopWords.add(Stopline);
			Stopline = stopReader.readLine();
		}
		
		stopReader.close();
	}
	
	public boolean hasNext() throws IOException
	{
		this.curLine = topicReader.readLine();
		
		if (curLine != null) return true;
		else{
			topicReader.close();
			return false;
		}
	}
	
	public Query next() throws IOException
	{	
		
		StringBuilder queryBuilder = new StringBuilder();
		
//		while(hasNext()){

			while(!curLine.startsWith("</top>")){
				curLine = topicReader.readLine();

				if(curLine.startsWith("<num>")){
					TopicID = Integer.parseInt(curLine.split(":")[1].trim());
				}
				
				if(curLine.startsWith("<title>")){
					queryBuilder.append(curLine.substring(7)).append(" ");
					curLine = topicReader.readLine();
				}
				
//				if(curLine.startsWith("<desc>")){
//					curLine = topicReader.readLine();
//					while(!curLine.startsWith("<narr>")){
//						queryBuilder.append(curLine).append(" ");
//						curLine = topicReader.readLine();
//					}
//				}
//				
//				if(curLine.startsWith("<narr>")){
//					curLine = topicReader.readLine();
//					while(!curLine.startsWith("</top>")){
//						queryBuilder.append(curLine).append(" ");
//						curLine = topicReader.readLine();
//					}
//				}
			}
			
			String query = queryBuilder.toString().trim();
			
			ArrayList<String> Token = WordTokenizer(query);
			String newquery = stemNonStop(Token);
			
			Query TopicQuery = new Query();
			TopicQuery.SetTopicId(Integer.toString(TopicID));
			TopicQuery.SetQueryContent(newquery);
			return TopicQuery;
			
//		}
			
//		return null;
	}
	
	
	
	public ArrayList<String> WordTokenizer( String texts ) {
		// this constructor will tokenize the input texts
		// please remove all punctuations
		ArrayList<String> words = new ArrayList<>(); 
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
				words.add(word[i]);
			}
		}
		return words;
	}
	
	public String stemNonStop(ArrayList<String> Token){
		
		StringBuilder newquery = new StringBuilder();
		
		for(int i = 0; i < Token.size(); i++){
			if(!stopWords.contains(Token.get(i))){
				char[] res = Token.get(i).toLowerCase().toCharArray();
				
				Stemmer s = new Stemmer();
				s.add(res, res.length);
				s.stem();
				
				newquery.append(s.toString()).append(" ");
			}
		}
		
		return newquery.toString();
	}
}
