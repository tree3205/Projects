#!/usr/bin/env python
#
# Scanner for a calculator interpreter

import calc_scanner3 as calc_scanner
import calc_ast as calc_ast


class ParserError(Exception):
    """ Parser error exception.

        pos: position in the input token stream where the error occurred.
        type: bad token type
    """

    def __init__(self, pos, type):
        self.pos = pos
        self.type = type

    def __str__(self):
        return '(Found bad token %s at %d)' % (calc_scanner.TOKENS[self.type], self.pos)


class Parser(object):
    """Implement a parser for the following grammar:
    
       # ExprLines :== Expr ';' (Expr ';') * EOT

       ExprFull  :== Expr EOT
       Expr      :== PriExpr (OPER PriExpr)*
       PriExpr   :== INT | '(' Expr ')'
    """

    def __init__(self, tokens):
        self.tokens = tokens
        self.curindex = 0
        self.curtoken = tokens[0]
    
    def parse(self):
        #self.parse_exprlines()
        e1 = self.parse_exprfull()
        return e1
        
    def parse_exprlines(self):
        """ ExprLines :== Expr ';' (Expr ';')* EOT """

        self.parse_expr()
        self.token_accept(calc_scanner.TK_SEMICOLON)
        while self.token_current().type != calc_scanner.TK_EOT:
            self.parse_expr()
            self.token_accept(calc_scanner.TK_SEMICOLON)
        self.token_accept(calc_scanner.TK_EOT)

    def parse_exprfull(self):
        """ ExprFull  :== Expr EOT """

        e1 = self.parse_expr()
        self.token_accept(calc_scanner.TK_EOT)
        return calc_ast.ExprFull(e1)

    def parse_expr(self):
        """ Expr :== PriExpr (OPER PriExpr)* EOT """

        e1 = self.parse_priexpr()
        token = self.token_current()
        while token.type == calc_scanner.TK_OPER:
            oper = token.val
            self.token_accept_any()
            e2 = self.parse_priexpr()
            token = self.token_current()
            e1 = calc_ast.ExprBinary(e1, oper, e2)
        return e1
    
    def parse_priexpr(self):
        """ PriExpr   :== INT | '(' Expr ')' """

        token = self.token_current()
        if token.type == calc_scanner.TK_INT:
            e1 = calc_ast.ExprValue(token.val)
            self.token_accept_any()
        elif token.type == calc_scanner.TK_LPAREN:
            #print "1.token.Value: %d"%token.type
            self.token_accept_any()
            #print "2.token.Value: %d"%token.type
            e1 = self.parse_expr()
            #print e1
            self.token_accept(calc_scanner.TK_RPAREN)
            #print "3.token.Value: %d"%token.type
        else:
            raise ParserError(self.curtoken.pos, self.curtoken.type)

        return e1

    def token_current(self):
        return self.curtoken
        
    def token_accept_any(self):
        # Do not increment curindex if curtoken is TK_EOT.
        if self.curtoken.type != calc_scanner.TK_EOT:
            self.curindex += 1
            self.curtoken = self.tokens[self.curindex]

    def token_accept(self, type):
        if self.curtoken.type != type:
            raise ParserError(self.curtoken.pos, self.curtoken.type)
        self.token_accept_any()


if __name__ == '__main__':
    exprs = ['1',
             '723',
             '1 + 1',
             '3 - 2',
             '9 * 10',
             '(1)',
             '1 + 2 * (3 + 4)',
             '(1   +   2)   *   (   (3 * 10) / 5)',
             '1 + &',
             '(',
             '((1+2)*3+(4*5)))',
             '1 + + 2',
              """!This is a test
                1 + 2; 
                3 + 4;
                """,
             '(1))']

    for exp in exprs:
        print '=============='
        print exp
        
        scanner = calc_scanner.Scanner(exp)
        
        try:
            tokens = scanner.scan()
            print tokens
        except calc_scanner.ScannerError as e:
            print e
            continue

        parser = Parser(tokens)

        try:
            tree = parser.parse()

            print "here is the tree: %s" %(tree)
        except ParserError as e:
            print e
            print 'Not Parsed!'
            continue

        print 'Parsed!'
