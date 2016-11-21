package lu.itrust.adtop.model.measure;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MeasureContainer {
	@JsonProperty
	private Long analysisId;
	@JsonProperty
	private double impact;
	@JsonProperty
	private double probability;
	@JsonProperty
	private Asset asset;
	@JsonProperty
	private Scenario scenario;
	@JsonProperty
	private List<Standard> standards;
	
	@JsonCreator
	public MeasureContainer(@JsonProperty("analysisId")Long analysisId,@JsonProperty("impact") double impact, @JsonProperty("probability")double probability, @JsonProperty("asset")Asset asset, @JsonProperty("scenario")Scenario scenario,
			@JsonProperty("standards")List<Standard> standards) {
		this.analysisId = analysisId;
		this.impact = impact;
		this.probability = probability;
		this.asset = asset;
		this.scenario = scenario;
		this.standards = standards;
	}
	
	@JsonCreator
	public MeasureContainer(){
		this.asset = new Asset();
		this.scenario = new Scenario();
		this.standards = new ArrayList<Standard>();
		
	}
	
	/* GETTERS AND SETTERS */
	
	public void setAnalysisId(Long analysisId) {
		this.analysisId = analysisId;
	}


	public double getImpact() {
		return impact;
	}


	public void setImpact(double impact) {
		this.impact = impact;
	}


	public double getProbability() {
		return probability;
	}


	public void setProbability(double probability) {
		this.probability = probability;
	}


	public Asset getAsset() {
		return asset;
	}


	public void setAsset(Asset asset) {
		this.asset = asset;
	}


	public Scenario getScenario() {
		return scenario;
	}


	public void setScenario(Scenario scenario) {
		this.scenario = scenario;
	}


	public List<Standard> getStandards() {
		return standards;
	}

	
	@JsonIgnore
	public boolean isEmpty(){
		// TODO : sdfdsf && impact && probability==null &&
		if(this.scenario.getName()==null && this.standards.isEmpty())
			return true;
		else return false;
	}


	public void setStandards(List<Standard> standards) {
		this.standards = standards;
	}	
	
	public void clear() {
		this.scenario = new Scenario();
		standards = new ArrayList<Standard>();
	}
	
	public double getALE() {
		return impact * probability;
	}

	/**
	 * @return the analysisId
	 */
	public Long getAnalysisId() {
		return analysisId;
	}
}
