package VirtuTuile.Infrastructure;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

/**
 * Rassemblement de constantes et de fonctions utilitaires.
 * Tout est statique.
 */
public final class Utilities
{
    // Nombre de millimètres par pixel lorsque le zoom est à 100%.
    public static final int MM_PER_PIXEL = 5;

    private final static NumberFormat FORMAT = NumberFormat.getInstance(Locale.getDefault());
    
    /**
     * Extrait un nombre décimal d'un chaîne de caractères selon le locale de l'utilisateur.
     * @param s : la chaîne de caractères.
     * @return le nombre en décimal.
     * @throws ParseException si chaîne invalide.
     */
    public static double parseDoubleLocale(String s) throws ParseException
    {
        try
        {
            Number number = FORMAT.parse(s);
            return number.doubleValue();
        }
        catch (ParseException e)
        {
            throw e;
        }
    }
    
    /**
     * Verifie si la string contient un espace à l'intérieur, excluant le début ou la fin
     * @param s String à vérifier
     * @return hasSpaceSplit true si contient un espace intérieur, false sinon
     */
    public static boolean stringHasSpaceSplit(String s)
    {
        boolean hasSpaceSplit = false;
        String trimmedS = s.trim();
        
        if (trimmedS.indexOf(' ') != -1 && trimmedS.lastIndexOf(' ') == trimmedS.indexOf(' '))
        {
            hasSpaceSplit = true;
        }
        
        return hasSpaceSplit;
    }
    
    /**
     * Verifie si la string contient un slash à l'intérieur, excluant le premier et le dernier caractère
     * @param s String à vérifier
     * @return hasSpaceSplit true si contient un espace intérieur, false sinon
     */
    public static boolean stringHasSlashSplit(String s)
    {
        boolean hasSlashSplit = false;
        String trimmedS = s.trim();
        
        if (trimmedS.indexOf('/') != -1 && trimmedS.indexOf('/') != 0 && 
                trimmedS.indexOf('/') != trimmedS.length() - 1 &&
                trimmedS.lastIndexOf('/') == trimmedS.indexOf('/'))
        {
            hasSlashSplit = true;
        }
        
        return hasSlashSplit;
    }
    
    /**
     * Prend une string contenant des pouces entiers, fractionnaires ou les deux séparés par un espace, et retourne un
     * double avec le total en pouces
     * @param inchesFieldText la string à traiter
     * @return un double représentant le total de pouces contenu dans la string reçue
     * @throws ParseException Exception engendrée lors de la lecture d'un double invalide
     */
    public static double getInchesFromField(String inchesFieldText) throws ParseException
    {
        double dblFullInches = 0;
        double numerator = 0;
        double denominator = 1;
        String strFractionInches = "";
        try
        {
            if (Utilities.stringHasSpaceSplit(inchesFieldText))
            {
                inchesFieldText = inchesFieldText.trim();
                String[] spaceSplit = inchesFieldText.split(" ");
                String strFullInches = spaceSplit[0];
                strFractionInches = spaceSplit[1];
                dblFullInches = Utilities.parseDoubleLocale(strFullInches);
            }
            else if (Utilities.stringHasSlashSplit(inchesFieldText))
            {
                strFractionInches = inchesFieldText.trim();
            }
            else
            {
                dblFullInches = Utilities.parseDoubleLocale(inchesFieldText);
            }
            if (!"".equals(strFractionInches))
            {
                strFractionInches = strFractionInches.trim();
                String[] slashSplit = strFractionInches.split("/");
                numerator = Utilities.parseDoubleLocale(slashSplit[0]);
                denominator = Utilities.parseDoubleLocale(slashSplit[1]);
            }
        }
        catch(ParseException e) { throw e; }
        return dblFullInches + (numerator / denominator);
    }
    
    /**
     * Converti un nombre de pixels en millimètres.
     * @param pixels : le nombre de pixels.
     * @param zoom : le facteur avec lequel les pixels doivent être convertis.
     * @return la conversion en millimètres.
     */
    public static double pixelsToMm(double pixels, double zoom)
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
        return (int) (mm * 0.00328084);
    }
    
    /**
     * Converti un nombre de millimètres en un restant de pouces après avoir converti en pieds.
     * @param mm : le nombre de millimètres.
     * @return la conversion en pouces restant après la convertion en pieds.
     */
    public static double mmToRemainingInches(double mm)
    {
        int feet = mmToFeet(mm);
        double inches = mm * 0.0393701;
        return inches - feet * 12;
    }
    
    /**
     * Converti un nombre de millimètres en pouces.
     * @param mm : le nombre de millimètres.
     * @return la conversion en pouces.
     */
    public static double mmToInches(double mm)
    {
        return mm * 0.0393701;
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
     * Converti un nombre de pieds en mm.
     * @param feet: le nombre de pieds.
     * @return la conversion en millimètres
     */
    public static double feetToMm(double feet)
    {
        return feet * 304.8;
    }
    
    /**
     * Converti un nombre de pouces en mm.
     * @param inches: le nombre de pouces.
     * @return la conversion en millimètres
     */
    public static double inchesToMm(double inches)
    {
        return inches * 25.4;
    }
    
    /**
     * Prend deux coins et retourne un rectangle.
     * @param corner1 le premier coin.
     * @param corner2 le deuxième coin.
     * @return le rectangle défini par les deux coins
     */
    public static Rectangle2D.Double cornersToRectangle(Point2D.Double corner1, Point2D.Double corner2)
    {
        // Ordonne les coordonnées.
        double leftMostX, rightMostX, topMostY, downMostY;
        if (corner1.x < corner2.x)
        {
            leftMostX = corner1.x;
            rightMostX = corner2.x;
        }
        else
        {
            leftMostX = corner2.x;
            rightMostX = corner1.x;
        }
        if (corner1.y < corner2.y)
        {
            topMostY = corner1.y;
            downMostY = corner2.y;
        }
        else
        {
            topMostY = corner2.y;
            downMostY = corner1.y;
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
    public static void movePointToGrid(Point2D.Double point, double gridDistance)
    {
        gridDistance *= MM_PER_PIXEL;
        
        double deltaX = point.x % gridDistance;
        double deltaY = point.y % gridDistance;
        double newX, newY;
        
        if (deltaX < gridDistance / 2)
        {
            newX = point.x - deltaX;
        }
        else
        {
            newX = point.x + gridDistance - deltaX;
        }
        
        if (deltaY < gridDistance / 2)
        {
            newY = point.y - deltaY;
        }
        else
        {
            newY = point.y + gridDistance - deltaY;
        }
        point.setLocation(newX, newY);
    }
}
