package lu.itrust.adtop.utils;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Class manage languages
 * 
 * @author ersagun
 *
 */
public class LanguageContainer {

	private static final String FRENCH = "French";

	private static final String DEFAULT_LANGUAGE = "English";

	/**
	 * Supported languages
	 */
	@SuppressWarnings("rawtypes")
	private Map supportedLanguages;

	/**
	 * Translation
	 */
	private ResourceBundle translation;

	/*
	 * Current language
	 */
	private String language;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public LanguageContainer(String language) {
		this.supportedLanguages = new HashMap();
		this.supportedLanguages.put(DEFAULT_LANGUAGE, Locale.ENGLISH);
		this.supportedLanguages.put(FRENCH, Locale.FRENCH);
		this.language = language == null ? DEFAULT_LANGUAGE : language;
		this.translation = ResourceBundle.getBundle("language", (Locale) this.supportedLanguages.get(language));
	}

	/** GETTERS AND SETTERS **/

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getString(String keyword) {
		try {
			return translation.getString(keyword);
		} catch (MissingResourceException e) {
			if (!DEFAULT_LANGUAGE.equals(language))
				return Language.getWord(DEFAULT_LANGUAGE, keyword);
			throw e;
		}
	}

	public String getString(String key, Object[] params) {
		try {
			return MessageFormat.format(translation.getString(key), params);
		} catch (MissingResourceException e) {
			if (!DEFAULT_LANGUAGE.equals(language))
				return Language.getWord(DEFAULT_LANGUAGE, key, params);
			throw e;
		}
	}

	public String getString(String key, Object[] params, String message) {
		try {
			return getString(key, params);
		} catch (MissingResourceException e) {
			return message;
		}
	}

	@SuppressWarnings("rawtypes")
	public Map getSupportedLanguages() {
		return supportedLanguages;
	}

	public void setSupportedLanguages(@SuppressWarnings("rawtypes") Map supportedLanguages) {
		this.supportedLanguages = supportedLanguages;
	}

	public ResourceBundle getTranslation() {
		return translation;
	}

	public void setTranslation(ResourceBundle translation) {
		this.translation = translation;
	}
}