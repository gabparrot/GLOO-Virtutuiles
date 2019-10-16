/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VirtuTuile.Domain;

import java.awt.Polygon;
import java.awt.Color;

/**
 *
 * @author gabparrot
 */
public class IrregularSurface extends Polygon implements ElementarySurface
{
    private boolean isHole;
    private Color color;
    private Covering covering;
    
    // TODO vérifier si attributs de coordonnées doivent être ajoutés

    public IrregularSurface(boolean isHole, Color color, int[] xpoints, 
                            int[] ypoints, int npoints)
    {
        super(xpoints, ypoints, npoints);
        this.isHole = isHole;
        this.color = color;
    }

    @Override
    public boolean isHole()
    {
        return isHole;
    }

    @Override
    public Color getColor()
    {
        return color;
    }

    @Override
    public void setColor(Color newColor)
    {
        this.color =  newColor;
    }

    @Override
    public void setIsHole(boolean newStatus)
    {
        this.isHole = newStatus;
    }
    
    @Override
    public Covering getCovering()
    {
        return covering;
    }

    @Override
    public void setCovering(int offsetX, int offsetY, Color groutColor,
                            int groutWidth, int angle, Pattern pattern, 
                            TileType tileType, Color tileColor)
    {
        this.covering = new Covering(offsetX, offsetY, groutColor,
                                     groutWidth, angle, pattern, tileType, 
                                     tileColor);
    }
}
