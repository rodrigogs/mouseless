package com.sedentary.mouseless.commons;

/**
 * Created by Rodrigo Gomes da Silva on 29/10/2014.
 */
public class Coordinates {
    private Float x;
    private Float y;
    private Float z;

    public Coordinates() {
    }

    public Coordinates(Float x, Float y, Float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Float getX() {
        return x;
    }

    public void setX(Float x) {
        this.x = x;
    }

    public Float getY() {
        return y;
    }

    public void setY(Float y) {
        this.y = y;
    }

    public Float getZ() {
        return z;
    }

    public void setZ(Float z) {
        this.z = z;
    }
}
