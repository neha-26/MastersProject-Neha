package org.myproj.services;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.builder.GraphTypeBuilder;
import org.jgrapht.util.SupplierUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.myproj.model.FormSub;
import org.springframework.stereotype.Service;

@Service
public class GraphService {

	static DecimalFormat df = new DecimalFormat("#.######");
	static Map<String, Integer> score = null;
	static int totalVertice = 0;
	static Graph<String, DefaultEdge> graph = null;
	static Graph<String, DefaultWeightedEdge> graph1 = null;
	static Graph<String, DefaultWeightedEdge> graph1Copy = null;
	static HashMap<String, Integer> scoreMap = null;
	static HashMap<Integer, Integer> rankMap = null;
	static ArrayList<ArrayList<String>> groupArray = new ArrayList<ArrayList<String>>();
	/*
	 * static int K_start = 5; static int K_increment = 6; static int MIN_SCC_SIZE =
	 * 50; static double DELETED_NODE_PERCENT = 50.0;
	 */
	static int K_start;
	static int K_increment;
	static int MIN_SCC_SIZE;
	static double DELETED_NODE_PERCENT;
	static double VERTEX_PERCENTAGE;
	static double WEIGHT_THRESHOLD;

	static int Kcore, id, depth;
	static Map<Integer, Set<String>> sccMap = new HashMap<>();
	static Map<Integer, Integer> idKMap = new HashMap<>();
	static List<Integer> group = new ArrayList<>();;

