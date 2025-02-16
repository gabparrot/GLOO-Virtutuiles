package VirtuTuile.Domain;

import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Classe définissant une surface irrégulière définie par une série de points.
 * @author gabparrot
 */
public class IrregularSurface implements Surface, Serializable
{
    private final Path2D.Double path = new Path2D.Double();
    private boolean isHole;
    private Color color;
    private final Covering covering = new Covering(this);

    /**
     * Constructeur de IrregularSurface
     * @param polygon représentant la forme de la surface.
     * @param isHole représentant si oui on non la surface est un trou (non-couvrable).
     * @param color représentant la couleur de la surface.
     */
    public IrregularSurface(Path2D.Double polygon, boolean isHole, Color color)
    {
        path.append(polygon, false);
        this.isHole = isHole;
        this.color = color;
    }

    /**
     * Getter du statut de trou (non couvrable)
     * @return isHole booleen representant si oui ou non la surface est un trou
     */    
    @Override
    public boolean isHole()
    {
        return isHole;
    }
    
    /**
     * Setter du statut de trou (non couvrable)
     * @param newStatus Booleen representant si oui ou non la surface devra etre un trou non couvrable
     */
    @Override
    public void setIsHole(boolean newStatus)
    {
        this.isHole = newStatus;
        coverSurface();
    }
    /**
     * Getter de la couleur de la surface, visible lorsqu'elle n'est pas couverte
     * @return color objet Color
     */
    @Override
    public Color getColor()
    {
        return color;
    }
    
