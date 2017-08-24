/**
 */
package de.imotep.featuremodel.variability.metamodel.FeatureModel;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Exclude Constraint</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.ExcludeConstraint#getExcludedFeatureA <em>Excluded Feature A</em>}</li>
 *   <li>{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.ExcludeConstraint#getExcludedFeatureB <em>Excluded Feature B</em>}</li>
 * </ul>
 * </p>
 *
 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.FeatureModelPackage#getExcludeConstraint()
 * @model
 * @generated
 */
public interface ExcludeConstraint extends Constraint {
	/**
	 * Returns the value of the '<em><b>Excluded Feature A</b></em>' reference.
	 * It is bidirectional and its opposite is '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.Feature#getExcludeConstraintsA <em>Exclude Constraints A</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Excluded Feature A</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Excluded Feature A</em>' reference.
	 * @see #setExcludedFeatureA(Feature)
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.FeatureModelPackage#getExcludeConstraint_ExcludedFeatureA()
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.Feature#getExcludeConstraintsA
	 * @model opposite="excludeConstraintsA" required="true"
	 * @generated
	 */
	Feature getExcludedFeatureA();

	/**
	 * Sets the value of the '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.ExcludeConstraint#getExcludedFeatureA <em>Excluded Feature A</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Excluded Feature A</em>' reference.
	 * @see #getExcludedFeatureA()
	 * @generated
	 */
	void setExcludedFeatureA(Feature value);

	/**
	 * Returns the value of the '<em><b>Excluded Feature B</b></em>' reference.
	 * It is bidirectional and its opposite is '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.Feature#getExcludeConstraintsB <em>Exclude Constraints B</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Excluded Feature B</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Excluded Feature B</em>' reference.
	 * @see #setExcludedFeatureB(Feature)
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.FeatureModelPackage#getExcludeConstraint_ExcludedFeatureB()
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.Feature#getExcludeConstraintsB
	 * @model opposite="excludeConstraintsB" required="true"
	 * @generated
	 */
	Feature getExcludedFeatureB();

	/**
	 * Sets the value of the '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.ExcludeConstraint#getExcludedFeatureB <em>Excluded Feature B</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Excluded Feature B</em>' reference.
	 * @see #getExcludedFeatureB()
	 * @generated
	 */
	void setExcludedFeatureB(Feature value);

} // ExcludeConstraint
