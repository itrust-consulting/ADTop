package lu.itrust.adtop.utils;

import org.apache.log4j.Logger;
/**
 * Class manage Logs 
 * This singleton class return one instance of log 
 * @author ersagun
 *
 */
public class Log implements LogInterface {

	/**
	 * logger of ADTop
	 */
	private static Log logger;
	
	/**
	 * Logger of  ADTop
	 */
	private static Logger log; 
	
	private Log() {
		log = Logger.getLogger("ADTopLogger");
	}

	public static Log getInstance() {
		if (Log.logger == null) {
			synchronized (Log.class) {
				if (Log.logger == null)
					Log.logger = new Log();
			}
		}
		return Log.logger;
	}
	
	/** GETTERS AND SETTERS **/

	public void showLog(Exception e) {
		if (log.isDebugEnabled())
			e.printStackTrace();

		log.warn(" : " + e.getMessage());
	}

	public static void log(Exception e) {
		Log.getInstance().showLog(e);
	}

	public static Log getLogger() {
		return logger;
	}

	public static void setLogger(Log logger) {
		Log.logger = logger;
	}

	public static Logger getLog() {
		return log;
	}

	public static void setLog(Logger log) {
		Log.log = log;
	}

}
