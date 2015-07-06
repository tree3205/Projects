 # File:    bubble.c
 #
 # Purpose: Use bubble sort to sort a list of ints.
 #
 # Compile: gcc -g -Wall -o bubble bubble.c
 # Usage:   bubble <n> <g|i>
 #             n:   number of elements in list
 #            'g':  generate list using a random number generator
 #            'i':  user input list
 #
 # Args:		rdi = a
 #				rsi = n
 #
 #

        .section .text

        .global Bubble_sort


Bubble_sort:
			push	%rbp
			mov 	%rsp,	%rbp
			sub		$24,	%rsp			# Make space to put i, n, array on the stack

			# NUmber of elements list_length = %r8 = n = %rsi. Save on the stack
			mov 	%rsi,	%r8             # r8 = list_length = n
			mov 	%r8, 	16(%rsp)        # n

			# Current element in subscript i = %r9
			mov 	$0, 	%r9
			mov  	%r9, 8(%rsp)       		# Store i = r9 on stack

			# Save array on the stack
			mov		%rdi, 	0(%rsp)			# a[]

loop_tst1:	
			cmp 	$2, 	%r8				# Is list_length = n >= 2?
			jge		loop_tst2				# If list_length = n >= 2, jump to the second loop
			jmp 	done_loop1				# If list_length = n < 2, we're done

loop_tst2:  
			sub 	$1, 	%r8 			# Put list_length-1 in r8
			cmp 	%r8,	%r9				# If i = r9 >= list_length-1 = r8?
			jge		done_loop2				# If i = r9 >= list_length-1 = r8, jump to done_loop2
			mov		16(%rsp), %r8			# Reteive list_length = r8

			# Put a[i] in r10
			mov		0(%rsp), %r12			# Reteive array a
			mov 	0(%r12, %r9, 8), %r10 	# a[i] is located at a + i*8 = r12 + r9*8 = r10

			# Put a[i+1] in r11			
			add		$1, 	%r9 			# r9 = i+1
			mov 	0(%r12, %r9, 8), %r11   # a[i+1] is located at a + (i+1)*8 = r12 + r9*8 = r11

			cmp 	%r11,	%r10			# If a[i] = r10 <= a[i+1] = r11?
			jle		done_if					# If a[i] = r10 <= a[i+1] = r11, jump to done_if


			mov		8(%rsp), %r9			# Reteive r9 = i
			imul	$8,		%r9				# r9 = r9*8	
			add		%r12,	%r9				# Get the absolute address of a[i]: r9 = a + i*8
			mov 	%r9,	%rdi			# Put the absolute address of a[i] in the first arg

			mov		8(%rsp), %r9			# Reteive r9 = i
			add 	$1,		%r9				# i = i+1
			imul	$8,		%r9				# r9 = (i+1)*8
			add 	%r12,	%r9				# Get the absolute address of a[i+1]: r9 = a (+(i+1)*8
			mov		%r9,	%rsi			# Put the absolute address of a[i+1] in the second arg
			call	Swap
			jmp 	done_if

done_if:	
			mov		8(%rsp), %r9			# Reteive i = r9
			add		$1,		%r9				# i++
			mov		%r9, 	8(%rsp)			# Save new i on the stack
			jmp 	loop_tst2

done_loop2:
			mov		16(%rsp), %r8			# Reteive list_length = r8
			sub 	$1, 	%r8				# list_length--
			mov		%r8, 	16(%rsp)		# Save new list_length on the stack
			jmp 	loop_tst1

done_loop1:
			leave
        	ret


#######################################################################
 # Function:     Swap
 # Purpose:      Swap contents of x_p and y_p
 #
 # Args:
 #				rdi = *x_p
 #				rsi = *y_p


Swap:
        	push 	%rbp
        	mov  	%rsp, 	%rbp		

        	mov		0(%rdi), %r10			# r10 = *x_p
        	mov		0(%rsi), %r11			# r11 = *y_p
        	mov 	%r10,	0(%rsi)			# *y_p = r10
        	mov 	%r11,	0(%rdi)			# *x_p = r11

        	leave
        	ret










