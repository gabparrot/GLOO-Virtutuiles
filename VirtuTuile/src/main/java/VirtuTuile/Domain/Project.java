package VirtuTuile.Domain;

import java.awt.Color;
import java.awt.Shape;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Area;

//TODO FOR MERGE
/**
 * Changer de mode
 * Unselect
 * Permettre 2 select, select pointe maintenant sur une array list séparée
 * Ça va aussi changer le selectedSurface normal en plus de celui dans l'array list, pour le highlight
 * Dès que l'arraylist a 2 surfaces, attempt merge
 * Retourner booléen de confirmation
 * si réussi: change selectedSurface normal pour la nouvelle fusionnée,
 *            retombe en mode select?
 *            purger la liste
 *            Redessiner le canevas
 * Si pas réussi: msgbox d'erreur
 * le merge:
 *      Verifier surface commune d'une dimension minimum (meme que minimum pour tuiles ou plus?
 *      Déterminer quelle est plus grosse surface
 *      Si aire égale, priorise plus complexe donc combined gagne sur elem, combined 3 gagne sur combined 2
 *      Sinon randomize ou prendre le point le plus en haut à gauche ? 
 *      Prendre chacune des bornes des 2 surfaces
 *      Creer nouvelle surfaceCombinee avec ces bornes, qui prend le covering de la plus grosse surface
 *      Pour defusion eventuelle:
 *          surfaceCombinee recoit en attribut les 2 Surfaces initiales
 *          S'il y avait une surfaceCombinee dans les 2 initiales, prendre sa liste de surface aussi
 *          Modifier bornes de la surfaceCombinee (move, resize) modifie aussi les bornes élémentaires
 *          ** Faudra trouver comment je gère le 1 pixel de différence qui disparait entre les 2 bornes
 *          Défusion replace toutes les surfaces élémentaires dans la liste de surface du projet
 *          Toutes les surfaces élémentaires prennent covering de la Combinée
 *          Combinée est supprimée
 *      
 *      
 * 
*/

/**
 * @class definissant le projet en cours
 * @author gabparrot
 */
public class Project
{
    private String projectName;
    private Shape selectedTile = null;
    private Map<TileType, Integer> qtyPerTileType = new HashMap<>();
    private final ArrayList<Surface> surfaces = new ArrayList<>();
    private Surface selectedSurface = null;
    private final ArrayList<Surface> surfacesToMerge = new ArrayList<>();
    
