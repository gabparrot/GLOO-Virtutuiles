package VirtuTuile.Domain;

import java.awt.Color;
import java.awt.Shape;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Area;
import java.awt.geom.AffineTransform;
import VirtuTuile.Infrastructure.Utilities;


/**
 * @class definissant le projet en cours
 * @author gabparrot
 */
public class Project
{
    private String projectName;
    private Shape selectedTile = null;
    private Map<TileType, Integer> qtyPerTileType = new HashMap<>();
    private final ArrayList<Surface> surfaces = new ArrayList<>();
    private Surface selectedSurface = null;
    private final ArrayList<Surface> surfacesToMerge = new ArrayList<>();
    
    /**
     * Constructeur.
     * @param projectName : le nom du projet.
     */
    public Project(String projectName)
    {
        this.projectName = projectName;
        /*java.awt.Polygon poly = new java.awt.Polygon();                             // Test
        poly.addPoint(2000, 4000);                                                  // Test
        poly.addPoint(4000, 4000);                                                  // Test
        poly.addPoint(4000, 2000);                                                  // Test
        CombinedSurface surface = new CombinedSurface(false, Color.GREEN, poly);    // Test
        surfaces.add(surface);
        */
    }
    
    /**
     * Désélectionne la surface sélectionnée.
     */
    public void unselect()
    {
        selectedSurface = null;
        
    }
    
    public void purgeSurfacesToMerge()
    {
        surfacesToMerge.clear();
    }
        
    /**
     * Getter pour la liste des surfaces.
     * @return la liste des surfaces du projet.
     */
    public ArrayList<Surface> getSurfaces()
    {
        return surfaces;
    }
    
    /**
     * Crée une nouvelle surface rectangulaire.
     * @param rectangle la forme du rectangle.
     * @return true si la création à réussie, false sinon.
     */
    public boolean addRectangularSurface(Rectangle2D.Double rectangle)
    {
        boolean noConflict = conflictCheck(rectangle);
        if (noConflict)
        {
            surfaces.add(new RectangularSurface(rectangle, false, new Color(113, 148, 191)));
        }
        return noConflict;
    }
    
    /**
     * Vérifie s'il y a un conflit avec une nouvelle surface.
     * @param shape : la forme de la surface à vérifier.
     * @return true s'il n'y a pas de conflit, false s'il y a conflit.
     */
    public boolean conflictCheck(Shape shape)
    {
        return boundsConflictCheck(shape) || areaConflictCheck(shape);
    }
    
    /**
     * Vérifie s'il y a intersection avec les bornes d'une nouvelle surface.
     * @param shape : la forme de la surface à vérifier.
     * @return true s'il n'y a pas de conflit, false s'il y a conflit.
     */
    public boolean boundsConflictCheck(Shape shape)
    {
        boolean status = true;
        
        for (Surface surface : surfaces)
        {
            Rectangle2D intersection = shape.getBounds2D().createIntersection(surface.getBounds2D());
            
            if (intersection.getWidth() > 0.1 && intersection.getHeight() > 0.1 && surface != shape)
            {
                status = false;
            }
        }
        
        return status;
    }
    
    /**
     * Vérifie s'il y a intersection avec l'aire d'une nouvelle surface.
     * @param shape : la forme de la surface à vérifier.
     * @return true s'il n'y a pas de conflit, false s'il y a conflit.
     */
    public boolean areaConflictCheck(Shape shape)
    {
        boolean status = true;
        Area newArea = new Area(shape);
        
        for (Surface surface : surfaces)
        {
            Area area = new Area(surface);
            area.intersect(newArea);
            
            if (!area.isEmpty() && surface != shape)
            {
                status = false;
            }
        }
        
        return status;
    }
    
    /**
     * Fusionne les 2 surfaces marquees comme selectionnees par surfacesToMerge
     * @return boolSuccess Booleen representant la reussite ou l'echec de l'operation
     */
    public boolean mergeSelectedSurfaces()
    {
        boolean boolSuccess = false;
        
        if (surfacesToMerge.isEmpty())
        {
            surfacesToMerge.add(selectedSurface);
            boolSuccess = true;
        }
        else if (surfacesToMerge.size() == 1)
        {
            surfacesToMerge.add(selectedSurface);
            int bigSurface;
            int smallSurface;
            
            if (Utilities.getSurfaceArea(surfacesToMerge.get(0)) > Utilities.getSurfaceArea(surfacesToMerge.get(1)))
            {
                bigSurface = 0;
                smallSurface = 1;
            }
            else
            {
                bigSurface = 1;
                smallSurface = 0;
            }
            
            boolean mergedIsHole = surfacesToMerge.get(bigSurface).isHole();
            Color mergedColor = surfacesToMerge.get(bigSurface).getColor();
            surfaces.add(new CombinedSurface(surfacesToMerge, mergedIsHole, mergedColor));
            surfaces.remove(surfacesToMerge.get(0));
            surfaces.remove(surfacesToMerge.get(1));
            surfacesToMerge.removeAll(surfacesToMerge);
        }
        
        return boolSuccess;
    }
        
