/*
 * $RCSfile$
 *
 * Copyright 1997-2008 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 *
 * $Revision$
 * $Date$
 * $State$
 */

package javax.media.j3d;

/**
 * Scale interpolation behavior.  This class defines a behavior
 * that modifies the uniform scale component of its target
 * TransformGroup by linearly interpolating between a pair of
 * specified scale values (using the value generated by the
 * specified Alpha object).  The interpolated scale value is
 * used to generate a scale transform in the local coordinate
 * system of this interpolator.
 */

public class ScaleInterpolator extends TransformInterpolator {

    float minimumScale;
    float maximumScale;
    private Transform3D scale = new Transform3D();


    // We can't use a boolean flag since it is possible
    // that after alpha change, this procedure only run
    // once at alpha.finish(). So the best way is to
    // detect alpha value change.
    private float prevAlphaValue = Float.NaN;
    private WakeupCriterion passiveWakeupCriterion =
    (WakeupCriterion) new WakeupOnElapsedFrames(0, true);

    // non-public, default constructor used by cloneNode
    ScaleInterpolator() {
    }

    /**
      * Constructs a trivial scale interpolator that varies its target
      * TransformGroup node between the two specified alpha values
      * using the specified alpha, an identity matrix,
      * a minimum scale = 0.1f, and a maximum scale = 1.0f.
      * @param alpha the alpha object for this interpolator
      * @param target the TransformGroup node affected by this interpolator
      */
    public ScaleInterpolator(Alpha alpha,
			     TransformGroup target) {

	super(alpha, target);
	this.minimumScale = 0.1f;
	this.maximumScale = 1.0f;
    }

    /**
      * Constructs a new scaleInterpolator object that varies its target
      * TransformGroup node's scale component between two scale values
      * (minimumScale and maximumScale).
      * @param alpha the alpha object for this interpolator
      * @param target the TransformGroup node affected by this interpolator
      * @param axisOfTransform the transform that defines the local coordinate
      * system in which this interpolator operates; the scale is done
      * about the origin of this local coordinate system.
      * @param minimumScale the starting scale
      * @param maximumScale the ending scale
      */
    public ScaleInterpolator(Alpha alpha,
			     TransformGroup target,
			     Transform3D axisOfTransform,
			     float minimumScale,
			     float maximumScale) {

	super(alpha, target, axisOfTransform);

	this.minimumScale = minimumScale;
	this.maximumScale = maximumScale;
    }

    /**
      * This method sets the minimumScale for this interpolator.
      * @param scale The new minimal scale
      */
    public void setMinimumScale(float scale) {
	this.minimumScale = scale;
    }

    /**
      * This method retrieves this interpolator's minimumScale.
      * @return the interpolator's minimal scale value
      */
    public float getMinimumScale() {
	return this.minimumScale;
    }

    /**
      * This method sets the maximumScale for this interpolator.
      * @param scale the new maximum scale
      */
    public void setMaximumScale(float scale) {
	this.maximumScale = scale;
    }

    /**
      * This method retrieves this interpolator's maximumScale.
      * @return the interpolator's maximum scale vslue
      */
    public float getMaximumScale() {
	return this.maximumScale;
    }

    /**
     * @deprecated As of Java 3D version 1.3, replaced by
     * <code>TransformInterpolator.setTransformAxis(Transform3D)</code>
     */
    public void setAxisOfScale(Transform3D axisOfScale) {
        setTransformAxis(axisOfScale);
    }

     /**
     * @deprecated As of Java 3D version 1.3, replaced by
     * <code>TransformInterpolator.getTransformAxis()</code>
     */
    public Transform3D getAxisOfScale() {
        return getTransformAxis();
    }


    /**
     * Computes the new transform for this interpolator for a given
     * alpha value.
     *
     * @param alphaValue alpha value between 0.0 and 1.0
     * @param transform object that receives the computed transform for
     * the specified alpha value
     *
     * @since Java 3D 1.3
     */
    public void computeTransform(float alphaValue, Transform3D transform) {

	double val = (1.0-alphaValue)*minimumScale + alphaValue*maximumScale;

	// construct a Transform3D from:  axis  * scale * axisInverse
	scale.set(val);
	transform.mul(axis, scale);
	transform.mul(transform, axisInverse);
    }

    /**
     * Used to create a new instance of the node.  This routine is called
     * by <code>cloneTree</code> to duplicate the current node.
     * @param forceDuplicate when set to <code>true</code>, causes the
     *  <code>duplicateOnCloneTree</code> flag to be ignored.  When
     *  <code>false</code>, the value of each node's
     *  <code>duplicateOnCloneTree</code> variable determines whether
     *  NodeComponent data is duplicated or copied.
     *
     * @see Node#cloneTree
     * @see Node#cloneNode
     * @see Node#duplicateNode
     * @see NodeComponent#setDuplicateOnCloneTree
     */
    public Node cloneNode(boolean forceDuplicate) {
        ScaleInterpolator si = new ScaleInterpolator();
        si.duplicateNode(this, forceDuplicate);
        return si;
    }


   /**
     * Copies all ScaleInterpolator information from
     * <code>originalNode</code> into
     * the current node.  This method is called from the
     * <code>cloneNode</code> method which is, in turn, called by the
     * <code>cloneTree</code> method.<P>
     *
     * @param originalNode the original node to duplicate.
     * @param forceDuplicate when set to <code>true</code>, causes the
     *  <code>duplicateOnCloneTree</code> flag to be ignored.  When
     *  <code>false</code>, the value of each node's
     *  <code>duplicateOnCloneTree</code> variable determines whether
     *  NodeComponent data is duplicated or copied.
     *
     * @exception RestrictedAccessException if this object is part of a live
     *  or compiled scenegraph.
     *
     * @see Node#duplicateNode
     * @see Node#cloneTree
     * @see NodeComponent#setDuplicateOnCloneTree
     */
    void duplicateAttributes(Node originalNode, boolean forceDuplicate) {

        super.duplicateAttributes(originalNode, forceDuplicate);

	ScaleInterpolator si = (ScaleInterpolator) originalNode;

        setMinimumScale(si.getMinimumScale());
        setMaximumScale(si.getMaximumScale());

    }
}
