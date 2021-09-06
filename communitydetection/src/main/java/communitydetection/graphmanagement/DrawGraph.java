package communitydetection.graphmanagement;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.jgrapht.graph.AbstractGraph;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import communitydetection.graphnodes.Community;
import communitydetection.graphnodes.GraficNode;
import communitydetection.graphnodes.Node;

public class DrawGraph {

    private double x_max, x_min, y_max, y_min;
    private double proportionX, proportionY;

    private int xSize;
    private int ySize;
    private int vertexSize;

    /**
     * Constructor that sets the dimension in pixels of the image that will be
     * produce and the dimension of the vertices
     * 
     * @param xSize      The width of the image in pixels
     * @param ySize      The height of the image in pixels
     * @param vertexSize The diameter of the vertex in pixels
     */
    public DrawGraph(int xSize, int ySize, int vertexSize) {
        this.xSize = xSize;
        this.ySize = ySize;
        this.vertexSize = vertexSize;
    }

    /**
     * Creates a graph of <code>GraficNode</code> with the same nodes and edges of
     * the one given, it assigns also a community for each <code>GraficNode</code>
     * using the given partition.
     * 
     * @param net          The original graph.
     * @param communityNet The graph of the community of the original graph.
     * @return A graph made of <code>GraficNode</code> with the same structure of
     *         net.
     */
    public DefaultUndirectedWeightedGraph<GraficNode, DefaultWeightedEdge> makeDrawable(
            AbstractGraph<? extends Node, DefaultWeightedEdge> net,
            AbstractGraph<Community, DefaultWeightedEdge> communityNet) {

        DefaultUndirectedWeightedGraph<GraficNode, DefaultWeightedEdge> drawNet = new DefaultUndirectedWeightedGraph<GraficNode, DefaultWeightedEdge>(
                DefaultWeightedEdge.class);

        Community[] communities = communityNet.vertexSet().toArray(new Community[0]);
        for (int i_comm = 0; i_comm < communities.length; i_comm++) {
            ArrayList<Node> nodesInsideCommunity = communities[i_comm].getNodes();
            for (int i_node = 0; i_node < nodesInsideCommunity.size(); i_node++) {
                GraficNode grafic = new GraficNode(nodesInsideCommunity.get(i_node), i_comm);
                drawNet.addVertex(grafic);
            }
        }

        DefaultWeightedEdge[] edges = net.edgeSet().toArray(new DefaultWeightedEdge[0]);
        for (int i = 0; i < edges.length; i++) {
            Node s = net.getEdgeSource(edges[i]);
            Node t = net.getEdgeTarget(edges[i]);
            drawNet.addEdge(s.getGrafical(), t.getGrafical());
        }

        return drawNet;
    }

    /**
     * Creates an image of the given graph of <code>GraficNode</code>
     * 
     * @param net The net that will be represented
     * @return A <code>BufferedImage</code> representing the net
     * @see BufferedImage
     */
    public BufferedImage draw(AbstractGraph<GraficNode, DefaultWeightedEdge> net) {
        int[][] coordinates = getCoordinates(net);
        BufferedImage img = new BufferedImage(xSize, ySize, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D graphics2d = img.createGraphics();
        graphics2d.setColor(Color.white);
        graphics2d.fillRect(0, 0, xSize, ySize);
        drawEdges(net, graphics2d);
        drawVertex(coordinates, graphics2d);
        return img;

    }

    /**
     * Draws the vertices represented by given coordinates using the given graphics
     * 
     * @param coordinates The coordinates of the vertices that will be drawn
     * @param graphics2d  The graphics used to draw vertices
     */
    private void drawVertex(int[][] coordinates, Graphics2D graphics2d) {
        Color[] color = initializeColor();
        for (int i = 0; i < coordinates.length; i++) {
            int x = coordinates[i][0];
            int y = coordinates[i][1];
            graphics2d.setColor(color[coordinates[i][2] * 157 % color.length]);
            graphics2d.fillRoundRect(x, y, vertexSize, vertexSize, vertexSize, vertexSize);
        }
    }

    /**
     * Draws the edges of the given graph using the given graphics
     * 
     * @param net        The graph of the edges that will be drawn
     * @param graphics2d The graphics used to draw vertices
     */
    private void drawEdges(AbstractGraph<GraficNode, DefaultWeightedEdge> net, Graphics2D graphics2d) {
        DefaultWeightedEdge[] edges = net.edgeSet().toArray(new DefaultWeightedEdge[0]);
        graphics2d.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i < edges.length; i++) {
            GraficNode source = net.getEdgeSource(edges[i]);
            GraficNode dest = net.getEdgeTarget(edges[i]);
            int x1 = (int) Math.round(proportionX * (source.getX() - x_min)) + 10 + vertexSize / 2;
            int y1 = (int) Math.round(proportionY * (source.getY() - y_min)) + 10 + vertexSize / 2;
            int x2 = (int) Math.round(proportionX * (dest.getX() - x_min)) + 10 + vertexSize / 2;
            int y2 = (int) Math.round(proportionY * (dest.getY() - y_min)) + 10 + vertexSize / 2;
            graphics2d.drawLine(x1, y1, x2, y2);
        }
    }

    /**
     * Creates the coordinates (x, y and color) of each node of the given graph
     * 
     * @param net The graph that has to be represented.
     * @return The coordinates of each node.
     */
    private int[][] getCoordinates(AbstractGraph<GraficNode, DefaultWeightedEdge> net) {
        GraficNode[] points = net.vertexSet().toArray(new GraficNode[0]);
        this.x_max = points[0].getX();
        this.y_max = points[0].getY();
        this.x_min = points[0].getX();
        this.y_min = points[0].getY();
        for (int i = 1; i < points.length; i++) {
            double x = points[i].getX();
            double y = points[i].getY();
            if (x > x_max)
                x_max = x;
            if (x < x_min)
                x_min = x;
            if (y > y_max)
                y_max = y;
            if (y < y_min)
                y_min = y;
        }
        this.proportionX = (xSize - 20) / (x_max - x_min);
        this.proportionY = (ySize - 20) / (y_max - y_min);
        int[][] coordinates = new int[points.length][3];
        for (int i = 0; i < points.length; i++) {
            double x = points[i].getX();
            double y = points[i].getY();
            coordinates[i][0] = (int) Math.round(proportionX * (x - x_min)) + 10;
            coordinates[i][1] = (int) Math.round(proportionY * (y - y_min)) + 10;
            coordinates[i][2] = points[i].getCommunityId();
        }
        return coordinates;
    }

    /**
     * Initializes an array of <code>Color</code> removing all greys from the array
     * 
     * @return The array of <code>Color</code>
     */
    private Color[] initializeColor() {
        Color[] colori = new Color[125];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                for (int j2 = 0; j2 < 5; j2++) {
                    colori[i * 25 + j * 5 + j2] = new Color(51 * i + 51, 51 * j + 51, 51 * j2 + 51);
                }
            }
        }
        // Removing greyscale
        Color[] temp = new Color[120];
        int i2 = 0;
        for (int j = 0; j < colori.length; j++) {
            if (j % 31 != 0) {
                temp[j - i2] = colori[j];
            } else {
                i2++;
            }
        }
        return temp;
    }
}
