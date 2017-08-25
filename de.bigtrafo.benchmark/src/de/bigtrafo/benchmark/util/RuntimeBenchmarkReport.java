package de.bigtrafo.benchmark.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.Unit;

public class RuntimeBenchmarkReport {
	private int count = 0;
	String name = "";
	String logfilePath = "";
	PrintWriter out;
	String date;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public RuntimeBenchmarkReport(String name, String logDirectory) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
		this.date = sdf.format(new Date());
		logfilePath = logDirectory + "/" + date + ".log";
	}

	private boolean PRINT_TO_CONSOLE = true;

	public void start() {
		createReport();
		String start = " Starting runtime benchmark: " + name + " ";
		StringBuilder info = new StringBuilder();
		for (int i = 0; i < start.length(); i++)
			info.append('=');
		info.append('\n');
		info.append(start);
		info.append('\n');
		for (int i = 0; i < start.length(); i++)
			info.append('=');
		info.append('\n');

		addToReport(info);
	}

	private void createReport() {
		try {
			Files.write(Paths.get(logfilePath), new String().getBytes(), StandardOpenOption.CREATE_NEW);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void beginNewEntry(String id) {
		count++;
		StringBuilder info = new StringBuilder();
		info.append('\n');
		info.append("Benchmark run " + count + ": input model " + id + " \n");
		addToReport(info);
	}

	public void addSubEntry(Unit unit, int before, int after, long runtime) {
		StringBuilder info = new StringBuilder();
		String unitKind = (unit instanceof Rule) ? "rule" : "unit";
		info.append("   Applied " + unitKind + " " + unit.getName() + " in " + sec(runtime) + " sec, ");
		info.append("change: " + before + " -> " + after + " nodes (delta = " + (after - before) + ")\n");
		addToReport(info);
	}

	public void finishEntry(int before, int after, long runtime) {
		StringBuilder info = new StringBuilder();
		info.append("Execution time: " + sec(runtime) + " sec\n");
		info.append("Performed change: " + before + " -> " + after + " nodes (delta = " + (after - before) + ")\n");
		addToReport(info);
	}

	private void addToReport(StringBuilder info) {
		if (PRINT_TO_CONSOLE)
			System.out.print(info);

		try {
			Files.write(Paths.get(logfilePath), info.toString().getBytes(), StandardOpenOption.APPEND);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static double sec(long msecTime) {
		return (msecTime * 1.0) / 1000;
	}

	public void addIncorrectnessEntry() {
		StringBuilder info = new StringBuilder();
		info.append("! Found potential error: The output model is not equal to the reference output model.");
	}

}
