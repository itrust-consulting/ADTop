package lu.itrust.adtop.controller.screen;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.Pane;
/**
 * Class contains elements of customers screen
 * @author ersagun
 *
 */
public class SelectCustomerController {

	/**
	 * Container of customers
	 */
	@FXML
	private ChoiceBox<String> selectCustomer;
	
	/**
	 * Screen of this controller
	 */
	@FXML
	private Pane chooseSelectionScreen;
	
	
	
	/* GETTERS AND SETTERS */
	
	public ChoiceBox<String> getSelectCustomer() {
		return selectCustomer;
	}
	public void setSelectCustomer(ChoiceBox<String> selectCustomer) {
		this.selectCustomer = selectCustomer;
	}
	public Pane getChooseSelectionScreen() {
		return chooseSelectionScreen;
	}
	public void setChooseSelectionScreen(Pane chooseSelectionScreen) {
		this.chooseSelectionScreen = chooseSelectionScreen;
	}
}
