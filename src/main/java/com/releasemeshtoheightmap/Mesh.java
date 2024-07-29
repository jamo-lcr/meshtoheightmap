package com.releasemeshtoheightmap;
import java.util.Arrays;
import java.util.Comparator;

public class Mesh {
    public Vector3[] verts;
    public int[] indicies;

    public Filedata data;
    public Vector3[] extremepoints;
    public Triangel[] triangels;

    public Vector3 pos;
    public Vector3 rot;
    public Vector3 scale;
    public Mesh(Vector3[] verts,int[] indicies){
        this.verts = verts;
        this.indicies = indicies;
        data= new Filedata(verts);
        settriangels();
        this.scale = new Vector3(1,1,1);
        this.rot = new Vector3(1,1,1);
        this.pos = new Vector3(1,1,1);
    }
    public void settriangels(){
    
        if(indicies.length%3!=0){
            System.err.println("Indicies have not the right length pls check your file");
        }
        else{
            triangels = new Triangel[(int)(indicies.length/3)];
            for(int i=0;i<indicies.length/3;i++){
                Triangel triangel = new Triangel(verts[indicies[i*3]-1], verts[indicies[(i*3)+1]-1], verts[indicies[(i*3)+2]-1]);
                triangels[i]=triangel;
            }
            
        }
        
    }
    
    public static Triangel[] sorttriangelsP1tominx(Triangel[] triangels){
        Arrays.sort(triangels, Comparator.comparingDouble(x -> x.p1.x));
        return triangels;
    }
    public static Triangel[] sorttriangelsP2tominx(Triangel[] triangels){
        Arrays.sort(triangels, Comparator.comparingDouble(x -> x.p2.x));
        return triangels;
    }

    public static Triangel[] sorttriangelsP1tominy(Triangel[] triangels){
        Arrays.sort(triangels, Comparator.comparingDouble(y -> y.p1.y));
        return triangels;
    }
    public static Triangel[] sorttriangelsP2tominy(Triangel[] triangels){
        Arrays.sort(triangels, Comparator.comparingDouble(y -> y.p2.y));
        return triangels;
    }
    public void Applytransformedverts(Vector3[] verts){
        this.verts=verts;
    }
    public Vector3[] Transform(Vector3 scale, Vector3 rot, Vector3 move) {
        System.out.println(scale.debugVector(2));
        System.out.println(rot.debugVector(2));
        System.out.println(move.debugVector(2));
        return move(move, rotate(rot, scale(scale, verts)));
        //return move(move, verts);
    }
    
    private Vector3[] rotate(Vector3 rot, Vector3[] verts) {
        float rx = (float)Math.toRadians(rot.x);
        float ry = (float)Math.toRadians(rot.y);
        float rz = (float)Math.toRadians(rot.z);
    
        float cosX = (float)Math.cos(rx);
        float sinX = (float)Math.sin(rx);
        float cosY = (float)Math.cos(ry);
        float sinY = (float)Math.sin(ry);
        float cosZ = (float)Math.cos(rz);
        float sinZ = (float)Math.sin(rz);
    
        for (int i = 0; i < verts.length; i++) {
            float x = verts[i].x;
            float y = verts[i].y;
            float z = verts[i].z;
    
            // Rotate around Z axis
            float x1 = x * cosZ - y * sinZ;
            float y1 = x * sinZ + y * cosZ;
    
            // Rotate around Y axis
            float x2 = x1 * cosY + z * sinY;
            float z2 = -x1 * sinY + z * cosY;
    
            // Rotate around X axis
            float y2 = y1 * cosX - z2 * sinX;
            z2 = y1 * sinX + z2 * cosX;
    
            verts[i] = new Vector3(x2, y2, z2);
        }
        return verts;
    }
    
    private Vector3[] scale(Vector3 scale, Vector3[] verts) {
        for (int i = 0; i < verts.length; i++) {
            verts[i].x *= scale.x;
            verts[i].y *= scale.y;
            verts[i].z *= scale.z;
        }
        return verts;
    
    }
    
    private Vector3[] move(Vector3 mov, Vector3[] verts) {
        for (int i = 0; i < verts.length; i++) {
            verts[i].x += mov.x;
            verts[i].y += mov.y;
            verts[i].z += mov.z;
        }
        return verts;
    }
}
