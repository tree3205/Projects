#
# Program to read two numbers, store them in memory, add them, store
# the result, and print the result.
# 
	.text
	.globl	main
main:
	subu	$sp, $sp, 16	        # Make additional stack space.
	sw	$ra, 12($sp)		# Save the return address
                                        #    (Not necessary with MARS)
	
	# Ask the OS to read a number and store it in memory.
	li	$v0, 5			# Code for read int.
	syscall				# Ask the system for service.
	sw      $v0, 8($sp)		# Copy to memory (this is x).

	# Ask for another number.
	li	$v0, 5			# Code for read int.
	syscall				# Ask the system for service.
	sw      $v0, 4($sp)		# Copy to memory (this is y).

        # Load the two values from memory into registers
        lw      $t0, 8($sp)             # Get first int (x)
        lw      $t1, 4($sp)             # Get second int (y)

	# Add the values we just loaded from memory 
	add	$t2, $t0, $t1           # Add the two values

        # Now store the result
        sw      $t2, 0($sp)             # This is z

	# Ask the system to print it.
        lw      $a0, 0($sp)             # Put the result where it can be
                                        #    printed
	li	$v0, 1			# Code for print int.
	syscall

	# Restore the values from the stack, and release the stack space.
	lw	$ra, 12($sp)		# Retrieve the return address
	addu	$sp, $sp, 16	        # Free up stack space.

	# Return -- go to the address left by the caller.
	#jr	$ra                     # Can do this with SPIM
                                        #   Breaks MARS

        # Exit system call:  this works with MARS and SPIM
         li      $v0, 10
         syscall