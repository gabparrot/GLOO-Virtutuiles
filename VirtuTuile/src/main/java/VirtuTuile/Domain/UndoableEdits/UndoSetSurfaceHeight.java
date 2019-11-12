package VirtuTuile.Domain.UndoableEdits;

import VirtuTuile.Domain.Project;
import VirtuTuile.Domain.Surface;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

public class UndoSetSurfaceHeight implements javax.swing.undo.UndoableEdit
{
    Project project;
    double oldHeight;
    double newHeight;
    Surface surface;
    
    public UndoSetSurfaceHeight(Project project, double oldHeight,
            double newHeight, Surface surface)
    {
        this.project = project;
        this.oldHeight = oldHeight;
        this.newHeight = newHeight;
        this.surface = surface;
    }
    
    @Override
    public void undo() throws CannotUndoException
    {
        project.setSurfaceHeight(oldHeight, surface);
    }

    @Override
    public boolean canUndo()
    {
        return true;
    }

    @Override
    public void redo() throws CannotRedoException
    {
        project.setSurfaceHeight(newHeight, surface);
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
        return "Modification de la hauteur d'une surface";
    }

    @Override
    public String getUndoPresentationName()
    {
        return "Annuler la modification de la hauteur de la surface";
    }

    @Override
    public String getRedoPresentationName()
    {
        return "Refaire la modification de la hauteur de la surface";
    }
}
