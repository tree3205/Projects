# calc_eval.py

from byteplay import *
from types import CodeType, FunctionType
import pprint

import scanner
import parser
import ast


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
        self.env = {}

    def generate(self):

        if type(self.tree) is not ast.Program:
            raise CodeGenError(self.tree)
        else:
            self.gen_command(self.tree.command)
            self.code.append((RETURN_VALUE, None))

            pprint.pprint(self.code)

            code_obj = Code(self.code, [], [], False, False, False, 'gencode', '', 0, '')
            code = code_obj.to_code()
            func = FunctionType(code, globals(), 'gencode')
            return func

    def gen_command(self, tree):

        if type(tree) is ast.ExprCommand:
            self.gen_expr(tree.expr)
        elif type(tree) is ast.AssignCommand:
            self.gen_expr(tree.expr)
            self.code.append((STORE_FAST, tree.name))
        elif type(tree) is ast.SeqCommand:
            self.gen_command(tree.command1)
            self.gen_command(tree.command2)
        else:
            raise CodeGenError(tree)

    def gen_expr(self, tree):

        if type(tree) is ast.ExprVariable:
            self.code.append((LOAD_FAST, tree.name))
        elif type(tree) is ast.ExprValue:
            self.code.append((LOAD_CONST, tree.value))
        elif type(tree) is ast.ExprBinary:
            self.gen_expr(tree.expr1)
            self.gen_expr(tree.expr2)
            op = tree.oper
            if op == '+':
                self.code.append((BINARY_ADD, None))
            elif op == '-':
                self.code.append((BINARY_SUBTRACT, None))
            elif op == '*':
                self.code.append((BINARY_MULTIPLY, None))
            elif op == '/':
                self.code.append((BINARY_DIVIDE, None))
        else:
            raise CodeGenError(tree)


if __name__ == '__main__':
    exprs = [ '1 + 2 * 3;',
              '1 + 2 * 3 + 4;',
              '1 * 2 + 3 / 4;',
              """a = 1;
                 b = 2;
                 a + b;"""]

    for exp in exprs:
        print '=============='
        print exp
        
        scan = scanner.Scanner(exp)
        
        try:
            tokens = scan.scan()
            print tokens
        except scanner.ScannerError as e:
            print e
            continue

        parse = parser.Parser(tokens)

        try:
            tree = parse.parse()
            print tree
        except parser.ParserError as e:
            print e
            print 'Not Parsed!'
            continue

        cg = CodeGen(tree)
        code = cg.generate()
        print code()
