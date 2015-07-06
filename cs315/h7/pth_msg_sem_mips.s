# File:     pth_msg_sem.c
# Purpose:  Try to implement semaphores with mips implementation of binary semaphores.
#
# Functions: sem_post and sem_wait.
# 


###########################################################
# Function: sem_post
# Purpose: Unlock the semaphore of dest,
# 	   Sem_post(& sem) sets sem to 1.
# C Prototype: sem_post(&semaphores[dest]);
# Args: a0 = &semaphores[dest]
	
		
	.text
	.globl sem_post
	
sem_post:	
	addi	$sp, $sp, -8
	sw	$ra, 0($sp)
	
	li	$t0, 1
	sw	$t0, 0($a0)
	
	lw	$ra, 0($sp)
	addi 	$sp, $sp, 8
	jr	$ra
	
###########################################################
# Function: sem_wait
# Purpose: Wait for our semaphore to be unlocked,
# 	   Sem_wait(& sem) blocks until sem has the value 1.
# C Prototype: sem_wait(&semaphores[my_rank]);

# Agrs: a0= &semaphores[my_rank]

	.globl sem_wait

sem_wait:
	addi	$sp, $sp, -8
	sw	$ra, 0($sp)
	
	add $t0, $t0,$zero
	ll	$t1, 0($a0)
	sc	$t0, 0($a0)
	bne	$t0, 1, sem_wait
	bne	$t1, 1, sem_wait
	
	lw 	$ra, 0($sp)
	addi $sp, $sp, 8
	jr	$ra						
	 	