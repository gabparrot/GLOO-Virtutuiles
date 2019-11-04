package VirtuTuile.Domain;

import VirtuTuile.Domain.UndoableEdits.*;
import java.awt.Color;
import java.util.ArrayList;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import javax.swing.undo.UndoManager;

/**
 * @class definissant le controlleur de Larman, faisant le pont entre le GUI et le logiciel
 * @author 
 */
public class Controller
{
    private Project project = null;
    private UndoManager undoManager = null;
    
    public void newProject()
    {
        project = new Project();
        undoManager = new UndoManager();
    }
    
    public void closeProject()
    {
        project = null;
        undoManager = null;
    }

    public void undo()
    {
        if (undoManager.canUndo()) undoManager.undo();
    }
    
    public String getUndoPresentationName()
    {
        if (undoManager.canUndo()) return undoManager.getUndoPresentationName();
        else return "Rien";
    }
    
    public void redo()
    {
        if (undoManager.canRedo()) undoManager.redo();
    }
    
    public String getRedoPresentationName()
    {
        if (undoManager.canRedo()) return undoManager.getRedoPresentationName();
        else return "Rien";
    }
    
    public boolean projectExists()
    {
        return (project != null);
    }

    /**
     * Fait une requête au projet pour la liste des surfaces.
     * @return la liste des surfaces du projet.
     */
    public ArrayList<Surface> getSurfaces()
    {
        return project.getSurfaces();
    }
    
    /**
     * Désélectionne la surface sélectionnée.
     */
    public void unselect()
    {
        project.unselect();
    }
    
    /**
     * Retourne les bornes extérieures d'une surface, qui décrivent jusqu'à quel point une surface
     * peut être déplacée dans les quatre directions.
     * @param surface : la surface en question.
     * @return les bornes dans un tableau [gauche, en-haut, droite, en-bas]
     */
    public double[] getSurroundingBounds(Surface surface)
    {
        return project.getSurroundingBounds(surface);
    }
    
    /**
     * Crée une surface rectangulaire.
     * [UNDOABLE]
     * @param rectangle la forme du rectangle.
     * @return true si la création à réussie, false sinon.
     */
    public boolean addRectangularSurface(Rectangle2D.Double rectangle)
    {
        boolean status = project.addRectangularSurface(rectangle);
        if (status)
        {
            RectangularSurface surface = (RectangularSurface)
                    (project.getSurfaces().get(project.getSurfaces().size() - 1));
            undoManager.addEdit(new UndoAddRectangularSurface(project, surface));
        }
        return status;
    }
    
