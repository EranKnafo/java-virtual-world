import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AStarPathingStrategy implements PathingStrategy {

    @Override
    public List<Point> computePath(
            Point start,
            Point end,
            Predicate<Point> canPassThrough,
            BiPredicate<Point, Point> withinReach,
            Function<Point, Stream<Point>> potentialNeighbors) {

        if (start.equals(end) || withinReach.test(start, end)) {
            return java.util.Collections.emptyList();
        }
        Map<Point, Integer> gScore = new HashMap<>();
        Map<Point, Integer> fScore = new HashMap<>();
        Map<Point, Point> cameFrom = new HashMap<>();
        Set<Point> closed = new HashSet<>();

        Comparator<Point> cmp = Comparator.comparingInt(p -> fScore.getOrDefault(p, Integer.MAX_VALUE));
        PriorityQueue<Point> open = new PriorityQueue<>(cmp);

        gScore.put(start, 0);
        fScore.put(start, heuristic(start, end));
        open.add(start);

        while (!open.isEmpty()) {
            Point current = open.poll();

            // If this node was already expanded (a stale duplicate), skip it.
            if (closed.contains(current)) {
                continue;
            }

            if (withinReach.test(current, end)) {
                // Path goes from start to 'current' (adjacent to end). Exclude start.
                List<Point> path = reconstructPath(cameFrom, current);
                return path.stream()
                        .filter(p -> !p.equals(start))
                        .collect(Collectors.toList());
            }

            closed.add(current);

            for (Point neighbor : potentialNeighbors.apply(current).toList()) {
                if (!canPassThrough.test(neighbor) || closed.contains(neighbor)) {
                    continue;
                }

                int tentativeG = gScore.getOrDefault(current, Integer.MAX_VALUE) + 1;
                if (tentativeG < gScore.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    cameFrom.put(neighbor, current);
                    gScore.put(neighbor, tentativeG);
                    fScore.put(neighbor, tentativeG + heuristic(neighbor, end));
                    open.add(neighbor);
                }
            }
        }

        return Collections.emptyList();
    }

    private int heuristic(Point a, Point b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

    private List<Point> reconstructPath(Map<Point, Point> cameFrom, Point current) {
        LinkedList<Point> path = new LinkedList<>();
        for (Point cur = current; cur != null; cur = cameFrom.get(cur)) {
            path.addFirst(cur);
        }
        return path;
    }
}
