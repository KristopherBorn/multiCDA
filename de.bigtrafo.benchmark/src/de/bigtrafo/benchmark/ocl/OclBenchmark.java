package de.bigtrafo.benchmark.ocl;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.henshin.interpreter.ApplicationMonitor;
import org.eclipse.emf.henshin.interpreter.EGraph;
import org.eclipse.emf.henshin.interpreter.Engine;
import org.eclipse.emf.henshin.interpreter.UnitApplication;
import org.eclipse.emf.henshin.interpreter.impl.BasicApplicationMonitor;
import org.eclipse.emf.henshin.interpreter.impl.EGraphImpl;
import org.eclipse.emf.henshin.interpreter.impl.EngineImpl;
import org.eclipse.emf.henshin.interpreter.impl.UnitApplicationImpl;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.Unit;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;
import org.eclipse.emf.henshin.trace.Trace;

import de.bigtrafo.benchmark.util.CorrectnessCheckUtil;
import de.bigtrafo.benchmark.util.RuntimeBenchmarkReport;

public class OclBenchmark {
	private static final String FILE_PATH = "ocl/";
	private static final String FILE_PATH_INSTANCES = "instances/";
	private static final String FILE_PATH_OUTPUT = "output/";
	private static final String FILE_PATH_REFERENCE_OUTPUT = "reference/";
	
	private static final String FILE_NAME_ECORE = "/PetriNetWithOCLPaper.ecore";
	private static final String FILE_NAME_OCLAS = "/PetriNetWithOCLPaper.ecoreecore.oclas";
	private static final String FILE_NAME_RULES = "OCL2NGC.henshin";
	private static final String FILE_NAME_TRACE = "Trace.trace";
	private static final String FILE_EXTENSION_NGC = "graphConstraint";
	
	private static final String PARAMETER_NAME_TRACEROOT = "traceroot";
	private static final String PARAMETER_NAME_INVARIANT = "invariant";
	private static final String PARAMETER_NAME_NGC = "ngc";

	
	enum mode {
		CLASSIC
	}

	/**
	 * Relative path to the model files.
	 */

	public static void main(String[] args) {
		Module module = loadModule();
		// MaintainabilityBenchmarkUtil.runMaintainabilityBenchmark(module);
		String[] examples = {
				 "01", "02", "03",
				"04", "05a", "05b",
				"06", "07", "08", "09"
				};
		int runs = 1;
		RuntimeBenchmarkReport reporter = new RuntimeBenchmarkReport(OclBenchmark.class.getSimpleName(),
				FILE_PATH + FILE_PATH_OUTPUT);
		reporter.start();
		for (String example : examples) {
			for (int i = 0; i < runs; i++) {
				run(example, reporter);
				System.gc();
			}
		}
	}

