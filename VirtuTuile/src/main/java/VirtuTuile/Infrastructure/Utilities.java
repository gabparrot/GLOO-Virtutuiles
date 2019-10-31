package VirtuTuile.Infrastructure;

import VirtuTuile.Domain.CombinedSurface;
import VirtuTuile.Domain.IrregularSurface;
import VirtuTuile.Domain.RectangularSurface;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.math.BigDecimal;
import java.util.ArrayList;

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

    /**
     * Donne l'aire de la Shape demandee (ou surface)
     * @param surface L'object Surface dont on veut connaître l'aire
     * @return area l'aire de la surface, un double
     */
    public static double getSurfaceArea(Shape surface) {
        double area = 0;
        if (surface instanceof RectangularSurface) {
            return surface.getBounds2D().getHeight() * surface.getBounds2D().getWidth();
        } else if (surface instanceof CombinedSurface) {
            if (((CombinedSurface) surface).isRectangular()) {
                return surface.getBounds2D().getHeight() * surface.getBounds2D().getWidth();
            } else {
                for (int i = 0; i < ((CombinedSurface) surface).getAbsorbedSurfaces().size(); i++) {
                    area = area + getSurfaceArea(((CombinedSurface) surface).getAbsorbedSurfaces().get(i));
                }
            }
        } else if (surface instanceof IrregularSurface) {
            ArrayList<Double> allX = new ArrayList<>();
            ArrayList<Double> allY = new ArrayList<>();
            int nbPoints = 0;
            AffineTransform at = new AffineTransform();
            PathIterator iter = ((IrregularSurface) surface).getPathIterator(at);
            // Compter le nombre de points et placer les X et Y dans des listes
            for (; iter.isDone(); iter.next()) {
                nbPoints++;
                double[] currentCoords = new double[2];
                iter.currentSegment(currentCoords);
                allX.add(currentCoords[0]);
                allY.add(currentCoords[1]);
            }
            double base;
            double height;
            switch (nbPoints) {
            // Cas d'erreurs retournent 0            }
                case 0:
                    area = 0;
                    break;
                case 1:
                    area = 0;
                    break;
                case 2:
                    area = 0;
                    break;
            //triangle
                case 3:
                    base = Math.sqrt(Math.pow(allX.get(0) - allX.get(1), 2) + Math.pow(allY.get(0) - allY.get(1), 2));
                    height = Math.sqrt(Math.pow(allX.get(0) - allX.get(2), 2) + Math.pow(allY.get(0) - allY.get(2), 2));
                    area = (base * height) / 2;
                    break;
            //Rectangle (Pourrait exister par fusion 2 triangle par exemple)
                case 4:
                    base = Math.sqrt(Math.pow(allX.get(0) - allX.get(1), 2) + Math.pow(allY.get(0) - allY.get(1), 2));
                    height = Math.sqrt(Math.pow(allX.get(0) - allX.get(2), 2) + Math.pow(allY.get(0) - allY.get(2), 2));
                    area = (base * height);
                    break;
            // Calcule l'aire de tout polygone de 5+ sommets
                default:
                    int j = nbPoints - 1;
                    for (int i = 0; i < nbPoints; i++) {
                        area = area + (allX.get(j) + allX.get(i)) * (allY.get(j) - allY.get(i));
                        j = i;
                    }
                    area = area / 2;
                    return area;
            }
        }
        return area;
    }
}
