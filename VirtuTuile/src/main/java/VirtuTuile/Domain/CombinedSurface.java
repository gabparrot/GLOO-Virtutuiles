package VirtuTuile.Domain;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

/**
 * Une surface combinée à partir de deux autres surfaces.
 * @author gabparrot
 */
public class CombinedSurface implements Surface, Serializable
{
    private boolean isHole;
    private Color color;
    private Covering covering;
    private Path2D.Double uncoveredPath = new Path2D.Double();
    private Path2D.Double path = new Path2D.Double();
    private Surface firstSurface;
    private Surface secondSurface;

    public CombinedSurface(Surface s1, Surface s2, boolean isHole, Color color, Covering covering)
    {
        Area area = new Area(s1);
        area.add(new Area(s2));
        path.append(area, false);
        Area uncoveredArea = new Area();
        if (s1.isHole())
        {
            uncoveredArea.add(new Area(s1));
        }
        else if (s1 instanceof CombinedSurface)
        {
            uncoveredArea.add(new Area(((CombinedSurface) s1).getUncoveredPath()));
        }
        if (s2.isHole())
        {
            uncoveredArea.add(new Area(s1));
        }
        else if (s2 instanceof CombinedSurface)
        {
            uncoveredArea.add(new Area(((CombinedSurface) s2).getUncoveredPath()));
        }
        uncoveredPath.append(uncoveredArea, false);
        firstSurface = s1;
        secondSurface = s2;
        this.isHole = isHole;
        this.color = color;
        // Copie du Covering:
        try
        {
            this.covering = (Covering) covering.clone();
            this.covering.setParent(this);
            this.covering.cover();
        }
        catch (CloneNotSupportedException e) { e.printStackTrace(System.out); }
    }
   
    /**
     * Retourne l'aire de la surface qui ne doit pas être couverte.
     * @return l'aire de la surface qui ne doit pas être couverte.
     */
    public Path2D.Double getUncoveredPath()
    {
        return uncoveredPath;
    }
    
    public void setUncoveredPath(Path2D.Double uncoveredPath)
    {
        this.uncoveredPath = uncoveredPath;
    }

    public Surface getFirstSurface()
    {
        return firstSurface;
    }
    
