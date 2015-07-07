$(document).ready(function() {
    $('#changepwdForm')
        .formValidation({
            message: 'This value is not valid',
            icon: {
                valid: 'glyphicon glyphicon-ok',
                invalid: 'glyphicon glyphicon-remove',
                validating: 'glyphicon glyphicon-refresh'
            },
            fields: {
                currentPWD: {
                    validators: {
                        notEmpty: {
                            message: 'The password is required and can\'t be empty'
                        }
                    }
                },
                newPWD: {
                    validators: {
                        notEmpty: {
                            message: 'The password is required and can\'t be empty'
                        }
                    }
                },
                confirmPWD: {
                    validators: {
                        notEmpty: {
                            message: 'The confirm password is required and can\'t be empty'
                        },
                        identical: {
                            field: 'newPWD',
                            message: 'The password and its confirm are not the same'
                        }
                    }
                }
            }
        });
});