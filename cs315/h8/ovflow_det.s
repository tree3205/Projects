/* File:     ovflow_dec.c
 * Purpose:   Implement the stub which is supposed to detect whether addition of 
 *				two signed longs has overflowed with X86-64.
 *
 * Functions: Overflow
 * Args: rdi = m, rsi = n
 */

		.section .text
		.global Overflow

Overflow:
		push	%rbp
		mov		%rsp,	%rbp		

		mov		$0x7fffffffffffffff, %r8 	# r8 = max
		mov		$0x8000000000000000, %r9	# r9 = min

		cmp		$0,		%rsi
		jg		if_overflow
		jl		if_underflow

		mov		$0,		%rax
		jmp		done

if_overflow:
		sub 	%rsi,	%r8		# r8 = r8 - rsi = max - n
		cmp		%r8,	%rdi	# if m = rdi > max - n = r8?
		jg		ovflow
        mov		$0,		%rax
		jmp		done

if_underflow:
		sub 	%rsi,	%r9		# r9 = r9 - rsi = min - n
		cmp		%r9,	%rdi	# if m = rdi < mim - n = r9?
		jl		ovflow
        mov		$0,		%rax
		jmp		done

ovflow:
		mov		$1,		%rax
        
done:
		leave
		ret


/* # Overflow2
 * Overflow:
 *		add 	%rsi,	%rdi		#rdi = rsi + rdi = m + n
 *		jo		overflow
 *		mov		$0,		%rax        # if not overflow, return 0
 *		jmp 	done
 *
 * overflow:
 *		mov		$1,		%rax		# if overflow, return 1
 *		
 * done:
 *		leave
 *		ret
 *
 */