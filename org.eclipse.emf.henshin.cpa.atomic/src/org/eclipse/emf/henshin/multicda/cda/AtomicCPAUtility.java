package org.eclipse.emf.henshin.multicda.cda;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.multicda.cda.conflict.ConflictAtom;

public class AtomicCPAUtility {
	
	ResourceSet commonResourceSet;
	
	// (17.04.2017) niorgends genutzt - DEPRECATED!
//	/**
//	 * Persists the results of an atomic critical pair analysisDuration in the file system.
//	 * 
//	 * @param cpaResult A <code>CPAResult</code> of a critical pair analysisDuration.
//	 * @param path The path for saving the full result set.
//	 * @return a <code>HashMap</code> of the saved results.
//	 */
//	public static HashMap<String, Set<CriticalPairNode>> persistAtomicCpaResult(CPAResult cpaResult, String path) {
//		
//		/*
//		 * Je Regelpaarung speichern von:
//		 * - Regel1 und Regel2
//		 * 	--> SUBMETHOD!
//		 * - die ConflictPartCandidates (1*Node ODER 2*Node+2*Edge) --> "Graph" als Datentyp in dem es gespeichert wird.
//		 * 	- sowie den verschiedenen Mappings in die beiden Regeln
//		 * 		- Je gefundenem ConflictPartCandidate existiert eine Isntanz der Klasse Span in der jeweils ein Grpah und die Mappings enthalten sind.
//		 * 		--> müssen alle ConflictPartCandidate  unabhängiggepsiechert werdne, 
//		 * 			oder reicht je NodeType ein Graph zu speichern und die verschiedenen Mappings zu ändern?
//		 * 				vermutlich besser nicht!
//		 *  --> SUBMETHOD!
//		 * - die Conflict Atoms
//		 *  - erneut Graph als Datenstruktur
//		 * 	- [KEINE Instanzen des jeweiligen Metamodels]
//		 * 	- Im Prinziep das PushoutResult!
//		 * 	 - + Verknüpfung in die Regeln, also die Mappings 
//		 *  --> SUBMETHOD!
//		 * - die minimal conflict reason
//		 *  - erneut Graph als Datenstruktur
//		 * 	- [KEINE Instanzen des jeweiligen Metamodels]
//		 * 	- Im Prinziep das PushoutResult!
//		 * 	 - + Verknüpfung in die Regeln, also die Mappings 
//		 *  --> SUBMETHOD!
//		 * 
//		 */
//			 return new HashMap();
//	}

	public void persistAtomicCPAResult(String/*String or File or URI?*/path, Rule rule1,Rule rule2, List<Span> conflictPartCandidates, List<ConflictAtom> conflictAtoms, Set<Span> minimalConflictReasons){
		//TODO: für "path" an Henshin oritentieren. Siehe Wizards usw.
		//TODO: wenn möglich auch noch die grafischen Repräsentationen der Regeln hinzufügen!
		// persistHenshinDiagramsOfRules
		
		Date timestamp = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd-HHmmss");
		String timestampFolder = simpleDateFormat.format(timestamp);

		String pathWithDateStamp = path + File.separator + timestampFolder;
		
		// naming of each single conflict
		String folderName = rule1.getName() + ", " + rule2.getName();

		int numberForRulePair = 1;

//		if (persistedCPs.containsKey(folderName)) {
//			numberForRulePair = persistedCPs.get(folderName).size() + 1;
//		} else {
//			persistedCPs.put(folderName, new HashSet<CriticalPairNode>());
//		}

		String criticalPairKind = "conflict";
//		if (cp instanceof Conflict) {
//			criticalPairKind = ((Conflict) cp).getConflictKind().toString();
//		} else if (cp instanceof Dependency) {
//			criticalPairKind = ((Dependency) cp).getDependencyKind().toString();
//		}

		String formatedNumberForRulePair = new DecimalFormat("00").format(numberForRulePair);

		String numberedNameOfCPKind = "(" + formatedNumberForRulePair + ") " + criticalPairKind;
		

		commonResourceSet = new ResourceSetImpl();
		
		// persist rules.
		persistRules(path, rule1, rule2);
		
		//persist ConflictPartCandidates
		persistSpans(path, rule1, rule2, conflictPartCandidates);

		//persist conflictAtoms
		List<Span> conflictAtomSpans = extractSpans(conflictAtoms);
		persistSpans(path, rule1, rule2, conflictAtomSpans);
		
		//persist minimalConflictReasons
		persistSpans(path, rule1, rule2, minimalConflictReasons);
	}
	
	private List<Span> extractSpans(List<ConflictAtom> conflictAtoms) {
		List<Span> spans = new LinkedList<Span>();
		for(ConflictAtom conflictAtom : conflictAtoms)
			spans.add(conflictAtom.getSpan());
		return spans;
	}

	private void persistSpans(String/*String or File or URI?*/path, Rule rule1,Rule rule2, Collection<Span> spans){
		//TODO: Graph Mappings! 
		
	}

	private void persistRules(String/*String or File or URI?*/path, Rule rule1,Rule rule2){
		persistRule(path, rule1, "(1)");
		persistRule(path, rule2, "(2)");
	}
	
	private void persistRule(String/*String or File or URI?*/path, Rule rule, String prefix){
		// save the first rule in the file system
		String fileNameRule1 = prefix + rule.getName() + ".henshin";
		String fullPathRule1 = path + fileNameRule1;
		URI firstRuleURI = saveRuleInFileSystem(commonResourceSet, rule, fullPathRule1);

	}
	
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

	
}
