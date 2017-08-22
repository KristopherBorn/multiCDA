/**
 * <copyright>
 * Copyright (c) 2010-2016 Henshin developers. All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the Eclipse Public License v1.0 which 
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * </copyright>
 */
package org.eclipse.emf.henshin.cpa;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.henshin.cpa.persist.CriticalPairNode;
import org.eclipse.emf.henshin.cpa.persist.EssentialCriticalPairError;
import org.eclipse.emf.henshin.cpa.result.Conflict;
import org.eclipse.emf.henshin.cpa.result.CriticalPair;
import org.eclipse.emf.henshin.cpa.result.CriticalPair.AppliedAnalysis;
import org.eclipse.emf.henshin.cpa.result.Dependency;
import org.eclipse.emf.henshin.interpreter.Match;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.NestedCondition;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.Unit;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gmf.runtime.diagram.core.DiagramEditingDomainFactory;
import org.eclipse.gmf.runtime.diagram.ui.resources.editor.internal.util.DiagramIOUtil;
import org.eclipse.gmf.runtime.emf.core.resources.GMFResource;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.LayoutConstraint;
import org.eclipse.gmf.runtime.notation.Location;
import org.eclipse.gmf.runtime.notation.Shape;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.gmf.runtime.notation.impl.DiagramImpl;

/**
 * Utility for the critical pair analysis. Up till now mainly used for persisting the results in the file system.
 * 
 * @author Kristopher Born
 *
 */
public class CPAUtility {
	
	
	static Location location;

	/**
	 * Persists the results of a critical pair analysis in the file system.
	 * 
	 * @param cpaResult A <code>CPAResult</code> of a critical pair analysis.
	 * @param path The path for saving the full result set.
	 * @param rulesAndAssociatedFileNames 
	 * @return a <code>HashMap</code> of the saved results.
	 */
	public static HashMap<String, EnumMap<CriticalPair.AppliedAnalysis, Set<CriticalPairNode>>> persistCpaResult(List<CriticalPair> cpaResult, String path, HashMap<Rule, String> rulesAndAssociatedFileNames) {

		Date timestamp = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd-HHmmss");
		String timestampFolder = simpleDateFormat.format(timestamp);

		String pathWithDateStamp = path + File.separator + timestampFolder;

		HashMap<String, EnumMap<CriticalPair.AppliedAnalysis, Set<CriticalPairNode>>> persistedCPs = new HashMap<String, EnumMap<CriticalPair.AppliedAnalysis, Set<CriticalPairNode>>>();

		for (CriticalPair cp : cpaResult) {
			// naming of each single conflict
			String folderName = cp.getFirstRule().getName() + ", " + cp.getSecondRule().getName();

			int numberForRulePair = 1;


			EnumMap<CriticalPair.AppliedAnalysis, Set<CriticalPairNode>> allPersistedCPsOfRuleCombination = persistedCPs.get(folderName);
			// check if folder representative for rulepair exists in hashmap otherwise create it. 
			if(allPersistedCPsOfRuleCombination == null){
				persistedCPs.put(folderName, new EnumMap<CriticalPair.AppliedAnalysis, Set<CriticalPairNode>>(CriticalPair.AppliedAnalysis.class));
				allPersistedCPsOfRuleCombination = persistedCPs.get(folderName);
			}
			
			AppliedAnalysis appliedAnalysis = cp.getAppliedAnalysis();
			
			// check if folder representative for analysis kind exists in EnumMap otherwise create it.
			if (allPersistedCPsOfRuleCombination.containsKey(appliedAnalysis)) {
				numberForRulePair = allPersistedCPsOfRuleCombination.get(cp.getAppliedAnalysis()).size() + 1;
			} else {
				allPersistedCPsOfRuleCombination.put(appliedAnalysis, new HashSet<CriticalPairNode>());
			}

			String criticalPairKind = "";
			if (cp instanceof Conflict) {
				criticalPairKind = ((Conflict) cp).getConflictKind().toString();
			} else if (cp instanceof Dependency) {
				criticalPairKind = ((Dependency) cp).getDependencyKind().toString();
			} else {
				criticalPairKind = appliedAnalysis.toString();
			}

			String formatedNumberForRulePair = new DecimalFormat("00").format(numberForRulePair);

			String numberedNameOfCPKind = "(" + formatedNumberForRulePair + ") " + criticalPairKind;
			//TODO: hier die typen der kritischen Elemente hinzufügen
			String criticalElements = ""; //TODO: better naming! 
//			cp.getCriticalElements()

			// persist a single critical pair.
			CriticalPairNode newCriticalPairNode = null;
			if(cp instanceof Conflict || cp instanceof Dependency){
				newCriticalPairNode = persistSingleCriticalPair(cp, numberedNameOfCPKind, pathWithDateStamp);				
			}
			// TODO (2017-04-16): deaktiviert bis zur Integration der atomic CDA in Henshin 
//			else if (cp instanceof MinimalConflict || cp instanceof ConflictAtom) {
//				newCriticalPairNode = simplePersistSingleConflict(cp, numberedNameOfCPKind, pathWithDateStamp); 
//			}
			
			Module module1 = (Module) cp.getFirstRule().eContainer();
			Resource eResourceOfRule1 = module1.eResource(); // workspace Resource , i.e. files2/refactoring.henshin
			URI uriOfOriginalFirstRuleFile = eResourceOfRule1.getURI();
			
			Module module2= (Module) cp.getSecondRule().eContainer();
			Resource eResourceOfRule2 = module2.eResource(); // workspace Resource , i.e. files2/refactoring.henshin
			URI uriOfOriginalSecondRuleFile = eResourceOfRule2.getURI();
			
			if(newCriticalPairNode != null){
			
//			System.out.println(uriOfEResource.toString());
//			String pathToFile = rulesAndAssociatedFileNames.get(cp.getFirstRule());			
//			IFile firstRuleContainingFile = new File(pathToFile);
//			URI firstRuleDiagramURI = addDIAGRAMtoFileName(newCriticalPairNode.getFirstRuleURI());
//			// persist diagramFile: URI der zu persistiertenden Regel, Datei mit Regel. diagram Datei veruschen selbstständig zu finden.
			URI uriOfDiagramRule1 = persistDiagramFile(uriOfOriginalFirstRuleFile, newCriticalPairNode.getFirstRuleURI(), cp.getFirstRule()); //TODO:
			URI uriOfDiagramRule2 = persistDiagramFile(uriOfOriginalSecondRuleFile, newCriticalPairNode.getSecondRuleURI(), cp.getSecondRule());
//			
//			if(peristedFirstRuleDiagram && peristedSecondRuleDiagram){				
				newCriticalPairNode.setFirstRuleDiagramURI(uriOfDiagramRule1);
				newCriticalPairNode.setSecondRuleDiagramURI(uriOfDiagramRule2);
//			}

			persistedCPs.get(folderName).get(appliedAnalysis).add(newCriticalPairNode);
			
			}
		}

		return persistedCPs;
	}

