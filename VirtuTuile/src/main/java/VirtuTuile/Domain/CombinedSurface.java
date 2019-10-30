package VirtuTuile.Domain;

import java.awt.Color;
import java.awt.geom.Area;
import java.util.ArrayList;

/**
 *
 * @author gabparrot
 */
public class CombinedSurface extends Area implements Surface
{
    private boolean isHole;
    private Color color;
    private Covering covering;
    private ArrayList<Surface> absorbedSurfaces = new ArrayList<>();

    /**
     * Constructeur.
     * @param isHole : la surface est-elle un trou?
     * @param color : la couleur de la surface.
     * @param surfaces : la liste des surfaces a fusionner
     */
    public CombinedSurface(boolean isHole, Color color, ArrayList<Surface> surfaces)
    {
        this.isHole = isHole;
        this.color = color;
        addAbsorbedSurfaces(surfaces);
    }
    
    public void addAbsorbedSurfaces(ArrayList<Surface> surfaces)
    {
        for (int i = 0; i < surfaces.size(); i++)
            if (surfaces.get(i) instanceof ElementarySurface)
            {
                absorbedSurfaces.add(surfaces.get(i));
            }
            else 
            {
                addAbsorbedSurfaces(((CombinedSurface) surfaces.get(i)).getAbsorbedSurfaces());
            }
    }
   
    public ArrayList<Surface> getAbsorbedSurfaces()
    {
        return absorbedSurfaces;
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
    public void setCovering(double offsetX, double offsetY, Color groutColor,
                            double groutWidth, int angle, Pattern pattern, 
                            TileType tileType, Color tileColor)
    {
        this.covering = new Covering(offsetX, offsetY, groutColor,
                                     groutWidth, angle, pattern, tileType, 
                                     tileColor);
    }
}