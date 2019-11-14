package VirtuTuile.Domain;

import VirtuTuile.Infrastructure.Utilities;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.awt.geom.Area;
import java.awt.geom.AffineTransform;

/**
 * @class definissant les tuiles et leur dispositions sur une surface
 * @author gabparrot
 */
public class Covering implements Serializable
{
    private double offsetX = 0;
    private double offsetY = 0;
    private Color jointColor = Color.GRAY;
    private double jointWidth = 5;
    private boolean isNinetyDegree = false;
    private Pattern pattern = Pattern.A;
    private final java.util.ArrayList<Area> tiles = new java.util.ArrayList<>();
    private TileType tileType = Utilities.DEFAULT_TILE_1;
    private final Surface parent;
    
    public Covering(Surface parent)
    {
        this.parent = parent;
    }
    
    public void clearCovering()
    {
        tiles.clear();
    }
    
    /**
     * Cette fonction est appelée chaque fois que le Covering doit être modifié.
     * @param bounds Rectangle2D représentant le getBounds entourant la surface à couvrir
     */
    public void coverSurface(Rectangle2D bounds)
    {
        if (parent.isHole())
        {
            return;
        }
        tiles.clear();
        switch (pattern)
        {
            case A:
                coverSurfaceA(bounds);
                break;
                
            case B:
                coverSurfaceB(bounds);
                break;
            
            case C:
                coverSurfaceC(bounds);
                break;
            
            case D:
                coverSurfaceD(bounds);
                break;
                
            case E:
                coverSurfaceE(bounds);
                break;
        }
    }
    
    
    private void coverSurfaceA(Rectangle2D bounds)
    {
        Point2D.Double boundsTopLeft = new Point2D.Double(bounds.getX(), bounds.getY());
        Point2D.Double boundsBotRight = new Point2D.Double(bounds.getX() + bounds.getWidth(), 
                                                           bounds.getY() + bounds.getHeight());
        
        
        
        // Départ haut-gauche, après bordure de coulis
        double tileWidth;
        double tileHeight;
        Area boundsArea = new Area(bounds);
        Area surfaceArea = new Area(parent);
        
        Point2D.Double coveredRectTopLeft = new Point2D.Double(boundsTopLeft.getX() + jointWidth + 1, 
                                                               boundsTopLeft.getY() + jointWidth + 1);
        Point2D.Double coveredRectBotRight = new Point2D.Double(boundsBotRight.getX() - jointWidth - 1,
                                                                boundsBotRight.getY() - jointWidth - 1);
        Rectangle2D coveredRect = Utilities.cornersToRectangle(coveredRectTopLeft, coveredRectBotRight);        
        Area coveredArea = new Area(coveredRect);
        
        Point2D.Double currentPoint = new Point2D.Double(coveredRect.getX(), coveredRect.getY());
        
        if (parent instanceof CombinedSurface)
        {
            coveredArea.subtract(((CombinedSurface) parent).getUncoveredArea());
        }
        
        if (isNinetyDegree == false)
        {
            tileWidth = tileType.getWidth();
            tileHeight = tileType.getHeight();
        }
        else
        {
            tileWidth = tileType.getHeight();
            tileHeight = tileType.getWidth();
        }
        
        double offsetXInverse = tileWidth - (offsetX % tileWidth);
        double offsetYInverse = tileHeight - (offsetY % tileHeight);
        
        if (offsetX != 0 || offsetY != 0)
        {
            currentPoint.setLocation(currentPoint.getX() - offsetXInverse, currentPoint.getY() - offsetYInverse);
        }
        
        while (currentPoint.getX() < coveredRectBotRight.getX() && currentPoint.getY() < coveredRectBotRight.getY())
        {
            Point2D.Double tileTopLeft = currentPoint;
            Point2D.Double tileBotRight = new Point2D.Double(currentPoint.getX() + tileWidth,
                                                             currentPoint.getY() + tileHeight);
            Rectangle2D tileRect = Utilities.cornersToRectangle(tileTopLeft, tileBotRight);
            Area tileArea = new Area(tileRect);
            tileArea.intersect(boundsArea);
            tileArea.intersect(surfaceArea);
            tileArea.intersect(coveredArea);
            if (!tileArea.isEmpty())
            {
                tiles.add(tileArea);
            }
            // Si on dépasse en X, on descend et on repart a gauche. Si on depasse en Y, la boucle va se terminer
            if (currentPoint.getX() + tileWidth + jointWidth < coveredRectBotRight.getX())
            {
                currentPoint.setLocation(currentPoint.getX() + tileWidth + jointWidth, currentPoint.getY());
            }
            else
            {
                if (offsetX <= 0.1)
                {
                currentPoint.setLocation(coveredRectTopLeft.getX(), currentPoint.getY() + tileHeight + jointWidth);
                }
                else
                {
                    currentPoint.setLocation(coveredRectTopLeft.getX() - offsetXInverse, 
                                             currentPoint.getY() + tileHeight + jointWidth);
                }
            }

        }
        //TEST
            System.out.println("Quantité de tuiles posées sur cette surface: " + tiles.size());
        
        /**
        * Plan du covering:
        * Fait dès la création d'une surface, avec valeurs par défaut (si couvrir est coché)
        * Aussi fait dès qu'on switch de ne pas couvrir à couvrir avec la radio box (garder valeurs?)
        * Creer un array d'objets tuiles(shape)
        * Getbounds se fait dans la surface, passé en param
        * Dessiner à partir de (0,0) du GetBounds
        * Créer tuiles 1 par 1
        * 
        * Le GUI inspetor mode s'occupera de montrer les tuiles trop petites, on les cree quand même
        * Pour les autres tuiles, on trace la tuile selon la dimension du tileType donne
        * On parcourt de gauche a droite de haut en bas
        * Si 90 degrés coché, on switch width et height de chaque tuile
        * apres chaque tuile, X avance de width + joint width
        * On fait un intersect pour chaque tuile, si pas de superposition, détruite, sinon, crop à partir intersect
        * On crée si le point de départ est intérieur même si on déborde, puis on crop par intersect
        * Comme la couleur est pareille pour chaque tuile, pas besoin de donner l'attribut a chaque
        * Il sera lu par le GUI directement comme attribut de covering
        * Arreter de tracer la tuile quand on atteint la bordure et passer a la suivante
        * Chaque fois qu'on atteint une bordure en X, se deplacer de hauteur de tuile + coulis vers le bas
        * Si surface irreguliere, possiblement parcourir tout le canevas avec contains pour ne pas skip
        * Le GUI doit recevoir l'array de tuiles au complet en retour
        * Le GUI pourrait simplement imposer une couleur de background = jointColor prioritaire sur surface color
        * La jointColor apparait par dessus la couleur de surface (trou) mais est une propriété différente
        * if covering, draw jointCOlor, else surfaceColor
        * Le GUI dessine ensuite toutes les tuiles (rect shape) et les fill avec la tileColor
        * La balance est la couleur de coulis qui apparait entre les tuiles
        * Le deplacement flush tout et refait toutes les tuiles en temps 
        */
    }
    