    /**
     * Setter de la couleur de la surface, visible lorsqu'elle n'est pas couverte
     * @param newColor objet Color
     */
    @Override
    public void setColor(Color newColor)
    {
        this.color =  newColor;
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
     * Retourne l'aire de cette IrregularSurface
     * @return area Un double representant l'aire
     */
    @Override
    public double getArea()
    {
        return path.getBounds2D().getWidth() * path.getBounds2D().getHeight();
    }
    
    /**
     * Demande au covering de se couvrir de tuiles, selon les règles définies par ses attributs actuels
     */
    @Override
    public void coverSurface()
    {
        covering.cover();
    }

    /**
     * Tente de déplacer la surface horizontalement vers la coordonnée X reçue, en préservant sa valeur en Y. Arrête au
     * premier obstacle rencontré. Si le déplacement est impossible, il est annulé.
     * @param x La destination en X, en mm
     * @param project le projet en cours
     * @return booléen représentant si [true] l'opération a été effectuée avec succès, ou a été annulée [false]
     */
    @Override
    public boolean setX(double x, Project project)
    {
        if (x < 0)
        {
            return false;
        }
        Rectangle2D bounds = path.getBounds2D();
        double deltaX = x - bounds.getX();
        AffineTransform translationTransform = new AffineTransform();
        translationTransform.translate(deltaX, 0);
        path.transform(translationTransform);
        if (project.conflictCheck(this))
        {
            coverSurface();
            return true;
        }
        else
        {
            translationTransform = new AffineTransform();
            translationTransform.translate(-deltaX, 0);
            path.transform(translationTransform);
            return false;
        }
    }

    /**
     * Tente de déplacer la surface verticalement vers la coordonnée Y reçue, en préservant sa valeur en X. Arrête au
     * premier obstacle rencontré. Si le déplacement est impossible, il est annulé.
     * @param y La destination en Y, en mm
     * @param project le projet en cours
     * @return booléen représentant si [true] l'opération a été effectuée avec succès, ou a été annulée [false]
     */
    @Override
    public boolean setY(double y, Project project)
    {
        if (y < 0)
        {
            return false;
        }
        Rectangle2D bounds = path.getBounds2D();
        double deltaY = y - bounds.getY();
        AffineTransform translationTransform = new AffineTransform();
        translationTransform.translate(0, deltaY);
        path.transform(translationTransform);
        if (project.conflictCheck(this))
        {
            coverSurface();
            return true;
        }
        else
        {
            translationTransform = new AffineTransform();
            translationTransform.translate(0, -deltaY);
            path.transform(translationTransform);
            return false;
        }
    }

    /**
     * Tente de modifier la largeur de la surface pour celle demandée. Si cette opération cause une superposition entre
     * deux surfaces, elle est annulée.
     * @param width la largeur demandée.
     * @param project le projet en cours.
     * @return booléen représentant si [true] l'opération a été effectuée avec succès, ou a été annulée [false]
     */
    @Override
    public boolean setWidth(double width, Project project)
    {
        if (width < 100)
        {
            return false;
        }
        Rectangle2D bounds = path.getBounds2D();
        double oldWidth = bounds.getWidth();
        double oldX = bounds.getX();
        
        AffineTransform scaleTransform = new AffineTransform();
        scaleTransform.scale(width / oldWidth, 1);
        path.transform(scaleTransform);
        setX(oldX, project);
        
        if (project.conflictCheck(this))
        {
            coverSurface();
            return true;
        }
        else
        {
            scaleTransform = new AffineTransform();
            scaleTransform.scale(oldWidth / width, 1);
            path.transform(scaleTransform);
            setX(oldX, project);
            return false;
        }
    }

    /**
     * Tente de modifier la hauteur de la surface pour celle demandée. Si cette opération cause une superposition entre
     * deux surfaces, elle est annulée.
     * @param height la largeur demandée.
     * @param project le projet en cours.
     * @return booléen représentant si [true] l'opération a été effectuée avec succès, ou a été annulée [false]
     */
    @Override
    public boolean setHeight(double height, Project project)
    {
        if (height < 100)
        {
            return false;
        }
        Rectangle2D bounds = path.getBounds2D();
        double oldHeight = bounds.getHeight();
        double oldY = bounds.getY();
        
        AffineTransform scaleTransform = new AffineTransform();
        scaleTransform.scale(1, height / oldHeight);
        path.transform(scaleTransform);
        setY(oldY, project);
        
        if (project.conflictCheck(this))
        {
            coverSurface();
            return true;
        }
        else
        {
            scaleTransform = new AffineTransform();
            scaleTransform.scale(1, oldHeight / height);
            path.transform(scaleTransform);
            setY(oldY, project);
            return false;
        }
    }

    /**
     * Tente de déplacer la surface vers la coordonnée XY reçue. Arrête au premier obstacle rencontré. Si le déplacement
     * est impossible, il est annulé.
     * @param x La destination en X, en mm
     * @param y La destination en Y, en mm
     * @param project le projet en cours
     */
    @Override
    public void setXY(double x, double y, Project project)
    {
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        double deltaX = x - path.getBounds2D().getX();
        double deltaY = y - path.getBounds2D().getY();
        AffineTransform translationTransform = new AffineTransform();
        translationTransform.translate(deltaX, deltaY);
        path.transform(translationTransform);
        if (!project.conflictCheck(this))
        {
            translationTransform = new AffineTransform();
            translationTransform.translate(-deltaX, -deltaY);
            path.transform(translationTransform);
        }
        else
        {
            coverSurface();
        }
    }

    @Override
    public void moveVertexToPoint(Point2D.Double vertex, Point2D.Double point)
    {
        PathIterator iterator = getPathIterator(null);
        Path2D.Double newPath = new Path2D.Double();
        double[] v = new double[2];
        while (!iterator.isDone())
        {
            int segmentType = iterator.currentSegment(v);
            v[0] = Math.round(v[0]);
            v[1] = Math.round(v[1]);
            if (v[0] == vertex.x && v[1] == vertex.y)
            {
                v[0] = point.x;
                v[1] = point.y;
            }
            switch (segmentType)
            {
                case PathIterator.SEG_MOVETO:
                    newPath.moveTo(v[0], v[1]);
                    break;
                case PathIterator.SEG_LINETO:
                    newPath.lineTo(v[0], v[1]);
                    break;
                case PathIterator.SEG_CLOSE:
                    break;
                default:
                    break;
            }
            iterator.next();
        }
        Area newArea = new Area(newPath);
        Rectangle2D bounds = newArea.getBounds2D();
        if (newArea.isSingular() && !newArea.isEmpty() && bounds.getWidth() > 100 && bounds.getHeight() > 100)
        {
            path.reset();
            path.append(newPath, false);
        }
    }

    @Override
    public Rectangle getBounds()
    {
        return path.getBounds();
    }

    @Override
    public Rectangle2D getBounds2D()
    {
        return path.getBounds2D();
    }

    @Override
    public boolean contains(double x, double y)
    {
        return path.contains(x, y);
    }

    @Override
    public boolean contains(Point2D p)
    {
        return path.contains(p);
    }

    @Override
    public boolean intersects(double x, double y, double w, double h)
    {
        return path.intersects(x, y, w, h);
    }

    @Override
    public boolean intersects(Rectangle2D r)
    {
        return path.intersects(r);
    }

    @Override
    public boolean contains(double x, double y, double w, double h)
    {
        return path.contains(x, y, w, h);
    }

    @Override
    public boolean contains(Rectangle2D r)
    {
        return path.contains(r);
    }

    @Override
    public PathIterator getPathIterator(AffineTransform at)
    {
        return path.getPathIterator(at);
    }

    @Override
    public PathIterator getPathIterator(AffineTransform at, double flatness)
    {
        return path.getPathIterator(at, flatness);
    }
    
    public void reset()
    {
        path.reset();
    }
    
    public void append(PathIterator pi, boolean connect)
    {
        path.append(pi, connect);
    }
    
    public void append(Shape s, boolean connect)
    {
        path.append(s, connect);
    }
    
    public Path2D.Double getPath()
    {
        return path;
    }
}