    /**
     * Déplace une surface à une nouvelle position.
     * @param newPos : nouvelle position.
     * @param surface : la surface qui doit être déplacée.
     */
    public void moveSurfaceToPoint(Point2D newPos, Surface surface)
    {
        if (surface instanceof RectangularSurface)
        {
            setRectangularSurfaceXY(newPos.getX(), newPos.getY(), (RectangularSurface) surface);
        }
        else if (surface instanceof IrregularSurface)
        {
            java.awt.geom.AffineTransform translationAT = new java.awt.geom.AffineTransform();
            translationAT.setToTranslation(newPos.getX(), newPos.getY());
            ((IrregularSurface) surface).moveTo(newPos.getX(), newPos.getY());
        }
        else if (surface instanceof CombinedSurface)
        {
           double deltaX = newPos.getX() - ((CombinedSurface) surface).getBounds2D().getX();
           double deltaY = newPos.getY() - ((CombinedSurface) surface).getBounds2D().getY();
           AffineTransform translationTransform = new AffineTransform();
           translationTransform.translate(deltaX, deltaY);
           ((CombinedSurface) surface).transform(translationTransform);
        }   
    }
    
    /**
     * Déplace une surface rectangulaire à une nouvelle position x y.
     * @param x la coordonnée horizontale
     * @param y la coordonnée verticale
     * @param surface la surface qui doit être déplacée
     */
    public void setRectangularSurfaceXY(double x, double y, RectangularSurface surface)
    {
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        
        double oldX = surface.x;
        double oldY = surface.y;
        
        double surroundingBounds[] = getSurroundingBounds(surface);
        surface.x = x;
        
        // Si il y a conflit, il faut glisser la surface dans deux directions:
        if (!conflictCheck(surface))
        {
            // Déplacement à droite
            if (x > oldX)
            {
                surface.x = Math.min(x, surroundingBounds[2] - surface.width);
            }
            // Déplacement à gauche
            else
            {
                surface.x = Math.max(x, surroundingBounds[0]);
            }
        }
        
        surroundingBounds = getSurroundingBounds(surface);
        surface.y = y;
        
        if (!conflictCheck(surface))
        {
            // Déplacement vers le bas
            if (y > oldY)
            {
                surface.y = Math.min(y, surroundingBounds[3] - surface.height);
            }
            // Déplacement vers le haut
            else
            {
                surface.y = Math.max(y, surroundingBounds[1]);
            }
        }
    }
    
    /**
     * Retourne les bornes extérieures d'une surface, qui décrivent jusqu'à quel point une surface
     * peut être déplacée dans les quatre directions.
     * @param inputSurface : la surface en question.
     * @return les bornes dans un tableau [gauche, en-haut, droite, en-bas]
     */
    public double[] getSurroundingBounds(Surface inputSurface)
    {
        Rectangle2D b = inputSurface.getBounds2D();
        double x = b.getX();
        double y = b.getY();
        double w = b.getWidth();
        double h = b.getHeight();
        double surroundingBounds[] = {0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE};
        Area totalArea = new Area();
        for (Surface surface : surfaces)
        {
            if (surface != inputSurface) totalArea.add(new Area(surface));
        }
        // LEFT
        Area leftArea = new Area(totalArea);
        leftArea.intersect(new Area(new Rectangle2D.Double(0, y, x, h)));
        Rectangle2D leftRect = leftArea.getBounds2D();
        if (leftRect.getX() + leftRect.getWidth() > 0)
        {
            surroundingBounds[0] = leftRect.getX() + leftRect.getWidth();
        }
        
        // UP
        Area upArea = new Area(totalArea);
        upArea.intersect(new Area(new Rectangle2D.Double(x, 0, w, y)));
        Rectangle2D upRect = upArea.getBounds2D();
        if (upRect.getY() + upRect.getHeight() > 0)
        {
            surroundingBounds[1] = upRect.getY() + upRect.getHeight();
        }
        
        // RIGHT
        Area rightArea = new Area(totalArea);
        rightArea.intersect(new Area(new Rectangle2D.Double(x + w, y, Integer.MAX_VALUE, h)));
        Rectangle2D rightRect = rightArea.getBounds2D();
        if (!rightArea.isEmpty())
        {
            surroundingBounds[2] = rightRect.getX();
        }
        
        // DOWN
        Area downArea = new Area(totalArea);
        downArea.intersect(new Area(new Rectangle2D.Double(x, y + h, w, Integer.MAX_VALUE)));
        Rectangle2D downRect = downArea.getBounds2D();
        if (!downArea.isEmpty())
        {
            surroundingBounds[3] = downRect.getY();
        }
        
        return surroundingBounds;
    }
    
