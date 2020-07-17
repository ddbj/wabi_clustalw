package jp.ac.nig.ddbj.wabi.validator.mafft;

import static org.junit.Assert.assertEquals;

import java.util.regex.Pattern;

import org.junit.Test;

import jp.ac.nig.ddbj.wabi.request.WabiRequest;
import jp.ac.nig.ddbj.wabi.util.ConfMafft;
import jp.ac.nig.ddbj.wabi.validator.clustalw.ClustalwRequestValidationUtil;

public class MafftRequestValidationUtilTest {
	protected static Pattern patternParameters = Pattern.compile(ConfMafft.RequestValidationPattern.parameters);
	protected static Pattern patternParameters_each = Pattern.compile(ConfMafft.RequestValidationPattern.parameters_eachParameter);

	@Test
	public void testValidateParameters() {
		WabiRequest request = null;
		MafftPostRequestValidator validator = new MafftPostRequestValidator();
		String parameters = null;
		String message = null;

		//誤ったparameters ";"を入れる
		parameters = "--auto;mkdir test";
		message = MafftRequestValidationUtil.validateParameters(parameters, validator.getAcceptOptions(request));
		assertEquals(true, message.matches("parameters check error:.*"));

		//parameters何もなし。※実際にこれを送るとclustalw2では-ALIGN -INFILE=hogeと同じ処理が行われる。
		parameters = "";
		message = MafftRequestValidationUtil.validateParameters(parameters, validator.getAcceptOptions(request));
		assertEquals("no error", message);

		//正しいparameters, accept options
		parameters = "--auto";
		message = MafftRequestValidationUtil.validateParameters(parameters, validator.getAcceptOptions(request));
		assertEquals("no error", message);

		//正しいparameters, accept options2
		parameters = "--globalpair --maxiterate 1000 --op 1.53 --ep 0.123 --lop -2.00";
		message = MafftRequestValidationUtil.validateParameters(parameters, validator.getAcceptOptions(request));
		assertEquals("no error", message);

		//許可されていないaccept options1
		parameters = "--HELP";
		message = MafftRequestValidationUtil.validateParameters(parameters, validator.getAcceptOptions(request));
		assertEquals(true, message.matches("accept option check error:.*"));

	}
}