/**
 * Created by berz on 21.05.15.
 */
var $commonServices = {
    liveForms :
        function(pages, moneyFields, trForms){
            /*
            * pages: ["administrator/depositorTypeSettings", "administrator/moneymotion/index"]
            * moneyFields: ['minSum', 'maxSum', 'sumFrom']
            *
             */
            return {
                pages: pages,
                engine: null,
                moneyFields: moneyFields,
                trForms: trForms,
                init: function (engine) {
                    var commonError = "Сервис работы с таблицами и формами не запущен. ";

                    if(typeof(engine) == 'undefined'){
                        alert(commonError + "Не задана ссылка на движок обработки страницы");
                        return;
                    }

                    if( typeof(engine.maskMoneySettings) == 'undefined'){
                        alert(commonError + "В движке обработки страницы не задана переменная maskMoneySettings, отвечающая за формат денежных единиц");
                    }

                    if( typeof(engine.utils) == 'undefined'){
                        alert(commonError + "В движке не реализован модуль utils");
                    }

                    if( typeof(engine.utils.loadingStatus) == 'undefined'){
                        alert(commonError + "В модуле utils движка сраницы не задана функция loadingStatus, которая отвечает за картинку статуса загрузки");
                    }

                    this.engine = engine;


                    var trForms = this.trForms;

                    for (var f in trForms) {
                        $("form." + trForms[f]).each(
                            (function (service) {
                                return function (i, e) {
                                    service.bindTrForm(e, trForms[f]);
                                }
                            })(this));
                    }

                    this.maskFields();

                },

                maskFields: function () {
                    $(
                        (function (service) {
                            var s = [];
                            for (var i in service.moneyFields) {
                                s.push('input[name=' + service.moneyFields[i] + ']');
                            }
                            return s.join(",");
                        })(this)
                    ).maskMoney(this.engine.maskMoneySettings);
                    $('input[name=length], input[name=minLength], input[name=maxLength], input[name=lengthFrom]').mask('999');
                    $('input[name=percent]').maskMoney(this.engine.maskPercentsSettings);
                },

                bindTrForm: function (form, cl) {
                    $('button.' + cl + '[type=submit]', $(form).parent("tr").eq(0)).click(
                        (function (service) {
                            return function () {
                                var dataPercents = service.getTrFormInfo(form, cl);
                                var parentElement = service.getTrFormParentElement(form);
                                service.engine.utils.loadingStatus.run(parentElement);

                                $.request({
                                    url: $(form).attr('action'),
                                    data: dataPercents,
                                    success: function (d) {
                                        service.updateTrFormInfo(
                                            parentElement, cl, d
                                        );
                                    }
                                });

                                return false;
                            }
                        })(this)
                    );
                    $('button.delete.' + cl + '[type=button]', $(form).parent("tr")).click(
                        (function (service) {
                            return function () {
                                var dataPercents = service.getTrFormInfo(form, cl);
                                var parentElement = service.getTrFormParentElement(form);
                                service.engine.utils.loadingStatus.run(parentElement);

                                $.ajax({
                                    method: 'DELETE',
                                    url: $(form).attr('action'),
                                    success: function (d) {
                                        service.updateTrFormInfo(
                                            parentElement, cl, d
                                        );
                                    }
                                });
                                return false;
                            }
                        })(this)
                    );
                },
                updateTrFormInfo: function (parentElement, cl, data) {
                    $(parentElement).html(data);
                    var forms = this.getFormsFromLoadedData(parentElement, cl);
                    forms.each(
                        (function (service) {
                            return function (i, form) {
                                var classList = form.className.split(/\s+/);
                                var realCl = null;
                                for(var cli in classList){
                                    if($.inArray(classList[cli], service.trForms) > -1){
                                        realCl = classList[cli];
                                    }
                                }
                                service.bindTrForm(form, realCl || cl);
                            }
                        })(this)
                    );

                    this.maskFields();
                },
                getFormsFromLoadedData: function (parentElement, cl) {
                    return $('form.' + cl + ', form.' + cl + 'Child', parentElement);
                },
                getTrFormParentElement: function (form) {
                    return $(form).parents('table').eq(0).parents().eq(0);
                },
                getTrFormInfo: function (form, cl) {
                    var data = {};
                    $('input.' + cl, $(form).parent("tr")).each(
                        (function (service) {
                            return function (i, e) {
                                data[$(e).attr('name')] = service.getVal(e);
                            }
                        })(this));
                    return data;
                },
                formDataConvertionToDelete: function (data) {
                    return {
                        _method: "DELETE"
                    }
                },
                getVal: function (e) {
                    if ($.inArray($(e).attr('name'), this.moneyFields) > -1) {
                        return ($(e).val().split(this.engine.maskMoneySettings.thousands).join(''));
                    }
                    else return $.properVal(e);
                }

            }
        },
    onlyNumericInput : function (inp, maxChars) {
        $(inp).each(function (ind, elem) {
            $(elem).keydown(function (e) {

                // Allow: backspace, delete, tab, escape, enter and .
                if ($.inArray(e.keyCode, [46, 8, 9, 27, 13, 110, 190]) !== -1 ||
                    // Allow: Ctrl+A
                    (e.keyCode == 65 && e.ctrlKey === true) ||
                    // Allow: Ctrl+C
                    (e.keyCode == 67 && e.ctrlKey === true) ||
                    // Allow: Ctrl+X
                    (e.keyCode == 88 && e.ctrlKey === true) ||
                    // Allow: home, end, left, right
                    (e.keyCode >= 35 && e.keyCode <= 39)) {
                    // let it happen, don't do anything
                    return;
                }
                // limit length
                if (typeof maxChars !== 'undefined') {

                    //var val = (typeof($(elem).data('mask')) != 'undefined')? $(elem).cleanVal() : $(elem).val();
                    var val = $.properVal(elem);

                    if (val.length >= maxChars)
                        e.preventDefault();
                }
                // Ensure that it is a number and stop the keypress
                if ((e.shiftKey || (e.keyCode < 48 || e.keyCode > 57)) && (e.keyCode < 96 || e.keyCode > 105)) {
                    e.preventDefault();
                }
            });
        });
    }
}

$(document).ready(function () {
    $.properVal = function (e) {
        return (typeof($(e).data('mask')) != 'undefined' && !$(e).hasClass('date')) ? $(e).cleanVal() : $(e).val();
    },
    /**
     * Запрос, модифицированный особым образом, для большей совместимости со Spring MVC
     */
        $.request = function (input) {

            if(typeof(input.method) == 'undefined'){
                input.method = 'POST';
            }

            if(typeof(input.data) == 'object'){
                if(typeof(input.data['_method']) != 'undefined'){
                    input.method = input.data['_method'];
                    delete input.data['_method'];
                }
            }

            if (input.method != 'POST'){
                input.contentType = "application/json";
                input.data = JSON.stringify(input.data);
            }

            $.ajax(input);
        },

        $("a.submitParentFormLink").click(function(){
            $(this).parent("form").submit();
        });

        $("a.submitSameContainerFormLink").click(function(){
            $('form', $(this).parents()[0]).submit();
        });
});