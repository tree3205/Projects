/* File:     pth_msg_sem.c
 * Purpose:  Try to implement semaphores with x86-64 implementation of binary semaphores.
 *
 * Functions: sem_init, sem_destroy, sem_post and sem_wait.
 * 
 */

 
###########################################################
# Function: sem_init
# Purpose: Initialize all semaphores to 0 -- i.e., locked 
# C Prototype: sem_init(&semaphores[thread], 0, 0);
# 
# Agrs: rdi = &semaphores[thread], rsi = 0, rdx = 0

		.section .text
		.global sem_init

sem_init:

		push	%rbp
		mov		%rsp, %rbp
		mov		%rdx, 0(%rdi)

		leave
		ret

###########################################################
# Function: sem_destroy
# Purpose: Destroy semaphores
# C Prototype: sem_destroy(&semaphores[thread]);
# 
# Agrs: rdi = &semaphores[thread]
	
		.global sem_destroy

sem_destroy:
		
		ret

###########################################################
# Function: sem_post
# Purpose: Unlock the semaphore of dest,
# 			Sem_post(& sem) sets sem to 1.
# C Prototype: sem_post(&semaphores[dest]);

# Agrs: rdi = &semaphores[dest]

		.global sem_post

sem_post:

		push	%rbp
		mov		%rsp,	%rbp

		mov		$1,		%r8
		xchg	%r8,	0(%rdi)

		leave
		ret

###########################################################
# Function: sem_wait
# Purpose: Wait for our semaphore to be unlocked,
# 			Sem_wait(& sem) blocks until sem has the value 1.
# C Prototype: sem_wait(&semaphores[my_rank]);

# Agrs: rdi = &semaphores[my_rank]

		.global	sem_wait

sem_wait:
		
		push	%rbp
		mov		%rsp,	%rbp

wait:
		mov		$0,		%r8
		xchg	%r8,	0(%rdi)

		cmp		$1,		%r8
		jne		wait

		leave
		ret

