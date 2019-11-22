package VirtuTuile.Domain;

import java.awt.geom.Rectangle2D;
import java.awt.Color;
import java.awt.geom.Area;

/**
 * Une surface rectangulaire.
 * @author gabparrot
 */
public class RectangularSurface extends Rectangle2D.Double implements Surface
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
     * Représente si cette surface est non couvrable
     * @return false si la surface doit être couverte, true sinon.
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
     * @return area Un double representant l'aire en mm carrés
     */
    @Override
    public double getArea()
    {
        return width * height;
    }
    
    /**
     * Demande au covering de se couvrir de tuiles, selon les valeurs qu'il porte déjà en attribut
     */
    @Override
    public void coverSurface()
    {
        covering.cover();
    }

    /**
     * Tente de repositionner la surface à une nouvelle coordonnée X (horizontale), préserve sa valeur verticale. En cas
     * de superposition, l'opération est annulée.
     * @param x la nouvelle coordonnée X, en mm
     * @param project le projet possédant cette surface
     * @return booléen représentant si l'opération s'est effectuée (true) ou annulée (false)
     */
    @Override
    public boolean setX(double x, Project project)
    {
        if (x < 0)
        {
            return false;
        }
        double oldX = this.x;
        this.x = x;
        if (project.conflictCheck(this))
        {
            coverSurface();
            return true;
        }
        else
        {
            this.x = oldX;
            return false;
        }
    }

    /**
     * Tente de repositionner la surface à une nouvelle coordonnée Y (verticale), préserve sa valeur horizontale. En cas
     * de superposition, l'opération est annulée.
     * @param y la nouvelle coordonnée Y, en mm
     * @param project le projet possédant cette surface
     * @return si oui ou non l'opération s'est déroulée avec succès
     */
    @Override
    public boolean setY(double y, Project project)
    {
        if (y < 0)
        {
            return false;
        }
        double oldY = this.y;
        this.y = y;
        if (project.conflictCheck(this))
        {
            coverSurface();
            return true;
        }
        else
        {
            this.y = oldY;
            return false;
        }
    }

    /**
     * Tente de redimensionner la largeur de la surface, en conservant son coin haut-gauche à la même position. 
     * En cas de superposition, l'opération est annulée.
     * @param width la nouvelle largeur
     * @param project le projet possédant cette surface
     * @return si oui ou non l'opération s'est déroulée avec succès
     */
    @Override
    public boolean setWidth(double width, Project project)
    {
        if (width < 100)
        {
            return false;
        }
        
        double oldWidth = this.width;
        
        this.width = width;
        
        if (project.conflictCheck(this))
        {
            coverSurface();
            return true;
        }
        else
        {
            this.width = oldWidth;
            return false;
        }
    }

    /**
     * Tente de redimensionner la hauteur de la surface, en conservant son coin haut-gauche à la même position. 
     * En cas de superposition, l'opération est annulée.
     * @param height la nouvelle largeur
     * @param project le projet possédant cette surface
     * @return si oui ou non l'opération s'est déroulée avec succès
     */
    @Override
    public boolean setHeight(double height, Project project)
    {
        if (height < 100)
        {
            return false;
        }
        
        double oldHeight = this.height;
        
        this.height = height;
        
        if (project.conflictCheck(this))
        {
            coverSurface();
            return true;
        }
        else
        {
            this.height = oldHeight;
            return false;
        }
    }
    
    /**
     * Tente de repositionner la surface à une nouvelle coordonnée Y (verticale), préserve sa valeur horizontale. En cas
     * de superposition, l'opération est annulée, et la surface est poussée jusqu'au prochain obstacle dans cette
     * direction.
     * @param y la nouvelle coordonnée Y, en mm
     * @param x la nouvelle coordonéée X, en mm
     * @param project le projet possédant cette surface

     */
    @Override
    public void setXY(double x, double y, Project project)
    {
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        double oldX = this.x;
        double oldY = this.y;
        this.x = x;
        this.y = y;
        
        if (!project.conflictCheck(this))
        {
            this.x = oldX;
            this.y = oldY;
            pushHorizontally(x, oldX, project);
            pushVertically(y, oldY, project);
        }
        coverSurface();
    }
    
    /**
     * Pousse la surface horizontalement vers la coordonnée X demandée, arrête au prochain obstacle
     * @param newX Position horizontale demandée, en mm
     * @param oldX
     * @param project 
     */
    private void pushHorizontally(double newX, double oldX, Project project)
    {
        double surroundingBounds[] = getSurroundingBounds(project);
        this.x = newX;
        
        if (!project.conflictCheck(this))
        {
            // Déplacement à droite
            if (newX > oldX)
            {
                this.x = Math.min(newX, surroundingBounds[2] - this.width);
            }
            // Déplacement à gauche
            else
            {
                this.x = Math.max(newX, surroundingBounds[0]);
            }
        }
    }
    
    /**
     * Pousse la surface verticalement vers la coordonnée Y demandée, arrête au prochain obstacle
     * @param newY Position verticale demandée, en mm
     * @param oldY Ancienne position verticale
     * @param project le projet en cours
     */
    private void pushVertically(double newY, double oldY, Project project)
    {
        double surroundingBounds[] = getSurroundingBounds(project);
        this.y = newY;
        
        if (!project.conflictCheck(this))
        {
            // Déplacement vers le bas
            if (newY > oldY)
            {
                this.y = Math.min(newY, surroundingBounds[3] - this.height);
            }
            // Déplacement vers le haut
            else
            {
                this.y = Math.max(newY, surroundingBounds[1]);
            }
        }
    }
    
    /**
     * Retourne les bornes entourantes, qui décrivent jusqu'à quel point une surface
     * peut être déplacée dans les quatre directions.
     * @param project : le project dans lequel la surface existe.
     * @return les bornes dans un tableau [gauche, en-haut, droite, en-bas]
     */
    public double[] getSurroundingBounds(Project project)
    {
        double surroundingBounds[] = {0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE};
        Area totalArea = new Area();
        
        for (Surface surface : project.getSurfaces())
        {
            if (surface != this) totalArea.add(new Area(surface));
        }
        // LEFT
        Area leftArea = new Area(totalArea);
        leftArea.intersect(new Area(new Rectangle2D.Double(0, y, x, height)));
        Rectangle2D leftRect = leftArea.getBounds2D();
        if (leftRect.getX() + leftRect.getWidth() > 0)
        {
            surroundingBounds[0] = leftRect.getX() + leftRect.getWidth();
        }
        
        // UP
        Area upArea = new Area(totalArea);
        upArea.intersect(new Area(new Rectangle2D.Double(x, 0, width, y)));
        Rectangle2D upRect = upArea.getBounds2D();
        if (upRect.getY() + upRect.getHeight() > 0)
        {
            surroundingBounds[1] = upRect.getY() + upRect.getHeight();
        }
        
        // RIGHT
        Area rightArea = new Area(totalArea);
        rightArea.intersect(new Area(new Rectangle2D.Double(x + width, y, Integer.MAX_VALUE, height)));
        Rectangle2D rightRect = rightArea.getBounds2D();
        if (!rightArea.isEmpty())
        {
            surroundingBounds[2] = rightRect.getX();
        }
        
        // DOWN
        Area downArea = new Area(totalArea);
        downArea.intersect(new Area(new Rectangle2D.Double(x, y + height, width, Integer.MAX_VALUE)));
        Rectangle2D downRect = downArea.getBounds2D();
        if (!downArea.isEmpty())
        {
            surroundingBounds[3] = downRect.getY();
        }
        return surroundingBounds;
    }
}
