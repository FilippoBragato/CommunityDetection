package communitydetection.graphmanagement;

import java.util.Random;

import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import communitydetection.graphnodes.Node;
import communitydetection.graphnodes.SimpleNode;

/**
 * Used to create standard graph with a community structure and a l-partition
 * model. About its field:
 * <ul>
 * <li><code>l</code> is the number of communities in the graph;</li>
 * <li><code>g</code> is the number of nodes inside each community;</li>
 * <li><code>p_in</code> is the probability of the existence of an edge between
 * two nodes in the same community;</li>
 * <li><code>p_out</code> is the probability of the existence of an edge between
 * two nodes in different communities.</li>
 * </ul>
 * 
 * @author Filippo Bragato
 */
public class TestGraphCreator {
    private int l;
    private int g;
    private double p_in;
    private double p_out;

    /**
     * Default constructor with standard values for l-partition
     */
    public TestGraphCreator() {
        this.l = 4;
        this.g = 32;
        this.p_in = 0.25;
        this.p_out = 1.0 / 12.0;
    }

    /**
     * Constructor that allows to set every field of the class.
     * 
     * @param l     The number of communities in the graph
     * @param g     The number of nodes inside each community
     * @param z_in  The expected value of the number of edges that every vertex
     *              shares with vertices of the same community
     * @param z_out The expected value of the number of edges that every vertex
     *              shares with vertices of different communities
     */
    public TestGraphCreator(int l, int g, double z_in, double z_out) {
        this.l = l;
        this.g = g;
        this.p_in = z_in / (g - 1);
        this.p_out = z_out / (g * (l - 1));
    }

    /**
     * Creates a standard graph of <code>SimpleNode</code> with a community
     * structure and a l-partition model, with the features described by the fields
     * of this object.
     * 
     * @return Graph of <code>SimpleNode</code> with a community structure.
     */
    public DefaultUndirectedWeightedGraph<Node, DefaultWeightedEdge> lpartition() {

        DefaultUndirectedWeightedGraph<Node, DefaultWeightedEdge> net = new DefaultUndirectedWeightedGraph<Node, DefaultWeightedEdge>(
                DefaultWeightedEdge.class);

        SimpleNode[] nodes = new SimpleNode[l * g];
        for (int i = 0; i < nodes.length; i++) {
            SimpleNode node = new SimpleNode(i);
            nodes[i] = node;
            net.addVertex(node);
        }

        Random rng = new Random();
        for (int i = 0; i < nodes.length; i++) {
            for (int j = i + 1; j < nodes.length; j++) {
                if ((i / g) == (j / g)) {
                    if (rng.nextDouble() < p_in) {
                        net.addEdge(nodes[i], nodes[j]);
                    }
                } else {
                    if (rng.nextDouble() < p_out) {
                        net.addEdge(nodes[i], nodes[j]);
                    }
                }
            }
        }
        return net;
    }
}
