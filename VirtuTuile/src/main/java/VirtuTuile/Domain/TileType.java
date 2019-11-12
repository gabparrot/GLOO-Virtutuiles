package VirtuTuile.Domain;

import java.awt.Color;
import java.util.ArrayList;
import java.io.Serializable;

/**
 * @class definissant un type de tuiles disponibles pour le tuilage
 * @author gabparrot
 */
public class TileType implements Serializable
{
    private double width;
    private double height;
    private ArrayList<Color> colors;
    private String name;
    private int nbPerBox;

    // Constructeur avec paramètres
    public TileType(double widthInput, double heightInput, String nameInput,
            int nbPerBoxInput,ArrayList<Color> colors)
    {
        this.width = widthInput;
        this.height = heightInput;
        this.name = nameInput;
        this.nbPerBox = nbPerBoxInput;
        this.colors = colors;
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
    
    public ArrayList<Color> getColors()
    {
        return colors;
    }
    
    public Color[] getColorArray()
    {
        Color[] colorArray = new Color[colors.size()];
        for (int i = 0; i < colors.size(); i++)
        {
            colorArray[i] = colors.get(i);
        }
        return colorArray;
    }
    
    public String[] getColorStrings()
    {
        String[] colorStrings = new String[colors.size()];
        for (int i = 0; i < colors.size(); i++)
        {
            Color c = colors.get(i);
            colorStrings[i] = c.getRed() + "," + c.getGreen() + "," + c.getBlue();
        }
        return colorStrings;
    }

    public void setColors(ArrayList<Color> colors)
    {
        this.colors = colors;
    }   
}