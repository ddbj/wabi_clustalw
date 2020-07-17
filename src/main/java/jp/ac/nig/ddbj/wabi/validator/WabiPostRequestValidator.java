package jp.ac.nig.ddbj.wabi.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import jp.ac.nig.ddbj.wabi.request.WabiRequest;

/**
 * POST リクエストされた CLUSTALW入力データ の妥当性を検証します.
 */
public abstract class WabiPostRequestValidator implements Validator {
	
	protected WabiRequest request = null;
	
	public boolean supports(Class<?> clazz) {
		return WabiRequest.class.isAssignableFrom(clazz);
	}

	/**
	 * POSTリクエストの入力値を検証します。
	 */
	public void validate(Object target, Errors errors) {
		if (!(target instanceof WabiRequest)) {
			return;
		}
		request = (WabiRequest)target;
		WabiRequestValidationUtil.validateQuerySequence(request.getQuerySequence(), errors);
		WabiRequestValidationUtil.validateFormat(request.getFormat(), errors);
		WabiRequestValidationUtil.validateResult(request.getResult(), errors);
		WabiRequestValidationUtil.validateAddress(request.getAddress(), request.getResult(), errors);
	}

	/**
	 * CLUSTALW入力データ の parameters値 として受け付け可能なオプションです。
	 * 例: blastall の場合は "ABCDEFGIJKLMPQSTUVWXYZabdefglmnqrstvwyz" 等。
	 */
	protected abstract String getAcceptOptions(WabiRequest request);
}
