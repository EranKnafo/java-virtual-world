import java.util.Comparator;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// This current (given) implementation is not as good as Astar pathing, seeimingly purposeful

class SingleStepPathingStrategy
        implements PathingStrategy {
    public List<Point> computePath(Point start, Point end,
                                   Predicate<Point> canPassThrough,
                                   BiPredicate<Point, Point> withinReach,
                                   Function<Point, Stream<Point>> potentialNeighbors) {
        /* Does not check withinReach.  Since only a single step is taken
         * on each call, the caller will need to check if the destination
         * has been reached.
         */
//        return potentialNeighbors.apply(start)
//                .filter(canPassThrough)
//                .filter(pt ->
//                        !pt.equals(start)
//                                && !pt.equals(end)
//                                && Math.abs(end.x - pt.x) <= Math.abs(end.x - start.x)
//                                && Math.abs(end.y - pt.y) <= Math.abs(end.y - start.y))
//                .limit(1)
//                .collect(Collectors.toList());
//    }

        // a possible way to improve this is by using this version that
        // won't let an entity get stuck for too long. This is just a thought
        // but it does not align with the fact that single path takes the nearest
        // neighbor to the goal. This uses the comparator import at the top (commented out)
        return potentialNeighbors.apply(start)
                .filter(canPassThrough)
                .filter(pt -> !pt.equals(start) && !pt.equals(end))
                .filter(pt -> pt.distanceSquared(end) < start.distanceSquared(end))
                .sorted(Comparator.comparingInt(pt -> pt.distanceSquared(end)))
                .limit(1)
                .collect(Collectors.toList());
    }
}
