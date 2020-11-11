window.table = $('#contamination-table').DataTable({
  "pageLength": 10,
  "pagingType": "simple",
  "scrollY": "330px",
  "order": [
    [1, "desc"]
  ],
  "scrollCollapse": true,
  buttons: ['csv'],
  "columnDefs": [{
      "targets": [1],
      "orderData": [1, 2, 3],
      "visible": true,
      "searchable": false
    },
    {
      "targets": [2],
      "visible": true,
      "searchable": false
    },
    {
      "targets": [3],
      "visible": true,
      "searchable": false
    }
  ],
  data: {{json(cont_data)}},
  "columns": [{
      "data": "id"
    },
    {
      "data": "status"
    },
    {
      "data": "overallLevel"
    },
    {
      "data": "sampleMeanCoverage"
    }
  ]
});

{{include "samples_details.js"}}
