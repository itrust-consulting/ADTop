package lu.itrust.adtop.model.matrix;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import lu.itrust.adtop.exception.ADException;
import lu.itrust.adtop.model.measure.Measure;
import lu.itrust.adtop.model.measure.Standard;
import lu.itrust.adtop.tools.Pack;

/**
 * Class describe an XLS Association-Matrix file
 * 
 * @author ersagun
 *
 */
public class AssociationMatrix {

	/**
	 * A matrix, contains for each attack the measures associated
	 */
	Map<String, Map<String, Double>> matrix;

	private Double[][] simpleMatrix;

	private String[] attacks;
	private String[] measures;

	/**
	 * A matrix, contains for each attack the measures associated
	 */
	Map<String, Map<Measure, List<String>>> matrixMeasureAttack;

	/**
	 * Boolean contains true if matrix is exported
	 */
	Boolean exported;

	/**
	 * Boolean contains true if matrix is imported
	 */
	Boolean imported;

	public Map<String, Map<Measure, List<String>>> getMatrixMeasureAttack() {
		return matrixMeasureAttack;
	}

	public void setMatrixMeasureAttack(Map<String, Map<Measure, List<String>>> matrixMeasureAttack) {
		this.matrixMeasureAttack = matrixMeasureAttack;
	}

	public AssociationMatrix() {
		this.matrix = new HashMap<String, Map<String, Double>>();
		this.matrixMeasureAttack = new HashMap<>();
		this.exported = false;
		this.imported = false;
	}

	/**
	 * Import a matrix from Association matrix xls file TODO : ask if same
	 * matrix
	 * 
	 * @param adr
	 *            adresse of matrix
	 * @throws AssociationMatrixCorruptedException
	 * @throws IOException
	 * @throws FileNotFoundException
	 */

