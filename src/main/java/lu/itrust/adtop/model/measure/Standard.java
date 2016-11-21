package lu.itrust.adtop.model.measure;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Standard {
	@JsonProperty
	private long id;
	@JsonProperty
	private String name;
	@JsonProperty
	private List<Measure> measures = Collections.emptyList();

	@JsonIgnore
	private Map<String, Measure> mappedMeasures = Collections.emptyMap();

	@JsonCreator
	public Standard(@JsonProperty("id") long id, @JsonProperty("name") String name, @JsonProperty("measures") List<Measure> measures) {
		this.id = id;
		this.name = name;
		this.measures = measures;
	}

	@JsonCreator
	public Standard() {

	}

	/* GETTERS AND SETTERS */

	@JsonIgnore
	public double getMeasureCostWithName(String name) {
		Measure measure = getMeasureWithName(name);
		return measure == null ? 0D : measure.getCost();
	}

	@JsonIgnore
	public Measure getMeasureWithName(String name) {
		if (mappedMeasures.size() != measures.size())
			updateMappedMeasure();
		return mappedMeasures.get(name);
	}

	public boolean hasMeasure(String name) {
		if (mappedMeasures.size() != measures.size())
			updateMappedMeasure();
		return mappedMeasures.containsKey(name);
	}

	private synchronized void updateMappedMeasure() {
		if (!mappedMeasures.isEmpty())
			mappedMeasures.clear();
		mappedMeasures = measures.stream().collect(Collectors.toMap(Measure::getName, Function.identity()));
	}

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

	public List<Measure> getMeasures() {
		return measures;
	}

	public void setMeasures(List<Measure> measures) {
		this.measures = measures;
	}

}
