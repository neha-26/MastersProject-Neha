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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.builder.GraphTypeBuilder;
import org.jgrapht.util.SupplierUtil;
import org.json.JSONObject;

public class EdgeWeightKcore1 {
	
	static Graph<String, DefaultWeightedEdge> graph = null;
	static DecimalFormat df = new DecimalFormat("#####.######");
	static final double VERTEX_PERCENTAGE = 0.4;
	static final double WEIGHT_THRESHOLD = 0.3;
	static Map<String, Integer> score = null;
	static int totalVertice = 0;
	static int K_start = 1;
	static int K_increment = 1;
	static final int MIN_SCC_SIZE = 100;
	static int Kcore = 0;
	static HashMap<String, Integer> scoreMap = null;
	static HashMap<Integer, Integer> rankMap = null;
	static MultiValuedMap<String, ArrayList<String>> mainMap = new ArrayListValuedHashMap<>();

	public static void main(String[] args) {
		Instant start = Instant.now();

		graph = GraphTypeBuilder
				// .undirected()
				.directed()
				.allowingMultipleEdges(true)
				.allowingSelfLoops(true)
				.vertexSupplier(SupplierUtil.createStringSupplier())
				//.edgeSupplier(SupplierUtil.createDefaultWeightedEdgeSupplier())
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

		// Delete edges
		Graph<String, DefaultWeightedEdge> subgraph = deleteEdge();
		ConnectivityInspector<String, DefaultWeightedEdge> ci = new ConnectivityInspector<>(subgraph);
		List<Set<String>> ls = ci.connectedSets();
		Set<String> setS = ls.get(0);
		Graph<String, DefaultWeightedEdge> sgraph = createSubGraph(setS);//from original graph
		
		Graph<String, DefaultWeightedEdge> subgraph2 = deleteNodes(sgraph);// delete node with k < 5

		int d = 1, i = 1;// for main parent node//bug
		
		/*parent.setName("SCC - "+i);
		parent.setSize(6000);
		parent.setDepth(d);*/
		
		createJSONData(subgraph2, d + 1, i + 1, Kcore, "1");
		
		Instant finish = Instant.now();
		long timeElapsed = Duration.between(start, finish).toMillis();//in millis
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
		
		int count = 0;
		Set<Integer> rSet = generateRandomNumber(totalVertex,random_vertex);
		
		//for (int i = 0; i < tenPercent; i++) {
		for (Integer randomVar:  rSet) {
			
			Object uObj = vertAry[randomVar];
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
				double w = graph.getEdgeWeight((DefaultWeightedEdge) ed);
				w += Double.valueOf(df.format(1.0/degree));
				graph.setEdgeWeight((DefaultWeightedEdge) ed, Double.valueOf(df.format(w)));
				
			}
		}//main random num for loop ends
		
