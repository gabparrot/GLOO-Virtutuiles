package VirtuTuile.Domain;

import VirtuTuile.Domain.UndoableEdits.*;
import java.awt.Color;
import java.util.ArrayList;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import javax.swing.undo.UndoManager;

/**
 * @class definissant le controlleur de Larman, faisant le pont entre le GUI et le logiciel
 * @author 
 */
public class Controller
{
    private Project project = null;
    private UndoManager undoManager = null;
    private Surface firstSurfaceToMerge = null;
    
    /**
     * Crée un nouveau projet.
     */
    public void newProject()
    {
        project = new Project();
        undoManager = new UndoManager();
    }
    
    /**
     * Ferme le projet en cours.
     */
    public void closeProject()
    {
        project = null;
        undoManager = null;
    }

    /**
     * Annule la dernière opération.
     */
    public void undo()
    {
        if (undoManager.canUndo()) undoManager.undo();
    }
    
    /**
     * Retourne un texte explicatif décrivant la dernière opération.
     * @return un texte explicatif décrivant la dernière opération.
     */
    public String getUndoPresentationName()
    {
        if (undoManager.canUndo()) return undoManager.getUndoPresentationName();
        else return "Rien";
    }
    
    /**
     * Refait la dernière opération annulée.
     */
    public void redo()
    {
        if (undoManager.canRedo()) undoManager.redo();
    }
    
    /**
     * Retourne un texte explicatif décrivant la dernière opération annulée.
     * @return un texte explicatif décrivant la dernière opération annulée.
     */
    public String getRedoPresentationName()
    {
        if (undoManager.canRedo()) return undoManager.getRedoPresentationName();
        else return "Rien";
    }
    
    /**
     * Retourne true si un projet est ouvert en ce moment.
     * @return true si un projet est ouvert en ce moment.
     */
    public boolean projectExists()
    {
        return (project != null);
    }

