package com.omwh.utils;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/** Captures the exact parent edges of a mounted entity tree for post-teleport verification. */
public final class MountTreeSnapshot<T> {
    public record Edge<T>(T parent, T child) {
        public Edge {
            Objects.requireNonNull(parent, "parent");
            Objects.requireNonNull(child, "child");
        }
    }

    private final List<Edge<T>> edges;

    public MountTreeSnapshot(List<Edge<T>> edges) {
        this.edges = List.copyOf(edges);
    }

    public boolean isIntact(Function<T, T> currentParent) {
        Objects.requireNonNull(currentParent, "currentParent");
        return edges.stream().allMatch(edge -> currentParent.apply(edge.child()) == edge.parent());
    }
}
