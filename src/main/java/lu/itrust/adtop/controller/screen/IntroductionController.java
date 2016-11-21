package lu.itrust.adtop.controller.screen;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import lu.itrust.adtop.controller.Controller;
import lu.itrust.adtop.utils.Language;
/**
 * Contains all introdcution controller screen components
 *
 * @author ersagun
 *
 */
public class IntroductionController extends Controller  {

	/**
	 * Screens of the introduction 
	 */ 
	@FXML
	private Pane introductionScreen;

	
	/**
	 * Title of the application
	 */
	@FXML
	private Text title; 
	

	/**
	 * Get the current language and initialize title with language selected
	 * @param languages
	 */
	public void initializeTitle(String languages) {
		this.getTitle().setText((Language.getWord(languages, "title")));
	}


	/**
	 * This methods allow user to disable the Introduction screen
	 */
	@Override
	public void disableScreen() {
		setVisibleScreen(introductionScreen,false);
		
	}

	
	/* GETTERS AND SETTERS */
	
	public Text getTitle() {
		return title;
	}


	public void setTitle(Text title) {
		this.title = title;
	}
	
	public Pane getImportScreen() {
		return introductionScreen;
	}

	public void setImportScreen(Pane importScreen) {
		this.introductionScreen = importScreen;
	}

	
	public Pane getIntroductionScreen() {
		return introductionScreen;
	}


	public void setIntroductionScreen(Pane introductionScreen) {
		this.introductionScreen = introductionScreen;
	}

}
