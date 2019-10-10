package VirtuTuile.Domain;

import java.util.Set;
import java.util.HashSet;
import java.awt.Shape;
import java.util.Map;
import java.util.HashMap;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * @class definissant le projet en cours
 * @author gabparrot
 */
public class Project
{
    private String projectName;
    // TODO: Pas sûr du type ici ou ce que c'est censé être donc mis un Set, à checker
    private Set<String> history = new HashSet<String>();
    private Shape selectedTile = null;
    // Renommé "quantities" ici car flou. Pas sûr du type.
    private Map<TileType, Integer> qtyPerTileType = new HashMap<>();
    //private List<Surface> surfaces = new ArrayList<>();
    
    /**
     * 
     * @param projectName 
     */
    public Project(boolean isMetric, String projectName)
    {
        this.projectName = projectName;
    }
    
    /**
     * Annuler la dernière action
     */
    
    public boolean conflictCheck()
    {
        
    }
    
    public void undo()
    {
        // TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    /**
     * Refaire l'action annulée
     */
    public void redo()
    {
        // TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    /**
     * // TODO docs
     * @param point 
     */
    public void switchSelectionStatus(Point point)
    {
        // TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    /**
     * // TODO docs
     * @param delta 
     */
    public void moveSelectedSurface(Point delta)
    {
        // TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }
   
    // Getters et Setters
    public String getProjectName()
    {
        return projectName;
    }

    public void setProjectName(String projectName)
    {
        this.projectName = projectName;
    }

    public Set<String> getHistory()
    {
        return history;
    }

    public void setHistory(Set<String> history)
    {
        this.history = history;
    }

    public Shape getSelectedTile()
    {
        return selectedTile;
    }

    public void setSelectedTile(Shape selectedTile)
    {
        this.selectedTile = selectedTile;
    }

    public Map<TileType, Integer> getQtyPerTileType()
    {
        return qtyPerTileType;
    }

    public void setQtyPerTileType(Map<TileType, Integer> qtyPerTileType)
    {
        this.qtyPerTileType = qtyPerTileType;
    }

    
}
