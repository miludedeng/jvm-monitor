$(function() {
    var serverBasic = '/';

    var trTemplate = '<tr class="odd gradeX">\n\
                            <td>{{Method}}</td>\n\
                            <td>{{Precent}}%</td>\n\
                            <td>{{Time}}s</td>\n\
                        </tr>';
    var listCount = 25;
    var freshProfileList = function(data) {
        $("#profile-list").html("");
        for (var i = 0; i < data.length; i++) {
            if (data[i].error) {
                continue;
            }
            var temp = trTemplate.replace(/{{Method}}/g, data[i].Method).replace(/{{Precent}}/g, data[i].Precent.toFixed(2)).replace(/{{Time}}/g, data[i].Time.toFixed(2));
            $("#profile-list").append(temp);
        }
    }

    setInterval(function() {
        if (!$("#profile-panel").is(':visible')) {
            return;
        }
        $.get(serverBasic + '/vm_profile_list/' + $("#vm-id").val() + "/" + listCount, function(data) {
            freshProfileList(eval("(" + data + ")"));
        });
    }, 1000);

    $(".profile-list-count").change(function() {
        listCount = $(this).val();
    });

});
