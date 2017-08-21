package org.eclipse.emf.henshin.cpa.atomic.runner;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.internal.resources.Project;
import org.eclipse.core.internal.resources.WorkspaceRoot;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import de.imotep.featuremodel.variability.metamodel.FeatureModel.FeatureModelPackage;

public class ConflictsOfFeatureModelRunner2 extends Runner{
	
	public static void main(String args[]){

		FeatureModelPackage.eINSTANCE.eClass();
		
		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		Map<String, Object> m = reg.getExtensionToFactoryMap();
		m.put("xmi", new XMIResourceFactoryImpl());

		ResourceSetImpl resourceSet = new ResourceSetImpl();

		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore",
				new EcoreResourceFactoryImpl());
		
		List<String> deactivatedRules = new LinkedList<String>();
		
		final File binaryFolder = new File(Runner.class.getProtectionDomain().getCodeSource().getLocation().getPath());
//		File projectFolder = binaryFolder.getParentFile();
//		projectFolder.
//		IWorkspaceRoot workspaceRoot = WorkspaceRoot.get
//		WorkspaceRoot workspaceRoot = new WorkspaceRoot();
//		IWorkspace workspace= ResourcesPlugin.getWorkspace();    
//		IPath binaryFolderPath = Path.fromOSString(binaryFolder.getAbsolutePath());
//		IPath projectIPath = binaryFolderPath.removeLastSegments(1);
//		IPath testDataIPath = projectIPath.append("testData");
//		IFile binaryFolderIfile= workspace.getRoot().getFileForLocation(location);
//		IFolder projectFolder = binaryFolderIfile.getParent();
//		binaryFolderIfile.getProjectRelativePath().a
		
		String filePath = binaryFolder.toString();
		String projectPath = filePath.replaceAll("bin", "");
		
		
		System.out.println(projectPath);
		String subDirectoryPath = "testData\\featureModelingWithoutUpperLimitsOnReferences\\fmedit_noAmalgamation_noNACs_noAttrChange_additionalPreserveProgrammatic\\normal_rules\\";
		String fullSubDirectoryPath = projectPath + subDirectoryPath;
		
		Runner runner = new Runner();
		runner.setNoApplicationConditions(true);
		runner.setNoMultirules(true);
		runner.setAnalysisKinds(false, false, true, false, true, false);
		runner.run(fullSubDirectoryPath, deactivatedRules);
	}
}
