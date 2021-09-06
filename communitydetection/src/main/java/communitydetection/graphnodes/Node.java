package communitydetection.graphnodes;

import java.util.ArrayList;

/**
 * <p>
 * A generic node used to create the graph for Louvain's algorithm and 
 * FastFruchtermanReingold algortithm.
 * </p>
 * <p>
 * Implementations of this class only add fields that carry information about
 * the node itself, such as an id.
 * </p>
 * 
 * @author Filippo Bragato
 */
public interface Node {
    /**
     * Gets the total weighted degree, that is the sum of the weigths of all 
     * the edges that are connected to the node.
     * It's used to apply Louvain's algorithm faster
     * @return The sum of the weigths of all the edges that are connected to
     * the node
     */
    public double getTotalWeightedDegree();
    
    /**
     * Sets the total weighted degree, that is the sum of the weigths of all 
     * the edges that are connected to the node.
     * It's used to apply Louvain's algorithm faster
     * @param weightedDegree The sum of the weigths of all the edges that are connected to
     * the node
     */
    public void setTotalWeightedDegree(double weightedDegree);

    /**
     * A node can contain several Nodes inside. Gets the list of node inside 
     * this.
     * @return An <code>ArrayList</code> containing all the Nodes inside this
     */
    public ArrayList<Node> getNodes();

    /**
     * A node can contain several Nodes inside. Gets the number of nodes inside
     * this
     * @return The number of nodes inside this
     */
    public int getSize();

    /**
     * In order to be represented in a 2D layout a Node must have a relative
     * <code>GraficNode</code>. Gets the corresponding <code>GraficNode</code>
     * @return The corresponding <code>GraficNode</code>
     * @see GraficNode
     */
    public GraficNode getGrafical();

    /** 
     * In order to be represented in a 2D layout a Node must have a relative
     * <code>GraficNode</code>. Sets the corresponding <code>GraficNode</code>
     * @param graficNode The corresponding <code>GraficNode</code>
     * @see GraficNode
     */
    public void setGrafical(GraficNode graficNode);
}