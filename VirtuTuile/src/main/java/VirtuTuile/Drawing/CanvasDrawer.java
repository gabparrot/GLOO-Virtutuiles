package VirtuTuile.Drawing;

import java.awt.*;

/**
 * Objet qui permet de déssiner sur le canevas.
 * @author Petros Fytilis
 */
public class CanvasDrawer
{
    // Référence au controller.
    private final VirtuTuile.Domain.Controller controller;
    
    // Référence au parent.
    private final VirtuTuile.GUI.CanvasPanel parent;
    
    // Largeur (en pixels) entre chaque ligne de la grille lorsque le zoom est à 100%.
    private final int gridDistanceDefault = 20;
    
    // Largeur (en pixels) courante entre chaque ligne de la grille.
    private double gridDistance = gridDistanceDefault;
    
    // !!!TEST!!!
    private final Rectangle testRectangle = new Rectangle(50, 50, 100, 200);
    
    /**
     * Constructeur.
     * @param controller : le controller du MainWindow.
     * @param parent : le canevas qui contient l'afficheur.
     */
    public CanvasDrawer(VirtuTuile.Domain.Controller controller, VirtuTuile.GUI.CanvasPanel parent)
    {
        this.controller = controller;
        this.parent = parent;
    }
    
    /**
     * Déssine tous les éléments du canevas.
     * @param g : Graphics du canevas.
     */
    public void draw(Graphics g)
    {
        drawGrid(g);
        drawSurfaces(g);
    }
    
    /**
     * Déssine la grille du canevas.
     * @param g : Graphics du canevas.
     */
    private void drawGrid(Graphics g)
    {
        gridDistance = gridDistanceDefault * parent.getZoom();
        int nbColumns = (int) (parent.getWidth() / gridDistance + 1);
        int nbRows = (int) (parent.getHeight() / gridDistance + 1);

        for (int column = 0; column < nbColumns; column++)
        {
            g.drawLine( (int) (column * gridDistance), 0, (int) (column * gridDistance), parent.getHeight());
        }
        
        for (int row = 0; row < nbRows; row++)
        {
            g.drawLine( 0, (int) (row * gridDistance), parent.getWidth(), (int) (row * gridDistance));
        }
    }
    
    /**
     * Déssine les surfaces sur le canevas.
     * @param g : Graphics du canevas.
     */
    private void drawSurfaces(Graphics g)
    {
        float zoom = parent.getZoom();
        
        // !!!TEST!!!
        g.setColor(Color.red);
        g.fillRect((int) (testRectangle.x * zoom), (int) (testRectangle.y * zoom),
                   (int) (testRectangle.width * zoom), (int) (testRectangle.height * zoom));
        g.setColor(Color.black);
        /// !!!FIN TEST!!!
    }
}