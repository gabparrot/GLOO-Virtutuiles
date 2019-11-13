package VirtuTuile.Domain;

import java.awt.Color;
import java.io.Serializable;

/**
 * @class definissant un type de tuiles disponibles pour le tuilage
 * @author gabparrot
 */
public class TileType implements Serializable
{
    private double width;
    private double height;
    private Color color;
    private String name;
    private int nbPerBox;

    // Constructeur avec paramètres
    public TileType(double widthInput, double heightInput, String nameInput,
            int nbPerBoxInput, Color color)
    {
        this.width = widthInput;
        this.height = heightInput;
        this.name = nameInput;
        this.nbPerBox = nbPerBoxInput;
        this.color = color;
    }
    
    // Getters et Setters
    public double getWidth()
    {
        return width;
    }

    public void setWidth(double width)
    {
        this.width = width;
    }

    public double getHeight()
    {
        return height;
    }

    public void setHeight(double height)
    {
        this.height = height;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getNbPerBox()
    {
        return nbPerBox;
    }

    public void setNbPerBox(int nbPerBox)
    {
        this.nbPerBox = nbPerBox;
    }
    
    public Color getColor()
    {
        return color;
    }

    public void setColor(Color color)
    {
        this.color = color;
    }   
}