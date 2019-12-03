package VirtuTuile.Domain.UndoableEdits;

import VirtuTuile.Domain.RectangularSurface;
import java.awt.geom.Rectangle2D;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

public class UndoMoveVertexRectangularSurface implements javax.swing.undo.UndoableEdit
{
    private final Rectangle2D oldRect;
    private Rectangle2D newRect;
    private final RectangularSurface surface;
    
    public UndoMoveVertexRectangularSurface(Rectangle2D oldRect, Rectangle2D newRect, RectangularSurface surface)
    {
        this.oldRect = oldRect;
        this.newRect = newRect;
        this.surface = surface;
    }
    
    public RectangularSurface getSurface()
    {
        return surface;
    }
    
    public Rectangle2D getNewRect()
    {
        return newRect;
    }
    
    @Override
    public void undo() throws CannotUndoException
    {
        surface.setRect(oldRect);
        surface.coverSurface();
    }

    @Override
    public boolean canUndo()
    {
        return true;
    }

    @Override
    public void redo() throws CannotRedoException
    {
        surface.setRect(newRect);
        surface.coverSurface();
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
        if (anEdit instanceof UndoMoveVertexRectangularSurface)
        {
            UndoMoveVertexRectangularSurface newEdit = (UndoMoveVertexRectangularSurface) anEdit;
            if (this.surface == newEdit.getSurface())
            {
                this.newRect = newEdit.getNewRect();
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
        return "Remodelisation de surface rectangulaire";
    }

    @Override
    public String getUndoPresentationName()
    {
        return "Annuler la remodelisation d'une surface rectangulaire";
    }

    @Override
    public String getRedoPresentationName()
    {
        return "Refaire la remodelisation d'une surface rectangulaire";
    }
}
