package VirtuTuile.GUI;

import VirtuTuile.Domain.Pattern;
import VirtuTuile.Infrastructure.Utilities;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * La fenêtre principale de l'application.
 */
public class MainWindow extends JFrame
{

    // Le controller de l'application.
    public VirtuTuile.Domain.Controller controller;

    // Enumération des différents modes de l'application.
    private enum ApplicationModes
    {
        SELECT, SURFACEMOVE, RECTANGLE, CIRCLE, POLYGON, MERGE, TILEMOVE ,NONE;
    }

    private enum ContextMenuModes
    {
        ALIGN_LEFT, ALIGN_RIGHT, ALIGN_TOP, ALIGN_BOTTOM,
        CENTER_H, CENTER_V, STICK_H, STICK_V, RELATIVE_MOVE, NONE;
    }

    private final String patternA = "Quadrillé";
    private final String patternB = "L-shape";
    private final String patternC = "Deux-par-deux";
    private final String patternD = "Diagonal";
    
    private ContextMenuModes contextMode = ContextMenuModes.NONE;
    private ApplicationModes selectedMode = ApplicationModes.NONE;

    private Point2D.Double firstRectangleCorner = null;
    private Point2D.Double firstEllipseCorner = null;
    private Path2D.Double polygonInProgress = null;
    private int numberVertices = 0;

    // Un point d'origine pour le déplacement de surface. Décrit la différence x et y entre
    // le coin supérieur-gauche de la surface et le clic intérieur de la surface.
    private Point2D.Double originPoint = null;

    // Si le projet fonctionne en mesure métrique ou impérial.
    private boolean isMetric = true;

    // Si la grille est magnétique en ce moment.
    private boolean gridIsMagnetic = true;
    
    private boolean movingVertex = false;
    
    // La couleur utilisé pour les boutons désactivés.
    private final Color disabledColor = new Color(240, 240, 240);
    
    FileNameExtensionFilter filter = new FileNameExtensionFilter("SER File", "ser");

    /**
     * Constructeur.
     */
    public MainWindow()
    {
        controller = new VirtuTuile.Domain.Controller();
        initComponents();
        canvasPanel.assignControllerToDrawer(controller);
        surfaceXFieldInches.setVisible(false);
        xInLabel.setVisible(false);
        surfaceYFieldInches.setVisible(false);
        yInLabel.setVisible(false);
        surfaceWidthFieldInches.setVisible(false);
        widthInLabel.setVisible(false);
        surfaceHeightFieldInches.setVisible(false);
        heightInLabel.setVisible(false);
        toolBar.setVisible(false);
        createTileWindow.setVisible(false);
    }
    
    private void unselectSurface()
    {
        controller.unselectSurface();
        disableSurfaceButtons();
        disableCoveringButtons();
        disableTileTypeButtons();
        unselect();
    }
    
    private void unselect()
    {
        contextMode = ContextMenuModes.NONE;
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        firstRectangleCorner = null;
        firstEllipseCorner = null;
        polygonInProgress = null;
        canvasPanel.setTemporaryRectangle(null);
        canvasPanel.setTemporaryEllipse(null);
        canvasPanel.setTemporaryPolygon(null);
        canvasPanel.setTemporaryLine(null);
        numberVertices = 0;
        canvasPanel.repaint();
    }
    
    private void disableSurfaceButtons()
    {
        surfaceXField.setText("");
        surfaceXField.setEnabled(false);
        surfaceXFieldInches.setText("");
        surfaceXFieldInches.setEnabled(false);
        
        surfaceYField.setText("");
        surfaceYField.setEnabled(false);
        surfaceYFieldInches.setText("");
        surfaceYFieldInches.setEnabled(false);
        
        surfaceWidthField.setText("");
        surfaceWidthField.setEnabled(false);
        surfaceWidthFieldInches.setText("");
        surfaceWidthFieldInches.setEnabled(false);
        
        surfaceHeightField.setText("");
        surfaceHeightField.setEnabled(false);
        surfaceHeightFieldInches.setText("");
        surfaceHeightFieldInches.setEnabled(false);
        
        surfaceColorButton.setBackground(disabledColor);
        surfaceColorButton.setEnabled(false);
        
        coverButtonGroup.clearSelection();
        doNotCoverRadioButton.setEnabled(false);
        coverRadioButton.setEnabled(false);
    }
    
    private void disableCoveringButtons()
    {
        tileTypeComboBox.setSelectedItem(null);
        tileTypeComboBox.setEnabled(false);
        
        patternComboBox.setSelectedItem(null);
        patternComboBox.setEnabled(false);
        
        jointColorButton.setBackground(disabledColor);
        jointColorButton.setEnabled(false);
        
        jointWidthField.setText("");
        jointWidthField.setEnabled(false);
        
        orientationGroup.clearSelection();
        ninetyOrientationRadioButton.setEnabled(false);
        zeroOrientationRadioButton.setEnabled(false);
        
        offsetXField.setText("");
        offsetXField.setEnabled(false);
        
        offsetYField.setText("");
        offsetYField.setEnabled(false);
        
        rowOffsetField.setText("");
        rowOffsetField.setEnabled(false);
        
        rotationTextField.setText("");
        rotationTextField.setEnabled(false);
    }
    
    private void disableTileTypeButtons()
    {
        nbTilesOnSurfaceLabels.setText("0 tuiles");
        nbBoxesOnSurfaceLabel.setText("0 boîtes");
        
        tileNameField.setText("");
        tileNameField.setEnabled(false);
        
        tileColorButton.setBackground(disabledColor);
        tileColorButton.setEnabled(false);
        
        tileWidthField.setText("");
        tileWidthField.setEnabled(false);
        
        tileHeightField.setText("");
        tileHeightField.setEnabled(false);
        
        tileNbPerBoxField.setEnabled(false);
        tileNbPerBoxField.setText("");
    }

    private void updatePanelInformation()
    {        
        updateSurfacePanel();
        updateCoveringPanel();
        updateTileTypePanel();
    }
    
    private void updateSurfacePanel()
    {
        Rectangle2D bounds = controller.getBounds2D();
        if (isMetric)
        {
            surfaceXField.setText(String.format("%.03f", bounds.getX() / 1000.));
            surfaceYField.setText(String.format("%.03f", bounds.getY() / 1000.));
            surfaceWidthField.setText(String.format("%.03f", bounds.getWidth() / 1000.));
            surfaceHeightField.setText(String.format("%.03f", bounds.getHeight() / 1000.));
        }
        else
        {
            surfaceXField.setText(String.valueOf(Utilities.mmToFeet(bounds.getX())));
            surfaceXFieldInches.setText(String.format("%.02f",
                    Utilities.mmToRemainingInches(bounds.getX())));
            surfaceYField.setText(String.valueOf(Utilities.mmToFeet(bounds.getY())));
            surfaceYFieldInches.setText(String.format("%.02f",
                    Utilities.mmToRemainingInches(bounds.getY())));
            surfaceWidthField.setText(String.valueOf(Utilities.mmToFeet(bounds.getWidth())));
            surfaceWidthFieldInches.setText(String.format("%.02f",
                    Utilities.mmToRemainingInches(bounds.getWidth())));
            surfaceHeightField.setText(String.valueOf(Utilities.mmToFeet(bounds.getHeight())));
            surfaceHeightFieldInches.setText(String.format("%.02f",
                    Utilities.mmToRemainingInches(bounds.getHeight())));
        }
        surfaceColorButton.setBackground(controller.getColor());
        if (controller.isHole())
        {
            doNotCoverRadioButton.setSelected(true);
        }
        else
        {
            coverRadioButton.setSelected(true);
        }
    }
    
    private void updateCoveringPanel()
    {
        if (isMetric)
        {
            jointWidthField.setText(String.format("%.03f", controller.getJointWidth()));
            offsetXField.setText(String.format("%.03f", controller.getOffsetX() / 10));
            offsetYField.setText(String.format("%.03f", controller.getOffsetY() / 10));
        }
        else
        {
            jointWidthField.setText(String.format("%.03f",
                    Utilities.mmToInches(controller.getJointWidth())));
            offsetXField.setText(String.format("%.03f",
                    Utilities.mmToInches(controller.getOffsetX())));
            offsetYField.setText(String.format("%.03f",
                    Utilities.mmToInches(controller.getOffsetY())));
        }
        rowOffsetField.setText(String.format("" + controller.getRowOffset()));
        if (controller.isNinetyDegree())
        {
            ninetyOrientationRadioButton.setSelected(true);
        }
        else
        {
            zeroOrientationRadioButton.setSelected(true);
        }
        
        updatePatternComboBox();
        
        jointColorButton.setBackground(controller.getJointColor());
        DefaultComboBoxModel model = new DefaultComboBoxModel(controller.getTileNames());
        tileTypeComboBox.setModel(model);
        rotationTextField.setText("" + controller.getRotation());
    }
    
    private void updatePatternComboBox()
    {
        String patternString;
        switch (controller.getPattern())
        {
            case CHECKERED:
                patternString = patternA; break;
            case LSHAPE:
                patternString = patternB; break;
            case TWOBYTWO:
                patternString = patternC; break;
            default:
                patternString = patternD;
        }
        patternComboBox.setSelectedItem(patternString);
    }
    
    private void updateTileTypePanel()
    {
        if (controller.hasTileType())
        {
            nbTilesOnSurfaceLabels.setText(controller.getTileQuantity() + " tuiles");
            int boxesNeeded = (int) Math.ceil((double) controller.getTileQuantity() / controller.getTileNbPerBox());
            nbBoxesOnSurfaceLabel.setText(boxesNeeded + " boîtes");
            enableTileTypePanelButtons();
            tileTypeComboBox.setSelectedItem(controller.getTileName());
            if (isMetric)
            {
                tileWidthField.setText(String.format("%.03f", controller.getTileWidth() / 10));
                tileHeightField.setText(String.format("%.03f", controller.getTileHeight() / 10));
            }
            else
            {
                tileWidthField.setText(String.format("%.03f",
                        Utilities.mmToInches(controller.getTileWidth())));
                tileHeightField.setText(String.format("%.03f",
                        Utilities.mmToInches(controller.getTileHeight())));
            }
            tileNameField.setText(controller.getTileName());
            tileNbPerBoxField.setText(String.format("%d", controller.getTileNbPerBox()));
            tileColorButton.setBackground(controller.getTileColor());
        }
        else
        {
            nbTilesOnSurfaceLabels.setText("0 tuiles");
            nbBoxesOnSurfaceLabel.setText("0 boîtes");
            tileTypeComboBox.setSelectedItem(null);
            disableTileTypeButtons();
        }
    }

    private void enablePanelButtons()
    {
        enableSurfacePanelButtons();
        enableCoveringPanelButtons();
        if (controller.hasTileType())
        {
            enableTileTypePanelButtons();
        }
    }
    
    private void enableSurfacePanelButtons()
    {
        surfaceXField.setEnabled(true);
        surfaceXFieldInches.setEnabled(true);
        surfaceYField.setEnabled(true);
        surfaceYFieldInches.setEnabled(true);
        surfaceWidthField.setEnabled(true);
        surfaceWidthFieldInches.setEnabled(true);
        surfaceHeightField.setEnabled(true);
        surfaceHeightFieldInches.setEnabled(true);
        surfaceColorButton.setEnabled(true);
        coverRadioButton.setEnabled(true);
        doNotCoverRadioButton.setEnabled(true);
    }
    
    private void enableCoveringPanelButtons()
    {
        tileTypeComboBox.setEnabled(true);
        jointWidthField.setEnabled(true);
        jointColorButton.setEnabled(true);
        patternComboBox.setEnabled(true);
        offsetXField.setEnabled(true);
        offsetYField.setEnabled(true);
        rowOffsetField.setEnabled(true);
        zeroOrientationRadioButton.setEnabled(true);
        ninetyOrientationRadioButton.setEnabled(true);
        rotationTextField.setEnabled(true);

        switch (controller.getPattern())
        {
            case CHECKERED:
                
                jointWidthField.setEditable(true);
                updateFieldVisibilityCheckered();
                break;
            case DIAGONAL:
                jointWidthField.setEditable(true);
                updateFieldVisibilityDiagonal();
                break;
            case LSHAPE:
                jointWidthField.setEditable(false);
                updateFieldVisibilityLShape();
                break;
            case TWOBYTWO:
                jointWidthField.setEditable(false);
                updateFieldVisibilityTwoByTwo();
                break;
        }
    }
    
    private void updateFieldVisibilityCheckered()
    {
        orientationLabel.setVisible(true);
        zeroOrientationRadioButton.setVisible(true);
        ninetyOrientationRadioButton.setVisible(true);
        
        rowOffsetLabel1.setVisible(true);
        rowOffsetField.setVisible(true);
        rowOffsetLabel2.setVisible(true);
        
        rotationLabel1.setVisible(false);
        rotationTextField.setVisible(false);
        rotationLabel2.setVisible(false);
    }
    
    private void updateFieldVisibilityLShape()
    {
        orientationLabel.setVisible(false);
        zeroOrientationRadioButton.setVisible(false);
        ninetyOrientationRadioButton.setVisible(false);
        
        rowOffsetLabel1.setVisible(false);
        rowOffsetField.setVisible(false);
        rowOffsetLabel2.setVisible(false);
        
        rotationLabel1.setVisible(false);
        rotationTextField.setVisible(false);
        rotationLabel2.setVisible(false);
    }
    
    private void updateFieldVisibilityTwoByTwo()
    {
        orientationLabel.setVisible(false);
        zeroOrientationRadioButton.setVisible(false);
        ninetyOrientationRadioButton.setVisible(false);
        
        rowOffsetLabel1.setVisible(false);
        rowOffsetField.setVisible(false);
        rowOffsetLabel2.setVisible(false);
        
        rotationLabel1.setVisible(false);
        rotationTextField.setVisible(false);
        rotationLabel2.setVisible(false);
    }
    
    private void updateFieldVisibilityDiagonal()
    {
        orientationLabel.setVisible(false);
        zeroOrientationRadioButton.setVisible(false);
        ninetyOrientationRadioButton.setVisible(false);
        
        rowOffsetLabel1.setVisible(true);
        rowOffsetField.setVisible(true);
        rowOffsetLabel2.setVisible(true);
        
        rotationLabel1.setVisible(true);
        rotationTextField.setVisible(true);
        rotationLabel2.setVisible(true);
    }
    
    private void enableTileTypePanelButtons()
    {
        tileNameField.setEnabled(true);
        tileWidthField.setEnabled(true);
        tileHeightField.setEnabled(true);
        tileColorButton.setEnabled(true);
        tileNbPerBoxField.setEnabled(true);
        
        if (controller.tileTypeDimensionsAreLocked())
        {
            tileWidthField.setEditable(false);
            tileHeightField.setEditable(false);
        }
        else
        {
            tileWidthField.setEditable(true);
            tileHeightField.setEditable(true);
        }
    }

    /**
     * Update les deux scrollbars.
     */
    public void updateScrollbars()
    {
        Point2D.Double farthestPoint = controller.getFarthestPoint();
        verticalScrollBar.setMaximum(Math.max(Math.max(canvasPanel.getHeight() + 100,
                (int) ((farthestPoint.y / Utilities.MM_PER_PIXEL + 100) * canvasPanel.getZoom())),
                verticalScrollBar.getValue() + verticalScrollBar.getVisibleAmount() + 100));
        verticalScrollBar.setVisibleAmount(canvasPanel.getHeight());
        horizontalScrollBar.setMaximum(Math.max(Math.max(canvasPanel.getWidth() + 100,
                (int) ((farthestPoint.x / Utilities.MM_PER_PIXEL + 100) * canvasPanel.getZoom())),
                horizontalScrollBar.getValue() + horizontalScrollBar.getVisibleAmount() + 100));
        horizontalScrollBar.setVisibleAmount(canvasPanel.getWidth());
    }

    /**
     * Prend un Point en pixels et retourne un Point en métrique.
     *
     * @param point : un Point représentant des coordonnées en pixels.
     * @return un Point représentant des millimètres.
     */
    private Point2D.Double pointToMetric(Point point)
    {
        int posXPixels = point.x + canvasPanel.getHorizontalOffset();
        int posYPixels = point.y + canvasPanel.getVerticalOffset();
        double posXMetric = Utilities.pixelsToMm(posXPixels, canvasPanel.getZoom());
        double posYMetric = Utilities.pixelsToMm(posYPixels, canvasPanel.getZoom());
        return new Point2D.Double(posXMetric, posYMetric);
    }
    
