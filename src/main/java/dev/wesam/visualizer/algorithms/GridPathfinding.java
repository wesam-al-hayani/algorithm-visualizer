package dev.wesam.visualizer.algorithms;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public final class GridPathfinding {
    private GridPathfinding() { }
    public enum Method { BFS, DFS, DIJKSTRA, A_STAR }
    public record Cell(int row, int column) { }
    public record Result(List<Cell> path, List<Cell> visited, int cost) {
        public Result { path = List.copyOf(path); visited = List.copyOf(visited); }
    }
    private static final int[][] DIRECTIONS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    public static Result find(boolean[][] walls, Cell start, Cell target, Method method) {
        int rows = walls.length, columns = rows == 0 ? 0 : walls[0].length;
        if (!valid(start, rows, columns) || !valid(target, rows, columns)) throw new IllegalArgumentException("invalid endpoint");
        for (boolean[] row : walls) if (row.length != columns) throw new IllegalArgumentException("ragged grid");
        if (walls[start.row][start.column] || walls[target.row][target.column]) return new Result(List.of(), List.of(), -1);
        int total = rows * columns, source = id(start, columns), goal = id(target, columns);
        int[] parent = new int[total], distance = new int[total]; Arrays.fill(parent, -1); Arrays.fill(distance, Integer.MAX_VALUE);
        boolean[] done = new boolean[total]; List<Cell> visited = new ArrayList<>(); distance[source] = 0;
        if (method == Method.BFS || method == Method.DFS) {
            ArrayDeque<Integer> frontier = new ArrayDeque<>(); frontier.add(source);
            while (!frontier.isEmpty()) {
                int current = method == Method.DFS ? frontier.removeLast() : frontier.removeFirst();
                if (done[current]) continue; done[current] = true; visited.add(cell(current, columns));
                if (current == goal) break;
                for (int next : neighbors(current, walls, rows, columns)) if (!done[next] && parent[next] == -1) {
                    parent[next] = current; distance[next] = distance[current] + 1; frontier.add(next);
                }
            }
        } else {
            PriorityQueue<Node> frontier = new PriorityQueue<>(Comparator.comparingInt(Node::priority));
            frontier.add(new Node(source, 0));
            while (!frontier.isEmpty()) {
                int current = frontier.remove().id;
                if (done[current]) continue; done[current] = true; visited.add(cell(current, columns));
                if (current == goal) break;
                for (int next : neighbors(current, walls, rows, columns)) {
                    int candidate = distance[current] + 1;
                    if (candidate < distance[next]) {
                        distance[next] = candidate; parent[next] = current;
                        int heuristic = method == Method.A_STAR ? manhattan(cell(next, columns), target) : 0;
                        frontier.add(new Node(next, candidate + heuristic));
                    }
                }
            }
        }
        if (!done[goal]) return new Result(List.of(), visited, -1);
        List<Cell> path = new ArrayList<>();
        for (int at = goal; at != -1; at = parent[at]) path.add(cell(at, columns));
        Collections.reverse(path);
        return new Result(path, visited, distance[goal]);
    }

    private static List<Integer> neighbors(int id, boolean[][] walls, int rows, int columns) {
        Cell cell = cell(id, columns); List<Integer> result = new ArrayList<>(4);
        for (int[] d : DIRECTIONS) { int r = cell.row + d[0], c = cell.column + d[1]; if (r >= 0 && c >= 0 && r < rows && c < columns && !walls[r][c]) result.add(r * columns + c); }
        return result;
    }
    private static int manhattan(Cell a, Cell b) { return Math.abs(a.row - b.row) + Math.abs(a.column - b.column); }
    private static boolean valid(Cell cell, int rows, int columns) { return cell.row >= 0 && cell.column >= 0 && cell.row < rows && cell.column < columns; }
    private static int id(Cell cell, int columns) { return cell.row * columns + cell.column; }
    private static Cell cell(int id, int columns) { return new Cell(id / columns, id % columns); }
    private record Node(int id, int priority) { }
}

