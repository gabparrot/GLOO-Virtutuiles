package VirtuTuile.Domain;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

public class CircularSurface implements Surface, Serializable
{
    private final Path2D.Double path = new Path2D.Double();
    private boolean isHole;
    private Color color;
    private final Covering covering = new Covering(this);
    
    public CircularSurface(Rectangle2D.Double bounds, boolean isHole, Color color)
    {
        path.append(
                new Ellipse2D.Double(bounds.x, bounds.y, bounds.width, bounds.height), false);
        this.isHole = isHole;
        this.color = color;
    }

    @Override
    public boolean isHole()
    {
        return isHole;
    }
    
    @Override
    public void setIsHole(boolean newStatus)
    {
        this.isHole = newStatus;
        coverSurface();
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
    public Covering getCovering()
    {
        return covering;
    }
    
    @Override
    public double getArea()
    {
        return path.getBounds2D().getWidth() * path.getBounds2D().getHeight();
    }
    
    @Override
    public void coverSurface()
    {
        covering.cover();
    }

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

    @Override
    public void setXY(double x, double y, Project project)
    {
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        double deltaX = x - getBounds2D().getX();
        double deltaY = y - getBounds2D().getY();
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
