package Indexing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import Classes.Path;


class Content{
	int block;
	String term;
	
	public Content(int b, String t){
		this.block = b;
		this.term = t;
	}
}

public class MyIndexWriter {
	// We suggest you to write very efficient code here, otherwise, your memory cannot hold our corpus...

	String StoragePath;
	FileWriter docIdx;
	LinkedHashMap<String, LinkedHashMap<Integer, Integer>> token;
	LinkedHashMap<String, Integer> docMap;
	LinkedHashMap<String, Integer> termDic;

//	String type;
	int docid;
	int blockNumber;
	final int BLOCK = 40000;
	
	
	public MyIndexWriter(String type) throws IOException {
		// This constructor should initiate the FileWriter to output your index files
		// remember to close files if you finish writing the index
		
		if (type.equals("trectext"))
			StoragePath = Path.IndexTextDir;
		else
			StoragePath = Path.IndexWebDir;
		
		new File(StoragePath).mkdir();
		
		docIdx = new FileWriter(StoragePath + Path.docIdxDir);

		this.blockNumber = 0;
		this.token = new LinkedHashMap<>();
		this.docMap = new LinkedHashMap<>();
		this.termDic = new LinkedHashMap<>();
		this.docid = 1;
		
	}
	
	public void IndexADocument(String docno, String content) throws IOException {
		// you are strongly suggested to build the index by installments
		// you need to assign the new non-negative integer docId to each document, which will be used in MyIndexReader
		
		docIdx.append(docno + "\n");
	
		String[] tokens = content.split(" ");
		
		for(String word: tokens) {
			if(token.containsKey(word)) {		
				LinkedHashMap<Integer, Integer> cur = token.get(word);
				if(cur.containsKey(docid))
					cur.put(docid, cur.get(docid)+1);
				else 
					cur.put(docid, 1);
			} else {
				LinkedHashMap<Integer, Integer> cur = new LinkedHashMap<>();
				cur.put(docid, 1);
				token.put(word, cur);
			}	
		}
		
		++docid;
		
		if(docid % BLOCK == 0) 
			saveBlock();	
	}
	
	private void saveBlock() throws IOException {

		// TODO Auto-generated method stub
		FileWriter blockFile = new FileWriter(StoragePath + Path.blockIdxDir + blockNumber++);
		
		for(Map.Entry<String, LinkedHashMap<Integer, Integer>> record:token.entrySet()){
			String word = record.getKey();
			LinkedHashMap<Integer, Integer> docFreq = record.getValue();
			
			int colFreq = 0;			

			for(Map.Entry<Integer, Integer> freq: docFreq.entrySet()){
				colFreq += freq.getValue();
				blockFile.append(freq.getKey() + ":" + freq.getValue() + ",");
			}
			blockFile.append("\n");
			blockFile.flush();
			
			if(termDic.containsKey(word))
				termDic.put(word, termDic.get(word) + colFreq);
			else
				termDic.put(word, colFreq);
			
			token.put(word, new LinkedHashMap<Integer, Integer>());
		}
		blockFile.flush();
		blockFile.close();
	}

	

	
	private void mergeAllIndex() throws IOException{
		
		FileWriter wholeIdx = new FileWriter(StoragePath+Path.wholeIdx);
//		blockNumber--;
		
		FileInputStream[] fileInputStream = new FileInputStream[blockNumber];
		BufferedReader[] reader = new BufferedReader[blockNumber];
		String[] line = new String[blockNumber];
		
		for(int i=0; i<blockNumber; i++){
			fileInputStream[i] = new FileInputStream(StoragePath + Path.blockIdxDir + i);
			reader[i] = new BufferedReader(new InputStreamReader(fileInputStream[i]));
			line[i] = reader[i].readLine();
		}
		
		Iterator<String> termList = termDic.keySet().iterator();
		
		while(line[blockNumber-1] != null){
			
			wholeIdx.append(termList.next() + "\n");
			
			for(int i=0; i<blockNumber; i++){
				
				if(line[i] != null){
					wholeIdx.append(line[i]);
					line[i] = reader[i].readLine();
				}
				
			}
			
			wholeIdx.append("\n");
			
		}
		
		wholeIdx.close();
		for(int i=0; i<blockNumber; i++){
			reader[i].close();
			fileInputStream[i].close();		
		}

	}
	

	private void saveToken() throws IOException {
		// TODO Auto-generated method stub
		
		FileWriter termIdx = new FileWriter(StoragePath+Path.termIdxDir);
		
		for(Entry<String, Integer> term: termDic.entrySet()){
			termIdx.append(term.getKey() + "\n" + term.getValue() + "\n");
		}
		
		termIdx.close();
		
	}
	
	public void Close() throws IOException, ClassNotFoundException {
		// close the index writer, and you should output all the buffered content (if any).
		// if you write your index into several files, you need to fuse them here.
		saveBlock(); 
		mergeAllIndex();
		saveToken();
		docIdx.close();
	}

	
}
