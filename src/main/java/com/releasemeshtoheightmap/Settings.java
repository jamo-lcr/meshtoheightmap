package com.releasemeshtoheightmap;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
public class Settings {


    public String OutputfilePath;
    public File MeshFile;
    public int TexturesizeX;
    public int TexturesizeY;
    //precalculated
    public int pixelstocalculate;
    // custom settings
    public boolean SortingTriangelLists;

    public Vector3 scale;
    public Vector3 rotation;
    public Vector3 position;

    public static Settings Loadsettingsfromfile(String Filepath){
        try (BufferedReader reader = new BufferedReader(new FileReader(Filepath))) {
            String line;
            String OutputfilePath= null;
            File MeshFile= null;
            int TexturesizeX=0;
            int TexturesizeY=0;
            Vector3 pos=new Vector3(0,0,0);
            Vector3 rot=new Vector3(1,1,1);
            Vector3 scale=new Vector3(1,1,1);
            boolean SortingTriangelLists=true;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    String value = parts[1].trim();

                    switch (key) {
                        case "OutputfilePath":
                            OutputfilePath = value;
                            break;
                        case "MeshFile":
                            MeshFile = new File(value);
                            break;
                        case "TexturesizeX":
                            TexturesizeX = Integer.parseInt(value);
                            break;
                        case "TexturesizeY":
                            TexturesizeY = Integer.parseInt(value);
                            break;
                        case "SortingTriangelLists":
                            SortingTriangelLists = Boolean.parseBoolean(value);
                            break;
                        case "pos_X":
                            pos.x = Float.parseFloat(value);
                        case "pos_Y":
                            pos.y = Float.parseFloat(value);
                        case "pos_Z":
                            pos.z = Float.parseFloat(value);
                        case "rot_X":
                            rot.x = Float.parseFloat(value);
                        case "rot_Y":
                            rot.y = Float.parseFloat(value);
                        case "rot_Z":
                            rot.z = Float.parseFloat(value);
                        case "scale_X":
                            scale.x = Float.parseFloat(value);
                        case "scale_Y":
                            scale.y = Float.parseFloat(value);
                        case "scale_Z":
                            scale.z = Float.parseFloat(value);

                            break;
                        
                        default:
                            System.err.println("Unknown setting: " + key);
                    }
                    
                }
            }
            Settings settings = new Settings(MeshFile, OutputfilePath, TexturesizeX, TexturesizeY,SortingTriangelLists,scale,rot,pos);
            return settings;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        System.err.println("An Error Occourt: Loading the settingsfile"+Filepath+" Failed");
        return null;
    }
    public static void Savesettings(String Filepath, Settings settings) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(Filepath))) {
            writer.write("OutputfilePath=" + settings.OutputfilePath);
            writer.newLine();
            writer.write("MeshFile=" + settings.MeshFile.getPath());
            writer.newLine();
            writer.write("TexturesizeX=" + settings.TexturesizeX);
            writer.newLine();
            writer.write("TexturesizeY=" + settings.TexturesizeY);
            writer.newLine();
            writer.write("SortingTriangelLists=" + settings.SortingTriangelLists);
            writer.newLine();
            writer.write("pos_X=" + settings.position.x);
            writer.newLine();
            writer.write("pos_Y=" + settings.position.y);
            writer.newLine();
            writer.write("pos_Z=" + settings.position.z);
            writer.newLine();
            writer.write("rot_X=" + settings.rotation.x);
            writer.newLine();
            writer.write("rot_Y=" + settings.rotation.y);
            writer.newLine();
            writer.write("rot_Z=" + settings.rotation.z);
            writer.newLine();
            writer.write("scale_X=" + settings.scale.x);
            writer.newLine();
            writer.write("scale_Y=" + settings.scale.y);
            writer.newLine();
            writer.write("scale_Z=" + settings.scale.z);
            writer.newLine();
            
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("An Error Occourt: Saving the settingsfile"+Filepath+" Failed");
        }
    }
    public Settings (File Meshfile,String OutputfilePath,int TexturesizeX,int TexturesizeY,boolean SortingTriangelLists,Vector3 scale,Vector3 rot,Vector3 pos){
        this.OutputfilePath=OutputfilePath;
        this.MeshFile=Meshfile;
        this.TexturesizeX=TexturesizeX;
        this.TexturesizeY=TexturesizeY;
        this.pixelstocalculate = TexturesizeX*TexturesizeY;
        this.SortingTriangelLists=SortingTriangelLists;
        this.scale = scale;
        this.rotation = rot;
        this.position = pos;
    }
    public Filetype getFiletype(){
        String filetype = MeshFile.getAbsolutePath().substring(MeshFile.getAbsolutePath().lastIndexOf(".")+1);
        System.out.println("Recognised: "+filetype);
        if(filetype.equals(Filetype.obj.toString())){
            return Filetype.obj;
        }
        return Filetype.Unknow;
    }
}