    private void coverSurfaceB(Rectangle2D bounds)
    {
        
    }
    
    private void coverSurfaceC(Rectangle2D bounds)
    {
        
    }
    
    private void coverSurfaceD(Rectangle2D bounds)
    {
        
    }
    
    private void coverSurfaceE(Rectangle2D bounds)
    {
        
    }
    
    // Getters et Setters
    public int getNbTiles()
    {
        return tiles.size();
    }
    
    public double getOffsetX()
    {
        return offsetX;
    }

    public void setOffsetX(double offsetX)
    {
        this.offsetX = offsetX;
    }

    public double getOffsetY()
    {
        return offsetY;
    }

    public void setOffsetY(double offsetY)
    {
        this.offsetY = offsetY;
    }

    public Color getJointColor()
    {
        return jointColor;
    }

    public void setJointColor(Color jointColor)
    {
        this.jointColor = jointColor;
    }

    public double getJointWidth()
    {
        return jointWidth;
    }

    public void setJointWidth(double jointWidth)
    {
        this.jointWidth = jointWidth;
    }

    public boolean isNinetyDegree()
    {
        return isNinetyDegree;
    }

    public void setIsNinetyDegree(boolean isNinetyDegree)
    {
        this.isNinetyDegree = isNinetyDegree;
    } 

    public Pattern getPattern()
    {
        return pattern;
    }

    public void setPattern(Pattern pattern)
    {
        this.pattern = pattern;
    }
    
    public TileType getTileType()
    {
        return tileType;
    }
    
    public void setTileType(TileType tileType)
    {
        this.tileType = tileType;
    }

    public java.util.ArrayList<Area> getTiles()
    {
        return tiles;
    }
}