    public Surface getSecondSurface()
    {
        return secondSurface;
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
     * Getter du covering representant les tuiles sur la surface, si elle est couverte
     * @return covering l'objet covering 
     */
    @Override
    public Covering getCovering()
    {   
        return covering;
    }
    
    /**
     * Retourne l'aire de cette CombinedSurface
     * @return area Un double representant l'aire
     */
    @Override
    public double getArea()
    {
        return path.getBounds2D().getWidth() * path.getBounds2D().getHeight();
    }
    
    /**
     * Couvre la surface de tuiles.
     */
    @Override
    public void coverSurface()
    {
        covering.cover();
    }
    
    /**
     * Enlève les petits trous de la surface.
     */
    public void approximateSurface()
    {
        AffineTransform translation = new AffineTransform();
        translation.translate(10000, 10000);
        path.transform(translation);
        translation = new AffineTransform();
        translation.translate(-10000, -10000);
        path.transform(translation);
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
            uncoveredPath.transform(translationTransform);
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
            uncoveredPath.transform(translationTransform);
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
     * @param width la largeur demandée
     * @param project le projet en cours
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
        
        if (project.conflictCheck(this))
        {
            uncoveredPath.transform(scaleTransform);
            setX(oldX, project);
            coverSurface();
            return true;
        }
        else
        {
            scaleTransform = new AffineTransform();
            scaleTransform.scale(oldWidth / width, 1);
            path.transform(scaleTransform);
            return false;
        }
    }

    /**
     * Tente de modifier la hauteur de la surface pour celle demandée. Si cette opération cause une superposition entre
     * deux surfaces, elle est annulée.
     * @param height la hauteur demandée
     * @param project le projet en cours
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
        
        if (project.conflictCheck(this))
        {
            uncoveredPath.transform(scaleTransform);
            setY(oldY, project);
            coverSurface();
            return true;
        }
        else
        {
            scaleTransform = new AffineTransform();
            scaleTransform.scale(1, oldHeight / height);
            path.transform(scaleTransform);
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
            uncoveredPath.transform(translationTransform);
            coverSurface();
        }
    }

    @Override
    public void moveVertexToPoint(Point2D.Double vertex, Point2D.Double point)
    {
        boolean vertexFound = false;
        PathIterator iterator = getPathIterator(null);
        Path2D.Double newPath = new Path2D.Double();
        double[] v = new double[6];
        while (!iterator.isDone())
        {
            int segmentType = iterator.currentSegment(v);
            for (int i = 0; i < 6; i++)
            {
                v[i] = Math.round(v[i]);
            }
            for (int i = 0; i < 5; i += 2)
            {
                if (v[i] == vertex.x && v[i + 1] == vertex.y)
                {
                    v[i] = point.x;
                    v[i + 1] = point.y;
                    vertexFound = true;
                }
            }
            switch (segmentType)
            {
                case PathIterator.SEG_MOVETO:
                    newPath.moveTo(v[0], v[1]);
                    break;
                case PathIterator.SEG_LINETO:
                    newPath.lineTo(v[0], v[1]);
                    break;
                case PathIterator.SEG_QUADTO:
                    newPath.quadTo(v[0], v[1], v[2], v[3]);
                    break;
                case PathIterator.SEG_CUBICTO:
                    newPath.curveTo(v[0], v[1], v[2], v[3], v[4], v[5]);
                    break;
            }
            iterator.next();
        }
        Area newArea = new Area(newPath);
        Rectangle2D bounds = newArea.getBounds2D();
        if (!newArea.isEmpty() && bounds.getWidth() > 100 && bounds.getHeight() > 100)
        {
            Area uncoveredArea = new Area(uncoveredPath);
            uncoveredArea.subtract(newArea);
            if (uncoveredArea.isEmpty())
            {
                path.reset();
                path.append(newPath, false);
            }
        }
        if (!vertexFound && contains(point))
        {
            moveUncoveredAreaVertexToPoint(vertex, point);
        }
    }
    
    private void moveUncoveredAreaVertexToPoint(Point2D.Double vertex, Point2D.Double point)
    {
        PathIterator iterator = uncoveredPath.getPathIterator(null);
        Path2D.Double newPath = new Path2D.Double();
        double[] v = new double[6];
        while (!iterator.isDone())
        {
            int segmentType = iterator.currentSegment(v);
            for (int i = 0; i < 6; i++)
            {
                v[i] = Math.round(v[i]);
            }
            for (int i = 0; i < 5; i += 2)
            {
                if (v[i] == vertex.x && v[i + 1] == vertex.y)
                {
                    v[i] = point.x;
                    v[i + 1] = point.y;
                }
            }
            switch (segmentType)
            {
                case PathIterator.SEG_MOVETO:
                    newPath.moveTo(v[0], v[1]);
                    break;
                case PathIterator.SEG_LINETO:
                    newPath.lineTo(v[0], v[1]);
                    break;
                case PathIterator.SEG_QUADTO:
                    newPath.quadTo(v[0], v[1], v[2], v[3]);
                    break;
                case PathIterator.SEG_CUBICTO:
                    newPath.curveTo(v[0], v[1], v[2], v[3], v[4], v[5]);
                    break;
            }
            iterator.next();
        }
        Area newArea = new Area(newPath);
        Rectangle2D bounds = newArea.getBounds2D();
        if (!newArea.isEmpty() && bounds.getWidth() > 100 && bounds.getHeight() > 100)
        {
            newArea.subtract(new Area(path));
            if (newArea.isEmpty())
            {
                uncoveredPath.reset();
                uncoveredPath.append(newPath, false);
            }
        }
    }
    
    private int countSubPaths(Path2D.Double path)
    {
        PathIterator iterator = path.getPathIterator(null);
        double[] v = new double[6];
        int numberSubPaths = 0;
        while (!iterator.isDone())
        {
            int segmentType = iterator.currentSegment(v);
            switch (segmentType)
            {
                case PathIterator.SEG_MOVETO:
                    ++numberSubPaths;
                    break;
            }
            iterator.next();
        }
        return numberSubPaths;
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
