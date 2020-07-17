<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<!DOCTYPE html>
<html dir="ltr" lang="ja">
<head>
<meta charset="UTF-8" />
<title><tiles:insertAttribute name="title" ignore="true" /></title>
<style type="text/css">
div {
  border-style: solid;
  border-width: 1px;
  border-color: black;
  background-color: khaki;
  margin: 10px;
}
</style>
<script type="text/javascript" src="../../resources/js/jquery-1.7.2.min.js"></script>
<script type="text/javascript">
$(document).ready(function() {
  $('#id_button_get').click(function() {
    var url = "/wabi/blast/" + $('#id_get_requestId').val() + "?format=" + $('#id_get_format').val();
    $.ajax({
      type: 'GET',
      url: url,
      dataType: 'json',
      cache: false,
      success: function(data, dataType) {
        $('#id_get_textarea_response').text("success:" + data);
      },
      error: function(xmlreq, textStatus, errorThrown) {
        $('#id_get_textarea_response').text("error:" + xmlreq.status);
      }
    });
  });
  $('#id_button_getenv').click(function() {
    var url = "/wabi/blast?format=" + $('#id_getenv_format').val() + "&info=" + $('#id_getenv_info').val();
    $.ajax({
      type: 'GET',
      url: url,
      dataType: 'json',
      cache: false,
      success: function(data, dataType) {
        $('#id_getenv_textarea_response').text("success:" + data);
      },
      error: function(xmlreq, textStatus, errorThrown) {
        $('#id_getenv_textarea_response').text("error:" + xmlreq.status);
      }
    });
  });
  $('#id_button_help').click(function() {
    var url = "/wabi/blast/help/" + $('#id_help_helpCommand').val() + "?format=" + $('#id_help_format').val() + "&program=" + $('#id_help_program').val();
    $.ajax({
      type: 'GET',
      url: url,
      dataType: 'json',
      cache: false,
      success: function(data, dataType) {
        $('#id_help_textarea_response').text("success:" + data);
      },
      error: function(xmlreq, textStatus, errorThrown) {
        $('#id_help_textarea_response').text("error:" + xmlreq.status);
      }
    });
  });
});
</script>
</head>

<body>

<div>
  <form action="/wabi/blast" method="post">
    <ul>
      <li><input type="text" size="100" name="datasets"   value="ddbjall"                         > datasets   </li>
      <li><input type="text" size="100" name="database"   value="hum"                             > database   </li>
      <li><input type="text" size="100" name="program"    value="blastn"                          > program    </li>
      <li><input type="text" size="100" name="parameters" value=" -v 100 -b 100 -e 10 -F F -W 11" > parameters </li>
      <li><input type="text" size="100" name="format"     value="json"                            > format     </li>
      <li><input type="text" size="100" name="result"     value="www"                             > result     </li>
      <li><input type="text" size="100" name="address"    value=""                                > address    </li>
      <li><textarea name="querySequence" rows="5" cols="100">
>my query sequence 1
CACCCTCTCTTCACTGGAAAGGACACCATGAGCACGGAAAGCATGATCCAGGACGTGGAA
GCTGGCCGAGGAGGCGCTCCCCAGGAAGACAGCAGGGCCCCAGGGCTCCAGGCGGTGCTG
GTTCCTCAGCCTCTTCTCCTTCCTGCTCGTGGCAGGCGCCGCCAC</textarea> querySequence </li>
    </ul>
    <input type="submit">
  </form>
</div>

<div>
  <form action="/wabi/blast" method="get">
    <ul>
      <li><input id="id_get_requestId" type="text" size="100" name="requestId" value="wabi_blast_2013-0628-1327-07-535-827844" > requestId </li>
      <li><input id="id_get_format"    type="text" size="100" name="format"    value="json"                                    > format    </li>
      <li><input id="id_get_imageId"   type="text" size="100" name="imageId"   value=""                                        > imageId   </li>
      <li><input id="id_get_info"      type="text" size="100" name="info"      value="request"                                 > info      </li>
    </ul>
    <input type="button" id="id_button_get" value="GET">
    <textarea id="id_get_textarea_response" rows="5" cols="100"></textarea>
  </form>
</div>

<div>
  <a href="/wabi/blast">getenv</a>
  <form action="/wabi/blast" method="get">
    <ul>
      <li><input id="id_getenv_format" type="text" size="100" name="format" value="json"    > format </li>
      <li><input id="id_getenv_info"   type="text" size="100" name="info"   value="request" > info   </li>
    </ul>
    <input type="button" id="id_button_getenv" value="GETENV">
    <textarea id="id_getenv_textarea_response" rows="5" cols="100"></textarea>
  </form>
</div>

<div>
  <a href="/wabi/blast/help">help</a>
  <form action="/wabi/blast/help" method="get">
    <ul>
      <li><input id="id_help_format"      type="text" size="100" name="format"      value="json" > format      </li>
      <li><input id="id_help_program"     type="text" size="100" name="program"     value=""     > program     </li>
      <li><input id="id_help_helpCommand" type="text" size="100" name="helpCommand" value=""     > helpCommand </li>
    </ul>
    <input type="button" id="id_button_help" value="GET help">
    <textarea id="id_help_textarea_response" rows="5" cols="100"></textarea>
  </form>
</div>

</body>
</html>
