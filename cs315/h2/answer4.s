# Write a MIPS assembly language program that reads in two ints and compares them. 
# Depending on the outcome of the comparison it should print one of the messages ``First is greater than second,''
# ``They're equal,'' or ``First is less than second.''

	.text
	.globl	main
main:
	subu	$sp, $sp, 8	    # Make additional stack space.
	sw	$ra, 4($sp)	    # Save the return address
                                     
	# Ask the OS to read a number and store it in memory.
	li	$v0, 5		    # Code for read int.
	syscall			    # Ask the system for service.
	move    $t0, $v0	    # Put the input value in a safe
                                    #    place

	# Ask for another number.
	li	$v0, 5		    # Code for read int.
	syscall			    # Ask the system for service.
	move    $t1, $v0	    # Put the input value in a safe
                                    #    place

        # Branch to appropriate print statement
        beq 	$t0, $t1, eq	    # if $t0 == $t1, go to eq
        slt	$t2, $t0, $t1	    # $t2 = 1 if $t0 < $t1.  Otherwise
                                    # $t2 = 0 if st0 >= $t1
        bne	$t2, $zero, Else    # go to Else if $t2 != 0, which means $t0 < $t1
        

        # Print when first is greater than second input
        li      $v0, 4              # Code to print a string
        la      $a0, gr_msg         # Put the string in $a0
        syscall                     # Print the string
        j done

eq:	li      $v0, 4              # Code to print a string
        la      $a0, eq_msg         # Put the string in $a0
        syscall			    # Print the string 
        j done                   

Else:	
	li      $v0, 4              # Code to print a string
        la      $a0, le_msg         # Put the string in $a0
        syscall
        j done                     # Print the string
               
done:	# Restore the values from the stack, and release the stack space.
	lw	$ra, 12($sp)	    # Retrieve the return address
	addu	$sp, $sp, 16	    # Free up stack space.

	# Return -- go to the address left by the caller.
	#jr		$ra         # Can do this with SPIM
                                    #   Breaks MARS

    	# Exit system call:  this works with MARS and SPIM
    	li      $v0, 10
    	syscall

	.data
eq_msg: .asciiz "They're equal"
le_msg: .asciiz "First is less than second."
gr_msg: .asciiz "First is greater than second"
