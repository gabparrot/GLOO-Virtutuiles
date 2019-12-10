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
    private int modCounterX;
    private int modCounterY;
    private double angleAdjustX;
    private double angleAdjustY;
    private final ArrayList<Area> tiles = new java.util.ArrayList<>();
    
    public DiagonalCoveringStrategy(
            double tileWidth, double tileHeight, double jointWidth, double offsetX, double offsetY, int rowOffset, 
            Area fullArea, int rotation, int pModCounterX, int pModCounterY, double pAngleAdjustX, double pAngleAdjustY)
    {
        this.rotation = rotation;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.jointWidth = jointWidth;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.rowOffset = rowOffset;
        this.fullArea = fullArea;
        this.modCounterX = pModCounterX;
        this.modCounterY = pModCounterY;
        this.angleAdjustX = pAngleAdjustX;
        this.angleAdjustY = pAngleAdjustY;
        
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
        //TODO ne pas offsetRow tout le temps
        //TODO offsetRow ne considère pas le joint
        //Arranger le rowInverter qui cause des sauts
        
        // === Définition des variables === \\
        
        // Cadre de la surface
        Rectangle2D bounds = fullArea.getBounds2D();
        double boundsWidth = bounds.getWidth();
        double boundsHeight = bounds.getHeight();
        double boundsX = bounds.getX();
        double boundsY = bounds.getY();
        
        // Aire intérieure de la surface et son coutour de coulis
        Area innerArea = Geometry.getInnerArea(fullArea, jointWidth);
        
        // Compteurs de colonnes/rangées pour la boucle
        int currentCol = 0;         
        int currentRow = 0;
        
        // Dimensions des tuiles
        
        double tileJointedWidth = tileWidth + jointWidth;
        double tileJointedHeight = tileHeight + jointWidth;
        
        if (rotation > 90)
        {
            tileJointedWidth = tileHeight + jointWidth;
            tileJointedHeight = tileWidth + jointWidth;
        }
        
        // Variables trigonométriques
        int theta = rotation;       // angle de la rotation
        int phi = 90 - theta;    // angle complémentaire
        
        if (rotation > 90)
        {
            theta = rotation - 90;
            phi = 90 - theta;
        }
        
        double sinTheta = Math.sin(Math.toRadians(theta));
        double cosTheta  = Math.cos(Math.toRadians(theta));
        double sinPhi = Math.sin(Math.toRadians(phi));
        double cosPhi = Math.cos(Math.toRadians(phi));

        // Calcul du déplacement vers la prochaine colonne
        double nextColX = tileJointedWidth * cosTheta;
        double nextColY = tileJointedWidth * sinTheta;
        
        // Calcul du déplacement de la prochaine rangée
        double nextRowX = -(tileJointedHeight * sinTheta);
        double nextRowY = tileJointedHeight * cosTheta;
        
        // Cadre englobant la surface d'origine et la surface qui aurait subit <rotation>
        double frameWidth = boundsWidth * cosTheta + boundsHeight * cosPhi;           
        double frameHeight = boundsHeight * sinPhi + boundsWidth * sinTheta;
        
        // Calcul du nombre de colonnes/rangées maximum
        int maxCols = (int) Math.ceil(frameWidth / tileJointedWidth) + 9; // + pour permettre décalage
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
        double offsetModX = this.offsetX;
        double offsetModY = this.offsetY;
        
        double[] offsets = offsetModulos(offsetModX, offsetModY,tileJointedWidth, tileJointedHeight, 
                                         sinTheta, cosTheta,sinPhi, cosPhi);
        
        this.modCounterX = (int) (this.offsetX / (tileJointedWidth * sinPhi + tileJointedHeight * cosPhi));
        this.modCounterY = (int) (this.offsetY /(tileJointedWidth * cosPhi + tileJointedHeight * sinPhi));
        
        offsetModX = offsets[0];
        offsetModY = offsets[1];
        
        double rowOffsetMod;
        double rowOffsetModX;
        double rowOffsetModY;
        

        if (theta == 90)
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

        modelRewind.translate(modelOriginX - modelRotatedX - nextRowX * 5 - nextColX * 5, 
                              modelOriginY - modelRotatedY - nextRowY * 5 - nextColY * 5);
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
    private double[] offsetModulos(double pOffsetModX, double pOffsetModY, double pTileJointedWidth, 
                                   double pTileJointedHeight, double pSinTheta, double pCosTheta, double pSinPhi, 
                                   double pCosPhi)
    {
        double[] offsets = new double[2];
        double newOffsetModX = pOffsetModX % (pTileJointedWidth * pSinPhi + pTileJointedHeight * pCosPhi);
        double newOffsetModY = pOffsetModY % (pTileJointedWidth * pCosPhi + pTileJointedHeight * pSinPhi);
        int newModCounterX = (int) (this.offsetX / (pTileJointedWidth * pSinPhi + pTileJointedHeight * pCosPhi));
        int newModCounterY =(int) (this.offsetY /(pTileJointedWidth * pCosPhi + pTileJointedHeight * pSinPhi));
        
        if (newModCounterX != this.modCounterX)
        { 
            if (newModCounterX < this.modCounterX)
            {
                this.angleAdjustX -= (pTileJointedHeight * pSinTheta); // Compense angle vers gauche
                this.angleAdjustY += (pTileJointedWidth * pSinTheta);  // compense angle vers bas
            }
            if (newModCounterX > this.modCounterX)
            {
                this.angleAdjustX += (pTileJointedHeight * pSinTheta); // Compense angle vers droite
                this.angleAdjustY -= (pTileJointedWidth * pSinTheta);  // compense angle vers haut
            }
        }

        if (newModCounterY != this.modCounterY)
        {
            if (newModCounterY < this.modCounterY)
            {
                this.angleAdjustX -= (pTileJointedHeight * pSinTheta); // Compense angle vers gauche
                this.angleAdjustY -= (pTileJointedWidth * pSinTheta);  // compense angle vers bas
            }
            if (newModCounterY > this.modCounterY)
            {
                this.angleAdjustX += (pTileJointedHeight * pSinTheta); // Compense angle vers droite
                this.angleAdjustY += (pTileJointedWidth * pSinTheta);  // compense angle vers haut
            }

        }
        
        newOffsetModX = newOffsetModX % (pTileJointedWidth * pSinPhi + pTileJointedHeight * pCosPhi);
        newOffsetModY = newOffsetModY % (pTileJointedWidth * pCosPhi + pTileJointedHeight * pSinPhi);       
        offsets[0] = newOffsetModX;
        offsets[1] = newOffsetModY;
        
        double oldAdjustX = angleAdjustX;
        this.angleAdjustX %= (pTileJointedWidth * pCosPhi + pTileJointedHeight * pSinPhi);
        
        if (oldAdjustX < angleAdjustX)
        {
            this.angleAdjustX -= (pTileJointedHeight * pSinTheta); // Compense angle vers gauche
            this.angleAdjustY += (pTileJointedWidth * pSinTheta);  // compense angle vers bas
        }
        if (oldAdjustX > angleAdjustX)
        {
            this.angleAdjustX += (pTileJointedHeight * pSinTheta); // Compense angle vers droite
            this.angleAdjustY -= (pTileJointedWidth * pSinTheta);  // compense angle vers haut
        }
        
        double oldAdjustY = angleAdjustY;
        this.angleAdjustY %= (pTileJointedWidth * pCosPhi + pTileJointedHeight * pSinPhi);
        
        if (oldAdjustY < angleAdjustY)
        {
            this.angleAdjustX -= (pTileJointedHeight * pSinTheta); // Compense angle vers gauche
            this.angleAdjustY -= (pTileJointedWidth * pSinTheta);  // compense angle vers bas
        }
        if (oldAdjustY > angleAdjustY)
        {
            this.angleAdjustX += (pTileJointedHeight * pSinTheta); // Compense angle vers droite
            this.angleAdjustY += (pTileJointedWidth * pSinTheta);  // compense angle vers haut
        }
        
        
        // MAUVAISE COMPARAISON JE DOIS COMPARER AVEC LE MODULO GLOBAL PAS CELUI DU ANGLEADJUST DERP
        if (rotation > 90 && oldAdjustX != angleAdjustX)
        {
            this.angleAdjustX = -1 * angleAdjustX;
            this.angleAdjustY = -1 * angleAdjustY;
        }
        
        if (rotation > 90 && oldAdjustY != angleAdjustY)
        {
            this.angleAdjustX = angleAdjustX * 2;
            this.angleAdjustY = angleAdjustY * 2;
        }
        
        this.modCounterX = newModCounterX;
        this.modCounterY = newModCounterY;
        return offsets;
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
    
    public int getModCounterX()
    {
        return modCounterX;
    }
    
    public int getModCounterY()
    {
        return modCounterY;
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