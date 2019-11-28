package VirtuTuile.Domain.UndoableEdits;

import VirtuTuile.Domain.Controller;
import VirtuTuile.Domain.TileType;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

public class UndoSetTileWidth implements javax.swing.undo.UndoableEdit
{
    private final TileType tileType;
    private final double oldWidth;
    private final double newWidth;
    private final Controller controller;
    
    public UndoSetTileWidth(TileType tileType, double oldWidth, double newWidth, Controller controller)
    {
        this.tileType = tileType;
        this.oldWidth = oldWidth;
        this.newWidth = newWidth;
        this.controller = controller;
    }
    
    @Override
    public void undo() throws CannotUndoException
    {
        tileType.setWidth(oldWidth);
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
        tileType.setWidth(newWidth);
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
        return "Annuler le changement de la largeur d'un matériau";
    }

    @Override
    public String getRedoPresentationName()
    {
        return "Refaire le changement de la largeur d'un matériau";
    }
}
