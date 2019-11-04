package VirtuTuile.GUI;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import VirtuTuile.Domain.Drawing.CanvasDrawer;

/**
 * Le panneau dans lequel les surfaces sont dessinées.
 * @author Petros Fytilis
 */
public class CanvasPanel extends javax.swing.JPanel
{   
    // Tableau avec les incréments de zoom.
    private static final double[] ZOOM_LEVELS = {.1, .3, .5, .65, .8, .9, 1, 1.1, 1.2, 1.35, 1.5,
                                                 1.7, 2, 2.4, 3, 4, 5, 7.5, 10};
    
    // Niveau de zoom actuel.
    private double zoom = 1;
    
    // Offsets du canevas. Lorsque les offsets grandissent, la caméra se déplace en-bas et à-droite.
    private int verticalOffset = 0;
    private int horizontalOffset = 0;
    
    // Largeur (en pixels) entre chaque ligne de la grille lorsque le zoom est à 100%.
    private double gridDistance = 20;
    
    // Objet qui déssine sur le panneau.
    private final CanvasDrawer drawer = new CanvasDrawer(this);
    
    // Rectangle temporaire qui sert de user feedback lors de la création de sruface.
    Rectangle2D.Double temporaryRectangle = null;
    
    // Si la fenêtre est en mode debug.
    private boolean isDebug = false;
    
    /**
     * Constructeur.
     */
    public CanvasPanel()
    {
    }

    /**
     * Retourne si la fenêtre est en mode debug ou pas.
     * @return true is la fenêtere est en mode debug, false sinon.
     */
    public boolean isDebug()
    {
        return isDebug;
    }
    
    /**
     * Alterne le mode debug entre activé et non-activé.
     */
    public void toggleIsDebug()
    {
        isDebug = !isDebug;
    }
    
    /**
     * Setter pour la distance de la grille, en pixels, lorsque le zoom est à 100%.
     * @param newGridDistance : la nouvelle distance en pixels.
     */
    public void setGridDistance(double newGridDistance)
    {
        gridDistance = newGridDistance;
    }
    
    /**
     * Retourne la distance courante de la grille, en pixels, en tenant compte du zoom actuel.
     * @return la distance courante, en pixels.
     */
    public double getGridDistanceZoomed()
    {
        return gridDistance * zoom;
    }
    
    /**
     * Retourne la distance de la grille, en pixels, sans tenir compte du zoom.
     * @return la distance de la grille, en pixels, sans tenir compte du zoom.
     */
    public double getGridDistance()
    {
        return gridDistance;
    }
    
    /**
     * Setter pour le rectangle temporaire qui sert de user feedback lors de la création de surface.
     * @param rectangle : le rectangle
     */
    public void setTemporaryRectangle(Rectangle2D.Double rectangle)
    {
        temporaryRectangle = rectangle;
    }
    
    /**
     * Getter pour le rectangle temporaire qui sert de user feedback lors de la création de surface.
     * @return 
     */
    public Rectangle2D.Double getTemporaryRectangle()
    {
        return temporaryRectangle;
    }
    
    /**
     * Assigne un Controller au Drawer.
     * @param controller : le controller de l'application.
     */
    public void assignControllerToDrawer(VirtuTuile.Domain.Controller controller)
    {
        drawer.setController(controller);
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
     * @param x : la position x de la souris.
     * @param y : la position y de la souris.
     * @param maxHorizontal : la valeur maximale du horizontalScrollbar.
     * @param maxVertical : la valeur maximale du verticalScrollbar.
     * @return le nouveau facteur de zoom.
     */
    public double changeZoom(int increment, int x, int y, int maxHorizontal, int maxVertical)
    {
        int lx = x + horizontalOffset;
        int ly = y + verticalOffset;
        double oldZoom = zoom;
        
        double newZoom = (Math.round(zoom * 100) - increment * 5) / 100.;
        if (newZoom >= 0.1)
        {
            zoom = newZoom;
        }
        
        double zoomFactor = zoom / oldZoom;
        int lx2 = (int) (lx * zoomFactor);
        int ly2 = (int) (ly * zoomFactor);
        horizontalOffset = Math.min(maxHorizontal, Math.max(0, lx2 - x));
        verticalOffset = Math.min(maxVertical, Math.max(0, ly2 - y));
        
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
        
        try
        {
            ((MainWindow) javax.swing.SwingUtilities.getWindowAncestor(this)).updateScrollbars();
        }
        catch (Exception e) {}
            
        Graphics2D g2d = (Graphics2D) g.create();
        
        drawer.draw(g2d);
        
        g2d.dispose();
    }
}
