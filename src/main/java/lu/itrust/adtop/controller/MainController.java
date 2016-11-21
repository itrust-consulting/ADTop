package lu.itrust.adtop.controller;

import javafx.fxml.FXML;
import lu.itrust.adtop.controller.screen.IntroductionController;
import lu.itrust.adtop.controller.screen.TreeController;

/**
 * Control of main screens like import control, result controller, start analysis control
 * tree control
 * 
 * @author ersagun
 *
 */
public class MainController extends Controller {


	/**
	 * Screen shows importation of adt, json from ts or dl
	 */
	@FXML
	private IntroductionController introductionController;
	

	/**
	 * Screen of tree 
	 */
	@FXML
	private TreeController treeController;
	
	
	/**
	 * Disable this screen
	 */
	@Override
	public void disableScreen() {
		introductionController.disableScreen();
		treeController.disableScreen();
	}
	
		
	/* GETTERS AND SETTERS */
	

	public TreeController getTreeController() {
		return treeController;
	}

	public void setTreeController(TreeController treeController) {
		this.treeController = treeController;
	}
	
	public IntroductionController getIntroductionController() {
		return introductionController;
	}


	public void setIntroductionController(IntroductionController introductionController) {
		this.introductionController = introductionController;
	}


}
