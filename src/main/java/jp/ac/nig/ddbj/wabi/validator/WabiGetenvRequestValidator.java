package jp.ac.nig.ddbj.wabi.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import jp.ac.nig.ddbj.wabi.request.WabiGetenvRequest;

/**
 * GET リクエストされた BLAST入力データ の妥当性を検証します.
 */
public class WabiGetenvRequestValidator implements Validator {
	public boolean supports(Class clazz) {
		return WabiGetenvRequest.class.isAssignableFrom(clazz);
	}

	/**
	 * GET "/clustalw/" リクエストの入力値を検証します。
	 */
	public void validate(Object target, Errors errors) {
		if (!(target instanceof WabiGetenvRequest)) {
			return;
		}
		WabiGetenvRequest request = (WabiGetenvRequest)target;
		WabiRequestValidationUtil.validateFormat(request.getFormat(), errors);
		WabiRequestValidationUtil.validateInfoEnv(request.getInfo(), errors);
	}
}
