package VirtuTuile.Domain.Drawing;

import VirtuTuile.Domain.Surface;
import VirtuTuile.Domain.CombinedSurface;
import VirtuTuile.Domain.RectangularSurface;
import VirtuTuile.Infrastructure.Utilities;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.util.ArrayList;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

/**
 * Objet qui permet de dessiner sur le canevas.
 */
public class CanvasDrawer
{
    private final VirtuTuile.GUI.CanvasPanel parent;
    private VirtuTuile.Domain.Controller controller;
    private final TexturePaint holeTexture;
    
    /**
     * Constructeur.
     * @param parent : le canevas qui contient l'afficheur.
     */
    public CanvasDrawer(VirtuTuile.GUI.CanvasPanel parent)
    {
        this.parent = parent;
        
        // Mise en place de la texture pour les trous:
        BufferedImage bi = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        ImageIcon icon = new ImageIcon(getClass().getResource("/dots.png"));
        Graphics g = bi.createGraphics();
        icon.paintIcon(null, g, 0, 0);
        g.dispose();
        holeTexture = new TexturePaint(bi, new Rectangle(0, 0, 100, 100));
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
     * Dessine tous les éléments du canevas.
     * @param g2d : Graphics du canevas.
     */
    public void draw(Graphics2D g2d)
    {
        if (controller != null && controller.projectExists())
        {
            ArrayList<Surface> surfaces = controller.getSurfaces();
            Surface selectedSurface = controller.getSelectedSurface();
            AffineTransform transform = getTransform();
        
            drawGrid(g2d);
            drawSurfaces(g2d, surfaces, transform);
            drawSelectedSurface(g2d, selectedSurface, transform);
            drawTempRect(g2d, transform);
            drawDebug(g2d, selectedSurface, transform);
        }
    }
    
    /**
     * Réglage de la transformation en fonction du zoom et des offsets.
     */
    private AffineTransform getTransform()
    {
        AffineTransform transform = new AffineTransform();
        transform.translate(-parent.getHorizontalOffset(), - parent.getVerticalOffset());
        transform.scale(1. / Utilities.MM_PER_PIXEL, 1. / Utilities.MM_PER_PIXEL);
        transform.scale(parent.getZoom(), parent.getZoom());
        return transform;
    }
    
    /**
     * Dessine la grille du canevas.
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
            g2d.drawLine((int) Math.round(column * gridDistance - horizontalOffset), 0,
                         (int) Math.round(column * gridDistance - horizontalOffset),
                         parent.getHeight());
        }
        
        for (int row = 0; row < nbRows; row++)
        {
            g2d.drawLine(0, (int) Math.round(row * gridDistance - verticalOffset),
                    parent.getWidth(), (int) Math.round(row * gridDistance - verticalOffset));
        }
    }
    
    /**
     * Dessine les surfaces sur le canevas.
     */
    private void drawSurfaces(Graphics2D g2d, ArrayList<Surface> surfaces,
                              AffineTransform transform)
    {   
        // Dessine chaque surface du projet.
        for (Surface surface : surfaces)
        {   
            boolean hasTiles = !surface.getCovering().getTiles().isEmpty();
            Area copy = new Area(surface);
            copy.transform(transform);
            
            // Dessine l'interieur de la surface.
            if (surface.isHole() || !hasTiles)
            {
                g2d.setColor(surface.getColor());
            }
            else
            {
                g2d.setColor(surface.getCovering().getJointColor());
            }
            g2d.fill(copy);
            
            // Dessine la texture d'une surface non-couverte.
            if (surface.isHole())
            {
                g2d.setPaint(holeTexture);
                g2d.fill(copy);
            }
            
            // Dessine le uncoveredArea d'une surface combinée.
            else if (surface instanceof CombinedSurface)
            {
                Area uncoveredArea = new Area(((CombinedSurface) surface).getUncoveredArea());
                uncoveredArea.transform(transform);
                g2d.setColor(surface.getColor());
                g2d.fill(uncoveredArea);
                g2d.setPaint(holeTexture);
                g2d.fill(uncoveredArea);
            }
            
            // Dessine les tuiles.
            if (hasTiles)
            {
                drawTiles(g2d, surface, transform);
            }
            
            // Dessine le contour de la surface.
            g2d.setColor(Color.BLACK);
            g2d.draw(copy);
        }
    }
    
    /**
     * Dessine le contour de la surface sélectionnée.
     */
    private void drawSelectedSurface(Graphics2D g2d, Surface selectedSurface,
                                     AffineTransform transform)
    {
        if (selectedSurface != null)
        {
            Area copy = new Area(selectedSurface);
            copy.transform(transform);
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(5));
            g2d.draw(copy);
            g2d.setStroke(new BasicStroke(1));
        }
    }
    
    private void drawTiles(Graphics2D g2d, Surface surface, AffineTransform transform)
    {
        ArrayList<Area> tiles = surface.getCovering().getTiles();
        Color tileColor = surface.getCovering().getTileType().getColor();
        Color jointColor = surface.getCovering().getJointColor();

        for (Area tile : tiles)
        {
            Area tileCopy = new Area(tile);
            tileCopy.transform(transform);
            g2d.setColor(tileColor);
            g2d.fill(tileCopy);
            g2d.setColor(jointColor);
            g2d.draw(tileCopy);
        }
    }
    
    /**
     * Dessine un rectangle temporaire lors de la création de surface rectangulaire.
     */
    private void drawTempRect(Graphics2D g2d, AffineTransform transform)
    {
        Rectangle2D.Double temp = parent.getTemporaryRectangle();
        if (temp != null)
        {
            Area copy = new Area(temp);
            copy.transform(transform);
            g2d.setColor(new Color(113, 148, 191));
            g2d.fill(copy);
            g2d.setColor(Color.BLACK);
            g2d.draw(copy);
        }
    }
    
    /**
    * Dessine un rectangle montrant les bornes extérieures de la surface sélectionnée
    * si le mode debug est actif.
    */
    private void drawDebug(Graphics2D g2d, Surface selectedSurface, AffineTransform transform)
    {
        if (parent.isDebug() && selectedSurface != null && selectedSurface instanceof RectangularSurface)
        {
            double surroundingBounds[] = controller.getSurroundingBounds();
            Rectangle2D.Double devRec = new Rectangle2D.Double(surroundingBounds[0],
                    surroundingBounds[1], surroundingBounds[2] - surroundingBounds[0],
                    surroundingBounds[3] - surroundingBounds[1]);
            Area devArea = new Area(devRec);
            devArea.transform(transform);
            g2d.setColor(Color.MAGENTA);
            g2d.setStroke(new BasicStroke(3));
            g2d.draw(devArea);
            g2d.setStroke(new BasicStroke(1));
        }
    }
}