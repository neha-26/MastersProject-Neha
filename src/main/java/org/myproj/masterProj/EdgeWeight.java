package org.myproj.masterProj;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.builder.GraphTypeBuilder;
import org.jgrapht.util.SupplierUtil;

public class EdgeWeight {
	
	static Graph<String, DefaultWeightedEdge> graph = null;
	static DecimalFormat df = new DecimalFormat("##.#####");
	static final double VERTEX_PERCENTAGE = 0.4;

	public static void main(String[] args) {
		Instant start = Instant.now();

		graph = GraphTypeBuilder
				// .undirected()
				.directed()
				.allowingMultipleEdges(true)
				.allowingSelfLoops(true)
				.vertexSupplier(SupplierUtil.createStringSupplier())
				//.edgeSupplier(SupplierUtil.createDefaultEdgeSupplier())
				.edgeClass(DefaultWeightedEdge.class)
				.weighted(true)
				.buildGraph();

		// if graph is directed the use .directed()
		// .allowingMultipleEdges(true)
		// .allowingSelfLoops(true)
		// if graph is undirected the use only .undirected()

		// File file = new File("C:\\2019-Fall\\GA work\\email-Enron.txt"); //directed graph
		// File file = new File("C:\\2019-Fall\\GA work\\Wiki-Vote.txt");
		 File file = new File("C:\\2019-Fall\\GA work\\com-amazon.ungraph.txt");
		//File file = new File("C:\\2019-Fall\\GA work\\com-dblp.ungraph.txt"); // undirected graph
		 //File file = new File("C:\\2019-Fall\\GA work\\CA-AstroPh.txt"); //directed graph
		// File file = new File("C:\\2019-Fall\\GA work\\com-orkut.ungraph.txt");//memory error
		
		 //File file = new File("C:\\2019-Fall\\GA work\\web-Google.txt\\web-Google.txt");//directed graph

		Scanner sc;
		try {
			sc = new Scanner(file);
			while (sc.hasNextLine()) {
				String[] s = sc.nextLine().split("\\s+");
				graph.addVertex(s[0]);
				graph.addVertex(s[1]);
				graph.addEdge(s[0], s[1]);
				graph.setEdgeWeight(s[0], s[1], 0.0);
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		// Parent node
		Graph<String, DefaultWeightedEdge> subgraph = deleteEdge();
		
		Instant finish = Instant.now();
		long timeElapsed = Duration.between(start, finish).toMillis();  //in millis
		System.out.println(" *****total time in millis:"+ timeElapsed);
	}

	private static Graph<String, DefaultWeightedEdge> deleteEdge() {
		
		Object[] vertAry =  graph.vertexSet().toArray();
		//System.out.println(" array size : "+ vertAry.length);
		boolean visited[] = new boolean[vertAry.length]; 
		 
		int totalVertex  = graph.vertexSet().size();
		System.out.println(" Graph vertex : "+ totalVertex);
		int totalEdges = graph.edgeSet().size();
		System.out.println(" Graph edges : "+  totalEdges);
		int random_vertex = (int) (VERTEX_PERCENTAGE * totalVertex);
		System.out.println(" random_vertex size : "+ random_vertex);
		
	
		//double weight[] = new double[totalVertex];
		 double weight[] = new double[graph.edgeSet().size()];
		//for (int i = 0; i < totalVertex; i++) {
			//weight[i] = 0.0;
		//}
		
		//fetch index of all the vertex from the array
		//List<Object> ls = Arrays.asList(vertAry); // ls
		//int index[] = new int[totalVertex];
		/*
		 * for (int i = 0; i < totalVertex; i++) { index[i] = ls.indexOf(vertAry[i]); }
		 */
		
		int count = 0;
		Set<Integer> rSet = generateRandomNumber(totalVertex,random_vertex);
		
		//for (int i = 0; i < tenPercent; i++) {
		for (Integer randomVar:  rSet) {
			
			//System.out.println("----------------Random num : "+ i);
			// randomNumbers[randomVar])
			//int u = Integer.parseInt(vertAry[randomVar]);
			Object uObj = vertAry[randomVar];
			//System.out.println(" Current U : "+ uObj.toString());
			String u = uObj.toString();
			
			visited[randomVar]=true;//index is set as true
			
			int degree = graph.degreeOf(u);
			//System.out.println(" degree of "+ u + " : "+ degree);
			
			Set<DefaultWeightedEdge> edg = graph.edgesOf(u);
			for (Object ed : edg) {
				count++;
				//String u1 = graph.getEdgeSource((DefaultWeightedEdge) ed);
				//String v = graph.getEdgeTarget((DefaultWeightedEdge) ed);
				
				//System.out.println( u1 +" -> "+ v);
				
				//1st hop
				/*if(u == u1) { //consider only source vertex
					count++;
					int indexOfV = ls.indexOf(v);
					visited[indexOfV]=true;
					weight[indexOfV] +=  Double.valueOf(df.format(1.0/degree));
				}// 1st hop end
				*/
				double w = graph.getEdgeWeight((DefaultWeightedEdge) ed);
				//System.out.println(" w :"+w);
				w += Double.valueOf(df.format(1.0/degree));
				graph.setEdgeWeight((DefaultWeightedEdge) ed, Double.valueOf(df.format(w)));
				
				//2nd hop
				/*int degreeOfV = graph.degreeOf(v);
				Set<DefaultEdge> edgOfV = graph.edgesOf(v);
				
				for (Object eofV : edgOfV) {
					
					String sr = graph.getEdgeSource((DefaultEdge) eofV);
					String tr = graph.getEdgeTarget((DefaultEdge) eofV);
					int degreeOfTr = graph.degreeOf(tr);
					int indexOfTr = ls.indexOf(tr);
					
					if(sr.equals(v) && !tr.equals(u) && !visited[indexOfTr]) {
					//if(sr == v && (sr != u || tr != u)) {
					//if(!visited[indexOfV]) {
						visited[indexOfTr] = true;
						
						System.out.println( u1 +" -> "+ v);
						
						//if(v == sr) { //consider only source vertex
							weight[indexOfTr] += (1.0/degreeOfV) * (1.0/degreeOfTr);
						//}
					}
					
				}//2nd hop end
				*/
				
			}
		}//main random num for loop ends
		System.out.println(" count : "+ count);
		
		
		//write output in file
		 PrintWriter writer;
		 int edgeCount = 0;
			try {
				writer = new PrintWriter("C:\\2019-Fall\\GA work\\outputEdgeWeight.txt", "UTF-8");
					
				        int i = 0;
					    for(DefaultWeightedEdge e : graph.edgeSet()) {
					    	double we = graph.getEdgeWeight(e);
					    	//weight[i] = we;
					    	if(we > 0.0) {
					    		weight[i] = we;
					    		edgeCount++;
					    		 writer.println(e +" -> "+ we);
					    		 i++;
					    	}
					    	//i++;
					    }
				 writer.close();
				// edgeCount = i;
				 System.out.println(" size of weight array : "+weight.length);
				 System.out.println("New edgeCount : "+ edgeCount);
				 
				    
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		
			//find median of edge weight
			//double median = findMedian(weight, graph.edgeSet().size());
			double median = findMedian(weight, edgeCount);
			System.out.println("MEDIAN = " + median); 
			
			//delete edge from graph for w > threshold	
			int i = 0;
			List<DefaultWeightedEdge> edgeList = new ArrayList<DefaultWeightedEdge>();
		    for(DefaultWeightedEdge e : graph.edgeSet()) {
		    	double we = graph.getEdgeWeight(e);
		    	//if(we > (median - 0.08091)) {
		    	if(we > (0.3)) {
		    		edgeList.add(e);
		    	}
		    	i++;
		    }
		    System.out.println(" No. of egdes to be deleted : "+ edgeList.size());
		    
		    graph.removeAllEdges(edgeList);
		    System.out.println(" After edge deletion -> ");
		    System.out.println(" Graph vertex :"+ graph.vertexSet().size());
		    System.out.println(" Graph edges :"+ graph.edgeSet().size());
		    
		   // System.out.println(" Percentage of edges deleted : "+ (double)((edgeList.size()*100)/totalEdges) +"%");
		    System.out.println(" Percentage of edges deleted : "+ (double)((edgeList.size()*100)/edgeCount) +"%");
		    
		return graph;
	}
	
	// Function for calculating median 
    public static double findMedian(double a[], int n) 
    { 
        // First we sort the array 
        Arrays.sort(a); 
  
        // check for even case 
        if (n % 2 != 0) 
            return (double)a[n / 2]; 
  
        return  Double.valueOf(df.format((double)(a[(n - 1) / 2] + a[n / 2]) / 2.0)); 
    } 
    
	private static Set<Integer> generateRandomNumber(int NUMBER_RANGE, int random_vertex_size) {
		Random random = new Random();

        Set<Integer> set = new HashSet<Integer>(random_vertex_size);

        while(set.size()< random_vertex_size) {
            while (set.add(random.nextInt(NUMBER_RANGE)) != true);
        }
        return set;
	}
}
