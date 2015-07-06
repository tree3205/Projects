from byteplay import *
from types import CodeType, FunctionType
import pprint
import sys

import scanner
import parser
import ast

import time
import struct
import marshal
import imp

class CodeGenError(Exception):
    """ Code Generator Error """

    def __init__(self, ast):
        self.ast = ast

    def __str__(self):
        return 'Error at ast node: %s' % (str(self.ast))


class CodeGen(object):

    def __init__(self, tree):
    
        self.tree = tree
        self.code = []
        self.env = []

    def add_env(self, name, type, value):
    
        print "OLD: %s" % self.env
        e = self.env[-1]
        tag = "%s%s" % (name, len(self.env) - 1)
        print "tag: %s" % tag
        e[name] = [tag, type, value]
        print "NEW: %s" % self.env
        print "=========="


    def lookup_env(self, name):
    
        print "LOOK: %s" % name
        print "ENV: %s" % self.env

        for e in self.env[::-1]:
            if name in e:
                print "e[name]: %s" % e[name]
                print "+++++++++"
                return e[name]
        #raise NameError(name)
        return [name, None, None]

    def generate_code(self):
    
        self.gen_command(self.tree)
        self.code.append((LOAD_CONST, None))
        self.code.append((RETURN_VALUE, None))

        pprint.pprint(self.code)
        return self.code

    def generate(self):
    
        if type(self.tree) is not ast.Program:
            raise CodeGenError(self.tree)

        self.gen_command(self.tree.command)
        self.code.append((LOAD_CONST, None))
        self.code.append((RETURN_VALUE, None))


        pprint.pprint(self.code)
        code_obj = Code(self.code, [], [], False, False, False, 'gencode', '', 0, '')
        code = code_obj.to_code()
        func = FunctionType(code, globals(), 'gencode')
        return func

    def gen_command(self, tree):
    
        if type(tree) is ast.AssignCommand:
            self.gen_Assign_Command(tree)
        elif type(tree) is ast.CallCommand:
            self.gen_Call_Command(tree)
        elif type(tree) is ast.SequentialCommand:
            self.gen_Sequential_Command(tree)
        elif type(tree) is ast.IfCommand:
            self.gen_If_Command(tree)
        elif type(tree) is ast.WhileCommand:
            self.gen_While_Command(tree)
        elif type(tree) is ast.LetCommand:
            self.gen_Let_Command(tree)
        elif type(tree) is ast.ReturnCommand:
            self.gen_Return_Command(tree)
        else:
            raise CodeGenError(tree)

    def gen_Assign_Command(self, tree):
    
        self.gen_expr(tree.expression)
        self.code.append((STORE_FAST, self.lookup_env(tree.variable.identifier)[0]))

    def gen_Call_Command(self, tree):
        self.gen_expr(tree.callexpression)

    def gen_Sequential_Command(self, tree):
    
        self.gen_command(tree.command1)
        self.gen_command(tree.command2)

    def gen_If_Command(self, tree):
    
        l1 = Label()
        l2 = Label()

        self.gen_expr(tree.expression)
        self.code.append((POP_JUMP_IF_FALSE, l1))
        self.gen_command(tree.command1)
        self.code.append((JUMP_FORWARD, l2))
        self.code.append((l1, None))
        self.gen_command(tree.command2)
        self.code.append((l2, None))


    def gen_While_Command(self, tree):
    
        l1 = Label()
        l2 = Label()
        l3 = Label()

        self.code.append((SETUP_LOOP,l1))
        self.code.append((l3, None))
        self.gen_expr(tree.expression)
        self.code.append((POP_JUMP_IF_FALSE, l2))
        self.gen_command(tree.command)
        self.code.append((JUMP_ABSOLUTE, l3))
        self.code.append((l2, None))
        self.code.append(((POP_BLOCK, None)))
        self.code.append((l1, None))


    def gen_Let_Command(self, tree):
    
        self.env.append({})
        self.gen_declaration(tree.declaration)
        self.gen_command(tree.command)
        self.env.pop()

    def gen_Return_Command(self,tree):

        if type(tree.expression) == ast.VnameExpression:
            self.code.append((LOAD_FAST, self.loopup_env(tree.expression.variable.identifier)[0]))
        elif type(tree.expression) == ast.IntegerExpression:
            print tree.expression.value
            self.code.append((LOAD_FAST, tree.expression.value))
        elif type(tree.expression) == ast.BinaryExpression:
            self.gen_expr(tree.expression)
        elif type(tree.expression) == ast.CallExpression:
            self.gen_expr(tree.expression)

        self.code.append((RETURN_VALUE, None))

    def gen_declaration(self, tree):
    
        if type(tree) is ast.ConstDeclaration:
            #self.add_env(tree.identifier, 'Integer', self.gen_expr(tree.expression))
            self.add_env(tree.identifier, 'Integer', None)
            self.gen_expr(tree.expression)
            self.code.append((STORE_FAST, self.lookup_env(tree.identifier)[0]))
        elif type(tree) is ast.VarDeclaration:
            self.add_env(tree.identifier, tree.type_denoter.identifier, None)
        elif type(tree) is ast.SequentialDeclaration:
            self.gen_declaration(tree.decl1)
            self.gen_declaration(tree.decl2)
        elif type(tree) is ast.FunctionDeclaration:
            c = CodeGen(tree.command)
            func_code = c.generate_code()
            name = tree.name
            parameters = c.get_parameter(tree.formal_args)

            code_obj = Code(func_code, [], parameters, False, False, False, name, '', 0, '')
            self.code.append((LOAD_CONST, code_obj))
            self.code.append((MAKE_FUNCTION, 0))
            self.code.append((STORE_FAST, tree.name))
        else:
            raise CodeGenError(tree)

    def get_parameter(self, tree):
        if type(tree) is ast.FormalArgs:
            return [tree.tk_argident]
        elif type(tree) is ast.SequentialFormalArgs:
            return [tree.formal_args1.tk_argident] + self.get_parameter(tree.formal_args2)

    def calc_args(self,tree):
        args_count = 0
        if type(tree) is ast.SequentialPassedArgs:
            args_count = self.calc_args(tree.passedArg1) + self.calc_args(tree.passedArg2)

        elif type(tree) is ast.PassedArg:
            args_count = args_count + 1
            return args_count

        return args_count

    def gen_expr(self, tree):
    
        if type(tree) is ast.IntegerExpression:
            self.code.append((LOAD_CONST, int(tree.value)))
            return tree.value
        elif type(tree) is ast.VnameExpression:
            self.gen_variable(tree.variable)
            return self.lookup_env(tree.variable.identifier)[2]
        elif type(tree) is ast.BinaryExpression:
            self.gen_expr(tree.expr1)
            self.gen_expr(tree.expr2)
            self.gen_oper(tree.oper)
        elif type(tree) is ast.PassedArg:
            if type(tree.passedArg) is str:
                self.code.append((LOAD_FAST, self.lookup_env(tree.passedArg)[0]))
            elif type(tree.passedArg) is int:
                self.code.append((LOAD_CONST, tree.passedArg))
        elif type(tree) is ast.SequentialPassedArgs:
            self.gen_expr(tree.passedArg1)
            self.gen_expr(tree.passedArg2)
        elif type(tree) is ast.CallExpression:
            arg_count = self.calc_args(tree.expression)
            if tree.identifier == 'putint' :
                self.gen_expr(tree.expression)
                self.code.append((PRINT_ITEM, None))
                self.code.append((PRINT_NEWLINE, None))
            elif tree.identifier == 'getint' and type(tree.expression) is ast.PassedArg:
                self.code.append((LOAD_GLOBAL, 'input'))
                self.code.append((CALL_FUNCTION, 0))
                self.add_env(tree.expression.passedArg, 'HARDCODE', None)
                self.code.append((STORE_FAST, self.lookup_env(tree.expression.passedArg)[0]))
            else:
                self.code.append((LOAD_FAST, tree.identifier))
                self.gen_expr(tree.expression)
                self.code.append((CALL_FUNCTION, arg_count))
        else:
            raise CodeGenError(tree)

    def gen_variable(self, tree):
    
        self.code.append((LOAD_FAST, self.lookup_env(tree.identifier)[0]))


    def gen_oper(self, tree):
    
        if tree == '+':
            self.code.append((BINARY_ADD, None))
        elif tree == '-':
            self.code.append((BINARY_SUBTRACT, None))
        elif tree == '*':
            self.code.append((BINARY_MULTIPLY, None))
        elif tree == '/':
            self.code.append((BINARY_DIVIDE, None))
        elif tree == '\\':
            self.code.append((BINARY_MODULO, None))
        elif tree == '>' or tree == '<':
            self.code.append((COMPARE_OP, tree))
        elif tree == '=':
            self.code.append((COMPARE_OP, '=='))

def write_pyc_file(code, name):

    pyc_file = name + '.pyc'
    print pyc_file
    with open(pyc_file,'wb') as pyc_f:
        magic = int(imp.get_magic().encode('hex'), 16)
        pyc_f.write(struct.pack(">L",magic))
        pyc_f.write(struct.pack(">L",time.time()))
        marshal.dump(code.func_code, pyc_f)

if __name__ == '__main__':

    if len(sys.argv) > 1:
        f = open(sys.argv[1])
        prog = f.read()
        f.close()

        s = scanner.Scanner(prog)
        p = parser.Parser(s.scan())
        tree = p.parse()
        c = CodeGen(tree)
        bytecode = c.generate()
        print bytecode()

        str = str(sys.argv[1])
        if str.endswith(".mt"):
            name = str[:-3]
            write_pyc_file(bytecode, name)





