package VirtuTuile.Domain;

import java.awt.Shape;
import java.awt.Color;
import java.io.Serializable;

/**
 * Interface pour une surface.
 * @author gabparrot
 */
public interface Surface extends Shape, Serializable
{
    public  boolean     isHole              ();
    public  void        setIsHole           (boolean newStatus);  
    public  Color       getColor            ();
    public  void        setColor            (Color color);
    public  void        coverSurface        ();
    public  Covering    getCovering         ();
    public  double      getArea             ();
}