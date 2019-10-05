package VirtuTuile.Drawing;

import java.awt.*;

/**
 * Objet qui permet de déssiner sur le canevas.
 * @author Petros Fytilis
 */
public class CanvasDrawer
{
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
     * @param parent : le canevas qui contient l'afficheur.
     */
    public CanvasDrawer(VirtuTuile.GUI.CanvasPanel parent)
    {
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
        
        int verticalOffset = parent.getVerticalOffset() % (int) gridDistance;
        int horizontalOffset = parent.getHorizontalOffset() % (int) gridDistance;

        int nbColumns = (int) (parent.getWidth() / gridDistance + 1);
        int nbRows = (int) (parent.getHeight() / gridDistance + 1);

        for (int column = 0; column < nbColumns; column++)
        {
            g.drawLine( (int) (column * gridDistance) - horizontalOffset, 0, (int) (column * gridDistance) - horizontalOffset, parent.getHeight());
        }
        
        for (int row = 0; row < nbRows; row++)
        {
            g.drawLine( 0, (int) (row * gridDistance) - verticalOffset, parent.getWidth(), (int) (row * gridDistance) - verticalOffset);
        }
    }
    
    /**
     * Déssine les surfaces sur le canevas.
     * @param g : Graphics du canevas.
     */
    private void drawSurfaces(Graphics g)
    {
        float zoom = parent.getZoom();
        int verticalOffset = parent.getVerticalOffset();
        int horizontalOffset = parent.getHorizontalOffset();
        
        // !!!TEST!!!
        g.setColor(Color.red);
        g.fillRect((int) (testRectangle.x * zoom) - horizontalOffset, (int) (testRectangle.y * zoom) - verticalOffset,
                   (int) (testRectangle.width * zoom), (int) (testRectangle.height * zoom));
        g.setColor(Color.black);
        /// !!!FIN TEST!!!
    }
}