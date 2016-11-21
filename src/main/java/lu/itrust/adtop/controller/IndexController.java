package lu.itrust.adtop.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lu.itrust.adtop.controller.screen.ConnectionController;
import lu.itrust.adtop.exception.ADException;
import lu.itrust.adtop.model.measure.Measure;
import lu.itrust.adtop.model.measure.MeasureContainer;
import lu.itrust.adtop.model.measure.Standard;
import lu.itrust.adtop.model.tree.AttackDefenceTree;
import lu.itrust.adtop.model.tree.AttackDefenceTreeGenerator;
import lu.itrust.adtop.tools.Pack;
import lu.itrust.adtop.ui.GUI;
import lu.itrust.adtop.utils.Configuration;
import lu.itrust.adtop.utils.Language;
import lu.itrust.adtop.utils.Log;

/**
 * Control Index page. Root of screens. From this class we can access to all
 * screen and their contents. The behavior of some buttons who has an
 * interaction with other screens are declared in this controller
 * 
 * @author ersagun
 *
 */
public class IndexController extends Controller implements Initializable {

	private static final String STRING_ZERO = "0";

	/**
	 * main controller is a sub screen all screens are merged in this controller
	 */
	@FXML
	private MainController mainController;

	/**
	 * menu controller contains all menu and items
	 */
	@FXML
	private MenuController menuController;

	/**
	 * index screen contains menu and main controller in a grid
	 */
	@FXML
	private GridPane indexScreen;

	/**
	 * Package contains all objects of the application
	 */
	private Pack pack;

	/**
	 * Current language of user
	 */
	private String language;

	/**
	 * Copyright text of application
	 */
	@FXML
	private Text copyright;

	private String treePath;

	private String currentPath;

	/**
	 * initialization of screen
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		pack = new Pack();
		// Default Language
		language = "English";
		try {
			languageInstructions(menuController.getLanguageEN().getText());
		} catch (Exception e) {
			e.printStackTrace();
		}
		initializeSetOnActions();
		// demo();
		menuController.update(pack);
	}

	/**
	 * Initialize the menu items
	 */
	public void initializeSetOnActions() {
		initializeMenuItems();
	}

	/**
	 * Initialize copyright text of the software for different languages
	 * 
	 * @param languages
	 */
	public void initializeCopyright(String languages) {
		Calendar calendar = Calendar.getInstance();
		String year = calendar.get(Calendar.YEAR) + "";
		copyright.setText(Language.getWord(languages, "copyright", new Object[] { year }));
	}

	/**
	 * initialization of buttons
	 */

