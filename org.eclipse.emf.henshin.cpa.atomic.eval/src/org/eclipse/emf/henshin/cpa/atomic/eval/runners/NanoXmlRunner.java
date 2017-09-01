package org.eclipse.emf.henshin.cpa.atomic.eval.runners;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ContentHandler.Registry;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.henshin.cpa.atomic.eval.EvalRunner;
import org.eclipse.emf.henshin.cpa.atomic.eval.Granularity;
import org.eclipse.emf.henshin.cpa.atomic.eval.Type;
import org.eclipse.emf.henshin.cpa.atomic.eval.util.HenshinRuleLoader;
import org.eclipse.emf.henshin.model.Rule;

import de.imotep.featuremodel.variability.metamodel.FeatureModel.FeatureModelPackage;

public class NanoXmlRunner extends EvalRunner {

//	public static List<Granularity> granularities =  Arrays.asList(Granularity.coarse,Granularity.fine,Granularity.ess,Granularity.binary);

	public static List<Granularity> granularities =  Arrays.asList(
			Granularity.fine,
			Granularity.ess
//			,Granularity.essUnfiltered
			);
//	public static List<Granularity> granularities =  Arrays.asList(Granularity.essSecondDeleting);

	List<String> subset = Arrays.asList(
	"28039_removeChild",
	"28085_getParent",
	"28215_setAttribute",
	"28325_setAttribute"
	,"28364_addChildren"
	);
	
	public static Type type = Type.dependencies;
	
	public static void main(String[] args) {
		new NanoXmlRunner().run(granularities, type);
	}
	
	@Override
	public void init() {
//
	}

	@Override
	public List<Rule> getRules() {
		final File f = new File(NanoXmlRunner.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		String filePath = f.toString();
		String projectPath = filePath.replaceAll("bin", "");
		String subDirectoryPath = "rules\\nanoxml\\";
		String fullSubDirectoryPath = projectPath + subDirectoryPath;
		File dir = new File(fullSubDirectoryPath);
		return HenshinRuleLoader.loadAllRulesFromFileSystemPaths(dir);
//		return HenshinRuleLoader.loadAllRulesFromFileSystemPaths(dir).subList(4, 8);
//		return HenshinRuleLoader.loadAllRulesFromFileSystemPaths(dir).stream().filter(r -> subset.contains(r.getName())).collect(Collectors.toList());

	}

	@Override
	public String getDomainName() {
		return "nanoxml";
	}
	

}
