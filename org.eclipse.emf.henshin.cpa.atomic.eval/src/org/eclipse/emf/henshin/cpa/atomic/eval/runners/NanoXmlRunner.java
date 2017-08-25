package org.eclipse.emf.henshin.cpa.atomic.eval.runners;

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
import org.eclipse.emf.henshin.cpa.atomic.eval.EvalRunner;
import org.eclipse.emf.henshin.cpa.atomic.eval.Granularity;
import org.eclipse.emf.henshin.cpa.atomic.eval.Type;
import org.eclipse.emf.henshin.cpa.atomic.eval.util.HenshinRuleLoader;
import org.eclipse.emf.henshin.model.Rule;

import de.imotep.featuremodel.variability.metamodel.FeatureModel.FeatureModelPackage;

public class NanoXmlRunner extends EvalRunner {

	private ResourceSetImpl resourceSet;

	public static List<Granularity> granularities = Arrays.asList(Granularity.fine,Granularity.ess);
	public static Type type = Type.dependencies;
	
	public static void main(String[] args) {
		new NanoXmlRunner().run(granularities, type);
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
		URI fileURI = URI.createURI("rules\\nanoxml\\nanoXML.ecore");
		Resource res = resourceSet.getResource(fileURI, true);
		EPackage pack = (EPackage) res.getContents().get(0);
		EPackage.Registry.INSTANCE.put(pack.getNsURI(), pack);
	}

	@Override
	public List<Rule> getRules() {
		final File f = new File(NanoXmlRunner.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		String filePath = f.toString();
		String projectPath = filePath.replaceAll("bin", "");
		String subDirectoryPath = "rules\\nanoxml\\";
		String fullSubDirectoryPath = projectPath + subDirectoryPath;
		File dir = new File(fullSubDirectoryPath);
		return HenshinRuleLoader.loadAllRulesFromFileSystemPaths(dir).subList(0, 8);
	}
	

}
