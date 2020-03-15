package org.myproj.services;

import org.springframework.stereotype.Service;
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
import org.json.JSONArray;
import org.json.JSONObject;

@Service
public class GraphService {

	static Map<String, Integer> score = null;
	static int totalVertice = 0;
	static Graph<String, DefaultEdge> graph = null;
	static HashMap<String, Integer> scoreMap = null;
	static HashMap<Integer, Integer> rankMap = null;
	static ArrayList<ArrayList<String>> groupArray = new ArrayList<ArrayList<String>>();
	// JSONObject finalJson = new JSONObject();
	static int K_start = 5;
	static int K_increment = 6;
	static final int MIN_SCC_SIZE = 50;
	static int Kcore,id,depth;
	static Map<Integer, Set<String>> sccMap = new HashMap<>();
	static Map<Integer, Integer> idKMap = new HashMap<>();

	public JSONArray preprocess() {
		Instant start = Instant.now();

		graph = GraphTypeBuilder.undirected()
//				.directed()
				.vertexSupplier(SupplierUtil.createStringSupplier())
				.edgeSupplier(SupplierUtil.createDefaultEdgeSupplier()).buildGraph();

		File file = new File("C:\\2019-Fall\\GA work\\com-dblp.ungraph.txt"); // undirected graph

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

		System.out.println("**BEFORE edge set of graph : " + graph.edgeSet().size());
		deleteNodes(graph);
		System.out.println("**AFTER edge set of graph : " + graph.edgeSet().size());

		Instant finish = Instant.now();
		long timeElapsed = Duration.between(start, finish).toMillis(); // in millis
		System.out.println(" *****total time in millis:" + timeElapsed);

		List<Set<String>> ls = getConnectedComponentsList(graph);

		// create jsonArray
		JSONArray jAry = new JSONArray();
		JSONObject joChild;
		depth = 1; id = 1;
		idKMap.put(id, Kcore);

		for (Set<String> ss : ls) {
			// Set ss = ls.get(1);
			if (ss.size() > MIN_SCC_SIZE) {// check diff b/w first and second SCC
				joChild = new JSONObject();

				joChild.put("name", "SCC - " + id);
				joChild.put("size", 1000000);
				joChild.put("depth", depth);
				joChild.put("id", id);

				jAry.put(joChild);
				sccMap.put(id, ss);
				id++;
			}
			
		}
		depth++;
		return jAry;
	}

	public JSONArray clickProcess(int nodeid) {
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
		//int d = 1, i = 0;

		for (Set<String> ss : ls) {
			// Set ss = ls.get(1);
			double percent = ((ss.size()) * 100.00) / totalVertice;
			int nodeSize = (int) ((percent / 100) * 4000000);
					
			idKMap.put(id, Kcore);//Kcore to be used next time this id is clicked
			if (ss.size() > MIN_SCC_SIZE) {// check diff b/w first and second SCC
				joChild = new JSONObject();

				joChild.put("name", "SCC - " + id);
				joChild.put("size", nodeSize);
				joChild.put("depth", depth);
				joChild.put("id", id);

				jAry.put(joChild);
				sccMap.put(id, ss);
				//put k for each id
				id++;
			}
			
		}
		depth++;
		
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

	}

	private static void deleteNodes(Graph<String, DefaultEdge> graph) {
		System.out.println("**Inside deleteNodes method start***");
		score = countDegree(graph);

		totalVertice = score.size();

		System.out.println("size of score map : " + score.size());
		// Set<DefaultEdge> s = null;
		int k = K_start; 
		double nodeDeleted = 0.0;
		while (nodeDeleted < 50.0) {
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
		// ArrayList<Graph<String, DefaultEdge>> subGraphList = new
		// ArrayList<Graph<String, DefaultEdge>>();

		// for (Set<String> ss : ls) {
		subgraph = GraphTypeBuilder
				// .undirected()
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

	public JSONArray createNodeArray() {
		JSONArray jAry = new JSONArray();

		JSONObject jobj2 = new JSONObject();

		jobj2.put("name", "SCC - " + 1);
		jobj2.put("size", "16000");
		jobj2.put("depth", 1);
		jobj2.put("id", 1);

		jAry.put(jobj2);
		return jAry;
	}

	public JSONArray createNodeChildArray() {
		JSONArray jAry = new JSONArray();
		JSONObject jobj = new JSONObject();

		jobj.put("name", "SCC - " + 2);
		jobj.put("size", "3000");
		jobj.put("depth", 2);
		jobj.put("id", 2);

		JSONObject jobj2 = new JSONObject();

		jobj2.put("name", "SCC - " + 3);
		jobj2.put("size", "3000");
		jobj2.put("depth", 2);
		jobj2.put("id", 3);

		jAry.put(jobj);
		jAry.put(jobj2);

		return jAry;
	}

}
