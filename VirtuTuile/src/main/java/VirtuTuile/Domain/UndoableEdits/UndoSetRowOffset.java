package VirtuTuile.Domain.UndoableEdits;

import VirtuTuile.Domain.Covering;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

public class UndoSetRowOffset implements javax.swing.undo.UndoableEdit
{
    private final int oldOffset;
    private final int newOffset;
    private final Covering covering;
    
    public UndoSetRowOffset(int oldOffset, int newOffset, Covering covering)
    {
        this.oldOffset = oldOffset;
        this.newOffset = newOffset;
        this.covering = covering;
    }
    
    @Override
    public void undo() throws CannotUndoException
    {
        covering.setRowOffset(oldOffset);
    }

    @Override
    public boolean canUndo()
    {
        return true;
    }

    @Override
    public void redo() throws CannotRedoException
    {
        covering.setRowOffset(newOffset);
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
        return "Changement du décalage entre les rangées du recouvrement";
    }

    @Override
    public String getUndoPresentationName()
    {
        return "Annuler le changement du décalage entre les rangées du recouvrement";
    }

    @Override
    public String getRedoPresentationName()
    {
        return "Refaire le changement du décalage entre les rangées du recouvrement";
    }
}
