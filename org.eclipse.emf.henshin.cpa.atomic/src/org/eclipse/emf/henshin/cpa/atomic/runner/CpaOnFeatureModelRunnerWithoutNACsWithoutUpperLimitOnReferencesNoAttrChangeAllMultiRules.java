package org.eclipse.emf.henshin.cpa.atomic.runner;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import de.imotep.featuremodel.variability.metamodel.FeatureModel.FeatureModelPackage;

public class CpaOnFeatureModelRunnerWithoutNACsWithoutUpperLimitOnReferencesNoAttrChangeAllMultiRules extends Runner{
	
	// Relative path to the transformations.
//	static String TRANSFORMATIONS = "transformations/"; //überflüssig
	
//	private static Engine engine;// = new EngineImpl(); //überflüssig
	
//	private static Module module; //überflüssig
	
//	private static HenshinResourceSet henshinResourceSet; // überflüssig

	public static void main(String args[]){
		System.out.println("test");
		FeatureModelPackage.eINSTANCE.eClass();

		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		Map<String, Object> m = reg.getExtensionToFactoryMap();
		m.put("xmi", new XMIResourceFactoryImpl());

		ResourceSetImpl resourceSet = new ResourceSetImpl();

		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore",
				new EcoreResourceFactoryImpl());
		
		List<String> deactivatedRules = new LinkedList<String>();
		final File f = new File(Runner.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		String filePath = f.toString();

		String projectPath = filePath.replaceAll("bin", "");
		System.out.println(projectPath);
		String subDirectoryPath = "testData\\featureModelingWithoutUpperLimitsOnReferences\\fmedit_noAmalgamation_noNACs_noAttrChange_additionalPreserveProgrammatic\\normal_rules\\";
		String fullSubDirectoryPath = projectPath + subDirectoryPath;
		
		Runner runner = new Runner();
		runner.setNoApplicationConditions(true);
		runner.setNoMultirules(true);
		runner.setAnalysisKinds(false, false, false, false, true, false);
//		runner.limitSetOfRulesByRuleNames(limitedSetOfRulesByRuleNames);
		runner.run(fullSubDirectoryPath, deactivatedRules);
	}
}
