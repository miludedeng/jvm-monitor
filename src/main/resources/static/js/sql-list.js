$(function() {
    var serverBasic = '';

    var trTemplate = '<tr class="odd gradeX">\n\
                            <td>{{Cost}}</td>\n\
                            <td>{{Sql}}</td>\n\
                            <td>{{Date}}</td>\n\
                            <td><input type="button" value="view" class="btn btn-default btn-sm btn-sql-view"  data-toggle="modal" data-target="#sqlModal" data-info="{{Detail}}"></td>\n\
                        </tr>';
    //加载agent按钮添加事件
    $('.btn-load-sql-agent').click(function(){
        $.get(serverBasic + 'sql_stat/load_agent/' + $("#vm-id").val(), function(response) {
            response = eval("(" + response + ")");
            if("success"==response.status){
                console.log(response.message);
                alert("成功载入sql agent");
            }else{
                console.log(response.message);
                alert("载入失败");
            }
        });
    });

});
