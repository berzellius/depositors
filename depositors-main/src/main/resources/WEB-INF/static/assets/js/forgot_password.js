/**
 * Created by berz on 18.06.15.
 */
/**
 * Created by berz on 17.05.15.
 */
//////////////////
// Pages Signup //
//////////////////

"use strict";

$(document).ready(function(){

    /* Активация пользователя */
    $('.forgot_password-form')
        .bootstrapValidator({
            feedbackIcons: {
                valid: 'icon icon-check',
                invalid: 'icon icon-cross',
                validating: 'icon icon-refresh'
            },
            fields: {

                deposit_id: {
                    validators: {
                        notEmpty: {
                            message: 'Введите номер счета'
                        }
                    }
                }
            }
        })
        .on('success.field.bv', function(e, data) {
            // // $(e.target)  --> The field element
            // // data.bv      --> The BootstrapValidator instance
            // // data.field   --> The field name
            // // data.element --> The field element

            // var $parent = data.element.parents('.form-group');

            // // Remove the has-success class
            // $parent.removeClass('has-success');

            // // Hide the success icon
            // $parent.find('.form-control-feedback[data-bv-icon-for="' + data.field + '"]').hide();
        });
});