	/**
	 * Methods initialize behavior of all menu items
	 */
	public void initializeMenuItems() {
		initializeImportMenuItems();
		initializeExportMenuItems();
		initializeGenerateMenuItems();
		initializeEvaluateMenuItems();
		initializeHelpMenuItems();

		/**
		 * File exit
		 */
		menuController.getExit().setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent actionEvent) {
				Platform.exit();
				System.exit(0);
			}
		});

	}

	/**
	 * File importations
	 */
	public void initializeImportMenuItems() {
		menuController.getImportAttackDefenceTree().setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				try {
					if (importAttackDefenceTree()) {
						if (evaluateAttackDefenceTree()) {
							pack.updateIntialProbability();
							showAttackDefenceTreeOnScreen();
							generateAndReplaceTree(pack.getAttackDefenceTree());
							showInformation(Language.getWord(language, "INFO.ATImported.title"), Language.getWord(language, "INFO.ATImported.description"));
							menuController.update(pack);
						}
					}

				} catch (Exception exception) {
					exception.printStackTrace();
					Log.log(exception);
					if (exception instanceof ADException) {
						ADException adException = (ADException) exception;
						showWarning(Language.getWord(language, "WAR.ATImported.title"),
								Language.getWord(language, adException.getCode(), adException.getParameters(), adException.getMessage()));
					} else
						showWarning(Language.getWord(language, "WAR.ATImported.title"), Language.getWord(language, "WAR.ATImported.description") + exception.getMessage());
					pack.getAttackDefenceTree().clear();
				}
			}
		});

		menuController.getImportExtractRATRICK().setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				try {

					Stage stage = new Stage();
					FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/connectionScreen.fxml"));
					Parent root = (Parent) fxmlLoader.load();
					ConnectionController controller = fxmlLoader.<ConnectionController>getController();
					controller.setView(stage);
					controller.setPack(pack);
					controller.setLanguage(language);
					stage.setScene(new Scene(root));
					stage.setTitle(Language.getWord(language, "label.ts.connection", "TRICK Service Connection"));
					stage.initModality(Modality.APPLICATION_MODAL);
					stage.initOwner(indexScreen.getScene().getWindow());
					stage.showAndWait();
					menuController.update(pack);
				} catch (Exception exception) {
					exception.printStackTrace();
					Log.log(exception);
				}
			}
		});

		menuController.getImportExtractRAJSON().setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				if (importRRF()) {
					showInformation(Language.getWord(language, "INFO.RAImported.title"), Language.getWord(language, "INFO.RAImported.description"));
					menuController.update(pack);
				} else {
					pack.getMeasureContainer().clear();
				}
			}
		});

		menuController.getImportAssociationMatrix().setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				if (!pack.getAttackDefenceTree().isEmpty() && !pack.getMeasureContainer().isEmpty() && pack.getAttackDefenceTree().isEvaluated()
						|| !pack.getAttackDefenceTree().isEmpty()) {
					try {
						if (importAssociationMatrix()) {
							showInformation(Language.getWord(language, "INFO.AMImported.title"), Language.getWord(language, "INFO.AMImported.description"));
							menuController.update(pack);
						}
					} catch (Exception amce) {
						Log.log(amce);
						if (amce instanceof ADException) {
							ADException adException = (ADException) amce;
							showWarning(Language.getWord(language, "WAR.AMImported.title"),
									Language.getWord(language, adException.getCode(), adException.getParameters(), adException.getMessage()));
						} else
							amce.printStackTrace();
						pack.getAssociationMatrix().clear();
					}
				} else
					showWarning(Language.getWord(language, "WAR.AMImported2.title"), Language.getWord(language, "WAR.AMImported2.description"));
			}
		});

		menuController.getImportDL().setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
			}
		});
	}

	/**
	 * File exports
	 */
	public void initializeExportMenuItems() {
		menuController.getExportAssociationMatrix().setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				if (exportAssociationMatrix()) {
					showInformation(Language.getWord(language, "INFO.AMExported.title"), Language.getWord(language, "INFO.AMExported.description"));
					menuController.update(pack);
				}
			}
		});

		menuController.getExportRRF().setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				if (exportRRF()) {
					showInformation(Language.getWord(language, "INFO.RRF.exportation.title", "RRF export"),
							Language.getWord(language, "INFO.RRF.exportation.description", "RRF has been successfully exported"));
					menuController.update(pack);
				}
			}
		});

		menuController.getExportADT().setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				if (exportAttackDefenceTree()) {
					showInformation(Language.getWord(language, "INFO.ADTExported.title"), Language.getWord(language, "INFO.ADTExported.description"));
					menuController.update(pack);
				}
			}
		});
	}

	/**
	 * Generations
	 */
	public void initializeGenerateMenuItems() {
		menuController.getGenerateAttackDefenceTree().setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				if (!pack.getAttackDefenceTree().isEmpty()) {
					try {
						if (pack.getAttackDefenceTree().isImported() || checkCorrespondanceScenario()) {
							// pack.getAttackDefenceTree().reset();
							pack.getAttackDefenceTree().generateFromAT(pack);
							try {
								if (evaluateAttackDefenceTree()) {
									generateAndReplaceTree(pack.getAttackDefenceTree());
									showAttackDefenceTreeOnScreen();
									showInformation(Language.getWord(language, "INFO.ADTGen.title"), Language.getWord(language, "INFO.ADTGen.description"));
									menuController.update(pack);
								}
							} catch (ADException e1) {
								Log.log(e1);
								showWarning(Language.getWord(language, "WAR.ADTGen.title"), Language.getWord(language, e1.getCode(), e1.getParameters(), e1.getMessage()));
							}
						}
					} catch (ADException e1) {
						Log.log(e1);
						showWarning(Language.getWord(language, "WAR.ADTGen.title"), Language.getWord(language, e1.getCode(), e1.getParameters(), e1.getMessage()));
					}
				} else
					showWarning(Language.getWord(language, "WAR.ADTGen2.title"), Language.getWord(language, "WAR.ADTGen2.description"));
			}
		});
	}

	/**
	 * Evaluations
	 */
	public void initializeEvaluateMenuItems() {

		menuController.getFindOptimalAttackDefenceTreeMultiple().setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				try {
					List<Double> l = addConstraintQuestionScreen();
					int numberOfTrees = askNumberOfTree();
					if (numberOfTrees > -1)
						startOptimalTreeGeneration(numberOfTrees, l, 0);
				} catch (ADException e1) {
					e1.printStackTrace();
				} catch (NumberFormatException e1) {
					showWarning(Language.getWord(language, "WAR.NumberFormatException.title"), Language.getWord(language, "WAR.NumberFormatException.description"));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
	}

	public static Integer toInt(String value, Integer defaultValue) {
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	public static Double toDouble(String value, Double defaultValue) {
		try {
			return Double.parseDouble(value);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	public int askNumberOfTree() {
		TextInputDialog dialog = new TextInputDialog(STRING_ZERO);
		dialog.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
			Integer value = toInt(newValue, null);
			if (value == null) {
				if (!newValue.isEmpty() && !oldValue.equals(newValue))
					dialog.getEditor().setText(toInt(oldValue, 1).toString());
				else
					dialog.getEditor().setText(STRING_ZERO);
			} else if (oldValue.equals(STRING_ZERO) || newValue.startsWith(STRING_ZERO) && !newValue.equals(STRING_ZERO))
				dialog.getEditor().setText(value.toString());
		});
		dialog.setTitle(Language.getWord(language, "label.number.optimal.trees", "Number of optimal ADTrees"));
		dialog.initOwner(indexScreen.getScene().getWindow());
		Stage stage2 = (Stage) dialog.getDialogPane().getScene().getWindow();
		stage2.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				event.consume();
			}
		});

		dialog.setHeaderText(Language.getWord(language, "QUEST.number.optimal.adtrees.display", "How many optimal ADTrees would you like to display?"));
		Optional<String> result = dialog.showAndWait();
		return toInt(result.orElse("-1"), 1);
	}

	public void startOptimalTreeGeneration(int nbTabs, List<Double> l, int choice) throws IOException {
		// ask to user
		pack.initializeAttackDefenceTreeGenerator(choice, nbTabs, l);
		showAttackDefenceTreeTabsOnScreen();
		generateAndCreateAllTabs(pack.getAttackDefenceTreeGenerator(), nbTabs);
		menuController.update(pack);
	}

	public List<Double> addConstraintQuestionScreen() {
		List<Double> vals = null;
		Alert alert = showAlert(AlertType.CONFIRMATION, indexScreen.getScene().getWindow(), Language.getWord(language, "label.add.constaint", null, "Add constraint"),
				Language.getWord(language, "QUEST.add.constraint", null, "Do you want to add a constraint?"), null);
		ButtonType buttonTypeOne = new ButtonType(Language.getWord(language, "label.yes", "Yes"));
		ButtonType buttonTypeTwo = new ButtonType(Language.getWord(language, "label.no", "No"));
		alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == buttonTypeOne) {
			vals = showAddConstraintScreen();
		} else if (result.get() == buttonTypeTwo) {
			vals = new ArrayList<>(2);
			vals.add(0D);
			vals.add(0D);
		}
		return vals;
	}

	public List<Double> showAddConstraintScreen() {
		List<Double> list = new ArrayList<Double>(2);
		Stage stage2 = new Stage();
		Parent root2 = null;
		try {
			root2 = FXMLLoader.load(getClass().getResource("/fxml/addConstraintScreen.fxml"));
			stage2.setScene(new Scene(root2));
			stage2.setTitle(Language.getWord(language, "label.add.constraint.screen", "Add constraint screen"));
			stage2.initModality(Modality.APPLICATION_MODAL);
			stage2.initOwner(indexScreen.getScene().getWindow());
			stage2.setResizable(false);

			stage2.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent event) {
					// consume event
					event.consume();
				}
			});

			CheckBox maxCost = (CheckBox) root2.getScene().lookup("#checkBoxMaxSec");
			CheckBox maxSuc = (CheckBox) root2.getScene().lookup("#checkBoxMaxSucProb");

			maxSuc.setText(Language.getWord(language, "label.max.success.probability", "Maximum success probability:"));
			maxCost.setText(Language.getWord(language, "label.max.sec.cost", "Maximum security implementation cost:"));
			TextField maxSecCost = (TextField) root2.getScene().lookup("#maxSecCost");
			TextField maxSucProb = (TextField) root2.getScene().lookup("#maxSucProb");

			maxSecCost.textProperty().addListener(doubleFieldValidator());
			maxSucProb.textProperty().addListener(doubleFieldValidator());

			maxCost.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(final ActionEvent e) {
					if (maxCost.selectedProperty().get())
						maxSecCost.setDisable(false);
					else
						maxSecCost.setDisable(true);
				}
			});

			maxSuc.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(final ActionEvent e) {
					if (maxSuc.selectedProperty().get())
						maxSucProb.setDisable(false);
					else
						maxSucProb.setDisable(true);
				}
			});

			Button okConstraint = (Button) root2.getScene().lookup("#okConstraint");

			okConstraint.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(final ActionEvent e) {
					stage2.close();
					list.add(maxSecCost.isDisabled() ? 0D : toDouble(maxSecCost.getText(), 0D));
					list.add(maxSucProb.isDisabled() ? 0D : toDouble(maxSucProb.getText(), 0D));
				}
			});

			stage2.showAndWait();
		} catch (IOException exception) {
			exception.printStackTrace();
			Log.log(exception);
		}
		return list;
	}

	private ChangeListener<? super String> doubleFieldValidator() {
		return (observable, oldValue, newValue) -> {
			TextField textField = (TextField) ((StringProperty) observable).getBean();
			Double value = toDouble(newValue, null);
			if (value == null) {
				if (!newValue.isEmpty() && !oldValue.equals(newValue))
					textField.setText(toDouble(oldValue, 0D).toString());
				else
					textField.setText("");
			}
		};
	}

	/**
	 * Help
	 */
	public void initializeHelpMenuItems() {
		Calendar calendar = Calendar.getInstance();
		String year = calendar.get(Calendar.YEAR) + "";
		menuController.getHelpAboutADTop().setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				Stage stage = new Stage();
				Parent root = null;
				try {
					root = FXMLLoader.load(getClass().getResource("/fxml/about.fxml"));
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				// stage.getIcons().add(new
				// Image(getClass().getResourceAsStream(Configuration.getInstance().getConfigProperties().getItrustIcon())));
				TextArea ta = (TextArea) root.lookup("#textAbout");

				ta.setText(Language.getWord(language, "aboutText", new Object[] { year }));
				stage.setScene(new Scene(root));
				stage.setTitle(Language.getWord(language, "titleAbout"));
				stage.initModality(Modality.APPLICATION_MODAL);
				stage.initOwner(indexScreen.getScene().getWindow());

				Button ok = (Button) root.lookup("#okButton");
				ok.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(final ActionEvent e) {
						stage.close();
					}
				});
				stage.showAndWait();
			}
		});

		/**
		 * GET STARTED
		 */
		menuController.getHelpGetStarted().setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				try {
					openURL();
				} catch (IOException e1) {
					showWarning(Language.getWord(language, "label.title.file.not.found", "File not found"),
							Language.getWord(language, "ERROR.file.not.found", "File cannot be found"));
				}
			}
		});
		initializeLanguageMenuItems();
	}

	public void selectCustomer(List<String> choices) {
		ChoiceDialog<String> dialog = new ChoiceDialog<>("b", choices);
		dialog.setTitle("Choice Dialog");
		dialog.setHeaderText("Look, a Choice Dialog");
		dialog.setContentText("Choose your letter:");
		dialog.showAndWait();
	}

	public void openURL() throws IOException {
		String filename = Configuration.getInstance().getConfigProperties().getGetStarted(), temp = System.getProperty("java.io.tmpdir");
		File userGuide = new File(temp + File.separator + filename);
		if (!userGuide.exists())
			FileUtils.copyURLToFile(getClass().getResource(filename), userGuide);
		userGuide.deleteOnExit();
		GUI.getApplicationHostServices().showDocument("file://" + userGuide.getPath());
	}

	/**
	 * Languages
	 */
	public void initializeLanguageMenuItems() {
		menuController.getLanguageFR().setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				languageInstructions(menuController.getLanguageFR().getText());
			}
		});

		menuController.getLanguageEN().setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				languageInstructions(menuController.getLanguageEN().getText());
			}
		});
	}

	public void languageInstructions(String language) {
		this.language = language;
		toggledLanguageMenu();
		mainController.getIntroductionController().initializeTitle(language);
		initializeCopyright(language);
		menuController.initializeMenuLanguages(language);
		if (this.pack.getAttackDefenceTreeGenerator() != null && mainController.getTreeController().getTreeView().isDisable()
				&& !mainController.getTreeController().getTreeView().isVisible()) {
			generateAndCreateAllTabs(pack.getAttackDefenceTreeGenerator(), 10);
		} else if (this.pack.getAttackDefenceTreeGenerator() == null && !mainController.getTreeController().getTreeView().isDisable()
				&& mainController.getTreeController().getTreeView().isVisible()) {
			generateAndReplaceTree(this.pack.getAttackDefenceTree());
		}
	}

	private void toggledLanguageMenu() {
		menuController.getLanguageEN().setDisable(language.equals(menuController.getLanguageEN().getText()));
		menuController.getLanguageFR().setDisable(language.equals(menuController.getLanguageFR().getText()));
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
		showWarning(indexScreen.getScene().getWindow(), info, content);
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
		showInformation(indexScreen.getScene().getWindow(), info, content);
	}

	/**
	 * See if ADT file and pack.getMeasures() matching
	 * 
	 * @return bool if matching true,else false
	 * @throws CheckCorrespondanceScenarioException
	 */
	public boolean checkCorrespondanceScenario() {
		Boolean bool = false;
		if (!pack.getAttackDefenceTree().isEmpty()) {
			String adtFileScenario = pack.getAttackDefenceTree().getScenarioName().replace("\n", " ").replace("\r", "");
			if (!(pack.getMeasureContainer().getScenario().getName().equals(adtFileScenario)))
				throw new ADException("ERROR.scenario.attack_tree.analysis", "The Attack tree and extract of risk analysis do not match.");
			else
				bool = true;
		} else if (!pack.getAttackDefenceTree().isEmpty()) {
			String adtFileScenario = pack.getAttackDefenceTree().getScenarioName().replace("\n", " ").replace("\r", "");
			if (!(pack.getMeasureContainer().getScenario().getName().equals(adtFileScenario)))
				throw new ADException("ERROR.scenario.attack_tree.analysis", "The Attack tree and extract of risk analysis do not match.");
			else
				bool = true;
		}
		return bool;
	}

	/**
	 * Import ADT from XML File
	 * 
	 * @throws XMLFileCorruptedException
	 * @throws IOException
	 * @return exported
	 */
	public boolean importAttackDefenceTree() throws IOException {
		boolean imported = false;
		FileChooser fileChooser = new FileChooser();
		setDefaultPath(fileChooser);
		// Set extension filter
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(Language.getWord(language, "label.xml.files", "XML Files (*.xml)"), "*.xml");
		fileChooser.getExtensionFilters().add(extFilter);
		fileChooser.setTitle(Language.getWord(language, "chooseAttackDefenceTree"));
		File f = fileChooser.showOpenDialog(indexScreen.getScene().getWindow());
		if (f != null) {
			pack.getAttackDefenceTree().importFromXML(f.getAbsolutePath());
			pack.getAttackDefenceTree().generateAttackDefenceValues();
			pack.setUnTouchedDefenceTree(pack.getAttackDefenceTree().clone());
			generateAndReplaceTree(pack.getAttackDefenceTree());
			menuController.update(pack);
			imported = true;
			currentPath = this.treePath = f.getAbsolutePath();
		}
		return imported;
	}

	/**
	 * Export association matrix from XML File
	 * 
	 * @return exported
	 */
	public boolean exportRRF() {
		boolean exported = false;
		FileChooser fileChooser = new FileChooser();
		setDefaultPath(fileChooser);
		LocalDateTime now = LocalDateTime.now();

		NumberFormat f = new DecimalFormat("00");
		String month = String.valueOf(f.format(now.getMonthValue()));
		String day = String.valueOf(f.format(now.getDayOfMonth()));

		fileChooser.setInitialFileName("Export_rrf_" + now.getYear() + month + day + ".json");
		// Set extension filter
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(Language.getWord(language, "label.json.files", "JSON Files (*.json)"), "*.json");
		fileChooser.getExtensionFilters().add(extFilter);

		// Show save file dialog
		File file = fileChooser.showSaveDialog(indexScreen.getScene().getWindow());

		if (file != null) {
			try {
				exportRRF(file);
				exported = true;
			} catch (Exception e) {
				e.printStackTrace();
				exported = false;
			} finally {
				currentPath = file.getAbsolutePath();
			}
		}
		return exported;
	}

	/**
	 * maybe in measures exportRRF from a file vie Jackson
	 * 
	 * @param nf
	 *            file
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public void exportRRF(File nf) throws JsonGenerationException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		MeasureContainer measureContainer = pack.getMeasureContainer();
		Standard standard = measureContainer.getStandards().get(0);
		standard.getMeasures().forEach(measure -> measure.setOptimal(null));
		if (mainController.getTreeController().getTabs().isVisible() && pack.getAttackDefenceTreeGenerator() != null
				&& !pack.getAttackDefenceTreeGenerator().getAttackDefenceTrees().isEmpty()) {
			int index = mainController.getTreeController().getTabs().getSelectionModel().getSelectedIndex();
			AttackDefenceTree originalTree = pack.getAttackDefenceTreeGenerator().getAttackDefenceTrees().get(index);
			List<Measure> measures = originalTree.getMeasures().stream().filter(measure -> standard.hasMeasure(measure)).map(measure -> standard.getMeasureWithName(measure))
					.collect(Collectors.toList());
			Map<Measure, List<String>> measureAttackMapper = AttackDefenceTreeGenerator.forAttacksFindAllPositions(originalTree.getMeasures(), pack);
			AttackDefenceTree[] trees = new AttackDefenceTree[measures.size() + 1];
			trees[0] = pack.getUnTouchedDefenceTree();
			trees[0].evaluateAttackDefenceTree();
			trees[0].calculSetRosiValue(pack);

			System.out.println("P" + 0 + ": " + trees[0].getRoot().getSuccessValue());

			for (int i = 0, j = 1; i < measures.size(); i++, j++) {
				trees[j] = pack.getUnTouchedDefenceTree();
				trees[j].generateAttackDefenceValues();

				for (Measure measure : measures.subList(0, i + 1)) {
					for (String attackToTake : measureAttackMapper.get(measure))
						trees[j].addDefenceToAttackDefenceTree(measure, attackToTake, pack);
				}

				trees[j].generateAttackDefenceValues();
				trees[j].evaluateAttackDefenceTree();
				trees[j].calculSetRosiValue(pack);
				measures.get(i).setOptimal(1 - (trees[j].getRoot().getSuccessValue() / trees[j - 1].getRoot().getSuccessValue()));
				System.out.println("P" + j + ": " + trees[j].getRoot().getSuccessValue());
				System.out.println("Measure: " + measures.get(i).getName());
			}
		}
		pack.getAttackDefenceTree().setSetNumber(0); // reset set number
		mapper.writeValue(nf, measureContainer);
	}

	/**
	 * Export Association matrix file
	 * 
	 * @return exported
	 */
	public boolean exportAssociationMatrix() {
		boolean exported = false;
		FileChooser fileChooser = new FileChooser();
		LocalDateTime now = LocalDateTime.now();
		NumberFormat f = new DecimalFormat("00");
		String month = String.valueOf(f.format(now.getMonthValue()));
		String day = String.valueOf(f.format(now.getDayOfMonth()));
		fileChooser.setInitialFileName("Association_matrix_" + now.getYear() + month + day + ".xls");
		// Set extension filter
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(Language.getWord(language, "label.excel.files", "Excel Files (*.xls)"), "*.xls");
		fileChooser.getExtensionFilters().add(extFilter);

		// Show save file dialog
		File file = fileChooser.showSaveDialog(indexScreen.getScene().getWindow());

		if (file != null) {
			try {
				pack.getAssociationMatrix().createInitalMatrixInExcel(file, pack);
				exported = true;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				currentPath = file.getAbsolutePath();
			}
		}
		return exported;
	}

	/**
	 * Export attack-defence tree
	 * 
	 * @return exported
	 */
	public boolean exportAttackDefenceTree() {
		boolean exported = false;
		FileChooser fileChooser = new FileChooser();
		setDefaultPath(fileChooser);
		LocalDateTime now = LocalDateTime.now();
		NumberFormat f = new DecimalFormat("00");
		String month = String.valueOf(f.format(now.getMonthValue()));
		String day = String.valueOf(f.format(now.getDayOfMonth()));

		fileChooser.setInitialFileName("Export_ADTree_" + now.getYear() + month + day + ".xml");

		// Set extension filter
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(Language.getWord(language, "label.xml.files", "XML Files (*.xml)"), "*.xml");
		fileChooser.getExtensionFilters().add(extFilter);

		// Show save file dialog
		File file = fileChooser.showSaveDialog(indexScreen.getScene().getWindow());

		if (file != null) {
			try {

				AttackDefenceTree attackDefenceTree = pack.getAttackDefenceTreeGenerator() == null || pack.getAttackDefenceTreeGenerator().getAttackDefenceTrees().isEmpty()
						|| !mainController.getTreeController().getTabs().isVisible() ? pack.getAttackDefenceTree()
								: pack.getAttackDefenceTreeGenerator().getAttackDefenceTrees()
										.get(mainController.getTreeController().getTabs().getSelectionModel().getSelectedIndex());

				FileUtils.writeStringToFile(file, attackDefenceTree.export(new LinkedHashMap<>(attackDefenceTree.getMeasures().size())));

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				currentPath = file.getAbsolutePath();
			}
		}
		return exported;
	}

	/**
	 * Import pack.getMatrix() from XML File and convert Attack tree to
	 * attack-defence tree
	 * 
	 * @throws AssociationMatrixCorruptedException
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @return imported
	 */
	public boolean importAssociationMatrix() throws FileNotFoundException, IOException {
		boolean imported = false;
		FileChooser fileChooser = new FileChooser();
		setDefaultPath(fileChooser);
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(Language.getWord(language, "label.excel.files", "Excel Files (*.xls)"), "*.xls");
		fileChooser.getExtensionFilters().add(extFilter);

		fileChooser.setTitle(Language.getWord(language, "chooseAssociationMatrix"));
		File f = fileChooser.showOpenDialog(indexScreen.getScene().getWindow());

		if (f != null) {
			currentPath = f.getAbsolutePath();
			pack.getAssociationMatrix().importMatrixFromXLS(f.getAbsolutePath(), pack);
			imported = true;
			menuController.update(pack);
		}
		return imported;
	}

	/**
	 * 
	 * defence tree
	 * 
	 * @return evaluated
	 * @throws TreeCalculException
	 */
	public boolean evaluateAttackDefenceTree() {
		boolean evaluated = false;
		if (!pack.getAttackDefenceTree().isEmpty()) {
			pack.getAttackDefenceTree().evaluateAttackDefenceTree();
			pack.getAttackDefenceTree().setEvaluated(true);
			evaluated = true;
		}
		return evaluated;
	}

	/**
	 * Import measures from JSON File
	 * 
	 * @return imported
	 */
	public boolean importRRF() {
		boolean imported = false;
		FileChooser fileChooser = new FileChooser();
		setDefaultPath(fileChooser);
		// Set extension filter
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(Language.getWord(language, "label.json.files", "JSON Files (*.json)"), "*.json");
		fileChooser.getExtensionFilters().add(extFilter);

		fileChooser.setTitle(Language.getWord(this.language, "chooseRiskAnalysis"));
		File f = fileChooser.showOpenDialog(indexScreen.getScene().getWindow());

		if (f != null) {
			// JSON from file to Object
			try {
				ObjectMapper mapper = new ObjectMapper();
				pack.setMeasureContainer(mapper.readValue(f, MeasureContainer.class));
				imported = true;
				menuController.update(pack);
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				currentPath = f.getAbsolutePath();
			}
		}
		return imported;
	}

	/**
	 * Import DL from DL File, Defence Library
	 * 
	 * @return imported
	 */
	public boolean importDefenceLibrary() {
		boolean imported = false;
		FileChooser fileChooser = new FileChooser();
		setDefaultPath(fileChooser);
		// Set extension filter
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Excel Files (*.xls)", "*.xls");
		fileChooser.getExtensionFilters().add(extFilter);

		fileChooser.setTitle(Language.getWord(language, "label.title.choose.defence.library", "Choose a Defence Library"));
		File f = fileChooser.showOpenDialog(indexScreen.getScene().getWindow());
		if (f != null) {
			try {
				pack.getDefenceLibrary().importExcel(f.getAbsolutePath());
				imported = true;
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				currentPath = f.getAbsolutePath();
			}
		}
		return imported;
	}

	private void setDefaultPath(FileChooser fileChooser) {
		if (!(currentPath == null || currentPath.isEmpty())) {
			File path = new File(currentPath);
			fileChooser.setInitialDirectory(path.isDirectory() ? path : path.getParentFile());
		}
	}

	/**
	 * Method change screen and allows go to start analysis screen
	 */
	public void showAttackDefenceTreeOnScreen() {
		if (pack.getAttackDefenceTree().isImported() || !pack.getAttackDefenceTree().isEmpty()) {
			mainController.disableScreen();
			mainController.getTreeController().enableScreen("treeScreen");
			mainController.getTreeController().getTreeView().setVisible(true);
			mainController.getTreeController().getTreeView().setDisable(false);
		}
	}

	/**
	 * Method change screen and allows go to start analysis screen
	 */
	public void showAttackDefenceTreeTabsOnScreen() {
		mainController.disableScreen();
		mainController.getTreeController().enableScreen("treeScreen");
		mainController.getTreeController().getTreeView().setVisible(false);
		mainController.getTreeController().getTreeView().setDisable(true);
		mainController.getTreeController().getTabs().setVisible(true);
		mainController.getTreeController().getTabs().setDisable(false);
	}

	/**
	 * Method change screen and allows go to import screen
	 */
	public void goToIntroductionScreen() {
		mainController.disableScreen();
		mainController.getIntroductionController().enableScreen("introductionScreen");
	}

	@Override
	/**
	 * Allows disable current screen
	 */
	public void disableScreen() {
		mainController.disableScreen();
	}

	/**
	 * A demo version of events
	 */
	public void demo() {

		try {
			// Import attack defence tree
			this.treePath = "C:/Users/ersagun/Desktop/ATree.xml";
			pack.getAttackDefenceTree().importFromXML("C:/Users/ersagun/Desktop/ATree.xml");
			pack.getAttackDefenceTree().evaluateAttackDefenceTree();
			generateAndReplaceTree(pack.getAttackDefenceTree());
			System.out.println("tree generated");
			// import measures
			try {
				ObjectMapper mapper = new ObjectMapper();
				File f = new File("C:/Users/ersagun/Desktop/extractTS.json");
				MeasureContainer measures = mapper.readValue(f, MeasureContainer.class);
				pack.setMeasureContainer(measures);

				menuController.update(pack);
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			System.out.println("risk analysis ok");
			pack.getAttackDefenceTree().generateAttackDefenceValues();

			// Association matrix
			pack.getAssociationMatrix().importMatrixFromXLS("C:/Users/ersagun/Desktop/exemple.xls", pack);
			System.out.println("matrix ok");

			// pack.initializeAttackDefenceTreeGenerator(treePath,1,10);
			// generateAndCreateAllTabs(pack.getAttackDefenceTreeGenerator(),
			// 10);
			// showAttackDefenceTreeTabsOnScreen();
			menuController.update(pack);
		} catch (Exception e) {
			e.printStackTrace();
			Log.log(e);
			showWarning(Language.getWord(language, "attackTree.title"),
					Language.getWord(language, "ERROR.demo.import.association.matrix", "Issue with the import of the association matrix"));
			e.printStackTrace();
		}
	}

	/**
	 * Generate and replace the current tree on the screen by the Tree in param
	 * 
	 * @param attackDefenceTree
	 */
	public void generateAndReplaceTree(AttackDefenceTree attackDefenceTree) {
		TreeItem<String> treeItem = mainController.getTreeController().generateTreeItem(pack, attackDefenceTree, this.language);
		mainController.getTreeController().replaceTree(treeItem);
	}

	/**
	 * Generate and replace the current tree on the screen by the Tree in param
	 * 
	 * @param attackDefenceTree
	 */
	public void generateAndCreateAllTabs(AttackDefenceTreeGenerator attackDefenceTreeGenerator, int nbTabs) {
		mainController.getTreeController().getTabs().getTabs().clear();
		// loop for each and add in the table
		int i = 0;
		for (AttackDefenceTree adt : attackDefenceTreeGenerator.getAttackDefenceTrees()) {
			if (i < nbTabs)
				mainController.getTreeController().getTabs().getTabs()
						.add(new Tab(Language.getWord(this.language, "label.tree", new Object[] { Integer.toString(i + 1) }, "Tree " + (i + 1)),
								new TreeView<>(mainController.getTreeController().generateTreeItem(pack, adt, this.language))));
			i++;
		}
	}

	/* GETTERS AND SETTERS */

	public MainController getMainController() {
		return mainController;
	}

	public void setMainController(MainController mainController) {
		this.mainController = mainController;
	}

	public MenuController getMenuController() {
		return menuController;
	}

	public void setMenuController(MenuController menuController) {
		this.menuController = menuController;
	}

	public GridPane getIndexScreen() {
		return indexScreen;
	}

	public void setIndexScreen(GridPane indexScreen) {
		this.indexScreen = indexScreen;
	}

	public Pack getPack() {
		return pack;
	}

	public void setPack(Pack pack) {
		this.pack = pack;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public Text getCopyright() {
		return copyright;
	}

	public void setCopyright(Text copyright) {
		this.copyright = copyright;
	}

	public String getTreePath() {
		return treePath;
	}

	public void setTreePath(String treePath) {
		this.treePath = treePath;
	}

}
