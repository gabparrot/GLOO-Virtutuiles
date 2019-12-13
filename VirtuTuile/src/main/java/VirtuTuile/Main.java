package VirtuTuile;

import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

public class Main
{
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
        mainWindow.setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
        mainWindow.setVisible(true);
    }
}