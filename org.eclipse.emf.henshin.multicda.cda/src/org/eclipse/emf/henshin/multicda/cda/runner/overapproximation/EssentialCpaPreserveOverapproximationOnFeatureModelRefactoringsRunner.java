package org.eclipse.emf.henshin.multicda.cda.runner.overapproximation;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.multicda.cda.runner.Runner;
import org.eclipse.emf.henshin.preprocessing.RuleSetModifier;

import de.imotep.featuremodel.variability.metamodel.FeatureModel.FeatureModelPackage;

public class EssentialCpaPreserveOverapproximationOnFeatureModelRefactoringsRunner {
	
	// (25.04.2017) tested! working fine! 
	// results: ess.CPA: 1018 del.-use-confl; second rule del->preserve ess.CPA: 1758 del.-use-confl
	
	public static void main(String args[]){
		
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
		String subDirectoryPath = "testData\\featureModelingWithoutUpperLimitsOnReferences\\fmedit_noAmalgamation_noNACs_noAttrChange_additionalPreserveProgrammatic\\";
		String resultPath = projectPath + subDirectoryPath;
		
		String fullSubDirectoryPathOfOriginalRules = projectPath + subDirectoryPath + "normal_rules\\";
		
		// load normal rules
		File dirOriginal = new File(fullSubDirectoryPathOfOriginalRules);
		List<String> pathsToOriginalHenshinFiles = Runner.inspectDirectoryForHenshinFiles(dirOriginal);
		List<Rule> allLoadedOriginalRules = Runner.loadAllRulesFromFileSystemPaths(pathsToOriginalHenshinFiles, deactivatedRules);
		
		// load preserve rule 
		String fullSubDirectoryPathOfModifiedRules = projectPath + subDirectoryPath + "preserve_rules\\";
		File dirModified = new File(fullSubDirectoryPathOfModifiedRules);
		List<String> pathsToModifiedHenshinFiles = Runner.inspectDirectoryForHenshinFiles(dirModified);
		List<Rule> allLoadedModifiedRules = Runner.loadAllRulesFromFileSystemPaths(pathsToModifiedHenshinFiles, deactivatedRules);
		
//		List<Rule> allLoadedModifiedRules = RuleSetModifier.getDeleteToPreserveCopyOfRules(allLoadedOriginalRules, true);
		
		EssentialCpaPreserveOverapproximationUtil.analyseEssentialCpaPreserveOverapproximation(allLoadedOriginalRules, allLoadedModifiedRules, resultPath);
	}
}
