package Indexing;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import Classes.Path;

public class PreProcessedCorpusReader {
	private BufferedReader reader;
	
	public PreProcessedCorpusReader(String type) throws IOException {
		// This constructor opens the pre-processed corpus file, Path.ResultHM1 + type
		// You can use your own version, or download from http://crystal.exp.sis.pitt.edu:8080/iris/resource.jsp
		// Close the file when you do not use it any more
	
		this.reader = new BufferedReader(new InputStreamReader(new FileInputStream(Path.ResultHM1 + type)));
		System.out.println("Input file opened...");

	}
	

	public Map<String, String> NextDocument() throws IOException {
		// read a line for docNo, put into the map with <"DOCNO", docNo>
		// read another line for the content , put into the map with <"CONTENT", content>
		
		Map<String,String> doc = new HashMap<>();
		String docno = reader.readLine();
		if(docno != null){
			String content = reader.readLine();
			doc.put(docno, content);
			return doc;
		}
		
		System.out.println("Reading file is completed!!");
		reader.close();
		
		return null;
	}

}
