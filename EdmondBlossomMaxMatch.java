
import java.io.BufferedReader; 
import java.io.File; 
import java.io.FileReader; 
import java.io.StringReader; 
import java.util.ArrayList; 
import java.util.Collections; 
import java.util.HashMap; 
import java.util.LinkedList; 
import java.util.List; 

import javax.xml.crypto.dsig.spec.HMACParameterSpec; 

import java.util.LinkedHashSet; 
import java.util.Set; 


/** 
 * Get Maximum matching in general undirected graph Uses Edmonds blossom 
 * matching algorithm 
 *  
 * @author austin 
 *  
 */ 
public class EdmondBlossomMaxMatch {

    int earr[],narr[];
    enum MNodeType { 
        EVEN, ODD, FREE 
    } 
    class MNode { 

        int label = 0; 
        MNodeType type; 
        MNode parent = null; 
        LinkedHashSet<MNode> nbrs = new LinkedHashSet<MNode>(); 
        boolean contracted = false;
        MNode matchedWith = null; 
        int forIteration; 
    
        public Set<MNode> getNbrs() { 
            return nbrs; 
        } 
    
        void addNbr(MNode nbr) { 
            if (nbr == null) { 
                return; 
            } 
            if (nbr == this) { 
                return; 
            } 
            if (nbrs.contains(nbr)) { 
                return; 
            } 
            nbrs.add(nbr); 
        } 
    
        void removeNbr(MNode nbr) { 
    
            nbrs.remove(nbr); 
    
        } 
    
        public MNode getMatchedWith() { 
            return matchedWith; 
        } 
    
        public void setMatchedWith(MNode matchedWith) {
            this.matchedWith = matchedWith; 
        } 
    
        @Override 
        public String toString() { 
            return "" + label; 
        } 
    
    } 
    
    class Blossom { 
        MNode root; 
        List<MNode> cycle; 
        MNode blossomAlias; 
    }
    

    class Graph { 
        int nodeCount; 

        ArrayList<MNode> nodes = new ArrayList<MNode>(); 
    } 


    final static int BLOSSOMLABEL = -1; 

    static boolean verbose = true;

    Graph g = null; 
    //list of current blossoms stored in a map 
    HashMap<MNode, Blossom> currBlossoms = new HashMap<MNode, Blossom>();  
    // current list of Blossoms in a List 
    ArrayList<Blossom> currB = new ArrayList<Blossom>();  
    int bLabel = -2; // Blossom start label
    MNode currFreeNode = null; 
     
    void edmondExec() { 
        creatMAlternatingTrees(g.nodes); 
        printMatchings(true); 
    } 

    /** 
     * Take all the freenodes and find an alternating path with it If found then 
     * , update the augmenting path with new matchings 
     *  
     * @param freeNodes 
     */ 
    void creatMAlternatingTrees(ArrayList<MNode> freeNodes) {
        int iter = 1; 
        MNode augNode = null; 
        for (MNode freeNode : freeNodes) { 
            if (freeNode.type != MNodeType.FREE) 
                continue; 
            currFreeNode = freeNode; 
            if ((augNode = createMAlternatingTree(freeNode, iter, null)) != null) {
                while ((augNode != null) && (augNode.label < BLOSSOMLABEL))// blossom 
                { 
                    augNode = createMAlternatingTree(freeNode, ++iter, null);
                } 
                if (augNode != null) {
                    updateAugmentingPath(augNode, iter); 
                } 
            } 
            if (augNode == null) {
                updateNode(freeNode, iter, null, MNodeType.FREE);
            } 
            expandAllBlossoms(iter); 

            iter++; 
        } 
    } 

