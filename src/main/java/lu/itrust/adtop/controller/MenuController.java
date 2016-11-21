package lu.itrust.adtop.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import lu.itrust.adtop.tools.Pack;
import lu.itrust.adtop.utils.Language;


/**
 * All contents of the menu Pane
 * 
 * @author ersagun
 *
 */
public class MenuController extends Controller {

	public MenuItem getImportAttackDefenceTree() {
		return importAttackDefenceTree;
	}

	public void setImportAttackDefenceTree(MenuItem importAttackDefenceTree) {
		this.importAttackDefenceTree = importAttackDefenceTree;
	}

	/**
	 * Menu item for importation of adt file
	 */
	@FXML
	private MenuItem importAttackDefenceTree;


	/**
	 * Menu item for importation of json file
	 */
	@FXML
	private MenuItem importExtractRAJSON;

	/**
	 * Menu item for importation of RA TRICK Service (API)
	 */
	@FXML
	private MenuItem importExtractRATRICK;

	/**
	 * Menu item generate all possible trees get combinaisons
	 */
	@FXML
	private MenuItem findOptimalAttackDefenceTree;

	
	public MenuItem getFindOptimalAttackDefenceTreeMultiple() {
		return findOptimalAttackDefenceTree;
	}

	public void setFindOptimalAttackDefenceTree(MenuItem findOptimalAttackDefenceTree) {
		this.findOptimalAttackDefenceTree = findOptimalAttackDefenceTree;
	}

	public MenuItem getImportExtractRATRICK() {
		return importExtractRATRICK;
	}

	public void setImportExtractRATRICK(MenuItem importExtractRATRICK) {
		this.importExtractRATRICK = importExtractRATRICK;
	}
	
	

	/**
	 * Menu item for importation of Association Matrix
	 */
	@FXML
	private MenuItem importAssociationMatrix;

	/**
	 * Menu item for importation of dl file
	 */
	@FXML
	private MenuItem importDL;

	/**
	 * Menu item for exportation of Association Matrix
	 */
	@FXML
	private MenuItem exportAssociationMatrix;

	/**
	 * Menu item for exportation of export adt
	 */
	@FXML
	private MenuItem exportADT;

	/**
	 * Menu item for exportation of risk reduction factors
	 */
	@FXML
	private MenuItem exportRRF;

	/**
	 * Menu item for generation of attack defence tree
	 */
	@FXML
	private MenuItem generateAttackDefenceTree;


	/**
	 * Menu item for evaluating an attack tree
	 */
	@FXML
	private MenuItem helpGetStarted;
	
	/**
	 * Menu item for evaluating an attack tree
	 */
	@FXML
	private MenuItem exit;
	
	/**
	 * Menu item for evaluating an attack tree
	 */
	@FXML
	private MenuItem helpAboutADTop;

	@FXML
	private Menu helpMenu;

	@FXML
	private Menu evaluateMenu;

	@FXML
	private Menu generateMenu;

	@FXML
	private Menu importMenu;

	@FXML
	private Menu exportMenu;

	@FXML
	private Menu fileMenu;
	
	@FXML
	private Menu languageMenu;
	
	@FXML
	private MenuItem languageFR;
	
	@FXML
	private MenuItem languageEN;
	
	
	/** Import attack-defence tree to here**/
	public void initializeMenuLanguages(String languages) {
				importExtractRAJSON.setText(Language.getWord(languages, "importExtractRAJSON"));
				importExtractRATRICK.setText(Language.getWord(languages, "importExtractRATRICK"));
				importAssociationMatrix.setText(Language.getWord(languages, "exportAssociationMatrix"));
				importAttackDefenceTree.setText(Language.getWord(languages, "importAttackTree"));
				importDL.setText(Language.getWord(languages, "importDL"));
				exportAssociationMatrix.setText(Language.getWord(languages, "exportAssociationMatrix"));
				exportADT.setText(Language.getWord(languages, "exportADT"));
				exportRRF.setText(Language.getWord(languages, "exportRRF"));
				generateAttackDefenceTree.setText(Language.getWord(languages, "generateAttackDefenceTree"));
				helpGetStarted.setText(Language.getWord(languages, "helpGetStarted"));
				helpAboutADTop.setText(Language.getWord(languages, "helpAboutADTop"));
				exit.setText(Language.getWord(languages, "exit"));
				helpMenu.setText(Language.getWord(languages, "helpMenu"));
				evaluateMenu.setText(Language.getWord(languages, "evaluateMenu"));
				generateMenu.setText(Language.getWord(languages, "generateMenu"));
				importMenu.setText(Language.getWord(languages, "importMenu"));
				exportMenu.setText(Language.getWord(languages, "exportMenu"));
				fileMenu.setText(Language.getWord(languages, "fileMenu"));
				languageMenu.setText(Language.getWord(languages, "languageMenu"));
				findOptimalAttackDefenceTree.setText(Language.getWord(languages, "findOptimalAttackDefenceTree"));			
	}


	/**
	 * Disable this screen
	 */
	@Override
	public void disableScreen() {
		// Menu is never disabled
	}
	

