package jp.ac.nig.ddbj.wabi.validator.vecscreen;

import static org.junit.Assert.assertEquals;

import java.util.regex.Pattern;

import org.junit.Test;

import jp.ac.nig.ddbj.wabi.request.WabiRequest;
import jp.ac.nig.ddbj.wabi.util.ConfVecscreen;


public class VecscreenRequestValidationUtilTest {
	protected static Pattern patternParameters = Pattern.compile(ConfVecscreen.RequestValidationPattern.parameters);
	protected static Pattern patternParameters_each = Pattern.compile(ConfVecscreen.RequestValidationPattern.parameters_eachParameter);

	@Test
	public void testValidateParameters() {
		WabiRequest request = null;
		VecscreenPostRequestValidator validator = new VecscreenPostRequestValidator();
		String parameters = null;
		String message = null;

		//誤ったparameters ";"を入れる
		parameters = "-f 3;mkdir test";
		message = VecscreenRequestValidationUtil.validateParameters(parameters, validator.getAcceptOptions(request));
		assertEquals(true, message.matches("parameters check error:.*"));

		//parameters何もなし。※実際にこれを送るとclustalw2では-ALIGN -INFILE=hogeと同じ処理が行われる。
		parameters = "";
		message = VecscreenRequestValidationUtil.validateParameters(parameters, validator.getAcceptOptions(request));
		assertEquals("no error", message);

		//正しいparameters, accept options
		parameters = "-f 2";
		message = VecscreenRequestValidationUtil.validateParameters(parameters, validator.getAcceptOptions(request));
		assertEquals("no error", message);

		//許可されていないaccept options1
		//patternParametersでチェックする段階でハネられる。
		parameters = "-f 4";
		message = VecscreenRequestValidationUtil.validateParameters(parameters, validator.getAcceptOptions(request));
		assertEquals(true, message.matches("parameters check error:.*"));

	}
}