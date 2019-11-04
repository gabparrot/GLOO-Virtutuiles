package VirtuTuile.Domain.UndoableEdits;

import VirtuTuile.Domain.Project;
import VirtuTuile.Domain.Surface;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

public class UndoSetSurfaceIsHole implements javax.swing.undo.UndoableEdit
{
    Project project;
    boolean oldIsHole;
    boolean newIsHole;
    Surface surface;
    
    public UndoSetSurfaceIsHole(Project project, boolean oldIsHole,
            boolean newIsHole, Surface surface)
    {
        this.project = project;
        this.oldIsHole = oldIsHole;
        this.newIsHole = newIsHole;
        this.surface = surface;
    }
    
    @Override
    public void undo() throws CannotUndoException
    {
        surface.setIsHole(oldIsHole);
    }

    @Override
    public boolean canUndo()
    {
        return true;
    }

    @Override
    public void redo() throws CannotRedoException
    {
        surface.setIsHole(newIsHole);
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
        return "Changement du paramètre trou de la surface";
    }

    @Override
    public String getUndoPresentationName()
    {
        return "Annuler le changement du paramètre trou de la surface";
    }

    @Override
    public String getRedoPresentationName()
    {
        return "Refaire le changement du paramètre trou de la surface";
    }
}
