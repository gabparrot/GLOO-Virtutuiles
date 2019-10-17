package VirtuTuile.Domain.Drawing;

import VirtuTuile.Domain.Surface;
import java.awt.*;
import java.util.ArrayList;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;

/**
 * Objet qui permet de dessiner sur le canevas.
 * @author Petros Fytilis
 */
public class CanvasDrawer
{
    // Référence au parent.
    private final VirtuTuile.GUI.CanvasPanel parent;
    
    // Référence au controller.
    private VirtuTuile.Domain.Controller controller;
    
    /**
     * Constructeur.
     * @param parent : le canevas qui contient l'afficheur.
     */
    public CanvasDrawer(VirtuTuile.GUI.CanvasPanel parent)
    {
        this.parent = parent;
    }
    
    /**
     * Setter pour le controller.
     * @param controller : le controller de l'application.
     */
    public void setController(VirtuTuile.Domain.Controller controller)
    {
        this.controller = controller;
    }
    
    /**
     * Déssine tous les éléments du canevas.
     * @param g2d : Graphics du canevas.
     */
    public void draw(Graphics2D g2d)
    {
        drawGrid(g2d);
        if (controller != null) drawSurfaces(g2d);
    }
    
    /**
     * Dessine la grille du canevas.
     * @param g2d : Graphics du canevas.
     */
    private void drawGrid(Graphics2D g2d)
    {
        g2d.setColor(Color.DARK_GRAY);
        double gridDistance = parent.getGridDistanceZoomed();
        
        double verticalOffset = parent.getVerticalOffset() % gridDistance;
        double horizontalOffset = parent.getHorizontalOffset() % gridDistance;

        int nbColumns = (int) (parent.getWidth() / gridDistance + 2);
        int nbRows = (int) (parent.getHeight() / gridDistance + 2);

        for (int column = 0; column < nbColumns; column++)
        {
            g2d.drawLine((int) (column * gridDistance - horizontalOffset), 0,
                         (int) (column * gridDistance - horizontalOffset), parent.getHeight());
        }
        
        for (int row = 0; row < nbRows; row++)
        {
            g2d.drawLine(0, (int) (row * gridDistance - verticalOffset),
                         parent.getWidth(), (int) (row * gridDistance - verticalOffset));
        }
    }
    
    /**
     * Déssine les surfaces sur le canevas.
     * @param g : Graphics du canevas.
     */
    private void drawSurfaces(Graphics2D g2d)
    {
        double zoom = parent.getZoom();
        int verticalOffset = parent.getVerticalOffset();
        int horizontalOffset = parent.getHorizontalOffset();
        Surface selectedSurface = controller.getSelectedSurface();
        ArrayList<Surface> surfaces = controller.getSurfaces();
        
        // Réglage des transformations en fonction du zoom et des offsets.
        AffineTransform scaleTransform = new AffineTransform();
        scaleTransform.scale(0.1, 0.1);
        scaleTransform.scale(zoom, zoom);
        AffineTransform translationTransform = new AffineTransform();
        translationTransform.translate(-horizontalOffset, -verticalOffset);

        // Dessine chaque surface du projet.
        for (Surface surface : surfaces)
        {   
            Area copy = new Area(surface);
            copy.transform(scaleTransform);
            copy.transform(translationTransform);
            
            // Dessine l'interieur de la surface.
            g2d.setColor(surface.getColor());
            g2d.fill(copy);
            
            // Dessine le contour de la surface.
            g2d.setColor(Color.BLACK);
            if (surface == selectedSurface) g2d.setStroke(new BasicStroke(5));
            else g2d.setColor(Color.BLACK);
            g2d.draw(copy);
            g2d.setStroke(new BasicStroke(1));
        }
        
        // Dessine un rectangle temporaire lors de la création de surface rectangulaire.
        Rectangle temp = parent.getTemporaryRectangle();
        if (temp != null)
        {
            Area copy = new Area(temp);
            copy.transform(scaleTransform);
            copy.transform(translationTransform);
            
            // Dessine le rectangle temporaire.
            g2d.setColor(new Color(113, 148, 191));
            g2d.fill(copy);
            g2d.setColor(Color.BLACK);
            g2d.draw(copy);
        }
        
        /**
         * Dessine un rectangle montrant les bornes extérieures de la surface sélectionnée
         * si le mode debug est activé.
         */
        if (parent.isDebug() && selectedSurface != null)
        {
            int surroundingBounds[] = controller.getSurroundingBounds(selectedSurface);
            Rectangle devRec = new Rectangle(surroundingBounds[0], surroundingBounds[1], surroundingBounds[2] - surroundingBounds[0], surroundingBounds[3] - surroundingBounds[1]);
            Area devArea = new Area(devRec);
            devArea.transform(scaleTransform);
            devArea.transform(translationTransform);
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(3));
            g2d.draw(devArea);
            g2d.setStroke(new BasicStroke(1));
        }
    }
}