$(document).ready(function () {
    /*$('form#accountMoneyDataForm input[name=sum_inp]').change(function(){
     $('form#accountMoneyDataForm span#sum b').html($(this).val()*1000);
     $('form#accountMoneyDataForm input[name=sum]').val($(this).val()*1000);
     });*/


    signUpEngine.init();
});

var signUpEngine = {
    'currentTabIndex': null,

    'finishDataSet': null,

    'wizardID': '#reg_wizard',

    'sumTemplates': {
        'THOUSANDS': {
            'sumFrom': 1000,
            'tmpl': 'тыс.'
        },
        'MILLIONS': {
            'sumFrom': 1000000,
            'tmpl': 'млн'
        }
    },

    'accountingMoneyDataForm': null,
    'contactsForm': null,
    'passportForm': null,
    "endRegistrationForm": null,

    'loadHTMLResources': function () {
        this.accountingMoneyDataForm = $('form#accountMoneyDataForm');
        this.contactsForm = $('form#contactsForm');
        this.passportForm = $('form#passportForm');
        this.endRegistrationForm = $('form#endRegistration');
    },
    'currentDepositVariant': {
        'type': null,
        'sum': null,
        'length': null
    },
    'urlPrefix': '/',
    validateFIO: function (fio) {
        var words = fio.split(' ');
        var rt = 0;


        for (i in words) {
            words[i].replace(' ', '');

            if (0 < words[i].length && words[i].length < 2) return false;
            else rt++;
        }

        return (rt > 2);
    },
    bindCommonActions: function () {
        $('input.date').not('.birthday').datetimepicker({
            'language': 'ru',
            'viewMode': 1,
            'pickTime': false,
            'format': 'DD.MM.YYYY'
        });

        $('input.date.birthday').datetimepicker({
            'language': 'ru',
            'viewMode': 2,
            'pickTime': false,
            'format': 'DD.MM.YYYY'
        });

        $('.address_reg').hide();

        $('input[name=address_reg_eq_fact]').change(function () {
            if ($(this).is(':checked')) {
                $('.address_reg').hide();
            }
            else {
                $('.address_reg').show();
            }
        });

        $.get(
            this.urlPrefix + "rest/signup/region_list",
            (function (t) {
                return function (d) {
                    var opts = [];
                    for (var i in d) {
                        var o = document.createElement("option");
                        $(o).attr('value', d[i].id);
                        $(o).text(d[i].name);

                        opts.push(o);
                    }

                    $('div.region_fill select[data-type=region]').append(opts);

                    $('div.region_fill select[data-type=region]').change(function () {
                        $('select[data-type=city] option', $(this).parents('div.region_fill')).remove();
                        var loadingOpt = document.createElement("option");
                        $(loadingOpt).attr('value', '0');
                        $(loadingOpt).text('Загрузка...');
                        $('select[data-type=city]', $(this).parents('div.region_fill')).append(loadingOpt);

                        $.ajax({
                            'url': t.urlPrefix + "rest/signup/cities_by_region",
                            'data': {
                                'region_id': $(this).val()
                            },
                            'success': (function (sl) {
                                return function (cities) {
                                    var cOpts = [];
                                    for (var i in cities) {
                                        var o = document.createElement("option");
                                        $(o).attr('value', cities[i].id);
                                        $(o).text(cities[i].name);

                                        cOpts.push(o);
                                    }

                                    $('select[data-type=city] option', $(sl).parents('div.region_fill')).remove();
                                    $('select[data-type=city]', $(sl).parents('div.region_fill')).append(cOpts);

                                }
                            })(this)
                        });

                        $('div.form-group', $(this).parents('div.region_fill')).css('display', 'block');
                    });

                    $('div.region_fill select[data-type=region]').trigger("change");
                }
            })(this)
        );
    },
    'validateTabSwitch': function (tab, navigation, index) {

        var t = this;

        if (index == 1) {

            var fioValue = $("input[name=fio]", t.accountingMoneyDataForm);
            if (t.validateFIO(fioValue.val()) != true) {
                fioValue.parent().addClass('has-error');
                return false;
            }
            else {
                fioValue.parent().removeClass('has-error');
            }


            var mobileValue = $('input[name=mobile]', t.accountingMoneyDataForm);
            if ($.properVal(mobileValue).length != 10) {
                mobileValue.parent().addClass('has-error');
                return false;
            }
            else {
                mobileValue.parent().removeClass('has-error');
            }

            var emailValue = $("input[name=email]", t.accountingMoneyDataForm);
            if (circloidValidateForm(emailValue.val(), "email", "required") != true) {
                emailValue.parent().addClass("has-error");
                return false;
            }
            else {
                emailValue.parent().removeClass("has-error");
            }


            return (
                t.currentDepositVariant.length != null &&
                    t.currentDepositVariant.sum != null &&
                    t.currentDepositVariant.type != null
                );
        }

        if (index == 2) {

            var factIndex = $('input[name=fact-index]', t.contactsForm);
            if ($.properVal(factIndex).length < 6) {
                factIndex.parent().addClass('has-error');
                return false;
            }
            else {
                factIndex.parent().removeClass('has-error');
            }

            var factRegion = $('select[name=fact_region]', t.contactsForm);
            if (typeof(factRegion.val()) == 'undefined' || factRegion.val() == null || factRegion.val() == 0) {
                factRegion.parent().addClass('has-error');
                return false;
            }
            else {
                factRegion.parent().removeClass('has-error');
            }

            var factCity = $('select[name=fact_city]', t.contactsForm);
            if (typeof(factCity.val()) == 'undefined' || factCity.val() == null || factCity.val() == 0) {
                factCity.parent().addClass('has-error');
                return false;
            }
            else {
                factCity.parent().removeClass('has-error');
            }

            var addrRegEqFact = $('input[name=address_reg_eq_fact]', t.contactsForm);

            if (!addrRegEqFact.is(':checked')) {
                var regIndex = $('input[name=reg-index]', t.contactsForm);
                if ($.properVal(regIndex).length < 6) {
                    regIndex.parent().addClass('has-error');
                    return false;
                }
                else {
                    regIndex.parent().removeClass('has-error');
                }

                var regRegion = $('select[name=reg_region]', t.contactsForm);
                if (typeof(regRegion.val()) == 'undefined' || regRegion.val() == null || regRegion.val() == 0) {
                    regRegion.parent().addClass('has-error');
                    return false;
                }
                else {
                    regRegion.parent().removeClass('has-error');
                }

                var regCity = $('select[name=reg_city]', t.contactsForm);
                if (typeof(regCity.val()) == 'undefined' || regCity.val() == null || regCity.val() == 0) {
                    regCity.parent().addClass('has-error');
                    return false;
                }
                else {
                    regCity.parent().removeClass('has-error');
                }
            }

            return true;

        }

        if (index == 3) {

            var passpNum = $('input[name=passp-num]', t.passportForm);
            if ($.properVal(passpNum).length != 10) {
                passpNum.parent().addClass('has-error');
                return false;
            }
            else {
                passpNum.parent().removeClass('has-error');
            }

            var pasDateAcc = $('input[name=passp-date-acc]', t.passportForm);
            if ($.properVal(pasDateAcc) == '') {
                pasDateAcc.parent().addClass('has-error');
                return false;
            }
            else {
                pasDateAcc.parent().removeClass('has-error');
            }

            var passpGivenBy = $('textarea[name=passp-given-by]', t.passportForm);
            if ($.properVal(passpGivenBy) == '') {
                passpGivenBy.parent().addClass('has-error');
                return false;
            }
            else {
                passpGivenBy.parent().removeClass('has-error');
            }

            var passpCode = $('input[name=passp-code]', t.passportForm);
            if ($.properVal(passpCode).length != 6) {
                passpCode.parent().addClass('has-error');
                return false;
            }
            else {
                passpCode.parent().removeClass('has-error');
            }

            var birthDateAcc = $('input[name=passp-birthday]', t.passportForm);
            if ($.properVal(birthDateAcc) == '') {
                birthDateAcc.parent().addClass('has-error');
                return false;
            }
            else {
                birthDateAcc.parent().removeClass('has-error');
            }

            var passpBirthPlace = $('input[name=passp-birth-place]', t.passportForm);
            if ($.properVal(passpBirthPlace) == '') {
                passpBirthPlace.parent().addClass('has-error');
                return false;
            }
            else {
                passpBirthPlace.parent().removeClass('has-error');
            }

        }

        return true;
    },
    processField: function (e, skipNames, replaceNames, visibleValues) {
        if ($(e).attr('type') == 'text' || $(e).prop('tagName').toLowerCase() == 'textarea') {
            var name = $(e).attr('name');

            if (skipNames.indexOf(name) == -1 && typeof(this.currentDepositVariant[name]) == 'undefined') {
                name = (typeof(replaceNames[name]) != 'undefined') ? replaceNames[name] : name;

                this.finishDataSet[name] = $.properVal(e);

                if($.properVal(e) != $(e).val()){
                    visibleValues[name] = $(e).val();
                }

            }
        }

        if($(e).prop('tagName').toLowerCase() == 'select'){
            var name = $(e).attr('name');
            if (skipNames.indexOf(name) == -1 && typeof(this.currentDepositVariant[name]) == 'undefined') {
                name = (typeof(replaceNames[name]) != 'undefined') ? replaceNames[name] : name;
                this.finishDataSet[name] = $(e).val();

                var value = null;
                $('option', e).each(function(ind,elem){
                    if($(elem).attr('value') == $(e).val())
                        visibleValues[name] = $(elem).text()
                });
            }
        }
    },
    'init': function () {

        $(this.wizardID).bootstrapWizard(
            (function (t) {
                return {
                    'onNext': function (tab, navigation, index) {
                        return t.validateTabSwitch(tab, navigation, index);
                    },
                    'onTabShow': function (tab, navigation, index) {

                        var $total = navigation.find('li').length;
                        var $current = index + 1;
                        var $percent = ($current / $total) * 100;
                        var progressBar = $(t.wizardID).find('.progress-bar');

                        progressBar.css({width: $percent + '%'});

                        // Progress Bar Color Change - Optional
                        // First, remove the previous highilight-color-*
                        var regex = new RegExp('\\b' + 'progress-bar-' + '.+?\\b', 'g');
                        $(t.wizardID).find('.progress-bar')[0].className = progressBar[0].className.replace(regex, '');

                        // Progress Bar Color Change - Optional
                        if ($percent < 20) {
                            progressBar.addClass("progress-bar-danger");
                        } else if (($percent >= 20) && ($percent < 50)) {
                            progressBar.addClass("progress-bar-warning");
                        } else if (($percent >= 50) && ($percent < 75)) {
                            progressBar.addClass("progress-bar-info");
                        } else if (($percent >= 75) && ($percent < 100)) {
                            // Keep default progress bar color
                        } else {
                            progressBar.addClass("progress-bar-success");
                        }

                        t.currentTabIndex = $current - 1;


                        if (index == 3) {

                            var skipNames = ['length_inp', 'sum_inp', 'address_reg_eq_fact'];
                            var replaceNames = {
                                'type': 'depositorFormType', 'mobile' : 'mobilePhone', 'homephone' : 'homePhone',
                                'workphone' : 'workPhone', 'workphone_addt' : 'addtPhone', 'fact-index' : 'factIndex',
                                'fact_city' : 'factCity', 'reg-index' : 'regIndex', 'reg_city' : 'regCity', 'passp-num' : 'passportNum',
                                'passp-date-acc' : 'passportAccepted', 'passp-given-by' : 'departmentName', 'passp-code' : 'departmentCode',
                                'passp-birthday' : 'birthday', 'passp-birth-place' : 'birthplace', 'reg_region' : 'regRegion', 'fact_region' : 'factRegion'
                            };
                            var visibleValues = {};


                            t.finishDataSet = {};

                            var forms = [t.accountingMoneyDataForm, t.contactsForm, t.passportForm];
                            for (var fj in forms) {
                                $('input, textarea, select', forms[fj]).each(function (i, e) {
                                    t.processField(e, skipNames, replaceNames, visibleValues);
                                });
                            }


                            t.finishDataSet['depositCalculation'] = (function () {
                                var output = {};
                                for (i in t.currentDepositVariant) {
                                    if (typeof(replaceNames[i]) != 'undefined') {
                                        output[replaceNames[i]] = t.currentDepositVariant[i];
                                    }
                                    else {
                                        output[i] = t.currentDepositVariant[i];
                                    }
                                }

                                return output;
                            })();

                            //console.log(JSON.stringify(visibleValues));

                            if($('input[name=address_reg_eq_fact]', t.contactsForm).is(":checked")){
                                t.finishDataSet['regIndex'] = t.finishDataSet['factIndex'];
                                t.finishDataSet['regCity'] = t.finishDataSet['factCity'];

                                visibleValues['regCity'] = visibleValues['factCity']
                            }

                            visibleValues['regCity'] = visibleValues['regRegion'] + ", " + visibleValues['regCity'];
                            visibleValues['factCity'] = visibleValues['factRegion'] + ", " + visibleValues['factCity'];

                           // console.log(JSON.stringify(t.finishDataSet));

                            var el;
                            var v;
                            var vis;

                            $('span b.opts', t.endRegistrationForm).css('display', 'none');

                            for (i in t.finishDataSet) {
                                if (t.finishDataSet[i] instanceof Object) {
                                    for (j in t.finishDataSet[i]) {

                                        el = $('input[name=' + j + ']', t.endRegistrationForm);
                                        v = t.finishDataSet[i][j];
                                        vis = (typeof(visibleValues[j]) == 'undefined')? t.finishDataSet[i][j] : visibleValues[j];
                                        el.val(v);
                                        $('span b.value', el.parent()).html(vis);
                                        $('span b.opts.' + v, el.parent()).css('display', '');
                                    }
                                }
                                else {
                                    el = $('input[name=' + i + ']', t.endRegistrationForm);
                                    v = t.finishDataSet[i];
                                    vis = (typeof(visibleValues[i]) == 'undefined')? t.finishDataSet[i] : visibleValues[i];
                                    el.val(v);
                                    $('span b.value', el.parent()).html(vis);
                                }
                            }

                        }
                    },
                    'onTabClick': function (tab, navigation, index, clickedIndex) {

                        if (
                            clickedIndex - index == 1
                            )
                            return t.validateTabSwitch(tab, navigation, clickedIndex);
                        else if (clickedIndex < index) return true;
                        else return false;
                    }
                }
            })(this)
        );

        this.unsetLoadingStatus();

        this.loadHTMLResources();

        this.bindActions(this.accountingMoneyDataForm);
        this.bindActions(this.contactsForm);
        this.bindActions(this.passportForm)
        this.bindCommonActions();
    },
    'reportError': function () {
        alert('Произошла ошибка. Обновите страницу');
    },
    unsetLoadingStatus: function (dataForm) {
        $('div.preloader', dataForm).remove();
    },
    currentDepositorTypeSettingsValidation: function (currentDepositorTypeSettings) {
        if (typeof currentDepositorTypeSettings['minSum'] == 'undefined') return false;
        if (typeof currentDepositorTypeSettings['maxSum'] == 'undefined') return false;

        if (typeof currentDepositorTypeSettings['sumSettingsList'] == 'undefined') return false;

        if (currentDepositorTypeSettings['sumSettingsList'].length == 0) return false;

        return true;
    },
    updateLengthToDeposit: function (divLengthBar, sbInputBar, dataForm) {
        $('span#length', divLengthBar).css('display', 'block');
        $('span#length b', divLengthBar).html($(sbInputBar).val());

        this.currentDepositVariant.length = $(sbInputBar).val();

        $('div.deposit_calculation').css('display', 'block');
        $('div.deposit_calculation div.preloader_circle').css('display', 'block');
        $('div.deposit_calculation div.info').css('display', 'none');

        $.get(
            this.urlPrefix + "rest/signup/deposit_calculation",
            this.currentDepositVariant,
            function (res) {

                for (i in res) {
                    if ($('div.deposit_calculation table tr.' + i).length > 0) {
                        $($('div.deposit_calculation table tr.' + i + ' td').get(1)).html(res[i]);
                    }
                }

                $('div.deposit_calculation div.graph').css('display', 'block');

                var colors = ["#4596f1", "#f17d45"];
                var d1 = {'label':'Вклад','data':[[0, res['sum']], [1, res['sum']]]};
                var d2 = {'label':'Проценты','data':[[1, res['profit']]]};
                $.plot($('div.deposit_calculation div.graph'), [ d1, d2 ], {
                    series: {
                        stack: true,
                        bars: {
                            show: true,
                            fill: true,
                            lineWidth: 1,
                            barWidth: 0.6
                        }
                    },
                    grid: {
                        backgroundColor: '#FFFFFF',
                        borderColor: '#D6D6D9',
                        borderWidth: 0,
                        hoverable: true
                    },
                    tooltip: true,
                    tooltipOpts: {
                        content: "%s: <b>%y</b> руб.",
                        shifts: {
                            x: -40,
                            y: 25
                        },
                        defaultTheme : false
                    },
                    xaxis: {
                        show: true,
                        tickLength: 0,
                        tickFormatter: function(number) {
                            return (number == 0.25) ? 'Сегодня:<br /><b>' + res['sum'] + ' руб.</b>' : (number == 1.25)? 'Будет:<br /><b>' + res['sumInTheEnd'] + ' руб.</b>' : '';
                        }
                    },
                    yaxis: {
                        show: true,
                        tickLength: 0,
                        font: {color: '#fff'}
                    },
                    legend: false,
                    colors: colors
                });

                $('div.deposit_calculation div.preloader_circle').css('display', 'none');
                $('div.deposit_calculation div.info').css('display', 'block');
            }
        );
    },
    buildLengthBar: function (dataForm) {

        var sum = this.currentDepositVariant.sum;

        $('div.updatable.lengthbar', dataForm).remove();


        var sumSettingsList = this.currentDepositorTypeSettings['sumSettingsList'];
        for (i in sumSettingsList) {
            var next = parseInt(i) + 1;
            if (next == sumSettingsList.length || sum < sumSettingsList[next]['sumFrom']) {

                this.currentSumSettings = sumSettingsList[i];

                var divLengthBar = document.createElement('div');
                $(divLengthBar).addClass('form-group updatable lengthbar');
                var sbLabel = document.createElement('label');
                $(sbLabel).text('Срок, месяцев');
                var sbInputBar = document.createElement('input');
                $(sbInputBar).attr('name', 'length_inp');
                $(sbInputBar).attr('type', 'text');
                $(sbInputBar).addClass('form-control powerange-horizontal bg-color-blue');
                $(sbInputBar).css('display', 'none');

                $(divLengthBar).append(sbLabel);
                $(divLengthBar).append(sbInputBar);
                $(divLengthBar).append("<p><span id=\"length\"  style=\"display: none\"> Срок: <b></b> месяцев</span></p>")

                $(dataForm).append(divLengthBar);

                var powerange = new Powerange(sbInputBar,
                    {
                        'min': this.currentSumSettings['minLength'],
                        'max': this.currentSumSettings['maxLength'],
                        'step': 1,
                        'callback': (function (t) {
                            return function () {
                                t.updateLengthToDeposit(divLengthBar, sbInputBar);
                            }
                        })(this)
                    });

                return;
            }
        }
    },
    updateSumToDeposit: function (divSumBar, sbInputBar, dataForm, sumTemplate) {
        $('span#sum', divSumBar).css('display', 'block');
        $('span#sum b', divSumBar).html($(sbInputBar).val() * 100000 / sumTemplate['sumFrom']);

        this.currentDepositVariant.sum = $(sbInputBar).val() * 100000;
        this.buildLengthBar(dataForm);
    },
    buildSumSetBar: function (dataForm) {
        this.unsetLoadingStatus(dataForm);

        if (
            this.currentDepositorTypeSettingsValidation(this.currentDepositorTypeSettings)
            ) {

            var sumTemplate = null;

            for (j in this.sumTemplates) {
                if (
                    ((this.currentDepositorTypeSettings['minSum'] + this.currentDepositorTypeSettings['maxSum'])
                        / 2) >= this.sumTemplates[j].sumFrom
                    ) {
                    sumTemplate = this.sumTemplates[j];
                }
            }

            var divSumBar = document.createElement('div');
            $(divSumBar).addClass('form-group updatable');
            var sbLabel = document.createElement('label');
            $(sbLabel).text("Сумма, " + sumTemplate['tmpl'] + " рублей");
            var sbInputBar = document.createElement('input');
            $(sbInputBar).attr('name', 'sum_inp');
            $(sbInputBar).attr('type', 'text');
            $(sbInputBar).addClass('form-control powerange-horizontal bg-color-blue');
            $(sbInputBar).css('display', 'none');

            $(divSumBar).append(sbLabel);
            $(divSumBar).append(sbInputBar);
            $(divSumBar).append("<p><span id=\"sum\"  style=\"display: none\">Сумма: <b></b> " + sumTemplate['tmpl'] + " рублей</span></p>")

            $(dataForm).append(divSumBar);

            var powerange = new Powerange(sbInputBar,
                {
                    'min': this.currentDepositorTypeSettings['minSum'] / 100000,
                    'max': this.currentDepositorTypeSettings['maxSum'] / 100000,
                    'step': 1,
                    'hideRange': true,
                    'callback': (function (t) {
                        return function () {
                            t.updateSumToDeposit(divSumBar, sbInputBar, dataForm, sumTemplate);
                        }
                    })(this)
                });

            this.updateSumToDeposit(divSumBar, sbInputBar, dataForm, sumTemplate);
        }
        else this.reportError();
    },
    'bindActions': function (dataForm) {
        (function (t) {

            t.onlyNumericInput($('.numeric', dataForm));
            t.onlyNumericInput($('.phone10', dataForm), 10);
            t.onlyNumericInput($('.ind6', dataForm), 6);
            t.onlyNumericInput($('.code6', dataForm), 6);
            t.onlyNumericInput($('.passp10', dataForm), 10);


            $('.phone10.mobile', dataForm).mask('(Z00) 000-0000', {'translation': {Z: {pattern: '9'}}});
            $('.phone10', dataForm).mask('(000) 000-0000');
            $('.code6', dataForm).mask('000-000');
            $('.passp10', dataForm).mask('0000 000000');
            //$('.date', dataForm).mask('X0.Y0.0000', {'translation' : {X : {pattern : /[0-3]/}, Y : {pattern : /[0-1]/}}});


            $('div.initial', dataForm).css('display', 'block');

            $('input[name=depositorFormType]', dataForm).click(function () {
                t.setLoadingStatus(dataForm);

                (function (iType) {
                    $.get(
                        t.urlPrefix + "rest/signup",
                        {'type': $(iType).val()},
                        function (d) {
                            t.currentDepositorTypeSettings = d;
                            t.currentDepositVariant.type = $(iType).val();
                            t.buildSumSetBar(dataForm);
                        }
                    );
                })(this);


            });


        })(this);

        $('input[name=depositorFormType]:checked', dataForm).trigger("click");
    },
    'currentDepositorTypeSettings': null,
    'currentSumSettings': null,
    'setLoadingStatus': function (dataForm) {

        $('.updatable', dataForm).remove();
        $('.preloader', dataForm).remove();

        var preloaderDiv = document.createElement("div");
        var preloaderImg = document.createElement("img");

        $(preloaderDiv).addClass('form-group');
        $(preloaderDiv).addClass('preloader');
        $(preloaderImg).attr('src', this.urlPrefix + 'static/assets/images/additional/preloader.gif');
        $(preloaderDiv).append(preloaderImg);

        $(dataForm).append(preloaderDiv);
    },
    'onlyNumericInput': $commonServices.onlyNumericInput
}
