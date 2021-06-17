package engine.data;

import utils.Angles;

public class Vec2 {
    public double x;
    public double y;
    public Vec2(double x,double y) {this.x=x;this.y=y;}
    public double getX(){return x;}
    public double getY(){return y;}

    public Vec2 getNormalized() {
        double length = getLength();
        return new Vec2(x/length,y/length);
    }

    public double getAngle() {
        return Angles.getAngle(new Vec2(0,0),this);
    }

    public Vec2 plus(Vec2 other) {
        return new Vec2(this.x + other.x, this.y + other.y);
    }
    public Vec2 mult(double mult) {
        return new Vec2(this.x * mult, this.y * mult);
    }
    public Vec2 minus(Vec2 other) {
        return new Vec2(this.x - other.x, this.y - other.y);
    }

    public double getLength() {
        return Math.sqrt(Math.pow(x,2) + Math.pow(y,2));
    }
}
