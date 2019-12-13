package VirtuTuile.Domain.CoveringStrategies;

import VirtuTuile.Infrastructure.Geometry;
import VirtuTuile.Infrastructure.Utilities;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class DiagonalCoveringStrategy implements CoveringStrategy
{
    private final int rotation;
    private final double tileWidth;
    private final double tileHeight;
    private final double jointWidth;
    private final double tileJointedWidth;
    private final double tileJointedHeight;
    private final double offsetX;
    private final double offsetY;
    private final int rowOffset;
    private final double nextRowX;
    private final double nextRowY;
    private final double nextColX;
    private final double nextColY;
    private final Area fullArea;
    private final int theta;
    private final int phi;
    private final double sinTheta;
    private final double cosTheta;
    private final double sinPhi;
    private final double cosPhi;
    private final ArrayList<Area> tiles = new java.util.ArrayList<>();
    private double angleAdjustX;
    private double angleAdjustY;
    
    /* NOTES
    *  EN HAUT DE 45, ADJUST INVERSE DIMENSION - TRIGO
    *  DÉSACTIVER INVERT ROW?
    *  MODULO L'OFFSET PAR DIMENSIONS DE LA SURFACE POUR PERMETTRE VALEURS MAX
    */
    public DiagonalCoveringStrategy(
            double tileWidth, double tileHeight, double jointWidth, double offsetX, double offsetY, int rowOffset, 
            Area fullArea, int rotation)
    {
        this.rotation = rotation;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.jointWidth = jointWidth;
        
        if (Math.abs(offsetX) < 1000)
        {
            this.offsetX = offsetX;
        }
        else
        {
            this.offsetX = offsetX % 1000;
        }
        
        if (Math.abs(offsetY) < 1000)
        {
            this.offsetY = offsetX;
        }
        else
        {
            this.offsetY = offsetX % 1000;
        }
        
        this.rowOffset = rowOffset;
        this.fullArea = fullArea;
        this.angleAdjustX = 0;
        this.angleAdjustY = 0;

        if (rotation > 90)
        {
            theta = rotation - 90;   // angle de la rotation
            phi = 90 - theta;        // angle complémentaire      
            tileJointedWidth = tileHeight + jointWidth;
            tileJointedHeight = tileWidth + jointWidth;
        }
        else
        {
            theta = rotation;       // angle de la rotation
            phi = 90 - theta;       // angle complémentaire
            tileJointedWidth = tileWidth + jointWidth;
            tileJointedHeight = tileHeight + jointWidth;
        }         
        
        sinTheta = Math.sin(Math.toRadians(theta));
        cosTheta  = Math.cos(Math.toRadians(theta));
        sinPhi = Math.sin(Math.toRadians(phi));
        cosPhi = Math.cos(Math.toRadians(phi));
         
        // Calcul du déplacement vers la prochaine colonne
        nextColX = tileJointedWidth * cosTheta;
        nextColY = tileJointedWidth * sinTheta;
        
        // Calcul du déplacement de la prochaine rangée
        nextRowX = -(tileJointedHeight * sinTheta);
        nextRowY = tileJointedHeight * cosTheta;
        
    }
    
    /**
     * Crée et place les tuiles sur le covering, selon des rangées en diagonale. Le décalage de rangée
     * est considéré. Le tuiles suivent le modèle du tileType, l'orientation et le décalage en attribut dans le covering
     * @return la liste des tuiles.
     * 
     * PLAN:
     * - Retirer vars de mod/adjust de Covering() DONE
     * - Séparer adjust en 2 méthodes X Y avec multiplicateur DONE
     * - Rendre application de l'offset/adjust directe sans dépendance à l'état précédent DONE
     * - Intégrer shouldInvertRows TODO
     * - Tester 90 degrés et + DONE
     * - Inverser décalage pour theta > 45
     * - Tester edge cases
     * - Fix zones grises sur edge cases
    */
    @Override
    public ArrayList<Area> cover()
    {
        //TODO ne pas offsetRow tout le temps
        //TODO offsetRow ne considère pas le joint
        //Arranger le rowInverter qui cause des sauts
        
        // === Définition des variables === \\
        
        // Cadre de la surface
        Rectangle2D bounds  = fullArea.getBounds2D();
        double boundsWidth  = bounds.getWidth();
        double boundsHeight = bounds.getHeight();
        double boundsX = bounds.getX();
        double boundsY = bounds.getY();
        
        // Aire intérieure de la surface et son coutour de coulis
        Area innerArea = Geometry.getInnerArea(fullArea, jointWidth);
        
        // Compteurs de colonnes/rangées pour la boucle
        int currentCol = 0;         
        int currentRow = 0;
        
        // Cadre englobant la surface d'origine et la surface qui aurait subit <rotation>
        double frameWidth =  boundsWidth  * cosTheta + boundsHeight * cosPhi;           
        double frameHeight = boundsHeight * sinPhi   + boundsWidth  * sinTheta;
        
        // Calcul du nombre de colonnes/rangées maximum
        int maxCols = (int) Math.ceil(frameWidth / tileJointedWidth) + 10; // + pour permettre décalage
        int maxRows = (int) Math.ceil(frameHeight/ tileJointedHeight) + 7;
        
        if (theta > 30)
        {
            maxRows++;
        }
        if (theta > 45)
        {
            maxRows++;
        }
        if (theta > 75)
        {
            maxRows++;
        }
        
        // Décalage
        double[] offsets  = offsetModulos();
        double offsetModX = offsets[0];
        double offsetModY = offsets[1];
        
        double rowOffsetMod;
        double rowOffsetModX;
        double rowOffsetModY;

        if (rotation == 90)
        {
            rowOffsetMod = tileJointedHeight - (tileJointedHeight * rowOffset/100);
            rowOffsetModX = 0.0;
            rowOffsetModY = rowOffsetMod;
        }
        else
        {
            rowOffsetMod =  tileJointedWidth - (tileJointedWidth * rowOffset / 100.0);
            rowOffsetModX = rowOffsetMod * cosTheta;
            rowOffsetModY = rowOffsetMod * sinTheta;
        }

        // Calcul de la coordonnée de départ du tuilage
        double frameXOffset = boundsWidth * sinTheta * cosPhi;
        double frameYOffset = -(boundsWidth * sinTheta * sinPhi);
        
        // Création de la tuile tournée servant de modèle
        Rectangle2D tileBounds = new Rectangle2D.Double(boundsX, boundsY, tileWidth, tileHeight);
        Area modelTile = new Area(tileBounds);
        double anchorX = bounds.getCenterX();
        double anchorY = bounds.getCenterY();
        double modelOriginX = tileBounds.getCenterX();
        double modelOriginY = tileBounds.getCenterY();
        rotateTile(modelTile, anchorX, anchorY);
        double modelRotatedX = modelTile.getBounds2D().getCenterX();
        double modelRotatedY = modelTile.getBounds2D().getCenterY();
        AffineTransform modelRewind = new AffineTransform();
        
       // if (shouldInvertRow(tileJointedWidth * cosPhi + tileJointedHeight * cosPhi))
        //{
         //   currentRow++;
          //  maxRows++;
        //    modelRewind.translate(-rowOffsetModX - tileJointedWidth * cosTheta + tileJointedWidth * sinTheta, -rowOffsetModY);
       // }
//        if (shouldInvertCol(tileJointedWidth * cosPhi + tileJointedHeight * cosPhi))
//        {
//            //currentCol++;
//            //maxCols++;
//            //modelRewind.translate(0, -tileJointedWidth * sinTheta);
//        }

        modelRewind.translate(modelOriginX - modelRotatedX - nextRowX * 5 - nextColX * 6, 
                              modelOriginY - modelRotatedY - nextRowY * 5 - nextColY * 6);
        modelTile.transform(modelRewind);
        AffineTransform moveModelToStart = new AffineTransform();
        moveModelToStart.translate(frameXOffset + offsetModX + angleAdjustX, 
                                   frameYOffset + offsetModY + angleAdjustY);
        modelTile.transform(moveModelToStart);

        
        // Création et positionnement des tuiles
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
                
                newTile.intersect(innerArea);
                
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
                            subTile.intersect(innerArea);
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
                
                if (currentRow % 2 == 0)
                {
                    translateCol.translate(nextRowX + rowOffsetModX, nextRowY + rowOffsetModY);
                }
                else
                {
                    translateCol.translate(nextRowX - rowOffsetModX, nextRowY - rowOffsetModY);
                }
                modelTile.transform(translateCol);
                currentCol = 0;
                currentRow++;
            }
        }
            
        return tiles;
    }

    /**
     * Compense les offset subissant un modulo en angle
     * @param pOffsetModX 
     * @param pOffsetModY
     * @param pTileJointedWidth
     * @param pTileJointedHeight
     * @param pSinTheta
     * @param pCosTheta
     * @param pSinPhi
     * @param pCosPhi
     * @param changedX drapeau indiquant si pOffsetModX a subit un changement dans l'itération précédente
     * @param changedY drapeau indiquant si pOffsetModY a subit un changement dans l'itération précédente
     * @return offsets, un array avec les décalages X et Y résultants
     */
    private double[] offsetModulos()
    {
        double[] offsets = new double[2];
        
        // Appliquer le modulo sur l'offset
        double offsetModX = offsetX % (tileJointedWidth * sinPhi + tileJointedHeight * cosPhi);
        double offsetModY = offsetY % (tileJointedWidth * cosPhi + tileJointedHeight * sinPhi);
        offsets[0] = offsetModX;
        offsets[1] = offsetModY;
        
        // Appliquer la compensation
        int multiplierX = (int) (this.offsetX / (tileJointedWidth * sinPhi + tileJointedHeight * cosPhi));
        int multiplierY = (int) (this.offsetY /(tileJointedWidth * cosPhi + tileJointedHeight * sinPhi));
        setAdjustX(multiplierX);
        setAdjustY(-multiplierX);
        setAdjustX(multiplierY);
        setAdjustY(multiplierY);
        
        // Si la compensation dépasse les dimension d'une tuile, appliquer un modulo et réajuster
        multiplierX = (int) (this.angleAdjustX / (tileJointedWidth * sinPhi + tileJointedHeight * cosPhi));
        multiplierY = (int) (this.angleAdjustY / (tileJointedWidth * cosPhi + tileJointedHeight * sinPhi));
        moduloAdjusts(multiplierX, multiplierY, 0);  
        
        return offsets;

    }
    
    /**
     * Ajuste le décalage s'il est plus grand qu'une tuile. Compense selon la même règle, par récursion. Sans ceci, 
     * le covering pourrait sortir de la surface.
     * @param multiplierX Nombre de fois à appliquer l'ajustement en X
     * @param multiplierY Nombre de fois à appliquer l'ajustement en Y
     * @param recursionCounter Compteur de récursion pour protéger contre cas extrêmes
     */
    private void moduloAdjusts(int multiplierX, int multiplierY, int recursionCounter)
    {
        this.angleAdjustX %= (tileJointedWidth * sinPhi + tileJointedHeight * cosPhi);
        this.angleAdjustY %= (tileJointedWidth * cosPhi + tileJointedHeight * sinPhi);
        
        setAdjustX(multiplierX);
        setAdjustY(-multiplierX);
        setAdjustX(multiplierY);
        setAdjustY(multiplierY);        

        // Protège contre cas extrêmes où résultat boucle. Pourrait cause 1 petit "saut" du covering.
        if (recursionCounter > 10) 
        {
            return;
        }
        
        multiplierX = (int) (this.angleAdjustX / (tileJointedWidth * sinPhi + tileJointedHeight * cosPhi));
        multiplierY = (int) (this.angleAdjustY / (tileJointedWidth * cosPhi + tileJointedHeight * sinPhi));
        
        if (multiplierX != 0 || multiplierY != 0)
        {
            recursionCounter++;
            moduloAdjusts(multiplierX, multiplierY, recursionCounter);
        }
    }
    
    /**
    * Ajoute un décalage sur l'axe des X pour la tuile de départ, afin d'aligner les joints après l'application d'un 
    * modulo sur le décalage entré par l'utilisateur
    * @param pTileJointedHeight
    * @param pSinTheta
    * @param multiplier Nombre de fois à appliquer l'adjustement. Négatif vers gauche, positif vers droite
    */
    private void setAdjustX(int multiplier)
    {
        if (theta > 45)
        {
            //this.angleAdjustY += (((tileJointedWidth * sinPhi + tileJointedHeight * cosPhi) - tileJointedHeight * sinTheta) * multiplier);
        }
        else
        {
            this.angleAdjustX += ((tileJointedHeight * sinTheta) * multiplier);
        }
    }
    
    /**
    * Ajoute un décalage sur l'axe des Y pour la tuile de départ, afin d'aligner les joints après l'application d'un 
    * modulo sur le décalage entré par l'utilisateur
    * @param pTileJointedHeight
    * @param pSinTheta
    * @param multiplier Nombre de fois à appliquer l'adjustement. Négatif vers haut, Positif vers bas
    */ 
    private void setAdjustY(int multiplier)
    {
        if (theta > 45)
        {
            //this.angleAdjustX += (((tileJointedHeight * sinPhi + tileJointedWidth * cosPhi) - tileJointedHeight * sinTheta) * multiplier);
        }
        else
        {
            this.angleAdjustY += ((tileJointedWidth * sinTheta) * multiplier);
        }
    }
    

    
    private boolean shouldInvertRow(double pTileHeight)
    {
        double x = offsetY + angleAdjustY;
        if (x < 0)
        {
            x = Math.abs(x) + 2 * pTileHeight;
        }
        return x % (pTileHeight * 2) < pTileHeight;
    }
    
    private boolean shouldInvertCol(double pTileWidth)
    {
        double x = offsetX + angleAdjustX;
        if(x < 0)
        {
            x = Math.abs(x) + 2 * pTileWidth;
        }
        return x % (pTileWidth * 2) < pTileWidth;
    }
    
    /**
     * Performe une rotation sur une Area reçue, selon la valeur de l'attribut this.rotation, autour du pivot donné
     * @param tileToRotate La tuile (Area) à pivoter
     * @param anchorX La position horizontale du point pivot
     * @param anchorY La position verticale du point pivot
     * @return La tuile pivotée
     */
    private Area rotateTile(Area tileToRotate, double anchorX, double anchorY)
    {
        AffineTransform rotateAT = new AffineTransform();
        rotateAT.rotate(java.lang.Math.toRadians(rotation), anchorX, anchorY);
        tileToRotate.transform(rotateAT);
        return tileToRotate;
    }
    
    public double getAngleAdjustX()
    {
        return angleAdjustX;
    }
            
    public double getAngleAdjustY()
    {
        return angleAdjustY;
    }
}