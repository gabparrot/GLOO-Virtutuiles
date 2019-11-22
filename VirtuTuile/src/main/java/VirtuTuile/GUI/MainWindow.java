package VirtuTuile.GUI;

import VirtuTuile.Domain.Pattern;
import VirtuTuile.Infrastructure.Utilities;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import javax.swing.BoxLayout;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

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
        SELECT, SURFACEMOVE, RECTANGLE, POLYGON, MERGE, TILEMOVE ,NONE;
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

    // Un rectangle est défini par deux points, son premier point métrique est enregistré ici.
    private Point2D.Double firstRectangleCorner = null;

    // Un point d'origine pour le déplacement de surface. Décrit la différence x et y entre
    // le coin supérieur-gauche de la surface et le clic intérieur de la surface.
    private Point2D.Double originPoint = null;

    // Si le projet fonctionne en mesure métrique ou impérial.
    private boolean isMetric = true;

    // Si la grille est magnétique en ce moment.
    private boolean gridIsMagnetic = true;
    
    // La couleur utilisé pour les boutons désactivés.
    private final Color disabledColor = new Color(240, 240, 240);

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
        tileInfoWidth.setText("Largeur : ");
        tileInfoHeight.setText("Hauteur : ");
    }

    /**
     * Désélectionne la surface sélectionnée.
     */
    private void unselect()
    {
        contextMode = ContextMenuModes.NONE;
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

        firstRectangleCorner = null;
        canvasPanel.setTemporaryRectangle(null);
        controller.unselectSurface();

        disableSurfaceButtons();
        disableCoveringButtons();
        disableTileTypeButtons();
        
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
    }
    
    private void disableTileTypeButtons()
    {
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
        jointColorButton.setBackground(controller.getJointColor());
        DefaultComboBoxModel model = new DefaultComboBoxModel(controller.getTileTypeStrings());
        tileTypeComboBox.setModel(model);
    }
    
    private void updateTileTypePanel()
    {
        if (controller.hasTileType())
        {
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
        zeroOrientationRadioButton.setEnabled(true);
        ninetyOrientationRadioButton.setEnabled(true);
        offsetXField.setEnabled(true);
        offsetYField.setEnabled(true);
        if (controller.getPattern() == Pattern.CHECKERED)
        {
            rowOffsetField.setEnabled(true);
        } else
        {
            rowOffsetField.setEnabled(false);
        }
    }
    
    private void enableTileTypePanelButtons()
    {
        tileNameField.setEnabled(true);
        tileWidthField.setEnabled(true);
        tileHeightField.setEnabled(true);
        tileColorButton.setEnabled(true);
        tileNbPerBoxField.setEnabled(true);
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
        deleteSurfaceMenuItem = new javax.swing.JMenuItem();
        orientationGroup = new javax.swing.ButtonGroup();
        measureGroup = new javax.swing.ButtonGroup();
        toolBar = new javax.swing.JToolBar();
        undoButton = new javax.swing.JButton();
        redoButton = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        selectionToggle = new javax.swing.JToggleButton();
        surfaceMoveToggle = new javax.swing.JToggleButton();
        tileMoveToggle = new javax.swing.JToggleButton();
        rectangleToggle = new javax.swing.JToggleButton();
        polygonToggle = new javax.swing.JToggleButton();
        mergeToggle = new javax.swing.JToggleButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        quantitiesButton = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        magnetButton = new javax.swing.JToggleButton();
        inspectorButton = new javax.swing.JToggleButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
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
        jScrollPane1 = new javax.swing.JScrollPane();
        rightPanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        surfaceColorButton = new javax.swing.JButton();
        coverRadioButton = new javax.swing.JRadioButton();
        doNotCoverRadioButton = new javax.swing.JRadioButton();
        surfaceXField = new javax.swing.JTextField();
        surfaceYField = new javax.swing.JTextField();
        surfaceWidthField = new javax.swing.JTextField();
        surfaceHeightField = new javax.swing.JTextField();
        xFtLabel = new javax.swing.JLabel();
        yFtLabel = new javax.swing.JLabel();
        widthFtLabel = new javax.swing.JLabel();
        heightFtLabel = new javax.swing.JLabel();
        surfaceHeightFieldInches = new javax.swing.JTextField();
        surfaceWidthFieldInches = new javax.swing.JTextField();
        surfaceYFieldInches = new javax.swing.JTextField();
        surfaceXFieldInches = new javax.swing.JTextField();
        xInLabel = new javax.swing.JLabel();
        yInLabel = new javax.swing.JLabel();
        widthInLabel = new javax.swing.JLabel();
        heightInLabel = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jointColorButton = new javax.swing.JButton();
        jointWidthField = new javax.swing.JTextField();
        largeurJointText = new javax.swing.JLabel();
        patternComboBox = new javax.swing.JComboBox<>();
        zeroOrientationRadioButton = new javax.swing.JRadioButton();
        ninetyOrientationRadioButton = new javax.swing.JRadioButton();
        offsetXField = new javax.swing.JTextField();
        offsetXText = new javax.swing.JLabel();
        offsetYText = new javax.swing.JLabel();
        offsetYField = new javax.swing.JTextField();
        tileTypeComboBox = new javax.swing.JComboBox<>();
        tileWidthField = new javax.swing.JTextField();
        tileHeightField = new javax.swing.JTextField();
        largeurTuileText = new javax.swing.JLabel();
        hauteurTuileText = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        tileNameLabel = new javax.swing.JLabel();
        tileNameField = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        tileNbPerBoxField = new javax.swing.JTextField();
        tileColorButton = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();
        rowOffsetField = new javax.swing.JTextField();
        rowOffsetLabel = new javax.swing.JLabel();
        createTileButton = new javax.swing.JButton();
        topMenuBar = new javax.swing.JMenuBar();
        menuFichier = new javax.swing.JMenu();
        newProjectButton = new javax.swing.JMenuItem();
        openProjectButton = new javax.swing.JMenuItem();
        closeProjectButton = new javax.swing.JMenuItem();
        saveProjectButton = new javax.swing.JMenuItem();
        menuFichierQuitter = new javax.swing.JMenuItem();
        menuEdition = new javax.swing.JMenu();
        menuEditionAnnuler = new javax.swing.JMenuItem();
        menuEditionRepeter = new javax.swing.JMenuItem();
        menuAffichage = new javax.swing.JMenu();
        menuGridDistance = new javax.swing.JMenuItem();
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
        gridDistanceSlider.setMinimum(5);
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

        deleteSurfaceMenuItem.setText("Effacer la surface");
        deleteSurfaceMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                deleteSurfaceMenuItemActionPerformed(evt);
            }
        });
        surfacePopupMenu.add(deleteSurfaceMenuItem);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("VirtuTuile");
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
        selectionToggle.setToolTipText("Sélectionner une surface");
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
        toolBar.add(jSeparator1);

        quantitiesButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/quantities.png"))); // NOI18N
        quantitiesButton.setToolTipText("Calculer les quantités");
        quantitiesButton.setFocusable(false);
        quantitiesButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        quantitiesButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        quantitiesButton.setMaximumSize(new java.awt.Dimension(30, 30));
        quantitiesButton.setMinimumSize(new java.awt.Dimension(30, 30));
        quantitiesButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(quantitiesButton);
        toolBar.add(jSeparator2);

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
        inspectorButton.setToolTipText("Détecter les petites tuiles");
        inspectorButton.setFocusable(false);
        inspectorButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        inspectorButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        inspectorButton.setMaximumSize(new java.awt.Dimension(30, 30));
        inspectorButton.setMinimumSize(new java.awt.Dimension(30, 30));
        inspectorButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(inspectorButton);
        toolBar.add(jSeparator3);

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

        jLabel21.setText("Créer une nouvelle tuile");

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

        createTileWidthField.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                createTileWidthFieldActionPerformed(evt);
            }
        });

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
            .addGroup(createTileWindowLayout.createSequentialGroup()
                .addGroup(createTileWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(createTileWindowLayout.createSequentialGroup()
                        .addGap(48, 48, 48)
                        .addGroup(createTileWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel25, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(createTileWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel27)
                                .addGroup(createTileWindowLayout.createSequentialGroup()
                                    .addGroup(createTileWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(createTileWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel23)
                                            .addComponent(jLabel22))
                                        .addComponent(jLabel24))
                                    .addGap(23, 23, 23)
                                    .addGroup(createTileWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(createTileNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(createTileWindowLayout.createSequentialGroup()
                                            .addComponent(createTileWidthField, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(createWindowWidthLabel))
                                        .addGroup(createTileWindowLayout.createSequentialGroup()
                                            .addComponent(createTileHeightField)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(createWindowHeightLabel))
                                        .addComponent(createTileColorButton, javax.swing.GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE)
                                        .addComponent(createTileNbPerBoxField, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                    .addGroup(createTileWindowLayout.createSequentialGroup()
                        .addGap(87, 87, 87)
                        .addComponent(createTileOKButton)
                        .addGap(18, 18, 18)
                        .addComponent(createTileCancelButton)))
                .addContainerGap(52, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, createTileWindowLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel21)
                .addGap(89, 89, 89))
        );

        createTileWindowLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {createTileColorButton, createTileHeightField, createTileNbPerBoxField, createTileWidthField});

        createTileWindowLayout.setVerticalGroup(
            createTileWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(createTileWindowLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
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
                .addContainerGap(165, Short.MAX_VALUE)
                .addComponent(createTileWindow, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(165, Short.MAX_VALUE))
        );
        canvasPanelLayout.setVerticalGroup(
            canvasPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(canvasPanelLayout.createSequentialGroup()
                .addContainerGap(201, Short.MAX_VALUE)
                .addComponent(createTileWindow, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(200, Short.MAX_VALUE))
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

        tileState.setText("Dimensions Tuile : ");
        tileState.setToolTipText("");
        tileState.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        tileInfoWidth.setText("Largeur :");

        tileInfoHeight.setText("Hauteur :");

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
                                .addGap(40, 40, 40)
                                .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(yPixelCoordsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(xPixelCoordsLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                            .addComponent(tileInfoHeight)))
                    .addComponent(tileInfoWidth)
                    .addComponent(tileState, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(5, 5, 5))
        );

        jScrollPane1.setBackground(new java.awt.Color(0, 107, 107));

        rightPanel.setBackground(new java.awt.Color(0, 153, 153));
        rightPanel.setBorder(javax.swing.BorderFactory.createMatteBorder(10, 10, 10, 10, new java.awt.Color(0, 107, 107)));

        jLabel2.setText("x :");

        jLabel3.setText("y :");

        jLabel4.setText("Largeur :");

        jLabel5.setText("Hauteur :");

        jLabel11.setText("Couleur :");

        surfaceColorButton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        surfaceColorButton.setBorderPainted(false);
        surfaceColorButton.setEnabled(false);
        surfaceColorButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                surfaceColorButtonActionPerformed(evt);
            }
        });

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

        surfaceXField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        surfaceXField.setEnabled(false);
        surfaceXField.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                surfaceXFieldActionPerformed(evt);
            }
        });

        surfaceYField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        surfaceYField.setEnabled(false);
        surfaceYField.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                surfaceYFieldActionPerformed(evt);
            }
        });

        surfaceWidthField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        surfaceWidthField.setEnabled(false);
        surfaceWidthField.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                surfaceWidthFieldActionPerformed(evt);
            }
        });

        surfaceHeightField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        surfaceHeightField.setEnabled(false);
        surfaceHeightField.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                surfaceHeightFieldActionPerformed(evt);
            }
        });

        xFtLabel.setText("m");

        yFtLabel.setText("m");

        widthFtLabel.setText("m");

        heightFtLabel.setText("m");

        surfaceHeightFieldInches.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        surfaceHeightFieldInches.setEnabled(false);
        surfaceHeightFieldInches.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                surfaceHeightFieldInchesActionPerformed(evt);
            }
        });

        surfaceWidthFieldInches.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        surfaceWidthFieldInches.setEnabled(false);
        surfaceWidthFieldInches.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                surfaceWidthFieldInchesActionPerformed(evt);
            }
        });

        surfaceYFieldInches.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        surfaceYFieldInches.setEnabled(false);
        surfaceYFieldInches.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                surfaceYFieldInchesActionPerformed(evt);
            }
        });

        surfaceXFieldInches.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        surfaceXFieldInches.setToolTipText("");
        surfaceXFieldInches.setEnabled(false);
        surfaceXFieldInches.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                surfaceXFieldInchesActionPerformed(evt);
            }
        });

        xInLabel.setText("in");

        yInLabel.setText("in");

        widthInLabel.setText("in");

        heightInLabel.setText("in");

        jLabel6.setText("Couvir :");

        jPanel1.setBackground(new java.awt.Color(0, 107, 107));

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel1.setText("Surface :");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5))
        );

        jPanel2.setBackground(new java.awt.Color(0, 107, 107));

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel7.setText("Recouvrement :");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5))
        );

        jPanel3.setBackground(new java.awt.Color(0, 107, 107));

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel8.setText("Type de tuile :");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5))
        );

        jLabel9.setText("Décalage x :");

        jLabel10.setText("Décalage y :");

        jLabel14.setText("Type de tuile :");

        jLabel15.setText("Largeur joints :");

        jLabel16.setText("Couleur joints :");

        jLabel17.setText("Motif :");

        jLabel18.setText("Orientation :");

        jointColorButton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        jointColorButton.setBorderPainted(false);
        jointColorButton.setEnabled(false);
        jointColorButton.setMaximumSize(new java.awt.Dimension(100, 30));
        jointColorButton.setMinimumSize(new java.awt.Dimension(100, 30));
        jointColorButton.setPreferredSize(new java.awt.Dimension(100, 30));
        jointColorButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jointColorButtonActionPerformed(evt);
            }
        });

        jointWidthField.setEnabled(false);
        jointWidthField.setPreferredSize(new java.awt.Dimension(50, 30));
        jointWidthField.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jointWidthFieldActionPerformed(evt);
            }
        });

        largeurJointText.setText("mm");

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

        offsetXField.setEnabled(false);
        offsetXField.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                offsetXFieldActionPerformed(evt);
            }
        });

        offsetXText.setText("cm");

        offsetYText.setText("cm");

        offsetYField.setEnabled(false);
        offsetYField.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                offsetYFieldActionPerformed(evt);
            }
        });

        tileTypeComboBox.setEnabled(false);
        tileTypeComboBox.setMinimumSize(new java.awt.Dimension(200, 22));
        tileTypeComboBox.setPreferredSize(new java.awt.Dimension(200, 30));
        tileTypeComboBox.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                tileTypeComboBoxActionPerformed(evt);
            }
        });

        tileWidthField.setEnabled(false);
        tileWidthField.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                tileWidthFieldActionPerformed(evt);
            }
        });

        tileHeightField.setEnabled(false);
        tileHeightField.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                tileHeightFieldActionPerformed(evt);
            }
        });

        largeurTuileText.setText("cm");

        hauteurTuileText.setText("cm");

        jLabel12.setText("Largeur :");

        jLabel13.setText("Hauteur :");

        jLabel26.setText("Couleur :");

        tileNameLabel.setText("Nom :");

        tileNameField.setEnabled(false);
        tileNameField.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                tileNameFieldActionPerformed(evt);
            }
        });

        jLabel19.setText("Nb/boîte :");

        tileNbPerBoxField.setEnabled(false);
        tileNbPerBoxField.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                tileNbPerBoxFieldActionPerformed(evt);
            }
        });

        tileColorButton.setEnabled(false);
        tileColorButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                tileColorButtonActionPerformed(evt);
            }
        });

        jLabel20.setText("Décalage rangées :");

        rowOffsetField.setEnabled(false);
        rowOffsetField.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                rowOffsetFieldActionPerformed(evt);
            }
        });

        rowOffsetLabel.setText("%");

        createTileButton.setText("Créer nouveau");
        createTileButton.setEnabled(false);
        createTileButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                createTileButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout rightPanelLayout = new javax.swing.GroupLayout(rightPanel);
        rightPanel.setLayout(rightPanelLayout);
        rightPanelLayout.setHorizontalGroup(
            rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(rightPanelLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, rightPanelLayout.createSequentialGroup()
                        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rowOffsetField, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(offsetYText)
                            .addComponent(rowOffsetLabel)
                            .addComponent(offsetXText)))
                    .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, rightPanelLayout.createSequentialGroup()
                            .addComponent(jLabel15)
                            .addGap(5, 5, 5)
                            .addComponent(jointWidthField, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(largeurJointText, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(rightPanelLayout.createSequentialGroup()
                            .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel16)
                                .addComponent(jLabel17)
                                .addComponent(jLabel18))
                            .addGap(5, 5, 5)
                            .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(rightPanelLayout.createSequentialGroup()
                                    .addGap(1, 1, 1)
                                    .addComponent(zeroOrientationRadioButton)
                                    .addGap(18, 18, 18)
                                    .addComponent(ninetyOrientationRadioButton))
                                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(patternComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jointColorButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))))
            .addGroup(rightPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12)
                    .addComponent(jLabel13)
                    .addComponent(tileNameLabel))
                .addGap(13, 13, 13)
                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(rightPanelLayout.createSequentialGroup()
                        .addComponent(tileHeightField, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(hauteurTuileText))
                    .addGroup(rightPanelLayout.createSequentialGroup()
                        .addComponent(tileWidthField, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(largeurTuileText))
                    .addComponent(tileNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addGroup(rightPanelLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel14)
                    .addGroup(rightPanelLayout.createSequentialGroup()
                        .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel5)
                            .addComponent(jLabel11)
                            .addComponent(jLabel4)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(rightPanelLayout.createSequentialGroup()
                                .addComponent(coverRadioButton)
                                .addGap(30, 30, 30)
                                .addComponent(doNotCoverRadioButton))
                            .addGroup(rightPanelLayout.createSequentialGroup()
                                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(surfaceHeightField, javax.swing.GroupLayout.DEFAULT_SIZE, 61, Short.MAX_VALUE)
                                    .addComponent(surfaceWidthField)
                                    .addComponent(surfaceXField, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(surfaceYField, javax.swing.GroupLayout.Alignment.LEADING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(rightPanelLayout.createSequentialGroup()
                                        .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(yFtLabel)
                                            .addComponent(xFtLabel))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(rightPanelLayout.createSequentialGroup()
                                                .addComponent(surfaceXFieldInches, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(xInLabel))
                                            .addGroup(rightPanelLayout.createSequentialGroup()
                                                .addComponent(surfaceYFieldInches, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(yInLabel))))
                                    .addGroup(rightPanelLayout.createSequentialGroup()
                                        .addComponent(widthFtLabel)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(surfaceWidthFieldInches, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(widthInLabel))
                                    .addGroup(rightPanelLayout.createSequentialGroup()
                                        .addComponent(heightFtLabel)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(surfaceHeightFieldInches, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(heightInLabel))))
                            .addComponent(surfaceColorButton, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(rightPanelLayout.createSequentialGroup()
                        .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(offsetXField, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(offsetYField, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)))))
            .addGroup(rightPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel19)
                    .addComponent(jLabel26))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tileNbPerBoxField, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tileColorButton, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(createTileButton)))
            .addGroup(rightPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tileTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        rightPanelLayout.setVerticalGroup(
            rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rightPanelLayout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel2)
                    .addComponent(surfaceXField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(xFtLabel)
                    .addComponent(surfaceXFieldInches, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(xInLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel3)
                    .addComponent(surfaceYField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(yFtLabel)
                    .addComponent(surfaceYFieldInches, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(yInLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel4)
                    .addComponent(surfaceWidthField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(widthFtLabel)
                    .addComponent(surfaceWidthFieldInches, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(widthInLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel5)
                    .addComponent(surfaceHeightField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(heightFtLabel)
                    .addComponent(surfaceHeightFieldInches, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(heightInLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(surfaceColorButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(coverRadioButton)
                    .addComponent(doNotCoverRadioButton)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tileTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(jointWidthField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(largeurJointText, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(rightPanelLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jLabel16))
                    .addGroup(rightPanelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jointColorButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(patternComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(zeroOrientationRadioButton)
                    .addComponent(ninetyOrientationRadioButton)
                    .addComponent(jLabel18))
                .addGap(11, 11, 11)
                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(offsetXField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(offsetXText)))
                .addGap(5, 5, 5)
                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(offsetYText)
                        .addComponent(offsetYField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(5, 5, 5)
                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(rowOffsetField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(rowOffsetLabel))
                    .addComponent(jLabel20))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tileNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tileNameLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tileWidthField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(largeurTuileText)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tileHeightField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(hauteurTuileText)
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tileColorButton, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel26))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(tileNbPerBoxField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(createTileButton)
                .addGap(5, 5, 5))
        );

        jScrollPane1.setViewportView(rightPanel);

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addComponent(leftPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 692, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(leftPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 780, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 780, Short.MAX_VALUE)
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
    
    /**
     * Mets à jour les coordonnées de la souris sur le canevas.
     *
     * @param evt : position de la souris
     */
    
    private void canvasPanelMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_canvasPanelMouseMoved
        updateMouseCoordinates(evt);
        Point2D.Double metricPoint = pointToMetric(evt.getPoint());
        if (controller.projectExists())
        {
            showTileInfo(controller.getTileAtPoint(metricPoint));
        }
        // Dessine un rectangle temporaire lors de la création d'un rectangle.
        if (selectedMode == ApplicationModes.RECTANGLE && firstRectangleCorner != null)
        {
            if (gridIsMagnetic)
            {
                Utilities.movePointToGrid(metricPoint, canvasPanel.getGridDistance());
            }
            canvasPanel.setTemporaryRectangle(Utilities.cornersToRectangle(firstRectangleCorner,
                    metricPoint));
            canvasPanel.repaint();
        }
    }//GEN-LAST:event_canvasPanelMouseMoved

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
    
    /**
     * Décrémente le zoom.
     *
     * @param evt
     */
    private void zoomOutButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zoomOutButtonActionPerformed
        double newZoom = canvasPanel.zoomOutIncrement();
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
    }//GEN-LAST:event_zoomOutButtonActionPerformed

    /**
     * Incrémente le zoom.
     *
     * @param evt
     */
    private void zoomInButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zoomInButtonActionPerformed
        double newZoom = canvasPanel.zoomInIncrement();
        
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
    }//GEN-LAST:event_zoomInButtonActionPerformed

    private void menuFichierQuitterActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_menuFichierQuitterActionPerformed
    {//GEN-HEADEREND:event_menuFichierQuitterActionPerformed
        System.exit(0);
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
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
            {
                controller.saveProject(fileChooser.getSelectedFile());
            }
        }
    }//GEN-LAST:event_saveProjectButtonActionPerformed

    /**
     * Déplace le zoom par la roue de la souris.
     *
     * @param evt
     */
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

    /**
     * Exécute une opération dépendant du mode courant de l'application.
     *
     * @param evt
     */
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
            // Fusionner deux surfaces
            case MERGE:
                mergeSelectedSurfaces(pointToMetric(evt.getPoint()));
                break;

            // Change la sélection de surface.
            case SELECT:
                selectSurface(pointToMetric(evt.getPoint()));
                break;

            // Démarche pour créer un nouveau rectangle.
            case RECTANGLE:
                createNewRectangularSurface(pointToMetric(evt.getPoint()));
                break;

            // Set le point d'origine du mouvement de la souris pour déplacer une surface.
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
        // Annule la création en cours d'une surface rectangulaire.
        if (this.selectedMode == ApplicationModes.RECTANGLE)
        {
            unselect();
        }
        selectSurface(pointToMetric(evt.getPoint()));
        if (!controller.surfaceIsSelected()) return;
        // Display le context menu
        if (controller.surfaceIsRectangular())
        {
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
        else
        {
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

    /**
     * Combine deux surfaces.
     *
     * @param point Endroit où le clic est reçu dans le canevas
     */
    private void mergeSelectedSurfaces(Point2D.Double point)
    {
        selectSurface(point);
        if (controller.surfaceIsSelected())
        {
            if (!controller.mergeIsInProgress())
            {
                controller.setFirstSurfaceToMerge();
            } else
            {
                boolean flag = controller.mergeSurfaces();
                if (flag)
                {
                    selectSurface(point);
                } else
                {
                    unselect();
                    JOptionPane.showMessageDialog(this,
                            "La combinaison ne peut pas être effectuée.",
                            "Opération interdite", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else
        {
            unselect();
        }
    }

    /**
     * Sélectionne une surface.
     *
     * @param point : le point que la surface doit contenir, en mesure métrique.
     */
    private void selectSurface(Point2D.Double point)
    {
        controller.selectSurface(point);
        if (controller.surfaceIsSelected())
        {
            enablePanelButtons();
            updatePanelInformation();
            canvasPanel.repaint();
        } else
        {
            unselect();
        }
    }

    /**
     * Crée une nouvelle surface rectangulaire.
     *
     * @param point : un des deux coins du rectangle, en mesure métrique.
     */
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

            // Sélectionne le premier coin du nouveau rectangle.
            if (firstRectangleCorner == null)
            {
                unselect();
                firstRectangleCorner = point;
            } // Sélectionne le deuxième coin du nouveau rectangle.
            else
            {
                Rectangle2D.Double rectangle = Utilities.cornersToRectangle(firstRectangleCorner, point);
                unselect();

                if (rectangle.width < 100 || rectangle.height < 100)
                {
                    canvasPanel.repaint();
                    JOptionPane.showMessageDialog(this, "Surface trop petite.",
                            "Opération interdite", JOptionPane.ERROR_MESSAGE);
                } else
                {
                    // Fait une requête pour la création du rectangle.
                    boolean status = controller.addRectangularSurface(rectangle);

                    if (status)
                    {
                        selectSurface(new Point2D.Double(rectangle.x + 0.1, rectangle.y + 0.1));
                    } else
                    {
                        canvasPanel.repaint();
                        JOptionPane.showMessageDialog(this, "Création de la surface entraîne un conflit.",
                                "Opération interdite", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
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

    private void coverRadioButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_coverRadioButtonActionPerformed
    {//GEN-HEADEREND:event_coverRadioButtonActionPerformed
        controller.setSurfaceIsHole(false);
        canvasPanel.repaint();
    }//GEN-LAST:event_coverRadioButtonActionPerformed

    private void doNotCoverRadioButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_doNotCoverRadioButtonActionPerformed
    {//GEN-HEADEREND:event_doNotCoverRadioButtonActionPerformed
        controller.setSurfaceIsHole(true);
        canvasPanel.repaint();
    }//GEN-LAST:event_doNotCoverRadioButtonActionPerformed

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
        else if (selectedMode == ApplicationModes.TILEMOVE && controller.surfaceIsSelected()
                && originPoint != null && controller.hasTileType())
        {
            mouseDraggedTileMove(evt);
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
    
    private void mouseDraggedTileMove(MouseEvent evt)
    {
        Point2D.Double currentCursorPosition = pointToMetric(evt.getPoint());
        double deltaX = currentCursorPosition.x - originPoint.x;
        double deltaY = currentCursorPosition.y - originPoint.y;
        double tileTypeWidth = controller.getTileWidth();
        double tileTypeHeight = controller.getTileHeight();
        double tileWidth = zeroOrientationRadioButton.isSelected()? tileTypeWidth : tileTypeHeight;
        double tileHeight = zeroOrientationRadioButton.isSelected()? tileTypeHeight : tileTypeWidth;
        double jointWidth = controller.getJointWidth();
        double offsetX = (controller.getOffsetX() + deltaX) % (tileWidth + jointWidth);
        double offsetY = (controller.getOffsetY() + deltaY) % (2 * (tileHeight + jointWidth));
        controller.setOffsetXY(offsetX, offsetY);
        originPoint = currentCursorPosition;
        updatePanelInformation();
        canvasPanel.repaint();
    }
    
    private void canvasPanelMouseReleased(java.awt.event.MouseEvent evt)//GEN-FIRST:event_canvasPanelMouseReleased
    {//GEN-HEADEREND:event_canvasPanelMouseReleased
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
        unselect();
    }//GEN-LAST:event_deleteSurfaceMenuItemActionPerformed

    private void formComponentResized(java.awt.event.ComponentEvent evt)//GEN-FIRST:event_formComponentResized
    {//GEN-HEADEREND:event_formComponentResized

    }//GEN-LAST:event_formComponentResized

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

    private void zeroOrientationRadioButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_zeroOrientationRadioButtonActionPerformed
    {//GEN-HEADEREND:event_zeroOrientationRadioButtonActionPerformed
        controller.setIsNinetyDegree(false);
        canvasPanel.repaint();
    }//GEN-LAST:event_zeroOrientationRadioButtonActionPerformed

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
            unselect();
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
            unselect();
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
            unselect();
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
            unselect();
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
            unselect();
            selectedMode = ApplicationModes.NONE;
            toggleGroup.clearSelection();
            controller.closeProject();
            toolBar.setVisible(false);
            createTileButton.setEnabled(false);
            createTileWindow.setVisible(false);
            menuEditionAnnuler.setEnabled(false);
            menuEditionRepeter.setEnabled(false);
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
            toolBar.setVisible(true);
            createTileButton.setEnabled(true);
            menuEditionAnnuler.setEnabled(true);
            menuEditionRepeter.setEnabled(true);
            unselect();
        }
    }//GEN-LAST:event_newProjectButtonActionPerformed

    private void openProjectButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_openProjectButtonActionPerformed
    {//GEN-HEADEREND:event_openProjectButtonActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
        {
            if (toolBar.isVisible())
            {
                unselect();
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
                unselect();
            }
            controller.loadProject(fileChooser.getSelectedFile());
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

    private void patternComboBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_patternComboBoxActionPerformed
    {//GEN-HEADEREND:event_patternComboBoxActionPerformed
        if ((String) patternComboBox.getSelectedItem() != null)
        {
            Pattern pattern;
            switch ((String) patternComboBox.getSelectedItem())
            {
                case patternA:
                    pattern = Pattern.CHECKERED; break;
                case patternB:
                    pattern = Pattern.LSHAPE; break;
                case patternC:
                    pattern = Pattern.TWOBYTWO; break;
                default:
                    pattern = Pattern.DIAGONAL;
            }
            controller.setPattern(pattern);
            enablePanelButtons();
            canvasPanel.repaint();
        }
    }//GEN-LAST:event_patternComboBoxActionPerformed

    private void ninetyOrientationRadioButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_ninetyOrientationRadioButtonActionPerformed
    {//GEN-HEADEREND:event_ninetyOrientationRadioButtonActionPerformed
        controller.setIsNinetyDegree(true);
        canvasPanel.repaint();
    }//GEN-LAST:event_ninetyOrientationRadioButtonActionPerformed

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

    private void tileMoveToggleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tileMoveToggleActionPerformed
        selectedMode = ApplicationModes.TILEMOVE;
        unselect();
    }//GEN-LAST:event_tileMoveToggleActionPerformed

    private void createTileButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_createTileButtonActionPerformed
    {//GEN-HEADEREND:event_createTileButtonActionPerformed
        createTileWindow.setVisible(true);
    }//GEN-LAST:event_createTileButtonActionPerformed

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
        DefaultComboBoxModel model = new DefaultComboBoxModel(controller.getTileTypeStrings());
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
        createTileColorButton.setBackground(disabledColor);
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

    private void createTileWidthFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_createTileWidthFieldActionPerformed
    {//GEN-HEADEREND:event_createTileWidthFieldActionPerformed

    }//GEN-LAST:event_createTileWidthFieldActionPerformed

    private void menuResetZoomActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_menuResetZoomActionPerformed
    {//GEN-HEADEREND:event_menuResetZoomActionPerformed
        double newZoom = canvasPanel.setZoom(1);
        zoomLabel.setText(String.valueOf((int) (newZoom * 100)));
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

        if (newZoom * 100 > 5)
        {
            zoomLabel.setText(String.valueOf((int) (newZoom * 100)));
        }
        else
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
        
        canvasPanel.repaint();
    }//GEN-LAST:event_menuCustomZoomActionPerformed
    
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
    private javax.swing.JMenuItem deleteSurfaceMenuItem;
    private javax.swing.JRadioButton doNotCoverRadioButton;
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
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JPopupMenu.Separator jSeparator7;
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
    private javax.swing.JMenuItem newProjectButton;
    private javax.swing.JRadioButton ninetyOrientationRadioButton;
    private javax.swing.JTextField offsetXField;
    private javax.swing.JLabel offsetXText;
    private javax.swing.JTextField offsetYField;
    private javax.swing.JLabel offsetYText;
    private javax.swing.JMenuItem openProjectButton;
    private javax.swing.ButtonGroup orientationGroup;
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
    private javax.swing.JPanel rightPanel;
    private javax.swing.JTextField rowOffsetField;
    private javax.swing.JLabel rowOffsetLabel;
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