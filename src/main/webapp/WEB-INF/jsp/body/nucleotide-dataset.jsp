<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>

<h2>Data Sets</h2>

<p></p>
<select name="database">
	<option value="ddbjall" selected>DDBJ ALL (DDBJ periodical
		release + daily updates)</option>
	<option value="ddbjnew">DDBJ New (DDBJ daily updates)</option>
</select>
select-all clear-all
<p></p>
<table>
	<tr>
		<td colspan="5"><input id="std_div_title" type="checkbox" name="div_class" value="standard" checked />
                        <span style="font-size: 110%; font-weight: bold;">Standard divisions</span></td>
	</tr>
	<tr>
		<td><input class="std_div" type="checkbox" name="division" value="ddbjhum" checked />Human</td>
		<td><input class="std_div" type="checkbox" name="division" value="ddbjpri" checked />Primates</td>
		<td><input class="std_div" type="checkbox" name="division" value="ddbjrod" checked />Rodents</td>
		<td><input class="std_div" type="checkbox" name="division" value="ddbjmam" checked />Mammals</td>
		<td><input class="std_div" type="checkbox" name="division" value="ddbjvrt" checked />Vertebrates</td>
	</tr>
	<tr>
		<td><input class="std_div" type="checkbox" name="division" value="ddbjinv" checked />Invertebrates</td>
		<td><input class="std_div" type="checkbox" name="division" value="ddbjpln" checked />Plants</td>
		<td><input class="std_div" type="checkbox" name="division" value="ddbjbct" checked />Bacteria</td>
		<td><input class="std_div" type="checkbox" name="division" value="ddbjvir" checked />Viruses</td>
		<td><input class="std_div" type="checkbox" name="division" value="ddbjphg" checked />Phages</td>
	</tr>
	<tr>
		<td><input class="std_div" type="checkbox" name="division" value="ddbjsyn" />Synthetic DNAs</td>
		<td><input class="std_div" type="checkbox" name="division" value="ddbjenv" />ENV</td>
        <td></td>
        <td></td>
        <td></td>        
	</tr>
</table>

<table>
	<tr>
		<td colspan="3"><input id="ht_div_title" type="checkbox" name="div_class" value="high_throughput">
 <span style="font-size: 110%; font-weight: bold;">High throughput divisions</span></td>
	</tr>
	<tr>
		<td><input class="ht_div" type="checkbox" name="division" value="ddbjthg">HTG</td>
		<td><input class="ht_div" type="checkbox" name="division" value="ddbjpri">HTC</td>
		<td><input class="ht_div" type="checkbox" name="division" value="ddbjrod">TSA</td>
	</tr>
</table>

<table>
	<tr>
		<td colspan="5"><input id="est_div_title" type="checkbox" name="div_class" value="est">
 <span style="font-size: 110%; font-weight: bold;">EST divisions</span></td>
	</tr>
	<tr>
		<td><INPUT class="est_div" TYPE="checkbox" NAME="division" VALUE="est_atha">A.thaliana</td>
		<td><INPUT class="est_div" TYPE="checkbox" NAME="division" VALUE="est_btra">B.taurus</td>
		<td><INPUT class="est_div" TYPE="checkbox" NAME="division" VALUE="est_cele">C.elegans</td>
		<td><INPUT class="est_div" TYPE="checkbox" NAME="division" VALUE="est_cint">C.intestinalis</td>
		<td><INPUT class="est_div" TYPE="checkbox" NAME="division" VALUE="est_crei">C.reinhardtii</td>
	</tr>
	<tr>
		<td><INPUT class="est_div" TYPE="checkbox" NAME="division" VALUE="est_ddis">D.discoideum</td>
		<td><INPUT class="est_div" TYPE="checkbox" NAME="division" VALUE="est_dmel">D.melanogaster</td>
		<td><INPUT class="est_div" TYPE="checkbox" NAME="division" VALUE="est_drer">D.rerio</td>
		<td><INPUT class="est_div" TYPE="checkbox" NAME="division" VALUE="est_ggal">G.gallus</td>
		<td><INPUT class="est_div" TYPE="checkbox" NAME="division" VALUE="est_gmax">G.max</td>
	</tr>
	<TR>
		<td><INPUT class="est_div" TYPE="checkbox" NAME="division" VALUE="est_hum">H.sapiens</td>
		<td><INPUT class="est_div" TYPE="checkbox" NAME="division" VALUE="est_hvul">H.vulgare</td>
		<td><INPUT class="est_div" TYPE="checkbox" NAME="division" VALUE="est_mous">M.musculus</td>
		<td><INPUT class="est_div" TYPE="checkbox" NAME="division" VALUE="est_mtru">M.truncatula</td>
		<td><INPUT class="est_div" TYPE="checkbox" NAME="division" VALUE="est_osat">O.sativa</td>
	</tr>
	<tr>
		<td><INPUT class="est_div" TYPE="checkbox" NAME="division" VALUE="est_rnor">
			R.norvegicus</td>
		<td><INPUT class="est_div" TYPE="checkbox" NAME="division" VALUE="est_slyc">S.lycopersicum</td>
		<td><INPUT class="est_div" TYPE="checkbox" NAME="division" VALUE="est_taes">T.aestivum</td>
		<td><INPUT class="est_div" TYPE="checkbox" NAME="division" VALUE="est_xlae">X.laevis</td>
		<td><INPUT class="est_div" TYPE="checkbox" NAME="division" VALUE="est_xtro">X.tropicalis</td>
	</tr>
	<tr>
		<td><INPUT class="est_div" TYPE="checkbox" NAME="division" VALUE="est_zmay">
			Z.mays</td>
		<td></td>
		<td colspan="3"><INPUT class="est_div" TYPE="checkbox" NAME="division"
			VALUE="est_rest">Others</td>

	</tr>
</table>

<table>
	<tr>
		<td COLSPAN="4"><input id="other_div_title" type="checkbox" name="div_class" value="other">
 <span style="font-size: 110%; font-weight: bold;">Other divisions</span></td>
	</tr>
	<tr>
		<td><INPUT class="other_div" TYPE="checkbox" NAME="division" VALUE="ddbjpat">Patent</td>
		<td><INPUT class="other_div" TYPE="checkbox" NAME="division" VALUE="ddbjuna">Unannotated Seq</td>
		<td><INPUT class="other_div" TYPE="checkbox" NAME="division" VALUE="ddbjgss">GSS</td>
		<td><INPUT class="other_div" TYPE="checkbox" NAME="division" VALUE="ddbjsts">STS</td>
	</tr>
</table>


<!-- 
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
								onclick="append_children('gss','ja')">GSS: short single
								passのゲノム配列データ
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
	        
			<input id='hiddendiv' type='hidden' name='division' value='ddbjall'>
	        -->
