import processing.core.PImage;

import java.util.List;

public abstract class Dude extends Movable {
    public static final String DUDE_KEY = "dude";
    public static final int DUDE_ACTION_PERIOD = 0;
    public static final int DUDE_ANIMATION_PERIOD = 1;
    public static final int DUDE_LIMIT = 2;
    public static final int DUDE_NUM_PROPERTIES = 3;
    private final int resourceLimit;

    public Dude(String id, Point position, List<PImage> images, double actionPeriod,
                double animationPeriod, int resourceLimit) {
        super(id, position, images, actionPeriod, animationPeriod);
        this.resourceLimit = resourceLimit;
    }

    public int getResourceLimit() {
        return this.resourceLimit;
    }

    /**
     * Dudes can move to any unoccupied point or points occupied by stumps.
     */
    @Override
    public boolean isInvalidMove(WorldModel world, Point destination) {
        return world.isOccupied(destination) &&
                world.getOccupancyCell(destination).getClass() != Stump.class;
    }

    /**
     * The default movement of the Dude assumes we haven't yet reached the target.
     * Each subclass will specify behaviours for when we reach the target.
     */
    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
        Point nextPos = this.nextPosition(world, target.getPosition());

        if (!this.getPosition().equals(nextPos)) {
            world.moveEntity(scheduler, this, nextPos);
        }
        return false;
    }

    public abstract boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore);
}
