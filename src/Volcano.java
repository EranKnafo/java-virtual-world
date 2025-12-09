import processing.core.PImage;
import java.util.*;

/**
 * A volcano that erupts and changes the world around it.
 */
public class Volcano extends ActiveAnimatedEntity {
    private boolean hasErupted = false;
    
    public Volcano(String id, Point position, List<PImage> images, double animationPeriod, double actionPeriod) {
        super(id, position, images, animationPeriod, actionPeriod);
    }

    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        if (!hasErupted) {
            changeGroundToBurnt(world, imageStore);
            createLavaMonsters(world, imageStore, scheduler);
            transformNearbyEntities(world, imageStore, scheduler);
            hasErupted = true;
        }
        scheduler.scheduleEvent(this, new Activity(this, world, imageStore), this.getActionPeriod());
    }
    private void changeGroundToBurnt(WorldModel world, ImageStore imageStore) {
        int radius = 4;
        for (int dy = -radius; dy <= radius; dy++) {
            for (int dx = -radius; dx <= radius; dx++) {
                if (dx * dx + dy * dy <= radius * radius) {
                    Point tile = new Point(this.getPosition().x + dx, this.getPosition().y + dy);
                    
                    if (world.withinBounds(tile)) {
                        Background burntGround = new Background("burntvolcano", imageStore.getImageList("burntvolcano"));
                        world.setBackgroundCell(tile, burntGround);
                    }
                }
            }
        }
    }

    private void createLavaMonsters(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Random rand = new Random();
        int numMonsters = 2 + rand.nextInt(3);
        for (int i = 0; i < numMonsters; i++) {
            Point spawnLocation = findEmptySpot(world, 3);
            if (spawnLocation != null) {
                LavaMonster monster = new LavaMonster("lavamonster_" + i, spawnLocation, 
                    imageStore.getImageList("lavamonster"), 0.15, 0.8);
                world.addEntity(monster);
                monster.scheduleActions(scheduler, world, imageStore);
            }
        }
    }

    private void transformNearbyEntities(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        int searchRadius = 5;
        List<Entity> entitiesToTransform = new ArrayList<>();

        for (int dy = -searchRadius; dy <= searchRadius; dy++) {
            for (int dx = -searchRadius; dx <= searchRadius; dx++) {
                Point checkSpot = new Point(this.getPosition().x + dx, this.getPosition().y + dy);
                
                if (world.withinBounds(checkSpot)) {
                    Optional<Entity> entity = world.getOccupant(checkSpot);
                    if (entity.isPresent()) {
                        Entity e = entity.get();
                        // Transform various entity types
                        if (e instanceof Dude || e instanceof Tree || e instanceof Sapling || 
                            e instanceof Fairy || e instanceof House || e instanceof Plant || e instanceof Stump) {
                            entitiesToTransform.add(e);
                        }
                    }
                }
            }
        }

        for (Entity entity : entitiesToTransform) {
            transformEntity(entity, world, imageStore, scheduler);
        }
    }

    private void transformEntity(Entity entity, WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Point pos = entity.getPosition();
        String id = "burnt_" + entity.getId();

        world.removeEntity(scheduler, entity);

        Entity burntEntity = null;
        
        if (entity instanceof Dude) {
            burntEntity = new BurntDude(id, pos, imageStore.getImageList("burntdude"), 0.2, 0.5);
        } else if (entity instanceof Tree) {
            burntEntity = new BurntTree(id, pos, imageStore.getImageList("burnttree"));
        } else if (entity instanceof Sapling) {
            burntEntity = new BurntSapling(id, pos, imageStore.getImageList("burntsapling"), 0.5, 2.0);
        } else if (entity instanceof Fairy) {
            burntEntity = new BurntFairy(id, pos, imageStore.getImageList("burntfairy"), 0.2, 0.6);
        } else if (entity instanceof House) {
            burntEntity = new BurntHouse(id, pos, imageStore.getImageList("burnthouse"));
        } else if (entity instanceof Stump) {
            burntEntity = new BurntStump(id, pos, imageStore.getImageList("burntstump"));
        }

        if (burntEntity != null) {
            world.addEntity(burntEntity);
            if (burntEntity instanceof Active) {
                ((Active) burntEntity).scheduleActions(scheduler, world, imageStore);
            }
        }
    }

    // this is used to find a nearby spot to spawn lava monsters
    private Point findEmptySpot(WorldModel world, int searchRadius) {
        Random rand = new Random();
        for (int tries = 0; tries < 20; tries++) {
            int dx = rand.nextInt(searchRadius + searchRadius + 1) - searchRadius;
            int dy = rand.nextInt(searchRadius + searchRadius + 1) - searchRadius;
            Point spot = new Point(this.getPosition().x + dx, this.getPosition().y + dy);
            
            if (world.withinBounds(spot) && !world.isOccupied(spot)) {
                return spot;
            }
        }
        return null;
    }
}
