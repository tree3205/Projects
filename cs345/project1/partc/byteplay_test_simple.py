from byteplay import *
from types import CodeType, FunctionType

env = {}
code = []

code.append((LOAD_CONST, 1))
code.append((LOAD_CONST, 2))
code.append((BINARY_ADD, None))
code.append((RETURN_VALUE, None))

code_obj = Code(code, [], [], False, False, False, 'gencode', '', 0, '')
code = code_obj.to_code()
func = FunctionType(code, globals(), 'gencode')

print func()
