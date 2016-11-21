package lu.itrust.adtop.model.node;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bibliothek.util.xml.XElement;
import lu.itrust.adtop.exception.ADException;

/**
 * Class represent an attack node
 * 
 * @author ersagun
 *
 */
public class AttackNode extends Node implements Serializable, Cloneable {

	private static final long serialVersionUID = 6268185063097632465L;

	/**
	 * Success probability of an attack node
	 */
	private double successProbability;

	/**
	 * Success probability before evaluation
	 */
	private double basicSuccessProbability;

	public AttackNode() {
	}

	public AttackNode(String name) {
		super(name);
	}
	
	public AttackNode(Node root) {
		super(root.getName(), root.getType(), true);
	}

	public AttackNode(String name, String type, boolean extension) {
		super(name, type, extension);
	}

	

	public AttackNode(String name, double effect, String conjuctiveDisjunctive, boolean extension) {
		super(name, conjuctiveDisjunctive, extension);
		this.successProbability = effect;
	}

	/**
	 * TODO : REFACTOR
	 * 
	 * @param e
	 *            xElement to transform
	 */
	public void importAttackNodeFromXML(XElement e) {
		for (XElement child : e.getElements("node")) {
			String n = child.getElement("label").getValue().trim().replaceAll("\n", " ");
			double successProb = 0;

			/** check **/
			if (child.getElements("parameter") != null) {
				for (XElement xe : child.getElements("parameter")) {
					if (xe.getAttribute("domainId").getValue().equals("ProbSucc1"))
						successProb = Double.parseDouble(xe.getValue());
				}
			}
			String type = "";
			if (child.getAttribute("refinement") != null)
				type = child.getAttribute("refinement").getValue();

			AttackNode ch = new AttackNode(n, type, false);
			ch.setParent(this);
			ch.successProbability = successProb;
			ch.basicSuccessProbability = successProb;
			ch.importAttackNodeFromXML(child);
			this.addChild(ch);
		}
	}

	public double getSuccessValue() {
		return this.successProbability;
	}

	/**
	 * Calcul recursively the success probability of each node method change the
	 * calcul for each different node type
	 * 
	 * @throws TreeCalculException
	 */
	public void calculSuccessProb() {
		if (this.getChildren().size() > 1 || this.getChildren().size() == 1 && !this.hasAMeasure()) {
			if (this.type.equals("conjunctive"))
				((AttackNode) this).setSuccessProbability(calculConjunctive());
			else if (this.type.equals("disjunctive"))
				((AttackNode) this).setSuccessProbability(calculDisjunctive());
		}
	}

	/**
	 * Do a calcul if node is disjunctive, calcul: val*(1-this.successprob)
	 * 
	 * @return
	 * @throws TreeCalculException
	 */
	public double calculDisjunctive() {
		double value = 1;
		for (Node child : this.getChildren())
			if (child instanceof AttackNode) {
				value *= (1 - ((AttackNode) child).getSuccessProbability());
			}
		value = 1 - value;

		if (value > 1) {
			throw new ADException("ERROR.tree.compute.bad.value", "Your tree calcul is not correct each value of a node shoul be greater than 0 and smaller than 1");
		}
		return value;
	}

	/**
	 * Do a calcul if node is disjunctive, calcul: val*this.successprob
	 * 
	 * @return
	 * @throws TreeCalculException
	 */
	public double calculConjunctive() {

		double value = 1;
		for (Node child : this.getChildren()) {
			if (child instanceof AttackNode) {
				value *= ((AttackNode) child).getSuccessProbability();
			}
		}
		if (value > 1) {
			throw new ADException("ERROR.tree.compute.bad.value", "Your tree calcul is not correct each value of a node shoul be greater than 0 and smaller than 1");
		}

		return value;

	}

	/**
	 * Check if a node has a measure or not
	 * 
	 * @return
	 */
	public boolean hasAMeasure() {
		return this.getChildren().stream().anyMatch(node -> node instanceof DefenceNode);

	}

	/**
	 * Method check if the success probability of the childs of an attack Node
	 * are not zero
	 * 
	 * @return notZero boolean true if childs not zero
	 */
	public boolean childsNotZero() {
		boolean notZero = false;

		if (!this.getChildren().isEmpty()) {
			notZero = true;
			if (this.getChildren().stream().anyMatch(child -> child instanceof AttackNode && (((AttackNode) child).getSuccessProbability() == 0)))
				notZero = false;
		}
		return notZero;
	}

	/**
	 * Evaluate the possibility that the node can be calculable or not method
	 * evaluate if the node is a leaf or if the success probability of the
	 * current one is zero and the childs are not zero
	 * 
	 * @return node is possible to evalute is true
	 */
	public boolean evalPossibility() {
		boolean isAleaf = false;
		boolean possible = false;

		isAleaf = this.getChildren().stream().anyMatch(child -> !child.isLeaf());

		if (this.childsNotZero())
			possible = true;

		return possible || isAleaf;
	}

