/**
 * <copyright>
 * Copyright (c) 2010-2014 Henshin developers. All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the Eclipse Public License v1.0 which 
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * </copyright>
 */
package org.eclipse.emf.henshin.model;

import org.eclipse.emf.ecore.EClassifier;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Parameter</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.henshin.model.Parameter#getUnit <em>Unit</em>}</li>
 *   <li>{@link org.eclipse.emf.henshin.model.Parameter#getType <em>Type</em>}</li>
 *   <li>{@link org.eclipse.emf.henshin.model.Parameter#getKind <em>Kind</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.henshin.model.HenshinPackage#getParameter()
 * @model annotation="http://www.eclipse.org/emf/2002/Ecore constraints='nameNotEmpty nameNotTypeName nameNotKindAlias unknownKindDeprecated'"
 * @generated
 */
public interface Parameter extends NamedElement {
	
	/**
	 * Returns the value of the '<em><b>Unit</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link org.eclipse.emf.henshin.model.Unit#getParameters <em>Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Unit</em>' container reference.
	 * @see #setUnit(Unit)
	 * @see org.eclipse.emf.henshin.model.HenshinPackage#getParameter_Unit()
	 * @see org.eclipse.emf.henshin.model.Unit#getParameters
	 * @model opposite="parameters" transient="false"
	 * @generated
	 */
	Unit getUnit();

	/**
	 * Sets the value of the '{@link org.eclipse.emf.henshin.model.Parameter#getUnit <em>Unit</em>}' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Unit</em>' container reference.
	 * @see #getUnit()
	 * @generated
	 */
	void setUnit(Unit value);

	/**
	 * Returns the value of the '<em><b>Type</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Type</em>' reference.
	 * @see #setType(EClassifier)
	 * @see org.eclipse.emf.henshin.model.HenshinPackage#getParameter_Type()
	 * @model
	 * @generated
	 */
	EClassifier getType();

	/**
	 * Sets the value of the '{@link org.eclipse.emf.henshin.model.Parameter#getType <em>Type</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Type</em>' reference.
	 * @see #getType()
	 * @generated
	 */
	void setType(EClassifier value);

	/**
	 * Returns the value of the '<em><b>Kind</b></em>' attribute.
	 * The literals are from the enumeration {@link org.eclipse.emf.henshin.model.ParameterKind}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Kind</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Kind</em>' attribute.
	 * @see org.eclipse.emf.henshin.model.ParameterKind
	 * @see #setKind(ParameterKind)
	 * @see org.eclipse.emf.henshin.model.HenshinPackage#getParameter_Kind()
	 * @model
	 * @generated
	 */
	ParameterKind getKind();

	/**
	 * Sets the value of the '{@link org.eclipse.emf.henshin.model.Parameter#getKind <em>Kind</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Kind</em>' attribute.
	 * @see org.eclipse.emf.henshin.model.ParameterKind
	 * @see #getKind()
	 * @generated
	 */
	void setKind(ParameterKind value);
	
	/**
	 *@generated NOT
	*/
	String toCompactString();
} // Parameter
