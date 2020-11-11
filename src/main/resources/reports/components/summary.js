$('#summary-table').DataTable({
  "pageLength": 10,
  data: [{{sum_data}}],
  "columns": [{
      "data": "Yes"
    },
    {
      "data": "No"
    },
    {
      "data": "Coverage"
    },
    {
      "data": "Q1"
    },
    {
      "data": "Q3"
    }
  ]
});
