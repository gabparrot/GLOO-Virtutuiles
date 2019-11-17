package VirtuTuile.Domain.UndoableEdits;

import VirtuTuile.Domain.Project;
import VirtuTuile.Domain.Surface;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

public class UndoSetSurfaceY implements javax.swing.undo.UndoableEdit
{
    Project project;
    double oldY;
    double newY;
    Surface surface;
    
    public UndoSetSurfaceY(Project project, double oldY, double newY, Surface surface)
    {
        this.project = project;
        this.oldY = oldY;
        this.newY = newY;
        this.surface = surface;
    }
    
    @Override
    public void undo() throws CannotUndoException
    {
        surface.setY(oldY, project);
    }

    @Override
    public boolean canUndo()
    {
        return true;
    }

    @Override
    public void redo() throws CannotRedoException
    {
        surface.setY(newY, project);
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
        return "Annuler le déplacement vertical de la surface";
    }

    @Override
    public String getRedoPresentationName()
    {
        return "Refaire le déplacement vertical de la surface";
    }
}
