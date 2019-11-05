package org.myproj.masterProj;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.alg.scoring.Coreness;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.builder.GraphTypeBuilder;
import org.jgrapht.util.SupplierUtil;

public class KDegree {
	static Map<String,Integer> score = null;
	static int totalVertice = 0;
	public static void main(String[] args) {
		Instant start = Instant.now();
		
		Graph<String, DefaultEdge> graph = GraphTypeBuilder
				.undirected()
				//.allowingMultipleEdges(true)
				//.allowingSelfLoops(true)
				.vertexSupplier(SupplierUtil.createStringSupplier())
				.edgeSupplier(SupplierUtil.createDefaultEdgeSupplier())
				.buildGraph();

		//File file = new File("C:\\2019-Fall\\GA work\\email-Enron.txt"); 
		File file = new File("C:\\2019-Fall\\GA work\\Wiki-Vote.txt"); 
			      
		Scanner sc;
		try {
			sc = new Scanner(file);
			while (sc.hasNextLine()) {
			      //System.out.println(sc.nextLine()); 
			      String[] s = sc.nextLine().split("\\s+");
//			      System.out.print(""+s[0]);
//			      System.out.print(" - "+s[1]);
//			      System.out.println("");
			      graph.addVertex(s[0]);
			      graph.addVertex(s[1]);
			      graph.addEdge(s[0], s[1]);
			      
		}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
			  
//		for (String v : graph.vertexSet()) {
//			//System.out.println("vertex: " + v);
//		}

//		for (DefaultEdge e : graph.edgeSet()) {
//			//System.out.println("edge: " + e);
//		}
		
		Coreness c = new Coreness(graph);
		score = c.getScores();
		totalVertice = score.size();
		//HashMap<String, Integer> scoreMap = new HashMap<String, Integer>(score);
		HashMap<String, Integer> scoreMap = null;
		
		System.out.println("size of score map : "+ score.size());
		int ch = c.getVertexScore("1769");
		System.out.println(" begin with vertex 1769 : "+ ch);
		System.out.println(" begin with out degree 1769: "+ graph.outDegreeOf("1769"));
		System.out.println(" begin with IN degree 1769: "+ graph.inDegreeOf("1769"));
		//int i = c.getDegeneracy();
		//System.out.println(" i "+ i);
      //  for (Map.Entry<String,Integer> entry : score.entrySet())  {
          //  System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue()); 
     //   }
        
       
        
       
//        	for (Map.Entry<String,Integer> entry : score.entrySet())  {
//                //  System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue()); 
//        		if(entry.getValue() == k) {
//        			count++;
//        			((Set<String>) entry).remove(entry.getKey());
//        		}
//            }
//        	for(Iterator<String> iterator = score.keySet().iterator(); iterator.hasNext(); ) {
//        		  String key = iterator.next();
//        		  if(score.get(key) == k) {
//        			count++;  
//        		    iterator.remove();
//        		  }
//        	}
//        	for (Iterator<Map.Entry<String, Integer>> i= score.entrySet().iterator(); i.hasNext(); ) {
//  			  Map.Entry<String, Integer> entry= i.next();
//  			  
//  			  if (entry.getValue() == 1) {
//  			      i.remove();
//  			  }
//        	}
		
       // for(int k = 1; k<=2; k++) {
	   int k = 1, nodeDeleted = 0;
	    while(nodeDeleted < 45) {
		    System.out.println("-------K--------"+ k);
        	int count = 0;
        	scoreMap = new HashMap<String, Integer>(score);
        	System.out.println("New scoreMap size "+ scoreMap.size());
        	
        	HashMap<String, Integer> temp = new HashMap<String, Integer>(scoreMap);
        	for (String i : temp.keySet()) {
        		if (scoreMap.get(i) == k) {
        			//System.out.println(" i :"+ i);
        			count++;
        			scoreMap.remove(i);
        			
        			graph.removeAllEdges(graph.edgesOf(i));
        			graph.removeVertex(i);
        		}
        	}
  	
        	System.out.println("count : "+ count);
        	System.out.println("scoreMap size "+ scoreMap.size());
        	
        	int chh = c.getVertexScore("1769");
    		System.out.println("vertex 1769 : "+ chh);
    		
    		System.out.println(" out degree 1769: "+ graph.outDegreeOf("1769"));
        	
            c = new Coreness(graph);
        	score = c.getScores();
        	//*****************
        	
        	
        	
        	//******************
        	
        	System.out.println("scoreAfterRemoval size "+ score.size());
            //for (Map.Entry<String,Integer> entry : score.entrySet())  {
            // System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue()); 
           // }
            nodeDeleted = calculateNodeDeletedPerc(score.size());
            k++;
        }
	    Instant finish = Instant.now();
	    
	    long timeElapsed = Duration.between(start, finish).toMillis();  //in millis
	    System.out.println(" *****total time in millis:"+ timeElapsed);
	}

	private static int calculateNodeDeletedPerc(int size) {
		
		int rem = ((totalVertice - size)*100)/totalVertice;
		System.out.println(" % deleted "+ rem);
		return rem;
	}

}
