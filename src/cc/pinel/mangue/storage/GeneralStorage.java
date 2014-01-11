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

	public void setSearchTerm(String term) {
		secureStorage.putChars(SEARCH_TERM_KEY, term.toCharArray());
	}


	private String getValue(String key) {
		char[] value = secureStorage.getChars(SEARCH_TERM_KEY);
		return value == null ? null : new String(value);
	}
}
