package VirtuTuile.Domain;

import java.awt.Shape;
import java.awt.Color;

/**
 * Interface pour une surface.
 * @author gabparrot
 */
public interface Surface extends Shape
{
    public  boolean     isHole          ();
    public  void        setIsHole       (boolean newStatus);  
    public  Color       getColor        ();
    public  void        setColor        (Color color);
    public  Covering    getCovering     ();
    public  double      getArea         ();
}