package jp.ac.nig.ddbj.wabi.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import jp.ac.nig.ddbj.wabi.request.WabiGetRequest;
import jp.ac.nig.ddbj.wabi.request.WabiRequest;

/**
 * GET リクエストされた BLAST入力データ の妥当性を検証します.
 */
public class WabiGetRequestValidator implements Validator {

	protected WabiGetRequest request = null;

	public boolean supports(Class clazz) {
		return WabiGetRequest.class.isAssignableFrom(clazz);
	}

	/**
	 * GET "/blast/{id}" リクエストの入力値を検証します。
	 */
	public void validate(Object target, Errors errors) {
		if (!(target instanceof WabiGetRequest)) {
			return;
		}
		request = (WabiGetRequest)target;
		WabiRequestValidationUtil.validateFormat(request.getFormat(), errors);
	}
}
