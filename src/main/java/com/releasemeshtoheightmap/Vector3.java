package com.releasemeshtoheightmap;
public class Vector3 {
    float x;
    float y;
    float z;
    Vector3(float x,float y,float z){
        this.x =x;
        this.y =y;
        this.z =z;
    }
    Vector3(){
        this.x =0;
        this.y =0;
        this.z =0;
    }
    public String debugVector(int mode){
        if(mode ==0){
            System.out.println("X:"+x);
            System.out.println("Y:"+y);
            System.out.println("Z:"+z);
            return null;
        }
        if(mode ==1){
            System.out.println();
            System.out.print("X:"+x);
            System.out.print("Y:"+y);
            System.out.print("Z:"+z);
            System.out.println();
            return null;
        }
        if(mode==2){
            return "X:"+x+"Y:"+y+"Z:"+z;
        }
        return null;
    }
    public String getString(){
        String string = new String(x+" "+y+" "+z);

        return string;
    }


    Vector3 subtract(Vector3 v) {
        return new Vector3(this.x - v.x, this.y - v.y, this.z - v.z);
    }

    Vector3 cross(Vector3 v) {
        return new Vector3(
            this.y * v.z - this.z * v.y,
            this.z * v.x - this.x * v.z,
            this.x * v.y - this.y * v.x
        );
    }
    float dot(Vector3 v) {
        return this.x * v.x + this.y * v.y + this.z * v.z;
    }

    Vector3 multiply(float scalar) {
        return new Vector3(this.x * scalar, this.y * scalar, this.z * scalar);
    }
    Vector3 add(Vector3 v) {
        return new Vector3(this.x + v.x, this.y + v.y, this.z + v.z);
    }
    public static Vector3 parse(String s) {
        if (s == null || s.trim().isEmpty()) {
            throw new IllegalArgumentException("Input string is null or empty");
        }

        String[] parts = s.trim().split("\\s+");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Input string must contain exactly three components separated by spaces");
        }

        try {
            float x = Float.parseFloat(parts[0]);
            float y = Float.parseFloat(parts[1]);
            float z = Float.parseFloat(parts[2]);
            return new Vector3(x, y, z);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Input string contains non-numeric values", e);
        }
    }
}
