	.text
	.globl	main
main:
	# Setup
        addi	$sp, $sp, -12 	        # Make additional stack space.
        sw	$s0, 8($sp)                 # Put $s0 on stack
        sw	$s1, 4($sp)                 # Put $s1 on stack
        sw	$ra, 0($sp)                 # Save the return address
        
	# Print "Enter a positive int"
        la      $a0, i_msg             	
        li      $v0, 4                  # Code for print string
        syscall
	
	# Print a new line 
        la      $a0, newln              
        li      $v0, 4                  # Code for print string(new line)
        syscall
        
        # Ask the OS to read a number and put it in a temporary register
        li	$v0, 5                      # Code for read int.
        syscall                         # Ask the system for service.
        move    $s0, $v0                # Put n in a safe place
        
        # Now carry out the Fibo function
        move 	$a0, $s0                # arg is n
        jal	Fibo
        
        # Now print the result
        move	$t0, $v0
        
        li      $v0, 4                  # Code for string output
        la      $a0, o_msg1             # String to print
        syscall
        
        move	$a0, $s0
        li      $v0, 1 
        syscall
        
        li      $v0, 4                  
        la      $a0, o_msg2              
        syscall
        
        li      $v0, 4                  
        la      $a0, o_msg3              
        syscall
        
        move	$a0, $t0
        li	$v0, 1
        syscall
        
        # Restore the values from the stack, and release the stack space.
     	lw	$ra, 0($sp)                 # Retrieve the return address
        addi	$sp, $sp, 12 	        # Make additional stack space.
        
        ## Return -- go to the address left by the caller.
        li      $v0, 10                 
        syscall
        
        ###############################################################
        # Fibo function
        # $a0 is n
        
        # Setup
Fibo:   
        addi    $sp, $sp, -16           # Make space for return address
        sw      $ra, 0($sp)             # Save return address

        
        beq	$a0, $zero, rt              # if (n == 0) go to rt
        
        beq	$a0, 1, rt                  # if (n == 1) go to rt
        

      	sw	$a0, 12($sp)
        
        lw	$t0, 12($sp)
        addi	$a0, $t0, -1		
        jal	Fibo                        # Call Fibo(n-1)
        sw	$v0, 8($sp)
        
        lw	$t0, 12($sp)
        addi	$a0, $t0, -2		
        jal	Fibo                        # Call Fibo(n-2)
        sw	$v0, 4($sp)
        
        lw	$t1, 8($sp)
        lw	$t2, 4($sp)		
        add	$v0, $t1, $t2               # Add Fibo(n-1) and Fibo(n-2)
        j	done
        
        
rt:	move	$v0, $a0

        # Prepare for return
done:   lw      $ra, 0($sp)             # Retrieve return address
        addi    $sp, $sp, 16            # Adjust stack pointer
        jr	$ra
        
   	.data      
i_msg: .asciiz "Enter a positive int:"       
newln:  .asciiz "\n"
o_msg1: .asciiz "The "
o_msg2: .asciiz "th "
o_msg3: .asciiz  "Fibonacci number is "
 