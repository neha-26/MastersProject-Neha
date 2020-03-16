package org.myproj.masterProj;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.connectivity.GabowStrongConnectivityInspector;
import org.jgrapht.alg.connectivity.KosarajuStrongConnectivityInspector;
import org.jgrapht.alg.scoring.Coreness;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.builder.GraphTypeBuilder;
import org.jgrapht.util.SupplierUtil;
import org.json.JSONArray;
import org.json.JSONObject;

public class KDegree_2 {
	static Map<String, Integer> score = null;
	static int totalVertice = 0;
	static int K_start = 5;
	static int K_increment = 6;
	static final int MIN_SCC_SIZE = 100;
	static int Kcore = 0;
	static Graph<String, DefaultEdge> graph = null;
	static HashMap<String, Integer> scoreMap = null;
	static HashMap<Integer, Integer> rankMap = null;
	
	static JSONObject jo = new JSONObject();
	//static List<Map<Integer, Object>> mainList = new ArrayList<Map<Integer, Object>>();
	static Parent parent = new Parent();

	public static void main(String[] args) {
		Instant start = Instant.now();

		graph = GraphTypeBuilder
				// .undirected()
				.directed()
				.allowingMultipleEdges(true)
				.allowingSelfLoops(true)
				.vertexSupplier(SupplierUtil.createStringSupplier())
				.edgeSupplier(SupplierUtil.createDefaultEdgeSupplier()).buildGraph();

		// if graph is directed the use .directed()
		// .allowingMultipleEdges(true)
		// .allowingSelfLoops(true)
		// if graph is undirected the use only .undirected()

		// File file = new File("C:\\2019-Fall\\GA work\\email-Enron.txt"); //directed graph
		// File file = new File("C:\\2019-Fall\\GA work\\Wiki-Vote.txt");
		// File file = new File("C:\\2019-Fall\\GA work\\com-amazon.ungraph.txt");
		//File file = new File("C:\\2019-Fall\\GA work\\com-dblp.ungraph.txt"); // undirected graph
		// File file = new File("C:\\2019-Fall\\GA work\\CA-AstroPh.txt"); //directed graph
		// File file = new File("C:\\2019-Fall\\GA work\\com-orkut.ungraph.txt");//memory error
		
		 File file = new File("C:\\2019-Fall\\GA work\\web-Google.txt\\web-Google.txt");//directed graph

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
			e1.printStackTrace();
		}

		// Parent node
		Graph<String, DefaultEdge> subgraph = deleteNodes(graph);// delete node with k < 5

		int d = 1, i = 1;// for main parent node//bug
		
		/*parent.setName("SCC - "+i);
		parent.setSize(6000);
		parent.setDepth(d);*/
		
		createJSONData(subgraph, d + 1, i + 1, Kcore, "1");
		jo.put("name", "SCC - " + i);
		jo.put("size", 6000);
		jo.put("depth", d);
		// createJSONData(subgraph,d,i);

		System.out.println(" --main Map : "+ mainMap.toString());
		createJSONFromMap();
		
		// System.out.println(" FINAL JSON : "+ jo.toString());
		PrintWriter writer;
		try {
			writer = new PrintWriter("C:\\2019-Fall\\GA work\\outputJSON.txt", "UTF-8");
			writer.println(jo.toString());

			writer.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
	}
	private static void createGroupFromMap() {
		
		
		/*
		 * for (Integer key : mainMap.keySet()) {
		 * 
		 * }
		 */
		
	}

