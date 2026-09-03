package dev.wesam.visualizer.algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class EducationalHashTable {
    public enum Strategy { SEPARATE_CHAINING, LINEAR_PROBING, QUADRATIC_PROBING, DOUBLE_HASHING }
    public record OperationResult(boolean success, int hash, List<Integer> probes, int collisions, String message) {
        public OperationResult { probes = List.copyOf(probes); }
    }

    private static final Integer TOMBSTONE = Integer.MIN_VALUE;
    private final Strategy strategy;
    private final List<List<Integer>> chains;
    private final Integer[] slots;

    public EducationalHashTable(int capacity, Strategy strategy) {
        if (capacity < 3) throw new IllegalArgumentException("capacity must be at least 3");
        this.strategy = strategy;
        if (strategy == Strategy.SEPARATE_CHAINING) {
            chains = new ArrayList<>();
            for (int i = 0; i < capacity; i++) chains.add(new ArrayList<>());
            slots = null;
        } else { chains = null; slots = new Integer[capacity]; }
    }

    public OperationResult insert(int key) {
        validateKey(key); int hash = hash(key); List<Integer> probes = new ArrayList<>(); int collisions = 0;
        if (chains != null) {
            probes.add(hash); List<Integer> bucket = chains.get(hash);
            if (bucket.contains(key)) return result(false, hash, probes, collisions, "Key already present");
            collisions = bucket.size(); bucket.add(key);
            return result(true, hash, probes, collisions, "Inserted into chain " + hash);
        }
        int firstTombstone = -1;
        for (int attempt = 0; attempt < slots.length; attempt++) {
            int index = probe(key, attempt); probes.add(index);
            if (slots[index] == null) {
                slots[firstTombstone >= 0 ? firstTombstone : index] = key;
                return result(true, hash, probes, collisions, "Inserted at slot " + (firstTombstone >= 0 ? firstTombstone : index));
            }
            if (slots[index].equals(key)) return result(false, hash, probes, collisions, "Key already present");
            if (slots[index].equals(TOMBSTONE)) { if (firstTombstone < 0) firstTombstone = index; }
            else collisions++;
        }
        if (firstTombstone >= 0) { slots[firstTombstone] = key; return result(true, hash, probes, collisions, "Reused deleted slot"); }
        return result(false, hash, probes, collisions, "Table is full");
    }

    public OperationResult search(int key) {
        validateKey(key); int hash = hash(key); List<Integer> probes = new ArrayList<>(); int collisions = 0;
        if (chains != null) {
            probes.add(hash); boolean found = chains.get(hash).contains(key);
            return result(found, hash, probes, Math.max(0, chains.get(hash).size() - (found ? 1 : 0)), found ? "Key found" : "Key not found");
        }
        for (int attempt = 0; attempt < slots.length; attempt++) {
            int index = probe(key, attempt); probes.add(index);
            if (slots[index] == null) break;
            if (slots[index].equals(key)) return result(true, hash, probes, collisions, "Key found at slot " + index);
            if (!slots[index].equals(TOMBSTONE)) collisions++;
        }
        return result(false, hash, probes, collisions, "Key not found");
    }

    public OperationResult delete(int key) {
        OperationResult search = search(key);
        if (!search.success) return new OperationResult(false, search.hash, search.probes, search.collisions, "Key not found");
        if (chains != null) chains.get(search.hash).remove(Integer.valueOf(key));
        else {
            for (int index : search.probes) if (slots[index] != null && slots[index].equals(key)) { slots[index] = TOMBSTONE; break; }
        }
        return new OperationResult(true, search.hash, search.probes, search.collisions, "Deleted key");
    }

    public void clear() {
        if (chains != null) chains.forEach(List::clear);
        else java.util.Arrays.fill(slots, null);
    }

    public int capacity() { return chains != null ? chains.size() : slots.length; }
    public List<String> snapshot() {
        List<String> result = new ArrayList<>();
        if (chains != null) for (List<Integer> chain : chains) result.add(chain.toString());
        else for (Integer slot : slots) result.add(slot == null ? "—" : slot.equals(TOMBSTONE) ? "×" : slot.toString());
        return Collections.unmodifiableList(result);
    }

    private int hash(int key) { return Math.floorMod(key, capacity()); }
    private int probe(int key, int attempt) {
        int base = hash(key), capacity = capacity();
        return switch (strategy) {
            case LINEAR_PROBING -> (base + attempt) % capacity;
            case QUADRATIC_PROBING -> (base + attempt * attempt) % capacity;
            case DOUBLE_HASHING -> (base + attempt * (1 + Math.floorMod(key, capacity - 1))) % capacity;
            default -> base;
        };
    }
    private OperationResult result(boolean success, int hash, List<Integer> probes, int collisions, String message) {
        return new OperationResult(success, hash, probes, collisions, message);
    }
    private void validateKey(int key) { if (key == TOMBSTONE) throw new IllegalArgumentException("reserved key"); }
}

