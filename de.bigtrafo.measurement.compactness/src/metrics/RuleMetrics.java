/**
 */
package metrics;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.henshin.model.Rule;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Rule Metrics</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link metrics.RuleMetrics#getNumberOfNodes <em>Number Of Nodes</em>}</li>
 *   <li>{@link metrics.RuleMetrics#getNumberOfEdges <em>Number Of Edges</em>}</li>
 *   <li>{@link metrics.RuleMetrics#getNumberOfAttributes <em>Number Of Attributes</em>}</li>
 *   <li>{@link metrics.RuleMetrics#getRule <em>Rule</em>}</li>
 * </ul>
 *
 * @see metrics.MetricsPackage#getRuleMetrics()
 * @model
 * @generated
 */
public interface RuleMetrics extends EObject {
	/**
	 * Returns the value of the '<em><b>Number Of Nodes</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Number Of Nodes</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Number Of Nodes</em>' attribute.
	 * @see #setNumberOfNodes(int)
	 * @see metrics.MetricsPackage#getRuleMetrics_NumberOfNodes()
	 * @model
	 * @generated
	 */
	int getNumberOfNodes();

	/**
	 * Sets the value of the '{@link metrics.RuleMetrics#getNumberOfNodes <em>Number Of Nodes</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Number Of Nodes</em>' attribute.
	 * @see #getNumberOfNodes()
	 * @generated
	 */
	void setNumberOfNodes(int value);

	/**
	 * Returns the value of the '<em><b>Number Of Edges</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Number Of Edges</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Number Of Edges</em>' attribute.
	 * @see #setNumberOfEdges(int)
	 * @see metrics.MetricsPackage#getRuleMetrics_NumberOfEdges()
	 * @model
	 * @generated
	 */
	int getNumberOfEdges();

	/**
	 * Sets the value of the '{@link metrics.RuleMetrics#getNumberOfEdges <em>Number Of Edges</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Number Of Edges</em>' attribute.
	 * @see #getNumberOfEdges()
	 * @generated
	 */
	void setNumberOfEdges(int value);

	/**
	 * Returns the value of the '<em><b>Number Of Attributes</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Number Of Attributes</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Number Of Attributes</em>' attribute.
	 * @see #setNumberOfAttributes(int)
	 * @see metrics.MetricsPackage#getRuleMetrics_NumberOfAttributes()
	 * @model
	 * @generated
	 */
	int getNumberOfAttributes();

	/**
	 * Sets the value of the '{@link metrics.RuleMetrics#getNumberOfAttributes <em>Number Of Attributes</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Number Of Attributes</em>' attribute.
	 * @see #getNumberOfAttributes()
	 * @generated
	 */
	void setNumberOfAttributes(int value);

	/**
	 * Returns the value of the '<em><b>Rule</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Rule</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Rule</em>' reference.
	 * @see #setRule(Rule)
	 * @see metrics.MetricsPackage#getRuleMetrics_Rule()
	 * @model
	 * @generated
	 */
	Rule getRule();

	/**
	 * Sets the value of the '{@link metrics.RuleMetrics#getRule <em>Rule</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Rule</em>' reference.
	 * @see #getRule()
	 * @generated
	 */
	void setRule(Rule value);


	String createPresentationString();
} // RuleMetrics
