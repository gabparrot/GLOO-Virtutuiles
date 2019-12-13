package VirtuTuile.Domain.CoveringStrategies;

import VirtuTuile.Infrastructure.Geometry;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class CheckeredCoveringStrategy implements CoveringStrategy
{
    private final double tileWidth;
    private final double tileHeight;
    private final double jointWidth;
    private final double offsetX;
    private final double offsetY;
    private final int rowOffset;
    private final boolean isNinetyDegree;
    private final Area fullArea;
    private final ArrayList<Area> tiles = new java.util.ArrayList<>();
    
    public CheckeredCoveringStrategy(
            double tileWidth, double tileHeight, double jointWidth,
            double offsetX, double offsetY, int rowOffset,
            boolean isNintetyDegree, Area fullArea)
    {
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.jointWidth = jointWidth;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.rowOffset = rowOffset;
        this.isNinetyDegree = isNintetyDegree;
        this.fullArea = fullArea;
    }
    
    @Override
    public ArrayList<Area> cover()
    {
        // Position et orientation
        double tWidth = isNinetyDegree ? tileHeight : tileWidth;
        double tHeight = isNinetyDegree ? tileWidth : tileHeight;
        double offsetXMod = this.offsetX % (tWidth + jointWidth);
        double offsetYMod = this.offsetY % (tHeight + jointWidth);
        double rowOffsetMod = tWidth - rowOffset / 100. * tWidth -  jointWidth * rowOffset / 100;
        
        Rectangle2D bounds = fullArea.getBounds2D();
        Area innerArea = Geometry.getInnerArea(fullArea, jointWidth);
        
        Point2D.Double currentPoint = new Point2D.Double(bounds.getX() - tWidth + offsetXMod,
                                                         bounds.getY() - tHeight + offsetYMod);
        
        // Détermine si on devrait commencer par une rangée paire ou impaire
        int rowCount = 0;
        boolean shouldInvertRow = shouldInvertRow(tHeight);
        if (shouldInvertRow)
        {
            rowCount++;
            currentPoint.x -= rowOffsetMod + jointWidth;
        }
        
        while (currentPoint.getX() < bounds.getMaxX() && currentPoint.y < bounds.getMaxY())
        {
            Point2D.Double firstTileCorner = currentPoint;
            Point2D.Double secondTileCorner = new Point2D.Double(
                    currentPoint.x + tWidth, currentPoint.y + tHeight);
            Geometry.addTilesToList(firstTileCorner, secondTileCorner, tiles, innerArea);
            
            if (currentPoint.x + tWidth + jointWidth < bounds.getMaxX())
            {
                currentPoint.x += tWidth + jointWidth;
            }
            else // Si on dépasse en X, on descend et on repart a gauche.
            {
                if (rowCount % 2 == 1)
                {
                    currentPoint.setLocation(bounds.getX() - tWidth + offsetXMod, 
                                             currentPoint.y + tHeight + jointWidth);
                }
                // Décaler les rangées paires (celle du haut est #1)
                else
                {
                    currentPoint.setLocation(bounds.getX() - tWidth + offsetXMod - rowOffsetMod - jointWidth,
                                             currentPoint.y + tHeight + jointWidth);                    
                }
                rowCount++;
            }
        }
        return tiles;
    }

    private boolean shouldInvertRow(double tileHeight)
    {
        double x = offsetY;
        if (x < 0)
        {
            x = Math.abs(x) + 2 * tileHeight + 2* jointWidth;
        }
        return x % ((tileHeight + jointWidth) * 2) < tileHeight + jointWidth;
    }
}
