package VirtuTuile.Domain.UndoableEdits;

import VirtuTuile.Domain.Project;
import VirtuTuile.Domain.CircularSurface;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

public class UndoAddCircularSurface implements javax.swing.undo.UndoableEdit
{
    Project project;
    CircularSurface surface;
    
    public UndoAddCircularSurface(Project project, CircularSurface surface)
    {
        this.project = project;
        this.surface = surface;
    }
    
    @Override
    public void undo() throws CannotUndoException
    {
        project.unselect();
        project.removeSurface(surface);
    }

    @Override
    public boolean canUndo()
    {
        return true;
    }

    @Override
    public void redo() throws CannotRedoException
    {
        project.addSurface(surface);
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
        return "Création d'une surface circulaire";
    }

    @Override
    public String getUndoPresentationName()
    {
        return "Annuler la création de la surface circulaire";
    }

    @Override
    public String getRedoPresentationName()
    {
        return "Refaire la création de la surface circulaire";
    }
}
