package VirtuTuile.Domain.UndoableEdits;

import VirtuTuile.Domain.Project;
import VirtuTuile.Domain.Surface;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

public class UndoRemoveSurface implements javax.swing.undo.UndoableEdit
{
    Project project;
    Surface surface;
    
    public UndoRemoveSurface(Project project, Surface surface)
    {
        this.project = project;
        this.surface = surface;
    }
    
    @Override
    public void undo() throws CannotUndoException
    {
        project.addSurface(surface);
    }

    @Override
    public boolean canUndo()
    {
        return true;
    }

    @Override
    public void redo() throws CannotRedoException
    {
        project.removeSurface(surface);
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
        return "Suppression d'une surface";
    }

    @Override
    public String getUndoPresentationName()
    {
        return "Annuler la suppression de la surface";
    }

    @Override
    public String getRedoPresentationName()
    {
        return "Refaire la suppression de la surface";
    }
}
