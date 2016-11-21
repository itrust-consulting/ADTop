package lu.itrust.adtop.controller.screen;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Connection form input content for allow user to 
 * connect, in this screen user fill username text 
 * and password 
 * @author ersagun
 *
 */
public class FormController {
	@FXML
	private Button logIn;
	
	@FXML
	private PasswordField passwordText;

	@FXML
	private TextField urlApi;

	@FXML
	private TextField usernameText;

	
	/* GETTERS AND SETTERS */
	
	public Button getLogIn() {
		return logIn;
	}

	public void setLogIn(Button logIn) {
		this.logIn = logIn;
	}

	public PasswordField getPasswordText() {
		return passwordText;
	}

	public void setPasswordText(PasswordField passwordText) {
		this.passwordText = passwordText;
	}

	public TextField getUrlApi() {
		return urlApi;
	}

	public void setUrlApi(TextField urlApi) {
		this.urlApi = urlApi;
	}

	public TextField getUsernameText() {
		return usernameText;
	}

	public void setUsernameText(TextField usernameText) {
		this.usernameText = usernameText;
	}
	

}
