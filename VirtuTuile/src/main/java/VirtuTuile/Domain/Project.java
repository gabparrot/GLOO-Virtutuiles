package VirtuTuile.Domain;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Area;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.awt.Point;
import java.awt.Rectangle;

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
    
    /**
     * Constructeur.
     * @param projectName : le nom du projet.
     */
    public Project(String projectName)
    {
        this.projectName = projectName;
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
    public boolean addRectangularSurface(Rectangle rectangle)
    {
        boolean status = conflictCheck(rectangle);
        if (status)
        {
            surfaces.add(new RectangularSurface(rectangle, false, new Color(113, 148, 191)));
        }
        return status;
    }
    
    /**
     * Vérifie s'il y a conflit avec une nouvelle surface.
     * @param shape : la forme de la surface à vérifier.
     * @return true s'il n'y a pas de conflit, false s'il y a conflit.
     */
    public boolean conflictCheck(Shape shape)
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
     * Déplace une surface.
     * @param delta : déplacement x et y.
     * @param surface : la surface qui doit être déplacée.
     */
    public void moveSurface(Point delta, Surface surface)
    {
        if (surface instanceof RectangularSurface)
        {
            setRectangleX(((RectangularSurface) surface).x + delta.x, (RectangularSurface) surface);
            setRectangleY(((RectangularSurface) surface).y + delta.y, (RectangularSurface) surface);
        }
    }
    
    /**
     * Set le paramètre x d'une surface rectangulaire.
     * @param x : le nouveau paramètre x.
     * @param surface : la surface qui doit être modifiée.
     * @return : true si réussi, false sinon.
     */
    public boolean setRectangleX(int x, RectangularSurface surface)
    {
        if (x < 0) return false;
        
        int oldX = surface.x;
        
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
    public boolean setRectangleY(int y, RectangularSurface surface)
    {
        if (y < 0) return false;
        
        int oldY = surface.y;
        
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
    public boolean setRectangularSurfaceWidth(int width, RectangularSurface surface)
    {
        if (width < 100) return false;
        
        int oldWidth = surface.width;
        
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
    public boolean setRectangularSurfaceHeight(int height, RectangularSurface surface)
    {
        if (height < 100) return false;
        
        int oldHeight = surface.height;
        
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
    public Surface selectSurface(Point point)
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
     * Efface la surface selectionnee en les retirant de la liste
     */
    public void deleteSelectedSurface()
    {
        surfaces.remove(selectedSurface);
    }
    
    /**
     * @param deltaX
     * @param deltaY
     * Retourne la surface sélectionnée, peut être null.
     * @return la surface sélectionnée, si une surface est sélectionnée, null sinon.
     */
    public Surface getSelectedSurface()
    {
        if(selectedSurface != null)
        {
            return selectedSurface;
        }
        return null;
    }
   
    // Getters et Setters
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
