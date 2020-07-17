package jp.ac.nig.ddbj.wabi.view;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.view.AbstractView;

public class StreamToImageView extends AbstractView {

	@Override
	protected void renderMergedOutputModel( Map<String, Object> model, HttpServletRequest req, HttpServletResponse res ) throws Exception {


		// Check if content type is specified.
		String contentType = "image/png";
		//String characterEncoding = "UTF-8";

		// Set content type and character encoding as given/determined.
		res.setContentType( contentType );
		//if ( characterEncoding != null )
		//	res.setCharacterEncoding( characterEncoding );

		
		// Make string to view.
		ServletOutputStream out = res.getOutputStream();
		try {
			String imageFileName = (String)model.get("filename");
			if (null==imageFileName) {
				throw new IOException();
			}
			BufferedInputStream bi = new BufferedInputStream(new FileInputStream(imageFileName));

			int iData = 0;
			while ((iData = bi.read()) != -1) {
				out.write(iData);
			}
			bi.close();
		} catch (IOException e) {
			res.setStatus(HttpServletResponse.SC_NOT_FOUND);
			out.print("Not found.");
		}
		out.close();

	}
	
}
