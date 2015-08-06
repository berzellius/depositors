/**
 * Created by berz on 17.05.15.
 */
//////////////////
// Pages Signup //
//////////////////

"use strict";

$(document).ready(function(){

    /* Активация пользователя */
    $('.registration-form')
        .bootstrapValidator({
            feedbackIcons: {
                valid: 'icon icon-check',
                invalid: 'icon icon-cross',
                validating: 'icon icon-refresh'
            },
            fields: {

                password: {
                    validators: {
                        notEmpty: {
                            message: 'Пароль не может быть пустым'
                        },
                        stringLength: {
                            min: 6,
                            max: 50,
                            message: 'Длина пароля - от 6 до 50 символов'
                        }
                    }
                },
                passwordConfirm: {
                    validators: {
                        identical: {
                            field: 'password',
                            message: 'Пароли не совпадают'
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
