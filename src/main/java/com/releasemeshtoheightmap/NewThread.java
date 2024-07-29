package com.releasemeshtoheightmap;
import java.util.Arrays;

public class NewThread extends Thread {
    float[][] yvalue;
    Mesh mesh;
    int x;
    int y;
    Settings settings;
    Vector3 scale;
    public int mode;

    public NewThread(float[][] yvaluepointer,Mesh meshpointer,int x,int y,Settings settings,Vector3 scale,int mode) {
        yvalue=yvaluepointer;
        mesh = meshpointer;
        this.x=x;
        this.y=y;
        this.settings=settings;
        this.scale=scale;
        this.mode = mode;
    }
    public void run() {
        Vector3 raydirection = new Vector3(0, -1f, 0);
        Vector3 rayorigin = new Vector3((x - (settings.TexturesizeX / 2)) / scale.x, mesh.data.maxY.y+1f,
                (y - (settings.TexturesizeY / 2)) / scale.z);
        Vector3 point = rayorigin;
        float max = Float.NEGATIVE_INFINITY;

        Triangel[] cuttedtris = null;
        if(mode==3){
            cuttedtris = cuttriangelarraysortedbyy2(cuttriangelarraysortedbyX2(mesh.triangels, point.x), point.z);
        }
        else{
            cuttedtris = mesh.triangels;
        }
            for (int i = 0; i < cuttedtris.length; i++) {
                if(cuttedtris[i]!=null){
                float intersection = cuttedtris[i].calculateIntersectionPoint(rayorigin, raydirection).y;

                if (!Float.isInfinite(intersection)) {

                    if (max <= intersection) {
                        max = intersection;
                        
                    }
                }
            }
            
        }
        
        yvalue[x][y]=(max)/mesh.data.maxY.y;
        BetaApp.progress++;

        BetaApp.showprogress();
           
    }
    public static Triangel[] cuttriangelarraysortedbyX2(Triangel[] tris, float x) {
        if (tris.length != 0) {
            Triangel[] returnarr = new Triangel[0];
            Integer index = findIndexP1x(tris, x);
            if (index != null) {
                Triangel[] cuttedarray = new Triangel[index];
                System.arraycopy(tris, 0, cuttedarray, 0, index);
                cuttedarray = Mesh.sorttriangelsP2tominx(cuttedarray);
                index = findIndexP2x(cuttedarray, x);
                if (index != null) {
                    Triangel[] newarray = new Triangel[cuttedarray.length - index];
                    System.arraycopy(cuttedarray, index, newarray, 0, newarray.length);
                    returnarr = newarray;
                }
            }
            return returnarr;
        }
        return tris;
    }
    public static Triangel[] cuttriangelarraysortedbyy2(Triangel[] tris, float y) {
        if (tris.length != 0) {
            // Sort triangles by the P1 y-coordinate
            Mesh.sorttriangelsP1tominy(tris);
    
            Triangel[] returnarr = new Triangel[0];
            Integer index = findIndexP1y(tris, y);
            if (index != null) {
                Triangel[] cuttedarray = new Triangel[index];
                System.arraycopy(tris, 0, cuttedarray, 0, index);                
                cuttedarray = Mesh.sorttriangelsP2tominy(cuttedarray);
                index = findIndexP2y(cuttedarray, y);
                if (index != null) {
                    Triangel[] newarray = new Triangel[cuttedarray.length - index];
                    System.arraycopy(cuttedarray, index, newarray, 0, newarray.length);
                    returnarr = newarray;
                }
            }
            return returnarr;
        }
        return tris;
    }

    public static Triangel[] cuttriangelarraysortedbyy3(Triangel[] tris, float y) {
    if (tris.length == 0) return tris;
    // Sort triangles by the P1 y-coordinate
    Mesh.sorttriangelsP1tominy(tris);
    Integer index = findIndexP1y(tris, y);
    Triangel[] cuttedarray = Arrays.copyOfRange(tris, 0, index);
    Mesh.sorttriangelsP2tominy(cuttedarray);
    index = findIndexP2y(cuttedarray, y);
    return Arrays.copyOfRange(cuttedarray, index, cuttedarray.length);
}


    private static Integer findIndexP1x(Triangel[] tris, float value) {
        if(tris.length==0){
            return null;
        }
        int left = 0;
        int right = tris.length - 1;
        int index = 0;
        while (left <= right) {
            index = left + ((right - left) / 2);
                if (tris[index].p1.x < value) {
                    left = index + 1;
                } else {
                    right = index - 1;
                }
        }
        if (tris[index].p1.x != value && value != 0) {
            index--;
        }
        return left;
    }
    private static Integer findIndexP2x(Triangel[] tris, float value) {
        if(tris.length==0){
            return null;
        }
        int left = 0;
        int right = tris.length - 1;
        int index = 0;
        while (left <= right) {
            index = left + ((right - left) / 2);
                if (tris[index].p2.x < value) {
                    left = index + 1;
                } else {
                    right = index - 1;
                }
        }
        if (tris[index].p2.x != value && value != 0) {
            index--;
        }
        return left;
    }

    private static Integer findIndexP1y(Triangel[] tris, float value) {
        if(tris.length==0){
            return null;
        }
        int left = 0;
        int right = tris.length - 1;
        int index = 0;
        while (left <= right) {
            index = left + ((right - left) / 2);
                if (tris[index].p1.y < value) {
                    left = index + 1;
                } else {
                    right = index - 1;
                }
        }
        if (tris[index].p1.y != value && value != 0) {
            index--;
        }
        return left;
    }
    private static Integer findIndexP2y(Triangel[] tris, float value) {
        if(tris.length==0){
            return null;
        }
        int left = 0;
        int right = tris.length - 1;
        int index = 0;
        while (left <= right) {
            index = left + ((right - left) / 2);
                if (tris[index].p2.y < value) {
                    left = index + 1;
                } else {
                    right = index - 1;
                }
        }
        if (tris[index].p2.y != value && value != 0) {
            index--;
        }
        return left;
    }

}
