#!/usr/bin/env python
#
# Scanner for a calculator interpreter

import calc_scanner2 as calc_scanner


class ParserError(Exception):
    """ Parser error exception.

        pos: Position in the input token stream where the error occurred.
        token: Bad token

    """
    def __init__(self, type,pos):
        self.type = type
        self.pos = pos

    def __str__(self):
        return '(%s at %s)' % (calc_scanner.TOKENS[self.type], self.pos)


class Parser(object):
    """Implement a parser for the following grammar:
    
       Expr      :== PriExpr (OPER PriExpr)* EOT
       PriExpr   :== INT | '(' Expr ')'
    """
    def __init__(self, tokens):
        self.tokens = tokens
        self.curindex = 0
        self.curtoken = tokens[0]
    
    def parse(self):
        self.parse_expr()
        
    def parse_expr(self):
        """ Expr :== PriExpr (OPER PriExpr)* EOT """

        self.parse_priexpr()
        token = self.token_current()
        while token.type == calc_scanner.TK_OPER:
            self.token_accept_any()
            self.parse_priexpr()
            token = self.token_current()
    
    def parse_priexpr(self):
        """ PriExpr   :== INT | '(' Expr ')' """
        token = self.token_current()
        if token.type == calc_scanner.TK_INT:
            self.token_accept_any()
        elif token.type == calc_scanner.TK_LPAREN:
            self.token_accept_any()
            self.parse_expr()
            self.token_accept(calc_scanner.TK_RPAREN)
        else:
            raise ParserError(token.type, self.curtoken.pos)

    def token_current(self):
        return self.curtoken
        
    def token_accept_any(self):
        if self.curtoken.type != calc_scanner.TK_EOT:
            self.curindex += 1
            self.curtoken = self.tokens[self.curindex]

    def token_accept(self, type):
        if self.curtoken.type != type:
            raise ParserError(type, self.curtoken.pos)
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
             '1 + + 2']

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
            parser.parse()
        except ParserError as e:
            print e
            print 'Not Parsed!'
            continue

        print 'Parsed!'
