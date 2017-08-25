/**
 */
package de.imotep.featuremodel.variability.metamodel.FeatureModel;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.FeatureModelFactory
 * @model kind="package"
 * @generated
 */
public interface FeatureModelPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "FeatureModel";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://de.imotep.variability/featuremodel";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "FeatureModel";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	FeatureModelPackage eINSTANCE = de.imotep.featuremodel.variability.metamodel.FeatureModel.impl.FeatureModelPackageImpl.init();

	/**
	 * The meta object id for the '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.impl.NamedElementImpl <em>Named Element</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.impl.NamedElementImpl
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.impl.FeatureModelPackageImpl#getNamedElement()
	 * @generated
	 */
	int NAMED_ELEMENT = 1;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAMED_ELEMENT__NAME = 0;

	/**
	 * The number of structural features of the '<em>Named Element</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAMED_ELEMENT_FEATURE_COUNT = 1;

	/**
	 * The number of operations of the '<em>Named Element</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAMED_ELEMENT_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.impl.FeatureModelImpl <em>Feature Model</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.impl.FeatureModelImpl
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.impl.FeatureModelPackageImpl#getFeatureModel()
	 * @generated
	 */
	int FEATURE_MODEL = 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_MODEL__NAME = NAMED_ELEMENT__NAME;

	/**
	 * The feature id for the '<em><b>Comments</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_MODEL__COMMENTS = NAMED_ELEMENT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Constraints</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_MODEL__CONSTRAINTS = NAMED_ELEMENT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Version</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_MODEL__VERSION = NAMED_ELEMENT_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Root</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_MODEL__ROOT = NAMED_ELEMENT_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Groups</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_MODEL__GROUPS = NAMED_ELEMENT_FEATURE_COUNT + 4;

	/**
	 * The number of structural features of the '<em>Feature Model</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_MODEL_FEATURE_COUNT = NAMED_ELEMENT_FEATURE_COUNT + 5;

	/**
	 * The number of operations of the '<em>Feature Model</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_MODEL_OPERATION_COUNT = NAMED_ELEMENT_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.impl.CommentImpl <em>Comment</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.impl.CommentImpl
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.impl.FeatureModelPackageImpl#getComment()
	 * @generated
	 */
	int COMMENT = 2;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMMENT__NAME = NAMED_ELEMENT__NAME;

	/**
	 * The feature id for the '<em><b>Element</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMMENT__ELEMENT = NAMED_ELEMENT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Text</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMMENT__TEXT = NAMED_ELEMENT_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Comment</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMMENT_FEATURE_COUNT = NAMED_ELEMENT_FEATURE_COUNT + 2;

	/**
	 * The number of operations of the '<em>Comment</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMMENT_OPERATION_COUNT = NAMED_ELEMENT_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.impl.ConstraintImpl <em>Constraint</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.impl.ConstraintImpl
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.impl.FeatureModelPackageImpl#getConstraint()
	 * @generated
	 */
	int CONSTRAINT = 3;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONSTRAINT__NAME = NAMED_ELEMENT__NAME;

	/**
	 * The feature id for the '<em><b>Language</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONSTRAINT__LANGUAGE = NAMED_ELEMENT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Code</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONSTRAINT__CODE = NAMED_ELEMENT_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Constraint</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONSTRAINT_FEATURE_COUNT = NAMED_ELEMENT_FEATURE_COUNT + 2;

	/**
	 * The number of operations of the '<em>Constraint</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONSTRAINT_OPERATION_COUNT = NAMED_ELEMENT_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.impl.FeatureImpl <em>Feature</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.impl.FeatureImpl
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.impl.FeatureModelPackageImpl#getFeature()
	 * @generated
	 */
	int FEATURE = 4;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE__NAME = NAMED_ELEMENT__NAME;

	/**
	 * The feature id for the '<em><b>Mandatory</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE__MANDATORY = NAMED_ELEMENT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Abstract</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE__ABSTRACT = NAMED_ELEMENT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Children</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE__CHILDREN = NAMED_ELEMENT_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Required Constraints</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE__REQUIRED_CONSTRAINTS = NAMED_ELEMENT_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Require Constraints</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE__REQUIRE_CONSTRAINTS = NAMED_ELEMENT_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Group</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE__GROUP = NAMED_ELEMENT_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Exclude Constraints A</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE__EXCLUDE_CONSTRAINTS_A = NAMED_ELEMENT_FEATURE_COUNT + 6;

	/**
	 * The feature id for the '<em><b>Exclude Constraints B</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE__EXCLUDE_CONSTRAINTS_B = NAMED_ELEMENT_FEATURE_COUNT + 7;

	/**
	 * The number of structural features of the '<em>Feature</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_FEATURE_COUNT = NAMED_ELEMENT_FEATURE_COUNT + 8;

	/**
	 * The operation id for the '<em>At Most In One Group</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE___AT_MOST_IN_ONE_GROUP__DIAGNOSTICCHAIN_MAP = NAMED_ELEMENT_OPERATION_COUNT + 0;

	/**
	 * The number of operations of the '<em>Feature</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_OPERATION_COUNT = NAMED_ELEMENT_OPERATION_COUNT + 1;

	/**
	 * The meta object id for the '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.impl.RequireConstraintImpl <em>Require Constraint</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.impl.RequireConstraintImpl
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.impl.FeatureModelPackageImpl#getRequireConstraint()
	 * @generated
	 */
	int REQUIRE_CONSTRAINT = 5;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REQUIRE_CONSTRAINT__NAME = CONSTRAINT__NAME;

	/**
	 * The feature id for the '<em><b>Language</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REQUIRE_CONSTRAINT__LANGUAGE = CONSTRAINT__LANGUAGE;

	/**
	 * The feature id for the '<em><b>Code</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REQUIRE_CONSTRAINT__CODE = CONSTRAINT__CODE;

	/**
	 * The feature id for the '<em><b>Required Feature</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REQUIRE_CONSTRAINT__REQUIRED_FEATURE = CONSTRAINT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Feature</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REQUIRE_CONSTRAINT__FEATURE = CONSTRAINT_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Require Constraint</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REQUIRE_CONSTRAINT_FEATURE_COUNT = CONSTRAINT_FEATURE_COUNT + 2;

	/**
	 * The number of operations of the '<em>Require Constraint</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REQUIRE_CONSTRAINT_OPERATION_COUNT = CONSTRAINT_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.impl.ExcludeConstraintImpl <em>Exclude Constraint</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.impl.ExcludeConstraintImpl
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.impl.FeatureModelPackageImpl#getExcludeConstraint()
	 * @generated
	 */
	int EXCLUDE_CONSTRAINT = 6;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXCLUDE_CONSTRAINT__NAME = CONSTRAINT__NAME;

	/**
	 * The feature id for the '<em><b>Language</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXCLUDE_CONSTRAINT__LANGUAGE = CONSTRAINT__LANGUAGE;

	/**
	 * The feature id for the '<em><b>Code</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXCLUDE_CONSTRAINT__CODE = CONSTRAINT__CODE;

	/**
	 * The feature id for the '<em><b>Excluded Feature A</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXCLUDE_CONSTRAINT__EXCLUDED_FEATURE_A = CONSTRAINT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Excluded Feature B</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXCLUDE_CONSTRAINT__EXCLUDED_FEATURE_B = CONSTRAINT_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Exclude Constraint</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXCLUDE_CONSTRAINT_FEATURE_COUNT = CONSTRAINT_FEATURE_COUNT + 2;

	/**
	 * The number of operations of the '<em>Exclude Constraint</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXCLUDE_CONSTRAINT_OPERATION_COUNT = CONSTRAINT_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.impl.GroupImpl <em>Group</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.impl.GroupImpl
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.impl.FeatureModelPackageImpl#getGroup()
	 * @generated
	 */
	int GROUP = 7;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GROUP__NAME = NAMED_ELEMENT__NAME;

	/**
	 * The feature id for the '<em><b>Features</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GROUP__FEATURES = NAMED_ELEMENT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Group Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GROUP__GROUP_TYPE = NAMED_ELEMENT_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Group</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GROUP_FEATURE_COUNT = NAMED_ELEMENT_FEATURE_COUNT + 2;

	/**
	 * The number of operations of the '<em>Group</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GROUP_OPERATION_COUNT = NAMED_ELEMENT_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.GroupType <em>Group Type</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.GroupType
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.impl.FeatureModelPackageImpl#getGroupType()
	 * @generated
	 */
	int GROUP_TYPE = 8;


	/**
	 * Returns the meta object for class '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.FeatureModel <em>Feature Model</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Feature Model</em>'.
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.FeatureModel
	 * @generated
	 */
	EClass getFeatureModel();

	/**
	 * Returns the meta object for the containment reference list '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.FeatureModel#getComments <em>Comments</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Comments</em>'.
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.FeatureModel#getComments()
	 * @see #getFeatureModel()
	 * @generated
	 */
	EReference getFeatureModel_Comments();

	/**
	 * Returns the meta object for the containment reference list '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.FeatureModel#getConstraints <em>Constraints</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Constraints</em>'.
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.FeatureModel#getConstraints()
	 * @see #getFeatureModel()
	 * @generated
	 */
	EReference getFeatureModel_Constraints();

	/**
	 * Returns the meta object for the attribute '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.FeatureModel#getVersion <em>Version</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Version</em>'.
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.FeatureModel#getVersion()
	 * @see #getFeatureModel()
	 * @generated
	 */
	EAttribute getFeatureModel_Version();

	/**
	 * Returns the meta object for the containment reference '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.FeatureModel#getRoot <em>Root</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Root</em>'.
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.FeatureModel#getRoot()
	 * @see #getFeatureModel()
	 * @generated
	 */
	EReference getFeatureModel_Root();

	/**
	 * Returns the meta object for the containment reference list '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.FeatureModel#getGroups <em>Groups</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Groups</em>'.
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.FeatureModel#getGroups()
	 * @see #getFeatureModel()
	 * @generated
	 */
	EReference getFeatureModel_Groups();

	/**
	 * Returns the meta object for class '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.NamedElement <em>Named Element</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Named Element</em>'.
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.NamedElement
	 * @generated
	 */
	EClass getNamedElement();

	/**
	 * Returns the meta object for the attribute '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.NamedElement#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.NamedElement#getName()
	 * @see #getNamedElement()
	 * @generated
	 */
	EAttribute getNamedElement_Name();

	/**
	 * Returns the meta object for class '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.Comment <em>Comment</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Comment</em>'.
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.Comment
	 * @generated
	 */
	EClass getComment();

	/**
	 * Returns the meta object for the reference '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.Comment#getElement <em>Element</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Element</em>'.
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.Comment#getElement()
	 * @see #getComment()
	 * @generated
	 */
	EReference getComment_Element();

	/**
	 * Returns the meta object for the attribute '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.Comment#getText <em>Text</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Text</em>'.
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.Comment#getText()
	 * @see #getComment()
	 * @generated
	 */
	EAttribute getComment_Text();

	/**
	 * Returns the meta object for class '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.Constraint <em>Constraint</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Constraint</em>'.
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.Constraint
	 * @generated
	 */
	EClass getConstraint();

	/**
	 * Returns the meta object for the attribute '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.Constraint#getLanguage <em>Language</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Language</em>'.
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.Constraint#getLanguage()
	 * @see #getConstraint()
	 * @generated
	 */
	EAttribute getConstraint_Language();

	/**
	 * Returns the meta object for the attribute '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.Constraint#getCode <em>Code</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Code</em>'.
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.Constraint#getCode()
	 * @see #getConstraint()
	 * @generated
	 */
	EAttribute getConstraint_Code();

	/**
	 * Returns the meta object for class '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.Feature <em>Feature</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Feature</em>'.
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.Feature
	 * @generated
	 */
	EClass getFeature();

	/**
	 * Returns the meta object for the attribute '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.Feature#isMandatory <em>Mandatory</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Mandatory</em>'.
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.Feature#isMandatory()
	 * @see #getFeature()
	 * @generated
	 */
	EAttribute getFeature_Mandatory();

	/**
	 * Returns the meta object for the attribute '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.Feature#isAbstract <em>Abstract</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Abstract</em>'.
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.Feature#isAbstract()
	 * @see #getFeature()
	 * @generated
	 */
	EAttribute getFeature_Abstract();

	/**
	 * Returns the meta object for the containment reference list '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.Feature#getChildren <em>Children</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Children</em>'.
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.Feature#getChildren()
	 * @see #getFeature()
	 * @generated
	 */
	EReference getFeature_Children();

	/**
	 * Returns the meta object for the reference list '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.Feature#getRequiredConstraints <em>Required Constraints</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Required Constraints</em>'.
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.Feature#getRequiredConstraints()
	 * @see #getFeature()
	 * @generated
	 */
	EReference getFeature_RequiredConstraints();

	/**
	 * Returns the meta object for the reference list '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.Feature#getRequireConstraints <em>Require Constraints</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Require Constraints</em>'.
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.Feature#getRequireConstraints()
	 * @see #getFeature()
	 * @generated
	 */
	EReference getFeature_RequireConstraints();

	/**
	 * Returns the meta object for the reference '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.Feature#getGroup <em>Group</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Group</em>'.
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.Feature#getGroup()
	 * @see #getFeature()
	 * @generated
	 */
	EReference getFeature_Group();

	/**
	 * Returns the meta object for the reference list '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.Feature#getExcludeConstraintsA <em>Exclude Constraints A</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Exclude Constraints A</em>'.
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.Feature#getExcludeConstraintsA()
	 * @see #getFeature()
	 * @generated
	 */
	EReference getFeature_ExcludeConstraintsA();

	/**
	 * Returns the meta object for the reference list '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.Feature#getExcludeConstraintsB <em>Exclude Constraints B</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Exclude Constraints B</em>'.
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.Feature#getExcludeConstraintsB()
	 * @see #getFeature()
	 * @generated
	 */
	EReference getFeature_ExcludeConstraintsB();

	/**
	 * Returns the meta object for the '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.Feature#atMostInOneGroup(org.eclipse.emf.common.util.DiagnosticChain, java.util.Map) <em>At Most In One Group</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>At Most In One Group</em>' operation.
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.Feature#atMostInOneGroup(org.eclipse.emf.common.util.DiagnosticChain, java.util.Map)
	 * @generated
	 */
	EOperation getFeature__AtMostInOneGroup__DiagnosticChain_Map();

	/**
	 * Returns the meta object for class '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.RequireConstraint <em>Require Constraint</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Require Constraint</em>'.
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.RequireConstraint
	 * @generated
	 */
	EClass getRequireConstraint();

	/**
	 * Returns the meta object for the reference '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.RequireConstraint#getRequiredFeature <em>Required Feature</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Required Feature</em>'.
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.RequireConstraint#getRequiredFeature()
	 * @see #getRequireConstraint()
	 * @generated
	 */
	EReference getRequireConstraint_RequiredFeature();

	/**
	 * Returns the meta object for the reference '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.RequireConstraint#getFeature <em>Feature</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Feature</em>'.
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.RequireConstraint#getFeature()
	 * @see #getRequireConstraint()
	 * @generated
	 */
	EReference getRequireConstraint_Feature();

	/**
	 * Returns the meta object for class '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.ExcludeConstraint <em>Exclude Constraint</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Exclude Constraint</em>'.
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.ExcludeConstraint
	 * @generated
	 */
	EClass getExcludeConstraint();

	/**
	 * Returns the meta object for the reference '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.ExcludeConstraint#getExcludedFeatureA <em>Excluded Feature A</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Excluded Feature A</em>'.
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.ExcludeConstraint#getExcludedFeatureA()
	 * @see #getExcludeConstraint()
	 * @generated
	 */
	EReference getExcludeConstraint_ExcludedFeatureA();

	/**
	 * Returns the meta object for the reference '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.ExcludeConstraint#getExcludedFeatureB <em>Excluded Feature B</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Excluded Feature B</em>'.
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.ExcludeConstraint#getExcludedFeatureB()
	 * @see #getExcludeConstraint()
	 * @generated
	 */
	EReference getExcludeConstraint_ExcludedFeatureB();

	/**
	 * Returns the meta object for class '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.Group <em>Group</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Group</em>'.
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.Group
	 * @generated
	 */
	EClass getGroup();

	/**
	 * Returns the meta object for the reference list '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.Group#getFeatures <em>Features</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Features</em>'.
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.Group#getFeatures()
	 * @see #getGroup()
	 * @generated
	 */
	EReference getGroup_Features();

	/**
	 * Returns the meta object for the attribute '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.Group#getGroupType <em>Group Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Group Type</em>'.
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.Group#getGroupType()
	 * @see #getGroup()
	 * @generated
	 */
	EAttribute getGroup_GroupType();

	/**
	 * Returns the meta object for enum '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.GroupType <em>Group Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Group Type</em>'.
	 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.GroupType
	 * @generated
	 */
	EEnum getGroupType();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	FeatureModelFactory getFeatureModelFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each operation of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.impl.FeatureModelImpl <em>Feature Model</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.impl.FeatureModelImpl
		 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.impl.FeatureModelPackageImpl#getFeatureModel()
		 * @generated
		 */
		EClass FEATURE_MODEL = eINSTANCE.getFeatureModel();

		/**
		 * The meta object literal for the '<em><b>Comments</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference FEATURE_MODEL__COMMENTS = eINSTANCE.getFeatureModel_Comments();

		/**
		 * The meta object literal for the '<em><b>Constraints</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference FEATURE_MODEL__CONSTRAINTS = eINSTANCE.getFeatureModel_Constraints();

		/**
		 * The meta object literal for the '<em><b>Version</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FEATURE_MODEL__VERSION = eINSTANCE.getFeatureModel_Version();

		/**
		 * The meta object literal for the '<em><b>Root</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference FEATURE_MODEL__ROOT = eINSTANCE.getFeatureModel_Root();

		/**
		 * The meta object literal for the '<em><b>Groups</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference FEATURE_MODEL__GROUPS = eINSTANCE.getFeatureModel_Groups();

		/**
		 * The meta object literal for the '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.impl.NamedElementImpl <em>Named Element</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.impl.NamedElementImpl
		 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.impl.FeatureModelPackageImpl#getNamedElement()
		 * @generated
		 */
		EClass NAMED_ELEMENT = eINSTANCE.getNamedElement();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NAMED_ELEMENT__NAME = eINSTANCE.getNamedElement_Name();

		/**
		 * The meta object literal for the '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.impl.CommentImpl <em>Comment</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.impl.CommentImpl
		 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.impl.FeatureModelPackageImpl#getComment()
		 * @generated
		 */
		EClass COMMENT = eINSTANCE.getComment();

		/**
		 * The meta object literal for the '<em><b>Element</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference COMMENT__ELEMENT = eINSTANCE.getComment_Element();

		/**
		 * The meta object literal for the '<em><b>Text</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute COMMENT__TEXT = eINSTANCE.getComment_Text();

		/**
		 * The meta object literal for the '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.impl.ConstraintImpl <em>Constraint</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.impl.ConstraintImpl
		 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.impl.FeatureModelPackageImpl#getConstraint()
		 * @generated
		 */
		EClass CONSTRAINT = eINSTANCE.getConstraint();

		/**
		 * The meta object literal for the '<em><b>Language</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CONSTRAINT__LANGUAGE = eINSTANCE.getConstraint_Language();

		/**
		 * The meta object literal for the '<em><b>Code</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CONSTRAINT__CODE = eINSTANCE.getConstraint_Code();

		/**
		 * The meta object literal for the '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.impl.FeatureImpl <em>Feature</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.impl.FeatureImpl
		 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.impl.FeatureModelPackageImpl#getFeature()
		 * @generated
		 */
		EClass FEATURE = eINSTANCE.getFeature();

		/**
		 * The meta object literal for the '<em><b>Mandatory</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FEATURE__MANDATORY = eINSTANCE.getFeature_Mandatory();

		/**
		 * The meta object literal for the '<em><b>Abstract</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FEATURE__ABSTRACT = eINSTANCE.getFeature_Abstract();

		/**
		 * The meta object literal for the '<em><b>Children</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference FEATURE__CHILDREN = eINSTANCE.getFeature_Children();

		/**
		 * The meta object literal for the '<em><b>Required Constraints</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference FEATURE__REQUIRED_CONSTRAINTS = eINSTANCE.getFeature_RequiredConstraints();

		/**
		 * The meta object literal for the '<em><b>Require Constraints</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference FEATURE__REQUIRE_CONSTRAINTS = eINSTANCE.getFeature_RequireConstraints();

		/**
		 * The meta object literal for the '<em><b>Group</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference FEATURE__GROUP = eINSTANCE.getFeature_Group();

		/**
		 * The meta object literal for the '<em><b>Exclude Constraints A</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference FEATURE__EXCLUDE_CONSTRAINTS_A = eINSTANCE.getFeature_ExcludeConstraintsA();

		/**
		 * The meta object literal for the '<em><b>Exclude Constraints B</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference FEATURE__EXCLUDE_CONSTRAINTS_B = eINSTANCE.getFeature_ExcludeConstraintsB();

		/**
		 * The meta object literal for the '<em><b>At Most In One Group</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation FEATURE___AT_MOST_IN_ONE_GROUP__DIAGNOSTICCHAIN_MAP = eINSTANCE.getFeature__AtMostInOneGroup__DiagnosticChain_Map();

		/**
		 * The meta object literal for the '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.impl.RequireConstraintImpl <em>Require Constraint</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.impl.RequireConstraintImpl
		 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.impl.FeatureModelPackageImpl#getRequireConstraint()
		 * @generated
		 */
		EClass REQUIRE_CONSTRAINT = eINSTANCE.getRequireConstraint();

		/**
		 * The meta object literal for the '<em><b>Required Feature</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference REQUIRE_CONSTRAINT__REQUIRED_FEATURE = eINSTANCE.getRequireConstraint_RequiredFeature();

		/**
		 * The meta object literal for the '<em><b>Feature</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference REQUIRE_CONSTRAINT__FEATURE = eINSTANCE.getRequireConstraint_Feature();

		/**
		 * The meta object literal for the '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.impl.ExcludeConstraintImpl <em>Exclude Constraint</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.impl.ExcludeConstraintImpl
		 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.impl.FeatureModelPackageImpl#getExcludeConstraint()
		 * @generated
		 */
		EClass EXCLUDE_CONSTRAINT = eINSTANCE.getExcludeConstraint();

		/**
		 * The meta object literal for the '<em><b>Excluded Feature A</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EXCLUDE_CONSTRAINT__EXCLUDED_FEATURE_A = eINSTANCE.getExcludeConstraint_ExcludedFeatureA();

		/**
		 * The meta object literal for the '<em><b>Excluded Feature B</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EXCLUDE_CONSTRAINT__EXCLUDED_FEATURE_B = eINSTANCE.getExcludeConstraint_ExcludedFeatureB();

		/**
		 * The meta object literal for the '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.impl.GroupImpl <em>Group</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.impl.GroupImpl
		 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.impl.FeatureModelPackageImpl#getGroup()
		 * @generated
		 */
		EClass GROUP = eINSTANCE.getGroup();

		/**
		 * The meta object literal for the '<em><b>Features</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference GROUP__FEATURES = eINSTANCE.getGroup_Features();

		/**
		 * The meta object literal for the '<em><b>Group Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute GROUP__GROUP_TYPE = eINSTANCE.getGroup_GroupType();

		/**
		 * The meta object literal for the '{@link de.imotep.featuremodel.variability.metamodel.FeatureModel.GroupType <em>Group Type</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.GroupType
		 * @see de.imotep.featuremodel.variability.metamodel.FeatureModel.impl.FeatureModelPackageImpl#getGroupType()
		 * @generated
		 */
		EEnum GROUP_TYPE = eINSTANCE.getGroupType();

	}

} //FeatureModelPackage