	public JSONArray preprocess(byte[] fileData, FormSub form) {

		Instant start = Instant.now();

		graph = GraphTypeBuilder.directed().allowingMultipleEdges(true).allowingSelfLoops(true)
				.vertexSupplier(SupplierUtil.createStringSupplier())
				.edgeSupplier(SupplierUtil.createDefaultEdgeSupplier()).buildGraph();

		K_start = form.getK_start();
		K_increment = form.getK_increment();
		MIN_SCC_SIZE = form.getMIN_SCC_SIZE();
		DELETED_NODE_PERCENT = form.getDELETED_NODE_PERCENT();

		// byte[] fileData = form.getFile1();
		InputStream is = null;
		BufferedReader bfReader = null;
		try {
			is = new ByteArrayInputStream(fileData);
			bfReader = new BufferedReader(new InputStreamReader(is));
			String temp = null;
			while ((temp = bfReader.readLine()) != null) {
				// System.out.println(temp);
				String[] s = temp.split("\\s+");
				// System.out.println("s : "+s.length);
				// System.out.println("s[0] : "+s[0]);
				graph.addVertex(s[0]);
				graph.addVertex(s[1]);
				graph.addEdge(s[0], s[1]);

			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (Exception ex) {

			}
		}

		System.out.println("**BEFORE edge set of graph : " + graph.edgeSet().size());
		deleteNodes(graph);
		System.out.println("**AFTER edge set of graph : " + graph.edgeSet().size());

		List<Set<String>> ls = getConnectedComponentsList(graph);
		
		Instant finish = Instant.now();
		long timeElapsed = Duration.between(start, finish).toMillis(); // in millis
		System.out.println(" *****total time in millis:" + timeElapsed);

		// create jsonArray
		JSONArray jAry = new JSONArray();
		JSONObject joChild;
		depth = 1;
		id = 1;
		//idKMap.put(id, Kcore);

		for (Set<String> ss : ls) {
			
			//need no of edges in this subgraph
			Graph<String, DefaultEdge> subGraph = createSubGraph(ss);
			int edgeCount = subGraph.edgeSet().size();
			
			idKMap.put(id, Kcore);
			if (ss.size() > MIN_SCC_SIZE) {// check diff b/w first and second SCC
				joChild = new JSONObject();

				joChild.put("name", "SCC - " + id);
				joChild.put("size", 1000000);
				joChild.put("depth", depth);
				joChild.put("id", id);
				
				joChild.put("totalNodes", ss.size());
				joChild.put("totalEdges", edgeCount);
				joChild.put("density", density(edgeCount,ss.size()));

				jAry.put(joChild);
				sccMap.put(id, ss);
				id++;
			}

		}
		depth++;
		return jAry;
	}

	private static double density(int edgeCount, int vertexCount) {
		/*System.out.println("### Density:");
		System.out.println("edgeCount : "+ edgeCount);
		System.out.println("edgeCount * 1.0 : "+ (edgeCount * 1.0));
		System.out.println("vertexCount : "+ vertexCount);
		System.out.println("vertexCount square : "+ Math.pow(vertexCount, 2));*/
		double d = (edgeCount * 1.0)/(Math.pow(vertexCount, 2));
		
		return Double.valueOf(df.format(d));
	}


	public void setGroup(List<Integer> grp) {
		group = grp;
	}

	public Integer[] getGroup() {
		//System.out.println(" SIZE##@@@@ : " + group.size());
		Integer[] a = new Integer[group.size()];

		for (int i = 0; i < group.size(); i++) {
			a[i] = (Integer) group.get(i);
			// System.out.println(someString);
		}

		return a;
	}

	public JSONArray clickProcess(int nodeid) {
		//Instant start = Instant.now();
		
		Set<String> nodeList = sccMap.get(nodeid);
		Graph<String, DefaultEdge> subGraph = createSubGraph(nodeList);

		Kcore = idKMap.get(nodeid);
		System.out.println("**BEFORE edge set of subGraph : " + subGraph.edgeSet().size());
		deleteNodesInSubgraph(subGraph);
		System.out.println("**AFTER edge set of subGraph : " + subGraph.edgeSet().size());

		List<Set<String>> ls = getConnectedComponentsList(subGraph);

		// create jsonArray
		JSONArray jAry = new JSONArray();
		JSONObject joChild;
		// int d = 1, i = 0;

		List<Integer> grp = new ArrayList<>();
		grp.add(nodeid);

		for (Set<String> ss : ls) {
			// Set ss = ls.get(1);
			double percent = ((ss.size()) * 100.00) / totalVertice;
			int nodeSize = (int) ((percent / 100) * 2000000);
			
			Graph<String, DefaultEdge> subGraph1 = createSubGraph(ss);
			int edgeCount = subGraph1.edgeSet().size();

			idKMap.put(id, Kcore);// Kcore to be used next time this id is clicked
			if (ss.size() > MIN_SCC_SIZE) {// check diff b/w first and second SCC
				joChild = new JSONObject();

				joChild.put("name", "SCC - " + id);
				joChild.put("size", nodeSize);
				joChild.put("depth", depth);
				joChild.put("id", id);
				
				joChild.put("totalNodes", ss.size());
				joChild.put("totalEdges", edgeCount);
				joChild.put("density", density(edgeCount,ss.size()));

				jAry.put(joChild);
				sccMap.put(id, ss);
				// put k for each id
				grp.add(id);
				id++;

			}

		}
		depth++;

		group = grp;
		setGroup(grp);
		sccMap.remove(nodeid);
		return jAry;
	}

	private List<Set<String>> getConnectedComponentsList(Graph<String, DefaultEdge> graph2) {

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

		// create map with key as index and value as size
		rankMap = new HashMap<Integer, Integer>();
		System.out.println("Rank Size");
		for (int i = 0; i < ls.size(); i++) {

			if (ls.get(i).size() > MIN_SCC_SIZE) {

				System.out.println(i + 1 + " " + ls.get(i).size());
				rankMap.put(i + 1, ls.get(i).size());
			}

		}

		return ls;
	}

	private void deleteNodesInSubgraph(Graph<String, DefaultEdge> sgraph) {
		System.out.println("**Inside deleteNodesInSubgraph method***");
		// System.out.println("*SUBGRAPH** BEFORE vertex set of sgraph : " +
		// sgraph.vertexSet().size());
		// System.out.println("*SUBGRAPH** BEFORE edge set of sgraph : " +
		// sgraph.edgeSet().size());

		Graph<String, DefaultEdge> graph = sgraph;
		score = countDegree(graph);
		int k = Kcore + K_increment;

		// totalVertice = score.size();

		System.out.println("size of score map : " + score.size());
		// Set<DefaultEdge> s = null;
		// int k = Kcore; // nodeDeleted = 0;
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

		Kcore = k;
		// return graph;
		if(count == 0) {
			deleteNodesInSubgraph(sgraph);
		}

	}

	private static void deleteNodes(Graph<String, DefaultEdge> graph) {
		System.out.println("**Inside deleteNodes method start***");
		score = countDegree(graph);

		totalVertice = score.size();

		System.out.println("size of score map : " + score.size());
		// Set<DefaultEdge> s = null;
		int k = K_start;
		double nodeDeleted = 0.0;
		while (nodeDeleted < DELETED_NODE_PERCENT) {
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
		// return graph;
	}

	private static Graph<String, DefaultEdge> createSubGraph(Set<String> ss) {

		Graph<String, DefaultEdge> subgraph = null;

		// for (Set<String> ss : ls) {
		subgraph = GraphTypeBuilder
				.directed().allowingMultipleEdges(true).allowingSelfLoops(true)
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

					if (ss.contains(u) && ss.contains(v) && subgraph.containsVertex(u) && subgraph.containsVertex(v)) {
						subgraph.addEdge(u, v);
					}
				}
			}

			System.out.println("SIZE of vertex set of subgraph : " + subgraph.vertexSet().size());

			System.out.println("SIZE of edge set of subgraph : " + subgraph.edgeSet().size());
			// System.out.println(" edge set of subgraph : "+ subgraph.edgeSet());
		} // if ends

