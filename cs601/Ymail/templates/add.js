$(document).ready(function() {
    $('#addMailForm')
        .formValidation({
            message: 'This value is not valid',
            icon: {
                valid: 'glyphicon glyphicon-ok',
                invalid: 'glyphicon glyphicon-remove',
                validating: 'glyphicon glyphicon-refresh'
            },
            fields: {
                email: {
                    validators: {
                        notEmpty: {
                            message: 'The email address is required and can\'t be empty'
                        },
                        emailAddress: {
                            message: 'The input is not a valid email address'
                        }
                    }
                },
                smtp: {
                    validators: {
                        notEmpty: {
                            message: 'The SMTP server is required and can\'t be empty'
                        }
                    }
                },
                pop: {
                    validators: {
                        notEmpty: {
                            message: 'The POP server is required and can\'t be empty'
                        }
                    }
                },
                smtpport: {
                    validators: {
                        notEmpty: {
                            message: 'The SMTP port is required and can\'t be empty'
                        }
                    }
                },
                popport: {
                    validators: {
                        notEmpty: {
                            message: 'The POP port is required and can\'t be empty'
                        }
                    }
                },
                password: {
                    validators: {
                        notEmpty: {
                            message: 'The password is required and can\'t be empty'
                        }
                    }
                },
                confirmPassword: {
                    validators: {
                        notEmpty: {
                            message: 'The confirm password is required and can\'t be empty'
                        },
                        identical: {
                            field: 'password',
                            message: 'The password and its confirm are not the same'
                        }
                    }
                }
            }
        });
});