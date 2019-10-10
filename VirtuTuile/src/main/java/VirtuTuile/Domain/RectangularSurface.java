package VirtuTuile.Domain;

import java.awt.Rectangle;
import java.awt.Color;

/**
 *
 * @author gabparrot
 */
public class RectangularSurface extends Rectangle implements ElementarySurface
{
    private boolean isHole;
    private Color color;
    private boolean selectionStatus = false;
    
    /**
     * Constructeur avec parametres
     * @param isHole
     * @param color
     * @param x
     * @param y
     * @param width
     * @param height 
     */
    public RectangularSurface(boolean isHole, Color color, int x, int y, int width, int height)
    {
        super(x, y, width, height);
        this.isHole = isHole;
        this.color = color;
    }

    // Implémentation des méthodes de Surface
    @Override
    public int getNbTiles()
    {
        // TODO
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isHole()
    {
        // TODO
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Color getColor()
    {
        // TODO
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getCoordX()
    {
        // TODO
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getCoordY()
    {
        // TODO
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setHole(boolean isHole)
    {
        // TODO
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setColor(Color color)
    {
        // TODO
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setCovering(Covering covering)
    {
        // TODO
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setCoordX(int coordX)
    {
        // TODO
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setCoordY(int coordY)
    {
        // TODO
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void switchSelection()
    {
        // TODO
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isSelected()
    {
        // TODO
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
