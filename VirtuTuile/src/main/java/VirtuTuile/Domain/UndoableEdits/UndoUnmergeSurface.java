package VirtuTuile.Domain.UndoableEdits;

import VirtuTuile.Domain.CombinedSurface;
import VirtuTuile.Domain.Project;
import VirtuTuile.Domain.Surface;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

public class UndoUnmergeSurface implements javax.swing.undo.UndoableEdit
{
    private final Project project;
    private final Surface s1, s2;
    private final CombinedSurface surface;
    
    public UndoUnmergeSurface(Project project, Surface s1, Surface s2, CombinedSurface surface)
    {
        this.project = project;
        this.s1 = s1;
        this.s2 = s2;
        this.surface = surface;
    }
    
    @Override
    public void undo() throws CannotUndoException
    {
        project.removeSurface(s1);
        project.removeSurface(s2);
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
        project.unselect();
        project.removeSurface(surface);
        project.addSurface(s1);
        project.addSurface(s2);
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
        return "Décombinaison d'une surface";
    }

    @Override
    public String getUndoPresentationName()
    {
        return "Annuler la décombinaison de la surface";
    }

    @Override
    public String getRedoPresentationName()
    {
        return "Refaire la décombinaison de la surface";
    }
}
