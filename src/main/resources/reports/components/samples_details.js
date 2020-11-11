$('#contamination-table tbody').on('click', 'tr', function() {

  if (window.row) {
    $(window.row).removeClass('bg-secondary text-white');
  }

  $(this).addClass('bg-secondary text-white');
  window.row = this;

  var data = window.table.row(this).data();
  var details = document.getElementById('detailsSample');
  details.innerHTML = `{{include "samples_details.html" }}`;

  window.data = data;

});

$(document).on("click", ".details-phylotree", function(e) {
  var data = window.data;
  var dialog = bootbox.dialog({
    title: "Phylogenetic Tree for <b>" + data.id + "</b>",
    message: '<div id="mynetwork2"></div>',
    show: false
  });

  dialog.find("div.modal-dialog").addClass("modal-lg");

  dialog.on('shown.bs.modal', function() {


    // create a network
    var networkContainer = document.getElementById('mynetwork2');
    var data = window.data;

    var dataNetwork = {
      nodes: data.nodes,
      edges: data.edges
    };
    var options = {
      layout: {
        hierarchical: {
          direction: "LR",
          sortMethod: "directed",
          parentCentralization: true
        }
      },
      edges: {
        shadow: true,
        length: 100,
        arrows: {
          to: {
            enabled: true
          }
        },
        font: {
          align: 'top',
          size: 15
        },
        widthConstraint: {
          maximum: 50
        }
      }
    };

    var network = new vis.Network(networkContainer, dataNetwork, options);
  });

  dialog.modal('show');

});
