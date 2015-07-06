# calc_eval.py

import scanner
import parser
import ast


class EvalError(Exception):
    """ Eval Error """

    def __init__(self, ast):
        self.ast = ast

    def __str__(self):
        return 'Error at ast node: %s' % (str(self.ast))

class Evaluator(object):

    def __init__(self, tree):
        self.tree = tree
        self.env = {}

    def run(self):

        if type(self.tree) is not ast.Program:
            raise EvalError(self.tree)
        return self.eval_command(self.tree.command)

    def eval_command(self, tree):

        if type(tree) is ast.ExprCommand:
            return self.eval_expr(tree.expr)
        elif type(tree) is ast.AssignCommand:
            value = self.eval_expr(tree.expr)
            self.env[tree.name] = value
            return value
        elif type(tree) is ast.SeqCommand:
            self.eval_command(tree.command1)
            return self.eval_command(tree.command2)
        else:
            raise EvalError(tree)

    def eval_expr(self, tree):

        if type(tree) is ast.ExprVariable:
            return self.env[tree.name]
        elif type(tree) is ast.ExprValue:
            return tree.value
        elif type(tree) is ast.ExprBinary:
            v1 = self.eval_expr(tree.expr1)
            v2 = self.eval_expr(tree.expr2)
            op = tree.oper
            if op == '+':
                return v1 + v2
            elif op == '-':
                return v1 - v2
            elif op == '*':
                return v1 * v2
            elif op == '/':
                return v1 / v2
        else:
            raise EvalError(tree)


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

        eval = Evaluator(tree)
        print eval.run()
