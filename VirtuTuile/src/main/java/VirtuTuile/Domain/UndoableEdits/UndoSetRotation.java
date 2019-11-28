package VirtuTuile.Domain.UndoableEdits;

import VirtuTuile.Domain.Covering;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

public class UndoSetRotation implements javax.swing.undo.UndoableEdit
{
    private final int oldRotation;
    private final int newRotation;
    private final Covering covering;
    
    public UndoSetRotation(int pOldRotation, int pNewRotation, Covering pCovering)
    {
        this.oldRotation = pOldRotation;
        this.newRotation = pNewRotation;
        this.covering = pCovering;
    }
    
    @Override
    public void undo() throws CannotUndoException
    {
        covering.setRotation(oldRotation);
    }

    @Override
    public boolean canUndo()
    {
        return true;
    }

    @Override
    public void redo() throws CannotRedoException
    {
        covering.setRotation(newRotation);
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
        return "Changement de rotation du recouvrement";
    }

    @Override
    public String getUndoPresentationName()
    {
        return "Annuler le changement de rotation du recouvrement";
    }

    @Override
    public String getRedoPresentationName()
    {
        return "Refaire le changement de rotation du recouvrement";
    }
}
