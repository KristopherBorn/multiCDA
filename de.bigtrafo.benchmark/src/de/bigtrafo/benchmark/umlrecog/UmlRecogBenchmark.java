
package de.bigtrafo.benchmark.umlrecog;

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
import org.eclipse.uml2.uml.UMLPackage;

import de.bigtrafo.benchmark.util.CorrectnessCheckUtil;
import de.bigtrafo.benchmark.util.LoadingHelper;
import de.bigtrafo.benchmark.util.RuntimeBenchmarkReport;

public class UmlRecogBenchmark {
	private static final String FILE_PATH = "umlrecog/";
	private static final String FILE_PATH_RULES = "rules";
	private static final String FILE_PATH_INSTANCES = "instances";
	private static final String FILE_PATH_INSTANCES_CORE = "instances/core_model_v2";
	private static final String FILE_PATH_OUTPUT = "output/";
	private static final String FILE_PATH_REFERENCE_OUTPUT = "reference/";
	
	private static final String FILE_NAME_INSTANCE = "bCMS_x_bCMS_UUIDMatcher_lifted_post-processed.symmetric";
	private static final String FILE_EXTENSION_SYMMETRIC = "symmetric";

	enum mode {
		CLASSIC
	}

	public static void main(String[] args) {
		Module module = loadModule();
		// // Uncomment the following line to print statistics about the
		// transformation.
		// MaintainabilityBenchmarkUtil.runMaintainabilityBenchmark(module);

		RuntimeBenchmarkReport reporter = new RuntimeBenchmarkReport(UmlRecogBenchmark.class.getSimpleName(),
				FILE_PATH + FILE_PATH_OUTPUT);
		reporter.start();
		
		List<String> examples = LoadingHelper.getModelLocations(FILE_PATH, FILE_PATH_INSTANCES, FILE_PATH_INSTANCES_CORE,
				FILE_NAME_INSTANCE);

		for (String example : examples) {
			runPerformanceBenchmark(module, example, reporter);
		}
	}

	/**
	 * Run the performance benchmark.
	 * 
	 * @param report
	 * 
	 * @param path
	 *            Relative path to the model files.
	 * @param iterations
	 *            Number of iterations.
	 */
	public static void runPerformanceBenchmark(Module module, String exampleID, RuntimeBenchmarkReport report) {
		report.beginNewEntry(exampleID);
		HenshinResourceSet rs = (HenshinResourceSet) module.eResource().getResourceSet();
	
		// Load the model into a graph:
		EObject instance = rs.getEObject(exampleID + "/" + FILE_NAME_INSTANCE);
		EGraph graph = new EGraphImpl(instance);
		graph.addGraph(instance);
		
		ApplicationMonitor monitor = new BasicApplicationMonitor();
		
		int graphInitially = graph.size();
		Engine engine = new EngineImpl();
		// engine.getOptions().put(Engine. OPTION_SORT_VARIABLES, false);
	
		System.gc();
		long startTime = System.currentTimeMillis();
	
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

	private static Module loadModule() {
		UMLPackage.eINSTANCE.eClass();
		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		Map<String, Object> m = reg.getExtensionToFactoryMap();
		m.put("symmetric", new XMIResourceFactoryImpl());
		m.put("uml", new XMIResourceFactoryImpl());

		// Create a resource set with a base directory:
		HenshinResourceSet rs = new HenshinResourceSet(FILE_PATH);
		EPackage diffPackage = rs.registerDynamicEPackages("Symmetric.ecore").get(0);
		rs.getPackageRegistry().put(diffPackage.getNsURI(), diffPackage);
		rs.getPackageRegistry().put(UMLPackage.eINSTANCE.getNsURI(), UMLPackage.eINSTANCE);

		Module module = LoadingHelper.loadAllRulesAsOneModule(rs, FILE_PATH, FILE_PATH_RULES);

		return module;
	}


	private static String saveResult(RuntimeBenchmarkReport runtimeBenchmarkReport, String exampleID,
			EObject instance) {
		String outputPath = FILE_PATH + FILE_PATH_OUTPUT + 
				//runtimeBenchmarkReport.getDate() + "/" + 
				exampleID;

		HenshinResourceSet resourceSet = new HenshinResourceSet(new Path(outputPath).toOSString());
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
		resourceSet.saveEObject(instance, FILE_NAME_INSTANCE);

		return outputPath;
	}
	
}
