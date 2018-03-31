/**
 * <copyright>
 * Copyright (c) 2010-2016 Henshin developers. All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the Eclipse Public License v1.0 which 
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * </copyright>
 */
package org.eclipse.emf.henshin.multicda.cpa.ui.wizard;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceFactoryImpl;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.Unit;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;
import org.eclipse.emf.henshin.multicda.cda.ConflictAnalysis;
import org.eclipse.emf.henshin.multicda.cda.DependencyAnalysis;
import org.eclipse.emf.henshin.multicda.cda.MultiGranularAnalysis;
import org.eclipse.emf.henshin.multicda.cda.Span;
import org.eclipse.emf.henshin.multicda.cpa.CDAOptions;
import org.eclipse.emf.henshin.multicda.cpa.CDAOptions.ConflictType;
import org.eclipse.emf.henshin.multicda.cpa.CDAOptions.GranularityType;
import org.eclipse.emf.henshin.multicda.cpa.CPAUtility;
import org.eclipse.emf.henshin.multicda.cpa.CpaByAGG;
import org.eclipse.emf.henshin.multicda.cpa.ICriticalPairAnalysis;
import org.eclipse.emf.henshin.multicda.cpa.UnsupportedRuleException;
import org.eclipse.emf.henshin.multicda.cpa.persist.SpanNode;
import org.eclipse.emf.henshin.multicda.cpa.result.CPAResult;
import org.eclipse.emf.henshin.multicda.cpa.result.CriticalPair;
import org.eclipse.emf.henshin.multicda.cpa.ui.presentation.CpaResultsView;
import org.eclipse.emf.henshin.multicda.cpa.ui.util.CpEditorUtil;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;

import agg.util.Pair;

public class CpaWizard extends Wizard {

	protected RuleAndCpKindSelectionWizardPage ruleAndCpKindSelectionWizardPage;
	protected OptionSettingsWizardPage optionSettingsWizardPage;
	String fileName = "";
	String resultPath = "";
	String optionsFile = "";
	MultiGranularAnalysis CDAdep;
	MultiGranularAnalysis CDAcon;
	Set<Span> cdaResultB = new HashSet<>();
	Set<Span> cdaResultC = new HashSet<>();
	Set<Span> cdaResultF = new HashSet<>();
	CPAResult cpaResult;
	HashMap<Rule, String> rulesAndAssociatedFileNames;

	/**
	 * Constructor of the wizard for configuring the critical pair analysis in the user interface.
	 * 
	 * @param selectedFiles The List of Files which serve as source for the rules.
	 */
	public CpaWizard(List<?> selectedFiles) {

		rulesAndAssociatedFileNames = new HashMap<Rule, String>();

		// collect the rules of all selected files
		for (Object selection : selectedFiles) {
			if (selection instanceof IFile) {

				IPath pathOfSelection = ((IFile) selection).getLocation();
				String pathAsString = pathOfSelection.toString();
				String pathWithoutFile = pathAsString.substring(0, pathAsString.indexOf(pathOfSelection.lastSegment()));

				if (resultPath.equals(""))
					resultPath = pathWithoutFile;
				resultPath = greatestCommonPrefix(resultPath, pathWithoutFile);
				IPath pathOfHenshinTransformationRules = pathOfSelection;
				//adapt file selection in case of a henshin_diagram files
				if (pathOfSelection.getFileExtension().equals("henshin_diagram")) {
					pathOfHenshinTransformationRules = pathOfSelection.removeFileExtension();
					pathOfHenshinTransformationRules = pathOfHenshinTransformationRules.addFileExtension("henshin");
				}
				String fileNameOfTransformationRules = pathOfHenshinTransformationRules
						.segment(pathOfHenshinTransformationRules.segmentCount() - 1);
				HenshinResourceSet henshinResourceSet = new HenshinResourceSet();
				Module module = henshinResourceSet.getModule(pathOfHenshinTransformationRules.toOSString());
				for (Unit unit : module.getUnits()) {
					if (unit instanceof Rule) {
						rulesAndAssociatedFileNames.put((Rule) unit, fileNameOfTransformationRules);
					}
				}
			}
			// filename for the options. Defined here static for the usage of the options with this wizard.
			optionsFile = resultPath + ".cpa.options";
		}
	}

