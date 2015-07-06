
all: findminmax_par1 findminmax_par2 excu

findminmax_par1 : findminmax_par1.c measure.h 
	
	gcc -o findminmax_par1 findminmax_par1.c

findminmax_par2 : findminmax_par2.c measure.h 
	
	gcc -o findminmax_par2 findminmax_par2.c

excu : findminmax_seq findminmax_par1 findminmax_par2
	./findminmax_seq 1 400000000 
	./findminmax_par1 1 400000000 1 
	./findminmax_par1 1 400000000 2 
	./findminmax_par1 1 400000000 4 
	./findminmax_par2 1 400000000 1 
	./findminmax_par2 1 400000000 2 
	./findminmax_par2 1 400000000 4

clean:
	/bin/rm -f findminmax_par1
	/bin/rm -f findminmax_par2

