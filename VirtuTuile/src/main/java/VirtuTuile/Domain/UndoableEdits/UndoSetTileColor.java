package VirtuTuile.Domain.UndoableEdits;

import VirtuTuile.Domain.TileType;
import java.awt.Color;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

public class UndoSetTileColor implements javax.swing.undo.UndoableEdit
{
    private final TileType tileType;
    private final Color oldColor;
    private final Color newColor;
    
    public UndoSetTileColor(TileType tileType, Color oldColor, Color newColor)
    {
        this.tileType = tileType;
        this.oldColor = oldColor;
        this.newColor = newColor;
    }
    
    @Override
    public void undo() throws CannotUndoException
    {
        tileType.setColor(oldColor);
    }

    @Override
    public boolean canUndo()
    {
        return true;
    }

    @Override
    public void redo() throws CannotRedoException
    {
        tileType.setColor(newColor);
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
        return "Changement de la couleur d'un matériau";
    }

    @Override
    public String getUndoPresentationName()
    {
        return "Annuler le changement de la couleur d'un matériau";
    }

    @Override
    public String getRedoPresentationName()
    {
        return "Refaire le changement de la couleur d'un matériau";
    }
}