    /**
     * Set le paramètre x d'une surface rectangulaire.
     * @param x : le nouveau paramètre x.
     * @param surface : la surface qui doit être modifiée.
     * @return : true si réussi, false sinon.
     */
    public boolean setRectangularSurfaceX(double x, RectangularSurface surface)
    {
        if (x < 0) return false;
        
        double oldX = surface.x;
        
        surface.x = x;
        
        if (conflictCheck(surface))
        {
            return true;
        }
        else
        {
            surface.x = oldX;
            return false;
        }
    }
    
    /**
     * Set le paramètre y d'une surface rectangulaire.
     * @param y : le nouveau paramètre y.
     * @param surface : la surface qui doit être modifiée.
     * @return : true si réussi, false sinon.
     */
    public boolean setRectangularSurfaceY(double y, RectangularSurface surface)
    {
        if (y < 0) return false;
        
        double oldY = surface.y;
        
        surface.y = y;
        
        if (conflictCheck(surface))
        {
            return true;
        }
        else
        {
            surface.y = oldY;
            return false;
        }
    }
    
    /**
     * Set le paramètre width d'une surface rectangulaire.
     * @param width : le nouveau paramètre width.
     * @param surface : la surface qui doit être modifiée.
     * @return : true si réussi, false sinon.
     */
    public boolean setRectangularSurfaceWidth(double width, RectangularSurface surface)
    {
        if (width < 100) return false;
        
        double oldWidth = surface.width;
        
        surface.width = width;
        
        if (conflictCheck(surface))
        {
            return true;
        }
        else
        {
            surface.width = oldWidth;
            return false;
        }
    }
    
    /**
     * Set le paramètre height d'une surface rectangulaire.
     * @param height : le nouveau paramètre height.
     * @param surface : la surface qui doit être modifiée.
     * @return : true si réussi, false sinon.
     */
    public boolean setRectangularSurfaceHeight(double height, RectangularSurface surface)
    {
        if (height < 100) return false;
        
        double oldHeight = surface.height;
        
        surface.height = height;
        
        if (conflictCheck(surface))
        {
            return true;
        }
        else
        {
            surface.height = oldHeight;
            return false;
        }
    }
    
    /**
     * Sélectionne une surface.
     * @param point : le point qui doit être à l'intérieur de la surface.
     * @return la surface sélectionnée, peut être null.
     */
    public Surface selectSurface(Point2D point)
    {
        for (Surface surface : surfaces)
        {
            if (surface.contains(point))
            {
                selectedSurface = surface;
                return surface;
            }
        }
        selectedSurface = null;
        return selectedSurface;
    }
    
    /**
     * Efface la surface selectionnée.
     */
    public void deleteSelectedSurface()
    {
        if (selectedSurface != null) surfaces.remove(selectedSurface);
    }
    
    /**
     * Retourne la surface sélectionnée, peut être null.
     * @return la surface sélectionnée, si une surface est sélectionnée, null sinon.
     */
    public Surface getSelectedSurface()
    {
        return selectedSurface;
    }
   
    public String getProjectName()
    {
        return projectName;
    }

    public void setProjectName(String projectName)
    {
        this.projectName = projectName;
    }

    public Shape getSelectedTile()
    {
        return selectedTile;
    }

    public void setSelectedTile(Shape selectedTile)
    {
        this.selectedTile = selectedTile;
    }

    public Map<TileType, Integer> getQtyPerTileType()
    {
        return qtyPerTileType;
    }

    public void setQtyPerTileType(Map<TileType, Integer> qtyPerTileType)
    {
        this.qtyPerTileType = qtyPerTileType;
    }
    
    /**
     * Trouve le point le plus à droite et en-bas de toutes les surfaces.
     * @return le point le plus à droite et en-bas de toutes les surfaces. 
     */
    public Point2D.Double getFarthestPoint()
    {
        Point2D.Double point = new Point2D.Double(0, 0);
        for (Surface surface : surfaces)
        {
            Rectangle2D bounds = surface.getBounds2D();
            if (bounds.getX() + bounds.getWidth() > point.x)
            {
                point.x = bounds.getX() + bounds.getWidth();
            }
            if (bounds.getY() + bounds.getHeight() > point.y)
            {
                point.y = bounds.getY() + bounds.getHeight();
            }
        }
        return point;
    }
    
    /**
     * Annuler la dernière action
     */
    public void undo()
    {
        // TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    /**
     * Refaire l'action annulée
     */
    public void redo()
    {
        // TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
