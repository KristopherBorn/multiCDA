package org.eclipse.emf.henshin.multicda.cda.runner.overapproximation;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.henshin.model.Action;
import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.HenshinFactory;
import org.eclipse.emf.henshin.model.Mapping;
import org.eclipse.emf.henshin.model.MappingList;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.Unit;
import org.eclipse.emf.henshin.model.impl.HenshinFactoryImpl;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;
import org.eclipse.emf.henshin.multicda.cda.runner.Runner;
import org.junit.Assert;

public class DeleteToPreserveTransformer {
	
	private  static HenshinFactory henshinFactory = HenshinFactoryImpl.eINSTANCE;

	
	private static String path = "testData\\featureModelingWithoutUpperLimitsOnReferences\\fmedit_noAmalgamation_noNACs_noAttrChange_additionalPreserveProgrammatic\\preserve_rules\\";
	
//	private String targetPath;
	
	public static void main(String[] args){
		
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
		
		int amountOfModifiedFiles = 0;
		
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
						
			//: filter rules
			EList<Unit> units = module.getUnits();
			Rule theRule = null;
			boolean multipleRules = false;
			for(Unit unit : units){
				if(unit instanceof Rule){
//					if(theRule != null)
//						multipleRules = true;
					multipleRules = (theRule != null) ?  true : false;
					theRule = (Rule) unit;
				}
			}
			if(multipleRules)
				System.err.println("amount of rules: "+units.size());
			
			//: edit rules
			if(theRule != null){
				
				
				
				System.out.println("processing rule: "+theRule.getName());
				
				int numberOfLhsNodesBefore = theRule.getRhs().getNodes().size();
				System.out.println("numberOfRhsNodesBefore: "+numberOfLhsNodesBefore);
				

				
				
				Action deleteAction = new Action(Action.Type.DELETE);
				
				// Über die Liste der zu löschenden Knoten iterieren.
				EList<Node> deletionNodes = theRule.getActionNodes(deleteAction);
				System.err.println("deletionNodes before: "+deletionNodes.size());
				int amountOfMappingsBefore = theRule.getMappings().size();
				System.err.println("mappingsBefore: "+amountOfMappingsBefore);
				// Hinzufügen entsprechender Knoten zur RHS und erstellen der entsprechenden Mappings
				for(Node deletionNode : deletionNodes){
					// Neuen Knoten erstellen und Knoten der LHS hinzufügen
					Node newRhsNode = henshinFactory.createNode(theRule.getRhs(), deletionNode.getType(), deletionNode.getName()); //: ggf. Null von getName() abfangen?
					// Mapping erstellen
					Mapping createMapping = henshinFactory.createMapping(deletionNode, newRhsNode);
					theRule.getMappings().add(createMapping); //: prüfen ob das notwendig ist
					
				}
				
				Assert.assertTrue(true);
				
				// "sysout debug!":
				EList<Node> deletionNodesAfter = theRule.getActionNodes(deleteAction);
				System.err.println("deletionNodes after: "+deletionNodesAfter.size());
				MappingList mappingsAfter = theRule.getMappings();
				System.err.println("mappingsAfter: "+mappingsAfter.size()+" ; +"+(mappingsAfter.size()-amountOfMappingsBefore));
				

				// Über die Liste der zu löschenden Kanten iterieren.
				// Hinzufügen der entsprechenden Knoten zur RHS.
				EList<Edge> deletionEdges = theRule.getActionEdges(deleteAction);
				for(Edge deletionEdge : deletionEdges){
					MappingList allMappings = theRule.getAllMappings();
					//resolve source node in LHS
					Node sourceInLhs = deletionEdge.getSource();//.getActionNode();
					Node sourceInRhs = allMappings.getImage(sourceInLhs, theRule.getRhs());
					if(sourceInLhs == null || sourceInRhs == null)
						System.out.println("ERROR");
					//resolve target node in LHS
					Node targetInLhs = deletionEdge.getTarget();//.getActionNode();
					Node targetInRhs = allMappings.getImage(targetInLhs, theRule.getRhs());
					if(targetInLhs == null || targetInRhs == null)
						System.out.println("ERROR");
					henshinFactory.createEdge(sourceInRhs, targetInRhs, deletionEdge.getType());
				}

				
				int numberOfLhsNodesAfter = theRule.getRhs().getNodes().size();
				System.out.println("numberOfRhsNodesAfter: "+numberOfLhsNodesAfter);
				
				int numberOfDeleteActionNodes = theRule.getActionNodes(deleteAction).size();
				System.err.println("numberOfDeleteActionNodes: "+numberOfDeleteActionNodes);
				int numberOfDeleteActionEdges = theRule.getActionEdges(deleteAction).size();
				System.err.println("numberOfDeleteActionEdges: "+numberOfDeleteActionEdges);
			}
			
			amountOfModifiedFiles++;
			System.out.println("amountOfModifiedFiles: "+amountOfModifiedFiles);
			
			//: save results
			if(!failedRules.contains(theRule.getName())){
				resourceSet.saveEObject(module, henshinFile.getPath());
			}
		}
		
		System.err.println("failed rules:");
		for(String failedRuleName : failedRules){
			System.err.println(failedRuleName);
		}
	}
	
	
//	//origins from Runner.java
//	private static List<String> inspectDirectoryForHenshinFiles(File dir) {
//		List<String> pathsToHenshinFiles = new LinkedList<String>();
//		File[] directoryListing = dir.listFiles();
//		if (directoryListing != null) {
//			for (File child : directoryListing) {
//				System.out.println(": recursive call of exploration method");
//				String fileName = child.getName();
//				if (fileName.endsWith(".henshin")) {
//					pathsToHenshinFiles.add(child.getAbsolutePath());
//				} else if (!child.getName().contains(".")) {
//					File subDir = child;
//					pathsToHenshinFiles.addAll(inspectDirectoryForHenshinFiles(subDir));
//				}
//			}
//		} else {
//			// Handle the case where dir is not really a directory.
//			// Checking dir.isDirectory() above would not be sufficient
//			// to avoid race conditions with another process that deletes
//			// directories.
//		}
//		return pathsToHenshinFiles;
//	}
	
	private static List<File> inspectDirectoryForHenshinFiles(File dir) {
		List<File> henshinFiles = new LinkedList<File>();
		File[] directoryListing = dir.listFiles();
		if (directoryListing != null) {
			for (File child : directoryListing) {
//				System.out.println(": recursive call of exploration method");
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
