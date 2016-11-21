package lu.itrust.adtop.model.measure;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Scenario {

	@JsonProperty
	private long id;
	@JsonProperty
	private String name;

	@JsonCreator
	public Scenario(@JsonProperty("id") long id, @JsonProperty("name") String name) {
		this.id = id;
		this.name = name;
	}

	@JsonCreator
	public Scenario() {

	}
	
	/* GETTERS AND SETTERS */

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

}
