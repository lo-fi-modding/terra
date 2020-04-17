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
import java.text.NumberFormat;

/**
 * Contains the definition of a 3x3 matrix of floats, and associated functions to transform
 * it. The matrix is column-major to match OpenGL's interpretation, and it looks like this:
 * <p>
 *      m00  m10  m20<br>
 *      m01  m11  m21<br>
 *      m02  m12  m22<br>
 *
 * @author Richard Greenlees
 * @author Kai Burjack
 */
public class Matrix3f implements Externalizable, Matrix3fc {
    private static final long serialVersionUID = 1L;

    public float m00, m01, m02;
    public float m10, m11, m12;
    public float m20, m21, m22;

    /**
     * Create a new {@link Matrix3f} and set it to identity.
     */
    public Matrix3f() {
        this.m00 = 1.0f;
        this.m11 = 1.0f;
        this.m22 = 1.0f;
    }

    /* (non-Javadoc)
     * @see org.joml.Matrix3fc#m00()
     */
    @Override
    public float m00() {
        return this.m00;
    }
    /* (non-Javadoc)
     * @see org.joml.Matrix3fc#m01()
     */
    @Override
    public float m01() {
        return this.m01;
    }
    /* (non-Javadoc)
     * @see org.joml.Matrix3fc#m02()
     */
    @Override
    public float m02() {
        return this.m02;
    }
    /* (non-Javadoc)
     * @see org.joml.Matrix3fc#m10()
     */
    @Override
    public float m10() {
        return this.m10;
    }
    /* (non-Javadoc)
     * @see org.joml.Matrix3fc#m11()
     */
    @Override
    public float m11() {
        return this.m11;
    }
    /* (non-Javadoc)
     * @see org.joml.Matrix3fc#m12()
     */
    @Override
    public float m12() {
        return this.m12;
    }
    /* (non-Javadoc)
     * @see org.joml.Matrix3fc#m20()
     */
    @Override
    public float m20() {
        return this.m20;
    }
    /* (non-Javadoc)
     * @see org.joml.Matrix3fc#m21()
     */
    @Override
    public float m21() {
        return this.m21;
    }
    /* (non-Javadoc)
     * @see org.joml.Matrix3fc#m22()
     */
    @Override
    public float m22() {
        return this.m22;
    }

    /**
     * Return a string representation of this matrix.
     * <p>
     * This method creates a new {@link DecimalFormat} on every invocation with the format string "<code>0.000E0;-</code>".
     *
     * @return the string representation
     */
    public String toString() {
        final DecimalFormat formatter = new DecimalFormat(" 0.000E0;-");
        final String str = this.toString(formatter);
        final StringBuffer res = new StringBuffer();
        int eIndex = Integer.MIN_VALUE;
        for (int i = 0; i < str.length(); i++) {
            final char c = str.charAt(i);
            if (c == 'E') {
                eIndex = i;
            } else if (c == ' ' && eIndex == i - 1) {
                // workaround Java 1.4 DecimalFormat bug
                res.append('+');
                continue;
            } else if (Character.isDigit(c) && eIndex == i - 1) {
                res.append('+');
            }
            res.append(c);
        }
        return res.toString();
    }

