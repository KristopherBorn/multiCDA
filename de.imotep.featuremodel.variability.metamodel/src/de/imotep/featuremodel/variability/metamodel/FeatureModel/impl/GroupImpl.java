/**
 */
package de.imotep.featuremodel.variability.metamodel.FeatureModel.impl;

import de.imotep.featuremodel.variability.metamodel.FeatureModel.Feature;
import de.imotep.featuremodel.variability.metamodel.FeatureModel.FeatureModelPackage;
import de.imotep.featuremodel.variability.metamodel.FeatureModel.Group;
import de.imotep.featuremodel.variability.metamodel.FeatureModel.GroupType;
import java.util.Collection;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectWithInverseResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Group</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.impl.GroupImpl#getFeatures <em>Features</em>}</li>
 *   <li>{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.impl.GroupImpl#getGroupType <em>Group Type</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class GroupImpl extends NamedElementImpl implements Group {
	/**
	 * The cached value of the '{@link #getFeatures() <em>Features</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFeatures()
	 * @generated
	 * @ordered
	 */
	protected EList<Feature> features;

	/**
	 * The default value of the '{@link #getGroupType() <em>Group Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getGroupType()
	 * @generated
	 * @ordered
	 */
	protected static final GroupType GROUP_TYPE_EDEFAULT = GroupType.OR;

	/**
	 * The cached value of the '{@link #getGroupType() <em>Group Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getGroupType()
	 * @generated
	 * @ordered
	 */
	protected GroupType groupType = GROUP_TYPE_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected GroupImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return FeatureModelPackage.Literals.GROUP;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Feature> getFeatures() {
		if (features == null) {
			features = new EObjectWithInverseResolvingEList<Feature>(Feature.class, this, FeatureModelPackage.GROUP__FEATURES, FeatureModelPackage.FEATURE__GROUP);
		}
		return features;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public GroupType getGroupType() {
		return groupType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setGroupType(GroupType newGroupType) {
		GroupType oldGroupType = groupType;
		groupType = newGroupType == null ? GROUP_TYPE_EDEFAULT : newGroupType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, FeatureModelPackage.GROUP__GROUP_TYPE, oldGroupType, groupType));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case FeatureModelPackage.GROUP__FEATURES:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getFeatures()).basicAdd(otherEnd, msgs);
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
			case FeatureModelPackage.GROUP__FEATURES:
				return ((InternalEList<?>)getFeatures()).basicRemove(otherEnd, msgs);
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
			case FeatureModelPackage.GROUP__FEATURES:
				return getFeatures();
			case FeatureModelPackage.GROUP__GROUP_TYPE:
				return getGroupType();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case FeatureModelPackage.GROUP__FEATURES:
				getFeatures().clear();
				getFeatures().addAll((Collection<? extends Feature>)newValue);
				return;
			case FeatureModelPackage.GROUP__GROUP_TYPE:
				setGroupType((GroupType)newValue);
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
			case FeatureModelPackage.GROUP__FEATURES:
				getFeatures().clear();
				return;
			case FeatureModelPackage.GROUP__GROUP_TYPE:
				setGroupType(GROUP_TYPE_EDEFAULT);
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
			case FeatureModelPackage.GROUP__FEATURES:
				return features != null && !features.isEmpty();
			case FeatureModelPackage.GROUP__GROUP_TYPE:
				return groupType != GROUP_TYPE_EDEFAULT;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (groupType: ");
		result.append(groupType);
		result.append(')');
		return result.toString();
	}

} //GroupImpl