    private void showTileInfo(Point2D.Double dimensions)
    {
        double tileWidth = dimensions.x;
        double tileHeight = dimensions.y;
        if (tileWidth == 0 || tileHeight == 0)
        {
            tileInfoWidth.setText("Largeur : ");
            tileInfoHeight.setText("Hauteur : ");
            return;
        }
        if (isMetric)
        {            
            tileInfoWidth.setText("Largeur : " + String.format("%.03f", tileWidth / 10) + " cm");
            tileInfoHeight.setText("Hauteur : " + String.format("%.03f", tileHeight / 10) + " cm");         
        }
        else
        {
            double tileWidthImp = Utilities.mmToInches(tileWidth);
            double tileHeightImp = Utilities.mmToInches(tileHeight);
            tileInfoWidth.setText("Largeur : " + String.format("%.03f", tileWidthImp) + " pouces");
            tileInfoHeight.setText("Hauteur : " + String.format("%.03f", tileHeightImp) + " pouces");         
        }                
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT
     * modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        java.awt.GridBagConstraints gridBagConstraints;

        menuBar = new javax.swing.JMenu();
        toggleGroup = new javax.swing.ButtonGroup();
        coverButtonGroup = new javax.swing.ButtonGroup();
        gridDistanceDialog = new javax.swing.JDialog();
        gridDistanceTopLabel = new javax.swing.JLabel();
        gridDistanceSlider = new javax.swing.JSlider();
        gridDistanceLabel = new javax.swing.JLabel();
        gridDistanceOKButton = new javax.swing.JButton();
        imperialGridDistanceDialog = new javax.swing.JDialog();
        imperialGridDistanceTopLabel = new javax.swing.JLabel();
        imperialGridDistanceSlider = new javax.swing.JSlider();
        imperialGridDistanceLabel = new javax.swing.JLabel();
        imperialGridDistanceOKButton = new javax.swing.JButton();
        surfacePopupMenu = new javax.swing.JPopupMenu();
        relativeMoveMenuItem = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JPopupMenu.Separator();
        pushSubMenu = new javax.swing.JMenu();
        pushTopMenuItem = new javax.swing.JMenuItem();
        pushBottomMenuItem = new javax.swing.JMenuItem();
        pushLeftJMenuItem = new javax.swing.JMenuItem();
        pushRightMenuItem = new javax.swing.JMenuItem();
        stickSubMenu = new javax.swing.JMenu();
        stickHMenuItem = new javax.swing.JMenuItem();
        stickVMenuItem = new javax.swing.JMenuItem();
        alignSubMenu = new javax.swing.JMenu();
        alignTopMenuItem = new javax.swing.JMenuItem();
        alignBottomMenuItem = new javax.swing.JMenuItem();
        alignLeftMenuItem = new javax.swing.JMenuItem();
        alignRightMenuItem = new javax.swing.JMenuItem();
        centerSubMenu = new javax.swing.JMenu();
        centerHMenuItem = new javax.swing.JMenuItem();
        centerVMenuItem = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JPopupMenu.Separator();
        startPatternSubMenu = new javax.swing.JMenu();
        startPatternFullTileMenuItem = new javax.swing.JMenuItem();
        startColumnFullTileMenuItem = new javax.swing.JMenuItem();
        startRowFullTileMenuItem = new javax.swing.JMenuItem();
        centerPatternSubMenu = new javax.swing.JMenu();
        centerPatternHMenuItem = new javax.swing.JMenuItem();
        centerPatternVMenuItem = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        decombineMenuItem = new javax.swing.JMenuItem();
        deleteSurfaceMenuItem = new javax.swing.JMenuItem();
        orientationGroup = new javax.swing.ButtonGroup();
        measureGroup = new javax.swing.ButtonGroup();
        inspectorDialog = new javax.swing.JDialog();
        inspectorTopLabel = new javax.swing.JLabel();
        inspectorSlider = new javax.swing.JSlider();
        inspectorLabel = new javax.swing.JLabel();
        inspectorOKButton = new javax.swing.JButton();
        toolBar = new javax.swing.JToolBar();
        undoButton = new javax.swing.JButton();
        redoButton = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        selectionToggle = new javax.swing.JToggleButton();
        surfaceMoveToggle = new javax.swing.JToggleButton();
        tileMoveToggle = new javax.swing.JToggleButton();
        mergeToggle = new javax.swing.JToggleButton();
        jSeparator9 = new javax.swing.JToolBar.Separator();
        rectangleToggle = new javax.swing.JToggleButton();
        ellipseToggleButton = new javax.swing.JToggleButton();
        polygonToggle = new javax.swing.JToggleButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        magnetButton = new javax.swing.JToggleButton();
        inspectorButton = new javax.swing.JToggleButton();
        collisionCheckToggleButton = new javax.swing.JToggleButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        quantitiesButton = new javax.swing.JButton();
        debugToggleButton = new javax.swing.JToggleButton();
        mainPanel = new javax.swing.JPanel();
        leftPanel = new javax.swing.JPanel();
        zoomOutButton = new javax.swing.JButton();
        zoomInButton = new javax.swing.JButton();
        canvasPanel = new VirtuTuile.GUI.CanvasPanel();
        createTileWindow = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        createTileNameField = new javax.swing.JTextField();
        createTileOKButton = new javax.swing.JButton();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        createTileWidthField = new javax.swing.JTextField();
        createTileHeightField = new javax.swing.JTextField();
        createTileNbPerBoxField = new javax.swing.JTextField();
        createTileColorButton = new javax.swing.JButton();
        createTileCancelButton = new javax.swing.JButton();
        createWindowWidthLabel = new javax.swing.JLabel();
        createWindowHeightLabel = new javax.swing.JLabel();
        xPixelCoordsLabel = new javax.swing.JLabel();
        xMeasureCoordsLabel = new javax.swing.JLabel();
        zoomLabel = new javax.swing.JLabel();
        percentLabel = new javax.swing.JLabel();
        verticalScrollBar = new javax.swing.JScrollBar();
        horizontalScrollBar = new javax.swing.JScrollBar();
        yMeasureCoordsLabel = new javax.swing.JLabel();
        yPixelCoordsLabel = new javax.swing.JLabel();
        metricButton = new javax.swing.JToggleButton();
        imperialButton = new javax.swing.JToggleButton();
        tileState = new javax.swing.JLabel();
        tileInfoWidth = new javax.swing.JLabel();
        tileInfoHeight = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        nbTilesOnSurfaceLabels = new javax.swing.JLabel();
        nbBoxesOnSurfaceLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        newPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        surfaceXField = new javax.swing.JTextField();
        xFtLabel = new javax.swing.JLabel();
        surfaceXFieldInches = new javax.swing.JTextField();
        xInLabel = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        surfaceYField = new javax.swing.JTextField();
        yFtLabel = new javax.swing.JLabel();
        surfaceYFieldInches = new javax.swing.JTextField();
        yInLabel = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        surfaceWidthField = new javax.swing.JTextField();
        widthFtLabel = new javax.swing.JLabel();
        surfaceWidthFieldInches = new javax.swing.JTextField();
        widthInLabel = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        surfaceHeightField = new javax.swing.JTextField();
        heightFtLabel = new javax.swing.JLabel();
        surfaceHeightFieldInches = new javax.swing.JTextField();
        heightInLabel = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        surfaceColorButton = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        coverRadioButton = new javax.swing.JRadioButton();
        doNotCoverRadioButton = new javax.swing.JRadioButton();
        jLabel7 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        tileTypeComboBox = new javax.swing.JComboBox<>();
        jLabel15 = new javax.swing.JLabel();
        jointWidthField = new javax.swing.JTextField();
        largeurJointText = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jointColorButton = new javax.swing.JButton();
        jLabel17 = new javax.swing.JLabel();
        patternComboBox = new javax.swing.JComboBox<>();
        orientationLabel = new javax.swing.JLabel();
        zeroOrientationRadioButton = new javax.swing.JRadioButton();
        ninetyOrientationRadioButton = new javax.swing.JRadioButton();
        jLabel9 = new javax.swing.JLabel();
        offsetXField = new javax.swing.JTextField();
        offsetXText = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        offsetYField = new javax.swing.JTextField();
        offsetYText = new javax.swing.JLabel();
        rowOffsetLabel1 = new javax.swing.JLabel();
        rowOffsetField = new javax.swing.JTextField();
        rowOffsetLabel2 = new javax.swing.JLabel();
        rotationLabel1 = new javax.swing.JLabel();
        rotationTextField = new javax.swing.JTextField();
        rotationLabel2 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        tileNameLabel = new javax.swing.JLabel();
        tileNameField = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        tileWidthField = new javax.swing.JTextField();
        largeurTuileText = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        tileHeightField = new javax.swing.JTextField();
        hauteurTuileText = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        tileColorButton = new javax.swing.JButton();
        jLabel19 = new javax.swing.JLabel();
        tileNbPerBoxField = new javax.swing.JTextField();
        createTileButton = new javax.swing.JButton();
        topMenuBar = new javax.swing.JMenuBar();
        menuFichier = new javax.swing.JMenu();
        newProjectButton = new javax.swing.JMenuItem();
        openProjectButton = new javax.swing.JMenuItem();
        closeProjectButton = new javax.swing.JMenuItem();
        saveProjectButton = new javax.swing.JMenuItem();
        exportMenuItem = new javax.swing.JMenuItem();
        menuFichierQuitter = new javax.swing.JMenuItem();
        menuEdition = new javax.swing.JMenu();
        menuEditionAnnuler = new javax.swing.JMenuItem();
        menuEditionRepeter = new javax.swing.JMenuItem();
        menuAffichage = new javax.swing.JMenu();
        menuGridDistance = new javax.swing.JMenuItem();
        inspectorMenuItem = new javax.swing.JMenuItem();
        jSeparator8 = new javax.swing.JPopupMenu.Separator();
        menuCustomZoom = new javax.swing.JMenuItem();
        menuResetZoom = new javax.swing.JMenuItem();
        menuAide = new javax.swing.JMenu();
        menuAidePropos = new javax.swing.JMenuItem();

        menuBar.setText("jMenu1");

        gridDistanceDialog.setTitle("Modifier la distance de la grille");
        gridDistanceDialog.setAlwaysOnTop(true);
        gridDistanceDialog.setIconImages(null);
        gridDistanceDialog.setMinimumSize(new java.awt.Dimension(330, 160));
        gridDistanceDialog.setResizable(false);

        gridDistanceTopLabel.setText("Distance entre chaque ligne de la grille:");

        gridDistanceSlider.setMajorTickSpacing(10);
        gridDistanceSlider.setMinimum(1);
        gridDistanceSlider.setMinorTickSpacing(5);
        gridDistanceSlider.setValue(20);
        gridDistanceSlider.addChangeListener(new javax.swing.event.ChangeListener()
        {
            public void stateChanged(javax.swing.event.ChangeEvent evt)
            {
                gridDistanceSliderStateChanged(evt);
            }
        });

        gridDistanceLabel.setText("20 cm");

        gridDistanceOKButton.setText("OK");
        gridDistanceOKButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                gridDistanceOKButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout gridDistanceDialogLayout = new javax.swing.GroupLayout(gridDistanceDialog.getContentPane());
        gridDistanceDialog.getContentPane().setLayout(gridDistanceDialogLayout);
        gridDistanceDialogLayout.setHorizontalGroup(
            gridDistanceDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, gridDistanceDialogLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(gridDistanceDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(gridDistanceTopLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(gridDistanceOKButton, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(gridDistanceDialogLayout.createSequentialGroup()
                        .addComponent(gridDistanceSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(gridDistanceLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(10, 10, 10))
        );
        gridDistanceDialogLayout.setVerticalGroup(
            gridDistanceDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(gridDistanceDialogLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(gridDistanceTopLabel)
                .addGap(10, 10, 10)
                .addGroup(gridDistanceDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(gridDistanceSlider, javax.swing.GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE)
                    .addComponent(gridDistanceLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, 0)
                .addComponent(gridDistanceOKButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        imperialGridDistanceDialog.setTitle("Modifier la distance de la grille");
        imperialGridDistanceDialog.setAlwaysOnTop(true);
        imperialGridDistanceDialog.setIconImages(null);
        imperialGridDistanceDialog.setMinimumSize(new java.awt.Dimension(340, 160));
        imperialGridDistanceDialog.setResizable(false);

        imperialGridDistanceTopLabel.setText("Distance entre chaque ligne de la grille:");

        imperialGridDistanceSlider.setMajorTickSpacing(10);
        imperialGridDistanceSlider.setMaximum(40);
        imperialGridDistanceSlider.setMinimum(1);
        imperialGridDistanceSlider.setMinorTickSpacing(5);
        imperialGridDistanceSlider.setValue(12);
        imperialGridDistanceSlider.addChangeListener(new javax.swing.event.ChangeListener()
        {
            public void stateChanged(javax.swing.event.ChangeEvent evt)
            {
                imperialGridDistanceSliderStateChanged(evt);
            }
        });

        imperialGridDistanceLabel.setText("12 pouces");

        imperialGridDistanceOKButton.setText("OK");
        imperialGridDistanceOKButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                imperialGridDistanceOKButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout imperialGridDistanceDialogLayout = new javax.swing.GroupLayout(imperialGridDistanceDialog.getContentPane());
        imperialGridDistanceDialog.getContentPane().setLayout(imperialGridDistanceDialogLayout);
        imperialGridDistanceDialogLayout.setHorizontalGroup(
            imperialGridDistanceDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, imperialGridDistanceDialogLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(imperialGridDistanceDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(imperialGridDistanceDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(imperialGridDistanceTopLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(imperialGridDistanceOKButton, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(imperialGridDistanceDialogLayout.createSequentialGroup()
                        .addComponent(imperialGridDistanceSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(imperialGridDistanceLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );
        imperialGridDistanceDialogLayout.setVerticalGroup(
            imperialGridDistanceDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(imperialGridDistanceDialogLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(imperialGridDistanceTopLabel)
                .addGap(10, 10, 10)
                .addGroup(imperialGridDistanceDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(imperialGridDistanceSlider, javax.swing.GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE)
                    .addComponent(imperialGridDistanceLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, 0)
                .addComponent(imperialGridDistanceOKButton)
                .addGap(10, 10, 10))
        );

        surfacePopupMenu.setLabel("Surface");

        relativeMoveMenuItem.setText("Repositionner par rapport à...");
        relativeMoveMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                relativeMoveMenuItemActionPerformed(evt);
            }
        });
        surfacePopupMenu.add(relativeMoveMenuItem);
        surfacePopupMenu.add(jSeparator7);

        pushSubMenu.setText("Pousser");

        pushTopMenuItem.setText("Pousser la surface en-haut");
        pushTopMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                pushTopMenuItemActionPerformed(evt);
            }
        });
        pushSubMenu.add(pushTopMenuItem);

        pushBottomMenuItem.setText("Pousser la surface en-bas");
        pushBottomMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                pushBottomMenuItemActionPerformed(evt);
            }
        });
        pushSubMenu.add(pushBottomMenuItem);

        pushLeftJMenuItem.setText("Pousser la surface à gauche");
        pushLeftJMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                pushLeftJMenuItemActionPerformed(evt);
            }
        });
        pushSubMenu.add(pushLeftJMenuItem);

        pushRightMenuItem.setText("Pousser la surface à droite");
        pushRightMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                pushRightMenuItemActionPerformed(evt);
            }
        });
        pushSubMenu.add(pushRightMenuItem);

        surfacePopupMenu.add(pushSubMenu);

        stickSubMenu.setText("Coller");

        stickHMenuItem.setText("Coller horizontalement avec...");
        stickHMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                stickHMenuItemActionPerformed(evt);
            }
        });
        stickSubMenu.add(stickHMenuItem);

