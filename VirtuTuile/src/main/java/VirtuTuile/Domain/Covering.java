package VirtuTuile.Domain;

import java.awt.Color;
import java.awt.Shape;

/**
 * @class definissant les tuiles et leur dispositions sur une surface
 * @author gabparrot
 */
public class Covering
        
       /* 
        TODO ici, je ne vois pas comment on va retourner des trucs comme tiles.color
        et getTiles
        */
{
    private double offsetX;
    private double offsetY;
    private Color groutColor;
    private double groutWidth;
    private int angle;
    private Pattern pattern;
    private java.util.ArrayList<Shape> tiles = new java.util.ArrayList<>();
    private TileType tileType;
    private Color tileColor;

    /**
     * Contructeur avec parametres
     * //TODO ajouter descriptions params
     * @param offsetX
     * @param offsetY
     * @param groutColor
     * @param groutWidth
     * @param angle
     * @param pattern 
     * @param tileType
     * @param tileColor
     */
    public Covering(double offsetX, double offsetY, Color groutColor, double groutWidth, 
                    int angle, Pattern pattern, TileType tileType, 
                    Color tileColor)
    {
        // Attributs du tuilage
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.groutColor = groutColor;
        this.groutWidth = groutWidth;
        this.angle = angle;
        this.pattern = pattern;
        this.tileType = tileType;
        this.tileColor = tileColor;
        
        // Appliquer le tuilage
        this.coverSurface();
    }
    
    private void coverSurface()
    {
        // TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private int getNbTiles()
    {
        return tiles.size();
    }
    
    // Getters et Setters
    public double getOffsetX()
    {
        return offsetX;
    }

    public void setOffsetX(double offsetX)
    {
        this.offsetX = offsetX;
    }

    public double getOffsetY()
    {
        return offsetY;
    }

    public void setOffsetY(double offsetY)
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

    public double getGroutWidth()
    {
        return groutWidth;
    }

    public void setGroutWidth(double groutWidth)
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
    
    public TileType getTileType()
    {
        return tileType;
    }
    
    public void setTileType(TileType tileType)
    {
        this.tileType = tileType;
    }

    public java.util.ArrayList<Shape> getTiles()
    {
        return tiles;
    }

    public void setTiles(java.util.ArrayList<Shape> tiles)
    {
        this.tiles = tiles;
    }
    
    public Color getTileColor()
    {
        return tileColor;
    }
    
    public void setTileColor(Color tileColor)
    {
        this.tileColor = tileColor;
    }  
}