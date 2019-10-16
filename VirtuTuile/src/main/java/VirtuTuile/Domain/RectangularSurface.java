package VirtuTuile.Domain;

import java.awt.Rectangle;
import java.awt.Color;

/**
 * Une surface rectangulaire.
 * @author gabparrot
 */
public class RectangularSurface extends Rectangle implements ElementarySurface
{
    // Si true, la surface ne doit pas être couverte.
    private boolean isHole;
    
    // La couleur de la surface.
    private Color color;
    
    // Le revêtement de la surface.
    private Covering covering;
    
    /**
     * Constructeur.
     * @param rectangle la forme du rectangle.
     * @param isHole si la surface doit être couverte ou pas.
     * @param color la couleur de la surface.
     */
    public RectangularSurface(Rectangle rectangle, boolean isHole, Color color)
    {
        super(rectangle);
        this.isHole = isHole;
        this.color = color;
    }

    // Implémentation des méthodes de Surface

    /**
     * La surface doit-elle être couverte?
     * @return false is la surface doit être couverte, true sinon.
     */
    @Override
    public boolean isHole()
    {
        return isHole;
    }

    /**
     * Setter pour le paramètre isHole. False si la surface doit être couverte, true sinon.
     * @param newStatus : false si la surface doit être couverte, true sinon.
     */
    @Override
    public void setIsHole(boolean newStatus)
    {
        this.isHole = newStatus;
    }

    /**
     * Retourne la couleur de la surface.
     * @return la couleur de la surface.
     */
    @Override
    public Color getColor()
    {
        return color;
    }

    /**
     * Setter pour la couleur de la surface.
     * @param color : la nouvelle couleur.
     */
    @Override
    public void setColor(Color color)
    {
        this.color = color;
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
                                     groutWidth, angle, pattern, tileType, tileColor);
    }
}
