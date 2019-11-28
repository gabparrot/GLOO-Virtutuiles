package VirtuTuile.Domain.UndoableEdits;

import VirtuTuile.Domain.TileType;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

public class UndoSetTileName implements javax.swing.undo.UndoableEdit
{
    private final TileType tileType;
    private final String oldName;
    private final String newName;
    
    public UndoSetTileName(TileType tileType, String oldName, String newName)
    {
        this.tileType = tileType;
        this.oldName = oldName;
        this.newName = newName;
    }
    
    @Override
    public void undo() throws CannotUndoException
    {
        tileType.setName(oldName);
    }

    @Override
    public boolean canUndo()
    {
        return true;
    }

    @Override
    public void redo() throws CannotRedoException
    {
        tileType.setName(newName);
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
        return "Changement du nom d'un matériau";
    }

    @Override
    public String getUndoPresentationName()
    {
        return "Annuler le changement du nom du matériau";
    }

    @Override
    public String getRedoPresentationName()
    {
        return "Refaire le changement du nom du matériau";
    }
}
