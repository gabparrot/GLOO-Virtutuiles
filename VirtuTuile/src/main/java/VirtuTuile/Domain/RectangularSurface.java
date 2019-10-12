package VirtuTuile.Domain;

import java.awt.Rectangle;
import java.awt.Color;

/**
 *
 * @author gabparrot
 */
public class RectangularSurface extends Rectangle implements ElementarySurface
{
    private boolean isHole;
    private Color color;
    private boolean selectedStatus;
    private Covering covering;
    
    /**
     * Constructeur avec parametres
     * @param isHole
     * @param color
     * @param x
     * @param y
     * @param width
     * @param height 
     */
    public RectangularSurface(boolean isHole, Color color, int x, int y, 
                              int width, int height)
    {
        super(x, y, width, height);
        this.isHole = isHole;
        this.color = color;
        this.selectedStatus = false;
    }

    // Implémentation des méthodes de Surface

    @Override
    public boolean getIsHole()
    {
        return isHole;
    }

    @Override
    public void setIsHole(boolean newStatus)
    {
        this.isHole = newStatus;
    }

    @Override
    public Color getColor()
    {
        if (isHole == true)
        {
            return color;
        }
        return this.covering.getTileColor();
    }

    @Override
    public void setColor(Color color)
    {
        // TODO Est-ce qu'on utilise la classe super() ?
        this.color = color;
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
    public boolean getSelectedStatus()
    {
        return selectedStatus;
    }

    @Override
    public void setSelectedStatus(boolean newStatus)
    {
        this.selectedStatus = newStatus;
    }
    
}
