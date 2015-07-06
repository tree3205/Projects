# calc_ast.py - Abstract Syntax Tree for Calculator Expressions


class AST(object):
    
    def __init__(self):
        pass


class ExprFull(AST):

    def __init__(self, expr):
        self.expr = expr

    def __str__(self):
        return '( %s )' % (str(self.expr))


class ExprBinary(AST):

    def __init__(self, expr1, oper, expr2):
        self.expr1 = expr1
        self.oper  = oper
        self.expr2 = expr2

    def __str__(self):
        return '( %s %s %s )' % (str(self.expr1), self.oper, str(self.expr2))


class ExprValue(AST):

    def __init__(self, value):
        self.value = value


    def __str__(self):
        return '( %d )' % (self.value)
        

if __name__ == '__main__':

    ex1 = ExprFull(ExprBinary(ExprBinary(ExprValue(1), '+', ExprValue(2)), '+', ExprValue(3)))

    print ex1
