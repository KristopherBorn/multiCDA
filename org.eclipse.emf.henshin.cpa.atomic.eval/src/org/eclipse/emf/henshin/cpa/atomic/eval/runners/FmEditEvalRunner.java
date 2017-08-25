package org.eclipse.emf.henshin.cpa.atomic.eval.runners;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

public class FmEditEvalRunner extends EvalRunner {

	private ResourceSetImpl resourceSet;
	List<String> ofInterest = Arrays.asList("addToGroup_features_Feature","removeFromGroup_features_Feature" );

	public static List<Granularity> granularities =  Arrays.asList(Granularity.fine,Granularity.ess);
//	public static List<Granularity> granularities =  Arrays.asList(Granularity.atoms,Granularity.coarse,Granularity.fine,Granularity.ess);
//	public static List<Granularity> granularities =  Arrays.asList(Granularity.atoms,Granularity.fine,Granularity.coarse,Granularity.ess,Granularity.essUnfiltered);
	public static Type type = Type.conflicts;
	
	public static void main(String[] args) {
		new FmEditEvalRunner().run(granularities, type);
	}
	
	@Override
	public void init() {
		FeatureModelPackage.eINSTANCE.eClass();

		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		Map<String, Object> m = reg.getExtensionToFactoryMap();
		m.put("xmi", new XMIResourceFactoryImpl());
		resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore",
				new EcoreResourceFactoryImpl());
	}

	@Override
	public List<Rule> getRules() {
		final File f = new File(FmEditEvalRunner.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		String filePath = f.toString();
		String projectPath = filePath.replaceAll("bin", "");
		String subDirectoryPath = "rules\\fmedit";
		String fullSubDirectoryPath = projectPath + subDirectoryPath;
		File dir = new File(fullSubDirectoryPath);
		return HenshinRuleLoader.loadAllRulesFromFileSystemPaths(dir);
//		return HenshinRuleLoader.loadAllRulesFromFileSystemPaths(dir).subList(0, 10);
//		return HenshinRuleLoader.loadAllRulesFromFileSystemPaths(dir).stream().filter(r -> ofInterest.contains(r.getName())).collect(Collectors.toList());

	}
	

}
