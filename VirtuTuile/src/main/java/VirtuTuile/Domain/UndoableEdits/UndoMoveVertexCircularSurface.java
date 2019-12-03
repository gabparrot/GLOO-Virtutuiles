package VirtuTuile.Domain.UndoableEdits;

import VirtuTuile.Domain.CircularSurface;
import java.awt.geom.Path2D;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

public class UndoMoveVertexCircularSurface implements javax.swing.undo.UndoableEdit
{
    private final Path2D.Double oldPath;
    private Path2D.Double newPath;
    private final CircularSurface surface;
    
    public UndoMoveVertexCircularSurface(Path2D.Double oldPath, Path2D.Double newPath, CircularSurface surface)
    {
        this.oldPath = oldPath;
        this.newPath = newPath;
        this.surface = surface;
    }
    
    public CircularSurface getSurface()
    {
        return surface;
    }
    
    public Path2D.Double getNewPath()
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
        if (anEdit instanceof UndoMoveVertexCircularSurface)
        {
            UndoMoveVertexCircularSurface newEdit = (UndoMoveVertexCircularSurface) anEdit;
            if (this.surface == newEdit.getSurface())
            {
                this.newPath = newEdit.getNewPath();
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
        return "Remodelisation de surface circulaire";
    }

    @Override
    public String getUndoPresentationName()
    {
        return "Annuler la remodelisation d'une surface circulaire";
    }

    @Override
    public String getRedoPresentationName()
    {
        return "Refaire la remodelisation d'une surface circulaire";
    }
}
