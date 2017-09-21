package PreProcessData;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Classes.Path;

/**
 * This is for INFSCI 2140 in 2017
 *
 */
public class TrectextCollection implements DocumentCollection {
	//you can add essential private methods or variables
	
	private String Begin_Index = "<DOCNO>";
	private String End_Index = "</DOCNO>";
	private String Begin_Doc = "<DOC>";
	private String End_Doc = "</DOC>";
	private String Begin_Content = "<TEXT>";
	private String End_Content = "</TEXT>";
	
	private String index;
	
	private BufferedReader reader;
	private FileInputStream file;
	
	
	// YOU SHOULD IMPLEMENT THIS METHOD
	public TrectextCollection() throws IOException {
		// This constructor should open the file in Path.DataTextDir
		// and also should make preparation for function nextDocument()
		// you cannot load the whole corpus into memory here!!
		
		try{
			this.file = new FileInputStream(Path.DataTextDir);
			this.reader = new BufferedReader(new InputStreamReader(file));
			this.index = "";
			System.out.println("File is loaded successfully");
		}catch (IOException e)
        {
            System.out.println("File input error!");
        }	
	}
	
	// YOU SHOULD IMPLEMENT THIS METHOD
	public Map<String, String> nextDocument() throws IOException {
		// this method should load one document from the corpus, and return this document's number and content.
		// the returned document should never be returned again.
		// when no document left, return null
		// NTT: remember to close the file that you opened, when you do not use it any more
		
		String line = reader.readLine();
		boolean flag = false;
		
		while(line != null){
			
			if(line.startsWith(Begin_Doc)){
				StringBuilder text = new StringBuilder();
				Map<String,String> doc = new HashMap<>();
				
				while(!line.startsWith(End_Doc)){
					
					if(line.startsWith(Begin_Index)){
						Pattern p = Pattern.compile(Pattern.quote(Begin_Index)+"(.*?)"+Pattern.quote(End_Index));
						Matcher m = p.matcher(line);
						while (m.find()) {
				            index = m.group(1);
				        }
					}
					
					if(line.startsWith(Begin_Content)){
						line = reader.readLine();
						while(line != null && !line.startsWith(End_Content)){
							text.append(line);
							line = reader.readLine();
						}
					}
						
					line = reader.readLine();
				}
				
				String content = text.toString()
						.replaceAll("\\&[a-zA-Z]{1,10};", "")
						.replaceAll("<[^>]*>", "").replaceAll("[(/>)<]", "");
				doc.put(index, content);
				return doc;
			}
		}
		
		System.out.println("Reading file is completed!!");
		reader.close();
		file.close();
		
		return null;
	}
	
}
