package lu.itrust.adtop.model.node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import bibliothek.util.xml.XElement;
import lu.itrust.adtop.exception.ADException;
import lu.itrust.adtop.model.measure.Measure;
import lu.itrust.adtop.tools.Pack;

/**
 * Class represent a node Each children of a node can be a node because a child
 * can be attack node or a defence node
 * 
 * @author ersagun
 *
 */
public abstract class Node implements Cloneable {

	private static final long serialVersionUID = -6125112270273587747L;

	public final static Pattern SET_PATTERN = Pattern.compile("(.*)Set(.*)");

	/**
	 * Parent of the current node
	 */
	protected Node parent;

	/**
	 * children of the current node
	 */
	protected List<Node> children = Collections.emptyList();

	/**
	 * name of the current node
	 */
	protected String name;

	/**
	 * type of the current node
	 */
	protected String type;

	private boolean containsValue;

	private boolean extension = true;

	public boolean isContainsValue() {
		return containsValue;
	}

	public void setContainsValue(boolean containsValue) {
		this.containsValue = containsValue;
	}

	public Node() {
		this.name = "";
		this.type = "";
		this.parent = null;
		this.containsValue = false;
	}

	public Node(String name) {
		this.name = name;
		this.type = "";
		this.parent = null;
		this.containsValue = false;
	}

	public Node(String name, String type, boolean extension) {
		this.name = name;
		this.type = type;
		this.parent = null;
		this.containsValue = false;
		this.setExtension(extension);
	}

	/**
	 * print recursively a node
	 * 
	 * @return s
	 */
	public String toString() {
		String s = "";
		if (this.children != null) {
			for (Node n : this.children) {
				s += n.printNode() + "\n";
				n.toString();
			}
		}
		return s;
	}

	/**
	 * print a node
	 * 
	 * @return s String presentation of a node
	 */
	public String printNode() {
		String s = "";
		s += "{" + this.name + ", ";
		if (this.children != null) {
			for (Node n : this.children) {
				n.toString();
				s = s + n.getName() + ", parent: " + n.getParent().getName();
			}
		}
		s += "} \n";
		return s;
	}

	/**
	 * Check if the current node is a leaf
	 * 
	 * @return
	 */
	public final boolean isLeaf() {
		if (children == null)
			return true;
		if (children.isEmpty())
			return true;
		return false;
	}

	/**
	 * Add param like a child to current node
	 * 
	 * @param child
	 */
	public void addChild(Node child) {
		if (children == null || children.isEmpty() && !(children instanceof ArrayList))
			children = new ArrayList<>();
		children.add(child);
		child.setParent(this);
	}

	/**
	 * remove all children of current node
	 * 
	 * @param node
	 *            node to remove all child
	 */
	public final void removeAllChildren() {
		this.children.forEach(Node::removeAllChildren);
		children.clear();
	}

	/**
	 * remove children in parameter
	 * 
	 * @param child
	 *            child to remove
	 */
	public final void removeChild(final Node child) {
		if (children == null) {
			System.err.println("Tried to remove child from node with no children");
			return;
		}
		final int index = children.indexOf(child);
		if (index == -1) {
			System.err.println("Tried to remove child from that" + " is not contained in children");
			return;
		}
		final List<Node> newChildren = ((Node) child).children;
		children.remove(index);
		children.addAll(index, newChildren);
	}

	public abstract String returnTypeValue();