        stickVMenuItem.setText("Coller verticalement avec...");
        stickVMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                stickVMenuItemActionPerformed(evt);
            }
        });
        stickSubMenu.add(stickVMenuItem);

        surfacePopupMenu.add(stickSubMenu);

        alignSubMenu.setText("Aligner");
        alignSubMenu.setActionCommand("Aligner");

        alignTopMenuItem.setText("Aligner en haut avec...");
        alignTopMenuItem.setActionCommand("Aligner en-haut avec...");
        alignTopMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                alignTopMenuItemActionPerformed(evt);
            }
        });
        alignSubMenu.add(alignTopMenuItem);
        alignTopMenuItem.getAccessibleContext().setAccessibleDescription("");

        alignBottomMenuItem.setText("Aligner en-bas avec...");
        alignBottomMenuItem.setToolTipText("");
        alignBottomMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                alignBottomMenuItemActionPerformed(evt);
            }
        });
        alignSubMenu.add(alignBottomMenuItem);

        alignLeftMenuItem.setText("Aligner à gauche avec...");
        alignLeftMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                alignLeftMenuItemActionPerformed(evt);
            }
        });
        alignSubMenu.add(alignLeftMenuItem);
        alignLeftMenuItem.getAccessibleContext().setAccessibleDescription("");

        alignRightMenuItem.setText("Aligner à droite avec...");
        alignRightMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                alignRightMenuItemActionPerformed(evt);
            }
        });
        alignSubMenu.add(alignRightMenuItem);

        surfacePopupMenu.add(alignSubMenu);
        alignSubMenu.getAccessibleContext().setAccessibleName("");

        centerSubMenu.setText("Centrer");

        centerHMenuItem.setText("Centrer horizontalement avec...");
        centerHMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                centerHMenuItemActionPerformed(evt);
            }
        });
        centerSubMenu.add(centerHMenuItem);

        centerVMenuItem.setText("Centrer verticalement avec...");
        centerVMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                centerVMenuItemActionPerformed(evt);
            }
        });
        centerSubMenu.add(centerVMenuItem);

        surfacePopupMenu.add(centerSubMenu);
        surfacePopupMenu.add(jSeparator6);

        startPatternSubMenu.setText("Débuter le motif avec...");

        startPatternFullTileMenuItem.setText("Débuter le motif avec une tuile pleine");
        startPatternFullTileMenuItem.setToolTipText("La première tuile en haut à gauche de la surface ne sera pas tronquée");
        startPatternFullTileMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                startPatternFullTileMenuItemActionPerformed(evt);
            }
        });
        startPatternSubMenu.add(startPatternFullTileMenuItem);

        startColumnFullTileMenuItem.setText("Débuter le motif avec une colonne pleine");
        startColumnFullTileMenuItem.setToolTipText("Évite le tronquage de la première colonne de la surfacec");
        startColumnFullTileMenuItem.setActionCommand("");
        startColumnFullTileMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                startColumnFullTileMenuItemActionPerformed(evt);
            }
        });
        startPatternSubMenu.add(startColumnFullTileMenuItem);

        startRowFullTileMenuItem.setText("Débuter le motif avec une rangée pleine");
        startRowFullTileMenuItem.setToolTipText("Évite de tronquer les tuiles de la première rangée");
        startRowFullTileMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                startRowFullTileMenuItemActionPerformed(evt);
            }
        });
        startPatternSubMenu.add(startRowFullTileMenuItem);

        surfacePopupMenu.add(startPatternSubMenu);

        centerPatternSubMenu.setText("Centrer le motif");

        centerPatternHMenuItem.setText("Centrer le motif horizontalement");
        centerPatternHMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                centerPatternHMenuItemActionPerformed(evt);
            }
        });
        centerPatternSubMenu.add(centerPatternHMenuItem);

        centerPatternVMenuItem.setText("Centrer le motif verticalement");
        centerPatternVMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                centerPatternVMenuItemActionPerformed(evt);
            }
        });
        centerPatternSubMenu.add(centerPatternVMenuItem);

        surfacePopupMenu.add(centerPatternSubMenu);
        surfacePopupMenu.add(jSeparator5);

        decombineMenuItem.setText("Décombiner la surface");
        decombineMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                decombineMenuItemActionPerformed(evt);
            }
        });
        surfacePopupMenu.add(decombineMenuItem);

        deleteSurfaceMenuItem.setText("Effacer la surface");
        deleteSurfaceMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                deleteSurfaceMenuItemActionPerformed(evt);
            }
        });
        surfacePopupMenu.add(deleteSurfaceMenuItem);

        inspectorDialog.setTitle("Modifier la taille minimale du mode inspecteur");
        inspectorDialog.setAlwaysOnTop(true);
        inspectorDialog.setIconImages(null);
        inspectorDialog.setMinimumSize(new java.awt.Dimension(330, 160));
        inspectorDialog.setResizable(false);

        inspectorTopLabel.setText("Taille minimale d'une tuile:");

        inspectorSlider.setMajorTickSpacing(10);
        inspectorSlider.setMinimum(1);
        inspectorSlider.setMinorTickSpacing(5);
        inspectorSlider.setValue(20);
        inspectorSlider.addChangeListener(new javax.swing.event.ChangeListener()
        {
            public void stateChanged(javax.swing.event.ChangeEvent evt)
            {
                inspectorSliderStateChanged(evt);
            }
        });

        inspectorLabel.setText("20 mm");

        inspectorOKButton.setText("OK");
        inspectorOKButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                inspectorOKButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout inspectorDialogLayout = new javax.swing.GroupLayout(inspectorDialog.getContentPane());
        inspectorDialog.getContentPane().setLayout(inspectorDialogLayout);
        inspectorDialogLayout.setHorizontalGroup(
            inspectorDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, inspectorDialogLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(inspectorDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(inspectorTopLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(inspectorOKButton, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(inspectorDialogLayout.createSequentialGroup()
                        .addComponent(inspectorSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(inspectorLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(10, 10, 10))
        );
        inspectorDialogLayout.setVerticalGroup(
            inspectorDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(inspectorDialogLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(inspectorTopLabel)
                .addGap(10, 10, 10)
                .addGroup(inspectorDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(inspectorSlider, javax.swing.GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE)
                    .addComponent(inspectorLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, 0)
                .addComponent(inspectorOKButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("VirtuTuile");
        setBackground(new java.awt.Color(251, 177, 143));
        setMinimumSize(new java.awt.Dimension(1000, 700));
        addComponentListener(new java.awt.event.ComponentAdapter()
        {
            public void componentResized(java.awt.event.ComponentEvent evt)
            {
                formComponentResized(evt);
            }
        });

        toolBar.setBackground(new java.awt.Color(102, 153, 255));
        toolBar.setFloatable(false);
        toolBar.setRollover(true);

        undoButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/undo.png"))); // NOI18N
        undoButton.setFocusable(false);
        undoButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        undoButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        undoButton.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseEntered(java.awt.event.MouseEvent evt)
            {
                undoButtonMouseEntered(evt);
            }
        });
        undoButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                undoButtonActionPerformed(evt);
            }
        });
        toolBar.add(undoButton);

        redoButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/redo.png"))); // NOI18N
        redoButton.setFocusable(false);
        redoButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        redoButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        redoButton.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseEntered(java.awt.event.MouseEvent evt)
            {
                redoButtonMouseEntered(evt);
            }
        });
        redoButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                redoButtonActionPerformed(evt);
            }
        });
        toolBar.add(redoButton);
        toolBar.add(jSeparator4);

        toggleGroup.add(selectionToggle);
        selectionToggle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/select.png"))); // NOI18N
        selectionToggle.setToolTipText("Sélection et remodelage");
        selectionToggle.setFocusable(false);
        selectionToggle.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        selectionToggle.setMargin(new java.awt.Insets(0, 0, 0, 0));
        selectionToggle.setMaximumSize(new java.awt.Dimension(30, 30));
        selectionToggle.setMinimumSize(new java.awt.Dimension(30, 30));
        selectionToggle.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        selectionToggle.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                selectionToggleActionPerformed(evt);
            }
        });
        toolBar.add(selectionToggle);

        toggleGroup.add(surfaceMoveToggle);
        surfaceMoveToggle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/moveIcon.png"))); // NOI18N
        surfaceMoveToggle.setToolTipText("Déplacer une surface");
        surfaceMoveToggle.setFocusable(false);
        surfaceMoveToggle.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        surfaceMoveToggle.setMargin(new java.awt.Insets(0, 0, 0, 0));
        surfaceMoveToggle.setMaximumSize(new java.awt.Dimension(30, 30));
        surfaceMoveToggle.setMinimumSize(new java.awt.Dimension(30, 30));
        surfaceMoveToggle.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        surfaceMoveToggle.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                surfaceMoveToggleActionPerformed(evt);
            }
        });
        toolBar.add(surfaceMoveToggle);

        toggleGroup.add(tileMoveToggle);
        tileMoveToggle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/tileMoveIcon.png"))); // NOI18N
        tileMoveToggle.setToolTipText("Déplacer un motif");
        tileMoveToggle.setFocusable(false);
        tileMoveToggle.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        tileMoveToggle.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tileMoveToggle.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                tileMoveToggleActionPerformed(evt);
            }
        });
        toolBar.add(tileMoveToggle);
        toolBar.add(jSeparator9);

        toggleGroup.add(mergeToggle);
        mergeToggle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/combine.png"))); // NOI18N
        mergeToggle.setToolTipText("Combiner deux surfaces");
        mergeToggle.setFocusable(false);
        mergeToggle.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        mergeToggle.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        mergeToggle.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                mergeToggleActionPerformed(evt);
            }
        });
        toolBar.add(mergeToggle);
        toolBar.add(jSeparator9);

        toggleGroup.add(rectangleToggle);
        rectangleToggle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/rectangle.png"))); // NOI18N
        rectangleToggle.setToolTipText("Créer une surface rectangulaire");
        rectangleToggle.setFocusable(false);
        rectangleToggle.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        rectangleToggle.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rectangleToggle.setMaximumSize(new java.awt.Dimension(30, 30));
        rectangleToggle.setMinimumSize(new java.awt.Dimension(30, 30));
        rectangleToggle.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        rectangleToggle.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                rectangleToggleActionPerformed(evt);
            }
        });
        toolBar.add(rectangleToggle);

        toggleGroup.add(ellipseToggleButton);
        ellipseToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ellipse.png"))); // NOI18N
        ellipseToggleButton.setToolTipText("Créer une surface circulaire");
        ellipseToggleButton.setFocusable(false);
        ellipseToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ellipseToggleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        ellipseToggleButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                ellipseToggleButtonActionPerformed(evt);
            }
        });
        toolBar.add(ellipseToggleButton);

        toggleGroup.add(polygonToggle);
        polygonToggle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/polygon.png"))); // NOI18N
        polygonToggle.setToolTipText("Créer une surface irrégulière");
        polygonToggle.setFocusable(false);
        polygonToggle.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        polygonToggle.setMargin(new java.awt.Insets(0, 0, 0, 0));
        polygonToggle.setMaximumSize(new java.awt.Dimension(30, 30));
        polygonToggle.setMinimumSize(new java.awt.Dimension(30, 30));
        polygonToggle.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        polygonToggle.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                polygonToggleActionPerformed(evt);
            }
        });
        toolBar.add(polygonToggle);
        toolBar.add(jSeparator1);

        magnetButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/magnet.png"))); // NOI18N
        magnetButton.setSelected(true);
        magnetButton.setToolTipText("Activer la grille magnétique");
        magnetButton.setFocusable(false);
        magnetButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        magnetButton.setIconTextGap(0);
        magnetButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        magnetButton.setMaximumSize(new java.awt.Dimension(30, 30));
        magnetButton.setMinimumSize(new java.awt.Dimension(30, 30));
        magnetButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        magnetButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                magnetButtonActionPerformed(evt);
            }
        });
        toolBar.add(magnetButton);

        inspectorButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/glass.png"))); // NOI18N
        inspectorButton.setSelected(true);
        inspectorButton.setToolTipText("Détecter les petites tuiles");
        inspectorButton.setFocusable(false);
        inspectorButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        inspectorButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        inspectorButton.setMaximumSize(new java.awt.Dimension(30, 30));
        inspectorButton.setMinimumSize(new java.awt.Dimension(30, 30));
        inspectorButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        inspectorButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                inspectorButtonActionPerformed(evt);
            }
        });
        toolBar.add(inspectorButton);

        collisionCheckToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/collision.png"))); // NOI18N
        collisionCheckToggleButton.setToolTipText("Activer la détection de collision");
        collisionCheckToggleButton.setFocusable(false);
        collisionCheckToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        collisionCheckToggleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        collisionCheckToggleButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                collisionCheckToggleButtonActionPerformed(evt);
            }
        });
        toolBar.add(collisionCheckToggleButton);
        toolBar.add(jSeparator3);

        quantitiesButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/quantities.png"))); // NOI18N
        quantitiesButton.setToolTipText("Calculer les quantités");
        quantitiesButton.setFocusable(false);
        quantitiesButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        quantitiesButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        quantitiesButton.setMaximumSize(new java.awt.Dimension(30, 30));
        quantitiesButton.setMinimumSize(new java.awt.Dimension(30, 30));
        quantitiesButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        quantitiesButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                quantitiesButtonActionPerformed(evt);
            }
        });
        toolBar.add(quantitiesButton);

        debugToggleButton.setText("DEBUG");
        debugToggleButton.setToolTipText("Activer mode debug");
        debugToggleButton.setFocusable(false);
        debugToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        debugToggleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        debugToggleButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                debugToggleButtonActionPerformed(evt);
            }
        });
        toolBar.add(debugToggleButton);

        leftPanel.setBackground(new java.awt.Color(153, 153, 153));
        leftPanel.setPreferredSize(new java.awt.Dimension(0, 0));

        zoomOutButton.setText("-");
        zoomOutButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                zoomOutButtonActionPerformed(evt);
            }
        });

        zoomInButton.setText("+");
        zoomInButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                zoomInButtonActionPerformed(evt);
            }
        });

        canvasPanel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter()
        {
            public void mouseDragged(java.awt.event.MouseEvent evt)
            {
                canvasPanelMouseDragged(evt);
            }
            public void mouseMoved(java.awt.event.MouseEvent evt)
            {
                canvasPanelMouseMoved(evt);
            }
        });
        canvasPanel.addMouseWheelListener(new java.awt.event.MouseWheelListener()
        {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt)
            {
                canvasPanelMouseWheelMoved(evt);
            }
        });
        canvasPanel.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mousePressed(java.awt.event.MouseEvent evt)
            {
                canvasPanelMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt)
            {
                canvasPanelMouseReleased(evt);
            }
        });

        createTileWindow.setBackground(new java.awt.Color(0, 153, 153));
        createTileWindow.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        createTileWindow.setAutoscrolls(true);

        jLabel21.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText("Créer un nouveau type de tuile");
        jLabel21.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        createTileNameField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        createTileOKButton.setText("OK");
        createTileOKButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                createTileOKButtonActionPerformed(evt);
            }
        });

        jLabel22.setText("Nom :");

        jLabel23.setText("Largeur :");

        jLabel24.setText("Hauteur :");

        jLabel25.setText("Nb/boîte :");

        jLabel27.setText("Couleur :");

        createTileWidthField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        createTileHeightField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        createTileNbPerBoxField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        createTileColorButton.setBackground(new java.awt.Color(250, 204, 161));
        createTileColorButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                createTileColorButtonActionPerformed(evt);
            }
        });

        createTileCancelButton.setText("Annuler");
        createTileCancelButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                createTileCancelButtonActionPerformed(evt);
            }
        });

        createWindowWidthLabel.setText("cm");

        createWindowHeightLabel.setText("cm");

        javax.swing.GroupLayout createTileWindowLayout = new javax.swing.GroupLayout(createTileWindow);
        createTileWindow.setLayout(createTileWindowLayout);
        createTileWindowLayout.setHorizontalGroup(
            createTileWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, 307, Short.MAX_VALUE)
            .addGroup(createTileWindowLayout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addGroup(createTileWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel25, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(createTileWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel27)
                        .addGroup(createTileWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(createTileWindowLayout.createSequentialGroup()
                                .addComponent(createTileOKButton)
                                .addGap(18, 18, 18)
                                .addComponent(createTileCancelButton))
                            .addGroup(createTileWindowLayout.createSequentialGroup()
                                .addGroup(createTileWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(createTileWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel23)
                                        .addComponent(jLabel22))
                                    .addComponent(jLabel24))
                                .addGap(23, 23, 23)
                                .addGroup(createTileWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(createTileNameField, javax.swing.GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)
                                    .addComponent(createTileWidthField)
                                    .addComponent(createTileHeightField)
                                    .addComponent(createTileColorButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(createTileNbPerBoxField))))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(createTileWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(createWindowWidthLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(createWindowHeightLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(24, 24, 24))
        );
        createTileWindowLayout.setVerticalGroup(
            createTileWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(createTileWindowLayout.createSequentialGroup()
                .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(createTileWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(createTileNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(createTileWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(createTileWidthField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23)
                    .addComponent(createWindowWidthLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(createTileWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(createTileHeightField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24)
                    .addComponent(createWindowHeightLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(createTileWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(createTileWindowLayout.createSequentialGroup()
                        .addComponent(createTileColorButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(createTileWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(createTileNbPerBoxField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel25))
                        .addGap(18, 18, 18)
                        .addGroup(createTileWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(createTileOKButton)
                            .addComponent(createTileCancelButton)))
                    .addComponent(jLabel27))
                .addGap(14, 14, 14))
        );

        createTileWindowLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {createTileColorButton, createTileHeightField, createTileNameField, createTileNbPerBoxField, createTileWidthField});

        javax.swing.GroupLayout canvasPanelLayout = new javax.swing.GroupLayout(canvasPanel);
        canvasPanel.setLayout(canvasPanelLayout);
        canvasPanelLayout.setHorizontalGroup(
            canvasPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, canvasPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(createTileWindow, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        canvasPanelLayout.setVerticalGroup(
            canvasPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(canvasPanelLayout.createSequentialGroup()
                .addContainerGap(282, Short.MAX_VALUE)
                .addComponent(createTileWindow, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(282, Short.MAX_VALUE))
        );

        xPixelCoordsLabel.setText("X: 0 pixels");

        xMeasureCoordsLabel.setText("X: 0.000 mètres");

        zoomLabel.setText("100");
        zoomLabel.setIconTextGap(7);

        percentLabel.setText("%");

        verticalScrollBar.setVisibleAmount(40);
        verticalScrollBar.addAdjustmentListener(new java.awt.event.AdjustmentListener()
        {
            public void adjustmentValueChanged(java.awt.event.AdjustmentEvent evt)
            {
                verticalScrollBarAdjustmentValueChanged(evt);
            }
        });

        horizontalScrollBar.setOrientation(javax.swing.JScrollBar.HORIZONTAL);
        horizontalScrollBar.setVisibleAmount(40);
        horizontalScrollBar.addAdjustmentListener(new java.awt.event.AdjustmentListener()
        {
            public void adjustmentValueChanged(java.awt.event.AdjustmentEvent evt)
            {
                horizontalScrollBarAdjustmentValueChanged(evt);
            }
        });

        yMeasureCoordsLabel.setText("Y: 0.000 mètres");

        yPixelCoordsLabel.setText("Y: 0 pixels");

        measureGroup.add(metricButton);
        metricButton.setSelected(true);
        metricButton.setText("Métrique");
        metricButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                metricButtonActionPerformed(evt);
            }
        });

        measureGroup.add(imperialButton);
        imperialButton.setText("Impérial");
        imperialButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                imperialButtonActionPerformed(evt);
            }
        });

        tileState.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        tileState.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        tileState.setText("Tuile: ");
        tileState.setToolTipText("");
        tileState.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        tileInfoWidth.setText("Largeur :");

        tileInfoHeight.setText("Hauteur :");

        jLabel28.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel28.setText("Surface:");

        nbTilesOnSurfaceLabels.setText("0 tuiles");

        nbBoxesOnSurfaceLabel.setText("0 boîtes");

        javax.swing.GroupLayout leftPanelLayout = new javax.swing.GroupLayout(leftPanel);
        leftPanel.setLayout(leftPanelLayout);
        leftPanelLayout.setHorizontalGroup(
            leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(leftPanelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(leftPanelLayout.createSequentialGroup()
                        .addComponent(zoomOutButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(zoomLabel)
                        .addGap(0, 0, 0)
                        .addComponent(percentLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(zoomInButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(metricButton)
                        .addGap(18, 18, 18)
                        .addComponent(imperialButton))
                    .addGroup(leftPanelLayout.createSequentialGroup()
                        .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(leftPanelLayout.createSequentialGroup()
                                .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(xMeasureCoordsLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE)
                                    .addComponent(yMeasureCoordsLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(20, 20, 20)
                                .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(yPixelCoordsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(xPixelCoordsLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 80, Short.MAX_VALUE)
                                .addComponent(jLabel28)
                                .addGap(14, 14, 14)
                                .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(nbBoxesOnSurfaceLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(nbTilesOnSurfaceLabels, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tileState, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(tileInfoHeight, javax.swing.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
                                    .addComponent(tileInfoWidth, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addComponent(horizontalScrollBar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(canvasPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, 0)
                        .addComponent(verticalScrollBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(20, 20, 20))
        );
        leftPanelLayout.setVerticalGroup(
            leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(leftPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(zoomOutButton)
                    .addComponent(zoomInButton)
                    .addComponent(zoomLabel)
                    .addComponent(percentLabel)
                    .addComponent(metricButton)
                    .addComponent(imperialButton))
                .addGap(10, 10, 10)
                .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(verticalScrollBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(canvasPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, 0)
                .addComponent(horizontalScrollBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(leftPanelLayout.createSequentialGroup()
                        .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(xMeasureCoordsLabel)
                            .addComponent(xPixelCoordsLabel))
                        .addGap(3, 3, 3)
                        .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(yPixelCoordsLabel)
                            .addComponent(yMeasureCoordsLabel)
                            .addComponent(tileInfoHeight)
                            .addComponent(nbBoxesOnSurfaceLabel)))
                    .addComponent(tileInfoWidth)
                    .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tileState, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(nbTilesOnSurfaceLabels)
                        .addComponent(jLabel28)))
                .addGap(5, 5, 5))
        );

        jScrollPane1.setBackground(new java.awt.Color(0, 107, 107));

        newPanel.setBackground(new java.awt.Color(0, 153, 153));
        newPanel.setBorder(javax.swing.BorderFactory.createMatteBorder(10, 10, 10, 10, new java.awt.Color(0, 107, 107)));
        newPanel.setLayout(new java.awt.GridBagLayout());

        jLabel1.setBackground(new java.awt.Color(0, 107, 107));
        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel1.setText("Surface :");
        jLabel1.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        newPanel.add(jLabel1, gridBagConstraints);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel2.setText("x :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        newPanel.add(jLabel2, gridBagConstraints);

        surfaceXField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        surfaceXField.setEnabled(false);
        surfaceXField.setPreferredSize(new java.awt.Dimension(60, 27));
        surfaceXField.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                surfaceXFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        newPanel.add(surfaceXField, gridBagConstraints);

        xFtLabel.setText("m");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        newPanel.add(xFtLabel, gridBagConstraints);

        surfaceXFieldInches.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        surfaceXFieldInches.setEnabled(false);
        surfaceXFieldInches.setMinimumSize(new java.awt.Dimension(7, 23));
        surfaceXFieldInches.setPreferredSize(new java.awt.Dimension(60, 23));
        surfaceXFieldInches.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                surfaceXFieldInchesActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        newPanel.add(surfaceXFieldInches, gridBagConstraints);

        xInLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        xInLabel.setText("in");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        newPanel.add(xInLabel, gridBagConstraints);

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel3.setText("y :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        newPanel.add(jLabel3, gridBagConstraints);

        surfaceYField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        surfaceYField.setEnabled(false);
        surfaceYField.setPreferredSize(new java.awt.Dimension(50, 27));
        surfaceYField.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                surfaceYFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        newPanel.add(surfaceYField, gridBagConstraints);

        yFtLabel.setText("m");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        newPanel.add(yFtLabel, gridBagConstraints);

        surfaceYFieldInches.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        surfaceYFieldInches.setEnabled(false);
        surfaceYFieldInches.setPreferredSize(new java.awt.Dimension(7, 23));
        surfaceYFieldInches.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                surfaceYFieldInchesActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        newPanel.add(surfaceYFieldInches, gridBagConstraints);

        yInLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        yInLabel.setText("in");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        newPanel.add(yInLabel, gridBagConstraints);

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel4.setText("Largeur :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        newPanel.add(jLabel4, gridBagConstraints);

        surfaceWidthField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        surfaceWidthField.setEnabled(false);
        surfaceWidthField.setPreferredSize(new java.awt.Dimension(50, 27));
        surfaceWidthField.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                surfaceWidthFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        newPanel.add(surfaceWidthField, gridBagConstraints);

        widthFtLabel.setText("m");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        newPanel.add(widthFtLabel, gridBagConstraints);

        surfaceWidthFieldInches.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        surfaceWidthFieldInches.setEnabled(false);
        surfaceWidthFieldInches.setPreferredSize(new java.awt.Dimension(7, 23));
        surfaceWidthFieldInches.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                surfaceWidthFieldInchesActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        newPanel.add(surfaceWidthFieldInches, gridBagConstraints);

        widthInLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        widthInLabel.setText("in");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        newPanel.add(widthInLabel, gridBagConstraints);

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel5.setText("Hauteur :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        newPanel.add(jLabel5, gridBagConstraints);

        surfaceHeightField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        surfaceHeightField.setEnabled(false);
        surfaceHeightField.setPreferredSize(new java.awt.Dimension(50, 27));
        surfaceHeightField.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                surfaceHeightFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        newPanel.add(surfaceHeightField, gridBagConstraints);

        heightFtLabel.setText("m");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        newPanel.add(heightFtLabel, gridBagConstraints);

        surfaceHeightFieldInches.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        surfaceHeightFieldInches.setEnabled(false);
        surfaceHeightFieldInches.setPreferredSize(new java.awt.Dimension(7, 23));
        surfaceHeightFieldInches.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                surfaceHeightFieldInchesActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        newPanel.add(surfaceHeightFieldInches, gridBagConstraints);

        heightInLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        heightInLabel.setText("in");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        newPanel.add(heightInLabel, gridBagConstraints);

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel11.setText("Couleur :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        newPanel.add(jLabel11, gridBagConstraints);

        surfaceColorButton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        surfaceColorButton.setBorderPainted(false);
        surfaceColorButton.setEnabled(false);
        surfaceColorButton.setMinimumSize(new java.awt.Dimension(100, 30));
        surfaceColorButton.setPreferredSize(new java.awt.Dimension(7, 27));
        surfaceColorButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                surfaceColorButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        newPanel.add(surfaceColorButton, gridBagConstraints);

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel6.setText("Couvir :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        newPanel.add(jLabel6, gridBagConstraints);

        coverRadioButton.setBackground(new java.awt.Color(0, 153, 153));
        coverButtonGroup.add(coverRadioButton);
        coverRadioButton.setText("Oui");
        coverRadioButton.setEnabled(false);
        coverRadioButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                coverRadioButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        newPanel.add(coverRadioButton, gridBagConstraints);

        doNotCoverRadioButton.setBackground(new java.awt.Color(0, 153, 153));
        coverButtonGroup.add(doNotCoverRadioButton);
        doNotCoverRadioButton.setText("Non");
        doNotCoverRadioButton.setEnabled(false);
        doNotCoverRadioButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                doNotCoverRadioButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 7;
        newPanel.add(doNotCoverRadioButton, gridBagConstraints);

        jLabel7.setBackground(new java.awt.Color(0, 107, 107));
        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel7.setText("Recouvrement :");
        jLabel7.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.weightx = 1.0;
        newPanel.add(jLabel7, gridBagConstraints);

        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel14.setText("Type de tuile :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        newPanel.add(jLabel14, gridBagConstraints);

        tileTypeComboBox.setEnabled(false);
        tileTypeComboBox.setMinimumSize(new java.awt.Dimension(100, 22));
        tileTypeComboBox.setPreferredSize(new java.awt.Dimension(140, 30));
        tileTypeComboBox.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                tileTypeComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        newPanel.add(tileTypeComboBox, gridBagConstraints);

        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel15.setText("Largeur joints :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        newPanel.add(jLabel15, gridBagConstraints);

        jointWidthField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jointWidthField.setEnabled(false);
        jointWidthField.setPreferredSize(new java.awt.Dimension(7, 27));
        jointWidthField.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jointWidthFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        newPanel.add(jointWidthField, gridBagConstraints);

        largeurJointText.setText("mm");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        newPanel.add(largeurJointText, gridBagConstraints);

        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel16.setText("Couleur joints :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        newPanel.add(jLabel16, gridBagConstraints);

        jointColorButton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        jointColorButton.setBorderPainted(false);
        jointColorButton.setEnabled(false);
        jointColorButton.setMaximumSize(new java.awt.Dimension(100, 30));
        jointColorButton.setMinimumSize(new java.awt.Dimension(100, 30));
        jointColorButton.setPreferredSize(new java.awt.Dimension(100, 27));
        jointColorButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jointColorButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        newPanel.add(jointColorButton, gridBagConstraints);

        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel17.setText("Motif :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 12;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        newPanel.add(jLabel17, gridBagConstraints);

        patternComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Quadrillé", "L-shape", "Deux-par-deux", "Diagonal" }));
        patternComboBox.setSelectedItem(null);
        patternComboBox.setEnabled(false);
        patternComboBox.setFocusable(false);
        patternComboBox.setPreferredSize(new java.awt.Dimension(50, 30));
        patternComboBox.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                patternComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        newPanel.add(patternComboBox, gridBagConstraints);

        orientationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        orientationLabel.setText("Orientation :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 13;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        newPanel.add(orientationLabel, gridBagConstraints);

        zeroOrientationRadioButton.setBackground(new java.awt.Color(0, 153, 153));
        orientationGroup.add(zeroOrientationRadioButton);
        zeroOrientationRadioButton.setText("0°");
        zeroOrientationRadioButton.setEnabled(false);
        zeroOrientationRadioButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                zeroOrientationRadioButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 13;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        newPanel.add(zeroOrientationRadioButton, gridBagConstraints);

        ninetyOrientationRadioButton.setBackground(new java.awt.Color(0, 153, 153));
        orientationGroup.add(ninetyOrientationRadioButton);
        ninetyOrientationRadioButton.setText("90°");
        ninetyOrientationRadioButton.setEnabled(false);
        ninetyOrientationRadioButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                ninetyOrientationRadioButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        newPanel.add(ninetyOrientationRadioButton, gridBagConstraints);

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel9.setText("Décalage x :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 14;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        newPanel.add(jLabel9, gridBagConstraints);

        offsetXField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        offsetXField.setEnabled(false);
        offsetXField.setPreferredSize(new java.awt.Dimension(7, 27));
        offsetXField.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                offsetXFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 14;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        newPanel.add(offsetXField, gridBagConstraints);

        offsetXText.setText("cm");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 14;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        newPanel.add(offsetXText, gridBagConstraints);

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel10.setText("Décalage y :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 15;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        newPanel.add(jLabel10, gridBagConstraints);

        offsetYField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        offsetYField.setEnabled(false);
        offsetYField.setPreferredSize(new java.awt.Dimension(7, 27));
        offsetYField.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                offsetYFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        newPanel.add(offsetYField, gridBagConstraints);

        offsetYText.setText("cm");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 15;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        newPanel.add(offsetYText, gridBagConstraints);

        rowOffsetLabel1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        rowOffsetLabel1.setText("Déc. rangées :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 16;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        newPanel.add(rowOffsetLabel1, gridBagConstraints);

        rowOffsetField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        rowOffsetField.setEnabled(false);
        rowOffsetField.setPreferredSize(new java.awt.Dimension(7, 27));
        rowOffsetField.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                rowOffsetFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 16;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        newPanel.add(rowOffsetField, gridBagConstraints);

        rowOffsetLabel2.setText("%");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 16;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        newPanel.add(rowOffsetLabel2, gridBagConstraints);

        rotationLabel1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        rotationLabel1.setText("Rotation :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 17;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        newPanel.add(rotationLabel1, gridBagConstraints);

        rotationTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        rotationTextField.setEnabled(false);
        rotationTextField.setPreferredSize(new java.awt.Dimension(7, 27));
        rotationTextField.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                rotationTextFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 17;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        newPanel.add(rotationTextField, gridBagConstraints);

        rotationLabel2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        rotationLabel2.setText("°");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 17;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        newPanel.add(rotationLabel2, gridBagConstraints);

        jLabel8.setBackground(new java.awt.Color(0, 107, 107));
        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel8.setText("Type de tuile :");
        jLabel8.setInheritsPopupMenu(false);
        jLabel8.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 18;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        newPanel.add(jLabel8, gridBagConstraints);

        tileNameLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        tileNameLabel.setText("Nom :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 19;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        newPanel.add(tileNameLabel, gridBagConstraints);

        tileNameField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        tileNameField.setEnabled(false);
        tileNameField.setPreferredSize(new java.awt.Dimension(7, 27));
        tileNameField.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                tileNameFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 19;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        newPanel.add(tileNameField, gridBagConstraints);

        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel12.setText("Largeur :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 20;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        newPanel.add(jLabel12, gridBagConstraints);

        tileWidthField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        tileWidthField.setEnabled(false);
        tileWidthField.setPreferredSize(new java.awt.Dimension(7, 27));
        tileWidthField.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                tileWidthFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 20;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        newPanel.add(tileWidthField, gridBagConstraints);

        largeurTuileText.setText("cm");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 20;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        newPanel.add(largeurTuileText, gridBagConstraints);

        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel13.setText("Hauteur :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 21;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        newPanel.add(jLabel13, gridBagConstraints);

        tileHeightField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        tileHeightField.setEnabled(false);
        tileHeightField.setPreferredSize(new java.awt.Dimension(7, 27));
        tileHeightField.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                tileHeightFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 21;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        newPanel.add(tileHeightField, gridBagConstraints);

        hauteurTuileText.setText("cm");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 21;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        newPanel.add(hauteurTuileText, gridBagConstraints);

        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel26.setText("Couleur :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 22;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        newPanel.add(jLabel26, gridBagConstraints);

        tileColorButton.setEnabled(false);
        tileColorButton.setMinimumSize(new java.awt.Dimension(33, 30));
        tileColorButton.setPreferredSize(new java.awt.Dimension(33, 30));
        tileColorButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                tileColorButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 22;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        newPanel.add(tileColorButton, gridBagConstraints);

        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel19.setText("Nb/boîte :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 23;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        newPanel.add(jLabel19, gridBagConstraints);

        tileNbPerBoxField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        tileNbPerBoxField.setEnabled(false);
        tileNbPerBoxField.setPreferredSize(new java.awt.Dimension(7, 27));
        tileNbPerBoxField.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                tileNbPerBoxFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 23;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        newPanel.add(tileNbPerBoxField, gridBagConstraints);

        createTileButton.setText("Créer un nouveau type de tuile");
        createTileButton.setEnabled(false);
        createTileButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                createTileButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 24;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.weighty = 100000.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        newPanel.add(createTileButton, gridBagConstraints);

        jScrollPane1.setViewportView(newPanel);

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addComponent(leftPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 713, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 316, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(leftPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 920, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 920, Short.MAX_VALUE)
        );

        menuFichier.setText("Fichier");

        newProjectButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));
        newProjectButton.setText("Nouveau Projet");
        newProjectButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                newProjectButtonActionPerformed(evt);
            }
        });
        menuFichier.add(newProjectButton);

        openProjectButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
        openProjectButton.setText("Ouvrir Projet");
        openProjectButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                openProjectButtonActionPerformed(evt);
            }
        });
        menuFichier.add(openProjectButton);

        closeProjectButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.CTRL_DOWN_MASK));
        closeProjectButton.setText("Fermer Projet");
        closeProjectButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                closeProjectButtonActionPerformed(evt);
            }
        });
        menuFichier.add(closeProjectButton);

        saveProjectButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
        saveProjectButton.setText("Enregistrer Projet");
        saveProjectButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                saveProjectButtonActionPerformed(evt);
            }
        });
        menuFichier.add(saveProjectButton);

        exportMenuItem.setText("Exporter Rendu");
        exportMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                exportMenuItemActionPerformed(evt);
            }
        });
        menuFichier.add(exportMenuItem);

        menuFichierQuitter.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK));
        menuFichierQuitter.setText("Quitter");
        menuFichierQuitter.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                menuFichierQuitterActionPerformed(evt);
            }
        });
        menuFichier.add(menuFichierQuitter);

        topMenuBar.add(menuFichier);

        menuEdition.setText("Edition");

        menuEditionAnnuler.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK));
        menuEditionAnnuler.setText("Annuler");
        menuEditionAnnuler.setEnabled(false);
        menuEditionAnnuler.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                menuEditionAnnulerActionPerformed(evt);
            }
        });
        menuEdition.add(menuEditionAnnuler);

        menuEditionRepeter.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.CTRL_DOWN_MASK));
        menuEditionRepeter.setText("Répéter");
        menuEditionRepeter.setEnabled(false);
        menuEditionRepeter.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                menuEditionRepeterActionPerformed(evt);
            }
        });
        menuEdition.add(menuEditionRepeter);

        topMenuBar.add(menuEdition);

        menuAffichage.setText("Affichage");

        menuGridDistance.setText("Distance grille");
        menuGridDistance.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                menuGridDistanceActionPerformed(evt);
            }
        });
        menuAffichage.add(menuGridDistance);

        inspectorMenuItem.setText("Taille minimale inspecteur");
        inspectorMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                inspectorMenuItemActionPerformed(evt);
            }
        });
        menuAffichage.add(inspectorMenuItem);
        menuAffichage.add(jSeparator8);

        menuCustomZoom.setText("Zoom personnalisé");
        menuCustomZoom.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                menuCustomZoomActionPerformed(evt);
            }
        });
        menuAffichage.add(menuCustomZoom);

        menuResetZoom.setText("Réinitialiser le zoom");
        menuResetZoom.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                menuResetZoomActionPerformed(evt);
            }
        });
        menuAffichage.add(menuResetZoom);

        topMenuBar.add(menuAffichage);

        menuAide.setText("Aide");

        menuAidePropos.setText("À propos de");
        menuAidePropos.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                menuAideProposActionPerformed(evt);
            }
        });
        menuAide.add(menuAidePropos);

        topMenuBar.add(menuAide);

        setJMenuBar(topMenuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(toolBar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(mainPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(toolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void canvasPanelMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_canvasPanelMouseMoved
        updateMouseCoordinates(evt);
        Point2D.Double metricPoint = pointToMetric(evt.getPoint());
        if (controller.projectExists())
        {
            showTileInfo(controller.getTileAtPoint(metricPoint));
        }
        if (selectedMode == ApplicationModes.RECTANGLE && firstRectangleCorner != null)
        {
            canvasPanelMouseMovedTemporaryRectangle(metricPoint);
        }
        else if (selectedMode == ApplicationModes.CIRCLE && firstEllipseCorner != null)
        {
            canvasPanelMouseMovedTemporaryCircle(metricPoint);
        }
        else if (selectedMode == ApplicationModes.POLYGON && numberVertices > 0)
        {
            canvasPanelMouseMovedTemporaryPolygon(metricPoint);
        }
    }//GEN-LAST:event_canvasPanelMouseMoved

    private void canvasPanelMouseMovedTemporaryRectangle(Point2D.Double metricPoint)
    {
        if (gridIsMagnetic)
        {
            Utilities.movePointToGrid(metricPoint, canvasPanel.getGridDistance());
        }
        canvasPanel.setTemporaryRectangle(Utilities.cornersToRectangle(firstRectangleCorner,
                metricPoint));
        canvasPanel.repaint();
    }
    
    private void canvasPanelMouseMovedTemporaryCircle(Point2D.Double metricPoint)
    {
        if (gridIsMagnetic)
        {
            Utilities.movePointToGrid(metricPoint, canvasPanel.getGridDistance());
        }
        canvasPanel.setTemporaryEllipse(Utilities.cornersToRectangle(firstEllipseCorner,
                metricPoint));
        canvasPanel.repaint();
    }
    
    private void canvasPanelMouseMovedTemporaryPolygon(Point2D.Double metricPoint)
    {
        if (gridIsMagnetic)
        {
            Utilities.movePointToGrid(metricPoint, canvasPanel.getGridDistance());
        }
        if (numberVertices == 1)
        {
            Point2D firstPoint = polygonInProgress.getCurrentPoint();
            canvasPanel.setTemporaryLine(new Line2D.Double(firstPoint, metricPoint));
        }
        else
        {
            Path2D.Double pathCopy = new Path2D.Double(polygonInProgress);
            pathCopy.lineTo(metricPoint.x, metricPoint.y);
            canvasPanel.setTemporaryPolygon(pathCopy);
        }
        canvasPanel.repaint();
    } 
    
    private void updateMouseCoordinates(MouseEvent evt)
    {
        int posXPixels = evt.getX() + canvasPanel.getHorizontalOffset();
        int posYPixels = evt.getY() + canvasPanel.getVerticalOffset();
        xPixelCoordsLabel.setText("X: " + posXPixels + " pixels");
        yPixelCoordsLabel.setText("Y: " + posYPixels + " pixels");
        Point2D.Double metricPoint = pointToMetric(evt.getPoint());
        double posXMetric = metricPoint.x;
        double posYMetric = metricPoint.y;     
        if (isMetric)
        {
            xMeasureCoordsLabel.setText("X: " + String.format("%.03f", posXMetric / 1000.)
                    + " mètres");
            yMeasureCoordsLabel.setText("Y: " + String.format("%.03f", posYMetric / 1000.)
                    + " mètres");            
        }
        else
        {
            int posXFeet = Utilities.mmToFeet(posXMetric);
            double posXInches = Utilities.mmToRemainingInches(posXMetric);
            int posYFeet = Utilities.mmToFeet(posYMetric);
            double posYInches = Utilities.mmToRemainingInches(posYMetric);
            xMeasureCoordsLabel.setText("X: " + posXFeet + "ft "
                    + String.format("%.02f", posXInches) + "in");
            yMeasureCoordsLabel.setText("Y: " + posYFeet + "ft "
                    + String.format("%.02f", posYInches) + "in");
        }
    }

    private void zoomOutButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zoomOutButtonActionPerformed
        double newZoom = canvasPanel.zoomOutIncrement();
        if (newZoom * 100 > 5)
        {
            zoomLabel.setText(String.valueOf((int) (newZoom * 100)));
        }
        else if(newZoom * 100 > 0.001)
        {
            zoomLabel.setText(String.format("%.03f", newZoom * 100));
        }
        else
        {
            zoomLabel.setText("(" + String.format("%6.3e", newZoom * 100) + ")");
        }
        canvasPanel.repaint();
    }//GEN-LAST:event_zoomOutButtonActionPerformed

    private void zoomInButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zoomInButtonActionPerformed
        double newZoom = canvasPanel.zoomInIncrement();
        
        if (newZoom * 100 > 5)
        {
            zoomLabel.setText(String.valueOf((int) (newZoom * 100)));
        }
        else if(newZoom * 100 > 0.001)
        {
            zoomLabel.setText(String.format("%.03f", newZoom * 100));
        }
        else
        {
            zoomLabel.setText("(" + String.format("%6.3e", newZoom * 100) + ")");
        }
        
        canvasPanel.repaint();
    }//GEN-LAST:event_zoomInButtonActionPerformed

    private void menuFichierQuitterActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_menuFichierQuitterActionPerformed
    {//GEN-HEADEREND:event_menuFichierQuitterActionPerformed
        if (JOptionPane.showConfirmDialog(null, "Voulez-vous quitter?", "Quitter",
                JOptionPane.YES_NO_OPTION) == 0)
        {
            System.exit(0);
        }
    }//GEN-LAST:event_menuFichierQuitterActionPerformed

    private void menuAideProposActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_menuAideProposActionPerformed
    {//GEN-HEADEREND:event_menuAideProposActionPerformed
        JOptionPane.showMessageDialog(null, "VirtuTuile 2019\n"
                + "vous est présenté par\n"
                + "Équipe 8:\n"
                + "Petros Fytilis\n" + "Gabriel Chevrette-Parrot\n"
                + "Martin Sasseville", "À propos de",
                JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_menuAideProposActionPerformed

    private void saveProjectButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_saveProjectButtonActionPerformed
    {//GEN-HEADEREND:event_saveProjectButtonActionPerformed
        if (toolBar.isVisible())
        {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Enregistrer Projet");
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.addChoosableFileFilter(filter);
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
            {
                controller.saveProject(new File(fileChooser.getSelectedFile().toString() + ".ser"));
            }
        }
    }//GEN-LAST:event_saveProjectButtonActionPerformed

    private void canvasPanelMouseWheelMoved(java.awt.event.MouseWheelEvent evt)//GEN-FIRST:event_canvasPanelMouseWheelMoved
    {//GEN-HEADEREND:event_canvasPanelMouseWheelMoved
        double newZoom = canvasPanel.changeZoom(evt.getWheelRotation(), evt.getX(), evt.getY(),
                horizontalScrollBar.getMaximum(), verticalScrollBar.getMaximum());
        if (newZoom * 100 > 5)
        {
            zoomLabel.setText(String.valueOf((int) (newZoom * 100)));
        }
        else if(newZoom * 100 > 0.001)
        {
            if (String.valueOf(newZoom * 100).length() > 6)
            {
                zoomLabel.setText(String.valueOf(newZoom * 100).substring(0, 5));   
            }
            else
            {
                zoomLabel.setText(String.valueOf(newZoom * 100)); 
            }
        }
        else
        {
            zoomLabel.setText("(" + String.format("%6.3e", newZoom * 100) + ")");
        }
          

        canvasPanel.repaint();
        horizontalScrollBar.setValue(canvasPanel.getHorizontalOffset());
        verticalScrollBar.setValue(canvasPanel.getVerticalOffset());
    }//GEN-LAST:event_canvasPanelMouseWheelMoved

    private void verticalScrollBarAdjustmentValueChanged(java.awt.event.AdjustmentEvent evt)//GEN-FIRST:event_verticalScrollBarAdjustmentValueChanged
    {//GEN-HEADEREND:event_verticalScrollBarAdjustmentValueChanged
        canvasPanel.setVerticalOffset(evt.getValue());
        canvasPanel.repaint();
    }//GEN-LAST:event_verticalScrollBarAdjustmentValueChanged

    private void horizontalScrollBarAdjustmentValueChanged(java.awt.event.AdjustmentEvent evt)//GEN-FIRST:event_horizontalScrollBarAdjustmentValueChanged
    {//GEN-HEADEREND:event_horizontalScrollBarAdjustmentValueChanged
        canvasPanel.setHorizontalOffset(evt.getValue());
        canvasPanel.repaint();
    }//GEN-LAST:event_horizontalScrollBarAdjustmentValueChanged

    private void canvasPanelMousePressed(java.awt.event.MouseEvent evt)//GEN-FIRST:event_canvasPanelMousePressed
    {//GEN-HEADEREND:event_canvasPanelMousePressed
        if (!toolBar.isVisible()) return;
        // LEFT CLICK
        if (evt.getButton() == MouseEvent.BUTTON1)
        {
            if (contextMode != ContextMenuModes.NONE)
            {
                contextMenuActionHandler(evt);
            } else
            {
                leftClickActionHandler(evt);
            }
        } // RIGHT CLICK
        else if (evt.getButton() == MouseEvent.BUTTON3)
        {
            rightClickActionHandler(evt);
        }
    }//GEN-LAST:event_canvasPanelMousePressed

    private void leftClickActionHandler(MouseEvent evt)
    {
        switch (selectedMode)
        {
            case MERGE:
                mergeSelectedSurfaces(pointToMetric(evt.getPoint()));
                break;

            case SELECT:
                selectSurface(pointToMetric(evt.getPoint()));
                break;

            case RECTANGLE:
                createNewRectangularSurface(pointToMetric(evt.getPoint()));
                break;
                
            case CIRCLE:
                createNewCircularSurface(pointToMetric(evt.getPoint()));
                break;

            case POLYGON:
                addPointToPolygon(pointToMetric(evt.getPoint()));
                break;
                
            case SURFACEMOVE:
                Point2D.Double point = pointToMetric(evt.getPoint());
                selectSurface(point);
                if (controller.surfaceIsSelected())
                {
                    Rectangle2D bounds = controller.getBounds2D();
                    this.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    originPoint = new Point2D.Double(point.x - bounds.getX(),
                            point.y - bounds.getY());
                }
                break;

            // Début le déplacement du covering à l'intérieur d'une surface pointée
            case TILEMOVE:
                originPoint = pointToMetric(evt.getPoint());
                selectSurface((Point2D.Double) originPoint);
                if (controller.surfaceIsSelected())
                {
                    this.setCursor(new Cursor(Cursor.HAND_CURSOR));
                }
                break;
        }
    }

    private void rightClickActionHandler(MouseEvent evt)
    {
        if (selectedMode == ApplicationModes.RECTANGLE && firstRectangleCorner != null)
        {
            unselectSurface();
            return;
        }
        else if (selectedMode == ApplicationModes.CIRCLE && firstEllipseCorner != null)
        {
            unselectSurface();
            return;
        }
        else if (selectedMode == ApplicationModes.POLYGON && polygonInProgress != null)
        {
            createIrregularSurface();
            return;
        }
        selectSurface(pointToMetric(evt.getPoint()));
        if (!controller.surfaceIsSelected()) return;
        // Display le context menu
        if (controller.surfaceIsRectangular())
        {
            decombineMenuItem.setEnabled(false);
            pushSubMenu.setEnabled(true);
            double surroundingBounds[] = controller.getSurroundingBounds();
            if (surroundingBounds[2] == Integer.MAX_VALUE)
            {
                pushRightMenuItem.setEnabled(false);
            } else
            {
                pushRightMenuItem.setEnabled(true);
            }
            if (surroundingBounds[3] == Integer.MAX_VALUE)
            {
                pushBottomMenuItem.setEnabled(false);
            } else
            {
                pushBottomMenuItem.setEnabled(true);
            }
        }
        else if (controller.surfaceIsCombined())
        {
            decombineMenuItem.setEnabled(true);
            pushSubMenu.setEnabled(false);
        }
        else
        {
            decombineMenuItem.setEnabled(false);
            pushSubMenu.setEnabled(false);
        }
        if (controller.isHole() || !controller.hasTileType())
        {
            centerPatternSubMenu.setEnabled(false);
            startPatternSubMenu.setEnabled(false);
        }
        else
        {
            centerPatternSubMenu.setEnabled(true);
            startPatternSubMenu.setEnabled(true);
        }
        surfacePopupMenu.show(canvasPanel, evt.getX(), evt.getY());
    }

    private void contextMenuActionHandler(MouseEvent evt)
    {
        switch (contextMode)
        {
            case NONE:
                break;
            case ALIGN_TOP:
                alignTop(pointToMetric(evt.getPoint()));
                break;
            case ALIGN_BOTTOM:
                alignBottom(pointToMetric(evt.getPoint()));
                break;
            case ALIGN_LEFT:
                alignLeft(pointToMetric(evt.getPoint()));
                break;
            case ALIGN_RIGHT:
                alignRight(pointToMetric(evt.getPoint()));
                break;
            case CENTER_H:
                centerH(pointToMetric(evt.getPoint()));
                break;
            case CENTER_V:
                centerV(pointToMetric(evt.getPoint()));
                break;
            case STICK_H:
                stickH(pointToMetric(evt.getPoint()));
                break;
            case STICK_V:
                stickV(pointToMetric(evt.getPoint()));
                break;
            case RELATIVE_MOVE:
                moveRelatively(pointToMetric(evt.getPoint()));
        }
        contextMode = ContextMenuModes.NONE;
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    private void mergeSelectedSurfaces(Point2D.Double point)
    {
        selectSurface(point);
        if (controller.surfaceIsSelected())
        {
            if (!controller.mergeIsInProgress())
            {
                controller.setFirstSurfaceToMerge();
            }
            else
            {
                boolean flag = controller.mergeSurfaces();
                if (flag)
                {
                    controller.selectLastSurfaceAdded();
                }
                else
                {
                    unselectSurface();
                    JOptionPane.showMessageDialog(this,
                            "La combinaison ne peut pas être effectuée.",
                            "Opération interdite", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        else
        {
            unselectSurface();
        }
    }

    private void selectSurface(Point2D.Double point)
    {
        controller.selectVertex(point);
        if (controller.vertexIsSelected())
        {
            movingVertex = true;
        }
        else
        {
            controller.selectSurface(point);
            if (controller.surfaceIsSelected())
            {
                enablePanelButtons();
                updatePanelInformation();
                canvasPanel.repaint();
            }
            else
            {
                unselectSurface();
            }
        }
    }

    private void createNewRectangularSurface(Point2D.Double point)
    {
        if (canvasPanel.getZoom() * 100 < 5)
        {
            JOptionPane.showMessageDialog(this, 
                    "Impossible de créer une surface avec un zoom inférieur à 5%.\nVeuillez augmenter le zoom, puis réessayer", 
                    "Action interdite", JOptionPane.ERROR_MESSAGE);
        }
        else
        {
            if (gridIsMagnetic)
            {
                Utilities.movePointToGrid(point, canvasPanel.getGridDistance());
            }
            if (firstRectangleCorner == null)
            {
                unselectSurface();
                firstRectangleCorner = point;
            }
            else
            {
                Rectangle2D.Double rectangle = Utilities.cornersToRectangle(firstRectangleCorner, point);
                unselectSurface();
                if (rectangle.width < 100 || rectangle.height < 100)
                {
                    canvasPanel.repaint();
                    JOptionPane.showMessageDialog(this, "Surface trop petite.",
                            "Opération interdite", JOptionPane.ERROR_MESSAGE);
                }
                else
                {
                    boolean status = controller.addRectangularSurface(rectangle);
                    if (status)
                    {
                        controller.selectLastSurfaceAdded();
                        enablePanelButtons();
                        updatePanelInformation();
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(this, "Création de la surface entraîne un conflit.",
                                "Opération interdite", JOptionPane.ERROR_MESSAGE);
                    }
                    canvasPanel.repaint();
                }
            }
        }
    }
    
    private void createNewCircularSurface(Point2D.Double point)
    {
        if (canvasPanel.getZoom() * 100 < 5)
        {
            JOptionPane.showMessageDialog(this, 
                    "Impossible de créer une surface avec un zoom inférieur à 5%.\nVeuillez augmenter le zoom, puis réessayer", 
                    "Action interdite", JOptionPane.ERROR_MESSAGE);
        }
        else
        {
            if (gridIsMagnetic)
            {
                Utilities.movePointToGrid(point, canvasPanel.getGridDistance());
            }
            if (firstEllipseCorner == null)
            {
                unselectSurface();
                firstEllipseCorner = point;
            }
            else
            {
                Rectangle2D.Double rectangle = Utilities.cornersToRectangle(firstEllipseCorner, point);
                unselectSurface();
                if (rectangle.width < 100 || rectangle.height < 100)
                {
                    canvasPanel.repaint();
                    JOptionPane.showMessageDialog(this, "Surface trop petite.",
                            "Opération interdite", JOptionPane.ERROR_MESSAGE);
                }
                else
                {
                    boolean status = controller.addCircularSurface(rectangle);
                    if (status)
                    {
                        controller.selectLastSurfaceAdded();
                        enablePanelButtons();
                        updatePanelInformation();
                    }
                    else
                    {
                        
                        JOptionPane.showMessageDialog(this, "Création de la surface entraîne un conflit.",
                                "Opération interdite", JOptionPane.ERROR_MESSAGE);
                    }
                    canvasPanel.repaint();
                }
            }
        }
    }

    private void addPointToPolygon(Point2D.Double point)
    {
        if (canvasPanel.getZoom() * 100 < 5)
        {
            JOptionPane.showMessageDialog(this, 
                    "Impossible de créer une surface avec un zoom inférieur à 5%.\nVeuillez augmenter le zoom, puis réessayer", 
                    "Action interdite", JOptionPane.ERROR_MESSAGE);
        }
        else
        {
            if (gridIsMagnetic)
            {
                Utilities.movePointToGrid(point, canvasPanel.getGridDistance());
            }
            if (polygonInProgress == null)
            {
                polygonInProgress = new Path2D.Double();
                polygonInProgress.moveTo(point.x, point.y);
                numberVertices++;
            }
            else
            {
                if (numberVertices == 1)
                {
                    Point2D firstPoint = polygonInProgress.getCurrentPoint();
                    canvasPanel.setTemporaryLine(new Line2D.Double(
                            firstPoint.getX(), firstPoint.getY(),
                            point.x, point.y));
                }
                else
                {
                    canvasPanel.setTemporaryLine(null);
                }
                polygonInProgress.lineTo(point.x, point.y);
                numberVertices++;
                canvasPanel.setTemporaryPolygon(polygonInProgress);
                repaint();
            }
        }
    }
    
    private void createIrregularSurface()
    {
        boolean status = false;
        Rectangle2D bounds = polygonInProgress.getBounds2D();
        if (bounds.getWidth() < 100 || bounds.getHeight() < 100 || numberVertices < 3)
        {
            JOptionPane.showMessageDialog(this, "Surface trop petite.",
                            "Opération interdite", JOptionPane.ERROR_MESSAGE);
        }
        else
        {
            status = controller.addIrregularSurface(polygonInProgress);
            if (!status)
            {
                JOptionPane.showMessageDialog(this, "Création de la surface impossible.",
                        "Opération interdite", JOptionPane.ERROR_MESSAGE);
            }
        }
        unselectSurface();
        if (status)
        {
            controller.selectLastSurfaceAdded();
            enablePanelButtons();
            updatePanelInformation();
        }
        repaint();
    }
    
    private void rectangleToggleActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_rectangleToggleActionPerformed
    {//GEN-HEADEREND:event_rectangleToggleActionPerformed
        selectedMode = ApplicationModes.RECTANGLE;
        unselect();
    }//GEN-LAST:event_rectangleToggleActionPerformed

    private void selectionToggleActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_selectionToggleActionPerformed
    {//GEN-HEADEREND:event_selectionToggleActionPerformed
        selectedMode = ApplicationModes.SELECT;
        unselect();
    }//GEN-LAST:event_selectionToggleActionPerformed

    private void polygonToggleActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_polygonToggleActionPerformed
    {//GEN-HEADEREND:event_polygonToggleActionPerformed
        selectedMode = ApplicationModes.POLYGON;
        unselect();
    }//GEN-LAST:event_polygonToggleActionPerformed

    private void surfaceMoveToggleActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_surfaceMoveToggleActionPerformed
    {//GEN-HEADEREND:event_surfaceMoveToggleActionPerformed
        selectedMode = ApplicationModes.SURFACEMOVE;
        unselect();
    }//GEN-LAST:event_surfaceMoveToggleActionPerformed

    private void canvasPanelMouseDragged(java.awt.event.MouseEvent evt)//GEN-FIRST:event_canvasPanelMouseDragged
    {//GEN-HEADEREND:event_canvasPanelMouseDragged
        canvasPanelMouseMoved(evt);
        if (selectedMode == ApplicationModes.SURFACEMOVE && controller.surfaceIsSelected()
                && originPoint != null)
        {
            mouseDraggedSurfaceMove(evt);
        }
        else if (selectedMode == ApplicationModes.RECTANGLE && firstRectangleCorner != null)
        {
            mouseDraggedRectangle(evt);
        }
        else if (selectedMode == ApplicationModes.CIRCLE && firstEllipseCorner != null)
        {
            mouseDraggedCircle(evt);
        }
        else if (selectedMode == ApplicationModes.TILEMOVE && controller.surfaceIsSelected()
                && originPoint != null && controller.hasTileType())
        {
            mouseDraggedTileMove(evt);
        }
        else if (selectedMode == ApplicationModes.SELECT && controller.surfaceIsSelected() && movingVertex && 
                canvasPanel.getZoom() > 5)
        {
            mouseDraggedVertexMove(evt);
        }
    }//GEN-LAST:event_canvasPanelMouseDragged

    private void mouseDraggedSurfaceMove(MouseEvent evt)
    {
        Point2D.Double currentCursorPosition = pointToMetric(evt.getPoint());
        double x = currentCursorPosition.x - originPoint.x;
        double y = currentCursorPosition.y - originPoint.y;
        Point2D.Double newPos = new Point2D.Double(x, y);
        if (gridIsMagnetic)
        {
            Utilities.movePointToGrid(newPos, canvasPanel.getGridDistance());
        }
        controller.moveSurfaceToPoint(newPos);
        updatePanelInformation();
        canvasPanel.repaint();
    }
    
    private void mouseDraggedRectangle(MouseEvent evt)
    {
        Point2D.Double point = pointToMetric(evt.getPoint());
        if (gridIsMagnetic)
        {
            Utilities.movePointToGrid(point, canvasPanel.getGridDistance());
        }
        canvasPanel.setTemporaryRectangle(Utilities.cornersToRectangle(firstRectangleCorner,
                point));
        canvasPanel.repaint();
    }
    
    private void mouseDraggedCircle(MouseEvent evt)
    {
        Point2D.Double point = pointToMetric(evt.getPoint());
        if (gridIsMagnetic)
        {
            Utilities.movePointToGrid(point, canvasPanel.getGridDistance());
        }
        canvasPanel.setTemporaryEllipse(Utilities.cornersToRectangle(firstEllipseCorner,
                point));
        canvasPanel.repaint();
    }
    
    private void mouseDraggedTileMove(MouseEvent evt)
    {
        Point2D.Double currentCursorPosition = pointToMetric(evt.getPoint());
        double deltaX = currentCursorPosition.x - originPoint.x;
        double deltaY = currentCursorPosition.y - originPoint.y;
        double offsetX = controller.getOffsetX() + deltaX;
        double offsetY = controller.getOffsetY() + deltaY;
        controller.setOffsetXY(offsetX, offsetY);
        originPoint = currentCursorPosition;
        updatePanelInformation();
        canvasPanel.repaint();
    }
    
    private void mouseDraggedVertexMove(MouseEvent evt)
    {
        Point2D.Double point = pointToMetric(evt.getPoint());
        if (gridIsMagnetic)
        {
            Utilities.movePointToGrid(point, canvasPanel.getGridDistance());
        }
        controller.moveVertexToPoint(point);
        repaint();
    }
    
    private void canvasPanelMouseReleased(java.awt.event.MouseEvent evt)//GEN-FIRST:event_canvasPanelMouseReleased
    {//GEN-HEADEREND:event_canvasPanelMouseReleased
        movingVertex = false;
        if (originPoint != null)
        {
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            originPoint = null;
        }
    }//GEN-LAST:event_canvasPanelMouseReleased

    /**
     * Affiche un dialogue pour modifier la distance de la grille.
     *
     * @param evt
     */
    private void menuGridDistanceActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_menuGridDistanceActionPerformed
    {//GEN-HEADEREND:event_menuGridDistanceActionPerformed
        JDialog dialog;
        if (isMetric)
        {
            dialog = gridDistanceDialog;
        } else
        {
            dialog = imperialGridDistanceDialog;
        }

        dialog.setSize(dialog.getPreferredSize());
        dialog.setModal(true);
        dialog.setVisible(true);
    }//GEN-LAST:event_menuGridDistanceActionPerformed

    private void gridDistanceSliderStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_gridDistanceSliderStateChanged
    {//GEN-HEADEREND:event_gridDistanceSliderStateChanged
        gridDistanceLabel.setText(gridDistanceSlider.getValue() + " cm");
    }//GEN-LAST:event_gridDistanceSliderStateChanged

    private void gridDistanceOKButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_gridDistanceOKButtonActionPerformed
    {//GEN-HEADEREND:event_gridDistanceOKButtonActionPerformed
        canvasPanel.setGridDistance(gridDistanceSlider.getValue() * 10 / Utilities.MM_PER_PIXEL);
        canvasPanel.repaint();
        gridDistanceDialog.dispose();
    }//GEN-LAST:event_gridDistanceOKButtonActionPerformed

    private void imperialGridDistanceSliderStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_imperialGridDistanceSliderStateChanged
    {//GEN-HEADEREND:event_imperialGridDistanceSliderStateChanged
        imperialGridDistanceLabel.setText(imperialGridDistanceSlider.getValue() + " pouces");
    }//GEN-LAST:event_imperialGridDistanceSliderStateChanged

    private void imperialGridDistanceOKButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_imperialGridDistanceOKButtonActionPerformed
    {//GEN-HEADEREND:event_imperialGridDistanceOKButtonActionPerformed
        double value = imperialGridDistanceSlider.getValue() * 10 / Utilities.MM_PER_PIXEL;
        canvasPanel.setGridDistance(Utilities.inchesToCm(value));
        canvasPanel.repaint();
        imperialGridDistanceDialog.dispose();
    }//GEN-LAST:event_imperialGridDistanceOKButtonActionPerformed

    private void magnetButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_magnetButtonActionPerformed
    {//GEN-HEADEREND:event_magnetButtonActionPerformed
        gridIsMagnetic = !gridIsMagnetic;
    }//GEN-LAST:event_magnetButtonActionPerformed

    private void debugToggleButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_debugToggleButtonActionPerformed
    {//GEN-HEADEREND:event_debugToggleButtonActionPerformed
        canvasPanel.toggleIsDebug();
        canvasPanel.repaint();
    }//GEN-LAST:event_debugToggleButtonActionPerformed

    private void pushLeftJMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_pushLeftJMenuItemActionPerformed
    {//GEN-HEADEREND:event_pushLeftJMenuItemActionPerformed
        double bounds[] = controller.getSurroundingBounds();
        controller.moveSurfaceToPoint(new Point2D.Double(bounds[0], controller.getBounds2D().getY()));
        canvasPanel.repaint();
        updatePanelInformation();
    }//GEN-LAST:event_pushLeftJMenuItemActionPerformed

    private void pushRightMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_pushRightMenuItemActionPerformed
    {//GEN-HEADEREND:event_pushRightMenuItemActionPerformed
        double bounds[] = controller.getSurroundingBounds();
        if (bounds[2] != Integer.MAX_VALUE)
        {
            controller.moveSurfaceToPoint(new Point2D.Double(bounds[2] - controller.getBounds2D().getWidth(),
                    controller.getBounds2D().getY()));
            canvasPanel.repaint();
            updatePanelInformation();
        }
    }//GEN-LAST:event_pushRightMenuItemActionPerformed

    private void pushTopMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_pushTopMenuItemActionPerformed
    {//GEN-HEADEREND:event_pushTopMenuItemActionPerformed
        double bounds[] = controller.getSurroundingBounds();
        controller.moveSurfaceToPoint(new Point2D.Double(controller.getBounds2D().getX(),
                bounds[1]));
        canvasPanel.repaint();
        updatePanelInformation();
    }//GEN-LAST:event_pushTopMenuItemActionPerformed

    private void pushBottomMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_pushBottomMenuItemActionPerformed
    {//GEN-HEADEREND:event_pushBottomMenuItemActionPerformed
        double bounds[] = controller.getSurroundingBounds();
        if (bounds[3] != Integer.MAX_VALUE)
        {
            controller.moveSurfaceToPoint(new Point2D.Double(controller.getBounds2D().getX(),
                    bounds[3] - controller.getBounds2D().getHeight()));
            canvasPanel.repaint();
            updatePanelInformation();
        }
    }//GEN-LAST:event_pushBottomMenuItemActionPerformed

    private void deleteSurfaceMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_deleteSurfaceMenuItemActionPerformed
    {//GEN-HEADEREND:event_deleteSurfaceMenuItemActionPerformed
        controller.removeSelectedSurface();
        unselectSurface();
    }//GEN-LAST:event_deleteSurfaceMenuItemActionPerformed

    private void formComponentResized(java.awt.event.ComponentEvent evt)//GEN-FIRST:event_formComponentResized
    {//GEN-HEADEREND:event_formComponentResized

    }//GEN-LAST:event_formComponentResized
    
    private void tileNbPerBoxFieldActionPerformed(ActionEvent evt)
       {
           try
           {
                int newTileNbPerBox = Integer.parseInt(tileNbPerBoxField.getText());
                if (newTileNbPerBox >= 1)
                {
                    controller.setTileNbPerBox(newTileNbPerBox);
                    tileNbPerBoxField.setText(String.format("%d", controller.getTileNbPerBox()));
                }
                else
                {
                    JOptionPane.showMessageDialog(this, "Une boîte doit contenir au moins 1 tuile", 
                            "Opération interdite", JOptionPane.ERROR_MESSAGE);
                    tileNbPerBoxField.setText(String.format("%d", controller.getTileNbPerBox()));
                }
           } catch (NumberFormatException e)
           {
               tileNbPerBoxField.setText(String.format("%d", controller.getTileNbPerBox()));
           }
       }

    private void tileNameFieldActionPerformed(ActionEvent evt)
       {
            String newTileName = tileNameField.getText();
           
            if(newTileName.length() < 5)
            {
                JOptionPane.showMessageDialog(this, "Le nom de la tuile doit avoir au moins 5 lettres", 
                        "Opération interdite", JOptionPane.ERROR_MESSAGE);
                tileNameField.setText(controller.getTileName());
            }
            else
            {
                controller.setTileName(newTileName);
                tileNameField.setText(controller.getTileName());
                updatePanelInformation();
            }
       }
    
    private void metricButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_metricButtonActionPerformed
    {//GEN-HEADEREND:event_metricButtonActionPerformed
        isMetric = true;
        xPixelCoordsLabel.setText("X: 0 pixels");
        yPixelCoordsLabel.setText("Y: 0 pixels");
        xMeasureCoordsLabel.setText("X: 0.000 mètres");
        yMeasureCoordsLabel.setText("Y: 0.000 mètres");
        xFtLabel.setText("m");
        yFtLabel.setText("m");
        widthFtLabel.setText("m");
        heightFtLabel.setText("m");
        offsetXText.setText("cm");
        offsetYText.setText("cm");
        largeurJointText.setText("mm");
        largeurTuileText.setText("cm");
        hauteurTuileText.setText("cm");
        createWindowWidthLabel.setText("cm");
        createWindowHeightLabel.setText("cm");
        surfaceXFieldInches.setVisible(false);
        xInLabel.setVisible(false);
        surfaceYFieldInches.setVisible(false);
        yInLabel.setVisible(false);
        surfaceWidthFieldInches.setVisible(false);
        widthInLabel.setVisible(false);
        surfaceHeightFieldInches.setVisible(false);
        heightInLabel.setVisible(false);

        if (controller.surfaceIsSelected())
        {
            updatePanelInformation();
        }

    }//GEN-LAST:event_metricButtonActionPerformed

    private void imperialButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_imperialButtonActionPerformed
    {//GEN-HEADEREND:event_imperialButtonActionPerformed
        isMetric = false;
        xPixelCoordsLabel.setText("X: 0 pixels");
        yPixelCoordsLabel.setText("Y: 0 pixels");
        xMeasureCoordsLabel.setText("X: 0ft 0.00in");
        yMeasureCoordsLabel.setText("Y: 0ft 0.00in");
        xFtLabel.setText("ft");
        yFtLabel.setText("ft");
        widthFtLabel.setText("ft");
        heightFtLabel.setText("ft");
        offsetXText.setText("in");
        offsetYText.setText("in");
        largeurJointText.setText("in");
        largeurTuileText.setText("in");
        hauteurTuileText.setText("in");
        createWindowWidthLabel.setText("in");
        createWindowHeightLabel.setText("in");
        surfaceXFieldInches.setVisible(true);
        xInLabel.setVisible(true);
        surfaceYFieldInches.setVisible(true);
        yInLabel.setVisible(true);
        surfaceWidthFieldInches.setVisible(true);
        widthFtLabel.setVisible(true);
        widthInLabel.setVisible(true);
        surfaceHeightFieldInches.setVisible(true);
        heightInLabel.setVisible(true);

        if (controller.surfaceIsSelected())
        {
            updatePanelInformation();
        }
    }//GEN-LAST:event_imperialButtonActionPerformed

    private void mergeToggleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mergeToggleActionPerformed
        this.selectedMode = ApplicationModes.MERGE;
        unselect();
    }//GEN-LAST:event_mergeToggleActionPerformed

    private void menuEditionAnnulerActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_menuEditionAnnulerActionPerformed
    {//GEN-HEADEREND:event_menuEditionAnnulerActionPerformed
        controller.undo();
        if (controller.surfaceIsSelected())
        {
            updatePanelInformation();
        }
        else
        {
            unselectSurface();
        }
        canvasPanel.repaint();
    }//GEN-LAST:event_menuEditionAnnulerActionPerformed

    private void menuEditionRepeterActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_menuEditionRepeterActionPerformed
    {//GEN-HEADEREND:event_menuEditionRepeterActionPerformed
        controller.redo();
        if (controller.surfaceIsSelected())
        {
            updatePanelInformation();
        }
        else
        {
            unselectSurface();
        }
        canvasPanel.repaint();
    }//GEN-LAST:event_menuEditionRepeterActionPerformed

    private void undoButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_undoButtonActionPerformed
    {//GEN-HEADEREND:event_undoButtonActionPerformed
        controller.undo();
        if (controller.surfaceIsSelected())
        {
            updatePanelInformation();
        }
        else
        {
            unselectSurface();
        }
        canvasPanel.repaint();
        undoButton.setToolTipText(controller.getUndoPresentationName());
    }//GEN-LAST:event_undoButtonActionPerformed

    private void redoButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_redoButtonActionPerformed
    {//GEN-HEADEREND:event_redoButtonActionPerformed
        controller.redo();
        if (controller.surfaceIsSelected())
        {
            updatePanelInformation();
        }
        else
        {
            unselectSurface();
        }
        canvasPanel.repaint();
        redoButton.setToolTipText(controller.getRedoPresentationName());
    }//GEN-LAST:event_redoButtonActionPerformed

    private void undoButtonMouseEntered(java.awt.event.MouseEvent evt)//GEN-FIRST:event_undoButtonMouseEntered
    {//GEN-HEADEREND:event_undoButtonMouseEntered
        undoButton.setToolTipText(controller.getUndoPresentationName());
    }//GEN-LAST:event_undoButtonMouseEntered

    private void redoButtonMouseEntered(java.awt.event.MouseEvent evt)//GEN-FIRST:event_redoButtonMouseEntered
    {//GEN-HEADEREND:event_redoButtonMouseEntered
        redoButton.setToolTipText(controller.getRedoPresentationName());
    }//GEN-LAST:event_redoButtonMouseEntered

    private void closeProjectButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_closeProjectButtonActionPerformed
    {//GEN-HEADEREND:event_closeProjectButtonActionPerformed
        if (toolBar.isVisible() && JOptionPane.showConfirmDialog(null,
                "Voulez-vous fermer le projet en cours?",
                "Fermer projet",
                JOptionPane.YES_NO_OPTION) == 0)
        {
            unselectSurface();
            selectedMode = ApplicationModes.NONE;
            toggleGroup.clearSelection();
            controller.closeProject();
            toolBar.setVisible(false);
            createTileButton.setEnabled(false);
            createTileWindow.setVisible(false);
            menuEditionAnnuler.setEnabled(false);
            menuEditionRepeter.setEnabled(false);
            collisionCheckToggleButton.setSelected(false);
        }
    }//GEN-LAST:event_closeProjectButtonActionPerformed

    private void newProjectButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_newProjectButtonActionPerformed
    {//GEN-HEADEREND:event_newProjectButtonActionPerformed
        if (!toolBar.isVisible() || JOptionPane.showConfirmDialog(null,
                "Pour ouvrir un nouveau projet, le projet en cours doit être fermé. Voulez-vous continuer?",
                "Nouveau projet",
                JOptionPane.YES_NO_OPTION) == 0)
        {
            controller.createNewProject();
            collisionCheckToggleButton.setSelected(false);
            toolBar.setVisible(true);
            createTileButton.setEnabled(true);
            menuEditionAnnuler.setEnabled(true);
            menuEditionRepeter.setEnabled(true);
            unselectSurface();
        }
    }//GEN-LAST:event_newProjectButtonActionPerformed

    private void openProjectButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_openProjectButtonActionPerformed
    {//GEN-HEADEREND:event_openProjectButtonActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Ouvrir Projet");
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(filter);
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
        {
            if (toolBar.isVisible())
            {
                unselectSurface();
                selectedMode = ApplicationModes.NONE;
                toggleGroup.clearSelection();
                controller.closeProject();
                controller.createNewProject();
            } else
            {
                controller.createNewProject();
                toolBar.setVisible(true);
                createTileButton.setEnabled(true);
                menuEditionAnnuler.setEnabled(true);
                menuEditionRepeter.setEnabled(true);
                unselectSurface();
            }
            controller.loadProject(fileChooser.getSelectedFile());
            collisionCheckToggleButton.setSelected(false);
            canvasPanel.repaint();
        }
    }//GEN-LAST:event_openProjectButtonActionPerformed

    private void alignTopMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_alignTopMenuItemActionPerformed
    {//GEN-HEADEREND:event_alignTopMenuItemActionPerformed
        contextMode = ContextMenuModes.ALIGN_TOP;
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_alignTopMenuItemActionPerformed

    private void alignTop(Point2D.Double point)
    {
        Rectangle2D bounds = controller.getBounds2DByPoint(point);
        if (bounds != null)
        {
            double newY = bounds.getY();
            boolean flag = controller.setSurfaceY(newY);
            if (flag)
            {
                canvasPanel.repaint();
            } else
            {
                JOptionPane.showMessageDialog(this, "Déplacement illégal.",
                        "Opération interdite", JOptionPane.ERROR_MESSAGE);
            }
        } else
        {
            JOptionPane.showMessageDialog(this, "Aucune surface n'a été sélectionnée.",
                    "Opération interdite", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void alignBottomMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_alignBottomMenuItemActionPerformed
    {//GEN-HEADEREND:event_alignBottomMenuItemActionPerformed
        contextMode = ContextMenuModes.ALIGN_BOTTOM;
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_alignBottomMenuItemActionPerformed

    private void alignBottom(Point2D.Double point)
    {
        Rectangle2D bounds = controller.getBounds2DByPoint(point);
        Rectangle2D selectedBounds = controller.getBounds2D();
        if (bounds != null)
        {
            double newY = bounds.getY() + bounds.getHeight() - selectedBounds.getHeight();
            boolean flag = controller.setSurfaceY(newY);
            if (flag)
            {
                canvasPanel.repaint();
            } else
            {
                JOptionPane.showMessageDialog(this, "Déplacement illégal.",
                        "Opération interdite", JOptionPane.ERROR_MESSAGE);
            }
        } else
        {
            JOptionPane.showMessageDialog(this, "Aucune surface n'a été sélectionnée.",
                    "Opération interdite", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void alignLeftMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_alignLeftMenuItemActionPerformed
    {//GEN-HEADEREND:event_alignLeftMenuItemActionPerformed
        contextMode = ContextMenuModes.ALIGN_LEFT;
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_alignLeftMenuItemActionPerformed

    private void alignLeft(Point2D.Double point)
    {
        Rectangle2D bounds = controller.getBounds2DByPoint(point);
        if (bounds != null)
        {
            double newX = bounds.getX();
            boolean flag = controller.setSurfaceX(newX);
            if (flag)
            {
                canvasPanel.repaint();
            } else
            {
                JOptionPane.showMessageDialog(this, "Déplacement illégal.",
                        "Opération interdite", JOptionPane.ERROR_MESSAGE);
            }
        } else
        {
            JOptionPane.showMessageDialog(this, "Aucune surface n'a été sélectionnée.",
                    "Opération interdite", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void alignRightMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_alignRightMenuItemActionPerformed
    {//GEN-HEADEREND:event_alignRightMenuItemActionPerformed
        contextMode = ContextMenuModes.ALIGN_RIGHT;
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_alignRightMenuItemActionPerformed

    private void alignRight(Point2D.Double point)
    {
        Rectangle2D bounds = controller.getBounds2DByPoint(point);
        Rectangle2D selectedBounds = controller.getBounds2D();
        if (bounds != null)
        {
            double newX = bounds.getX() + bounds.getWidth() - selectedBounds.getWidth();
            boolean flag = controller.setSurfaceX(newX);
            if (flag)
            {
                canvasPanel.repaint();
            } else
            {
                JOptionPane.showMessageDialog(this, "Déplacement illégal.",
                        "Opération interdite", JOptionPane.ERROR_MESSAGE);
            }
        } else
        {
            JOptionPane.showMessageDialog(this, "Aucune surface n'a été sélectionnée.",
                    "Opération interdite", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void centerHMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_centerHMenuItemActionPerformed
    {//GEN-HEADEREND:event_centerHMenuItemActionPerformed
        contextMode = ContextMenuModes.CENTER_H;
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_centerHMenuItemActionPerformed

    private void centerH(Point2D.Double point)
    {
        Rectangle2D bounds = controller.getBounds2DByPoint(point);
        Rectangle2D selectedBounds = controller.getBounds2D();
        if (bounds != null)
        {
            double midPoint = bounds.getX() + bounds.getWidth() / 2;
            double newX = midPoint - selectedBounds.getWidth() / 2;
            boolean flag = controller.setSurfaceX(newX);
            if (flag)
            {
                canvasPanel.repaint();
            } else
            {
                JOptionPane.showMessageDialog(this, "Déplacement illégal.",
                        "Opération interdite", JOptionPane.ERROR_MESSAGE);
            }
        } else
        {
            JOptionPane.showMessageDialog(this, "Aucune surface n'a été sélectionnée.",
                    "Opération interdite", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void centerVMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_centerVMenuItemActionPerformed
    {//GEN-HEADEREND:event_centerVMenuItemActionPerformed
        contextMode = ContextMenuModes.CENTER_V;
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_centerVMenuItemActionPerformed

    private void centerV(Point2D.Double point)
    {
        Rectangle2D bounds = controller.getBounds2DByPoint(point);
        Rectangle2D selectedBounds = controller.getBounds2D();
        if (bounds != null)
        {
            double midPoint = bounds.getY() + bounds.getHeight() / 2;
            double newY = midPoint - selectedBounds.getHeight() / 2;
            boolean flag = controller.setSurfaceY(newY);
            if (flag)
            {
                canvasPanel.repaint();
            } else
            {
                JOptionPane.showMessageDialog(this, "Déplacement illégal.",
                        "Opération interdite", JOptionPane.ERROR_MESSAGE);
            }
        } else
        {
            JOptionPane.showMessageDialog(this, "Aucune surface n'a été sélectionnée.",
                    "Opération interdite", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void stickHMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_stickHMenuItemActionPerformed
    {//GEN-HEADEREND:event_stickHMenuItemActionPerformed
        contextMode = ContextMenuModes.STICK_H;
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_stickHMenuItemActionPerformed

    private void stickH(Point2D.Double point)
    {
        Rectangle2D bounds = controller.getBounds2DByPoint(point);
        Rectangle2D boundsSelected = controller.getBounds2D();
        if (bounds != null && boundsSelected != null)
        {
            if (bounds.equals(boundsSelected))
            {
                JOptionPane.showMessageDialog(this, "Sélection de la même surface.",
                        "Opération interdite", JOptionPane.ERROR_MESSAGE);
            } else
            {
                double newX;
                if (bounds.getX() > boundsSelected.getX())
                {
                    newX = bounds.getX() - boundsSelected.getWidth();
                } else
                {
                    newX = bounds.getX() + bounds.getWidth();
                }
                boolean flag = controller.setSurfaceX(newX);
                if (flag)
                {
                    canvasPanel.repaint();
                } else
                {
                    JOptionPane.showMessageDialog(this, "Déplacement illégal.",
                            "Opération interdite", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else
        {
            JOptionPane.showMessageDialog(this, "Aucune surface n'a été sélectionnée.",
                    "Opération interdite", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void stickVMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_stickVMenuItemActionPerformed
    {//GEN-HEADEREND:event_stickVMenuItemActionPerformed
        contextMode = ContextMenuModes.STICK_V;
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_stickVMenuItemActionPerformed

    private void stickV(Point2D.Double point)
    {
        Rectangle2D bounds = controller.getBounds2DByPoint(point);
        Rectangle2D boundsSelected = controller.getBounds2D();
        if (bounds != null && boundsSelected != null)
        {
            if (bounds.equals(boundsSelected))
            {
                JOptionPane.showMessageDialog(this, "Sélection de la même surface.",
                        "Opération interdite", JOptionPane.ERROR_MESSAGE);
            } else
            {
                double newY;
                if (bounds.getY() > boundsSelected.getY())
                {
                    newY = bounds.getY() - boundsSelected.getHeight();
                } else
                {
                    newY = bounds.getY() + bounds.getHeight();
                }
                boolean flag = controller.setSurfaceY(newY);
                if (flag)
                {
                    canvasPanel.repaint();
                } else
                {
                    JOptionPane.showMessageDialog(this, "Déplacement illégal.", "Opération interdite",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } else
        {
            JOptionPane.showMessageDialog(this, "Aucune surface n'a été sélectionnée.",
                    "Opération interdite", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void tileHeightFieldActionPerformed(ActionEvent evt)
   {
        double newTileHeight;
        if (isMetric)
        {
            try
            {
                newTileHeight = Utilities.parseDoubleLocale(tileHeightField.getText()) * 10;
                if(newTileHeight < 20)
                {
                    JOptionPane.showMessageDialog(this, "Une tuile doit avoir au moins 2 cm ou 1 pouce de hauteur",
                            "Opération interdite", JOptionPane.ERROR_MESSAGE);
                    tileHeightField.setText(String.format("%.03f", controller.getTileHeight() / 10));
                }
                else
                {
                    controller.setTileHeight(newTileHeight);
                    tileHeightField.setText(String.format("%.03f", controller.getTileHeight() / 10));
                }
            } 
            catch (ParseException e)
            {
                tileHeightField.setText(String.format("%.03f", controller.getTileHeight() / 10));
            }
        } 
        else
        {
            try
            {
                double dblInchesField = Utilities.getInchesFromField(tileHeightField.getText());
                newTileHeight = Utilities.inchesToMm(dblInchesField);
                if(newTileHeight < 20)
                {
                    JOptionPane.showMessageDialog(this, "Une tuile doit avoir au moins 2 cm ou 1 pouce de hauteur",
                            "Opération interdite", JOptionPane.ERROR_MESSAGE);
                    tileHeightField.setText(String.format("%.03f", Utilities.mmToInches(controller.getTileHeight())));
                }
                else
                {
                    controller.setTileHeight(newTileHeight);
                    tileHeightField.setText(String.format("%.03f", Utilities.mmToInches(controller.getTileHeight())));
                }
            } 
            catch (ParseException e)
            {
                tileHeightField.setText(String.format("%.03f", Utilities.mmToInches(controller.getTileHeight())));
            } // fin else imperial
        } // fin if isMetric
        controller.refreshSurfaces();
        canvasPanel.repaint();
   }
    
    private void tileMoveToggleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tileMoveToggleActionPerformed
        selectedMode = ApplicationModes.TILEMOVE;
        unselect();
    }//GEN-LAST:event_tileMoveToggleActionPerformed

    private void createTileColorButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_createTileColorButtonActionPerformed
    {//GEN-HEADEREND:event_createTileColorButtonActionPerformed
        Color c = JColorChooser.showDialog(null, "Sélectionnez une couleur",
                createTileColorButton.getBackground());
        if (c != null)
        {
            createTileColorButton.setBackground(c);
        }
    }//GEN-LAST:event_createTileColorButtonActionPerformed

    private void createTileOKButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_createTileOKButtonActionPerformed
    {//GEN-HEADEREND:event_createTileOKButtonActionPerformed
        String newTileName = createTileNameField.getText().strip();
        Color newTileColor = createTileColorButton.getBackground();
        int newTileNbPerBox;
        double newTileWidth;
        double newTileHeight;
        try
        {
            newTileNbPerBox = Integer.parseInt(createTileNbPerBoxField.getText());
            if (isMetric)
            {
                newTileWidth = Utilities.parseDoubleLocale(createTileWidthField.getText()) * 10;
                newTileHeight = Utilities.parseDoubleLocale(createTileHeightField.getText()) * 10;
            }
            else
            {
                newTileWidth = Utilities.getInchesFromField(createTileWidthField.getText());
                newTileWidth = Utilities.inchesToMm(newTileWidth);
                newTileHeight = Utilities.getInchesFromField(createTileHeightField.getText());
                newTileHeight = Utilities.inchesToMm(newTileHeight);
            }
        }
        catch (ParseException | NumberFormatException e)
        {
            JOptionPane.showMessageDialog(this, "Données invalides",
                    "Opération interdite", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (newTileWidth < 20)
        {
            JOptionPane.showMessageDialog(this, "Une tuile doit avoir au moins 2 cm ou 1 pouce de largeur",
                    "Opération interdite", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (newTileHeight < 20)
        {
            JOptionPane.showMessageDialog(this, "Une tuile doit avoir au moins 2 cm ou 1 pouce de hauteur",
                    "Opération interdite", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (newTileName.length() < 5)
        {
            JOptionPane.showMessageDialog(this, "Le nom de la tuile doit avoir au moins 5 lettres",
                    "Opération interdite", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (newTileNbPerBox < 1)
        {
            JOptionPane.showMessageDialog(this, "Une boîte doit contenir au moins 1 tuile",
                    "Opération interdite", JOptionPane.ERROR_MESSAGE);
            return;
        }
        controller.addTileType(newTileWidth, newTileHeight, newTileName, newTileNbPerBox, newTileColor);
        createTileCancelButtonActionPerformed(evt);
        updateTileTypeComboBox();
    }//GEN-LAST:event_createTileOKButtonActionPerformed

    private void updateTileTypeComboBox()
    {
        DefaultComboBoxModel model = new DefaultComboBoxModel(controller.getTileNames());
        tileTypeComboBox.setModel(model);
        if (controller.surfaceIsSelected() && controller.hasTileType())
        {
            tileTypeComboBox.setSelectedItem(controller.getTileName());
        }
        else
        {
            tileTypeComboBox.setSelectedItem(null);
        }
    }
    
    private void createTileCancelButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_createTileCancelButtonActionPerformed
    {//GEN-HEADEREND:event_createTileCancelButtonActionPerformed
        createTileNameField.setText("");
        createTileWidthField.setText("");
        createTileHeightField.setText("");
        createTileColorButton.setBackground(new Color(250,204,161));
        createTileNbPerBoxField.setText("");
        createTileWindow.setVisible(false);
    }//GEN-LAST:event_createTileCancelButtonActionPerformed

    private void centerPatternHMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_centerPatternHMenuItemActionPerformed
    {//GEN-HEADEREND:event_centerPatternHMenuItemActionPerformed
        controller.centerPatternHorizontal();
        repaint();
        updatePanelInformation();
    }//GEN-LAST:event_centerPatternHMenuItemActionPerformed

    private void centerPatternVMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_centerPatternVMenuItemActionPerformed
    {//GEN-HEADEREND:event_centerPatternVMenuItemActionPerformed
        controller.centerPatternVertical();
        repaint();
        updatePanelInformation();
    }//GEN-LAST:event_centerPatternVMenuItemActionPerformed

    private void startPatternFullTileMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startPatternFullTileMenuItemActionPerformed
        controller.startPatternOnFullTile();
        updatePanelInformation();
        repaint();
    }//GEN-LAST:event_startPatternFullTileMenuItemActionPerformed

    private void startColumnFullTileMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startColumnFullTileMenuItemActionPerformed
        controller.startPatternOnFullColumn();
        updatePanelInformation();
        repaint();
    }//GEN-LAST:event_startColumnFullTileMenuItemActionPerformed

    private void startRowFullTileMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startRowFullTileMenuItemActionPerformed
        controller.startPatternOnFullRow();
        updatePanelInformation();
        repaint();
    }//GEN-LAST:event_startRowFullTileMenuItemActionPerformed

    private void relativeMoveMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_relativeMoveMenuItemActionPerformed
    {//GEN-HEADEREND:event_relativeMoveMenuItemActionPerformed
        contextMode = ContextMenuModes.RELATIVE_MOVE;
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_relativeMoveMenuItemActionPerformed

    private void menuResetZoomActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_menuResetZoomActionPerformed
    {//GEN-HEADEREND:event_menuResetZoomActionPerformed
        canvasPanel.setZoom(1);
        zoomLabel.setText("100");
        horizontalScrollBar.setValue(0);
        verticalScrollBar.setValue(0);
        canvasPanel.repaint();
    }//GEN-LAST:event_menuResetZoomActionPerformed

    private void menuCustomZoomActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_menuCustomZoomActionPerformed
    {//GEN-HEADEREND:event_menuCustomZoomActionPerformed
        double newZoom = 1;
        JFrame frameCustomZoom = new JFrame();
        String newZoomStr = JOptionPane.showInputDialog(frameCustomZoom, "Entrez le % de zoom désiré, un entier "
                                                        + "ou nombre décimal", "Zoom personnalisé", JOptionPane.OK_CANCEL_OPTION);
        if (newZoomStr != null && !newZoomStr.isBlank())
        {
            try
            {
                Double newZoomDbl = Utilities.parseDoubleLocale(newZoomStr);
                if (newZoomDbl.isNaN() || newZoomDbl.isInfinite() || newZoomDbl < 0)
                {
                    throw new ParseException("Nombre invalide", 0);
                }
                newZoom = canvasPanel.setZoom(newZoomDbl/100);
            }
            catch(java.text.ParseException e)
            {
                return;
            }
        }
        else
        {
            return;
        }

        if (newZoom * 100 > 5)
        {
            zoomLabel.setText(String.valueOf((int) (newZoom * 100)));
        }
        else
        {
            zoomLabel.setText(String.format("%.03f", newZoom * 100));
        }
        canvasPanel.repaint();
    }//GEN-LAST:event_menuCustomZoomActionPerformed

    private void createTileButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_createTileButtonActionPerformed
    {//GEN-HEADEREND:event_createTileButtonActionPerformed
        createTileWindow.setVisible(true);
    }//GEN-LAST:event_createTileButtonActionPerformed

    private void rowOffsetFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_rowOffsetFieldActionPerformed
    {//GEN-HEADEREND:event_rowOffsetFieldActionPerformed
        int offset;
        try
        {
            offset = Integer.parseInt(rowOffsetField.getText());
        }
        catch (NumberFormatException e)
        {
            offset = controller.getRowOffset();
        }
        controller.setRowOffset(Math.max(0, Math.min(100, offset)));
        rowOffsetField.setText("" + controller.getRowOffset());
        canvasPanel.repaint();
    }//GEN-LAST:event_rowOffsetFieldActionPerformed

    private void tileColorButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_tileColorButtonActionPerformed
    {//GEN-HEADEREND:event_tileColorButtonActionPerformed
        Color c = JColorChooser.showDialog(null, "Sélectionnez une couleur",
            controller.getTileColor());
        if (c != null)
        {
            tileColorButton.setBackground(c);
            controller.setTileColor(c);
            canvasPanel.repaint();
        }
    }//GEN-LAST:event_tileColorButtonActionPerformed

//GEN-FIRST:event_tileNbPerBoxFieldActionPerformed
 
//GEN-LAST:event_tileNbPerBoxFieldActionPerformed

//GEN-FIRST:event_tileNameFieldActionPerformed
 
//GEN-LAST:event_tileNameFieldActionPerformed

//GEN-FIRST:event_tileHeightFieldActionPerformed
 
//GEN-LAST:event_tileHeightFieldActionPerformed

    private void tileWidthFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_tileWidthFieldActionPerformed
    {//GEN-HEADEREND:event_tileWidthFieldActionPerformed
        if (isMetric)
        {
            try
            {
                double newTileWidth = Utilities.parseDoubleLocale(tileWidthField.getText()) * 10;
                if (newTileWidth < 20)
                {
                    JOptionPane.showMessageDialog(this, "Une tuile doit avoir au moins 2 cm ou 1 pouce de largeur",
                        "Opération interdite", JOptionPane.ERROR_MESSAGE);
                    tileWidthField.setText(String.format("%.03f", controller.getTileWidth() / 10));
                }
                else
                {
                    controller.setTileWidth(newTileWidth);
                    tileWidthField.setText(String.format("%.03f", controller.getTileWidth() / 10));
                }
            }
            catch (ParseException e)
            {
                tileWidthField.setText(String.format("%.03f", controller.getTileWidth() / 10));
            }
        }
        else
        {
            try
            {
                double dblInchesField = Utilities.getInchesFromField(tileWidthField.getText());
                double newTileWidth = Utilities.inchesToMm(dblInchesField);
                if(newTileWidth < 20)
                {
                    JOptionPane.showMessageDialog(this, "Une tuile doit avoir au moins 2 cm ou 1 pouce de largeur",
                        "Opération interdite", JOptionPane.ERROR_MESSAGE);
                    tileWidthField.setText(String.format("%.03f", Utilities.mmToInches(controller.getTileWidth())));
                }
                else
                {
                    controller.setTileWidth(newTileWidth);
                    tileWidthField.setText(String.format("%.03f", Utilities.mmToInches(controller.getTileWidth())));
                }
            }
            catch (ParseException e)
            {
                tileWidthField.setText(String.format("%.03f", Utilities.mmToInches(controller.getTileWidth())));
            } // fin else imperial
        } // fin if isMetric
        controller.refreshSurfaces();
        canvasPanel.repaint();
    }//GEN-LAST:event_tileWidthFieldActionPerformed

    private void tileTypeComboBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_tileTypeComboBoxActionPerformed
    {//GEN-HEADEREND:event_tileTypeComboBoxActionPerformed
        try
        {
            controller.setTileTypeByIndex(tileTypeComboBox.getSelectedIndex());
            updatePanelInformation();
            canvasPanel.repaint();
        } catch (Exception e)
        {
        }
    }//GEN-LAST:event_tileTypeComboBoxActionPerformed

    private void offsetYFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_offsetYFieldActionPerformed
    {//GEN-HEADEREND:event_offsetYFieldActionPerformed
        if (isMetric)
        {
            try
            {
                double offset = Utilities.parseDoubleLocale(offsetYField.getText()) * 10;
                controller.setOffsetY(offset);
                offsetYField.setText(String.format("%.03f", controller.getOffsetY() / 10));
                canvasPanel.repaint();
            }
            catch (ParseException e)
            {
                offsetYField.setText(String.format("%.03f", controller.getOffsetY() / 10));
            }
        }
        else
        {
            try
            {
                double dblInchesField = Utilities.getInchesFromField(offsetYField.getText());
                double offsetY = Utilities.inchesToMm(dblInchesField);
                controller.setOffsetY(offsetY);
                offsetYField.setText(String.format("%.03f", Utilities.mmToInches(controller.getOffsetY())));
                canvasPanel.repaint();
            }
            catch (ParseException e)
            {
                offsetYField.setText(String.format("%.03f", Utilities.mmToInches(controller.getOffsetY())));
            }
        }
    }//GEN-LAST:event_offsetYFieldActionPerformed

    private void offsetXFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_offsetXFieldActionPerformed
    {//GEN-HEADEREND:event_offsetXFieldActionPerformed
        if (isMetric)
        {
            try
            {
                double offset = Utilities.parseDoubleLocale(offsetXField.getText()) * 10;
                controller.setOffsetX(offset);
                offsetXField.setText(String.format("%.03f", controller.getOffsetX() / 10));
                canvasPanel.repaint();
            } catch (ParseException e)
            {
                offsetXField.setText(String.format("%.03f", controller.getOffsetX() / 10));
            }
        } else
        {
            try
            {
                double dblInchesField = Utilities.getInchesFromField(offsetXField.getText());
                double offsetX = Utilities.inchesToMm(dblInchesField);
                controller.setOffsetX(offsetX);
                offsetXField.setText(String.format("%.03f", Utilities.mmToInches(controller.getOffsetX())));
                canvasPanel.repaint();
            } catch (ParseException e)
            {
                offsetXField.setText(String.format("%.03f", Utilities.mmToInches(controller.getOffsetX())));
            }
        }
    }//GEN-LAST:event_offsetXFieldActionPerformed

    private void ninetyOrientationRadioButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_ninetyOrientationRadioButtonActionPerformed
    {//GEN-HEADEREND:event_ninetyOrientationRadioButtonActionPerformed
        controller.setIsNinetyDegree(true);
        canvasPanel.repaint();
    }//GEN-LAST:event_ninetyOrientationRadioButtonActionPerformed

    private void zeroOrientationRadioButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_zeroOrientationRadioButtonActionPerformed
    {//GEN-HEADEREND:event_zeroOrientationRadioButtonActionPerformed
        controller.setIsNinetyDegree(false);
        canvasPanel.repaint();
    }//GEN-LAST:event_zeroOrientationRadioButtonActionPerformed

    private void patternComboBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_patternComboBoxActionPerformed
    {//GEN-HEADEREND:event_patternComboBoxActionPerformed
        if ((String) patternComboBox.getSelectedItem() != null)
        {
            Pattern pattern;
            String message = "Ce motif peut seulement être appliqué si la longueur du "
                                + "plus long côté du matériau est égale au double de la longeur de "
                                + "son plus court côté plus la largeur du joint.";
            switch ((String) patternComboBox.getSelectedItem())
            {
                case patternA:
                    pattern = Pattern.CHECKERED;
                    break;
                case patternB:
                    pattern = Pattern.LSHAPE;
                    if (!hasTwoToOneRatio())
                    {
                        JOptionPane.showMessageDialog(this, message,
                                "Motif incompatible avec tuile", JOptionPane.ERROR_MESSAGE);
                        updatePatternComboBox();
                        return;
                    }
                    break;
                case patternC:
                    pattern = Pattern.TWOBYTWO;
                    if (!hasTwoToOneRatio())
                    {
                        JOptionPane.showMessageDialog(this, message,
                                "Motif incompatible avec tuile", JOptionPane.ERROR_MESSAGE);
                        updatePatternComboBox();
                        return;
                    }
                    break;
                default:
                    pattern = Pattern.DIAGONAL;
            }
            controller.setPattern(pattern);
            enablePanelButtons();
            canvasPanel.repaint();
        }
    }//GEN-LAST:event_patternComboBoxActionPerformed
    
    private boolean hasTwoToOneRatio()
    {
        if (controller.hasTileType())
        {
            double tileWidth = controller.getTileWidth();
            double tileHeight = controller.getTileHeight();
            double jointWidth = controller.getJointWidth();
            double longLength = tileWidth > tileHeight ? tileWidth : tileHeight;
            double shortLength = tileWidth > tileHeight ? tileHeight : tileWidth;
            return longLength == 2 * shortLength + jointWidth;
        }
        else
        {
            return false;
        }
    }
    
    private void jointWidthFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jointWidthFieldActionPerformed
    {//GEN-HEADEREND:event_jointWidthFieldActionPerformed
        if (isMetric)
        {
            try
            {
                double width = Utilities.parseDoubleLocale(jointWidthField.getText());
                controller.setJointWidth(width);
                jointWidthField.setText(String.format("%.03f", controller.getJointWidth()));
                canvasPanel.repaint();
            } catch (ParseException e)
            {
                jointWidthField.setText(String.format("%.03f", controller.getJointWidth()));
            }
        } else
        {
            try
            {
                double dblInchesField = Utilities.getInchesFromField(jointWidthField.getText());

                double jointWidth = Utilities.inchesToMm(dblInchesField);
                controller.setJointWidth(jointWidth);
                jointWidthField.setText(String.format("%.03f",
                    Utilities.mmToInches(controller.getJointWidth())));
            canvasPanel.repaint();
        } catch (ParseException e)
        {
            jointWidthField.setText(String.format("%.03f",
                Utilities.mmToInches(controller.getJointWidth())));
        }
        }
    }//GEN-LAST:event_jointWidthFieldActionPerformed

    private void jointColorButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jointColorButtonActionPerformed
    {//GEN-HEADEREND:event_jointColorButtonActionPerformed
        Color c = JColorChooser.showDialog(null, "Sélectionnez une couleur",
            controller.getJointColor());
        if (c != null)
        {
            jointColorButton.setBackground(c);
            controller.setJointColor(c);
            canvasPanel.repaint();
        }
    }//GEN-LAST:event_jointColorButtonActionPerformed

    private void surfaceXFieldInchesActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_surfaceXFieldInchesActionPerformed
    {//GEN-HEADEREND:event_surfaceXFieldInchesActionPerformed
        surfaceXFieldActionPerformed(evt);
    }//GEN-LAST:event_surfaceXFieldInchesActionPerformed

    private void surfaceYFieldInchesActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_surfaceYFieldInchesActionPerformed
    {//GEN-HEADEREND:event_surfaceYFieldInchesActionPerformed
        surfaceYFieldActionPerformed(evt);
    }//GEN-LAST:event_surfaceYFieldInchesActionPerformed

    private void surfaceWidthFieldInchesActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_surfaceWidthFieldInchesActionPerformed
    {//GEN-HEADEREND:event_surfaceWidthFieldInchesActionPerformed
        surfaceWidthFieldActionPerformed(evt);
    }//GEN-LAST:event_surfaceWidthFieldInchesActionPerformed

    private void surfaceHeightFieldInchesActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_surfaceHeightFieldInchesActionPerformed
    {//GEN-HEADEREND:event_surfaceHeightFieldInchesActionPerformed
        surfaceHeightFieldActionPerformed(evt);
    }//GEN-LAST:event_surfaceHeightFieldInchesActionPerformed

    private void surfaceHeightFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_surfaceHeightFieldActionPerformed
    {//GEN-HEADEREND:event_surfaceHeightFieldActionPerformed
        if (isMetric)
        {
            try
            {
                double height = Utilities.parseDoubleLocale(surfaceHeightField.getText()) * 1000;
                boolean status = controller.setSurfaceHeight(height);
                surfaceHeightField.setText(String.format("%.03f",
                    controller.getBounds2D().getHeight() / 1000.));
            canvasPanel.repaint();
            if (!status)
            {
                JOptionPane.showMessageDialog(this, "Modification illégale.",
                    "Opération interdite", JOptionPane.ERROR_MESSAGE);
            }
        }
        catch (ParseException e)
        {
            surfaceHeightField.setText(String.format("%.03f",
                controller.getBounds2D().getHeight() / 1000.));
        }
        }
        else
        {
            try
            {
                double feet = Utilities.parseDoubleLocale(surfaceHeightField.getText());

                double dblInchesField = Utilities.getInchesFromField(surfaceHeightFieldInches.getText());
                double totalInches = feet * 12 + dblInchesField;
                double mm = Utilities.inchesToMm(totalInches);
                boolean status = controller.setSurfaceHeight(mm);
                double newHeight = controller.getBounds2D().getHeight();
                surfaceHeightField.setText(String.valueOf(Utilities.mmToFeet(newHeight)));
                surfaceHeightFieldInches.setText(String.format("%.02f",
                    Utilities.mmToRemainingInches(newHeight)));
            canvasPanel.repaint();
            if (!status)
            {
                JOptionPane.showMessageDialog(this, "Modification illégale.",
                    "Opération interdite", JOptionPane.ERROR_MESSAGE);
            }
        }
        catch (ParseException e)
        {
            surfaceHeightField.setText(String.valueOf(Utilities.mmToFeet(controller.getBounds2D().getHeight())));
            surfaceHeightFieldInches.setText(String.format("%.02f",
                Utilities.mmToRemainingInches(controller.getBounds2D().getHeight())));
        }
        }
    }//GEN-LAST:event_surfaceHeightFieldActionPerformed

    private void surfaceWidthFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_surfaceWidthFieldActionPerformed
    {//GEN-HEADEREND:event_surfaceWidthFieldActionPerformed
        if (isMetric)
        {
            try
            {
                double width = Utilities.parseDoubleLocale(surfaceWidthField.getText()) * 1000;
                boolean status = controller.setSurfaceWidth(width);
                surfaceWidthField.setText(String.format("%.03f",
                    controller.getBounds2D().getWidth() / 1000.));
            canvasPanel.repaint();
            if (!status)
            {
                JOptionPane.showMessageDialog(this, "Modification illégale.",
                    "Opération interdite", JOptionPane.ERROR_MESSAGE);
            }
        } catch (ParseException e)
        {
            surfaceWidthField.setText(String.format("%.03f",
                controller.getBounds2D().getWidth() / 1000.));
        }
        } else
        {
            try
            {
                double feet = Utilities.parseDoubleLocale(surfaceWidthField.getText());

                double dblInchesField = Utilities.getInchesFromField(surfaceWidthFieldInches.getText());
                double totalInches = feet * 12 + dblInchesField;
                double mm = Utilities.inchesToMm(totalInches);
                boolean status = controller.setSurfaceWidth(mm);
                double newWidth = controller.getBounds2D().getWidth();
                surfaceWidthField.setText(String.valueOf(Utilities.mmToFeet(newWidth)));
                surfaceWidthFieldInches.setText(String.format("%.02f",
                    Utilities.mmToRemainingInches(newWidth)));
            canvasPanel.repaint();
            if (!status)
            {
                JOptionPane.showMessageDialog(this, "Modification illégale.",
                    "Opération interdite", JOptionPane.ERROR_MESSAGE);
            }
        } catch (ParseException e)
        {
            surfaceWidthField.setText(String.valueOf(Utilities.mmToFeet(controller.getBounds2D().getWidth())));
            surfaceWidthFieldInches.setText(String.format("%.02f",
                Utilities.mmToRemainingInches(controller.getBounds2D().getWidth())));
        }
        }
    }//GEN-LAST:event_surfaceWidthFieldActionPerformed

    private void surfaceYFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_surfaceYFieldActionPerformed
    {//GEN-HEADEREND:event_surfaceYFieldActionPerformed
        if (isMetric)
        {
            try
            {
                double y = Utilities.parseDoubleLocale(surfaceYField.getText()) * 1000;
                boolean status = controller.setSurfaceY(y);
                surfaceYField.setText(String.format("%.03f", controller.getBounds2D().getY() / 1000.));
                canvasPanel.repaint();
                if (!status)
                {
                    JOptionPane.showMessageDialog(this, "Modification illégale.",
                        "Opération interdite", JOptionPane.ERROR_MESSAGE);
                }
            }
            catch (ParseException e)
            {
                surfaceYField.setText(String.format("%.03f", controller.getBounds2D().getY() / 1000.));
            }
        }
        else
        {
            try
            {
                double feet = Utilities.parseDoubleLocale(surfaceYField.getText());

                double dblInchesField = Utilities.getInchesFromField(surfaceYFieldInches.getText());
                double totalInches = feet * 12 + dblInchesField;
                double mm = Utilities.inchesToMm(totalInches);
                boolean status = controller.setSurfaceY(mm);
                double newY = controller.getBounds2D().getY();
                surfaceYField.setText(String.valueOf(Utilities.mmToFeet(newY)));
                surfaceYFieldInches.setText(String.format("%.02f",
                    Utilities.mmToRemainingInches(newY)));
            canvasPanel.repaint();

            if (!status)
            {
                JOptionPane.showMessageDialog(this, "Modification illégale.",
                    "Opération interdite", JOptionPane.ERROR_MESSAGE);
            }
        }
        catch (ParseException e)
        {
            surfaceYField.setText(String.valueOf(Utilities.mmToFeet(controller.getBounds2D().getY())));
            surfaceYFieldInches.setText(String.format("%.02f",
                Utilities.mmToRemainingInches(controller.getBounds2D().getY())));
        }
        }
    }//GEN-LAST:event_surfaceYFieldActionPerformed

    private void surfaceXFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_surfaceXFieldActionPerformed
    {//GEN-HEADEREND:event_surfaceXFieldActionPerformed
        if (isMetric)
        {
            try
            {
                double x = Utilities.parseDoubleLocale(surfaceXField.getText()) * 1000;
                boolean status = controller.setSurfaceX(x);
                surfaceXField.setText(String.format("%.03f", controller.getBounds2D().getX() / 1000.));
                canvasPanel.repaint();
                if (!status)
                {
                    JOptionPane.showMessageDialog(this, "Modification illégale.",
                        "Opération interdite", JOptionPane.ERROR_MESSAGE);
                }
            }
            catch (ParseException e)
            {
                surfaceXField.setText(String.format("%.03f", controller.getBounds2D().getX() / 1000.));
            }
        }
        else
        {
            try
            {
                double feet = Utilities.parseDoubleLocale(surfaceXField.getText());

                double dblInchesField = Utilities.getInchesFromField(surfaceXFieldInches.getText());
                double totalInches = feet * 12 + dblInchesField;
                double mm = Utilities.inchesToMm(totalInches);
                boolean status = controller.setSurfaceX(mm);
                double newX = controller.getBounds2D().getX();
                surfaceXField.setText(String.valueOf(Utilities.mmToFeet(newX)));
                surfaceXFieldInches.setText(String.format("%.02f",
                    Utilities.mmToRemainingInches(newX)));
            canvasPanel.repaint();
            if (!status)
            {
                JOptionPane.showMessageDialog(this, "Modification illégale.",
                    "Opération interdite", JOptionPane.ERROR_MESSAGE);
            }
        }
        catch (ParseException e)
        {
            surfaceXField.setText(String.valueOf(Utilities.mmToFeet(controller.getBounds2D().getX())));
            surfaceXFieldInches.setText(String.format("%.02f",
                Utilities.mmToRemainingInches(controller.getBounds2D().getX())));
        }
        }
    }//GEN-LAST:event_surfaceXFieldActionPerformed

    private void doNotCoverRadioButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_doNotCoverRadioButtonActionPerformed
    {//GEN-HEADEREND:event_doNotCoverRadioButtonActionPerformed
        controller.setSurfaceIsHole(true);
        canvasPanel.repaint();
    }//GEN-LAST:event_doNotCoverRadioButtonActionPerformed

    private void coverRadioButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_coverRadioButtonActionPerformed
    {//GEN-HEADEREND:event_coverRadioButtonActionPerformed
        controller.setSurfaceIsHole(false);
        canvasPanel.repaint();
    }//GEN-LAST:event_coverRadioButtonActionPerformed

    private void surfaceColorButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_surfaceColorButtonActionPerformed
    {//GEN-HEADEREND:event_surfaceColorButtonActionPerformed
        Color c = JColorChooser.showDialog(null, "Sélectionnez une couleur",
            controller.getColor());
        if (c != null)
        {
            surfaceColorButton.setBackground(c);
            controller.setSurfaceColor(c);
            canvasPanel.repaint();
        }
    }//GEN-LAST:event_surfaceColorButtonActionPerformed

    private void rotationTextFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_rotationTextFieldActionPerformed
    {//GEN-HEADEREND:event_rotationTextFieldActionPerformed
        controller.setRotation(Integer.parseInt(rotationTextField.getText()));
        canvasPanel.repaint();
    }//GEN-LAST:event_rotationTextFieldActionPerformed
    private void inspectorButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_inspectorButtonActionPerformed
    {//GEN-HEADEREND:event_inspectorButtonActionPerformed
        canvasPanel.toggleIsInspector();
        canvasPanel.repaint();
    }//GEN-LAST:event_inspectorButtonActionPerformed

    private void quantitiesButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_quantitiesButtonActionPerformed
    {//GEN-HEADEREND:event_quantitiesButtonActionPerformed
        String[] tileNames = controller.getTileNames();
        String receipt = "Nombre de types de tuiles:          " + tileNames.length + "\n\n";
        int[] boxCapacities = controller.getBoxCapacities();
        int[] tileQuantities = controller.getTileQuantities();
        for (int i = 0; i < tileNames.length; i++)
        {
            receipt += "Nom de la tuile:                               " + tileNames[i]  + "\n";
            receipt += "Nombre de tuiles par boîte:          " + boxCapacities[i] + "\n";
            receipt += "Nombre de tuiles utilisées:          " + tileQuantities[i] + "\n";
            receipt += "Nombre de boîtes nécessaires: " +
                    (int)Math.ceil((double)tileQuantities[i] / (double)boxCapacities[i]) + "\n\n";
        }
        String message = receipt + "Enregistré sous VirtuTuile/FACTURE_VirtuTuile.txt";
        writeReceiptToFile(receipt.trim().replaceAll(" +", " "));
        JOptionPane.showMessageDialog(this, message,
                    "Calcul des quantités de tuiles", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_quantitiesButtonActionPerformed

    private void writeReceiptToFile(String receipt)
    {
        File file = new File("FACTURE_VirtuTuile.txt");
        FileWriter fr = null;
        try
        {
            fr = new FileWriter(file);
            fr.write(receipt);
        }
        catch (IOException e)
        {
            e.printStackTrace(System.out);
        }
        finally
        {
            try
            {
                if (fr != null)
                {
                    fr.close();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace(System.out);
            }
        }
    }
    
    private void inspectorOKButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_inspectorOKButtonActionPerformed
    {//GEN-HEADEREND:event_inspectorOKButtonActionPerformed
        canvasPanel.setInspectorLength(inspectorSlider.getValue());
        canvasPanel.repaint();
        inspectorDialog.dispose();
    }//GEN-LAST:event_inspectorOKButtonActionPerformed

    private void inspectorMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_inspectorMenuItemActionPerformed
    {//GEN-HEADEREND:event_inspectorMenuItemActionPerformed
        JDialog dialog = inspectorDialog;
        dialog.setSize(dialog.getPreferredSize());
        dialog.setModal(true);
        dialog.setVisible(true);
    }//GEN-LAST:event_inspectorMenuItemActionPerformed

    private void inspectorSliderStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_inspectorSliderStateChanged
    {//GEN-HEADEREND:event_inspectorSliderStateChanged
        inspectorLabel.setText(inspectorSlider.getValue() + " mm");
    }//GEN-LAST:event_inspectorSliderStateChanged

    private void collisionCheckToggleButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_collisionCheckToggleButtonActionPerformed
    {//GEN-HEADEREND:event_collisionCheckToggleButtonActionPerformed
        controller.toggleCollisionCheck();
    }//GEN-LAST:event_collisionCheckToggleButtonActionPerformed

    private void exportMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_exportMenuItemActionPerformed
    {//GEN-HEADEREND:event_exportMenuItemActionPerformed
        BufferedImage bImg = new BufferedImage(canvasPanel.getWidth(), canvasPanel.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D cg = bImg.createGraphics();
        canvasPanel.paintAll(cg);
        try
        {
                if (ImageIO.write(bImg, "png", new File("RENDU_VirtuTuile.png")))
                {
                    JOptionPane.showMessageDialog(this, "Sauvegardé sous VirtuTuile/RENDU_VirtuTuile.png",
                        "Sauvegardé", JOptionPane.INFORMATION_MESSAGE);
                }
        }
        catch (IOException e)
        {
                e.printStackTrace(System.out);
        }
    }//GEN-LAST:event_exportMenuItemActionPerformed

    private void decombineMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_decombineMenuItemActionPerformed
    {//GEN-HEADEREND:event_decombineMenuItemActionPerformed
        controller.unmergeSurface();
        canvasPanel.repaint();
    }//GEN-LAST:event_decombineMenuItemActionPerformed

    private void ellipseToggleButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_ellipseToggleButtonActionPerformed
    {//GEN-HEADEREND:event_ellipseToggleButtonActionPerformed
        selectedMode = ApplicationModes.CIRCLE;
        unselect();
    }//GEN-LAST:event_ellipseToggleButtonActionPerformed

    private void moveRelatively(Point2D.Double point) 
    {
        Rectangle2D bounds = controller.getBounds2DByPoint(point);
        Rectangle2D boundsSelected = controller.getBounds2D();

        if (bounds != null && boundsSelected != null) 
        {
            if (bounds.equals(boundsSelected))
            {
                JOptionPane.showMessageDialog(this, "Sélection de la même surface.",
                        "Opération interdite", JOptionPane.ERROR_MESSAGE);
            }
            else
            {
                String strMeasureUnit;
                if (isMetric)
                {
                    strMeasureUnit = "(en mètres): ";
                }
                else
                {
                    strMeasureUnit = "(en pouces): ";
                }

                // Prendre input utilisateur
                JTextField xField = new JTextField(5);
                JTextField yField = new JTextField(5);

                JPanel relativePosBox = new JPanel();

                relativePosBox.setLayout(new BoxLayout(relativePosBox, BoxLayout.PAGE_AXIS));
                relativePosBox.add(new JLabel("Position horizontale " + strMeasureUnit));
                relativePosBox.add(xField);

                relativePosBox.add(new JLabel("Position verticale " + strMeasureUnit));
                relativePosBox.add(yField);

                int result = JOptionPane.showConfirmDialog(null, relativePosBox,
                        "Entrez la position de la surface à déplacer, par rapport à la surface "
                        + "de référence dont le coin haut-gauche est de (0,0)",
                        JOptionPane.OK_CANCEL_OPTION);

                if (result == JOptionPane.OK_OPTION)
                {
                    double requestedXPos;
                    double requestedYPos;
                    // Créer le point à partir de l'input
                    if (isMetric)
                    {
                        try 
                        {
                            requestedXPos = Utilities.parseDoubleLocale(xField.getText()) * 1000;
                            requestedYPos = Utilities.parseDoubleLocale(yField.getText()) * 1000;
                        }
                        catch(ParseException e)
                        {
                            JOptionPane.showMessageDialog(this, "Coordonnées entrées invalides",
                                    "Opération interdite", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                    else
                    {
                        try 
                        {
                            requestedXPos = Utilities.getInchesFromField(xField.getText());
                            requestedYPos = Utilities.getInchesFromField(yField.getText());
                            requestedXPos = Utilities.inchesToMm(requestedXPos);
                            requestedYPos = Utilities.inchesToMm(requestedYPos);
                        }
                        catch(ParseException e)
                        {
                            JOptionPane.showMessageDialog(this, "Coordonnées entrées invalides",
                                    "Opération interdite", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                    double relativeXPos = bounds.getX() + requestedXPos;
                    double relativeYPos = bounds.getY() + requestedYPos;

                    Point2D.Double newPoint = new Point2D.Double(relativeXPos, relativeYPos);
                    Point2D.Double oldPoint = new Point2D.Double(boundsSelected.getX(), boundsSelected.getY());

                    controller.moveSurfaceToPoint(newPoint);

                    if (controller.getBounds2D().getX() == oldPoint.getX() && 
                            controller.getBounds2D().getY() == oldPoint.getY()) 
                    {
                        JOptionPane.showMessageDialog(this, "Déplacement illégal.",
                                "Opération interdite", JOptionPane.ERROR_MESSAGE);
                    }
                    updatePanelInformation();
                    canvasPanel.repaint();
                } 
            }
        }
        else
        {
            JOptionPane.showMessageDialog(this, "Surface sélectionnée invalide",
                    "Opération interdite", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[])
    {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try
        {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels())
            {
                if ("Nimbus".equals(info.getName()))
                {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex)
        {
            java.util.logging.Logger.getLogger(MainWindow

.class 



.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() ->
        {
            new MainWindow().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem alignBottomMenuItem;
    private javax.swing.JMenuItem alignLeftMenuItem;
    private javax.swing.JMenuItem alignRightMenuItem;
    private javax.swing.JMenu alignSubMenu;
    private javax.swing.JMenuItem alignTopMenuItem;
    private VirtuTuile.GUI.CanvasPanel canvasPanel;
    private javax.swing.JMenuItem centerHMenuItem;
    private javax.swing.JMenuItem centerPatternHMenuItem;
    private javax.swing.JMenu centerPatternSubMenu;
    private javax.swing.JMenuItem centerPatternVMenuItem;
    private javax.swing.JMenu centerSubMenu;
    private javax.swing.JMenuItem centerVMenuItem;
    private javax.swing.JMenuItem closeProjectButton;
    private javax.swing.JToggleButton collisionCheckToggleButton;
    private javax.swing.ButtonGroup coverButtonGroup;
    private javax.swing.JRadioButton coverRadioButton;
    private javax.swing.JButton createTileButton;
    private javax.swing.JButton createTileCancelButton;
    private javax.swing.JButton createTileColorButton;
    private javax.swing.JTextField createTileHeightField;
    private javax.swing.JTextField createTileNameField;
    private javax.swing.JTextField createTileNbPerBoxField;
    private javax.swing.JButton createTileOKButton;
    private javax.swing.JTextField createTileWidthField;
    private javax.swing.JPanel createTileWindow;
    private javax.swing.JLabel createWindowHeightLabel;
    private javax.swing.JLabel createWindowWidthLabel;
    private javax.swing.JToggleButton debugToggleButton;
    private javax.swing.JMenuItem decombineMenuItem;
    private javax.swing.JMenuItem deleteSurfaceMenuItem;
    private javax.swing.JRadioButton doNotCoverRadioButton;
    private javax.swing.JToggleButton ellipseToggleButton;
    private javax.swing.JMenuItem exportMenuItem;
    private javax.swing.JDialog gridDistanceDialog;
    private javax.swing.JLabel gridDistanceLabel;
    private javax.swing.JButton gridDistanceOKButton;
    private javax.swing.JSlider gridDistanceSlider;
    private javax.swing.JLabel gridDistanceTopLabel;
    private javax.swing.JLabel hauteurTuileText;
    private javax.swing.JLabel heightFtLabel;
    private javax.swing.JLabel heightInLabel;
    private javax.swing.JScrollBar horizontalScrollBar;
    private javax.swing.JToggleButton imperialButton;
    private javax.swing.JDialog imperialGridDistanceDialog;
    private javax.swing.JLabel imperialGridDistanceLabel;
    private javax.swing.JButton imperialGridDistanceOKButton;
    private javax.swing.JSlider imperialGridDistanceSlider;
    private javax.swing.JLabel imperialGridDistanceTopLabel;
    private javax.swing.JToggleButton inspectorButton;
    private javax.swing.JDialog inspectorDialog;
    private javax.swing.JLabel inspectorLabel;
    private javax.swing.JMenuItem inspectorMenuItem;
    private javax.swing.JButton inspectorOKButton;
    private javax.swing.JSlider inspectorSlider;
    private javax.swing.JLabel inspectorTopLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JPopupMenu.Separator jSeparator7;
    private javax.swing.JPopupMenu.Separator jSeparator8;
    private javax.swing.JToolBar.Separator jSeparator9;
    private javax.swing.JButton jointColorButton;
    private javax.swing.JTextField jointWidthField;
    private javax.swing.JLabel largeurJointText;
    private javax.swing.JLabel largeurTuileText;
    private javax.swing.JPanel leftPanel;
    private javax.swing.JToggleButton magnetButton;
    private javax.swing.JPanel mainPanel;
    private javax.swing.ButtonGroup measureGroup;
    private javax.swing.JMenu menuAffichage;
    private javax.swing.JMenu menuAide;
    private javax.swing.JMenuItem menuAidePropos;
    private javax.swing.JMenu menuBar;
    private javax.swing.JMenuItem menuCustomZoom;
    private javax.swing.JMenu menuEdition;
    private javax.swing.JMenuItem menuEditionAnnuler;
    private javax.swing.JMenuItem menuEditionRepeter;
    private javax.swing.JMenu menuFichier;
    private javax.swing.JMenuItem menuFichierQuitter;
    private javax.swing.JMenuItem menuGridDistance;
    private javax.swing.JMenuItem menuResetZoom;
    private javax.swing.JToggleButton mergeToggle;
    private javax.swing.JToggleButton metricButton;
    private javax.swing.JLabel nbBoxesOnSurfaceLabel;
    private javax.swing.JLabel nbTilesOnSurfaceLabels;
    private javax.swing.JPanel newPanel;
    private javax.swing.JMenuItem newProjectButton;
    private javax.swing.JRadioButton ninetyOrientationRadioButton;
    private javax.swing.JTextField offsetXField;
    private javax.swing.JLabel offsetXText;
    private javax.swing.JTextField offsetYField;
    private javax.swing.JLabel offsetYText;
    private javax.swing.JMenuItem openProjectButton;
    private javax.swing.ButtonGroup orientationGroup;
    private javax.swing.JLabel orientationLabel;
    private javax.swing.JComboBox<String> patternComboBox;
    private javax.swing.JLabel percentLabel;
    private javax.swing.JToggleButton polygonToggle;
    private javax.swing.JMenuItem pushBottomMenuItem;
    private javax.swing.JMenuItem pushLeftJMenuItem;
    private javax.swing.JMenuItem pushRightMenuItem;
    private javax.swing.JMenu pushSubMenu;
    private javax.swing.JMenuItem pushTopMenuItem;
    private javax.swing.JButton quantitiesButton;
    private javax.swing.JToggleButton rectangleToggle;
    private javax.swing.JButton redoButton;
    private javax.swing.JMenuItem relativeMoveMenuItem;
    private javax.swing.JLabel rotationLabel1;
    private javax.swing.JLabel rotationLabel2;
    private javax.swing.JTextField rotationTextField;
    private javax.swing.JTextField rowOffsetField;
    private javax.swing.JLabel rowOffsetLabel1;
    private javax.swing.JLabel rowOffsetLabel2;
    private javax.swing.JMenuItem saveProjectButton;
    private javax.swing.JToggleButton selectionToggle;
    private javax.swing.JMenuItem startColumnFullTileMenuItem;
    private javax.swing.JMenuItem startPatternFullTileMenuItem;
    private javax.swing.JMenu startPatternSubMenu;
    private javax.swing.JMenuItem startRowFullTileMenuItem;
    private javax.swing.JMenuItem stickHMenuItem;
    private javax.swing.JMenu stickSubMenu;
    private javax.swing.JMenuItem stickVMenuItem;
    private javax.swing.JButton surfaceColorButton;
    private javax.swing.JTextField surfaceHeightField;
    private javax.swing.JTextField surfaceHeightFieldInches;
    private javax.swing.JToggleButton surfaceMoveToggle;
    private javax.swing.JPopupMenu surfacePopupMenu;
    private javax.swing.JTextField surfaceWidthField;
    private javax.swing.JTextField surfaceWidthFieldInches;
    private javax.swing.JTextField surfaceXField;
    private javax.swing.JTextField surfaceXFieldInches;
    private javax.swing.JTextField surfaceYField;
    private javax.swing.JTextField surfaceYFieldInches;
    private javax.swing.JButton tileColorButton;
    private javax.swing.JTextField tileHeightField;
    private javax.swing.JLabel tileInfoHeight;
    private javax.swing.JLabel tileInfoWidth;
    private javax.swing.JToggleButton tileMoveToggle;
    private javax.swing.JTextField tileNameField;
    private javax.swing.JLabel tileNameLabel;
    private javax.swing.JTextField tileNbPerBoxField;
    private javax.swing.JLabel tileState;
    private javax.swing.JComboBox<String> tileTypeComboBox;
    private javax.swing.JTextField tileWidthField;
    private javax.swing.ButtonGroup toggleGroup;
    private javax.swing.JToolBar toolBar;
    private javax.swing.JMenuBar topMenuBar;
    private javax.swing.JButton undoButton;
    private javax.swing.JScrollBar verticalScrollBar;
    private javax.swing.JLabel widthFtLabel;
    private javax.swing.JLabel widthInLabel;
    private javax.swing.JLabel xFtLabel;
    private javax.swing.JLabel xInLabel;
    private javax.swing.JLabel xMeasureCoordsLabel;
    private javax.swing.JLabel xPixelCoordsLabel;
    private javax.swing.JLabel yFtLabel;
    private javax.swing.JLabel yInLabel;
    private javax.swing.JLabel yMeasureCoordsLabel;
    private javax.swing.JLabel yPixelCoordsLabel;
    private javax.swing.JRadioButton zeroOrientationRadioButton;
    private javax.swing.JButton zoomInButton;
    private javax.swing.JLabel zoomLabel;
    private javax.swing.JButton zoomOutButton;
    // End of variables declaration//GEN-END:variables
}