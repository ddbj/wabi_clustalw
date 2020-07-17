package jp.ac.nig.ddbj.wabi.util;

import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;

import net.arnx.jsonic.JSON;

public class StringUtil {
	static final String characterEncoding = "UTF-8";

	/**
	 * 文字列をエンコードした URI を返しますが、 "+" は " " に置換した状態で返します。
	 * また、改行をエンコードした "%0A" も改行文字に置換してから返します。
	 *
	 * @param in エンコードされていない URI
	 * @return エンコードされた URI だが、 "+" は " " に置換済み
	 * @throws UnsupportedEncodingException エンコードに失敗した場合
	 */
	public static String encodeURI(String in) throws UnsupportedEncodingException {
		if (null==in || in.isEmpty()) return in;
		String encoded = URLEncoder.encode(in, characterEncoding);
		encoded = encoded.replaceAll("\\+", " ");
		encoded = encoded.replaceAll("%0[Aa]", "\n");
		return encoded;
	}
}
