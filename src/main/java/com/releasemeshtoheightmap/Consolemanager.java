package com.releasemeshtoheightmap;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Scanner;

public class Consolemanager {
    public static Scanner scanner=new Scanner(System.in);
    public static String SettingsFolderpath="\\Settings\\";
    public static final String programminfos="...Infos..."; 
    public static final String asktoimportsettings="do you want to import Settings from a file?(Y/N)";

    public static void ShowProgramminfos(){
        System.out.println(programminfos);
    }
    public static boolean Showasktoimportsettings(){
        System.out.println(asktoimportsettings);
        String input;
        input = scanner.nextLine().trim();
        if (input.equalsIgnoreCase("Y")) {
            System.out.println("Import Settings!");
            return true;
        } else {
            System.out.println("Create new Settings!");
            return false;
        }
    }
    public static boolean AsktoloadGui(){
        System.out.println("Last chance Open GUI?(Y/N)");
        String input;
        input = scanner.nextLine().trim();
        if (input.equalsIgnoreCase("Y")) {
            System.out.println("Load Gui");
            return true;
        } else {
            System.out.println("Use Console");
            return false;
        }
    }
    public static int CalculateonCpu(){
        System.out.println("CalculateonCpu(very slow use only if gpu doesnt work)?(Y/N)");
        String input;
        input = scanner.nextLine().trim();
        if (input.equalsIgnoreCase("Y")) {
            System.out.println("Calculateoncpu");
            System.out.println("SortingLists(Enter number:\n 0 for no Listsorting\n 1 for x-coord.sorting\n 2 for y-and-x-coord.sorting)");
            if (input.equalsIgnoreCase("0")) {
                return 1;
            }
            if (input.equalsIgnoreCase("1")) {
                return 2;
            }
            if (input.equalsIgnoreCase("2")) {
                return 3;
            }
            if(!input.equalsIgnoreCase("2")||input.equalsIgnoreCase("1")||input.equalsIgnoreCase("0")){
                CalculateonCpu();
            }
        } else {
            System.out.println("CalculateonGpu");
            return 0;
        }
        return -1;
    }
    public static Settings Loadsettingsfromfile(){
        while (true) {
            System.out.println("Enter the FilePath of the Settingsfile(+name)");
            String Settingsfilepath = scanner.nextLine();
            File file = new File(Settingsfilepath);
            if (file.exists()) {
                System.out.println("Good job! the File exists.");
                return Settings.Loadsettingsfromfile(Settingsfilepath);
            }
            System.out.println("Check your filepath");
        }
    }
    public static void Savesettings(Settings settings){
        scanner=new Scanner(System.in);
        System.out.println("Savesettings?(Y/N)");
        String Savesettings = scanner.nextLine().trim();
        if (Savesettings.equalsIgnoreCase("Y")) {
            while (true) {
                System.out.println("Enterfilename (and evtally a custom Filepath)");
                String SettingsFilepath = scanner.nextLine();
                if(SettingsFilepath.isEmpty()==false){
                    File file = new File(System.getProperty("user.dir")+SettingsFolderpath+SettingsFilepath);
                    if (file.exists()==false) {
                        Settings.Savesettings(System.getProperty("user.dir")+SettingsFolderpath+SettingsFilepath, settings);
                        break;
                    }
                }
                System.out.println("Check your filename/location");
            }
        }
        else{
            //skip
            return;
        }

        
    }
    public static Settings SetnewSettings(){
        int xsize;
        int ysize;
        File Meshfile;
        String Outputfolder;
        while (true) {
            System.out.println("Enter the FilePath of the Meshfile(Obj,)");
            String MeshFilepath = scanner.nextLine();
            File file = new File(MeshFilepath);
            if (file.exists()) {
                System.out.println("Good job! the File exists.");
                Meshfile = file;
                break;
            }
            System.out.println("Check your filepath");
        }
        System.out.println("Enter the Texturesize");
        while (true) {

            try {
                System.out.println("Enter the X-size of the image:");
                int input = scanner.nextInt();
                xsize = input;
                break;
            } catch (NumberFormatException e) {
                System.out.println("plesase Enter an Intager as the X value");
            }
        }
        while (true) {
            try {
                System.out.println("Enter the Y-size of the image:");
                int input = scanner.nextInt();
                ysize = input;
                break;
            } catch (NumberFormatException e) {
                System.out.println("plesase Enter an Intager as the X value");
            }
        }
        Outputfolder =  System.getProperty("user.dir")+"\\Output\\";
        Settings settings= new Settings(Meshfile, Outputfolder, xsize, ysize,true,new Vector3(1,1,1),new Vector3(1,1,1),new Vector3(0,0,0));
        return settings;
    }
    public static void ShowProgressbar(int current, int total) {
        int barLength = 50; // Length of the progress bar
        float progress = ((float)current / (float)total);
        int progressBarFilled = (int) (barLength * progress);
    
        StringBuilder bar = new StringBuilder("[");
        for (int i = 0; i < barLength; i++) {
            if (i < progressBarFilled) {
                bar.append("=");
            } else {
                bar.append(" ");
            }
        }
        bar.append("] ").append(String.format("%d%%", (int) (progress * 100))+"  "+current+"/"+total);
    
        // Print the progress bar and carriage return to overwrite the line
        System.out.print("\r" + bar.toString());

    }
    public static boolean Addinputintime(int millisounds) {
        long starttime=new Date().getTime();
        long time =new Date().getTime();
        System.out.println("Gui will load in:"+millisounds+" milliscounds if you want to use the console press any key");
        while(time-starttime<millisounds){
            time =new Date().getTime();
            try {
                if (System.in.available() != 0) {
                    System.in.read();
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
    public static void Setui(String[] args){
        if(Consolemanager.Addinputintime(3000)==true){
            Gui.startGui(args);
        }
        if(Consolemanager.AsktoloadGui()==true){
            Gui.startGui(args);
        }
    }
}
