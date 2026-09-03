package dev.wesam.visualizer.structures;

import java.util.ArrayList;
import java.util.List;

/** Educational B-tree insertion/search implementation with configurable minimum degree. */
public final class BTree {
    public static final class Node {
        public final List<Integer> keys = new ArrayList<>();
        public final List<Node> children = new ArrayList<>();
        public boolean leaf;
        private Node(boolean leaf) { this.leaf = leaf; }
    }
    private final int degree;
    private Node root = new Node(true);
    private int splits;

    public BTree(int minimumDegree) { if(minimumDegree<2)throw new IllegalArgumentException("minimum degree must be at least 2");degree=minimumDegree; }
    public Node root(){return root;}
    public int splits(){return splits;}

    public boolean contains(int key){return contains(root,key);}
    private boolean contains(Node node,int key){int i=0;while(i<node.keys.size()&&key>node.keys.get(i))i++;if(i<node.keys.size()&&key==node.keys.get(i))return true;return !node.leaf&&contains(node.children.get(i),key);}

    public boolean insert(int key){
        if(contains(key))return false;
        if(root.keys.size()==2*degree-1){Node newRoot=new Node(false);newRoot.children.add(root);splitChild(newRoot,0);root=newRoot;}
        insertNonFull(root,key);return true;
    }
    private void insertNonFull(Node node,int key){
        int i=node.keys.size()-1;
        if(node.leaf){node.keys.add(0);while(i>=0&&key<node.keys.get(i)){node.keys.set(i+1,node.keys.get(i));i--;}node.keys.set(i+1,key);}
        else {while(i>=0&&key<node.keys.get(i))i--;i++;if(node.children.get(i).keys.size()==2*degree-1){splitChild(node,i);if(key>node.keys.get(i))i++;}insertNonFull(node.children.get(i),key);}
    }
    private void splitChild(Node parent,int childIndex){
        Node full=parent.children.get(childIndex),right=new Node(full.leaf);int median=full.keys.get(degree-1);
        for(int j=degree;j<full.keys.size();j++)right.keys.add(full.keys.get(j));
        while(full.keys.size()>degree-1)full.keys.remove(full.keys.size()-1);
        if(!full.leaf){for(int j=degree;j<full.children.size();j++)right.children.add(full.children.get(j));while(full.children.size()>degree)full.children.remove(full.children.size()-1);}
        parent.keys.add(childIndex,median);parent.children.add(childIndex+1,right);splits++;
    }

    public List<Integer> inOrder(){List<Integer> out=new ArrayList<>();walk(root,out);return out;}
    private void walk(Node n,List<Integer> out){for(int i=0;i<n.keys.size();i++){if(!n.leaf)walk(n.children.get(i),out);out.add(n.keys.get(i));}if(!n.leaf)walk(n.children.get(n.keys.size()),out);}
    public boolean invariantsHold(){return validate(root,true,0,new int[]{-1},Long.MIN_VALUE,Long.MAX_VALUE);}
    private boolean validate(Node n,boolean isRoot,int depth,int[] leafDepth,long low,long high){
        if(!isRoot&&(n.keys.size()<degree-1||n.keys.size()>2*degree-1))return false;
        for(int i=0;i<n.keys.size();i++){int key=n.keys.get(i);if(key<=low||key>=high||(i>0&&n.keys.get(i-1)>=key))return false;}
        if(n.leaf){if(leafDepth[0]<0)leafDepth[0]=depth;return leafDepth[0]==depth;}
        if(n.children.size()!=n.keys.size()+1)return false;
        for(int i=0;i<n.children.size();i++){long childLow=i==0?low:n.keys.get(i-1),childHigh=i==n.keys.size()?high:n.keys.get(i);if(!validate(n.children.get(i),false,depth+1,leafDepth,childLow,childHigh))return false;}
        return true;
    }
}

