#
# Read int from input with a function, print result
 	.text
	.globl	main
main:
	addi	$sp, $sp, -4		# Make additional stack space.
	sw	$ra, 0($sp)		# Save the return address
	
	# Ask the OS to read value using a function
        la      $a0, i_msg              # String prompt
        jal     rd_int                  # Jump to function that prints a msg
                                        #   and reads an int
	move	$t0, $v0		# Copy return value to safe location

	# Print input value with message
        li      $v0, 4                  # Code for string output
        la      $a0, o_msg              # String to print
        syscall

        li      $v0, 1                  # Print int code.
        move    $a0, $t0                # Int to print
	syscall

        li      $v0, 4                  # Print string code.
        la      $a0, newln              # String to print
	syscall

	# Restore the values from the stack, and release the stack space.
	lw	$ra, 0($sp)		# Retrieve return address
	addu	$sp, $sp, 4		# Free stack space.

	# Return -- go to the address left by the caller.  OK with SPIM.
	# jr	$ra

        # Exit system call.   Works with MARS and SPIM
        li      $v0, 10
        syscall


        # Function to print a message and read an int
        # String to print is in $a0
rd_int: 
        # Put return address on stack
	addi	$sp, $sp, -4		# Make additional stack space.
	sw	$ra, 0($sp)		# Save the return address

        li      $v0, 4                  # Print string code.
        syscall                         # Print prompt
	li	$v0, 5			# Code for read int.
	syscall				# Read int, int is in $v0.

	# Restore the values from the stack, and release the stack space.
	lw	$ra, 0($sp)		# Retrieve return address
	addu	$sp, $sp, 4		# Free stack space.

	# Return -- go to the address left by the caller.
	jr      $ra		        # Return.  Int is in $v0.


        .data
i_msg: .asciiz "Enter an int\n"
o_msg: .asciiz "Input value is "         
newln: .asciiz "\n"
