/**
 */
package de.imotep.featuremodel.variability.metamodel.FeatureModel.impl;

import de.imotep.featuremodel.variability.metamodel.FeatureModel.ExcludeConstraint;
import de.imotep.featuremodel.variability.metamodel.FeatureModel.Feature;
import de.imotep.featuremodel.variability.metamodel.FeatureModel.FeatureModelPackage;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Exclude Constraint</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.impl.ExcludeConstraintImpl#getExcludedFeatureA <em>Excluded Feature A</em>}</li>
 *   <li>{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.impl.ExcludeConstraintImpl#getExcludedFeatureB <em>Excluded Feature B</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ExcludeConstraintImpl extends ConstraintImpl implements ExcludeConstraint {
	/**
	 * The cached value of the '{@link #getExcludedFeatureA() <em>Excluded Feature A</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExcludedFeatureA()
	 * @generated
	 * @ordered
	 */
	protected Feature excludedFeatureA;

	/**
	 * The cached value of the '{@link #getExcludedFeatureB() <em>Excluded Feature B</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExcludedFeatureB()
	 * @generated
	 * @ordered
	 */
	protected Feature excludedFeatureB;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ExcludeConstraintImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return FeatureModelPackage.Literals.EXCLUDE_CONSTRAINT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Feature getExcludedFeatureA() {
		if (excludedFeatureA != null && excludedFeatureA.eIsProxy()) {
			InternalEObject oldExcludedFeatureA = (InternalEObject)excludedFeatureA;
			excludedFeatureA = (Feature)eResolveProxy(oldExcludedFeatureA);
			if (excludedFeatureA != oldExcludedFeatureA) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, FeatureModelPackage.EXCLUDE_CONSTRAINT__EXCLUDED_FEATURE_A, oldExcludedFeatureA, excludedFeatureA));
			}
		}
		return excludedFeatureA;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Feature basicGetExcludedFeatureA() {
		return excludedFeatureA;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetExcludedFeatureA(Feature newExcludedFeatureA, NotificationChain msgs) {
		Feature oldExcludedFeatureA = excludedFeatureA;
		excludedFeatureA = newExcludedFeatureA;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, FeatureModelPackage.EXCLUDE_CONSTRAINT__EXCLUDED_FEATURE_A, oldExcludedFeatureA, newExcludedFeatureA);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setExcludedFeatureA(Feature newExcludedFeatureA) {
		if (newExcludedFeatureA != excludedFeatureA) {
			NotificationChain msgs = null;
			if (excludedFeatureA != null)
				msgs = ((InternalEObject)excludedFeatureA).eInverseRemove(this, FeatureModelPackage.FEATURE__EXCLUDE_CONSTRAINTS_A, Feature.class, msgs);
			if (newExcludedFeatureA != null)
				msgs = ((InternalEObject)newExcludedFeatureA).eInverseAdd(this, FeatureModelPackage.FEATURE__EXCLUDE_CONSTRAINTS_A, Feature.class, msgs);
			msgs = basicSetExcludedFeatureA(newExcludedFeatureA, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, FeatureModelPackage.EXCLUDE_CONSTRAINT__EXCLUDED_FEATURE_A, newExcludedFeatureA, newExcludedFeatureA));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Feature getExcludedFeatureB() {
		if (excludedFeatureB != null && excludedFeatureB.eIsProxy()) {
			InternalEObject oldExcludedFeatureB = (InternalEObject)excludedFeatureB;
			excludedFeatureB = (Feature)eResolveProxy(oldExcludedFeatureB);
			if (excludedFeatureB != oldExcludedFeatureB) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, FeatureModelPackage.EXCLUDE_CONSTRAINT__EXCLUDED_FEATURE_B, oldExcludedFeatureB, excludedFeatureB));
			}
		}
		return excludedFeatureB;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Feature basicGetExcludedFeatureB() {
		return excludedFeatureB;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetExcludedFeatureB(Feature newExcludedFeatureB, NotificationChain msgs) {
		Feature oldExcludedFeatureB = excludedFeatureB;
		excludedFeatureB = newExcludedFeatureB;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, FeatureModelPackage.EXCLUDE_CONSTRAINT__EXCLUDED_FEATURE_B, oldExcludedFeatureB, newExcludedFeatureB);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setExcludedFeatureB(Feature newExcludedFeatureB) {
		if (newExcludedFeatureB != excludedFeatureB) {
			NotificationChain msgs = null;
			if (excludedFeatureB != null)
				msgs = ((InternalEObject)excludedFeatureB).eInverseRemove(this, FeatureModelPackage.FEATURE__EXCLUDE_CONSTRAINTS_B, Feature.class, msgs);
			if (newExcludedFeatureB != null)
				msgs = ((InternalEObject)newExcludedFeatureB).eInverseAdd(this, FeatureModelPackage.FEATURE__EXCLUDE_CONSTRAINTS_B, Feature.class, msgs);
			msgs = basicSetExcludedFeatureB(newExcludedFeatureB, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, FeatureModelPackage.EXCLUDE_CONSTRAINT__EXCLUDED_FEATURE_B, newExcludedFeatureB, newExcludedFeatureB));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case FeatureModelPackage.EXCLUDE_CONSTRAINT__EXCLUDED_FEATURE_A:
				if (excludedFeatureA != null)
					msgs = ((InternalEObject)excludedFeatureA).eInverseRemove(this, FeatureModelPackage.FEATURE__EXCLUDE_CONSTRAINTS_A, Feature.class, msgs);
				return basicSetExcludedFeatureA((Feature)otherEnd, msgs);
			case FeatureModelPackage.EXCLUDE_CONSTRAINT__EXCLUDED_FEATURE_B:
				if (excludedFeatureB != null)
					msgs = ((InternalEObject)excludedFeatureB).eInverseRemove(this, FeatureModelPackage.FEATURE__EXCLUDE_CONSTRAINTS_B, Feature.class, msgs);
				return basicSetExcludedFeatureB((Feature)otherEnd, msgs);
		}
		return super.eInverseAdd(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case FeatureModelPackage.EXCLUDE_CONSTRAINT__EXCLUDED_FEATURE_A:
				return basicSetExcludedFeatureA(null, msgs);
			case FeatureModelPackage.EXCLUDE_CONSTRAINT__EXCLUDED_FEATURE_B:
				return basicSetExcludedFeatureB(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case FeatureModelPackage.EXCLUDE_CONSTRAINT__EXCLUDED_FEATURE_A:
				if (resolve) return getExcludedFeatureA();
				return basicGetExcludedFeatureA();
			case FeatureModelPackage.EXCLUDE_CONSTRAINT__EXCLUDED_FEATURE_B:
				if (resolve) return getExcludedFeatureB();
				return basicGetExcludedFeatureB();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case FeatureModelPackage.EXCLUDE_CONSTRAINT__EXCLUDED_FEATURE_A:
				setExcludedFeatureA((Feature)newValue);
				return;
			case FeatureModelPackage.EXCLUDE_CONSTRAINT__EXCLUDED_FEATURE_B:
				setExcludedFeatureB((Feature)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case FeatureModelPackage.EXCLUDE_CONSTRAINT__EXCLUDED_FEATURE_A:
				setExcludedFeatureA((Feature)null);
				return;
			case FeatureModelPackage.EXCLUDE_CONSTRAINT__EXCLUDED_FEATURE_B:
				setExcludedFeatureB((Feature)null);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case FeatureModelPackage.EXCLUDE_CONSTRAINT__EXCLUDED_FEATURE_A:
				return excludedFeatureA != null;
			case FeatureModelPackage.EXCLUDE_CONSTRAINT__EXCLUDED_FEATURE_B:
				return excludedFeatureB != null;
		}
		return super.eIsSet(featureID);
	}

} //ExcludeConstraintImpl
