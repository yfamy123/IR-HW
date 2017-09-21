package PseudoRFSearch;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import Classes.Document;
import Classes.Query;
import IndexingLucene.MyIndexReader;
import Search.QueryRetrievalModel;

public class PseudoRFRetrievalModel {

	MyIndexReader ixreader;
	PriorityQueue<Document> documentRank;
	double mu = 2000;
	long collectionNum = 142065539;
	
	public PseudoRFRetrievalModel(MyIndexReader ixreader)
	{
		this.ixreader=ixreader;
		
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
	 * Search for the topic with pseudo relevance feedback in 2017 spring assignment 4. 
	 * The returned results (retrieved documents) should be ranked by the score (from the most relevant to the least).
	 * 
	 * @param aQuery The query to be searched for.
	 * @param TopN The maximum number of returned document
	 * @param TopK The count of feedback documents
	 * @param alpha parameter of relevance feedback model
	 * @return TopN most relevant document, in List structure
	 */
	public List<Document> RetrieveQuery( Query aQuery, int TopN, int TopK, double alpha) throws Exception {	
		// this method will return the retrieval result of the given Query, and this result is enhanced with pseudo relevance feedback
		// (1) you should first use the original retrieval model to get TopK documents, which will be regarded as feedback documents
		// (2) implement GetTokenRFScore to get each query token's P(token|feedback model) in feedback documents
		// (3) implement the relevance feedback model for each token: combine the each query token's original retrieval score P(token|document) with its score in feedback documents P(token|feedback model)
		// (4) for each document, use the query likelihood language model to get the whole query's new score, P(Q|document)=P(token_1|document')*P(token_2|document')*...*P(token_n|document')
		
		
		//get P(token|feedback documents)
		HashMap<String,Double> TokenRFScore=GetTokenRFScore(aQuery,TopK);
		
		
		// sort all retrieved documents from most relevant to least, and return TopN
		List<Document> results = new ArrayList<Document>();
		
		
		String[] Tokens = aQuery.GetQueryContent().split(" ");
		Set<String> nonExistWord = new HashSet<>();
		HashSet<Integer> docList = new HashSet<>();
		
		HashMap<String, HashMap<Integer, Integer>> record = new HashMap<>();
		
		for(int i = 0; i < Tokens.length; i++){
			int[][] posting = ixreader.getPostingList(Tokens[i]);

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
			int docLength = ixreader.docLength(docID);
			double score = 1;
			
			for(String word : Tokens){
				if(nonExistWord.contains(word)) continue;
				
				long collectionFreq = ixreader.CollectionFreq(word);
				HashMap<Integer, Integer> tokenInfo = record.get(word);
				int freq = tokenInfo.containsKey(docID)? tokenInfo.get(docID) : 0;
				
				double P = (freq + mu * collectionFreq / collectionNum) / (docLength + mu);
				double newP = alpha * P + (1 - alpha) * TokenRFScore.get(word);
				score *= newP;	
			}	
			
			if(score != 0){
				Document curr = new Document(Integer.toString(docID), ixreader.getDocno(docID), score);
				documentRank.add(curr);
			}
		}
		
		
		if(!documentRank.isEmpty()){
			for(int i = 0; i < TopN; i++){
				results.add(documentRank.poll());
			}
			return results;
		}

		return null;

	}
	
	public HashMap<String,Double> GetTokenRFScore(Query aQuery,  int TopK) throws Exception
	{
		// for each token in the query, you should calculate token's score in feedback documents: P(token|feedback documents)
		// use Dirichlet smoothing
		// save <token, score> in HashMap TokenRFScore, and return it
		HashMap<String,Double> TokenRFScore=new HashMap<String,Double>();
		
		String[] Tokens = aQuery.GetQueryContent().split(" ");
		QueryRetrievalModel model = new QueryRetrievalModel(ixreader);
		List<Document> relevantDoc = model.retrieveQuery(aQuery, TopK);
		
		for(String word : Tokens){
			if(word == null || word.length() == 0) continue;
			
			HashSet<Integer> docID = new HashSet<>();
			
			int docLen = 0;
			for(int i = 0; i < relevantDoc.size(); i++){
				int id = Integer.parseInt(relevantDoc.get(i).docid());
				docID.add(id);
				docLen += ixreader.docLength(id);
			}
			
			int freq = 0;
			
			int[][] posting = ixreader.getPostingList(word);
			if (posting == null || posting.length == 0) continue;
			
			for(int k = 0; k < posting.length; k++){
				if(docID.contains((posting[k][0]))){
					freq += posting[k][1];
				}
			}
			
			long collectionFreq = ixreader.CollectionFreq(word);
			double p = (freq + mu * collectionFreq / collectionNum) / (docLen + mu);
			TokenRFScore.put(word, p);
		}

		
		return TokenRFScore;
	}
	
	
}