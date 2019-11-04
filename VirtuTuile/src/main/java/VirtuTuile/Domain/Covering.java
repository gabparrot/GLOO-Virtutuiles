package VirtuTuile.Domain;

import VirtuTuile.Infrastructure.Utilities;
import java.awt.Color;
import java.awt.Shape;

/**
 * @class definissant les tuiles et leur dispositions sur une surface
 * @author gabparrot
 */
public class Covering
{
    private double offsetX = 0;
    private double offsetY = 0;
    private Color jointColor = Color.GRAY;
    private double jointWidth = 5;
    private boolean isNinetyDegree = false;
    private Pattern pattern = Pattern.A;
    private final java.util.ArrayList<Shape> tiles = new java.util.ArrayList<>();
    private TileType tileType = Utilities.DEFAULT_TILE_1;
    private Color tileColor = Utilities.DEFAULT_TILE_1.getColorArray()[0];
    
    public void clearCovering()
    {
        tiles.clear();
    }
    
    /**
     * Cette fonction est appelée chaque fois que le Covering doit être modifié.
     */
    public void coverSurface()
    {
        if (tileType == null || tileColor == null)
        {
            return;
        }
        else
        {
            tiles.clear();
        }
        switch (pattern)
        {
            case A:
                coverSurfaceA();
                break;
                
            case B:
                coverSurfaceB();
                break;
            
            case C:
                coverSurfaceC();
                break;
            
            case D:
                coverSurfaceD();
                break;
                
            case E:
                coverSurfaceE();
                break;
        }
    }
    
    private void coverSurfaceA()
    {
        /**
        * Plan du covering:
        * Creer un array d'objets tuiles(shape)
        * Dessiner à partir de la coord max X max Y (coin en haut a gauche)
        * Pour la premiere tuile, si offset plus petit que dimension (x ou y), arreter tuile a cette dimension
        * Le GUI inspetor mode s'occupera de montrer les tuiles trop petites, on les cree quand même
        * Pour les autres tuiles, on trace la tuile selon la dimension du tileType donne
        * On parcourt de gauche a droite de haut en bas
        * apres chaque tuile on cree la nouvelle tuile le nombre de pixels plus loin donne par groutWidth
        * Comme la couleur est pareille pour chaque tuile, pas besoin de donner l'attribut a chaque
        * Il sera lu par le GUI directement comme attribut de covering
        * Arreter de tracer la tuile quand on atteint la bordure et passer a la suivante
        * Chaque fois qu'on atteint une bordure en X, se deplacer de hauteur de tuile + coulis vers le bas
        * Si surface irreguliere, possiblement parcourir tout le canevas avec contains pour ne pas skip
        * Le GUI doit recevoir l'array de tuiles au complet en retour
        * Le GUI pourrait simplement imposer une couleur de background = groutColor prioritaire sur surface color
        * Le GUI dessine ensuite toutes les tuiles (rect shape) et les fill avec la tileColor
        * La balance est la couleur de coulis qui apparait entre les tuiles
        * Le deplacement flush tout et refait toutes les tuiles en temps 
        */
    }
    
    private void coverSurfaceB()
    {
        
    }
    
    private void coverSurfaceC()
    {
        
    }
    
    private void coverSurfaceD()
    {
        
    }
    
    private void coverSurfaceE()
    {
        
    }
    
    // Getters et Setters
    public int getNbTiles()
    {
        return tiles.size();
    }
    
    public double getOffsetX()
    {
        return offsetX;
    }

    public void setOffsetX(double offsetX)
    {
        this.offsetX = offsetX;
    }

    public double getOffsetY()
    {
        return offsetY;
    }

    public void setOffsetY(double offsetY)
    {
        this.offsetY = offsetY;
    }

    public Color getJointColor()
    {
        return jointColor;
    }

    public void setJointColor(Color groutColor)
    {
        this.jointColor = groutColor;
    }

    public double getJointWidth()
    {
        return jointWidth;
    }

    public void setJointWidth(double groutWidth)
    {
        this.jointWidth = groutWidth;
    }

    public boolean isNinetyDegree()
    {
        return isNinetyDegree;
    }

    public void setIsNinetyDegree(boolean isNinetyDegree)
    {
        this.isNinetyDegree = isNinetyDegree;
    }

    public Pattern getPattern()
    {
        return pattern;
    }

    public void setPattern(Pattern pattern)
    {
        this.pattern = pattern;
    }
    
    public TileType getTileType()
    {
        return tileType;
    }
    
    public void setTileType(TileType tileType)
    {
        this.tileType = tileType;
    }

    public java.util.ArrayList<Shape> getTiles()
    {
        return tiles;
    }
    
    public Color getTileColor()
    {
        return tileColor;
    }
    
    public void setTileColorByIndex(int index)
    {
        this.tileColor = tileType.getColors().get(index);
    }
    
    public void setTileColor(Color color)
    {
        this.tileColor = color;
    }
    
    public int getTileColorIndex()
    {
        return tileType.getColors().indexOf(tileColor);
    }

    public Object getColorString()
    {
        return tileColor;
    }
}