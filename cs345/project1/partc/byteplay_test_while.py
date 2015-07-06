from byteplay import *
from types import CodeType, FunctionType

env = {}
code = []

l1 = Label()
l2 = Label()
l3 = Label()

code.append((LOAD_CONST, 3))
code.append((STORE_FAST, 'x'))
code.append((l1, None))
code.append((LOAD_FAST, 'x'))
code.append((LOAD_CONST, 0))
code.append((COMPARE_OP, '>'))
code.append((POP_JUMP_IF_FALSE, l2))
code.append((LOAD_FAST, 'x'))
code.append((PRINT_ITEM, None))
code.append((PRINT_NEWLINE, None))
code.append((LOAD_FAST, 'x'))
code.append((LOAD_CONST, 1))
code.append((BINARY_SUBTRACT, None))
code.append((STORE_FAST, 'x'))
code.append((JUMP_ABSOLUTE, l1))
code.append((l2, None))
code.append((LOAD_CONST, None))
code.append((RETURN_VALUE, None))

code_obj = Code(code, [], [], False, False, False, 'gencode', '', 0, '')
code = code_obj.to_code()
func = FunctionType(code, globals(), 'gencode')

print func()
