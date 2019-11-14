package VirtuTuile.Domain.UndoableEdits;

import VirtuTuile.Domain.Covering;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

public class UndoSetOffsetX implements javax.swing.undo.UndoableEdit
{
    private final double oldOffset;
    private final double newOffset;
    private final Covering covering;
    
    public UndoSetOffsetX(double oldOffset, double newOffset, Covering covering)
    {
        this.oldOffset = oldOffset;
        this.newOffset = newOffset;
        this.covering = covering;
    }
    
    @Override
    public void undo() throws CannotUndoException
    {
        covering.setOffsetX(oldOffset);
    }

    @Override
    public boolean canUndo()
    {
        return true;
    }

    @Override
    public void redo() throws CannotRedoException
    {
        covering.setOffsetX(newOffset);
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
        return "Changement du décalage horizontal du recouvrement";
    }

    @Override
    public String getUndoPresentationName()
    {
        return "Annuler le changement du décalage horizontal du recouvrement";
    }

    @Override
    public String getRedoPresentationName()
    {
        return "Refaire le changement du décalage horizontal du recouvrement";
    }
}
