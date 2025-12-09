import processing.core.PImage;
import java.util.*;


// since BurntFairy and BurntDude have the same functionality (fleeing from Lava Monsters), we can use BurntDude as the base class
public class BurntFairy extends BurntDude {
    
    public BurntFairy(String id, Point position, List<PImage> images, double animationPeriod, double actionPeriod) {
        super(id, position, images, animationPeriod, actionPeriod);
    }
}
