/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VirtuTuile.Domain;

import java.awt.geom.Path2D;
import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.util.ArrayList;

/**
 *
 * @author gabparrot
 */
public class IrregularSurface extends Path2D.Double implements ElementarySurface
{
    private boolean isHole;
    private Color color;
    private final Covering covering = new Covering(this);
    
    // TODO vérifier si attributs de coordonnées doivent être ajoutés

    /**
     * Constructeur de IrregularSurface
     * @param isHole Booleen representant si oui on non la surface est un trou (non couvrable)
     * @param color objet Color representant la couleur de la surface, visible lorsqu'elle n'
     */
    public IrregularSurface(boolean isHole, Color color)
    {
        super();
        this.isHole = isHole;
        this.color = color;
    }

    /**
     * Getter du statut de trou (non couvrable)
     * @return isHole booleen representant si oui ou non la surface est un trou
     */    
    @Override
    public boolean isHole()
    {
        return isHole;
    }
    
    /**
     * Setter du statut de trou (non couvrable)
     * @param newStatus Booleen representant si oui ou non la surface devra etre un trou non couvrable
     */
    @Override
    public void setIsHole(boolean newStatus)
    {
        this.isHole = newStatus;
        coverSurface();
    }
    /**
     * Getter de la couleur de la surface, visible lorsqu'elle n'est pas couverte
     * @return color objet Color
     */
    @Override
    public Color getColor()
    {
        return color;
    }
    
    /**
     * Setter de la couleur de la surface, visible lorsqu'elle n'est pas couverte
     * @param newColor objet Color
     */
    @Override
    public void setColor(Color newColor)
    {
        this.color =  newColor;
    }
    
    /**
     * Getter du covering representant les tuiles sur la surface, si elle est couverte
     * @return covering l'objet covering 
     */
    @Override
    public Covering getCovering()
    {
        return covering;
    }
    
    /**
     * Retourne l'aire de cette IrregularSurface
     * @return area Un double representant l'aire
     */
    @Override
    public double getArea()
    {
        double area = 0;
        ArrayList<java.lang.Double> allX = new ArrayList<>();
        ArrayList<java.lang.Double> allY = new ArrayList<>();
        int nbPoints = 0;
        AffineTransform at = new AffineTransform();
        PathIterator iter = this.getPathIterator(at);
        
        // Compter le nombre de points et placer les X et Y dans des listes
        for (; iter.isDone(); iter.next()) 
        {
            nbPoints++;
            double[] currentCoords = new double[2];
            iter.currentSegment(currentCoords);
            allX.add(currentCoords[0]);
            allY.add(currentCoords[1]);
        }
        
        double base;
        double height;
        
        switch (nbPoints) 
        {
            
        // Cas d'erreurs retournent 0
            case 0:
                break;
                
            case 1:
                break;
                
            case 2:
                break;
                
        //triangle
            case 3:
                base = Math.sqrt(Math.pow(allX.get(0) - allX.get(1), 2) + Math.pow(allY.get(0) - allY.get(1), 2));
                height = Math.sqrt(Math.pow(allX.get(0) - allX.get(2), 2) + Math.pow(allY.get(0) - allY.get(2), 2));
                area = (base * height) / 2;
                break;
                
        // Calcule l'aire de tout polygone de 5+ sommets
            default:
                int j = nbPoints - 1;
                
                for (int i = 0; i < nbPoints; i++) 
                {
                    area = area + (allX.get(j) + allX.get(i)) * (allY.get(j) - allY.get(i));
                    j = i;
                }
                
                area = area / 2;
        }
        return area;
    }
    
    @Override
    public void coverSurface()
    {
        covering.cover();
    }
}
