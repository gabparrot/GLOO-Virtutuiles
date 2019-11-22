package VirtuTuile.Domain;

import java.awt.Color;
import java.awt.Shape;
import java.util.ArrayList;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Area;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Classe définissant le projet en cours
 * @author gabparrot
 */
public class Project
{
    private ArrayList<Surface> surfaces = new ArrayList<>();
    private Surface selectedSurface = null;
    private ArrayList<TileType> tileTypes = new ArrayList<>();
    private final double[][] TRANSLATION_DIRECTIONS = {{0, -0.1}, {0, 0.1}, {-0.1, 0}, {0.1, 0}};
    
    /**
     * Désélectionne la surface sélectionnée.
     */
    public void unselect()
    {
        selectedSurface = null;
    }

    /**
     * Getter pour la liste des surfaces.
     * @return la liste des surfaces du projet.
     */
    public ArrayList<Surface> getSurfaces()
    {
        return surfaces;
    }

    /**
     * Crée une nouvelle surface rectangulaire.
     * @param rectangle la forme du rectangle.
     * @return true si la création à réussie, false sinon.
     */
    public boolean addRectangularSurface(Rectangle2D.Double rectangle)
    {
        boolean noConflict = conflictCheck(rectangle);
        if (noConflict)
        {
            surfaces.add(new RectangularSurface(rectangle, false, new Color(113, 148, 191)));
        }
        return noConflict;
    }

    
    /**
     * Ajoute une surface, utilisée par le UndoManager.
     * @param surface : la surface qui doit être ajoutée.
     */
    public void addSurface(Surface surface)
    {
        surfaces.add(surface);
        surface.coverSurface();
    }
    
