# ast.py - Abstract Syntax Tree for Mini Triangle


class AST(object):
    
    def __init__(self):
        pass


class Program(AST):

    def __init__(self, command):
        self.command = command

    def __str__(self):
        return 'Program(%s)' % (str(self.command))


class AssignCommand(AST):

    def __init__(self, variable, expression):
        self.variable = variable
        self.expression = expression

    def __str__(self):
        return 'AssignCommand(%s,%s)' % (str(self.variable), str(self.expression))


class CallCommand(AST):

    def __init__(self, identifier, expression):
        self.identifier = identifier
        self.expression = expression

    def __str__(self):
        return 'CallCommand(%s,%s)' % (str(self.identifier), str(self.expression))


class SequentialCommand(AST):

    def __init__(self, command1, command2):
        self.command1 = command1
        self.command2 = command2

    def __str__(self):
        return 'SequentialCommand(%s,%s)' % (str(self.command1), str(self.command2))


class IfCommand(AST):

    def __init__(self, expression, command1, command2):
        self.expression = expression
        self.command1 = command1
        self.command2 = command2

    def __str__(self):
        return 'IfCommand(%s,%s,%s)' % (str(self.expression), str(self.command1), str(self.command2))


class WhileCommand(AST):

    def __init__(self, expression, command):
        self.expression = expression
        self.command = command

    def __str__(self):
        return 'WhileCommand(%s,%s)' % (str(self.expression), str(self.command))


class LetCommand(AST):

    def __init__(self, declaration, command):
        self.declaration = declaration
        self.command = command

    def __str__(self):
        return 'LetCommand(%s,%s)' % (str(self.declaration), str(self.command))


class IntegerExpression(AST):

    def __init__(self, value):
        self.value = value

    def __str__(self):
        return 'IntegerExpression(%s)' % (str(self.value))


class VnameExpression(AST):

    def __init__(self, variable):
        self.variable = variable

    def __str__(self):
        return 'VnameExpression(%s)' % (str(self.variable))


class UnaryExpression(AST):

    def __init__(self, operator, expression):
        self.operator = operator
        self.expression = expression

    def __str__(self):
        return 'UnaryExpression(%s,%s)' % (str(self.operator), str(self.expression))


class BinaryExpression(AST):

    def __init__(self, expr1, oper, expr2):
        self.expr1 = expr1
        self.oper  = oper
        self.expr2 = expr2

    def __str__(self):
        return 'BinaryExpression(%s,%s,%s)' % (str(self.expr1), self.oper, str(self.expr2))


class Vname(AST):

    def __init__(self, identifier):
        self.identifier = identifier

    def __str__(self):
        return 'Vname(%s)' % (str(self.identifier))


class ConstDeclaration(AST):

    def __init__(self, identifier, expression):
        self.identifier = identifier
        self.expression = expression

    def __str__(self):
        return 'ConstDeclaration(%s,%s)' % (str(self.identifier), str(self.expression))


class VarDeclaration(AST):

    def __init__(self, identifier, type_denoter):
        self.identifier = identifier
        self.type_denoter = type_denoter

    def __str__(self):
        return 'VarDeclaration(%s,%s)' % (str(self.identifier), str(self.type_denoter))


class SequentialDeclaration(AST):

    def __init__(self, decl1, decl2):
        self.decl1 = decl1
        self.decl2 = decl2

    def __str__(self):
        return 'SequentialDeclaration(%s,%s)' % (str(self.decl1), str(self.decl2))


class TypeDenoter(AST):

    def __init__(self, identifier):
        self.identifier = identifier

    def __str__(self):
        return 'TypeDonoter(%s)' % (str(self.identifier))


if __name__ == '__main__':
    program = Program()
    print program
    