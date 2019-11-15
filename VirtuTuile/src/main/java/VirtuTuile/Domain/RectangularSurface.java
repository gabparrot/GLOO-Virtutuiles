package VirtuTuile.Domain;

import java.awt.geom.Rectangle2D;
import java.awt.Color;

/**
 * Une surface rectangulaire.
 * @author gabparrot
 */
public class RectangularSurface extends Rectangle2D.Double implements ElementarySurface
{
    // Si true, la surface ne doit pas être couverte.
    private boolean isHole;
    
    // La couleur de la surface.
    private Color color;
    
    // Le revêtement de la surface.
    private final Covering covering = new Covering(this);
    
    /**
     * Constructeur.
     * @param rectangle la forme du rectangle.
     * @param isHole si la surface doit être couverte ou pas.
     * @param color la couleur de la surface.
     */
    public RectangularSurface(Rectangle2D.Double rectangle, boolean isHole, Color color)
    {
        super(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        this.isHole = isHole;
        this.color = color;
    }

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
        coverSurface();
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
    
    /**
     * Getter du covering representant les tuiles sur la surface, si elle est couverte
     * @return covering l'objet covering 
     */
    @Override
    public Covering getCovering()
    {
        return covering;
    }
    
    /**
     * Retourne l'aire de la surface rectangulaire.
     * @return area Un double representant l'aire
     */
    @Override
    public double getArea()
    {
        return width * height;
    }
    
    @Override
    public void coverSurface()
    {
        covering.cover();
    }
}
