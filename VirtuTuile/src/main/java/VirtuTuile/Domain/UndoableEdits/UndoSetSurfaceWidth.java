package VirtuTuile.Domain.UndoableEdits;

import VirtuTuile.Domain.Project;
import VirtuTuile.Domain.Surface;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

public class UndoSetSurfaceWidth implements javax.swing.undo.UndoableEdit
{
    Project project;
    double oldWidth;
    double newWidth;
    Surface surface;
    
    public UndoSetSurfaceWidth(Project project, double oldWidth,
            double newWidth, Surface surface)
    {
        this.project = project;
        this.oldWidth = oldWidth;
        this.newWidth = newWidth;
        this.surface = surface;
    }
    
    @Override
    public void undo() throws CannotUndoException
    {
        surface.setWidth(oldWidth, project);
    }

    @Override
    public boolean canUndo()
    {
        return true;
    }

    @Override
    public void redo() throws CannotRedoException
    {
        surface.setWidth(newWidth, project);
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
        return "Modification de la largeur d'une surface";
    }

    @Override
    public String getUndoPresentationName()
    {
        return "Annuler la modification de la largeur de la surface";
    }

    @Override
    public String getRedoPresentationName()
    {
        return "Refaire la modification de la largeur de la surface";
    }
}
