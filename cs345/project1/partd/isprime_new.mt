!isprime
 let var half: Integer;
     var half1: Integer;
     var half2: Integer;
     var i: Integer;
     var count: Integer;

     func isprime(x: Integer): Integer
     begin
        half := (x / 2) + 1;
        half1 := half + 1;
        half2 := half1 + 1;
        i := 2;
        count := 2;

        while i < half do
          if (x \ i) then
            i := i + 1;
          else
            i := half2;

        if (i = half) then
          putint(1);
        else
          putint(0);
    end
in
    begin
        getint(x);
        isprime(x);
    end