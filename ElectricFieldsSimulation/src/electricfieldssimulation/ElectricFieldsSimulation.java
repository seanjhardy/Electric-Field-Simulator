/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package electricfieldssimulation;

import javax.swing.JFrame;

/**
 *
 * @author s-hardy
 */
public class ElectricFieldsSimulation {

    /**
     * @param args the command line arguments
//     */
    private JFrame frame;
    public static void main(String[] args){
        //creates a new, non static instance of the tourno class
        ElectricFieldsSimulation main = new ElectricFieldsSimulation();
    }
    public ElectricFieldsSimulation(){
        frame = new GUIManager();
    }
}
