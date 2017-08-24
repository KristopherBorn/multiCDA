/**
 * <copyright>
 * Copyright (c) 2010-2012 Henshin developers. All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the Eclipse Public License v1.0 which 
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * </copyright>
 */
package de.bigtrafo.benchmark.umledit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.henshin.interpreter.ApplicationMonitor;
import org.eclipse.emf.henshin.interpreter.EGraph;
import org.eclipse.emf.henshin.interpreter.Engine;
import org.eclipse.emf.henshin.interpreter.Match;
import org.eclipse.emf.henshin.interpreter.RuleApplication;
import org.eclipse.emf.henshin.interpreter.impl.BasicApplicationMonitor;
import org.eclipse.emf.henshin.interpreter.impl.EGraphImpl;
import org.eclipse.emf.henshin.interpreter.impl.EngineImpl;
import org.eclipse.emf.henshin.interpreter.impl.RuleApplicationImpl;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.Unit;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;
import org.eclipse.uml2.uml.UMLPackage;

import de.bigtrafo.benchmark.util.MaintainabilityBenchmarkUtil;
import de.bigtrafo.measurement.compactness.RuleSetMetricsCalculator;
import de.imotep.featuremodel.variability.metamodel.FeatureModel.FeatureModel;
import de.imotep.featuremodel.variability.metamodel.FeatureModel.FeatureModelPackage;
import metrics.RuleMetrics;
import metrics.RuleSetMetrics;

public class UmlEditBenchmark {
	private static final String FILE_PATH = "umledit/";
	private static final String FILE_PATH_RULES = "rules/";
	private static final String FILE_NAME_RULES_CLASSIC = ".henshin";

	enum mode {
		CLASSIC
	}

	public static void main(String[] args) {
		Module module = loadModule();
		MaintainabilityBenchmarkUtil.runMaintainabilityBenchmark(module);
	}

	private static Module loadModule() {
		// Create a resource set with a base directory:
		HenshinResourceSet rs = new HenshinResourceSet(FILE_PATH);
		rs.getPackageRegistry().put(UMLPackage.eINSTANCE.getNsURI(), UMLPackage.eINSTANCE);

		Module module1 = null;
		for (String location : getLocations(FILE_PATH + FILE_PATH_RULES)) {
			location = FILE_PATH_RULES + location;
			if (module1 == null) {
				module1 = rs.getModule(location, false);
			} else {
				Module mod = rs.getModule(location, false);
				module1.getUnits().add(mod.getUnits().get(0));
			}
		}

		return module1;
	}

	private static Set<String> getLocations(String path) {
		Set<String> result = new HashSet<String>();
		try {
			Files.walk(Paths.get(path)).filter(Files::isRegularFile)
					.forEach(f -> result.add(f.getParent().getParent().getFileName() + "/" + f.getParent().getFileName()
							+ "/" + f.getFileName()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result.stream().filter(s -> s.endsWith(FILE_NAME_RULES_CLASSIC)).collect(Collectors.toSet());
	}

}
