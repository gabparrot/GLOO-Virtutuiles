package VirtuTuile.Domain;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Une surface combinée à partir de deux autres surfaces.
 * @author gabparrot
 */
public class CombinedSurface extends Area implements Surface, Serializable
{
    private boolean isHole;
    private Color color;
    private Covering covering;
    private Area uncoveredArea = new Area();
    private ArrayList<Surface> absorbedSurfaces = new ArrayList<>();

    /**
     * Constructeur.
     * @param s1: la première surface fusionnée.
     * @param s2: la deuxième surface fusionnée.
     * @param isHole : la surface est-elle un trou?
     * @param color : la couleur de la surface.
     * @param covering : le covering de la surface.
     */
    public CombinedSurface(Surface s1, Surface s2, boolean isHole, Color color, Covering covering)
    {
        super(s1);
        this.add(new Area(s2));
        
        if (s1.isHole())
        {
            uncoveredArea.add(new Area(s1));
        }
        else if (s1 instanceof CombinedSurface)
        {
            uncoveredArea.add(((CombinedSurface) s1).getUncoveredArea());
        }
        if (s2.isHole())
        {
            uncoveredArea.add(new Area(s2));
        }
        else if (s2 instanceof CombinedSurface)
        {
            uncoveredArea.add(((CombinedSurface) s2).getUncoveredArea());
        }
        
        absorbedSurfaces.add(s1);
        absorbedSurfaces.add(s2);
        
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
    public Area getUncoveredArea()
    {
        return uncoveredArea;
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
        double area = 0;
        if (this.isRectangular()) 
        {
            return getBounds2D().getHeight() * getBounds2D().getWidth();
        } 
        else 
        {
            for (int i = 0; i < absorbedSurfaces.size(); i++) 
            {
                area += absorbedSurfaces.get(i).getArea();
            }
        }
        return area;
    }
    
    /**
     * Permet la sauvegarde d'une surface combinée dans un OutputStream
     * @param out le OutputStream de destination
     * @throws IOException Erreur d'écriture
     */
    private void writeObject(ObjectOutputStream out) throws IOException
    {
        out.writeObject(AffineTransform.getTranslateInstance(0, 0).createTransformedShape(this));
        out.writeObject(AffineTransform.getTranslateInstance(0, 0).createTransformedShape(uncoveredArea));
        out.writeObject(isHole);
        out.writeObject(color);
        out.writeObject(covering);
        out.writeObject(absorbedSurfaces);
    }
    
    /**
     * Permet le chargement d'une surface combinée à partir d'un InputStream de sauvegarde
     * @param in L'InputStream de sauvegarde
     * @throws IOException erreur d'écriture
     * @throws ClassNotFoundException erreur de définition de classe
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        add(new Area((Shape) in.readObject()));
        uncoveredArea = new Area((Shape) in.readObject());
        isHole = (boolean) in.readObject();
        color = (Color) in.readObject();
        covering = (Covering) in.readObject();
        absorbedSurfaces = (ArrayList<Surface>) in.readObject();
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
        transform(translation);
        translation = new AffineTransform();
        translation.translate(-10000, -10000);
        transform(translation);
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
        Rectangle2D bounds = getBounds2D();
        double deltaX = x - bounds.getX();
        AffineTransform translationTransform = new AffineTransform();
        translationTransform.translate(deltaX, 0);
        transform(translationTransform);
        if (project.conflictCheck(this))
        {
            uncoveredArea.transform(translationTransform);
            coverSurface();
            return true;
        }
        else
        {
            translationTransform = new AffineTransform();
            translationTransform.translate(-deltaX, 0);
            transform(translationTransform);
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
        Rectangle2D bounds = getBounds2D();
        double deltaY = y - bounds.getY();
        AffineTransform translationTransform = new AffineTransform();
        translationTransform.translate(0, deltaY);
        transform(translationTransform);
        if (project.conflictCheck(this))
        {
            uncoveredArea.transform(translationTransform);
            coverSurface();
            return true;
        }
        else
        {
            translationTransform = new AffineTransform();
            translationTransform.translate(0, -deltaY);
            transform(translationTransform);
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
        Rectangle2D bounds = getBounds2D();
        double oldWidth = bounds.getWidth();
        double oldX = bounds.getX();
        
        AffineTransform scaleTransform = new AffineTransform();
        scaleTransform.scale(width / oldWidth, 1);
        transform(scaleTransform);
        
        if (project.conflictCheck(this))
        {
            uncoveredArea.transform(scaleTransform);
            setX(oldX, project);
            coverSurface();
            return true;
        }
        else
        {
            scaleTransform = new AffineTransform();
            scaleTransform.scale(oldWidth / width, 1);
            transform(scaleTransform);
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
        Rectangle2D bounds = getBounds2D();
        double oldHeight = bounds.getHeight();
        double oldY = bounds.getY();
        
        AffineTransform scaleTransform = new AffineTransform();
        scaleTransform.scale(1, height / oldHeight);
        transform(scaleTransform);
        
        if (project.conflictCheck(this))
        {
            uncoveredArea.transform(scaleTransform);
            setY(oldY, project);
            coverSurface();
            return true;
        }
        else
        {
            scaleTransform = new AffineTransform();
            scaleTransform.scale(1, oldHeight / height);
            transform(scaleTransform);
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
        double deltaX = x - getBounds2D().getX();
        double deltaY = y - getBounds2D().getY();
        AffineTransform translationTransform = new AffineTransform();
        translationTransform.translate(deltaX, deltaY);
        transform(translationTransform);
        if (!project.conflictCheck(this))
        {
            translationTransform = new AffineTransform();
            translationTransform.translate(-deltaX, -deltaY);
            transform(translationTransform);
        }
        else
        {
            uncoveredArea.transform(translationTransform);
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
        if (!newArea.isEmpty() && bounds.getWidth() > 100 && bounds.getHeight() > 100)
        {
            reset();
            add(new Area(newPath));
            uncoveredArea.intersect(this);
        }
    }
}