	/**
	 * Calcul for an attack defence tree recursively the success probability of
	 * each node method change the calcul for each different node type find the
	 * measure attached to the current node and do the calcul Check again number
	 * of measure it should not be more than 1 TODO : refactor
	 */
	public void calculSuccessProbADTImported() {

		// If node contains one defence node
		if (this instanceof AttackNode && this.getChildren().stream().anyMatch(node -> node instanceof DefenceNode)) {
			double value = 0;
			double effect = 1;
			int i = 0;

			try {
				this.calculSuccessProb();
			} catch (ADException e) {
				e.printStackTrace();
			}

			for (Node child : this.getChildren()) {
				if (child instanceof DefenceNode) {
					effect = ((DefenceNode) child).getEffectiveness();
					i++;
				}

			}

			if (i > 1)
				/** TODO: More node generated **/

				System.err.println("More than one measure");

			value = ((AttackNode) this).getSuccessProbability() * (1 - effect);
			((AttackNode) this).setSuccessProbability(value);

			// if doesn't contains defence node
		} else if (this instanceof AttackNode && !this.getChildren().isEmpty()) {
			int i = 0;

			try {
				((AttackNode) this).calculSuccessProb();

			} catch (ADException e) {
				e.printStackTrace();
			}
			if (i > 1)
				/** TODO: More node generated **/
				System.err.println("More than one measure");
		}

	}

	/**
	 * Check if a node contains an attack node
	 * 
	 * @return
	 */
	public boolean hasAttackNode() {
		return this.getChildren().stream().anyMatch(node -> node instanceof AttackNode);
	}

	/**
	 * Evalute the possibility that an attack node in a attack defence tree can
	 * be calculable or not. If an attackNode has a child who has a type called
	 * Counter calcul can be possible TODO :
	 * 
	 * @return containMeasure if the node contain measure is true
	 */
	public boolean evalPossibilityADT() {
		return this.getChildren().stream().anyMatch(node -> node instanceof DefenceNode);
	}

	/**
	 * Export all children of current node in a format ADTool XML
	 * 
	 * @return
	 */
	@Override
	public String exportChildren(Map<String, Integer> measureCounters) {
		String string = "";
		if (this.getType() != null && this.getName() != null) {
			
			if (this.getParent() instanceof DefenceNode)
				string += "<node refinement=\"" + this.getType() + "\"  switchRole=\"yes\" >" + "\n";
			 else
				string += "<node refinement=\"" + this.getType() + "\">" + "\n";
			
			string += "		<label>" + this.getName().replace(",", "") + "</label>" + "\n";
		}

		string += "		<parameter domainId=\"ProbSucc2\" category=\"basic\">" + this.getBasicSuccessProbability() + "</parameter>" + "\n";
		
		for (Node node : this.getChildren())
				string += "		" +  node.exportChildren(measureCounters)+ "\n</node>\n";
		return string;
	}

	/**
	 * Export ADTool XML presentation of one node
	 * 
	 * @return
	 */
	public String exportOneChild() {
		String string = "";
		if (this.getType() != null && this.getName() != null)
			string += "<node refinement=\"" + this.getType() + "\">" + "\n		<label>" + this.getName().replace(",", "") + "</label>" + "\n";
		if (this.getChildren().isEmpty())
			string += "		<parameter domainId=\"ProbSucc2\" category=\"basic\">" + this.getSuccessProbability() + "</parameter>" + "\n";
		else
			string += "	<parameter domainId=\"ProbSucc2\" category=\"derived\">" + this.getSuccessProbability() + "</parameter>" + "\n";
		return string;
	}

	/* GETTERS AND SETTERS */

	public double getSuccessProbability() {
		return successProbability;
	}

	public void setSuccessProbability(double successProbability) {
		this.successProbability = successProbability;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public double getBasicSuccessProbability() {
		return basicSuccessProbability;
	}

	public void setBasicSuccessProbability(double basicSuccessProbability) {
		this.basicSuccessProbability = basicSuccessProbability;
	}

	public List<Node> getChildren() {
		return this.children;
	}

	public void setChildren(ArrayList<Node> newChildren) {
		this.children = newChildren;
	}

	@Override
	/** TODO : LOOP calcul **/
	public void evaluateAllTypeNode() {
		this.calculSuccessProbADTImported();
	}

	@Override
	public void setSuccessValue(double successProb) {
		this.successProbability = successProb;

	}

	@Override
	public void setBasicSuccesValue(double successProb) {
		basicSuccessProbability = successProb;
	}

	@Override
	public String returnTypeValue() {
		return "Success_Probability";
	}

}
