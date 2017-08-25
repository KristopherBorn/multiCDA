/**
 */
package de.imotep.featuremodel.variability.metamodel.FeatureModel.impl;

import de.imotep.featuremodel.variability.metamodel.FeatureModel.Feature;
import de.imotep.featuremodel.variability.metamodel.FeatureModel.FeatureModelPackage;
import de.imotep.featuremodel.variability.metamodel.FeatureModel.RequireConstraint;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Require Constraint</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.impl.RequireConstraintImpl#getRequiredFeature <em>Required Feature</em>}</li>
 *   <li>{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.impl.RequireConstraintImpl#getFeature <em>Feature</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class RequireConstraintImpl extends ConstraintImpl implements RequireConstraint {
	/**
	 * The cached value of the '{@link #getRequiredFeature() <em>Required Feature</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRequiredFeature()
	 * @generated
	 * @ordered
	 */
	protected Feature requiredFeature;

	/**
	 * The cached value of the '{@link #getFeature() <em>Feature</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFeature()
	 * @generated
	 * @ordered
	 */
	protected Feature feature;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected RequireConstraintImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return FeatureModelPackage.Literals.REQUIRE_CONSTRAINT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Feature getRequiredFeature() {
		if (requiredFeature != null && requiredFeature.eIsProxy()) {
			InternalEObject oldRequiredFeature = (InternalEObject)requiredFeature;
			requiredFeature = (Feature)eResolveProxy(oldRequiredFeature);
			if (requiredFeature != oldRequiredFeature) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, FeatureModelPackage.REQUIRE_CONSTRAINT__REQUIRED_FEATURE, oldRequiredFeature, requiredFeature));
			}
		}
		return requiredFeature;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Feature basicGetRequiredFeature() {
		return requiredFeature;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetRequiredFeature(Feature newRequiredFeature, NotificationChain msgs) {
		Feature oldRequiredFeature = requiredFeature;
		requiredFeature = newRequiredFeature;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, FeatureModelPackage.REQUIRE_CONSTRAINT__REQUIRED_FEATURE, oldRequiredFeature, newRequiredFeature);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRequiredFeature(Feature newRequiredFeature) {
		if (newRequiredFeature != requiredFeature) {
			NotificationChain msgs = null;
			if (requiredFeature != null)
				msgs = ((InternalEObject)requiredFeature).eInverseRemove(this, FeatureModelPackage.FEATURE__REQUIRED_CONSTRAINTS, Feature.class, msgs);
			if (newRequiredFeature != null)
				msgs = ((InternalEObject)newRequiredFeature).eInverseAdd(this, FeatureModelPackage.FEATURE__REQUIRED_CONSTRAINTS, Feature.class, msgs);
			msgs = basicSetRequiredFeature(newRequiredFeature, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, FeatureModelPackage.REQUIRE_CONSTRAINT__REQUIRED_FEATURE, newRequiredFeature, newRequiredFeature));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Feature getFeature() {
		if (feature != null && feature.eIsProxy()) {
			InternalEObject oldFeature = (InternalEObject)feature;
			feature = (Feature)eResolveProxy(oldFeature);
			if (feature != oldFeature) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, FeatureModelPackage.REQUIRE_CONSTRAINT__FEATURE, oldFeature, feature));
			}
		}
		return feature;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Feature basicGetFeature() {
		return feature;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetFeature(Feature newFeature, NotificationChain msgs) {
		Feature oldFeature = feature;
		feature = newFeature;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, FeatureModelPackage.REQUIRE_CONSTRAINT__FEATURE, oldFeature, newFeature);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setFeature(Feature newFeature) {
		if (newFeature != feature) {
			NotificationChain msgs = null;
			if (feature != null)
				msgs = ((InternalEObject)feature).eInverseRemove(this, FeatureModelPackage.FEATURE__REQUIRE_CONSTRAINTS, Feature.class, msgs);
			if (newFeature != null)
				msgs = ((InternalEObject)newFeature).eInverseAdd(this, FeatureModelPackage.FEATURE__REQUIRE_CONSTRAINTS, Feature.class, msgs);
			msgs = basicSetFeature(newFeature, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, FeatureModelPackage.REQUIRE_CONSTRAINT__FEATURE, newFeature, newFeature));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case FeatureModelPackage.REQUIRE_CONSTRAINT__REQUIRED_FEATURE:
				if (requiredFeature != null)
					msgs = ((InternalEObject)requiredFeature).eInverseRemove(this, FeatureModelPackage.FEATURE__REQUIRED_CONSTRAINTS, Feature.class, msgs);
				return basicSetRequiredFeature((Feature)otherEnd, msgs);
			case FeatureModelPackage.REQUIRE_CONSTRAINT__FEATURE:
				if (feature != null)
					msgs = ((InternalEObject)feature).eInverseRemove(this, FeatureModelPackage.FEATURE__REQUIRE_CONSTRAINTS, Feature.class, msgs);
				return basicSetFeature((Feature)otherEnd, msgs);
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
			case FeatureModelPackage.REQUIRE_CONSTRAINT__REQUIRED_FEATURE:
				return basicSetRequiredFeature(null, msgs);
			case FeatureModelPackage.REQUIRE_CONSTRAINT__FEATURE:
				return basicSetFeature(null, msgs);
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
			case FeatureModelPackage.REQUIRE_CONSTRAINT__REQUIRED_FEATURE:
				if (resolve) return getRequiredFeature();
				return basicGetRequiredFeature();
			case FeatureModelPackage.REQUIRE_CONSTRAINT__FEATURE:
				if (resolve) return getFeature();
				return basicGetFeature();
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
			case FeatureModelPackage.REQUIRE_CONSTRAINT__REQUIRED_FEATURE:
				setRequiredFeature((Feature)newValue);
				return;
			case FeatureModelPackage.REQUIRE_CONSTRAINT__FEATURE:
				setFeature((Feature)newValue);
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
			case FeatureModelPackage.REQUIRE_CONSTRAINT__REQUIRED_FEATURE:
				setRequiredFeature((Feature)null);
				return;
			case FeatureModelPackage.REQUIRE_CONSTRAINT__FEATURE:
				setFeature((Feature)null);
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
			case FeatureModelPackage.REQUIRE_CONSTRAINT__REQUIRED_FEATURE:
				return requiredFeature != null;
			case FeatureModelPackage.REQUIRE_CONSTRAINT__FEATURE:
				return feature != null;
		}
		return super.eIsSet(featureID);
	}

} //RequireConstraintImpl
