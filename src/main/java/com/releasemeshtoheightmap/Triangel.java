package com.releasemeshtoheightmap;
public class Triangel {
    Vector3 a;
    Vector3 b;
    Vector3 c;

    //for more efficent calculation
    Vector2 p1;//min
    Vector2 p2;//max

    public Triangel (Vector3 a, Vector3 b,Vector3 c){
        this.a=a;
        this.b=b;
        this.c=c;
        setbox(a, b, c);
    }
    public void setbox(Vector3 a, Vector3 b,Vector3 c){
        float minX = Math.min(Math.min(a.x,b.x), c.x);
        float minZ = Math.min(Math.min(a.z,b.z), c.z);

        float maxX = Math.max(Math.max(a.x,b.x), c.x);
        float maxZ = Math.max(Math.max(a.z,b.z), c.z);
        p1 = new Vector2(minX,minZ);
        p2 = new Vector2(maxX,maxZ);
    }

    public Vector3 calculateIntersectionPoint(Vector3 rayOrigin, Vector3 rayDirection) {
        final float EPSILON = 1e-6f;
        //System.out.println(rayOrigin.debugVector(2)+"DIR: "+rayDirection.debugVector(2));
        // Calculate edges of the triangle
        Vector3 edge1 = b.subtract(a);
        Vector3 edge2 = c.subtract(a);
    
        // Calculate the determinant
        Vector3 h = rayDirection.cross(edge2);
        float dotProduct = edge1.dot(h);
        if (dotProduct > -EPSILON && dotProduct < EPSILON) {
            return new Vector3(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);
        }
        float f = 1.0f / dotProduct;
        Vector3 s = rayOrigin.subtract(a);
        float u = f * s.dot(h);
        // If the determinant is close to zero, the ray is parallel to the triangle
        // Check if the intersection point is outside the triangle
        if (u < 0.0f || u > 1.0f) {
            //System.out.println("test1");
            return new Vector3(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);
        }
        Vector3 q = s.cross(edge1);
        float v = f * rayDirection.dot(q);
    
        // Check if the intersection point is outside the triangle
        //edited +Epsion pixel error on paralel edges
        if (v < 0.0f || u + v > 1.0f+EPSILON) {
            //System.out.println("test2");
            return new Vector3(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);
        }

        float t = f * edge2.dot(q);
        // Ray intersection
        if (t > EPSILON) {
            //System.out.println(rayOrigin.add(rayDirection.multiply(t)).debugVector(2));

            return (rayOrigin.add(rayDirection.multiply(t)));
        } else {
            // The intersection lies behind the ray origin
            return new Vector3(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);
        }
        
    }
    public String debug(boolean writeinconsole){
        if(writeinconsole==true)
        System.out.println("Triangelpoints: "+" A:"+a.debugVector(2)+" B:"+b.debugVector(2)+" C:"+c.debugVector(2));
        return "Triangelpoints: "+" A:"+a.debugVector(2)+" B:"+b.debugVector(2)+" C:"+c.debugVector(2)+"\n";
    }
    
}
