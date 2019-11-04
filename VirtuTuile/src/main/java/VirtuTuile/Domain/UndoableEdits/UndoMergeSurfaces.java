package VirtuTuile.Domain.UndoableEdits;

import VirtuTuile.Domain.Project;
import VirtuTuile.Domain.Surface;
import VirtuTuile.Domain.CombinedSurface;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

public class UndoMergeSurfaces implements javax.swing.undo.UndoableEdit
{
    Project project;
    Surface s1, s2;
    CombinedSurface s3;
    
    public UndoMergeSurfaces(Project project, Surface s1, Surface s2, CombinedSurface s3)
    {
        this.project = project;
        this.s1 = s1;
        this.s2 = s2;
        this.s3 = s3;
    }
    
    @Override
    public void undo() throws CannotUndoException
    {
        project.removeSurface(s3);
        project.addSurface(s1);
        project.addSurface(s2);
    }

    @Override
    public boolean canUndo()
    {
        return true;
    }

    @Override
    public void redo() throws CannotRedoException
    {
        project.removeSurface(s1);
        project.removeSurface(s2);
        project.addSurface(s3);
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
        return "Fusion de surfaces";
    }

    @Override
    public String getUndoPresentationName()
    {
        return "Annuler la fusion des surfaces";
    }

    @Override
    public String getRedoPresentationName()
    {
        return "Refaire la fusion des surfaces";
    }
}
