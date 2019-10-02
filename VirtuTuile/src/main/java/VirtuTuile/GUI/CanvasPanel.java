package VirtuTuile.GUI;

import java.awt.*;

/**
 * @author Petros Fytilis
 */
public class CanvasPanel extends javax.swing.JPanel
{
    // Largeur (en pixels) entre chaque ligne de la grille lorsque le zoom est à 100%
    int largeurGrilleBase = 20;
    
    // Largeur (en pixels) courante entre chaque ligne de la grille
    int largeurGrille = largeurGrilleBase;
    
    // !!!TEST!!!
    Rectangle testRectangle = new Rectangle(50, 50, 100, 200);
    public void resizeTestRectangle(int x, int y, int width, int height)
    {
        testRectangle.x = x;
        testRectangle.y = y;
        testRectangle.width = width;
        testRectangle.height = height;
    }
    /// !!!FIN TEST!!!
    
    public CanvasPanel()
    {
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        
        // !!!TEST!!!
        g.setColor(Color.red);
        g.fillRect(testRectangle.x, testRectangle.y, testRectangle.width, testRectangle.height);
        g.setColor(Color.black);
        /// !!!FIN TEST!!!
        
        int nbColumns = getWidth() / largeurGrille + 1;
        int nbRows = getHeight() / largeurGrille + 1;

        for (int column = 0; column < nbColumns; column++)
        {
            g.drawLine( column * largeurGrille, 0,
                        column * largeurGrille, getHeight());
        }
        
        for (int row = 0; row < nbRows; row++)
        {
            g.drawLine( 0, row * largeurGrille,
                        getWidth(), row * largeurGrille);
        }
        
        g2d.dispose();
    }
}