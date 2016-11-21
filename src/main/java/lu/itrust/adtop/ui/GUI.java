package lu.itrust.adtop.ui;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lu.itrust.adtop.utils.Configuration;
/**
 * General user interface
 * Class create a Java FX window and charge index fxml file
 * 
 * @author ersagun
 *
 */
public class GUI extends Application {

	   private static HostServices hostServices ;

	/**
	 * Main function launch application
	 * @param args arguments
	 */
	public static void main(String[] args) {
		Application.launch(GUI.class, args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		try {
			stage.setTitle("ADTop"); 
	        hostServices = getHostServices();
			Parent root = FXMLLoader.load(getClass().getResource(Configuration.getInstance().getConfigProperties().getFxml()));
			stage.setScene(new Scene(root));
			String css = this.getClass().getResource("/fxml/style.css").toExternalForm(); 
			stage.getScene().getStylesheets().add(css);
			stage.setResizable(false);
			stage.show();
		} catch (Exception e) {
			System.out.print(e);
		}
	}
	
    public static HostServices getApplicationHostServices() {
        return hostServices ;
    }
	
}
