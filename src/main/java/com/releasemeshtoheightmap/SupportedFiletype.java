package com.releasemeshtoheightmap;
import java.io.File;

public interface SupportedFiletype {
    public  Mesh readmesh(File file);
    public Mesh getmesh();


}
