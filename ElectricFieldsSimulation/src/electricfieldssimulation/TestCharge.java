/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package electricfieldssimulation;

import static electricfieldssimulation.SimulationPanel.*;
import static java.awt.Color.GREEN;
import static java.awt.Color.RED;
import java.awt.Graphics;
import java.util.Arrays;
import net.jafama.FastMath;

/**
 *
 * @author seanjhardy
 */
public class TestCharge {
    public double x,y,xVel,yVel;
    public double charge;
    public double maxSpeed = 1;
    double[] force = new double[2];
    
    public TestCharge(int x, int y, double charge){
        this.x = x;
        this.y = y;
        this.charge = charge;
    }
    
    public double getX(){
        return x;
    }
    public double getY(){
        return y;
    }
    
    public double calculateField(double x, double y){
        return (charge/FastMath.pow(FastMath.hypot(this.y-y, this.x-x),2));// E proportional to Q/r^2
    }
    
    public void calculateForce(){
        force = calculateDirection(this, x,y,charge);
    }
    public void simulate(Graphics g, int width, int height){
        this.xVel += force[0]*100;
        this.yVel += force[1]*100;
        double speed = Math.abs(Math.hypot(yVel, xVel));
        if(speed >= maxSpeed){
            xVel /= speed;
            yVel /= speed;
            xVel *= maxSpeed;
            yVel *= maxSpeed;
        }
        //add velocity to position
        this.x += xVel;
        this.y += yVel;
        if(this.x > width){
            this.xVel *= -1;
            this.x = width-1;
        }if(this.y > height){
            this.yVel *= -1;
            this.y = height-1;
        }
        if(this.x < 0){
            this.xVel *= -1;
            this.x = 1;
        }if(this.y < 0){
            this.yVel *= -1;
            this.y = 1;
        }
        draw(g);
    }
    
    public void draw(Graphics g){
        if(charge > 0){
            g.setColor(RED);
        }else{
            g.setColor(GREEN);
        }
        g.fillOval((int)x-5, (int)y-5, 10, 10);
    }
}
