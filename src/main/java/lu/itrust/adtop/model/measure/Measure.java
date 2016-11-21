package lu.itrust.adtop.model.measure;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class represent a Measure from TRICK Service JSON file
 * @author ersagun
 *
 */
public class Measure {

	/**
	 * cost of measure
	 */
	@JsonProperty
	private double cost;
	
	/**
	 * efffectiveness of measure
	 */
	@JsonIgnore
	private double effectiveness;
	
	/**
	 * Name of measure
	 */
	@JsonProperty
	private String name;
	
	/**
	 * implRate of measure
	 */
	@JsonProperty
	private double implRate;
	
	@JsonProperty
	@JsonInclude(value=Include.NON_NULL)
	private Double optimal;
	
	/**
	 * id of measure
	 */
	@JsonProperty
	private Long id;

	@JsonCreator
	public Measure(@JsonProperty("cost") double cost,  @JsonProperty("name")String name,@JsonProperty("implRate") double implRate, @JsonProperty("id")Long id) {
		this.cost = cost;
		this.name = name;
		this.implRate = implRate;
		this.id = id;
	}

	
	public Measure(@JsonProperty("id")Long id){
		this.id = id;
	}
	
	@JsonCreator
	public Measure(){
		
	}

	/**
	 * Print a measure
	 */
	public String toString() {
		return "Measure: " + this.cost + " , " + this.effectiveness + " , " + this.name + " , " + this.implRate + " , " + id;
	}

	/* GETTERS AND SETTERS */
	
	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public double getRrf() {
		return effectiveness;
	}

	public void setRrf(double rrf) {
		this.effectiveness = rrf;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getImplRate() {
		return implRate;
	}

	public void setImplRate(double implRate) {
		this.implRate = implRate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public double getEffectiveness() {
		return effectiveness;
	}

	public void setEffectiveness(double effectiveness) {
		this.effectiveness = effectiveness;
	}


	/**
	 * @return the optimal
	 */
	public Double getOptimal() {
		return optimal;
	}


	/**
	 * @param optimal the optimal to set
	 */
	public void setOptimal(Double optimal) {
		this.optimal = optimal;
	}
}