/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VirtuTuile.Domain;

import java.awt.Shape;
import java.awt.Color;

/**
 *
 * @author gabparrot
 */
public interface Surface extends Shape
{
    boolean getIsHole();
    void setIsHole(boolean newStatus);  
    Color getColor();
    void setColor(Color color);
    boolean getSelectedStatus();
    void setSelectedStatus(boolean newStatus);
    Covering getCovering();
    void setCovering(int offsetX, int offsetY, Color groutColor,
                            int groutWidth, int angle, Pattern pattern, 
                            TileType tileType, Color color);
}
