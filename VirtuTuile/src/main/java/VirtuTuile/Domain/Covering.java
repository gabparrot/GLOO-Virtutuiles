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
public class Covering implements Serializable, Cloneable
{
    private double offsetX = 0;
    private double offsetY = 0;
    private int rowOffset = 0;
    private Color jointColor = Color.GRAY;
    private double jointWidth = 5;
    private boolean isNinetyDegree = false;
    private Pattern pattern = Pattern.A;
    private final java.util.ArrayList<Area> tiles = new java.util.ArrayList<>();
    private TileType tileType = Utilities.DEFAULT_TILE_1;
    private Surface parent;
    
    public Covering(Surface parent)
    {
        this.parent = parent;
        coverSurface();
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
    
    public void clearCovering()
    {
        tiles.clear();
    }
    
    public void cover()
    {
        coverSurface();
    }
    
    /**
     * Cette fonction est appelée chaque fois que le Covering doit être modifié.
     */
    private void coverSurface()
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
        
        double offsetXMod = this.offsetX % tileWidth;
        double offsetYMod = this.offsetY % tileHeight;
        double rowOffsetMod;
        if (rowOffset == 0)
        {
            rowOffsetMod = 0;
        }
        else
        {
            rowOffsetMod = tileWidth - (this.rowOffset * tileWidth); 
        }
        

        Area fullArea = new Area(parent);
        Rectangle2D bounds = fullArea.getBounds2D();
        if (parent instanceof CombinedSurface)
        {
            fullArea.subtract(((CombinedSurface) parent).getUncoveredArea());
        }
        
        Area innerArea = getInnerArea(fullArea);
        
        Point2D.Double currentPoint = new Point2D.Double(bounds.getX() - tileWidth + offsetXMod,
                                                         bounds.getY() - tileHeight + offsetYMod);       

        int rowCount = 1;
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
            
            // Si on dépasse en X, on descend et on repart a gauche.
            if (currentPoint.x + tileWidth + jointWidth < bounds.getMaxX())
            {
                currentPoint.x = currentPoint.getX() + tileWidth + jointWidth;
            }
            else if (rowCount % 2 != 0)
            {
                currentPoint.setLocation(bounds.getX() - tileWidth + offsetXMod, 
                                         currentPoint.y + tileHeight + jointWidth);
            }
            else
            {
                currentPoint.setLocation(bounds.getX() - tileWidth + offsetXMod - rowOffsetMod,
                                         currentPoint.y + tileHeight + jointWidth);
            }
            
            rowCount += 1;
        }
        System.out.println("Quantité de tuiles posées sur cette surface: " + tiles.size()); //TEST
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
        coverSurface();
    }

    public double getOffsetY()
    {
        return offsetY;
    }

    public void setOffsetY(double offsetY)
    {
        this.offsetY = offsetY;
        coverSurface();
    }

    public Color getJointColor()
    {
        return jointColor;
    }

    public void setJointColor(Color jointColor)
    {
        this.jointColor = jointColor;
        coverSurface();
    }

    public double getJointWidth()
    {
        return jointWidth;
    }

    public void setJointWidth(double jointWidth)
    {
        this.jointWidth = jointWidth;
        coverSurface();
    }

    public boolean isNinetyDegree()
    {
        return isNinetyDegree;
    }

    public void setIsNinetyDegree(boolean isNinetyDegree)
    {
        this.isNinetyDegree = isNinetyDegree;
        coverSurface();
    } 

    public Pattern getPattern()
    {
        return pattern;
    }

    public void setPattern(Pattern pattern)
    {
        this.pattern = pattern;
        coverSurface();
    }
    
    public TileType getTileType()
    {
        return tileType;
    }
    
    public void setTileType(TileType tileType)
    {
        this.tileType = tileType;
        coverSurface();
    }

    public java.util.ArrayList<Area> getTiles()
    {
        return tiles;
    }

    public void setRowOffset(int rowOffset)
    {
        this.rowOffset = rowOffset;
        coverSurface();
    }
    
    public int getRowOffset()
    {
        return rowOffset;
    }
}