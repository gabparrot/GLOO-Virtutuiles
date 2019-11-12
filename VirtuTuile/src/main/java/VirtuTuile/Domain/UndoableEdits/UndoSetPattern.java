package VirtuTuile.Domain.UndoableEdits;

import VirtuTuile.Domain.Covering;
import VirtuTuile.Domain.Pattern;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

public class UndoSetPattern implements javax.swing.undo.UndoableEdit
{
    private final Pattern oldPattern;
    private final Pattern newPattern;
    private final Covering covering;
    
    public UndoSetPattern(Pattern oldPattern, Pattern newPattern, Covering covering)
    {
        this.oldPattern = oldPattern;
        this.newPattern = newPattern;
        this.covering = covering;
    }
    
    @Override
    public void undo() throws CannotUndoException
    {
        covering.setPattern(oldPattern);
    }

    @Override
    public boolean canUndo()
    {
        return true;
    }

    @Override
    public void redo() throws CannotRedoException
    {
        covering.setPattern(newPattern);
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
        return "Changement du motif";
    }

    @Override
    public String getUndoPresentationName()
    {
        return "Annuler le changement du motif";
    }

    @Override
    public String getRedoPresentationName()
    {
        return "Refaire le changement du motif";
    }
}
