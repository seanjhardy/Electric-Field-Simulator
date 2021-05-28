/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package electricfieldssimulation;
import static electricfieldssimulation.GUIManager.*;
import java.awt.AWTException;
import java.awt.BasicStroke;
import java.awt.Color;
import static java.awt.Color.BLACK;
import static java.awt.Color.BLUE;
import static java.awt.Color.RED;
import static java.awt.Color.WHITE;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.KeyStroke;
import net.jafama.FastMath;

/**
 *
 * @author seanjhardy
 */
public class SimulationPanel extends JPanel implements MouseListener{
    private static double scale = 20;
    private static int menuWidth = 300;
    private static int width = (int)(getScreenSize().getWidth()/scale - menuWidth/scale);
    private static int height = (int)(getScreenSize().getHeight()/scale);
    private static Color[][] colours = new Color[(int)(width*scale)][(int)(height*scale)];
    private static ArrayList<PointCharge> charges = new ArrayList<>();
    private static ArrayList<int[]> equipotentials = new ArrayList<>();
    private static ArrayList<TestCharge> testCharges = new ArrayList<>();
    private static PointCharge selectedCharge;
    private int selectedEquipotential = -1;
    private int selectionType = 0;
    // 1 = positive charge
    // 2 = negative charge
    // 3 = move charge
    // 4 = equipotential
    // 5 = move equipotential
    // 6 = test Charge
    private static double spacing = 20;
    private Random random = new Random();
    
    //Menu
    private int mouseX, mouseY;
    private JButton exitBtn;
    private boolean drawFieldLines, drawField = true, drawEquipotentials = true, drawParticleField = false;
    private JButton drawFieldLinesBtn, drawFieldBtn, resetEquipotentialsBtn, resetFieldBtn, drawEquipotentialsBtn;
    private JSlider chargeSlider, fieldLinesSlider, scaleSlider;
    private JLabel fieldLinesSliderLabel, chargeSliderLabel, fieldStrengthLabel, scaleSliderLabel;
    private JLabel infoLabel;
    private static DecimalFormat df2 = new DecimalFormat("#.##");
    private String text = "<html>Controls: <br>ESC: Canel action"
            + "<br>1: Create positive charge"
            + "<br>2: Create negative charge"
            + "<br>3: Create equipotential"
            + "<br>4: Create test charge"
            + "<br><br>Click and drag to move placed objects<br>drag items to the X symbol to delete"
            + "<br><br> Created by Sean Hardy 2019</html>";
    private int chargeVal = 0;
    
    public SimulationPanel(){
        createMenu();
        setBindings();
        for(int i = 0; i < 0; i++){
        PointCharge p = new PointCharge(random.nextInt((int)(width*scale)),
                                        random.nextInt((int)(height*scale)),
                                        random.nextInt(10)*(random.nextBoolean()== false? 1 : -1));
        charges.add(p);
        }
        addMouseListener(this);
    }
    
