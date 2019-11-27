package VirtuTuile.Domain;

import VirtuTuile.Infrastructure.Utilities;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.awt.geom.Area;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

/**
 * Classe definissant les tuiles et leur dispositions sur une surface
 * @author gabparrot
 */
public class Covering implements Serializable, Cloneable
{
    private double offsetX = 0;
    private double offsetY = 0;
    private int rowOffset = 0;
    private Color jointColor = Color.GRAY;
    private double jointWidth = 5;
    private boolean isNinetyDegree = false;
    private int rotation = 0;
    private Pattern pattern = Pattern.CHECKERED;
    private transient ArrayList<Area> tiles = new java.util.ArrayList<>();
    private TileType tileType = null;
    private Surface parent;
    
    /**
     * Constructeur standard de covering
     * @param parent La surface qui possède ce covering en attribut
     */
    public Covering(Surface parent)
    {
        this.parent = parent;
        cover();
    }
    
    /**
     * Setter de la surface parente à ce covering
     * @param parent La surface qui possède ce covering en attribut
     */
    public void setParent(Surface parent)
    {
        this.parent = parent;
    }
    
    /**
     * Contructeur clone de Covering
     * @return Objet Covering clôné
     * @throws CloneNotSupportedException explique que clone impossible 
     */
    @Override
    public Object clone() throws CloneNotSupportedException 
    { 
        return super.clone(); 
    } 
    
    /**
     * Prend une tuile qui n'est pas singulière et la divise en des tuiles singulières.
     * @param tile : une tuile non-singulière.
     * @return une liste de tuiles singulières.
     */
    public ArrayList<Area> divideTile(Area tile)
    {
        PathIterator iterator = tile.getPathIterator(null);
        ArrayList<Area> subTiles = new ArrayList<>();
        Path2D.Double path = new Path2D.Double();
        while(!iterator.isDone())
        {
            double[] vertex = new double[2];
            int segmentType = iterator.currentSegment(vertex); 
            switch (segmentType)
            {
                case PathIterator.SEG_MOVETO:
                    path.moveTo(vertex[0], vertex[1]);
                    break;
                case PathIterator.SEG_CLOSE:
                    subTiles.add(new Area(path));
                    path.reset(); 
                    break;
                default:
                    path.lineTo(vertex[0], vertex[1]);
                    break;
            }
            iterator.next();
        }
        return subTiles;
    }
    
    /**
     * Cette fonction est appelée chaque fois que le Covering doit être modifié.
     */
    public final void cover()
    {
        tiles.clear();
        if (parent.isHole() || tileType == null)
        {
            return;
        }
        switch (pattern)
        {
            case CHECKERED:
                coverSurfaceA();
                break;
                
            case LSHAPE:
                coverSurfaceB();
                break;
            
            case TWOBYTWO:
                coverSurfaceC();
                break;
            
            case DIAGONAL:
                coverSurfaceD();
                break;     
        }
    }
    
    /*
    Plan rotation
    - Retirer  option de 90', isNinetyDegree des attributs
    - Ajouter attributs rotation
    - Si rotation est entre 0' et 45', ou entre 315' et 360', largeur est hauteur same, sinon on flip la valeur
    - Appliquer un AffineTransform de rotation
    - Dans le GUI, le mouseover doit aussi considérer les degrés pour switch width/height
    - ajouter getter setter de rotation
    */
    
