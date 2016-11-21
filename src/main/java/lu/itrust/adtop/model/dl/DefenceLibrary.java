package lu.itrust.adtop.model.dl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/**
 * Class represent a defence library
 * 
 * @author ersagun
 *
 */
public class DefenceLibrary {

	/**
	 * List of defence
	 */
	List<Defence> defenceList;

	public DefenceLibrary() {
		this.defenceList = new ArrayList<Defence>();
	}

	/**
	 * TODO : check corrupted File
	 * 
	 * @param adr address of excel file
	 * @throws FileNotFoundException if file is not found
	 * @throws IOException if input output problem
	 */
	
	public void importExcel(String adr) throws FileNotFoundException, IOException {
		HSSFWorkbook WBI = new HSSFWorkbook(new POIFSFileSystem(new FileInputStream(adr)));
		HSSFSheet sheetInput = WBI.getSheetAt(0);
		HSSFRow rowInput = sheetInput.getRow(0);
		int nbLineInputRows = sheetInput.getPhysicalNumberOfRows();
		for (int i = 0; i < nbLineInputRows - 1; i++) {
			rowInput = sheetInput.getRow(i);
			ArrayList<Attack> attacks = new ArrayList<Attack>();
			String nameOfMeasure = rowInput.getCell(1).getStringCellValue();
			Defence defence = new Defence((long) rowInput.getCell(0).getNumericCellValue(), nameOfMeasure,
					rowInput.getCell(2).getNumericCellValue());

			int j = 1;
			while (rowInput.getCell(j) != null) {
				attacks.add(new Attack(rowInput.getCell(j).getStringCellValue(),
						rowInput.getCell(j + 1).getNumericCellValue(), nameOfMeasure));
				j = j + 2;
			}

			defence.setAttackList(attacks);
			this.defenceList.add(defence);
			attacks.clear();
		}
		WBI.close();
	}

	
	/**
	 * Check if defence library is empty
	 * 
	 * @return boolean true if is empty
	 */
	public boolean isEmpty() {
		if (this.defenceList.isEmpty())
			return true;
		else
			return false;
	}

	/* GETTERS AND SETTERS */
	public List<Defence> getDefenceList() {
		return defenceList;
	}

	public void setDefenceList(List<Defence> defenceList) {
		this.defenceList = defenceList;
	}
}
