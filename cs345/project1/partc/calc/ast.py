# calc_ast.py - Abstract Syntax Tree for Calculator Expressions


class AST(object):
    
    def __init__(self):
        pass


class Program(AST):

    def __init__(self, command):
        self.command = command

    def __str__(self):
        return '( %s )' % (str(self.command))


class Command(AST):
    pass


class SeqCommand(Command):

    def __init__(self, command1, command2):
        self.command1 = command1
        self.command2 = command2

    def __str__(self):
        return '( %s %s )' % (str(self.command1), str(self.command2))


class AssignCommand(Command):

    def __init__(self, name, expr):
        self.name = name
        self.expr  = expr

    def __str__(self):
        return '( %s = %s )' % (str(self.name), str(self.expr))


class ExprCommand(Command):

    def __init__(self, expr):
        self.expr  = expr

    def __str__(self):
        return '( %s )' % (str(self.expr))


class Expr(AST):
    pass


class ExprBinary(Expr):

    def __init__(self, expr1, oper, expr2):
        self.expr1 = expr1
        self.oper  = oper
        self.expr2 = expr2

    def __str__(self):
        return '( %s %s %s )' % (str(self.expr1), self.oper, str(self.expr2))


class ExprValue(Expr):

    def __init__(self, value):
        self.value = value

    def __str__(self):
        return '( %d )' % (self.value)


class ExprVariable(Expr):

    def __init__(self, name):
        self.name = name

    def __str__(self):
        return '( %s )' % (self.name)

        

if __name__ == '__main__':
    pass
