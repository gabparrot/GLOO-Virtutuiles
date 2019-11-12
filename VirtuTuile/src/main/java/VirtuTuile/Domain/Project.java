package VirtuTuile.Domain;

import VirtuTuile.Infrastructure.Utilities;
import java.awt.Color;
import java.awt.Shape;
import java.util.Map;
import java.util.HashMap;
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
 * @class definissant le projet en cours
 * @author gabparrot
 */
public class Project
{
    private Map<TileType, Integer> qtyPerTileType = new HashMap<>();
    private ArrayList<Surface> surfaces = new ArrayList<>();
    private Surface selectedSurface = null;
    private final ArrayList<TileType> tileTypes = new ArrayList<>();
    
    Project()
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
     * Ajoûte une surface, utilisée par le UndoManager.
     * @param surface : la surface qui doit être ajoutée.
     */
    public void addSurface(Surface surface)
    {
        surfaces.add(surface);
    }
    
    /**
     * Vérifie s'il y a un conflit avec une nouvelle surface.
     * @param shape : la forme de la surface à vérifier.
     * @return true s'il n'y a pas de conflit, false s'il y a conflit.
     */
    private boolean conflictCheck(Shape shape)
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
        // Vérifie si la surface combinée est connexe:
        Area totalArea = new Area(s1);
        totalArea.add(new Area(s2));
        if (!totalArea.isSingular()) return null;
        
