import processing.core.PImage;
import java.util.*;

public class BurntDude extends ActiveAnimatedEntity {
    
    public BurntDude(String id, Point position, List<PImage> images, 
                            double animationPeriod, double actionPeriod) {
        super(id, position, images, animationPeriod, actionPeriod);
    }

    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> threat = world.findNearest(this.getPosition(), 
            new ArrayList<>(Arrays.asList(LavaMonster.class, Volcano.class)));
        
        if (threat.isPresent()) {
            moveAwayFrom(world, threat.get(), scheduler);
        } else {
            wanderRandomly(world, scheduler);
        }
        
    
        scheduler.scheduleEvent(this,
                new Activity(this, world, imageStore),
                this.getActionPeriod());
    }    private void moveAwayFrom(WorldModel world, Entity threat, EventScheduler scheduler) {
        Point awayPos = getAwayPosition(world, threat.getPosition());
        
        if (!this.getPosition().equals(awayPos)) {
            world.moveEntity(scheduler, this, awayPos);
        }
    }
    
    private Point getAwayPosition(WorldModel world, Point threatPos) {
        int horiz = Integer.compare(this.getPosition().x, threatPos.x);
        Point newPos = new Point(this.getPosition().x + horiz, this.getPosition().y);

        if (horiz == 0 || world.isOccupied(newPos) || !world.withinBounds(newPos)) {
            int vert = Integer.compare(this.getPosition().y, threatPos.y);
            newPos = new Point(this.getPosition().x, this.getPosition().y + vert);

            if (vert == 0 || world.isOccupied(newPos) || !world.withinBounds(newPos)) {
                newPos = this.getPosition();
            }
        }

        return newPos;
    }
    
    private void wanderRandomly(WorldModel world, EventScheduler scheduler) {
        Random rand = new Random();
        int dx = rand.nextInt(3) - 1; // -1, 0, or 1
        int dy = rand.nextInt(3) - 1;
        
        Point newPos = new Point(this.getPosition().x + dx, this.getPosition().y + dy);
        
        if (world.withinBounds(newPos) && !world.isOccupied(newPos)) {
            world.moveEntity(scheduler, this, newPos);
        }
    }
}
