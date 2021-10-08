
package testproject;

import javax.swing.SwingUtilities;

/**
 *
 * @author Ri Richi
 */
public class TestProject
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() ->
        {
            Controller contr = new Controller(); 
            contr.setUpGUI();
        });
    }
    
}
