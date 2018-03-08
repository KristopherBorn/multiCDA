/**
 */
package de.imotep.featuremodel.variability.metamodel.FeatureModel;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Require Constraint</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.RequireConstraint#getRequiredFeature <em>Required Feature</em>}</li>
 *   <li>{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.RequireConstraint#getFeature <em>Feature</em>}</li>
 * </ul>
 * </p>
 *
 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.FeatureModelPackage#getRequireConstraint()
 * @model
 * @generated
 */
public interface RequireConstraint extends Constraint {
	/**
	 * Returns the value of the '<em><b>Required Feature</b></em>' reference.
	 * It is bidirectional and its opposite is '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.Feature#getRequiredConstraints <em>Required Constraints</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Required Feature</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Required Feature</em>' reference.
	 * @see #setRequiredFeature(Feature)
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.FeatureModelPackage#getRequireConstraint_RequiredFeature()
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.Feature#getRequiredConstraints
	 * @model opposite="requiredConstraints" required="true"
	 * @generated
	 */
	Feature getRequiredFeature();

	/**
	 * Sets the value of the '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.RequireConstraint#getRequiredFeature <em>Required Feature</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Required Feature</em>' reference.
	 * @see #getRequiredFeature()
	 * @generated
	 */
	void setRequiredFeature(Feature value);

	/**
	 * Returns the value of the '<em><b>Feature</b></em>' reference.
	 * It is bidirectional and its opposite is '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.Feature#getRequireConstraints <em>Require Constraints</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Feature</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Feature</em>' reference.
	 * @see #setFeature(Feature)
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.FeatureModelPackage#getRequireConstraint_Feature()
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.Feature#getRequireConstraints
	 * @model opposite="requireConstraints" required="true"
	 * @generated
	 */
	Feature getFeature();

	/**
	 * Sets the value of the '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.RequireConstraint#getFeature <em>Feature</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Feature</em>' reference.
	 * @see #getFeature()
	 * @generated
	 */
	void setFeature(Feature value);

} // RequireConstraint
