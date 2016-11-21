package lu.itrust.adtop.model.tree;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.security.auth.Destroyable;

import org.apache.commons.math3.util.Precision;

import bibliothek.util.xml.XElement;
import bibliothek.util.xml.XIO;
import lu.itrust.adtop.exception.ADException;
import lu.itrust.adtop.model.measure.Measure;
import lu.itrust.adtop.model.node.AttackNode;
import lu.itrust.adtop.model.node.DefenceNode;
import lu.itrust.adtop.model.node.Node;
import lu.itrust.adtop.tools.Pack;

/**
 * Class represent an attack defence tree This class contains attack nodes but
 * also defence nodes
 * 
 * @author ersagun
 *
 */
public class AttackDefenceTree implements Cloneable, Destroyable {

	/**
	 * Number of set measure that attack-defence tree contains
	 */
	private int setNumber;

	private boolean imported;

	private double rosiValue;

	/**
	 * root of tree
	 */
	protected Node root;

	/**
	 * scenario name of the current tree
	 */
	protected String scenarioName;

	/**
	 * name of all subnodes of this tree
	 */
	protected Set<String> attacks;

	/**
	 * name of all subnodes of this tree
	 */
	protected Set<String> measures;

	protected boolean evaluated;

	private boolean destroyed = false;

	/**
	 * Clean the current tree
	 */
	public void clear() {
		this.scenarioName = "";
		this.root = new AttackNode();
		this.attacks = new HashSet<>();
		this.evaluated = false;
	}

	/**
	 * Fill the list value who contains all attacks of this tree
	 */
	public void generateAttackDefenceValues() {
		if (this.getRoot() instanceof AttackNode)
			this.attacks.add(this.getRoot().getName());
		this.getRoot().getChildren().stream().forEach(node -> generateForChildListValues(node));
	}

	/**
	 * Fill for each child the list value who contains all attacks of this tree
	 */
	public void generateForChildListValues(Node nodeN) {
		if (nodeN instanceof AttackNode)
			this.attacks.add(nodeN.getName());
		else if (nodeN instanceof DefenceNode)
			this.measures.add(nodeN.getName());
		nodeN.getChildren().stream().forEach(node -> generateForChildListValues(node));
	}

	/**
	 * Print a tree
	 */
	public String toString() {
		return getRoot().toString();
	}

	public AttackDefenceTree() {
		this.scenarioName = "";
		this.root = new AttackNode();
		this.attacks = new HashSet<String>();
		this.measures = new HashSet<String>();
		this.evaluated = false;
		this.setNumber = 0;
		this.rosiValue = 0;
		this.imported = false;
	}

	public AttackDefenceTree(AttackDefenceTree attackDefenceTree) {
		this.scenarioName = attackDefenceTree.getScenarioName();
		this.attacks = attackDefenceTree.getAttacks();
		this.measures = attackDefenceTree.getMeasures();
		this.evaluated = attackDefenceTree.isEvaluated();
		this.setNumber = attackDefenceTree.getSetNumber();
		this.imported = attackDefenceTree.isImported();
	}

	/**
	 * public AttackDefenceTree(AttackTree at) { this.root = at.getRoot();
	 * this.scenarioName = at.getScenarioName(); }
	 **/

	public void evaluateAttackDefenceTree() {
		this.root.evaluateImportedAttackDefenceTreeNodes();
	}

