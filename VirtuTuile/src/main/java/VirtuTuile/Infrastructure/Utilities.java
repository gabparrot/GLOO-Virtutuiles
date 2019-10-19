package VirtuTuile.Infrastructure;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.math.BigDecimal;

/**
 * Rassemblement de constantes et de fonctions utilitaires. Tout est statique.
 * @author Petros Fytilis
 */
public final class Utilities
{
    // Nombre de millimètres par pixel lorsque le zoom est à 100%.
    public static final int MM_PER_PIXEL = 10;
    
    /**
     * Converti un nombre de pixels en millimètres.
     * @param pixels : le nombre de pixels.
     * @param zoom : le facteur avec lequel les pixels doivent être convertis.
     * @return la conversion en millimètres.
     */
    public static double pixelsToMm(int pixels, double zoom)
    {
        return pixels * MM_PER_PIXEL / zoom;
    }
    
    /**
     * Converti un nombre de millimètres en pieds. Retourne un nombre entier.
     * @param mm : le nombre de millimètres.
     * @return la conversion en pieds en nombre entier.
     */
    public static int mmToFeet(double mm)
    {
        return (int) (mm / 1000. * 3.28084);
    }
    
    /**
     * Converti un nombre de millimètres en un restant de pouces après avoir converti en pieds.
     * @param mm : le nombre de millimètres.
     * @return la conversion en pouces restant après la convertion en pieds.
     */
    public static double mmToRemainingInches(double mm)
    {
        int feet = mmToFeet(mm);
        double inches = mm / 1000 * 39.3701;
        return inches - feet * 12;
    }
    
    /**
     * Converti un nombre de pouces en centimètres.
     * @param inches : le nombre de pouces.
     * @return le nombre de centimètres.
     */
    public static double inchesToCm(double inches)
    {
        return inches * 2.54;
    }
    
    
    /**
     * Converti un nombre de pieds en mm
     * @param feet: le nombre de pouces
     * @return la conversion en millimètres
     */
    public static double feetToMM(double feet)
    {
        return feet * 304.8;
    }
    
    /**
     * Converti un nombre de pouces en mm
     * @param inches: le nombre de pouces
     * @return la conversion en millimètres
     */
    public static double inchesToMM(double inches)
    {
        return inches * 25.4;
    }
    
    /**
     * Prend deux coins et retourne un rectangle.
     * @param corner1 le premier coin.
     * @param corner2 le deuxième coin.
     * @return le rectangle défini par les deux coins
     */
    public static Rectangle2D.Double cornersToRectangle(Point2D corner1, Point2D corner2)
    {
        // Ordonne les coordonnées.
        double leftMostX, rightMostX, topMostY, downMostY;
        if (corner1.getX() < corner2.getX())
        {
            leftMostX = corner1.getX();
            rightMostX = corner2.getX();
        }
        else
        {
            leftMostX = corner2.getX();
            rightMostX = corner1.getX();
        }
        if (corner1.getY() < corner2.getY())
        {
            topMostY = corner1.getY();
            downMostY = corner2.getY();
        }
        else
        {
            topMostY = corner2.getY();
            downMostY = corner1.getY();
        }
        double width = rightMostX - leftMostX;
        double height = downMostY - topMostY;
        return new Rectangle2D.Double(leftMostX, topMostY, width, height);
    }
    
    /**
     * Déplace un point sur la grille (utilisé pour la grille magnétique).
     * @param point : le point en métrique qui doit être déplacé
     * @param gridDistance : la distance de la grille, en pixels
     */
    public static void movePointToGrid(Point2D point, double gridDistance)
    {
        gridDistance *= MM_PER_PIXEL;
        
        double deltaX = point.getX() % gridDistance;
        double deltaY = point.getY() % gridDistance;
        double newX, newY;
        
        if (deltaX < gridDistance / 2)
        {
            newX = point.getX() - deltaX;
        }
        else
        {
            newX = point.getX() + gridDistance - deltaX;
        }
        
        if (deltaY < gridDistance / 2)
        {
            newY = point.getY() - deltaY;
        }
        else
        {
            newY = point.getY() + gridDistance - deltaY;
        }
        point.setLocation(newX, newY);
    }
}
