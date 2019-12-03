package VirtuTuile.Domain.CoveringStrategies;

import java.awt.geom.Area;
import java.util.ArrayList;

public class TwoByTwoCoveringStrategy implements CoveringStrategy
{
    private final double tileWidth;
    private final double tileHeight;
    private final double jointWidth;
    private final double offsetX;
    private final double offsetY;
    private final Area fullArea;
    private final ArrayList<Area> tiles = new java.util.ArrayList<>();
    
    public TwoByTwoCoveringStrategy(
            double tileWidth, double tileHeight, double jointWidth,
            double offsetX, double offsetY, Area fullArea)
    {
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.jointWidth = jointWidth;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.fullArea = fullArea;
    }
    
    @Override
    public ArrayList<Area> cover()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
