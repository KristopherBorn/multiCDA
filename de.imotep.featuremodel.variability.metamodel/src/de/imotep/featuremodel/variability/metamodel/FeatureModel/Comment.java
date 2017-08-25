/**
 */
package de.imotep.featuremodel.variability.metamodel.FeatureModel;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Comment</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.Comment#getElement <em>Element</em>}</li>
 *   <li>{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.Comment#getText <em>Text</em>}</li>
 * </ul>
 * </p>
 *
 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.FeatureModelPackage#getComment()
 * @model
 * @generated
 */
public interface Comment extends NamedElement {
	/**
	 * Returns the value of the '<em><b>Element</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Element</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Element</em>' reference.
	 * @see #setElement(NamedElement)
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.FeatureModelPackage#getComment_Element()
	 * @model required="true"
	 * @generated
	 */
	NamedElement getElement();

	/**
	 * Sets the value of the '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.Comment#getElement <em>Element</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Element</em>' reference.
	 * @see #getElement()
	 * @generated
	 */
	void setElement(NamedElement value);

	/**
	 * Returns the value of the '<em><b>Text</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Text</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Text</em>' attribute.
	 * @see #setText(String)
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.FeatureModelPackage#getComment_Text()
	 * @model
	 * @generated
	 */
	String getText();

	/**
	 * Sets the value of the '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.Comment#getText <em>Text</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Text</em>' attribute.
	 * @see #getText()
	 * @generated
	 */
	void setText(String value);

} // Comment
