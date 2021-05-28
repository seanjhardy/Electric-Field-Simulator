/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package electricfieldssimulation;

import static electricfieldssimulation.SimulationPanel.*;
import java.awt.Color;
import static java.awt.Color.WHITE;
import java.awt.Graphics;
import net.jafama.FastMath;

/**
 *
 * @author g-mascarenhas
 */
public class FieldLinePoint {
    public double x,y;
    public double lastX, lastY;
    public double xVel, yVel,speed, charge;
    public double[] lastDirection = {0,0};
    
    public FieldLinePoint(double x, double y,double charge){
        this.x = x;
        this.y = y;
        this.lastX = x;
        this.lastY = y;
        this.speed = 5;
        this.charge = charge;
    }
    
    public void simulate(Graphics g){
        for(int i = 0; i < 200; i ++){
            if(move(g, i)){
               i = 10000; 
            }
            if(testCollision(x,y)){
                i = 1000;
            }
            if(i % 50 == 0 && i != 0){
                g.drawLine((int)lastX,(int)lastY,(int)x,(int)y);
                double direction = FastMath.atan2(yVel*(charge > 0 ? -1 : 1),xVel*(charge > 0 ? -1 : 1));
                int[] xCoords = {(int)(x+FastMath.cos(direction + 120)*20),(int)x,(int)(x+FastMath.cos(direction - 120)*20)};
                int[] yCoords = {(int)(y+FastMath.sin(direction + 120)*20),(int)y,(int)(y+FastMath.sin(direction - 120)*20)};
                g.setColor(WHITE);
                g.drawLine(xCoords[0],yCoords[0],xCoords[1],yCoords[1]);
                g.drawLine(xCoords[1],yCoords[1],xCoords[2],yCoords[2]);
            }
            g.setColor(WHITE);
            g.drawLine((int)lastX,(int)lastY,(int)x,(int)y);
        }
    }
    public void simulateQuickStep(Graphics g){
        for(int i = 0; i < 1; i ++){
            move(g, i);
            if(testCollision(x,y)){
                i = 1000;
            }
            double distance = Math.abs(calculateFieldStrength((int)lastX, (int)lastY));
            g.setColor(new Color(255,255,255,(int)Math.min(255 * (distance*10000),255)));
            g.drawLine((int)lastX,(int)lastY,(int)x,(int)y);
        }
    }
    public boolean move(Graphics g, int step){
        lastX = x;
        lastY = y;
        double[] direction = calculateDirection(null, x,y,charge);
        direction = normalise(direction);
        double angleDiff = FastMath.abs(FastMath.toDegrees(FastMath.atan2(direction[1], direction[0])) - FastMath.toDegrees(FastMath.atan2(lastDirection[1],lastDirection[0])));
        if(angleDiff> 120 && angleDiff < 240 && step > 0){
            return true;
        }
        xVel = direction[0];
        yVel = direction[1];
        this.x += xVel*speed;
        this.y += yVel*speed;
        lastDirection = direction;
        return false;
    }
}
