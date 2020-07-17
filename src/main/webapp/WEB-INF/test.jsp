<!DOCTYPE html>
<html dir="ltr" lang="ja">

<head>
<meta charset="UTF-8" />
<title>BLAST | DDBJ</title>
<link rel="stylesheet" href="./themes/style.css" type="text/css" />
<link rel="stylesheet"
	href="http://www.ddbj.nig.ac.jp/wp-content/themes/ddbj/style.css"
	type="text/css" />
<script type="text/javascript" src="switchparam.js"></script>
</head>

<body
	class="page page-id-71 page-parent page-template page-template-onecolumn-page-e-php"
	onload="append_children('all','ja');document.getElementById('ddbjall').checked=true;">
	<div id="wrapper" class="hfeed">
		<div id="header">
			<div id="masthead">
				<!-- Header の写真 -->
				<h1 class="float">
					<a href="http://www.ddbj.nig.ac.jp/"><img
						src="img/header-mag_s.gif" alt="DDBJ" width="400" height="88"></a>
				</h1>

				<!-- RSS Twitter Language Search -->
				<div id="header_meta_top">
					<div style="margin-left: 0px; margin-right: 0：">
						<a href="blastn?lang=en" id="header_jp" title="JP">ENGLISH</a>
						&nbsp;&nbsp;

						<!-- Twitter , PSS , Language 切り替え -->
						<a href="http://twitter.com/DDBJ_topics"><img
							src="img/ico_twitter.png" alt="RSS" width="20" height="20"></a>

						<a href="http://www.ddbj.nig.ac.jp?feed=rss2&cat=45 "
							id="header_rss" title="RSS"><img src="img/ico_feed.png"
							alt="RSS" width="20" height="20"></a>&nbsp;&nbsp;
					</div>
					<form name="search"
						action="http://www.ddbj.nig.ac.jp/cgi-bin/search-wp-e.pl"
						method="get">
						<div style="margin-left: 0px; margin-right: 0px;">
							<!-- 検索 -->
							<input type="text" maxlength="30" size="25" name="q"><input
								type="submit" value="サイト内検索">
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
								href="http://www.ddbj.nig.ac.jp/index-j.html">HOME</a></li>
							<li id="menu-item-534"
								class="color red menu-item menu-item-type-post_type menu-item-object-page menu-item-534"><a
								href="http://www.ddbj.nig.ac.jp/submission-j.html">塩基配列の登録</a></li>
							<li id="menu-item-6067"
								class="menu-item menu-item-type-post_type menu-item-object-page menu-item-6067"><a
								href="http://www.ddbj.nig.ac.jp/ddbjingtop-j.html">利用の手引き</a></li>
							<li id="menu-item-532"
								class="menu-item menu-item-type-post_type menu-item-object-page current-menu-item page_item page-item-14 current_page_item menu-item-532"><a
								href="http://www.ddbj.nig.ac.jp/searches-j.html">検索・解析</a></li>
							<li id="menu-item-531"
								class="menu-item menu-item-type-post_type menu-item-object-page menu-item-531"><a
								href="http://www.ddbj.nig.ac.jp/ftp_soap-j.html">FTP・WebAPI</a></li>
							<li id="menu-item-530"
								class="menu-item menu-item-type-post_type menu-item-object-page menu-item-530"><a
								href="http://www.ddbj.nig.ac.jp/documents-j.html">レポート・統計</a></li>
							<li id="menu-item-529"
								class="menu-item menu-item-type-post_type menu-item-object-page menu-item-529"><a
								href="http://www.ddbj.nig.ac.jp/addresses-j.html">お問い合わせ</a></li>
						</ul>
					</div>
				</div>
				<!-- #access -->

			</div>
			<!-- #masthead -->
		</div>
		<!-- #header -->
		<div id="main">
			<div class="breadcrumb">
				<!-- Breadcrumb NavXT 4.0.1 -->
				<a title="HOME" href="http://www.ddbj.nig.ac.jp">HOME</a> &gt; <a
					href="http://www.ddbj.nig.ac.jp/searches-j.html">検索・解析</a> &gt;
				BLAST
			</div>
			<div id="primary" class="widget-area" role="complementary">
				<ul class="xoxo">
					<div id="sub">
						<ul class="link01">
							<li><p>
									<a href="megablast?lang=ja">megablast</a>
								</p></li>
							<li><p>
									<a href="blastn?lang=ja">blastn</a>
								</p></li>
							<li><p>
									<a href="tblastn?lang=ja">tblastn</a>
								</p></li>
							<li><p>
									<a href="tblastx?lang=ja">tblastx</a>
								</p></li>
							<li><p>
									<a href="blastp?lang=ja">blastp</a>
								</p></li>
							<li><p>
									<a href="blastx?lang=ja">blastx</a>
								</p></li>
							<br>
							<br>
							<li><p>
									<a
										href="http://www.ddbj.nig.ac.jp/search/help/blasthelp-j.html">ヘルプ</a>
								</p></li>

						</ul>
					</div>
				</ul>
			</div>
			<div id="container">
				<div id="content" role="main">
					<div class="h1_design">
						<h1>blastn (クエリ:塩基配列,検索対象:塩基配列)</h1>
					</div>

					<!-- ************************************************************************ -->

					<form method="post" action="newblastc.php"
						enctype="multipart/form-data">
						<h2>Query</h2>
						File Upload: <INPUT TYPE="file" NAME="up_file" MAXLENGTH="0"><br />
						&nbsp;or COPY & PASTE:<br />
						<TEXTAREA NAME="qseq" COLS=80 ROWS=10></TEXTAREA>
						<br /> <INPUT TYPE="submit" VALUE="Send to BLAST"> <INPUT
							TYPE="button" VALUE="Clear"> <br />
						<br />
						<h2>Program</h2>
						<ul>
							<li><input id='blastn' type="radio" name="blasttype"
								value="blastn" onclick="switch_blastn_blastn()" checked>
								blastn <!-- <li><input id='mpiblast' type="radio" name="blasttype" value="mpiblast" onclick="switch_mpiblast_blastn()" > mpi blast -->
						</ul>

						<h2>Data Sets</h2>
						<table width="500">
							<tr>
								<td width="350"><ul>
										<li><input type="radio" name="database" value="ddbjfull"
											onclick="append_children('full','ja')">DDBJ/EMBL/GenBank
											全配列データ (DDBJ定期リリース+新着データ)
										<li><input id="ddbjall" type="radio" name="database"
											value="ddbjall" onclick="append_children('all','ja')">古典的区分の配列データ
											(DDBJ定期リリース+新着データ。 但し以下を除く;
											PAT,ENV,EST,STS,GSS,HTG,HTC,UNA,SYN,TSA）
										<li><input type="radio" name="database" value="ddbjnew"
											onclick="append_children('new','ja')">DDBJ新着データ
										<li><input type="radio" name="database" value="pat"
											onclick="append_children('pat','ja')">PAT:
											特許に登録された塩基配列データ
										<li><input type="radio" name="database" value="env"
											onclick="append_children('env','ja')">ENV:
											環境上のサンプルに由来した配列
										<li><input type="radio" name="database" value="gss"
											onclick="append_children('gss','ja')">GSS: short
											single passのゲノム配列データ
										<li><input type="radio" name="database" value="htc"
											onclick="append_children('htc','ja')">HTC:
											EST以外のcDNA配列プロジェクトに由来する配列データ
										<li><input type="radio" name="database" value="htg"
											onclick="append_children('htg','ja')">HTG:
											ゲノムプロジェクトに由来する配列データ
										<li><input type="radio" name="database" value="sts"
											onclick="append_children('sts','ja')">STS:
											ゲノムシーケンシングのタグとなる配列データ
										<li><input type="radio" name="database" value="est"
											onclick="append_children('est','ja')">EST: Expressed
											sequence tag
										<li><input type="radio" name="database" value="other"
											onclick="append_children('other','ja')">その他
									</ul></td>
								<td><ul id="divisions"></ul></td>
							</tr>
						</table>

						<input id='hiddendiv' type='hidden' name='division'
							value='ddbjall'>


						<h2 id="opheader">Optional Parameters</h2>
						<table id="optable" border="0">
							<tr>
								<td width="50">SCORES</td>
								<td><INPUT TYPE="number" NAME="score" VALUE="100" SIZE="5"></td>
							</tr>
							<tr>
								<td width="50">ALIGNMENTS</td>
								<td><INPUT TYPE="number" NAME="alignments" VALUE="100"
									SIZE="5"></td>
							</tr>
							<tr>
								<td width="50">EXPECT</td>
								<td><INPUT TYPE="number" NAME="expect" VALUE=10 SIZE=5></td>
							</tr>
							<tr>
								<td width="50">FILTER</td>
								<td><INPUT TYPE="radio" NAME="filter" VALUE="T" CHECKED>
									ON <INPUT TYPE="radio" NAME="filter" VALUE="F"> OFF</td>
							</tr>
							<tr>
								<td>WORD SIZE</td>
								<td><INPUT TYPE="number" ID="wordsize" NAME="wordsize"
									VALUE="11" SIZE=5></td>
							</tr>
							<!--<tr><td width="50">OTHER OPTIONS</td><td><INPUT TYPE="TEXT" NAME="options" SIZE="40"></td></tr>-->
						</table>
					</form>

				</div>
			</div>
		</div>
		<div id="footer" role="contentinfo">
			<div id="footer"
				style="border-top: solid 1px #d3d3d3; padding: 10px; font-size: 100%; width: 800px;">
				<a href="http://www.ddbj.nig.ac.jp/DDBJ_site-j.html">サイトマップ</a>│<a
					href="http://www.ddbj.nig.ac.jp/notice/sitepolicy-j.html">サイトポリシー</a>│<a
					href="http://www.ddbj.nig.ac.jp/notice-j.html">当サイトのご利用にあたって</a>│<a
					href="http://www.ddbj.nig.ac.jp/notice/pinfo-j.html">個人情報の取扱いについて</a>

				<div class="lastmod"
					style="float: right; color: #666666; margin-right: 10px; font-size: 100%;">
					Last modified : March 31, 2012.</div>
				<address
					style="font-style: normal; font-size: 100%; color: #666666; margin-top: 20px;">Copyright
					DNA Data Bank of Japan. All Rights Reserved.</address>
			</div>
		</div>
</body>
</html>