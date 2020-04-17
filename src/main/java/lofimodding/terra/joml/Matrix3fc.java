/*
 * The MIT License
 *
 * Copyright (c) 2016-2020 JOML
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

/**
 * Interface to a read-only view of a 3x3 matrix of single-precision floats.
 *
 * @author Kai Burjack
 */
public interface Matrix3fc {
    /**
     * Return the value of the matrix element at column 0 and row 0.
     *
     * @return the value of the matrix element
     */
    float m00();

    /**
     * Return the value of the matrix element at column 0 and row 1.
     *
     * @return the value of the matrix element
     */
    float m01();

    /**
     * Return the value of the matrix element at column 0 and row 2.
     *
     * @return the value of the matrix element
     */
    float m02();

    /**
     * Return the value of the matrix element at column 1 and row 0.
     *
     * @return the value of the matrix element
     */
    float m10();

    /**
     * Return the value of the matrix element at column 1 and row 1.
     *
     * @return the value of the matrix element
     */
    float m11();

    /**
     * Return the value of the matrix element at column 1 and row 2.
     *
     * @return the value of the matrix element
     */
    float m12();

    /**
     * Return the value of the matrix element at column 2 and row 0.
     *
     * @return the value of the matrix element
     */
    float m20();

    /**
     * Return the value of the matrix element at column 2 and row 1.
     *
     * @return the value of the matrix element
     */
    float m21();

    /**
     * Return the value of the matrix element at column 2 and row 2.
     *
     * @return the value of the matrix element
     */
    float m22();

    /**
     * Apply rotation of <code>angleX</code> radians about the X axis, followed by a rotation of <code>angleY</code> radians about the Y axis and
     * followed by a rotation of <code>angleZ</code> radians about the Z axis and store the result in <code>dest</code>.
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
     * This method is equivalent to calling: <code>rotateX(angleX, dest).rotateY(angleY).rotateZ(angleZ)</code>
     *
     * @param angleX
     *            the angle to rotate about X
     * @param angleY
     *            the angle to rotate about Y
     * @param angleZ
     *            the angle to rotate about Z
     * @param dest
     *            will hold the result
     * @return dest
     */
    Matrix3f rotateXYZ(float angleX, float angleY, float angleZ, Matrix3f dest);
}
