package VirtuTuile.GUI;

import VirtuTuile.Domain.Covering;
import VirtuTuile.Domain.RectangularSurface;
import VirtuTuile.Domain.Surface;
import VirtuTuile.Domain.Pattern;
import VirtuTuile.Infrastructure.Utilities;
import java.awt.Color;
import java.awt.Cursor;
import javax.swing.JOptionPane;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import javax.swing.DefaultComboBoxModel;

/**
 * La fenêtre principale de l'application.
 *
 * @author Petros Fytilis
 */
public class MainWindow extends javax.swing.JFrame
{

    // Le controller de l'application.
    public VirtuTuile.Domain.Controller controller;

    // Enumération des différents modes de l'application.
    private enum ApplicationModes
    {
        SELECT, MOVE, RECTANGLE, POLYGON, MERGE, NONE;
    }
    private enum ContextMenuModes
    {
        ALIGN_LEFT, ALIGN_RIGHT, ALIGN_TOP, ALIGN_BOTTOM,
        CENTER_H, CENTER_V, STICK_H, STICK_V, NONE;
    }

    private ContextMenuModes contextMode = ContextMenuModes.NONE;
    private ApplicationModes selectedMode = ApplicationModes.NONE;

    // Un rectangle est défini par deux points, son premier point métrique est enregistré ici.
    private Point2D firstRectangleCorner = null;

    private Surface firstSurfaceToMerge = null;
    
    // Un point d'origine pour le déplacement de surface. Décrit la différence x et y entre
    // le coin supérieur-gauche de la surface et le clic intérieur de la surface.
    private Point2D originPoint = null;

    // Référence à la surface sélectionnée.
    private VirtuTuile.Domain.Surface selectedSurface = null;

    // Si le projet fonctionne en mesure métrique ou impérial.
    private boolean isMetric = true;

