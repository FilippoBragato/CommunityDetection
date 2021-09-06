package communitydetection.graphnodes;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A node identified by coordinates on a Cartesian plane.
 * 
 * @see Node
 * @author Filippo Bragato
 */
public class Node2D implements Node {
    private double x;
    private double y;
    private double totalWeightedDegree;
    private GraficNode grafical = null;

    @Override
    public double getTotalWeightedDegree() {
        return totalWeightedDegree;
    }

    @Override
    public void setTotalWeightedDegree(double totalWeightedDegree) {
        this.totalWeightedDegree = totalWeightedDegree;
    }

    @Override
    public ArrayList<Node> getNodes() {
        return new ArrayList<Node>(Arrays.asList(this));
    }

    public Node2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * A <code>Node2D</code> is identified by coordinates on a Cartesian plane. Get
     * the x of this node
     * 
     * @return The x of this node
     */
    public double getX() {
        return x;
    }

    /**
     * A <code>Node2D</code> is identified by coordinates on a Cartesian plane. Get
     * the y of this node
     * 
     * @return The y of this node
     */
    public double getY() {
        return y;
    }

    /**
     * A <code>Node2D</code> is identified by coordinates on a Cartesian plane. Set
     * the x of this node
     * 
     * @param x The x of this node
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * A <code>Node2D</code> is identified by coordinates on a Cartesian plane. Set
     * the y of this node
     * 
     * @param y The y of this node
     */
    public void setY(double y) {
        this.y = y;
    }

    @Override
    public int getSize() {
        return 1;
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
