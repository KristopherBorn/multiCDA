package de.bigtrafo.benchmark.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;

public class CorrectnessCheckUtil {
	
	/**
	 * Checks if the input files are identical. <br>
	 * TODO: Implement a more intelligent, semantic way to compare the files (maybe using EMFCompare).
	 * @param filePath1
	 * @param filePath2
	 */
	private static boolean checkEquality(String filePath1, String filePath2) {
		try {
			String text1 = new String(Files.readAllBytes(Paths.get(filePath1)));
			String text2 = new String(Files.readAllBytes(Paths.get(filePath2)));
			return text1.equals(text2);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

	}



	public static boolean performCorrectnessCheck(String resultPath, String referencePath, String fileExtension, RuntimeBenchmarkReport report) {
		String resultFile = resultPath + "/" + findFile(resultPath, fileExtension);
		String referenceFile = referencePath + "/" + findFile(referencePath, fileExtension);
		if (!checkEquality(resultFile, referenceFile))
			report.addIncorrectnessEntry();
		
		return true;
	}

	private static String findFile(String path, String fileExtension) {
		List<String> result = new ArrayList<String>();
		try {
			Files.walk(Paths.get(path))
					// .filter(Files::isRegularFile)
					.filter(s -> s.toString().endsWith(fileExtension))
					.forEach(f -> result.add(f.getFileName().toString()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (result.size() != 1)
			throw new IllegalStateException(
					"Expected exactly one " + fileExtension + " file in " + path + ", found " + result.size() + ".");
		return result.get(0);
	}

}
