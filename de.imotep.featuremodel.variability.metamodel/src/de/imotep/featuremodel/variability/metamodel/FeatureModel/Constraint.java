/**
 */
package de.imotep.featuremodel.variability.metamodel.FeatureModel;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Constraint</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.Constraint#getLanguage <em>Language</em>}</li>
 *   <li>{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.Constraint#getCode <em>Code</em>}</li>
 * </ul>
 * </p>
 *
 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.FeatureModelPackage#getConstraint()
 * @model abstract="true"
 * @generated
 */
public interface Constraint extends NamedElement {
	/**
	 * Returns the value of the '<em><b>Language</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Language</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Language</em>' attribute.
	 * @see #setLanguage(String)
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.FeatureModelPackage#getConstraint_Language()
	 * @model
	 * @generated
	 */
	String getLanguage();

	/**
	 * Sets the value of the '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.Constraint#getLanguage <em>Language</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Language</em>' attribute.
	 * @see #getLanguage()
	 * @generated
	 */
	void setLanguage(String value);

	/**
	 * Returns the value of the '<em><b>Code</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Code</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Code</em>' attribute.
	 * @see #setCode(String)
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.FeatureModelPackage#getConstraint_Code()
	 * @model
	 * @generated
	 */
	String getCode();

	/**
	 * Sets the value of the '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.Constraint#getCode <em>Code</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Code</em>' attribute.
	 * @see #getCode()
	 * @generated
	 */
	void setCode(String value);

} // Constraint