    private void coverSurfaceA()
    {
        // Position
        double tileWidth =  rotation >= 90 ? tileType.getHeight() : tileType.getWidth();
        double tileHeight = rotation >= 90 ? tileType.getWidth() : tileType.getHeight();
        double offsetXMod = this.offsetX % (tileWidth + jointWidth);
        double offsetYMod = this.offsetY % (tileHeight + jointWidth);
        double rowOffsetMod = tileWidth - rowOffset / 100. * tileWidth -  jointWidth * rowOffset / 100;
        
        // Définition de l'aire à couvrir
        Area fullArea = new Area(parent);
        Rectangle2D bounds = fullArea.getBounds2D();
        


        if (parent instanceof CombinedSurface)
        {
            fullArea.subtract(((CombinedSurface) parent).getUncoveredArea());
        }
        Area innerArea = getInnerArea(fullArea);
        
        // Ancrages de rotation
        double anchorX = innerArea.getBounds2D().getCenterX();
        double anchorY = innerArea.getBounds2D().getCenterY();
        
        
        // Détermine si on devrait commencer par une rangée paire ou impaire
        int rowCount = 0;
        boolean shouldInvertRow = shouldInvertRow(tileHeight);
        
        
        boolean allCovered = false;
        //int newTileCounter = 0;
        int columnCounter = 0;
        int rowCounter = 0;
        
        // Translation causée par la rotation
        double rotationXOffset = 0;
        double rotationYOffset = 0;
        if (rotation != 90 && rotation != 90)
        {
        rotationXOffset = Math.abs(Math.sin(Math.toRadians(rotation)) * tileHeight);
        rotationYOffset = Math.abs(Math.cos(Math.toRadians(rotation)) * tileHeight);
        }
        
        // Nombre de rangées/colonnes maximum qui rentre dans une surface
        int normalColumns = (int) Math.ceil(bounds.getWidth() / (tileWidth + jointWidth));
        double normalRows = (int) Math.ceil(bounds.getHeight() / (tileHeight + jointWidth));
        
        // Ajuster le maximum de rangées/colonnes en fonction de la translation de rotation s'il y a lieu
        int maxColumns = normalColumns;
        if (rotation > 0 && rotation != 90)
        {
        maxColumns = (int) Math.ceil(bounds.getWidth() / rotationXOffset);
        }
        
        int maxRows = (int) normalRows;
        if (rotation > 0 && rotation != 90)
        {
        maxRows = (int) Math.ceil(bounds.getHeight() / rotationYOffset);
        }
        
        // Nombre de rangées/colonnes additionnelles maximum causées par la rotation
        int bonusColumns = (int) Math.ceil(maxColumns - normalColumns);
        int bonusRows = (int) Math.ceil(maxRows - normalColumns);
        
        // Départ du recouvrement
        Point2D.Double currentPoint = new Point2D.Double(bounds.getX() - tileWidth  - tileWidth * (bonusColumns + jointWidth) + offsetXMod,
                                                         bounds.getY() - tileHeight - tileHeight * (bonusRows + jointWidth) + offsetYMod);
        
        // Double l'écart pour l'appliquer avant et après les rangées/colonnes normales
        maxColumns = maxColumns + bonusColumns; 
        maxRows = maxRows + bonusRows;
        
        //TEST
        System.out.println("rotationYOffset: " + rotationYOffset + ", rotationXOffset: " + rotationXOffset + ", maxColumns: " + maxColumns + ", maxRows: " + maxRows);
        
        if (shouldInvertRow)
        {
            rowCount++;
            currentPoint.x -= rowOffsetMod + jointWidth;
        }
        // Créer les tuiles et les ajouter à la liste
        while (!(allCovered))
        {
            Point2D.Double tileTopLeft = currentPoint;
            Point2D.Double tileBotRight = new Point2D.Double(currentPoint.x + tileWidth,
                                                             currentPoint.y + tileHeight);
            
            Area tile = new Area(Utilities.cornersToRectangle(tileTopLeft, tileBotRight));
            

            if (rotation != 0 && rotation != 90)
            {
                rotateTile(tile, anchorX, anchorY);
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
                    ArrayList<Area> subTiles = divideTile(tile);
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
            
            if (columnCounter <= maxColumns)
            {
                currentPoint.x += tileWidth + jointWidth;
                columnCounter++;
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
                    currentPoint.setLocation(bounds.getX() - tileWidth  - tileWidth * (bonusColumns + jointWidth) + offsetXMod, 
                                             currentPoint.y + tileHeight + jointWidth);
                }
                else  // Décaler les rangées paires (celle du haut est #1)
                {
                    currentPoint.setLocation(bounds.getX() - tileWidth  - tileWidth * (bonusColumns + jointWidth) - rowOffsetMod - jointWidth,
                                             currentPoint.y + tileHeight + jointWidth);

                }
                
                if (rowCounter > maxRows)
                {
                    System.out.println("rowCounter: " + rowCounter);
                    allCovered = true;
                }
                
                //newTileCounter = 0;
                rowCount++;
            }
        }
    }
    
    
    /**
     * Permet de placer les tuiles sur le covering, en rangées traditionnelles. Les tuiles suivent le modèle du type de 
     * tuile du covering, l'orientation, le décalage. Ce motif prend en compte le décalage de rangée
     *//*
    private void coverSurfaceAOLD()
    {
        // Position et orientation
        double tileWidth = isNinetyDegree ? tileType.getHeight() : tileType.getWidth();
        double tileHeight = isNinetyDegree ? tileType.getWidth() : tileType.getHeight();
        double offsetXMod = this.offsetX % (tileWidth + jointWidth);
        double offsetYMod = this.offsetY % (tileHeight + jointWidth);
        double rowOffsetMod = tileWidth - rowOffset / 100. * tileWidth -  jointWidth * rowOffset / 100;
        
        // Définition de l'aire à couvrir
        Area fullArea = new Area(parent);
        Rectangle2D bounds = fullArea.getBounds2D();
        if (parent instanceof CombinedSurface)
        {
            fullArea.subtract(((CombinedSurface) parent).getUncoveredArea());
        }
        Area innerArea = getInnerArea(fullArea);
        
        Point2D.Double currentPoint = new Point2D.Double(bounds.getX() - tileWidth + offsetXMod,
                                                         bounds.getY() - tileHeight + offsetYMod);
        
        // Détermine si on devrait commencer par une rangée paire ou impaire
        int rowCount = 0;
        boolean shouldInvertRow = shouldInvertRow(tileHeight);
        if (shouldInvertRow)
        {
            rowCount++;
            currentPoint.x -= rowOffsetMod + jointWidth;
        }
        
        while (currentPoint.getX() < bounds.getMaxX() && currentPoint.y < bounds.getMaxY())
        {
            Point2D.Double tileTopLeft = currentPoint;
            Point2D.Double tileBotRight = new Point2D.Double(currentPoint.x + tileWidth,
                                                             currentPoint.y + tileHeight);
            
            Area tile = new Area(Utilities.cornersToRectangle(tileTopLeft, tileBotRight));
            tile.intersect(innerArea);
            if (!tile.isEmpty())
            {
                if (tile.isSingular())
                {
                    tiles.add(tile);
                }
                else
                {
                    ArrayList<Area> subTiles = divideTile(tile);
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
            
            if (currentPoint.x + tileWidth + jointWidth < bounds.getMaxX())
            {
                currentPoint.x += tileWidth + jointWidth;
            }
            else // Si on dépasse en X, on descend et on repart a gauche.
            {
                if (rowCount % 2 == 1)
                {
                    currentPoint.setLocation(bounds.getX() - tileWidth + offsetXMod, 
                                             currentPoint.y + tileHeight + jointWidth);
                }
                // Décaler les rangées paires (celle du haut est #1)
                else
                {
                    currentPoint.setLocation(bounds.getX() - tileWidth + offsetXMod - rowOffsetMod - jointWidth,
                                             currentPoint.y + tileHeight + jointWidth);                    
                }
                rowCount++;
            }
        }
    }*/
    
