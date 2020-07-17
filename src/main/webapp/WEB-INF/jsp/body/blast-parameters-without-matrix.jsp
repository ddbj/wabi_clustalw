<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>


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
					<td>
					<INPUT TYPE="radio" NAME="filter" VALUE="T"> ON 
					<INPUT TYPE="radio" NAME="filter" VALUE="F" CHECKED> OFF
					</td>
				</tr>
				<tr>
					<td>WORD SIZE</td>
					<td><INPUT TYPE="number" ID="wordsize" NAME="wordsize"
						VALUE="${word_size}" SIZE=5></td>
				</tr>
				<tr><td width="50">OTHER OPTIONS</td><td><INPUT TYPE="TEXT" NAME="options" SIZE="40"></td></tr>
			</table>
