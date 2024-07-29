package com.releasemeshtoheightmap;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
public class Obj implements SupportedFiletype {
    public Mesh mesh;

    public Mesh readmesh(File file){
        List<Vector3> vertices = new ArrayList<>();
        List<Integer> indicies = new ArrayList<Integer>();
        try {
            List<String> lines = Files.readAllLines(Paths.get(file.toURI()));
            for (String line : lines) {
                if (line.startsWith("v ")) {
                    String[] content = line.split("\\s+");
                    float x = Float.parseFloat(content[1].replace(',', '.'));
                    float y = Float.parseFloat(content[2].replace(',', '.'));
                    float z = Float.parseFloat(content[3].replace(',', '.'));
                    vertices.add(new Vector3(x, y, z));
                }
                if (line.startsWith("f ")) {
                    String[] content = line.substring(2).trim().split("\\s+");
                    if (content.length == 4) { //quad
                        int indicie1 = Integer.parseInt(content[0].split("/")[0]) ;
                        int indicie2 = Integer.parseInt(content[1].split("/")[0]) ;
                        int indicie3 = Integer.parseInt(content[2].split("/")[0]) ;
                        int indicie4 = Integer.parseInt(content[3].split("/")[0]) ;
                        indicies.add(indicie1);
                        indicies.add(indicie2);
                        indicies.add(indicie3);
                        indicies.add(indicie1);
                        indicies.add(indicie3);
                        indicies.add(indicie4);
                    } else if (content.length == 3) { //triangel
                        int indicie1 = Integer.parseInt(content[0].split("/")[0]);
                        int indicie2 = Integer.parseInt(content[1].split("/")[0]);
                        int indicie3 = Integer.parseInt(content[2].split("/")[0]);
                        indicies.add(indicie1);
                        indicies.add(indicie2);
                        indicies.add(indicie3);
                    }
                }
            }
            System.out.println("readingverts Finished:");
        } catch (IOException e) {
            e.printStackTrace();
        }

        int[] intIndicesArray = indicies.stream().mapToInt(Integer::intValue).toArray();
        Mesh mesh = new Mesh(vertices.toArray(new Vector3[0]),intIndicesArray);
        
        return  mesh;
    }
    public void setmesh(Mesh m){
        this.mesh = m;
    }
    public void Debug(){
        for(int i=0;i<mesh.verts.length;i++){
            System.out.println("Verts"+i+"_"+mesh.verts[i]);
        }
        for(int i=0;i<mesh.indicies.length;i++){
            System.out.println("Indicies"+i+"_"+mesh.indicies);
        }
    }
    public Mesh getmesh(){
        return mesh;
    }

}
