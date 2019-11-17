package VirtuTuile.Domain.UndoableEdits;

import VirtuTuile.Domain.Project;
import VirtuTuile.Domain.Surface;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

public class UndoSetSurfaceX implements javax.swing.undo.UndoableEdit
{
    Project project;
    double oldX;
    double newX;
    Surface surface;
    
    public UndoSetSurfaceX(Project project, double oldX, double newX, Surface surface)
    {
        this.project = project;
        this.oldX = oldX;
        this.newX = newX;
        this.surface = surface;
    }
    
    @Override
    public void undo() throws CannotUndoException
    {
        surface.setX(oldX, project);
    }

    @Override
    public boolean canUndo()
    {
        return true;
    }

    @Override
    public void redo() throws CannotRedoException
    {
        surface.setX(newX, project);
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
        return "Déplacement horizontal d'une surface";
    }

    @Override
    public String getUndoPresentationName()
    {
        return "Annuler le déplacement horizontal de la surface";
    }

    @Override
    public String getRedoPresentationName()
    {
        return "Refaire le déplacement horizontal de la surface";
    }
}
