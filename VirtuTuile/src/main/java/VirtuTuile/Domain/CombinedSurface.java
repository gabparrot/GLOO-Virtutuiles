package VirtuTuile.Domain;

import java.awt.Color;
import java.awt.geom.Area;

/**
 *
 * @author gabparrot
 */
public class CombinedSurface extends Area implements Surface
{
    private boolean isHole;
    private Color color;
    private Covering covering;

    /**
     * Constructeur.
     * @param isHole : la surface est-elle un trou?
     * @param color : la couleur de la surface.
     */
    public CombinedSurface(boolean isHole, Color color)
    {
        this.isHole = isHole;
        this.color = color;
    }

    @Override
    public boolean isHole()
    {
        return isHole;
    }

    @Override
    public Color getColor()
    {
        return color;
    }

    @Override
    public void setColor(Color newColor)
    {
        this.color =  newColor;
    }

    @Override
    public void setIsHole(boolean newStatus)
    {
        this.isHole = newStatus;
    }
    
    @Override
    public Covering getCovering()
    {
        return covering;
    }

    @Override
    public void setCovering(int offsetX, int offsetY, Color groutColor,
                            int groutWidth, int angle, Pattern pattern, 
                            TileType tileType, Color tileColor)
    {
        this.covering = new Covering(offsetX, offsetY, groutColor,
                                     groutWidth, angle, pattern, tileType, 
                                     tileColor);
    }
}