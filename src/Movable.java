import processing.core.PImage;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class Movable extends ActiveAnimatedEntity {
    private PathingStrategy pathingStrategy;

// here you can change between pathing strategies to see how each one executes
    public Movable(String id, Point position, List<PImage> images, double actionPeriod, double animationPeriod) {
        super(id, position, images, actionPeriod, animationPeriod);
        this.pathingStrategy = new AStarPathingStrategy(); // change here (Starts as Astar)
    }

    public void setPathingStrategy(PathingStrategy strategy) {
        this.pathingStrategy = strategy;
    }

    public Point nextPosition(WorldModel world, Point destPos) {
        Predicate<Point> canPassThrough = pt -> world.withinBounds(pt) && !this.isInvalidMove(world, pt);
        BiPredicate<Point, Point> withinReach = Point::adjacent; // stop next to target
        Function<Point, Stream<Point>> neighbors = PathingStrategy.CARDINAL_NEIGHBORS;

        List<Point> path = this.pathingStrategy.computePath(getPosition(), destPos, canPassThrough, withinReach, neighbors);

        if (path == null || path.isEmpty()) {
            PathingStrategy single = new SingleStepPathingStrategy();
            List<Point> step = single.computePath(getPosition(), destPos, canPassThrough, withinReach, neighbors);
            if (step != null && !step.isEmpty()) {
                return step.get(0);
            }
            return getPosition();
        }
        return path.get(0);
    }
    public abstract boolean moveTo(WorldModel model, Entity target, EventScheduler scheduler);

    /**
     * The entity can move to destination if it's not occupied.
     */
    public boolean isInvalidMove(WorldModel world, Point destination) {
        return world.isOccupied(destination);
    }
}
