package org.eclipse.emf.henshin.cpa.atomic.runner.overapproximation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellReference;
import org.eclipse.emf.henshin.cpa.atomic.compareLogger.Logger2;
import org.eclipse.emf.henshin.model.Rule;

public class XlsOverapproximationWriter {


//	public XlsOverapproximationWriter() {
//	}

	private String essDelUseConflSheetName = "essDelUseConfl";
	private String MODIFIEDessDelUseConflSheetName = "MODIFIEDessDelUseConfl";
	private String overapproximationSheetName = "overapproximation";
	private String overapproximationMedianSheetName = "overapproxQuantMedian";
	private String lastRowOfFormula;
	private String lastColumnLetter;

	//TODO: ggf. R�ckgabewert anpassen. z.B. erzeugtes File Objekt.
	public void export(Logger2 normalLogger, Logger2 modifiedPreserveLogger, File resultFile) {
		

		// Project project = Workspace.

		Workbook wb = new HSSFWorkbook();
		
		Sheet normalSheet = writeDataInSheet(wb, normalLogger, essDelUseConflSheetName);
		Sheet modifiedSheet = writeDataInSheet(wb, modifiedPreserveLogger, MODIFIEDessDelUseConflSheetName);
		
		//TODO!
		Sheet overapproximationSheet = writeOverapproximationInSheet(wb, normalLogger.getFirstRules(), normalLogger.getSecondRules(), overapproximationSheetName);
		
		Sheet overapproximationQuantMedianSheet = writeOverapproximationQuantMedianSheet(wb, normalLogger.getFirstRules(), normalLogger.getSecondRules(), overapproximationMedianSheetName);

		addAmountQualErrorOnOverapprSheet(normalLogger.getFirstRules().size(), overapproximationSheet);
		addMedianQualErrorOnOverapprSheet(normalLogger.getFirstRules().size(), overapproximationSheet);
		
		FileOutputStream out;
		try {
			out = new FileOutputStream(resultFile);
			wb.write(out);
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			wb.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Sheet writeDataInSheet(Workbook wb, Logger2 logger, String sheetName) {
		Sheet createdSheet = wb.createSheet(sheetName);
		
//		DONE: Daten abrufen vom Logger
		List<Rule> secondRules = logger.getSecondRules();
		List<Rule> firstRules = logger.getFirstRules();
		
		String[][] analysisResults = logger.getAnalysisResults();
		
		//first column - rule Names
		Row secondRuleNameRow = createdSheet.createRow(0);
		Cell firstCell = secondRuleNameRow.createCell(0);
		firstCell.setCellValue("\\/firstRule / secondRule->");
		for(int i = 0; i < secondRules.size(); i++){
			Cell cell = secondRuleNameRow.createCell(i+1);
			cell.setCellValue(firstRules.get(i).getName());
		}
		
		for (int i = 0; i < (firstRules.size()); i++) {
			Row row = createdSheet.createRow(i+1);
			String[] arrayRow = analysisResults[i];
			Cell firstCellInRow = row.createCell(0);
			firstCellInRow.setCellValue(firstRules.get(i).getName());
			for (int j = 0; j < secondRules.size(); j++) {
				Cell cell = row.createCell(j+1);
				String value = arrayRow[j];
				cell.setCellValue(Integer.valueOf(value));
			}
		}
		
		// DONE: add Metrics here!:
		// SUM, TIMEOUTS, MAXIMUM, AVERAGE, MEDIAN, OVERALL RUNTIME
		
		lastRowOfFormula = String.valueOf(firstRules.size()+1);
		int lastColumnNumber = secondRules.size();
		lastColumnLetter = CellReference.convertNumToColString(lastColumnNumber);
		
		Row sumRow = createdSheet.createRow(firstRules.size()+1);
		sumRow.createCell(0).setCellValue("SUM:");
		String sumFormula = "SUM(B2:"+lastColumnLetter+lastRowOfFormula+")";
		Cell formulaCell = sumRow.createCell(1);
		formulaCell.setCellFormula(sumFormula);
		
		Row timeoutRow = createdSheet.createRow(firstRules.size()+2);
		timeoutRow.createCell(0).setCellValue("TIMEOUTS:");
		Cell timeoutCell = timeoutRow.createCell(1);
		String timeoutFormula = "COUNTIF(B2:"+lastColumnLetter+lastRowOfFormula+",C"+String.valueOf(firstRules.size()+3)+")";
		timeoutCell.setCellFormula(timeoutFormula);
		
		Cell toCell = timeoutRow.createCell(2);
		toCell.setCellValue("TO");

		Row maxRow = createdSheet.createRow(firstRules.size()+3);
		maxRow.createCell(0).setCellValue("MAXIMUM:");
		String maxFormula = "MAX(B2:"+lastColumnLetter+lastRowOfFormula+")";
		maxRow.createCell(1).setCellFormula(maxFormula);

		Row avgRow = createdSheet.createRow(firstRules.size()+4);
		avgRow.createCell(0).setCellValue("AVERAGE:");
		String avgFormula = "AVERAGE(B2:"+lastColumnLetter+lastRowOfFormula+")";
		avgRow.createCell(1).setCellFormula(avgFormula);
		
		Row totalRuntimeRow = createdSheet.createRow(firstRules.size()+5);
		totalRuntimeRow.createCell(0).setCellValue("TOTAL_RUNTIME:");
		totalRuntimeRow.createCell(1).setCellValue(String.valueOf(logger.getTotalRuntimeAmount())+" ms");
		if((logger.getTotalRuntimeAmount()/1000) > 1)
			totalRuntimeRow.createCell(1).setCellValue(String.valueOf(logger.getTotalRuntimeAmount()/1000)+" s");
		if((logger.getTotalRuntimeAmount()/(1000*60)) > 1)
			totalRuntimeRow.createCell(1).setCellValue(String.valueOf(logger.getTotalRuntimeAmount()/(1000*60))+" min");
		
		
		Row amountOfDelUseConflRow = createdSheet.createRow(firstRules.size()+6);
		amountOfDelUseConflRow.createCell(0).setCellValue("amount of RP with conflicts:");
		Cell amountOfDelUseConflCell = amountOfDelUseConflRow.createCell(1);
		amountOfDelUseConflCell.setCellFormula("COUNTIF(B2:"+lastColumnLetter+lastRowOfFormula+",\">0\")");
		
		
		
		return createdSheet;
	}

	//TODO: in wie weit macht es wirklich Sinn hier zwischen firstRules und secondRules zu unterscheiden???  
	private Sheet writeOverapproximationInSheet(Workbook wb, List<Rule> firstRules, List<Rule> secondRules,
			String sheetName) {
		Sheet createdSheet = wb.createSheet(sheetName);
		
		//first column - rule Names
		Row secondRuleNameRow = createdSheet.createRow(0);
		Cell firstCell = secondRuleNameRow.createCell(0);
		firstCell.setCellValue("\\/firstRule / secondRule->");
		for(int i = 0; i < secondRules.size(); i++){
			Cell cell = secondRuleNameRow.createCell(i+1);
			cell.setCellValue(secondRules.get(i).getName());
		}
		
		for (int i = 0; i < (firstRules.size()); i++) {
			Row row = createdSheet.createRow(i+1);
			Cell firstCellInRow = row.createCell(0);
			firstCellInRow.setCellValue(firstRules.get(i).getName());
			for (int j = 0; j < secondRules.size(); j++) {
				Cell cell = row.createCell(j+1);
				
				CellAddress address = cell.getAddress();
				String addressAsString = address.formatAsString();
				String fieldInModifiedSheet = MODIFIEDessDelUseConflSheetName+"!"+addressAsString;
				String termForQualError = MODIFIEDessDelUseConflSheetName+"!"+addressAsString+" / "+essDelUseConflSheetName+"!"+addressAsString;
				String formula = "IF(EXACT("+fieldInModifiedSheet+", 0), \"equal\", IF(ISERROR("+termForQualError+"), \"QualErr\", ("+termForQualError+")))";
				cell.setCellFormula(formula);
			}
		}
		
		// TODO: extract \"QualErr\" as a static String!
		
		// DONE: add Metrics here!:
		Row overapproximationResultRow = createdSheet.createRow(firstRules.size()+1);
		Cell overapproximationDescriptionCell = overapproximationResultRow.createCell(0);
		overapproximationDescriptionCell.setCellValue("overAppr [%]");
		Cell overapproximationResultCell = overapproximationResultRow.createCell(1);
		String currentRowOfFormula = String.valueOf(firstRules.size()+2);
		// Berechnung der �berapproximation
		String overapproximationResultFormula = "(("+MODIFIEDessDelUseConflSheetName+"!B"+currentRowOfFormula+" / "+essDelUseConflSheetName+"!B"+currentRowOfFormula+")-1)*100";
		overapproximationResultCell.setCellFormula(overapproximationResultFormula);
		
		Row intededAmountRow = createdSheet.createRow(firstRules.size()+2);
		Cell intendedAmountDescriptionCell = intededAmountRow.createCell(0);
		intendedAmountDescriptionCell.setCellValue("intended amount of RP with del-use-confl.:");
		Cell intededAmountResultCell = intededAmountRow.createCell(1);
		// Berechnung der �berapproximation
		String intededAmountResultFormula = essDelUseConflSheetName+"!B"+String.valueOf(firstRules.size()+7);
		intededAmountResultCell.setCellFormula(intededAmountResultFormula);		
		
		Row qualErrorRow = createdSheet.createRow(firstRules.size()+3);
		Cell qualErrorDescriptionCell = qualErrorRow.createCell(0);
		qualErrorDescriptionCell.setCellValue("amount of qual. Errors(unintended RPs):");
		Cell qualErrorResultCell = qualErrorRow.createCell(1);
		// Berechnung der �berapproximation
		String qualErrorResultFormula = "COUNTIF(B2 : "+lastColumnLetter+lastRowOfFormula+", \"QualErr\")";
		qualErrorResultCell.setCellFormula(qualErrorResultFormula);
		
		return createdSheet;
	}

	private void addAmountQualErrorOnOverapprSheet(int amountOfFirstRules, Sheet createdSheet) {
		Row quantErrorRow = createdSheet.createRow(amountOfFirstRules+4);
		Cell quantErrorDescriptionCell = quantErrorRow.createCell(0);
		quantErrorDescriptionCell.setCellValue("amount of RP with quant. errors:");
		Cell quantErrorResultCell = quantErrorRow.createCell(1);
		// Berechnung der �berapproximation
		String quantErrorResultFormula = overapproximationMedianSheetName+"!B"+String.valueOf(amountOfFirstRules+4);
		quantErrorResultCell.setCellFormula(quantErrorResultFormula);
	}

	private void addMedianQualErrorOnOverapprSheet(int amountOfFirstRules, Sheet createdSheet) {
		Row medianQualErrorRow = createdSheet.createRow(amountOfFirstRules+5);
		Cell medianQualErrorDescriptionCell = medianQualErrorRow.createCell(0);
		medianQualErrorDescriptionCell.setCellValue("median of qual. errors:");
		Cell medianQualErrorResultCell = medianQualErrorRow.createCell(1);
		String medianQualErrorResultFormula = overapproximationMedianSheetName+"!"+medianQualErrorResultCell.getAddress();
		medianQualErrorResultCell.setCellFormula(medianQualErrorResultFormula);
	}
	
	//TODO: in wie weit macht es wirklich Sinn hier zwischen firstRules und secondRules zu unterscheiden???  
		private Sheet writeOverapproximationQuantMedianSheet(Workbook wb, List<Rule> firstRules, List<Rule> secondRules,
				String sheetName) {
			Sheet createdSheet = wb.createSheet(sheetName);
			
			//first column - rule Names
			Row secondRuleNameRow = createdSheet.createRow(0);
			Cell firstCell = secondRuleNameRow.createCell(0);
			firstCell.setCellValue("\\/firstRule / secondRule->");
			for(int i = 0; i < secondRules.size(); i++){
				Cell cell = secondRuleNameRow.createCell(i+1);
				cell.setCellValue(secondRules.get(i).getName());
			}
			
			for (int i = 0; i < (firstRules.size()); i++) {
				Row row = createdSheet.createRow(i+1);
				Cell firstCellInRow = row.createCell(0);
				firstCellInRow.setCellValue(firstRules.get(i).getName());
				for (int j = 0; j < secondRules.size(); j++) {
					Cell cell = row.createCell(j+1);
					
					CellAddress address = cell.getAddress();
					String addressAsString = address.formatAsString();
					String fieldInModifiedSheet = overapproximationSheetName+"!"+addressAsString;
					String formula = "IF(EXACT("+fieldInModifiedSheet+", 1), \"-\", IF(ISNUMBER("+fieldInModifiedSheet+"), "+fieldInModifiedSheet +",\"-\"))";
					cell.setCellFormula(formula);
				}
			}
			
			Row amountQuantErrorRow = createdSheet.createRow(firstRules.size()+3);
			Cell amountQuantErrorDescriptionCell = amountQuantErrorRow.createCell(0);
			amountQuantErrorDescriptionCell.setCellValue("amount of RP with quant. errors:");
			Cell amountQuantErrorResultCell = amountQuantErrorRow.createCell(1);
			String amountQuantErrorResultFormula = "COUNTA(B2:"+lastColumnLetter+lastRowOfFormula+") - COUNTIF(B2:"+lastColumnLetter+lastRowOfFormula+",\"-\")";
			amountQuantErrorResultCell.setCellFormula(amountQuantErrorResultFormula);
			
			Row medianQualErrorRow = createdSheet.createRow(firstRules.size()+5);
			Cell medianQualErrorDescriptionCell = medianQualErrorRow.createCell(0);
			medianQualErrorDescriptionCell.setCellValue("median of qual. errors:");
			Cell medianQualErrorResultCell = medianQualErrorRow.createCell(1);
			String medianQualErrorResultFormula = "MEDIAN(B2:"+lastColumnLetter+lastRowOfFormula+")";
			medianQualErrorResultCell.setCellFormula(medianQualErrorResultFormula);
			
			return createdSheet;
		}

}
