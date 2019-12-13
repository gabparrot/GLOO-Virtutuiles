package VirtuTuile.Domain.CoveringStrategies;

import VirtuTuile.Infrastructure.Geometry;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class TwoByTwoCoveringStrategy implements CoveringStrategy
{

    private final double longLength;
    private final double shortLength;
    private final double jointWidth;
    private final double offsetX;
    private final double offsetY;
    private final Area fullArea;
    private final ArrayList<Area> tiles = new java.util.ArrayList<>();

    public TwoByTwoCoveringStrategy(
            double tileWidth, double tileHeight, double jointWidth,
            double offsetX, double offsetY, Area fullArea)
    {
        longLength = tileWidth > tileHeight ? tileWidth : tileHeight;
        shortLength = tileWidth > tileHeight ? tileHeight : tileWidth;
        this.jointWidth = jointWidth;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.fullArea = fullArea;
    }

    @Override
    public ArrayList<Area> cover()
    {
        if (longLength != 2 * shortLength + jointWidth)
        {
            return tiles;
        }
        
        double offsetXMod = offsetX % (2 * longLength + jointWidth);
        double offsetYMod = offsetY % (2 * longLength + jointWidth);
        
        Rectangle2D bounds = fullArea.getBounds2D();
        Area innerArea = Geometry.getInnerArea(fullArea, jointWidth);
        Point2D.Double currentPoint = new Point2D.Double(
                bounds.getX() - 2 * longLength - 2 * jointWidth + offsetXMod,
                bounds.getY() - 3 * longLength - 3 * jointWidth + offsetYMod);
        
        Point2D.Double firstTileCorner;
        Point2D.Double secondTileCorner;
        int tileCount = 1;
        int rowCount = 1;
        
        while(currentPoint.y - longLength - jointWidth < bounds.getMaxY())
        {
            firstTileCorner = currentPoint;
            
            if(tileCount < 3)
            {
                secondTileCorner = new Point2D.Double(currentPoint.x + shortLength, currentPoint.y + longLength);                                    
            }
            else
            {
                secondTileCorner = new Point2D.Double(currentPoint.x + longLength, currentPoint.y + shortLength);
            }
            
            Geometry.addTilesToList(firstTileCorner, secondTileCorner, tiles, innerArea);
            
            if(currentPoint.x - longLength - jointWidth < bounds.getMaxX())
            {
               switch(tileCount)
                {
                    case 1: case 2:
                        currentPoint.x += shortLength + jointWidth;
                        tileCount++;
                        break;                    
                    case 3:
                        currentPoint.y += shortLength + jointWidth;
                        tileCount++;
                        break;
                    case 4:
                        currentPoint.x += longLength + jointWidth;
                        currentPoint.y -= shortLength + jointWidth;
                        tileCount = 1;
                        break;
                }                            
            }
            else
            {
                rowCount += 1;
                tileCount = 1;
                if(rowCount % 2 == 0)
                {
                    currentPoint.x = bounds.getX() - 4 * shortLength - 4 * jointWidth + offsetXMod;
                    currentPoint.y += longLength + jointWidth;
                }
                else
                {                    
                    currentPoint.x = bounds.getX() - 3 * longLength - 3 * jointWidth + offsetXMod;
                    currentPoint.y += longLength + jointWidth;
                }                
            }
        }

        return tiles;
    }
}