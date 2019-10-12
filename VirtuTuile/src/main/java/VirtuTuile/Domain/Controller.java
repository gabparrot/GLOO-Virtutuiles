/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VirtuTuile.Domain;

//import java.awt.Point;
import java.awt.Color;

// Énumération des différents motifs de revêtement.
enum Pattern { A, B, C, D, E }

/**
 * @class definissant le controlleur de Larman, faisant le pont entre le GUI et le logiciel
 * @author 
 */
public class Controller
{
    // TODO vérifier si l'object project doit bien être ici et pas dans mainWindow, tout en suivant Larman
    public Project project;
    
    public Controller(){}

    public Project getCurrentProject()
    {
        return project;
    }

    public void setCurrentProject(Project selectedProject)
    {
        this.project = selectedProject;
    }

    // TODO types de patterns
    public void addRectangularSurface(int coordX, int coordY, int width, int height)
    {
        // TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public void addIrregularSurface(java.util.ArrayList<java.awt.Point> points)
    {
        // TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public void glueSelectedSurfaces()
    {
        // TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public void switchSelectionStatus(double posXMetric, double posYMetric)
    {
        project.switchSelectionStatus(posXMetric, posYMetric);
    }
    
    public void moveSelectedSurface(java.awt.Point point)
    {
        // TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public void deleteSelectedSurface()
    {
        // TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void addTileType(int width, int height, Color color[])
    {
        // TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public void setWidthSelectedRectangle(int width)
    {
        // TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public void setHeightSelectedRectangle(int height)
    {
        // TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public void MoveSelectedCovering(java.awt.Point delta)
    {
        // TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
