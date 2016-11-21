package lu.itrust.adtop.model.measure.alt;

import java.io.FileReader;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class represent a list of measure
 * @author ersagun
 *
 */
public class Measures {

	/**
	 * scenario name of the list of measure
	 */
	private String scenarioName;
	
	/**
	 * scenario name of the list of measure
	 */
	@JsonProperty
	private Long scenarioId;
	
	/**
	 * scenario name of the list of measure
	 */
	@JsonProperty
	private	Long riskAnalysisId;
	
	@JsonProperty
	private Long assetId;
	
	
	/**
	 * impact of the list of measure
	 */
	private Double impact;
	
	/**
	 * probability of list of measure
	 */
	private Double probability;
	
	/**
	 * list of measure
	 */
	@JsonProperty
	private ArrayList<Measure> measures;
	

	@JsonCreator
	public Measures() {
		this.scenarioName = "";
		measures = new ArrayList<Measure>();
	}
	
	@JsonCreator
		public Measures(@JsonProperty("scenarioId")Long scenarioId,@JsonProperty("riskAnalysisId")Long riskAnalysisId,@JsonProperty("assetId")Long assetId) {
			this.scenarioId = scenarioId;
			this.riskAnalysisId=riskAnalysisId;
			this.assetId=assetId;
		}
		
	

	
	public ArrayList<Measure> getMeasures() {
		return measures;
	}

	public void setMeasures(ArrayList<Measure> measures) {
		this.measures = measures;
	}

	public Long getScenarioId() {
		return scenarioId;
	}


	public void setScenarioId(Long scenarioId) {
		this.scenarioId = scenarioId;
	}


	public Long getRiskAnalysisId() {
		return riskAnalysisId;
	}


	public void setRiskAnalysisId(Long riskAnalysisId) {
		this.riskAnalysisId = riskAnalysisId;
	}


	/**
	 * TODO : check corrupted file
	 * import a list of measure from JSON File
	 * @param fileName address of json file
	 */
	public void importFromJSONFile(String fileName) {
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(new FileReader(fileName));

			JSONObject jsonObject = (JSONObject) obj;

			this.impact = (Double) jsonObject.get("impact");
			this.probability = (Double) jsonObject.get("probability");
			this.riskAnalysisId = (Long) jsonObject.get("analysisId");
			JSONArray standards = (JSONArray) jsonObject.get("standards");
			JSONObject scenarioObj = (JSONObject) jsonObject.get("scenario");
			JSONObject assetObj = (JSONObject) jsonObject.get("asset");
			

			for (int i = 0; i < standards.size(); i++) {
				JSONObject st = (JSONObject) standards.get(i);
				JSONArray arrayMeasures = (JSONArray) st.get("measures");
				for (int j = 0; j < arrayMeasures.size(); j++) {
					JSONObject measure = (JSONObject) arrayMeasures.get(j);
					this.measures.add(new Measure((double) measure.get("cost"), (double) measure.get("rrf"),
							(String) measure.get("name"), (Long) measure.get("implRate"), (Long) measure.get("id")));
				}
			}
			this.scenarioId = (Long)scenarioObj.get("id");
			this.assetId = (Long)assetObj.get("id");
			this.scenarioName = getCleanStringValue(scenarioObj.get("name"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getCleanStringValue(Object o){
		return ((String)o).replaceAll("\n", " ");
	}

	
	/** GETTERS AND SETTERS **/
	
	public String getScenarioName() {
		return scenarioName;
	}

	public void setScenarioName(String scenarioName) {
		this.scenarioName = scenarioName;
	}

	public Double getImpact() {
		return impact;
	}

	public void setImpact(Double impact) {
		this.impact = impact;
	}

	public Double getProbability() {
		return probability;
	}

	public void setProbability(Double probability) {
		this.probability = probability;
	}
	
	@JsonIgnore
	public boolean isEmpty(){
		if(this.scenarioName.equals("") && impact==null && probability==null && this.measures.size()<=0)
			return true;
		else return false;
	}


	public void clear() {
		this.scenarioName = "";
		measures = new ArrayList<Measure>();
	}
}