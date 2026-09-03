package dev.wesam.visualizer.algorithms;

import java.util.Arrays;

public final class UnionFind {
    private final int[] parent;
    private final int[] rank;

    public UnionFind(int size) {
        if (size < 0) throw new IllegalArgumentException("size cannot be negative");
        parent = new int[size];
        rank = new int[size];
        for (int i = 0; i < size; i++) parent[i] = i;
    }

    public int find(int item) {
        check(item);
        if (parent[item] != item) parent[item] = find(parent[item]);
        return parent[item];
    }

    public boolean union(int first, int second) {
        int a = find(first), b = find(second);
        if (a == b) return false;
        if (rank[a] < rank[b]) parent[a] = b;
        else if (rank[a] > rank[b]) parent[b] = a;
        else { parent[b] = a; rank[a]++; }
        return true;
    }

    public boolean connected(int first, int second) { return find(first) == find(second); }
    public int[] parents() { return parent.clone(); }
    public int[] ranks() { return rank.clone(); }

    private void check(int item) {
        if (item < 0 || item >= parent.length) throw new IndexOutOfBoundsException(item);
    }

    @Override public String toString() { return "parents=" + Arrays.toString(parent); }
}

