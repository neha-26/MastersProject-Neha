package org.myproj.masterProj;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.scoring.Coreness;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.builder.GraphTypeBuilder;
import org.jgrapht.util.SupplierUtil;

public class KDegree_2 {
	static Map<String,Integer> score = null;
	static int totalVertice = 0;
	static Graph<String, DefaultEdge> graph = null;
	static HashMap<String, Integer> scoreMap = null;
	public static void main(String[] args) {
		Instant start = Instant.now();
		
		 graph = GraphTypeBuilder
				//.undirected()
				.directed()
				.allowingMultipleEdges(true)
				.allowingSelfLoops(true)
				.vertexSupplier(SupplierUtil.createStringSupplier())
				.edgeSupplier(SupplierUtil.createDefaultEdgeSupplier())
				.buildGraph();

		 //if graph is directed the use .directed()
			//.allowingMultipleEdges(true)
			//.allowingSelfLoops(true)
		 //if graph is undirected the use only .undirected()
		 
		//File file = new File("C:\\2019-Fall\\GA work\\email-Enron.txt"); 
		//File file = new File("C:\\2019-Fall\\GA work\\Wiki-Vote.txt"); 
		//File file = new File("C:\\2019-Fall\\GA work\\com-amazon.ungraph.txt"); 
		//File file = new File("C:\\2019-Fall\\GA work\\com-dblp.ungraph.txt"); 
	   	  //File file = new File("C:\\2019-Fall\\GA work\\CA-AstroPh.txt"); //directed graph
		//File file = new File("C:\\2019-Fall\\GA work\\com-orkut.ungraph.txt"); //memory error
	   	  File file = new File("C:\\2019-Fall\\GA work\\web-Google.txt\\web-Google.txt");
		 
			      
		Scanner sc;
		try {
			sc = new Scanner(file);
			while (sc.hasNextLine()) {
			      String[] s = sc.nextLine().split("\\s+");
			      graph.addVertex(s[0]);
			      graph.addVertex(s[1]);
			      graph.addEdge(s[0], s[1]);
			      
		}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
			  

		score = countDegree(graph);
		
		totalVertice = score.size();
		
		System.out.println("size of score map : "+ score.size());
//		System.out.println(" begin with out degree 1769: "+ graph.outDegreeOf("1769"));
//		System.out.println(" begin with IN degree 1769: "+ graph.inDegreeOf("1769"));
//		System.out.println(" begin with IN degree 4971: "+ graph.inDegreeOf("4971"));
        
		graph = deleteNodes();
		
		//score = countDegree(graph);
    	//System.out.println("scoreAfterRemoval size "+ score.size());
    	//graph = deleteNodes();
    	
		
	    Instant finish = Instant.now();
	    long timeElapsed = Duration.between(start, finish).toMillis();  //in millis
	    System.out.println(" *****total time in millis:"+ timeElapsed);
	    
	    ConnectivityInspector ci = new ConnectivityInspector(graph);
	    
	    System.out.println("isConnected : "+ ci.isConnected());
	    List<Set<String>> ls =  ci.connectedSets();
	    System.out.println(" list size : "+ls.size());
	    
	    PrintWriter writer;
		try {
			writer = new PrintWriter("C:\\2019-Fall\\GA work\\output.txt", "UTF-8");
			
				
			    for (Set ss: ls) {
			    	//writer.println("--------------------------------------------");
			    	writer.println(" ********** New SSC BEGINS *******");
			    	for(Object se : ss) {
			    		//System.out.println(" vertex : "+ String.valueOf(se));
			    		writer.print(String.valueOf(se) + ",  ");
			    	}
			    	//System.out.println("---------------------");
			    	writer.println(" ");
			    }
			    writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   
	}

	private static Graph deleteNodes() {
		Set<DefaultEdge> s = null;
		   int k = 1, nodeDeleted = 0;
		    while(nodeDeleted < 49) {
			    System.out.println("-------K--------"+ k);
	        	int count = 0;
	        	scoreMap = new HashMap<String, Integer>(score);
	        	System.out.println("New scoreMap size "+ scoreMap.size());
	        	
	        	HashMap<String, Integer> temp = new HashMap<String, Integer>(scoreMap);
	        	for (String i : temp.keySet()) {
	        		if (scoreMap.get(i) <= k) {
	        			//System.out.println(" i :"+ i);
	        			count++;
	        			scoreMap.remove(i);
	        			
	        			graph.removeVertex(i);
	        			
	        		}
	        	}
	  	
	        	System.out.println("count : "+ count);
	        	System.out.println("scoreMap size "+ scoreMap.size());
	    		
	        	score = countDegree(graph);
	        	
	        	System.out.println("scoreAfterRemoval size "+ score.size());
	        	
	        	//twice
//	        	System.out.println("******twice*******");
//	        	 count = 0;
//	        	scoreMap = new HashMap<String, Integer>(score);
//	        	System.out.println("New scoreMap size "+ scoreMap.size());
//	        	
//	        	HashMap<String, Integer> temp1 = new HashMap<String, Integer>(scoreMap);
//	        	for (String i : temp1.keySet()) {
//	        		if (scoreMap.get(i) == k) {
//	        			//System.out.println(" i :"+ i);
//	        			count++;
//	        			scoreMap.remove(i);
//	        			graph.removeVertex(i);
//	        		}
//	        	}
//	        	System.out.println("count : "+ count);
//	        	System.out.println("scoreMap size "+ scoreMap.size());
//	        	
//	        	score = countDegree(graph);
//	        	
//	        	System.out.println("scoreAfterRemoval size "+ score.size());
	        	//end
	           
	            nodeDeleted = calculateNodeDeletedPerc(score.size());
	            k++;
	        }
		    
		return graph;
	}

	private static Map<String, Integer> countDegree(Graph graph) {
		 Set<String> vertSet = graph.vertexSet(); 
		 HashMap<String, Integer> temp = new HashMap<String, Integer>();
		 for(String s : vertSet) {
			 
			temp.put(s, graph.outDegreeOf(s));
			 
		 }
		
		return temp;
	}

	private static int calculateNodeDeletedPerc(int size) {
		
		int rem = ((totalVertice - size)*100)/totalVertice;
		System.out.println(" % deleted "+ rem);
		return rem;
	}

}