	public void importMatrixFromXLS(String adr, Pack p) throws FileNotFoundException, IOException {

		@SuppressWarnings("resource")
		HSSFWorkbook WBI = new HSSFWorkbook(new POIFSFileSystem(new FileInputStream(adr)));
		HSSFSheet sheetInput = WBI.getSheetAt(0);
		HSSFRow rowInput = sheetInput.getRow(0);

		if (rowInput == null || sheetInput == null)
			throw new ADException("ERROR.association_matrix.empty", "The association matrix is empty or contains an incorrect value. Please ensure that the spreadsheet exists with correct values.");

		int nbLineInputRows = sheetInput.getPhysicalNumberOfRows();
		int nbLineInputCol = rowInput.getPhysicalNumberOfCells();
		this.attacks = new String[nbLineInputCol];
		this.measures = new String[nbLineInputRows - 1];
		this.simpleMatrix = new Double[nbLineInputRows - 1][nbLineInputCol];

		int i = 1;
		while (nbLineInputCol >= i) {
			// For each measures attacks change to for attack measure
			HashMap<String, Double> temp = new HashMap<String, Double>();
			String nameOfAttack = sheetInput.getRow(0).getCell(i).getStringCellValue();
			this.attacks[i - 1] = nameOfAttack;
			int j = 1;
			while (nbLineInputRows - 1 >= j) {

				// measure founded for attack
				if (sheetInput.getRow(j) != null) {
					if (sheetInput.getRow(j).getCell(i) != null) {
						HSSFRow row = sheetInput.getRow(j);
						HSSFCell cell = sheetInput.getRow(j).getCell(i);

						Double value = null;
						if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC)
							value = cell.getNumericCellValue();

						if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING)
							try {
								value = Double.parseDouble(cell.getStringCellValue());
							} catch (NumberFormatException nfe) {
								throw new ADException("ERROR.association_matrix.bad.value",
										String.format("Association matrix contains a no float value %s, in Row: %d, Column: %d", nfe.getMessage(), j, i), nfe.getMessage(), j, i);
							}
						this.simpleMatrix[j - 1][i - 1] = value;
						String nameOfMeasure = row.getCell(0).getStringCellValue();
						this.measures[j - 1] = nameOfMeasure;

						if (!(value >= 0 && value <= 1))
							throw new ADException("ERROR.association_matrix.bad.effectiveness",
									String.format("Association matrix contains an effectiveness greater than 1 or smaller then 0 in Row: %d, Column: %d", j, i), j, i);
						if (value > 0)
							temp.put(nameOfMeasure, value);

					}
				}
				j++;
			}
			this.matrix.put(nameOfAttack, temp);
			i++;
		}
		this.imported = true;

		// printSimpleMatrix();
		createDefenceForAttacks(p);
		WBI.close();
	}

	public void createDefenceForAttacks(Pack p) {
		Map<String, Map<Measure, List<String>>> defenceAttack = new HashMap<>();
		Standard standard = p.getMeasureContainer().getStandards().get(0);
		for (int i = 0; i < this.simpleMatrix.length; i++) {
			defenceAttack.put(this.measures[i], new HashMap<>());
			defenceAttack.get(this.measures[i]).put(standard.getMeasureWithName(this.measures[i]), new ArrayList<String>());
			for (int j = 0; j < this.simpleMatrix[0].length; j++) {
				if (this.simpleMatrix[i][j] != null) {
					defenceAttack.get(this.measures[i]).get(standard.getMeasureWithName(this.measures[i])).add(this.attacks[j]);
				}
			}
		}
		this.matrixMeasureAttack = defenceAttack;
	}

	public void clear() {
		this.matrix = new HashMap<String, Map<String, Double>>();
		this.exported = false;
		this.imported = false;
	}

	public double giveEffectivenessByMeasureAttack(String attackName, String measureName) {
		int i = -1;
		int j = -1;
		for (int i1 = 0; i1 < this.attacks.length; i1++) {
			if (attackName.equals(this.attacks[i1]))
				i = i1;
		}
		for (int j1 = 0; j1 < this.measures.length; j1++) {
			if (measureName.equals(this.measures[j1]))
				j = j1;
		}

		if (j != -1 && i != -1)
			return this.simpleMatrix[j][i];
		else
			return -1;
	}

	public void printSimpleMatrix() {
		for (int i = 0; i < this.attacks.length; i++) {
			System.out.println(attacks[i] + i);
		}
		for (int j = 0; j < this.measures.length; j++) {
			System.out.println(measures[j] + j);
		}
		for (int i = 0; i < this.simpleMatrix.length; i++) {
			for (int j = 0; j < this.simpleMatrix[i].length; j++) {
				System.out.print(" " + this.simpleMatrix[i][j] + " " + i + ";" + j);
			}
			System.out.println("\n");
		}
	}

	public List<String> getAllAttacks() {
		List<String> attacksOnTree = new ArrayList<String>();
		for (Entry<String, Map<String, Double>> entry : this.matrix.entrySet()) {
			attacksOnTree.add(entry.getKey());
		}
		return attacksOnTree;
	}

	public List<String> getAllMeasures() {
		Set<String> measuresOnTree = new HashSet<String>();
		for (Entry<String, Map<String, Double>> entry : this.matrix.entrySet()) {
			for (Entry<String, Double> entry2 : entry.getValue().entrySet()) {
				measuresOnTree.add(entry2.getKey());
			}
		}
		return new ArrayList<String>(measuresOnTree);
	}

	/**
	 * Check if association matrix is empty
	 * 
	 * @return boolean true is empty
	 */
	public boolean isEmpty() {
		if (this.matrix.isEmpty())
			return true;
		else
			return false;
	}

	/**
	 * Print a matrix
	 */
	public String toString() {
		String result = "";
		for (Entry<String, Map<String, Double>> entry : this.matrix.entrySet()) {
			String nameOfAttack = entry.getKey();
			Map<String, Double> measure = entry.getValue();

			result = result + "{ Name of attack: " + nameOfAttack + ", ";
			for (Entry<String, Double> entryM : measure.entrySet()) {
				String nameOfMeasure = entryM.getKey();
				Double effectiveness = entryM.getValue();
				result = result + "Name of measure: " + nameOfMeasure + ", effectiveness of measure: " + effectiveness + ", ";
			}
			result = result + "} \n";
		}
		return result;
	}

	/**
	 * Create initial empty matrix with columns and rows
	 * 
	 * @param adr
	 *            address of matrix
	 * @throws FileNotFoundException
	 *             if file is not found
	 * @throws IOException
	 *             if input output problems
	 */
	public void createInitalMatrixInExcel(File f, Pack p) throws FileNotFoundException, IOException {

		List<Measure> measures = p.getMeasureContainer().getStandards().get(0).getMeasures();
		FileOutputStream fileOuto = new FileOutputStream(f);
		HSSFWorkbook WBO = new HSSFWorkbook();
		HSSFSheet sheetOutput = WBO.createSheet("Association_Matrix");
		HSSFRow row;
		int i = 0;

		for (Measure measure : measures) {
			row = sheetOutput.createRow(i + 1);
			row.createCell(0).setCellValue((measure.getName()));
		}

		i = 0;
		row = sheetOutput.createRow(0);
		for (Object attack : p.getAttackDefenceTree().getAttacks()) {
			row.createCell(i + 1).setCellValue(((String) attack));
			i++;
		}

		WBO.write(fileOuto);
		fileOuto.close();
		WBO.close();
		this.exported = true;
	}

	/* GETTERS AND SETTERS */

	public Map<String, Double> get(String key) {
		return this.matrix.get(key);
	}

	public Map<String, Map<String, Double>> getMatrix() {
		return matrix;
	}

	public void setMatrix(Map<String, Map<String, Double>> matrix) {
		this.matrix = matrix;
	}

	public Boolean getExported() {
		return exported;
	}

	public void setExported(Boolean exported) {
		this.exported = exported;
	}

	public Boolean getImported() {
		return imported;
	}

	public void setImported(Boolean imported) {
		this.imported = imported;
	}
}