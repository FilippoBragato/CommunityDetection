package communitydetection.algorithm;

import java.util.HashSet;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.jgrapht.alg.drawing.FRLayoutAlgorithm2D;
import org.jgrapht.alg.drawing.FRLayoutAlgorithm2D.TemperatureModel;
import org.jgrapht.alg.drawing.model.Box2D;
import org.jgrapht.alg.drawing.model.LayoutModel2D;
import org.jgrapht.alg.drawing.model.MapLayoutModel2D;
import org.jgrapht.alg.drawing.model.Point2D;
import org.jgrapht.graph.AbstractGraph;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import communitydetection.graphmanagement.DrawGraph;
import communitydetection.graphnodes.Community;
import communitydetection.graphnodes.GraficNode;
import communitydetection.graphnodes.Node;

/**
 * Faster version of Fruchterman-Reingold algorithm
 * 
 * @author Filippo Bragato
 */
public class FastFruchtermanReingold implements
        Function<DefaultUndirectedWeightedGraph<Node, DefaultWeightedEdge>, DefaultUndirectedWeightedGraph<GraficNode, DefaultWeightedEdge>> {

    private int finalIteration = 5;

    /**
     * Constructor that sets the number of final iteration of the algorithm.
     * Execution time is linearly proportional to this parameter, with a large
     * number of final iteration the result will be the same of the standard
     * Fruchterman-Reingold algorithm.
     * 
     * @param finalIteration The number of final iteration
     */
    public FastFruchtermanReingold(int finalIteration) {
        this.finalIteration = finalIteration;
    }

    /**
     * Creates a drawable graph, representing the given one, in which vertices are set according to Fruchterman-Reingold algorithm.
     * @param net The graph that will be represented
     * @return The drawable graph representing the given one
     */
    @Override
    public DefaultUndirectedWeightedGraph<GraficNode, DefaultWeightedEdge> apply(
            DefaultUndirectedWeightedGraph<Node, DefaultWeightedEdge> net) {

        DrawGraph drawer = new DrawGraph(500, 500, 10);
        Louvain lou = new Louvain();

        DefaultUndirectedWeightedGraph<Community, DefaultWeightedEdge> communityNet = lou.apply(net);

        DefaultUndirectedWeightedGraph<GraficNode, DefaultWeightedEdge> drNet = drawer.makeDrawable(net, communityNet);

        FRLayoutAlgorithm2D<Community, DefaultWeightedEdge> frLay = new FRLayoutAlgorithm2D<Community, DefaultWeightedEdge>();
        Box2D commBox = new Box2D(1000, 1000);
        LayoutModel2D<Community> commMap = new MapLayoutModel2D<Community>(commBox);
        frLay.layout(communityNet, commMap);

        Box2D graphBox = new Box2D(1000, 1000);
        LayoutModel2D<Node> graphMap = new MapLayoutModel2D<Node>(graphBox);

        for (Community community : communityNet.vertexSet()) {
            AbstractGraph<Node, DefaultWeightedEdge> subG = new AsSubgraph<Node, DefaultWeightedEdge>(net,
                    new HashSet<Node>(community.getNodes()));

            FRLayoutAlgorithm2D<Node, DefaultWeightedEdge> fr = new FRLayoutAlgorithm2D<Node, DefaultWeightedEdge>();
            Box2D box = new Box2D(1000 / Math.sqrt(communityNet.vertexSet().size()),
                    1000 / Math.sqrt(communityNet.vertexSet().size()));
            LayoutModel2D<Node> map = new MapLayoutModel2D<Node>(box);
            fr.layout(subG, map);

            double offsetX = commMap.get(community).getX();
            double offsetY = commMap.get(community).getY();

            for (Node node : subG.vertexSet()) {
                Point2D point = new Point2D(offsetX + map.get(node).getX(), offsetY + map.get(node).getY());
                graphMap.put(node, point);
            }
        }
        FRLayoutAlgorithm2D<Node, DefaultWeightedEdge> fr = new FRLayoutAlgorithm2D<Node, DefaultWeightedEdge>(
                finalIteration, 0.5, new TemperatureModelSupplier<>(), new Random());

        fr.setInitializer(new Keep<Node>(graphMap));

        fr.layout(net, graphMap);

        for (Node node : net.vertexSet()) {
            node.getGrafical().setX(graphMap.get(node).getX());
            node.getGrafical().setY(graphMap.get(node).getY());
        }
        return drNet;
    }

    private class Keep<V> implements Function<V, Point2D> {

        private LayoutModel2D<V> map;

        public Keep(LayoutModel2D<V> map) {
            this.map = map;
        }

        @Override
        public Point2D apply(V v) {
            return map.get(v);
        }

    }

    private class InverseLinearTemperatureModel implements TemperatureModel {
        private double a;
        private double b;

        /**
         * Create a new inverse linear temperature model.
         * 
         * @param a a
         * @param b b
         */
        public InverseLinearTemperatureModel(double a, double b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public double temperature(int iteration, int maxIterations) {
            if (iteration >= maxIterations - 1) {
                return 0.0;
            }
            return a * iteration + b;
        }

    }

    private class TemperatureModelSupplier<V> implements BiFunction<LayoutModel2D<V>, Integer, TemperatureModel> {

        @Override
        public TemperatureModel apply(LayoutModel2D<V> lay, Integer i) {
            return new InverseLinearTemperatureModel(-1, i);
        }
    }
}