	/**
	 * Generate an attack-defence tree from an attack tree
	 */
	public void generateFromAT(Pack p) {
		reset();
		Map<String, Double> defence = p.getAssociationMatrix().get(this.getRoot().getName());
		if (defence != null) {
			// node contains a measure already and has not a set
			if (defence.size() >= 1) {
				if (this.root.hasAMeasure()) {
					// already and has not a set
					if (!this.root.hasASet()) {
						int j = 0;
						for (int i = 0; i < this.root.getChildren().size(); i++) {
							if (this.root.getChildren().get(i) instanceof DefenceNode)
								j = i;
						}
						generateSetForATAddDefenceNode(defence, this.root, ((DefenceNode) this.root.getChildren().get(j)));
						this.root.getChildren().remove(j);

						// contains already a set
					} else {

						int j = 0;
						for (int i = 0; i < this.root.getChildren().size(); i++) {
							if (this.root.getChildren().get(i) instanceof DefenceNode && Node.SET_PATTERN.matcher(this.root.getChildren().get(i).getName()).matches())
								j = i;
						}
						generateDefenceNodeForAT(defence, ((DefenceNode) this.root.getChildren().get(j)));
					}
					// node not contains a measure
				} else {
					if (defence.size() > 1) {
						generateSetForAT(defence, this.root);
					} else if (defence.size() == 1) {
						generateDefenceNodeForAT(defence, this.root);
					}
				}
			}
			generateAdtForEachChild(this.root, /* defenceAlready, */ p);
		}
	}

	private void reset() {
		this.root.reset();
		this.setNumber = this.root.getSetCount();
		this.setMeasures(this.root.getMeasures());
	}

	/**
	 * Generate recursively for each child of node in parameter the defence node
	 * associated. Method check from matrix and do the association to find
	 * measures associated
	 * 
	 * @param root
	 *            node
	 */
	public void generateAdtForEachChild(Node root, Pack p) {
		for (Node n : root.getChildren()) {
			Map<String, Double> defence = p.getAssociationMatrix().get(n.getName());
			if (defence != null) {
				// node contains a measure already and has not a set
				if (!defence.isEmpty()) {
					if (n.hasAMeasure()) {
						// already and has not a set
						if (!n.hasASet()) {
							int j = 0;
							for (int i = 0; i < n.getChildren().size(); i++) {
								if (n.getChildren().get(i) instanceof DefenceNode)
									j = i;
							}
							generateSetForATAddDefenceNode(defence, n, ((DefenceNode) n.getChildren().get(j)));
							n.getChildren().remove(j);

							// contains already a set
						} else {
							int j = 0;
							for (int i = 0; i < n.getChildren().size(); i++) {
								if (n.getChildren().get(i) instanceof DefenceNode && Node.SET_PATTERN.matcher(n.getChildren().get(i).getName()).matches())
									j = i;
							}
							generateDefenceNodeForAT(defence, ((DefenceNode) n.getChildren().get(j)));
						}
						// node not contains a measure
					} else if (defence.size() > 1)
						generateSetForAT(defence, n);
					else
						generateDefenceNodeForAT(defence, n);
				}

			}
			generateAdtForEachChild(n, p);
		}
	}

	public List<String> getAlreadyDefence() {
		List<String> defenceAlready = new ArrayList<String>();
		this.root.addToListIfDefence(defenceAlready);
		this.getAlreadyDefenceForEachChild(defenceAlready, this.root);
		return defenceAlready;
	}

	public void getAlreadyDefenceForEachChild(List<String> defenceAlready, Node nodeN) {
		nodeN.getChildren().stream().forEach(node -> {
			this.getAlreadyDefenceForEachChild(defenceAlready, node);
			node.addToListIfDefence(defenceAlready);
		});

	}

	/**
	 * Check if a defence (measure) is already added to the adt generated
	 * 
	 * @param defenceAlready
	 * @param defence
	 * @return
	 */
	public Map<String, Double> checkIfDefenceIsAlready(List<String> defenceAlready, Map<String, Double> defence) {
		Map<String, Double> toReturn = new HashMap<String, Double>();
		defence.forEach((k, v) -> {
			if (!defenceAlready.contains(k)) {
				defenceAlready.add(k);
				toReturn.put(k, v);
			}
		});
		return toReturn;
	}

	/**
	 * create and add each measure(defence) to a new set and then ad this set as
	 * a child to the node n of parameter
	 * 
	 * @param defences
	 *            defence associated to this node
	 * @param parent
	 *            property of this defence nodes
	 */
	public void generateSetForAT(Map<String, Double> defences, Node parent) {
		DefenceNode set = generateSetAT(defences, parent);
		addToChildrenDefenceNode(parent, set);
	}