    /**
     * Vérifie s'il y a un conflit avec une nouvelle surface.
     * @param shape : la forme de la surface à vérifier.
     * @return true s'il n'y a pas de conflit, false s'il y a conflit.
     */
    public boolean conflictCheck(Shape shape)
    {
        for (Surface surface : surfaces)
        {
            if (surface == shape) continue;
            Area area = new Area(surface);
            area.intersect(new Area(shape));
            
            // Premier check:
            Rectangle2D intersection = area.getBounds2D();
            if (intersection.getWidth() > 0.1 && intersection.getHeight() > 0.1)
            {
                // Approximation:
                AffineTransform translation = new AffineTransform();
                translation.translate(10000, 10000);
                area.transform(translation);
                translation = new AffineTransform();
                translation.translate(-10000, -10000);
                area.transform(translation);
                
                // Deuxième check, après approximation:
                intersection = area.getBounds2D();
                if (intersection.getWidth() > 0.1 && intersection.getHeight() > 0.1)
                {
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * Fusionne deux surfaces.
     * @param s1 : la première surface à fusionner.
     * @param s2 : la deuxième surface à fusionner.
     * @return la nouvelle surface résultante de la fusion.
     */
    public CombinedSurface mergeSurfaces(Surface s1, Surface s2)
    {
        if (!surfacesAreConnected(s1, s2)) return null;
        
        // Combine les surfaces:
        Surface biggestSurface = s1.getArea() > s2.getArea() ? s1 : s2;
        Color mergedColor = biggestSurface.getColor();
        Covering mergedCovering = biggestSurface.getCovering();
        CombinedSurface newSurface = new CombinedSurface(s1, s2, false, mergedColor, mergedCovering);
        newSurface.approximateSurface();
        surfaces.add(newSurface);
        surfaces.remove(s1);
        surfaces.remove(s2);
        return newSurface;
    }
    
    /**
     * Retourne true si deux surfaces sont connexes, false sinon.
     * @param s1 : la première surface.
     * @param s2 : la deuxième surface.
     * @return  true si les deux surfaces sont connexes, false sinon.
     */
    private boolean surfacesAreConnected(Surface s1, Surface s2)
    {
        AffineTransform translation;
        Area intersectionArea;
        for (int i = 0; i < 4; i++)
        {
            translation = new AffineTransform();
            translation.translate(TRANSLATION_DIRECTIONS[i][0], TRANSLATION_DIRECTIONS[i][1]);
            intersectionArea = new Area(s1);
            intersectionArea.transform(translation);
            intersectionArea.intersect(new Area(s2));
            if (!intersectionArea.isEmpty())
            {
                Rectangle2D bounds = intersectionArea.getBounds2D();
                if (bounds.getWidth() >= 1 || bounds.getHeight() >= 1)
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Sélectionne une surface.
     * @param point : le point qui doit être à l'intérieur de la surface.
     */
    public void selectSurface(Point2D.Double point)
    {
        selectedSurface = getSurfaceAtPoint(point);
    }
    
    public Surface getSurfaceAtPoint(Point2D.Double point)
    {
        for (Surface surface : surfaces)
            {
                if (surface.contains(point))
                {
                    return surface;
                }
            }
        return null;
    }
    
    /**
     * Retourne la largeur et la hauteur d'une tuile.
     * @param point : le point qui se situe à l'intérieur de la tuile.
     * @return la largeur et la hauteur d'une tuile.
     */
    public Point2D.Double getTileAtPoint(Point2D.Double point)
    {        
        Surface surface = getSurfaceAtPoint(point);
        if (surface != null && !surface.isHole())
        {
            for (Area tile : surface.getCovering().getTiles())
            {
                if (tile.contains(point))
                {
                    Rectangle2D tileBounds = tile.getBounds2D();
                    return new Point2D.Double(tileBounds.getWidth(), tileBounds.getHeight());
                }
            }
        }
        return new Point2D.Double(0, 0);
    }
            
    
    /**
     * Trouve la surface qui contient le point (sans la sélectionner).
     * @param point : le point qui doit être à l'intérieur de la surface.
     * @return la surface trouvée, peut être null.
     */
    public Surface findSurface(Point2D.Double point)
    {
        for (Surface surface : surfaces)
        {
            if (surface.contains(point))
            {
                return surface;
            }
        }
        return null;
    }
    
    /**
     * Supprime la surface selectionnée.
     */
    public void removeSelectedSurface()
    {
        if (selectedSurface != null) surfaces.remove(selectedSurface);
    }
    
    /**
     * Supprime une surface de la liste.
     * @param surface : la surface qui doit être supprimée.
     */
    public void removeSurface(Surface surface)
    {
        surfaces.remove(surface);
    }
    
    /**
     * Retourne la surface sélectionnée, peut être null.
     * @return la surface sélectionnée, si une surface est sélectionnée, null sinon.
     */
    public Surface getSelectedSurface()
    {
        return selectedSurface;
    }
    
    /**
     * Retourne true si une surface est sélectionnée, false sinon.
     * @return true si une surface est sélectionnée, false sinon.
     */
    public boolean surfaceIsSelected()
    {
        return selectedSurface != null;
    }
    
    /**
     * Crée un nouveau type de tuile.
     * @param width : la largeur.
     * @param height : la hauteur.
     * @param name : le nom.
     * @param nbPerBox : le nombre de tuiles par boite.
     * @param color : la couleur.
     */
    public void createTileType(double width, double height, String name,
            int nbPerBox, Color color)
    {
        tileTypes.add(new TileType(width, height, name, nbPerBox, color));
    }
    
    
    /**
     * Retourne un tableau avec les noms des types de tuiles.
     * @return les noms des types de tuiles.
     */
    public String[] getTileTypeStrings()
    {
        String[] tileTypeStrings = new String[tileTypes.size()];
        for (int i = 0; i < tileTypes.size(); i++)
        {
            tileTypeStrings[i] = tileTypes.get(i).getName();
        }
        return tileTypeStrings;
    }
    
    /**
     * Change le type de tuile de la surface sélectionnée.
     * @param selectedIndex : l'index du type de tuile dans la liste des types de tuiles.
     */
    public void setTileTypeByIndex(int selectedIndex)
    {
        selectedSurface.getCovering().setTileType(tileTypes.get(selectedIndex));
    }
    
    /**
     * Retourne l'index d'un type de tuile.
     * @param tileType : le type de tuile.
     * @return l'index du type de tuile dans la liste des types de tuiles du projet.
     */
    public int getTileTypeIndex(TileType tileType)
    {
        return tileTypes.indexOf(tileType);
    }
    
    /**
     * Trouve le point le plus à droite et en-bas de toutes les surfaces.
     * @return le point le plus à droite et en-bas de toutes les surfaces. 
     */
    public Point2D.Double getFarthestPoint()
    {
        Point2D.Double point = new Point2D.Double(0, 0);
        for (Surface surface : surfaces)
        {
            Rectangle2D bounds = surface.getBounds2D();
            if (bounds.getX() + bounds.getWidth() > point.x)
            {
                point.x = bounds.getX() + bounds.getWidth();
            }
            if (bounds.getY() + bounds.getHeight() > point.y)
            {
                point.y = bounds.getY() + bounds.getHeight();
            }
        }
        return point;
    }
    
    /**
     * Ajoute les surfaces au fichier de sauvegarde
     * @param file le fichier de sauvegarde du projet en cours
     */
    public void saveSurfacesToFile(File file)
    {
        try
        {
            try (FileOutputStream fileOut = new FileOutputStream(file);
                    ObjectOutputStream out = new ObjectOutputStream(fileOut))
            {
                out.writeObject(surfaces);
                out.writeObject(tileTypes);
            }
        }
        catch(IOException i) { i.printStackTrace(System.out); }
    }
    
    /**
     * Charge les surfaces à partir du fichier de sauvegarde donné
     * @param file Le fichier de sauvegarde représentant le projet en cours
     */
    public void loadSurfacesFromFile(File file)
    {
        try
        {
            try (FileInputStream fileIn = new FileInputStream(file);
                    ObjectInputStream in = new ObjectInputStream(fileIn))
            {
                surfaces = (ArrayList<Surface>) in.readObject();
                tileTypes = (ArrayList<TileType>) in.readObject();
            }
        }
        catch (IOException | ClassNotFoundException i) { i.printStackTrace(System.out); }
    }
}
