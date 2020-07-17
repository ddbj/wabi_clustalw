<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<div id="masthead">
	<!-- Header の写真 -->
	<h1 class="float">
		<a href="http://www.ddbj.nig.ac.jp/"><img
			src="resources/images/header-mag_s.gif" alt="DDBJ" width="400"
			height="88"></a>
	</h1>

	<!-- RSS Twitter Language Search -->
	<div id="header_meta_top">
		<div style="margin-left: 0px; margin-right: 0：">
			<a href="${language_chooser_url}" id="header_jp" title="JP">${language_chooser_label}</a>
			&nbsp;&nbsp;

			<!-- Twitter , PSS , Language 切り替え -->
			<a href="http://twitter.com/DDBJ_topics"><img
				src="resources/images/ico_twitter.png" alt="RSS" width="20"
				height="20"></a> <a
				href="http://www.ddbj.nig.ac.jp?feed=rss2&cat=45 " id="header_rss"
				title="RSS"><img src="resources/images/ico_feed.png" alt="RSS"
				width="20" height="20"></a>&nbsp;&nbsp;
		</div>
		
		
		<form name="search"
			action="http://www.ddbj.nig.ac.jp/cgi-bin/search-wp-e.pl"
			method="get">
			<div style="margin-left: 0px; margin-right: 0px;">
				<!-- 検索 -->
				<spring:message code="label.site_search" var="site_search" scope="page"/>
				<input type="text" maxlength="30" size="25" name="q"><input
					type="submit" value="${site_search}">
			</div>
		</form>
	</div>
	<!-- END #header_meta -->

	<!-- Menu -->
	<div id="access" role="navigation">
		<div class="skip-link screen-reader-text">
			<a href="#content" title="コンテンツへ移動">コンテンツへ移動</a>
		</div>

		<div class="menu-header">
			<ul id="menu-ddbj%e3%80%80menu" class="menu">
				<li id="menu-item-8000"
					class="menu-item menu-item-type-post_type menu-item-object-page menu-item-8000"><a
					href="http://www.ddbj.nig.ac.jp/index-${language_code}.html">HOME</a></li>
				<li id="menu-item-534"
					class="color red menu-item menu-item-type-post_type menu-item-object-page menu-item-534"><a
					href="http://www.ddbj.nig.ac.jp/submission-${language_code}.html"><spring:message code="label.menu.submission" /></a></li>
				<li id="menu-item-6067"
					class="menu-item menu-item-type-post_type menu-item-object-page menu-item-6067"><a
					href="http://www.ddbj.nig.ac.jp/ddbjingtop-${language_code}.html"><spring:message code="label.menu.how_to_use" /></a></li>
				<li id="menu-item-532"
					class="menu-item menu-item-type-post_type menu-item-object-page current-menu-item page_item page-item-14 current_page_item menu-item-532"><a
					href="http://www.ddbj.nig.ac.jp/searches-${language_code}.html"><spring:message code="label.menu.analysis" /></a></li>
				<li id="menu-item-531"
					class="menu-item menu-item-type-post_type menu-item-object-page menu-item-531"><a
					href="http://www.ddbj.nig.ac.jp/ftp_soap-${language_code}.html"><spring:message code="label.menu.ftp" /></a></li>
				<li id="menu-item-530"
					class="menu-item menu-item-type-post_type menu-item-object-page menu-item-530"><a
					href="http://www.ddbj.nig.ac.jp/documents-${language_code}.html"><spring:message code="label.menu.report" /></a></li>
				<li id="menu-item-529"
					class="menu-item menu-item-type-post_type menu-item-object-page menu-item-529"><a
					href="http://www.ddbj.nig.ac.jp/addresses-${language_code}.html"><spring:message code="label.menu.contact" /></a></li>
			</ul>
		</div>
	</div>
	<!-- #access -->

</div>
<!-- #masthead -->