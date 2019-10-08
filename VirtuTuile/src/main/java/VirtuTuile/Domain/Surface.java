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
    int getNbTiles();
    boolean isHole();
    Color getColor();
    int getCoordX();
    int getCoordY();
    void setHole(boolean isHole);
    void setColor(Color color);
    void setCovering(Covering covering);
    void setCoordX(int coordX);
    void setCoordY(int coordY);
    void switchSelection();
    boolean isSelected();
}
