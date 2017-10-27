package org.eclipse.emf.henshin.multicda.cda.eval.runners;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.henshin.cpa.result.CPAResult;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.multicda.cda.eval.Granularity;
import org.eclipse.emf.henshin.multicda.cda.eval.Type;
import org.eclipse.emf.henshin.multicda.cda.eval.UmlEvalRunner;
import org.eclipse.emf.henshin.multicda.cda.eval.util.EssCPARunner;
import org.eclipse.emf.henshin.multicda.cda.eval.util.HenshinRuleLoader;
import org.eclipse.emf.henshin.multicda.cda.eval.util.Logger;
import org.eclipse.emf.henshin.multicda.cda.eval.util.RulePair;
import org.eclipse.emf.henshin.multicda.cda.eval.util.Logger.LogData;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.internal.impl.UMLPackageImpl;

public class UmlEditManualEvalRunner extends UmlEvalRunner {

	private ResourceSetImpl resourceSet;

	public static List<Granularity> granularities = Arrays.asList(
//			Granularity.coarse, 
			Granularity.fine
			, Granularity.ess
			, Granularity.essUnfiltered
			
	// , Granularity.binary
	);
	
	
	List<String> subset = Arrays.asList(
	"deleteParameter_IN_Operation"
	);
	
	public static Type type = Type.conflicts;

	public static void main(String[] args) {
		new UmlEditManualEvalRunner().run(granularities, type);
	}

	@Override
	public void init() {
		UMLPackage.eINSTANCE.eClass();

		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		Map<String, Object> m = reg.getExtensionToFactoryMap();

		EPackage.Registry.INSTANCE.put("http://www.eclipse.org/uml2/4.0.0/UML", UMLPackageImpl.eINSTANCE);

		m.put("xmi", new XMIResourceFactoryImpl());
		resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore",
				new EcoreResourceFactoryImpl());
	}

	@Override
	public List<Rule> getRules() {
		final File f = new File(
				UmlEditManualEvalRunner.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		String filePath = f.toString();
		String projectPath = filePath.replaceAll("bin", "");
		String subDirectoryPath = "rules\\umledit\\manual\\delete\\";
		String fullSubDirectoryPath = projectPath + subDirectoryPath;
		File dir = new File(fullSubDirectoryPath);
		return HenshinRuleLoader.loadAllRulesFromFileSystemPaths(dir);
//		return HenshinRuleLoader.loadAllRulesFromFileSystemPaths(dir).subList(0, 5);
//		return HenshinRuleLoader.loadAllRulesFromFileSystemPaths(dir).stream().filter(r -> subset.contains(r.getName())).collect(Collectors.toList());

	}

	@Override
	public String getDomainName() {
		return "umledit-manual";
	}
}
