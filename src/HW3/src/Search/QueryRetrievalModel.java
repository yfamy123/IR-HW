package Search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import Classes.Query;
import Classes.Document;
import IndexingLucene.MyIndexReader;

public class QueryRetrievalModel {
	
	protected MyIndexReader indexReader;
	PriorityQueue<Document> documentRank;
	double mu = 200;
	long collectionNum = 142065539;
	
	public QueryRetrievalModel(MyIndexReader ixreader) {
		indexReader = ixreader;
		
		this.documentRank = new PriorityQueue<Document>(new Comparator<Document>() {

			@Override
			public int compare(Document o1, Document o2) {
				// TODO Auto-generated method stub
				if(o1.score() > o2.score()) return -1;
				else if(o1.score() < o2.score()) return 1;
				else return 0;
			}
			
		});
	}
	
	/**
	 * Search for the topic's relevant documents. 
	 * The returned results (retrieved documents) should be ranked by the score (from the most relevant to the least).
	 * TopN specifies the maximum number of results to be returned.
	 * 
	 * @param aQuery The query to be searched for.
	 * @param TopN The maximum number of returned document
	 * @return
	 */
	
	public List<Document> retrieveQuery( Query aQuery, int TopN ) throws IOException {
		// NT: you will find our IndexingLucene.Myindexreader provides method: docLength()
		// implement your retrieval model here, and for each input query, return the topN retrieved documents
		// sort the docs based on their relevance score, from high to low
		
		String[] Tokens = aQuery.GetQueryContent().split(" ");
		Set<String> nonExistWord = new HashSet<>();
		HashSet<Integer> docList = new HashSet<>();
		
		HashMap<String, HashMap<Integer, Integer>> record = new HashMap<>();
		
		for(int i = 0; i < Tokens.length; i++){
			int posting[][] = indexReader.getPostingList(Tokens[i]);
//			System.out.println(Tokens[i]);
			
			if(posting == null || posting.length == 0 || posting[0].length == 0){
				nonExistWord.add(Tokens[i]);
				continue;
			}
			
			HashMap<Integer, Integer> tokenInfo = new HashMap<>();
			for(int j = 0; j < posting.length; j++){
				tokenInfo.put(posting[j][0], posting[j][1]);
				docList.add(posting[j][0]);
			}
			
			record.put(Tokens[i], tokenInfo);
		}
		
		for(int docID : docList){
			int docLength = indexReader.docLength(docID);
			double score = 1;
			
			for(String word : Tokens){
				if(nonExistWord.contains(word)) continue;
				
				long collectionFreq = indexReader.CollectionFreq(word);
				HashMap<Integer, Integer> tokenInfo = record.get(word);
				int freq = tokenInfo.containsKey(docID)? tokenInfo.get(docID) : 0;
				
				double p = (freq + mu * collectionFreq / collectionNum) / (docLength + mu);
				score *= p;	
			}	
			
			if(score != 0){
				Document curr = new Document(Integer.toString(docID), indexReader.getDocno(docID), score);
				documentRank.add(curr);
			}
		}
		
		List<Document> rankList = new ArrayList<>();
		
		if(!documentRank.isEmpty()){
			for(int i = 0; i < TopN; i++){
				rankList.add(documentRank.poll());
			}
			return rankList;
		}

		return null;
	}
	
}