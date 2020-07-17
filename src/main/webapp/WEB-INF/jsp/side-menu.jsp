<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<div id="primary" class="widget-area" role="complementary">
	<ul class="xoxo">
		<div id="sub">
			<ul class="link01">
				<li><p>
						<a href="megablast?lang=${language_code2}">megablast</a>
					</p></li>
				<li><p>
						<a href="blastn?lang=${language_code2}">blastn</a>
					</p></li>
				<li><p>
						<a href="tblastn?lang=${language_code2}">tblastn</a>
					</p></li>
				<li><p>
						<a href="tblastx?lang=${language_code2}">tblastx</a>
					</p></li>
				<li><p>
						<a href="blastp?lang=${language_code2}">blastp</a>
					</p></li>
				<li><p>
						<a href="blastx?lang=${language_code2}">blastx</a><br><br>
					</p></li>
				<li><p>
						<a href="http://www.ddbj.nig.ac.jp/search/help/blasthelp-${language_code}.html">
						<spring:message code="label.side-menu.help" /></a>
					</p></li>

			</ul>
		</div>
	</ul>
</div>