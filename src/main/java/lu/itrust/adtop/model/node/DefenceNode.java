package lu.itrust.adtop.model.node;

import java.util.Map;

/**
 * Class represent a Defence Node All measures are defence nodes
 * 
 * @author ersagun
 *
 */
public class DefenceNode extends Node {

	/**
	 * Effectiveness of a defence node which is a measure
	 */
	private double effectiveness;
	private double basicEffectiveness;
	private String typeString;

	public DefenceNode(String name, double effect, boolean extension) {
		super(name, "", extension);
		this.effectiveness = effect;
		this.typeString = "";
	}

	public DefenceNode(Node dn) {
		super(dn.getName());
		this.children = dn.getChildren();
		this.effectiveness = ((DefenceNode) dn).getEffectiveness();
		this.typeString = "";
	}

	public DefenceNode(String name, double effect, String conjuctiveDisjunctive) {
		super(name);
		this.effectiveness = effect;
		this.typeString = conjuctiveDisjunctive;
	}

	public double getSuccessValue() {
		return this.effectiveness;
	}

	/**
	 * Export children of a node in xml ADTool presentation TYPE Counter change
	 * 
	 * @return
	 */
	@Override
	public String exportChildren(Map<String, Integer> measureCounters) {
		String string = "";
		if (this.getType() != null && this.getName() != null) {
			boolean isSet = SET_PATTERN.matcher(this.getName()).matches();
			if (isSet || (this.getParent() instanceof AttackNode)) {
				string += "<node refinement=\"disjunctive\" switchRole=\"yes\">" + "\n";
			} else
				string += "<node refinement=\"disjunctive\">" + "\n";

			if (isSet)
				string += "		<label>" + this.getName().replace(",", "") + "</label>" + "\n";
			else {
				Integer index = measureCounters.get(name);
				string += "		<label>" + this.getName().replace(",", "") + (index == null ? "" : " " + (++index)) + "</label>" + "\n";
				measureCounters.put(name, index == null ? 1 : index);
			}
		}

		if (this.getChildren().isEmpty())
			string += "		<parameter domainId=\"ProbSucc2\" category=\"basic\">" + this.getEffectiveness() + "</parameter>" + "\n";
		else {
			string += "	<parameter domainId=\"ProbSucc2\" category=\"derived\">" + this.getEffectiveness() + "</parameter>" + "\n";
			for (Node node : this.getChildren())
				string += node.exportChildren(measureCounters) + "\n</node>\n";
		}
		return string;
	}

	/** TODO : LOOP calcul **/
	public void evaluateSetNode() {
		// evaluateSetNode();
		double effect = 1.0;
		if (SET_PATTERN.matcher(this.getName()).matches() || (this instanceof DefenceNode && this.hasAMeasure())) {
			for (Node node : this.getChildren()) {
				if (node instanceof DefenceNode)
					effect *= (1.0 - ((DefenceNode) node).getEffectiveness());
			}
			this.setEffectiveness(1.0 - effect);
		}
	}

	/* GETTERS AND SETTERS */

	public double getEffectiveness() {
		return effectiveness;
	}

	public void setEffectiveness(double effectiveness) {
		this.effectiveness = effectiveness;
	}

	public double getBasicEffectiveness() {
		return basicEffectiveness;
	}

	public void setBasicEffectiveness(double basicEffectiveness) {
		this.basicEffectiveness = basicEffectiveness;
	}

	public String getTypeString() {
		return typeString;
	}

	public void setTypeString(String typeString) {
		this.typeString = typeString;
	}

	public void evaluateDefenceWithAttackNode() {
		if (getChildren().stream().anyMatch(node -> node instanceof DefenceNode))
			this.evaluateOneSetNode();
		this.setEffectiveness(this.getEffectiveness() * (1 - this.getChildren().stream().filter(node -> node instanceof AttackNode).map(node -> ((AttackNode) node))
				.mapToDouble(AttackNode::getSuccessProbability).findAny().orElse(0d)));
	}

	@Override
	/** TODO : LOOP calcul **/
	public void evaluateAllTypeNode() {
		if (this instanceof DefenceNode) {
			this.evaluateSetNode();
			// if one child of a defence tree is an attack tree
			if (this.getChildren().stream().anyMatch(node -> node instanceof AttackNode)) {
				this.evaluateDefenceWithAttackNode();
			}
		}
	}

	@Override
	public String exportOneChild() {
		return null;
	}

	@Override
	public void setSuccessValue(double effect) {
		this.effectiveness = effect;

	}

	@Override
	public void setBasicSuccesValue(double effect) {
		this.effectiveness = effect;

	}

	@Override
	public String returnTypeValue() {
		return "Effectiveness";
	}

}
