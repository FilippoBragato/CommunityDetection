package communitydetection.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

import org.jgrapht.graph.AbstractGraph;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import communitydetection.graphnodes.Community;
import communitydetection.graphnodes.Node;

/**
 * Implementation of the Louvain's algorithm.
 * 
 * @author Filippo Bragato
 */
public class Louvain implements
        Function<AbstractGraph<Node, DefaultWeightedEdge>, DefaultUndirectedWeightedGraph<Community, DefaultWeightedEdge>> {
    private int nOfItereations;

    /**
     * Constructor of the original Louvain's algorithm.
     */
    public Louvain() {
        nOfItereations = 1;
    }

    /**
     * Constructor that sets the number of times original Louvain's algorithm will
     * be repeated in order to have better result.
     * 
     * @param nOfItereations The number of times original Louvain's algorithm will
     *                       be repeated.
     */
    public Louvain(int nOfItereations) {
        this.nOfItereations = nOfItereations;
    }

    /**
     * Creates a graph representing the communities of the given one.
     * 
     * @param network The graph that will be divided in communities.
     * @return The graph representing the communities of the given one.
     */
    @Override
    public DefaultUndirectedWeightedGraph<Community, DefaultWeightedEdge> apply(
            AbstractGraph<Node, DefaultWeightedEdge> network) {
        double m = initEdgesWeight(network);
        DefaultUndirectedWeightedGraph<Community, DefaultWeightedEdge> defNet = null;
        double maxMod = -1;
        for (int iteration = 0; iteration < nOfItereations; iteration++) {
            DefaultUndirectedWeightedGraph<Community, DefaultWeightedEdge> net = makeCommunity(network);
            ArrayList<Community> community = new ArrayList<>(net.vertexSet());

            Random rng = new Random();
            Collections.shuffle(community, rng);
            boolean converged = false;

            while (!converged) {
                converged = true;
                for (int i_c = 0; i_c < community.size(); i_c++) {

                    int nEdge = net.degreeOf(community.get(i_c));
                    if (nEdge != 0) {

                        double[] variationOfModularity = new double[nEdge];
                        ArrayList<Community> neighbors = getNeighbors(net, community.get(i_c));

                        for (int i_n = 0; i_n < neighbors.size(); i_n++) {
                            double sigma_tot = neighbors.get(i_n).getTotalWeightedDegree();
                            double k_i = community.get(i_c).getTotalWeightedDegree();
                            double k_i_in = net.getEdgeWeight(net.getEdge(community.get(i_c), neighbors.get(i_n)));
                            variationOfModularity[i_n] = k_i_in / m - (sigma_tot * k_i) / (2 * m * m);
                        }
                        int max_position = 0;
                        double max_modularity = variationOfModularity[0];
                        for (int i = 1; i < variationOfModularity.length; i++) {
                            if (variationOfModularity[i] > max_modularity) {
                                max_position = i;
                                max_modularity = variationOfModularity[i];
                            }
                        }
                        if (max_modularity > 0) {
                            converged = false;
                            Community destination = neighbors.remove(max_position);
                            for (Community n : neighbors) {
                                if (net.getEdge(destination, n) == null) {
                                    net.addEdge(destination, n);
                                    net.setEdgeWeight(destination, n,
                                            net.getEdgeWeight(net.getEdge(community.get(i_c), n)));
                                } else {
                                    double w = net.getEdgeWeight(net.getEdge(community.get(i_c), n))
                                            + net.getEdgeWeight(net.getEdge(destination, n));
                                    net.setEdgeWeight(destination, n, w);
                                }
                            }
                            destination.addNode((Node) community.get(i_c));
                            double weightedDegree = destination.getTotalWeightedDegree()
                                    + community.get(i_c).getTotalWeightedDegree()
                                    - net.getEdgeWeight(net.getEdge(community.get(i_c), destination));
                            destination.setTotalWeightedDegree(weightedDegree);
                            net.removeVertex(community.remove(i_c));
                        }
                    }
                }
            }
            if (iteration == 1) {
                defNet = net;
            } else {
                Double modularity = getModulartity(network, community, m);
                if (modularity > maxMod) {
                    maxMod = modularity;
                    defNet = net;
                }
            }
        }
        return defNet;
    }

    /**
     * Creates a graph of <code>Community</code> with the same nodes and edges of
     * the one given
     * 
     * @param network The original graph
     * @return A graph made of <code>Community</code> with the same structure of
     *         network.
     */
    private DefaultUndirectedWeightedGraph<Community, DefaultWeightedEdge> makeCommunity(
            AbstractGraph<Node, DefaultWeightedEdge> network) {
        Node[] oldNodes = network.vertexSet().toArray(new Node[0]);
        DefaultUndirectedWeightedGraph<Community, DefaultWeightedEdge> net = new DefaultUndirectedWeightedGraph<Community, DefaultWeightedEdge>(
                DefaultWeightedEdge.class);
        Hashtable<Node, Community> newNodes = new Hashtable<Node, Community>(oldNodes.length);
        for (int i = 0; i < oldNodes.length; i++) {
            Community newNode = new Community(oldNodes[i], i);
            newNodes.put(oldNodes[i], newNode);
            net.addVertex(newNode);
        }
        DefaultWeightedEdge[] oldEdges = network.edgeSet().toArray(new DefaultWeightedEdge[0]);
        for (int i = 0; i < oldEdges.length; i++) {
            Node s = network.getEdgeSource(oldEdges[i]);
            Node t = network.getEdgeTarget(oldEdges[i]);
            net.addEdge(newNodes.get(s), newNodes.get(t));
            net.setEdgeWeight(newNodes.get(s), newNodes.get(t), network.getEdgeWeight(oldEdges[i]));
        }
        return net;
    }

    /**
     * Initializes the total weighted degree for all the vertices and calculate the
     * sum of the weight of all the edges in the given graph.
     * 
     * @param network The original graph.
     * @return The sum of the weight of all the edges in the given graph.
     */
    private double initEdgesWeight(AbstractGraph<Node, DefaultWeightedEdge> network) {
        Set<Node> nodes = network.vertexSet();
        for (Node node : nodes) {
            double weight = 0;
            Set<DefaultWeightedEdge> edgeSet = network.edgesOf(node);
            for (DefaultWeightedEdge edge : edgeSet) {
                weight += network.getEdgeWeight(edge);
            }
            node.setTotalWeightedDegree(weight);
        }
        double m = 0;
        Set<DefaultWeightedEdge> edges = network.edgeSet();
        for (DefaultWeightedEdge e : edges) {
            m += network.getEdgeWeight(e);
        }
        return m;
    }

    /**
     * Gets all the nodes linked to a given one.
     * 
     * @param net The network containing the node.
     * @param c   The node whose neighbors will be found.
     * @return an ArrayList of node representing the neighbors of c.
     */
    private ArrayList<Community> getNeighbors(DefaultUndirectedWeightedGraph<Community, DefaultWeightedEdge> net,
            Community c) {
        Set<DefaultWeightedEdge> edges = net.edgesOf(c);
        ArrayList<Community> neighbors = new ArrayList<Community>();
        for (DefaultWeightedEdge edge : edges) {
            if (net.getEdgeSource(edge) != c)
                neighbors.add(net.getEdgeSource(edge));
            else
                neighbors.add(net.getEdgeTarget(edge));
        }
        return neighbors;
    }

    /**
     * Calculates the modularity of the given graph with the given partition.
     * 
     * @param network     The graph.
     * @param communities The partition.
     * @param m The sum of the weight of all the edges in the given graph.
     * @return The modularity according to the null model of Newman and Girvan 
     */
    private double getModulartity(AbstractGraph<Node, DefaultWeightedEdge> network, ArrayList<Community> communities,
            double m) {
        double modularity = 0;
        for (Community community : communities) {
            ArrayList<Node> nodes = community.getNodes();
            for (int i = 0; i < nodes.size(); i++) {
                for (int j = 0; j < nodes.size(); j++) {
                    modularity -= nodes.get(i).getTotalWeightedDegree() * nodes.get(j).getTotalWeightedDegree()
                            / (2 * m);
                    DefaultWeightedEdge e = network.getEdge(nodes.get(i), nodes.get(j));
                    if (e != null) {
                        modularity += network.getEdgeWeight(e);
                    }
                }
            }
        }
        modularity = modularity / (2 * m);
        if (Double.isNaN(modularity))
            modularity = 0;
        return modularity;
    }
}
