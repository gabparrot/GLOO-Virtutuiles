package VirtuTuile.Domain.UndoableEdits;

import VirtuTuile.Domain.Covering;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

public class UndoSetCoveringTileColor implements javax.swing.undo.UndoableEdit
{
    private final int oldIndex;
    private final int newIndex;
    private final Covering covering;
    
    public UndoSetCoveringTileColor(int oldIndex, int newIndex, Covering covering)
    {
        this.oldIndex = oldIndex;
        this.newIndex = newIndex;
        this.covering = covering;
    }
    
    @Override
    public void undo() throws CannotUndoException
    {
        covering.setTileColorByIndex(oldIndex);
    }

    @Override
    public boolean canUndo()
    {
        return true;
    }

    @Override
    public void redo() throws CannotRedoException
    {
        covering.setTileColorByIndex(newIndex);
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
        return "Changement de la couleur d'un recouvrement";
    }

    @Override
    public String getUndoPresentationName()
    {
        return "Annuler le changement de la couleur du recouvrement";
    }

    @Override
    public String getRedoPresentationName()
    {
        return "Refaire le changement de la couleur du recouvrement";
    }
}
