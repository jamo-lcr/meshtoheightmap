package com.releasemeshtoheightmap;
public class Filedata {
    Vector3 minX;
    Vector3 minY;
    Vector3 minZ;

    Vector3 maxX;
    Vector3 maxY;
    Vector3 maxZ;

    public Filedata(Vector3[] verts){
        minX=new Vector3();
        minY=new Vector3();
        minZ=new Vector3();

        maxX=new Vector3();
        maxY=new Vector3();
        maxZ=new Vector3();
        calculateextremepoints(verts);
    }
    public void calculateextremepoints(Vector3[] verts){
        for (Vector3 vert : verts) {
            if (vert.x < minX.x) {
                minX = vert;
            } 
            if (vert.x > maxX.x) {
                maxX = vert;
            } 
            if (vert.y < minY.y) {
                minY = vert;
            } 
            if (vert.y > maxY.y) {
                maxY = vert;
            } 
            if (vert.z < minZ.z) {
                minZ = vert;
            } 
            if (vert.z > maxZ.z) {
                maxZ = vert;
            }
        }
    }
    public void debug() {
        System.out.println("minX: " + minX.debugVector(2));
        System.out.println("maxX: " + maxX.debugVector(2));
        System.out.println("minY: " + minY.debugVector(2));
        System.out.println("maxY: " + maxY.debugVector(2));
        System.out.println("minZ: " + minZ.debugVector(2));
        System.out.println("maxZ: " + maxZ.debugVector(2));
    }

}
