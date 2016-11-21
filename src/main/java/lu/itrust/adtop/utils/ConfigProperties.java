package lu.itrust.adtop.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
/**
 * Class contains a config file content
 * @author ersagun
 *
 */
public class ConfigProperties {

	/**
	 * Path of itrust icon
	 */
	private String itrustIcon;
	
	/**
	 * path of fxml folder
	 */
	private String fxml;
	
	/**
	 * path of utils folder
	 */
	private String utils;
	
	private String getStarted;

	/**
	 * Open config file from address adr
	 * and create a config properties
	 * @param adr
	 */
	public ConfigProperties() {
		try {
			InputStream is = getClass().getResourceAsStream("/config.properties");
			Properties props = new Properties();
			props.load(is);
			is.close();
			this.itrustIcon = props.getProperty("itrustIcon");
			this.fxml = props.getProperty("fxml");
			this.utils = props.getProperty("utils");
			this.getStarted=props.getProperty("getStarted");
		} catch (IOException ioe) {
			// TODO : handle exception
		}

	}

	public String getGetStarted() {
		return getStarted;
	}

	public void setGetStarted(String getStarted) {
		this.getStarted = getStarted;
	}

	/** GETTERS AND SETTERS **/
	
	public String getItrustIcon() {
		return itrustIcon;
	}

	public void setItrustIcon(String itrustIcon) {
		this.itrustIcon = itrustIcon;
	}

	public String getFxml() {
		return fxml;
	}

	public void setFxml(String fxml) {
		this.fxml = fxml;
	}

	public String getUtils() {
		return utils;
	}

	public void setUtils(String utils) {
		this.utils = utils;
	}


}
