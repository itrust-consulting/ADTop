package lu.itrust.adtop.utils;
/**
 * Class singleton Languages 
 * contains one instance of the language
 * @author ersagun
 *
 */
public class Language {
	/**
	 * languages the singleton object
	 */
	private static Language language;
	
	/**
	 * Current language 
	 */
	private LanguageContainer languageContainer;

	
	private Language(String lan){
		this.languageContainer=new LanguageContainer(lan);
	}
	
	
	/**
	 * Return the last instance of the singleton object
	 * @param lan
	 * @return
	 */
	public static Language getInstance(String lan) {
		if (Language.language== null || !language.getLanguageContainer().getLanguage().equals(lan)) {
			synchronized (Language.class) {
				if (Language.language == null || !language.getLanguageContainer().getLanguage().equals(lan)){
					Language.language= new Language(lan);
				}
			}
		}
		return Language.language;
	}

	/** GETTERS AND SETTERS **/
	
	public static String getWord(String lan,String keyword) {
		return Language.getInstance(lan).getLanguageContainer().getString(keyword);
	}
	
	public static String getWord(String lan,String keyword, Object[] params) {
		return Language.getInstance(lan).getLanguageContainer().getString(keyword, params);
	}
	
	public static String getWord(String lan,String keyword, String message) {
		return Language.getInstance(lan).getLanguageContainer().getString(keyword, null,message);
	}
	
	public static String getWord(String lan,String keyword, Object[] params , String message) {
		return Language.getInstance(lan).getLanguageContainer().getString(keyword, params,message);
	}
	

	public static Language getLanguages() {
		return language;
	}

	public static void setLanguages(Language languages) {
		Language.language = languages;
	}

	public LanguageContainer getLanguageContainer() {
		return languageContainer;
	}

	public void setLanguageContainer(LanguageContainer language) {
		this.languageContainer = language;
	}
}