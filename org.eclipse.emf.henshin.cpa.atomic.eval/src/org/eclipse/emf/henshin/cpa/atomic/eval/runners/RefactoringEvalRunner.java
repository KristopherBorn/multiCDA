package org.eclipse.emf.henshin.cpa.atomic.eval.runners;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.henshin.cpa.atomic.eval.EvalRunner;
import org.eclipse.emf.henshin.cpa.atomic.eval.Granularity;
import org.eclipse.emf.henshin.cpa.atomic.eval.Type;
import org.eclipse.emf.henshin.cpa.atomic.eval.util.HenshinRuleLoader;
import org.eclipse.emf.henshin.model.Rule;

import de.imotep.featuremodel.variability.metamodel.FeatureModel.FeatureModelPackage;

public class RefactoringEvalRunner extends EvalRunner {

	private ResourceSetImpl resourceSet;

	public static List<Granularity> granularities =  Arrays.asList(Granularity.coarse,Granularity.fine,Granularity.ess,Granularity.binary);
	public static Type type = Type.conflicts;
	
	public static void main(String[] args) {
		new RefactoringEvalRunner().run(granularities, type);
	}
	
	@Override
	public void init() {
		EcorePackage.eINSTANCE.eClass();

		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		Map<String, Object> m = reg.getExtensionToFactoryMap();
		m.put("xmi", new XMIResourceFactoryImpl());
		resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore",
				new EcoreResourceFactoryImpl());
	}

	@Override
	public List<Rule> getRules() {
		final File f = new File(RefactoringEvalRunner.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		String filePath = f.toString();
		String projectPath = filePath.replaceAll("bin", "");
		String subDirectoryPath = "rules\\refactoring";
		String fullSubDirectoryPath = projectPath + subDirectoryPath;
		File dir = new File(fullSubDirectoryPath);
		return HenshinRuleLoader.loadAllRulesFromFileSystemPaths(dir);
	}
	

	@Override
	public String getDomainName() {
		return "refactoring";
	}
}
