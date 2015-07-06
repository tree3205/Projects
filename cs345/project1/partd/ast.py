# ast.py - Abstract Syntax Tree for Mini Triangle


class AST(object):
    
    def __init__(self):
        pass


class Program(AST):

    def __init__(self, command):
        self.command = command

    def __str__(self):
        return 'Program(%s)' % (str(self.command))

class Command(AST):
    pass

class AssignCommand(Command):

    def __init__(self, variable, expression):
        self.variable = variable
        self.expression = expression

    def __str__(self):
        return 'AssignCommand(%s,%s)' % (str(self.variable), str(self.expression))


class CallCommand(Command):

    def __init__(self, callexpression):
        self.callexpression = callexpression

    def __str__(self):
        return 'CallCommand(%s)' % (str(self.callexpression))

class SequentialCommand(Command):

    def __init__(self, command1, command2):
        self.command1 = command1
        self.command2 = command2

    def __str__(self):
        return 'SequentialCommand(%s,%s)' % (str(self.command1), str(self.command2))


class IfCommand(Command):

    def __init__(self, expression, command1, command2):
        self.expression = expression
        self.command1 = command1
        self.command2 = command2

    def __str__(self):
        return 'IfCommand(%s,%s,%s)' % (str(self.expression), str(self.command1), str(self.command2))


class WhileCommand(Command):

    def __init__(self, expression, command):
        self.expression = expression
        self.command = command

    def __str__(self):
        return 'WhileCommand(%s,%s)' % (str(self.expression), str(self.command))


class LetCommand(Command):

    def __init__(self, declaration, command):
        self.declaration = declaration
        self.command = command

    def __str__(self):
        return 'LetCommand(%s,%s)' % (str(self.declaration), str(self.command))


class ReturnCommand(Command):
    def __init__(self, expression):
        self.expression = expression

    def __str__(self):
        return 'ReturnCommand(%s)' % (str(self.expression))

class Expression(AST):
    pass

class IntegerExpression(Expression):

    def __init__(self, value):
        self.value = value

    def __str__(self):
        return 'IntegerExpression(%s)' % (str(self.value))


class VnameExpression(Expression):

    def __init__(self, variable):
        self.variable = variable

    def __str__(self):
        return 'VnameExpression(%s)' % (str(self.variable))


class CallExpression(Expression):

    def __init__(self, identifier, expression):
        self.identifier = identifier
        self.expression = expression

    def __str__(self):
        return 'CallExpression(%s,%s)' % (str(self.identifier), str(self.expression))


class BinaryExpression(Expression):

    def __init__(self, expr1, oper, expr2):
        self.expr1 = expr1
        self.oper  = oper
        self.expr2 = expr2

    def __str__(self):
        return 'BinaryExpression(%s,%s,%s)' % (str(self.expr1), self.oper, str(self.expr2))

class PassedArg(Expression):

    def __init__(self, passedArg):
        self.passedArg = passedArg

    def __str__(self):
        return 'PassedArg(%s)' % (str(self.passedArg))


class SequentialPassedArgs(Expression):

    def __init__(self, passedArg1, passedArg2):
        self.passedArg1 = passedArg1
        self.passedArg2 = passedArg2

    def __str__(self):
        return 'SequentialPassedArgs(%s,%s)' % (str(self.passedArg1), str(self.passedArg2))


class FormalArgs(Expression):

    def __init__(self, tk_argident, arg_type):
        self.tk_argident = tk_argident
        self.arg_type = arg_type

    def __str__(self):
        return 'FormalArgs(%s,%s)' % (str(self.tk_argident), str(self.arg_type))


class SequentialFormalArgs(Expression):

    def __init__(self, formal_args1, formal_args2):
        self.formal_args1 = formal_args1
        self.formal_args2 = formal_args2

    def __str__(self):
        return 'SequentialFormalArgs(%s,%s)' % (str(self.formal_args1), str(self.formal_args2))

class Vname(AST):

    def __init__(self, identifier):
        self.identifier = identifier

    def __str__(self):
        return 'Vname(%s)' % (str(self.identifier))


class Declaration(AST):
    pass


class ConstDeclaration(Declaration):

    def __init__(self, identifier, expression):
        self.identifier = identifier
        self.expression = expression

    def __str__(self):
        return 'ConstDeclaration(%s,%s)' % (str(self.identifier), str(self.expression))


class VarDeclaration(Declaration):

    def __init__(self, identifier, type_denoter):
        self.identifier = identifier
        self.type_denoter = type_denoter

    def __str__(self):
        return 'VarDeclaration(%s,%s)' % (str(self.identifier), str(self.type_denoter))

class FunctionDeclaration(Declaration):

    def __init__(self, name, formal_args, return_type_denoter, command):
        self.name = name
        self.formal_args = formal_args
        self.return_type_denoter = return_type_denoter
        self.command = command

    def __str__(self):
        return 'FunctionDeclaration(%s,%s,%s,%s)' % (str(self.name),
                                                     str(self.formal_args),
                                                     str(self.return_type_denoter),
                                                     str(self.command))

class SequentialDeclaration(Declaration):

    def __init__(self, decl1, decl2):
        self.decl1 = decl1
        self.decl2 = decl2

    def __str__(self):
        return 'SequentialDeclaration(%s,%s)' % (str(self.decl1), str(self.decl2))


class TypeDenoter(AST):

    def __init__(self, identifier):
        self.identifier = identifier

    def __str__(self):
        return 'TypeDenoter(%s)' % (str(self.identifier))


if __name__ == '__main__':
    program = Program()
    print program
    