    /**
     * Retourne la liste des surfaces du projet.
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
        firstSurfaceToMerge = null;
    }
    
    /**
     * Retourne les bornes entourantes d'une surface, qui décrivent jusqu'à quel point une surface
     * peut être déplacée dans les quatre directions.
     * @return les bornes dans un tableau [gauche, en-haut, droite, en-bas]
     */
    public double[] getSurroundingBounds()
    {
        return project.getSurroundingBounds(project.getSelectedSurface());
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
     * @return true pour réussite, false pour échec (surfaces disjointes).
     */
    public boolean mergeSurfaces()
    {
        CombinedSurface combinedSurface = project.mergeSurfaces(firstSurfaceToMerge,
                                                                project.getSelectedSurface());
        if (combinedSurface == null)
        {
            firstSurfaceToMerge = null;
            return false;
        }
        else
        {
            undoManager.addEdit(new UndoMergeSurfaces(project, firstSurfaceToMerge,
                    project.getSelectedSurface(), combinedSurface));
            firstSurfaceToMerge = null;
            return true;
        }
    }
    
    /**
     * Déplace la surface sélectionnée à une nouvelle position.
     * [UNDOABLE]
     * @param newPos : nouvelle position.
     */
    public void moveSurfaceToPoint(Point2D.Double newPos)
    {
        Surface surface = project.getSelectedSurface();
        Rectangle2D bounds = surface.getBounds2D();
        Point2D.Double oldPoint = new Point2D.Double(bounds.getX(), bounds.getY());
        project.moveSurfaceToPoint(newPos, surface);
        
        // Sauvegarde
        bounds = surface.getBounds2D();
        Point2D.Double newPoint = new Point2D.Double(bounds.getX(), bounds.getY());
        if (!oldPoint.equals(newPoint))
        {
            undoManager.addEdit(new UndoMoveSurfaceToPoint(project, oldPoint, newPoint, surface));
        }
    }
    
    /**
     * Change la couleur de la surface sélectionnée.
     * [UNDOABLE]
     * @param color : la nouvelle couleur.
     */
    public void setSurfaceColor(Color color)
    {
        Surface surface = project.getSelectedSurface();
        Color oldColor = surface.getColor();
        surface.setColor(color);
        undoManager.addEdit(new UndoSetSurfaceColor(project, oldColor, color, surface));
    }
    
    /**
     * Change le paramètre trou de la surface sélectionnée.
     * [UNDOABLE]
     * @param isHole : si la surface doit être couverte.
     */
    public void setSurfaceIsHole(boolean isHole)
    {
        Surface surface = project.getSelectedSurface();
        boolean oldIsHole = surface.isHole();
        surface.setIsHole(isHole);
        undoManager.addEdit(new UndoSetSurfaceIsHole(project, oldIsHole, isHole, surface));
    }
    
    /**
     * Set le paramètre x de la surface sélectionnée.
     * [UNDOABLE]
     * @param x : le nouveau paramètre x.
     * @return : true si réussi, false sinon.
     */
    public boolean setSurfaceX(double x)
    {
        Surface surface = project.getSelectedSurface();
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
     * Set le paramètre y de la surface sélectionnée.
     * [UNDOABLE]
     * @param y : le nouveau paramètre y.
     * @return : true si réussi, false sinon.
     */
    public boolean setSurfaceY(double y)
    {
        Surface surface = project.getSelectedSurface();
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
     * Set le paramètre width de la surface sélectionnée.
     * [UNDOABLE]
     * @param width : le nouveau paramètre width.
     * @return : true si réussi, false sinon.
     */
    public boolean setSurfaceWidth(double width)
    {
        Surface surface = project.getSelectedSurface();
        double oldWidth = surface.getBounds2D().getWidth();
        boolean status = project.setSurfaceWidth(width, surface);
        if (status)
        {
            double newWidth = surface.getBounds2D().getWidth();
            undoManager.addEdit(new UndoSetSurfaceWidth(project, oldWidth, newWidth, surface));
        }
        return status;
    }

    /**
     * Set le paramètre height de la surface sélectionnée.
     * [UNDOABLE]
     * @param height : le nouveau paramètre height.
     * @return : true si réussi, false sinon.
     */
    public boolean setSurfaceHeight(double height)
    {
        Surface surface = project.getSelectedSurface();
        double oldHeight = surface.getBounds2D().getHeight();
        boolean status = project.setSurfaceHeight(height, surface);
        if (status)
        {
            double newHeight = surface.getBounds2D().getHeight();
            undoManager.addEdit(new UndoSetSurfaceHeight(project, oldHeight, newHeight, surface));
        }
        return status;
    }
    
    /**
     * Sélectionne la surface qui contient le point.
     * @param point : le point qui doit être à l'intérieur de la surface.
     */
    public void selectSurface(Point2D.Double point)
    {
        project.selectSurface(point);
    }

    /**
     * Trouve les bornes de la surface qui contient le point (sans la sélectionner).
     * @param point : le point qui doit être à l'intérieur de la surface.
     * @return la surface trouvée, peut être null.
     */
    public Rectangle2D findSurfaceBounds(Point2D.Double point)
    {
        Surface surface = project.findSurface(point);
        if (surface != null)
        {
            return surface.getBounds2D();
        }
        else
        {
            return null;
        }
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
     * Trouve le point le plus à droite et en-bas de toutes les surfaces.
     * @return le point le plus à droite et en-bas de toutes les surfaces. 
     */
    public Point2D.Double getFarthestPoint()
    {
        return project.getFarthestPoint();
    }

    /**
     * Change le couleur des joints de la surface sélectionnée.
     * [UNDOABLE]
     * @param c : la nouvelle couleur.
     */
    public void setJointColor(Color c)
    {
        Covering covering = project.getSelectedSurface().getCovering();
        Color oldColor =  covering.getJointColor();
        covering.setJointColor(c);
        undoManager.addEdit(new UndoSetJointColor(oldColor, c, covering));
    }

    /**
     * Change l'orientation des tuiles de la surface sélectionnée.
     * [UNDOABLE]
     * @param isNinetyDegree : true si les tuiles doivent être à 90 degrés.
     */
    public void setIsNinetyDegree(boolean isNinetyDegree)
    {
        Covering covering = project.getSelectedSurface().getCovering();
        covering.setIsNinetyDegree(isNinetyDegree);
        undoManager.addEdit(new UndoSetIsNinetyDegree(isNinetyDegree, covering));
        covering.setIsNinetyDegree(isNinetyDegree);
    }

    /**
     * Change le motif de la surface sélectionnée.
     * [UNDOABLE]
     * @param pattern : le nouveau motif.
     */
    public void setPattern(Pattern pattern)
    {
        Covering covering = project.getSelectedSurface().getCovering();
        Pattern oldPattern = covering.getPattern();
        if (oldPattern != pattern)
        {
            covering.setPattern(pattern);
            undoManager.addEdit(new UndoSetPattern(oldPattern, pattern, covering));
        }
    }

    /**
     * Change le type de tuile de la surface sélectionnée.
     * [UNDOABLE]
     * @param selectedIndex : l'index du type de tuile dans la liste du projet.
     */
    public void setTileTypeByIndex(int selectedIndex)
    {
        Covering covering = project.getSelectedSurface().getCovering();
        TileType oldTileType = covering.getTileType();
        project.setTileTypeByIndex(selectedIndex);
        TileType newTileType = covering.getTileType();
        if (!oldTileType.getName().equals(newTileType.getName()))
        {
            undoManager.addEdit(new UndoSetTileType(oldTileType, newTileType, covering));
        }
    }

    /**
     * Change la largeur des joints de la surface sélectionnée.
     * [UNDOABLE]
     * @param width : la nouvelle largeur des joints.
     */
    public void setJointWidth(double width)
    {   
        Covering covering = project.getSelectedSurface().getCovering();
        double oldWidth = covering.getJointWidth();
        covering.setJointWidth(Math.max(0, width));
        undoManager.addEdit(new UndoSetJointWidth(oldWidth, width, covering));
    }

    /**
     * Change le décalage horizontal de la surface sélectionnée.
     * [UNDOABLE}
     * @param offset : le nouveau décalage horizontal.
     */
    public void setOffsetX(double offset)
    {
        Covering covering = project.getSelectedSurface().getCovering();
        double oldOffsetX = covering.getOffsetX();
        covering.setOffsetX(offset);
        undoManager.addEdit(new UndoSetOffsetX(oldOffsetX, offset, covering));
    }

    /**
     * Change le décalage vertical de la surface sélectionnée.
     * [UNDOABLE]
     * @param offset : le nouveau décalage vertical.
     */
    public void setOffsetY(double offset)
    {
        Covering covering = project.getSelectedSurface().getCovering();
        double oldOffsetY = covering.getOffsetY();
        covering.setOffsetY(offset);
        undoManager.addEdit(new UndoSetOffsetY(oldOffsetY, offset, covering));   
    }
    
    /**
     * Change le décalage horizontal et vertical de la surface sélectionnée.
     * [UNDOABLE]
     * @param offsetX : le nouveau décalage horizontal.
     * @param offsetY : le nouveau décalage vertical.
     */
    public void setOffsetXY(double offsetX, double offsetY)
    {
        Covering covering = project.getSelectedSurface().getCovering();
        double oldOffsetX = covering.getOffsetX();
        double oldOffsetY = covering.getOffsetY();
        covering.setOffsetX(offsetX);
        covering.setOffsetY(offsetY);
        undoManager.addEdit(new UndoSetOffsetXY(oldOffsetX, offsetX,
                oldOffsetY, offsetY, covering));
    }
    
    /**
     * Change le décalage entre les rangées de la surface sélectionnée.
     * [UNDOABLE]
     * @param rowOffset : le nouveau décalage entre les rangées.
     */
    public void setRowOffset(int rowOffset)
    {
        Covering covering = project.getSelectedSurface().getCovering();
        int oldRowOffset = covering.getRowOffset();
        covering.setRowOffset(rowOffset);
        undoManager.addEdit(new UndoSetRowOffset(oldRowOffset, rowOffset, covering));
    }
    
    /**
     * Sauvegarde le projet.
     * @param file : le fichier de sauvegarde.
     */
    public void saveProject(File file)
    {
        project.saveSurfacesToFile(file);
    }
    
    /**
     * Charge un projet.
     * @param file : le fichier de sauvegarde.
     */
    public void loadProject(File file)
    {
        project.loadSurfacesFromFile(file);
    }

    /**
     * Retourne les bornes de la surface sélectionnée.
     * @return les bornes de la surface sélectionnée.
     */
    public Rectangle2D getBounds2D()
    {
        return project.getSelectedBounds2D();
    }

    /**
     * Retourne la largeur des joints de la surface sélectionnée.
     * @return la largeur des joints de la surface sélectionnée.
     */
    public double getJointWidth()
    {
        return project.getJointWidth();
    }
    
    /**
     * Retourne la couleur la surface sélectionnée.
     * @return la couleur de la surface sélectionnée.
     */
    public Color getColor()
    {
        return project.getColor();
    }

    /**
     * Retourne le paramètre trou de la surface sélectionnée.
     * @return le paramètre trou de la surface sélectionnée.
     */
    public boolean isHole()
    {
        return project.isHole();
    }

    /**
     * Retourne l'orientation du recouvrement de la surface sélectionnée.
     * @return l'orientation du recouvrement de la surface sélectionnée.
     */
    public boolean isNinetyDegree()
    {
        return project.isNinetyDegree();
    }

    /**
     * Retourne le motif du recouvrement de la surface sélectionnée.
     * @return le motif du recouvrement de la surface sélectionnée.
     */
    public Pattern getPattern()
    {
        return project.getPattern();
    }

    /**
     * Retourne la couleur de joint du recouvrement de la surface sélectionnée.
     * @return la couleur de joint du recouvrement de la surface sélectionnée.
     */
    public Color getJointColor()
    {
        return project.getJointColor();
    }

    /**
     * Retourne le type de tuile du recouvrement de la surface sélectionnée.
     * @return le type de tuile du recouvrement de la surface sélectionnée.
     */
    public TileType getTileType()
    {
        return project.getTileType();
    }
    
    /**
     * Retourne le décalage horizontal du recouvrement de la surface sélectionnée.
     * @return le décalage horizontal du recouvrement de la surface sélectionnée.
     */
    public double getOffsetX()
    {
        return project.getOffsetX();
    }

    /**
     * Retourne le décalage vertical du recouvrement de la surface sélectionnée.
     * @return le décalage vertical du recouvrement de la surface sélectionnée.
     */
    public double getOffsetY()
    {
        return project.getOffsetY();
    }
    
    /**
     * Retourne le décalage entre les rangées du recouvrement de la surface sélectionnée.
     * @return le décalage entre les rangées du recouvrement de la surface sélectionnée.
     */
    public int getRowOffset()
    {
        return project.getRowOffset();
    }
    
    /**
     * Retourne true si une surface est sélectionnée.
     * @return true si une surface est sélectionnée.
     */
    public boolean surfaceIsSelected()
    {
        if (project != null)
        {
            return project.surfaceIsSelected();
        }
        else
        {
            return false;
        }
    }
    
    /**
     * Retourne true si une première surface a été sélectionnée pour une combinaison.
     * @return true si une première surface a été sélectionnée pour une combinaison.
     */
    public boolean mergeInProgress()
    {
        return firstSurfaceToMerge != null;
    }
    
    /**
     * Indique que la surface sélectionnée devra être combinée.
     */
    public void setFirstSurfaceToMerge()
    {
        firstSurfaceToMerge = project.getSelectedSurface();
    }
}
