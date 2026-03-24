package ZhuzheeEngine;

import java.awt.*;

public final class MathZ {
    /// find length of vector2D
    public static float Length(float x, float y) {
        return (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    /// normalize vector2 return as float[2]
    public static float[] Normalize(float x, float y) {
        float length = Length(x, y);
        return new float[]{x / length, y / length};
    }
}
