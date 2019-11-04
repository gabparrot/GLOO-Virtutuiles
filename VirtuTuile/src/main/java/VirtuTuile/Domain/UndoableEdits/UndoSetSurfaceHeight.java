package VirtuTuile.Domain.UndoableEdits;

import VirtuTuile.Domain.Project;
import VirtuTuile.Domain.RectangularSurface;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

public class UndoSetSurfaceHeight implements javax.swing.undo.UndoableEdit
{
    Project project;
    double oldHeight;
    double newHeight;
    RectangularSurface surface;
    
    public UndoSetSurfaceHeight(Project project, double oldHeight,
            double newHeight, RectangularSurface surface)
    {
        this.project = project;
        this.oldHeight = oldHeight;
        this.newHeight = newHeight;
        this.surface = surface;
    }
    
    @Override
    public void undo() throws CannotUndoException
    {
        project.setRectangularSurfaceHeight(oldHeight, surface);
    }

    @Override
    public boolean canUndo()
    {
        return true;
    }

    @Override
    public void redo() throws CannotRedoException
    {
        project.setRectangularSurfaceHeight(newHeight, surface);
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
        return "Modification de la hauteur d'une surface rectangulaire";
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
