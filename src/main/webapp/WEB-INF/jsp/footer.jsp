<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<div id="footer" role="contentinfo">
	<div id="footer"
		style="border-top: solid 1px #d3d3d3; padding: 10px; font-size: 100%; width: 800px;">
		<a href="http://www.ddbj.nig.ac.jp/DDBJ_site-${language_code}.html"><spring:message code="label.footer.site-map" /></a><c:if test="${'j' == language_code}">│<a
			href="http://www.ddbj.nig.ac.jp/notice/sitepolicy-${language_code}.html"><spring:message code="label.footer.site-policy" /></a>│<a
			href="http://www.ddbj.nig.ac.jp/notice-${language_code}.html"><spring:message code="label.footer.notice" /></a>│<a
			href="http://www.ddbj.nig.ac.jp/notice/pinfo-${language_code}.html"><spring:message code="label.footer.pinfo" /></a></c:if>

		<div class="lastmod"
			style="float: right; color: #666666; margin-right: 10px; font-size: 100%;">
			Last modified : March 31, 2012.</div>
		<address
			style="font-style: normal; font-size: 100%; color: #666666; margin-top: 20px;">Copyright
			DNA Data Bank of Japan. All Rights Reserved.</address>
	</div>
</div>