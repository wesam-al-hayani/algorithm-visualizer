package dev.wesam.visualizer.structures;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public final class BinomialHeap {
    public static final class Node {
        public int key, degree;
        public Node parent, child, sibling;
        private Node(int key){this.key=key;}
    }
    private Node head;
    private int size;
    public int size(){return size;}
    public boolean isEmpty(){return head==null;}

    public Node insert(int key){BinomialHeap one=new BinomialHeap();Node node=new Node(key);one.head=node;one.size=1;union(one);return node;}
    public int findMinimum(){if(head==null)throw new NoSuchElementException();int minimum=head.key;for(Node n=head.sibling;n!=null;n=n.sibling)minimum=Math.min(minimum,n.key);return minimum;}

    public void union(BinomialHeap other){
        if(other==this)return;
        head=mergeRootLists(head,other.head);size+=other.size;other.head=null;other.size=0;
        if(head==null)return;
        Node previous=null,current=head,next=current.sibling;
        while(next!=null){
            if(current.degree!=next.degree||(next.sibling!=null&&next.sibling.degree==current.degree)){previous=current;current=next;}
            else if(current.key<=next.key){current.sibling=next.sibling;link(next,current);}
            else {if(previous==null)head=next;else previous.sibling=next;link(current,next);current=next;}
            next=current.sibling;
        }
    }

    public int extractMinimum(){
        if(head==null)throw new NoSuchElementException();
        Node min=head,minPrevious=null,previous=null;
        for(Node current=head;current!=null;current=current.sibling){if(current.key<min.key){min=current;minPrevious=previous;}previous=current;}
        if(minPrevious==null)head=min.sibling;else minPrevious.sibling=min.sibling;
        Node child=min.child,reversed=null;int childCount=(1<<min.degree)-1;
        while(child!=null){Node next=child.sibling;child.sibling=reversed;child.parent=null;reversed=child;child=next;}
        BinomialHeap children=new BinomialHeap();children.head=reversed;children.size=childCount;
        size-=childCount+1;union(children);return min.key;
    }

    public List<Integer> rootDegrees(){List<Integer> out=new ArrayList<>();for(Node n=head;n!=null;n=n.sibling)out.add(n.degree);return out;}
    public boolean invariantHolds(){int previousDegree=-1,count=0;for(Node n=head;n!=null;n=n.sibling){if(n.degree<=previousDegree)return false;previousDegree=n.degree;int nodes=validate(n,null);if(nodes<0)return false;count+=nodes;}return count==size;}
    private int validate(Node n,Node parent){if(n.parent!=parent)return-1;int children=0,count=1,expected=n.degree-1;for(Node c=n.child;c!=null;c=c.sibling){if(c.key<n.key||c.degree!=expected--)return-1;int sub=validate(c,n);if(sub<0)return-1;count+=sub;children++;}return children==n.degree?count:-1;}

    private static void link(Node child,Node parent){child.parent=parent;child.sibling=parent.child;parent.child=child;parent.degree++;}
    private static Node mergeRootLists(Node a,Node b){
        Node dummy=new Node(0),tail=dummy;
        while(a!=null&&b!=null){if(a.degree<=b.degree){tail.sibling=a;a=a.sibling;}else{tail.sibling=b;b=b.sibling;}tail=tail.sibling;}
        tail.sibling=a!=null?a:b;return dummy.sibling;
    }
}

