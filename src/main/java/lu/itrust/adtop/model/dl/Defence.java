package lu.itrust.adtop.model.dl;

import java.util.ArrayList;

/**
 * A defence in a dl file
 * @author ersagun
 *
 */
public class Defence {

	/**
	 * Id of the defence
	 */
	private long id;
	
	/**
	 * Name of the defence
	 */
	private String name;
	
	/**
	 * Cost of attack
	 */
	private double cost;
	
	/**
	 * List of attack secured thanks to this defence
	 */
	private ArrayList<Attack> attackList;
	
	
	public Defence(long id,String name, double cost){
		this.id=id;
		this.name=name;
		this.cost=cost;
		this.attackList=new ArrayList<Attack>();
	}

	/* GETTERS AND SETTERS */
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public ArrayList<Attack> getAttackList() {
		return attackList;
	}

	public void setAttackList(ArrayList<Attack> attackList) {
		this.attackList = attackList;
	}
	
	
	

}
