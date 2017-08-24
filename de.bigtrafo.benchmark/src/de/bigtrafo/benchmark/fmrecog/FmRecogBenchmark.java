/**
 * <copyright>
 * Copyright (c) 2010-2012 Henshin developers. All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the Eclipse Public License v1.0 which 
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * </copyright>
 */
package de.bigtrafo.benchmark.fmrecog;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.henshin.interpreter.ApplicationMonitor;
import org.eclipse.emf.henshin.interpreter.EGraph;
import org.eclipse.emf.henshin.interpreter.Engine;
import org.eclipse.emf.henshin.interpreter.Match;
import org.eclipse.emf.henshin.interpreter.UnitApplication;
import org.eclipse.emf.henshin.interpreter.impl.BasicApplicationMonitor;
import org.eclipse.emf.henshin.interpreter.impl.EGraphImpl;
import org.eclipse.emf.henshin.interpreter.impl.EngineImpl;
import org.eclipse.emf.henshin.interpreter.impl.UnitApplicationImpl;
import org.eclipse.emf.henshin.interpreter.util.InterpreterUtil;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.Unit;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;

import de.bigtrafo.benchmark.util.CorrectnessCheckUtil;
import de.bigtrafo.benchmark.util.LoadingHelper;
import de.bigtrafo.benchmark.util.RuntimeBenchmarkReport;
import de.imotep.featuremodel.variability.metamodel.FeatureModel.FeatureModelPackage;

public class FmRecogBenchmark {

	private static final String FILE_PATH = "fmrecog/";
	private static final String FILE_PATH_RULES = "rules";
	private static final String FILE_PATH_INSTANCE = "instances/";
	private static final String FILE_PATH_OUTPUT = "output/";
	private static final String FILE_PATH_REFERENCE_OUTPUT = "reference/";

	private static final String FILE_NAME_DIFF = "1_x_2_FeatureModelMatcher_lifted_post-processed.symmetric";
	private static final String FILE_EXTENSION_SYMMETRIC = "symmetric";

	enum mode {
		CLASSIC
	}

	public static void main(String[] args) {
		//// Uncomment the following lines to print statistics about the
		//// transformation.
		// Module module = loadModule();
		// MaintainabilityBenchmarkUtil.runMaintainabilityBenchmark(module);

		String[] examples = { "opvar_05_var1", "opvar_10_var2", "opvar_15_var3", "opvar_20_var4", "opvar_25_var5",
				"sizevar_100_var1", "sizevar_100_var2", "sizevar_100_var3", "sizevar_200_var1", "sizevar_200_var2",
				"sizevar_200_var3", "sizevar_300_var1", "sizevar_300_var2", "sizevar_300_var3", "sizevar_300_var4",
				"sizevar_300_var5", "sizevar_400_var1", "sizevar_400_var2", "sizevar_400_var3", "sizevar_400_var4",
				"sizevar_400_var5", "sizevar_500_var1", "sizevar_500_var2", "sizevar_500_var3", "sizevar_500_var4",
				"sizevar_500_var5" };

		int runs = 1;
		RuntimeBenchmarkReport reporter = new RuntimeBenchmarkReport(FmRecogBenchmark.class.getSimpleName(),
				FILE_PATH + FILE_PATH_OUTPUT);
		reporter.start();
		for (String example : examples) {
			for (int i = 0; i < runs; i++) {
				runPerformanceBenchmark(example, reporter);
				System.gc();
			}
		}
	}

	/**
	 * Run the performance benchmark.
	 * 
	 * @param path
	 *            Relative path to the model files.
	 * @param iterations
	 *            Number of iterations.
	 */
	public static void runPerformanceBenchmark(String exampleID, RuntimeBenchmarkReport report) {
		report.beginNewEntry(exampleID);

		HenshinResourceSet rs = prepareResourceSet(FILE_PATH);
		Module module = loadModule(rs);
		EObject instance = rs.getEObject(FILE_PATH_INSTANCE + exampleID + "/" + FILE_NAME_DIFF);

		EGraph graph = new EGraphImpl();
		graph.addGraph(instance);

		long startTime = System.currentTimeMillis();

		ApplicationMonitor monitor = new BasicApplicationMonitor();
		int graphInitially = graph.size();
		Engine engine = new EngineImpl();
		// engine.getOptions().put(Engine. OPTION_SORT_VARIABLES, false);

		System.gc();
		startTime = System.currentTimeMillis();

		for (Unit unit : module.getUnits()) {
			Rule rule = (Rule) unit;
			long currentRunTime = System.currentTimeMillis();
			int graphCurrent = graph.size();

			List<Match> matches = InterpreterUtil.findAllMatches(engine, rule, graph, null);
			for (Match m : matches) {
				UnitApplication mainUnitApplication = new UnitApplicationImpl(engine, graph, unit, m);
				mainUnitApplication.execute(monitor);
			}

			long runtime = (System.currentTimeMillis() - currentRunTime);
			int graphChanged = graph.size();
			report.addSubEntry(unit, graphCurrent, graphChanged, runtime);
		}

		long runtime = (System.currentTimeMillis() - startTime);
		int graphChanged = graph.size();

		report.finishEntry(graphInitially, graphChanged, runtime);

		String resultPath = saveResult(report, exampleID, instance);
		String referencePath = FILE_PATH + FILE_PATH_REFERENCE_OUTPUT + exampleID;
		CorrectnessCheckUtil.performCorrectnessCheck(resultPath, referencePath, FILE_EXTENSION_SYMMETRIC, report);
	}

	private static String saveResult(RuntimeBenchmarkReport runtimeBenchmarkReport, String exampleID,
			EObject instance) {
		String outputPath = FILE_PATH + FILE_PATH_OUTPUT + runtimeBenchmarkReport.getDate() + "/" + exampleID;

		HenshinResourceSet resourceSet = new HenshinResourceSet(new Path(outputPath).toOSString());
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
		resourceSet.saveEObject(instance, FILE_NAME_DIFF);

		return outputPath;
	}

	private static Module loadModule(HenshinResourceSet rs) {
		Module module = LoadingHelper.loadAllRulesAsOneModule(rs, FILE_PATH, FILE_PATH_RULES);
		return module;
	}

	private static HenshinResourceSet prepareResourceSet(String path) {
		FeatureModelPackage.eINSTANCE.eClass();
		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		Map<String, Object> m = reg.getExtensionToFactoryMap();
		m.put("symmetric", new XMIResourceFactoryImpl());
		m.put("featuremodel", new XMIResourceFactoryImpl());

		// Create a resource set with a base directory:
		HenshinResourceSet rs = new HenshinResourceSet(path);
		EPackage diffPackage = rs.registerDynamicEPackages("Symmetric.ecore").get(0);
		rs.getPackageRegistry().put(diffPackage.getNsURI(), diffPackage);
		rs.getPackageRegistry().put(FeatureModelPackage.eINSTANCE.getNsURI(), FeatureModelPackage.eINSTANCE);
		return rs;
	}

}
