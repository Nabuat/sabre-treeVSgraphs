package edu.uky.cs.nil.sabre;

import java.io.File;
import java.io.FileWriter;

import edu.uky.cs.nil.sabre.io.DefaultPrinter;
import edu.uky.cs.nil.sabre.io.Printer;
import edu.uky.cs.nil.sabre.prog.ProgressionCostFactory;
import edu.uky.cs.nil.sabre.prog.ProgressionPlanner;
import edu.uky.cs.nil.sabre.prog.ProgressionSearch;
import edu.uky.cs.nil.sabre.search.Planner;
import edu.uky.cs.nil.sabre.search.Result;
import edu.uky.cs.nil.sabre.util.Worker;

import com.opencsv.CSVWriter;

public class treeTest{	
	
	static int limit = 0;
	static int maxlimit = 5;
	static double start = 0;
	static double end = 0;
	
	public static void main(String[] args) {
		try {
			Session session = new Session();
			String filePath = "D:\\Downloads\\sabre-0.8\\problems\\raiders.txt";
			File file = new File(filePath);
			session.setProblem(file);
			
			File file1 = new File("D:\\Downloads\\sabre-0.8\\results\\treeTestRaiders.csv"); 
	        FileWriter outputfile = new FileWriter(file1); 
	        CSVWriter writer = new CSVWriter(outputfile);
	        String[] header = { "Depth Limit", "Visided Nodes", "Time Taken (ms)" }; 
	        writer.writeNext(header); 
	        
			print("Compiled Problem", session.getCompiledProblem());
			session.setGoal(Number.get(2));
			session.setSearchLimit(Planner.UNLIMITED_NODES);
			session.setSpaceLimit(Planner.UNLIMITED_NODES);
			session.setTimeLimit(Planner.UNLIMITED_TIME);
			
			for(limit = 1; limit <= maxlimit; limit++) {
				end = 0;
				start = 0;
				session.setAuthorTemporalLimit(limit);
				session.setCharacterTemporalLimit(limit);
				session.setEpistemicLimit(1);
				start = System.currentTimeMillis();
				if(session.getPlanner() instanceof ProgressionPlanner) {
					ProgressionCostFactory h = ProgressionCostFactory.ZERO;
					session.setHeuristic(h);				
				}
				ProgressionSearch search = (ProgressionSearch) session.getSearch();
				Result<?> result = Worker.get(status -> search.get(status));		
				end = System.currentTimeMillis();
				
				System.out.println("Depth Limit " + limit);
				System.out.println("Visited Nodes " + result.visited);
				System.out.println("Generated Nodes " + result.generated);
				System.out.println("Time Taken: " + (end - start) + " ms");
				String [] data = { String.valueOf(limit), String.valueOf(result.visited), String.valueOf(end-start) }; 
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
	
	/**
	 * Prints summary statistics of a {@link Problem problem} to standard
	 * output if running in verbose mode.
	 * 
	 * @param key the type of problem
	 * @param problem the problem
	 */
	private static final void print(String key, Problem problem) {
		System.out.println(key + ": " + problem.name);
		System.out.println("  characters: " + problem.universe.characters.size());
		System.out.println("  entities:   " + problem.universe.entities.size());
		System.out.println("  fluents:    " + problem.fluents.size());
		System.out.println("  actions:    " + problem.actions.size());
		System.out.println("  triggers:   " + problem.triggers.size());
	}		
	
}