	public void addPages() {

		ruleAndCpKindSelectionWizardPage = new RuleAndCpKindSelectionWizardPage(rulesAndAssociatedFileNames);
		addPage(ruleAndCpKindSelectionWizardPage);

		optionSettingsWizardPage = new OptionSettingsWizardPage("OPTIONS", optionsFile);
		addPage(optionSettingsWizardPage);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#needsProgressMonitor
	 */
	@Override
	public boolean needsProgressMonitor() {
		return true;
	}

	/**
	 * By finishing the Wizard the calculation of the critical pairs starts and afterwards the results are loaded within
	 * the <code>CPAView</code>.
	 */
	@Override
	public boolean performFinish() {

		CDAOptions options = optionSettingsWizardPage.getOptions();
		options.persist(optionsFile);
		options.cpTypes = ruleAndCpKindSelectionWizardPage.cpType;
		Pair<Set<Rule>, Set<Rule>> selectedRules = ruleAndCpKindSelectionWizardPage.getEnabledRules();
		Map<Rule, Rule> ignoredRulePairs = new HashMap<>();
		String rulePairs = "";
		Set<GranularityType> gs = GranularityType.getGranularities(options.granularityType);
		if (gs.contains(GranularityType.BINARY) || gs.contains(GranularityType.COARSE)
				|| gs.contains(GranularityType.FINE))
			for (Rule r1 : selectedRules.first)
				for (Rule r2 : selectedRules.second)
					if (r1.isMultiRule() || r2.isMultiRule()) {
						ignoredRulePairs.put(r1, r2);
						rulePairs += "\n" + r1.getName() + " -> " + r2.getName();
					}
		if (!ignoredRulePairs.isEmpty()) {
			JOptionPane.showMessageDialog(null,
					"Multirule kindness of rules is not fully supported by the multicda jet. The following rule pairs will be ignored by binary, coarse and fine granularities:\n"
							+ rulePairs,
					"Ignored Rule Pairs", JOptionPane.INFORMATION_MESSAGE);
		}
		boolean analysableRules = true;

		if (analysableRules) {

			try {
				getContainer().run(false, false, new IRunnableWithProgress() {

					@Override
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

						int totalWork = 10100;

						// adjustment in the case of calculation of dependencies and conflicts
						if (options.cpTypes == ConflictType.BOTH)
							totalWork = 20100;

						monitor.beginTask("Calculating Critical Pairs... ", totalWork);

						monitor.worked(30);
						//Check multi regel MultiCDA
						CPAResult conflictResult = null;
						CPAResult dependencyResult = null;

						CPAResult essConflictResult = null;
						CPAResult essDependencyResult = null;
						Set<GranularityType> granularities = GranularityType.getGranularities(options.granularityType);
						if (options.cpTypes == ConflictType.BOTH || options.cpTypes == ConflictType.CONFLICT) {
							for (Rule r1 : selectedRules.first)
								for (Rule r2 : selectedRules.second)
									if (!ignoredRulePairs.containsKey(r1) && !ignoredRulePairs.containsKey(r2))
										if (!(options.isIgnoreSameRules() && r1 == r2)) {
											CDAcon = new ConflictAnalysis(r1, r2);
											if (granularities.contains(GranularityType.BINARY))
												cdaResultB.add(CDAcon.computeResultsBinary());
											if (granularities.contains(GranularityType.COARSE))
												cdaResultC.addAll(CDAcon.computeResultsCoarse());
											if (granularities.contains(GranularityType.FINE))
												cdaResultF.addAll(CDAcon.computeResultsFine());
										}
							if (granularities.contains(GranularityType.VERY_FINE)) {
								if (options.essentialCP || options.initialCP) {
									essConflictResult = runCPA(selectedRules.first, selectedRules.second, options, true,
											true, monitor);
									monitor.worked(1000);
								}
								if (options.otherCP) {
									conflictResult = runCPA(selectedRules.first, selectedRules.second, options, true,
											false, monitor);
									monitor.worked(1000);
								}
								conflictResult = CpaByAGG.joinCPAResults(essConflictResult, conflictResult);
							}
						}
						if (options.cpTypes == ConflictType.BOTH || options.cpTypes == ConflictType.DEPENDENCY) {
							for (Rule r1 : selectedRules.first)
								for (Rule r2 : selectedRules.second)
									if (!ignoredRulePairs.containsKey(r1) && !ignoredRulePairs.containsKey(r2))
										if (!(options.isIgnoreSameRules() && r1 == r2)) {
											CDAdep = new DependencyAnalysis(r1, r2);
											if (granularities.contains(GranularityType.BINARY))
												cdaResultB.add(CDAdep.computeResultsBinary());
											if (granularities.contains(GranularityType.COARSE))
												cdaResultC.addAll(CDAdep.computeResultsCoarse());
											if (granularities.contains(GranularityType.FINE))
												cdaResultF.addAll(CDAdep.computeResultsFine());

										}
							if (options.essentialCP || options.initialCP) {
								essDependencyResult = runCPA(selectedRules.first, selectedRules.second, options, false,
										true, monitor);
								monitor.worked(1000);
							}
							if (options.otherCP) {
								dependencyResult = runCPA(selectedRules.first, selectedRules.second, options, false,
										false, monitor);
								monitor.worked(1000);
							}
							dependencyResult = CpaByAGG.joinCPAResults(essDependencyResult, dependencyResult);
						}

						cpaResult = CpaByAGG.joinCPAResults(conflictResult, dependencyResult);

						ResourceSet resSet = new ResourceSetImpl();
						resSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore",
								new XMLResourceFactoryImpl());

						monitor.worked(20);
						monitor.done();
					}

				});

//				System.out.println(CDAresult);
				HashMap<String, List<SpanNode>> persistedB = CpEditorUtil.persistCdaResult(cdaResultB, resultPath);
				HashMap<String, List<SpanNode>> persistedC = CpEditorUtil.persistCdaResult(cdaResultC, resultPath);
				HashMap<String, List<SpanNode>> persistedF = CpEditorUtil.persistCdaResult(cdaResultF, resultPath);
				List<CriticalPair> essential = null;
				List<CriticalPair> initial = null;
				List<CriticalPair> other = null;
				if (cpaResult != null) {
					if (options.initialCP)
						initial = cpaResult.getInitialCriticalPairs();
					if (options.essentialCP) {
						essential = cpaResult.getEssentialCriticalPairs();
						if (initial != null)
							essential.removeAll(initial);
					}
					if (options.otherCP) {
						other = cpaResult.getOtherCriticalPairs();
//						if (initial != null) //TODO: Das löschen geht noch nicht, da es keine vernünftige equals methode gibt
//							other.removeAll(initial);
//						if (essential != null)
//							other.removeAll(essential);
					}
				}
				HashMap<String, List<SpanNode>> initialCpaResult = CPAUtility.persistCpaResult(initial, resultPath);
				HashMap<String, List<SpanNode>> essentialCpaResult = CPAUtility.persistCpaResult(essential, resultPath);
				HashMap<String, List<SpanNode>> otherCpaResult = CPAUtility.persistCpaResult(other, resultPath);

				ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);

				IViewPart cPAView = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
						.showView("org.eclipse.emf.henshin.multicda.cpa.ui.views.CPAView");
				if (cPAView instanceof CpaResultsView) {
					CpaResultsView view = (CpaResultsView) cPAView;
					view.setContent(persistedB, persistedC, persistedF, initialCpaResult, essentialCpaResult,
							otherCpaResult);
					view.update();
				}

				return true; // close Wizard

			} catch (InvocationTargetException e1) {
				// errors caused by the IRunnable
				e1.printStackTrace();
			} catch (InterruptedException e1) {
				// Should never occur because cancelable is set to false
				e1.printStackTrace();
			} catch (CoreException e) {
				// from ResourcesPlugin...refreshLocal(..)
				e.printStackTrace();
			}
		}

		return false; // keep Wizard open after ErrorMessage for reselection of the rules
	}

	private CPAResult runCPA(Set<Rule> r1, Set<Rule> r2, CDAOptions options, boolean conf, boolean essential,
			IProgressMonitor monitor) {

		boolean essentialTemp = options.essentialCP;
		options.essentialCP = essential;
		CPAResult result;
		ICriticalPairAnalysis cpa = new CpaByAGG();
		try {
			cpa.init(r1, r2, options);

		} catch (UnsupportedRuleException e) {
			MessageDialog.openError(getShell(), "Error occured while initialising the critical pair analysis!",
					e.getDetailedMessage() + "\n Thus critical pairs cannot be calculated.");
		}
		if (conf)
			result = cpa.runConflictAnalysis(monitor);
		else
			result = cpa.runDependencyAnalysis(monitor);
		options.essentialCP = essentialTemp;
		return result;
	}

	private String greatestCommonPrefix(String a, String b) {
		int minLength = Math.min(a.length(), b.length());
		for (int i = 0; i < minLength; i++) {
			if (a.charAt(i) != b.charAt(i)) {
				return a.substring(0, i);
			}
		}
		return a.substring(0, minLength);
	}

}