    /** 
     * Find alternating paths recursively. Node parameter will only be Even 
     * nodes. 
     *  
     * @param node 
     * @param iter 
     * @param parent 
     * @return 
     */ 
    MNode createMAlternatingTree(MNode node, int iter, MNode parent) {

        ArrayList<MNode> cycle; 
        MNode nodeMatched = null; 
        Blossom blossom = null; 
        updateNode(node, iter, parent, MNodeType.EVEN); 
        LinkedList<MNode> nbrsQ = new LinkedList<MNode>(node.getNbrs()); 
        MNode nbr = null; 
        while ((nbr = nbrsQ.poll()) != null) { 
            if (nbr.contracted) 
                continue; 
            if ((nbr == node)) 
                continue; 
            if (nbr.forIteration != iter) { 
                if (nbr.type == MNodeType.FREE) { 
                    updateNode(nbr, iter, node, MNodeType.ODD); 
                    return nbr; 
                } else { 
                    updateNode(nbr, iter, node, MNodeType.ODD); 
                    nodeMatched = nbr.matchedWith; 
                    MNode nodeInPath = null; 
                    nodeInPath = createMAlternatingTree(nodeMatched, iter, nbr); 

                    if (nodeInPath != null) {
                        return nodeInPath; 
                    } else { 
                        updateNode(node, iter, parent, MNodeType.EVEN); 
                        continue; 
                    } 
                } 
            } 
            if (nbr.type == MNodeType.ODD) { 
                continue; 
            } 

            if (nbr.type == MNodeType.EVEN) { 
                cycle = findBlossom(nbr, node, iter); 
                if (cycle == null) { // root.parent == null, we can ignore this
                    continue; 
                } 
                blossom = contractBlossom(cycle.subList(0, cycle.size()),
                        cycle.get(cycle.size() - 1), iter); 
                currBlossoms.put(blossom.blossomAlias, blossom); 
                currB.add(blossom); 
                return blossom.blossomAlias; 
            } 
        } 
        return null; 
    } 

    /** 
     * Find the Odd cycle that can be contracted into a blossom 
     *  
     * @param node 
     * @param nbr 
     * @param iter 
     * @return 
     */ 
    ArrayList<MNode> findBlossom(MNode node, MNode nbr, int iter) {

        ArrayList<MNode> cycle = new ArrayList<MNode>(); 
        MNode root = node; 
        MNode origNbr = nbr; 

        if ((root == currFreeNode) || (nbr == currFreeNode)) {
            return null; 
        } 
        cycle.add(root); 

        while ((nbr != null) && (nbr != root)) { 
            cycle.add(nbr); 
            nbr = nbr.parent; 
        } 
        root.parent = origNbr; 
        cycle.add(root); 
        return cycle; 
    } 

    /** 
     * Contract the blossom into a Blossom structure, updating the nbrs of the 
     * nodes comprising the odd cycle 
     *  
     * @param cycle 
     * @param root 
     * @param iter 
     * @return 
     */ 
    Blossom contractBlossom(List<MNode> cycle, MNode root, int iter) {

        MNode bALias = new MNode(); 
        MNode node = null; 
        bALias.forIteration = iter; 

        bALias.type = MNodeType.EVEN; 
        bALias.label = bLabel--; 

        Blossom blossom = new Blossom(); 
        blossom.cycle = cycle; 
        blossom.root = root; 
        blossom.blossomAlias = bALias; 
        for (int i = 0; i < cycle.size(); i++) {
            node = cycle.get(i); 
            node.contracted = true; 
        } 

        for (int i = 0; i < cycle.size(); i++) {
            node = cycle.get(i); 
            for (MNode nbr : node.getNbrs()) { 
                if (nbr.contracted) 
                    continue; 

                nbr.addNbr(bALias); 
                bALias.addNbr(nbr); 
            } 
            node.forIteration = iter; 
        } 

        if (root.matchedWith != null) {
            bALias.matchedWith = root.matchedWith; 
            root.matchedWith.matchedWith = bALias; 
        } 

        return blossom; 
    } 

