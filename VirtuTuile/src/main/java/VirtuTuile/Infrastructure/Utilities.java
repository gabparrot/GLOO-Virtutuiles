package VirtuTuile.Infrastructure;

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
    public static int pixelsToMm(int pixels, double zoom)
    {
        return (int) (pixels * MM_PER_PIXEL / zoom);
    }
    
    /**
     * Converti un nombre de millimètres en pieds. Retourne un nombre entier.
     * @param mm : le nombre de millimètres.
     * @return la conversion en pieds en nombre entier.
     */
    public static int mmToFeet(int mm)
    {
        return (int) (mm / 1000. * 3.28084);
    }
    
    /**
     * Converti un nombre de millimètres en un restant de pouces après avoir converti en pieds.
     * @param mm : le nombre de millimètres.
     * @return la conversion en pouces restant après la convertion en pieds.
     */
    public static double mmToRemainingInches(int mm)
    {
        int feet = mmToFeet(mm);
        double inches = mm / 1000. * 39.3701;
        return inches - feet * 12;
    }
    
    /**
     * Converti un nombre de pouces en centimètres.
     * @param inches : le nombre de pouces.
     * @return le nombre de centimètres.
     */
    public static double inchesToCm(int inches)
    {
        return inches * 2.54;
    }
}
