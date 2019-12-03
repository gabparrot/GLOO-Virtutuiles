package VirtuTuile.Domain.CoveringStrategies;

import VirtuTuile.Infrastructure.Geometry;
import VirtuTuile.Infrastructure.Utilities;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class DiagonalCoveringStrategy implements CoveringStrategy
{
    private final int rotation;
    private final double tileWidth;
    private final double tileHeight;
    private final double jointWidth;
    private final double offsetX;
    private final double offsetY;
    private final int rowOffset;
    private final Area fullArea;
    private final ArrayList<Area> tiles = new java.util.ArrayList<>();
    
    public DiagonalCoveringStrategy(
            double tileWidth, double tileHeight, double jointWidth,
            double offsetX, double offsetY, int rowOffset,
            Area fullArea, int rotation)
    {
        this.rotation = rotation;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.jointWidth = jointWidth;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.rowOffset = rowOffset;
        this.fullArea = fullArea;
    }
    
    /**
     * Crée et place les tuiles sur le covering, selon des rangées en diagonale. Le décalage de rangée
     * est considéré. Le tuiles suivent le modèle du tileType, l'orientation et le décalage en attribut dans le covering
     * @return la liste des tuiles.
     * 
     * Plan rotation
     * - Retirer  option de 90', isNinetyDegree des attributs DONE
     * - Ajouter attributs rotation NOPE
     * - Si rotation est entre 0' et 45', largeur est hauteur same, sinon on flip la valeur 
     * - Appliquer un AffineTransform de rotation
     * - Dans le GUI, le mouseover doit aussi considérer les degrés pour switch width/height
     * - ajouter getter setter de rotation
    */
    @Override
    public ArrayList<Area> cover()
    {
        // TODO ajouter l'offset
        // TOFIX Calcul nextRow doit être basé sur hauter de tuile, pas largeur
        // TOFIX Calcul nextCol semble donner la moitié du déplacement
        // TOFIX SOIT point de départ trop à gauche, SOIT tasse pas bien nextCol
        
        // Déclaration des variables
        Rectangle2D bounds;
        double boundsWidth = 0;
        double boundsHeight = 0;
        Area innerArea;
        double xStart = 0;
        double yStart = 0;
        double nextColX = 0;
        double nextColY = 0;
        double nextRowX = 0;
        double nextRowY = 0;
        double frameSide = 0;
        int maxCols = 0;
        int maxRows = 0;
        int currentCol = 0;
        int currentRow = 0;
        double rotSin = Math.sin(rotation);
        double rotCos = Math.cos(rotation);
        double rotTan = Math.tan(rotation);
        
        
        // Création de la tuile tournée servant de modèle
        bounds = fullArea.getBounds2D();
        boundsWidth = bounds.getWidth();
        boundsHeight = bounds.getHeight();
        double boundsX = bounds.getX();
        double boundsY = bounds.getY();
        innerArea = Geometry.getInnerArea(fullArea, jointWidth);
        Rectangle2D innerBounds = innerArea.getBounds2D();
        Rectangle2D tileBounds = new Rectangle2D.Double(innerBounds.getX(), innerBounds.getY(), tileWidth, tileHeight);
        Area modelTile = new Area(tileBounds);
        double anchorX = innerArea.getBounds2D().getCenterX();
        double anchorY = innerArea.getBounds2D().getCenterY();
        testRotateTile(modelTile, anchorX, anchorY);
        
        // Calcul du déplacement vers la prochaine colonne
        nextColX = (tileWidth + jointWidth) * rotCos;
        nextColY = (tileWidth + jointWidth) * rotSin;
        
        if (rotation > 90)
        {
            nextColX = -nextColX;
            nextColY = -nextColY;
        }

        //TEST
        System.out.println("nextColX: " + nextColX + ", nextColY" + nextColY);
        
        // Calcul du déplacement de la prochaine rangée
        nextRowX = -((tileHeight + jointWidth) * rotSin);
        nextRowY = (tileHeight + jointWidth) * rotCos;
        if (rotation > 90)
        {
            nextRowX = -nextRowX;
            nextRowY = -nextRowY;
        }
        //TEST
        System.out.println("nextRowX: " + nextRowX + ", nextRowY" + nextRowY);
        // Calcul du nombre de colonnes/rangées maximum
        double frameSideShortHalf = boundsWidth * rotSin;
        double frameSideLongHalf = boundsWidth * rotCos;
        frameSide = frameSideShortHalf + frameSideLongHalf;
        maxCols = (int) Math.ceil(frameSide / (tileWidth + jointWidth)) + 1; // +1 pour permettre décalage
        maxRows = (int) Math.ceil(frameSide / (tileHeight + jointWidth)) + 1;
        
        // Calcul de la coordonnée de départ
        double frameYOffset = frameSideLongHalf * rotSin;
        double frameXOffset = frameYOffset * rotTan;
        if (rotation > 90)
        {
            frameXOffset = -frameXOffset;
            frameYOffset = -frameYOffset;
        }
        
        xStart = boundsX + frameXOffset;
        yStart = boundsY + frameYOffset;

        // TEST
        System.out.println("xstart :" + xStart + ", ystart :" + yStart);
        
        // Création des tuiles
        while (currentRow < maxRows)
        {
            if (currentCol < maxCols) // Si ajoute une colonne
            {
                Area newTile = new Area(modelTile);
                AffineTransform translateRow = new AffineTransform();
                translateRow.translate(nextColX * currentCol, nextColY * currentCol);
                
                if (currentCol != 0) // Si on est au début de la rangée, translation == 0, on pose par dessus modèle
                {
                newTile.transform(translateRow);
                }
                
                //TEST intersect désactivé temporairement pour voir où vont les tuiles
                //newTile.intersect(innerArea);
                
                // Éliminer tuiles en dehors de la surface
                if (!newTile.isEmpty())
                {
                    // Séparer les tuiles constituées de morceaux distincts
                    if (newTile.isSingular())
                    {
                        tiles.add(newTile);
                    }
                    else
                    {
                        ArrayList<Area> subTiles = Geometry.divideTile(newTile);
                        for (Area subTile : subTiles)
                        {
                            //TEST intersect désactivé temporairement
                            //subTile.intersect(innerArea);
                            if (!subTile.isEmpty())
                            {
                                tiles.add(subTile);
                            }
                        }
                    }
                }
                currentCol++;
            }
            else // Si on ajoute une rangée
            {
                AffineTransform translateCol = new AffineTransform();
                translateCol.translate(nextRowX, nextRowY);
                modelTile.transform(translateCol);
                currentCol = 0;
                currentRow++;
            }
        }
        
        return tiles;
    }
    
    /**
     * Crée et place les tuiles sur le covering, selon des rangées en diagonale. Le décalage de rangée
     * est considéré. Le tuiles suivent le modèle du tileType, l'orientation et le décalage en attribut dans le covering
     * @return la liste des tuiles.
     * 
     * Plan rotation
     * - Retirer  option de 90', isNinetyDegree des attributs DONE
     * - Ajouter attributs rotation NOPE
     * - Si rotation est entre 0' et 45', largeur est hauteur same, sinon on flip la valeur 
     * - Appliquer un AffineTransform de rotation
     * - Dans le GUI, le mouseover doit aussi considérer les degrés pour switch width/height
     * - ajouter getter setter de rotation
    */
    //@Override TEST
    public ArrayList<Area> testCover()
    {
        double newWidth = tileWidth;
        double newHeight = tileHeight;
        double offsetXMod = this.offsetX % (tileWidth + jointWidth);
        double offsetYMod = this.offsetY % (tileHeight + jointWidth);
        //double rowOffsetMod = tileWidth - rowOffset / 100. * tileWidth -  jointWidth * rowOffset / 100;
        
        if (rotation > 45 && rotation <= 135) // Inverser les X et Y lorsque la tuile est sur le côté
        {
            newWidth = tileHeight;
            newHeight = tileWidth;
            offsetXMod = this.offsetY % (tileHeight + jointWidth);
            offsetYMod = this.offsetX % (tileWidth + jointWidth);
        }
        double rowOffsetMod = newWidth - rowOffset / 100. * newWidth -  jointWidth * rowOffset / 100;
        
        Rectangle2D bounds = fullArea.getBounds2D();
        Area innerArea = Geometry.getInnerArea(fullArea, jointWidth);
        
        // Ancrages de rotation
        double anchorX = innerArea.getBounds2D().getCenterX();
        double anchorY = innerArea.getBounds2D().getCenterY();
        
        // Détermine si on devrait commencer par une rangée paire ou impaire
        int rowCount = 0;
        boolean shouldInvertRow = shouldInvertRow(newHeight);
        
        //int newTileCounter = 0;
        int columnCounter = 0;
        int rowCounter = 0;
        double surfaceHypothenuse = Math.sqrt(bounds.getHeight() * bounds.getHeight() + bounds.getWidth() * bounds.getWidth());
        
        int maxColsOrRows = 0;
        
        //TODO à retirer après split codé
        maxColsOrRows = (int) Math.ceil(newHeight < newWidth ? surfaceHypothenuse / newHeight : surfaceHypothenuse / newWidth) * 4;
        
        int maxRows = 0;
        int maxCols = 0;
        
        if (rotation > 45 && rotation <= 135)
        {
            
        }
        else
        {
            //TODO
        }
        
        // Départ du recouvrement
        Point2D.Double currentPoint = new Point2D.Double(bounds.getX() - (Math.floor(maxColsOrRows/5) * (tileWidth + jointWidth)) + offsetXMod,
                                                         bounds.getY() - (Math.floor(maxColsOrRows/5) * (tileWidth + jointWidth)) + offsetYMod);
        
        if (shouldInvertRow)
        {
            rowCount++;
            currentPoint.x -= rowOffsetMod + jointWidth;
        }
        
        // Créer les tuiles et les ajouter à la liste
        boolean allCovered = false;
        boolean translationCalculated = false;
        double[] translation = new double[2];
        translation[0] = 0;
        translation[0] = 0;
        while (!(allCovered))
        {
            Point2D.Double tileTopLeft = currentPoint;
            Point2D.Double tileBotRight = new Point2D.Double(currentPoint.x + tileWidth,
                                                             currentPoint.y + tileHeight);
            
            Area tile = new Area(Utilities.cornersToRectangle(tileTopLeft, tileBotRight));
            

            if (rotation != 0)
            {   
                if (!translationCalculated)
                {
                    translation = calculateTranslation(tile, anchorX, anchorY, bounds);
                }
                rotateTile(tile, anchorX, anchorY, translation);
            }
            
            tile.intersect(innerArea);
            if (!tile.isEmpty())
            {
                if (tile.isSingular())
                {
                    tiles.add(tile);
                    //newTileCounter += 1;
                }
                else
                {
                    ArrayList<Area> subTiles = Geometry.divideTile(tile);
                    for (Area subTile : subTiles)
                    {
                        subTile.intersect(innerArea);
                        if (!subTile.isEmpty())
                        {
                            tiles.add(subTile);
                            //newTileCounter += 1;
                        }
                    }
                }
            }
           
            if (columnCounter < maxColsOrRows /2)
            {
                currentPoint.x += tileWidth + jointWidth;
                columnCounter += 1;
            }
            else // Si on dépasse en X, on descend et on repart a gauche.
            {
                columnCounter = 0;
                rowCounter ++;
                
                /*
                bounds.getX() - tileWidth + tileWidth * (bonusColumns + jointWidth) + offsetXMod,
                                                         bounds.getY() - tileHeight - tileHeight * (bonusRows + jointWidth) + offsetYMod
                */
                if (rowCount % 2 == 1)
                {
                    currentPoint.setLocation(bounds.getX() - (tileWidth + jointWidth) * 3 + offsetXMod, 
                                             currentPoint.y + tileHeight + jointWidth);
                }
                else  // Décaler les rangées paires (celle du haut est #1)
                {
                    currentPoint.setLocation(bounds.getX() - (tileWidth + jointWidth) * 3 + offsetXMod - rowOffsetMod - jointWidth,
                                             currentPoint.y + tileHeight + jointWidth);

                }
                
                if (rowCount > maxColsOrRows)
                {
                    System.out.println("rowCounter: " + rowCounter);
                    allCovered = true;
                }
                
                //newTileCounter = 0;
                rowCount++;
            }
        }
        return tiles;
    }
    
    private boolean shouldInvertRow(double tileHeight)
    {
        double x = offsetY;
        if (x < 0)
        {
            x = Math.abs(x) + 2 * tileHeight + 2* jointWidth;
        }
        return x % ((tileHeight + jointWidth) * 2) < tileHeight + jointWidth;
    }
    
    /**
     * Performe une rotation sur une Area reçue, selon la valeur de l'attribut this.rotation, autour du pivot donné
     * @param tileToRotate La tuile (Area) à pivoter
     * @param anchorX La position horizontale du point pivot
     * @param anchorY La position verticale du point pivot
     * @return La tuile pivotée
     */
    private Area testRotateTile(Area tileToRotate, double anchorX, double anchorY)
    {
        AffineTransform rotateAT = new AffineTransform();
        rotateAT.rotate(java.lang.Math.toRadians(rotation), anchorX, anchorY);
        tileToRotate.transform(rotateAT);
        return tileToRotate;
    }
    
    /**
     * Performe une rotation sur une Area reçue, selon la valeur de l'attribut this.rotation, autour du pivot donné
     * @param tileToRotate La tuile (Area) à pivoter
     * @param anchorX La position horizontale du point pivot
     * @param anchorY La position verticale du point pivot
     * @return La tuile pivotée
     */
    private Area rotateTile(Area tileToRotate, double anchorX, double anchorY, double[] translation)
    {
        // Turn
        double startX = tileToRotate.getBounds2D().getCenterX();
        double startY = tileToRotate.getBounds2D().getCenterY();
        AffineTransform rotateAT = new AffineTransform();
        rotateAT.rotate(java.lang.Math.toRadians(rotation), anchorX, anchorY);
        
        tileToRotate.transform(rotateAT);
        
        // Translate back to position
        double endX = tileToRotate.getBounds2D().getCenterX();
        double endY = tileToRotate.getBounds2D().getCenterY();
        AffineTransform translateAT = new AffineTransform();
        translateAT.translate(translation[0], translation[1]);
        tileToRotate.transform(translateAT);
        
        return tileToRotate;
    }
    
    private double[] calculateTranslation(Area tileToRotate, double anchorX, double anchorY, Rectangle2D bounds)
    {
        double startX = tileToRotate.getBounds2D().getCenterX();
        double startY = tileToRotate.getBounds2D().getCenterY();
        Area tileCopy = new Area(tileToRotate);
        AffineTransform rotateAT = new AffineTransform();
        rotateAT.rotate(java.lang.Math.toRadians(rotation), anchorX, anchorY);
        tileCopy.transform(rotateAT);
        
        double endX = tileToRotate.getBounds2D().getCenterX();
        double endY = tileToRotate.getBounds2D().getCenterY();
        double transX = 0;
        double transY = 0;
        
        if (rotation < 90)
        {
            transX = -(endX - startX) - bounds.getWidth();
            transY = -(endY - startY) - bounds.getHeight()/2;
        }
        else
        {
            transX = -(endX - startX) + bounds.getWidth()/2;
            transY = -(endY - startY) - bounds.getHeight()/3;
        }
        double[] translation = new double[2];
        translation[0] = transX;
        translation[1] = transY;
        
        return translation;
    }
}
