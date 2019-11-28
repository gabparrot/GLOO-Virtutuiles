package VirtuTuile.Domain.UndoableEdits;

import VirtuTuile.Domain.TileType;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

public class UndoSetTileNbPerBox implements javax.swing.undo.UndoableEdit
{
    private final TileType tileType;
    private final int oldQty;
    private final int newQty;
    
    public UndoSetTileNbPerBox(TileType tileType, int oldQty, int newQty)
    {
        this.tileType = tileType;
        this.oldQty = oldQty;
        this.newQty = newQty;
    }
    
    @Override
    public void undo() throws CannotUndoException
    {
        tileType.setNbPerBox(oldQty);
    }

    @Override
    public boolean canUndo()
    {
        return true;
    }

    @Override
    public void redo() throws CannotRedoException
    {
        tileType.setNbPerBox(newQty);
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
        return "Changement de la quantité d'un matériau";
    }

    @Override
    public String getUndoPresentationName()
    {
        return "Annuler le changement de la quantité d'un matériau";
    }

    @Override
    public String getRedoPresentationName()
    {
        return "Refaire le changement de la quantité d'un matériau";
    }
}