		//write output in file
		 PrintWriter writer;
		 int edgeCount = 0; 
		 double weight[] = new double[graph.edgeSet().size()];
		 // double weight[] = new double[edgeCount];
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
			//int i = 0;
			List<DefaultWeightedEdge> edgeList = new ArrayList<DefaultWeightedEdge>();
		    for(DefaultWeightedEdge e : graph.edgeSet()) {
		    	double we = graph.getEdgeWeight(e);
		    	//if(we > (median - 0.08091)) {
		    	if(we > (WEIGHT_THRESHOLD)) {
		    		edgeList.add(e);
		    	}
		    	//i++;
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
    public static double findMedian(double weight[], int n) 
    { 
    	System.out.println(" Calculate median : ");
    	//System.out.println(" weight[] size : "+ weight.length);
    	//System.out.println(" n : "+ n);
    	double a[] = new double[n];
    	
    	//weight array > 0.0
		for(int i=0; i < weight.length; i++ ) {
			if(weight[i] > 0.0) {
				a[i] = weight[i];
			}
		}
		//System.out.println(" a[] size : "+ a.length);
		
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
	
	private static ArrayList<Graph<String, DefaultWeightedEdge>> createSubGraph(List<Set<String>> ls) {

		Graph<String, DefaultWeightedEdge> subgraph = null;
		ArrayList<Graph<String, DefaultWeightedEdge>> subGraphList = new ArrayList<Graph<String, DefaultWeightedEdge>>();

		for (Set<String> ss : ls) {
			subgraph = GraphTypeBuilder
					 //.undirected()
					.directed()
					.allowingMultipleEdges(true)
					.allowingSelfLoops(true)
					.vertexSupplier(SupplierUtil.createStringSupplier())
					.edgeSupplier(SupplierUtil.createDefaultWeightedEdgeSupplier()).buildGraph();
			
			if (ss.size() > MIN_SCC_SIZE) {
				for (Object se : ss) {
					// System.out.println("*********vertex : "+ String.valueOf(se));
					subgraph.addVertex(String.valueOf(se));

					Set<DefaultWeightedEdge> edg = graph.edgesOf(String.valueOf(se));
					for (Object s : edg) {
						// System.out.println(" s : "+ s);
						String u = graph.getEdgeSource((DefaultWeightedEdge) s);
						String v = graph.getEdgeTarget((DefaultWeightedEdge) s);

						if (ss.contains(u) && ss.contains(v) && subgraph.containsVertex(u)
								&& subgraph.containsVertex(v)) {
							subgraph.addEdge(u, v);
						}
					}
				}

				System.out.println("SIZE of vertex set of subgraph : " + subgraph.vertexSet().size());
				
				System.out.println("SIZE of edge set of subgraph : " + subgraph.edgeSet().size());
				// System.out.println(" edge set of subgraph : "+ subgraph.edgeSet());
				subGraphList.add(subgraph);
			} // if ends
			
		}
		System.out.println("***^^^^^^^^^^^^^^^^^^^^ SIZE of subGraphList : " + subGraphList.size());
		return subGraphList;
	}
	
	private static Graph<String, DefaultWeightedEdge> createSubGraph(Set<String> ss) {

		Graph<String, DefaultWeightedEdge> subgraph = null;

		subgraph = GraphTypeBuilder
				// .undirected()
				.directed().allowingMultipleEdges(true).allowingSelfLoops(true)
				.vertexSupplier(SupplierUtil.createStringSupplier())
				//.edgeSupplier(SupplierUtil.createDefaultEdgeSupplier()).
				.edgeClass(DefaultWeightedEdge.class)
				.weighted(true).buildGraph();

			for (Object se : ss) {
				// System.out.println("*********vertex : "+ String.valueOf(se));
				subgraph.addVertex(String.valueOf(se));

				Set<DefaultWeightedEdge> edg = graph.edgesOf(String.valueOf(se));
				for (Object s : edg) {
					// System.out.println(" s : "+ s);
					String u = graph.getEdgeSource((DefaultWeightedEdge) s);
					String v = graph.getEdgeTarget((DefaultWeightedEdge) s);

					if (ss.contains(u) && ss.contains(v) && subgraph.containsVertex(u) && subgraph.containsVertex(v)) {
						subgraph.addEdge(u, v);
					}
				}
			}

			//System.out.println("SIZE of vertex set of subgraph : " + subgraph.vertexSet().size());

			//System.out.println("SIZE of edge set of subgraph : " + subgraph.edgeSet().size());
			
		return subgraph;
	}
	
	private static Graph<String, DefaultWeightedEdge> deleteNodes(Graph<String, DefaultWeightedEdge> graph) {
		System.out.println("**Inside deleteNodes method start***");
		score = countDegree(graph);

		totalVertice = score.size();

		System.out.println("size of score map : " + score.size());
		// Set<DefaultWeightedEdge> s = null;
		int k = K_start, nodeDeleted = 0;
		while (nodeDeleted < 50) {
			System.out.println("-------K--------" + k);
			int count = 0;
			scoreMap = new HashMap<String, Integer>(score);
			System.out.println("New scoreMap size " + scoreMap.size());

			HashMap<String, Integer> temp = new HashMap<String, Integer>(scoreMap);
			for (String i : temp.keySet()) {
				if (scoreMap.get(i) <= k) {
					// System.out.println(" i :"+ i);
					count++;
					scoreMap.remove(i);

					graph.removeVertex(i);

				}
			}

			System.out.println("No. of Removed nodes : " + count);
			System.out.println("scoreMap size " + scoreMap.size());

			score = countDegree(graph);

			System.out.println("scoreAfterRemoval size " + score.size());

			nodeDeleted = calculateNodeDeletedPerc(score.size());
			k++;
		}
		Kcore = --k;
		System.out.println("**Inside deleteNodes method end***");
		return graph;
	}
	
	private static void createJSONData(Graph<String, DefaultWeightedEdge> graph, int d, int i,int k, String parentP) {
		System.out.println();
		System.out.println("--------------------NEW CALL createJSONData "+ i+"-------------------");
		
		// children nodes
		List<Set<String>> ls = null;
		ArrayList<Graph<String, DefaultWeightedEdge>> subgrapghList = null;

			boolean b = deleteNodesInSubgraph(graph,k + K_increment);// delete node with degree < k
			System.out.println(" Boolean : "+ b);
			int counter = 1;
			if (b) { // need some condition
				System.out.println(" INSIDE - ");
				ls = getConnectedComponentsList(graph);// find all SCC
				subgrapghList = createSubGraph(ls);// get sub-graph for all SCC.size > 50

				
				ArrayList<String> al;
				//int counter = 1;//working here
				if (subgrapghList.size() > 0) {
					
					System.out.println("Size of list : "+subgrapghList.size());
					ArrayList<Parent> childArray = new ArrayList<Parent>();
					 al = new ArrayList<String>();
					
					for (Graph<String, DefaultWeightedEdge> g : subgrapghList) {

						
						al = new ArrayList<String>();
						//al.add(String.valueOf(i)+ "*");
						al.add(String.valueOf(i)+ "_" +counter);
						String parent = String.valueOf(i)+ "_" +counter;
						counter++;
						//mainMap.put(i-1+"_"+counter++, al);
						// mainMap.put(i-1+"", al);
						 mainMap.put(parentP, al);
						System.out.println("!!!!!!! main Map : "+ mainMap.toString());
						createJSONData(g, d + 1, i + 1,k + K_increment,parent);
						
					}
					
				} else {
					// find leaf nodes
					al = new ArrayList<String>();
					//System.out.println(" left joChildInner data : "+ joChildInner.toString());
					System.out.println("!!!BEFORE !!!! else !! main Map : "+ mainMap.toString());
					System.out.println(" @@@ SIZE of node of graph : "+ graph.vertexSet().size());
					if(graph.vertexSet().size() >0) {
					
						for (String node : graph.vertexSet()) {
							al.add(node);
						}
						//mainMap.put(i-1+"_"+ counter++, al);
						//mainMap.put(i-1+"_", al);
						//mainMap.put(i-1+"", al);
						mainMap.put(parentP, al);
					}
					
					//mainMap.put(i-1+"_"+ ++counter, al);
					//mainMap.put(i-1+"_", al);
					System.out.println("!!!!!!! else !! main Map : "+ mainMap.toString());
					
				}

			}else {
				//FOR 0 node deleted 
				ArrayList<String> al = new ArrayList<String>();
				 mainMap.put(parentP, al);
			}
			//working here
			
		}
	
private static List<Set<String>> getConnectedComponentsList(Graph<String, DefaultWeightedEdge> graph2) {
		
		ConnectivityInspector<String, DefaultWeightedEdge> ci = new ConnectivityInspector<>(graph2);

		System.out.println("isConnected : " + ci.isConnected());
		List<Set<String>> ls = ci.connectedSets();
		System.out.println(" list size : " + ls.size());

		// Rank List based on set size
		Collections.sort(ls, new Comparator<Set<?>>() {
			@Override
			public int compare(Set<?> o1, Set<?> o2) {
				return Integer.valueOf(o2.size()).compareTo(o1.size());
			}
		});
		// System.out.println(ls);

		// create map with key as index and value as size
		rankMap = new HashMap<Integer, Integer>();
		System.out.println("Rank Size");
		for (int i = 0; i < ls.size(); i++) {
			
			if (ls.get(i).size() > MIN_SCC_SIZE) {

				System.out.println(i + 1 + " " + ls.get(i).size());
				rankMap.put(i + 1, ls.get(i).size());
			}

			//rankMap.put(i + 1, ls.get(i).size());
		}
		
		  PrintWriter writer;
			try {
				writer = new PrintWriter("C:\\2019-Fall\\GA work\\output.txt", "UTF-8");
				writer.println("-----------<<<<<<<<<<<getConnectedComponentsList>>>>>>>>>>>>>>>>>>>----------");
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
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			

		return ls;
	}

	// private static Graph<String, DefaultWeightedEdge> deleteNodesInSubgraph(Graph<String,
	// DefaultWeightedEdge> sgraph) {
	private static boolean deleteNodesInSubgraph(Graph<String, DefaultWeightedEdge> sgraph, int k) {
		System.out.println("**Inside deleteNodesInSubgraph method***");
		//System.out.println("*SUBGRAPH** BEFORE vertex set of sgraph : " + sgraph.vertexSet().size());
		//System.out.println("*SUBGRAPH** BEFORE edge set of sgraph : " + sgraph.edgeSet().size());

		Graph<String, DefaultWeightedEdge> graph = sgraph;
		score = countDegree(graph);

		// totalVertice = score.size();

		System.out.println("size of score map : " + score.size());
		// Set<DefaultWeightedEdge> s = null;
		//int k = Kcore; // nodeDeleted = 0;
		// while(nodeDeleted < 50) {
		System.out.println("-------K--------" + k);
		int count = 0;
		scoreMap = new HashMap<String, Integer>(score);
		// System.out.println("New scoreMap size "+ scoreMap.size());

		HashMap<String, Integer> temp = new HashMap<String, Integer>(scoreMap);
		for (String i : temp.keySet()) {
			if (scoreMap.get(i) <= k) {
				// System.out.println(" i :"+ i);
				count++;
				scoreMap.remove(i);

				graph.removeVertex(i);

			}
		}

		System.out.println("No. of Removed nodes : " + count);
		System.out.println("scoreMap size after removal of node: " + scoreMap.size());

		
		//System.out.println("*SUBGRAPH** AFTER vertex set of sgraph original : " + sgraph.vertexSet().size());
		//System.out.println("*SUBGRAPH** AFTER vertex set of graph : " + graph.vertexSet().size());
		//System.out.println("*SUBGRAPH** AFTER edge set of graph : " + graph.edgeSet().size());

		//Kcore = k + K_increment;
		// return graph;
		if (count > 0 && count != score.size())
			return true;
		else 
			return false;
		
	}
	
	private static Map<String, Integer> countDegree(Graph<String, DefaultWeightedEdge> graph) {
		Set<String> vertSet = graph.vertexSet();
		HashMap<String, Integer> temp = new HashMap<String, Integer>();
		for (String s : vertSet) {

			temp.put(s, graph.outDegreeOf(s));
			// temp.put(s, graph.degreeOf(s));

		}

		return temp;
	}

	private static int calculateNodeDeletedPerc(int size) {

		int rem = ((totalVertice - size) * 100) / totalVertice;
		System.out.println(" % deleted " + rem);
		return rem;
	}
}