    /**
     * Constructeur.
     * @param projectName : le nom du projet.
     */
    public Project(String projectName)
    {
        this.projectName = projectName;
        /*java.awt.Polygon poly = new java.awt.Polygon();                             // Test
        poly.addPoint(2000, 4000);                                                  // Test
        poly.addPoint(4000, 4000);                                                  // Test
        poly.addPoint(4000, 2000);                                                  // Test
        CombinedSurface surface = new CombinedSurface(false, Color.GREEN, poly);    // Test
        surfaces.add(surface);
        */
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
     * Vérifie s'il y a un conflit avec une nouvelle surface.
     * @param shape : la forme de la surface à vérifier.
     * @return true s'il n'y a pas de conflit, false s'il y a conflit.
     */
    public boolean conflictCheck(Shape shape)
    {
        return boundsConflictCheck(shape) || areaConflictCheck(shape);
    }
    
    /**
     * Vérifie s'il y a intersection avec les bornes d'une nouvelle surface.
     * @param shape : la forme de la surface à vérifier.
     * @return true s'il n'y a pas de conflit, false s'il y a conflit.
     */
    public boolean boundsConflictCheck(Shape shape)
    {
        boolean status = true;
        for (Surface surface : surfaces)
        {
            Rectangle2D intersection = shape.getBounds2D().createIntersection(surface.getBounds2D());
            if (intersection.getWidth() > 0.1 && intersection.getHeight() > 0.1 && surface != shape)
            {
                status = false;
            }
        }
        return status;
    }
    
    /**
     * Vérifie s'il y a intersection avec l'aire d'une nouvelle surface.
     * @param shape : la forme de la surface à vérifier.
     * @return true s'il n'y a pas de conflit, false s'il y a conflit.
     */
    public boolean areaConflictCheck(Shape shape)
    {
        boolean status = true;
        Area newArea = new Area(shape);
        for (Surface surface : surfaces)
        {
            Area area = new Area(surface);
            area.intersect(newArea);
            if (!area.isEmpty() && surface != shape)
            {
                status = false;
            }
        }
        return status;
    }
    
    /**
     * Fusionne les 2 surfaces marquees comme selectionnees par surfacesToMerge
     */
    public boolean mergeSelectedSurfaces()
    {
        boolean boolSuccess = false;
        
        if (surfacesToMerge.isEmpty())
        {
            surfacesToMerge.add(selectedSurface);
            boolSuccess = true;
        }
        else if (surfacesToMerge.size() == 1)
        {
            surfacesToMerge.add(selectedSurface);
            int bigSurface;
            int smallSurface;
            
            if (getSurfaceArea(surfacesToMerge.get(0)) > getSurfaceArea(surfacesToMerge.get(1)))
            {
                bigSurface = 0;
                smallSurface = 1;
            }
            else
            {
                bigSurface = 1;
                smallSurface = 0;
            }
            surfacesToMerge.add(selectedSurface);
            Rectangle2D mergedSurface = 
                    surfacesToMerge.get(bigSurface).getBounds().union(surfacesToMerge.get(smallSurface).getBounds());
            
        }
        return boolSuccess;
    }
    
    /**
     * Donne l'aire de la surface demandee
     * @param surface L'object Surface dont on veut connaître l'aire
     * @return area l'aire de la surface, un double
     */
    public double getSurfaceArea(Surface surface)
    {
        double area = 0;
        
        if (surface instanceof RectangularSurface)
        {
            return (surface.getBounds2D().getHeight() * surface.getBounds2D().getWidth());
        }
        else if (surface instanceof CombinedSurface)
        {
            if (((CombinedSurface) surface).isRectangular())
            {
                return (surface.getBounds2D().getHeight() * surface.getBounds2D().getWidth());
            }
            else
            {
                for (int i = 0; i < ((CombinedSurface) surface).getAbsorbedSurfaces().size(); i++)
                {
                    area = area + getSurfaceArea(((CombinedSurface) surface).getAbsorbedSurfaces().get(i));
                }
            }
        }
        else if (surface instanceof IrregularSurface)
        {
            ArrayList<Double> allX = new ArrayList<>();
            ArrayList<Double> allY = new ArrayList<>();
            int nbPoints = 0;
            java.awt.geom.AffineTransform at = new java.awt.geom.AffineTransform();
            java.awt.geom.PathIterator iter = ((IrregularSurface) surface).getPathIterator(at);
            
            for (; iter.isDone(); iter.next())
            {
                nbPoints++;
                double[] currentCoords = new double[2];
                iter.currentSegment(currentCoords);
                allX.add(currentCoords[0]);
                allY.add(currentCoords[1]);
            }
            
            double base;
            double height;
            switch (nbPoints)
            {   
                
                // Cas d'erreurs retournent 0            }
                case 0:
                    area = 0;
                    break;
                    
                case 1:
                    area = 0;
                    break;
                    
                case 2:
                    area = 0;
                    break;
                    
                //triangle
                case 3:
                    base = Math.sqrt(Math.pow(allX.get(0) - allX.get(1), 2) + 
                                            Math.pow(allY.get(0) - allY.get(1),2));
                    height = Math.sqrt(Math.pow(allX.get(0) - allX.get(2), 2) + 
                                            Math.pow(allY.get(0) - allY.get(2),2));
                    area = (base * height)/2;
                    break;
                
                //Rectangle (Pourrait exister par fusion 2 triangle par exemple)
                case 4: 
                    base = Math.sqrt(Math.pow(allX.get(0) - allX.get(1), 2) + 
                                            Math.pow(allY.get(0) - allY.get(1),2));
                    height = Math.sqrt(Math.pow(allX.get(0) - allX.get(2), 2) + 
                                            Math.pow(allY.get(0) - allY.get(2),2));
                    area = (base * height);
                    break;
                    
                default:
                    
                    int j = nbPoints - 1;
                    
                    for (int i = 0; i < nbPoints; i++)
                    {
                        area = area + (allX.get(j) + allX.get(i)) * (allY.get(j) - allY.get(i));
                        j = i;
                    }
                    
                    area = area / 2;
                    return area;                   
            }
        }
        return area;
    }
    
    
    
    /**
     * Déplace une surface à une nouvelle position.
     * @param newPos : nouvelle position.
     * @param surface : la surface qui doit être déplacée.
     */
    public void moveSurfaceToPoint(Point2D newPos, Surface surface)
    {
        if (surface instanceof RectangularSurface)
        {
            setRectangularSurfaceXY(newPos.getX(), newPos.getY(), (RectangularSurface) surface);
        }
    }
    
    /**
     * Déplace une surface rectangulaire à une nouvelle position x y.
     * @param x la coordonnée horizontale
     * @param y la coordonnée verticale
     * @param surface la surface qui doit être déplacée
     */
    public void setRectangularSurfaceXY(double x, double y, RectangularSurface surface)
    {
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        
        double oldX = surface.x;
        double oldY = surface.y;
        
        double surroundingBounds[] = getSurroundingBounds(surface);
        surface.x = x;
        
        // Si il y a conflit, il faut glisser la surface dans deux directions:
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
     * Set le paramètre x d'une surface rectangulaire.
     * @param x : le nouveau paramètre x.
     * @param surface : la surface qui doit être modifiée.
     * @return : true si réussi, false sinon.
     */
    public boolean setRectangularSurfaceX(double x, RectangularSurface surface)
    {
        if (x < 0) return false;
        
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
    
    /**
     * Set le paramètre y d'une surface rectangulaire.
     * @param y : le nouveau paramètre y.
     * @param surface : la surface qui doit être modifiée.
     * @return : true si réussi, false sinon.
     */
    public boolean setRectangularSurfaceY(double y, RectangularSurface surface)
    {
        if (y < 0) return false;
        
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
    
    /**
     * Set le paramètre width d'une surface rectangulaire.
     * @param width : le nouveau paramètre width.
     * @param surface : la surface qui doit être modifiée.
     * @return : true si réussi, false sinon.
     */
    public boolean setRectangularSurfaceWidth(double width, RectangularSurface surface)
    {
        if (width < 100) return false;
        
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
    
    /**
     * Set le paramètre height d'une surface rectangulaire.
     * @param height : le nouveau paramètre height.
     * @param surface : la surface qui doit être modifiée.
     * @return : true si réussi, false sinon.
     */
    public boolean setRectangularSurfaceHeight(double height, RectangularSurface surface)
    {
        if (height < 100) return false;
        
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
    
    /**
     * Sélectionne une surface.
     * @param point : le point qui doit être à l'intérieur de la surface.
     * @return la surface sélectionnée, peut être null.
     */
    public Surface selectSurface(Point2D point)
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
     * Efface la surface selectionnée.
     */
    public void deleteSelectedSurface()
    {
        if (selectedSurface != null) surfaces.remove(selectedSurface);
    }
    
    /**
     * Retourne la surface sélectionnée, peut être null.
     * @return la surface sélectionnée, si une surface est sélectionnée, null sinon.
     */
    public Surface getSelectedSurface()
    {
        return selectedSurface;
    }
   
    public String getProjectName()
    {
        return projectName;
    }

    public void setProjectName(String projectName)
    {
        this.projectName = projectName;
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
     * Annuler la dernière action
     */
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
}
