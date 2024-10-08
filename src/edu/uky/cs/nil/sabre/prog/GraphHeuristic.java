package edu.uky.cs.nil.sabre.prog;

import edu.uky.cs.nil.sabre.Character;
import edu.uky.cs.nil.sabre.State;
import edu.uky.cs.nil.sabre.comp.CompiledProblem;
import edu.uky.cs.nil.sabre.hg.HeuristicGraph;
import edu.uky.cs.nil.sabre.hg.MaxGraph;
import edu.uky.cs.nil.sabre.hg.SumGraph;
import edu.uky.cs.nil.sabre.hg.UtilityNode;
import edu.uky.cs.nil.sabre.logic.Comparison;
import edu.uky.cs.nil.sabre.logic.Value;
import edu.uky.cs.nil.sabre.util.Worker.Status;

/**
 * A {@link ProgressionCost cost function}, meant to be used as a {@link
 * ProgressionSearch#heuristic heuristic} in a {@link ProgressionSearch
 * heuristic progression search}, which uses a {@link HeuristicGraph heuristic
 * graph} to estimate how many more actions would need to be taken in some state
 * before a character's utility can be improved.
 * <p>
 * By default, the {@link #evaluate(State, Character) evaluate} method
 * initializes its heuristic graph to the given state and then extends the
 * graph until it is possible for the utility of the given character to be
 * higher. It then returns the {@link
 * edu.uky.cs.nil.sabre.hg.CostSet#getCost(int) the cost} of achieving that
 * higher utility value.
 * <p>
 * When {@link #evaluate(ProgressionNode) evaluating} a {@link ProgressionNode
 * progression search node} this cost function uses the node as the state and
 * {@link ProgressionNode#getCharacter() the node's character} as the
 * character. Nodes which are {@link ProgressionNode#isExplained() explained}
 * always return a value of 0 to avoid penalizing solutions during search.
 * Otherwise, a node which just improved its character's utility, but for which
 * it might be possible to further improve utility, would have a non-zero cost
 * estimate and would be visited later during a heuristic search.
 * 
 * @author Stephen G. Ware
 */
public class GraphHeuristic implements ProgressionCost {
	
	/**
	 * A {@link GraphHeuristic progression cost function} that uses a {@link
	 * SumGraph sum heuristic graph}.
	 * 
	 * @author Stephen G. Ware
	 */
	public static class SumGraphHeuristic extends GraphHeuristic {

		/** The sum heuristic graph used by this cost function */
		protected final SumGraph graph;
		
		/**
		 * Constructs a new sum graph heuristic from a given sum graph.
		 * 
		 * @param graph the sum graph to use for calculating costs
		 */
		public SumGraphHeuristic(SumGraph graph) {
			super(graph);
			this.graph = graph;
		}
		
		/**
		 * Constructs a new sum graph heuristic and its sum graph.
		 * 
		 * @param problem that problem for which this heuristic will approximate
		 * costs
		 * @param status a status to update while building the sum graph
		 */
		public SumGraphHeuristic(CompiledProblem problem, Status status) {
			this(new SumGraph(problem, status));
		}
		
		@Override
		public String toString() {
			return SUM.toString();
		}
	}
	
	/**
	 * A {@link GraphHeuristic progression cost function} that uses a {@link
	 * MaxGraph max heuristic graph}.
	 * 
	 * @author Stephen G. Ware
	 */
	public static class MaxGraphHeuristic extends GraphHeuristic {
		
		/** The max heuristic graph used by this cost function */
		protected final MaxGraph graph;
		
		/**
		 * Constructs a new max graph heuristic from a given max graph.
		 * 
		 * @param graph the max graph to use for calculating costs
		 */
		public MaxGraphHeuristic(MaxGraph graph) {
			super(graph);
			this.graph = graph;
		}
		
		/**
		 * Constructs a new max graph heuristic and its max graph.
		 * 
		 * @param problem that problem for which this heuristic will approximate
		 * costs
		 * @param status a status to update while building the max graph
		 */
		public MaxGraphHeuristic(CompiledProblem problem, Status status) {
			this(new MaxGraph(problem, status));
		}
		
		@Override
		public String toString() {
			return MAX.toString();
		}
	}

	/**
	 * A {@link ProgressionCostFactory factory} for producing {@link
	 * SumGraphHeuristic sum graph heuristics}, which define the cost of a
	 * proposition to be the sum of the cost of its parts.
	 */
	public static final ProgressionCostFactory SUM = new ProgressionCostFactory() {
		
		/** Serial version ID */
		private static final long serialVersionUID = 1L;

		@Override
		public String toString() {
			return "sum graph";
		}
		
		@Override
		public SumGraphHeuristic getCost(CompiledProblem problem, Status status) {
			return new SumGraphHeuristic(problem, status);
		}
	};
	
	/**
	 * A {@link ProgressionCostFactory factory} for producing {@link
	 * MaxGraphHeuristic max graph heuristics}, which define the cost of a
	 * proposition to be the same as its highest cost part.
	 */
	public static final ProgressionCostFactory MAX = new ProgressionCostFactory() {

		/** Serial version ID */
		private static final long serialVersionUID = 1L;
		
		@Override
		public String toString() {
			return "max graph";
		}
		
		@Override
		public MaxGraphHeuristic getCost(CompiledProblem problem, Status status) {
			return new MaxGraphHeuristic(problem, status);
		}
	};
	
	/** The heuristic graph used by this cost function */
	protected final HeuristicGraph graph;
	
	/**
	 * Constructs a new heuristic graph cost function.
	 * 
	 * @param graph the heuristic graph to use for calculating costs
	 */
	public GraphHeuristic(HeuristicGraph graph) {
		this.graph = graph;
	}

	@Override
	public <N> double evaluate(ProgressionNode<N> node) {
		if(node.isExplained(node.getCharacter()))
			return 0;
		else
			return evaluate(node, node.getCharacter());
	}
	
	/**
	 * Uses a {@link HeuristicGraph heuristic graph} to estimate the number of
	 * actions that need to be taken from the given state to improve the
	 * utility of the given character.
	 * 
	 * @param state the state from which the actions would be taken
	 * @param character the character whose utility is to be improved
	 * @return an estimated number of actions that need to be taken to improve
	 * the character's utility
	 */
	public double evaluate(State state, Character character) {
		UtilityNode utility = graph.getUtility(character);
		graph.initialize(state);
		Value start = utility.getValue(0);
		while(utility.getCost(Comparison.GREATER_THAN, start) == Double.POSITIVE_INFINITY && graph.extend());
		return utility.getCost(Comparison.GREATER_THAN, start);
	}
}