/**
 * Created by berz on 13.05.15.
 */
//////////////////////////////
// Main Dashboard Functions //
//////////////////////////////

"use strict";
var loadGraph = function ($) {
    $(document).ready(function () {


        /**
         * circloidDateRangeChart Charts with Date Range Selector
         * @param  {string} placeholder:        id or class of parent block
         * @param  {string} graphPlaceholder:    id of graph
         * @param  {string} graphType:            enter graph type [bar, line, pie, donut]
         */
        function circloidDateRangeChart(placeholder, graphPlaceholder, graphType) {

            if (graphType === undefined || graphType == null) {
                graphType = "line";
            }

            /* Display loading icon */
            $(placeholder).closest(".block").find(".block-controls .icon.loading").remove();
            $(placeholder).closest(".block").find(".block-controls").append("<span class='icon loading' style='opacity:1;'></span>");


            lineChartFlot(graphPlaceholder);

            /**
             * lineChartFlot creates the line chart
             * @param {string} placeholder:    as stated in parent function
             */
            function lineChartFlot(placeholder) {
                /* Get parameters set in in placeholder */
                var colors = $(placeholder).data("graph-colors").split(',');
                var dateRange = $(placeholder).closest(".block").find(".date-range-select").val();

                /* SAMPLE DATA: This "data" variable contains SAMPLE DATA just to show you the format of the data that you need to pass into the chart */
                var data = {
                    "label1": {
                        "label": "Эти данные",
                        "data": [
                            [1436313600, 2052], // unix timestamp
                            [1436313600+20, 1460],
                            [1436313600+30, 1492],
                            [1436313600+40, 1794],
                            [1436313600+50, 1384],
                            [1436313600+60, 2122],
                            [1436313600+70, 2880],
                            [1436313600+80, 2545],
                            [1436313600+90, 3908],
                            [1436313600+100, 4935],
                            [1436313600+110, 3907],
                            [1436313600+120, 4937]
                        ]
                    },
                    "xaxis": [
                        [1436313600, "Jan"],
                        [1436313600+20, "Feb"],
                        [1436313600+30, "Mar"],
                        [1436313600+40, "Apr"],
                        [1436313600+50, "May"],
                        [1436313600+60, "Jun"],
                        [1436313600+70, "Jul"],
                        [1436313600+80, "Aug"],
                        [1436313600+90, "Sept"],
                        [1436313600+100, "Oct"],
                        [1436313600+110, "Nov"],
                        [1436313600+120, "Dec"]
                    ]
                };

                var options = {
                    series: {
                        lines: {
                            show: true,
                            fill: true,
                            lineWidth: 1.5
                        },
                        points: {
                            show: true,
                            radius: 6
                        }
                    },
                    shadowSize: 0,
                    grid: {
                        backgroundColor: '#FFFFFF',
                        borderColor: '#D6D6D9',
                        borderWidth: 1,
                        hoverable: true
                    },
                    legend: {
                        show: true,
                        position: "nw"
                    },
                    xaxis: {
                        ticks: data.xaxis
                    },
                    tooltip: true,
                    tooltipOpts: {
                        content: function (label, xval, yval, flotItem) {
                            return label + ": <b>" + yval.toFixed(2).replace(/\d(?=(\d{3})+\.)/g, '$&,') + "</b>";
                        },
                        shifts: {
                            x: -40,
                            y: 25
                        },
                        defaultTheme: false
                    },
                    colors: colors
                }


                var plotChart = $.plot(placeholder, [data.label1], options);

                if (plotChart) {
                    $(placeholder).closest(".block").find(".block-controls .icon-success").remove();
                    $(placeholder).closest(".block").find(".block-controls .icon-error").remove();
                    $(placeholder).closest(".block").find(".block-controls").append("<span class='icon icon-check icon-size-medium icon-success' style='opacity:1;'></span>");
                    $(placeholder).closest(".block").find(".block-controls .icon-success").delay(3000).fadeOut(1000, function () {
                        $(this).remove();
                    });
                    $(placeholder).closest(".block").find(".block-controls .icon.loading").remove();
                    return true;
                }

                return false;
            }
        }


        /* Call Functions */


        // Checks if element exists on the page before calling function
        if ($('#overview').length > 0) {
            circloidDateRangeChart("", "", "line");
        }

        circloidWidgets();

        //circloidTaskListWidget();
    });
}