    // Si la grille est magnétique en ce moment.
    private boolean gridIsMagnetic = true;

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
        tileXFieldInches.setVisible(false);
        tileYFieldInches.setVisible(false);
        xTuileInchesText.setVisible(false);
        yTuileInchesText.setVisible(false);
        toolBar.setVisible(false);
    }

    /**
     * Désélectionne la surface sélectionnée.
     */
    private void unselect()
    {
        contextMode = ContextMenuModes.NONE;
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        
        firstSurfaceToMerge = null;
        firstRectangleCorner = null;
        canvasPanel.setTemporaryRectangle(null);
        selectedSurface = null;
        controller.unselect();

        surfaceColorButton.setEnabled(false);

        surfaceXField.setText("");
        surfaceYField.setText("");
        surfaceWidthField.setText("");
        surfaceHeightField.setText("");
        surfaceXFieldInches.setText("");
        surfaceYFieldInches.setText("");
        surfaceWidthFieldInches.setText("");
        surfaceHeightFieldInches.setText("");
        surfaceColorButton.setBackground(new Color(240, 240, 240));
        doNotCoverRadioButton.setEnabled(false);
        coverRadioButton.setEnabled(false);
        coverButtonGroup.clearSelection();
        
        tileTypeComboBox.setSelectedItem(null);
        tileColorComboBox.setSelectedItem(null);
        jointWidthField.setText("");
        jointColorButton.setBackground(new Color(240, 240, 240));
        patternComboBox.setSelectedItem(null);
        orientationGroup.clearSelection();

        surfaceXField.setEnabled(false);
        surfaceXFieldInches.setEnabled(false);
        surfaceWidthField.setEnabled(false);
        surfaceWidthFieldInches.setEnabled(false);
        surfaceYField.setEnabled(false);
        surfaceYFieldInches.setEnabled(false);
        surfaceHeightField.setEnabled(false);
        surfaceHeightFieldInches.setEnabled(false);
        
        tileTypeComboBox.setEnabled(false);
        tileColorComboBox.setEnabled(false);
        jointWidthField.setEnabled(false);
        jointColorButton.setEnabled(false);
        patternComboBox.setEnabled(false);
        zeroOrientationRadioButton.setEnabled(false);
        ninetyOrientationRadioButton.setEnabled(false);

        canvasPanel.repaint();
    }

    private void updatePanelInformation()
    {
        Rectangle2D bounds = selectedSurface.getBounds2D();
        Covering covering = selectedSurface.getCovering();
        if (isMetric)
        {
            surfaceXField.setText(String.format("%.03f", bounds.getX() / 1000.));
            surfaceYField.setText(String.format("%.03f", bounds.getY() / 1000.));
            surfaceWidthField.setText(String.format("%.03f", bounds.getWidth() / 1000.));
            surfaceHeightField.setText(String.format("%.03f", bounds.getHeight() / 1000.));
            jointWidthField.setText(String.format("%.03f", covering.getJointWidth()));
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
            jointWidthField.setText(String.format("%.03f",
                    Utilities.mmToInches(covering.getJointWidth())));
        }
        surfaceColorButton.setBackground(selectedSurface.getColor());
        if (selectedSurface.isHole()) doNotCoverRadioButton.setSelected(true);
        else coverRadioButton.setSelected(true);
        if (covering.isNinetyDegree()) ninetyOrientationRadioButton.setSelected(true);
        else zeroOrientationRadioButton.setSelected(true);
        patternComboBox.setSelectedItem(covering.getPattern().toString());
        jointColorButton.setBackground(covering.getJointColor());
        
        String[] colorStrings = covering.getTileType().getColorStrings();
        DefaultComboBoxModel model = new DefaultComboBoxModel(colorStrings);
        tileColorComboBox.setModel(model);
        tileColorComboBox.setSelectedIndex(covering.getTileColorIndex());
        
        model = new DefaultComboBoxModel(controller.getTileTypeStrings());
        tileTypeComboBox.setModel(model);
        tileTypeComboBox.setSelectedItem(covering.getTileType().getName());
    }
    
    private void enablePanelButtons()
    {
        surfaceXField.setEnabled(true);
        surfaceXFieldInches.setEnabled(true);
        surfaceYField.setEnabled(true);
        surfaceYFieldInches.setEnabled(true);
        surfaceColorButton.setEnabled(true);
        coverRadioButton.setEnabled(true);
        doNotCoverRadioButton.setEnabled(true);

        tileTypeComboBox.setEnabled(true);
        tileColorComboBox.setEnabled(true);
        jointWidthField.setEnabled(true);
        jointColorButton.setEnabled(true);
        patternComboBox.setEnabled(true);
        zeroOrientationRadioButton.setEnabled(true);
        ninetyOrientationRadioButton.setEnabled(true);

        if (selectedSurface instanceof VirtuTuile.Domain.RectangularSurface)
        {
            surfaceWidthField.setEnabled(true);
            surfaceWidthFieldInches.setEnabled(true);
            surfaceHeightField.setEnabled(true);
            surfaceHeightFieldInches.setEnabled(true);
        }
        else
        {
            surfaceWidthField.setEnabled(false);
            surfaceWidthFieldInches.setEnabled(false);
            surfaceHeightField.setEnabled(false);
            surfaceHeightFieldInches.setEnabled(false);
        }
    }

    /**
     * Update les deux scrollbars.
     */
    public void updateScrollbars()
    {
        Point2D.Double farthestPoint = controller.getFarthestPoint();
        verticalScrollBar.setMaximum(Math.max(Math.max(canvasPanel.getHeight() + 100,
                (int) ((farthestPoint.y / 10 + 100) * canvasPanel.getZoom())),
                verticalScrollBar.getValue() + verticalScrollBar.getVisibleAmount() + 100));
        verticalScrollBar.setVisibleAmount(canvasPanel.getHeight());
        horizontalScrollBar.setMaximum(Math.max(Math.max(canvasPanel.getWidth() + 100,
                (int) ((farthestPoint.x / 10 + 100) * canvasPanel.getZoom())),
                horizontalScrollBar.getValue() + horizontalScrollBar.getVisibleAmount() + 100));
        horizontalScrollBar.setVisibleAmount(canvasPanel.getWidth());
    }

    /**
     * Prend un Point en pixels et retourne un Point en métrique
     *
     * @param point : un Point représentant des coordonnées en pixels.
     * @return un Point représentant des millimètres.
     */
    private Point2D.Double pointToMetric(java.awt.Point point)
    {
        int posXPixels = point.x + canvasPanel.getHorizontalOffset();
        int posYPixels = point.y + canvasPanel.getVerticalOffset();
        double posXMetric = Utilities.pixelsToMm(posXPixels, canvasPanel.getZoom());
        double posYMetric = Utilities.pixelsToMm(posYPixels, canvasPanel.getZoom());
        return new Point2D.Double(posXMetric, posYMetric);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT
     * modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

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
        deleteSurfaceMenuItem = new javax.swing.JMenuItem();
        orientationGroup = new javax.swing.ButtonGroup();
        measureGroup = new javax.swing.ButtonGroup();
        toolBar = new javax.swing.JToolBar();
        undoButton = new javax.swing.JButton();
        redoButton = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        selectionToggle = new javax.swing.JToggleButton();
        moveToggle = new javax.swing.JToggleButton();
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
        tileXField = new javax.swing.JTextField();
        xTuileText = new javax.swing.JLabel();
        tileXFieldInches = new javax.swing.JTextField();
        xTuileInchesText = new javax.swing.JLabel();
        yTuileText = new javax.swing.JLabel();
        yTuileInchesText = new javax.swing.JLabel();
        tileYFieldInches = new javax.swing.JTextField();
        tileYField = new javax.swing.JTextField();
        tileTypeComboBox = new javax.swing.JComboBox<>();
        tileWidthField = new javax.swing.JTextField();
        tileHeightField = new javax.swing.JTextField();
        largeurTuileText = new javax.swing.JLabel();
        hauteurTuileText = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        tileColorComboBox = new javax.swing.JComboBox<>();
        topMenuBar = new javax.swing.JMenuBar();
        menuFichier = new javax.swing.JMenu();
        menuFichierNouveauProjet = new javax.swing.JMenuItem();
        menuFichierOuvrirProjet = new javax.swing.JMenuItem();
        menuFichierFermerProjet = new javax.swing.JMenuItem();
        menuFichierEnregistrerProjet = new javax.swing.JMenuItem();
        menuFichierQuitter = new javax.swing.JMenuItem();
        menuEdition = new javax.swing.JMenu();
        menuEditionAnnuler = new javax.swing.JMenuItem();
        menuEditionRepeter = new javax.swing.JMenuItem();
        menuAffichage = new javax.swing.JMenu();
        menuGridDistance = new javax.swing.JMenuItem();
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
        gridDistanceSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                gridDistanceSliderStateChanged(evt);
            }
        });

        gridDistanceLabel.setText("20 cm");

        gridDistanceOKButton.setText("OK");
        gridDistanceOKButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
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
        imperialGridDistanceSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                imperialGridDistanceSliderStateChanged(evt);
            }
        });

        imperialGridDistanceLabel.setText("12 pouces");

        imperialGridDistanceOKButton.setText("OK");
        imperialGridDistanceOKButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
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

        pushSubMenu.setText("Pousser");

        pushTopMenuItem.setText("Pousser la surface en-haut");
        pushTopMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pushTopMenuItemActionPerformed(evt);
            }
        });
        pushSubMenu.add(pushTopMenuItem);

        pushBottomMenuItem.setText("Pousser la surface en-bas");
        pushBottomMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pushBottomMenuItemActionPerformed(evt);
            }
        });
        pushSubMenu.add(pushBottomMenuItem);

        pushLeftJMenuItem.setText("Pousser la surface à gauche");
        pushLeftJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pushLeftJMenuItemActionPerformed(evt);
            }
        });
        pushSubMenu.add(pushLeftJMenuItem);

        pushRightMenuItem.setText("Pousser la surface à droite");
        pushRightMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pushRightMenuItemActionPerformed(evt);
            }
        });
        pushSubMenu.add(pushRightMenuItem);

        surfacePopupMenu.add(pushSubMenu);

        stickSubMenu.setText("Coller");

        stickHMenuItem.setText("Coller horizontalement avec...");
        stickHMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stickHMenuItemActionPerformed(evt);
            }
        });
        stickSubMenu.add(stickHMenuItem);

        stickVMenuItem.setText("Coller verticalement avec...");
        stickVMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stickVMenuItemActionPerformed(evt);
            }
        });
        stickSubMenu.add(stickVMenuItem);

        surfacePopupMenu.add(stickSubMenu);

        alignSubMenu.setText("Aligner");
        alignSubMenu.setActionCommand("Aligner");

        alignTopMenuItem.setActionCommand("Aligner en-haut avec...");
        alignTopMenuItem.setLabel("Aligner en-haut avec...");
        alignTopMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                alignTopMenuItemActionPerformed(evt);
            }
        });
        alignSubMenu.add(alignTopMenuItem);
        alignTopMenuItem.getAccessibleContext().setAccessibleDescription("");

        alignBottomMenuItem.setText("Aligner en-bas avec...");
        alignBottomMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                alignBottomMenuItemActionPerformed(evt);
            }
        });
        alignSubMenu.add(alignBottomMenuItem);

        alignLeftMenuItem.setText("Aligner à gauche avec...");
        alignLeftMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                alignLeftMenuItemActionPerformed(evt);
            }
        });
        alignSubMenu.add(alignLeftMenuItem);
        alignLeftMenuItem.getAccessibleContext().setAccessibleDescription("");

        alignRightMenuItem.setText("Aligner à droite avec...");
        alignRightMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                alignRightMenuItemActionPerformed(evt);
            }
        });
        alignSubMenu.add(alignRightMenuItem);

        surfacePopupMenu.add(alignSubMenu);
        alignSubMenu.getAccessibleContext().setAccessibleName("");

        centerSubMenu.setText("Centrer");

        centerHMenuItem.setText("Centrer horizontalement avec...");
        centerHMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                centerHMenuItemActionPerformed(evt);
            }
        });
        centerSubMenu.add(centerHMenuItem);

        centerVMenuItem.setText("Centrer verticalement avec...");
        centerVMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                centerVMenuItemActionPerformed(evt);
            }
        });
        centerSubMenu.add(centerVMenuItem);

        surfacePopupMenu.add(centerSubMenu);

        deleteSurfaceMenuItem.setText("Effacer la surface");
        deleteSurfaceMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteSurfaceMenuItemActionPerformed(evt);
            }
        });
        surfacePopupMenu.add(deleteSurfaceMenuItem);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("VirtuTuile");
        setMinimumSize(new java.awt.Dimension(1000, 700));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
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
        undoButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                undoButtonMouseEntered(evt);
            }
        });
        undoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                undoButtonActionPerformed(evt);
            }
        });
        toolBar.add(undoButton);

        redoButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/redo.png"))); // NOI18N
        redoButton.setFocusable(false);
        redoButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        redoButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        redoButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                redoButtonMouseEntered(evt);
            }
        });
        redoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
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
        selectionToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectionToggleActionPerformed(evt);
            }
        });
        toolBar.add(selectionToggle);

        toggleGroup.add(moveToggle);
        moveToggle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/moveIcon.png"))); // NOI18N
        moveToggle.setToolTipText("Déplacer une surface");
        moveToggle.setFocusable(false);
        moveToggle.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        moveToggle.setMargin(new java.awt.Insets(0, 0, 0, 0));
        moveToggle.setMaximumSize(new java.awt.Dimension(30, 30));
        moveToggle.setMinimumSize(new java.awt.Dimension(30, 30));
        moveToggle.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        moveToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveToggleActionPerformed(evt);
            }
        });
        toolBar.add(moveToggle);

        toggleGroup.add(rectangleToggle);
        rectangleToggle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/rectangle.png"))); // NOI18N
        rectangleToggle.setToolTipText("Créer une surface rectangulaire");
        rectangleToggle.setFocusable(false);
        rectangleToggle.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        rectangleToggle.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rectangleToggle.setMaximumSize(new java.awt.Dimension(30, 30));
        rectangleToggle.setMinimumSize(new java.awt.Dimension(30, 30));
        rectangleToggle.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        rectangleToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
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
        polygonToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
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
        mergeToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
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
        magnetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
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
        debugToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                debugToggleButtonActionPerformed(evt);
            }
        });
        toolBar.add(debugToggleButton);

        leftPanel.setBackground(new java.awt.Color(153, 153, 153));
        leftPanel.setPreferredSize(new java.awt.Dimension(0, 0));

        zoomOutButton.setText("-");
        zoomOutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zoomOutButtonActionPerformed(evt);
            }
        });

        zoomInButton.setText("+");
        zoomInButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zoomInButtonActionPerformed(evt);
            }
        });

        canvasPanel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                canvasPanelMouseDragged(evt);
            }
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                canvasPanelMouseMoved(evt);
            }
        });
        canvasPanel.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                canvasPanelMouseWheelMoved(evt);
            }
        });
        canvasPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                canvasPanelMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                canvasPanelMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout canvasPanelLayout = new javax.swing.GroupLayout(canvasPanel);
        canvasPanel.setLayout(canvasPanelLayout);
        canvasPanelLayout.setHorizontalGroup(
            canvasPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        canvasPanelLayout.setVerticalGroup(
            canvasPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        xPixelCoordsLabel.setText("X: 0 pixels");

        xMeasureCoordsLabel.setText("X: 0.000 mètres");

        zoomLabel.setText("100");

        percentLabel.setText("%");

        verticalScrollBar.setVisibleAmount(40);
        verticalScrollBar.addAdjustmentListener(new java.awt.event.AdjustmentListener() {
            public void adjustmentValueChanged(java.awt.event.AdjustmentEvent evt) {
                verticalScrollBarAdjustmentValueChanged(evt);
            }
        });

        horizontalScrollBar.setOrientation(javax.swing.JScrollBar.HORIZONTAL);
        horizontalScrollBar.setVisibleAmount(40);
        horizontalScrollBar.addAdjustmentListener(new java.awt.event.AdjustmentListener() {
            public void adjustmentValueChanged(java.awt.event.AdjustmentEvent evt) {
                horizontalScrollBarAdjustmentValueChanged(evt);
            }
        });

        yMeasureCoordsLabel.setText("Y: 0.000 mètres");

        yPixelCoordsLabel.setText("Y: 0 pixels");

        measureGroup.add(metricButton);
        metricButton.setSelected(true);
        metricButton.setText("Métrique");
        metricButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                metricButtonActionPerformed(evt);
            }
        });

        measureGroup.add(imperialButton);
        imperialButton.setText("Impérial");
        imperialButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                imperialButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout leftPanelLayout = new javax.swing.GroupLayout(leftPanel);
        leftPanel.setLayout(leftPanelLayout);
        leftPanelLayout.setHorizontalGroup(
            leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(leftPanelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(leftPanelLayout.createSequentialGroup()
                        .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(xMeasureCoordsLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(yMeasureCoordsLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(68, 68, 68)
                        .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(yPixelCoordsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(xPixelCoordsLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 450, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, leftPanelLayout.createSequentialGroup()
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
                                    .addComponent(horizontalScrollBar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(canvasPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(0, 0, 0)
                                .addComponent(verticalScrollBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(20, 20, 20))))
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
                .addGap(3, 3, 3)
                .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(xMeasureCoordsLabel)
                    .addComponent(xPixelCoordsLabel))
                .addGap(3, 3, 3)
                .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(yPixelCoordsLabel)
                    .addComponent(yMeasureCoordsLabel))
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
        surfaceColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                surfaceColorButtonActionPerformed(evt);
            }
        });

        coverRadioButton.setBackground(new java.awt.Color(0, 153, 153));
        coverButtonGroup.add(coverRadioButton);
        coverRadioButton.setText("Oui");
        coverRadioButton.setEnabled(false);
        coverRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                coverRadioButtonActionPerformed(evt);
            }
        });

        doNotCoverRadioButton.setBackground(new java.awt.Color(0, 153, 153));
        coverButtonGroup.add(doNotCoverRadioButton);
        doNotCoverRadioButton.setText("Non");
        doNotCoverRadioButton.setEnabled(false);
        doNotCoverRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doNotCoverRadioButtonActionPerformed(evt);
            }
        });

        surfaceXField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        surfaceXField.setEnabled(false);
        surfaceXField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                surfaceXFieldActionPerformed(evt);
            }
        });

        surfaceYField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        surfaceYField.setEnabled(false);
        surfaceYField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                surfaceYFieldActionPerformed(evt);
            }
        });

        surfaceWidthField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        surfaceWidthField.setEnabled(false);
        surfaceWidthField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                surfaceWidthFieldActionPerformed(evt);
            }
        });

        surfaceHeightField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        surfaceHeightField.setEnabled(false);
        surfaceHeightField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                surfaceHeightFieldActionPerformed(evt);
            }
        });

        xFtLabel.setText("m");

        yFtLabel.setText("m");

        widthFtLabel.setText("m");

        heightFtLabel.setText("m");

        surfaceHeightFieldInches.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        surfaceHeightFieldInches.setEnabled(false);
        surfaceHeightFieldInches.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                surfaceHeightFieldInchesActionPerformed(evt);
            }
        });

        surfaceWidthFieldInches.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        surfaceWidthFieldInches.setEnabled(false);
        surfaceWidthFieldInches.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                surfaceWidthFieldInchesActionPerformed(evt);
            }
        });

        surfaceYFieldInches.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        surfaceYFieldInches.setEnabled(false);
        surfaceYFieldInches.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                surfaceYFieldInchesActionPerformed(evt);
            }
        });

        surfaceXFieldInches.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        surfaceXFieldInches.setToolTipText("");
        surfaceXFieldInches.setEnabled(false);
        surfaceXFieldInches.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
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
        jLabel8.setText("Tuile :");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5))
        );

        jLabel9.setText("x :");

        jLabel10.setText("y :");

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
        jointColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jointColorButtonActionPerformed(evt);
            }
        });

        jointWidthField.setEnabled(false);
        jointWidthField.setPreferredSize(new java.awt.Dimension(50, 30));
        jointWidthField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jointWidthFieldActionPerformed(evt);
            }
        });

        largeurJointText.setText("mm");

        patternComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "A", "B", "C", "D", "E" }));
        patternComboBox.setSelectedIndex(-1);
        patternComboBox.setSelectedItem(null);
        patternComboBox.setEnabled(false);
        patternComboBox.setFocusable(false);
        patternComboBox.setPreferredSize(new java.awt.Dimension(50, 30));
        patternComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                patternComboBoxActionPerformed(evt);
            }
        });

        zeroOrientationRadioButton.setBackground(new java.awt.Color(0, 153, 153));
        orientationGroup.add(zeroOrientationRadioButton);
        zeroOrientationRadioButton.setText("0");
        zeroOrientationRadioButton.setEnabled(false);
        zeroOrientationRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zeroOrientationRadioButtonActionPerformed(evt);
            }
        });

        ninetyOrientationRadioButton.setBackground(new java.awt.Color(0, 153, 153));
        orientationGroup.add(ninetyOrientationRadioButton);
        ninetyOrientationRadioButton.setText("90");
        ninetyOrientationRadioButton.setEnabled(false);
        ninetyOrientationRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ninetyOrientationRadioButtonActionPerformed(evt);
            }
        });

        tileXField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tileXFieldActionPerformed(evt);
            }
        });

        xTuileText.setText("m");

        xTuileInchesText.setText("in");

        yTuileText.setText("m");

        yTuileInchesText.setText("in");

        tileTypeComboBox.setEnabled(false);
        tileTypeComboBox.setMinimumSize(new java.awt.Dimension(200, 22));
        tileTypeComboBox.setPreferredSize(new java.awt.Dimension(200, 30));
        tileTypeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tileTypeComboBoxActionPerformed(evt);
            }
        });

        tileWidthField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tileWidthFieldActionPerformed(evt);
            }
        });

        largeurTuileText.setText("cm");

        hauteurTuileText.setText("cm");

        jLabel12.setText("Largeur :");

        jLabel13.setText("Hauteur :");

        jLabel26.setText("Couleur tuile :");

        tileColorComboBox.setEnabled(false);
        tileColorComboBox.setPreferredSize(new java.awt.Dimension(100, 30));
        tileColorComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tileColorComboBoxActionPerformed(evt);
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
                .addContainerGap()
                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(rightPanelLayout.createSequentialGroup()
                        .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel16)
                            .addComponent(jLabel17)
                            .addComponent(jLabel18))
                        .addGap(5, 5, 5)
                        .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(rightPanelLayout.createSequentialGroup()
                                .addComponent(zeroOrientationRadioButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(ninetyOrientationRadioButton))
                            .addComponent(jointColorButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(patternComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                        .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, 49, Short.MAX_VALUE)
                                .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jLabel12)
                            .addComponent(jLabel13))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(rightPanelLayout.createSequentialGroup()
                                .addComponent(tileWidthField, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(largeurTuileText))
                            .addGroup(rightPanelLayout.createSequentialGroup()
                                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(tileXField, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tileYField, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(rightPanelLayout.createSequentialGroup()
                                        .addComponent(yTuileText)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(tileYFieldInches, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(yTuileInchesText))
                                    .addGroup(rightPanelLayout.createSequentialGroup()
                                        .addComponent(xTuileText)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(tileXFieldInches, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(xTuileInchesText, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(rightPanelLayout.createSequentialGroup()
                                .addComponent(tileHeightField, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(hauteurTuileText))))
                    .addComponent(jLabel14)
                    .addGroup(rightPanelLayout.createSequentialGroup()
                        .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel15)
                            .addComponent(jLabel26))
                        .addGap(5, 5, 5)
                        .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(rightPanelLayout.createSequentialGroup()
                                .addComponent(jointWidthField, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(largeurJointText, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(tileColorComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(tileTypeComboBox, 0, 207, Short.MAX_VALUE)))
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
                    .addComponent(jLabel26)
                    .addComponent(tileColorComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(jointWidthField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(largeurJointText))
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
                .addGap(4, 4, 4)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(tileXField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(xTuileText)
                    .addComponent(tileXFieldInches, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(xTuileInchesText))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(yTuileText)
                    .addComponent(yTuileInchesText)
                    .addComponent(tileYFieldInches, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tileYField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addContainerGap(34, Short.MAX_VALUE))
        );

        jScrollPane1.setViewportView(rightPanel);

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addComponent(leftPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 691, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(leftPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 630, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 630, Short.MAX_VALUE)
        );

        menuFichier.setText("Fichier");

        menuFichierNouveauProjet.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        menuFichierNouveauProjet.setText("Nouveau Projet");
        menuFichierNouveauProjet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuFichierNouveauProjetActionPerformed(evt);
            }
        });
        menuFichier.add(menuFichierNouveauProjet);

        menuFichierOuvrirProjet.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        menuFichierOuvrirProjet.setText("Ouvrir Projet");
        menuFichierOuvrirProjet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuFichierOuvrirProjetActionPerformed(evt);
            }
        });
        menuFichier.add(menuFichierOuvrirProjet);

        menuFichierFermerProjet.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.CTRL_MASK));
        menuFichierFermerProjet.setText("Fermer Projet");
        menuFichierFermerProjet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuFichierFermerProjetActionPerformed(evt);
            }
        });
        menuFichier.add(menuFichierFermerProjet);

        menuFichierEnregistrerProjet.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        menuFichierEnregistrerProjet.setText("Enregistrer Projet");
        menuFichierEnregistrerProjet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuFichierEnregistrerProjetActionPerformed(evt);
            }
        });
        menuFichier.add(menuFichierEnregistrerProjet);

        menuFichierQuitter.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        menuFichierQuitter.setText("Quitter");
        menuFichierQuitter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuFichierQuitterActionPerformed(evt);
            }
        });
        menuFichier.add(menuFichierQuitter);

        topMenuBar.add(menuFichier);

        menuEdition.setText("Edition");

        menuEditionAnnuler.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_MASK));
        menuEditionAnnuler.setText("Annuler");
        menuEditionAnnuler.setEnabled(false);
        menuEditionAnnuler.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuEditionAnnulerActionPerformed(evt);
            }
        });
        menuEdition.add(menuEditionAnnuler);

        menuEditionRepeter.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Y, java.awt.event.InputEvent.CTRL_MASK));
        menuEditionRepeter.setText("Répéter");
        menuEditionRepeter.setEnabled(false);
        menuEditionRepeter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuEditionRepeterActionPerformed(evt);
            }
        });
        menuEdition.add(menuEditionRepeter);

        topMenuBar.add(menuEdition);

        menuAffichage.setText("Affichage");

        menuGridDistance.setText("Distance grille");
        menuGridDistance.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuGridDistanceActionPerformed(evt);
            }
        });
        menuAffichage.add(menuGridDistance);

        topMenuBar.add(menuAffichage);

        menuAide.setText("Aide");

        menuAidePropos.setText("À propos de");
        menuAidePropos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
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
        // Update les coordonnées en pixels
        int posXPixels = evt.getX() + canvasPanel.getHorizontalOffset();
        int posYPixels = evt.getY() + canvasPanel.getVerticalOffset();
        xPixelCoordsLabel.setText("X: " + posXPixels + " pixels");
        yPixelCoordsLabel.setText("Y: " + posYPixels + " pixels");

        // Conversion en mesure métrique.
        Point2D.Double metricPoint = pointToMetric(evt.getPoint());
        double posXMetric = metricPoint.getX();
        double posYMetric = metricPoint.getY();

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

        // Update les coordonnées métriques
        if (isMetric)
        {
            xMeasureCoordsLabel.setText("X: " + String.format("%.03f", posXMetric / 1000.)
                    + " mètres");
            yMeasureCoordsLabel.setText("Y: " + String.format("%.03f", posYMetric / 1000.)
                    + " mètres");
        } // Update les coordonnées impériales
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
    }//GEN-LAST:event_canvasPanelMouseMoved

    /**
     * Décrémente le zoom.
     *
     * @param evt
     */
    private void zoomOutButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zoomOutButtonActionPerformed
        double newZoom = canvasPanel.zoomOutIncrement();
        zoomLabel.setText(String.valueOf((int) (newZoom * 100)));
        canvasPanel.repaint();
    }//GEN-LAST:event_zoomOutButtonActionPerformed

    /**
     * Incrémente le zoom.
     *
     * @param evt
     */
    private void zoomInButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zoomInButtonActionPerformed
        double newZoom = canvasPanel.zoomInIncrement();
        zoomLabel.setText(String.valueOf((int) (newZoom * 100)));
        canvasPanel.repaint();
    }//GEN-LAST:event_zoomInButtonActionPerformed

    private void menuFichierQuitterActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_menuFichierQuitterActionPerformed
    {//GEN-HEADEREND:event_menuFichierQuitterActionPerformed
        System.exit(0);
    }//GEN-LAST:event_menuFichierQuitterActionPerformed

    private void menuAideProposActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_menuAideProposActionPerformed
    {//GEN-HEADEREND:event_menuAideProposActionPerformed
        javax.swing.JOptionPane.showMessageDialog(null, "VirtuTuile 2019\n"
                + "vous est présenté par\n"
                + "Petros Fytilis\n" + "Gabriel Chevrette-Parrot\n"
                + "Nathalie Ponton\n" + "Martin Sasseville", "A propos de",
                JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_menuAideProposActionPerformed

    private void menuFichierEnregistrerProjetActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_menuFichierEnregistrerProjetActionPerformed
    {//GEN-HEADEREND:event_menuFichierEnregistrerProjetActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_menuFichierEnregistrerProjetActionPerformed

    /**
     * Déplace le zoom par la roue de la souris.
     *
     * @param evt
     */
    private void canvasPanelMouseWheelMoved(java.awt.event.MouseWheelEvent evt)//GEN-FIRST:event_canvasPanelMouseWheelMoved
    {//GEN-HEADEREND:event_canvasPanelMouseWheelMoved
        double newZoom = canvasPanel.changeZoom(evt.getWheelRotation(), evt.getX(), evt.getY(),
                horizontalScrollBar.getMaximum(), verticalScrollBar.getMaximum());
        zoomLabel.setText(String.valueOf(Math.round(newZoom * 100)));
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
        // LEFT CLICK
        if (evt.getButton() == java.awt.event.MouseEvent.BUTTON1)
        {
            if (contextMode != ContextMenuModes.NONE)
            {
                contextMenuActionHandler(evt);
            }
            else
            {
                leftClickActionHandler(evt);
            }
        }
        // RIGHT CLICK
        else if (evt.getButton() == java.awt.event.MouseEvent.BUTTON3)
        {
            rightClickActionHandler(evt);
        }
    }//GEN-LAST:event_canvasPanelMousePressed

    private void leftClickActionHandler(java.awt.event.MouseEvent evt)
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
            case MOVE:
                Point2D.Double point = pointToMetric(evt.getPoint());
                selectSurface(point);
                if (selectedSurface != null)
                {
                    Rectangle2D bounds = selectedSurface.getBounds2D();
                    this.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    originPoint = new Point2D.Double(point.x - bounds.getX(),
                            point.y - bounds.getY());
                }
        }
    }
    
    private void rightClickActionHandler(java.awt.event.MouseEvent evt)
    {
        // Annule la création en cours d'une surface rectangulaire.
        if (this.selectedMode == ApplicationModes.RECTANGLE)
        {
            unselect();
        }
        selectSurface(pointToMetric(evt.getPoint()));
        // Display le context menu
        if (selectedSurface != null)
        {
            double surroundingBounds[] = controller.getSurroundingBounds(selectedSurface);
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
            surfacePopupMenu.show(canvasPanel, evt.getX(), evt.getY());
        }
    }
    
    private void contextMenuActionHandler(java.awt.event.MouseEvent evt)
    {
        switch (contextMode)
        {
            case NONE:
                break;
            case ALIGN_TOP:
                allignTop(pointToMetric(evt.getPoint()));
                break;
            case ALIGN_BOTTOM:
                allignBottom(pointToMetric(evt.getPoint()));
                break;
            case ALIGN_LEFT:
                allignLeft(pointToMetric(evt.getPoint()));
                break;
            case ALIGN_RIGHT:
                allignRight(pointToMetric(evt.getPoint()));
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
        if (selectedSurface != null)
        {
            if (firstSurfaceToMerge == null)
            {
                firstSurfaceToMerge = selectedSurface;
            }
            else
            {
                boolean flag = controller.mergeSurfaces(firstSurfaceToMerge, selectedSurface);
                firstSurfaceToMerge = null;
                if (flag)
                {
                    selectSurface(point);
                }
                else
                {
                    unselect();
                    JOptionPane.showMessageDialog(this,
                            "Erreur: surfaces disjointes ne peuvent pas être combinées.");
                }
            }
        }
        else
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
        VirtuTuile.Domain.Surface surface = controller.selectSurface(point);
        if (surface != null)
        {
            selectedSurface = surface;

            updatePanelInformation();

            enablePanelButtons();
            
            canvasPanel.repaint();
        }
        else
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
                JOptionPane.showMessageDialog(this,
                        "Erreur: surface trop petite.");
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
                    JOptionPane.showMessageDialog(this,
                            "Erreur: création de la surface entraîne un conflit.");
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
        java.awt.Color c = javax.swing.JColorChooser.showDialog(null, "Sélectionnez une couleur",
                selectedSurface.getColor());
        if (c != null)
        {
            surfaceColorButton.setBackground(c);
            controller.setSurfaceColor(c, selectedSurface);
            canvasPanel.repaint();
        }
    }//GEN-LAST:event_surfaceColorButtonActionPerformed

    private void coverRadioButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_coverRadioButtonActionPerformed
    {//GEN-HEADEREND:event_coverRadioButtonActionPerformed
        controller.setSurfaceIsHole(false, selectedSurface);
        canvasPanel.repaint();
    }//GEN-LAST:event_coverRadioButtonActionPerformed

    private void doNotCoverRadioButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_doNotCoverRadioButtonActionPerformed
    {//GEN-HEADEREND:event_doNotCoverRadioButtonActionPerformed
        controller.setSurfaceIsHole(true, selectedSurface);
        canvasPanel.repaint();
    }//GEN-LAST:event_doNotCoverRadioButtonActionPerformed

    private void moveToggleActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_moveToggleActionPerformed
    {//GEN-HEADEREND:event_moveToggleActionPerformed
        selectedMode = ApplicationModes.MOVE;
        unselect();
    }//GEN-LAST:event_moveToggleActionPerformed

    private void canvasPanelMouseDragged(java.awt.event.MouseEvent evt)//GEN-FIRST:event_canvasPanelMouseDragged
    {//GEN-HEADEREND:event_canvasPanelMouseDragged
        canvasPanelMouseMoved(evt);
        if (selectedMode == ApplicationModes.MOVE && selectedSurface != null && originPoint != null)
        {
            // Déplace la surface
            Point2D.Double currentCursorPosition = pointToMetric(evt.getPoint());
            double x = currentCursorPosition.x - originPoint.getX();
            double y = currentCursorPosition.y - originPoint.getY();
            Point2D.Double newPos = new Point2D.Double(x, y);
            if (gridIsMagnetic)
            {
                Utilities.movePointToGrid(newPos, canvasPanel.getGridDistance());
            }
            controller.moveSurfaceToPoint(newPos, selectedSurface);
            updatePanelInformation();
            canvasPanel.repaint();

        } // Dessine un rectangle temporaire lors de la création d'un rectangle.
        else if (selectedMode == ApplicationModes.RECTANGLE && firstRectangleCorner != null)
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
    }//GEN-LAST:event_canvasPanelMouseDragged

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
        javax.swing.JDialog dialog;
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
        canvasPanel.setGridDistance(gridDistanceSlider.getValue());
        canvasPanel.repaint();
        gridDistanceDialog.dispose();
    }//GEN-LAST:event_gridDistanceOKButtonActionPerformed

    private void imperialGridDistanceSliderStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_imperialGridDistanceSliderStateChanged
    {//GEN-HEADEREND:event_imperialGridDistanceSliderStateChanged
        imperialGridDistanceLabel.setText(imperialGridDistanceSlider.getValue() + " pouces");
    }//GEN-LAST:event_imperialGridDistanceSliderStateChanged

    private void imperialGridDistanceOKButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_imperialGridDistanceOKButtonActionPerformed
    {//GEN-HEADEREND:event_imperialGridDistanceOKButtonActionPerformed
        canvasPanel.setGridDistance(Utilities.inchesToCm(imperialGridDistanceSlider.getValue()));
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
        double bounds[] = controller.getSurroundingBounds(selectedSurface);
        controller.moveSurfaceToPoint(new Point2D.Double(bounds[0],
                selectedSurface.getBounds2D().getY()), selectedSurface);
        canvasPanel.repaint();
        updatePanelInformation();
    }//GEN-LAST:event_pushLeftJMenuItemActionPerformed

    private void pushRightMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_pushRightMenuItemActionPerformed
    {//GEN-HEADEREND:event_pushRightMenuItemActionPerformed
        double bounds[] = controller.getSurroundingBounds(selectedSurface);
        if (bounds[2] != Integer.MAX_VALUE)
        {
            controller.moveSurfaceToPoint(new Point2D.Double(bounds[2] - selectedSurface.getBounds2D().getWidth(),
                    selectedSurface.getBounds2D().getY()), selectedSurface);
            canvasPanel.repaint();
            updatePanelInformation();
        }
    }//GEN-LAST:event_pushRightMenuItemActionPerformed

    private void pushTopMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_pushTopMenuItemActionPerformed
    {//GEN-HEADEREND:event_pushTopMenuItemActionPerformed
        double bounds[] = controller.getSurroundingBounds(selectedSurface);
        controller.moveSurfaceToPoint(new Point2D.Double(selectedSurface.getBounds2D().getX(),
                bounds[1]), selectedSurface);
        canvasPanel.repaint();
        updatePanelInformation();
    }//GEN-LAST:event_pushTopMenuItemActionPerformed

    private void pushBottomMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_pushBottomMenuItemActionPerformed
    {//GEN-HEADEREND:event_pushBottomMenuItemActionPerformed
        double bounds[] = controller.getSurroundingBounds(selectedSurface);
        if (bounds[3] != Integer.MAX_VALUE)
        {
            controller.moveSurfaceToPoint(new Point2D.Double(selectedSurface.getBounds2D().getX(),
                    bounds[3] - selectedSurface.getBounds2D().getHeight()), selectedSurface);
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
                double width = Double.parseDouble(surfaceWidthField.getText()) * 1000;
                boolean status = controller.setRectangularSurfaceWidth(width,
                        (RectangularSurface) selectedSurface);
                surfaceWidthField.setText(String.format("%.03f",
                        selectedSurface.getBounds2D().getWidth() / 1000.));
                canvasPanel.repaint();
                if (!status)
                {
                    JOptionPane.showMessageDialog(this, "Erreur: modification illégale.");
                }
            } catch (java.lang.NumberFormatException e)
            {
                surfaceWidthField.setText(String.format("%.03f",
                        selectedSurface.getBounds2D().getWidth() / 1000.));
            }
        } else
        {
            try
            {
                double feet = Double.parseDouble(surfaceWidthField.getText());
                double inches = Double.parseDouble(surfaceWidthFieldInches.getText());
                double totalInches = 12 * feet + inches;
                double mm = Utilities.inchesToMM(totalInches);
                boolean status = controller.setRectangularSurfaceWidth(mm,
                        (RectangularSurface) selectedSurface);
                double newWidth = selectedSurface.getBounds2D().getWidth();
                surfaceWidthField.setText(String.valueOf(Utilities.mmToFeet(newWidth)));
                surfaceWidthFieldInches.setText(String.format("%.02f",
                        Utilities.mmToRemainingInches(newWidth)));
                canvasPanel.repaint();
                if (!status)
                {
                    JOptionPane.showMessageDialog(this, "Erreur: modification illégale.");
                }
            } catch (java.lang.NumberFormatException e)
            {
                surfaceWidthField.setText(String.valueOf(Utilities.mmToFeet(selectedSurface.getBounds2D().getWidth())));
                surfaceWidthFieldInches.setText(String.format("%.02f",
                        Utilities.mmToRemainingInches(selectedSurface.getBounds2D().getWidth())));
            }
        }
    }//GEN-LAST:event_surfaceWidthFieldActionPerformed

    private void surfaceXFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_surfaceXFieldActionPerformed
    {//GEN-HEADEREND:event_surfaceXFieldActionPerformed
        if (isMetric)
        {
            try
            {
                double x = Double.parseDouble(surfaceXField.getText()) * 1000;
                boolean status = controller.setSurfaceX(x, selectedSurface);
                surfaceXField.setText(String.format("%.03f",
                        selectedSurface.getBounds2D().getX() / 1000.));
                canvasPanel.repaint();
                if (!status)
                {
                    JOptionPane.showMessageDialog(this, "Erreur: modification illégale.");
                }
            } catch (java.lang.NumberFormatException e)
            {
                surfaceXField.setText(String.format("%.03f",
                        selectedSurface.getBounds2D().getX() / 1000.));
            }
        } else
        {
            try
            {
                double feet = Double.parseDouble(surfaceXField.getText());
                double inches = Double.parseDouble(surfaceXFieldInches.getText());
                double totalInches = 12 * feet + inches;
                double mm = Utilities.inchesToMM(totalInches);
                boolean status = controller.setSurfaceX(mm, selectedSurface);
                double newX = selectedSurface.getBounds2D().getX();
                surfaceXField.setText(String.valueOf(Utilities.mmToFeet(newX)));
                surfaceXFieldInches.setText(String.format("%.02f",
                        Utilities.mmToRemainingInches(newX)));
                canvasPanel.repaint();
                if (!status)
                {
                    JOptionPane.showMessageDialog(this, "Erreur: modification illégale.");
                }
            } catch (java.lang.NumberFormatException e)
            {
                surfaceXField.setText(String.valueOf(Utilities.mmToFeet(selectedSurface.getBounds2D().getX())));
                surfaceXFieldInches.setText(String.format("%.02f",
                        Utilities.mmToRemainingInches(selectedSurface.getBounds2D().getX())));
            }
        }
    }//GEN-LAST:event_surfaceXFieldActionPerformed

    private void surfaceYFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_surfaceYFieldActionPerformed
    {//GEN-HEADEREND:event_surfaceYFieldActionPerformed
        if (isMetric)
        {
            try
            {
                double y = Double.parseDouble(surfaceYField.getText()) * 1000;
                boolean status = controller.setSurfaceY(y, selectedSurface);
                surfaceYField.setText(String.format("%.03f",
                        selectedSurface.getBounds2D().getY() / 1000.));
                canvasPanel.repaint();
                if (!status)
                {
                    JOptionPane.showMessageDialog(this, "Erreur: modification illégale.");
                }
            } catch (java.lang.NumberFormatException e)
            {
                surfaceYField.setText(String.format("%.03f",
                        selectedSurface.getBounds2D().getY() / 1000.));
            }
        } else
        {
            try
            {
                double feet = Double.parseDouble(surfaceYField.getText());
                double inches = Double.parseDouble(surfaceYFieldInches.getText());
                double totalInches = 12 * feet + inches;
                double mm = Utilities.inchesToMM(totalInches);
                boolean status = controller.setSurfaceY(mm, selectedSurface);
                double newY = selectedSurface.getBounds2D().getY();
                surfaceYField.setText(String.valueOf(Utilities.mmToFeet(newY)));
                surfaceYFieldInches.setText(String.format("%.02f",
                        Utilities.mmToRemainingInches(newY)));
                canvasPanel.repaint();
                if (!status)
                {
                    JOptionPane.showMessageDialog(this, "Erreur: modification illégale.");
                }
            } catch (java.lang.NumberFormatException e)
            {
                surfaceYField.setText(String.valueOf(Utilities.mmToFeet(selectedSurface.getBounds2D().getY())));
                surfaceYFieldInches.setText(String.format("%.02f",
                        Utilities.mmToRemainingInches(selectedSurface.getBounds2D().getY())));
            }
        }
    }//GEN-LAST:event_surfaceYFieldActionPerformed

    private void surfaceHeightFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_surfaceHeightFieldActionPerformed
    {//GEN-HEADEREND:event_surfaceHeightFieldActionPerformed
        if (isMetric)
        {
            try
            {
                double height = Double.parseDouble(surfaceHeightField.getText()) * 1000;
                boolean status = controller.setRectangularSurfaceHeight(height,
                        (RectangularSurface) selectedSurface);
                surfaceHeightField.setText(String.format("%.03f",
                        selectedSurface.getBounds2D().getHeight() / 1000.));
                canvasPanel.repaint();
                if (!status)
                {
                    JOptionPane.showMessageDialog(this, "Erreur: modification illégale.");
                }
            } catch (java.lang.NumberFormatException e)
            {
                surfaceHeightField.setText(String.format("%.03f",
                        selectedSurface.getBounds2D().getHeight() / 1000.));
            }
        } else
        {
            try
            {
                double feet = Double.parseDouble(surfaceHeightField.getText());
                double inches = Double.parseDouble(surfaceHeightFieldInches.getText());
                double totalInches = 12 * feet + inches;
                double mm = Utilities.inchesToMM(totalInches);
                boolean status = controller.setRectangularSurfaceHeight(mm,
                        (RectangularSurface) selectedSurface);
                double newHeight = selectedSurface.getBounds2D().getHeight();
                surfaceHeightField.setText(String.valueOf(Utilities.mmToFeet(newHeight)));
                surfaceHeightFieldInches.setText(String.format("%.02f",
                        Utilities.mmToRemainingInches(newHeight)));
                canvasPanel.repaint();
                if (!status)
                {
                    JOptionPane.showMessageDialog(this, "Erreur: modification illégale.");
                }
            } catch (java.lang.NumberFormatException e)
            {
                surfaceHeightField.setText(String.valueOf(Utilities.mmToFeet(selectedSurface.getBounds2D().getHeight())));
                surfaceHeightFieldInches.setText(String.format("%.02f",
                        Utilities.mmToRemainingInches(selectedSurface.getBounds2D().getHeight())));
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
        controller.setIsNinetyDegree(selectedSurface, true);
    }//GEN-LAST:event_zeroOrientationRadioButtonActionPerformed

    private void tileWidthFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_tileWidthFieldActionPerformed
    {//GEN-HEADEREND:event_tileWidthFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tileWidthFieldActionPerformed

    private void tileXFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_tileXFieldActionPerformed
    {//GEN-HEADEREND:event_tileXFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tileXFieldActionPerformed

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
        xTuileText.setText("m");
        yTuileText.setText("m");
        xTuileInchesText.setText("");
        yTuileInchesText.setText("");
        largeurJointText.setText("mm");
        largeurTuileText.setText("cm");
        hauteurTuileText.setText("cm");
        surfaceXFieldInches.setVisible(false);
        xInLabel.setVisible(false);
        surfaceYFieldInches.setVisible(false);
        yInLabel.setVisible(false);
        surfaceWidthFieldInches.setVisible(false);
        widthInLabel.setVisible(false);
        surfaceHeightFieldInches.setVisible(false);
        heightInLabel.setVisible(false);
        tileXFieldInches.setVisible(false);
        tileYFieldInches.setVisible(false);
        xTuileInchesText.setVisible(false);
        yTuileInchesText.setVisible(false);

        if (selectedSurface != null)
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
        xTuileText.setText("ft");
        yTuileText.setText("ft");
        xTuileInchesText.setText("in");
        yTuileInchesText.setText("in");
        largeurJointText.setText("in");
        largeurTuileText.setText("in");
        hauteurTuileText.setText("in");
        surfaceXFieldInches.setVisible(true);
        xInLabel.setVisible(true);
        surfaceYFieldInches.setVisible(true);
        yInLabel.setVisible(true);
        surfaceWidthFieldInches.setVisible(true);
        widthFtLabel.setVisible(true);
        widthInLabel.setVisible(true);
        surfaceHeightFieldInches.setVisible(true);
        heightInLabel.setVisible(true);
        tileXFieldInches.setVisible(true);
        tileYFieldInches.setVisible(true);
        xTuileInchesText.setVisible(true);
        yTuileInchesText.setVisible(true);

        if (selectedSurface != null)
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
        unselect();
        controller.undo();
        canvasPanel.repaint();
    }//GEN-LAST:event_menuEditionAnnulerActionPerformed

    private void menuEditionRepeterActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_menuEditionRepeterActionPerformed
    {//GEN-HEADEREND:event_menuEditionRepeterActionPerformed
        unselect();
        controller.redo();
        canvasPanel.repaint();
    }//GEN-LAST:event_menuEditionRepeterActionPerformed

    private void undoButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_undoButtonActionPerformed
    {//GEN-HEADEREND:event_undoButtonActionPerformed
        unselect();
        controller.undo();
        canvasPanel.repaint();
        undoButton.setToolTipText(controller.getUndoPresentationName());
    }//GEN-LAST:event_undoButtonActionPerformed

    private void redoButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_redoButtonActionPerformed
    {//GEN-HEADEREND:event_redoButtonActionPerformed
        unselect();
        controller.redo();
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

    private void menuFichierFermerProjetActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_menuFichierFermerProjetActionPerformed
    {//GEN-HEADEREND:event_menuFichierFermerProjetActionPerformed
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
            menuEditionAnnuler.setEnabled(false);
            menuEditionRepeter.setEnabled(false);
        }
    }//GEN-LAST:event_menuFichierFermerProjetActionPerformed

    private void menuFichierNouveauProjetActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_menuFichierNouveauProjetActionPerformed
    {//GEN-HEADEREND:event_menuFichierNouveauProjetActionPerformed
        if (!toolBar.isVisible() || JOptionPane.showConfirmDialog(null,
                "Pour ouvrir un nouveau projet, le projet en cours doit être fermé. Voulez-vous continuer?",
                "Nouveau projet",
                JOptionPane.YES_NO_OPTION) == 0)
        {    
            controller.newProject();
            toolBar.setVisible(true);
            menuEditionAnnuler.setEnabled(true);
            menuEditionRepeter.setEnabled(true);
            unselect();
        }
    }//GEN-LAST:event_menuFichierNouveauProjetActionPerformed

    private void menuFichierOuvrirProjetActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_menuFichierOuvrirProjetActionPerformed
    {//GEN-HEADEREND:event_menuFichierOuvrirProjetActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_menuFichierOuvrirProjetActionPerformed

    private void alignTopMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_alignTopMenuItemActionPerformed
    {//GEN-HEADEREND:event_alignTopMenuItemActionPerformed
        contextMode = ContextMenuModes.ALIGN_TOP;
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_alignTopMenuItemActionPerformed

    private void allignTop(Point2D.Double point)
    {
        Surface surface = controller.findSurface(point);
        if (surface != null)
        {
            double newY = surface.getBounds2D().getY();
            boolean flag = controller.setSurfaceY(newY, selectedSurface);
            if (flag)
            {
                canvasPanel.repaint();
            }
            else
            {
                JOptionPane.showMessageDialog(this, "Erreur: déplacement illégal.");
            }
        }
        else
        {
            JOptionPane.showMessageDialog(this, "Erreur: aucune surface n'a été sélectionnée.");
        }
    }
    
    private void alignBottomMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_alignBottomMenuItemActionPerformed
    {//GEN-HEADEREND:event_alignBottomMenuItemActionPerformed
        contextMode = ContextMenuModes.ALIGN_BOTTOM;
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_alignBottomMenuItemActionPerformed

    private void allignBottom(Point2D.Double point)
    {
        Surface surface = controller.findSurface(point);
        if (surface != null)
        {
            Rectangle2D bounds = surface.getBounds2D();
            double newY = bounds.getY() + bounds.getHeight() - selectedSurface.getBounds2D().getHeight();
            boolean flag = controller.setSurfaceY(newY, selectedSurface);
            if (flag)
            {
                canvasPanel.repaint();
            }
            else
            {
                JOptionPane.showMessageDialog(this, "Erreur: déplacement illégal.");
            }
        }
        else
        {
            JOptionPane.showMessageDialog(this, "Erreur: aucune surface n'a été sélectionnée.");
        }
    }
    
    private void alignLeftMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_alignLeftMenuItemActionPerformed
    {//GEN-HEADEREND:event_alignLeftMenuItemActionPerformed
        contextMode = ContextMenuModes.ALIGN_LEFT;
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_alignLeftMenuItemActionPerformed

    private void allignLeft(Point2D.Double point)
    {
        Surface surface = controller.findSurface(point);
        if (surface != null)
        {
            double newX = surface.getBounds2D().getX();
            boolean flag = controller.setSurfaceX(newX, selectedSurface);
            if (flag)
            {
                canvasPanel.repaint();
            }
            else
            {
                JOptionPane.showMessageDialog(this, "Erreur: déplacement illégal.");
            }
        }
        else
        {
            JOptionPane.showMessageDialog(this, "Erreur: aucune surface n'a été sélectionnée.");
        }
    }
    
    private void alignRightMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_alignRightMenuItemActionPerformed
    {//GEN-HEADEREND:event_alignRightMenuItemActionPerformed
        contextMode = ContextMenuModes.ALIGN_RIGHT;
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_alignRightMenuItemActionPerformed
    
    private void allignRight(Point2D.Double point)
    {
        Surface surface = controller.findSurface(point);
        if (surface != null)
        {
            Rectangle2D bounds = surface.getBounds2D();
            double newX = bounds.getX() + bounds.getWidth() - selectedSurface.getBounds2D().getWidth();
            boolean flag = controller.setSurfaceX(newX, selectedSurface);
            if (flag)
            {
                canvasPanel.repaint();
            }
            else
            {
                JOptionPane.showMessageDialog(this, "Erreur: déplacement illégal.");
            }
        }
        else
        {
            JOptionPane.showMessageDialog(this, "Erreur: aucune surface n'a été sélectionnée.");
        }
    }
    
    private void centerHMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_centerHMenuItemActionPerformed
    {//GEN-HEADEREND:event_centerHMenuItemActionPerformed
        contextMode = ContextMenuModes.CENTER_H;
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_centerHMenuItemActionPerformed

    private void centerH(Point2D.Double point)    
    {
        Surface surface = controller.findSurface(point);
        if (surface != null)
        {
            Rectangle2D bounds = surface.getBounds2D();
            double midPoint = bounds.getX() + bounds.getWidth() / 2;
            double newX = midPoint - selectedSurface.getBounds2D().getWidth() / 2;
            boolean flag = controller.setSurfaceX(newX, selectedSurface);
            if (flag)
            {
                canvasPanel.repaint();
            }
            else
            {
                JOptionPane.showMessageDialog(this, "Erreur: déplacement illégal.");
            }
        }
        else
        {
            JOptionPane.showMessageDialog(this, "Erreur: aucune surface n'a été sélectionnée.");
        }
    }
    
    private void centerVMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_centerVMenuItemActionPerformed
    {//GEN-HEADEREND:event_centerVMenuItemActionPerformed
        contextMode = ContextMenuModes.CENTER_V;
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_centerVMenuItemActionPerformed

    private void centerV(Point2D.Double point)
    {
        Surface surface = controller.findSurface(point);
        if (surface != null)
        {
            Rectangle2D bounds = surface.getBounds2D();
            double midPoint = bounds.getY() + bounds.getHeight() / 2;
            double newY = midPoint - selectedSurface.getBounds2D().getHeight() / 2;
            boolean flag = controller.setSurfaceY(newY, selectedSurface);
            if (flag)
            {
                canvasPanel.repaint();
            }
            else
            {
                JOptionPane.showMessageDialog(this, "Erreur: déplacement illégal.");
            }
        }
        else
        {
            JOptionPane.showMessageDialog(this, "Erreur: aucune surface n'a été sélectionnée.");
        }
    }
    
    private void stickHMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_stickHMenuItemActionPerformed
    {//GEN-HEADEREND:event_stickHMenuItemActionPerformed
        contextMode = ContextMenuModes.STICK_H;
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_stickHMenuItemActionPerformed

    private void stickH(Point2D.Double point)
    {
        Surface surface = controller.findSurface(point);
        if (surface != null)
        {
            if (surface == selectedSurface)
            {
                JOptionPane.showMessageDialog(this, "Erreur: sélection de la même surface.");
            }
            else
            {
                Rectangle2D bounds1 = selectedSurface.getBounds2D();
                Rectangle2D bounds2 = surface.getBounds2D();
                double newX;
                if (bounds1.getX() > bounds2.getX())
                {
                    newX = bounds2.getX() + bounds2.getWidth();
                }
                else
                {
                    newX = bounds2.getX() - bounds1.getWidth();
                }
                boolean flag = controller.setSurfaceX(newX, selectedSurface);
                if (flag)
                {
                    canvasPanel.repaint();
                }
                else
                {
                    JOptionPane.showMessageDialog(this, "Erreur: déplacement illégal.");
                }
            }
        }
        else
        {
            JOptionPane.showMessageDialog(this, "Erreur: aucune surface n'a été sélectionnée.");
        }
    }
    
    private void stickVMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_stickVMenuItemActionPerformed
    {//GEN-HEADEREND:event_stickVMenuItemActionPerformed
        contextMode = ContextMenuModes.STICK_V;
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_stickVMenuItemActionPerformed
    
    private void stickV(Point2D.Double point)
    {
        Surface surface = controller.findSurface(point);
        if (surface != null)
        {
            if (surface == selectedSurface)
            {
                JOptionPane.showMessageDialog(this, "Erreur: sélection de la même surface.");
            }
            else
            {
                Rectangle2D bounds1 = selectedSurface.getBounds2D();
                Rectangle2D bounds2 = surface.getBounds2D();
                double newY;
                if (bounds1.getY() > bounds2.getY())
                {
                    newY = bounds2.getY() + bounds2.getHeight();
                }
                else
                {
                    newY = bounds2.getY() - bounds1.getHeight();
                }
                boolean flag = controller.setSurfaceY(newY, selectedSurface);
                if (flag)
                {
                    canvasPanel.repaint();
                }
                else
                {
                    JOptionPane.showMessageDialog(this, "Erreur: déplacement illégal.");
                }
            }
        }
        else
        {
            JOptionPane.showMessageDialog(this, "Erreur: aucune surface n'a été sélectionnée.");
        }
    }
    
    private void tileTypeComboBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_tileTypeComboBoxActionPerformed
    {//GEN-HEADEREND:event_tileTypeComboBoxActionPerformed
        try
        {
            controller.setTileTypeByIndex(selectedSurface, tileTypeComboBox.getSelectedIndex());
            updatePanelInformation();
        }
        catch (Exception e) {}
    }//GEN-LAST:event_tileTypeComboBoxActionPerformed

    private void tileColorComboBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_tileColorComboBoxActionPerformed
    {//GEN-HEADEREND:event_tileColorComboBoxActionPerformed
        int index = tileColorComboBox.getSelectedIndex();
        if (index > -1) controller.setCoveringTileColorByIndex(selectedSurface, index);
    }//GEN-LAST:event_tileColorComboBoxActionPerformed

    private void jointWidthFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jointWidthFieldActionPerformed
    {//GEN-HEADEREND:event_jointWidthFieldActionPerformed
        if (isMetric)
        {
            try
            {
                double width = Double.parseDouble(jointWidthField.getText());
                controller.setJointWidth(selectedSurface, width);
                jointWidthField.setText(String.format("%.03f",
                        selectedSurface.getCovering().getJointWidth()));
            }
            catch (java.lang.NumberFormatException e)
            {
                jointWidthField.setText(String.format("%.03f",
                        selectedSurface.getCovering().getJointWidth()));
            }
        }
        else
        {
            try
            {
                double width = Utilities.inchesToMM(Double.parseDouble(jointWidthField.getText()));
                controller.setJointWidth(selectedSurface, width);
                jointWidthField.setText(String.format("%.03f",
                        Utilities.mmToInches(selectedSurface.getCovering().getJointWidth())));
            }
            catch (java.lang.NumberFormatException e)
            {
                jointWidthField.setText(String.format("%.03f",
                        Utilities.mmToInches(selectedSurface.getCovering().getJointWidth())));
            }
        }
    }//GEN-LAST:event_jointWidthFieldActionPerformed

    private void jointColorButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jointColorButtonActionPerformed
    {//GEN-HEADEREND:event_jointColorButtonActionPerformed
        java.awt.Color c = javax.swing.JColorChooser.showDialog(null, "Sélectionnez une couleur",
            selectedSurface.getCovering().getJointColor());
        if (c != null)
        {
            jointColorButton.setBackground(c);
            controller.setJointColor(c, selectedSurface);
            canvasPanel.repaint();
        }
    }//GEN-LAST:event_jointColorButtonActionPerformed

    private void patternComboBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_patternComboBoxActionPerformed
    {//GEN-HEADEREND:event_patternComboBoxActionPerformed
        if ((String) patternComboBox.getSelectedItem() != null)
        {
            controller.setPattern(selectedSurface,
                    Pattern.valueOf((String) patternComboBox.getSelectedItem()));
        }
    }//GEN-LAST:event_patternComboBoxActionPerformed

    private void ninetyOrientationRadioButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_ninetyOrientationRadioButtonActionPerformed
    {//GEN-HEADEREND:event_ninetyOrientationRadioButtonActionPerformed
        controller.setIsNinetyDegree(selectedSurface, true);
    }//GEN-LAST:event_ninetyOrientationRadioButtonActionPerformed
    
    
    
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
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
    private javax.swing.JMenu centerSubMenu;
    private javax.swing.JMenuItem centerVMenuItem;
    private javax.swing.ButtonGroup coverButtonGroup;
    private javax.swing.JRadioButton coverRadioButton;
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
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel26;
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
    private javax.swing.JMenu menuEdition;
    private javax.swing.JMenuItem menuEditionAnnuler;
    private javax.swing.JMenuItem menuEditionRepeter;
    private javax.swing.JMenu menuFichier;
    private javax.swing.JMenuItem menuFichierEnregistrerProjet;
    private javax.swing.JMenuItem menuFichierFermerProjet;
    private javax.swing.JMenuItem menuFichierNouveauProjet;
    private javax.swing.JMenuItem menuFichierOuvrirProjet;
    private javax.swing.JMenuItem menuFichierQuitter;
    private javax.swing.JMenuItem menuGridDistance;
    private javax.swing.JToggleButton mergeToggle;
    private javax.swing.JToggleButton metricButton;
    private javax.swing.JToggleButton moveToggle;
    private javax.swing.JRadioButton ninetyOrientationRadioButton;
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
    private javax.swing.JPanel rightPanel;
    private javax.swing.JToggleButton selectionToggle;
    private javax.swing.JMenuItem stickHMenuItem;
    private javax.swing.JMenu stickSubMenu;
    private javax.swing.JMenuItem stickVMenuItem;
    private javax.swing.JButton surfaceColorButton;
    private javax.swing.JTextField surfaceHeightField;
    private javax.swing.JTextField surfaceHeightFieldInches;
    private javax.swing.JPopupMenu surfacePopupMenu;
    private javax.swing.JTextField surfaceWidthField;
    private javax.swing.JTextField surfaceWidthFieldInches;
    private javax.swing.JTextField surfaceXField;
    private javax.swing.JTextField surfaceXFieldInches;
    private javax.swing.JTextField surfaceYField;
    private javax.swing.JTextField surfaceYFieldInches;
    private javax.swing.JComboBox<String> tileColorComboBox;
    private javax.swing.JTextField tileHeightField;
    private javax.swing.JComboBox<String> tileTypeComboBox;
    private javax.swing.JTextField tileWidthField;
    private javax.swing.JTextField tileXField;
    private javax.swing.JTextField tileXFieldInches;
    private javax.swing.JTextField tileYField;
    private javax.swing.JTextField tileYFieldInches;
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
    private javax.swing.JLabel xTuileInchesText;
    private javax.swing.JLabel xTuileText;
    private javax.swing.JLabel yFtLabel;
    private javax.swing.JLabel yInLabel;
    private javax.swing.JLabel yMeasureCoordsLabel;
    private javax.swing.JLabel yPixelCoordsLabel;
    private javax.swing.JLabel yTuileInchesText;
    private javax.swing.JLabel yTuileText;
    private javax.swing.JRadioButton zeroOrientationRadioButton;
    private javax.swing.JButton zoomInButton;
    private javax.swing.JLabel zoomLabel;
    private javax.swing.JButton zoomOutButton;
    // End of variables declaration//GEN-END:variables
}
