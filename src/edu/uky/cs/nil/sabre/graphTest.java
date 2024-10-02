package edu.uky.cs.nil.sabre;

import java.io.File;
import java.io.FileWriter;

import edu.uky.cs.nil.sabre.comp.CompiledProblem;
import edu.uky.cs.nil.sabre.graph.StateGraph;
import edu.uky.cs.nil.sabre.graph.StateNode;
import edu.uky.cs.nil.sabre.logic.True;

import java.util.HashMap;
import java.util.ArrayDeque;
import java.util.Deque;

import com.opencsv.CSVWriter;

public class graphTest {

	private static StateNode n = null;
	static int depth = 0;
	static int visited = 0;
	static int limit = 0;
	static int maxlimit = 3;
	static double end = 0;
	static double start = 0;
	static HashMap<StateNode, Integer> map = new HashMap<>();
	static Deque<StateNode> q = new ArrayDeque<>();

	public static void main(String[] args) {
		try {
			Session session = new Session();
			String filePath = "D:\\Downloads\\sabre-0.8\\problems\\raiders.txt";
			File file = new File(filePath);
			session.setProblem(file);
			print("Compiled Problem", session.getCompiledProblem());
			File file1 = new File("D:\\Downloads\\sabre-0.8\\results\\graphTestRaiders.csv"); 
	        FileWriter outputfile = new FileWriter(file1); 
	        CSVWriter writer = new CSVWriter(outputfile);
	        String[] header = { "Depth Limit", "Visided Nodes", "Time Taken (ms)" }; 
	        writer.writeNext(header); 
	        
			for(limit = 1; limit <= maxlimit; limit++) {
				visited = 0;
				end = 0;
				start = 0;
				CompiledProblem g = (CompiledProblem) session.getCompiledProblem();
				StateGraph graph = new StateGraph(g);
				start = System.currentTimeMillis();
				visit(graph.root, 0);				
				while (q.size() > 0) {
					n = q.poll();
					depth = map.get(n);
					if(depth < limit) {
						for(Action a : g.actions)
							if(a.precondition.evaluate(n).equals(True.TRUE)) {
								visit(n.getAfter(a), depth + 1);			 
							}
					}  
				}				
				end = System.currentTimeMillis();
				System.out.println("Depth Limit " + limit);
				System.out.println("Visited Nodes: " + visited);
				System.out.println("Time Taken " + (end - start) + "ms");
				String [] data = { String.valueOf(limit), String.valueOf(visited), String.valueOf(end-start) }; 
				writer.writeNext(data); 
				System.out.println("----------------------------------------");
		    }
			writer.close(); 
		}
		catch(Throwable t) {
			if(t instanceof RuntimeException && t.getCause() != null)
			t = t.getCause();
			System.err.println("Error: " + t.getMessage());
		}
	}


	static void visit(StateNode n, int a) {
		if(!map.containsKey(n)) {
			map.put(n, a);
			q.add(n);
			visited++;
			for(Character i : n.graph.characters) {
				visit(n.getBeliefs(i), a);
			}
		}
	}
	
	private static final void print(String key, Problem problem) {
		System.out.println(key + ": " + problem.name);
		System.out.println("  characters: " + problem.universe.characters.size());
		System.out.println("  entities:   " + problem.universe.entities.size());
		System.out.println("  fluents:    " + problem.fluents.size());
		System.out.println("  actions:    " + problem.actions.size());
		System.out.println("  triggers:   " + problem.triggers.size());
	}
}
