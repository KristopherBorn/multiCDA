package org.eclipse.emf.henshin.cpa.atomic.runner;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import de.imotep.featuremodel.variability.metamodel.FeatureModel.FeatureModelPackage;

public class CpaOnFeatureModelRunner extends Runner{
	
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
//		deactivatedRules.add("Refactoring_1-4"); // bringt ähnlich gute ERgebnisse wie die anderen Refactorings, ist daher also bei Bedarf verzichtbar
		deactivatedRules.add("Refactoring_1-9"); // most likely DEADLOCK in Atmoic CPA!
		deactivatedRules.add("deleteGroup_IN_FeatureModel"); // most likely DEADLOCK in Atmoic CPA!
//		deactivatedRules.add("removeFromGroup_features_Feature"); // bringt auf für essential und atomic halbwegs interessante Ergebnisse
		deactivatedRules.add("PushDownGroup"); //WICHTIG!: SEHR INTERESSANT FÜR ATOMIC UND ESSENTIAL! LEider nicht mit normaler CPA ausführbar :-/
		
		final File f = new File(Runner.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		String filePath = f.toString();
		// String shortendPath = filePath.replaceAll("org.eclipse.emf.henshin.cpa.atomic.main\\bin", "");
		String projectPath = filePath.replaceAll("bin", "");
		// String shortendPath0 = filePath.replaceAll("bin", "");
		// String shortendPath1 = shortendPath0.substring(0, shortendPath0.length()-1);
		// String projectPath = shortendPath1.replaceAll("org.eclipse.emf.henshin.cpa.atomic.main", "");
		// String shortendPath = filePath.replaceAll("bin", "");
		System.out.println(projectPath);
		String subDirectoryPath = "testData\\featureModeling\\fmedit_noAmalgamation\\rules\\";
		String fullSubDirectoryPath = projectPath + subDirectoryPath;
		
		Runner runner = new Runner();
		runner.setAnalysisKinds(false, false, true, false, false, false);
		runner.run(fullSubDirectoryPath, deactivatedRules);
	}
}
