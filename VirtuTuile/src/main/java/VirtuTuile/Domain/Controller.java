package VirtuTuile.Domain;

import java.awt.Color;
import java.util.ArrayList;
import java.awt.Point;
import java.awt.Rectangle;

/**
 * @class definissant le controlleur de Larman, faisant le pont entre le GUI et le logiciel
 * @author 
 */
public class Controller
{
    public Project project;
    
    /**
     * Constructeur.
     */
    public Controller()
    {
        project = new Project("TEST_PROJECT");
    }

    /**
     * Va chercher le projet courant.
     * @return le projet courant.
     */
    public Project getCurrentProject()
    {
        return project;
    }

    /**
     * Set le projet courant.
     * @param selectedProject : le projet courant a preciser.
     */
    public void setCurrentProject(Project selectedProject)
    {
        this.project = selectedProject;
    }

    /**
     * Fait une requête au projet pour la liste des surfaces.
     * @return la liste des surfaces du projet.
     */
    public ArrayList<Surface> getSurfaces()
    {
        return project.getSurfaces();
    }
    
    /**
     * Cree une surface rectangulaire.
     * @param rectangle la forme du rectangle.
     * @return true si la création à réussie, false sinon.
     */
    public boolean addRectangularSurface(Rectangle rectangle)
    {
        return project.addRectangularSurface(rectangle);
    }
    
    /**
     * Crée une nouvelle surface irreguliere.
     * @param points le tableau des points de la surface
     */
    public void addIrregularSurface(java.util.ArrayList<Point> points)
    {
        // TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    /**
     * Colle deux surfaces sans les fusionner.
     * @param 
     */
    public void glueSelectedSurfaces()
    {
        // TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    /**
     * Déplace une surface.
     * @param delta : déplacement x et y.
     * @param surface : la surface qui doit être déplacée.
     */
    public void moveSurface(Point delta, Surface surface)
    {
        project.moveSurface(delta, surface);
    }
    
    /**
     * Set le paramètre x d'une surface rectangulaire.
     * @param x : le nouveau paramètre x.
     * @param surface : la surface qui doit être modifiée.
     * @return : true si réussi, false sinon.
     */
    public boolean setRectangleX(int x, RectangularSurface surface)
    {
        return project.setRectangleX(x, surface);
    }

    /**
     * Set le paramètre y d'une surface rectangulaire.
     * @param y : le nouveau paramètre y.
     * @param surface : la surface qui doit être modifiée.
     * @return : true si réussi, false sinon.
     */
    public boolean setRectangleY(int y, RectangularSurface surface)
    {
        return project.setRectangleY(y, surface);
    }

    /**
     * Set le paramètre width d'une surface rectangulaire.
     * @param width : le nouveau paramètre width.
     * @param surface : la surface qui doit être modifiée.
     * @return : true si réussi, false sinon.
     */
    public boolean setRectangularSurfaceWidth(int width, RectangularSurface surface)
    {
        return project.setRectangularSurfaceWidth(width, surface);
    }

    /**
     * Set le paramètre height d'une surface rectangulaire.
     * @param height : le nouveau paramètre height.
     * @param surface : la surface qui doit être modifiée.
     * @return : true si réussi, false sinon.
     */
    public boolean setRectangularSurfaceHeight(int height, RectangularSurface surface)
    {
        return project.setRectangularSurfaceHeight(height, surface);
    }
    
    /**
     * Selectionne la surface qui contient le point.
     * @param point : le point qui doit être à l'intérieur de la surface.
     * @return la surface sélectionnée, peut être null.
     */
    public Surface selectSurface(Point point)
    {
        return project.selectSurface(point);
    }
    
    /**
     * Retourne la surface sélectionnée. Peut être null.
     * @return la surface sélectionnée, si une surface est sélectionnée, null sinon.
     */
    public Surface getSelectedSurface()
    {
        return project.getSelectedSurface();
    }
    
     /**
     * Supprime une surface.
     */
    public void deleteSelectedSurface()
    {
        project.deleteSelectedSurface();
    }

    /**
     * Ajoute un type de tuile dans la liste des tuiles disponibles.
     * @param width : la largeur de la tuile.
     * @param height : la hauteur de la tuile.
     * @param color : la couleur de la tuile.
     */
    public void addTileType(int width, int height, Color color[])
    {
        // TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }
       
    /**
     * Bouge le revetement (motif) de la surface dans l'interface.
     * @param delta : le point sélectionné par la souris.
     */
    public void MoveSelectedCovering(Point delta)
    {
        // TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
