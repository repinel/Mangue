/*
 * Copyright 2014 Roque Pinel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cc.pinel.mangue.util;

/**
 * Utils methods for strings.
 * 
 * @author Roque Pinel
 *
 */
public class StringUtils {

	/**
	 * The basic list of HTML escaped characters to be replaced.
	 */
	private static final String[][] BASIC_HTML_ESCAPE = {
		{"\"", "&quot;" },
		{ "&", "&amp;" },
		{ "<", "&lt;" },
		{ ">", "&gt;" },
	};

	/**
	 * Unescapes HTML characters from the input.
	 * 
	 * @param input the input
	 * @return the unescaped html string
	 */
	public static String unescapeHtml(final String input) {
		String unescapeHexHtmlOutput = unescapeHtmlByNumber(input);
		return unescapeHtmlByName(unescapeHexHtmlOutput);
	}

	/**
	 * Unescapes HTML characters based on ASCII numbers.
	 * 
	 * @param input the input
	 * @return
	 */
	private static String unescapeHtmlByNumber(final String input) {
		if (input.indexOf("&#") == -1)
			return input;

		String out = "";

		for (int i = 0, length = input.length(); i < length; i++) {
			if (input.charAt(i) == '&' && i + 1 < length && input.charAt(i + 1) == '#') {
				int j = i + 2;
				boolean isValid = true;
				int n = 0;
				for ( ; j < length; j++) {
					char c = input.charAt(j);
					if (c == ';')
						break;
					else if (c >= '0' || c <= '9') {
						n = n * 10 + (c - '0');
					} else {
						isValid = false;
						break;
					}
				}
				if (isValid && n != 0) {
					char unescape = (char) n;
					out += unescape;
					i = j;
				} else {
					out += input.charAt(i);
				}
			} else {
				out += input.charAt(i);
			}
		}

		return out;
	}

	/**
	 * Unescapes HTML characters based on it name (value).
	 * 
	 * @param input the input
	 * @return
	 */
	private static String unescapeHtmlByName(final String input) {
		String result = input;

		for (int i = 0; i < BASIC_HTML_ESCAPE.length; i++) {
			result = replace(result, BASIC_HTML_ESCAPE[i][1], BASIC_HTML_ESCAPE[i][0]);
		}

		return result;
	}

	/**
	 * Replaces the pattern occurrences from the input with the replacement string.
	 * 
	 * @param input the input
	 * @param pattern the pattern
	 * @param replacement the replacement
	 * @return
	 */
	public static String replace(String input, String pattern, String replacement) {
		String out = "";

		int n = input.length();
		int m = pattern.length();
		int lastFound = -1;

		for (int i = 0; i < n; ) {
			if (i + m >= n) {
				out += input.substring(lastFound == -1 ? 0 : i, n);
				break;
			}

			boolean hasMatched = true;
			for (int j = 0; j < m; j++) {
				if (pattern.charAt(j) != input.charAt(i + j)) {
					hasMatched = false;
					break;
				}
			}

			if (hasMatched) {
				if (lastFound == -1 && i > 0)
					out += input.substring(0, i);
				else if (lastFound + m < i)
					out += input.substring(lastFound + m, i);
				out += replacement;
				lastFound = i;
				i += m;
			} else {
				i++;
			}
		}

		return out;
	}
}
