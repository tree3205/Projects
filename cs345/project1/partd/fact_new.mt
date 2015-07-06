! Factorial_new                                     
let                                                                                
    var fact: Integer;
    func factorial(x: Integer): Integer             
        begin                                       
            if x = 0 then                           
                putint(1);                          
            else                                    
                begin                               
                    fact:=1;                        
                    while x > 0 do                  
                        begin                       
                            fact := fact * x;       
                            x := x - 1;             
                        end                         
                    putint(fact);                   
                end                                 
        end                                         
in                                                  
    begin                                           
        getint(x);
        factorial(x);                               
    end                                             