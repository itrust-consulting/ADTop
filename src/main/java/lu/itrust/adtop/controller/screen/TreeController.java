package lu.itrust.adtop.controller.screen;

import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import lu.itrust.adtop.controller.Controller;
import lu.itrust.adtop.model.node.DefenceNode;
import lu.itrust.adtop.model.node.Node;
import lu.itrust.adtop.model.tree.AttackDefenceTree;
import lu.itrust.adtop.tools.Pack;
import lu.itrust.adtop.utils.Language;

/***
 * Manage tree screen In this class we can create a tree item thanks to an
 * attack tree or an attack defence tree and we can add this tree item to
 * current treeview.
 * 
 * @author ersagun
 *
 */
public class TreeController extends Controller {

	/**
	 * Java FX presentation of the tree. This object contains the tree item
	 * version of the current tree.
	 */
	@FXML
	private TreeView<String> treeView;

	/**
	 * Root node of the ADT file in treeItem. TreeView can get the root tree
	 * 
	 */
	private TreeItem<String> rootADT = null;

	/**
	 * Tree screen, this screen contains tree generated
	 */
	@FXML
	private Pane treeScreen;

	/**
	 * Tabs of the tree screen. All generated trees are stored in this object
	 */
	@FXML
	TabPane tabs;

	/**
	 * This method allow to disable tree screen
	 */
	@Override
	public void disableScreen() {
		setVisibleScreen(treeScreen, false);
	}

	/**
	 * This method replace the current node of screen by the tree item in
	 * parameter
	 * 
	 * @param treeItem
	 *            replace by this tree item
	 */

	public void replaceTree(TreeItem<String> treeItem) {
		this.rootADT = treeItem;
		this.treeView.setRoot(this.rootADT);
	}

	/**
	 * Generate a treeItem for the node in parameter and return tree generated
	 * in format tree item this tree has presentation like :
	 * name:type:effectiveness/success/:
	 * 
	 * @param nodeN
	 *            Node to generate in tree item
	 * @return root tree item of node
	 */
	public TreeItem<String> generateTreeItem(Pack p, AttackDefenceTree adt, String languages) {
		TreeItem<String> root;
		Text rootText;
		if (p.getAttackDefenceTreeGenerator() != null){
			rootText = new Text(adt.getRoot().getName() + printType(adt.getRoot()) + Language.getWord(languages, adt.getRoot().returnTypeValue()) + " : "
					+ adt.getRoot().getSuccessValue() + ", " + Language.getWord(languages, "cost") + " : " + (int)adt.getCostTotal(p) + ", ROSI : "
					+ (int)(p.getALE() - adt.getRosiValue()) + ", " + Language.getWord(languages, "impact") + " : " + (int)printImpact(p));
		}
		else
			rootText = new Text(
					adt.getRoot().getName() + printType(adt.getRoot()) + Language.getWord(languages, adt.getRoot().returnTypeValue()) + " : " + adt.getRoot().getSuccessValue());

		rootText.setFont(new Font("Courrier", 16));
		root = new TreeItem<String>("", rootText);

		root.setExpanded(true);
		buildChildren(adt.getRoot(), root, p, languages);
		return root;
	}

	/**
	 * Recursively create tree item for node in parameter then add it to tree
	 * item in parameter this tree has presentation like :
	 * name:type:effectiveness/success/:
	 * 
	 * @param n
	 *            node to convert
	 * @param tw
	 *            tree item parent
	 */
	public void tree_recursive(Node n, TreeItem<String> tw, Pack p, String languages) {
		TreeItem<String> root;
		if (n instanceof DefenceNode) {
			Text defenceDescription = new Text(n.getName() + printType(n) + Language.getWord(languages, n.returnTypeValue()) + " : " + n.getSuccessValue() + " "
					+ Language.getWord(languages, "cost") + " : " + printCost(n, p));
			defenceDescription.setFont(new Font("Courrier", 14));
			defenceDescription.setFill(Color.GREEN);
			root = new TreeItem<String>("", defenceDescription);
		} else {
			Text attackDescription = new Text(n.getName() + printType(n) + Language.getWord(languages, n.returnTypeValue()) + " : " + n.getSuccessValue());
			attackDescription.setFont(new Font("Courrier", 12));
			attackDescription.setFill(Color.RED);
			root = new TreeItem<String>("", attackDescription);
		}
		root.setExpanded(true);
		buildChildren(n, root, p, languages);
		tw.getChildren().add(root);
	}

	private String printType(Node n) {
		return n.getType().isEmpty() ? ", " : ", " + n.getType() + ", ";
	}

	/**
	 * Build all children of node in parameter and call for each child tree
	 * recursive. Tree recursive create for each node a tree item in this
	 * presentation : name:type:effectiveness/success/:
	 * 
	 * @param n
	 *            node to convert
	 * @param root
	 *            tree item parent
	 */
	public void buildChildren(Node n, TreeItem<String> root, Pack p, String languages) {
		n.getChildren().stream().forEach(node -> tree_recursive(node, root, p, languages));
	}

	/**
	 * If Trick service risk analysis is charged, method return the cost of the
	 * node n else it return 0.
	 * 
	 * @param n
	 * @param p
	 * @return
	 */
	public double printCost(Node n, Pack p) {
		if (!p.getMeasureContainer().isEmpty())
			return p.getMeasureContainer().getStandards().get(0).getMeasureCostWithName(n.getName());
		else
			return 0D;
	}

	/**
	 * If TRICK Service risk analysis file is imported, method return the impact
	 * from TRICK Service file, else it return 0.
	 * 
	 * @param p
	 * @return
	 */
	public double printImpact(Pack p) {
		if (!p.getMeasureContainer().isEmpty())
			return p.getMeasureContainer().getImpact();
		else
			return 0D;
	}

	/* GETTERS AND SETTERS */

	public TreeItem<String> getRootADT() {
		return rootADT;
	}

	public void setRootADT(TreeItem<String> rootADT) {
		this.rootADT = rootADT;
	}

	public Pane getTreeScreen() {
		return treeScreen;
	}

	public void setTreeScreen(Pane treeScreen) {
		this.treeScreen = treeScreen;
	}

	public TreeView<String> getTreeView() {
		return treeView;
	}

	public void setTreeView(TreeView<String> treeView) {
		this.treeView = treeView;
	}

	public TabPane getTabs() {
		return tabs;
	}

	public void setTabs(TabPane tabs) {
		this.tabs = tabs;
	}

}
