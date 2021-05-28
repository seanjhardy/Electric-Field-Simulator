/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package electricfieldssimulation;

import net.jafama.FastMath;

/**
 *
 * @author s-hardy
 */
public class PointCharge{
    private double x,y;
    private double charge;
    private int size;
    
    public PointCharge(int x, int y, double charge){
        this.charge = charge;
        this.size = 20;
        this.x = x;
        this.y = y;
    }
    
    public double calculateField(double x, double y){
        return (charge/FastMath.pow(FastMath.hypot(this.y-y, this.x-x),2));// E proportional to Q/r^2
    }
    public double getX(){
        return x;
    }
    public double getY(){
        return y;
    }
    public double getCharge(){
        return charge;
    }
    public int getSize(){
        return size;
    }
    
    public void setX(double x){
        this.x = x;
    }
    public void setY(double y){
        this.y = y;
    }
}
