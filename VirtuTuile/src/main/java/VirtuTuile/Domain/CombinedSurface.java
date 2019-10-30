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
    private final ArrayList<Surface> absorbedSurfaces;

    /**
     * Constructeur.
     * @param isHole : la surface est-elle un trou?
     * @param color : la couleur de la surface.
     * @param surfaces : la liste des surfaces a fusionner
     */
    public CombinedSurface(ArrayList<Surface> surfaces, boolean isHole, Color color)
    {
        super(surfaces.get(0));
        absorbedSurfaces = new ArrayList<>();
        this.isHole = isHole;
        this.color = color;
        addAbsorbedSurfaces(surfaces);
    }
    
    /**
     * Ajoute toutes les surfaces de la liste recuee a absorbedSurfaces. S'il y a une combined, prend toutes les 
     * ElementarySurface contenues par recursion
     * @param surfaces 
     */
    public void addAbsorbedSurfaces(ArrayList<Surface> surfaces)
    {
        for (int i = 0; i < surfaces.size(); i++)
            if (surfaces.get(i) instanceof ElementarySurface)
            {
                absorbedSurfaces.add(surfaces.get(i));
                Area toAbsorb = new Area(surfaces.get(i));
                this.add(toAbsorb);
            }
            else 
            {
                addAbsorbedSurfaces(((CombinedSurface) surfaces.get(i)).getAbsorbedSurfaces());
            }
    }
   
    /**
     * getter de absorbedSurfaces
     * @return absorbedSurfaces, ArrayList des ElementarySurface qui composent la CombinedSurface
     */
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