package VirtuTuile.Infrastructure;

/**
 * Rassemblement de constantes et de fonctions utilitaires. Tout est statique.
 * @author Petros Fytilis
 */
public final class Utilities
{
    // Nombre de pixels par mètre lorsque le zoom est à 100%.
    public static final int PIXELS_PAR_METRE_BASE = 100;
    
    /**
     * Converti un nombre de pixels en mètres.
     * @param pixels : le nombre de pixels.
     * @param zoom : le facteur avec lequel les pixels doivent être convertis.
     * @return la conversion en mètres.
     */
    public static double pixelsToMeters(int pixels, double zoom)
    {
        return pixels / (PIXELS_PAR_METRE_BASE * zoom);
    }
    
    /**
     * Converti un nombre de mètres en pieds. Retourne un nombre entier.
     * @param meters : le nombre de mètres.
     * @return la conversion en pieds en nombre entier.
     */
    public static int metersToFeet(double meters)
    {
        return (int) (meters * 3.28084);
    }
    
    /**
     * Converti un nombre de mètres en un restant de pouces après avoir converti en pieds.
     * @param meters : le nombre de mètres.
     * @return la conversion en pouces restant après la convertion en pieds.
     */
    public static double metersToRemainingInches(double meters)
    {
        int feet = metersToFeet(meters);
        double inches = meters * 39.3701;
        return inches - feet * 12;
    }
}