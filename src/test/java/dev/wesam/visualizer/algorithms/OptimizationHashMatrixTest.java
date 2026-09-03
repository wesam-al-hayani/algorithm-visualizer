package dev.wesam.visualizer.algorithms;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class OptimizationHashMatrixTest {
    @Test void everyHashStrategyHandlesCollisionsSearchAndDelete() {
        for (EducationalHashTable.Strategy strategy : EducationalHashTable.Strategy.values()) {
            EducationalHashTable table = new EducationalHashTable(11, strategy);
            assertTrue(table.insert(1).success()); assertTrue(table.insert(12).success());
            assertTrue(table.search(12).success()); assertTrue(table.delete(1).success());
            assertFalse(table.search(1).success()); assertTrue(table.search(12).success());
            assertTrue(table.insert(-3).success()); table.clear(); assertFalse(table.search(12).success());
        }
    }

    @Test void knapsackDpAndBranchBoundAgree() {
        int[] weights = {2,3,4,5}, values = {3,4,5,8};
        assertEquals(12, OptimizationAlgorithms.knapsack(weights, values, 8).maximumValue());
        assertEquals(12, OptimizationAlgorithms.branchAndBoundKnapsack(weights, values, 8).maximumValue());
    }

    @Test void tspVariantsAgree() {
        int[][] distances = {{0,10,15,20},{10,0,35,25},{15,35,0,30},{20,25,30,0}};
        assertEquals(80, OptimizationAlgorithms.heldKarp(distances).cost());
        assertEquals(80, OptimizationAlgorithms.bruteForceTsp(distances).cost());
    }

    @Test void exactAndApproximationProblems() {
        List<GraphAlgorithms.Edge> triangle = List.of(new GraphAlgorithms.Edge(0,1), new GraphAlgorithms.Edge(1,2), new GraphAlgorithms.Edge(2,0));
        assertEquals(2, OptimizationAlgorithms.exactVertexCover(3, triangle).vertices().size());
        assertTrue(OptimizationAlgorithms.approximateVertexCover(3, triangle).vertices().size() <= 4);
        assertEquals(2, OptimizationAlgorithms.exactMaxCut(3, triangle).weight());
        var maxSat = OptimizationAlgorithms.exactMaxSat(2, List.of(new int[]{1}, new int[]{-1,2}, new int[]{-2}));
        assertEquals(2, maxSat.satisfiedClauses());
    }

    @Test void strassenMatchesOrdinaryIncludingPaddedSize() {
        int[][] a = {{1,2,3},{4,5,6},{7,8,9}}, b = {{9,8,7},{6,5,4},{3,2,1}};
        assertArrayEquals(MatrixAlgorithms.ordinaryMultiply(a,b), MatrixAlgorithms.strassen(a,b));
    }
}

