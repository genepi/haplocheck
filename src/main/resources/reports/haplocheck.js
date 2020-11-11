$(document).ready(function() {

//icons
feather.replace()

$('#samples').on('click', function() {
  $("#samples").addClass('active');
  $("#summary").removeClass('active');
  $("#about").removeClass('active');
  $("#samples-panel").show();
  $("#summary-panel").hide();
  $("#about-panel").hide();
});

$('#summary').on('click', function() {
  $("#samples").removeClass('active');
  $("#summary").addClass('active');
  $("#about").removeClass('active');
  $("#samples-panel").hide();
  $("#summary-panel").show();
  $("#about-panel").hide();
});

$('#about').on('click', function() {
  $("#samples").removeClass('active');
  $("#summary").removeClass('active');
  $("#about").addClass('active');
  $("#samples-panel").hide();
  $("#summary-panel").hide();
  $("#about-panel").show();
});

{{include "components/samples.js"}}
{{include "components/summary.js"}}

});
