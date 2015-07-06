# Write a MIPS assembly language program that reads in three ints and subtracts the last int from the sum of the first two ints. 
# When it's done with the calculations it should print the result.
		.text
		.globl	main
main:
	subu	$sp, $sp, 20	        # Make additional stack space.
	sw	$ra, 16($sp)		# Save the return address

	# Ask the OS to read a number and store it in memory.
	li	$v0, 5			# Code for read int.
	syscall				# Ask the system for service.
	sw  	$v0, 12($sp)		# Copy to memory (this is x).

	# Ask for the second number.
	li	$v0, 5			# Code for read int.
	syscall				# Ask the system for service.
	sw      $v0, 8($sp)		# Copy to memory (this is y).

	# Ask for the third number.
	li	$v0, 5			# Code for read int.
	syscall				# Ask the system for service.
	sw      $v0, 4($sp)		# Copy to memory (this is z).

	# Load the first two values from memory into registers
    	lw      $t0, 12($sp)            # Get first int (x)
    	lw      $t1, 8($sp)             # Get second int (y)

    	# Add the values x and y we've just loaded from memory 
	add	$t2, $t0, $t1   	# Add the two values

	# Load the third value from memory into registers
	lw      $t3, 4($sp)             # Get third int (z)

	# Substracts the third int from the sum of the first two ints
	sub 	$t4, $t2, $t3		# Substract the values

	# Now store the result
    	sw      $t4, 0($sp)             # This is the result

    	# Ask the system to print it.
    	lw      $a0, 0($sp)             # Put the result where it can be
                                    	# printed
	li	$v0, 1			# Code for print int.
	syscall

	# Restore the values from the stack, and release the stack space.
	lw	$ra, 16($sp)		# Retrieve the return address
	addu	$sp, $sp, 20	        # Free up stack space.

	# Return -- go to the address left by the caller.
	#jr		$ra                     # Can do this with SPIM
                                    #   Breaks MARS

    	# Exit system call:  this works with MARS and SPIM
    	li      $v0, 10
    	syscall
