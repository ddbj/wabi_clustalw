package jp.ac.nig.ddbj.wabi.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import jp.ac.nig.ddbj.wabi.request.BlastGetenvRequest;

/**
 * GET リクエストされた BLAST入力データ の妥当性を検証します.
 */
public class BlastGetenvRequestValidator implements Validator {
	public boolean supports(Class clazz) {
		return BlastGetenvRequest.class.isAssignableFrom(clazz);
	}

	/**
	 * GET "/blast/" リクエストの入力値を検証します。
	 */
	public void validate(Object target, Errors errors) {
		if (!(target instanceof BlastGetenvRequest)) {
			return;
		}
		BlastGetenvRequest request = (BlastGetenvRequest)target;
		BlastRequestValidationUtil.validateFormat(request.getFormat(), errors);
		BlastRequestValidationUtil.validateInfoEnv(request.getInfo(), errors);
	}
}