	private static URI persistDiagramFile(URI uriOfFirstRuleFile, URI uriForSavingSingleRule, Rule ruleToPresent) {
		
		
		boolean ADAPT_DIAGRAMS = false;
		
		//TODO: Uri to persist single Rule
//		URI uriForSavingSingleRule = ???;
		
		
//		IFile iFile = FileBuffers.getWorkspaceFileAtLocation(appended);
		java.net.URI netUriOfFirstRule = null;
		try {
			netUriOfFirstRule = new java.net.URI(uriOfFirstRuleFile.toString());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		IPath potentialHenshinFilePath = new Path(netUriOfFirstRule.getSchemeSpecificPart());
		IFile potentialHenshinFile = FileBuffers.getWorkspaceFileAtLocation(potentialHenshinFilePath);
		boolean henshinFileExists = potentialHenshinFile.exists();

		String fileNameOfRule = uriOfFirstRuleFile.lastSegment();
		String fileNameOfDiagram = fileNameOfRule.replace(".henshin", ".henshin_diagram");
		URI uriOfPotentialDiagramFile = uriOfFirstRuleFile.trimSegments(1).appendSegment(fileNameOfDiagram);
		
//		Path potentialDiagramFilePath = new Path(uriOfPotentialDiagramFile.toFileString());
		

		java.net.URI netUriOfDiagram = null;
		try {
			netUriOfDiagram = new java.net.URI(uriOfPotentialDiagramFile.toString());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		IPath potentialDiagramFilePath = new Path(netUriOfDiagram.getSchemeSpecificPart());
		IFile potentialDiagramFile = FileBuffers.getWorkspaceFileAtLocation(potentialDiagramFilePath);//iFile.getProject().getFile //potentialDiagramFilePath.//potentialDiagramFilePath.toFile();
//		boolean diagramFileExists = potentialDiagramFile.exists(); //TODO: potentialDiagramFile might be null - prevent NPE!!!

		
		TransactionalEditingDomain editingDomain = DiagramEditingDomainFactory.getInstance().createEditingDomain();
		editingDomain.setID("org.eclipse.emf.henshin.diagram.EditingDomain"); //$NON-NLS-1$
		
		Diagram diagram = null;
		try {
			diagram = DiagramIOUtil.load(editingDomain, potentialDiagramFile, true,	null/*PrograssMonitor*/);
		} catch (CoreException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		if (diagram != null) {
			System.out.println("test");
		}
		
		Module moduleInDiagram = null;
		if(diagram instanceof DiagramImpl){
			EObject element = diagram.getElement();
			if(element instanceof Module){
				Module module = (Module) element;
				moduleInDiagram = module;
			}
		}
		
		// erstellen der Henshin Datei für die Visualissierung einer regel
		String fileNameOfSavedRule = uriForSavingSingleRule.lastSegment();
		String fileNameForSavingDiagramRule = fileNameOfSavedRule.replace(".henshin", "_DIAG.henshin");
		URI uriForSavingDiagramRule = uriForSavingSingleRule.trimSegments(1).appendSegment(fileNameForSavingDiagramRule);
		HenshinResourceSet resourceSetForCopySaveTest =  new HenshinResourceSet();
		Resource firstRuleResForCopySaveTest = resourceSetForCopySaveTest.createResource(uriForSavingDiagramRule, "henshin");
		
		GMFResource gmfResource =  (GMFResource) diagram.eResource();
		gmfResource.basicSetResourceSet(resourceSetForCopySaveTest, null);
		Resource eResource2 = moduleInDiagram.eResource(); //files2/refactoring.henshin
		eResource2.setURI(uriForSavingDiagramRule);
//		try {
//			eResource2.save(null);
//		} catch (IOException e2) {
//			// TODO Auto-generated catch block
//			e2.printStackTrace();
//		}		
		// TODO: Zielspeicherpfad für diagram file erstellen

//		String fileNameOfSavedRule = uriForSavingSingleRule.lastSegment();
		String fileNameForSavingDiagram = fileNameOfSavedRule.replace(".henshin", "_DIAG.henshin_diagram");
		URI uriForSavingDiagram = uriForSavingSingleRule.trimSegments(1).appendSegment(fileNameForSavingDiagram);
		java.net.URI netUriOfNewDiagram = null;
		try {
			netUriOfNewDiagram = new java.net.URI(uriForSavingDiagram.toString());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot(); 
		java.net.URI rootUri = root.getLocationURI(); 
		java.net.URI relativizeDUri = rootUri.relativize(netUriOfNewDiagram); 
		IPath path = new Path(relativizeDUri.getPath()); 
		IFile newHenshinDiagramFileForCopySaveTest = root.getFile(path); 
		// TODO: rename to: "newHenshinDiagramFileForCopySaveTest"

		IProject project = potentialHenshinFile.getProject();
		try {
			project.refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (CoreException e4) {
			// TODO Auto-generated catch block
			e4.printStackTrace();
		}
		IPath removeFirstSegments = path.removeFirstSegments(1);
		IFile file = project.getFile(removeFirstSegments.toString()); // such as file.exists() == false
		IPath removeLastSegments2 = removeFirstSegments.removeLastSegments(1);
		IFile file3 = project.getFile(removeLastSegments2);
		boolean file3exists = file3.exists();
		IPath removeLastSegments3 = removeLastSegments2.removeLastSegments(1);
		IFile file4 = project.getFile(removeLastSegments3);
		boolean file4exists = file4.exists();
		String contents = "";
//		InputStream source = new ByteArrayInputStream(contents.getBytes());
		final InputStream is = new ByteArrayInputStream("".getBytes());
		try {
			file.create(is, true, null);
		} catch (CoreException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		IFile file2 = project.getFile(removeFirstSegments);
		
		//TODO: try: "newDiagramFilePath.removeFirstSegments(?);"
		IPath newDiagramFilePath = new Path(netUriOfNewDiagram.getSchemeSpecificPart());
		IPath removeLastSegments = newDiagramFilePath.removeLastSegments(1);
//		String pathToDiagram = netUriOfNewDiagram.getRawPath();
		
//		Path potentialHenshinFilePathTest = new Path(uriOfFirstRuleFile.t);
//		IFile newHenshinDiagramFileForCopySaveTest = FileBuffers.getWorkspaceFileAtLocation(newDiagramFilePath);
//		potentialHenshinFile.toString()
//		File potentialHenshinFile = potentialHenshinFilePath.toFile();
		
//		IFile newDiagramFile = FileBuffers.getWorkspaceFileAtLocation(newDiagramFilePath);
		
		
		
//		IPath removeLastSegmentsForCopySaveTest = potentialHenshinFile.getProjectRelativePath().removeLastSegments(2);
////		IPath removeLastSegments3 = file.getRawLocation();
//		IPath appendedFirstTestSaveForCopySaveTest = removeLastSegmentsForCopySaveTest.append("files3");
//		IPath appendedTestSaveForCopySaveTest = appendedFirstTestSaveForCopySaveTest.append("(1)testSave.henshin_diagram");
//		IFile newHenshinDiagramFileForCopySaveTest = 
//				try {
//					potentialDiagramFile.copy(newDiagramFilePath, true, null);
//				} catch (CoreException e3) {
//					// TODO Auto-generated catch block
//					e3.printStackTrace();
//				}//.getProject().getFile(netUriOfNewDiagram.getRawPath()); //getFile(newDiagramFilePath);
//		resourceSetForCopySaveTest.
//		getFile(new Path(uri.toPlatformResourceString(true))) 
//		IFile newHenshinDiagramFileForCopySaveTest = FileBuffers.getWorkspaceFileAtLocation(newDiagramFilePath);
//		IFile newHenshinDiagramFileForCopySaveTest = FileBuffers.getWorkspaceFileAtLocation(newDiagramFilePath);
		// Datei muss zuerst erzeugt werden bevor diese anschließend mit dem Diagramm beschrieben werden kann!
		
//				IFile newHenshinDiagramFileForCopySaveTest = FileBuffers.getWorkspaceFileAtLocation(newDiagramFilePath);
				
//				if(newHenshinDiagramFileForCopySaveTest.exists())
//					System.err.println("JUHU!");
				
				InputStream fis = null;
				
				// TEMPORÄRDEAKTIVIERT UMD FREI NCIHTUNTER DEM geSCHIWNDIGKEITSNACHTEIL ZU LEIDEN!!!!
		// LÖSCHEN DER ÜBERFLÜSSGEN VIEWS (und Rules -> move in front of rule saving!)
			if(ADAPT_DIAGRAMS){
				View viewToKeep = null;
				List<View> viewsToDelete = new LinkedList<View>();
				List<Rule> rulesToDelete = new LinkedList<Rule>();
	//			System.err.println("number of eContent in diagram: "+numberOfContents);
				EList children = diagram.getChildren();
				for(Object child : children){
					if(child instanceof View){
						View view = (View) child;
	//					EList children2 = view.getChildren();
						String type = view.getType();
						System.err.println("type of view '"+view.toString()+"' is: "+type+" and has "+children.size()+" children");
						EObject element = view.getElement(); // hier sind die Regeln, also entspricht jede der Views einer Regel!
	//					String string = element.getClass().toString(); // kann zu NPE führen (getClass() = null). vllt. im Fall von Notizen?
						System.err.println("the views ");
						
						
						if(element instanceof Rule){
							Rule ruleOfView = (Rule) element;
							if(!ruleOfView.getName().equalsIgnoreCase(ruleToPresent.getName())){
								viewsToDelete.add(view);
								rulesToDelete.add(ruleOfView);
							}else {
								viewToKeep = view;
							}
						}
					}
				}
				
				
				// reposition the remaining rule in the diagram
				if(viewToKeep != null){
					if(viewToKeep instanceof Shape){
						Shape shape = (Shape) viewToKeep;
						LayoutConstraint layoutConstraint = shape.getLayoutConstraint();
						if(layoutConstraint instanceof Location){
							location = (Location) layoutConstraint;
							
							RecordingCommand rcmd = new RecordingCommand(editingDomain) {
								@Override
								protected void doExecute() {
									location.setX(0);
									location.setY(0);
								}
							};
							
							editingDomain.getCommandStack().execute(rcmd);
						}
					}
				}
				
					
				System.err.println("Start deleting");
				for(View viewToDelete : viewsToDelete){
					editingDomain.getCommandStack().execute(RemoveCommand.create(editingDomain, viewToDelete));
				}
				
				System.err.println("number of rules before: "+moduleInDiagram.getUnits().size());
				for(Rule ruleToDelete : rulesToDelete){
					editingDomain.getCommandStack().execute(RemoveCommand.create(editingDomain, ruleToDelete));
				}
				System.err.println("number of rules after: "+moduleInDiagram.getUnits().size());
			}
			
			
			// SPEICHERN des Moduls!:
			try {
				eResource2.save(null);
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}	
				
				
		newHenshinDiagramFileForCopySaveTest = file;		
		
		boolean diagramFileCreated = newHenshinDiagramFileForCopySaveTest.exists();
		try {
			DiagramIOUtil.save(editingDomain, newHenshinDiagramFileForCopySaveTest, diagram, null);
		} catch (CoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		
		return uriForSavingDiagram;
	}

	
	/**
	 * Persists a single critical pair (<code>cp</code>) in the file system.
	 * 
	 * @param cp The <code>CriticalPair</code> to be saved.
	 * @param numberedNameOfCriticalPair The numbered name of the critical pair.
	 * @param path The path for saving the files.
	 * @return a <code>CriticalPairNode</code>.
	 */
	private static CriticalPairNode persistSingleCriticalPair(CriticalPair cp, String numberedNameOfCriticalPair,
			String path) {

		ResourceSet commonResourceSet = new ResourceSetImpl();

		Match firstMatch = null;
		Match secondMatch = null;

		if (cp instanceof Conflict) {
			firstMatch = ((Conflict) cp).getMatch1();
			secondMatch = ((Conflict) cp).getMatch2();
		} else if (cp instanceof Dependency) {
			firstMatch = ((Dependency) cp).getComatch();
			secondMatch = ((Dependency) cp).getMatch();
		}

		Rule firstRule = cp.getFirstRule();
		EPackage minimalModelAsEcoreInstance = (EPackage) cp.getMinimalModel(); //TODO: introduce typeCheck by instnaceof here!
		Rule secondRule = cp.getSecondRule();

		Graph firstRuleLHS = firstRule.getLhs();
		Graph firstRuleRHS = firstRule.getRhs();
		Graph secondRuleLHS = secondRule.getLhs();
		Graph secondRuleRHS = secondRule.getRhs();

		// serves for naming back the nodes of the involved rules
		HashMap<Node, String> renameMap = new HashMap<Node, String>();

		// this counter serves to give each node in the minimal model a unique and ascending number.
		int differentElementsCounter = 0;
		Map<Integer, String> hashToName = new HashMap<Integer, String>();

		Match match = firstMatch;
		// ------------------- First Rule: LHS -----------------;
		differentElementsCounter = processLhsOrRhsOfRuleForPersisting(minimalModelAsEcoreInstance, firstRuleLHS, true,
				differentElementsCounter, hashToName, match, renameMap);

		// ------------------- First Rule: RHS -----------------;
		differentElementsCounter = processLhsOrRhsOfRuleForPersisting(minimalModelAsEcoreInstance, firstRuleRHS, true,
				differentElementsCounter, hashToName, match, renameMap);

		match = secondMatch;

		// ------------------- Second Rule: LHS -----------------;
		differentElementsCounter = processLhsOrRhsOfRuleForPersisting(minimalModelAsEcoreInstance, secondRuleLHS, false,
				differentElementsCounter, hashToName, match, renameMap);

		// ------------------- Second Rule: RHS -----------------;
		differentElementsCounter = processLhsOrRhsOfRuleForPersisting(minimalModelAsEcoreInstance, secondRuleRHS, false,
				differentElementsCounter, hashToName, match, renameMap);

		// process the NACs of the second rule
		EList<NestedCondition> nestedConditions = secondRuleLHS.getNestedConditions();
		// ------------------- First Rule: NAC -----------------;
		for (NestedCondition nestCond : nestedConditions) {
			if (nestCond.isNAC()) {
				Graph secondRuleNAC = nestCond.getConclusion();
				EList<Node> nacNodes = secondRuleNAC.getNodes();
				for (Node nacNode : nacNodes) {
					if (match.getNodeTarget(nacNode) != null) {
						int overlapNodeHash = match.getNodeTarget(nacNode).hashCode();
						if (hashToName.containsKey(overlapNodeHash)) {

							String newName = hashToName.get(overlapNodeHash);
							renameMap.put(nacNode, nacNode.getName());
							nacNode.setName(newName);

						} else {

							differentElementsCounter++;
							String newName = differentElementsCounter + "";
							renameMap.put(nacNode, nacNode.getName());
							nacNode.setName(newName);

							EList<EClassifier> eclasses = minimalModelAsEcoreInstance.getEClassifiers();

							for (EClassifier eclass : eclasses) {
								if (eclass.hashCode() == overlapNodeHash) {
									hashToName.put(overlapNodeHash, newName);
									break;
								}
							}
						}
					}
				}
			} else {
				break;
			}
		}

		// renaming of the nodes within the overlap graph
//		renameMap
		renameNodesOfMinimalModel(minimalModelAsEcoreInstance, hashToName);

		String pathForCurrentCriticalPair = path + File.separator + firstRule.getName() + "_AND_"
				+ secondRule.getName() + File.separator + cp.getAppliedAnalysis() + File.separator + numberedNameOfCriticalPair + File.separator;

		// save the first rule in the file system
		String fileNameRule1 = "(1)" + firstRule.getName() + ".henshin";
		String fullPathRule1 = pathForCurrentCriticalPair + fileNameRule1;
		URI firstRuleURI = saveRuleInFileSystem(commonResourceSet, firstRule, fullPathRule1);
		//TODO: hier das Speichern der grafischen Repräsentation der Regel hinzufügen!

		// save the minimal model in the file system
		String fileNameMinimalModel = "minimal-model" + ".ecore";
		String fullPathMinimalModel = pathForCurrentCriticalPair + fileNameMinimalModel;

		URI overlapURI = saveMinimalModelAsEcoreInstanceInFileSystem(commonResourceSet, minimalModelAsEcoreInstance, fullPathMinimalModel);

		// save the second rule in the file system
		String fileNameRule2 = "(2)" + secondRule.getName() + ".henshin";
		String fullPathRule2 = pathForCurrentCriticalPair + fileNameRule2;
		URI secondRuleURI = saveRuleInFileSystem(commonResourceSet, secondRule, fullPathRule2);
		//TODO: hier das Speichern der grafischen Repräsentation der Regel hinzufügen!
		// TODO: was wird an Daten benötigt?
			/* 	- Pfad: "fullPathRule2"
			 *  - ???
			 */
//		URI secondRuleURI = saveRuleDiagramInFileSystem(commonResourceSet, secondRule, fullPathRule2);
//		// TODO:
//		// aus HEnshinDiagramEditor - Zeile 378
//		// newInput - Datei im Wokrspace - Type: IEditorInput
//		getDocumentProvider(newInput).saveDocument(progressMonitor, newInput,
//				getDocumentProvider().getDocument(getEditorInput()), true);
		
		
		
		

		// save a dummy for the HenshinCPEditor
		String fileName = "dummy.henshinCp";
		String fullPath = pathForCurrentCriticalPair + fileName;
		URI criticalPairURI = saveCriticalPairInFileSystem(commonResourceSet, null/* criticalPairOfThisProcess */,
				fullPath);

		// rename the Nodes of the rules back to have the original rule for the renaming for the next processed critical
		// pair.
		for (Node node : renameMap.keySet()) {
//			node.setName(renameMap.get(node)); //deaktiviert für case study!
		}

		
		// TEMPORÄR FÜR TEST DER EssentialCriticalPairError
		if(cp.getAppliedAnalysis().equals(AppliedAnalysis.ESSENTIAL)){
			return new EssentialCriticalPairError("Test", firstRuleURI, secondRuleURI, overlapURI,
					criticalPairURI);
		}
		
					//TODO: vermutlich erweitern der CriticalPairNode Klasse, 
							//so dass auch die URI der grafischen Repräsentationen hinterlegt werden.   
		return new CriticalPairNode(numberedNameOfCriticalPair, firstRuleURI, secondRuleURI, overlapURI,
				criticalPairURI);
	}
	
	
	
//	TODO: zuerst duplizieren der Methode und ein "simples" generelles Speichern für atomic Conflicts ableiten 
//	später eine Vereinheitlichung mit umbenennung anstreben (wenn die Ergebnisse für die Beispiele bekannt sind!)
	/**
	 * Persists a single critical pair (<code>cp</code>) in the file system.
	 * 
	 * @param cp The <code>CriticalPair</code> to be saved.
	 * @param numberedNameOfCriticalPair The numbered name of the critical pair.
	 * @param path The path for saving the files.
	 * @return a <code>CriticalPairNode</code>.
	 */
	private static CriticalPairNode simplePersistSingleConflict(CriticalPair cp, String numberedNameOfCriticalPair,
			String path) {
	
		ResourceSet commonResourceSet = new ResourceSetImpl();
	
//		Match firstMatch = null;
//		Match secondMatch = null;
	
//		if (cp instanceof Conflict) {
//			firstMatch = ((Conflict) cp).getMatch1();
//			secondMatch = ((Conflict) cp).getMatch2();
//		} else if (cp instanceof Dependency) {
//			firstMatch = ((Dependency) cp).getComatch();
//			secondMatch = ((Dependency) cp).getMatch();
//		}
	
		Rule firstRule = cp.getFirstRule();
//		EPackage minimalModelAsEcoreInstance = (EPackage) cp.getMinimalModel(); //TODO: introduce typeCheck by instnaceof here!
		
		Rule secondRule = cp.getSecondRule();
	
//		Graph firstRuleLHS = firstRule.getLhs();
//		Graph firstRuleRHS = firstRule.getRhs();
//		Graph secondRuleLHS = secondRule.getLhs();
//		Graph secondRuleRHS = secondRule.getRhs();
//	
//		// serves for naming back the nodes of the involved rules
//		HashMap<Node, String> renameMap = new HashMap<Node, String>();
//	
//		// this counter serves to give each node in the minimal model a unique and ascending number.
//		int differentElementsCounter = 0;
//		Map<Integer, String> hashToName = new HashMap<Integer, String>();
//	
//		Match match = firstMatch;
//		// ------------------- First Rule: LHS -----------------;
//		differentElementsCounter = processLhsOrRhsOfRuleForPersisting(minimalModelAsEcoreInstance, firstRuleLHS,
//				differentElementsCounter, hashToName, match, renameMap);
//	
//		// ------------------- First Rule: RHS -----------------;
//		differentElementsCounter = processLhsOrRhsOfRuleForPersisting(minimalModelAsEcoreInstance, firstRuleRHS,
//				differentElementsCounter, hashToName, match, renameMap);
//	
//		match = secondMatch;
//	
//		// ------------------- Second Rule: LHS -----------------;
//		differentElementsCounter = processLhsOrRhsOfRuleForPersisting(minimalModelAsEcoreInstance, secondRuleLHS,
//				differentElementsCounter, hashToName, match, renameMap);
//	
//		// ------------------- Second Rule: RHS -----------------;
//		differentElementsCounter = processLhsOrRhsOfRuleForPersisting(minimalModelAsEcoreInstance, secondRuleRHS,
//				differentElementsCounter, hashToName, match, renameMap);
//	
//		// process the NACs of the second rule
//		EList<NestedCondition> nestedConditions = secondRuleLHS.getNestedConditions();
//		// ------------------- First Rule: NAC -----------------;
//		for (NestedCondition nestCond : nestedConditions) {
//			if (nestCond.isNAC()) {
//				Graph secondRuleNAC = nestCond.getConclusion();
//				EList<Node> nacNodes = secondRuleNAC.getNodes();
//				for (Node nacNode : nacNodes) {
//					if (match.getNodeTarget(nacNode) != null) {
//						int overlapNodeHash = match.getNodeTarget(nacNode).hashCode();
//						if (hashToName.containsKey(overlapNodeHash)) {
//	
//							String newName = hashToName.get(overlapNodeHash);
//							renameMap.put(nacNode, nacNode.getName());
//							nacNode.setName(newName);
//	
//						} else {
//	
//							differentElementsCounter++;
//							String newName = differentElementsCounter + "";
//							renameMap.put(nacNode, nacNode.getName());
//							nacNode.setName(newName);
//	
//							EList<EClassifier> eclasses = minimalModelAsEcoreInstance.getEClassifiers();
//	
//							for (EClassifier eclass : eclasses) {
//								if (eclass.hashCode() == overlapNodeHash) {
//									hashToName.put(overlapNodeHash, newName);
//									break;
//								}
//							}
//						}
//					}
//				}
//			} else {
//				break;
//			}
//		}
//	
//		// renaming of the nodes within the overlap graph
//		renameNodesOfMinimalModel(minimalModelAsEcoreInstance, hashToName);
	
		String pathForCurrentCriticalPair = path + File.separator + firstRule.getName() + "_AND_"
				+ secondRule.getName() + File.separator + cp.getAppliedAnalysis() + File.separator + numberedNameOfCriticalPair + File.separator;
	
		// save the first rule in the file system
		String fileNameRule1 = "(1)" + firstRule.getName() + ".henshin";
		String fullPathRule1 = pathForCurrentCriticalPair + fileNameRule1;
		URI firstRuleURI = saveRuleInFileSystem(commonResourceSet, firstRule, fullPathRule1);
		//TODO: hier das Speichern der grafischen Repräsentation der Regel hinzufügen!
	
		// save the minimal model in the file system
//		String fileNameMinimalModel = "minimal-model" + ".ecore";
		String fullPathMinimalModel = pathForCurrentCriticalPair; // + fileNameMinimalModel;
	
//		TODO: speichern des Graph () der zentraler Teil des Spans ist!
//		URI overlapURI = saveMinimalModelAsEcoreInstanceInFileSystem(commonResourceSet, minimalModelAsEcoreInstance, fullPathMinimalModel); // ÜBERFLÜSSIG!
		HenshinResourceSet resourceSet = new HenshinResourceSet(fullPathMinimalModel);
		Graph graph = (Graph) cp.getMinimalModel();
		resourceSet.saveEObject(graph, "minimal-model.xmi");
		
	
		// save the second rule in the file system
		String fileNameRule2 = "(2)" + secondRule.getName() + ".henshin";
		String fullPathRule2 = pathForCurrentCriticalPair + fileNameRule2;
		URI secondRuleURI = saveRuleInFileSystem(commonResourceSet, secondRule, fullPathRule2);
		//TODO: hier das Speichern der grafischen Repräsentation der Regel hinzufügen!
		// TODO: was wird an Daten benötigt?
			/* 	- Pfad: "fullPathRule2"
			 *  - ???
			 */
	//	URI secondRuleURI = saveRuleDiagramInFileSystem(commonResourceSet, secondRule, fullPathRule2);
	//	// TODO:
	//	// aus HEnshinDiagramEditor - Zeile 378
	//	// newInput - Datei im Wokrspace - Type: IEditorInput
	//	getDocumentProvider(newInput).saveDocument(progressMonitor, newInput,
	//			getDocumentProvider().getDocument(getEditorInput()), true);
		
		
		
		
	
		// save a dummy for the HenshinCPEditor
		String fileName = "dummy.henshinCp";
		String fullPath = pathForCurrentCriticalPair + fileName;
		URI criticalPairURI = saveCriticalPairInFileSystem(commonResourceSet, null/* criticalPairOfThisProcess */,
				fullPath);
	
//		// rename the Nodes of the rules back to have the original rule for the renaming for the next processed critical
//		// pair.
//		for (Node node : renameMap.keySet()) {
//			node.setName(renameMap.get(node));
//		}
//	
//		
//		// TEMPORÄR FÜR TEST DER EssentialCriticalPairError
//		if(cp.getAppliedAnalysis().equals(AppliedAnalysis.ESSENTIAL)){
//			return new EssentialCriticalPairError("Test", firstRuleURI, secondRuleURI, overlapURI,
//					criticalPairURI);
//		}
//		
					//TODO: vermutlich erweitern der CriticalPairNode Klasse, 
							//so dass auch die URI der grafischen Repräsentationen hinterlegt werden. 
		URI overlapURI = firstRuleURI.trimSegments(1).appendSegment("minimal-model.xmi"); //TODO: name as class constant!
		return new CriticalPairNode(numberedNameOfCriticalPair, firstRuleURI, secondRuleURI, overlapURI,
				criticalPairURI);
	}
	
	
	
	//temporär deaktiviert für user study!
//	/**
//	 * Renames the nodes of the minimal model based on the names of the rules.
//	 * 
//	 * @param minimalModel The <code>EGraph</code>, which should be a minimal model of a critical pair.
//	 * @param hashValueToNameMapping A HashMap, mapping the future names to the hash values of the nodes of the rules.
//	 */
//	private static void renameNodesOfMinimalModel(EPackage minimalModel, Map<Integer, String> hashValueToNameMapping) {
//		EList<EClassifier> eclasses = minimalModel.getEClassifiers();
//		for (EClassifier eclass : eclasses) {
//			String name = eclass.getName();
//			String newName = hashValueToNameMapping.get(eclass.hashCode()) + ":" + name;
//			eclass.setName(newName);
//		}
//	}
	
	
	//versuch die Knoten im overlapGraph zu bennenen nach dem Schema: KnotenNameAusREgel1_KnotenNameAusRegel2
	private static void renameNodesOfMinimalModel(EPackage minimalModel, Map<Integer, String> hashValueToNameMapping) {
	EList<EClassifier> eclasses = minimalModel.getEClassifiers();
	for (EClassifier eclass : eclasses) {
		String name = eclass.getName();
		String newName = hashValueToNameMapping.get(eclass.hashCode()) + ":" + name;
//		eclass.setName(newName); // für survey deaktiviert!
		// funktioniert nur für survey:
		// einfügen des trennenden ":" zwischen Namenskombination und Type-Namen
		// finde letzte Zahl oder "_". Je nachdem was größer ist an dieser Stelle das Symbol ":" einfügen
//		name.*(?:\D|^)(\d+)
		int lastIndexOfNumber = 0; //name.lastIndexOf(".*(?<=\\D)(\\d+)\\D*");name.split(".*(?<=\\D)(\\d+)\\D*");
		
		char[] crs = name.toCharArray();
		for (int i = 0; i < crs.length; i++) {
		    if (crs[i] >= '0' && crs[i] <= '9'    /* crs.  Character(crs).isDigit()*/) {
		    	lastIndexOfNumber = i;
		    }
		}
		
		int lastIndexOf_ = name.lastIndexOf("_");
		int indexToPlaceColon = 0;
		if(lastIndexOfNumber>lastIndexOf_){
			indexToPlaceColon = lastIndexOfNumber;
		}else {
			indexToPlaceColon= lastIndexOf_;
		}

		StringBuffer strBuffer = new StringBuffer();
		strBuffer.append(name.substring(0, indexToPlaceColon+1)); //leadingPart
		strBuffer.append(":"); // new part
		strBuffer.append(name.substring(indexToPlaceColon+1));//trailingPart
		newName = strBuffer.toString();
		eclass.setName(newName);		
	}
}

	/**
	 * Saves an <code>EGraph</code>, which might be a minimal model on the given path within the file system.
	 * 
	 * @param resourceSet The common <code>ResourceSet</code>.
	 * @param minimalModel The minimal model to be saved.
	 * @param fullPathMinimalModel The full path of the file.
	 * @return the <code>URI</code> of the saved file.
	 */
	private static URI saveMinimalModelAsEcoreInstanceInFileSystem(ResourceSet resourceSet, EPackage minimalModel,
			String fullPathMinimalModel) {
		URI overlapURI = URI.createFileURI(fullPathMinimalModel);
		Resource overlapResource = resourceSet.createResource(overlapURI, "ecore");

		overlapResource.getContents().add(minimalModel);

		try {
			overlapResource.save(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return overlapURI;
	}

	/**
	 * Saves a <code>Rule</code> on the given path within the file system.
	 * 
	 * @param resourceSet The common <code>ResourceSet</code>.
	 * @param rule The <code>Rule</code> to be saved.
	 * @param fullFilePath The full path of the file.
	 * @return the <code>URI</code> of the saved file.
	 */
	private static URI saveRuleInFileSystem(ResourceSet resourceSet, Rule rule, String fullFilePath) {
		URI firstRuleURI = URI.createFileURI(fullFilePath);
		Resource firstRuleRes = resourceSet.createResource(firstRuleURI, "henshin");
		firstRuleRes.getContents().add(rule);

		try {
			firstRuleRes.save(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return firstRuleURI;
	}

//	/**
//	 * Processes the the LHS or RHS of a rule by renaming the nodes in the minimal model.
//	 * 
//	 * @param minimalModel The minimal model of the critical pair.
//	 * @param LHS_or_RHS_ofRule The LHS or RHS of a rule.
//	 * @param differentElementsCounter A counter to give each node in the minimal model a unique and ascending number.
//	 * @param hashToName a <code>HashMap</code> to store associated names in the minimal model to the nodes of the
//	 *            rules.
//	 * @param match The match linking the rule into the minimal model.
//	 * @return The resulting value of the <code>differentElementsCounter</code>
//	 */
//	private static int processLhsOrRhsOfRuleForPersisting(EPackage minimalModel, Graph LHS_or_RHS_ofRule,
//			int differentElementsCounter, Map<Integer, String> hashToName, Match match, HashMap<Node, String> renameMap) {
//		for (Node elem : LHS_or_RHS_ofRule.getNodes()) {
//			if (match.getNodeTarget(elem) != null) {
//				int overlapNodeHash = match.getNodeTarget(elem).hashCode();
//				if (hashToName.containsKey(overlapNodeHash)) {
//
//					String newName = hashToName.get(overlapNodeHash);
//					renameMap.put(elem, elem.getName());
//					elem.setName(newName);
//				} else {
//					differentElementsCounter++;
//
//					String newName = differentElementsCounter + "";
//					renameMap.put(elem, elem.getName());
//					elem.setName(newName);
//
//					EList<EClassifier> eclasses = minimalModel.getEClassifiers();
//
//					for (EClassifier eclass : eclasses) {
//						if (eclass.hashCode() == overlapNodeHash) {
//							hashToName.put(overlapNodeHash, newName);
//						}
//					}
//				}
//			}
//		}
//		return differentElementsCounter;
//	}
	
	
	//new version for user study
	private static int processLhsOrRhsOfRuleForPersisting(EPackage minimalModel, Graph LHS_or_RHS_ofRule, boolean isFirstRule,
			int differentElementsCounter, Map<Integer, String> hashToName, Match match, HashMap<Node, String> renameMap) {
		for (Node elem : LHS_or_RHS_ofRule.getNodes()) {
			if (match.getNodeTarget(elem) != null) {
				int overlapNodeHash = match.getNodeTarget(elem).hashCode();
				//new: -->>
				if(LHS_or_RHS_ofRule.isLhs()){ //minMod ist aus LHS der REgeln zusammengesetzt, daher ist RHS irrelevant!
					EObject nodeInOverlapEcoreGraph = match.getNodeTarget(elem);
					if(nodeInOverlapEcoreGraph instanceof EClass){
						EClass eClass = (EClass) nodeInOverlapEcoreGraph; //DONE: prevent NPE!!!
						String formerName = eClass.getName();
						System.err.println("old name: "+formerName);
						String newName = "";
						//TODO: check if name allready contains assosciated
						
						//behandelt den Fall, dass der erste Namenteil bereits vorhanden ist und fügt den zweiten Namensteil ein. (wenn es sich um die LHS der zweiten Regel handelt)
						if(!isFirstRule){
							int indexOf_ = formerName.indexOf("_");
							StringBuffer strBuffer = new StringBuffer();
							if(!formerName.contains("_"))
								strBuffer.append("_");
							strBuffer.append(formerName.substring(0, indexOf_+1)); //leadingPart
							strBuffer.append(elem.getName()); // new part
							strBuffer.append(formerName.substring(indexOf_+1));//trailingPart
							newName = strBuffer.toString();
						}
						//behandelt den Fall, dass der erste Namensteil noch nicht vorhanden ist und fügt den ersten Namensteil und den trennen "_" ein. (wenn es sich um die LHS der ersten Regel handelt) 
						else {
							StringBuffer strBuffer = new StringBuffer();
							strBuffer.append(elem.getName());
							strBuffer.append("_");
							strBuffer.append(formerName);
							newName = strBuffer.toString();
						}
						
						
//						elem.getName() // der Name des Knoten aus dem Graph
						// EGAL!!//prüfen, ob der Name des Knotens schon enthalten ist. Geht nur, wenn der Name nciht teil von TypNamen ist!
						//	wenn nicht: prüfen ob ein "_" als Trenner zwischen NAmen der ersten REgel und der zweiten REgel bereits enthalten ist.
						// wenn nicht: name der ersten Regel
						
						
						eClass.setName(newName);;
//						nodeInOverlapEcoreGraph.eContainingFeature().getName() //überflüssig?
						String currentNameOfEclass = nodeInOverlapEcoreGraph.eClass().getName(); //überflüssig?
						System.err.println("new name: "+eClass.getName());
					}
				}
				// <<-- new
				
				if (hashToName.containsKey(overlapNodeHash)) {

					String newName = hashToName.get(overlapNodeHash);
					renameMap.put(elem, elem.getName());
//					elem.setName(newName);
				} else {
					differentElementsCounter++;

					String newName = differentElementsCounter + "";
					renameMap.put(elem, elem.getName());
//					elem.setName(newName);

					EList<EClassifier> eclasses = minimalModel.getEClassifiers();

					for (EClassifier eclass : eclasses) {
						if (eclass.hashCode() == overlapNodeHash) {
							hashToName.put(overlapNodeHash, newName);
						}
					}
				}
			}
		}
		return differentElementsCounter;
	}

	/**
	 * Changes the order of the rules within the module based on the order of the strings.
	 * 
	 * @param module The <code>Module</code> in which the order of the rules shall be changed.
	 * @param newRuleOrder The new order of the rules based on a set of sorted <code>String</code> values.
	 */
	public void changeRuleOrder(Module module, String[] newRuleOrder) {
		Vector<String> ruleOrderList = new Vector<String>(Arrays.asList(newRuleOrder));

		BasicEList<Unit> newSortedRulesList = new BasicEList<Unit>();

		// 1. order of Rules
		for (String nameOfOrderedRule : ruleOrderList) {
			for (Unit rule : module.getUnits()) {
				if (rule.getName().equals(nameOfOrderedRule)) {
					newSortedRulesList.add(rule);
				}
			}
		}

		// add remaining rules
		// 1.1 reduce original rules by already processed ones
		for (Unit rule : newSortedRulesList) {
			module.getUnits().remove(rule);
		}
		// add the remaining rules to the
		newSortedRulesList.addAll(module.getUnits());

		// 1.2 replace old list by new list
		module.getUnits().clear();
		module.getUnits().addAll(newSortedRulesList);
	}

	/**
	 * This method clears the both lists. The rules within the <code>Module</code> will be searched for the two of which
	 * the names had been passed and each rule will be added to the associated <code>List</code>. This method serves for
	 * analyzing explicit combinations of rules. Frequently used in the test suite.
	 * 
	 * @param module The <code>Module</code> containing the rules.
	 * @param firstRule The first <code>Rule</code> as a <code>List</code>. The <code>List</code> will be cleared and
	 *            afterwards filled with the first <code>Rule</code> as single containment of the <code>List</code>.
	 * @param firstRuleName The name of the first <code>Rule</code>, which will be searched in the <code>Module</code>
	 *            and added to the <code>List</code>.
	 * @param secondRule The second <code>Rule</code> as a <code>List</code>. The <code>List</code> will be cleared and
	 *            afterwards filled with the second <code>Rule</code> as single containment of the <code>List</code>.
	 * @param secondRuleName The name of the second <code>Rule</code>, which will be searched in the <code>Module</code>
	 *            and added to the <code>List</code>.
	 */
	public static void extractSingleRules(Module module, List<Rule> firstRule, String firstRuleName,
			List<Rule> secondRule, String secondRuleName) {
		for (Unit unit : module.getUnits()) {
			if (unit.getName().equalsIgnoreCase(firstRuleName))
				firstRule.add((Rule) unit);
			if (unit.getName().equalsIgnoreCase(secondRuleName))
				secondRule.add((Rule) unit);
		}
	}

	/**
	 * Extracts all the rules of a module within a List.
	 * 
	 * @param module The <code>Module</code> containing the rules.
	 * @return a <code>List</code> containing the <code>Rule</code>s within the <code>Module</code>.
	 */
	public static List<Rule> extractAllRules(Module module) {
		List<Rule> allExtractedRules = new LinkedList<Rule>();
		for (Unit unit : module.getUnits()) {
			if (unit instanceof Rule)
				allExtractedRules.add((Rule) unit);
		}
		return allExtractedRules;
	}

	private static URI saveCriticalPairInFileSystem(ResourceSet resourceSet,
			org.eclipse.emf.henshin.cpa.criticalpair.CriticalPair criticalPairOfThisProcess, String fullPath) {
		URI fileURI = URI.createFileURI(fullPath);
		Resource criticalPairResource = resourceSet.createResource(fileURI, "henshinCp");

		try {
			criticalPairResource.save(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileURI;
	}
}