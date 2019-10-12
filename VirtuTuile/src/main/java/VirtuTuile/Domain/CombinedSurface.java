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
    private boolean selectedStatus = false;
    private Covering covering;
    
    // TODO vérifier si attributs de coordonnées doivent être ajoutés

    public CombinedSurface(boolean isHole, Color color, int[] xpoints, int[] ypoints, int npoints)
    {
        // TODO CONSTRUCTEUR COMMENT ON FAIT ÇA?
        this.isHole = isHole;
        this.color = color;
    }

    @Override
    public boolean getIsHole()
    {
        return isHole;
    }

    @Override
    public Color getColor()
    {
        if (isHole == true)
        {
            return color;
        }
        return covering.getTileColor();
    }

    @Override
    public void setColor(Color newColor)
    {
        // TODO Est-ce qu'on utilise la classe super?
        this.color =  newColor;
    }

    @Override
    public void setIsHole(boolean newStatus)
    {
        this.isHole = newStatus;
    }
    
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

    @Override
    public void setSelectedStatus(boolean newStatus)
    {
        this.selectedStatus = newStatus;
    }

    @Override
    public boolean getSelectedStatus()
    {
        if (selectedStatus == true)
        {
            return false;
        } 
        return true;
    }
    
}
