package lu.itrust.adtop.controller;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBase;
import javafx.scene.text.Text;
import javafx.stage.Window;

/**
 * Class abstract Controller contains similar methods between controllers
 * enabling a screen, setting visible a button or text managed in this class
 * 
 * @author ersagun
 *
 */
public abstract class Controller {

	/**
	 * Return all screen attributes associated to the controller Only screen
	 * attributes should contains word Screen
	 * 
	 * @return list of screen
	 */
	public List<Node> getAvailableScreen() {
		List<Node> nReturned = new LinkedList<>();
		for (Field field : this.getClass().getDeclaredFields()) {
			try {
				field.setAccessible(true);
				if (field.getName().matches("(.*)Screen(.*)") && field.get(this) != null)
					nReturned.add((Node) field.get(this));

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return nReturned;
	}

	/**
	 * Set visible the Text object to true or false and
	 * 
	 * @param text
	 *            text to set visible
	 * @param string
	 *            string show on this text
	 * @param bool
	 *            boolean is true or false
	 */
	public void setVisibleText(Text text, String string, boolean bool) {
		text.setDisable(!bool);
		text.setVisible(bool);
		if (!string.isEmpty()) {
			text.setText(string);
		}
	}

	/**
	 * Set visible the button in parameter
	 * 
	 * @param button
	 *            to make visible
	 * @param bool
	 *            if true is visible else not visible
	 */
	public void setVisibleButton(ButtonBase button, Boolean bool) {
		button.setDisable(!bool);
		button.setVisible(bool);
	}

	/**
	 * Set visible the screen (Node) in parameter
	 * 
	 * @param node
	 *            node to set visible
	 * @param bool
	 *            to set visible is true else false
	 */
	public void setVisibleScreen(Node node, boolean bool) {
		node.setDisable(!bool);
		node.setVisible(bool);
	}

	/**
	 * Allows go to screen in parameter
	 * 
	 * @param screen
	 *            Screen
	 */
	public void goToScreen(Node screen) {
		setVisibleScreen(screen, true);
	}

	/**
	 * Each controller should be able to to disable its screen
	 */
	public abstract void disableScreen();

	/**
	 * Return all screen attributes associated to the controller Only screen
	 * attributes should contains word Screen
	 * 
	 * @param str
	 *            String name of screen
	 */
	public void enableScreen(String str) {
		getAvailableScreen().stream().forEach((node) -> {
			if (node.getId().equals(str))
				setVisibleScreen(node, true);
			else
				setVisibleScreen(node, false);
		});
	}

	/**
	 * This methods add a pop up to inform user about warnings
	 * 
	 * @param info
	 *            information to provide
	 * @param content
	 *            content to provide
	 */
	public static void showWarning(Window parent, String info, String content) {
		showAlert(AlertType.WARNING, parent, info, null, content).showAndWait();
	}

	/**
	 * This methods add a pop up to inform user about informations
	 * 
	 * @param info
	 *            information to provide
	 * @param content
	 *            content to provide
	 */
	public static void showInformation(Window parent, String info, String content) {
		showAlert(AlertType.INFORMATION, parent, info, null, content).showAndWait();
	}

	public static Alert showAlert(AlertType type, Window parent, String title, String headerText, String content) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(headerText);
		if (content != null)
			alert.setContentText(content);
		alert.initOwner(parent);
		return alert;
	}

}
