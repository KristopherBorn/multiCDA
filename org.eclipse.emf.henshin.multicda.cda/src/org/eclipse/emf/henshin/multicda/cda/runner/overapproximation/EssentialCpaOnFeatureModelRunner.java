package org.eclipse.emf.henshin.multicda.cda.runner.overapproximation;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.Unit;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;
import org.eclipse.emf.henshin.multicda.cda.runner.Runner;
import org.eclipse.emf.henshin.multicda.cpa.CPAOptions;
import org.eclipse.emf.henshin.multicda.cpa.CpaByAGG;
import org.eclipse.emf.henshin.multicda.cpa.ICriticalPairAnalysis;
import org.eclipse.emf.henshin.multicda.cpa.UnsupportedRuleException;
import org.eclipse.emf.henshin.multicda.cpa.result.CPAResult;
import org.eclipse.emf.henshin.multicda.cpa.result.Conflict;
import org.eclipse.emf.henshin.multicda.cpa.result.ConflictKind;
import org.eclipse.emf.henshin.multicda.cpa.result.CriticalPair;

import de.imotep.featuremodel.variability.metamodel.FeatureModel.FeatureModelPackage;

public class EssentialCpaOnFeatureModelRunner {
	
//	private static String path = "testData\\featureModelingWithoutUpperLimitsOnReferences\\fmedit_noAmalgamation_noNACs_noAttrChange_additionalPreserveManual\\normal_rules\\";

	static String path = "testData\\featureModelingWithoutUpperLimitsOnReferences\\fmedit_noAmalgamation_noNACs_noAttrChange\\rules\\";
	
