import processing.core.PImage;
import java.util.*;

public class LavaMonster extends Movable {

    public LavaMonster(String id, Point position, List<PImage> images,
                       double animationPeriod, double actionPeriod) {
        super(id, position, images, actionPeriod, animationPeriod);
    }

    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> target = world.findNearest(this.getPosition(),
                new ArrayList<>(Arrays.asList(Tree.class, Stump.class)));

        if (target.isPresent()) {
            if (this.moveTo(world, target.get(), scheduler)) {
                burnTarget(world, target.get(), imageStore, scheduler);
            }
        }

        scheduler.scheduleEvent(this,
                new Activity(this, world, imageStore),
                this.getActionPeriod());
    }

    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
        if (this.getPosition().adjacent(target.getPosition())) {
            return true;
        } else {
            Point nextPos = this.nextPosition(world, target.getPosition());

            if (!this.getPosition().equals(nextPos)) {
                world.moveEntity(scheduler, this, nextPos);
            }
            return false;
        }
    }

   private void burnTarget(WorldModel world, Entity target, ImageStore imageStore, EventScheduler scheduler) {
    Point p = target.getPosition();   

    if (!world.withinBounds(p)) {
        return;
    }

    world.removeEntity(scheduler, target);   

    Background burntBg = new Background(
            "burntvolcano",
            imageStore.getImageList("burntvolcano")
    );

    world.setBackgroundCell(p, burntBg);    
    }
}
