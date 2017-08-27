package org.eclipse.emf.henshin.cpa.atomic.cc;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.henshin.model.HenshinFactory;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.Rule;

public class HenshinUtil {

	public static void persist(Rule result, Collection<EPackage> metamodels) {
		Module module = HenshinFactory.eINSTANCE.createModule();
		module.getImports().addAll(metamodels);
		module.getUnits().add(result);
		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		Map<String, Object> m = reg.getExtensionToFactoryMap();
		m.put("henshin", new XMIResourceFactoryImpl());
		ResourceSetImpl resSet = new ResourceSetImpl();
		Resource resource = (Resource) resSet.createResource(URI.createURI("my.henshin"));
		resource.getContents().add(module);

		try {
			resource.save(Collections.EMPTY_MAP);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
