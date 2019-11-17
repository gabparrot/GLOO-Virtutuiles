package VirtuTuile.Domain;

import VirtuTuile.Infrastructure.Utilities;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.awt.geom.Area;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

/**
 * @class definissant les tuiles et leur dispositions sur une surface
 * @author gabparrot
 */
public class Covering implements Serializable, Cloneable
{
    private double offsetX = 0;
    private double offsetY = 0;
    private int rowOffset = 0;
    private Color jointColor = Color.GRAY;
    private double jointWidth = 5;
    private boolean isNinetyDegree = false;
    private Pattern pattern = Pattern.A;
    private transient ArrayList<Area> tiles = new java.util.ArrayList<>();
    private TileType tileType = Utilities.DEFAULT_TILE_1;
    private Surface parent;
    
    public Covering(Surface parent)
    {
        this.parent = parent;
        cover();
    }
    
    public void setParent(Surface parent)
    {
        this.parent = parent;
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException 
    { 
        return super.clone(); 
    } 
    
    /**
     * Cette fonction est appelée chaque fois que le Covering doit être modifié.
     */
    public final void cover()
    {
        tiles.clear();
        if (parent.isHole())
        {
            return;
        }
        switch (pattern)
        {
            case A:
                coverSurfaceA();
                break;
                
            case B:
                coverSurfaceB();
                break;
            
            case C:
                coverSurfaceC();
                break;
            
            case D:
                coverSurfaceD();
                break;     
        }
    }
    
    
    private void coverSurfaceA()
    {
        
        double tileWidth = isNinetyDegree ? tileType.getHeight() : tileType.getWidth();
        double tileHeight = isNinetyDegree ? tileType.getWidth() : tileType.getHeight();
        
        double offsetXMod = this.offsetX % (tileWidth + jointWidth);
        double offsetYMod = this.offsetY % (tileHeight + jointWidth);
        double rowOffsetMod;
        
        rowOffsetMod = tileWidth - (rowOffset / 100. * tileWidth);
        rowOffsetMod -= jointWidth * rowOffset / 100;
        

        Area fullArea = new Area(parent);
        Rectangle2D bounds = fullArea.getBounds2D();
        if (parent instanceof CombinedSurface)
        {
            fullArea.subtract(((CombinedSurface) parent).getUncoveredArea());
        }
        
        Area innerArea = getInnerArea(fullArea);
        
        Point2D.Double currentPoint = new Point2D.Double(bounds.getX() - tileWidth + offsetXMod,
                                                         bounds.getY() - tileHeight + offsetYMod);
        int tileCount = 0;
        int rowCount = 0;
        while (currentPoint.getX() < bounds.getMaxX() && currentPoint.y < bounds.getMaxY())
        {
            Point2D.Double tileTopLeft = currentPoint;
            Point2D.Double tileBotRight = new Point2D.Double(currentPoint.x + tileWidth,
                                                             currentPoint.y + tileHeight);
            
            Area tile = new Area(Utilities.cornersToRectangle(tileTopLeft, tileBotRight));
            tile.intersect(innerArea);
            
            if (!tile.isEmpty())
            {
                tiles.add(tile);
            }
            
            if (currentPoint.x + tileWidth + jointWidth < bounds.getMaxX())
            {
                currentPoint.x = currentPoint.getX() + tileWidth + jointWidth;
            }
            else // Si on dépasse en X, on descend et on repart a gauche.
            {
                if (tileCount < tiles.size())
                {
                    rowCount += 1;
                    tileCount = tiles.size();
                }
                if (rowCount % 2 == 0)
                {
                
                currentPoint.setLocation(bounds.getX() - tileWidth + offsetXMod, 
                                         currentPoint.y + tileHeight + jointWidth);
                }
                // Décaler les rangées paires (celle du haut est #1)
                else
                {
                    currentPoint.setLocation(bounds.getX() - tileWidth + offsetXMod - rowOffsetMod - jointWidth,
                                             currentPoint.y + tileHeight + jointWidth);                    
                }
            }
        }
    }
    
    private void coverSurfaceB()
    {
        
    }
    
    private void coverSurfaceC()
    {
        
    }
    
    private void coverSurfaceD()
    {
        
    }
    
    /**
     * Prend une aire et enlève une bordure sur le périmètre.
     * @param fullArea : l'aire originale.
     * @return une nouvelle aire, plus petite, avec une bordure de moins.
     */
    private Area getInnerArea(Area fullArea)
    {
        Area innerArea = new Area(fullArea);
        
        Area topLeftCopy = new Area(fullArea);
        AffineTransform translate = new AffineTransform();
        translate.translate(-jointWidth, -jointWidth);
        topLeftCopy.transform(translate);
        
        Area topRightCopy = new Area(fullArea);
        translate = new AffineTransform();
        translate.translate(jointWidth, -jointWidth);
        topRightCopy.transform(translate);
        
        Area botLeftCopy = new Area(fullArea);
        translate = new AffineTransform();
        translate.translate(-jointWidth, jointWidth);
        botLeftCopy.transform(translate);
        
        Area botRightCopy = new Area(fullArea);
        translate = new AffineTransform();
        translate.translate(jointWidth, jointWidth);
        botRightCopy.transform(translate);
        
        innerArea.intersect(topLeftCopy);
        innerArea.intersect(topRightCopy);
        innerArea.intersect(botLeftCopy);
        innerArea.intersect(botRightCopy);
        
        return innerArea;
    }
    
    /**
     * Retourne le nombre de tuiles du couvrement.
     * @return le nombre de tuiles du couvrement.
     */
    public int getNbTiles()
    {
        return tiles.size();
    }
    
    /**
     * Retourne le décalage horizontal du couvrement.
     * @return : le décalage horizontal du couvrement.
     */
    public double getOffsetX()
    {
        return offsetX;
    }

    /**
     * Change le décalage horizontal du couvrement.
     * @param offsetX : le nouveau décalage horizontal.
     */
    public void setOffsetX(double offsetX)
    {
        this.offsetX = offsetX;
        cover();
    }

    /**
     * Retourne le décalage vertical du couvrement.
     * @return le décalage vertical du couvrement.
     */
    public double getOffsetY()
    {
        return offsetY;
    }

    /**
     * Change le décalage vertical du couvrement.
     * @param offsetY : le nouveau décalage vertical.
     */
    public void setOffsetY(double offsetY)
    {
        this.offsetY = offsetY;
        cover();
    }

    /**
     * Retourne la couleur des joints du couvrement.
     * @return la couleur des joints du couvrement.
     */
    public Color getJointColor()
    {
        return jointColor;
    }

    /**
     * Change la couleur des joints du couvrement.
     * @param jointColor : la nouvelle couleur des joints.
     */
    public void setJointColor(Color jointColor)
    {
        this.jointColor = jointColor;
        cover();
    }

    /**
     * Retourne la largeur des joints du couvrement.
     * @return la largeur des joints du recouvrement.
     */
    public double getJointWidth()
    {
        return jointWidth;
    }

    /**
     * Change la largeur des joints du couvrement.
     * @param jointWidth la nouvelle largeur des joints.
     */
    public void setJointWidth(double jointWidth)
    {
        this.jointWidth = jointWidth;
        cover();
    }

    /**
     * Retourne true si l'orientation est à 90 degrés, false si elle est à 0 degré.
     * @return true si l'orientation est à 90 degrés, false si elle est à 0 degré.
     */
    public boolean isNinetyDegree()
    {
        return isNinetyDegree;
    }

    /**
     * Change l'orientation des tuiles du couvrement.
     * @param isNinetyDegree : true pour une orientation de 90 degrés, false pour 0 degré.
     */
    public void setIsNinetyDegree(boolean isNinetyDegree)
    {
        this.isNinetyDegree = isNinetyDegree;
        cover();
    } 

    /**
     * Retourne le motif du couvrement.
     * @return le motif du couvrement.
     */
    public Pattern getPattern()
    {
        return pattern;
    }

    /**
     * Change le motif du couvrement.
     * @param pattern : le nouveau motif.
     */
    public void setPattern(Pattern pattern)
    {
        this.pattern = pattern;
        cover();
    }
    
    /**
     * Retourne le type de tuile du couvrement.
     * @return le type de tuile du couvrement.
     */
    public TileType getTileType()
    {
        return tileType;
    }
    
    /**
     * Change le type de tuile du couvrement.
     * @param tileType : le nouveau type de tuile.
     */
    public void setTileType(TileType tileType)
    {
        this.tileType = tileType;
        cover();
    }

    /**
     * Retourne les tuiles du couvrement.
     * @return les tuiles du couvrement.
     */
    public java.util.ArrayList<Area> getTiles()
    {
        return tiles;
    }

    /**
     * Change l'offset (en pourcentage) entre les rangées paires et impaires.
     * @param rowOffset : le nouvel offset en pourcentage entre les rangées.
     */
    public void setRowOffset(int rowOffset)
    {
        this.rowOffset = rowOffset;
        cover();
    }
    
    /**
     * Retourne l'offset (en pourcentage) entre les rangées paires et impaires.
     * @return : le nouvel offset en pourcentage entre les rangées.
     */
    public int getRowOffset()
    {
        return rowOffset;
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        tiles = new java.util.ArrayList<>();
        in.defaultReadObject();
        cover();
    }
}