	private static void createJSONFromMap() {
		//main Map : {1=[[2]], 2=[[3]], 3=[[4]], 4=[[5]], 5=[[6]], 6=[[7]], 7=[[8], [8]],
		//8=[[9], [170035, 109015, 125859]], 9=[[10]], 10=[[11]], 11=[[12], [12]]}
		 //depth = parent + 1;
		//size = static
		 int d = 1;
		 int size_decrement = 500;
		 JSONObject jsonP = new JSONObject();
		 JSONArray jsonTemp = new JSONArray();
		 JSONObject jsonMTemp = new JSONObject();
		 JSONObject jsonChild = null;
		 JSONArray ja = new JSONArray();
		 
		 for (String key : mainMap.keySet()) {
			 System.out.println("Key = " + key);
		     System.out.println("   Values = " + mainMap.get(key));
		     
		     jsonP = new JSONObject();
		     jsonP.put("SCC - "+ key, key);
		     jsonP.put("name", key);//"SCC - "+ key
		     jsonP.put("size", 6500 - size_decrement);//increment if parent is same /if depth is same
		     jsonP.put("depth", d++);
		     
		     ja = new JSONArray();
		     for(ArrayList<String> aList : mainMap.get(key)){
		    	 jsonChild = new JSONObject();
		    	 jsonChild.put("SCC - "+ key, key);
			     jsonChild.put("name", aList);//"SCC - "+ aList
			     jsonChild.put("size", 6500 - size_decrement);//increment if parent is same /if depth is same
			     jsonChild.put("depth", d);
			     ja.put(jsonChild);
			     
			     jsonTemp = ja;
			     
		     }
		     jsonP.put("children", ja);
		     System.out.println("jsonP : "+ jsonP.toString());
		     
		     if(!jsonMTemp.isNull("SCC - "+"1")) {
		    	 //jsonMTemp.getJSONArray(key);
		    	 System.out.println("temp : "+ jsonMTemp.getJSONObject("SCC - 1").toString());
		     }
		     
		     jsonMTemp = jsonP;
		 } 
		
	}