		// }
		return subgraph;
	}

	private static Map<String, Integer> countDegree(Graph<String, DefaultEdge> graph) {
		Set<String> vertSet = graph.vertexSet();
		HashMap<String, Integer> temp = new HashMap<String, Integer>();
		for (String s : vertSet) {

			// temp.put(s, graph.outDegreeOf(s));
			temp.put(s, graph.degreeOf(s));

		}

		return temp;
	}

	private static double calculateNodeDeletedPerc(int size) {

		double rem = ((totalVertice - size) * 100.00) / totalVertice;
		System.out.println(" % deleted " + rem);
		return rem;
	}



	public JSONArray preprocessEdge(byte[] fileData, FormSub form) {

		graph1 = GraphTypeBuilder
				.directed().allowingMultipleEdges(true).allowingSelfLoops(true)
				.vertexSupplier(SupplierUtil.createStringSupplier())
				.edgeClass(DefaultWeightedEdge.class).weighted(true).buildGraph();
		graph1Copy = GraphTypeBuilder
				.directed().allowingMultipleEdges(true).allowingSelfLoops(true)
				.vertexSupplier(SupplierUtil.createStringSupplier())
				.edgeClass(DefaultWeightedEdge.class).weighted(true).buildGraph();

		K_start = form.getK_start1();
		K_increment = form.getK_increment1();
		MIN_SCC_SIZE = form.getMIN_SCC_SIZE1();
		DELETED_NODE_PERCENT = form.getDELETED_NODE_PERCENT1();
		VERTEX_PERCENTAGE = form.getVERTEX_PERCENTAGE();
		WEIGHT_THRESHOLD = form.getWEIGHT_THRESHOLD();

		InputStream is = null;
		BufferedReader bfReader = null;
		try {
			is = new ByteArrayInputStream(fileData);
			bfReader = new BufferedReader(new InputStreamReader(is));
			String temp = null;
			while ((temp = bfReader.readLine()) != null) {
				String[] s = temp.split("\\s+");
				graph1.addVertex(s[0]);
				graph1.addVertex(s[1]);
				graph1.addEdge(s[0], s[1]);
				graph1.setEdgeWeight(s[0], s[1], 0.0);
				
				//copy
				graph1Copy.addVertex(s[0]);
				graph1Copy.addVertex(s[1]);
				graph1Copy.addEdge(s[0], s[1]);
				graph1Copy.setEdgeWeight(s[0], s[1], 0.0);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (Exception ex) {

			}
		}

		// Delete edges
		Graph<String, DefaultWeightedEdge> subgraph = deleteEdge(graph1);

		// check weakly connected component in original graph
		//ConnectivityInspector<String, DefaultWeightedEdge> ci = new ConnectivityInspector<>(subgraph);
		//List<Set<String>> ls = ci.connectedSets();

		// create jsonArray
		JSONArray jAry = new JSONArray();
		//for (Set<String> s : ls) {
			
		//	if (s.size() > MIN_SCC_SIZE) {//keep more

				//Graph<String, DefaultWeightedEdge> sgraph = createSubGraph1(s);// from original graph

				// kcore
				//deleteNodes1(sgraph);
				//List<Set<String>> lsedge = getConnectedComponentsList1(sgraph);
				deleteNodes1(subgraph);
				List<Set<String>> lsedge = getConnectedComponentsList1(subgraph);
				
				JSONObject joChild;
				depth = 1;
				id = 1;
				//idKMap.put(id, Kcore);

				for (Set<String> ss : lsedge) {
					// Set ss = ls.get(1);
					Graph<String, DefaultWeightedEdge> subGraph = createSubGraph1(ss);
					int edgeCount = subGraph.edgeSet().size();
					//System.out.println(" @@@ edgecount : "+ edgeCount);
					
					idKMap.put(id, Kcore);
					if (ss.size() > MIN_SCC_SIZE) {// check diff b/w first and second SCC
						joChild = new JSONObject();

						joChild.put("name", "SCC - " + id);
						joChild.put("size", 600000);//1000000
						joChild.put("depth", depth);
						joChild.put("id", id);
						
						joChild.put("totalNodes", ss.size());
						joChild.put("totalEdges", edgeCount);
						joChild.put("density", density(edgeCount,ss.size()));

						jAry.put(joChild);
						sccMap.put(id, ss);
						id++;
					}

				}
				depth++;

		//	} // if end
	//	}//first for commented
		
		return jAry;

	}

	private static Graph<String, DefaultWeightedEdge> deleteEdge(Graph<String, DefaultWeightedEdge> graph) {
		//Graph<String, DefaultWeightedEdge> graph = graph1;
		Object[] vertAry = graph.vertexSet().toArray();
		// System.out.println(" array size : "+ vertAry.length);
		boolean visited[] = new boolean[vertAry.length];

		int totalVertex = graph.vertexSet().size();
		System.out.println(" Graph vertex : " + totalVertex);
		int totalEdges = graph.edgeSet().size();
		System.out.println(" Graph edges : " + totalEdges);
		int random_vertex = (int) (VERTEX_PERCENTAGE * totalVertex);
		System.out.println(" random_vertex size : " + random_vertex);

		int count = 0;
		Set<Integer> rSet = generateRandomNumber(totalVertex, random_vertex);

		// for (int i = 0; i < tenPercent; i++) {
		for (Integer randomVar : rSet) {

			Object uObj = vertAry[randomVar];
			String u = uObj.toString();

			visited[randomVar] = true;// index is set as true

			int degree = graph.degreeOf(u);
			// System.out.println(" degree of "+ u + " : "+ degree);

			Set<DefaultWeightedEdge> edg = graph.edgesOf(u);
			for (Object ed : edg) {
				count++;
				// String u1 = graph.getEdgeSource((DefaultWeightedEdge) ed);
				// String v = graph.getEdgeTarget((DefaultWeightedEdge) ed);

				// System.out.println( u1 +" -> "+ v);

				// 1st hop
				double w = graph.getEdgeWeight((DefaultWeightedEdge) ed);
				w += Double.valueOf(df.format(1.0 / degree));
				graph.setEdgeWeight((DefaultWeightedEdge) ed, Double.valueOf(df.format(w)));

			}
		} // main random num for loop ends

		// write output in file
		PrintWriter writer;
		int edgeCount = 0;
		double weight[] = new double[graph.edgeSet().size()];
		
		int i = 0;
		for (DefaultWeightedEdge e : graph.edgeSet()) {
			double we = graph.getEdgeWeight(e);
			// weight[i] = we;
			if (we > 0.0) {
				weight[i] = we;
				edgeCount++;
				i++;
			}
			// i++;
		}
	//	System.out.println(" @@@ edgecount : "+ edgeCount);
		// find median of edge weight
		// double median = findMedian(weight, graph.edgeSet().size());
		double median = findMedian(weight, edgeCount);
		System.out.println("MEDIAN = " + median);

		// delete edge from graph for w > threshold
		// int i = 0;
		List<DefaultWeightedEdge> edgeList = new ArrayList<DefaultWeightedEdge>();
		for (DefaultWeightedEdge e : graph.edgeSet()) {
			double we = graph.getEdgeWeight(e);
			// if(we > (median - 0.08091)) {
			if (we > (WEIGHT_THRESHOLD)) {
				edgeList.add(e);
			}
			// i++;
		}
		System.out.println(" No. of egdes to be deleted : " + edgeList.size());

		graph.removeAllEdges(edgeList);
		System.out.println(" After edge deletion -> ");
		System.out.println(" 	Graph vertex :" + graph.vertexSet().size());
		System.out.println(" 	Graph edges :" + graph.edgeSet().size());

		// System.out.println(" Percentage of edges deleted : "+
		// (double)((edgeList.size()*100)/totalEdges) +"%");
		System.out.println(" Percentage of edges deleted : " + (double) ((edgeList.size() * 100) / edgeCount) + "%");

		return graph;
	}

	private static Graph<String, DefaultWeightedEdge> createSubGraph1(Set<String> ss) {

		Graph<String, DefaultWeightedEdge> subgraph = null;

		subgraph = GraphTypeBuilder
				.directed().allowingMultipleEdges(true).allowingSelfLoops(true)
				.vertexSupplier(SupplierUtil.createStringSupplier())
				.edgeClass(DefaultWeightedEdge.class).weighted(true).buildGraph();

		if (ss.size() > MIN_SCC_SIZE) {
			for (Object se : ss) {
				// System.out.println("*********vertex : "+ String.valueOf(se));
				subgraph.addVertex(String.valueOf(se));

				Set<DefaultWeightedEdge> edg = graph1Copy.edgesOf(String.valueOf(se));
				for (Object s : edg) {
					// System.out.println(" s : "+ s);
					String u = graph1Copy.getEdgeSource((DefaultWeightedEdge) s);
					String v = graph1Copy.getEdgeTarget((DefaultWeightedEdge) s);

					if (ss.contains(u) && ss.contains(v) && subgraph.containsVertex(u) && subgraph.containsVertex(v)) {
						subgraph.addEdge(u, v);
					}
				}
			}

			System.out.println("SIZE of vertex set of subgraph : " + subgraph.vertexSet().size());

			System.out.println("SIZE of edge set of subgraph : " + subgraph.edgeSet().size());
			// System.out.println(" edge set of subgraph : "+ subgraph.edgeSet());
		} // if ends

		// }
		return subgraph;
	}
	
	private static void deleteNodes1(Graph<String, DefaultWeightedEdge> graph) {
		System.out.println("**Inside deleteNodes method start***");
		score = countDegree1(graph);

		totalVertice = score.size();

		System.out.println("size of score map : " + score.size());
		// Set<DefaultEdge> s = null;
		int k = K_start;
		double nodeDeleted = 0.0;
		while (nodeDeleted < DELETED_NODE_PERCENT) {
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

			score = countDegree1(graph);

			System.out.println("scoreAfterRemoval size " + score.size());

			nodeDeleted = calculateNodeDeletedPerc(score.size());
			k++;
		}
		Kcore = --k;
		System.out.println("**Inside deleteNodes method end***");
		// return graph;
	}

	private List<Set<String>> getConnectedComponentsList1(Graph<String, DefaultWeightedEdge> graph2) {

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

		// create map with key as index and value as size
		rankMap = new HashMap<Integer, Integer>();
		System.out.println("Rank Size");
		for (int i = 0; i < ls.size(); i++) {

			if (ls.get(i).size() > MIN_SCC_SIZE) {

				System.out.println(i + 1 + " " + ls.get(i).size());
				rankMap.put(i + 1, ls.get(i).size());
			}

		}

		return ls;
	}
	
	// Function for calculating median
	public static double findMedian(double weight[], int n) {
		System.out.println(" Calculate median : ");
		 System.out.println(" weight[] size : "+ weight.length);
		 System.out.println(" n : "+ n);
		double a[] = new double[n];

		// weight array > 0.0
		for (int i = 0; i < weight.length; i++) {
			if (weight[i] > 0.0) {
				a[i] = weight[i];
			}
		}
		// System.out.println(" a[] size : "+ a.length);

		// First we sort the array
		Arrays.sort(a);

		// check for even case
		if (n % 2 != 0)
			return (double) a[n / 2];

		return Double.valueOf(df.format((double) (a[(n - 1) / 2] + a[n / 2]) / 2.0));
	}

	private static Set<Integer> generateRandomNumber(int NUMBER_RANGE, int random_vertex_size) {
		Random random = new Random();

		Set<Integer> set = new HashSet<Integer>(random_vertex_size);

		while (set.size() < random_vertex_size) {
			while (set.add(random.nextInt(NUMBER_RANGE)) != true)
				;
		}
		return set;
	}
	private static Map<String, Integer> countDegree1(Graph<String, DefaultWeightedEdge> graph) {
		Set<String> vertSet = graph.vertexSet();
		HashMap<String, Integer> temp = new HashMap<String, Integer>();
		for (String s : vertSet) {

			// temp.put(s, graph.outDegreeOf(s));
			temp.put(s, graph.degreeOf(s));

		}

		return temp;
	}

	/*private static double calculateNodeDeletedPerc(int size) {

		double rem = ((totalVertice - size) * 100.00) / totalVertice;
		System.out.println(" % deleted " + rem);
		return rem;
	}*/
	public JSONArray clickProcess1(int nodeid) {
		Set<String> nodeList = sccMap.get(nodeid);
		Graph<String, DefaultWeightedEdge> subGraph = createSubGraph1(nodeList);

		Kcore = idKMap.get(nodeid);
		System.out.println("**BEFORE edge set of subGraph : " + subGraph.edgeSet().size());
		deleteNodesInSubgraph1(subGraph);
		System.out.println("**AFTER edge set of subGraph : " + subGraph.edgeSet().size());

		List<Set<String>> ls = getConnectedComponentsList1(subGraph);

		// create jsonArray
		JSONArray jAry = new JSONArray();
		JSONObject joChild;
		// int d = 1, i = 0;

		List<Integer> grp = new ArrayList<>();
		grp.add(nodeid);

		for (Set<String> ss : ls) {
			// Set ss = ls.get(1);
			double percent = ((ss.size()) * 100.00) / totalVertice;
			int nodeSize = (int) ((percent / 100) * 1000000);//4000000
			
			Graph<String, DefaultWeightedEdge> subGraph1 = createSubGraph1(ss);
			int edgeCount = subGraph1.edgeSet().size();
			//System.out.println(" @@@ edgecount : "+ edgeCount);
			
			idKMap.put(id, Kcore);// Kcore to be used next time this id is clicked
			if (ss.size() > MIN_SCC_SIZE) {// check diff b/w first and second SCC
				joChild = new JSONObject();

				joChild.put("name", "SCC - " + id);
				joChild.put("size", nodeSize);
				joChild.put("depth", depth);
				joChild.put("id", id);
				
				joChild.put("totalNodes", ss.size());
				joChild.put("totalEdges", edgeCount);
				joChild.put("density", density(edgeCount,ss.size()));
				//m

				jAry.put(joChild);
				sccMap.put(id, ss);
				// put k for each id
				grp.add(id);
				id++;

			}

		}
		depth++;

		group = grp;
		setGroup(grp);
		sccMap.remove(nodeid);
		return jAry;
	}
	
	private void deleteNodesInSubgraph1(Graph<String, DefaultWeightedEdge> sgraph) {
		System.out.println("**Inside deleteNodesInSubgraph method***");

		Graph<String, DefaultWeightedEdge> graph = sgraph;
		score = countDegree1(graph);
		int k = Kcore + K_increment;

		// totalVertice = score.size();

		System.out.println("size of score map : " + score.size());
		// Set<DefaultEdge> s = null;
		// int k = Kcore; // nodeDeleted = 0;
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

		Kcore = k;
		// return graph;
		if(count == 0) {
			deleteNodesInSubgraph1(sgraph);
		}

	}

}
