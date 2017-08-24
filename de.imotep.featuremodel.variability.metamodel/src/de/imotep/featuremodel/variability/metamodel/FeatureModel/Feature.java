/**
 */
package de.imotep.featuremodel.variability.metamodel.FeatureModel;

import java.util.Map;
import org.eclipse.emf.common.util.DiagnosticChain;
import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Feature</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.Feature#isMandatory <em>Mandatory</em>}</li>
 *   <li>{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.Feature#isAbstract <em>Abstract</em>}</li>
 *   <li>{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.Feature#getChildren <em>Children</em>}</li>
 *   <li>{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.Feature#getRequiredConstraints <em>Required Constraints</em>}</li>
 *   <li>{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.Feature#getRequireConstraints <em>Require Constraints</em>}</li>
 *   <li>{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.Feature#getGroup <em>Group</em>}</li>
 *   <li>{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.Feature#getExcludeConstraintsA <em>Exclude Constraints A</em>}</li>
 *   <li>{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.Feature#getExcludeConstraintsB <em>Exclude Constraints B</em>}</li>
 * </ul>
 * </p>
 *
 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.FeatureModelPackage#getFeature()
 * @model
 * @generated
 */
public interface Feature extends NamedElement {
	/**
	 * Returns the value of the '<em><b>Mandatory</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Mandatory</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Mandatory</em>' attribute.
	 * @see #setMandatory(boolean)
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.FeatureModelPackage#getFeature_Mandatory()
	 * @model
	 * @generated
	 */
	boolean isMandatory();

	/**
	 * Sets the value of the '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.Feature#isMandatory <em>Mandatory</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Mandatory</em>' attribute.
	 * @see #isMandatory()
	 * @generated
	 */
	void setMandatory(boolean value);

	/**
	 * Returns the value of the '<em><b>Abstract</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Abstract</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Abstract</em>' attribute.
	 * @see #setAbstract(boolean)
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.FeatureModelPackage#getFeature_Abstract()
	 * @model
	 * @generated
	 */
	boolean isAbstract();

	/**
	 * Sets the value of the '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.Feature#isAbstract <em>Abstract</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Abstract</em>' attribute.
	 * @see #isAbstract()
	 * @generated
	 */
	void setAbstract(boolean value);

	/**
	 * Returns the value of the '<em><b>Children</b></em>' containment reference list.
	 * The list contents are of type {@link de.imotep.featuremodel.variability.metamodel.FeatureModel.Feature}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Children</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Children</em>' containment reference list.
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.FeatureModelPackage#getFeature_Children()
	 * @model containment="true"
	 * @generated
	 */
	EList<Feature> getChildren();

	/**
	 * Returns the value of the '<em><b>Required Constraints</b></em>' reference list.
	 * The list contents are of type {@link de.imotep.featuremodel.variability.metamodel.FeatureModel.RequireConstraint}.
	 * It is bidirectional and its opposite is '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.RequireConstraint#getRequiredFeature <em>Required Feature</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Required Constraints</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Required Constraints</em>' reference list.
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.FeatureModelPackage#getFeature_RequiredConstraints()
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.RequireConstraint#getRequiredFeature
	 * @model opposite="requiredFeature"
	 * @generated
	 */
	EList<RequireConstraint> getRequiredConstraints();

	/**
	 * Returns the value of the '<em><b>Require Constraints</b></em>' reference list.
	 * The list contents are of type {@link de.imotep.featuremodel.variability.metamodel.FeatureModel.RequireConstraint}.
	 * It is bidirectional and its opposite is '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.RequireConstraint#getFeature <em>Feature</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Require Constraints</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Require Constraints</em>' reference list.
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.FeatureModelPackage#getFeature_RequireConstraints()
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.RequireConstraint#getFeature
	 * @model opposite="feature"
	 * @generated
	 */
	EList<RequireConstraint> getRequireConstraints();

	/**
	 * Returns the value of the '<em><b>Group</b></em>' reference.
	 * It is bidirectional and its opposite is '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.Group#getFeatures <em>Features</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Group</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Group</em>' reference.
	 * @see #setGroup(Group)
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.FeatureModelPackage#getFeature_Group()
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.Group#getFeatures
	 * @model opposite="features"
	 * @generated
	 */
	Group getGroup();

	/**
	 * Sets the value of the '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.Feature#getGroup <em>Group</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Group</em>' reference.
	 * @see #getGroup()
	 * @generated
	 */
	void setGroup(Group value);

	/**
	 * Returns the value of the '<em><b>Exclude Constraints A</b></em>' reference list.
	 * The list contents are of type {@link de.imotep.featuremodel.variability.metamodel.FeatureModel.ExcludeConstraint}.
	 * It is bidirectional and its opposite is '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.ExcludeConstraint#getExcludedFeatureA <em>Excluded Feature A</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Exclude Constraints A</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Exclude Constraints A</em>' reference list.
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.FeatureModelPackage#getFeature_ExcludeConstraintsA()
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.ExcludeConstraint#getExcludedFeatureA
	 * @model opposite="excludedFeatureA"
	 * @generated
	 */
	EList<ExcludeConstraint> getExcludeConstraintsA();

	/**
	 * Returns the value of the '<em><b>Exclude Constraints B</b></em>' reference list.
	 * The list contents are of type {@link de.imotep.featuremodel.variability.metamodel.FeatureModel.ExcludeConstraint}.
	 * It is bidirectional and its opposite is '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.ExcludeConstraint#getExcludedFeatureB <em>Excluded Feature B</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Exclude Constraints B</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Exclude Constraints B</em>' reference list.
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.FeatureModelPackage#getFeature_ExcludeConstraintsB()
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.ExcludeConstraint#getExcludedFeatureB
	 * @model opposite="excludedFeatureB"
	 * @generated
	 */
	EList<ExcludeConstraint> getExcludeConstraintsB();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model
	 * @generated
	 */
	boolean atMostInOneGroup(DiagnosticChain chain, Map<?, ?> context);

} // Feature
