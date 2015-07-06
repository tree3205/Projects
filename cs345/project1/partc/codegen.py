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
        print "### add_env %s <= %s" % (name, value)
        print "OLD add_env: %s" % self.env
        e = self.env[-1]
        e[name] = [type, value]
        print "NEW add_env: %s" % self.env

    def lookup_env(self, name):
        print "lookup_env: %s" % self.env
        for e in self.env[::-1]:
            if name in e:
                print "### lookup_env %s => %s" % (name, e[name])
                return e[name]

        raise NameError(name)

    def update_env(self, name, value):
        print "### update_env %s <= %s" % (name, value)
        print "OLD update_env: %s" % self.env
        for e in self.env[::-1]:
            if name in e:
                e[name][1] = value
                print "NEW update_env: %s" % self.env
                return

        raise NameError(name)

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

        self.update_env(tree.variable.identifier, self.gen_expr(tree.expression))
        #self.gen_expr(tree.expression)
        self.code.append((STORE_FAST, tree.variable.identifier))

    def gen_Call_Command(self, tree):
        self.gen_expr(tree.callexpression)

        "need to work"


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
        self.code.append((POP_TOP, None))
        self.env.pop()
        print "^^^"
        print self.env

    def gen_Return_Command(self,tree):

        if type(tree.expression) == ast.VnameExpression:
            tmp = self.lookup_env(tree.expression.variable.identifier)[1]
            self.code.append((LOAD_CONST, tmp))
        elif type(tree.expression) == ast.IntegerExpression:
            self.code.append((LOAD_CONST, tree.expression.value))
        elif type(tree.expression) == ast.BinaryExpression:
            self.gen_expr(tree.expression)
        elif type(tree.expression) == ast.CallExpression:
            self.gen_expr(tree.expression)

        self.code.append((RETURN_VALUE, None))

    def gen_declaration(self, tree):
        if type(tree) is ast.ConstDeclaration:
            self.add_env(tree.identifier, 'Integer', self.gen_expr(tree.expression))
            #self.gen_expr(tree.expression)
            self.code.append((STORE_FAST, tree.identifier))
        elif type(tree) is ast.VarDeclaration:
            self.add_env(tree.identifier, tree.type_denoter.identifier, None)
            pass
        elif type(tree) is ast.SequentialDeclaration:
            self.gen_declaration(tree.decl1)
            self.gen_declaration(tree.decl2)
        elif type(tree) is ast.FunctionDeclaration:
            cx = CodeGen(tree.command)
            cx.env = self.env
            paras = cx.get_parameter(tree.formal_args)
            parameters = [i[0] for i in paras]
            for pm in paras:
                print pm
                cx.add_env(pm[0], pm[1], 'PARSED_VAR')
            func_code = cx.generate_code()
            name = tree.name


            code_obj = Code(func_code, [], parameters, False, False, False, name, '', 0, '')
            self.code.append((LOAD_CONST, code_obj))
            self.code.append((MAKE_FUNCTION, 0))
            self.code.append((STORE_FAST, tree.name))


            "need to work"

        else:
            raise CodeGenError(tree)

    def get_parameter(self, tree):
        if type(tree) is ast.FormalArgs:
            return [(tree.tk_argident, tree.arg_type.identifier)]
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
            return self.lookup_env(tree.variable.identifier)[1]

        elif type(tree) is ast.BinaryExpression:

            self.gen_expr(tree.expr1)
            self.gen_expr(tree.expr2)
            self.gen_oper(tree.oper)

            "need to work"

        elif type(tree) is ast.PassedArg:
            if type(tree.passedArg) is str:
                tmp = self.lookup_env(tree.passedArg)[1]
                if tmp == 'PARSED_VAR':
                    self.code.append((LOAD_FAST, tree.passedArg))
                else:
                    self.code.append((LOAD_CONST, tmp))
                    #self.code.append((LOAD_FAST, tree.passedArg))
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
                #self.code.append((LOAD_GLOBAL, 'input'))
                #self.code.append((CALL_FUNCTION, 0))
                tmp = input()
                self.update_env(tree.expression.passedArg, tmp)
                self.code.append((LOAD_CONST, tmp))
                self.code.append((STORE_FAST, tree.expression.passedArg))

            else:
                self.code.append((LOAD_FAST, tree.identifier))
                self.gen_expr(tree.expression)
                self.code.append((CALL_FUNCTION, arg_count))
        else:
            raise CodeGenError(tree)

    def gen_variable(self, tree):
        tmp = self.lookup_env(tree.identifier)[1]
        if tmp == 'PARSED_VAR':
            self.code.append((LOAD_FAST, tree.identifier))
        else:
            self.code.append((LOAD_CONST, tmp))
        #self.code.append((LOAD_FAST, tree.identifier))


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





