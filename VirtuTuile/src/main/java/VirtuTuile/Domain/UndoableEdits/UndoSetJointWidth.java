package VirtuTuile.Domain.UndoableEdits;

import VirtuTuile.Domain.Covering;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

public class UndoSetJointWidth implements javax.swing.undo.UndoableEdit
{
    private final double oldWidth;
    private final double newWidth;
    private final Covering covering;
    
    public UndoSetJointWidth(double oldWidth, double newWidth, Covering covering)
    {
        this.oldWidth = oldWidth;
        this.newWidth = newWidth;
        this.covering = covering;
    }
    
    @Override
    public void undo() throws CannotUndoException
    {
        covering.setJointWidth(oldWidth);
    }

    @Override
    public boolean canUndo()
    {
        return true;
    }

    @Override
    public void redo() throws CannotRedoException
    {
        covering.setJointWidth(newWidth);
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
        return "Changement de la largeur des joints";
    }

    @Override
    public String getUndoPresentationName()
    {
        return "Annuler le changement de la largeur des joints";
    }

    @Override
    public String getRedoPresentationName()
    {
        return "Refaire le changement de la largeur des joints";
    }
}
