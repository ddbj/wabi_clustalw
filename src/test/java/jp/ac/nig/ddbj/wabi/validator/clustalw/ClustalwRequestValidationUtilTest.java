package jp.ac.nig.ddbj.wabi.validator.clustalw;

import static org.junit.Assert.assertEquals;

import java.util.regex.Pattern;

import org.junit.Test;

import jp.ac.nig.ddbj.wabi.request.WabiRequest;
import jp.ac.nig.ddbj.wabi.util.ConfClustalw;

public class ClustalwRequestValidationUtilTest {
	protected static Pattern patternParameters = Pattern.compile(ConfClustalw.RequestValidationPattern.parameters);
	protected static Pattern patternParameters_each = Pattern.compile(ConfClustalw.RequestValidationPattern.parameters_eachParameter);

	@Test
	public void testValidateParameters() {
		WabiRequest request = null;
		ClustalwPostRequestValidator validator = new ClustalwPostRequestValidator();
		String parameters = null;
		String message = null;

		//誤ったparameters ";"を入れる
		parameters = "-ALIGN;mkdir test";
		message = ClustalwRequestValidationUtil.validateParameters(parameters, validator.getAcceptOptions(request));
		assertEquals(true, message.matches("parameters check error:.*"));

		//parameters何もなし。※実際にこれを送るとclustalw2では-ALIGN -INFILE=hogeと同じ処理が行われる。
		parameters = "";
		message = ClustalwRequestValidationUtil.validateParameters(parameters, validator.getAcceptOptions(request));
		assertEquals("no error", message);

		//正しいparameters, accept options
		parameters = "-ALIGN";
		message = ClustalwRequestValidationUtil.validateParameters(parameters, validator.getAcceptOptions(request));
		assertEquals("no error", message);

		//正しいparameters, accept options2
		parameters = "-ALIGN -TYPE=PROTEIN -OUTPUT=CLUSTAL -PWMATRIX=BLOSUM -MATRIX=BLOSUM -GAPOPEN=10.0";
		message = ClustalwRequestValidationUtil.validateParameters(parameters, validator.getAcceptOptions(request));
		assertEquals("no error", message);

		//正しいparameters, accept options3
		parameters = "-PROFILE";
		message = ClustalwRequestValidationUtil.validateParameters(parameters, validator.getAcceptOptions(request));
		assertEquals("no error", message);

		//許可されていないaccept options1
		parameters = "-HELP";
		message = ClustalwRequestValidationUtil.validateParameters(parameters, validator.getAcceptOptions(request));
		assertEquals(true, message.matches("accept option check error:.*"));

		//許可されていないaccept options2
		parameters = "-ALIGN -INFILE=sample.txt";
		message = ClustalwRequestValidationUtil.validateParameters(parameters, validator.getAcceptOptions(request));
		assertEquals(true, message.matches("accept option check error:.*"));
	}
}