	public void generateSetForATAddDefenceNode(Map<String, Double> defences, Node parent, DefenceNode existNode) {
		DefenceNode set = generateSetAT(defences, parent);
		addToChildrenDefenceNode(set, existNode);
		addToChildrenDefenceNode(parent, set);
	}

	private DefenceNode generateSetAT(Map<String, Double> defences, Node parent) {
		incrementSetNumber();
		DefenceNode set = new DefenceNode("Set " + this.setNumber, 0, true);
		set.setParent(parent);
		set.setType("disjunctive");
		set.setTypeString("Counter");
		for (Entry<String, Double> entry : defences.entrySet()) {
			String nameOfMeasure = entry.getKey().replaceAll("\n", " ");
			Double effect = entry.getValue();
			DefenceNode dn = new DefenceNode(nameOfMeasure, effect, true);
			dn.setParent(set);
			dn.setType("disjunctive");
			dn.setTypeString("Counter");
			addToChildrenDefenceNode(set, dn);
		}
		return set;
	}

	/**
	 * Get de defence from hashmap defence and create a defence node and insert
	 * this node as a child of node n (in this case number of defence should be
	 * equals to 1)
	 * 
	 * @param defence
	 *            hashmap of defence
	 * @param parent
	 *            owner of this defence
	 */
	public void generateDefenceNodeForAT(Map<String, Double> defences, Node parent) {
		defences.forEach((key, value) -> {
			DefenceNode dn = new DefenceNode(key.replaceAll("\n", " "), value, true);
			dn.setParent(parent);
			dn.setType("disjunctive");
			dn.setTypeString("Counter");
			addToChildrenDefenceNode(parent, dn);
		});
	}

	/**
	 * Add a defence node as a children to the node node in parameter
	 * 
	 * @param node
	 *            node who will accept the defence node as a child
	 * @param dn
	 *            defence node child
	 */
	public void addToChildrenDefenceNode(Node node, DefenceNode dn) {
		node.addChild(dn);
	}

	/**
	 * Evaluate success probability for the root of the AttackDefence tree
	 * 
	 * @throws TreeCalculException
	 */
	/**
	 * public void evaluateRoot() throws TreeCalculException {
	 * this.root.calculSuccessProb(); this.root.calculSuccessProbADT(); }
	 **/

	/**
	 * Check if the attack tree is empty
	 * 
	 * @return boolean
	 */
	public boolean isEmpty() {
		if (this.attacks != null && this.scenarioName != null && this.root != null) {
			if (this.scenarioName.isEmpty() && root.getName().isEmpty() && this.attacks.isEmpty())
				return true;
			else
				return false;
		} else
			return true;
	}

	/**
	 * Export attack defence tree in format XML for ADTool
	 * 
	 * @param measureCounters
	 * 
	 * @return
	 */
	public String export(Map<String, Integer> measureCounters) {
		String string = "<?xml version='1.0'?>\n" + "<adtree>\n";
		string += this.root.exportOneChild();

		for (Node node : this.root.getChildren())
			string += node.exportChildren(measureCounters) + "\n</node>\n";

		string += "</node>\n<domain id=\"ProbSucc2\">\n" + "<class>lu.uni.adtool.domains.adtpredefined.ProbSucc</class>\n" + "<tool>ADTool2</tool>\n" + "</domain>\n"
				+ "</adtree>\n";
		return string;
	}