    public void createMenu(){
        exitBtn = new JButton("X");
        exitBtn.addActionListener((ActionEvent e) -> {
            if(e.getSource() == exitBtn){
                System.exit(0);
            }
        });
        exitBtn.setForeground(WHITE);
        exitBtn.setBackground(new Color(255, 0, 0));
        exitBtn.setFocusPainted(false);
        add(exitBtn);
        
        drawFieldLinesBtn = new JButton("Draw Field Lines");
        drawFieldLinesBtn.addActionListener((ActionEvent e) -> {
            if(e.getSource() == drawFieldLinesBtn){
                if(drawFieldLines){
                    if(!drawParticleField){
                        drawFieldLinesBtn.setText("Draw FieldParticles");
                        drawParticleField = true;
                        drawFieldLinesBtn.setBackground(new Color(0, 255, 191));
                    }else{
                        drawFieldLinesBtn.setText("Draw Nothing");
                        drawFieldLines = false;
                        drawParticleField = false;
                        drawFieldLinesBtn.setBackground(new Color(16, 29, 48));
                    }
                }else{
                    drawFieldLinesBtn.setText("Draw Field Lines");
                    drawFieldLines = true;
                    drawParticleField = false;
                    drawFieldLinesBtn.setBackground(new Color(0, 157, 255));
                }
            }
        });
        drawFieldLinesBtn.setForeground(WHITE);
        drawFieldLinesBtn.setBackground(new Color(16, 29, 48));
        drawFieldLinesBtn.setFocusPainted(false);
        add(drawFieldLinesBtn);
        
        drawFieldBtn = new JButton("Draw Field");
        drawFieldBtn.addActionListener((ActionEvent e) -> {
            if(e.getSource() == drawFieldBtn){
                drawField = !drawField;
                if(drawField){
                    drawFieldBtn.setBackground(new Color(0, 157, 255));
                }else{
                    drawFieldBtn.setBackground(new Color(16, 29, 48));
                }
            }
        });
        drawFieldBtn.setForeground(WHITE);
        drawFieldBtn.setBackground(new Color(0, 157, 255));
        drawFieldBtn.setFocusPainted(false);
        add(drawFieldBtn);
        
        resetEquipotentialsBtn = new JButton("Reset Equipotentials");
        resetEquipotentialsBtn.addActionListener((ActionEvent e) -> {
            if(e.getSource() == resetEquipotentialsBtn){
                equipotentials = new ArrayList<>();
                if(drawFieldLines){
                    drawFieldLinesBtn.setBackground(new Color(0, 157, 255));
                }else{
                    drawFieldLinesBtn.setBackground(new Color(16, 29, 48));
                }
            }
        });
        resetEquipotentialsBtn.setForeground(WHITE);
        resetEquipotentialsBtn.setBackground(new Color(16, 29, 48));
        resetEquipotentialsBtn.setFocusPainted(false);
        add(resetEquipotentialsBtn);
        
        resetFieldBtn = new JButton("Reset Field");
        resetFieldBtn.addActionListener((ActionEvent e) -> {
            if(e.getSource() == resetFieldBtn){
                equipotentials = new ArrayList<>();
                charges = new ArrayList<>();
                testCharges = new ArrayList<>();
                if(drawFieldLines){
                    drawFieldLinesBtn.setBackground(new Color(0, 157, 255));
                }else{
                    drawFieldLinesBtn.setBackground(new Color(16, 29, 48));
                }
            }
        });
        resetFieldBtn.setForeground(WHITE);
        resetFieldBtn.setBackground(new Color(16, 29, 48));
        resetFieldBtn.setFocusPainted(false);
        add(resetFieldBtn);
        
        drawEquipotentialsBtn = new JButton("Draw Equipotentials");
        drawEquipotentialsBtn.addActionListener((ActionEvent e) -> {
            if(e.getSource() == drawEquipotentialsBtn){
                drawEquipotentials = !drawEquipotentials;
                if(drawEquipotentials){
                    drawEquipotentialsBtn.setBackground(new Color(0, 157, 255));
                }else{
                    drawEquipotentialsBtn.setBackground(new Color(16, 29, 48));
                }
            }
        });
        drawEquipotentialsBtn.setForeground(WHITE);
        drawEquipotentialsBtn.setBackground(new Color(0, 157, 255));
        drawEquipotentialsBtn.setFocusPainted(false);
        add(drawEquipotentialsBtn);
        
        chargeSliderLabel = new JLabel("Charge Strength");
        chargeSliderLabel.setForeground(WHITE);
        chargeSliderLabel.setBackground(new Color(16, 29, 48));
        add(chargeSliderLabel);
        
        chargeSlider = new JSlider(JSlider.HORIZONTAL, 0,500,100);
        chargeSlider.setForeground(WHITE);
        chargeSlider.setMajorTickSpacing(100);
        chargeSlider.setMinorTickSpacing(50);
        chargeSlider.setPaintTicks(true);
        chargeSlider.setPaintLabels(true);
        chargeSlider.setBackground(new Color(16, 29, 48));
        add(chargeSlider);
        
        fieldLinesSliderLabel = new JLabel("Field Line Density");
        fieldLinesSliderLabel.setForeground(WHITE);
        fieldLinesSliderLabel.setBackground(new Color(16, 29, 48));
        add(fieldLinesSliderLabel);
        
        fieldLinesSlider = new JSlider(JSlider.HORIZONTAL, 0,50,50);
        fieldLinesSlider.setForeground(WHITE);
        fieldLinesSlider.setMajorTickSpacing(25);
        fieldLinesSlider.setMinorTickSpacing(5);
        fieldLinesSlider.setPaintTicks(true);
        fieldLinesSlider.setPaintLabels(true);
        fieldLinesSlider.setBackground(new Color(16, 29, 48));
        add(fieldLinesSlider);
        
        scaleSlider = new JSlider(JSlider.HORIZONTAL, 1,40,20);
        scaleSlider.setForeground(WHITE);
        scaleSlider.setMajorTickSpacing(10);
        scaleSlider.setMinorTickSpacing(5);
        scaleSlider.setPaintTicks(true);
        scaleSlider.setPaintLabels(true);
        scaleSlider.setBackground(new Color(16, 29, 48));
        add(scaleSlider);
        
        infoLabel = new JLabel(text);
        infoLabel.setForeground(WHITE);
        infoLabel.setBackground(new Color(16, 29, 48));
        add(infoLabel);
        
        fieldStrengthLabel = new JLabel(text);
        fieldStrengthLabel.setForeground(WHITE);
        fieldStrengthLabel.setBackground(new Color(16, 29, 48));
        add(fieldStrengthLabel);
        
        scaleSliderLabel = new JLabel("Resolution");
        scaleSliderLabel.setForeground(WHITE);
        scaleSliderLabel.setBackground(new Color(16, 29, 48));
        add(scaleSliderLabel);
        
    }
    public void setBindings(){
        InputMap im = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap am = getActionMap();
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "escape");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_1, 0), "positivePointCharge");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_2, 0), "negativePointCharge");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_3, 0), "equipotential");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_4, 0), "testChargePositive");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_5, 0), "testChargeNegative");
        
        am.put("escape", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
               selectionType = 0;
               if(selectedCharge != null){
                   charges.remove(selectedCharge);
               }
               if(selectedEquipotential != -1){
                   equipotentials.remove(selectedEquipotential);
               }
            }
        });
        am.put("positivePointCharge", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectionType = 1;
            }
        });
        am.put("negativePointCharge", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectionType = 2;
            }
        });
        am.put("equipotential", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectionType = 4;
            }
        });
        am.put("testChargePositive", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectionType = 6;
            }
        });
        am.put("testChargeNegative", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectionType = 7;
            }
        });
    }
    
    @Override
    public synchronized void paintComponent(Graphics g){
        scale = scaleSlider.getValue();
        width = (int)(getScreenSize().getWidth()/scale - menuWidth/scale);
        height = (int)(getScreenSize().getHeight()/scale);
        g.setColor(new Color(0, 0, 0));
        g.fillRect(0, 0, 1920, 1080);
        g.setColor(new Color(16, 29, 48));
        g.fillRect(0,0, (int) (width*scale), (int)(height*scale));
        exitBtn.setBounds((int)(width*scale+menuWidth - 50),0,50,50);
        drawFieldLinesBtn.setBounds((int)(width*scale+menuWidth - 250),100,200,50);
        drawFieldBtn.setBounds((int)(width*scale+menuWidth - 250),200,200,50);
        drawEquipotentialsBtn.setBounds((int)(width*scale+menuWidth - 250),300,200,50);
        resetEquipotentialsBtn.setBounds((int)(width*scale+menuWidth - 250),400,200,50);
        resetFieldBtn.setBounds((int)(width*scale+menuWidth - 250),500,200,50);
        chargeSliderLabel.setBounds((int)(width*scale+menuWidth - 250),550,200,50);
        chargeSlider.setBounds((int)(width*scale+menuWidth - 250),600,200,50);
        fieldLinesSliderLabel.setBounds((int)(width*scale+menuWidth - 250),650,200,50);
        fieldLinesSlider.setBounds((int)(width*scale+menuWidth - 250),700,200,50);
        scaleSliderLabel.setBounds((int)(width*scale+menuWidth - 250),750,200,50);
        scaleSlider.setBounds((int)(width*scale+menuWidth - 250),800,200,50);
        infoLabel.setBounds((int)(width*scale+menuWidth - 250),850,300,200);
        fieldStrengthLabel.setBounds(-100,-100,50,50);
        chargeVal = chargeSlider.getValue();
        if(chargeVal == 0){
            chargeVal = 1;
        }
        if(drawField){
            drawField(g);
        }else{
            g.setColor(new Color(0, 0, 0));
            g.fillRect(0,0, (int) (width*scale), (int)(height*scale));
        }
        if(drawFieldLines){
            drawFieldLines(g);
        }
        if(drawEquipotentials){
            for(int[] coords: equipotentials){
                drawEquipotentials(g, coords[0],coords[1]);
            }
        }
        drawTestCharges(g);
        drawCharges(g);
        drawSelectionType(g);
        g.setColor(new Color(16, 29, 48));
        g.fillRect((int)(width*scale), 0,(int)(menuWidth*scale),(int)(height*scale));
        revalidate();
        repaint();
    }
    
    public void drawField(Graphics g){
        for(int x = 0; x < width; x ++){
            for(int y = 0; y < height; y ++){
                double fieldStrength = calculateFieldStrength((int)(x*scale),(int)(y*scale));
                Color color = calculateFieldColor(fieldStrength*1000);
                g.setColor(color);
                g.fillRect((int)(x*scale),(int)(y*scale),(int)(scale),(int)(scale));
            }
        }
    }
    public void drawFieldLines(Graphics g){
        spacing = ((55-fieldLinesSlider.getValue())*charges.size());
        g.setColor(WHITE);
        if(drawParticleField){
            for(int x = 0; x < width*scale; x += 20){
                for(int y = 0; y < height*scale; y += 20){
                    FieldLinePoint particle = new FieldLinePoint(x,y,1);
                    particle.simulateQuickStep(g);
                }
            }
        }else{
            for(PointCharge p: charges){
                for(double angle = 0.0; angle < 360.0; angle += spacing){
                    double x = p.getX() + FastMath.cos(FastMath.toRadians(angle))*20;
                    double y = p.getY() + FastMath.sin(FastMath.toRadians(angle))*20;
                    FieldLinePoint particle = new FieldLinePoint(x,y,p.getCharge());
                    particle.simulate(g);
                }
            }
        }
    }
    public void drawCharges(Graphics g){
        for(PointCharge p: charges){
            g.setColor(new Color(p.getCharge() > 0 ? 255 : 0, p.getCharge() < 0 ? 255 : 0, p.getCharge() < 0 ? 255 : 0));
            g.fillOval((int)p.getX()-p.getSize()/2,(int)p.getY()-p.getSize()/2,p.getSize(),p.getSize());
            if(p.getCharge() > 0){
                g.setColor(WHITE);
                g.fillRect((int)(p.getX()-5),(int)(p.getY()-1),10,2);
                g.fillRect((int)(p.getX()-1),(int)(p.getY()-5),2,10);
            }else{
                g.setColor(BLACK);
                g.fillRect((int)(p.getX()-5),(int)(p.getY()-1),10,2);
            }
        }
    }
    public void drawEquipotentials(Graphics g, int x, int y){
        g.setColor(WHITE);
        Graphics2D g2d = (Graphics2D)g;
        g2d.setStroke(new BasicStroke(3));
        g.drawLine(x,y-30,x,y);
        g.drawLine(x-20,y,x+20,y);
        g.drawLine(x-20,y,x,y+30);
        g.drawLine(x+20,y,x,y+30);
        g.drawOval(x-10,y,20,20);
        g2d.setStroke(new BasicStroke(1));
        boolean potentialDrawn = false;
        int startX = x, startY = y;
        double equipotentialStrength = calculateFieldStrength(x,y);
        double[] field = new double[4];
        double fieldStrength;
        int direction = 0;
        int i = 0;
        int step = (int) (scale*2);
        while(!potentialDrawn && i < 500){
            for(int a = direction-90; a < direction+90; a += 10){
                fieldStrength = calculateFieldStrength((int)(x+FastMath.cos(FastMath.toRadians(a))*step),(int)(y+FastMath.sin(FastMath.toRadians(a))*step));
                if(FastMath.abs(fieldStrength-equipotentialStrength) < FastMath.abs(field[3]-equipotentialStrength)){
                    field[0] = (int)(x+FastMath.cos(FastMath.toRadians(a))*step);
                    field[1] = (int)(y+FastMath.sin(FastMath.toRadians(a))*step);
                    field[2] = a;
                    field[3] = fieldStrength;
                }
            }
            if(field[0] != 0){
                g.drawLine(x,y,(int)field[0],(int)field[1]);
                x = (int)field[0];
                y = (int)field[1];
                direction = (int) field[2];
                if((FastMath.abs(x-startX) + FastMath.abs(y-startY)) < (int) (scale)*2){
                    potentialDrawn = true;
                }
            }
            field[0] = 0;field[1] = 0;field[2] = 0;field[3] = 0;
            i++;
        }
    }
    public void drawSelectionType(Graphics g){
        mouseX = (int)MouseInfo.getPointerInfo().getLocation().getX();
        mouseY = (int)MouseInfo.getPointerInfo().getLocation().getY();
        mouseX = (int)FastMath.min(mouseX,width*scale);
        if(FastMath.hypot(mouseY - height*scale*0.93, mouseX-width*scale*0.95) < 50){
            Graphics2D g2d = (Graphics2D)g;
            g2d.setStroke(new BasicStroke(20));
            g2d.setColor(RED);
            g.drawLine((int)(width*scale*0.95)-30, (int)(height*scale*0.93)-30, (int)(width*scale*0.95)+30, (int)(height*scale*0.93)+30);
            g.drawLine((int)(width*scale*0.95)-30, (int)(height*scale*0.93)+30, (int)(width*scale*0.95)+30, (int)(height*scale*0.93)-30);
            g2d.setStroke(new BasicStroke(1));
        }else{
            Graphics2D g2d = (Graphics2D)g;
            g2d.setStroke(new BasicStroke(15));
            g2d.setColor(RED);
            g.drawLine((int)(width*scale*0.95)-20, (int)(height*scale*0.93)-20, (int)(width*scale*0.95)+20, (int)(height*scale*0.93)+20);
            g.drawLine((int)(width*scale*0.95)-20, (int)(height*scale*0.93)+20, (int)(width*scale*0.95)+20, (int)(height*scale*0.93)-20);
            g2d.setStroke(new BasicStroke(1));
        }
        if(selectionType == 1){
            g.setColor(RED);
            g.fillOval(mouseX-20,mouseY-20,40,40);
            g.setColor(WHITE);
            g.fillRect(mouseX-10,mouseY-3,20,6);
            g.fillRect(mouseX-3,mouseY-10,6,20);
        }else if(selectionType == 2){
            g.setColor(BLUE);
            g.fillOval(mouseX-20,mouseY-20,40,40);
            g.setColor(WHITE);
            g.fillRect(mouseX-10,mouseY-3,20,6);
        }else if(selectionType == 3){
            if(selectedCharge != null){
                selectedCharge.setX(mouseX);
                selectedCharge.setY(mouseY);
            }
        }else if(selectionType == 4){
            Graphics2D g2d = (Graphics2D)g;
            g2d.setStroke(new BasicStroke(3));
            g.setColor(WHITE);
            g.drawLine(mouseX,mouseY-30,mouseX,mouseY);
            g.drawLine(mouseX-20,mouseY,mouseX+20,mouseY);
            g.drawLine(mouseX-20,mouseY,mouseX,mouseY+30);
            g.drawLine(mouseX+20,mouseY,mouseX,mouseY+30);
            g.drawOval(mouseX-10,mouseY,20,20);
            g2d.setStroke(new BasicStroke(1));
            fieldStrengthLabel.setBounds(mouseX+20,mouseY,200,50);
            fieldStrengthLabel.setText("Field Strength: " + df2.format(calculateFieldStrength(mouseX,mouseY)*10000));
        }else if(selectionType == 5){
            fieldStrengthLabel.setBounds(mouseX+20,mouseY,200,50);
            fieldStrengthLabel.setText("Field Strength: " + df2.format(calculateFieldStrength(mouseX,mouseY)*10000));
            if(selectedEquipotential != -1){
                equipotentials.get(selectedEquipotential)[0] = mouseX;
                equipotentials.get(selectedEquipotential)[1] = mouseY;
            }
        }else if(selectionType == 6){
            g.setColor(RED);
            g.fillOval(mouseX-10,mouseY-10,20,20);
        }else if(selectionType == 7){
            g.setColor(BLUE);
            g.fillOval(mouseX-10,mouseY-10,20,20);
        }
    }
    public void drawTestCharges(Graphics g){
        for(TestCharge charge: testCharges){
            charge.calculateForce();
                    }
        for(TestCharge charge: testCharges){
            charge.simulate(g, (int)(width*scale),(int)(height*scale));
        }
    }
    
    public static double calculateFieldStrength(int x, int y){
        double resultantField = 0.0;
        for(PointCharge point: charges){
            resultantField += point.calculateField(x,y);
        }
        for(TestCharge charge: testCharges){
            resultantField += charge.calculateField(x,y);
        }
        return resultantField;
    }
    public Color calculateFieldColor(double field){
        /*Result = (color2 - color1) * fraction + color1*/
        //field range from 0 - 400
        int R1 = (int)FastMath.min(FastMath.max(255*field,0),255);
        int G1 = 0;
        int B1 = -(int)FastMath.min(FastMath.max(255*field,-255),0);
        if(FastMath.abs(field)>1){
            if(field > 0){
                int R2 = FastMath.max(FastMath.min((int) ((255-R1) * FastMath.sqrt((255*field)-255)/30 + R1),255),0);
                int G2 = FastMath.max(FastMath.min((int) ((255-G1) * FastMath.sqrt((255*field)-255)/15 + G1),255),0);
                int B2 = FastMath.max(FastMath.min((int) ((255-B1) * FastMath.sqrt((255*field)-255)/30 + B1),255),0);
                return new Color(R2,G2,B2);
            }else{
                int R2 = FastMath.max(FastMath.min((int) ((191-R1) * FastMath.sqrt((255*-field)-255)/30 + R1),255),0);
                int G2 = FastMath.max(FastMath.min((int) ((255-G1) * FastMath.sqrt((255*-field)-255)/15 + G1),255),0);
                int B2 = FastMath.max(FastMath.min((int) ((252-B1) * FastMath.sqrt((255*-field)-255)/30 + B1),255),0);
                return new Color(R2,G2,B2);
            }
            
        }else{
            return new Color(R1,G1,B1);
        }
    }
    public static double[] calculateDirection(TestCharge change, double x, double y, double charge){
        double[] force = {0.0,0.0};
        for(PointCharge point: charges){
            if(FastMath.hypot(point.getY()-y, point.getX()-x) > 15){
                double field = point.calculateField(x,y);
                double direction = FastMath.atan2(point.getY()-y, point.getX()-x);
                force[0] -= (field*FastMath.cos(direction)) * (charge > 0 ? 1 : -1);
                force[1] -= (field*FastMath.sin(direction)) * (charge > 0 ? 1 : -1);
            }
        }
        for(TestCharge point: testCharges){
            if((change == null) || (change != point)){
                if(FastMath.hypot(point.getY()-y, point.getX()-x) > 15){
                    double field = point.calculateField(x,y);
                    double direction = FastMath.atan2(point.getY()-y, point.getX()-x);
                    force[0] -= (field*FastMath.cos(direction)) * (charge > 0 ? 1 : -1);
                    force[1] -= (field*FastMath.sin(direction)) * (charge > 0 ? 1 : -1);
                }
            }
        }
        return force;
    }
    public static boolean testCollision(double x,double y){
        for(PointCharge p : charges){
            if(FastMath.hypot(p.getY()-y, p.getX()-x) < 20){
                return true;
            }
        }
        for(TestCharge p : testCharges){
            if(FastMath.hypot(p.getY()-y, p.getX()-x) < 30){
                return true;
            }
        }
        return false;
    }
    //vector normalisation
    public static double getScale(){
        return scale;
    }
    public static double length(double x,double y){
        return FastMath.hypot(y, x);
    }
    public static double[] normalise(double[] point){
        double[] newPoint = point;
        double length = length(newPoint[0],newPoint[1]);
        if(length > 0){
            newPoint[0] /= length;
            newPoint[1] /= length;
        }
        return newPoint;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }
    @Override
    public void mousePressed(MouseEvent e) {
        boolean actionPerformed = false;
        if(selectionType == 1 && !actionPerformed){
            if(FastMath.hypot(mouseY - height*scale*0.93, mouseX-width*scale*0.95) > 50){
                PointCharge p = new PointCharge(mouseX, mouseY, chargeVal/10.0);
                charges.add(p);
                selectionType = 0;
                actionPerformed = true;
            }else{
                selectionType = 0;
                actionPerformed = true;
            }
        }if(selectionType == 2 && !actionPerformed){
            if(FastMath.hypot(mouseY - height*scale*0.93, mouseX-width*scale*0.95) > 50){
                PointCharge p = new PointCharge(mouseX, mouseY, -chargeVal/10.0);
                charges.add(p);
                selectionType = 0;
                actionPerformed = true;
            }else{
                selectionType = 0;
                actionPerformed = true;
            }
        }if(selectionType == 3 && !actionPerformed){
            if(selectedCharge != null){
                selectionType = 0;
                actionPerformed = true;
                if(FastMath.hypot(mouseY - height*scale*0.93, mouseX-width*scale*0.95) < 50){
                    charges.remove(selectedCharge);
                }
                selectedCharge = null;
            }
        }if(selectionType == 4 && !actionPerformed){
            selectionType = 0;
            actionPerformed = true;
            int[] coords = {mouseX,mouseY};
            equipotentials.add(coords);
        }if(selectionType == 5 && !actionPerformed){
            if(FastMath.hypot(mouseY - height*scale*0.93, mouseX-width*scale*0.95) < 50){
                equipotentials.remove(selectedEquipotential);
            }
            selectionType = 0;
            selectedEquipotential = -1;
            actionPerformed = true;
        }if(selectionType == 6 && !actionPerformed){
            if(FastMath.hypot(mouseY - height*scale*0.93, mouseX-width*scale*0.95) > 50){
                testCharges.add(new TestCharge(mouseX,mouseY, chargeVal/10.0));
            }
            selectionType = 0;
            selectedEquipotential = -1;
            actionPerformed = true;
        }if(selectionType == 7 && !actionPerformed){
            if(FastMath.hypot(mouseY - height*scale*0.93, mouseX-width*scale*0.95) > 50){
                testCharges.add(new TestCharge(mouseX,mouseY, -chargeVal/10.0));
            }
            selectionType = 0;
            selectedEquipotential = -1;
            actionPerformed = true;
        }
        
        if(selectionType == 0 && !actionPerformed){
            for(PointCharge p : charges){
                if(FastMath.hypot(p.getY()-mouseY, p.getX()-mouseX) < 10 && selectedCharge != p){
                    selectedCharge = p;
                    selectionType = 3;
                    actionPerformed = true;
                }
            }
        }if(selectionType == 0 && !actionPerformed){
            for(int eq = 0; eq < equipotentials.size(); eq++){
                int[] equipotential = equipotentials.get(eq);
                if(FastMath.hypot(equipotential[1]-mouseY, equipotential[0]-mouseX) < 20 && selectedEquipotential != eq){
                    selectedEquipotential = eq;
                    selectionType = 5;
                }
            }
        }
    }
    @Override
    public void mouseReleased(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    @Override
    public void mouseEntered(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    @Override
    public void mouseExited(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
