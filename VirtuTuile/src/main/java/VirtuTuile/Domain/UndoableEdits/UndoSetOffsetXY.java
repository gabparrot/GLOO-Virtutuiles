package VirtuTuile.Domain.UndoableEdits;

import VirtuTuile.Domain.Covering;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

public class UndoSetOffsetXY implements javax.swing.undo.UndoableEdit
{
    private final double oldOffsetX;
    private double newOffsetX;
    private final double oldOffsetY;
    private double newOffsetY;
    private final Covering covering;
    
    public UndoSetOffsetXY(double oldOffsetX, double newOffsetX,
            double oldOffsetY, double newOffsetY, Covering covering)
    {
        this.oldOffsetX = oldOffsetX;
        this.newOffsetX = newOffsetX;
        this.oldOffsetY = oldOffsetY;
        this.newOffsetY = newOffsetY;
        this.covering = covering;
    }
    
    public Covering getCovering()
    {
        return covering;
    }
    
    public double getNewOffsetX()
    {
        return newOffsetX;
    }
    
    public double getNewOffsetY()
    {
        return newOffsetY;
    }
    
    @Override
    public void undo() throws CannotUndoException
    {
        covering.setOffsetX(oldOffsetX);
        covering.setOffsetY(oldOffsetY);
    }

    @Override
    public boolean canUndo()
    {
        return true;
    }

    @Override
    public void redo() throws CannotRedoException
    {
        covering.setOffsetX(newOffsetX);
        covering.setOffsetY(newOffsetY);
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
        if (anEdit instanceof UndoSetOffsetXY)
        {
            UndoSetOffsetXY newEdit = (UndoSetOffsetXY) anEdit;
            if (this.covering == newEdit.getCovering())
            {
                this.newOffsetX = newEdit.getNewOffsetX();
                this.newOffsetY = newEdit.getNewOffsetY();
                return true;
            }
        }
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
        return "Changement du décalage du recouvrement";
    }

    @Override
    public String getUndoPresentationName()
    {
        return "Annuler le changement du décalage du recouvrement";
    }

    @Override
    public String getRedoPresentationName()
    {
        return "Refaire le changement du décalage du recouvrement";
    }
}
