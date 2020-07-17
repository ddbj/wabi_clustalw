package jp.ac.nig.ddbj.wabi.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import jp.ac.nig.ddbj.wabi.request.BlastGetRequest;

/**
 * GET リクエストされた BLAST入力データ の妥当性を検証します.
 */
public class BlastGetRequestValidator implements Validator {
	public boolean supports(Class clazz) {
		return BlastGetRequest.class.isAssignableFrom(clazz);
	}

	/**
	 * GET "/blast/{id}" リクエストの入力値を検証します。
	 */
	public void validate(Object target, Errors errors) {
		if (!(target instanceof BlastGetRequest)) {
			return;
		}
		BlastGetRequest request = (BlastGetRequest)target;
		String imageId = request.getImageId();
		BlastRequestValidationUtil.validateRequestId(request.getRequestId(), errors);
		BlastRequestValidationUtil.validateFormat(request.getFormat(), errors);
		BlastRequestValidationUtil.validateImageId(request.getImageId(), errors);
		BlastRequestValidationUtil.validateInfo(request.getInfo(), null==imageId || imageId.isEmpty(), errors);
	}
}
