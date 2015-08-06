/**
 * Created by berz on 18.05.15.
 */


var adminPagesEngine = {
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
        userInfoService: {
            pages: ["administrator/index"],
            engine: null,
            table : null,
            init: function (engine) {

                this.engine = engine;

                var cont = $('div#userInfoContainer');

                if (cont.length != 1) {
                    alert('Произошла ошибка. Сервис просмотра данных вкладчиков не запущен');
                    return;
                }

                this.infoContainer = cont;
                this.infoContainerPreloadHTML = cont.html();

                if($("#nonActivated").length > 0){
                    this.table = $("#nonActivated");
                }
                else{
                    alert("Сервис просмотра данных вкладчиков не запущен: не найдена основная таблица");
                }

                this.bind();
            },
            bind: function () {
                $('a.userInfoLink').click(
                    (function (eng, service) {
                        return function () {
                            $('tr', service.table).removeClass("info");
                            $(this).parents('tr').eq(0).addClass("info");

                            service.infoContainer.html(service.infoContainerPreloadHTML);
                            service.infoContainer.fadeIn();


                            service.bindWindow();

                            $.get(
                                this, function (d) {
                                    service.infoContainer.html(d);
                                    service.bindWindow();
                                }
                            );

                            return false;
                        }
                    })(this.engine, this)
                );
            },
            bindWindow: function () {
                $('div.block-controls span.block-control-remove', this.infoContainer).click(
                    (function (service) {
                        return function () {
                            service.closeWindow();
                        }
                    })(this)
                );
            },
            closeWindow: function () {
                this.infoContainer.fadeOut();
                $('tr', this.table).removeClass("info");
            },
            infoContainer: null,
            infoContainerPreloadHTML: null
        },
        depositorSettingsService: $commonServices.liveForms(
            ["administrator/depositorTypeSettings", "administrator/moneymotion/index", "administrator/depositors/card"],
            ['minSum', 'maxSum', 'sumFrom'],
            ["sumSettingsForm", "percentsForm", "moneyMotionForm"]
        )
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
        }
    }
}