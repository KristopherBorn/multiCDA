package org.eclipse.emf.henshin.multicda.cda.eval.runners.overapproxrunner;

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
import org.eclipse.emf.henshin.multicda.cda.eval.EvalRunner;
import org.eclipse.emf.henshin.multicda.cda.eval.Granularity;
import org.eclipse.emf.henshin.multicda.cda.eval.Type;
import org.eclipse.emf.henshin.multicda.cda.eval.util.HenshinRuleLoader;

import de.imotep.featuremodel.variability.metamodel.FeatureModel.FeatureModelPackage;

public class NanoXmlOverapproxRunner extends OverapproxEvalRunner {

	public static Type type = Type.dependencies;
	
	public static void main(String[] args) {
		new NanoXmlOverapproxRunner().run(type);
	}
	
	@Override
	public void init() {
//
	}

	@Override
	public List<Rule> getRules() {
		final File f = new File(NanoXmlOverapproxRunner.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		String filePath = f.toString();
		String projectPath = filePath.replaceAll("bin", "");
		String subDirectoryPath = "rules\\nanoxml\\";
		String fullSubDirectoryPath = projectPath + subDirectoryPath;
		File dir = new File(fullSubDirectoryPath);
		return HenshinRuleLoader.loadAllRulesFromFileSystemPaths(dir);
//		return HenshinRuleLoader.loadAllRulesFromFileSystemPaths(dir).subList(0, 8);
	}

	@Override
	public String getDomainName() {
		return "overapprox\\nanoxml";
	}
	

}
