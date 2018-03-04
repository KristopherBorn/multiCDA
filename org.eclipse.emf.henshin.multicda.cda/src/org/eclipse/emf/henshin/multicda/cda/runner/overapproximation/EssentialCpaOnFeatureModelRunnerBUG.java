package org.eclipse.emf.henshin.multicda.cda.runner.overapproximation;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
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

public class EssentialCpaOnFeatureModelRunnerBUG {
	
	private static String path = "testData\\featureModelingWithoutUpperLimitsOnReferences\\fmedit_noAmalgamation_noNACs_noAttrChange_additionalPreserveProgrammatic\\normal_rules\\";
	
	public static void main(String[] args){
		
		List<Rule> rules = new LinkedList<Rule>();
		
		final File f = new File(Runner.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		String filePath = f.toString();
		// String shortendPath = filePath.replaceAll("org.eclipse.emf.henshin.multicda.cda\\bin", "");
		String projectPath = filePath.replaceAll("bin", "");
		// String shortendPath0 = filePath.replaceAll("bin", "");
		// String shortendPath1 = shortendPath0.substring(0, shortendPath0.length()-1);
		// String projectPath = shortendPath1.replaceAll("org.eclipse.emf.henshin.multicda.cda", "");
		// String shortendPath = filePath.replaceAll("bin", "");
		System.out.println(projectPath);
//		String subDirectoryPath = "testData\\featureModelingWithoutUpperLimitsOnReferences\\fmedit_noAmalgamation_noNACs_noAttrChange_additionalPreserve\\normal_rules\\";
		String fullSubDirectoryPath = projectPath + path;
		
		File dir = new File(fullSubDirectoryPath);
		
//		List<String> filePaths = inspectDirectoryForHenshinFiles(dir);
		List<File> henshinFiles = inspectDirectoryForHenshinFiles(dir);
		
		System.out.println("number of henshin files: "+henshinFiles.size());
		
		List<String> failedRules = new LinkedList<String>();
		
		for(File henshinFile : henshinFiles){
			
			//load Henshin file
			
//			String path2 = henshinFile.getPath(); //C:\Users ... t\AddFeatureToGroup_execute.henshin
			
			File containingFolder = henshinFile.getParentFile();
			String absolutePath = containingFolder.getAbsolutePath(); //C:\Users\ ...
//			String path3 = containingFolder.getPath(); //C:\Users\ ...
			
			String henshinFileName = henshinFile.getName(); // AddFeatureToGroup_execute.henshin
			
//			System.out.println("test");
			
//			int lastIndexOf1 = path.lastIndexOf(".", path.length());
//			int lastIndexOf2 = path.lastIndexOf("\\");
//			String path = 
//			String filename = 
//			path.la
			
//			// Create a resource set with a base directory:
			HenshinResourceSet resourceSet = new HenshinResourceSet(absolutePath);
//			
//			// Load the module:
			Module module = resourceSet.getModule(henshinFileName, false);
						
			//TODO: filter rules
			EList<Unit> units = module.getUnits();
			Rule theRule = null;
			boolean multipleRules = false;
			for(Unit unit : units){
				if(unit instanceof Rule){
//					if(theRule != null)
//						multipleRules = true;
					multipleRules = (theRule != null) ?  true : false;
					theRule = (Rule) unit;
					rules.add(theRule);
				}
			}
			if(multipleRules)
				System.err.println("amount of rules: "+units.size());
		}
		
		ICriticalPairAnalysis cpaByAGG = new CpaByAGG();
		
		CPAOptions options = new CPAOptions();
		options.setEssential(true);
		
		try {
			cpaByAGG.init(rules, options);
		} catch (UnsupportedRuleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		CPAResult conflicts = cpaByAGG.runConflictAnalysis();
		
		CPAResult filteredConflicts = filterConflicts(conflicts);
		
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
	
	
		private static List<File> inspectDirectoryForHenshinFiles(File dir) {
			List<File> henshinFiles = new LinkedList<File>();
			File[] directoryListing = dir.listFiles();
			if (directoryListing != null) {
				for (File child : directoryListing) {
//					System.out.println("TODO: recursive call of exploration method");
					String fileName = child.getName();
					if (fileName.endsWith(".henshin")) {
						henshinFiles.add(child);
					} else if (!child.getName().contains(".")) {
						File subDir = child;
						henshinFiles.addAll(inspectDirectoryForHenshinFiles(subDir));
					}
				}
			} else {
				// Handle the case where dir is not really a directory.
				// Checking dir.isDirectory() above would not be sufficient
				// to avoid race conditions with another process that deletes
				// directories.
			}
			return henshinFiles;
		}

}
