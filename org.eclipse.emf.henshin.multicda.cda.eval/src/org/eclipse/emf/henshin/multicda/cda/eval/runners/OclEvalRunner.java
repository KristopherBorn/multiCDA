package org.eclipse.emf.henshin.multicda.cda.eval.runners;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ContentHandler.Registry;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;
import org.eclipse.emf.henshin.multicda.cda.eval.EvalRunner;
import org.eclipse.emf.henshin.multicda.cda.eval.Granularity;
import org.eclipse.emf.henshin.multicda.cda.eval.Type;
import org.eclipse.emf.henshin.multicda.cda.eval.util.HenshinRuleLoader;

import de.imotep.featuremodel.variability.metamodel.FeatureModel.FeatureModelPackage;

public class OclEvalRunner extends EvalRunner {

	public static List<Granularity> granularities =  Arrays.asList(Granularity.coarse,Granularity.fine,Granularity.ess,Granularity.binary);
	public static Type type = Type.conflicts;
	
	public static void main(String[] args) {
		new OclEvalRunner().run(granularities, type);
	}
	
	@Override
	public void init()  {
	
	Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
	Map<String, Object> m = reg.getExtensionToFactoryMap();
	m.put("ecore", new XMIResourceFactoryImpl());
	m.put("oclas", new XMIResourceFactoryImpl());
	m.put("graphConstraint", new XMIResourceFactoryImpl());

	// Create a resource set with a base directory:
	HenshinResourceSet resourceSet = new HenshinResourceSet("rules\\ocl");
	resourceSet.registerDynamicEPackages("Pivot.ecore");
	resourceSet.registerDynamicEPackages("GraphConstraint.ecore");


	}

	@Override
	public List<Rule> getRules() {
		final File f = new File(OclEvalRunner.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		String filePath = f.toString();
		String projectPath = filePath.replaceAll("bin", "");
		String subDirectoryPath = "rules\\ocl\\";
		String fullSubDirectoryPath = projectPath + subDirectoryPath;
		File dir = new File(fullSubDirectoryPath);
		return HenshinRuleLoader.loadAllRulesFromFileSystemPaths(dir);
//		return HenshinRuleLoader.loadAllRulesFromFileSystemPaths(dir).subList(0, 8);
	}

	@Override
	public String getDomainName() {
		return "nanoxml";
	}
	

}
