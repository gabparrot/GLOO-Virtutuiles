package VirtuTuile.GUI;

import java.awt.*;

/**
 * Le panneau dans lequel les surfaces sont déssinées.
 * @author Petros Fytilis
 */
public class CanvasPanel extends javax.swing.JPanel
{   
    // Tableau avec les incréments de zoom.
    private static final float[] ZOOM_LEVELS = {.3f, .5f, .67f, .8f, .9f, 1f, 1.1f, 1.2f, 1.33f, 1.5f, 1.7f, 2f, 2.4f, 3f};
    
    // Niveau de zoom actuel.
    private float zoom = 1f;
    
    // Référence au parent.
    private VirtuTuile.GUI.MainWindow mainWindow;
    
    // Objet qui déssine sur le panneau.
    private final VirtuTuile.Drawing.CanvasDrawer drawer;
    
    /**
     * Constructeur.
     * @param mainWindow : la fenêtre parente.
     */
    public CanvasPanel(VirtuTuile.GUI.MainWindow mainWindow)
    {
        this.mainWindow = mainWindow;
        this.drawer = new VirtuTuile.Drawing.CanvasDrawer(mainWindow.controller, this);
    }

    /**
     * Se rend au prochain incrément de zoom.
     * @return le nouveau facteur de zoom.
     */
    public float zoomInIncrement()
    {
        if (zoom < 3f)
        {
            float newZoom = .5f;
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
    public float zoomOutIncrement()
    {
        if (zoom > .3f)
        {
            float newZoom = 2.4f;
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
     * Déplacement (positive ou négative) du zoom.
     * @param increment : le nombre de clics de rotation de la souris.
     * @return le nouveau facteur de zoom.
     */
    public float changeZoom(int increment)
    {
        float newZoom = zoom + (float) increment / 50;
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
    public float getZoom()
    {
        return zoom;
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