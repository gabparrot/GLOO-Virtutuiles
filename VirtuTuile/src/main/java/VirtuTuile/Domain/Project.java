package VirtuTuile.Domain;

import VirtuTuile.Infrastructure.Utilities;
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
 * @class définissant le projet en cours
 * @author gabparrot
 */
public class Project
{
    private ArrayList<Surface> surfaces = new ArrayList<>();
    private Surface selectedSurface = null;
    private final ArrayList<TileType> tileTypes = new ArrayList<>();
    private final double[][] TRANSLATION_DIRECTIONS = {{0, -0.1}, {0, 0.1}, {-0.1, 0}, {0.1, 0}};
    
    // TO-DO: delete constructor
    public Project()
    {
        tileTypes.add(Utilities.DEFAULT_TILE_1);
        tileTypes.add(Utilities.DEFAULT_TILE_2);
    }
    
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
            
            Rectangle2D intersection = area.getBounds2D();
            
            if (intersection.getWidth() > 0.1 && intersection.getHeight() > 0.1)
            {
                return false;
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
    
    //=== Déplacement de surface ===//
    
    //TODO work in progress
    public boolean moveSurfaceToPoint(Point2D.Double newPos)
    {
        boolean success = false;
        
        /**
         * Faire pushHoriz
         * Faire pushVert
         * Faire setX
         * Faire setY
         * Finir moveSurfaceToPoint
         */
        
        
        return success;
        
    }
    
    //TODO WORK IN PROGRESS
    private void pushHorizontally(double newX, double oldX)
    {
        double surroundingBounds[] = getSurroundingBounds();
        
        setX(newX);
        
        if (!conflictCheck(selectedSurface))
        {
            // Déplacement à droite
            if (newX > oldX)
            {
                setX(Math.min(newX, surroundingBounds[2] - selectedSurface.getWidth()));
            }
            // Déplacement à gauche
            else
            {
                setX(Math.max(newX, surroundingBounds[0]));
            }
        }
    }
    
    //TODO WORK IN PROGRESS
    private void setX(double newX)
    {
        
    }
    
    //TODO WORK IN PROGRESS
    public double[] getSurroundingBounds()
    {
        double surroundingBounds[] = {0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE};
        Area totalArea = new Area();
        Rectangle2D selectedSurfaceBounds = selectedSurface.getBounds2D();
        
        for (Surface surface : surfaces)
        {
            if (surface != selectedSurface) totalArea.add(new Area(surface));
        }
        
        
        // LEFT
        Area leftArea = new Area(totalArea);
        leftArea.intersect(new Area(new Rectangle2D.Double(0, selectedSurfaceBounds.getY(), 
                selectedSurfaceBounds.getX(), selectedSurfaceBounds.getHeight())));
        Rectangle2D leftRect = leftArea.getBounds2D();
        
        if (leftRect.getX() + leftRect.getWidth() > 0)
        {
            surroundingBounds[0] = leftRect.getX() + leftRect.getWidth();
        }
        
        // UP
        Area upArea = new Area(totalArea);
        upArea.intersect(new Area(new Rectangle2D.Double(selectedSurfaceBounds.getX(), 0, 
                selectedSurfaceBounds.getWidth(), selectedSurfaceBounds.getY())));
        Rectangle2D upRect = upArea.getBounds2D();
        
        if (upRect.getY() + upRect.getHeight() > 0)
        {
            surroundingBounds[1] = upRect.getY() + upRect.getHeight();
        }
        
        // RIGHT
        Area rightArea = new Area(totalArea);
        rightArea.intersect(new Area(new Rectangle2D.Double(
                selectedSurfaceBounds.getX() + selectedSurfaceBounds.getWidth(), selectedSurfaceBounds.getY(), 
                Integer.MAX_VALUE, selectedSurfaceBounds.getHeight())));
        Rectangle2D rightRect = rightArea.getBounds2D();
        
        if (!rightArea.isEmpty())
        {
            surroundingBounds[2] = rightRect.getX();
        }
        
        // DOWN
        Area downArea = new Area(totalArea);
        downArea.intersect(new Area(new Rectangle2D.Double(
                selectedSurfaceBounds.getX(), selectedSurfaceBounds.getY() + selectedSurfaceBounds.getHeight(), 
                selectedSurfaceBounds.getWidth(), Integer.MAX_VALUE)));
        Rectangle2D downRect = downArea.getBounds2D();
        
        if (!downArea.isEmpty())
        {
            surroundingBounds[3] = downRect.getY();
        }
        return surroundingBounds;
    }
    
    public String[] getTileAtPoint(Point2D.Double point)
    {
        String tileName = "";
        Double tileWidth = 0.0;
        Double tileHeight = 0.0;
        Double tileXPos = 0.0;
        Double tileYPos = 0.0;
        
        String[] tileInfos = new String[5];
        
        Surface surface = getSurfaceAtPoint(point);
        
        if (surface != null && !surface.isHole())
        {
            Covering covering = surface.getCovering();
            for (Area tile : covering.getTiles())
            {
                if (tile.contains(point))
                {
                    Rectangle2D tileBounds = tile.getBounds2D();
                    TileType tileType = covering.getTileType();
                    
                    tileName = tileType.getName();
                    tileWidth = tileBounds.getWidth();
                    tileHeight = tileBounds.getHeight();
                    tileXPos = tileBounds.getX();
                    tileYPos = tileBounds.getY();
                    break;
                }
            }
        }
        
        tileInfos[0] = tileName;
        tileInfos[1] = tileWidth.toString();
        tileInfos[2] = tileHeight.toString();
        tileInfos[3] = tileXPos.toString();
        tileInfos[4] = tileYPos.toString();
        
        return tileInfos;
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
            }
        }
        catch (IOException | ClassNotFoundException i) { i.printStackTrace(System.out); }
    }
}
