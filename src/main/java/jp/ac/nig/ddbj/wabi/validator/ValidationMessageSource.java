package jp.ac.nig.ddbj.wabi.validator;

import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * 入力値検証のメッセージを取得するために使います。
 */
public class ValidationMessageSource {
	private MessageSourceAccessor message;

	public void setMessageSource(MessageSource message) {
		this.message = new MessageSourceAccessor(message);
	}

	public String obtainMsg(String key, String defaultMessage) {
		return this.message.getMessage(key, defaultMessage);
	}
}