	/**
	 * After each interaction 
	 * evaluate the situation and disable or show the buttons 
	 * of menu controller
	 * @param p
	 */
	public void update(Pack p) {
		this.importExtractRATRICK.setDisable(false);
		this.importAssociationMatrix.setDisable(false);
		
		if(!p.getAssociationMatrix().isEmpty() && !p.getAttackDefenceTree().isEmpty() && ! p.getMeasureContainer().isEmpty()){
			this.generateAttackDefenceTree.setDisable(false);
			this.findOptimalAttackDefenceTree.setDisable(false);
		}
		
		if (!p.getMeasureContainer().isEmpty())
			this.exportRRF.setDisable(false);
		
		if (!p.getAttackDefenceTree().isEmpty() && !p.getMeasureContainer().isEmpty()) {
			this.exportAssociationMatrix.setDisable(false);
			this.importAssociationMatrix.setDisable(false);
		}

		if (p.getAssociationMatrix().getExported()){
			this.importAssociationMatrix.setDisable(false);
			this.generateAttackDefenceTree.setDisable(false);
		}

		if (!p.getAttackDefenceTree().isEmpty()) {
			this.exportADT.setDisable(false);
		}
	}

	
	/* GETTERS AND SETTERS */

	public MenuItem getImportJSON() {
		return importExtractRAJSON;
	}

	public void setImportJSON(MenuItem importJSON) {
		this.importExtractRAJSON = importJSON;
	}

	public MenuItem getImportDL() {
		return importDL;
	}

	public void setImportDL(MenuItem importDL) {
		this.importDL = importDL;
	}

	public MenuItem getImportExtractRAJSON() {
		return importExtractRAJSON;
	}

	public void setImportExtractRAJSON(MenuItem importExtractRAJSON) {
		this.importExtractRAJSON = importExtractRAJSON;
	}

	public MenuItem getImportAssociationMatrix() {
		return importAssociationMatrix;
	}

	public void setImportAssociationMatrix(MenuItem importAssociationMatrix) {
		this.importAssociationMatrix = importAssociationMatrix;
	}

	public MenuItem getExportAssociationMatrix() {
		return exportAssociationMatrix;
	}

	public void setExportAssociationMatrix(MenuItem exportAssociationMatrix) {
		this.exportAssociationMatrix = exportAssociationMatrix;
	}

	public MenuItem getExportADT() {
		return exportADT;
	}

	public void setExportADT(MenuItem exportADT) {
		this.exportADT = exportADT;
	}

	public MenuItem getExportRRF() {
		return exportRRF;
	}

	public void setExportRRF(MenuItem exportRRF) {
		this.exportRRF = exportRRF;
	}

	public MenuItem getGenerateAttackDefenceTree() {
		return generateAttackDefenceTree;
	}

	public void setGenerateAttackDefenceTree(MenuItem generateAttackDefenceTree) {
		this.generateAttackDefenceTree = generateAttackDefenceTree;
	}

	public MenuItem getHelpGetStarted() {
		return helpGetStarted;
	}

	public void setHelpGetStarted(MenuItem helpGetStarted) {
		this.helpGetStarted = helpGetStarted;
	}

	public MenuItem getHelpAboutADTop() {
		return helpAboutADTop;
	}

	public void setHelpAboutADTop(MenuItem helpAboutADTop) {
		this.helpAboutADTop = helpAboutADTop;
	}

	public MenuItem getExit() {
		return exit;
	}

	public void setExit(MenuItem exit) {
		this.exit = exit;
	}
	

	public Menu getHelpMenu() {
		return helpMenu;
	}

	public void setHelpMenu(Menu helpMenu) {
		this.helpMenu = helpMenu;
	}

	public Menu getEvaluateMenu() {
		return evaluateMenu;
	}

	public void setEvaluateMenu(Menu evaluateMenu) {
		this.evaluateMenu = evaluateMenu;
	}

	public Menu getGenerateMenu() {
		return generateMenu;
	}

	public void setGenerateMenu(Menu generateMenu) {
		this.generateMenu = generateMenu;
	}

	public Menu getImportMenu() {
		return importMenu;
	}

	public void setImportMenu(Menu importMenu) {
		this.importMenu = importMenu;
	}

	public Menu getExportMenu() {
		return exportMenu;
	}

	public void setExportMenu(Menu exportMenu) {
		this.exportMenu = exportMenu;
	}

	public Menu getFileMenu() {
		return fileMenu;
	}

	public void setFileMenu(Menu fileMenu) {
		this.fileMenu = fileMenu;
	}

	public Menu getLanguageMenu() {
		return languageMenu;
	}

	public void setLanguageMenu(Menu languageMenu) {
		this.languageMenu = languageMenu;
	}

	public MenuItem getLanguageFR() {
		return languageFR;
	}

	public void setLanguageFR(MenuItem languageFR) {
		this.languageFR = languageFR;
	}

	public MenuItem getLanguageEN() {
		return languageEN;
	}

	public void setLanguageEN(MenuItem languageEN) {
		this.languageEN = languageEN;
	}

}
