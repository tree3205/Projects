#!/usr/bin/env python
## coding=utf-8
# Parser for Mini Triangle

import scanner as  mini_scanner
import ast as mini_ast


class ParserError(Exception):
    """ Parser error exception.

        pos: position in the input token stream where the error occurred.
        type: bad token type
    """

    def __init__(self, pos, type):
        self.pos = pos
        self.type = type

    def __str__(self):
        return '(Found bad token %s at %d)' % (mini_scanner.TOKENS[self.type], self.pos)


class Parser(object):
    """Implement a parser for the following grammar:
    
       Program          :== Command EOT

       Command          :== sin
       CallCommand      :== Identifier '(' Expr ')'gle-command ( ';' singgle-command )* EOT
       single-command   :== AssignCommand | CallCommand | IfCommand | WhileCommand| LetCommand
       AssignCommand    :== Variable ':=' Expr
       IfCommand        :== if  Expr then single-command else single-command
       WhileCommand     :== while Expr do single-command
       LetCommand       :== let Declaration in single-command
       Vname            :== Identifier

       Expr             :== PriExpr (Operator PriExpr)*
       PriExpr          :== IntegerExprssion| VnameExprssion | UnaryExpression 

       IntegerExprssion :== Value
       VnameExprssion   :== Variable
       UnaryExpression  :== OPER PriExpr

       Declaration :== single-declaration (';' single-declaration)* 
       single-Declaration :==  const Identifier '~' Expr | var identifier' :' Type-denoter

    """

    def __init__(self, tokens):
        self.tokens = tokens
        self.curindex = 0
        self.curtoken = tokens[0]
    
    def parse(self):

        c1 = self.parse_program()
        return c1


    def parse_program(self):
        """ Program  :== Command EOT """

        c1 = self.parse_command()
        self.token_accept(mini_scanner.TK_EOT)
        return mini_ast.Program(c1)

        """e1 = self.parse_expr()
        self.token_accept(mini_scanner.TK_EOT)
        return mini_ast.Program(e1)"""

        """e1 = self.parse_declaration()
        self.token_accept(mini_scanner.TK_EOT)
        return mini_ast.Program(e1)"""

    def parse_command(self):
        """ Command  :== single-command (';' singgle-command )* """

        c1 = self.parse_singlecommand()
        while self.token_current().type == mini_scanner.TK_SEMICOLON:
            self.token_accept_any()
            c2 = self.parse_singlecommand()
            c1 = mini_ast.SequentialCommand(c1, c2)
    
        return c1

    def parse_singlecommand(self):
        """ AssignCommand    :== Vname ':=' Expr
            CallCommand      :== Identifier '(' Expr ')'
            IfCommand        :== if  Expr then single-command else single-command
            WhileCommand     :== while Expr do single-command
            LetCommand       :== let Declaration in single-command
            BeginCommand     :== begin Command end
            Vname            :== Identifier
            VnameExprssion   :== Variable"""
        
        
        token = self.token_current()  
        next_token = self.token_next()
        if token.type == mini_scanner.TK_IDENTIFIER and next_token.type == mini_scanner.TK_BECOMES:
            v1 =  mini_ast.Vname(token.val)
            self.token_accept_any()
            self.token_accept(mini_scanner.TK_BECOMES)
            e1 = self.parse_expr()
            c1 = mini_ast.AssignCommand(v1, e1)
        elif token.type == mini_scanner.TK_IDENTIFIER and next_token.type == mini_scanner.TK_LPAREN:
            v1 = token.val     
            self.token_accept_any()
            self.token_accept(mini_scanner.TK_LPAREN)
            e1 = self.parse_expr()
            self.token_accept(mini_scanner.TK_RPAREN)
            c1 = mini_ast.CallCommand(v1, e1)
        elif token.type == mini_scanner.TK_IF:
            self.token_accept_any()
            e1 = self.parse_expr()
            self.token_accept(mini_scanner.TK_THEN)
            c1 = self.parse_singlecommand()
            self.token_accept(mini_scanner.TK_ELSE)
            c2 = self.parse_singlecommand()
            c1 = mini_ast.IfCommand(e1, c1, c2)
        elif token.type == mini_scanner.TK_WHILE:
            self.token_accept_any()
            e1 = self.parse_expr()
            self.token_accept(mini_scanner.TK_DO)
            c1 = self.parse_singlecommand()
            c1 = mini_ast.WhileCommand(e1, c1)
        elif token.type == mini_scanner.TK_LET:
            self.token_accept_any()         
            d1 = self.parse_declaration()
            self.token_accept(mini_scanner.TK_IN)
            c1 = self.parse_singlecommand()
            c1 = mini_ast.LetCommand(d1, c1)
        elif token.type == mini_scanner.TK_BEGIN:
            self.token_accept_any()
            c1 = self.parse_command()
            self.token_accept(mini_scanner.TK_END)
        else:
            raise ParserError(self.curtoken.pos, self.curtoken.type)

        return c1


    def parse_expr(self):
        """ Expr :== PriExpr (Operator PriExpr)* """

        e1 = self.parse_priexpr()
        token = self.token_current()
        while token.type == mini_scanner.TK_OPERATOR:
            oper = token.val
            self.token_accept_any()
            e2 = self.parse_priexpr()
            token = self.token_current()
            e1 = mini_ast.BinaryExpression(e1, oper, e2)
        return e1
        
    def parse_priexpr(self):
        """ 
            PriExpr          :== IntegerExprssion| VnameExprssion | UnaryExpression 
                                 | '(' Expr ')' 
            IntegerExprssion :== Value
            VnameExprssion   :== Variable
            UnaryExpression  :== OPER PriExpr
            
        '"""
        token = self.token_current()
        if  token.type == mini_scanner.TK_INTLITERAL:
            e1 = mini_ast.IntegerExpression(token.val)
            self.token_accept_any()
        elif token.type == mini_scanner.TK_IDENTIFIER:
            e1 = mini_ast.VnameExpression(mini_ast.Vname(token.val))
            self.token_accept_any()
        elif token.type == mini_scanner.TK_OPERATOR:
            oper = token.val
            self.token_accept_any()
            e1 = self.parse_priexpr()
            e1 = mini_ast.UnaryExpression(oper, e1)
        elif token.type == mini_scanner.TK_LPAREN:
            self.token_accept_any()
            e1 = self.parse_expr()
            self.token_accept(mini_scanner.TK_RPAREN)
        else:
            raise ParserError(self.curtoken.pos, self.curtoken.type)
        return e1


    def parse_declaration(self):
        """ Declaration :== single-declaration (';' single-declaration)* """

        d1 = self.parse_singledeclaration()
        
        while self.token_current().type == mini_scanner.TK_SEMICOLON:
            self.token_accept_any()
            d2 = self.parse_singledeclaration()
            d1 = mini_ast.SequentialDeclaration(d1, d2)
        return d1

    def parse_singledeclaration(self):
        """ single-Declaration :==  const Identifier '~' Expr
                                   | var Identifier ':' Type-denoter """
        
        token = self.token_current()
        next_token = self.token_next()
        if token.type == mini_scanner.TK_CONST and next_token.type == mini_scanner.TK_IDENTIFIER:
            e1 = next_token.val
            self.token_accept_any()
            self.token_accept_any()
            self.token_accept(mini_scanner.TK_IS)
            e2 = self.parse_expr()
            e1 = mini_ast.ConstDeclaration(e1, e2)
        elif token.type == mini_scanner.TK_VAR and next_token.type == mini_scanner.TK_IDENTIFIER:
            e1 = next_token.val
            self.token_accept_any()
            self.token_accept_any()
            self.token_accept(mini_scanner.TK_COLON)
            token = self.token_current()
            e2 = mini_ast.TypeDenoter(token.val)
            self.token_accept(mini_scanner.TK_IDENTIFIER)
            e1 = mini_ast.VarDeclaration(e1, e2)
        else:
            raise ParserError(self.curtoken.pos, self.curtoken.type)

        return e1




    def token_current(self):
        return self.curtoken

    def token_next(self):
        return self.tokens[self.curindex + 1]

    def token_accept_any(self):
        # Do not increment curindex if curtoken is TK_EOT.
        if self.curtoken.type != mini_scanner.TK_EOT:
            self.curindex += 1
            self.curtoken = self.tokens[self.curindex]

    def token_accept(self, type):
        if self.curtoken.type != type:
            raise ParserError(self.curtoken.pos, self.curtoken.type)
        self.token_accept_any()


if __name__ == '__main__':
    exprs = ["""! Factorial
                let var x: Integer;
                    var fact: Integer
                in
                    begin
                        getint(x);
                        fact := 1;
                        while x > 0 do
                            begin
                                fact := fact * x;
                                x := x - 1
                            end;
                            putint(fact)
                    end"""]

    for exp in exprs:
        print '=============='
        print exp
        
        scanner = mini_scanner.Scanner(exp)
        
        try:
            tokens = scanner.scan()
            print tokens
        except mini_scanner.ScannerError as e:
            print e
            continue

        parser = Parser(tokens)

        try:
            tree = parser.parse()

            print "!!! here is the tree: %s" %(tree)
        except ParserError as e:
            print e
            print 'Not Parsed!'
            continue

        print 'Parsed!'

    