	/**
	 * Run the benchmark.
	 * @param report 
	 * 
	 * @param path
	 *            Relative path to the model files.
	 * @param iterations
	 *            Number of iterations.
	 */
	public static void run(String exampleID, RuntimeBenchmarkReport report) {
		report.beginNewEntry(exampleID);
		Module module = loadModule();
		HenshinResourceSet resourceSet = (HenshinResourceSet) module.eResource().getResourceSet();

		Unit initUnit = module.getUnit("init");
		Unit mainUnit = module.getUnit("main");

		// Load the model into a graph:
		Resource ecore = resourceSet.getResource(FILE_PATH_INSTANCES + exampleID + FILE_NAME_ECORE);
		EObject root = (EObject) resourceSet.getEObject(FILE_PATH_INSTANCES + exampleID + FILE_NAME_OCLAS);
		EGraph graph = new EGraphImpl(root);
		graph.addTree(ecore.getContents().get(0));
		
		// graph.addGraph(root);

		int graphInitially = graph.size();

		// Create an engine and a rule application:
		Engine engine = new EngineImpl();

		UnitApplication initUnitApplication = new UnitApplicationImpl(engine, graph, initUnit, null);
		ApplicationMonitor monitor = new BasicApplicationMonitor();
		System.gc();

		Trace trace = null;
		EObject nestedGraphConstraint = null;
		BasicEList<EObject> invariants = getInvariants(root);
		if (invariants.size() == 1) {
			initUnitApplication.setParameterValue(PARAMETER_NAME_INVARIANT, invariants.get(0));
			 if (!initUnitApplication.execute(monitor)) {
				 throw new RuntimeException("Error during initialization");
			 }
			nestedGraphConstraint = (EObject) initUnitApplication.getResultParameterValue(PARAMETER_NAME_NGC);
			trace = (Trace) initUnitApplication.getResultParameterValue(PARAMETER_NAME_TRACEROOT);
		} else {
			 throw new RuntimeException("Please specify a test model with exactly one invariant!");
		}

		UnitApplication mainUnitApplication = new UnitApplicationImpl(engine, graph, mainUnit, null);
		monitor = new BasicApplicationMonitor();

		System.gc();

		long startTime = System.currentTimeMillis();
		if (!mainUnitApplication.execute(monitor)) {
			throw new RuntimeException("Error during transformation");
		}
		long runtime = (System.currentTimeMillis() - startTime);
		int graphChanged = graph.size();

		report.finishEntry(graphInitially, graphChanged, runtime);
		
		String resultPath = saveResult(report, exampleID, nestedGraphConstraint, ecore.getContents().get(0), root, trace);
		String referencePath = FILE_PATH + FILE_PATH_REFERENCE_OUTPUT + exampleID;
		CorrectnessCheckUtil.performCorrectnessCheck(resultPath, referencePath, FILE_EXTENSION_NGC, report);
	}

	private static BasicEList<EObject> getInvariants(EObject root) {
		BasicEList<EObject> invariants = new BasicEList<EObject>();
		TreeIterator<EObject> iter = root.eAllContents();
		while (iter.hasNext()) {
			EObject eObject = iter.next();
			if (eObject.eClass().getName().equals("Class")) {
				{
					EStructuralFeature feature = eObject.eClass().getEStructuralFeature("ownedInvariant");
					invariants.addAll((Collection) eObject.eGet(feature));
				}
			}
		}
		return invariants;
	}

	private static Module loadModule() {
		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		Map<String, Object> m = reg.getExtensionToFactoryMap();
		m.put("ecore", new XMIResourceFactoryImpl());
		m.put("oclas", new XMIResourceFactoryImpl());
		m.put("graphConstraint", new XMIResourceFactoryImpl());

		// Create a resource set with a base directory:
		HenshinResourceSet resourceSet = new HenshinResourceSet(FILE_PATH);
		resourceSet.registerDynamicEPackages("Pivot.ecore");
		resourceSet.registerDynamicEPackages("GraphConstraint.ecore");

		// Load the module and find the rule:
		String location = FILE_NAME_RULES;

		Module module = resourceSet.getModule(location, false);
		return module;
	}


	private static String saveResult(RuntimeBenchmarkReport runtimeBenchmarkReport, String exampleID, EObject ngc, EObject ecore, EObject oclas, Trace trace) {
		String outputPath = FILE_PATH + FILE_PATH_OUTPUT + runtimeBenchmarkReport.getDate() + "/" + exampleID;
		
		HenshinResourceSet resourceSet = new HenshinResourceSet(new Path(outputPath).toOSString());
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
		String name = getNameOfNestedGraphConstraint(ngc);
		resourceSet.saveEObject(ecore, FILE_NAME_ECORE);
		resourceSet.saveEObject(oclas, FILE_NAME_OCLAS);
		resourceSet.saveEObject(ngc, name+"."+FILE_EXTENSION_NGC);
		resourceSet.saveEObject(trace, FILE_NAME_TRACE);
		
		return outputPath;
	}

	private static String getNameOfNestedGraphConstraint(EObject ngc) {
		EStructuralFeature nameFeature = ngc.eClass().getEStructuralFeature("name");
		if (nameFeature == null)
			return "No Name";
		else
			return (String) ngc.eGet(nameFeature);
	}

}
