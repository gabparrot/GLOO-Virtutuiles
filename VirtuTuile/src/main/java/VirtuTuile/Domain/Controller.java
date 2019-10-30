package VirtuTuile.Domain;

import java.awt.Color;
import java.util.ArrayList;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

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
     * Désélectionne la surface sélectionnée.
     */
    public void unselect()
    {
        project.unselect();
    }
    
    /**
     * Retourne les bornes extérieures d'une surface, qui décrivent jusqu'à quel point une surface
     * peut être déplacée dans les quatre directions.
     * @param surface : la surface en question.
     * @return les bornes dans un tableau [gauche, en-haut, droite, en-bas]
     */
    public double[] getSurroundingBounds(Surface surface)
    {
        return project.getSurroundingBounds(surface);
    }
    
    /**
     * Crée une surface rectangulaire.
     * @param rectangle la forme du rectangle.
     * @return true si la création à réussie, false sinon.
     */
    public boolean addRectangularSurface(Rectangle2D.Double rectangle)
    {
        return project.addRectangularSurface(rectangle);
    }
    
    /**
     * Crée une nouvelle surface irrégulière.
     */
    public void addIrregularSurface()
    {
        // TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    /**
     * Colle deux surfaces sans les fusionner.
     */
    public void glueSelectedSurfaces()
    {
        // TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public boolean mergeSelectedSurfaces()
    {
        return project.mergeSelectedSurfaces();
    }
    
    /**
     * Déplace une surface à une nouvelle position.
     * @param newPos : nouvelle position.
     * @param surface : la surface qui doit être déplacée.
     */
    public void moveSurfaceToPoint(Point2D newPos, Surface surface)
    {
        project.moveSurfaceToPoint(newPos, surface);
    }
    
    /**
     * Set le paramètre x d'une surface rectangulaire.
     * @param x : le nouveau paramètre x.
     * @param surface : la surface qui doit être modifiée.
     * @return : true si réussi, false sinon.
     */
    public boolean setRectangularSurfaceX(double x, RectangularSurface surface)
    {
        return project.setRectangularSurfaceX(x, surface);
    }

    /**
     * Set le paramètre y d'une surface rectangulaire.
     * @param y : le nouveau paramètre y.
     * @param surface : la surface qui doit être modifiée.
     * @return : true si réussi, false sinon.
     */
    public boolean setRectangularSurfaceY(double y, RectangularSurface surface)
    {
        return project.setRectangularSurfaceY(y, surface);
    }

    /**
     * Set le paramètre width d'une surface rectangulaire.
     * @param width : le nouveau paramètre width.
     * @param surface : la surface qui doit être modifiée.
     * @return : true si réussi, false sinon.
     */
    public boolean setRectangularSurfaceWidth(double width, RectangularSurface surface)
    {
        return project.setRectangularSurfaceWidth(width, surface);
    }

    /**
     * Set le paramètre height d'une surface rectangulaire.
     * @param height : le nouveau paramètre height.
     * @param surface : la surface qui doit être modifiée.
     * @return : true si réussi, false sinon.
     */
    public boolean setRectangularSurfaceHeight(double height, RectangularSurface surface)
    {
        return project.setRectangularSurfaceHeight(height, surface);
    }
    
    /**
     * Sélectionne la surface qui contient le point.
     * @param point : le point qui doit être à l'intérieur de la surface.
     * @return la surface sélectionnée, peut être null.
     */
    public Surface selectSurface(Point2D point)
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
    public void addTileType(double width, double height, Color color[])
    {
        // TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }
       
    /**
     * Bouge le revetement (motif) de la surface dans l'interface.
     * @param delta : le point sélectionné par la souris.
     */
    public void MoveSelectedCovering(Point2D delta)
    {
        // TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    /**
     * Trouve le point le plus à droite et en-bas de toutes les surfaces.
     * @return le point le plus à droite et en-bas de toutes les surfaces. 
     */
    public Point2D.Double getFarthestPoint()
    {
        return project.getFarthestPoint();
    }
}
