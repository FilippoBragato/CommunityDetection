package communitydetection.graphnodes;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A node identified by an id that contains multiple <code>Node</code>s
 * 
 * @see Node
 * @author Filippo Bragato
 */
public class Community implements Node {
    private ArrayList<Node> nodesInCommunity;
    private int id;
    private double totalWeightedDegree;
    private GraficNode grafical = null;

    /**
     * Creates a new <code>Community</code> with a single node and an id
     * 
     * @param node The node of the Community
     * @param id   The id of the Community
     */
    public Community(Node node, int id) {
        nodesInCommunity = new ArrayList<Node>(Arrays.asList(node));
        this.id = id;
        this.totalWeightedDegree = node.getTotalWeightedDegree();
    }

    /**
     * Gets the id of the community
     * 
     * @return The id of the community
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id of the community
     * 
     * @param id The id of the community
     */
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public double getTotalWeightedDegree() {
        return totalWeightedDegree;
    }

    @Override
    public void setTotalWeightedDegree(double weightedDegree) {
        this.totalWeightedDegree = weightedDegree;
    }

    /**
     * Adds a single node to the community
     * 
     * @param node The node to add to the community
     */
    public void addNode(Node node) {
        this.nodesInCommunity.addAll(node.getNodes());
    }

    @Override
    public ArrayList<Node> getNodes() {
        ArrayList<Node> nodes = new ArrayList<>();
        for (Node node : nodesInCommunity) {
            nodes.addAll(node.getNodes());
        }
        return nodes;
    }

    @Override
    public int getSize() {
        return nodesInCommunity.size();
    }

    @Override
    public void setGrafical(GraficNode graficNode) {
        this.grafical = graficNode;
    }

    @Override
    public GraficNode getGrafical() {
        return this.grafical;
    }

}