/**
 */
package metrics;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.henshin.model.Rule;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Rule Set Metrics</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link metrics.RuleSetMetrics#getRuleMetrics <em>Rule Metrics</em>}</li>
 *   <li>{@link metrics.RuleSetMetrics#getNumberOfRules <em>Number Of Rules</em>}</li>
 *   <li>{@link metrics.RuleSetMetrics#getRuleSet <em>Rule Set</em>}</li>
 *   <li>{@link metrics.RuleSetMetrics#getTotalNumberOfNodes <em>Total Number Of Nodes</em>}</li>
 *   <li>{@link metrics.RuleSetMetrics#getTotalNumberOfEdges <em>Total Number Of Edges</em>}</li>
 *   <li>{@link metrics.RuleSetMetrics#getTotalNumberOfAttributes <em>Total Number Of Attributes</em>}</li>
 * </ul>
 *
 * @see metrics.MetricsPackage#getRuleSetMetrics()
 * @model
 * @generated
 */
public interface RuleSetMetrics extends EObject {
	/**
	 * Returns the value of the '<em><b>Rule Metrics</b></em>' containment reference list.
	 * The list contents are of type {@link metrics.RuleMetrics}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Rule Metrics</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Rule Metrics</em>' containment reference list.
	 * @see metrics.MetricsPackage#getRuleSetMetrics_RuleMetrics()
	 * @model containment="true"
	 * @generated
	 */
	EList<RuleMetrics> getRuleMetrics();

	/**
	 * Returns the value of the '<em><b>Number Of Rules</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Number Of Rules</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Number Of Rules</em>' attribute.
	 * @see #setNumberOfRules(int)
	 * @see metrics.MetricsPackage#getRuleSetMetrics_NumberOfRules()
	 * @model
	 * @generated
	 */
	int getNumberOfRules();

	/**
	 * Sets the value of the '{@link metrics.RuleSetMetrics#getNumberOfRules <em>Number Of Rules</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Number Of Rules</em>' attribute.
	 * @see #getNumberOfRules()
	 * @generated
	 */
	void setNumberOfRules(int value);

	/**
	 * Returns the value of the '<em><b>Rule Set</b></em>' reference list.
	 * The list contents are of type {@link org.eclipse.emf.henshin.model.Rule}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Rule Set</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Rule Set</em>' reference list.
	 * @see metrics.MetricsPackage#getRuleSetMetrics_RuleSet()
	 * @model
	 * @generated
	 */
	EList<Rule> getRuleSet();

	/**
	 * Returns the value of the '<em><b>Total Number Of Nodes</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Total Number Of Nodes</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Total Number Of Nodes</em>' attribute.
	 * @see #setTotalNumberOfNodes(int)
	 * @see metrics.MetricsPackage#getRuleSetMetrics_TotalNumberOfNodes()
	 * @model
	 * @generated
	 */
	int getTotalNumberOfNodes();

	/**
	 * Sets the value of the '{@link metrics.RuleSetMetrics#getTotalNumberOfNodes <em>Total Number Of Nodes</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Total Number Of Nodes</em>' attribute.
	 * @see #getTotalNumberOfNodes()
	 * @generated
	 */
	void setTotalNumberOfNodes(int value);

	/**
	 * Returns the value of the '<em><b>Total Number Of Edges</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Total Number Of Edges</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Total Number Of Edges</em>' attribute.
	 * @see #setTotalNumberOfEdges(int)
	 * @see metrics.MetricsPackage#getRuleSetMetrics_TotalNumberOfEdges()
	 * @model
	 * @generated
	 */
	int getTotalNumberOfEdges();

	/**
	 * Sets the value of the '{@link metrics.RuleSetMetrics#getTotalNumberOfEdges <em>Total Number Of Edges</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Total Number Of Edges</em>' attribute.
	 * @see #getTotalNumberOfEdges()
	 * @generated
	 */
	void setTotalNumberOfEdges(int value);

	/**
	 * Returns the value of the '<em><b>Total Number Of Attributes</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Total Number Of Attributes</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Total Number Of Attributes</em>' attribute.
	 * @see #setTotalNumberOfAttributes(int)
	 * @see metrics.MetricsPackage#getRuleSetMetrics_TotalNumberOfAttributes()
	 * @model
	 * @generated
	 */
	int getTotalNumberOfAttributes();

	/**
	 * Sets the value of the '{@link metrics.RuleSetMetrics#getTotalNumberOfAttributes <em>Total Number Of Attributes</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Total Number Of Attributes</em>' attribute.
	 * @see #getTotalNumberOfAttributes()
	 * @generated
	 */
	void setTotalNumberOfAttributes(int value);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model
	 * @generated
	 */
	RuleMetrics findRuleMetrics(Rule rule);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model
	 * @generated
	 */
	String createPresentationString();

} // RuleSetMetrics
