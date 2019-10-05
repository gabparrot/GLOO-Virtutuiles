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
    public static float pixelsToMeters(int pixels, float zoom)
    {
        return pixels / (PIXELS_PAR_METRE_BASE * zoom);
    }
    
    /**
     * Converti un nombre de mètres en pieds.
     * @param meters : le nombre de mètres.
     * @return la conversion en pieds.
     */
    public static float metersToFeet(float meters)
    {
        return (float) (meters * 3.28084);
    }
}