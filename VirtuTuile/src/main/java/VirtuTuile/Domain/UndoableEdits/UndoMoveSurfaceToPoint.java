package VirtuTuile.Domain.UndoableEdits;

import VirtuTuile.Domain.Project;
import VirtuTuile.Domain.Surface;
import java.awt.geom.Point2D;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

public class UndoMoveSurfaceToPoint implements javax.swing.undo.UndoableEdit
{
    Project project;
    Point2D.Double oldPoint;
    Point2D.Double newPoint;
    Surface surface;
    
    public UndoMoveSurfaceToPoint(Project project, Point2D.Double oldPoint,
            Point2D.Double newPoint, Surface surface)
    {
        this.project = project;
        this.oldPoint = oldPoint;
        this.newPoint = newPoint;
        this.surface = surface;
    }
    
    public Point2D.Double getNewPoint()
    {
        return newPoint;
    }
    
    public Surface getSurface()
    {
        return surface;
    }

    @Override
    public void undo() throws CannotUndoException
    {
        project.moveSurfaceToPoint(oldPoint, surface);
    }

    @Override
    public boolean canUndo()
    {
        return true;
    }

    @Override
    public void redo() throws CannotRedoException
    {
        project.moveSurfaceToPoint(newPoint, surface);
    }
    
    @Override
    public boolean canRedo()
    {
        return true;
    }

    @Override
    public void die()
    {
    }

    @Override
    public boolean addEdit(UndoableEdit anEdit)
    {
        if (anEdit instanceof UndoMoveSurfaceToPoint)
        {
            UndoMoveSurfaceToPoint newEdit = (UndoMoveSurfaceToPoint) anEdit;
            if (this.surface == newEdit.getSurface())
            {
                this.newPoint = newEdit.getNewPoint();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean replaceEdit(UndoableEdit anEdit)
    {
        return false;
    }

    @Override
    public boolean isSignificant()
    {
        return true;
    }

    @Override
    public String getPresentationName()
    {
        return "Déplacement d'une surface";
    }

    @Override
    public String getUndoPresentationName()
    {
        return "Annuler le déplacement de la surface";
    }

    @Override
    public String getRedoPresentationName()
    {
        return "Refaire le déplacement de la surface";
    }
}
