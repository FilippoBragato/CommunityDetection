package communitydetection.graphnodes;

import java.util.ArrayList;
import java.util.List;

/**
 * A node identified by an id
 * 
 * @see Node
 * @author Filippo Bragato
 */
public class SimpleNode implements Node {

    private int id;
    private double totalWeightedDegree;
    private GraficNode grafical;

    /**
     * Constructor that sets the id of the node.
     * 
     * @param id The id of the node.
     */
    public SimpleNode(int id) {
        this.id = id;
    }

    /**
     * A <code>SimpleNode</code> is identified by an id. Gets the id of this node.
     * 
     * @return The id of this node.
     */
    public int getId() {
        return this.id;
    }

    /**
     * A <code>SimpleNode</code> is identified by an id. Sets the id of this node.
     * 
     * @param id The id of this node.
     */
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public ArrayList<Node> getNodes() {
        return new ArrayList<Node>(List.of(this));
    }

    @Override
    public double getTotalWeightedDegree() {
        return totalWeightedDegree;
    }

    @Override
    public void setTotalWeightedDegree(double weightedDegree) {
        this.totalWeightedDegree = weightedDegree;

    }

    @Override
    public int getSize() {
        return 1;
    }

    @Override
    public GraficNode getGrafical() {
        return grafical;
    }

    @Override
    public void setGrafical(GraficNode graficNode) {
        this.grafical = graficNode;
    }

}
