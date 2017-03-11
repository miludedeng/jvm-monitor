/*!
 * Start Bootstrap - SB Admin 2 v3.3.7+1 (http://startbootstrap.com/template-overviews/sb-admin-2)
 * Copyright 2013-2017 Start Bootstrap
 * Licensed under MIT (https://github.com/BlackrockDigital/startbootstrap/blob/gh-pages/LICENSE)
 */
$.fn.pageBar = function(total, index, size) {
    pageTotal = parseInt(total % size == 0 ? total / size : (total / size + 1));
    pageSize = size;
    this.html("");
    var firstEle = '<li ' + (index == 1 ? 'class="disabled"' : '') + '>\n\
        <a href="javascript:;" ' + (index == 1 ? 'onclick="return false;"':'') + ' data-index="1" class="' + (index != 1 ? 'go-page':'') + ' Prev"><<</a>\n\
        </li>';
    var prevEle = '<li ' + (index == 1 ? 'class="disabled"' : '') + '>\n\
        <a href="javascript:;" ' + (index == 1 ? 'onclick="return false;"':'') + ' data-index=' + (index-1) + ' class="' + (index != 1 ? 'go-page':'') + ' Prev">Prev</a>\n\
        </li>';
    this.append(firstEle);
    this.append(prevEle);
    if (pageTotal > 5) {
        var i = 1;
        if (pageTotal - index <= 2) {
            i = pageTotal - 4;
        } else if (index <= 3) {
            i = 1;
        } else {
            i = index - 2;
        }
        for (var j = 0; j < 5; j++, i++) {
            var ele = '<li ' + (index == i ? 'class="active"' : '') + '><a href="javascript:;" data-index="' + i + '" class="go-page">' + i + '</a></li>';
            this.append(ele);
        }
    } else {
        for (var i = 1; i <= pageTotal; i++) {
            var ele = '<li ' + (index == i ? 'class="active"' : '') + '><a href="javascript:;" data-index="' + i + '" class="go-page">' + i + '</a></li>';
            this.append(ele);
        }
    }
    var nextEle = '<li ' + (index == pageTotal ? 'class="disabled" disabled' : '') + '>\n\
        <a href="javascript:;" data-index=' + (index+1) + ' class="' + (index != pageTotal ? 'go-page':'') + ' Next">Next</a>\n\
        </li>';
    var lastEle = '<li ' + (index == pageTotal ? 'class="disabled" disabled' : '') + '>\n\
        <a href="javascript:;" data-index="'+pageTotal+'" class="' + (index != pageTotal ? 'go-page':'') + ' Next">>></a>\n\
        </li>';
    this.append(nextEle);
    this.append(lastEle);
};
