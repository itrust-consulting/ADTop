package lu.itrust.adtop.model.tree;

import lu.itrust.adtop.model.node.AttackNode;
import lu.itrust.adtop.model.node.DefenceNode;

public class AttackDefenceTreeCreator {

	private AttackDefenceTree adt;
	
	
	public AttackDefenceTreeCreator(){
		this.adt=new AttackDefenceTree();
	}


	public AttackDefenceTree getAdt() {
		return adt;
	}


	public void setAdt(AttackDefenceTree adt) {
		this.adt = adt;
	}
	
	public void addARoot(String name, String type, double effect, String conjuctiveDisjunctive){
		if(type.equals("Attack Node"))
			this.adt.setRoot(new AttackNode(name,effect,conjuctiveDisjunctive, true));
		else if (type.equals("Defence Node"))
			this.adt.setRoot(new DefenceNode(name,effect,conjuctiveDisjunctive));	
	}
	
	public void addToThisChildren(){
		
	}
}
