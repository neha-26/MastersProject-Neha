package org.myproj.masterProj;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

import org.apache.commons.lang.ArrayUtils;
import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;

public class ALGraph {
	private int numVertices;
	private static LinkedList<Integer> adjLists[];

	static Map<Integer, Integer> score = null;
	static int totalVertice = 0;
	// static Graph<String, DefaultEdge> graph = null;
	static ALGraph graph = null;
	static HashMap<Integer, Integer> scoreMap = null;
	static HashMap<Integer, Integer> rankMap = null;
	static HashMap<Integer, Integer> data = new HashMap<Integer, Integer>();
	static Set<Integer> ver = new HashSet<Integer>();

	ALGraph(int vertices) {
		System.out.println("total vertices : "+ vertices);
		numVertices = vertices;
		adjLists = new LinkedList[vertices];

//		for (int i = 0; i < vertices; i++)
//			adjLists[i] = new LinkedList();
	}

	void addEdge(int src, int dest) {
		//System.out.println(" src : "+ src);
		adjLists[src].add(dest);
	}

	public static void main(String[] args) {
		// code
		//ALGraph graph = new ALGraph(4);

//		g.addEdge(0, 1);
//		g.addEdge(0, 2);
//		g.addEdge(1, 2);
//		g.addEdge(2, 3);

		//System.out.println("Vertices : " + adjLists.length);
		Instant start = Instant.now();
		//File file = new File("C:\\2019-Fall\\GA work\\email-Enron.txt");
		// File file = new File("C:\\2019-Fall\\GA work\\Wiki-Vote.txt");
		File file = new File("C:\\2019-Fall\\GA work\\com-amazon.ungraph.txt");
		// File file = new File("C:\\2019-Fall\\GA work\\com-dblp.ungraph.txt");
		// File file = new File("C:\\2019-Fall\\GA work\\CA-AstroPh.txt"); //directed
		// graph
		// File file = new File("C:\\2019-Fall\\GA work\\com-orkut.ungraph.txt");
		// //memory error
		//File file = new File("C:\\2019-Fall\\GA work\\web-Google.txt\\web-Google.txt");
		 
		Scanner sc;
		try {
			sc = new Scanner(file);
			while (sc.hasNextLine()) {
				String[] s = sc.nextLine().split("\\s+");
				//graph.addVertex(s[0]);
				//graph.addVertex(s[1]);
				//graph.addEdge(s[0], s[1]);
				data.put(Integer.parseInt(s[0]),Integer.parseInt(s[1]));
				ver.add(Integer.parseInt(s[0]));
				ver.add(Integer.parseInt(s[1]));

			}
			System.out.println(" ver size : "+ ver.size());
			//graph = new ALGraph(ver.size());
			int max = Collections.max(ver);
			graph = new ALGraph(max+1);
			
			for (int i = 0; i <= max; i++)
				adjLists[i] = new LinkedList();
			
			for (int i : data.keySet()) {
				//System.out.println(" i : "+ i);
				//System.out.println(" data.get(i) : "+ data.get(i));
				graph.addEdge(i, data.get(i));
			}


		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Instant finish = Instant.now();
		long timeElapsed = Duration.between(start, finish).toMillis(); // in millis
		System.out.println(" *****total time in millis:" + timeElapsed);

		score = countDegree(graph);
//
		totalVertice = score.size();
		System.out.println("size of score map : " + score.size());
//
		//graph = deleteNodes();
//
//		ConnectivityInspector ci = new ConnectivityInspector(graph);
//
//		System.out.println("isConnected : " + ci.isConnected());
//		List<Set<String>> ls = ci.connectedSets();
//		System.out.println(" list size : " + ls.size());

		// Rank List based on set size
//		Collections.sort(ls, new Comparator<Set<?>>() {
//			@Override
//			public int compare(Set<?> o1, Set<?> o2) {
//				return Integer.valueOf(o2.size()).compareTo(o1.size());
//			}
//		});

		// create map with key as index and value as size
//		rankMap = new HashMap<Integer, Integer>();
//		System.out.println("Rank Size");
//		for (int i = 0; i < ls.size(); i++) {
//
//			System.out.println(i + 1 + " " + ls.get(i).size());
//
//			rankMap.put(i + 1, ls.get(i).size());
//		}

		// create file with data for plotting graphs

//		PrintWriter writer;
//		try {
//			writer = new PrintWriter("C:\\2019-Fall\\GA work\\output.txt", "UTF-8");
//
//			for (Set ss : ls) {
//				writer.println(" ********** New SSC BEGINS *******");
//				for (Object se : ss) {
//					writer.print(String.valueOf(se) + ",  ");
//				}
//				writer.println(" ");
//			}
//			writer.close();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}

	}

	private static void rankScc(List<Set<String>> ls) {

		int size = 0;
		for (Set scc : ls) {
			size = scc.size();
		}
	}

	private static ALGraph deleteNodes() {
		Set<DefaultEdge> s = null;
		int k = 5, nodeDeleted = 0;
		while (nodeDeleted < 80) {
			System.out.println("-------K--------" + k);
			int count = 0;
			scoreMap = new HashMap<Integer, Integer>(score);
			System.out.println("New scoreMap size " + scoreMap.size());

			HashMap<Integer, Integer> temp = new HashMap<Integer, Integer>(scoreMap);
			for (int i : temp.keySet()) {
				if (scoreMap.get(i) <= k) {
					// System.out.println(" i :"+ i);
					count++;
					scoreMap.remove(i);

					//graph.removeVertex(i);
					
					//Commented because of ERROR
					//adjLists = (LinkedList<Integer>[]) ArrayUtils.remove(, i);

				}
			}

			System.out.println("count : " + count);
			System.out.println("scoreMap size " + scoreMap.size());

			score = countDegree(graph);

			System.out.println("scoreAfterRemoval size " + score.size());

			nodeDeleted = calculateNodeDeletedPerc(score.size());
			k++;
		}

		return graph;
	}

	private static Map<Integer, Integer> countDegree(ALGraph graph) {
		//Set<String> vertSet = graph.vertexSet();
		HashMap<Integer, Integer> temp = new HashMap<Integer, Integer>();
		for (Integer s : ver) {
			temp.put(s, adjLists[s].size());
		}
		return temp;
	}

	private static int calculateNodeDeletedPerc(int size) {

		int rem = ((totalVertice - size) * 100) / totalVertice;
		System.out.println(" % deleted " + rem);
		return rem;
	}

}