	/**
	 * TODO : REFACTOR Generate for each xElement in param a node and add node n
	 * like a parent
	 * 
	 * @param e
	 *            xElement to convert in a node
	 * @param n
	 *            parent node of this xElement
	 */
	public void generateTreeFromXml(XElement e, Node n) {
		XElement root = e.getElement("node");
		AttackNode chAttackNode = null;
		DefenceNode chDefenceNode = null;
		boolean nextIsAttackNode = true, extension = false;

		for (XElement child : root.getElements("node")) {

			if (child.getAttribute("switchRole") != null) {
				if (!this.getRoot().getChildren().isEmpty()) {
					Node lastNode = this.getRoot().getChildren().get(this.getRoot().getChildren().size() - 1);
					if (lastNode instanceof AttackNode)
						nextIsAttackNode = false;
					else if (lastNode instanceof DefenceNode)
						nextIsAttackNode = true;
				} else {
					if (n instanceof AttackNode)
						nextIsAttackNode = false;
					else if (n instanceof DefenceNode)
						nextIsAttackNode = true;
				}
			}

			/** TODO : Create new one **/
			if (nextIsAttackNode) {
				double successProb = 0;
				String nodeName = "";
				for (XElement xe : child.getElements("parameter")) {
					if (xe.getAttribute("domainId") != null) {
						if (xe.getAttribute("domainId").getValue().equals("ProbSucc1"))
							successProb = Double.parseDouble(xe.getValue());
					}
				}
				String type = "";
				if (child.getAttribute("refinement") != null)
					type = child.getAttribute("refinement").getValue();

				nodeName = child.getElement("label").getValue().replaceAll("\n", " ");
				// TODO : Generate differently
				chAttackNode = new AttackNode(nodeName, type, extension);
				chAttackNode.setParent(n);
				chAttackNode.setContainsValue(successProb != 0);
				chAttackNode.setSuccessProbability(successProb);
				chAttackNode.setBasicSuccessProbability(successProb);
				this.attacks.add(nodeName);
				chAttackNode.importAllTypeNodeFromXML(child);
				this.root.addChild(chAttackNode);
			} else {
				double effectiveness = 0;
				String nodeName = "";
				for (XElement xe : child.getElements("parameter")) {
					if (xe.getAttribute("domainId") != null) {
						if (xe.getAttribute("domainId").getValue().equals("ProbSucc1")) {
							effectiveness = Double.parseDouble(xe.getValue());
						}
					}
				}
				String type = "";
				if (child.getAttribute("refinement") != null)
					type = child.getAttribute("refinement").getValue();

				nodeName = child.getElement("label").getValue().replaceAll("\n", " ");
				// TODO : Generate differently
				// chAttackNode = new AttackNode(nodeName, type);
				chDefenceNode = new DefenceNode(nodeName, effectiveness, extension);
				chDefenceNode.setContainsValue(effectiveness != 0);
				chDefenceNode.setParent(n);
				chDefenceNode.setType(type);
				chDefenceNode.setTypeString("Counter");
				chDefenceNode.setBasicEffectiveness(effectiveness);
				this.attacks.add(nodeName);
				chDefenceNode.toString();
				chDefenceNode.importAllTypeNodeFromXML(child);
				this.root.addChild(chDefenceNode);
			}

		}
	}

	/**
	 * Import and attack tree from XML file in address fileName TODO : Check
	 * corrupted file and REFACTOR
	 * 
	 * @param fileName
	 *            address of xml file
	 * @throws IOException
	 *             if input output exception
	 * @throws XMLFileCorruptedException
	 *             if xml file corrupted
	 */
	public void importFromXML(String fileName) throws IOException {

		InputStream fileStream = null;
		BufferedInputStream in = null;
		XElement element = null;

		try {
			fileStream = new FileInputStream(new File(fileName));
			in = new BufferedInputStream(fileStream);
			element = XIO.readUTF(in);
			XElement node = element.getElement("node");
			this.scenarioName = node.getElement("label").getValue().replaceAll("\n", " ");
			String type = "";
			if (node.getAttribute("refinement") != null)
				type = node.getAttribute("refinement").getValue();
			this.root = new AttackNode(scenarioName, type, false);
			double successProb = 0;
			if (node.getElements("parameter") != null) {
				for (XElement xe : node.getElements("parameter")) {
					if (xe.getAttribute("domainId") != null) {
						if (xe.getAttribute("domainId").getValue().equals("ProbSucc1"))
							successProb = Double.parseDouble(xe.getValue());
						this.root.setContainsValue(true);
					}
				}
			}
			this.root.setSuccessValue(successProb);
			this.root.setBasicSuccesValue(successProb);
			generateTreeFromXml(element, this.root);
		} catch (IOException ioexception) {
			throw new ADException("ERROR.attack.tree.import.corrected", String.format("\nAttack tree file corruption: %s", ioexception.getMessage()), ioexception.getMessage(),
					ioexception);
		} catch (NullPointerException ioexception) {
			throw new ADException("ERROR.attack_tree.import.bad.file", "Attack tree file corruption the content is modified.");
		} finally {
			if (in != null)
				in.close();
			if (fileStream != null)
				fileStream.close();
		}
	}

