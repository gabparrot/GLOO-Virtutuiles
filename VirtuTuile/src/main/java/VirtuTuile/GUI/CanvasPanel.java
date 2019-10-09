package VirtuTuile.GUI;

import java.awt.*;

/**
 * Le panneau dans lequel les surfaces sont dessinées.
 * @author Petros Fytilis
 */
public class CanvasPanel extends javax.swing.JPanel
{   
    // Tableau avec les incréments de zoom.
    private static final double[] ZOOM_LEVELS = {.1, .3, .5, .65, .8, .9, 1,
                                                 1.1, 1.2, 1.35, 1.5, 1.7, 2, 2.4, 3, 4, 5, 7.5, 10};
    
    // Niveau de zoom actuel.
    private double zoom = 1;
    
    // Offsets du canevas. Lorsque les offsets grandissent, la caméra se déplace en-bas et à-droite.
    private int verticalOffset = 0;
    private int horizontalOffset = 0;
    
    // Objet qui déssine sur le panneau.
    private final VirtuTuile.Drawing.CanvasDrawer drawer = new VirtuTuile.Drawing.CanvasDrawer(this);
    
    /**
     * Constructeur.
     */
    public CanvasPanel()
    {
    }

    /**
     * Se rend au prochain incrément de zoom.
     * @return le nouveau facteur de zoom.
     */
    public double zoomInIncrement()
    {
        if (zoom < 10)
        {
            double newZoom = .3;
            for (int i = ZOOM_LEVELS.length - 1; i > 0; i--)
            {
                if (ZOOM_LEVELS[i] <= zoom)
                {
                    newZoom = ZOOM_LEVELS[i + 1];
                    break;
                }
            }
            zoom = newZoom;
        }
        return zoom;
    }
    
    /**
     * Se rend au dernier incrément de zoom.
     * @return le nouveau facteur de zoom. 
     */
    public double zoomOutIncrement()
    {
        if (zoom > .1)
        {
            double newZoom = 7.5;
            for (int i = 0; i < ZOOM_LEVELS.length; i++)
            {
                if (ZOOM_LEVELS[i] >= zoom)
                {
                    newZoom = ZOOM_LEVELS[i - 1];
                    break;
                }
            }
            zoom = newZoom;
        }
        return zoom;
    }
    
    /**
     * Déplacement (positive ou négative) du zoom en sauts de 5%.
     * @param increment : le nombre de clics de rotation de la roue de souris.
     * @return le nouveau facteur de zoom.
     */
    public double changeZoom(int increment)
    {
        double newZoom = (Math.round(zoom * 100) - increment * 5) / 100.;
        if (newZoom >= 0.1)
        {
            zoom = newZoom;
        }
        return zoom;
    }
    
    /**
     * Retourne le facteur de zoom.
     * @return le facteur de zoom.
     */
    public double getZoom()
    {
        return zoom;
    }
    
    /**
     * Retourne le offset vertical du canevas.
     * @return le offset vertical du canevas.
     */
    public int getVerticalOffset()
    {
        return verticalOffset;
    }           
    
    /**
     * Affecte le offset vertical du canevas.
     * @param offset : le nouveau offset vertical.
     */
    public void setVerticalOffset(int offset)
    {
        verticalOffset = offset;
    }
    
    /**
     * Retourne le offset horizontal du canevas.
     * @return le offset horizontal du canevas.
     */
    public int getHorizontalOffset()
    {
        return horizontalOffset;
    }           
    
    /**
     * Affecte le offset horizontal du canevas.
     * @param offset : le nouveau offset horizontal.
     */
    public void setHorizontalOffset(int offset)
    {
        horizontalOffset = offset;
    }
    
    /**
     * Déssine les contenus du canevas.
     * @param g : Graphics du canevas.
     */
    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);   
        drawer.draw(g);
        
        // Graphics2D g2d = (Graphics2D) g.create();
        //g2d.dispose();
    }
}