package VirtuTuile.Domain;

import VirtuTuile.Domain.CoveringStrategies.CoveringStrategy;
import VirtuTuile.Domain.CoveringStrategies.LShapeCoveringStrategy;
import VirtuTuile.Domain.CoveringStrategies.DiagonalCoveringStrategy;
import VirtuTuile.Domain.CoveringStrategies.CheckeredCoveringStrategy;
import VirtuTuile.Domain.CoveringStrategies.TwoByTwoCoveringStrategy;
import java.awt.Color;
import java.io.Serializable;
import java.awt.geom.Area;
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
    private int rotation = 45;
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
     * Cette fonction est appelée chaque fois que le Covering doit être modifié.
     */
    public final void cover()
    {
        tiles.clear();
        if (parent.isHole() || tileType == null)
        {
            return;
        }
        CoveringStrategy coveringStrategy;
        Area fullArea = new Area(parent);
        if (parent instanceof CombinedSurface)
        {
            fullArea.subtract(new Area(((CombinedSurface) parent).getUncoveredPath()));
        }
        switch (pattern)
        {
            case CHECKERED:
                coveringStrategy = new CheckeredCoveringStrategy
                        (tileType.getWidth(), tileType.getHeight(), jointWidth, offsetX,
                        offsetY, rowOffset, isNinetyDegree, fullArea);
                break;
            case LSHAPE:
                coveringStrategy = new LShapeCoveringStrategy(
                        tileType.getWidth(), tileType.getHeight(), jointWidth,
                        offsetX, offsetY, fullArea);
                break;
            case TWOBYTWO:
                coveringStrategy = new TwoByTwoCoveringStrategy(
                        tileType.getWidth(), tileType.getHeight(), jointWidth,
                        offsetX, offsetY, fullArea);
                break;
            case DIAGONAL:
                switch (rotation)
                {
                    case 0: 
                        coveringStrategy = new CheckeredCoveringStrategy(tileType.getWidth(), tileType.getHeight(),
                                jointWidth, offsetX,offsetY, rowOffset, isNinetyDegree, fullArea);
                        break;
                    case 90:
                        isNinetyDegree = true;
                        coveringStrategy = new CheckeredCoveringStrategy(tileType.getWidth(), tileType.getHeight(), 
                                jointWidth, offsetX,offsetY, rowOffset, isNinetyDegree, fullArea);
                        isNinetyDegree = false;
                        break;
                    default:
                        if (isNinetyDegree) // Inverser width et height et appliquer degrés indépendamment
                        {
                            coveringStrategy = new DiagonalCoveringStrategy(tileType.getHeight(), tileType.getWidth(),
                                jointWidth, offsetX, offsetY, rowOffset, fullArea, rotation);
                        }
                        else
                        {
                            coveringStrategy = new DiagonalCoveringStrategy(tileType.getWidth(), tileType.getHeight(),
                            jointWidth, offsetX, offsetY, rowOffset, fullArea, rotation);
                        }
                }
                break;
            default:
                throw new IllegalStateException();
        }
        tiles = coveringStrategy.cover();
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
        this.rotation = Math.abs(degreesOfRotation) % 180;
        cover();
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
    }
}