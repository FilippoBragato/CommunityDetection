package communitydetection;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;

import org.jgrapht.alg.drawing.FRLayoutAlgorithm2D;
import org.jgrapht.alg.drawing.model.Box2D;
import org.jgrapht.alg.drawing.model.MapLayoutModel2D;
import org.jgrapht.alg.drawing.model.Point2D;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import communitydetection.algorithm.FastFruchtermanReingold;
import communitydetection.algorithm.Louvain;
import communitydetection.graphmanagement.DrawGraph;
import communitydetection.graphmanagement.TestGraphCreator;
import communitydetection.graphnodes.Community;
import communitydetection.graphnodes.GraficNode;
import communitydetection.graphnodes.Node;
import communitydetection.graphnodes.SimpleNode;

/**
 * A simple demo for the project
 */
public class Demo {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Insert the number of communities");
        int l = scanner.nextInt();
        System.out.println("Insert the number of nodes for each community");
        int g = scanner.nextInt();
        System.out.println("Insert the expected value of the number of edges that every vertex shares with vertices of the same community");
        double z_in = scanner.nextDouble();
        System.out.println("Insert the expected value of the number of edges that every vertex shares with vertices of other communities");
        double z_out = scanner.nextDouble();
        TestGraphCreator creator = new TestGraphCreator(l, g, z_in, z_out);
        DefaultUndirectedWeightedGraph<Node, DefaultWeightedEdge> network = creator.lpartition();


        System.out.println("Insert the number of iteration for Louvain's Algorithm");
        int iterLou = scanner.nextInt();
        DefaultUndirectedWeightedGraph<Community, DefaultWeightedEdge> commNet;
        Louvain louvain = new Louvain(iterLou);
        long start = System.currentTimeMillis();
        commNet = louvain.apply(network);
        long end = System.currentTimeMillis();
        System.out.println("\n\nLouvain used " + (end-start)/1000.0 +"s");
        System.out.println("\nLouvain found " + commNet.vertexSet().size() +" communities");
        Community[] communities = commNet.vertexSet().toArray(new Community[0]);
        double fails = 0;
        for (int i = 0; i < communities.length; i++) {
            ArrayList<Node> nodes = communities[i].getNodes();
            System.out.print("Community " + i + " has "+nodes.size()+" nodes");
            int[] c = new int[l];
            if(commNet.vertexSet().size()>l)
                c = new int[commNet.vertexSet().size()];

            for (int j = 0; j < nodes.size(); j++) {
                int nodeId = ((SimpleNode)nodes.get(j)).getId();
                c[nodeId/g]++;
            }
            int tempFails = 0;
            int max=0;
            for (int k = 0; k < c.length; k++) {
                if(c[k]>max){
                    tempFails = tempFails + max;
                    max=c[k];
                }
                else{
                    tempFails = tempFails + c[k];
                }
            }
            fails +=tempFails;
            System.out.println("");
        }
        fails/=((l-1)*g);
        System.out.println("Louvain produced a relative error of " + fails +"\n\n");

        
        FRLayoutAlgorithm2D<Node, DefaultWeightedEdge> frLay = new FRLayoutAlgorithm2D<Node, DefaultWeightedEdge>();
        Box2D box = new Box2D(1000, 1000);
        start = System.currentTimeMillis();
        MapLayoutModel2D<Node> map = new MapLayoutModel2D<Node>(box);
        frLay.layout(network, map);
        end = System.currentTimeMillis();
        System.out.println("Fruchterman-Reingold Algorithm used " + (end-start)/1000.0 +"s\nYou can find the result in TrueFruchtermanReingold.png in main directory\n\n");
        DrawGraph drawer = new DrawGraph(1000,1000, 10);
        DefaultUndirectedWeightedGraph<GraficNode, DefaultWeightedEdge> drawableNet = drawer.makeDrawable(network, commNet);
        for (GraficNode GraficN : drawableNet.vertexSet()) {
            Node node = GraficN.getNode();
            Point2D point = map.get(node);
            GraficN.setX(point.getX());
            GraficN.setY(point.getY());
        }
        BufferedImage img = drawer.draw(drawableNet);
        try {
            ImageIO.write(img, "png", new File("TrueFruchtermanReingold.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Insert the number of final iteration for FastFruchtermanReingold Algorithm");
        int iterFR = scanner.nextInt();
        FastFruchtermanReingold rf = new FastFruchtermanReingold(iterFR);
        start = System.currentTimeMillis();
        drawableNet = rf.apply(network);
        end = System.currentTimeMillis();
        System.out.println("RecursiveFRalgorithm used " + (end-start)/1000.0 +"s\nYou can find the result in FastFruchtermanReingold.png in main directory");
        drawer = new  DrawGraph(1000,1000, 10);
        img = drawer.draw(drawableNet);
        try {
            ImageIO.write(img, "png", new File("FastFruchtermanReingold.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        scanner.close();
    }   
}