    /** 
     * Expand the blossom, update the neighbors and the parent pointers 
     *  
     * @param blossom 
     * @param stem 
     * @param antennae 
     * @param iter 
     * @return 
     */ 
    MNode expandBlossom(Blossom blossom, MNode stem, MNode antennae, int iter) {
        List<MNode> cycle = blossom.cycle; 
        MNode newRoot = null; 
        MNode newOut = null; 

        for (MNode node : cycle) { 
            for (MNode nbr : node.getNbrs()) { 
                if (nbr.contracted) 
                    continue; 
                nbr.removeNbr(blossom.blossomAlias); 
                if ((newRoot == null) && (nbr == stem)
                        && (node == blossom.root)) { 
                    newRoot = node; 
                } else if ((newOut == null) && (nbr == antennae)) {
                    newOut = node; 
                } 
            } 
            node.forIteration = iter; 
        } 
        for (MNode node : cycle) { 
            node.contracted = false; 
        } 

        if (newRoot == null)
            newRoot = blossom.root; 

        if ((blossom.root != null)
                && (blossom.root.matchedWith != null) 
                && (blossom.root.matchedWith.matchedWith == blossom.blossomAlias)) { 
            blossom.root.matchedWith.matchedWith = blossom.root; 
        } 

        antennae.parent = newOut; 

        if (newRoot == blossom.root) { 
            if (newOut.matchedWith.parent == newOut) { 
                Collections.reverse(cycle); 
                MNode n = null; 
                for (int i = 0; i < cycle.size() - 1; i++) {
                    n = cycle.get(i); 
                    n.parent = cycle.get(i + 1); 
                } 
            } 
        } else { 

            if (newRoot.parent == newRoot.matchedWith) { 
                Collections.reverse(cycle); 
                MNode n = null; 
                for (int i = 0; i < cycle.size() - 1; i++) {
                    n = cycle.get(i); 
                    n.parent = cycle.get(i + 1); 
                } 
            } 
        } 
        if (newRoot != null)
            newRoot.parent = stem; 
        return newOut; 
    } 

    /** 
     * Expand all the blossoms. Will be called when no alternating path through 
     * the existing blossoms can be found 
     *  
     * @param iter 
     */ 
    void expandAllBlossoms(int iter) {
        Blossom blossom = null; 

        for (int i = currB.size() - 1; i >= 0; i--) { 
            blossom = currB.get(i); 
            List<MNode> cycle = blossom.cycle; 
            for (MNode node : cycle) { 
                node.contracted = false; 
                for (MNode nbr : node.getNbrs()) { 
                    if (nbr.contracted) 
                        continue; 
                    nbr.removeNbr(blossom.blossomAlias); 
                } 
                node.forIteration = iter; 
            } 
            if ((blossom.root != null)
                    && (blossom.root.matchedWith != null)
                    && (blossom.root.matchedWith.matchedWith == blossom.blossomAlias)) { 
                blossom.root.matchedWith.matchedWith = blossom.root; 
            } 
        } 
        currB.clear(); 
        currBlossoms.clear(); 
    } 

    /** 
     * Update the alternating path , with new matchings, expanding blossoms 
     * along the path 
     *  
     * @param pathNode 
     * @param iter 
     */ 
    void updateAugmentingPath(MNode pathNode, int iter) {

        MNode n = pathNode; 
        MNode np = null; 
        MNode npp = null; 
        Blossom blossom; 

        while (n != null) { 
            np = n.parent; 
            npp = np.parent; 

            if (np.label < BLOSSOMLABEL) { 
                blossom = currBlossoms.get(np); 
                while ((n.parent = expandBlossom(blossom, npp, n, iter)).label < BLOSSOMLABEL) {

                    np = n.parent; 
                    blossom = currBlossoms.get(np); 
                    npp = np.parent; 
                } 
                np = n.parent; 
                npp = np.parent; 
            } 
            if ((npp != null) && (npp.label < BLOSSOMLABEL)) {
                blossom = currBlossoms.get(npp); 
                while ((np.parent = expandBlossom(blossom, npp.parent, np, iter)).label < BLOSSOMLABEL) {
                    npp = np.parent; 
                    blossom = currBlossoms.get(np); 
                } 
                npp = np.parent; 
            } 
            n.matchedWith = np; 
            np.matchedWith = n; 
            n = npp; 
        } 
    } 

    void updateNode(MNode node, int iter, MNode parent, MNodeType type) {
        node.forIteration = iter; 
        node.parent = parent; 
        node.type = type; 
    } 
     


