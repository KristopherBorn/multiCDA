/**
 */
package de.imotep.featuremodel.variability.metamodel.FeatureModel.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Map;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.DiagnosticChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EObjectWithInverseResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;
import de.imotep.featuremodel.variability.metamodel.FeatureModel.ExcludeConstraint;
import de.imotep.featuremodel.variability.metamodel.FeatureModel.Feature;
import de.imotep.featuremodel.variability.metamodel.FeatureModel.FeatureModelPackage;
import de.imotep.featuremodel.variability.metamodel.FeatureModel.Group;
import de.imotep.featuremodel.variability.metamodel.FeatureModel.RequireConstraint;
import de.imotep.featuremodel.variability.metamodel.FeatureModel.util.FeatureModelValidator;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Feature</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.impl.FeatureImpl#isMandatory <em>Mandatory</em>}</li>
 *   <li>{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.impl.FeatureImpl#isAbstract <em>Abstract</em>}</li>
 *   <li>{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.impl.FeatureImpl#getChildren <em>Children</em>}</li>
 *   <li>{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.impl.FeatureImpl#getRequiredConstraints <em>Required Constraints</em>}</li>
 *   <li>{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.impl.FeatureImpl#getRequireConstraints <em>Require Constraints</em>}</li>
 *   <li>{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.impl.FeatureImpl#getGroup <em>Group</em>}</li>
 *   <li>{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.impl.FeatureImpl#getExcludeConstraintsA <em>Exclude Constraints A</em>}</li>
 *   <li>{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.impl.FeatureImpl#getExcludeConstraintsB <em>Exclude Constraints B</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class FeatureImpl extends NamedElementImpl implements Feature {
	/**
	 * The default value of the '{@link #isMandatory() <em>Mandatory</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isMandatory()
	 * @generated
	 * @ordered
	 */
	protected static final boolean MANDATORY_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isMandatory() <em>Mandatory</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isMandatory()
	 * @generated
	 * @ordered
	 */
	protected boolean mandatory = MANDATORY_EDEFAULT;

	/**
	 * The default value of the '{@link #isAbstract() <em>Abstract</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isAbstract()
	 * @generated
	 * @ordered
	 */
	protected static final boolean ABSTRACT_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isAbstract() <em>Abstract</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isAbstract()
	 * @generated
	 * @ordered
	 */
	protected boolean abstract_ = ABSTRACT_EDEFAULT;

	/**
	 * The cached value of the '{@link #getChildren() <em>Children</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getChildren()
	 * @generated
	 * @ordered
	 */
	protected EList<Feature> children;

	/**
	 * The cached value of the '{@link #getRequiredConstraints() <em>Required Constraints</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRequiredConstraints()
	 * @generated
	 * @ordered
	 */
	protected EList<RequireConstraint> requiredConstraints;

	/**
	 * The cached value of the '{@link #getRequireConstraints() <em>Require Constraints</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRequireConstraints()
	 * @generated
	 * @ordered
	 */
	protected EList<RequireConstraint> requireConstraints;

	/**
	 * The cached value of the '{@link #getGroup() <em>Group</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getGroup()
	 * @generated
	 * @ordered
	 */
	protected Group group;

	/**
	 * The cached value of the '{@link #getExcludeConstraintsA() <em>Exclude Constraints A</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExcludeConstraintsA()
	 * @generated
	 * @ordered
	 */
	protected EList<ExcludeConstraint> excludeConstraintsA;

	/**
	 * The cached value of the '{@link #getExcludeConstraintsB() <em>Exclude Constraints B</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExcludeConstraintsB()
	 * @generated
	 * @ordered
	 */
	protected EList<ExcludeConstraint> excludeConstraintsB;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected FeatureImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return FeatureModelPackage.Literals.FEATURE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isMandatory() {
		return mandatory;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setMandatory(boolean newMandatory) {
		boolean oldMandatory = mandatory;
		mandatory = newMandatory;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, FeatureModelPackage.FEATURE__MANDATORY, oldMandatory, mandatory));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isAbstract() {
		return abstract_;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setAbstract(boolean newAbstract) {
		boolean oldAbstract = abstract_;
		abstract_ = newAbstract;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, FeatureModelPackage.FEATURE__ABSTRACT, oldAbstract, abstract_));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Feature> getChildren() {
		if (children == null) {
			children = new EObjectContainmentEList<Feature>(Feature.class, this, FeatureModelPackage.FEATURE__CHILDREN);
		}
		return children;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<RequireConstraint> getRequiredConstraints() {
		if (requiredConstraints == null) {
			requiredConstraints = new EObjectWithInverseResolvingEList<RequireConstraint>(RequireConstraint.class, this, FeatureModelPackage.FEATURE__REQUIRED_CONSTRAINTS, FeatureModelPackage.REQUIRE_CONSTRAINT__REQUIRED_FEATURE);
		}
		return requiredConstraints;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<RequireConstraint> getRequireConstraints() {
		if (requireConstraints == null) {
			requireConstraints = new EObjectWithInverseResolvingEList<RequireConstraint>(RequireConstraint.class, this, FeatureModelPackage.FEATURE__REQUIRE_CONSTRAINTS, FeatureModelPackage.REQUIRE_CONSTRAINT__FEATURE);
		}
		return requireConstraints;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Group getGroup() {
		if (group != null && group.eIsProxy()) {
			InternalEObject oldGroup = (InternalEObject)group;
			group = (Group)eResolveProxy(oldGroup);
			if (group != oldGroup) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, FeatureModelPackage.FEATURE__GROUP, oldGroup, group));
			}
		}
		return group;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Group basicGetGroup() {
		return group;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetGroup(Group newGroup, NotificationChain msgs) {
		Group oldGroup = group;
		group = newGroup;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, FeatureModelPackage.FEATURE__GROUP, oldGroup, newGroup);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setGroup(Group newGroup) {
		if (newGroup != group) {
			NotificationChain msgs = null;
			if (group != null)
				msgs = ((InternalEObject)group).eInverseRemove(this, FeatureModelPackage.GROUP__FEATURES, Group.class, msgs);
			if (newGroup != null)
				msgs = ((InternalEObject)newGroup).eInverseAdd(this, FeatureModelPackage.GROUP__FEATURES, Group.class, msgs);
			msgs = basicSetGroup(newGroup, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, FeatureModelPackage.FEATURE__GROUP, newGroup, newGroup));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<ExcludeConstraint> getExcludeConstraintsA() {
		if (excludeConstraintsA == null) {
			excludeConstraintsA = new EObjectWithInverseResolvingEList<ExcludeConstraint>(ExcludeConstraint.class, this, FeatureModelPackage.FEATURE__EXCLUDE_CONSTRAINTS_A, FeatureModelPackage.EXCLUDE_CONSTRAINT__EXCLUDED_FEATURE_A);
		}
		return excludeConstraintsA;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<ExcludeConstraint> getExcludeConstraintsB() {
		if (excludeConstraintsB == null) {
			excludeConstraintsB = new EObjectWithInverseResolvingEList<ExcludeConstraint>(ExcludeConstraint.class, this, FeatureModelPackage.FEATURE__EXCLUDE_CONSTRAINTS_B, FeatureModelPackage.EXCLUDE_CONSTRAINT__EXCLUDED_FEATURE_B);
		}
		return excludeConstraintsB;
	}

	/**
	 * Checks whether a feature is referenced by one
	 * @link{Group} at most. There shall be no feature which
	 * is referenced by more than one @link{Group}
	 * Note: This invariant is already cross-checked
	 * through the bidirectional references between @link{Group}
	 * and @link{Feature}. This constraint is needed because the default EMFValidation
	 * is only executed if there has been defined at least one own constraint
	 * concerning each meta model.
	 * @generated NOT
	 */
	public boolean atMostInOneGroup(DiagnosticChain chain, Map<?, ?> context) {
		
		int groupRefs = 0;
		Resource resource = this.eResource();
		if (resource != null){
			TreeIterator<EObject> it = resource.getAllContents();
			while (it.hasNext()) {
				EObject obj = it.next();
				if(obj instanceof Group){
					Group grp = (Group) obj;
					if(grp.getFeatures().contains(this)){
						groupRefs++;
					}					
				}
			}
		}
		if (groupRefs > 1) {
			if (chain != null) {
				chain.add
					(new BasicDiagnostic
						(Diagnostic.ERROR,
						 FeatureModelValidator.DIAGNOSTIC_SOURCE,
						 FeatureModelValidator.FEATURE__AT_MOST_IN_ONE_GROUP,
						 "Feature shall be in one group at most!",
						 new Object [] { this }));
			}
			return false;
		}
		return true;
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
			case FeatureModelPackage.FEATURE__REQUIRED_CONSTRAINTS:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getRequiredConstraints()).basicAdd(otherEnd, msgs);
			case FeatureModelPackage.FEATURE__REQUIRE_CONSTRAINTS:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getRequireConstraints()).basicAdd(otherEnd, msgs);
			case FeatureModelPackage.FEATURE__GROUP:
				if (group != null)
					msgs = ((InternalEObject)group).eInverseRemove(this, FeatureModelPackage.GROUP__FEATURES, Group.class, msgs);
				return basicSetGroup((Group)otherEnd, msgs);
			case FeatureModelPackage.FEATURE__EXCLUDE_CONSTRAINTS_A:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getExcludeConstraintsA()).basicAdd(otherEnd, msgs);
			case FeatureModelPackage.FEATURE__EXCLUDE_CONSTRAINTS_B:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getExcludeConstraintsB()).basicAdd(otherEnd, msgs);
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
			case FeatureModelPackage.FEATURE__CHILDREN:
				return ((InternalEList<?>)getChildren()).basicRemove(otherEnd, msgs);
			case FeatureModelPackage.FEATURE__REQUIRED_CONSTRAINTS:
				return ((InternalEList<?>)getRequiredConstraints()).basicRemove(otherEnd, msgs);
			case FeatureModelPackage.FEATURE__REQUIRE_CONSTRAINTS:
				return ((InternalEList<?>)getRequireConstraints()).basicRemove(otherEnd, msgs);
			case FeatureModelPackage.FEATURE__GROUP:
				return basicSetGroup(null, msgs);
			case FeatureModelPackage.FEATURE__EXCLUDE_CONSTRAINTS_A:
				return ((InternalEList<?>)getExcludeConstraintsA()).basicRemove(otherEnd, msgs);
			case FeatureModelPackage.FEATURE__EXCLUDE_CONSTRAINTS_B:
				return ((InternalEList<?>)getExcludeConstraintsB()).basicRemove(otherEnd, msgs);
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
			case FeatureModelPackage.FEATURE__MANDATORY:
				return isMandatory();
			case FeatureModelPackage.FEATURE__ABSTRACT:
				return isAbstract();
			case FeatureModelPackage.FEATURE__CHILDREN:
				return getChildren();
			case FeatureModelPackage.FEATURE__REQUIRED_CONSTRAINTS:
				return getRequiredConstraints();
			case FeatureModelPackage.FEATURE__REQUIRE_CONSTRAINTS:
				return getRequireConstraints();
			case FeatureModelPackage.FEATURE__GROUP:
				if (resolve) return getGroup();
				return basicGetGroup();
			case FeatureModelPackage.FEATURE__EXCLUDE_CONSTRAINTS_A:
				return getExcludeConstraintsA();
			case FeatureModelPackage.FEATURE__EXCLUDE_CONSTRAINTS_B:
				return getExcludeConstraintsB();
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
			case FeatureModelPackage.FEATURE__MANDATORY:
				setMandatory((Boolean)newValue);
				return;
			case FeatureModelPackage.FEATURE__ABSTRACT:
				setAbstract((Boolean)newValue);
				return;
			case FeatureModelPackage.FEATURE__CHILDREN:
				getChildren().clear();
				getChildren().addAll((Collection<? extends Feature>)newValue);
				return;
			case FeatureModelPackage.FEATURE__REQUIRED_CONSTRAINTS:
				getRequiredConstraints().clear();
				getRequiredConstraints().addAll((Collection<? extends RequireConstraint>)newValue);
				return;
			case FeatureModelPackage.FEATURE__REQUIRE_CONSTRAINTS:
				getRequireConstraints().clear();
				getRequireConstraints().addAll((Collection<? extends RequireConstraint>)newValue);
				return;
			case FeatureModelPackage.FEATURE__GROUP:
				setGroup((Group)newValue);
				return;
			case FeatureModelPackage.FEATURE__EXCLUDE_CONSTRAINTS_A:
				getExcludeConstraintsA().clear();
				getExcludeConstraintsA().addAll((Collection<? extends ExcludeConstraint>)newValue);
				return;
			case FeatureModelPackage.FEATURE__EXCLUDE_CONSTRAINTS_B:
				getExcludeConstraintsB().clear();
				getExcludeConstraintsB().addAll((Collection<? extends ExcludeConstraint>)newValue);
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
			case FeatureModelPackage.FEATURE__MANDATORY:
				setMandatory(MANDATORY_EDEFAULT);
				return;
			case FeatureModelPackage.FEATURE__ABSTRACT:
				setAbstract(ABSTRACT_EDEFAULT);
				return;
			case FeatureModelPackage.FEATURE__CHILDREN:
				getChildren().clear();
				return;
			case FeatureModelPackage.FEATURE__REQUIRED_CONSTRAINTS:
				getRequiredConstraints().clear();
				return;
			case FeatureModelPackage.FEATURE__REQUIRE_CONSTRAINTS:
				getRequireConstraints().clear();
				return;
			case FeatureModelPackage.FEATURE__GROUP:
				setGroup((Group)null);
				return;
			case FeatureModelPackage.FEATURE__EXCLUDE_CONSTRAINTS_A:
				getExcludeConstraintsA().clear();
				return;
			case FeatureModelPackage.FEATURE__EXCLUDE_CONSTRAINTS_B:
				getExcludeConstraintsB().clear();
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
			case FeatureModelPackage.FEATURE__MANDATORY:
				return mandatory != MANDATORY_EDEFAULT;
			case FeatureModelPackage.FEATURE__ABSTRACT:
				return abstract_ != ABSTRACT_EDEFAULT;
			case FeatureModelPackage.FEATURE__CHILDREN:
				return children != null && !children.isEmpty();
			case FeatureModelPackage.FEATURE__REQUIRED_CONSTRAINTS:
				return requiredConstraints != null && !requiredConstraints.isEmpty();
			case FeatureModelPackage.FEATURE__REQUIRE_CONSTRAINTS:
				return requireConstraints != null && !requireConstraints.isEmpty();
			case FeatureModelPackage.FEATURE__GROUP:
				return group != null;
			case FeatureModelPackage.FEATURE__EXCLUDE_CONSTRAINTS_A:
				return excludeConstraintsA != null && !excludeConstraintsA.isEmpty();
			case FeatureModelPackage.FEATURE__EXCLUDE_CONSTRAINTS_B:
				return excludeConstraintsB != null && !excludeConstraintsB.isEmpty();
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eInvoke(int operationID, EList<?> arguments) throws InvocationTargetException {
		switch (operationID) {
			case FeatureModelPackage.FEATURE___AT_MOST_IN_ONE_GROUP__DIAGNOSTICCHAIN_MAP:
				return atMostInOneGroup((DiagnosticChain)arguments.get(0), (Map<?, ?>)arguments.get(1));
		}
		return super.eInvoke(operationID, arguments);
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
		result.append(" (mandatory: ");
		result.append(mandatory);
		result.append(", abstract: ");
		result.append(abstract_);
		result.append(')');
		return result.toString();
	}

} //FeatureImpl
