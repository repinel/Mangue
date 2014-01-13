package cc.pinel.mangue.storage;

import com.amazon.kindle.kindlet.KindletContext;
import com.amazon.kindle.kindlet.security.SecureStorage;

public class GeneralStorage {

	private final String SEARCH_TERM_KEY = "";

	private final SecureStorage secureStorage;

	public GeneralStorage(KindletContext context) {
		this.secureStorage = context.getSecureStorage();
	}

	public String getSearchTerm() {
		return getValue(SEARCH_TERM_KEY);
	}

	public boolean setSearchTerm(String term) {
		return secureStorage.putChars(SEARCH_TERM_KEY, term.toCharArray());
	}

	public boolean removeSearchTerm() {
		return secureStorage.remove(SEARCH_TERM_KEY);
	}


	private String getValue(String key) {
		char[] value = secureStorage.getChars(SEARCH_TERM_KEY);
		return value == null ? null : new String(value);
	}
}
