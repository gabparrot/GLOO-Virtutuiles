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
    public  boolean     isHole          ();
    public  void        setIsHole       (boolean newStatus);  
    public  Color       getColor        ();
    public  void        setColor        (Color color);
    public  void        coverSurface    ();
    public  Covering    getCovering     ();
    public  double      getArea         ();
    public  boolean     setX            (double x, Project project);
    public  boolean     setY            (double y, Project project);
    public  boolean     setWidth        (double width, Project project);
    public  boolean     setHeight       (double height, Project project);
    public  void        setXY           (double x, double y, Project project);
}