	/**
	 * Check if a node contains an attack node
	 * 
	 * @return
	 */
	public boolean hasAttackNode() {
		return this.getChildren().stream().anyMatch(node -> node.getType().equals("conjunctive") || node.getType().equals("disjunctive"));
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
	 * Check if a node has a measure or not
	 * 
	 * @return
	 */
	public boolean hasASet() {
		return this.getChildren().stream().anyMatch(node -> node instanceof DefenceNode && SET_PATTERN.matcher(node.getName()).matches());
	}

	/**
	 * TODO : REFACTOR
	 * 
	 * @param e
	 *            xElement to transform
	 */
	public void importAllTypeNodeFromXML(XElement e) {
		AttackNode chAttackNode = null;
		DefenceNode chDefenceNode = null;
		boolean nextIsAttackNode = true, extension = false;

		for (XElement child : e.getElements("node")) {
			if (child.getAttribute("switchRole") != null) {
				if (!this.getChildren().isEmpty()) {
					Node lastNode = this.getChildren().get(this.getChildren().size() - 1);

					if (lastNode instanceof AttackNode)
						nextIsAttackNode = false;
					else if (lastNode instanceof DefenceNode)
						nextIsAttackNode = true;
				} else {
					if (this instanceof AttackNode)
						nextIsAttackNode = false;
					else if (this instanceof DefenceNode)
						nextIsAttackNode = true;
				}
			} else if (SET_PATTERN.matcher(this.getName()).matches()) {
				nextIsAttackNode = false;
			} else
				nextIsAttackNode = this instanceof AttackNode;

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
				chAttackNode.setParent(this);
				chAttackNode.setContainsValue(successProb != 0);
				chAttackNode.setSuccessProbability(successProb);
				chAttackNode.setBasicSuccessProbability(successProb);
				chAttackNode.importAllTypeNodeFromXML(child);
				this.addChild(chAttackNode);
			} else {
				double effectiveness = 0;
				String nodeName = "";
				for (XElement xe : child.getElements("parameter")) {
					if (xe.getAttribute("domainId") != null) {
						if (xe.getAttribute("domainId").getValue().equals("ProbSucc1"))
							effectiveness = Double.parseDouble(xe.getValue());
					}
				}
				String type = "";
				if (child.getAttribute("refinement") != null)
					type = child.getAttribute("refinement").getValue();

				nodeName = child.getElement("label").getValue().replaceAll("\n", " ");
				// TODO : Generate differently
				chDefenceNode = new DefenceNode(nodeName, effectiveness, extension);

				chDefenceNode.setContainsValue(effectiveness != 0);
				chDefenceNode.setParent(this);
				chDefenceNode.setType(type);
				chDefenceNode.setTypeString("Counter");
				chDefenceNode.setBasicEffectiveness(effectiveness);
				chDefenceNode.importAllTypeNodeFromXML(child);
				this.addChild(chDefenceNode);
			}
		}
	}

	public void addToListIfDefence(List<String> defenceAlready) {
		if (this instanceof DefenceNode)
			defenceAlready.add(this.getName());
	}

	public void evaluateAllSetNode() {
		this.evaluateAllChildSetNode();
		if (this instanceof DefenceNode)
			((DefenceNode) this).evaluateSetNode();
	}

	public void evaluateAllChildSetNode() {
		this.getChildren().stream().forEach(node -> {
			node.evaluateAllChildSetNode();
			if (node instanceof DefenceNode) {
				((DefenceNode) node).evaluateSetNode();
			}
		});
	}

	public void evaluateOneSetNode() {
		this.getChildren().stream().filter(node -> node instanceof DefenceNode).map(node -> (DefenceNode) node).forEach(DefenceNode::evaluateSetNode);
	}

	public void evaluateImportedAttackDefenceTreeNodes() {
		this.evaluateImportedAttackDefenceTreeAllChildsNodes();
		this.evaluateAllTypeNode();
	}

	public void evaluateImportedAttackDefenceTreeAllChildsNodes() {
		if (this instanceof AttackNode)
			((AttackNode) this).setSuccessProbability(((AttackNode) this).getBasicSuccessProbability());

		this.getChildren().stream().forEach(node -> {
			node.evaluateImportedAttackDefenceTreeAllChildsNodes();
			node.evaluateAllTypeNode();
		});
	}

	public abstract void evaluateAllTypeNode();

