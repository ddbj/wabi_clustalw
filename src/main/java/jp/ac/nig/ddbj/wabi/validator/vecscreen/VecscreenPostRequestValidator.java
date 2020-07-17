package jp.ac.nig.ddbj.wabi.validator.vecscreen;

import org.springframework.validation.Errors;

import jp.ac.nig.ddbj.wabi.request.WabiRequest;
import jp.ac.nig.ddbj.wabi.util.ConfVecscreen;
import jp.ac.nig.ddbj.wabi.validator.WabiPostRequestValidator;

/**
 * POST リクエストされた CLUSTALW入力データ の妥当性を検証します.
 */
public class VecscreenPostRequestValidator extends WabiPostRequestValidator {

	/**
	 * POST "/vecscreen" リクエストの入力値を検証します。
	 */
	@Override
	public void validate(Object target, Errors errors) {
		super.validate(target, errors);

		//データベースのチェック
		VecscreenRequestValidationUtil.validateDatabase(request.getDatabase(), errors);

		//パラメータのチェックを追加
		VecscreenRequestValidationUtil.validateParameters(request.getParameters(), getAcceptOptions(request), errors);
	}

	/**
	 * Vecscreen入力データ の parameters値 として受け付け可能なオプションです。
	 * 例: blastall の場合は "ABCDEFGIJKLMPQSTUVWXYZabdefglmnqrstvwyz" 等。
	 */
	@Override
	protected String getAcceptOptions(WabiRequest request) {
		String acceptOptionsVecscreen = ConfVecscreen.RequestValidationPattern.parameters_AcceptOptions_vecscreen;
		return acceptOptionsVecscreen;
	}

}
