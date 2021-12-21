package io.github.foundationgames.jsonem.util;

import net.minecraft.client.util.math.Vector2f;

/**
 * Implementation of {@code equals()} on {@link Vector2f}
 */
public class Vector2fComparable extends Vector2f {
    public Vector2fComparable(float x, float y) {
        super(x, y);
    }

    public static Vector2fComparable of(Vector2f vec) {
        return new Vector2fComparable(vec.getX(), vec.getY());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Vector2f vec) {
            return this.getX() == vec.getX() && this.getY() == vec.getY();
        }
        return super.equals(obj);
    }
}
