package cc.pinel.mangue.util;

public class StringUtils {

	public static String unescapeHtml(final String input) {
		if (input.indexOf("&#") == -1)
			return input;

		StringBuilder sb = new StringBuilder();

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
					System.out.println("Found: &#" + n + "; = " + unescape);

					sb.append(unescape);
					i = j;
				} else {
					sb.append(input.charAt(i));
				}
			} else {
				sb.append(input.charAt(i));
			}
		}

		return sb.toString();
	}
}
