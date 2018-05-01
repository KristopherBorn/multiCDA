/**
 * <copyright>
 * Copyright (c) 2010-2016 Henshin developers. All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the Eclipse Public License v1.0 which 
 * accompanies this distribution, and is available at
 * http:
 * </copyright>
 */
package org.eclipse.emf.henshin.multicda.cpa.ui.util;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.presentation.EcoreEditor;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.multicda.cda.Span;
import org.eclipse.emf.henshin.multicda.cda.Utils;
import org.eclipse.emf.henshin.multicda.cda.conflict.DeleteDeleteConflictReason;
import org.eclipse.emf.henshin.multicda.cda.conflict.DeleteReadConflictReason;
import org.eclipse.emf.henshin.multicda.cpa.persist.SpanNode;
import org.eclipse.emf.henshin.multicda.cpa.result.CriticalPair;
import org.eclipse.emf.henshin.multicda.cpa.ui.presentation.HenshinCPEditor;
import org.eclipse.emf.henshin.presentation.HenshinEditor;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.MeasurementUnit;
import org.eclipse.gmf.runtime.notation.NotationFactory;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiEditorInput;

import agg.util.Pair;

public class CpEditorUtil {

	public static String[] getInnerEditorIDs() {

		return new String[] { "org.eclipse.emf.henshin.presentation.HenshinEditorID",
				"org.eclipse.gmf.ecore.part.EcoreDiagramEditorID",
				"org.eclipse.emf.henshin.presentation.HenshinEditorID" };

	}

	public static Map<String, List<SpanNode>> persistCdaResult(Set<Span> cdaResult, String path) {

		Map<String, List<SpanNode>> persistedNodes = new TreeMap<>();
		if (cdaResult != null) {
			List<Span> reasons = new ArrayList<>(cdaResult);
			Collections.sort(reasons);
			for (Span reason : reasons)
				if (reason != null) {
					// naming of each single conflict
					String folderName = reason.getRule1().getName() + ", " + reason.getRule2().getName();

					int numberForRulePair = 1;

					if (persistedNodes.containsKey(folderName)) {
						numberForRulePair = persistedNodes.get(folderName).size() + 1;
					} else {
						persistedNodes.put(folderName, new ArrayList<>());
					}

					String spanKind = reason.getClass().getSimpleName();
					if (reason instanceof DeleteReadConflictReason)
						spanKind = "DRCR";
					if (reason instanceof DeleteDeleteConflictReason)
						spanKind = "DDCR";
//			Replace(str, @"([a-z])([A-Z])", "$1 $2")
					spanKind = spanKind.replaceAll("([a-z])([A-Z])", "$1 $2");

					String formatedNumberForRulePair = new DecimalFormat("00").format(numberForRulePair);

					String numberedNameOfCPKind = "(" + formatedNumberForRulePair + ") " + spanKind;

//			// persist a single critical pair.
//			SpanNode newCriticalPairNode = persistSingleCriticalPair(span, numberedNameOfCPKind,
//					pathWithDateStamp);

					String pathForCurrentCriticalPair = path + File.separator + reason.getRule1().getName() + "_AND_"
							+ reason.getRule2().getName() + File.separator + numberedNameOfCPKind + File.separator;

					ResourceSet commonResourceSet = new ResourceSetImpl();
					// save the first rule in the file system
					String fileNameRule1 = "(1)" + reason.getRule1().getName() + ".henshin";
					String fullPathRule1 = pathForCurrentCriticalPair + fileNameRule1;
					URI firstRuleURI = saveRuleInFileSystem(commonResourceSet, reason.getRule1(), fullPathRule1);

//			// save the minimal model in the file system
//			String fileNameMinimalModel = "minimal-model" + ".ecore";
//			String fullPathMinimalModel = pathForCurrentCriticalPair + fileNameMinimalModel;

//			URI overlapURI = saveMinimalModelInFileSystem(commonResourceSet, minimalModel, fullPathMinimalModel);

					// save the second rule in the file system
					String fileNameRule2 = "(2)" + reason.getRule2().getName() + ".henshin";
					String fullPathRule2 = pathForCurrentCriticalPair + fileNameRule2;
					URI secondRuleURI = saveRuleInFileSystem(commonResourceSet, reason.getRule2(), fullPathRule2);

					// save a dummy for the HenshinCPEditor
					String fileName = "dummy.henshinCp";
					String fullPath = pathForCurrentCriticalPair + fileName;
					URI criticalPairURI = saveRuleInFileSystem(commonResourceSet, reason.graph, fullPath);

					// save the minimal model in the file system
					String fileNameMinimalModel = "minimal-model" + ".ecore";
					String fullPathMinimalModel = pathForCurrentCriticalPair + fileNameMinimalModel;
					EPackage reasonModel = Utils.graphToEPackage(reason);
					EPackage s2Model = null;
					if (reason instanceof DeleteDeleteConflictReason)
						s2Model = Utils.graphToEPackage(((DeleteDeleteConflictReason) reason).getSpan2());

					Pair<URI, URI> overlapURI = saveMinimalModelInFileSystem(commonResourceSet, reasonModel, s2Model,
							fullPathMinimalModel);
					persistedNodes.get(folderName).add(new SpanNode(numberedNameOfCPKind, firstRuleURI, secondRuleURI,
							overlapURI, criticalPairURI));
				}
		}
		return persistedNodes;
	}