    /**
     * Crée et place les tuiles sur le covering, selon un motif croisé en L, pour chaque tuile. Le décalage de rangée
     * est ignoré. Le tuiles suivent le modèle du tileType, l'orientation et le décalage en attribut dans le covering
     */
    private void coverSurfaceB()
    {
    }
    
    /**
     * Crée et place les tuiles sur le covering, selon un motif , par paire de tuiles. Le décalage de rangée
     * est considéré, par bloc de 2x2 tuiles. Le tuiles suivent le modèle du tileType, l'orientation et le décalage 
     * en attribut dans le covering
     */
    private void coverSurfaceC()
    {
    }
    
    /**
     * Crée et place les tuiles sur le covering, selon des rangées en diagonale. Le décalage de rangée
     * est considéré. Le tuiles suivent le modèle du tileType, l'orientation et le décalage en attribut dans le covering
     */
    private void coverSurfaceD()
    {
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
     * Prend une aire et enlève une bordure sur le périmètre.
     * @param fullArea : l'aire originale.
     * @return une nouvelle aire, plus petite, avec une bordure de moins.
     */
    private Area getInnerArea(Area fullArea)
    {
        Area innerArea = new Area(fullArea);
        
        Area topLeftCopy = new Area(fullArea);
        AffineTransform translate = new AffineTransform();
        translate.translate(-jointWidth, -jointWidth);
        topLeftCopy.transform(translate);
        
        Area topRightCopy = new Area(fullArea);
        translate = new AffineTransform();
        translate.translate(jointWidth, -jointWidth);
        topRightCopy.transform(translate);
        
        Area botLeftCopy = new Area(fullArea);
        translate = new AffineTransform();
        translate.translate(-jointWidth, jointWidth);
        botLeftCopy.transform(translate);
        
        Area botRightCopy = new Area(fullArea);
        translate = new AffineTransform();
        translate.translate(jointWidth, jointWidth);
        botRightCopy.transform(translate);
        
        innerArea.intersect(topLeftCopy);
        innerArea.intersect(topRightCopy);
        innerArea.intersect(botLeftCopy);
        innerArea.intersect(botRightCopy);
        
        return innerArea;
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
    
    /**
     * Retourne le nombre de tuiles du couvrement.
     * @return le nombre de tuiles du couvrement.
     */
    public int getNbTiles()
    {
        return tiles.size();
    }
    
    /**
     * Retourne le décalage horizontal du couvrement.
     * @return : le décalage horizontal du couvrement.
     */
    public double getOffsetX()
    {
        return offsetX;
    }

    /**
     * Change le décalage horizontal du couvrement.
     * @param offsetX : le nouveau décalage horizontal.
     */
    public void setOffsetX(double offsetX)
    {
        this.offsetX = offsetX;
        cover();
    }

    /**
     * Retourne le décalage vertical du couvrement.
     * @return le décalage vertical du couvrement.
     */
    public double getOffsetY()
    {
        return offsetY;
    }

    /**
     * Change le décalage vertical du couvrement.
     * @param offsetY : le nouveau décalage vertical.
     */
    public void setOffsetY(double offsetY)
    {
        this.offsetY = offsetY;
        cover();
    }

    /**
     * Retourne la couleur des joints du couvrement.
     * @return la couleur des joints du couvrement.
     */
    public Color getJointColor()
    {
        return jointColor;
    }

    /**
     * Change la couleur des joints du couvrement.
     * @param jointColor : la nouvelle couleur des joints.
     */
    public void setJointColor(Color jointColor)
    {
        this.jointColor = jointColor;
        cover();
    }

    /**
     * Retourne la largeur des joints du couvrement.
     * @return la largeur des joints du recouvrement.
     */
    public double getJointWidth()
    {
        return jointWidth;
    }

    /**
     * Change la largeur des joints du couvrement.
     * @param jointWidth la nouvelle largeur des joints.
     */
    public void setJointWidth(double jointWidth)
    {
        this.jointWidth = jointWidth;
        cover();
    }

    /**
     * Retourne true si l'orientation est à 90 degrés, false si elle est à 0 degré.
     * @return true si l'orientation est à 90 degrés, false si elle est à 0 degré.
     */
    public boolean isNinetyDegree()
    {
        return isNinetyDegree;
    }

    /**
     * Change l'orientation des tuiles du couvrement.
     * @param isNinetyDegree : true pour une orientation de 90 degrés, false pour 0 degré.
     */
    public void setIsNinetyDegree(boolean isNinetyDegree)
    {
        this.isNinetyDegree = isNinetyDegree;
        cover();
    } 

    /**
     * Retourne le motif du couvrement.
     * @return le motif du couvrement.
     */
    public Pattern getPattern()
    {
        return pattern;
    }

    /**
     * Change le motif du couvrement.
     * @param pattern : le nouveau motif.
     */
    public void setPattern(Pattern pattern)
    {
        this.pattern = pattern;
        cover();
    }
    
    /**
     * Retourne le segmentType de tuile du couvrement.
     * @return le segmentType de tuile du couvrement.
     */
    public TileType getTileType()
    {
        return tileType;
    }
    
    /**
     * Change le segmentType de tuile du couvrement.
     * @param tileType : le nouveau segmentType de tuile.
     */
    public void setTileType(TileType tileType)
    {
        this.tileType = tileType;
        cover();
    }

    /**
     * Retourne les tuiles du couvrement.
     * @return les tuiles du couvrement.
     */
    public java.util.ArrayList<Area> getTiles()
    {
        return tiles;
    }

    /**
     * Change l'offset (en pourcentage) entre les rangées paires et impaires.
     * @param rowOffset : le nouvel offset en pourcentage entre les rangées.
     */
    public void setRowOffset(int rowOffset)
    {
        this.rowOffset = rowOffset;
        cover();
    }
    
    /**
     * Retourne l'offset (en pourcentage) entre les rangées paires et impaires.
     * @return : le nouvel offset en pourcentage entre les rangées.
     */
    public int getRowOffset()
    {
        return rowOffset;
    }
    
    /**
     * Retourne la rotation des tuiles, un entier représentant les degrés
     * @return rotation des tuiles en degrés
     */
    public int getRotation()
    {
        return rotation;
    }
    
    /**
     * Change la rotation pour le degré entier demandé. Si entre 180 et 360, ramene entre 0 et 180 car même effet
     * @param degreesOfRotation Rotation à appliquer en degrés
     */
    public void setRotation(int degreesOfRotation)
    {
        //TEST
        System.out.println("Setter de rotation reçoit en param: " + degreesOfRotation);
        if (degreesOfRotation > 360) 
        {
            degreesOfRotation = degreesOfRotation % 360;
        }
        if (degreesOfRotation < 0) // si négative, rotation positive direction inverse
        {
            degreesOfRotation = 360 + degreesOfRotation;
        }
        if (degreesOfRotation == 360 || degreesOfRotation == 180)
        {
            this.rotation = 0;
        }
        else if (degreesOfRotation > 180)
        {
            this.rotation = degreesOfRotation - 180;
        }
        else
        {
            this.rotation = degreesOfRotation;
        }
    }
    
    /**
     * Permet le chargement d'un covering à partir d'une sauvegarde
     * @param in L'inputStream à lire
     * @throws IOException Erreur de lecture du fichier
     * @throws ClassNotFoundException Classe inconnue trouvée dans le fichier
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        tiles = new java.util.ArrayList<>();
        in.defaultReadObject();
        cover();
    }
}