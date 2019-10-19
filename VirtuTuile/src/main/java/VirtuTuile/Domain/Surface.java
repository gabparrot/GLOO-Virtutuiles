package VirtuTuile.Domain;

import java.awt.Shape;
import java.awt.Color;

/**
 * Interface pour une surface.
 * @author gabparrot
 */
public interface Surface extends Shape
{
    boolean     isHole          ();
    void        setIsHole       (boolean newStatus);  
    Color       getColor        ();
    void        setColor        (Color color);
    Covering    getCovering     ();
    void        setCovering     (double offsetX, double offsetY, Color groutColor,
                                 double groutWidth, int angle, Pattern pattern, 
                                 TileType tileType, Color color);
}