#5. This C program finds the nth Fibonacci number. Translate the program into MIPS assembly language. 

 	.text
	.globl	main
main:
	addi	$sp, $sp, 4		# Make additional stack space.
	sw	$ra, 0($sp)		# Save the return address

	# Ask the OS to read a number n and store it in memory.
	li	$v0, 5		    	# Code for read int.(that is n)
	syscall			    	# Ask the system for service.
	move    $t0, $v0	    	# Put the input value in a safe
                                    	#    place
        
        # The loop     
        li      $t1, 1                  # Initialize f_old (f_old) to 1   
        li 	$t2, 0			# Initialize f_older (f_older) to 0
        li	$t3, 2			# Initialize i(i) to 2
lp_tst: bgt     $t3, $t0, done          # If $t3 > $t0 (i >  n), 
                                        #    branch out of loop.
                                        #    Otherwise continue.
        add $t4, $t1, $t2  		# set $t4: f_new = f_old + f_older
        add $t2, $t1, $zero		# set f_older = f_old
        add $t1, $t4, $zero    		# set f_old = f_new
        addi $t3, $t3, 1                # Increment $t3 (i++)
        j       lp_tst                  # Go to the loop test
        
# Done with the loop, print result
done:   li      $v0, 1                  # Code to print an int
        move    $a0, $t1                # Put the int in $a0
        syscall                         # Print the int

	# Restore the values from the stack, and release the stack space.
     	lw	$ra, 0($sp)		# Retrieve the return address
	addu	$sp, $sp, 4 	        # Free up added stack space.

	# Return -- go to the address left by the caller.
	# jr	$ra
        li      $v0, 10
        syscall

 