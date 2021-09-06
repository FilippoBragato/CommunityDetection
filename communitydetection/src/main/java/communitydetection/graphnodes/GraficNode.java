package communitydetection.graphnodes;

/**
 * <p>
 * A node used to display graph in 2D layout.
 * </p>
 * <p>
 * It's caracterized by having coordinates on a Cartesian plane and a community.
 * Coordinates define the position of the node, the id of the community defines
 * the color of the node.
 * </p>
 * 
 * @author Filippo Bragato
 */
public class GraficNode {
    private double x;
    private double y;
    private int communityId;
    private Node node;

    /**
     * Creates a new <code>GraficNode</code> initializing all its field.
     * 
     * @param node        The node corresponding to this <code>GraficNode</code>
     * @param x           The x in the Cartesian plane
     * @param y           The y in the Cartesian plane
     * @param communityId The id of the community
     */
    public GraficNode(Node node, double x, double y, int communityId) {
        this.node = node;
        this.x = x;
        this.y = y;
        this.communityId = communityId;
    }

    /**
     * Creates a new <code>GraficNode</code> initializing node and communityId
     * 
     * @param node        The node corresponding to this <code>GraficNode</code>
     * @param communityId The id of the community
     */
    public GraficNode(Node node, int communityId) {
        node.setGrafical(this);
        this.node = node;
        this.communityId = communityId;
    }

    /**
     * A <code>GraficNode</code> is identified by the id of a community. Get the id
     * of this community
     * 
     * @return The id of this community
     */
    public int getCommunityId() {
        return communityId;
    }

    /**
     * Gets the corrisponding node of this <code>GraficNode</code>.
     * 
     * @return The corrisponding node.
     */
    public Node getNode() {
        return node;
    }

    /**
     * A <code>GraficNode</code> is identified by coordinates on a Cartesian plane.
     * Get the x of this node
     * 
     * @return The x of this node
     */
    public double getX() {
        return x;
    }

    /**
     * A <code>GraficNode</code> is identified by coordinates on a Cartesian plane.
     * Get the y of this node
     * 
     * @return The y of this node
     */
    public double getY() {
        return y;
    }

    /**
     * A <code>GraficNode</code> is identified by coordinates on a Cartesian plane.
     * Set the x of this node
     * 
     * @param x The x of this node
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * A <code>GraficNode</code> is identified by coordinates on a Cartesian plane.
     * Set the y of this node
     * 
     * @param y The y of this node
     */
    public void setY(double y) {
        this.y = y;
    }
}
