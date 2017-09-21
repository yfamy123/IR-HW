package Indexing;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import Classes.Path;


public class MyIndexReader {
	// We suggest you to write very efficient code here, otherwise, your memory cannot hold our corpus...
	HashMap<String, Integer> docMapStrToNum = new HashMap<>();
	HashMap<Integer, String> docMapNumToStr = new HashMap<>();
	String StoragePath;
	BufferedReader readerIdx;
	BufferedReader readerTerm;
	String post;
	int lineID;
	
	public MyIndexReader( String type ) throws IOException {
		//read the index files you generated in task 1
		//remember to close them when you finish using them
		//use appropriate structure to store your index
		
		if (type.equals("trectext"))
			StoragePath = Path.IndexTextDir;
		else
			StoragePath = Path.IndexWebDir;
		
		this.readerIdx = new BufferedReader(new InputStreamReader(new FileInputStream(StoragePath + Path.wholeIdx)));
		this.readerTerm = new BufferedReader(new InputStreamReader(new FileInputStream(StoragePath + Path.termIdxDir)));
		
		BufferedReader readerDoc = new BufferedReader(new InputStreamReader(new FileInputStream(StoragePath + Path.docIdxDir)));
		
		String line = readerDoc.readLine();
		int i = 1;
		while(line != null){
			docMapStrToNum.put(line, i);
			docMapNumToStr.put(i, line);
			line = readerDoc.readLine();
			++i;
		}
		
		readerDoc.close();
		
	}
	
	//get the non-negative integer dociId for the requested docNo
	//If the requested docno does not exist in the index, return -1
	public int GetDocid( String docno ) throws IOException {
		
		if(docMapStrToNum.containsKey(docno)) return docMapStrToNum.get(docno);
		else return -1;
		
	}

	// Retrieve the docno for the integer docid
	public String GetDocno( int docid ) {
		
		if(docMapNumToStr.containsKey(docid)) return docMapNumToStr.get(docid);
		else return null;
	}
	
	/**
	 * Get the posting list for the requested token.
	 * 
	 * The posting list records the documents' docids the token appears and corresponding frequencies of the term, such as:
	 *  
	 *  [docid]		[freq]
	 *  1			3
	 *  5			7
	 *  9			1
	 *  13			9
	 * 
	 * ...
	 * 
	 * In the returned 2-dimension array, the first dimension is for each document, and the second dimension records the docid and frequency.
	 * 
	 * For example:
	 * array[0][0] records the docid of the first document the token appears.
	 * array[0][1] records the frequency of the token in the documents with docid = array[0][0]
	 * ...
	 * 
	 * NOTE that the returned posting list array should be ranked by docid from the smallest to the largest. 
	 * 
	 * @param token
	 * @return
	 */
	public int[][] GetPostingList( String token ) throws IOException {
		if(post == null) return null;
		
		String[] list = post.split(",");
		int[][] postingList = new int[list.length][2];
		
		int i = 0;
		
		for(String item : list){
			String[] docFreq = item.split(":");
			postingList[i][0] = Integer.parseInt((docFreq[0]));
			postingList[i][1] = Integer.parseInt((docFreq[1]));
			++i;
		}
		
		return postingList;

	}

	// Return the number of documents that contains the token.
	public int GetDocFreq( String token ) throws IOException {
		
		String line = readerIdx.readLine();
		
		while(line != null){
			if(line.equals(token)){
				this.post = readerIdx.readLine();
				break;
			}
			line = readerIdx.readLine();
			line = readerIdx.readLine();
		}
		
		if(post != null) return post.split(",").length;
		else return 0;
	}
	
	// Return the total number of times the token appears in the collection.
	public long GetCollectionFreq( String token ) throws IOException {
		
		String line = readerTerm.readLine();
		
		while(line != null){
			if(line.equals(token)){
				return Integer.parseInt(readerTerm.readLine());
			}
			line = readerTerm.readLine();
			line = readerTerm.readLine();
		}
		
		return 0;
		
	}
	
	public void Close() throws IOException {
		readerIdx.close();
		readerTerm.close();
		
	}
	
}