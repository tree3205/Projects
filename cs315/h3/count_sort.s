# File:     count_sort.c
# Purpose:  Implement count sort of a list of ints
#
# Input:    n (number of elements)
#           elements of list
# Output:   sorted list
#
# Note:     List is statically allocated.  Recompile if n > 100
	
	
	.text
	.globl	main
main:	
        addi	$sp, $sp, -412          # Make additional stack space.
                                        #   3 words for $ra, $s0, $s1
                                        #   100 words for list
        sw      $ra, 408($sp)           # Put contents of $ra on stack
        sw      $s0, 404($sp)           # Put $s0 on stack
        sw      $s1, 400($sp)           # Put $s1 on stack
        move    $s0, $sp                # $s0 = stores start address of list
                                        #     = $sp
        
        # Ask the OS to read a number and put it in $s1 = n
        li	$v0, 5                      # Code for read int.
        syscall                         # Ask the system for service.
        move    $s1, $v0                # Put the input value (n) in a safe
                                        # place  
                                        
        # Now read in the list
        move    $a0, $s0                # First arg is list
        move    $a1, $s1                # Second arg is n
        jal     rd_lst
        
        # Now print the list
        move    $a0, $s0                # First arg is list
        move    $a1, $s1                # Second arg is n
        jal	pr_lst		


        # Now sort the list
        move    $a0, $s0                # First arg is list
        move    $a1, $s1                # Second arg is n
        jal     count_sort
        
        # Now print the list
        move    $a0, $s0                # First arg is list
        move    $a1, $s1                # Second arg is n
        jal	pr_lst
         			
        # Prepare for return
        lw      $ra, 408($sp)           # Retrieve return address
        lw      $s0, 404($sp)           # Retrieve $s0
        lw      $s1, 400($sp)           # Retrieve $s1
        addi	$sp, $sp, 412           # Make additional stack space.
    
        li      $v0, 10                 # For MARS
        syscall

                                                                                             
                                                                                                                                           
                                                                                                      
        ###############################################################
        # Read_list function
        #    $a0 is the address of the beginning of list (In/out)
        #    $a1 is n (In)
        # Note:  $a0 isn't changed:  the block of memory it refers
        #    to is changed
rd_lst: 
        # Setup
        addi    $sp, $sp, -8            # Make space for return address
        sw      $ra, 0($sp)             # Save return address

        # Main for loop
        move    $t0, $zero              # $t0 = i = 0
rd_tst: bge     $t0, $a1, rddone        # If  i = $t0 >= $a1 = n
                                        #    branch out of loop.
                                        # Otherwise continue.
        li	$v0, 5                      # Code for read int.
        syscall                         # Ask the system for service.
        sll     $t1, $t0, 2             # Words are 4 bytes:  use 4*i, not i
        add     $t1, $a0, $t1           # $t1 = list + i
        sw      $v0, 0($t1)             # Put the input value in $v0 in
                                        #    list[i]
        addi    $t0, $t0, 1             # i++
        j       rd_tst                  # Go to the loop test
        
        # Prepare for return
rddone: lw      $ra, 0($sp)             # retrieve return address
        addi    $sp, $sp 8              # adjust stack pointer
        jr      $ra                     # return
       
       
        ###############################################################
        # Count_sort function	
        # $a0 is the address of the beginning of list
        # $a1 is n
count_sort:
	
        # Setup
        addi	$sp, $sp, -416          # Make additional stack space.
                                        #   3 words for $ra, $s0, $s1,
                                        #   $s2, $s3, $s4
                                        #   100 words for new list.
        sw      $ra, 412($sp)           # Put contents of $ra on stack
        sw      $s2, 408($sp)           # Put $s2 on stack
        sw      $s3, 404($sp)           # Put $s3 on stack
        sw	$s4, 400($sp)               # Put $s4 on stack
        move    $s2, $sp                # $s2 = stores start address of new list
                                        #     = $sp 
                        
        # We'll using $a0, $a1 in function calls 
        move	$s0, $a0                # $s0 is list
        move	$s1, $a1                # $s1 is n
        
        # Main for loop
        move	$s3, $zero              # $s3 = i = 0
        lp_tst:	bge	$s3, $s1, lpdone	# If  i = $s2 >= $s1 = n
                                        #    branch out of loop.
                                        #    Otherwise continue.
        
        # Find position to insert
        
        # Load list[i]
        move	$t0, $s3                # $t0 = $s3 = i
        sll	$t1, $t0, 2                 # $t1 = byte offset of list[i]
        add	$t1, $t1, $s0               # $t1 = absolute addr of list[i]
        lw	$t7, 0($t1)                 # $t2 = list[i]
        
        move	$a0, $t7                # First arg is list[i]
        move	$a1, $s3                # Second arg is i
        move	$a2, $s0                # Third arg is list
        move	$a3, $s1                # Fourth arg is n
        jal	fnd_pos                     # Call Fnd_pos function
        move	$s4, $v0                # Put loc in a safe place
        
        move	$t3, $s4                # $t3 = $s4 = loc
        sll	$t4, $t3, 2                 # $t4 = byte offset of new_list[loc]
        add	$t4, $t4, $s2               # $t4 = absolute addr of new_list[loc]
        sw	$t7, 0($t4)                 # new_list[loc] = list[i]
        
        # Increment i
        addi    $s3, $s3, 1             # i++
        j       lp_tst                  # Go to the loop test
        
        # Prepare for return                             
