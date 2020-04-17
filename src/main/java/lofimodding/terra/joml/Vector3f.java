/*
 * The MIT License
 *
 * Copyright (c) 2015-2020 Richard Greenlees
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package lofimodding.terra.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.text.DecimalFormat;

/**
 * Contains the definition of a Vector comprising 3 floats and associated
 * transformations.
 *
 * @author Richard Greenlees
 * @author Kai Burjack
 * @author F. Neurath
 */
public class Vector3f implements Externalizable, Vector3fc {
    private static final long serialVersionUID = 1L;

    /**
     * The x component of the vector.
     */
    public float x;
    /**
     * The y component of the vector.
     */
    public float y;
    /**
     * The z component of the vector.
     */
    public float z;

    /**
     * Create a new {@link Vector3f} of <code>(0, 0, 0)</code>.
     */
    public Vector3f() {
    }

    /**
     * Create a new {@link Vector3f} with the given component values.
     *
     * @param x
     *          the value of x
     * @param y
     *          the value of y
     * @param z
     *          the value of z
     */
    public Vector3f(final float x, final float y, final float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    private Vector3f thisOrNew() {
        return this;
    }

    /* (non-Javadoc)
     * @see org.joml.Vector3fc#x()
     */
    @Override
    public float x() {
        return this.x;
    }

    /* (non-Javadoc)
     * @see org.joml.Vector3fc#y()
     */
    @Override
    public float y() {
        return this.y;
    }

    /* (non-Javadoc)
     * @see org.joml.Vector3fc#z()
     */
    @Override
    public float z() {
        return this.z;
    }

    /**
     * Set the x, y and z components to the supplied values.
     *
     * @param x
     *          the x component
     * @param y
     *          the y component
     * @param z
     *          the z component
     * @return this
     */
    public Vector3f set(final float x, final float y, final float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    /**
     * Add the supplied vector to this one.
     *
     * @param v
     *          the vector to add
     * @return a vector holding the result
     */
    public Vector3f add(final Vector3fc v) {
        return this.add(v, this.thisOrNew());
    }

    /* (non-Javadoc)
     * @see org.joml.Vector3fc#add(org.joml.Vector3fc, org.joml.Vector3f)
     */
    @Override
    public Vector3f add(final Vector3fc v, final Vector3f dest) {
        dest.x = this.x + v.x();
        dest.y = this.y + v.y();
        dest.z = this.z + v.z();
        return dest;
    }

    /**
     * Multiply the given matrix with this Vector3f and store the result in <code>this</code>.
     *
     * @param mat
     *          the matrix
     * @return a vector holding the result
     */
    public Vector3f mul(final Matrix3fc mat) {
        return this.mul(mat, this.thisOrNew());
    }

    /* (non-Javadoc)
     * @see org.joml.Vector3fc#mul(org.joml.Matrix3fc, org.joml.Vector3f)
     */
    @Override
    public Vector3f mul(final Matrix3fc mat, final Vector3f dest) {
        final float rx = mat.m00() * this.x + mat.m10() * this.y + mat.m20() * this.z;
        final float ry = mat.m01() * this.x + mat.m11() * this.y + mat.m21() * this.z;
        final float rz = mat.m02() * this.x + mat.m12() * this.y + mat.m22() * this.z;
        dest.x = rx;
        dest.y = ry;
        dest.z = rz;
        return dest;
    }

    /**
     * Return a string representation of this vector.
     * <p>
     * This method creates a new {@link DecimalFormat} on every invocation with the format string "<code>0.000E0;-</code>".
     *
     * @return the string representation
     */
    public String toString() {
        return "(" + this.x + ' ' + this.y + ' ' + this.z + ')';
    }

    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        out.writeFloat(this.x);
        out.writeFloat(this.y);
        out.writeFloat(this.z);
    }

    @Override
    public void readExternal(final ObjectInput in) throws IOException {
        this.x = in.readFloat();
        this.y = in.readFloat();
        this.z = in.readFloat();
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Float.floatToIntBits(this.x);
        result = prime * result + Float.floatToIntBits(this.y);
        result = prime * result + Float.floatToIntBits(this.z);
        return result;
    }

    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (this.getClass() != obj.getClass())
            return false;
        final Vector3f other = (Vector3f) obj;
        if (Float.floatToIntBits(this.x) != Float.floatToIntBits(other.x))
            return false;
        if (Float.floatToIntBits(this.y) != Float.floatToIntBits(other.y))
            return false;
        return Float.floatToIntBits(this.z) == Float.floatToIntBits(other.z);
    }
}
