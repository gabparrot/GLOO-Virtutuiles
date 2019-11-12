package VirtuTuile.Domain.UndoableEdits;

import VirtuTuile.Domain.Covering;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

public class UndoSetIsNinetyDegree implements javax.swing.undo.UndoableEdit
{
    private final boolean newValue;
    private final Covering covering;
    
    public UndoSetIsNinetyDegree(boolean newValue, Covering covering)
    {
        this.newValue = newValue;
        this.covering = covering;
    }
    
    @Override
    public void undo() throws CannotUndoException
    {
        covering.setIsNinetyDegree(!newValue);
    }

    @Override
    public boolean canUndo()
    {
        return true;
    }

    @Override
    public void redo() throws CannotRedoException
    {
        covering.setIsNinetyDegree(newValue);
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
        return "Changement de l'orientation des tuiles";
    }

    @Override
    public String getUndoPresentationName()
    {
        return "Annuler le changement de l'orientation des tuiles";
    }

    @Override
    public String getRedoPresentationName()
    {
        return "Refaire le changement de l'orientation des tuiles";
    }
}
