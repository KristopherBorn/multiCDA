/**
 */
package metrics;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
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
 * @see metrics.MetricsFactory
 * @model kind="package"
 * @generated
 */
public interface MetricsPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "metrics";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://metrics";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "metrics";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	MetricsPackage eINSTANCE = metrics.impl.MetricsPackageImpl.init();

	/**
	 * The meta object id for the '{@link metrics.impl.RuleSetMetricsImpl <em>Rule Set Metrics</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see metrics.impl.RuleSetMetricsImpl
	 * @see metrics.impl.MetricsPackageImpl#getRuleSetMetrics()
	 * @generated
	 */
	int RULE_SET_METRICS = 0;

	/**
	 * The feature id for the '<em><b>Rule Metrics</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RULE_SET_METRICS__RULE_METRICS = 0;

	/**
	 * The feature id for the '<em><b>Number Of Rules</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RULE_SET_METRICS__NUMBER_OF_RULES = 1;

	/**
	 * The feature id for the '<em><b>Rule Set</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RULE_SET_METRICS__RULE_SET = 2;

	/**
	 * The feature id for the '<em><b>Total Number Of Nodes</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RULE_SET_METRICS__TOTAL_NUMBER_OF_NODES = 3;

	/**
	 * The feature id for the '<em><b>Total Number Of Edges</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RULE_SET_METRICS__TOTAL_NUMBER_OF_EDGES = 4;

	/**
	 * The feature id for the '<em><b>Total Number Of Attributes</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RULE_SET_METRICS__TOTAL_NUMBER_OF_ATTRIBUTES = 5;

	/**
	 * The number of structural features of the '<em>Rule Set Metrics</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RULE_SET_METRICS_FEATURE_COUNT = 6;

	/**
	 * The operation id for the '<em>Find Rule Metrics</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RULE_SET_METRICS___FIND_RULE_METRICS__RULE = 0;

	/**
	 * The operation id for the '<em>Create Presentation String</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RULE_SET_METRICS___CREATE_PRESENTATION_STRING = 1;

	/**
	 * The number of operations of the '<em>Rule Set Metrics</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RULE_SET_METRICS_OPERATION_COUNT = 2;

	/**
	 * The meta object id for the '{@link metrics.impl.RuleMetricsImpl <em>Rule Metrics</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see metrics.impl.RuleMetricsImpl
	 * @see metrics.impl.MetricsPackageImpl#getRuleMetrics()
	 * @generated
	 */
	int RULE_METRICS = 1;

	/**
	 * The feature id for the '<em><b>Number Of Nodes</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RULE_METRICS__NUMBER_OF_NODES = 0;

	/**
	 * The feature id for the '<em><b>Number Of Edges</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RULE_METRICS__NUMBER_OF_EDGES = 1;

	/**
	 * The feature id for the '<em><b>Number Of Attributes</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RULE_METRICS__NUMBER_OF_ATTRIBUTES = 2;

	/**
	 * The feature id for the '<em><b>Rule</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RULE_METRICS__RULE = 3;

	/**
	 * The number of structural features of the '<em>Rule Metrics</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RULE_METRICS_FEATURE_COUNT = 4;

	/**
	 * The number of operations of the '<em>Rule Metrics</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RULE_METRICS_OPERATION_COUNT = 0;


	/**
	 * Returns the meta object for class '{@link metrics.RuleSetMetrics <em>Rule Set Metrics</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Rule Set Metrics</em>'.
	 * @see metrics.RuleSetMetrics
	 * @generated
	 */
	EClass getRuleSetMetrics();

	/**
	 * Returns the meta object for the containment reference list '{@link metrics.RuleSetMetrics#getRuleMetrics <em>Rule Metrics</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Rule Metrics</em>'.
	 * @see metrics.RuleSetMetrics#getRuleMetrics()
	 * @see #getRuleSetMetrics()
	 * @generated
	 */
	EReference getRuleSetMetrics_RuleMetrics();

	/**
	 * Returns the meta object for the attribute '{@link metrics.RuleSetMetrics#getNumberOfRules <em>Number Of Rules</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Number Of Rules</em>'.
	 * @see metrics.RuleSetMetrics#getNumberOfRules()
	 * @see #getRuleSetMetrics()
	 * @generated
	 */
	EAttribute getRuleSetMetrics_NumberOfRules();

	/**
	 * Returns the meta object for the reference list '{@link metrics.RuleSetMetrics#getRuleSet <em>Rule Set</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Rule Set</em>'.
	 * @see metrics.RuleSetMetrics#getRuleSet()
	 * @see #getRuleSetMetrics()
	 * @generated
	 */
	EReference getRuleSetMetrics_RuleSet();

	/**
	 * Returns the meta object for the attribute '{@link metrics.RuleSetMetrics#getTotalNumberOfNodes <em>Total Number Of Nodes</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Total Number Of Nodes</em>'.
	 * @see metrics.RuleSetMetrics#getTotalNumberOfNodes()
	 * @see #getRuleSetMetrics()
	 * @generated
	 */
	EAttribute getRuleSetMetrics_TotalNumberOfNodes();

	/**
	 * Returns the meta object for the attribute '{@link metrics.RuleSetMetrics#getTotalNumberOfEdges <em>Total Number Of Edges</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Total Number Of Edges</em>'.
	 * @see metrics.RuleSetMetrics#getTotalNumberOfEdges()
	 * @see #getRuleSetMetrics()
	 * @generated
	 */
	EAttribute getRuleSetMetrics_TotalNumberOfEdges();

	/**
	 * Returns the meta object for the attribute '{@link metrics.RuleSetMetrics#getTotalNumberOfAttributes <em>Total Number Of Attributes</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Total Number Of Attributes</em>'.
	 * @see metrics.RuleSetMetrics#getTotalNumberOfAttributes()
	 * @see #getRuleSetMetrics()
	 * @generated
	 */
	EAttribute getRuleSetMetrics_TotalNumberOfAttributes();

	/**
	 * Returns the meta object for the '{@link metrics.RuleSetMetrics#findRuleMetrics(org.eclipse.emf.henshin.model.Rule) <em>Find Rule Metrics</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Find Rule Metrics</em>' operation.
	 * @see metrics.RuleSetMetrics#findRuleMetrics(org.eclipse.emf.henshin.model.Rule)
	 * @generated
	 */
	EOperation getRuleSetMetrics__FindRuleMetrics__Rule();

	/**
	 * Returns the meta object for the '{@link metrics.RuleSetMetrics#createPresentationString() <em>Create Presentation String</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Create Presentation String</em>' operation.
	 * @see metrics.RuleSetMetrics#createPresentationString()
	 * @generated
	 */
	EOperation getRuleSetMetrics__CreatePresentationString();

	/**
	 * Returns the meta object for class '{@link metrics.RuleMetrics <em>Rule Metrics</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Rule Metrics</em>'.
	 * @see metrics.RuleMetrics
	 * @generated
	 */
	EClass getRuleMetrics();

	/**
	 * Returns the meta object for the attribute '{@link metrics.RuleMetrics#getNumberOfNodes <em>Number Of Nodes</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Number Of Nodes</em>'.
	 * @see metrics.RuleMetrics#getNumberOfNodes()
	 * @see #getRuleMetrics()
	 * @generated
	 */
	EAttribute getRuleMetrics_NumberOfNodes();

	/**
	 * Returns the meta object for the attribute '{@link metrics.RuleMetrics#getNumberOfEdges <em>Number Of Edges</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Number Of Edges</em>'.
	 * @see metrics.RuleMetrics#getNumberOfEdges()
	 * @see #getRuleMetrics()
	 * @generated
	 */
	EAttribute getRuleMetrics_NumberOfEdges();

	/**
	 * Returns the meta object for the attribute '{@link metrics.RuleMetrics#getNumberOfAttributes <em>Number Of Attributes</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Number Of Attributes</em>'.
	 * @see metrics.RuleMetrics#getNumberOfAttributes()
	 * @see #getRuleMetrics()
	 * @generated
	 */
	EAttribute getRuleMetrics_NumberOfAttributes();

	/**
	 * Returns the meta object for the reference '{@link metrics.RuleMetrics#getRule <em>Rule</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Rule</em>'.
	 * @see metrics.RuleMetrics#getRule()
	 * @see #getRuleMetrics()
	 * @generated
	 */
	EReference getRuleMetrics_Rule();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	MetricsFactory getMetricsFactory();

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
		 * The meta object literal for the '{@link metrics.impl.RuleSetMetricsImpl <em>Rule Set Metrics</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see metrics.impl.RuleSetMetricsImpl
		 * @see metrics.impl.MetricsPackageImpl#getRuleSetMetrics()
		 * @generated
		 */
		EClass RULE_SET_METRICS = eINSTANCE.getRuleSetMetrics();

		/**
		 * The meta object literal for the '<em><b>Rule Metrics</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference RULE_SET_METRICS__RULE_METRICS = eINSTANCE.getRuleSetMetrics_RuleMetrics();

		/**
		 * The meta object literal for the '<em><b>Number Of Rules</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RULE_SET_METRICS__NUMBER_OF_RULES = eINSTANCE.getRuleSetMetrics_NumberOfRules();

		/**
		 * The meta object literal for the '<em><b>Rule Set</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference RULE_SET_METRICS__RULE_SET = eINSTANCE.getRuleSetMetrics_RuleSet();

		/**
		 * The meta object literal for the '<em><b>Total Number Of Nodes</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RULE_SET_METRICS__TOTAL_NUMBER_OF_NODES = eINSTANCE.getRuleSetMetrics_TotalNumberOfNodes();

		/**
		 * The meta object literal for the '<em><b>Total Number Of Edges</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RULE_SET_METRICS__TOTAL_NUMBER_OF_EDGES = eINSTANCE.getRuleSetMetrics_TotalNumberOfEdges();

		/**
		 * The meta object literal for the '<em><b>Total Number Of Attributes</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RULE_SET_METRICS__TOTAL_NUMBER_OF_ATTRIBUTES = eINSTANCE.getRuleSetMetrics_TotalNumberOfAttributes();

		/**
		 * The meta object literal for the '<em><b>Find Rule Metrics</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation RULE_SET_METRICS___FIND_RULE_METRICS__RULE = eINSTANCE.getRuleSetMetrics__FindRuleMetrics__Rule();

		/**
		 * The meta object literal for the '<em><b>Create Presentation String</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation RULE_SET_METRICS___CREATE_PRESENTATION_STRING = eINSTANCE.getRuleSetMetrics__CreatePresentationString();

		/**
		 * The meta object literal for the '{@link metrics.impl.RuleMetricsImpl <em>Rule Metrics</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see metrics.impl.RuleMetricsImpl
		 * @see metrics.impl.MetricsPackageImpl#getRuleMetrics()
		 * @generated
		 */
		EClass RULE_METRICS = eINSTANCE.getRuleMetrics();

		/**
		 * The meta object literal for the '<em><b>Number Of Nodes</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RULE_METRICS__NUMBER_OF_NODES = eINSTANCE.getRuleMetrics_NumberOfNodes();

		/**
		 * The meta object literal for the '<em><b>Number Of Edges</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RULE_METRICS__NUMBER_OF_EDGES = eINSTANCE.getRuleMetrics_NumberOfEdges();

		/**
		 * The meta object literal for the '<em><b>Number Of Attributes</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RULE_METRICS__NUMBER_OF_ATTRIBUTES = eINSTANCE.getRuleMetrics_NumberOfAttributes();

		/**
		 * The meta object literal for the '<em><b>Rule</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference RULE_METRICS__RULE = eINSTANCE.getRuleMetrics_Rule();

	}

} //MetricsPackage