    /**
     * Crée une nouvelle surface irrégulière.
     */
    public void addIrregularSurface()
    {
        // TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Fusionne deux surfaces.
     * [UNDOABLE]
     * @param s1 : la première surface à fusionner.
     * @param s2 : la deuxième surface à fusionner.
     * @return true pour réussite, false pour échec (surfaces disjointes).
     */
    public boolean mergeSurfaces(Surface s1, Surface s2)
    {
        CombinedSurface combinedSurface = project.mergeSurfaces(s1, s2);
        if (combinedSurface == null) return false;
        undoManager.addEdit(new UndoMergeSurfaces(project, s1, s2, combinedSurface));
        return true;
    }
    
    /**
     * Déplace une surface à une nouvelle position.
     * [UNDOABLE]
     * @param newPos : nouvelle position.
     * @param surface : la surface qui doit être déplacée.
     */
    public void moveSurfaceToPoint(Point2D.Double newPos, Surface surface)
    {
        Rectangle2D bounds = surface.getBounds2D();
        Point2D.Double oldPoint = new Point2D.Double(bounds.getX(), bounds.getY());
        project.moveSurfaceToPoint(newPos, surface);
        
        // Sauvegarde
        bounds = surface.getBounds2D();
        Point2D.Double newPoint = new Point2D.Double(bounds.getX(), bounds.getY());
        undoManager.addEdit(new UndoMoveSurfaceToPoint(project, oldPoint, newPoint, surface));
    }
    
    /**
     * Change la couleur d'une surface.
     * [UNDOABLE]
     * @param color : la nouvelle couleur.
     * @param surface : la surface.
     */
    public void setSurfaceColor(Color color, Surface surface)
    {
        Color oldColor = surface.getColor();
        surface.setColor(color);
        undoManager.addEdit(new UndoSetSurfaceColor(project, oldColor, color, surface));
    }
    
    /**
     * Change le paramètre trou d'une surface.
     * [UNDOABLE]
     * @param isHole : si la surface doit être couverte.
     * @param surface : la surface.
     */
    public void setSurfaceIsHole(boolean isHole, Surface surface)
    {
        boolean oldIsHole = surface.isHole();
        surface.setIsHole(isHole);
        undoManager.addEdit(new UndoSetSurfaceIsHole(project, oldIsHole, isHole, surface));
    }
    
    /**
     * Set le paramètre x d'une surface.
     * [UNDOABLE]
     * @param x : le nouveau paramètre x.
     * @param surface : la surface qui doit être modifiée.
     * @return : true si réussi, false sinon.
     */
    public boolean setSurfaceX(double x, Surface surface)
    {
        double oldX = surface.getBounds2D().getX();
        boolean status = project.setSurfaceX(x, surface);
        if (status)
        {
            double newX = surface.getBounds2D().getX();
            undoManager.addEdit(new UndoSetSurfaceX(project, oldX, newX, surface));
        }
        return status;
    }

    /**
     * Set le paramètre y d'une surface.
     * [UNDOABLE]
     * @param y : le nouveau paramètre y.
     * @param surface : la surface qui doit être modifiée.
     * @return : true si réussi, false sinon.
     */
    public boolean setSurfaceY(double y, Surface surface)
    {
        double oldY = surface.getBounds2D().getY();
        boolean status = project.setSurfaceY(y, surface);
        if (status)
        {
            double newY = surface.getBounds2D().getY();
            undoManager.addEdit(new UndoSetSurfaceY(project, oldY, newY, surface));
        }
        return status;
    }

    /**
     * Set le paramètre width d'une surface rectangulaire.
     * [UNDOABLE]
     * @param width : le nouveau paramètre width.
     * @param surface : la surface qui doit être modifiée.
     * @return : true si réussi, false sinon.
     */
    public boolean setRectangularSurfaceWidth(double width, RectangularSurface surface)
    {
        double oldWidth = surface.width;
        boolean status = project.setRectangularSurfaceWidth(width, surface);
        if (status)
        {
            double newWidth = surface.width;
            undoManager.addEdit(new UndoSetSurfaceWidth(project, oldWidth, newWidth, surface));
        }
        return status;
    }

    /**
     * Set le paramètre height d'une surface rectangulaire.
     * [UNDOABLE]
     * @param height : le nouveau paramètre height.
     * @param surface : la surface qui doit être modifiée.
     * @return : true si réussi, false sinon.
     */
    public boolean setRectangularSurfaceHeight(double height, RectangularSurface surface)
    {
        double oldHeight = surface.height;
        boolean status = project.setRectangularSurfaceHeight(height, surface);
        if (status)
        {
            double newHeight = surface.height;
            undoManager.addEdit(new UndoSetSurfaceHeight(project, oldHeight, newHeight, surface));
        }
        return status;
    }
    
    /**
     * Sélectionne la surface qui contient le point.
     * @param point : le point qui doit être à l'intérieur de la surface.
     * @return la surface sélectionnée, peut être null.
     */
    public Surface selectSurface(Point2D.Double point)
    {
        return project.selectSurface(point);
    }

    /**
     * Trouve la surface qui contient le point (sans la sélectionner).
     * @param point : le point qui doit être à l'intérieur de la surface.
     * @return la surface trouvée, peut être null.
     */
    public Surface findSurface(Point2D.Double point)
    {
        return project.findSurface(point);
    }
    
    /**
     * Retourne la surface sélectionnée. Peut être null.
     * @return la surface sélectionnée, si une surface est sélectionnée, null sinon.
     */
    public Surface getSelectedSurface()
    {
        return project.getSelectedSurface();
    }
    
     /**
     * Supprime la surface sélectionnée.
     * [UNDOABLE]
     */
    public void removeSelectedSurface()
    {
        undoManager.addEdit(new UndoRemoveSurface(project, project.getSelectedSurface()));
        project.removeSelectedSurface();
    }
    
    /**
     * Ajoute un type de tuile dans la liste des tuiles disponibles.
     * @param tileType : le type de tuile à ajouter.
     */
    public void addTileType(TileType tileType)
    {
        project.addTileType(tileType);
    }

    /**
     * Retourne un tableau avec les noms des types de tuiles du projet.
     * @return les noms des types de tuiles du projet.
     */
    public String[] getTileTypeStrings()
    {
        return project.getTileTypeStrings();
    }
    
    /**
     * Bouge le revetement (motif) de la surface dans l'interface.
     * @param delta : le point sélectionné par la souris.
     */
    public void MoveSelectedCovering(Point2D.Double delta)
    {
        // TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    /**
     * Trouve le point le plus à droite et en-bas de toutes les surfaces.
     * @return le point le plus à droite et en-bas de toutes les surfaces. 
     */
    public Point2D.Double getFarthestPoint()
    {
        return project.getFarthestPoint();
    }

    /**
     * Change le couleur des joints d'une surface.
     * [UNDOABLE]
     * @param c : la nouvelle couleur.
     * @param surface : la surface en question.
     */
    public void setJointColor(Color c, Surface surface)
    {
        Covering covering = surface.getCovering();
        Color oldColor =  covering.getJointColor();
        covering.setJointColor(c);
        undoManager.addEdit(new UndoSetJointColor(oldColor, c, covering));
    }

    /**
     * Change l'orientation des tuiles d'une surface.
     * [UNDOABLE]
     * @param surface : la surface en question.
     * @param isNinetyDegree : true si les tuiles doivent être à 90 degrés.
     */
    public void setIsNinetyDegree(Surface surface, boolean isNinetyDegree)
    {
        Covering covering = surface.getCovering();
        covering.setIsNinetyDegree(isNinetyDegree);
        undoManager.addEdit(new UndoSetIsNinetyDegree(isNinetyDegree, covering));
        covering.coverSurface();
    }

    /**
     * Change le motif d'une surface.
     * [UNDOABLE]
     * @param surface : la surface en question.
     * @param pattern : le nouveau motif.
     */
    public void setPattern(Surface surface, Pattern pattern)
    {
        Covering covering = surface.getCovering();
        Pattern oldPattern = covering.getPattern();
        if (oldPattern != pattern)
        {
            covering.setPattern(pattern);
            undoManager.addEdit(new UndoSetPattern(oldPattern, pattern, covering));
            covering.coverSurface();
        }
    }

    /**
     * Change la couleur des tuiles d'une surface.
     * [UNDOABLE]
     * @param surface : la surface en question.
     * @param index : l'index de la nouvelle couleur dans la liste des couleurs du type de tuile.
     */
    public void setCoveringTileColorByIndex(Surface surface, int index)
    {
        Covering covering = surface.getCovering();
        int oldIndex = covering.getTileColorIndex();
        if (oldIndex != index)
        {
            covering.setTileColorByIndex(index);
            undoManager.addEdit(new UndoSetCoveringTileColor(oldIndex, index, covering));
        }
    }

    /**
     * Change le type de tuile d'une surface.
     * [UNDOABLE]
     * @param surface : la surface en question.
     * @param selectedIndex : l'index du type de tuile dans la liste du projet.
     */
    public void setTileTypeByIndex(Surface surface, int selectedIndex)
    {
        Covering covering = surface.getCovering();
        TileType oldTileType = covering.getTileType();
        project.setTileTypeByIndex(surface, selectedIndex);
        TileType newTileType = covering.getTileType();
        if (oldTileType != newTileType)
        {
            Color oldColor = covering.getTileColor();
            covering.setTileColorByIndex(0);
            undoManager.addEdit(new UndoSetTileType(oldTileType, newTileType, covering, oldColor));
            covering.coverSurface();
        }
    }

    /**
     * Change la largeur des joints d'une surface.
     * [UNDOABLE]
     * @param surface la surface en question.
     * @param width : la nouvelle largeur des joints.
     */
    public void setJointWidth(Surface surface, double width)
    {   
        Covering covering = surface.getCovering();
        double oldWidth = covering.getJointWidth();
        surface.getCovering().setJointWidth(Math.max(0, width));
        undoManager.addEdit(new UndoSetJointWidth(oldWidth, width, covering));
        covering.coverSurface();
    }
}
