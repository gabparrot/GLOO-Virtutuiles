package VirtuTuile.Domain.UndoableEdits;

import VirtuTuile.Domain.RectangularSurface;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

public class UndoMoveVertexRectangularSurface implements javax.swing.undo.UndoableEdit
{
    private final Rectangle2D oldRect;
    private Rectangle2D newRect;
    private final RectangularSurface surface;
    private final Point2D.Double oldPoint;
    private Point2D.Double newPoint;
    
    public UndoMoveVertexRectangularSurface(
            Rectangle2D oldRect, Rectangle2D newRect, RectangularSurface surface,
            Point2D.Double oldPoint, Point2D.Double newPoint)
    {
        this.oldRect = oldRect;
        this.newRect = newRect;
        this.surface = surface;
        this.oldPoint = oldPoint;
        this.newPoint = newPoint;
    }
    
    public RectangularSurface getSurface()
    {
        return surface;
    }
    
    public Rectangle2D getNewRect()
    {
        return newRect;
    }
    
    public Point2D.Double getOldPoint()
    {
        return oldPoint;
    }
    
    public Point2D.Double getNewPoint()
    {
        return newPoint;
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
            if (this.surface == newEdit.getSurface() && this.newPoint.equals(newEdit.getOldPoint()))
            {
                this.newPoint = newEdit.getNewPoint();
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
