package VirtuTuile.Domain.Drawing;

import VirtuTuile.Domain.Surface;
import VirtuTuile.Domain.CombinedSurface;
import VirtuTuile.Domain.Controller;
import VirtuTuile.Domain.Covering;
import VirtuTuile.Domain.RectangularSurface;
import VirtuTuile.GUI.CanvasPanel;
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
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

/**
 * Objet qui permet de dessiner sur le canevas.
 */
public class CanvasDrawer
{
    private final CanvasPanel parent;
    private Controller controller;
    private TexturePaint holeTexture;
    private final BasicStroke SMALL_STROKE = new BasicStroke(1);
    private final BasicStroke BIG_STROKE = new BasicStroke(3);
    private final BasicStroke HUGE_STROKE = new BasicStroke(5);
    private final Color TEMP_RECT_COLOR = new Color(113, 148, 191);
    
    /**
     * Constructeur.
     * @param parent : le canevas qui contient l'afficheur.
     */
    public CanvasDrawer(CanvasPanel parent)
    {
        this.parent = parent;
        holeTextureSetup();
    }
    
    /**
     * Mise en place de la texture pour les trous.
     */
    private void holeTextureSetup()
    {
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
    public void setController(Controller controller)
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
            drawTempPoly(g2d, transform);
            drawDebug(g2d, selectedSurface, transform);
        }
    }
    
    /**
     * Réglage de la transformation en fonction du zoom et des offsets.
     */
    private AffineTransform getTransform()
    {
        AffineTransform transform = new AffineTransform();
        transform.translate(-parent.getHorizontalOffset(), -parent.getVerticalOffset());
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
        if (parent.getGridDistanceZoomed() > 5)
        {
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
<<<<<<< Updated upstream
            copy.transform(transform);
            
            // Dessine l'interieur de la surface.
            if (surface.isHole() || !hasTiles)
            {
                g2d.setColor(surface.getColor());
            }
            else if (parent.getGridDistanceZoomed() <= 5)
            {
                g2d.setColor(surface.getCovering().getTileType().getColor());
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
=======
            if (surface instanceof CombinedSurface)
            {
                copy.subtract(((CombinedSurface) surface).getUncoveredArea());
            }
            copy.transform(transform);
            
            // Dessine la surface.
            setColor(g2d, surface, hasTiles);
            g2d.fill(copy);
>>>>>>> Stashed changes
            
            // Dessine le uncoveredArea d'une surface combinée.
            if (surface instanceof CombinedSurface)
            {
                Area uncoveredArea = new Area(((CombinedSurface) surface).getUncoveredArea());
                uncoveredArea.transform(transform);
<<<<<<< Updated upstream
                g2d.setColor(surface.getColor());
                g2d.fill(uncoveredArea);
                g2d.setPaint(holeTexture);
=======
                Color surfCol = surface.getColor();
                g2d.setColor(new Color(surfCol.getRed(), surfCol.getGreen(), surfCol.getBlue(), 160));
>>>>>>> Stashed changes
                g2d.fill(uncoveredArea);
                g2d.setColor(Color.BLACK);
                g2d.draw(uncoveredArea);
            }
            
            // Dessine les tuiles.
            if (hasTiles && parent.getGridDistanceZoomed() > 5)
            {
                drawTiles(g2d, surface, transform);
            }
            
            // Dessine le contour de la surface.
            g2d.setColor(Color.BLACK);
            g2d.draw(copy);
        }
    }
    
    private void setColor(Graphics2D g2d, Surface surface, boolean hasTiles)
    {
        // Dessine l'interieur de la surface.
        if (surface.isHole())
        {
            Color surfCol = surface.getColor();
            g2d.setColor(new Color(surfCol.getRed(), surfCol.getGreen(), surfCol.getBlue(), 160));
        }
        else if (!hasTiles)
        {
            g2d.setColor(surface.getColor());
        }
        else if (parent.getGridDistanceZoomed() <= 5)
        {
            g2d.setColor(surface.getCovering().getTileType().getColor());
        }
        else
        {
            g2d.setColor(surface.getCovering().getJointColor());
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
            Area surfaceCopy = new Area(selectedSurface);
            surfaceCopy.transform(transform);
            g2d.setColor(Color.BLACK);
            g2d.setStroke(HUGE_STROKE);
            g2d.draw(surfaceCopy);
            g2d.setStroke(SMALL_STROKE);
            
            PathIterator iterator = surfaceCopy.getPathIterator(null);
            g2d.setColor(Color.MAGENTA);
            double[] vertex = new double[2];
            while (!iterator.isDone())
            {
                int segmentType = iterator.currentSegment(vertex);
                if (segmentType != PathIterator.SEG_CLOSE)
                {
                    g2d.fillOval((int) vertex[0] - 5, (int) vertex[1] - 5, 10, 10);
                }
                iterator.next();
            }
            if (selectedSurface instanceof CombinedSurface)
            {
                Area uncoveredAreaCopy = new Area(((CombinedSurface) selectedSurface).getUncoveredArea());
                uncoveredAreaCopy.transform(transform);
                iterator = uncoveredAreaCopy.getPathIterator(null);
                while (!iterator.isDone())
                {
                    int segmentType = iterator.currentSegment(vertex);
                    if (segmentType != PathIterator.SEG_CLOSE)
                    {
                        g2d.fillOval((int) vertex[0] - 5, (int) vertex[1] - 5, 10, 10);
                    }
                    iterator.next();
                }
            }
        }
    }
    
    private void drawTiles(Graphics2D g2d, Surface surface, AffineTransform transform)
    {
        int minSize = parent.getInspectorLength();
        Covering covering = surface.getCovering();
        ArrayList<Area> tiles = covering.getTiles();
        Color tileColor = covering.getTileType().getColor();
        Color jointColor = covering.getJointColor();

        for (Area tile : tiles)
        {
            Area tileCopy = new Area(tile);
            tileCopy.transform(transform);
            g2d.setColor(tileColor);
            g2d.fill(tileCopy);
            Rectangle2D bounds = tile.getBounds2D();
            if (parent.isInspector() && (bounds.getWidth() < minSize || bounds.getHeight() < minSize))
            {
                g2d.setColor(Color.RED);
                g2d.setStroke(BIG_STROKE);
                g2d.draw(tileCopy);
                g2d.setStroke(SMALL_STROKE);
            }
            else
            {
                g2d.setColor(jointColor);
                g2d.draw(tileCopy);
            }
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
            g2d.setColor(TEMP_RECT_COLOR);
            g2d.fill(copy);
            g2d.setColor(Color.BLACK);
            g2d.draw(copy);
        }
    }
    
    private void drawTempPoly(Graphics2D g2d, AffineTransform transform)
    {
        Line2D.Double tempLine = parent.getTemporaryLine();
        if (tempLine != null)
        {
            double zoom = parent.getZoom();
            int ratio = Utilities.MM_PER_PIXEL;
            double x1 = (tempLine.x1 / ratio * zoom - parent.getHorizontalOffset());
            double y1 = (tempLine.y1 / ratio * zoom - parent.getVerticalOffset());
            double x2 = (tempLine.x2 / ratio * zoom - parent.getHorizontalOffset());
            double y2 = (tempLine.y2 / ratio * zoom - parent.getVerticalOffset());
            g2d.setColor(Color.BLACK);
            g2d.setStroke(BIG_STROKE);
            g2d.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
            g2d.setStroke(SMALL_STROKE);
        }
        else
        {
            Path2D.Double tempPolygon = parent.getTemporaryPolygon();
            if (tempPolygon != null)
            {
                Area copy = new Area(tempPolygon);
                copy.transform(transform);
                g2d.setColor(Color.BLACK);
                g2d.setStroke(BIG_STROKE);
                g2d.draw(copy);
                g2d.setStroke(SMALL_STROKE);
            }
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
            g2d.setStroke(BIG_STROKE);
            g2d.draw(devArea);
            g2d.setStroke(SMALL_STROKE);
        }
    }
}