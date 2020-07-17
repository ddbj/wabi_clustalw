package jp.ac.nig.ddbj.wabi.view;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.ogalab.util.os.FileIO;

import org.springframework.web.servlet.view.AbstractView;

import jp.ac.nig.ddbj.wabi.util.StringUtil;

public class StreamToPlainTextView extends AbstractView {

	@Override
	protected void renderMergedOutputModel( Map<String, Object> model, HttpServletRequest req, HttpServletResponse res ) throws Exception {


		// Check if content type is specified.
		String contentType = "text/plain; charset=utf-8";
		String characterEncoding = "UTF-8";

		// Set content type and character encoding as given/determined.
		res.setContentType( contentType );
		if ( characterEncoding != null )
			res.setCharacterEncoding( characterEncoding );

		
		// Make string to view.
		ServletOutputStream out = res.getOutputStream();
		PrintWriter    pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(out)));
		try {
			String resultFileName = (String)model.get("filename");
			if (null==resultFileName) {
				throw new IOException();
			}
			BufferedReader br = FileIO.getBufferedReader(resultFileName);

			String line = null;
			while ((line = br.readLine()) != null) {
				if (line.startsWith("Query= ")) {
					line = "Query= " + StringUtil.encodeURI(line.substring("Query= ".length()));
					/*
					 * Note: ユーザー入力値を含む部分はエスケープ処理してレスポンス出力する。
					 */
				}
				pw.println(line);
			}
			br.close();
		} catch (IOException e) {
			res.setStatus(HttpServletResponse.SC_NOT_FOUND);
			pw.println("Not found.");
		}
		pw.close();

	}
	
}
