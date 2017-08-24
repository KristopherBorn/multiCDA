/**
 */
package metrics.impl;

import metrics.MetricsPackage;
import metrics.RuleMetrics;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import org.eclipse.emf.henshin.model.Rule;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Rule Metrics</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link metrics.impl.RuleMetricsImpl#getNumberOfNodes <em>Number Of Nodes</em>}</li>
 *   <li>{@link metrics.impl.RuleMetricsImpl#getNumberOfEdges <em>Number Of Edges</em>}</li>
 *   <li>{@link metrics.impl.RuleMetricsImpl#getNumberOfAttributes <em>Number Of Attributes</em>}</li>
 *   <li>{@link metrics.impl.RuleMetricsImpl#getRule <em>Rule</em>}</li>
 * </ul>
 *
 * @generated
 */
public class RuleMetricsImpl extends MinimalEObjectImpl.Container implements RuleMetrics {
	/**
	 * The default value of the '{@link #getNumberOfNodes() <em>Number Of Nodes</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNumberOfNodes()
	 * @generated
	 * @ordered
	 */
	protected static final int NUMBER_OF_NODES_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getNumberOfNodes() <em>Number Of Nodes</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNumberOfNodes()
	 * @generated
	 * @ordered
	 */
	protected int numberOfNodes = NUMBER_OF_NODES_EDEFAULT;

	/**
	 * The default value of the '{@link #getNumberOfEdges() <em>Number Of Edges</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNumberOfEdges()
	 * @generated
	 * @ordered
	 */
	protected static final int NUMBER_OF_EDGES_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getNumberOfEdges() <em>Number Of Edges</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNumberOfEdges()
	 * @generated
	 * @ordered
	 */
	protected int numberOfEdges = NUMBER_OF_EDGES_EDEFAULT;

	/**
	 * The default value of the '{@link #getNumberOfAttributes() <em>Number Of Attributes</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNumberOfAttributes()
	 * @generated
	 * @ordered
	 */
	protected static final int NUMBER_OF_ATTRIBUTES_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getNumberOfAttributes() <em>Number Of Attributes</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNumberOfAttributes()
	 * @generated
	 * @ordered
	 */
	protected int numberOfAttributes = NUMBER_OF_ATTRIBUTES_EDEFAULT;

	/**
	 * The cached value of the '{@link #getRule() <em>Rule</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRule()
	 * @generated
	 * @ordered
	 */
	protected Rule rule;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected RuleMetricsImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MetricsPackage.Literals.RULE_METRICS;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getNumberOfNodes() {
		return numberOfNodes;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setNumberOfNodes(int newNumberOfNodes) {
		int oldNumberOfNodes = numberOfNodes;
		numberOfNodes = newNumberOfNodes;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MetricsPackage.RULE_METRICS__NUMBER_OF_NODES, oldNumberOfNodes, numberOfNodes));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getNumberOfEdges() {
		return numberOfEdges;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setNumberOfEdges(int newNumberOfEdges) {
		int oldNumberOfEdges = numberOfEdges;
		numberOfEdges = newNumberOfEdges;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MetricsPackage.RULE_METRICS__NUMBER_OF_EDGES, oldNumberOfEdges, numberOfEdges));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getNumberOfAttributes() {
		return numberOfAttributes;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setNumberOfAttributes(int newNumberOfAttributes) {
		int oldNumberOfAttributes = numberOfAttributes;
		numberOfAttributes = newNumberOfAttributes;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MetricsPackage.RULE_METRICS__NUMBER_OF_ATTRIBUTES, oldNumberOfAttributes, numberOfAttributes));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Rule getRule() {
		if (rule != null && rule.eIsProxy()) {
			InternalEObject oldRule = (InternalEObject)rule;
			rule = (Rule)eResolveProxy(oldRule);
			if (rule != oldRule) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, MetricsPackage.RULE_METRICS__RULE, oldRule, rule));
			}
		}
		return rule;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Rule basicGetRule() {
		return rule;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRule(Rule newRule) {
		Rule oldRule = rule;
		rule = newRule;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MetricsPackage.RULE_METRICS__RULE, oldRule, rule));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case MetricsPackage.RULE_METRICS__NUMBER_OF_NODES:
				return getNumberOfNodes();
			case MetricsPackage.RULE_METRICS__NUMBER_OF_EDGES:
				return getNumberOfEdges();
			case MetricsPackage.RULE_METRICS__NUMBER_OF_ATTRIBUTES:
				return getNumberOfAttributes();
			case MetricsPackage.RULE_METRICS__RULE:
				if (resolve) return getRule();
				return basicGetRule();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case MetricsPackage.RULE_METRICS__NUMBER_OF_NODES:
				setNumberOfNodes((Integer)newValue);
				return;
			case MetricsPackage.RULE_METRICS__NUMBER_OF_EDGES:
				setNumberOfEdges((Integer)newValue);
				return;
			case MetricsPackage.RULE_METRICS__NUMBER_OF_ATTRIBUTES:
				setNumberOfAttributes((Integer)newValue);
				return;
			case MetricsPackage.RULE_METRICS__RULE:
				setRule((Rule)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case MetricsPackage.RULE_METRICS__NUMBER_OF_NODES:
				setNumberOfNodes(NUMBER_OF_NODES_EDEFAULT);
				return;
			case MetricsPackage.RULE_METRICS__NUMBER_OF_EDGES:
				setNumberOfEdges(NUMBER_OF_EDGES_EDEFAULT);
				return;
			case MetricsPackage.RULE_METRICS__NUMBER_OF_ATTRIBUTES:
				setNumberOfAttributes(NUMBER_OF_ATTRIBUTES_EDEFAULT);
				return;
			case MetricsPackage.RULE_METRICS__RULE:
				setRule((Rule)null);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case MetricsPackage.RULE_METRICS__NUMBER_OF_NODES:
				return numberOfNodes != NUMBER_OF_NODES_EDEFAULT;
			case MetricsPackage.RULE_METRICS__NUMBER_OF_EDGES:
				return numberOfEdges != NUMBER_OF_EDGES_EDEFAULT;
			case MetricsPackage.RULE_METRICS__NUMBER_OF_ATTRIBUTES:
				return numberOfAttributes != NUMBER_OF_ATTRIBUTES_EDEFAULT;
			case MetricsPackage.RULE_METRICS__RULE:
				return rule != null;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (numberOfNodes: ");
		result.append(numberOfNodes);
		result.append(", numberOfEdges: ");
		result.append(numberOfEdges);
		result.append(", numberOfAttributes: ");
		result.append(numberOfAttributes);
		result.append(')');
		return result.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public String createPresentationString() {

		StringBuffer result = new StringBuffer();

		result.append("#Elements:\t");
		result.append(numberOfNodes);
		result.append("\t");
		result.append(numberOfEdges);
		result.append("\t");
		result.append(numberOfAttributes);
		return result.toString();
	}
} //RuleMetricsImpl
