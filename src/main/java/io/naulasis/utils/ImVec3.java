package io.naulasis.utils;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ImVec3{
    public float x, y, z;

    public void add(ImVec3 vec) { this.x += vec.x; this.y += vec.y; this.z += vec.z; }

    public void subtract(ImVec3 vec) { this.x -= vec.x; this.y -= vec.y; this.z -= vec.z; }

    public void multiply(ImVec3 vec) { this.x *= vec.x; this.y *= vec.y; this.z *= vec.z; }

    public void divide(ImVec3 vec) { this.x /= vec.x; this.y /= vec.y; this.z /= vec.z; }

    @Override
    public ImVec3 clone() { return new ImVec3(x, y, z); }

    public void set(float x, float y, float z) { this.x = x; this.y = y; this.z = z; }

    public void set(ImVec3 vec) { this.x = vec.x; this.y = vec.y; this.z = vec.z; }

    public void zero() { this.x = 0; this.y = 0; this.z = 0; }
}
