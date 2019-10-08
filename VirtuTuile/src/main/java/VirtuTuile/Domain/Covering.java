package VirtuTuile.Domain;

import java.awt.Color;
import java.awt.Shape;

/**
 * @class definissant les tuiles et leur dispositions sur une surface
 * @author gabparrot
 */
public class Covering
{
    private int offsetX;
    private int offsetY;
    private Color groutColor;
    private int groutWidth;
    private int angle;
    private Pattern pattern;
    private Shape tiles;

    
    /**
     * Contructeur avec parametres
     * //TODO ajouter descriptions params
     * @param offsetX
     * @param offsetY
     * @param groutColor
     * @param groutWidth
     * @param angle
     * @param pattern 
     * @param tiles
     */
    public Covering(int offsetX, int offsetY, Color groutColor, int groutWidth, int angle, 
            Pattern pattern, Shape tiles)
    {
        // Attributs du tuilage
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.groutColor = groutColor;
        this.groutWidth = groutWidth;
        this.angle = angle;
        this.pattern = pattern;
        
        // Appliquer le tuilage
        this.coverSurface();
    }
    
    private void coverSurface()
    {
        // TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    // Getters et Setters
    public int getOffsetX()
    {
        return offsetX;
    }

    public void setOffsetX(int offsetX)
    {
        this.offsetX = offsetX;
    }

    public int getOffsetY()
    {
        return offsetY;
    }

    public void setOffsetY(int offsetY)
    {
        this.offsetY = offsetY;
    }

    public Color getGroutColor()
    {
        return groutColor;
    }

    public void setGroutColor(Color groutColor)
    {
        this.groutColor = groutColor;
    }

    public int getGroutWidth()
    {
        return groutWidth;
    }

    public void setGroutWidth(int groutWidth)
    {
        this.groutWidth = groutWidth;
    }

    public int getAngle()
    {
        return angle;
    }

    public void setAngle(int angle)
    {
        this.angle = angle;
    }

    public Pattern getPattern()
    {
        return pattern;
    }

    public void setPattern(Pattern pattern)
    {
        this.pattern = pattern;
    } 

    public Shape getTiles()
    {
        return tiles;
    }

    public void setTiles(Shape tiles)
    {
        this.tiles = tiles;
    }
    
    
}
