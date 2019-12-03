package VirtuTuile.Domain.UndoableEdits;

import VirtuTuile.Domain.CombinedSurface;
import java.awt.geom.Path2D;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

public class UndoMoveVertexCombinedSurface implements javax.swing.undo.UndoableEdit
{
    private final Path2D.Double oldArea;
    private Path2D.Double newArea;
    private final Path2D.Double oldUncoveredArea;
    private Path2D.Double newUncoveredArea;
    private final CombinedSurface surface;
    
    public UndoMoveVertexCombinedSurface(
            Path2D.Double oldArea, Path2D.Double newArea,
            Path2D.Double oldUncoveredArea, Path2D.Double newUncoveredArea, CombinedSurface surface)
    {
        this.oldArea = oldArea;
        this.newArea = newArea;
        this.oldUncoveredArea = oldUncoveredArea;
        this.newUncoveredArea = newUncoveredArea;
        this.surface = surface;
    }
    
    public CombinedSurface getSurface()
    {
        return surface;
    }
    
    public Path2D.Double getNewArea()
    {
        return newArea;
    }
    
    public Path2D.Double getNewUncoveredArea()
    {
        return newUncoveredArea;
    }
    
    @Override
    public void undo() throws CannotUndoException
    {
        surface.reset();
        surface.append(oldArea, false);
        surface.setUncoveredPath(oldUncoveredArea);
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
        surface.append(newArea, false);
        surface.setUncoveredPath(newUncoveredArea);
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
        if (anEdit instanceof UndoMoveVertexCombinedSurface)
        {
            UndoMoveVertexCombinedSurface newEdit = (UndoMoveVertexCombinedSurface) anEdit;
            if (this.surface == newEdit.getSurface())
            {
                this.newArea = newEdit.getNewArea();
                this.newUncoveredArea = newEdit.getNewUncoveredArea();
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
        return "Remodelisation de surface combinée";
    }

    @Override
    public String getUndoPresentationName()
    {
        return "Annuler la remodelisation d'une surface combinée";
    }

    @Override
    public String getRedoPresentationName()
    {
        return "Refaire la remodelisation d'une surface combinée";
    }
}
