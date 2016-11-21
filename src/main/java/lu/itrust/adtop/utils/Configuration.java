package lu.itrust.adtop.utils;
/**
 * Configuration contains a config properties 
 * Singleton class, only one instance of configuration is returned
 * @author ersagun
 *
 */
public class Configuration {

	/**
	 * configuration singleton object
	 */
	private static Configuration config;
	
	/**
	 * properties from properties file
	 */
	private ConfigProperties configProperties;

	private Configuration() {
		this.configProperties = new ConfigProperties();
	}

	/**
	 * Return instance of the object
	 * @param address
	 * @return
	 */
	public static Configuration getInstance() {
		if (Configuration.config == null) {
			synchronized (Configuration.class) {
				if (Configuration.config == null)
					Configuration.config = new Configuration();
			}
		}
		return Configuration.config;
	}

	/** GETTERS AND SETTERS **/
	
	public static Configuration getConfig() {
		return config;
	}

	public static void setConfig(Configuration config) {
		Configuration.config = config;
	}

	public ConfigProperties getConfigProperties() {
		return this.configProperties;
	}

	public void setConfigProperties(ConfigProperties configProperties) {
		this.configProperties = configProperties;
	}
}