    void readNextGraph(BufferedReader bufReader) throws Exception {
        try { 

            int nodesCount = Integer.parseInt(bufReader.readLine());
            int edgesCount = Integer.parseInt(bufReader.readLine());
            g = new Graph(); 
            g.nodeCount = nodesCount; 
            int u,v;
            narr = new int[nodesCount];
            earr = new int[edgesCount*2];
            initGraph();
                 
            for(int j=0;j< nodesCount;j++)
            {
                String[] strarr = bufReader.readLine().split(",");
                narr[j]=Integer.parseInt(strarr[2]);
            }
            for (int k = 0; k < edgesCount; k++)
            {
                String[] strArr = bufReader.readLine().split(","); u=0;v=0;
                u = Integer.parseInt(strArr[0]);
                v = Integer.parseInt(strArr[1]);
                createNode(u,v); 
            } 
        } catch (Exception e) { 
            e.printStackTrace(); 
            throw e; 
        } 
    }
     void changegraph(BufferedReader bufReader) throws Exception {
         try{
                
            int u,v;
            int nodesCount = Integer.parseInt(bufReader.readLine());
            int edgesCount = Integer.parseInt(bufReader.readLine());
            for(int j=0;j< nodesCount;j++)
            {
                String[] strarr = bufReader.readLine().split(",");
                narr[j]=Integer.parseInt(strarr[2]);
            }
            
            for (int k = 0; k < edgesCount; k++)
            {
                String[] strArr = bufReader.readLine().split(",");
                u = Integer.parseInt(strArr[0]);
                v = Integer.parseInt(strArr[1]);
                for(int m=3;m<=5;m++)
                {
                    strArr[m]="0";
                }
                for(int l=0;l<edgesCount;l=l+2)
                {
                    if(u== earr[l] || u== earr[l+1])
                        {
                            if(v==earr[l] || v== earr[l+1])
                                strArr[3]="255";
                        }
                } 
            } 
        } catch (Exception e) { 
            e.printStackTrace(); 
            throw e; 
        }
         
     } 

    void initGraph() { 
        for (int i = 0; i < g.nodeCount; i++) {
            g.nodes.add(createNode(i)); 
        } 
        currBlossoms.clear(); 
    } 

    MNode createNode(int u) { 
        MNode node = null; 

        node = new MNode(); 
        node.label = u; 
        node.type = MNodeType.FREE; 
        // g.nodes.set(u, node); 

        return node; 
    } 

    void createNode(int u, int v) {
        ArrayList<Integer> nodes = new ArrayList<Integer>(); 

        g.nodes.get(u).addNbr(g.nodes.get(v)); 
        g.nodes.get(v).addNbr(g.nodes.get(u)); 
    } 

    void printMatchings(boolean print) {
        if ((!print) && (!verbose)) 
            return; 
        // ArrayList<MNode> matched = new ArrayList<MNode>(); 
        
        int c = 0; 
        for (MNode node : g.nodes) { 
            if (node.matchedWith == null)
                continue; 
            earr[c]=node.label;
            earr[c+1]=node.matchedWith.label;
            c=c+2;
            
        }  
    } 

    public static void main(String filepath) {
        BufferedReader bufReader = null; 
        if (filepath.length() < 0) {
            System.out.println(filepath);
            // Unit Test Mode 
            bufReader = new BufferedReader( 
                    new StringReader( 
                            "1\n10\n10\n0 1\n0 2\n2 3\n3 4\n4 5\n5 0\n6 1\n4 7\n8 1\n7 9\n")); 
        } else { 
             
                try { 
                    bufReader = new BufferedReader(new FileReader(filepath)); 
                } catch (Exception e) { 
                    e.printStackTrace(); 
                    return; 
                } 

                EdmondBlossomMaxMatch dsp = new EdmondBlossomMaxMatch();
                try {

                        dsp.readNextGraph(bufReader); 
                        dsp.edmondExec(); 
                        dsp.changegraph(bufReader);
                } catch (Exception e) { 
                    System.err.println("Exiting : " + e); 
                    e.printStackTrace(); 
                } finally { 
                    try { 
                        bufReader.close(); 
                    } catch (Exception f) { 

                    } 
                } 
        } 
    } 

} 