package lu.itrust.adtop.controller.screen;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.sun.javafx.collections.ObservableListWrapper;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lu.itrust.adtop.controller.Controller;
import lu.itrust.adtop.model.measure.MeasureContainer;
import lu.itrust.adtop.tools.Pack;
import lu.itrust.adtop.tools.API.ConnectorAPIImpl;
import lu.itrust.adtop.utils.Language;

/**
 * Class manage Connection to TRICK Service API Connecting to API selection of
 * customer Implementation allows Importation of Risk analysis file from remote
 * server
 * 
 * @author ersagun
 *
 */
@SuppressWarnings("restriction")
public class ConnectionController implements Initializable {

	private String language;

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 * Basic authentication informations
	 */
	private ConnectorAPIImpl authentication;

	/**
	 * Controller of customers selection screen
	 */
	@FXML
	private SelectCustomerController selectCustomerController;

	/**
	 * Controller of form screen
	 */
	@FXML
	private FormController formController;

	private String customersValue;

	private String analysisValue;

	private String versionValue;

	private String scenarioValue;

	private String standardValue;

	private String assetValue;

	private Pack pack;

	public Pack getPack() {
		return pack;
	}

	public void setPack(Pack pack) {
		this.pack = pack;
	}

	public String getAssetValue() {
		return assetValue;
	}

	public void setAssetValue(String assetValue) {
		this.assetValue = assetValue;
	}

	public String getScenarioValue() {
		return scenarioValue;
	}

	public void setScenarioValue(String scenarioValue) {
		this.scenarioValue = scenarioValue;
	}

	public String getStandardValue() {
		return standardValue;
	}

	public void setStandardValue(String standardValue) {
		this.standardValue = standardValue;
	}

	public String getVersionValue() {
		return versionValue;
	}

	public void setVersionValue(String versionValue) {
		this.versionValue = versionValue;
	}

	private ComboBox<String> analysis;

	private ComboBox<String> customers;

	private ComboBox<String> versions;

	private ComboBox<String> assets;

	private ComboBox<String> scenarios;

	private ComboBox<String> standard;

	private String customerId;

	private String analysisId;
	private String versionId;
	private String scenarioId;
	private String assetId;
	private String standardName;

	public String getScenarioId() {
		return scenarioId;
	}

	public void setScenarioId(String scenarioId) {
		this.scenarioId = scenarioId;
	}

	public String getAssetId() {
		return assetId;
	}

	public void setAssetId(String assetId) {
		this.assetId = assetId;
	}

	public String getStandardName() {
		return standardName;
	}

	public void setStandardName(String standardName) {
		this.standardName = standardName;
	}

	public String getVersionId() {
		return versionId;
	}

	public void setVersionId(String versionId) {
		this.versionId = versionId;
	}

	public String getAnalysisId() {
		return analysisId;
	}

