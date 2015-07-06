var MyValidator = function() {  
    var handleSubmit = function() {  
        // validate register form and then submit
        $('.form-horizontal').validate({
            errorElement : 'span',  
            errorClass : 'help-block',  
            focusInvalid : false,  
            rules: {
                registerEmail: {
                	required: true,
                	email: true
                },
                registerName: {
                    required: true,
                    minlength: "3"
                },
                registerPWD: {
                    required: true,
                    minlength: "5"
                },
                comfirmPWD: {
                    required: true,
                    minlength: "5",
                    equalTo: "#registerPWD"
                }
            },
    
            messages: {
            	registerName: {
            		required: "Please enter a username",
            		minlength: "Your username must consist of at least 3 characters"
            	},
            	registerPWD: {
            		required: "Please provide a password",
            		minlength: "Your username must be at least 5 characters long"
            	},
            	comfirmPWD: {
            		required: "Please provide a password",
            		minlength: "Your username must be at least 5 characters long",
            		equalTo: "Please enter the same password as above"
            	}
            },

            highlight : function(element) {  
                $(element).closest('.form-group').addClass('has-error');  
            }, 

            success : function(label) {  
                label.closest('.form-group').removeClass('has-error');  
                label.remove();  
            },  
  
            errorPlacement : function(error, element) {  
                element.parent('div').append(error);  
            },  
  
            submitHandler : function(form) {  
                form.submit();  
            }  
        });  
        
        $('.form-horizontal input').keypress(function(e) {  
            if (e.which == 13) {  
                if ($('.form-horizontal').validate().form()) {  
                    $('.form-horizontal').submit();  
                }  
                return false;  
            }  
        });  

    }
    return {  
        init : function() {  
            handleSubmit();  
        }  
    };  
}(); 