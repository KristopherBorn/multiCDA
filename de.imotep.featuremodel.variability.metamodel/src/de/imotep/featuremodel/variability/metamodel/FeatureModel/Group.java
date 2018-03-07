/**
 */
package de.imotep.featuremodel.variability.metamodel.FeatureModel;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Group</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.Group#getFeatures <em>Features</em>}</li>
 *   <li>{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.Group#getGroupType <em>Group Type</em>}</li>
 * </ul>
 * </p>
 *
 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.FeatureModelPackage#getGroup()
 * @model
 * @generated
 */
public interface Group extends NamedElement {
	/**
	 * Returns the value of the '<em><b>Features</b></em>' reference list.
	 * The list contents are of type {@link de.imotep.featuremodel.variability.metamodel.FeatureModel.Feature}.
	 * It is bidirectional and its opposite is '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.Feature#getGroup <em>Group</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Features</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Features</em>' reference list.
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.FeatureModelPackage#getGroup_Features()
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.Feature#getGroup
	 * @model opposite="group" lower="2"
	 * @generated
	 */
	EList<Feature> getFeatures();

	/**
	 * Returns the value of the '<em><b>Group Type</b></em>' attribute.
	 * The literals are from the enumeration {@link de.imotep.featuremodel.variability.metamodel.FeatureModel.GroupType}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Group Type</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Group Type</em>' attribute.
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.GroupType
	 * @see #setGroupType(GroupType)
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.FeatureModelPackage#getGroup_GroupType()
	 * @model
	 * @generated
	 */
	GroupType getGroupType();

	/**
	 * Sets the value of the '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.Group#getGroupType <em>Group Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Group Type</em>' attribute.
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.GroupType
	 * @see #getGroupType()
	 * @generated
	 */
	void setGroupType(GroupType value);

} // Group
