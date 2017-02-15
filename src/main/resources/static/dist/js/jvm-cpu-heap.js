/*!
 * Start Bootstrap - SB Admin 2 v3.3.7+1 (http://startbootstrap.com/template-overviews/sb-admin-2)
 * Copyright 2013-2017 Start Bootstrap
 * Licensed under MIT (https://github.com/BlackrockDigital/startbootstrap/blob/gh-pages/LICENSE)
 */
$(function() {
    var serverBasic = 'http://'+location.host+'/';
    var option1 = {
        tooltip: {
            formatter: "{a} <br/>{b} : {c}%"
        },
        series: [{
            name: 'CPU使用率',
            type: 'gauge',
            detail: { formatter: '{value}%' },
            data: [{ value: 50, name: 'CPU' }]
        }]
    };
    var option2 = {
        tooltip: {
            formatter: "{a} <br/>{b} : {c}%"
        },
        series: [{
            name: 'Heap使用率',
            type: 'gauge',
            detail: { formatter: '{value}%' },
            data: [{ value: 50, name: 'Heap' }]
        }]
    };
    var cpuUsage = null;
    var heapUsage = null;

    var setSpacePrecent = function(name, precent) {
        var height = 0;
        if (name === 'old') height = 347;
        if (name === 'eden') height = 205;
        if (name === 'from' || name === 'to') height = 48;
        $('#' + name + '-top').css('height', height * (1 - precent));
        $('#' + name + '-foot').css('height', height * precent);
    }

    var freshVmMonitor = function(data) {
        option1.series[0].data[0].value = (data.CPUPrecent).toFixed(1) - 0;
        cpuUsage.setOption(option1, true);
        option2.series[0].data[0].value = (data.HeapPrecent).toFixed(1) - 0;
        heapUsage.setOption(option2, true);

        $('#heap-used').text(data.HeapUsed);
        $('#heap-max').text(data.HeapMax);
        $('#non-heap-used').text(data.NonHeapUsed);
        $('#non-heap-max').text(data.NonHeapMax);

        $('#up-time').text(data.UpTime);

        $('#thr-count').text(data.ThreadCount);
        $('#thr-max-count').text(data.ThreadPeak);
        $('#total-thr-count').text(data.ThreadCreated);

        $('#class-loaded').text(data.TotalLoadedClasses);

        $('#gc-time').text(data.GCTime);
        $('#gc-runs').text(data.GCRuns);

        $('#old-mem').text(data.OldSpaceUsed + " / " + data.OldSpaceCap + " / " + data.OldSpaceMax);
        $('#eden-mem').text(data.EdenSpaceUsed + " / " + data.EdenSpaceCap + " / " + data.EdenSpaceMax);
        $('#sur1-mem').text(data.Sur1SpaceUsed + " / " + data.Sur1SpaceCap + " / " + data.Sur1SpaceMax);
        $('#sur2-mem').text(data.Sur2SpaceUsed + " / " + data.Sur2SpaceCap + " / " + data.Sur2SpaceMax);

        var precent1 = data.OldSpacePrecent.toFixed(2);
        setSpacePrecent('old', precent1);
        var precent2 = data.EdenSpacePrecent.toFixed(2);
        setSpacePrecent('eden', precent2);
        var precent3 = data.Sur1SpacePrecent.toFixed(2);
        setSpacePrecent('from', precent3);
        var precent4 = data.Sur2SpacePrecent.toFixed(2);
        setSpacePrecent('to', precent4);
    }

    setInterval(function() {
        if (!$("#mon-panel").is(':visible')) {
            return;
        }
        if (!cpuUsage) {
            cpuUsage = echarts.init(document.getElementById('cpuPrecent'));
        }
        if (!heapUsage) {
            heapUsage = echarts.init(document.getElementById('heapPrecent'));
        }
        $.get(serverBasic + '/vm_mon_info/' + $("#vm-id").val(), function(data) {
            if (data.error) {
                return false;
            }
            freshVmMonitor(eval("(" + data + ")"));
        });

    }, 1000);

});
