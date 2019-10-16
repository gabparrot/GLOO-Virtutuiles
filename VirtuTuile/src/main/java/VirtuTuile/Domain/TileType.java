package VirtuTuile.Domain;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

/**
 * @class definissant un type de tuiles disponibles pour le tuilage
 * @author gabparrot
 */
public class TileType
{
    private int width;
    private int height;
    private Set<Color> colors = new HashSet<>();
    private String name;
    private int nbPerBox;

    // Constructeur avec paramètres
    public TileType(int widthInput, int heightInput, String nameInput, int nbPerBoxInput)
    {
        this.width = widthInput;
        this.height = heightInput;
        this.name = nameInput;
        this.nbPerBox = nbPerBoxInput;
    }
    
    // Getters et Setters
    public int getWidth()
    {
        return width;
    }

    public void setWidth(int width)
    {
        this.width = width;
    }

    public int getHeight()
    {
        return height;
    }

    public void setHeight(int height)
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
    
    public Set<Color> getColors()
    {
        return colors;
    }

    public void setColors(HashSet<Color> colors)
    {
        this.colors = colors;
    }   
}