package VirtuTuile.Domain.UndoableEdits;

import VirtuTuile.Domain.Covering;
import java.awt.Color;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

public class UndoSetJointColor implements javax.swing.undo.UndoableEdit
{
    private final Color oldColor;
    private final Color newColor;
    private final Covering covering;
    
    public UndoSetJointColor(Color oldColor, Color newColor, Covering covering)
    {
        this.oldColor = oldColor;
        this.newColor = newColor;
        this.covering = covering;
    }
    
    @Override
    public void undo() throws CannotUndoException
    {
        covering.setJointColor(oldColor);
        covering.coverSurface();
    }

    @Override
    public boolean canUndo()
    {
        return true;
    }

    @Override
    public void redo() throws CannotRedoException
    {
        covering.setJointColor(newColor);
        covering.coverSurface();
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
        return "Changement de la couleur des joints";
    }

    @Override
    public String getUndoPresentationName()
    {
        return "Annuler le changement de la couleur des joints";
    }

    @Override
    public String getRedoPresentationName()
    {
        return "Refaire le changement de la couleur des joints";
    }
}