	public void findRecursivelyPlaceToAddMeasure(Measure measure, String attackToTake, Pack p) {
		for (Node node : this.getChildren()) {
			node.findRecursivelyPlaceToAddMeasure(measure, attackToTake, p);
			if (node.getName().equals(attackToTake))
				node.checkPlaceAndAdd(measure, attackToTake, p);
		}
	}

	public void checkPlaceAndAdd(Measure measure, String attackToTake, Pack p) {
		if (this instanceof AttackNode) {
			if (this.hasAMeasure()) {
				Node set = this.getChildren().stream().filter(node -> node instanceof DefenceNode).findAny().orElse(null);
				if (set == null || set.children.parallelStream().anyMatch(child -> child.name.equals(measure.getName())))
					return;
				if (set.getChildren().size() > 0) {
					set.addChild(new DefenceNode(measure.getName(), p.getAssociationMatrix().giveEffectivenessByMeasureAttack(attackToTake, measure.getName()), true));
				} else {
					DefenceNode setNode = new DefenceNode("Set " + (p.getAttackDefenceTree().getSetNumber() + 1), 0, true);
					set.setParent(setNode);
					setNode.addChild(set);
					setNode.addChild(new DefenceNode(measure.getName(), p.getAssociationMatrix().giveEffectivenessByMeasureAttack(attackToTake, measure.getName()), true));
					this.removeChild(set);
					this.addChild(setNode);
					p.getAttackDefenceTree().incrementSetNumber();
				}

			} else if (!this.children.parallelStream().anyMatch(child -> child.name.equals(measure.getName()))) {
				DefenceNode dn = new DefenceNode(measure.getName(), p.getAssociationMatrix().giveEffectivenessByMeasureAttack(attackToTake, measure.getName()), true);
				dn.setParent(this);
				this.addChild(dn);
			}
		}
	}

	/* GETTERS AND SETTERS */

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public final void setName(final String n) {
		this.name = n;
	}

	public String getName() {
		return this.name;
	}

	public List<Node> getChildren() {
		return this.children;
	}

	public void setChildren(ArrayList<Node> newChildren) {
		this.children = newChildren;
	}

	public String toStringAll() {
		String a = "";
		for (Node n : this.getChildren()) {
			a += n.getName();
			a += n.toString();
		}
		return a;
	}

	public String toStringOne() {
		return this.getName();
	}

	public abstract double getSuccessValue();

	public abstract String exportOneChild();

	public abstract void setSuccessValue(double successProb);

	public abstract void setBasicSuccesValue(double successProb);
	
	public abstract String exportChildren(Map<String, Integer> measureCounters);

	/**
	 * Parent must be updated
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Node clone() {
		try {
			Node node = (Node) super.clone();
			node.parent = null; // it must be update by parent
			if (node.children != null) {
				node.children = this.children.isEmpty() ? Collections.emptyList() : new ArrayList<>(this.children.size());
				for (Node child : this.children) {
					Node childClone = child.clone();
					node.children.add(childClone);
					childClone.setParent(node);
				}
			}
			return node;
		} catch (CloneNotSupportedException e) {
			throw new ADException("ERROR.internal", "Unknown error occurred", e);
		}
	}

	/**
	 * @return the extension
	 */
	public boolean isExtension() {
		return extension;
	}

	/**
	 * @param extension
	 *            the extension to set
	 */
	protected void setExtension(boolean extension) {
		this.extension = extension;
	}

	/**
	 * @return true if himself is an extension
	 */
	public boolean reset() {
		this.children.removeIf(child -> child.reset());
		return isExtension();
	}

	public int getSetCount() {
		return (SET_PATTERN.matcher(name).matches() ? 1 : 0) + children.stream().mapToInt(Node::getSetCount).sum();
	}

	public Set<String> getMeasures() {
		Set<String> measures = children.stream().map(Node::getMeasures).flatMap(set -> set.stream()).collect(Collectors.toSet());
		if(this instanceof DefenceNode && !measures.contains(name))
			measures.add(name);
		return measures;
	}
}