    /**
     * Return a string representation of this matrix by formatting the matrix elements with the given {@link NumberFormat}.
     *
     * @param formatter
     *          the {@link NumberFormat} used to format the matrix values with
     * @return the string representation
     */
    public String toString(final NumberFormat formatter) {
        return formatter.format(this.m00) + " " + formatter.format(this.m10) + " " + formatter.format(this.m20) + "\n"
             + formatter.format(this.m01) + " " + formatter.format(this.m11) + " " + formatter.format(this.m21) + "\n"
             + formatter.format(this.m02) + " " + formatter.format(this.m12) + " " + formatter.format(this.m22) + "\n";
    }

    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        out.writeFloat(this.m00);
        out.writeFloat(this.m01);
        out.writeFloat(this.m02);
        out.writeFloat(this.m10);
        out.writeFloat(this.m11);
        out.writeFloat(this.m12);
        out.writeFloat(this.m20);
        out.writeFloat(this.m21);
        out.writeFloat(this.m22);
    }

    @Override
    public void readExternal(final ObjectInput in) throws IOException {
        this.m00 = in.readFloat();
        this.m01 = in.readFloat();
        this.m02 = in.readFloat();
        this.m10 = in.readFloat();
        this.m11 = in.readFloat();
        this.m12 = in.readFloat();
        this.m20 = in.readFloat();
        this.m21 = in.readFloat();
        this.m22 = in.readFloat();
    }

    /**
     * Apply rotation of <code>angleX</code> radians about the X axis, followed by a rotation of <code>angleY</code> radians about the Y axis and
     * followed by a rotation of <code>angleZ</code> radians about the Z axis.
     * <p>
     * When used with a right-handed coordinate system, the produced rotation will rotate a vector
     * counter-clockwise around the rotation axis, when viewing along the negative axis direction towards the origin.
     * When used with a left-handed coordinate system, the rotation is clockwise.
     * <p>
     * If <code>M</code> is <code>this</code> matrix and <code>R</code> the rotation matrix,
     * then the new matrix will be <code>M * R</code>. So when transforming a
     * vector <code>v</code> with the new matrix by using <code>M * R * v</code>, the
     * rotation will be applied first!
     * <p>
     * This method is equivalent to calling: <code>rotateX(angleX).rotateY(angleY).rotateZ(angleZ)</code>
     *
     * @param angleX
     *            the angle to rotate about X
     * @param angleY
     *            the angle to rotate about Y
     * @param angleZ
     *            the angle to rotate about Z
     * @return this
     */
    public Matrix3f rotateXYZ(final float angleX, final float angleY, final float angleZ) {
        return this.rotateXYZ(angleX, angleY, angleZ, this);
    }

    /* (non-Javadoc)
     * @see org.joml.Matrix3fc#rotateXYZ(float, float, float, org.joml.Matrix3f)
     */
    @Override
    public Matrix3f rotateXYZ(final float angleX, final float angleY, final float angleZ, final Matrix3f dest) {
        final float sinX = (float) Math.sin(angleX);
        final float cosX = (float) Math.cos(angleX);
        final float sinY = (float) Math.sin(angleY);
        final float cosY = (float) Math.cos(angleY);
        final float sinZ = (float) Math.sin(angleZ);
        final float cosZ = (float) Math.cos(angleZ);
        final float m_sinX = -sinX;
        final float m_sinY = -sinY;
        final float m_sinZ = -sinZ;

        // rotateX
        final float nm10 = this.m10 * cosX + this.m20 * sinX;
        final float nm11 = this.m11 * cosX + this.m21 * sinX;
        final float nm12 = this.m12 * cosX + this.m22 * sinX;
        final float nm20 = this.m10 * m_sinX + this.m20 * cosX;
        final float nm21 = this.m11 * m_sinX + this.m21 * cosX;
        final float nm22 = this.m12 * m_sinX + this.m22 * cosX;
        // rotateY
        final float nm00 = this.m00 * cosY + nm20 * m_sinY;
        final float nm01 = this.m01 * cosY + nm21 * m_sinY;
        final float nm02 = this.m02 * cosY + nm22 * m_sinY;
        dest.m20 = this.m00 * sinY + nm20 * cosY;
        dest.m21 = this.m01 * sinY + nm21 * cosY;
        dest.m22 = this.m02 * sinY + nm22 * cosY;
        // rotateZ
        dest.m00 = nm00 * cosZ + nm10 * sinZ;
        dest.m01 = nm01 * cosZ + nm11 * sinZ;
        dest.m02 = nm02 * cosZ + nm12 * sinZ;
        dest.m10 = nm00 * m_sinZ + nm10 * cosZ;
        dest.m11 = nm01 * m_sinZ + nm11 * cosZ;
        dest.m12 = nm02 * m_sinZ + nm12 * cosZ;
        return dest;
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Float.floatToIntBits(this.m00);
        result = prime * result + Float.floatToIntBits(this.m01);
        result = prime * result + Float.floatToIntBits(this.m02);
        result = prime * result + Float.floatToIntBits(this.m10);
        result = prime * result + Float.floatToIntBits(this.m11);
        result = prime * result + Float.floatToIntBits(this.m12);
        result = prime * result + Float.floatToIntBits(this.m20);
        result = prime * result + Float.floatToIntBits(this.m21);
        result = prime * result + Float.floatToIntBits(this.m22);
        return result;
    }

    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (this.getClass() != obj.getClass())
            return false;
        final Matrix3f other = (Matrix3f) obj;
        if (Float.floatToIntBits(this.m00) != Float.floatToIntBits(other.m00))
            return false;
        if (Float.floatToIntBits(this.m01) != Float.floatToIntBits(other.m01))
            return false;
        if (Float.floatToIntBits(this.m02) != Float.floatToIntBits(other.m02))
            return false;
        if (Float.floatToIntBits(this.m10) != Float.floatToIntBits(other.m10))
            return false;
        if (Float.floatToIntBits(this.m11) != Float.floatToIntBits(other.m11))
            return false;
        if (Float.floatToIntBits(this.m12) != Float.floatToIntBits(other.m12))
            return false;
        if (Float.floatToIntBits(this.m20) != Float.floatToIntBits(other.m20))
            return false;
        if (Float.floatToIntBits(this.m21) != Float.floatToIntBits(other.m21))
            return false;
        return Float.floatToIntBits(this.m22) == Float.floatToIntBits(other.m22);
    }
}
