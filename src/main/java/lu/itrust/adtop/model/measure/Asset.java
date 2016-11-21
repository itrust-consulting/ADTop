package lu.itrust.adtop.model.measure;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Asset {

	@JsonProperty
	private long id;
	@JsonProperty
	private String name;
	@JsonProperty
	private double value;
	
	@JsonCreator
	public Asset(@JsonProperty("id")long id, @JsonProperty("name")String name, @JsonProperty("value")double value) {
		this.id = id;
		this.name = name;
		this.value = value;
	}
	
	@JsonCreator
	public Asset(){
		
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


	public double getValue() {
		return value;
	}


	public void setValue(double value) {
		this.value = value;
	}
	
	
}
