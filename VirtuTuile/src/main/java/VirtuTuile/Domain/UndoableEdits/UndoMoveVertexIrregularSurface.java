package VirtuTuile.Domain.UndoableEdits;

import VirtuTuile.Domain.IrregularSurface;
import java.awt.geom.Path2D;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

public class UndoMoveVertexIrregularSurface implements javax.swing.undo.UndoableEdit
{
    private final Path2D.Double oldPath;
    private Path2D.Double newPath;
    private final IrregularSurface surface;
    
    public UndoMoveVertexIrregularSurface(Path2D.Double oldPath, Path2D.Double newPath, IrregularSurface surface)
    {
        this.oldPath = oldPath;
        this.newPath = newPath;
        this.surface = surface;
    }
    
    public IrregularSurface getSurface()
    {
        return surface;
    }
    
    public Path2D.Double getNewArea()
    {
        return newPath;
    }
    
    @Override
    public void undo() throws CannotUndoException
    {
        surface.reset();
        surface.append(oldPath, false);
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
        surface.reset();
        surface.append(newPath, false);
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
        if (anEdit instanceof UndoMoveVertexIrregularSurface)
        {
            UndoMoveVertexIrregularSurface newEdit = (UndoMoveVertexIrregularSurface) anEdit;
            if (this.surface == newEdit.getSurface())
            {
                this.newPath = newEdit.getNewArea();
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
        return "Remodelisation de surface irrégulière";
    }

    @Override
    public String getUndoPresentationName()
    {
        return "Annuler la remodelisation d'une surface irrégulière";
    }

    @Override
    public String getRedoPresentationName()
    {
        return "Refaire la remodelisation d'une surface irrégulière";
    }
}
