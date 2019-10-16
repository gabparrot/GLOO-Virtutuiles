package VirtuTuile;

import javax.swing.UIManager.*;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * @author Petros Fytilis
 */
public class Main
{
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {   
        try
        {
            for (LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels())
            {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | IllegalAccessException |
                 InstantiationException | UnsupportedLookAndFeelException e) {}
        
        VirtuTuile.GUI.MainWindow mainWindow = new VirtuTuile.GUI.MainWindow();
        
        try
        {
            javax.swing.ImageIcon img = new javax.swing.ImageIcon("src/main/resources/logo.png");
            mainWindow.setIconImage(img.getImage());
        }
        catch (Exception e){}
        
        mainWindow.setVisible(true);
    }
}