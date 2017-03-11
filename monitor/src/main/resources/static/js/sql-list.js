$(function() {
    var serverBasic = '';
    var trTemplate = '<tr class="odd gradeX">\n\
                            <td>{{Cost}}</td>\n\
                            <td class="sql-text">{{Sql}}</td>\n\
                            <td>{{Date}}</td>\n\
                            <td><input type="button" value="view" class="btn btn-default btn-sm btn-sql-view"  data-toggle="modal" data-target="#sqlModal" data-info="{{Detail}}"></td>\n\
                        </tr>';
    //加载agent按钮添加事件
    $('.btn-load-sql-agent').click(function() {
        $.get(serverBasic + 'sql_stat/load_agent/' + $("#vm-id").val(), function(response) {
            response = eval("(" + response + ")");
            if ("success" == response.status) {
                console.log(response.message);
                alert("成功载入sql agent");
                dealBtnAndLoadList("success","true");
            } else {
                console.log(response.message);
                alert("载入失败");
            }
        });
    });
    $('a[data-id="sql-panel"]').click(function() {
        $.get(serverBasic + 'sql_stat/is_load_agent/' + $("#vm-id").val(), function(response) {
            response = eval("(" + response + ")");
            dealBtnAndLoadList(response.status, response.message);
        });
    });

    $(".vm-list").on('click', '.vm-title', function() {
        var vmId = $(this).attr('data-vm-id');
        $("#sql-list").html('');
        $.get(serverBasic + 'sql_stat/is_load_agent/' + vmId, function(response) {
            response = eval("(" + response + ")");
            dealBtnAndLoadList(response.status, response.message);
        });
    });

    $(".btn-load-sql-list").click(function(){
        loadSqlList(1,10,true);
    });

    $(".btn-filter-submit").click(function(){
        loadSqlList(1,10,false);
    });

    var dealBtnAndLoadList = function(status, message) {
        if ("success" == status) {
            if ("true" == message) {
                $('.btn-load-sql-agent').addClass('disabled');
                $('.btn-load-sql-agent').attr("disabled", "true");
                $('.btn-load-sql-list').removeClass('disabled');
                $('.btn-load-sql-list').removeAttr("disabled");
                loadSqlList();
            } else {
                $('.btn-load-sql-agent').removeClass('disabled');
                $('.btn-load-sql-agent').removeAttr("disabled");
                $('.btn-load-sql-list').addClass('disabled');
                $('.btn-load-sql-list').attr("disabled", "true");
            }
        } else {
            $('.btn-load-sql-agent').addClass('disabled');
            $('.btn-load-sql-agent').attr("disabled", "true");
            $('.btn-load-sql-list').addClass('disabled');
            $('.btn-load-sql-list').attr("disabled", "true");
        }
    }

    var loadSqlList = function(index,size,refresh) {
        if (!$("#sql-panel").is(':visible')) {
            return false;
        }
        if(!index){
            index = 1;
        }
        if(!size){
            size = 10;
        }
        var url = serverBasic + 'sql_stat/load_sql_list/' + $("#vm-id").val() + '/'+size+'/'+index+'/';
        if(refresh){
            url += '1';
        }else{
            url += '0';
        }
        var min = $('.sql-filter-min').val();
        var max = $('.sql-filter-max').val();
        if(!min) min = 0;
        if(!max) max = 0;
        var params = {min: min,max: max};
        $.get(url, params, function(response) {
            response = eval("(" + response + ")");
            if(response.status=='failed'){
                return false;
            }
            var data = response.data.List;
            var total = response.data.Total;
            $("#sql-list").html('');
            for (var i = 0; i < data.length; i++) {
                var cost = data[i].cost;
                var sql = data[i].sql;
                var date = new Date(data[i].date.time).format('yyyy-MM-dd HH:mm:ss.S');
                var temp = trTemplate.replace(/{{Cost}}/g, cost + "ms").replace(/{{Sql}}/g, sql).replace(/{{Date}}/g, date);
                $("#sql-list").append(temp);
            }
            $('.sql-page-bar').pageBar(total,index,size);
        });
    }

    $('.sql-page-bar').on('click','.go-page',function(){
        var pageIndex = new Number($(this).attr('data-index'));
        loadSqlList(pageIndex);
    });

    $("#sql-list").on('click', '.btn-sql-view', function() {
        $('.sql-modal-body').html($(this).parent().parent().find(".sql-text").text());
    });

    Date.prototype.format = function(fmt) { //author: meizz 
        var o = {
            "M+": this.getMonth() + 1, //月份 
            "d+": this.getDate(), //日 
            "H+": this.getHours(), //小时 
            "m+": this.getMinutes(), //分 
            "s+": this.getSeconds(), //秒 
            "q+": Math.floor((this.getMonth() + 3) / 3), //季度 
            "S": this.getMilliseconds() //毫秒 
        };
        if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
        for (var k in o)
            if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
        return fmt;
    }

});
