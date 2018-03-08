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

import java.net.URISyntaxException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.presentation.EcoreEditor;
import org.eclipse.emf.henshin.multicda.cpa.ui.presentation.HenshinCPEditor;
import org.eclipse.emf.henshin.presentation.HenshinEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiEditorInput;

public class CpEditorUtil {

	public static String[] getInnerEditorIDs() {

		return new String[] { "org.eclipse.emf.henshin.presentation.HenshinEditorID",
				"org.eclipse.gmf.ecore.part.EcoreDiagramEditorID",
				"org.eclipse.emf.henshin.presentation.HenshinEditorID" };



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
		 * Ansatz f�r Feedback an Nutzer, dass eines der zugeh�rigen files nciht geladen werden konnte: (stammt aus
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
