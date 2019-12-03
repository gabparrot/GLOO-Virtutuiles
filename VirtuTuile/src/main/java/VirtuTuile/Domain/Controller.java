package VirtuTuile.Domain;

import VirtuTuile.Domain.UndoableEdits.*;
import java.awt.Color;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import javax.swing.undo.UndoManager;

/**
 * Classe definissant le controlleur de Larman, faisant le pont entre le GUI et le logiciel
 */
public class Controller
{
    private Project project = null;
    private UndoManager undoManager = null;
    private Surface firstSurfaceToMerge = null;
    
    /**
     * Crée un nouveau projet.
     */
    public void createNewProject()
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
     * Désélectionne la surface sélectionnée.
     */
    public void unselectSurface()
    {
        project.unselect();
        firstSurfaceToMerge = null;
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
     * Sélectionne la surface à la fin de la liste des surfaces.
     */
    public void selectLastSurfaceAdded()
    {
        project.selectLastSurfaceAdded();
    }
    
    /**
     * Sélectionne un sommet d'une surface.
     * @param point le point qui touche au sommet.
     */
    public void selectVertex(Point2D.Double point)
    {
        project.selectVertex(point);
    }
    
    /**
     * Recouvre à nouveau toutes les surfaces du projet.
     */
    public void refreshSurfaces()
    {
        for (Surface surface : project.getSurfaces())
        {
            surface.coverSurface();
        }
    }
    
    /**
     * Annule la dernière opération.
     */
    public void undo()
    {
        if (undoManager.canUndo()) undoManager.undo();
    }
    
    /**
     * Refait la dernière opération annulée.
     */
    public void redo()
    {
        if (undoManager.canRedo()) undoManager.redo();
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
        refreshSurfaces();
    }

//************************************************************************************************\\
//                                      UNDOABLE EDITS                                            \\
//************************************************************************************************\\
    /**
     * Crée un nouveau type de tuile.
     * @param width : la largeur.
     * @param height : la hauteur.
     * @param name : le nom.
     * @param nbPerBox : le nombre de tuiles par boite.
     * @param color : la couleur.
     */
    public void addTileType(double width, double height, String name,
            int nbPerBox, Color color)
    {
        project.createTileType(width, height, name, nbPerBox, color);
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
     * Crée une nouvelle surface circulaire.
     * @param rectangle les bornes de l'ellipse.
     * @return true si la création à réussie, false sinon.
     */
    public boolean addCircularSurface(Rectangle2D.Double rectangle)
    {
        boolean status = project.addCircularSurface(rectangle);
        if (status)
        {
            CircularSurface surface = (CircularSurface)
                    (project.getSurfaces().get(project.getSurfaces().size() - 1));
            undoManager.addEdit(new UndoAddCircularSurface(project, surface));
        }
        return status;
    }
    
    /**
     * Crée une nouvelle surface irrégulière.
     * [UNDOABLE]
     * @param polygon la forme de la surface irrégulière.
     * @return true si la création à réussie, false sinon.
     */
    public boolean addIrregularSurface(Path2D.Double polygon)
    {
        boolean status = project.addIrregularSurface(polygon);
        if (status)
        {
            IrregularSurface surface = (IrregularSurface)
                    (project.getSurfaces().get(project.getSurfaces().size() - 1));
            undoManager.addEdit(new UndoAddIrregularSurface(project, surface));
        }
        return status;
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
     * Fusionne deux surfaces.
     * [UNDOABLE]
     * @return true pour réussite, false pour échec (surfaces disjointes).
     */
    public boolean mergeSurfaces()
    {
        Surface secondSurfaceToMerge = project.getSelectedSurface();
        if (firstSurfaceToMerge == secondSurfaceToMerge)
        {
            firstSurfaceToMerge = null;
            return false;
        }
        CombinedSurface combinedSurface = project.mergeSurfaces(firstSurfaceToMerge,
                                                                secondSurfaceToMerge);
        if (combinedSurface == null)
        {
            firstSurfaceToMerge = null;
            return false;
        }
        else
        {
            undoManager.addEdit(new UndoMergeSurfaces(project, firstSurfaceToMerge,
                    secondSurfaceToMerge, combinedSurface));
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
        surface.setXY(newPos.x, newPos.y, project);
        
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
        Color newColor = surface.getColor();
        if (!oldColor.equals(newColor))
        {
            undoManager.addEdit(new UndoSetSurfaceColor(project, oldColor, newColor, surface));
        }
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
        boolean newIsHole = surface.isHole();
        if (oldIsHole != newIsHole)
        {
            undoManager.addEdit(new UndoSetSurfaceIsHole(project, oldIsHole, newIsHole, surface));
        }
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
        boolean status = surface.setX(x, project);
        if (status)
        {
            double newX = surface.getBounds2D().getX();
            if (oldX != newX)
            {
                undoManager.addEdit(new UndoSetSurfaceX(project, oldX, newX, surface));
            }
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
        boolean status = surface.setY(y, project);
        if (status)
        {
            double newY = surface.getBounds2D().getY();
            if (oldY != newY)
            {
                undoManager.addEdit(new UndoSetSurfaceY(project, oldY, newY, surface));
            }
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
        boolean status = surface.setWidth(width, project);
        if (status)
        {
            double newWidth = surface.getBounds2D().getWidth();
            if (oldWidth != newWidth)
            {
                undoManager.addEdit(new UndoSetSurfaceWidth(project, oldWidth, newWidth, surface));
            }
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
        boolean status = surface.setHeight(height, project);
        if (status)
        {
            double newHeight = surface.getBounds2D().getHeight();
            if (oldHeight != newHeight)
            {
                undoManager.addEdit(new UndoSetSurfaceHeight(project, oldHeight, newHeight, surface));
            }
        }
        return status;
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
        Color newColor = covering.getJointColor();
        if (!oldColor.equals(newColor))
        {
            undoManager.addEdit(new UndoSetJointColor(oldColor, newColor, covering));
        }
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
        if (selectedIndex != project.getTileTypeIndex(oldTileType))
        {
            project.setTileTypeByIndex(selectedIndex);
            TileType newTileType = covering.getTileType();
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
        double newWidth = covering.getJointWidth();
        if (oldWidth != newWidth)
        {
            undoManager.addEdit(new UndoSetJointWidth(oldWidth, newWidth, covering));
        }
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
        double newOffsetX = covering.getOffsetX();
        if (oldOffsetX != newOffsetX)
        {
            undoManager.addEdit(new UndoSetOffsetX(oldOffsetX, newOffsetX, covering));
        }
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
        double newOffsetY = covering.getOffsetY();
        if (oldOffsetY != newOffsetY)
        {
            undoManager.addEdit(new UndoSetOffsetY(oldOffsetY, newOffsetY, covering));
        }   
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
        double newOffsetX = covering.getOffsetX();
        double newOffsetY = covering.getOffsetY();
        if (oldOffsetX != newOffsetX || oldOffsetY != newOffsetY)
        {
            undoManager.addEdit(new UndoSetOffsetXY(oldOffsetX, offsetX,
                oldOffsetY, offsetY, covering));
        }
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
        int newRowOffset = covering.getRowOffset();
        if (oldRowOffset != newRowOffset)
        {
            undoManager.addEdit(new UndoSetRowOffset(oldRowOffset, newRowOffset, covering));
        }
    }
    
    /**
     * Change la rotation du recouvrement de la surface sélectionnée
     * [UNDOABLE]
     * @param pNewRotation Un entier représentant les degrés de la rotation souhaitée
     */
    public void setRotation(int pNewRotation)
    {
        Covering covering = project.getSelectedSurface().getCovering();
        int oldRotation = covering.getRotation();
        covering.setRotation(pNewRotation);
        int newRotation = covering.getRotation();
        if (oldRotation != newRotation)
        {
            undoManager.addEdit(new UndoSetRotation(oldRotation, newRotation, covering));
        }
    }

    /**
     * Change le nom de la tuile du recouvrement de la surface sélectionnée.
     * [UNDOABLE]
     * @param name : le nouveau nom.
     */
    public void setTileName(String name)
    {
        TileType tileType = project.getSelectedSurface().getCovering().getTileType();
        String oldName = tileType.getName();
        tileType.setName(name);
        String newName = tileType.getName();
        if (!oldName.equals(newName))
        {
            undoManager.addEdit(new UndoSetTileName(tileType, oldName, newName));
        }
    }

    /**
     * Change la quantité par boîte de la tuile du recouvrement de la surface sélectionnée.
     * [UNDOABLE]
     * @param nbPerBox : la nouvelle quantité.
     */
    public void setTileNbPerBox(int nbPerBox)
    {
        TileType tileType = project.getSelectedSurface().getCovering().getTileType();
        int oldQty = tileType.getNbPerBox();
        tileType.setNbPerBox(nbPerBox);
        int newQty = tileType.getNbPerBox();
        if (oldQty != newQty)
        {
            undoManager.addEdit(new UndoSetTileNbPerBox(tileType, oldQty, newQty));
        }
    }

    /**
     * Change la couleur de la tuile du recouvrement de la surface sélectionnée.
     * [UNDOABLE]
     * @param color : la nouvelle couleur.
     */
    public void setTileColor(Color color)
    {
        TileType tileType = project.getSelectedSurface().getCovering().getTileType();
        Color oldColor = tileType.getColor();
        tileType.setColor(color);
        Color newColor = tileType.getColor();
        if (!oldColor.equals(newColor))
        {
            undoManager.addEdit(new UndoSetTileColor(tileType, oldColor, newColor));
        }
    }

    /**
     * Change la largeur de la tuile du recouvrement de la surface sélectionnée.
     * [UNDOABLE]
     * @param width : la nouvelle largeur.
     */
    public void setTileWidth(double width)
    {
       TileType tileType = project.getSelectedSurface().getCovering().getTileType();
       double oldWidth = tileType.getWidth();
       tileType.setWidth(width);
       double newWidth = tileType.getWidth();
       if (oldWidth != newWidth)
       {
           undoManager.addEdit(new UndoSetTileWidth(tileType, oldWidth, newWidth, this));
       }
    }

    /**
     * Change la hauteur de la tuile du recouvrement de la surface sélectionnée.
     * [UNDOABLE]
     * @param height : la nouvelle hauteur.
     */
    public void setTileHeight(double height)
    {
        TileType tileType = project.getSelectedSurface().getCovering().getTileType();
        double oldHeight = tileType.getHeight();
        tileType.setHeight(height);
        double newHeight = tileType.getHeight();
        if (oldHeight != newHeight)
        {
            undoManager.addEdit(new UndoSetTileHeight(tileType, oldHeight, newHeight, this));
        }
    }
    
    /**
     * Déplace le sommet sélectionné vers un point.
     * [UNDOABLE]
     * @param point le point destination du sommet.
     */
    public void moveVertexToPoint(Point2D.Double point)
    {
        Surface surface = project.getSelectedSurface();
        if (surface instanceof RectangularSurface)
        {
            Rectangle2D oldRect = surface.getBounds2D();
            project.moveVertexToPoint(point);
            Rectangle2D newRect = surface.getBounds2D();
            if (!oldRect.equals(newRect))
            {
                undoManager.addEdit(new UndoMoveVertexRectangularSurface(
                        oldRect, newRect, (RectangularSurface) surface));
            }
        }
        else if (surface instanceof IrregularSurface)
        {
            Path2D.Double oldPath = new Path2D.Double(((IrregularSurface) surface).getPath());
            project.moveVertexToPoint(point);
            Path2D.Double newPath = new Path2D.Double(((IrregularSurface) surface).getPath());
            if (!oldPath.equals(newPath))
            {
                undoManager.addEdit(new UndoMoveVertexIrregularSurface(
                        oldPath, newPath, (IrregularSurface) surface));
            }
        }
        else if (surface instanceof CombinedSurface)
        {
            Path2D.Double oldPath = new Path2D.Double(((CombinedSurface) surface).getPath());
            Path2D.Double oldUncoveredPath =
                    new Path2D.Double(((CombinedSurface) surface).getUncoveredPath());
            project.moveVertexToPoint(point);
            Path2D.Double newPath = new Path2D.Double(((CombinedSurface) surface).getPath());
            Path2D.Double newUncoveredPath =
                    new Path2D.Double(((CombinedSurface) surface).getUncoveredPath());
            if (!oldPath.equals(newPath) || !oldUncoveredPath.equals(newUncoveredPath))
            {
                undoManager.addEdit(new UndoMoveVertexCombinedSurface(
                        oldPath, newPath, oldUncoveredPath,
                        newUncoveredPath, (CombinedSurface) surface));
            }
        }
        else if (surface instanceof CircularSurface)
        {
            Path2D.Double oldPath = new Path2D.Double(((CircularSurface) surface).getPath());
            project.moveVertexToPoint(point);
            Path2D.Double newPath = new Path2D.Double(((CircularSurface) surface).getPath());
            if (!oldPath.equals(newPath))
            {
                undoManager.addEdit(new UndoMoveVertexCircularSurface(
                        oldPath, newPath, (CircularSurface) surface));
            }
        }
    }

    /**
     * Défusionne deux surfaces combinées.
     * [UNDOABLE]
     */
    public void unmergeSurface()
    {
        CombinedSurface surface = ((CombinedSurface) project.getSelectedSurface());
        undoManager.addEdit(new UndoUnmergeSurface(
                project, surface.getFirstSurface(), surface.getSecondSurface(), surface));
        project.unmergeSurface();
    }
    
//************************************************************************************************\\
//                                      GETTERS                                                   \\
//************************************************************************************************\\
    /**
     * Retourne les bornes de la surface sélectionnée.
     * @return les bornes de la surface sélectionnée.
     */
    public Rectangle2D getBounds2D()
    {
        return project.getSelectedSurface().getBounds2D();
    }
    
    /**
     * Retourne les bornes de la surface qui contient le point (sans la sélectionner).
     * @param point : le point qui doit être à l'intérieur de la surface.
     * @return les bornes de la surface trouvée, peut être null.
     */
    public Rectangle2D getBounds2DByPoint(Point2D.Double point)
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
     * Retourne true si un sommet est sélectionné, false sinon.
     * @return true si un sommet est sélectionné, false sinon.
     */
    public boolean vertexIsSelected()
    {
        return project.vertexIsSelected();
    }
    
    /**
     * Retourne les bornes entourantes d'une surface rectangulaire, qui décrivent
     * jusqu'à quel point elle peut être déplacée dans les quatre directions.
     * @return les bornes dans un tableau [gauche, en-haut, droite, en-bas]
     */
    public double[] getSurroundingBounds()
    {
        Surface selectedSurface = project.getSelectedSurface();
        if (selectedSurface instanceof RectangularSurface)
        {
            return ((RectangularSurface) selectedSurface).getSurroundingBounds(project);
        }
        else
        {
            throw new IllegalArgumentException();
        }
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
     * Retourne la couleur la surface sélectionnée.
     * @return la couleur de la surface sélectionnée.
     */
    public Color getColor()
    {
        return project.getSelectedSurface().getColor();
    }

    /**
     * Retourne le paramètre trou de la surface sélectionnée.
     * @return le paramètre trou de la surface sélectionnée.
     */
    public boolean isHole()
    {
        return project.getSelectedSurface().isHole();
    }

    /**
     * Retourne la largeur des joints de la surface sélectionnée.
     * @return la largeur des joints de la surface sélectionnée.
     */
    public double getJointWidth()
    {
        return project.getSelectedSurface().getCovering().getJointWidth();
    }
    
    /**
     * Retourne l'orientation du recouvrement de la surface sélectionnée.
     * @return l'orientation du recouvrement de la surface sélectionnée.
     */
    public boolean isNinetyDegree()
    {
        return project.getSelectedSurface().getCovering().isNinetyDegree();
    }

    /**
     * Retourne le motif du recouvrement de la surface sélectionnée.
     * @return le motif du recouvrement de la surface sélectionnée.
     */
    public Pattern getPattern()
    {
        return project.getSelectedSurface().getCovering().getPattern();
    }

    /**
     * Retourne la couleur de joint du recouvrement de la surface sélectionnée.
     * @return la couleur de joint du recouvrement de la surface sélectionnée.
     */
    public Color getJointColor()
    {
        return project.getSelectedSurface().getCovering().getJointColor();
    }
    
    /**
     * Retourne le décalage horizontal du recouvrement de la surface sélectionnée.
     * @return le décalage horizontal du recouvrement de la surface sélectionnée.
     */
    public double getOffsetX()
    {
        return project.getSelectedSurface().getCovering().getOffsetX();
    }

    /**
     * Retourne le décalage vertical du recouvrement de la surface sélectionnée.
     * @return le décalage vertical du recouvrement de la surface sélectionnée.
     */
    public double getOffsetY()
    {
        return project.getSelectedSurface().getCovering().getOffsetY();
    }
    
    /**
     * Retourne le décalage entre les rangées du recouvrement de la surface sélectionnée.
     * @return le décalage entre les rangées du recouvrement de la surface sélectionnée.
     */
    public int getRowOffset()
    {
        return project.getSelectedSurface().getCovering().getRowOffset();
    }
    
    /**
     * Retourne la rotation du recouvrement de la surface sélectionnée
     * @return La rotation, en degrés
     */
    public int getRotation()
    {
        return project.getSelectedSurface().getCovering().getRotation();
    }
    
    /**
     * Retourne les informations d'une tuile.
     * @param point: un point
     * @return un array avec les informations d'une tuile.
     */
    public Point2D.Double getTileAtPoint(Point2D.Double point)
    {
        return project.getTileAtPoint(point);
    }

    /**
     * Retourne le nom de la tuile du recouvrement de la surface sélectionnée.
     * @return le nom de la tuile du recouvrement de la surface sélectionnée.
     */
    public String getTileName()
    {
        return project.getSelectedSurface().getCovering().getTileType().getName();
    }
    
    /**
     * Retourne un tableau avec les noms des types de tuiles du projet.
     * @return les noms des types de tuiles du projet.
     */
    public String[] getTileNames()
    {
        return project.getTileNames();
    }
    
    /**
     * Retourne true si le Covering de la selectedSurface possède un type de tuile.
     * @return true si le Covering de la selectedSurface possède un type de tuile.
     */
    public boolean hasTileType()
    {
        return project.getSelectedSurface().getCovering().getTileType() != null;
    }
    
    /**
     * Retourne la largeur de la tuile du recouvrement de la surface sélectionnée.
     * @return la largeur de la tuile du recouvrement de la surface sélectionnée.
     */
    public double getTileWidth()
    {
        return project.getSelectedSurface().getCovering().getTileType().getWidth();
    }
    
    /**
     * Retourne la hauteur de la tuile du recouvrement de la surface sélectionnée.
     * @return la hauteur de la tuile du recouvrement de la surface sélectionnée.
     */
    public double getTileHeight()
    {
        return project.getSelectedSurface().getCovering().getTileType().getHeight();
    }
    
    /**
     * Retourne le nombre de tuiles par boîte de la tuile du recouvrement de la surface sélectionnée.
     * @return le nombre de tuiles par boîte de la tuile du recouvrement de la surface sélectionnée.
     */
    public int getTileNbPerBox()
    {
        return project.getSelectedSurface().getCovering().getTileType().getNbPerBox();
    }
    
    /**
     * Retourne le nombre de tuiles par boite par type de tuiles.
     * @return le nombre de tuiles par boite par type de tuiles.
     */
    public int[] getBoxCapacities()
    {
        return project.getBoxCapacities();
    }    
    
    /**
     * Retourne la couleur de la tuile du recouvrement de la surface sélectionnée. 
     * @return la couleur de la tuile du recouvrement de la surface sélectionnée.
     */
    public Color getTileColor()
    {
        return project.getSelectedSurface().getCovering().getTileType().getColor();
    }
    
    /**
     * Retourne le nombre de tuiles utilisées par la surface sélectionnée
     * @return le nombre de tuiles utilisées par la surface sélectionnée.
     */
    public int getTileQuantity()
    {
        return project.getTileQuantity();
    }
    
    /**
     * Retourne le nombre de tuiles utilisées par type de tuiles.
     * @return le nombre de tuiles utilisées par type de tuiles.
     */
    public int[] getTileQuantities()
    {       
        return project.getTileQuantities();
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
     * Retourne true si la surface sélectionnée est rectangulaire.
     * @return true si la surface sélectionnée est rectangulaire.
     */
    public boolean surfaceIsRectangular()
    {
        return project.getSelectedSurface() instanceof RectangularSurface;
    }
    
    /**
     * Retourne true si la surface sélectionnée est combinée.
     * @return true si la surface sélectionnée est combinée.
     */
    public boolean surfaceIsCombined()
    {
        return project.getSelectedSurface() instanceof CombinedSurface;
    }
    
    /**
     * Retourne true si une première surface a été sélectionnée pour une combinaison.
     * @return true si une première surface a été sélectionnée pour une combinaison.
     */
    public boolean mergeIsInProgress()
    {
        return firstSurfaceToMerge != null;
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
     * Retourne un texte explicatif décrivant la dernière opération.
     * @return un texte explicatif décrivant la dernière opération.
     */
    public String getUndoPresentationName()
    {
        if (undoManager.canUndo()) return undoManager.getUndoPresentationName();
        else return "Rien";
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

//************************************************************************************************\\
//                                      OTHER                                                     \\
//************************************************************************************************\\
    
    /**
     * Indique que la surface sélectionnée devra être combinée.
     */
    public void setFirstSurfaceToMerge()
    {
        firstSurfaceToMerge = project.getSelectedSurface();
    }

    /**
     * Centre le motif horizontalement de la surface sélectionnée.
     */
    public void centerPatternHorizontal()
    {
        Surface selectedSurface = project.getSelectedSurface();
        double surfaceWidth = selectedSurface.getBounds2D().getWidth();
        Covering covering = selectedSurface.getCovering();
        double tileWidth;
        if (covering.isNinetyDegree())
        {
            tileWidth = covering.getTileType().getHeight();
        }
        else
        {
            tileWidth = covering.getTileType().getWidth();
        }
        double jointWidth = covering.getJointWidth();
        
        double newOffsetX = -tileWidth +
                (((surfaceWidth - jointWidth) % (tileWidth + jointWidth)) - 2 * jointWidth) / 2;
        setOffsetX(newOffsetX);
    }

    /**
     * Centre le motif verticalement de la surface sélectionnée.
     */
    public void centerPatternVertical()
    {
        Surface selectedSurface = project.getSelectedSurface();
        double surfaceHeight = selectedSurface.getBounds2D().getHeight();
        Covering covering = selectedSurface.getCovering();
        double tileHeight;
        if (covering.isNinetyDegree())
        {
            tileHeight = covering.getTileType().getWidth();
        }
        else
        {
            tileHeight = covering.getTileType().getHeight();
        }
        double jointWidth = covering.getJointWidth();
        
        double newOffsetY = -tileHeight +
                (((surfaceHeight - jointWidth) % (tileHeight + jointWidth)) - 2 * jointWidth) / 2;
        setOffsetY(newOffsetY);
    }
    
    /**
     * Débute le motif des tuiles de la surface sélectionnée par une tuile pleine
     */
    public void startPatternOnFullTile()
    {
        setOffsetXY(0, 0);
    }
    
    /**
     * Débute le motif des tuiles de la surface sélectionnée par une colonne pleine
     */
    public void startPatternOnFullColumn()
    {
        setOffsetX(0.0);
    }
    
    /**
     * Débute le motif des tuiles de la surface sélectionnée par une colonne pleine
     */
    public void startPatternOnFullRow()
    {
        setOffsetY(0.0);
    }

    public void toggleCollisionCheck()
    {
        project.toggleCollisionCheck();
    }
    
    /**
     * Vérifie si le type de tuile sélectionné est utilisé dans un motif qui exige
     * des dimensions fixes.
     * @return true si le type de tuile sélectionnée est utilisé par un motif qui exige
     * des dimensions fixes.
     */
    public boolean tileTypeDimensionsAreLocked()
    {
        return project.tileTypeDimensionsAreLocked();
    }
}
