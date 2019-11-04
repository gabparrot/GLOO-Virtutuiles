package VirtuTuile.Domain.UndoableEdits;

import VirtuTuile.Domain.Project;
import VirtuTuile.Domain.Surface;
import java.awt.Color;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

public class UndoSetSurfaceColor implements javax.swing.undo.UndoableEdit
{
    Project project;
    Color oldColor;
    Color newColor;
    Surface surface;
    
    public UndoSetSurfaceColor(Project project, Color oldColor, Color newColor, Surface surface)
    {
        this.project = project;
        this.oldColor = oldColor;
        this.newColor = newColor;
        this.surface = surface;
    }
    
    @Override
    public void undo() throws CannotUndoException
    {
        surface.setColor(oldColor);
    }

    @Override
    public boolean canUndo()
    {
        return true;
    }

    @Override
    public void redo() throws CannotRedoException
    {
        surface.setColor(newColor);
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
        return "Changement de la couleur d'une surface";
    }

    @Override
    public String getUndoPresentationName()
    {
        return "Annuler le changement de la couleur de la surface";
    }

    @Override
    public String getRedoPresentationName()
    {
        return "Refaire le changement de la couleur de la surface";
    }
}