	static JSONObject joChild = new JSONObject();
	static JSONObject joChild1 = new JSONObject();
	static JSONArray ja = new JSONArray();
	//static HashMap<Integer,Object> mainMap = new HashMap<Integer, Object>();
	static ArrayList<Parent> children = new ArrayList<Parent>();
	//static Parent children = new Parent();
	static Parent child = new Parent();
	//static HashMap<Integer, ArrayList<Integer>> mainMap = new HashMap<>();
	//static MultiValuedMap<Integer, ArrayList<Integer>> mainMap = new ArrayListValuedHashMap<>();
	static MultiValuedMap<String, ArrayList<String>> mainMap = new ArrayListValuedHashMap<>();
	
	
	private static void createJSONData(Graph<String, DefaultEdge> graph, int d, int i,int k, String parentP) {
		System.out.println();
		System.out.println("--------------------NEW CALL createJSONData "+ i+"-------------------");
		
		// children nodes
		List<Set<String>> ls = null;
		ArrayList<Graph<String, DefaultEdge>> subgrapghList = null;
		JSONObject joChildInner = new JSONObject();
		Parent innerParent = new Parent();
		//String parent = i-1+"";

			boolean b = deleteNodesInSubgraph(graph,k + K_increment);// delete node with degree < k
			System.out.println(" Boolean : "+ b);
			int counter = 1;
			if (b) { // need some condition
				System.out.println(" INSIDE - ");
				ls = getConnectedComponentsList(graph);// find all SCC
				subgrapghList = createSubGraph(ls);// get sub-graph for all SCC.size > 50

				joChildInner = joChild;
				joChild = new JSONObject();// working here
				
				ArrayList<String> al;
				//int counter = 1;//working here
				if (subgrapghList.size() > 0) {
					
					System.out.println("Size of list : "+subgrapghList.size());
					ArrayList<Parent> childArray = new ArrayList<Parent>();
					 al = new ArrayList<String>();
					
					for (Graph<String, DefaultEdge> g : subgrapghList) {

						System.out.println(" Writing JSON data -"+ i);
						
						joChild.put("name", "SCC - "+ i);
						joChild.put("size", "3000");
						joChild.put("depth", d);
						joChildInner.put("children", joChild);
						//System.out.println("### joChildInner data : "+ joChildInner.toString());
						
						//new code
						/*child = new Parent();
						child.setName("SCC - "+ i);
						child.setSize(3000);
						child.setDepth(d);
						childArray.add(child);
						ArrayList<Parent> p = parent2.getChildren();
					    
					    
					    //System.out.println("Parent : "+ parent2.toString());
					    //find proper parent to insert current children
					    //Parent [name=SCC - 1, depth=1, size=6000, children=[Parent [name=SCC - 2, depth=2, size=3000, children=null]]]
						parent2.setChildren(childArray); */
						
						/*al.add(i);//working for normal map
						mainMap.put(i-1, al); */
						/*al = new ArrayList<Integer>();
						al.add(i); */
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
							joChild1 = new JSONObject();
							joChild1.put("name", node);
							joChild1.put("size", 1000);
							joChild1.put("depth", d);
							ja.put(joChild1);
							
							//new code
							child = new Parent();
							child.setName("SCC - "+ i);
							child.setSize(3000);
							child.setDepth(d);
							children.add(child);
							
							//working for normal map
							/* al.add(Integer.parseInt(node));
							mainMap.put(i-1, al); */
							//mainMap.put(i-1, Integer.parseInt(node));
							//working
							//al.add(Integer.parseInt(node));
							al.add(node);
						}
						//mainMap.put(i-1+"_"+ counter++, al);
						//mainMap.put(i-1+"_", al);
						//mainMap.put(i-1+"", al);
						mainMap.put(parentP, al);
					}
					
					joChildInner.put("children", ja); // working here
					innerParent.setChildren(children);
					
					//mainMap.put(i-1+"_"+ ++counter, al);
					//mainMap.put(i-1+"_", al);
					System.out.println("!!!!!!! else !! main Map : "+ mainMap.toString());
					
				}

			}else {
				//FOR 0 node deleted 
				System.out.println("%%%% else -left joChildInner data : "+ joChildInner.toString());
				System.out.println(" i :"+ i);
				//System.out.println(" counter :"+ counter);
				ArrayList<String> al = new ArrayList<String>();
				 mainMap.put(parentP, al);
			}
			//working here
			jo = joChildInner;
			//System.out.println("$$$ JO : "+ jo.toString());
			
		}
	 	

	private static ArrayList<Graph<String, DefaultEdge>> createSubGraph(List<Set<String>> ls) {

		Graph<String, DefaultEdge> subgraph = null;
		ArrayList<Graph<String, DefaultEdge>> subGraphList = new ArrayList<Graph<String, DefaultEdge>>();

		for (Set<String> ss : ls) {
			subgraph = GraphTypeBuilder
					 //.undirected()
					.directed()
					.allowingMultipleEdges(true)
					.allowingSelfLoops(true)
					.vertexSupplier(SupplierUtil.createStringSupplier())
					.edgeSupplier(SupplierUtil.createDefaultEdgeSupplier()).buildGraph();
			
			if (ss.size() > MIN_SCC_SIZE) {
				for (Object se : ss) {
					// System.out.println("*********vertex : "+ String.valueOf(se));
					subgraph.addVertex(String.valueOf(se));

					Set<DefaultEdge> edg = graph.edgesOf(String.valueOf(se));
					for (Object s : edg) {
						// System.out.println(" s : "+ s);
						String u = graph.getEdgeSource((DefaultEdge) s);
						String v = graph.getEdgeTarget((DefaultEdge) s);

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

	private static List<Set<String>> getConnectedComponentsList(Graph<String, DefaultEdge> graph2) {
		
		ConnectivityInspector<String, DefaultEdge> ci = new ConnectivityInspector<>(graph2);

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

	// private static Graph<String, DefaultEdge> deleteNodesInSubgraph(Graph<String,
	// DefaultEdge> sgraph) {
	private static boolean deleteNodesInSubgraph(Graph<String, DefaultEdge> sgraph, int k) {
		System.out.println("**Inside deleteNodesInSubgraph method***");
		//System.out.println("*SUBGRAPH** BEFORE vertex set of sgraph : " + sgraph.vertexSet().size());
		//System.out.println("*SUBGRAPH** BEFORE edge set of sgraph : " + sgraph.edgeSet().size());

		Graph<String, DefaultEdge> graph = sgraph;
		score = countDegree(graph);

		// totalVertice = score.size();

		System.out.println("size of score map : " + score.size());
		// Set<DefaultEdge> s = null;
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

	private static Graph<String, DefaultEdge> deleteNodes(Graph<String, DefaultEdge> graph) {
		System.out.println("**Inside deleteNodes method start***");
		score = countDegree(graph);

		totalVertice = score.size();

		System.out.println("size of score map : " + score.size());
		// Set<DefaultEdge> s = null;
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

	private static Map<String, Integer> countDegree(Graph<String, DefaultEdge> graph) {
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
