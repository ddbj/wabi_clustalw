<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>

<script type="text/javascript">

$(function(){

  $('#std_div_title').click(function(){
    if (this.checked) {
      $('.std_div').attr('checked', 'checked');
    }
    else {
      $('.std_div').removeAttr('checked');
    }
  });

  $('#ht_div_title').click(function(){
    if (this.checked) {
      $('.ht_div').attr('checked', 'checked');
    }
    else {
      $('.ht_div').removeAttr('checked');
    }
  });


  $('#est_div_title').click(function(){
    if (this.checked) {
      $('.est_div').attr('checked', 'checked');
    }
    else {
      $('.est_div').removeAttr('checked');
    }
  });

  $('#other_div_title').click(function(){
    if (this.checked) {
      $('.other_div').attr('checked', 'checked');
    }
    else {
      $('.other_div').removeAttr('checked');
    }
  });

});
</script>
