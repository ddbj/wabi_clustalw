package jp.ac.nig.ddbj.wabi.view;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLStreamException;

import net.ogalab.util.container.MapUtil;

import org.springframework.web.servlet.view.AbstractView;

import net.arnx.jsonic.JSON;

import jp.ac.nig.ddbj.wabi.util.StringUtil;

/** SpringMVCのmodelをXML形式で出力する。StAXを使っているのでメモリにやさしい.
 * 
 * 
 * @author oogasawa
 *
 */
public class LinkedHashMapToXmlView extends AbstractView {

	@Override
	protected void renderMergedOutputModel( Map<String, Object> model, HttpServletRequest req, HttpServletResponse res ) throws Exception {

		String contentType = "text/xml; charset=utf-8";
		res.setContentType( contentType );

		String characterEncoding = "UTF-8";
		if ( characterEncoding != null )
			res.setCharacterEncoding( characterEncoding );

		ServletOutputStream out = res.getOutputStream();

		// 参考 : http://www.javainthebox.net/laboratory/JavaSE6/stax/stax.html
		XMLOutputFactory factory = XMLOutputFactory.newInstance();
		XMLStreamWriter writer = factory.createXMLStreamWriter(out);
		writer.writeStartDocument();
		writer.writeDTD("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" "
				+ "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
		writer.writeStartElement("result");

		LinkedHashMap<String, Object> map = (LinkedHashMap<String,Object>)model.get("linked-hash-map");
		Set<String> keySet = map.keySet();
		for (String k : keySet) {
			writer.writeStartElement(k);
			writeElem(writer, map.get(k));
			writer.writeEndElement();
		}

		writer.writeEndElement();
		writer.writeEndDocument();
		writer.close();
		out.close(); // 要るの？

	}

	private void writeElem(XMLStreamWriter writer, Object object) throws XMLStreamException, UnsupportedEncodingException {
		if (null==object) {
			writer.writeCharacters(String.valueOf(object));
		} else if (object instanceof Map) {
			Map map = (Map)object;
			for (Object key : map.keySet()) {
				if (key instanceof String) {
					writer.writeStartElement((String)key);
					writeElem(writer, map.get(key));
					writer.writeEndElement();
				} else {
					writer.writeStartElement("EntrySet");
					writer.writeStartElement("key");
					writeElem(writer, key);
					writer.writeEndElement();
					writer.writeStartElement("value");
					writeElem(writer, map.get(key));
					writer.writeEndElement();
					writer.writeEndElement();
				}
			}
		} else if (object instanceof Iterable) {
			Iterable iterable = (Iterable)object;
			for (Object item : iterable) {
				writer.writeStartElement("item");
				writeElem(writer, item);
				writer.writeEndElement();
			}
		} else {
			writer.writeCharacters(StringUtil.encodeURI(String.valueOf(object)));
		}
	}
}
