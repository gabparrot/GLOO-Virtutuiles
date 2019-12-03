package VirtuTuile.Infrastructure;

import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public final class Geometry
{
    public static Area getInnerArea(Area fullArea, double jointWidth)
    {
        Area innerArea = new Area(fullArea);
        
        Area topCopy = new Area(fullArea);
        AffineTransform translate = new AffineTransform();
        translate.translate(0, -jointWidth);
        topCopy.transform(translate);
        
        Area rightCopy = new Area(fullArea);
        translate = new AffineTransform();
        translate.translate(jointWidth, 0);
        rightCopy.transform(translate);
        
        Area leftCopy = new Area(fullArea);
        translate = new AffineTransform();
        translate.translate(-jointWidth, 0);
        leftCopy.transform(translate);
        
        Area botCopy = new Area(fullArea);
        translate = new AffineTransform();
        translate.translate(0, jointWidth);
        botCopy.transform(translate);
        
        innerArea.intersect(topCopy);
        innerArea.intersect(rightCopy);
        innerArea.intersect(leftCopy);
        innerArea.intersect(botCopy);
        
        return innerArea;
    }
    
    public static void addTilesToList(Point2D.Double c1, Point2D.Double c2, ArrayList<Area> tiles, Area innerArea)
    {
        Area tile = new Area(Utilities.cornersToRectangle(c1, c2));
        tile.intersect(innerArea);
        if (!tile.isEmpty())
        {
            if (tile.isSingular())
            {
                tiles.add(tile);
            }
            else
            {
                ArrayList<Area> subTiles = divideTile(tile);
                for (Area subTile : subTiles)
                {
                    subTile.intersect(innerArea);
                    if (!subTile.isEmpty())
                    {
                        tiles.add(subTile);
                    }
                }
            }
        }
    }
    
    /**
     * Prend une tuile qui n'est pas singulière et la divise en des tuiles singulières.
     * @param tile : une tuile non-singulière.
     * @return une liste de tuiles singulières.
     */
    public static ArrayList<Area> divideTile(Area tile)
    {
        PathIterator iterator = tile.getPathIterator(null);
        ArrayList<Area> subTiles = new ArrayList<>();
        Path2D.Double path = new Path2D.Double();
        while(!iterator.isDone())
        {
            double[] vertex = new double[6];
            int segmentType = iterator.currentSegment(vertex); 
            switch (segmentType)
            {
                case PathIterator.SEG_MOVETO:
                    path.moveTo(vertex[0], vertex[1]);
                    break;
                case PathIterator.SEG_CLOSE:
                    subTiles.add(new Area(path));
                    path.reset(); 
                    break;
                case PathIterator.SEG_LINETO:
                    path.lineTo(vertex[0], vertex[1]);
                    break;
                case PathIterator.SEG_QUADTO:
                    path.quadTo(vertex[0], vertex[1], vertex[2], vertex[3]);
                    break;
                case PathIterator.SEG_CUBICTO:
                    path.curveTo(vertex[0], vertex[1], vertex[2], vertex[3], vertex[4], vertex[5]);
                    break;
            }
            iterator.next();
        }
        return subTiles;
    }
}
