package VirtuTuile.GUI;

import VirtuTuile.Domain.RectangularSurface;
import VirtuTuile.Infrastructure.Utilities;
import java.awt.Color;
import java.awt.Cursor;
import javax.swing.JOptionPane;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

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
        SELECT, MOVE, RECTANGLE, POLYGON, NONE;
    }

    // Le mode courant de l'application.
    private ApplicationModes selectedMode = ApplicationModes.NONE;

    // Un rectangle est défini par deux points, son premier point métrique est enregistré ici.
    private Point2D firstRectangleCorner = null;

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
    }

    /**
     * @return true si le système actuel est métrique, false pour impérial.
     */
    public boolean isMetric()
    {
        return isMetric;
    }

    /**
     * Setter pour la variable isMetric
     *
     * @param isMetric true pour un système métrique, false pour un système impérial.
     */
    public void setIsMetric(boolean isMetric)
    {
        this.isMetric = isMetric;
    }

    /**
     * Désélectionne la surface sélectionnée.
     */
    private void unselect()
    {
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

        surfaceXField.setEditable(false);
        surfaceXFieldInches.setEditable(false);
        surfaceWidthField.setEditable(false);
        surfaceWidthFieldInches.setEditable(false);
        surfaceYField.setEditable(false);
        surfaceYFieldInches.setEditable(false);
        surfaceHeightField.setEditable(false);
        surfaceHeightFieldInches.setEditable(false);

        surfaceColorButton.setBackground(new Color(240, 240, 240));
        doNotCoverRadioButton.setEnabled(false);
        coverRadioButton.setEnabled(false);
        coverButtonGroup.clearSelection();
        canvasPanel.repaint();
    }

    private void updateSurfaceDimensionsPanel()
    {
        Rectangle2D bounds = selectedSurface.getBounds2D();
        if (isMetric)
        {
            surfaceXField.setText(String.format("%.03f", bounds.getX() / 1000.));
            surfaceYField.setText(String.format("%.03f", bounds.getY() / 1000.));
            surfaceWidthField.setText(String.format("%.03f", bounds.getWidth() / 1000.));
            surfaceHeightField.setText(String.format("%.03f", bounds.getHeight() / 1000.));
        } else
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
    }

    /**
     * Update les deux scrollbars.
     */
    public void updateScrollbars()
    {
        Point2D.Double farthestPoint = controller.getFarthestPoint();
        verticalScrollBar.setMaximum(Math.max(canvasPanel.getHeight() + 100,
                (int) ((farthestPoint.y / 10 + 100) * canvasPanel.getZoom())));
        verticalScrollBar.setVisibleAmount(canvasPanel.getHeight());
        horizontalScrollBar.setMaximum(Math.max(canvasPanel.getWidth() + 100,
                (int) ((farthestPoint.x / 10 + 100) * canvasPanel.getZoom())));
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
        pushTopMenuItem = new javax.swing.JMenuItem();
        pushBottomMenuItem = new javax.swing.JMenuItem();
        pushLeftJMenuItem = new javax.swing.JMenuItem();
        pushRightMenuItem = new javax.swing.JMenuItem();
        deleteSurfaceMenuItem = new javax.swing.JMenuItem();
        jButton1 = new javax.swing.JButton();
        jScrollBar1 = new javax.swing.JScrollBar();
        buttonGroupMesures = new javax.swing.ButtonGroup();
        toolBar = new javax.swing.JToolBar();
        selectionToggle = new javax.swing.JToggleButton();
        moveToggle = new javax.swing.JToggleButton();
        rectangleToggle = new javax.swing.JToggleButton();
        polygonToggle = new javax.swing.JToggleButton();
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
        jButton6 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jTextField2 = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jComboBox2 = new javax.swing.JComboBox<>();
        jTextField7 = new javax.swing.JTextField();
        jTextField6 = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jComboBox3 = new javax.swing.JComboBox<>();
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

        pushTopMenuItem.setText("Pousser la surface en-haut");
        pushTopMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                pushTopMenuItemActionPerformed(evt);
            }
        });
        surfacePopupMenu.add(pushTopMenuItem);

        pushBottomMenuItem.setText("Pousser la surface en-bas");
        pushBottomMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                pushBottomMenuItemActionPerformed(evt);
            }
        });
        surfacePopupMenu.add(pushBottomMenuItem);

        pushLeftJMenuItem.setText("Pousser la surface à gauche");
        pushLeftJMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                pushLeftJMenuItemActionPerformed(evt);
            }
        });
        surfacePopupMenu.add(pushLeftJMenuItem);

        pushRightMenuItem.setText("Pousser la surface à droite");
        pushRightMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                pushRightMenuItemActionPerformed(evt);
            }
        });
        surfacePopupMenu.add(pushRightMenuItem);

        deleteSurfaceMenuItem.setText("Effacer la surface");
        deleteSurfaceMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                deleteSurfaceMenuItemActionPerformed(evt);
            }
        });
        surfacePopupMenu.add(deleteSurfaceMenuItem);

        jButton1.setText("jButton1");

        buttonGroupMesures.add(metricButton);
        buttonGroupMesures.add(imperialButton);

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

        toggleGroup.add(moveToggle);
        moveToggle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/moveIcon.png"))); // NOI18N
        moveToggle.setToolTipText("Déplacer une surface");
        moveToggle.setFocusable(false);
        moveToggle.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        moveToggle.setMargin(new java.awt.Insets(0, 0, 0, 0));
        moveToggle.setMaximumSize(new java.awt.Dimension(30, 30));
        moveToggle.setMinimumSize(new java.awt.Dimension(30, 30));
        moveToggle.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        moveToggle.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
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

        metricButton.setSelected(true);
        metricButton.setText("Métrique");
        metricButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                metricButtonActionPerformed(evt);
            }
        });

        imperialButton.setText("Impérial");
        imperialButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
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
                        .addGap(0, 394, Short.MAX_VALUE))
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
                    .addComponent(verticalScrollBar, javax.swing.GroupLayout.DEFAULT_SIZE, 504, Short.MAX_VALUE)
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

        surfaceXField.setEditable(false);
        surfaceXField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        surfaceXField.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                surfaceXFieldActionPerformed(evt);
            }
        });

        surfaceYField.setEditable(false);
        surfaceYField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        surfaceYField.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                surfaceYFieldActionPerformed(evt);
            }
        });

        surfaceWidthField.setEditable(false);
        surfaceWidthField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        surfaceWidthField.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                surfaceWidthFieldActionPerformed(evt);
            }
        });

        surfaceHeightField.setEditable(false);
        surfaceHeightField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
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

        surfaceHeightFieldInches.setEditable(false);
        surfaceHeightFieldInches.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        surfaceHeightFieldInches.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                surfaceHeightFieldInchesActionPerformed(evt);
            }
        });

        surfaceWidthFieldInches.setEditable(false);
        surfaceWidthFieldInches.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        surfaceWidthFieldInches.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                surfaceWidthFieldInchesActionPerformed(evt);
            }
        });

        surfaceYFieldInches.setEditable(false);
        surfaceYFieldInches.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        surfaceYFieldInches.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                surfaceYFieldInchesActionPerformed(evt);
            }
        });

        surfaceXFieldInches.setEditable(false);
        surfaceXFieldInches.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        surfaceXFieldInches.setToolTipText("");
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

        jLabel19.setText("cm");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jRadioButton1.setBackground(new java.awt.Color(0, 153, 153));
        jRadioButton1.setText("0");
        jRadioButton1.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jRadioButton1ActionPerformed(evt);
            }
        });

        jRadioButton2.setBackground(new java.awt.Color(0, 153, 153));
        jRadioButton2.setText("90");

        jTextField2.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jTextField2ActionPerformed(evt);
            }
        });

        jLabel20.setText("m");

        jLabel21.setText("in");

        jLabel22.setText("m");

        jLabel23.setText("in");

        jButton2.setText("Couvrir");

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jTextField7.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jTextField7ActionPerformed(evt);
            }
        });

        jLabel24.setText("cm");

        jLabel25.setText("cm");

        jLabel12.setText("Largeur :");

        jLabel13.setText("Hauteur :");

        jLabel26.setText("Couleur tuile :");

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout rightPanelLayout = new javax.swing.GroupLayout(rightPanel);
        rightPanel.setLayout(rightPanelLayout);
        rightPanelLayout.setHorizontalGroup(
            rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(rightPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, rightPanelLayout.createSequentialGroup()
                        .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(rightPanelLayout.createSequentialGroup()
                        .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel16)
                            .addComponent(jLabel17)
                            .addComponent(jLabel18))
                        .addGap(18, 18, 18)
                        .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(rightPanelLayout.createSequentialGroup()
                                .addComponent(jRadioButton1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                                .addComponent(jRadioButton2))
                            .addComponent(jButton6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
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
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 5, Short.MAX_VALUE)
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
                                .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel24))
                            .addGroup(rightPanelLayout.createSequentialGroup()
                                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(rightPanelLayout.createSequentialGroup()
                                        .addComponent(jLabel22)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel23))
                                    .addGroup(rightPanelLayout.createSequentialGroup()
                                        .addComponent(jLabel20)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(rightPanelLayout.createSequentialGroup()
                                .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel25))))
                    .addGroup(rightPanelLayout.createSequentialGroup()
                        .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel15)
                            .addComponent(jLabel26)
                            .addComponent(jLabel14))
                        .addGap(18, 18, 18)
                        .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(rightPanelLayout.createSequentialGroup()
                                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jTextField1)
                                    .addComponent(jComboBox3, 0, 76, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel19))))))
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
                .addGap(11, 11, 11)
                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel16)
                    .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(jRadioButton1)
                    .addComponent(jRadioButton2))
                .addGap(5, 5, 5)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jLabel22)
                    .addComponent(jLabel23)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25)
                    .addComponent(jLabel13))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jScrollPane1.setViewportView(rightPanel);

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addComponent(leftPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 635, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(leftPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 604, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 604, Short.MAX_VALUE)
        );

        menuFichier.setText("Fichier");

        menuFichierNouveauProjet.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        menuFichierNouveauProjet.setText("Nouveau Projet");
        menuFichier.add(menuFichierNouveauProjet);

        menuFichierOuvrirProjet.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        menuFichierOuvrirProjet.setText("Ouvrir Projet");
        menuFichier.add(menuFichierOuvrirProjet);

        menuFichierFermerProjet.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.CTRL_MASK));
        menuFichierFermerProjet.setText("Fermer Projet");
        menuFichier.add(menuFichierFermerProjet);

        menuFichierEnregistrerProjet.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        menuFichierEnregistrerProjet.setText("Enregistrer Projet");
        menuFichierEnregistrerProjet.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                menuFichierEnregistrerProjetActionPerformed(evt);
            }
        });
        menuFichier.add(menuFichierEnregistrerProjet);

        menuFichierQuitter.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
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

        menuEditionAnnuler.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_MASK));
        menuEditionAnnuler.setText("Annuler");
        menuEdition.add(menuEditionAnnuler);

        menuEditionRepeter.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Y, java.awt.event.InputEvent.CTRL_MASK));
        menuEditionRepeter.setText("Répéter");
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
        updateScrollbars();
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
        updateScrollbars();
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
        double newZoom = canvasPanel.changeZoom(evt.getWheelRotation());
        zoomLabel.setText(String.valueOf(Math.round(newZoom * 100)));
        canvasPanel.repaint();
        updateScrollbars();
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
            switch (this.selectedMode)
            {
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
                    break;
            }
        } // RIGHT CLICK
        else if (evt.getButton() == java.awt.event.MouseEvent.BUTTON3)
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
    }//GEN-LAST:event_canvasPanelMousePressed

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

            updateSurfaceDimensionsPanel();

            if (surface instanceof VirtuTuile.Domain.RectangularSurface)
            {
                surfaceXField.setEditable(true);
                surfaceXFieldInches.setEditable(true);
                surfaceWidthField.setEditable(true);
                surfaceWidthFieldInches.setEditable(true);
                surfaceYField.setEditable(true);
                surfaceYFieldInches.setEditable(true);
                surfaceHeightField.setEditable(true);
                surfaceHeightFieldInches.setEditable(true);
            }
            surfaceColorButton.setEnabled(true);
            surfaceColorButton.setBackground(selectedSurface.getColor());
            doNotCoverRadioButton.setEnabled(true);
            coverRadioButton.setEnabled(true);
            if (selectedSurface.isHole())
            {
                doNotCoverRadioButton.setSelected(true);
            } else
            {
                coverRadioButton.setSelected(true);
            }
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
                    updateScrollbars();
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
            selectedSurface.setColor(c);
            canvasPanel.repaint();
        }
    }//GEN-LAST:event_surfaceColorButtonActionPerformed

    private void coverRadioButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_coverRadioButtonActionPerformed
    {//GEN-HEADEREND:event_coverRadioButtonActionPerformed
        selectedSurface.setIsHole(false);
    }//GEN-LAST:event_coverRadioButtonActionPerformed

    private void doNotCoverRadioButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_doNotCoverRadioButtonActionPerformed
    {//GEN-HEADEREND:event_doNotCoverRadioButtonActionPerformed
        selectedSurface.setIsHole(true);
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
            updateSurfaceDimensionsPanel();
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
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        originPoint = null;
        updateScrollbars();
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
        controller.moveSurfaceToPoint(new Point2D.Double(bounds[0], selectedSurface.getBounds2D().getY()),
                selectedSurface);
        canvasPanel.repaint();
        updateSurfaceDimensionsPanel();
        updateScrollbars();
    }//GEN-LAST:event_pushLeftJMenuItemActionPerformed

    private void pushRightMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_pushRightMenuItemActionPerformed
    {//GEN-HEADEREND:event_pushRightMenuItemActionPerformed
        double bounds[] = controller.getSurroundingBounds(selectedSurface);
        if (bounds[2] != Integer.MAX_VALUE)
        {
            controller.moveSurfaceToPoint(new Point2D.Double(bounds[2] - selectedSurface.getBounds2D().getWidth(),
                    selectedSurface.getBounds2D().getY()), selectedSurface);
            canvasPanel.repaint();
            updateSurfaceDimensionsPanel();
            updateScrollbars();
        }
    }//GEN-LAST:event_pushRightMenuItemActionPerformed

    private void pushTopMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_pushTopMenuItemActionPerformed
    {//GEN-HEADEREND:event_pushTopMenuItemActionPerformed
        double bounds[] = controller.getSurroundingBounds(selectedSurface);
        controller.moveSurfaceToPoint(new Point2D.Double(selectedSurface.getBounds2D().getX(), bounds[1]),
                selectedSurface);
        canvasPanel.repaint();
        updateSurfaceDimensionsPanel();
        updateScrollbars();
    }//GEN-LAST:event_pushTopMenuItemActionPerformed

    private void pushBottomMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_pushBottomMenuItemActionPerformed
    {//GEN-HEADEREND:event_pushBottomMenuItemActionPerformed
        double bounds[] = controller.getSurroundingBounds(selectedSurface);
        if (bounds[3] != Integer.MAX_VALUE)
        {
            controller.moveSurfaceToPoint(new Point2D.Double(selectedSurface.getBounds2D().getX(),
                    bounds[3] - selectedSurface.getBounds2D().getHeight()), selectedSurface);
            canvasPanel.repaint();
            updateSurfaceDimensionsPanel();
            updateScrollbars();
        }
    }//GEN-LAST:event_pushBottomMenuItemActionPerformed

    private void deleteSurfaceMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_deleteSurfaceMenuItemActionPerformed
    {//GEN-HEADEREND:event_deleteSurfaceMenuItemActionPerformed
        controller.deleteSelectedSurface();
        unselect();
    }//GEN-LAST:event_deleteSurfaceMenuItemActionPerformed

    private void formComponentResized(java.awt.event.ComponentEvent evt)//GEN-FIRST:event_formComponentResized
    {//GEN-HEADEREND:event_formComponentResized
        updateScrollbars();
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
                updateScrollbars();
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
                updateScrollbars();
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
                boolean status = controller.setRectangularSurfaceX(x,
                        (RectangularSurface) selectedSurface);
                surfaceXField.setText(String.format("%.03f",
                        selectedSurface.getBounds2D().getX() / 1000.));
                canvasPanel.repaint();
                updateScrollbars();
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
                boolean status = controller.setRectangularSurfaceX(mm,
                        (RectangularSurface) selectedSurface);
                double newX = selectedSurface.getBounds2D().getX();
                surfaceXField.setText(String.valueOf(Utilities.mmToFeet(newX)));
                surfaceXFieldInches.setText(String.format("%.02f",
                        Utilities.mmToRemainingInches(newX)));
                canvasPanel.repaint();
                updateScrollbars();
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
                boolean status = controller.setRectangularSurfaceY(y,
                        (RectangularSurface) selectedSurface);
                surfaceYField.setText(String.format("%.03f",
                        selectedSurface.getBounds2D().getY() / 1000.));
                canvasPanel.repaint();
                updateScrollbars();
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
                boolean status = controller.setRectangularSurfaceY(mm,
                        (RectangularSurface) selectedSurface);
                double newY = selectedSurface.getBounds2D().getY();
                surfaceYField.setText(String.valueOf(Utilities.mmToFeet(newY)));
                surfaceYFieldInches.setText(String.format("%.02f",
                        Utilities.mmToRemainingInches(newY)));
                canvasPanel.repaint();
                updateScrollbars();
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
                updateScrollbars();
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
                updateScrollbars();
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

    private void jRadioButton1ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jRadioButton1ActionPerformed
    {//GEN-HEADEREND:event_jRadioButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButton1ActionPerformed

    private void jTextField7ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jTextField7ActionPerformed
    {//GEN-HEADEREND:event_jTextField7ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField7ActionPerformed

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jTextField2ActionPerformed
    {//GEN-HEADEREND:event_jTextField2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField2ActionPerformed

    private void metricButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_metricButtonActionPerformed
    {//GEN-HEADEREND:event_metricButtonActionPerformed
        setIsMetric(true);
        xPixelCoordsLabel.setText("X: 0 pixels");
        yPixelCoordsLabel.setText("Y: 0 pixels");
        xMeasureCoordsLabel.setText("X: 0.000 mètres");
        yMeasureCoordsLabel.setText("Y: 0.000 mètres");
        xFtLabel.setText("m");
        yFtLabel.setText("m");
        widthFtLabel.setText("m");
        heightFtLabel.setText("m");
        surfaceXFieldInches.setVisible(false);
        xInLabel.setVisible(false);
        surfaceYFieldInches.setVisible(false);
        yInLabel.setVisible(false);
        surfaceWidthFieldInches.setVisible(false);
        widthInLabel.setVisible(false);
        surfaceHeightFieldInches.setVisible(false);
        heightInLabel.setVisible(false);

        if (selectedSurface != null)
        {
            updateSurfaceDimensionsPanel();
        }

    }//GEN-LAST:event_metricButtonActionPerformed

    private void imperialButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_imperialButtonActionPerformed
    {//GEN-HEADEREND:event_imperialButtonActionPerformed
        setIsMetric(false);
        xPixelCoordsLabel.setText("X: 0 pixels");
        yPixelCoordsLabel.setText("Y: 0 pixels");
        xMeasureCoordsLabel.setText("X: 0ft 0.00in");
        yMeasureCoordsLabel.setText("Y: 0ft 0.00in");
        xFtLabel.setText("ft");
        yFtLabel.setText("ft");
        widthFtLabel.setText("ft");
        heightFtLabel.setText("ft");
        surfaceXFieldInches.setVisible(true);
        xInLabel.setVisible(true);
        surfaceYFieldInches.setVisible(true);
        yInLabel.setVisible(true);
        surfaceWidthFieldInches.setVisible(true);
        widthFtLabel.setVisible(true);
        widthInLabel.setVisible(true);
        surfaceHeightFieldInches.setVisible(true);
        heightInLabel.setVisible(true);

        if (selectedSurface != null)
        {
            updateSurfaceDimensionsPanel();
        }
    }//GEN-LAST:event_imperialButtonActionPerformed

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
    private javax.swing.ButtonGroup buttonGroupMesures;
    private VirtuTuile.GUI.CanvasPanel canvasPanel;
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
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton6;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JComboBox<String> jComboBox3;
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
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JScrollBar jScrollBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JPanel leftPanel;
    private javax.swing.JToggleButton magnetButton;
    private javax.swing.JPanel mainPanel;
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
    private javax.swing.JToggleButton metricButton;
    private javax.swing.JToggleButton moveToggle;
    private javax.swing.JLabel percentLabel;
    private javax.swing.JToggleButton polygonToggle;
    private javax.swing.JMenuItem pushBottomMenuItem;
    private javax.swing.JMenuItem pushLeftJMenuItem;
    private javax.swing.JMenuItem pushRightMenuItem;
    private javax.swing.JMenuItem pushTopMenuItem;
    private javax.swing.JButton quantitiesButton;
    private javax.swing.JToggleButton rectangleToggle;
    private javax.swing.JPanel rightPanel;
    private javax.swing.JToggleButton selectionToggle;
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
    private javax.swing.ButtonGroup toggleGroup;
    private javax.swing.JToolBar toolBar;
    private javax.swing.JMenuBar topMenuBar;
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
    private javax.swing.JButton zoomInButton;
    private javax.swing.JLabel zoomLabel;
    private javax.swing.JButton zoomOutButton;
    // End of variables declaration//GEN-END:variables
}