var depositorPagesEngine = {
    $: null,
    maskMoneySettings: {
        thousands: ',',
        decimal: '.',
        precision: 0
    },
    maskPercentsSettings: {
        decimal: '.',
        precision: 2
    },
    init: function (jQuery, page) {
        this.$ = jQuery;

        for (var s in this.services) {
            if (typeof (this.services[s].pages) !== 'undefined') {
                if ($.inArray(page, this.services[s].pages) > -1) {
                    this.services[s].init(this);
                }
            }
            else {
                this.services[s].init(this);
            }
        }

        if ($('div#preloadimage').length > 0) {
            this.utils.loadingStatus.init($('div#preloadimage').html());
        }
        else {
            alert('Произошла ошибка. Утилита отображения статуса загрузки не запущена!');
        }

    },
    services: {
        moneyMotionForms: $commonServices.liveForms(
            ["depositor/moneymotion"],
            ["sum"],
            ["moneyMotionForm"]
        ),
        graph: {
            pages: ["depositor"],
            placeholder : ".block.overview-block",
            graphPlaceholder: "#overview",
            graphData: null,
            engine: null,
            colors: null,
            dateRange: null,
            init: function(engine){
                this.engine = engine;
                this.loadData();
                this.colors = $(this.graphPlaceholder).data("graph-colors").split(',');
                this.dateRange = $(this.graphPlaceholder).closest(".block").find(".date-range-select").val();
            },
            loadData : function(){
                $.get(
                    this.engine.utils.urlPrefix + "rest/depositor/graph",
                    (function(service){
                        return function(d){

                            var all = d['all'];
                            var available = d['available'];

                            if(typeof(all) == 'undefined' || typeof(available) == 'undefined'){
                                alert("Проблема с загрузкой данных для графиков");
                                return;
                            }



                            $(service.placeholder).closest(".block").find(".block-controls .icon.loading").remove();
                            $(service.placeholder).closest(".block").find(".block-controls").append("<span class='icon loading' style='opacity:1;'></span>");

                            var plotChart = $.plot(
                                service.graphPlaceholder,
                                [
                                    {
                                        'label': 'Все средства', 'data': service.valuesDataTransform(all)
                                    },
                                    {
                                        'label': 'Доступно для снятия', 'data': service.valuesDataTransform(available)
                                    }
                                ],
                                service.options(service.xaxisDataTransform(all))
                            );


                            if (plotChart) {
                                $(service.graphPlaceholder).closest(".block").find(".block-controls .icon-success").remove();
                                $(service.graphPlaceholder).closest(".block").find(".block-controls .icon-error").remove();
                                $(service.graphPlaceholder).closest(".block").find(".block-controls").append("<span class='icon icon-check icon-size-medium icon-success' style='opacity:1;'></span>");
                                $(service.graphPlaceholder).closest(".block").find(".block-controls .icon-success").delay(3000).fadeOut(1000, function () {
                                    $(this).remove();
                                });
                                $(service.graphPlaceholder).closest(".block").find(".block-controls .icon.loading").remove();


                                return true;
                            }
                        }
                    })(this)
                );
            },
            valuesDataTransform: function(data){
                if(typeof(data['values']) == 'undefined') return null;
                var rt = [];

                for(var i in data['values']){
                    rt.push([data['values'][i].timestamp, data['values'][i].saldo]);
                }

                return rt;
            },
            xaxisDataTransform: function(data){
                if(typeof(data['descriptions']) == 'undefined') return null;
                var rt = [];

                for(var i in data['descriptions']){
                    rt.push([data['descriptions'][i].timestamp, data['descriptions'][i].description]);
                }

                return rt;
            },
            options: function(xaxis){
                return {
                    series: {
                        lines: {
                            show: true,
                                fill: true,
                                lineWidth: 1.5
                        },
                        points: {
                            show: true,
                                radius: 6
                        }
                    },
                    shadowSize: 0,
                        grid: {
                    backgroundColor: '#FFFFFF',
                        borderColor: '#D6D6D9',
                        borderWidth: 1,
                        hoverable: true
                },
                    legend: {
                        show: true,
                            position: "nw"
                    },
                    xaxis: {
                        ticks: xaxis
                    },
                    yaxis: {
                        tickFormatter: function(v, axis){
                            return (v == 0)? 0 :
                                (v < 1000000)? (v/1000) + " тыс." :
                                    (v/1000000) + " млн.";
                        }
                    },
                    tooltip: true,
                        tooltipOpts: {
                    content: function (label, xval, yval, flotItem) {
                        var dt = new Date(xval);
                        return label + ": <b>" + yval.toFixed(2).replace(/\d(?=(\d{3})+\.)/g, '$&,') + "</b>"
                            + "<br /><b>" + (dt.getDay()+1) + "." + (dt.getMonth()+1) + "." + dt.getFullYear() + "</b>";
                    },
                    shifts: {
                        x: -40,
                            y: 25
                    },
                    defaultTheme: false
                },
                    colors: this.colors
                }
            }
        }
    },
    utils: {
        loadingStatus: {
            imageHTML: null,
            init: function (image) {
                this.imageHTML = image;
            },
            run: function (element) {
                if (this.imageHTML != null)
                    $(element).html(this.imageHTML);
            }
        },
        urlPrefix: '/'
    }
}
