package VirtuTuile.Domain.UndoableEdits;

import VirtuTuile.Domain.Controller;
import VirtuTuile.Domain.TileType;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

public class UndoSetTileHeight implements javax.swing.undo.UndoableEdit
{
    private final TileType tileType;
    private final double oldHeight;
    private final double newHeight;
    private final Controller controller;
    
    public UndoSetTileHeight(TileType tileType, double oldHeight, double newHeight, Controller controller)
    {
        this.tileType = tileType;
        this.oldHeight = oldHeight;
        this.newHeight = newHeight;
        this.controller = controller;
    }
    
    @Override
    public void undo() throws CannotUndoException
    {
        tileType.setHeight(oldHeight);
        controller.refreshSurfaces();
    }

    @Override
    public boolean canUndo()
    {
        return true;
    }

    @Override
    public void redo() throws CannotRedoException
    {
        tileType.setHeight(newHeight);
        controller.refreshSurfaces();
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
        return "Annuler le changement de la hauteur d'un matériau";
    }

    @Override
    public String getRedoPresentationName()
    {
        return "Refaire le changement de la hauteur matériau";
    }
}
