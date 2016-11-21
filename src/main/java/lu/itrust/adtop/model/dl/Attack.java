package lu.itrust.adtop.model.dl;

/**
 * An attack in a dl file
 * @author ersagun
 *
 */
public class Attack {

	/**
	 * Name of attack
	 */
	private String name;
	
	/**
	 * Success probability of the attack
	 */
	private double successProbability;
	
	/**
	 * the name of the attached measure of attack
	 */
	private String measureAttached;
	
	public Attack(String name,double successProbability,String measureAttached){
		this.name=name;
		this.successProbability=successProbability;
		this.measureAttached=measureAttached;
	}

	
	/* GETTERS AND SETTERS */
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getEffectiveness() {
		return successProbability;
	}

	public void setEffectiveness(double effectiveness) {
		this.successProbability = effectiveness;
	}

	public String getMeasureAttached() {
		return measureAttached;
	}

	public void setMeasureAttached(String measureAttached) {
		this.measureAttached = measureAttached;
	}

	public double getSuccessProbability() {
		return successProbability;
	}

	public void setSuccessProbability(double successProbability) {
		this.successProbability = successProbability;
	}
	
	
}