lpdone: 
        # Now print the list
        move    $a0, $s2                # First arg is list
        move    $a1, $s1                # Second arg is n
        jal	pr_lst
        
        move	$a0, $s2
        move	$a1, $s0
        move	$a2, $s1
        jal 	copy_list               # Copy sorted list to orignal position
	
        lw      $ra, 412($sp)           # Retrieve return address
        lw      $s2, 408($sp)           # Retrieve $s0
        lw      $s3, 404($sp)           # Retrieve $s1
        lw      $s4, 400($sp)           # Retrieve $s2
        addi    $sp, $sp, 416           # Adjust stack pointer
        jr      $ra                     # return  
        

	###############################################################
        # Fnd_pos function	
        # $a0 is val(list[i])
        # $a1 is i
        # $a2 is the address of the beginning of list
        # $a3 is n
fnd_pos:      
        # Setup
        addi	$sp, $sp, -12           # Make additional stack space.
        sw	$s0, 8($sp)                 # Put $s0 on stack
        sw	$s1, 4($sp)                 # Put $s1 on stack
        sw	$ra, 0($sp)   
        
        # The loop
        li	$t0, 0                      # Initialize j = 0
        li	$t1, 0                      # Initialize count = 0
flp_tst: 
        bge 	$t0, $a3, lmdone        # If  j = $t0 >= $a3 = n
                                        #    branch out of loop.
                                        #    Otherwise continue. 
                                        
        # Load list[j]
        sll	$t3, $t0, 2                 # $t1 = byte offset of list[j]
        add	$t3, $t3, $a2               # $t1 = absolute addr of list[j]
        lw	$t2, 0($t3)                 # $t2 = list[j]
        
        blt	$t2, $a0, plus              # If list[j] < val, go to plus
        beq	$t2, $a0, further           # If list[j] = val, go to further
        
        j	lpstep
           

plus: 	addi 	$t1, $t1, 1             # count++
        j	lpstep

further:                                # j < i, go to plus
        blt 	$t0, $a1, plus

lpstep:
        # Increment of j
        addi    $t0, $t0, 1             # j++
        j       flp_tst                 # Go to the loop test
 
lmdone: move    $v0 $t1                 # Set return val = count
        lw      $ra, 0($sp)             # Retrieve return address
        addi    $sp, $sp, 12            # Adjust stack pointer
        jr      $ra                     # Return  
        
	###############################################################
        # Copy_list function	
        # $a0 is new_list
        # $a1 is list
        # $a2 is n
copy_list:
        # Setup
        addi    $sp, $sp, -12           # Make space for return address
        sw      $ra, 0($sp)             # Save return address
        
        # Main for loop
        move 	$t0, $zero              # $t0 = i = 0
cp_tst: bge     $t0, $a2, done        	# If  i = $t0 >= $a2 = n
                                        #    branch out of loop.
                                        # Otherwise continue.  
                                        
        sll	$t6, $t0, 2                 # $t1 = byte offset of list[i]
        add	$t6, $t6, $a0               # $t1 = absolute addr of new_list[i]
        lw	$t5, 0($t6)                 # $t2 = new_list[i]
        
        sll	$t3, $t0, 2                 # $t3 = byte offset of list[i]
        add	$t3, $t3, $a1               # $t3 = absolute addr of list[i]
        sw	$t5, 0($t3)                 # set list[i] = new_list[i]
        
        # Increment of i
        addi    $t0, $t0, 1             # i++
        j       cp_tst                  # Go to the loop test
        
done:   lw      $ra, 0($sp)             # retrieve return address
        addi    $sp, $sp 12             # adjust stack pointer
        jr      $ra   
     
     	###############################################################
        # Print_list function
        #    $a0 is the address of the beginning of list (In)
        #    $a1 is n  (In)
pr_lst: 
        # Setup
        addi    $sp, $sp, -4            # Make space for return address
        sw      $ra, 0($sp)             # Save return address

        # Main for loop
        move    $t2, $a0                # Need $a0 for syscall:  so
                                        #    copy to t2
        move    $t0, $zero              # $t0 = i = 0
pr_tst: bge     $t0, $a1, prdone        # If  i = $t0 >= $a1 = n 
                                        #    branch out of loop.
                                        #    Otherwise continue.
        sll     $t1, $t0, 2             # Words are 4 bytes:  use 4*i, not i
        add     $t1, $t2, $t1           # $t1 = list + i
        lw      $a0, 0($t1)             # Put the value to print in $a0
        li	$v0, 1                      # Code for print int.
        syscall

        # Print a space 
        la      $a0, space              # 
        li      $v0, 4                  # Code for print string
        syscall

        addi    $t0, $t0, 1             # i++
        j       pr_tst                  # Go to the loop test
        
        # print a newline
prdone: 
        la      $a0, newln
        li      $v0, 4                  # code for print string
        syscall

        # Prepare for return
        lw      $ra, 0($sp)             # retrieve return address
        addi    $sp, $sp 4              # adjust stack pointer
        jr      $ra                     # return
        
        ###############################################################
        # Data
        .data
space:  .asciiz " "
newln:  .asciiz "\n"                                                                                                                   