	public static void main(String[] args){
		
		// VERDAMMT noch mal schon wieder das registrieren!!!!
		FeatureModelPackage.eINSTANCE.eClass(); //TODO(11.04.2017): Gibt es einen programmatischen Weg um generell  

		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		Map<String, Object> m = reg.getExtensionToFactoryMap();
		m.put("xmi", new XMIResourceFactoryImpl());
		
		final File f = new File(Runner.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		String filePath = f.toString();
		String projectPath = filePath.replaceAll("bin", "");

		System.out.println(projectPath);
//		String subDirectoryPath = "testData\\featureModelingWithoutUpperLimitsOnReferences\\fmedit_noAmalgamation_noNACs_noAttrChange_additionalPreserve\\normal_rules\\";
		String fullSubDirectoryPath = projectPath + path;
		
		File dir = new File(fullSubDirectoryPath);
		
//		List<String> filePaths = inspectDirectoryForHenshinFiles(dir);
//		List<File> henshinFiles = inspectDirectoryForHenshinFiles(dir);
		
		
		
		// Aus Runner.java
//		File dir = new File(fullSubDirectoryPath);
		List<String> pathsToHenshinFiles = inspectDirectoryForHenshinFiles(dir);

		LoaderUtil loaderUtil = new LoaderUtil();
		List<Rule> allLoadedRules = loaderUtil.loadAllRulesFromFileSystemPaths(pathsToHenshinFiles);

		System.out.println("number of henshin files: "+pathsToHenshinFiles.size());
		
		
//		List<String> failedRules = new LinkedList<String>();
//		
//		for(File henshinFile : henshinFiles){
//			
//			//load Henshin file
//			
////			String path2 = henshinFile.getPath(); //C:\Users ... t\AddFeatureToGroup_execute.henshin
//			
//			File containingFolder = henshinFile.getParentFile();
//			String absolutePath = containingFolder.getAbsolutePath(); //C:\Users\ ...
////			String path3 = containingFolder.getPath(); //C:\Users\ ...
//			
//			String henshinFileName = henshinFile.getName(); // AddFeatureToGroup_execute.henshin
//			
////			System.out.println("test");
//			
////			int lastIndexOf1 = path.lastIndexOf(".", path.length());
////			int lastIndexOf2 = path.lastIndexOf("\\");
////			String path = 
////			String filename = 
////			path.la
//			
////			// Create a resource set with a base directory:
//			HenshinResourceSet resourceSet = new HenshinResourceSet(absolutePath);
////			
////			// Load the module:
//			Module module = resourceSet.getModule(henshinFileName, false);
//						
//			//TODO: filter rules
//			EList<Unit> units = module.getUnits();
//			Rule theRule = null;
//			boolean multipleRules = false;
//			for(Unit unit : units){
//				if(unit instanceof Rule){
////					if(theRule != null)
////						multipleRules = true;
//					multipleRules = (theRule != null) ?  true : false;
//					theRule = (Rule) unit;
//					rules.add(theRule);
//				}
//			}
//			if(multipleRules)
//				System.err.println("amount of rules: "+units.size());
//		}
		
//		ICriticalPairAnalysis cpaByAGG = new CpaByAGG();
//		
//		CPAOptions options = new CPAOptions();
//		options.setEssential(true);
//		try {
//			cpaByAGG.init(allLoadedRules, options);
//		} catch (UnsupportedRuleException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
//		TODO: introduce a method to set "options" on AGG CPA!
		Runner runner = new Runner();
		runner.setAnalysisKinds(false, false, true, false, false, false);
		List<String> deactivatedRules = new LinkedList<String>(); //TODO(11.04.2017): langfristig diesen störenden Parameter per Refactoring entfernen
		runner.run(fullSubDirectoryPath, deactivatedRules);
		
		CPAResult essentialCpaResults = runner.getEssentialCpaResults();
		
		CPAResult filteredConflicts = filterConflicts(essentialCpaResults);
		
		System.out.println("number of filtered conflicts: "+filteredConflicts.getEssentialCriticalPairs().size());
			
	}
	
	private static CPAResult filterConflicts(CPAResult conflicts) {
		CPAResult filteredResult = new CPAResult();
		System.err.println("results before filtering: "+conflicts.getEssentialCriticalPairs().size());
		for(CriticalPair cp : conflicts.getEssentialCriticalPairs()){
			if(cp instanceof Conflict){
				Conflict conflict = (Conflict) cp;
				if(conflict.getConflictKind() == ConflictKind.DELETE_USE_CONFLICT){
					filteredResult.addResult(conflict);
				}
			}
		}
		System.err.println("results after filtering: "+filteredResult.getEssentialCriticalPairs().size());
		return filteredResult;
	}

	// load normal rules Rules
	
	
	
	
	// use essentialCPA Export
	
	// run essential CPA for conflicts
	
	// extract delete-use-conflicts
	
	
//		private static List<File> inspectDirectoryForHenshinFiles(File dir) {
//			List<File> henshinFiles = new LinkedList<File>();
//			File[] directoryListing = dir.listFiles();
//			if (directoryListing != null) {
//				for (File child : directoryListing) {
////					System.out.println("TODO: recursive call of exploration method");
//					String fileName = child.getName();
//					if (fileName.endsWith(".henshin")) {
//						henshinFiles.add(child);
//					} else if (!child.getName().contains(".")) {
//						File subDir = child;
//						henshinFiles.addAll(inspectDirectoryForHenshinFiles(subDir));
//					}
//				}
//			} else {
//				// Handle the case where dir is not really a directory.
//				// Checking dir.isDirectory() above would not be sufficient
//				// to avoid race conditions with another process that deletes
//				// directories.
//			}
//			return henshinFiles;
//		}

		private static List<String> inspectDirectoryForHenshinFiles(File dir) {
			List<String> pathsToHenshinFiles = new LinkedList<String>();
			File[] directoryListing = dir.listFiles();
			if (directoryListing != null) {
				for (File child : directoryListing) {
					System.out.println("TODO: recursive call of exploration method");
					String fileName = child.getName();
					if (fileName.endsWith(".henshin")) {
						pathsToHenshinFiles.add(child.getAbsolutePath());
					} else if (!child.getName().contains(".")) {
						File subDir = child;
						pathsToHenshinFiles.addAll(inspectDirectoryForHenshinFiles(subDir));
					}
				}
			} else {
				// Handle the case where dir is not really a directory.
				// Checking dir.isDirectory() above would not be sufficient
				// to avoid race conditions with another process that deletes
				// directories.
			}
			return pathsToHenshinFiles;
		}
		
		
		// (10.04.2017) scheint als statische Methode zu Problemen beim laden/auflösen der imports eines Moduls zu führen!
//		private List<Rule> loadAllRulesFromFileSystemPaths(List<String> pathsToHenshinFiles) {
//			List<Rule> allEditRulesWithoutAmalgamation = new LinkedList<Rule>();
//
//			for (String pathToHenshinFiles : pathsToHenshinFiles) {
//				HenshinResourceSet henshinResourceSet = new HenshinResourceSet();
//				Module module = henshinResourceSet.getModule(pathToHenshinFiles, true);
//				for (Unit unit : module.getUnits()) {
//					if (unit instanceof Rule /* && numberOfAddedRules<10 */) {
//						// rulesAndAssociatedFileNames.put((Rule) unit, fileName);
////						boolean deactivatedRule = false;
////						for (String deactivatedRuleName : namesOfDeactivatedRules) {
////							if (unit.getName().contains(deactivatedRuleName))
////								deactivatedRule = true;
////						}
////						if (!deactivatedRule) {
//							allEditRulesWithoutAmalgamation.add((Rule) unit);
////						}
//					}
//				}
//			}
//			return allEditRulesWithoutAmalgamation;
//		}
}