	public boolean isImported() {
		return imported;
	}

	public void setImported(boolean imported) {
		this.imported = imported;
	}

	public void evaluateAllCounterMeasures() {
		this.root.evaluateAllSetNode();
	}

	public double getCostTotal(Pack p) {
		double costTotal = 0;
		for (String measure : this.getMeasures()) {
			if (!p.getMeasureContainer().isEmpty())
				costTotal += p.getMeasureContainer().getStandards().get(0).getMeasureCostWithName(measure);
		}
		return Precision.round(costTotal, 2);
	}

	// check
	public double calculSetRosiValue(Pack p) {
		Double optVal = p.getMeasureContainer().getImpact() * this.getRoot().getSuccessValue() + getCostTotal(p);
		this.rosiValue = Precision.round(optVal, 3);
		return this.rosiValue;
	}

	public void addDefenceToAttackDefenceTree(Measure measure, String attackToTake, Pack p) {
		this.getRoot().findRecursivelyPlaceToAddMeasure(measure, attackToTake, p);
		if (this.getRoot().getName().equals(attackToTake))
			this.getRoot().checkPlaceAndAdd(measure, attackToTake, p);
	}

	public synchronized void incrementSetNumber() {
		setNumber++;
	}

	/* GETTERS AND SETTERS */

	public Set<String> getMeasures() {
		return measures;
	}

	public void setMeasures(Set<String> measures) {
		this.measures = measures;
	}

	public Set<String> getAttacks() {
		return attacks;
	}

	public void setAttacks(Set<String> attacks) {
		this.attacks = attacks;
	}

	public int getSetNumber() {
		return setNumber;
	}

	public void setSetNumber(int setNumber) {
		this.setNumber = setNumber;
	}

	public Set<String> getListValues() {
		return attacks;
	}

	public void setListValues(Set<String> listValues) {
		this.attacks = listValues;
	}

	public Node getRoot() {
		return root;
	}

	public void setRoot(Node root) {
		this.root = root;
	}

	public String getScenarioName() {
		return scenarioName;
	}

	public void setScenarioName(String scenarioName) {
		this.scenarioName = scenarioName;
	}

	public boolean isEvaluated() {
		return evaluated;
	}

	public void setEvaluated(boolean evaluated) {
		this.evaluated = evaluated;
	}

	public double getRosiValue() {
		return rosiValue;
	}

	public void setRosiValue(double rosiValue) {
		this.rosiValue = rosiValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public AttackDefenceTree clone() {
		AttackDefenceTree attackDefenceTree;
		try {
			attackDefenceTree = (AttackDefenceTree) super.clone();
			if (this.root != null)
				attackDefenceTree.root = this.root.clone();
			if (measures != null)
				attackDefenceTree.measures = measures.isEmpty() ? new HashSet<>() : new HashSet<>(measures);

			if (attacks != null)
				attackDefenceTree.attacks = attacks.isEmpty() ? new HashSet<>() : new HashSet<>(attacks);

			return attackDefenceTree;
		} catch (CloneNotSupportedException e) {
			throw new ADException("ERROR.internal", "Unknown error occurred", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.security.auth.Destroyable#destroy()
	 */
	@Override
	public synchronized void destroy() {
		if (isDestroyed())
			return;
		this.destroyed = true;
		
		if (this.root != null) {
			this.root.removeAllChildren();
			this.root = null;
		}

		if (measures != null) {
			measures.clear();
			measures = null;
		}

		if (attacks != null) {
			attacks.clear();
			attacks = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.security.auth.Destroyable#isDestroyed()
	 */
	@Override
	public boolean isDestroyed() {
		return destroyed;
	}

}
