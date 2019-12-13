package VirtuTuile.Domain.CoveringStrategies;

import VirtuTuile.Infrastructure.Geometry;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class LShapeCoveringStrategy implements CoveringStrategy
{
    private final double longLength;
    private final double shortLength;
    private final double jointWidth;
    private final double offsetX;
    private final double offsetY;
    private final Area fullArea;
    private final ArrayList<Area> tiles = new java.util.ArrayList<>();
    
    public LShapeCoveringStrategy(
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
        
        double offsetXMod = offsetX % (longLength + 2 * shortLength + 2 * jointWidth);
        double offsetYMod = offsetY % (longLength + 2 * shortLength + 2 * jointWidth);
        
        Rectangle2D bounds = fullArea.getBounds2D();
        Area innerArea = Geometry.getInnerArea(fullArea, jointWidth);
        Point2D.Double currentPoint = new Point2D.Double(
                bounds.getX() - longLength - shortLength - jointWidth + offsetXMod,
                bounds.getY() - longLength - 3 * shortLength - 3 * jointWidth + offsetYMod);
        
        int rowCount = 3;
        double nextJump = longLength + jointWidth;
        boolean standingNext = true;
        Point2D.Double firstTileCorner;
        Point2D.Double secondTileCorner;
        
        while (currentPoint.y < bounds.getMaxY())
        {
            firstTileCorner = currentPoint;
            if (standingNext)
            {
                secondTileCorner = new Point2D.Double(
                        currentPoint.x + shortLength, currentPoint.y + longLength);
            }
            else
            {
                secondTileCorner = new Point2D.Double(
                        currentPoint.x + longLength, currentPoint.y + shortLength);
            }
            Geometry.addTilesToList(firstTileCorner, secondTileCorner, tiles, innerArea);
            
            if (currentPoint.x + nextJump < bounds.getMaxX())
            {
                currentPoint.x += nextJump;
                standingNext = !standingNext;
            }
            // Si on dépasse en X, on descend et on repart a gauche.
            else
            {
                currentPoint.x = bounds.getX() - longLength - 2 * shortLength - 2 * jointWidth + offsetXMod;
                currentPoint.y += shortLength + jointWidth;
                rowCount++;
                switch (rowCount % 4)
                {
                    case 0:
                        standingNext = true;
                        break;
                    case 1:
                        currentPoint.x += shortLength + jointWidth;
                        standingNext = false;
                        break;
                    case 2:
                        standingNext = false;
                        break;
                    case 3:
                        currentPoint.x -= shortLength + jointWidth;
                        standingNext = false;
                }
            }
        }
        return tiles;
    }
}
