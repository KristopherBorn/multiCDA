package org.eclipse.emf.henshin.multicda.cda.runner.overapproximation;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.Unit;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;

public class LoaderUtil {

	// Constructor
	public LoaderUtil(){
		
	}
	
	public List<Rule> loadAllRulesFromFileSystemPaths(List<String> pathsToHenshinFiles) {
		List<Rule> allEditRulesWithoutAmalgamation = new LinkedList<Rule>();

		for (String pathToHenshinFiles : pathsToHenshinFiles) {
			HenshinResourceSet henshinResourceSet = new HenshinResourceSet();
			Module module = henshinResourceSet.getModule(pathToHenshinFiles);
			for (Unit unit : module.getUnits()) {
				if (unit instanceof Rule /* && numberOfAddedRules<10 */) {
					// rulesAndAssociatedFileNames.put((Rule) unit, fileName);
//					boolean deactivatedRule = false;
//					for (String deactivatedRuleName : namesOfDeactivatedRules) {
//						if (unit.getName().contains(deactivatedRuleName))
//							deactivatedRule = true;
//					}
//					if (!deactivatedRule) {
						allEditRulesWithoutAmalgamation.add((Rule) unit);
//					}
				}
			}
		}
		return allEditRulesWithoutAmalgamation;
	}
}