        // Combine les surfaces:
        Surface biggestSurface = s1.getArea() > s2.getArea() ? s1 : s2;
        Color mergedColor = biggestSurface.getColor();
        CombinedSurface newSurface = new CombinedSurface(s1, s2, false, mergedColor);
        surfaces.add(newSurface);
        surfaces.remove(s1);
        surfaces.remove(s2);
        return newSurface;
    }
   
    /**
     * Déplace une surface à une nouvelle position.
     * @param newPos : nouvelle position.
     * @param surface : la surface qui doit être déplacée.
     */
    public void moveSurfaceToPoint(Point2D.Double newPos, Surface surface)
    {
        if (newPos.x < 0) newPos.x = 0;
        if (newPos.y < 0) newPos.y = 0;
        
        if (surface instanceof RectangularSurface)
        {
            setSurfaceXY(newPos.getX(), newPos.getY(), (RectangularSurface) surface);
        }
        else if (surface instanceof IrregularSurface)
        {
            setSurfaceXY(newPos.getX(), newPos.getY(), (IrregularSurface) surface);
        }
        else if (surface instanceof CombinedSurface)
        {
            setSurfaceXY(newPos.getX(), newPos.getY(), (CombinedSurface) surface);
        }   
    }

    private void setSurfaceXY(double x, double y, RectangularSurface surface)
    {   
        double oldX = surface.x;
        double oldY = surface.y;
        
        surface.x = x;
        surface.y = y;
        
        // Cas: aucun conflit.
        if (conflictCheck(surface))
        {
            return;
        }
        else
        {
            surface.x = oldX;
            surface.y = oldY;
        }
        
        double surroundingBounds[] = getSurroundingBounds(surface);
        surface.x = x;
        
        // Cas: conflit horizontal.
        if (!conflictCheck(surface))
        {
            // Déplacement à droite
            if (x > oldX)
            {
                surface.x = Math.min(x, surroundingBounds[2] - surface.width);
            }
            // Déplacement à gauche
            else
            {
                surface.x = Math.max(x, surroundingBounds[0]);
            }
        }
        
        surroundingBounds = getSurroundingBounds(surface);
        surface.y = y;
        
        // Cas: conflit vertical.
        if (!conflictCheck(surface))
        {
            // Déplacement vers le bas
            if (y > oldY)
            {
                surface.y = Math.min(y, surroundingBounds[3] - surface.height);
            }
            // Déplacement vers le haut
            else
            {
                surface.y = Math.max(y, surroundingBounds[1]);
            }
        }
    }

    private void setSurfaceXY(double x, double y, IrregularSurface surface)
    {
        double deltaX = x - surface.getBounds2D().getX();
        double deltaY = y - surface.getBounds2D().getY();
        AffineTransform translationTransform = new AffineTransform();
        translationTransform.translate(deltaX, deltaY);
        surface.transform(translationTransform);
        if (!conflictCheck(surface))
        {
            translationTransform = new AffineTransform();
            translationTransform.translate(-deltaX, -deltaY);
            surface.transform(translationTransform);
        }
    }

    private void setSurfaceXY(double x, double y, CombinedSurface surface)
    {
        double deltaX = x - surface.getBounds2D().getX();
        double deltaY = y - surface.getBounds2D().getY();
        AffineTransform translationTransform = new AffineTransform();
        translationTransform.translate(deltaX, deltaY);
        surface.transform(translationTransform);
        if (!conflictCheck(surface))
        {
            translationTransform = new AffineTransform();
            translationTransform.translate(-deltaX, -deltaY);
            surface.transform(translationTransform);
        }
        else
        {
            surface.getUncoveredArea().transform(translationTransform);
        }
    }

    /**
     * Retourne les bornes extérieures d'une surface, qui décrivent jusqu'à quel point une surface
     * peut être déplacée dans les quatre directions.
     * @param inputSurface : la surface en question.
     * @return les bornes dans un tableau [gauche, en-haut, droite, en-bas]
     */
    public double[] getSurroundingBounds(Surface inputSurface)
    {
        Rectangle2D b = inputSurface.getBounds2D();
        double x = b.getX();
        double y = b.getY();
        double w = b.getWidth();
        double h = b.getHeight();
        double surroundingBounds[] = {0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE};
        Area totalArea = new Area();
        for (Surface surface : surfaces)
        {
            if (surface != inputSurface) totalArea.add(new Area(surface));
        }
        // LEFT
        Area leftArea = new Area(totalArea);
        leftArea.intersect(new Area(new Rectangle2D.Double(0, y, x, h)));
        Rectangle2D leftRect = leftArea.getBounds2D();
        if (leftRect.getX() + leftRect.getWidth() > 0)
        {
            surroundingBounds[0] = leftRect.getX() + leftRect.getWidth();
        }
        
        // UP
        Area upArea = new Area(totalArea);
        upArea.intersect(new Area(new Rectangle2D.Double(x, 0, w, y)));
        Rectangle2D upRect = upArea.getBounds2D();
        if (upRect.getY() + upRect.getHeight() > 0)
        {
            surroundingBounds[1] = upRect.getY() + upRect.getHeight();
        }
        
        // RIGHT
        Area rightArea = new Area(totalArea);
        rightArea.intersect(new Area(new Rectangle2D.Double(x + w, y, Integer.MAX_VALUE, h)));
        Rectangle2D rightRect = rightArea.getBounds2D();
        if (!rightArea.isEmpty())
        {
            surroundingBounds[2] = rightRect.getX();
        }
        
        // DOWN
        Area downArea = new Area(totalArea);
        downArea.intersect(new Area(new Rectangle2D.Double(x, y + h, w, Integer.MAX_VALUE)));
        Rectangle2D downRect = downArea.getBounds2D();
        if (!downArea.isEmpty())
        {
            surroundingBounds[3] = downRect.getY();
        }
        
        return surroundingBounds;
    }
    
    /**
     * Set le paramètre x d'une surface.
     * @param x : le nouveau paramètre x.
     * @param surface : la surface qui doit être modifiée.
     * @return : true si réussi, false sinon.
     */
    public boolean setSurfaceX(double x, Surface surface)
    {
        if (x < 0) return false;
        
        if (surface instanceof RectangularSurface)
        {
            return setSurfaceX(x, (RectangularSurface) surface);
        }
        else if (surface instanceof CombinedSurface)
        {
            return setSurfaceX(x, (CombinedSurface) surface);
        }
        else
        {
            return setSurfaceX(x, (IrregularSurface) surface);
        }
    }
    
    private boolean setSurfaceX(double x, RectangularSurface surface)
    {
        double oldX = surface.x;
        surface.x = x;
        if (conflictCheck(surface))
        {
            return true;
        }
        else
        {
            surface.x = oldX;
            return false;
        }
    }
    
    private boolean setSurfaceX(double x, CombinedSurface surface)
    {
        Rectangle2D bounds = surface.getBounds2D();
        double deltaX = x - bounds.getX();
        AffineTransform translationTransform = new AffineTransform();
        translationTransform.translate(deltaX, 0);
        surface.transform(translationTransform);
        if (conflictCheck(surface))
        {
            surface.getUncoveredArea().transform(translationTransform);
            return true;
        }
        else
        {
            translationTransform = new AffineTransform();
            translationTransform.translate(-deltaX, 0);
            surface.transform(translationTransform);
            return false;
        }
    }
    
    private boolean setSurfaceX(double x, IrregularSurface surface)
    {
        Rectangle2D bounds = surface.getBounds2D();
        double deltaX = x - bounds.getX();
        AffineTransform translationTransform = new AffineTransform();
        translationTransform.translate(deltaX, 0);
        surface.transform(translationTransform);
        if (conflictCheck(surface))
        {
            return true;
        }
        else
        {
            translationTransform = new AffineTransform();
            translationTransform.translate(-deltaX, 0);
            surface.transform(translationTransform);
            return false;
        }
    }
    
    /**
     * Set le paramètre y d'une surface.
     * @param y : le nouveau paramètre y.
     * @param surface : la surface qui doit être modifiée.
     * @return : true si réussi, false sinon.
     */
    public boolean setSurfaceY(double y, Surface surface)
     {
        if (y < 0) return false;
        
        if (surface instanceof RectangularSurface)
        {
            return setSurfaceY(y, (RectangularSurface) surface);
        }
        else if (surface instanceof CombinedSurface)
        {
            return setSurfaceY(y, (CombinedSurface) surface);
        }
        else
        {
            return setSurfaceY(y, (IrregularSurface) surface);
        }
    }
    
    private boolean setSurfaceY(double y, RectangularSurface surface)
    {
        double oldY = surface.y;
        surface.y = y;
        if (conflictCheck(surface))
        {
            return true;
        }
        else
        {
            surface.y = oldY;
            return false;
        }
    }
    
    private boolean setSurfaceY(double y, CombinedSurface surface)
    {
        Rectangle2D bounds = surface.getBounds2D();
        double deltaY = y - bounds.getY();
        AffineTransform translationTransform = new AffineTransform();
        translationTransform.translate(0, deltaY);
        surface.transform(translationTransform);
        if (conflictCheck(surface))
        {
            surface.getUncoveredArea().transform(translationTransform);
            return true;
        }
        else
        {
            translationTransform = new AffineTransform();
            translationTransform.translate(0, -deltaY);
            surface.transform(translationTransform);
            return false;
        }
    }
    
    private boolean setSurfaceY(double y, IrregularSurface surface)
    {
        Rectangle2D bounds = surface.getBounds2D();
        double deltaY = y - bounds.getY();
        AffineTransform translationTransform = new AffineTransform();
        translationTransform.translate(0, deltaY);
        surface.transform(translationTransform);
        if (conflictCheck(surface))
        {
            return true;
        }
        else
        {
            translationTransform = new AffineTransform();
            translationTransform.translate(0, -deltaY);
            surface.transform(translationTransform);
            return false;
        }
    }
    
    /**
     * Set le paramètre width d'une surface.
     * @param width : le nouveau paramètre width.
     * @param surface : la surface qui doit être modifiée.
     * @return : true si réussi, false sinon.
     */
    public boolean setSurfaceWidth(double width, Surface surface)
    {
        if (width < 100) return false;
        
        if (surface instanceof RectangularSurface)
        {
            return setSurfaceWidth(width, (RectangularSurface) surface);
        }
        else if (surface instanceof CombinedSurface)
        {
            return setSurfaceWidth(width, (CombinedSurface) surface);
        }
        else
        {
            return setSurfaceWidth(width, (IrregularSurface) surface);
        }
    }
    
    private boolean setSurfaceWidth(double width, RectangularSurface surface)
    {
        double oldWidth = surface.width;
        
        surface.width = width;
        
        if (conflictCheck(surface))
        {
            return true;
        }
        else
        {
            surface.width = oldWidth;
            return false;
        }
    }
    
    private boolean setSurfaceWidth(double width, IrregularSurface surface)
    {
        Rectangle2D bounds = surface.getBounds2D();
        double oldWidth = bounds.getWidth();
        double oldX = bounds.getX();
        
        AffineTransform scaleTransform = new AffineTransform();
        scaleTransform.scale(width / oldWidth, 1);
        surface.transform(scaleTransform);
        setSurfaceX(oldX, surface);
        
        if (conflictCheck(surface))
        {
            return true;
        }
        else
        {
            scaleTransform = new AffineTransform();
            scaleTransform.scale(oldWidth / width, 1);
            surface.transform(scaleTransform);
            setSurfaceX(oldX, surface);
            return false;
        }
    }
    
    private boolean setSurfaceWidth(double width, CombinedSurface surface)
    {
        Area uncoveredArea = surface.getUncoveredArea();
        Rectangle2D bounds = surface.getBounds2D();
        double oldWidth = bounds.getWidth();
        double oldX = bounds.getX();
        
        AffineTransform scaleTransform = new AffineTransform();
        scaleTransform.scale(width / oldWidth, 1);
        surface.transform(scaleTransform);
        uncoveredArea.transform(scaleTransform);
        setSurfaceX(oldX, surface);
        
        if (conflictCheck(surface))
        {
            return true;
        }
        else
        {
            scaleTransform = new AffineTransform();
            scaleTransform.scale(oldWidth / width, 1);
            surface.transform(scaleTransform);
            uncoveredArea.transform(scaleTransform);
            setSurfaceX(oldX, surface);
            return false;
        }
    }
    
    /**
     * Set le paramètre height d'une surface.
     * @param height : le nouveau paramètre height.
     * @param surface : la surface qui doit être modifiée.
     * @return : true si réussi, false sinon.
     */
    public boolean setSurfaceHeight(double height, Surface surface)
    {
        if (height < 100) return false;
        
        if (surface instanceof RectangularSurface)
        {
            return setSurfaceHeight(height, (RectangularSurface) surface);
        }
        else if (surface instanceof CombinedSurface)
        {
            return setSurfaceHeight(height, (CombinedSurface) surface);
        }
        else
        {
            return setSurfaceHeight(height, (IrregularSurface) surface);
        }
    }
    
    private boolean setSurfaceHeight(double height, RectangularSurface surface)
    {
        double oldHeight = surface.height;
        
        surface.height = height;
        
        if (conflictCheck(surface))
        {
            return true;
        }
        else
        {
            surface.height = oldHeight;
            return false;
        }
    }
    
    private boolean setSurfaceHeight(double height, IrregularSurface surface)
    {
        Rectangle2D bounds = surface.getBounds2D();
        double oldHeight = bounds.getHeight();
        double oldY = bounds.getY();
        
        AffineTransform scaleTransform = new AffineTransform();
        scaleTransform.scale(1, height / oldHeight);
        surface.transform(scaleTransform);
        setSurfaceY(oldY, surface);
        
        if (conflictCheck(surface))
        {
            return true;
        }
        else
        {
            scaleTransform = new AffineTransform();
            scaleTransform.scale(1, oldHeight / height);
            surface.transform(scaleTransform);
            setSurfaceY(oldY, surface);
            return false;
        }
    }
    
    private boolean setSurfaceHeight(double height, CombinedSurface surface)
    {
        Area uncoveredArea = surface.getUncoveredArea();
        Rectangle2D bounds = surface.getBounds2D();
        double oldHeight = bounds.getHeight();
        double oldY = bounds.getY();
        
        AffineTransform scaleTransform = new AffineTransform();
        scaleTransform.scale(1, height / oldHeight);
        surface.transform(scaleTransform);
        uncoveredArea.transform(scaleTransform);
        setSurfaceY(oldY, surface);
        
        if (conflictCheck(surface))
        {
            return true;
        }
        else
        {
            scaleTransform = new AffineTransform();
            scaleTransform.scale(1, oldHeight / height);
            surface.transform(scaleTransform);
            uncoveredArea.transform(scaleTransform);
            setSurfaceY(oldY, surface);
            return false;
        }
    }
    
    /**
     * Sélectionne une surface.
     * @param point : le point qui doit être à l'intérieur de la surface.
     * @return la surface sélectionnée, peut être null.
     */
    public Surface selectSurface(Point2D.Double point)
    {
        for (Surface surface : surfaces)
        {
            if (surface.contains(point))
            {
                selectedSurface = surface;
                return surface;
            }
        }
        selectedSurface = null;
        return selectedSurface;
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

    public Map<TileType, Integer> getQtyPerTileType()
    {
        return qtyPerTileType;
    }

    public void setQtyPerTileType(Map<TileType, Integer> qtyPerTileType)
    {
        this.qtyPerTileType = qtyPerTileType;
    }
    
    /**
     * Ajoute un type de tuile dans la liste des tuiles disponibles.
     * @param tileType : le type de tuile à ajouter.
     */
    public void addTileType(TileType tileType)
    {
        tileTypes.add(tileType);
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
    
    public void setIsNinetyDegree(boolean isNinetyDegree)
    {
        selectedSurface.getCovering().setIsNinetyDegree(isNinetyDegree);
        selectedSurface.coverSurface();
    }
    
    public void setOffsetX(double offsetX)
    {
        selectedSurface.getCovering().setOffsetX(offsetX);
        selectedSurface.coverSurface();
    }
        
    public void setOffsetY(double offsetY)
    {
        selectedSurface.getCovering().setOffsetY(offsetY);
        selectedSurface.coverSurface();
    }
    
    public void setJointColor(Color jointColor)
    {
       selectedSurface.getCovering().setJointColor(jointColor);
       selectedSurface.coverSurface();
    }
    
    public void setJointWidth(double width)
    {
        selectedSurface.getCovering().setJointWidth(width);
        selectedSurface.coverSurface();
    }
    
    public void setPattern(Pattern pattern)
    {
        selectedSurface.getCovering().setPattern(pattern);
        selectedSurface.coverSurface();
    }
    
    public void setTileType(TileType tileType)
    {
        selectedSurface.getCovering().setTileType(tileType);
        selectedSurface.coverSurface();
    }
    
    public void setTileColor(Color color)
    {
        selectedSurface.getCovering().setTileColor(color);
        selectedSurface.coverSurface();
    }
    
    public void setTileColorByIndex(int index)
    {
        selectedSurface.getCovering().setTileColorByIndex(index);
        selectedSurface.coverSurface();
    }
    
    public void coverSurface()
    {
        selectedSurface.coverSurface();
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

    public void setTileTypeByIndex(Surface surface, int selectedIndex)
    {
        surface.getCovering().setTileType(tileTypes.get(selectedIndex));
        surface.coverSurface();
    }
    
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
