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

import java.util.Set;

import org.eclipse.emf.henshin.multicda.cpa.CDAOptions;
import org.eclipse.emf.henshin.multicda.cpa.CDAOptions.GranularityType;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

public class OptionSettingsWizardPage extends WizardPage {

	private Composite container;

	private CDAOptions cpaOptions;

	private boolean optionsLoaded;
	Button binaryButton;
	Button coarseButton;
	Button fineButton;

	private final static String COMPLETE = "complete critical pairs (if not selected, search up to first critical match)";
	private final static String IGNOREIDENTICALRULES = "ignore critical pairs of same rules";
	private final static String REDUCESAMEMATCH = "ignore critical pairs of same rules and same matches";

	/**
	 * Default Constructor for the second page of the wizard. This page provides the functionality to adapt the options
	 * for the critical pair analysis.
	 * 
	 * @param pageName The name of the page.
	 * @param optionsFile Path to the options file.
	 */
	public OptionSettingsWizardPage(String pageName, String optionsFile) {
		super(pageName);
		setTitle("Conflict and Dependency Analysis - Granularity of Analysis");
		setDescription("Please indicate the depth of analysis.");

		cpaOptions = new CDAOptions();

		if (cpaOptions.load(optionsFile)) {
			optionsLoaded = true;
		} else {
			optionsLoaded = false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.emf.henshin.multicda.cpa.ui.wizard.OptionSettingsWizardPage.createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout());
		Composite granularity = new Composite(container, SWT.NONE);
		granularity.setLayout(new GridLayout(2, true));

		binaryButton = new Button(granularity, SWT.CHECK);
		binaryButton.setText(GranularityType.BINARY.name);
		binaryButton.addListener(SWT.Selection, checkListener);
		binaryButton.setSelection(getGranularity().contains(GranularityType.BINARY));
		binaryButton.setData(GranularityType.BINARY);
		Label label = new Label(granularity, SWT.NONE);
		label.setText(GranularityType.BINARY.description);

		coarseButton = new Button(granularity, SWT.CHECK);
		coarseButton.setText(GranularityType.COARSE.name);
		coarseButton.addListener(SWT.Selection, checkListener);
		coarseButton.setSelection(getGranularity().contains(GranularityType.COARSE));
		coarseButton.setData(GranularityType.COARSE);
		label = new Label(granularity, SWT.NONE);
		label.setText(GranularityType.COARSE.description);

		fineButton = new Button(granularity, SWT.CHECK);
		fineButton.setText(GranularityType.FINE.name);
		fineButton.addListener(SWT.Selection, checkListener);
		fineButton.setSelection(getGranularity().contains(GranularityType.FINE));
		fineButton.setData(GranularityType.FINE);
		label = new Label(granularity, SWT.NONE);
		label.setText(GranularityType.FINE.description);

		Button enableCompleteButton = new Button(container, SWT.CHECK);
		enableCompleteButton.setText(COMPLETE);
		enableCompleteButton.addListener(SWT.Selection, checkListener);
		enableCompleteButton.setSelection(getComplete());
		enableCompleteButton.setData(true);

		Button enableIgnoreIdenticalRulesButton = new Button(container, SWT.CHECK);
		enableIgnoreIdenticalRulesButton.setText(IGNOREIDENTICALRULES);
		enableIgnoreIdenticalRulesButton.addListener(SWT.Selection, checkListener);
		enableIgnoreIdenticalRulesButton.setSelection(getIgnoreIdenticalRules());
		enableIgnoreIdenticalRulesButton.setData(false);

		setControl(container);
	}

	Listener checkListener = new Listener() {
		public void handleEvent(Event event) {
			Object data = event.widget.getData();
			Button button = (Button) (event.widget);
			if(!binaryButton.getSelection() && !coarseButton.getSelection() && !fineButton.getSelection())
				button.setSelection(true);
			else if (data instanceof GranularityType)
				cpaOptions.granularityType += (((Button) event.widget).getSelection() ? 1 : -1)
						* ((GranularityType) data).id;
			else if ((Boolean) data)
				setComplete(button.getSelection());
			else
				setIgnoreIdenticalRules(button.getSelection());
		}
	};

	/**
	 * Returns <code>true</code> if the options had been loaded.
	 * 
	 * @return <code>true</code> if the options had been loaded.
	 */
	protected boolean isOptionsLoaded() {
		return optionsLoaded;
	}

	public CDAOptions getOptions() {
		return cpaOptions;
	}

	public Boolean getComplete() {
		return cpaOptions.isComplete();
	}

	public Set<GranularityType> getGranularity() {
		return GranularityType.getGranularities(cpaOptions.granularityType);
	}

	public void setComplete(Boolean complete) {
		cpaOptions.setComplete(complete);
	}

	public Boolean getStrongAttrCheck() {
		return cpaOptions.isStrongAttrCheck();
	}

	public Boolean getEqualVariableNameOfAttrMapping() {
		return cpaOptions.isEqualVName();
	}

	public Boolean getIgnoreIdenticalRules() {
		return cpaOptions.isIgnoreSameRules();
	}

	public void setIgnoreIdenticalRules(Boolean ignoreIdenticalRules) {
		cpaOptions.setIgnoreSameRules(ignoreIdenticalRules);
	}

	public Boolean getReduceSameMatch() {
		return cpaOptions.isReduceSameRuleAndSameMatch();
	}

	public void setReduceSameMatch(Boolean reduceSameMatch) {
		cpaOptions.setReduceSameRuleAndSameMatch(reduceSameMatch);
	}

	public Boolean getDirectlyStrictConfluent() {
		return cpaOptions.isDirectlyStrictConfluent();
	}
}