	public void setAnalysisId(String analysisId) {
		this.analysisId = analysisId;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	private Button ok;

	private Stage view;

	public ComboBox<String> getVersions() {
		return versions;
	}

	public void setVersions(ComboBox<String> versions) {
		this.versions = versions;
	}

	public ComboBox<String> getAssets() {
		return assets;
	}

	public void setAssets(ComboBox<String> assets) {
		this.assets = assets;
	}

	public ComboBox<String> getScenarios() {
		return scenarios;
	}

	public void setScenarios(ComboBox<String> scenarios) {
		this.scenarios = scenarios;
	}

	public ComboBox<String> getStandard() {
		return standard;
	}

	public void setStandard(ComboBox<String> standard) {
		this.standard = standard;
	}

	public Button getOk() {
		return ok;
	}

	public void setOk(Button ok) {
		this.ok = ok;
	}

	public ComboBox<String> getAnalysis() {
		return analysis;
	}

	public void setAnalysis(ComboBox<String> analysis) {
		this.analysis = analysis;
	}

	public ComboBox<String> getCustomers() {
		return customers;
	}

	public void setCustomers(ComboBox<String> customers) {
		this.customers = customers;
	}

	public String getAnalysisValue() {
		return analysisValue;
	}

	public void setAnalysisValue(String analysisValue) {
		this.analysisValue = analysisValue;
	}

	public FormController getFormController() {
		return formController;
	}

	public void setFormController(FormController formController) {
		this.formController = formController;
	}

	public String getCustomersValue() {
		return customersValue;
	}

	public void setCustomersValue(String customersValue) {
		this.customersValue = customersValue;
	}

	/**
	 * Initialization of all components
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializeLogIn();
	}

	/**
	 * Initialization of login button action On click create an authentication,
	 * connect user with basic authentication and keep token thanks to token
	 * manage session of user
	 */
	public void initializeLogIn() {
		formController.getLogIn().setOnAction(new EventHandler<ActionEvent>() {
			@SuppressWarnings({ "unchecked" })
			@Override
			public void handle(final ActionEvent e) {
				authentication = new ConnectorAPIImpl(formController.getUrlApi().getText());
				if (authentication.connect(formController.getUsernameText().getText(), formController.getPasswordText().getText())) {

					if (authentication.isAuthorized()) {
						Stage stage = new Stage();
						Parent root = null;
						try {
							root = FXMLLoader.load(getClass().getResource("/fxml/TRICKAPI.fxml"));
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						ok = (Button) root.lookup("#ok");

						customers = (ComboBox<String>) root.lookup("#apiCustomers");
						analysis = (ComboBox<String>) root.lookup("#apiAnalysis");
						versions = (ComboBox<String>) root.lookup("#apiVersions");
						assets = (ComboBox<String>) root.lookup("#apiAssets");
						scenarios = (ComboBox<String>) root.lookup("#apiScenarios");
						standard = (ComboBox<String>) root.lookup("#apiStandard");
						
						customers.setItems(new ObservableListWrapper<String>(authentication.getCustomerNameString()));
						
						customers.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(final ActionEvent e) {

								if (customers.getSelectionModel().getSelectedItem() != null) {
									customersValue = customers.getSelectionModel().getSelectedItem();

									authentication.getCustomer().stream().filter(namable -> namable.getName().equals(customersValue)).findAny().ifPresent(customerSelected -> {
										analysis.setItems(new ObservableListWrapper<>(authentication.getAnalysisNameString(customerId = customerSelected.getId().toString())));
										analysis.setDisable(false);
									});
								}
							}
						});

						analysis.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(final ActionEvent e) {

								if (analysis.getSelectionModel().getSelectedItem() != null) {
									analysisValue = analysis.getSelectionModel().getSelectedItem();

									authentication.getAnalysis(customerId).stream().filter(namable -> namable.getName().equals(analysisValue)).findAny()
											.ifPresent(analysisSelected -> {

												versions.setItems(new ObservableListWrapper<String>(
														authentication.getVersionNameString(customerId, analysisId = analysisSelected.getId().toString())));

												versions.setDisable(false);
											});

								}
							}
						});

						versions.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(final ActionEvent e) {

								if (versions.getSelectionModel().getSelectedItem() != null) {
									versionValue = versions.getSelectionModel().getSelectedItem();
									authentication.getVersions(customerId, analysisId).stream().filter(namable -> namable.getName().equals(versionValue)).findAny()
											.ifPresent(versionSelected -> {

												assets.setItems(
														new ObservableListWrapper<String>(authentication.getAssetNameString(versionId = versionSelected.getId().toString())));

												scenarios.setItems(new ObservableListWrapper<String>(authentication.getScenariosNameString(versionId)));

												standard.setItems(new ObservableListWrapper<String>(authentication.getStandardNameString(versionId)));

												assets.setDisable(false);
												scenarios.setDisable(false);
												standard.setDisable(false);
											});
								}
							}
						});

						scenarios.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(final ActionEvent e) {
								scenarioValue = scenarios.getSelectionModel().getSelectedItem();
								authentication.getScenarios(versionId).stream().filter(namable -> namable.getName().equals(scenarioValue)).findAny()
										.ifPresent(scenarioSelected-> scenarioId = scenarioSelected.getId().toString());
								
							}
						});

						assets.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(final ActionEvent e) {
								assetValue = assets.getSelectionModel().getSelectedItem();
								authentication.getAssets(versionId).stream().filter(namable -> namable.getName().equals(assetValue)).findAny()
										.ifPresent(assetSelected -> assetId = assetSelected.getId().toString());
							}
						});

						standard.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(final ActionEvent e) {
								standardName = standard.getSelectionModel().getSelectedItem();
							}
						});

						ok.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(final ActionEvent e) {
								MeasureContainer mc = authentication.getMeasureContainer(versionId, assetId, scenarioId, standardName);
								if (mc != null) {
									showInformation(Language.getWord(language, "INFO.RAImported.title"), Language.getWord(language, "INFO.RAImported.description"));
									pack.setMeasureContainer(mc);
									stage.close();
									((Stage) formController.getLogIn().getScene().getWindow()).close();
								} /*
									 * else { TODO : check warning case }
									 */
							}
						});

						// stage.getIcons().add(new Image(getClass()
						// .getResourceAsStream(Configuration.getInstance().getConfigProperties().getItrustIcon())));
						stage.setScene(new Scene(root));
						stage.setTitle("TRICK Service Connection");
						stage.initOwner(view.getOwner());
						stage.initModality(Modality.APPLICATION_MODAL);
						stage.showAndWait();
					} else
						showWarning("Authentication is not authaurized", "You are not authorised");
				} else
					showWarning("Bad credential", "Please check your credential");
			}
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
	public void showWarning(String info, String content) {
		Controller.showWarning(this.view.getOwner(), info, content);
	}

	/**
	 * This methods add a pop up to inform user about informations
	 * 
	 * @param info
	 *            information to provide
	 * @param content
	 *            content to provide
	 */
	public void showInformation(String info, String content) {
		Controller.showInformation(this.view.getOwner(), info, content);

	}

	/* GETTERS AND SETTERS */

	public SelectCustomerController getSelectCustomerController() {
		return selectCustomerController;
	}

	public void setSelectCustomerController(SelectCustomerController selectCustomerController) {
		this.selectCustomerController = selectCustomerController;
	}

	public ConnectorAPIImpl getAuthentication() {
		return authentication;
	}

	public void setAuthentication(ConnectorAPIImpl authentication) {
		this.authentication = authentication;
	}

	public void setView(Stage view) {
		this.view = view;

	}
}
