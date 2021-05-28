/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package electricfieldssimulation;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author s-hardy
 */
public class GUIManager extends JFrame{
    private CardLayout layoutController;
    private static JPanel mainPanel;
    private String currentPanel = "main";
    private JPanel simulationPanel;
    private static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    
    public GUIManager(){
        super("Electric Field Simulation");
        simulationPanel = new SimulationPanel();
        layoutController = new CardLayout();
        mainPanel = new JPanel(layoutController);
        //This componentListener allows the panel to
        //dynamically resize every widget when the frame changes shape
        mainPanel.addComponentListener(new ComponentAdapter() {  
            public void componentResized(ComponentEvent evt) {
                switch (currentPanel) {
                    case "main":
                        simulationPanel.revalidate();
                        simulationPanel.repaint();
                        break;
                    default:
                        break;
                }
            }
        });
        layoutController.addLayoutComponent(simulationPanel, "main");  
        mainPanel.add(simulationPanel);
        add(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setSize((int)screenSize.getWidth(), (int)screenSize.getHeight());
        setBackground(new Color(16, 29, 48));
        setVisible(true); 
    }
    
    public static Dimension getScreenSize(){
        return screenSize;
    }
}
