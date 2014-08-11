<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
   pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
   <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" 
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Hello World</title>
<script src="//code.jquery.com/jquery-1.11.0.min.js"></script>
  <script src="//code.jquery.com/ui/1.11.0/jquery-ui.js"></script>
   <script type="text/javascript">
//   var initHeader = $.post("getHeaderInfo.action", function(data, textStatus) {
		
//		}, "json");
//	initHeader.done(function(){
   var labels = [];
   var subteam_count=0;
	
		$.post("getJSONResult.action", window.location.search.substring(1), function(data, textStatus) {
			$.each(data.columns_label, function(subteam, status){
				labels[subteam_count]="";
				var column_count=0;
				$(status).each(function() {
					$.each(this, function(label_id, label){
						if($('#block'+column_count).length == 0){
							var newTable = '<td><div id="block'+column_count+'">'+
							'<table border="1" width="100%" id="'+label_id+'"><thead><tr><th>';
							if(Object.keys(data.columns_label).length > 1)
							{
								newTable = newTable+subteam+': '+label;
							}
							else{
								newTable = newTabl+label;
							}
							newTable = newTable+'</th></tr></thead><tbody id="'+label_id+'_body" class="connectedSortable'+subteam_count+'"><tr class="sort-disabled"><td></td></tr></tbody>'+
							'</table>'+
							'<br/></div></td>';
							$(newTable).appendTo('#main_table');
							labels[subteam_count] += "#"+label_id+"_body, ";
						}
						else{
							var newTable = '<table border="1" width="100%" id="'+label_id+'"><thead><tr><th>';
							if(Object.keys(data.columns_label).length > 1)
							{
								newTable = newTable+subteam+': '+label;
							}
							else{
								newTable = newTabl+label;
							}
							newTable = newTable+'</th></tr></thead><tbody id="'+label_id+'_body" class="connectedSortable'+subteam_count+'"><tr class="sort-disabled"><td></td></tr></tbody></table><br/>';
							$(newTable).appendTo('#block'+column_count);
							labels[subteam_count] += "#"+label_id+"_body, ";
						}
						column_count++;
					});
				});
				subteam_count++;
			});
			$(data.columns).each(function() {
				var columnName = this["_column"];
				var body = this["_tracking_id"] + " " + this["_name"] + "<br/>" + this["_description"];
				if(body !== "")
					$('#'+columnName).find('tbody:last').append("<tr id='"+this["_id"]+"'><td>"+body+"</td></tr>");
		 	});
			}
		, "json").done(function(){
				for(var i=0; i<subteam_count; i++){
				var oldList, newList, item;
				$(labels[i].substring(0,labels[i].length-2) ).sortable({
			        start: function(event, ui) {
			            item = ui.item;
			            newList = oldList = ui.item.parent().parent();
			        },
			        stop: function(event, ui) {
			        	var dataToSend = "project_id="+item.attr('id')+"&status_id="+newList.attr('id');
			        	$.post("updateLabel.action", dataToSend , "json");
			        },
			        change: function(event, ui) {  
			            if(ui.sender) newList = ui.placeholder.parent().parent();
			        },
				    connectWith: ".connectedSortable"+i,
				    items: ">*:not(.sort-disabled)",
			   }).disableSelection();
			}
		});
		//});
   

</script>
</head>
<body>
<table border="1" id="main_table">
</table>
</body>
</html>