#!/usr/bin/env python
#
# Scanner for a calculator interpreter

import scanner
import ast


class ParserError(Exception):
    """ Parser error exception.

        pos: position in the input token stream where the error occurred.
        type: bad token type
    """

    def __init__(self, pos, type):
        self.pos = pos
        self.type = type

    def __str__(self):
        return '(Found bad token %s at %d)' % (scanner.TOKENS[self.type], self.pos)


class Parser(object):
    """Implement a parser for the following grammar:
    
       Program   :== Command ';' (Command ';')* EOT
       Command   :== Expr | IDENT = Expr
       Expr      :== SecExpr (OPER_SEC SecExpr)*
       SecExpr   :== PriExpr (OPER_PRI PriExpr)*       
       PriExpr   :== IDENT | INT | '(' Expr ')'

       OPER_SEC = (+,-)
       OPER_PRI = (*,/)
    """

    def __init__(self, tokens):
        self.tokens = tokens
        self.curindex = 0
        self.curtoken = tokens[0]
    
    def parse(self):
        #self.parse_exprlines()
        e1 = self.parse_program()
        return e1
        
    def parse_program(self):
        """ Program :== Command ';' (Command ';')* EOT """

        c1 = self.parse_command()
        self.token_accept(scanner.TK_SEMICOLON)
        while self.token_current().type != scanner.TK_EOT:
            c2 = self.parse_command()
            self.token_accept(scanner.TK_SEMICOLON)
            c1 = ast.SeqCommand(c1, c2)
        self.token_accept(scanner.TK_EOT)

        return ast.Program(c1)

    def parse_command(self):
        """ Command   :== Expr | IDENT = Expr """

        token = self.token_current()
        next_token = self.token_lookahead()
        if next_token.type == scanner.TK_EQUALS:
            self.token_accept(scanner.TK_IDENT)
            ident = token.val
            self.token_accept_any()
            expr = self.parse_expr()
            return ast.AssignCommand(ident, expr)
        else:
            e1 = self.parse_expr()
            return ast.ExprCommand(e1)

    def parse_expr(self):
        """ Expr :== SecExpr (OPER_SEC SecExpr)*"""

        e1 = self.parse_secexpr()
        token = self.token_current()
        while token.type == scanner.TK_OPER and token.val in ['+','-']:
            oper = token.val
            self.token_accept_any()
            e2 = self.parse_secexpr()
            token = self.token_current()
            e1 = ast.ExprBinary(e1, oper, e2)
        return e1
    
    def parse_secexpr(self):
        """ SecExpr :== PriExpr (OPER_PRI PriExpr)*"""

        e1 = self.parse_priexpr()
        token = self.token_current()
        while token.type == scanner.TK_OPER and token.val in ['*', '/']:
            oper = token.val
            self.token_accept_any()
            e2 = self.parse_priexpr()
            token = self.token_current()
            e1 = ast.ExprBinary(e1, oper, e2)
        return e1


    def parse_priexpr(self):
        """ PriExpr   :== IDENT | INT | '(' Expr ')' """

        token = self.token_current()

        if token.type == scanner.TK_IDENT:
            e1 = ast.ExprVariable(token.val)
            self.token_accept_any()
        elif token.type == scanner.TK_INT:
            e1 = ast.ExprValue(token.val)
            self.token_accept_any()
        elif token.type == scanner.TK_LPAREN:
            self.token_accept_any()
            e1 = self.parse_expr()
            self.token_accept(scanner.TK_RPAREN)
        else:
            raise ParserError(self.curtoken.pos, self.curtoken.type)

        return e1

    def token_current(self):
        return self.curtoken
        
    def token_lookahead(self):
        return self.tokens[self.curindex + 1]

    def token_accept_any(self):
        # Do not increment curindex if curtoken is TK_EOT.
        if self.curtoken.type != scanner.TK_EOT:
            self.curindex += 1
            self.curtoken = self.tokens[self.curindex]

    def token_accept(self, type):
        if self.curtoken.type != type:
            raise ParserError(self.curtoken.pos, self.curtoken.type)
        self.token_accept_any()


if __name__ == '__main__':
    exprs = ['1;',
              """!This is a test
                1 + 2; ! Foo
                3 + 4;
                """,
             '(1));',
             '1 + 2 * 3;',
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

        parse = Parser(tokens)

        try:
            tree = parse.parse()
            print tree
        except ParserError as e:
            print e
            print 'Not Parsed!'
            continue

        print 'Parsed!'