	/**
	 * Saves an <code>EGraph</code>, which might be a minimal model on the given path within the file system.
	 * 
	 * @param resourceSetThe common <code>ResourceSet</code>.
	 * @param minimalModel The minimal model to be saved.
	 * @param fullPathMinimalModel The full path of the file.
	 * @return the <code>URI</code> of the saved file.
	 */
	private static Pair<URI, URI> saveMinimalModelInFileSystem(ResourceSet resourceSet, EPackage minimalModel,
			EPackage s2Model, String fullPathMinimalModel) {
		URI overlapURI = URI.createFileURI(fullPathMinimalModel);
		Resource overlapResource = resourceSet.createResource(overlapURI, "ecore");

		overlapResource.getContents().add(minimalModel);
		Diagram d = createDiagram(minimalModel);

		URI diagUri = URI.createFileURI(fullPathMinimalModel + "_diagram");
		URI s2DiagUri = URI.createFileURI(fullPathMinimalModel.split(".ecore")[0] + "_s2.ecore_diagram");
		Resource diagramResource = resourceSet.createResource(diagUri, "ecore");
		d.setName(diagUri.lastSegment());
		diagramResource.getContents().add(d);

		if (s2Model != null) {
			overlapResource.getContents().add(s2Model);
			Diagram s2D = createDiagram(s2Model);
			Resource s2DiagramResource = resourceSet.createResource(s2DiagUri, "ecore");
			s2D.setName(diagUri.lastSegment());
			s2DiagramResource.getContents().add(s2D);
			try {
				s2DiagramResource.save(null);
			} catch (IOException e) {
			}
		}
		try {
			diagramResource.save(null);
			overlapResource.save(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new Pair<>(diagUri, s2DiagUri);
	}

	public static Diagram createDiagram(EObject object) {
		Diagram diagram = NotationFactory.eINSTANCE.createDiagram();
		diagram.setMeasurementUnit(MeasurementUnit.PIXEL_LITERAL);
		diagram.getStyles().add(NotationFactory.eINSTANCE.createDiagramStyle());
		diagram.setElement(object);
		diagram.setType("Ecore");
		return diagram;
	}

	/**
	 * Saves a <code>Rule</code> on the given path within the file system.
	 * 
	 * @param resourceSet The common <code>ResourceSet</code>.
	 * @param rule The <code>Rule</code> to be saved.
	 * @param fullFilePath The full path of the file.
	 * @return the <code>URI</code> of the saved file.
	 */
	private static URI saveRuleInFileSystem(ResourceSet resourceSet, EObject content, String fullFilePath) {
		URI firstRuleURI = URI.createFileURI(fullFilePath);
		Resource firstRuleRes;
		if (content instanceof CriticalPair || content instanceof Graph)
			firstRuleRes = resourceSet.createResource(firstRuleURI, "henshinCp");
		else
			firstRuleRes = resourceSet.createResource(firstRuleURI, "henshin");
		firstRuleRes.getContents().add(content);

		try {
			firstRuleRes.save(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return firstRuleURI;
	}

	public static void openResultInCpEditor(URI firstRuleUri, URI overlapUri, URI secondRuleUri) {

		/** URIs of henshin1, ecore[, henshin2 [.ecore, .wcoreextended, .gcore and .wcore files] */
		URI[] modelURIs = new URI[3];

		modelURIs[0] = firstRuleUri;
		modelURIs[1] = overlapUri;
		modelURIs[2] = secondRuleUri;

		IEditorInput[] editorInputs;
		try {

			editorInputs = createEditorInputsAndModelURIs(modelURIs);

			MultiEditorInput multiEditorInput = new MultiEditorInput(CpEditorUtil.getInnerEditorIDs(), editorInputs);

			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IEditorPart editor = page.openEditor(multiEditorInput, HenshinCPEditor.MULTI_EDITOR_ID, true);

			if (editor instanceof HenshinCPEditor) {

				HenshinCPEditor openedHenshinCPEditor = (HenshinCPEditor) editor;
				IEditorPart[] innerEditors = openedHenshinCPEditor.getInnerEditors();

				for (IEditorPart iEditorPart : innerEditors) {

					if (iEditorPart instanceof EcoreEditor) {
						EcoreEditor ecoreEditor = (EcoreEditor) iEditorPart;
						Viewer viewer = ecoreEditor.getViewer();

						TreeViewer tViewer = (TreeViewer) viewer;

						tViewer.expandToLevel(4);
					}

					if (iEditorPart instanceof HenshinEditor) {
						HenshinEditor henshinEditor = (HenshinEditor) iEditorPart;
					}
				}
			}

		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (PartInitException e) {
			e.printStackTrace();
		}

	}

	private static IEditorInput[] createEditorInputsAndModelURIs(URI[] modelURIs) throws URISyntaxException {

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot workspaceRoot = workspace.getRoot();

		java.net.URI myURI0 = new java.net.URI(modelURIs[0].toString());
		IFile[] iFile0 = workspaceRoot.findFilesForLocationURI(myURI0);
		FileEditorInput firstRuleFileEditorInput = new FileEditorInput(iFile0[0]);
		boolean firstRuleFileEditorInputExists = firstRuleFileEditorInput.exists();
		/*
		 * Ansatz für Feedback an Nutzer, dass eines der zugehörigen files nciht geladen werden konnte: (stammt aus
		 * HenshinateHenshinFileHandler)
		 * 
		 * 
		 * 
		 * "Please select exactly one *." + HenshinResource.FILE_EXTENSION + " file.");
		 */

		java.net.URI myURI1 = new java.net.URI(modelURIs[1].toString());
		IFile[] iFile1 = workspaceRoot.findFilesForLocationURI(myURI1);
		FileEditorInput minimalModelFileEditorInput = new FileEditorInput(iFile1[0]);
		boolean minimalModelFileEditorInputExists = minimalModelFileEditorInput.exists();

		java.net.URI myURI2 = new java.net.URI(modelURIs[2].toString());
		IFile[] iFile2 = workspaceRoot.findFilesForLocationURI(myURI2);
		FileEditorInput secondRuleFileEditorInput = new FileEditorInput(iFile2[0]);
		boolean secondRuleFileEditorInputExists = secondRuleFileEditorInput.exists();

		IEditorInput[] editorInputs = new IEditorInput[3];
		editorInputs[0] = firstRuleFileEditorInput;
		editorInputs[1] = minimalModelFileEditorInput;

		editorInputs[2] = secondRuleFileEditorInput;

		return editorInputs;
	}

}
