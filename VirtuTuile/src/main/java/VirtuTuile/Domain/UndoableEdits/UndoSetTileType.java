package VirtuTuile.Domain.UndoableEdits;

import VirtuTuile.Domain.TileType;
import VirtuTuile.Domain.Covering;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

public class UndoSetTileType implements javax.swing.undo.UndoableEdit
{
    private final TileType oldTile;
    private final TileType newTile;
    private final Covering covering;
    
    public UndoSetTileType(TileType oldTile, TileType newTile, Covering covering)
    {
        this.oldTile = oldTile;
        this.newTile = newTile;
        this.covering = covering;
    }
    
    @Override
    public void undo() throws CannotUndoException
    {
        covering.setTileType(oldTile);
    }

    @Override
    public boolean canUndo()
    {
        return true;
    }

    @Override
    public void redo() throws CannotRedoException
    {
        covering.setTileType(newTile);
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
        return "Changement du type de tuile d'un recouvrement";
    }

    @Override
    public String getUndoPresentationName()
    {
        return "Annuler le changement de type de tuile du recouvrement";
    }

    @Override
    public String getRedoPresentationName()
    {
        return "Refaire le changement du type de tuile du recouvrement";
    }
}
