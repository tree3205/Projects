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
    """ Implement a parser for the following grammar:
    
        Program          :== Command EOT

        Command          :== single-command (single-command) *
        single-command   :== AssignCommand | CallCommand | IfCommand | WhileCommand| LetCommand | BeginCommand | ReturnCommand
        AssignCommand    :== Variable ':=' Expr  ';'
        CallCommand      :== Identifier '('FormalArgs')' ';'
        IfCommand        :== if  Expr then single-command else single-command
        WhileCommand     :== while Expr do single-command
        LetCommand       :== let Declaration in single-command
        BeginCommand     :== begin Command end
        ReturnCommand    :== return Expr
        Vname            :== Identifier

        Declaration      :==  single-declaration (single-declaration)*
        single-Declaration :==  const Identifier '~' Expr ';'| var identifier' :' Type-denoter ';'
                                | func Identifier '(' FormalArgs')' ':'Type-denoter
                                        single-command;

        Expr             :== BinaryExpr | ArgsExpr

        BinaryExpr       :== BaseExpr (OPER_BASE BaseExpr)+ --> +,-,>,<,=
        BaseExpr         :== SecExpr (OPER_SEC SecExpr)+ --> *,/,\ \
        SecExpr          :== PriExpr(OPER_PRI PriExpr)+ -->(,)
        PriExpr          :==  IntegerExprssion| VnameExprssion | CallExpression | '(' Expr ')'
        IntegerExprssion :== Value | '-'Value
        VnameExprssion   :== Variable
        CallExpression   :== Identifier '(' PassedArgs ')'


        ArgsExpr         :== FormalArgs | PassedArgs
        FormalArgs :== Identifier ':' args-Typedenoter ( ',' Identifier ':'  args-Typedenoter )*
        PassedArgs :== Identifier (',' Identifier )* | Value (',' Value )*


    """

    def __init__(self, tokens):
        self.tokens = tokens
        self.curindex = 0
        self.curtoken = tokens[0]
        self.env = {}
    
    def parse(self):

        #c1 = self.parse_expr()
        c1 = self.parse_program()
        return c1


    def parse_program(self):
        """ Program  :== Command EOT """

        c1 = self.parse_command()
        self.token_accept(mini_scanner.TK_EOT)
        return mini_ast.Program(c1)

    def parse_command(self):
        """ Command          :== single-command (single-command)*"""

        c1 = self.parse_singlecommand()
        token = self.token_current()
        while token.type == mini_scanner.TK_IDENTIFIER or token.type == mini_scanner.TK_IF or token.type == mini_scanner.TK_WHILE or token.type == mini_scanner.TK_LET or token.type == mini_scanner.TK_BEGIN or token.type == mini_scanner.TK_RETURN:
            c2 = self.parse_singlecommand()
            c1 = mini_ast.SequentialCommand(c1, c2)
            token = self.token_current()
        return c1

    def parse_singlecommand(self):
        """ single-command   :== AssignCommand | CallCommand | IfCommand | WhileCommand| LetCommand | BeginCommand | ReturnCommand
            AssignCommand    :== Vname ':=' Expr ';'
            CallCommand      :== CallExpression ';'
            IfCommand        :== if  Expr then single-command else single-command
            WhileCommand     :== while Expr do single-command
            LetCommand       :== let Declaration in single-command
            BeginCommand     :== begin Command end
            ReturnCommand    :== return Expr ';'

            Vname            :== Identifier
            VnameExprssion   :== Variable
            PassedArgs :== Identifier (',' Identifier )* | Value (',' Value )*
        """

        token = self.token_current()  
        next_token = self.token_next()
        if token.type == mini_scanner.TK_IDENTIFIER and next_token.type == mini_scanner.TK_BECOMES:
            v1 =  mini_ast.Vname(token.val)
            self.token_accept_any()
            self.token_accept(mini_scanner.TK_BECOMES)
            e1 = self.parse_expr()
            self.token_accept(mini_scanner.TK_SEMICOLON)
            c1 = mini_ast.AssignCommand(v1, e1)
        elif token.type == mini_scanner.TK_IDENTIFIER and next_token.type == mini_scanner.TK_LPAREN:
            v1 = token.val
            self.token_accept_any()
            self.token_accept(mini_scanner.TK_LPAREN)
            e1 = self.parse_passedArgs()
            self.token_accept(mini_scanner.TK_RPAREN)
            e2 = mini_ast.CallExpression(v1,e1)
            self.token_accept(mini_scanner.TK_SEMICOLON)
            c1 = mini_ast.CallCommand(e2)
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
        elif token.type == mini_scanner.TK_RETURN:
            self.token_accept(mini_scanner.TK_RETURN)
            e1 = self.parse_expr()
            self.token_accept(mini_scanner.TK_SEMICOLON)
            c1 = mini_ast.ReturnCommand(e1)
        else:
            raise ParserError(self.curtoken.pos, self.curtoken.type)

        return c1


    def parse_declaration(self):
        """ Declaration :== single-declaration (single-declaration)* """

        d1 = self.parse_singledeclaration()
        token = self.token_current()
        while token.type == mini_scanner.TK_VAR or token.type == mini_scanner.TK_CONST or token.type == mini_scanner.TK_FUNCDEF:
            d2 = self.parse_singledeclaration()
            d1 = mini_ast.SequentialDeclaration(d1, d2)
            token = self.token_current()

        return d1

    def parse_singledeclaration(self):
        """ single-Declaration :==  const Identifier '~' Expr ';'
                                   | var Identifier ':' Type-denoter ';'
                                   | func Identifier '(' FormalArgs ')' ':'Type-denoter
                                        single-command

            FormalArgs :== Identifier ':' args-Typedenoter ( ',' Identifier ':'  args-Typedenoter )*
        """

        token = self.token_current()
        next_token = self.token_next()
        if token.type == mini_scanner.TK_CONST and next_token.type == mini_scanner.TK_IDENTIFIER:
            e1 = next_token.val
            self.token_accept_any()
            self.token_accept_any()
            self.token_accept(mini_scanner.TK_IS)
            e2 = self.parse_expr()
            self.token_accept(mini_scanner.TK_SEMICOLON)
            d1 = mini_ast.ConstDeclaration(e1, e2)
        elif token.type == mini_scanner.TK_VAR and next_token.type == mini_scanner.TK_IDENTIFIER:
            e1 = next_token.val
            self.token_accept_any()
            self.token_accept_any()
            self.token_accept(mini_scanner.TK_COLON)
            type_denoter = self.parse_type_denoter()
            self.token_accept(mini_scanner.TK_SEMICOLON)
            d1 = mini_ast.VarDeclaration(e1, type_denoter)
        elif token.type == mini_scanner.TK_FUNCDEF:
            self.token_accept_any()
            tk_ident = self.token_current()
            self.token_accept(mini_scanner.TK_IDENTIFIER)
            self.token_accept(mini_scanner.TK_LPAREN)
            formal_args = self.parse_formalArgs()
            self.token_accept(mini_scanner.TK_RPAREN)
            self.token_accept(mini_scanner.TK_COLON)
            func_type = self.parse_type_denoter()
            command = self.parse_singlecommand()
            d1 = mini_ast.FunctionDeclaration(tk_ident.val, formal_args, func_type, command)

        else:
            raise ParserError(self.curtoken.pos, self.curtoken.type)
        return d1

    def parse_formalArgs(self):
        """ Expr :== BinaryExpr | ArgsExpr

            ArgsExpr :== FormalArgs | InfromalArgs
            FormalArgs :== Identifier ':' args-Typedenoter ( ',' Identifier ':'  args-Typedenoter )*
        """
        token = self.token_current()
        next_token = self.token_next()
        if token.type == mini_scanner.TK_IDENTIFIER and next_token.type == mini_scanner.TK_COLON:
            tk_argident = token.val
            self.token_accept(mini_scanner.TK_IDENTIFIER)
            self.token_accept(mini_scanner.TK_COLON)
            arg_type = self.parse_type_denoter()
            formal_arg = mini_ast.FormalArgs(tk_argident, arg_type)
            while self.token_current().type == mini_scanner.TK_COMMA:
                self.token_accept_any()
                tk_argident = self.token_current().val
                self.token_accept(mini_scanner.TK_IDENTIFIER)
                self.token_accept(mini_scanner.TK_COLON)
                arg_type = self.parse_type_denoter()
                token = self.token_current()
                formal_arg2 = mini_ast.FormalArgs(tk_argident, arg_type)
                formal_arg = mini_ast.SequentialFormalArgs(formal_arg, formal_arg2)

        return formal_arg

    def parse_passedArgs(self):
        """ Expr :== BinaryExpr | ArgsExpr

            ArgsExpr :== FormalArgs | PassedArgs
            PassedArgs :== Identifier (',' Identifier )* | Value (',' Value )*
        """

        token = self.token_current()
        if token.type == mini_scanner.TK_IDENTIFIER:
            passedArg = mini_ast.PassedArg(token.val)
            token = self.token_accept_any()
            token = self.token_current()
            while token.type == mini_scanner.TK_COMMA:
                self.token_accept_any()
                token = self.token_current()
                passedArg2 = mini_ast.PassedArg(token.val)
                passedArg = mini_ast.SequentialPassedArgs(passedArg, passedArg2)
                self.token_accept_any()
                token = self.token_current()
        elif token.type == mini_scanner.TK_INTLITERAL:
            passedArg = mini_ast.PassedArg(token.val)
            token = self.token_accept_any()
            token = self.token_current()
            while token.type == mini_scanner.TK_COMMA:
                self.token_accept_any()
                token = self.token_current()
                passedArg2 = mini_ast.PassedArg(token.val)
                passedArg = mini_ast.SequentialPassedArgs(passedArg, passedArg2)
                self.token_accept_any()
                token = self.token_current()

        return passedArg



    def parse_expr(self):
        """  Expr :== BinaryExpr | ArgsExpr

             BinaryExpr :== BaseExpr (OPER_BASE BaseExpr)+ --> +,-,>,<,=
             BaseExpr :== SecExpr (OPER_SEC SecExpr)+ --> *,/,\ \
             SecExpr :== PriExpr(OPER_PRI PriExpr)+ -->(,)
             PriExpr :==  IntegerExprssion| VnameExprssion | '(' Expr ')' | CallExpression

             IntegerExprssion :== Value | '-'Value
             VnameExprssion   :== Variable
             CallExpression   :== Identifier '(' PassedArgs ')'
        """
        e1 = self.parse_baseExpr()
        token = self.token_current()
        while token.type == mini_scanner.TK_OPERATOR and token.val in ['+','-','>','<','=']:
            oper_base = token.val
            self.token_accept_any()
            e2 = self.parse_baseExpr()
            token = self.token_current()
            e1 = mini_ast.BinaryExpression(e1, oper_base, e2)

        return e1

    def parse_baseExpr(self):
        e1 = self.parse_secExpr()
        token = self.token_current()
        while token.type == mini_scanner.TK_OPERATOR and token.val in ['*','/', '\\']:
            oper_sec = token.val
            self.token_accept_any()
            e2 = self.parse_secExpr()
            token = self.token_current()
            e1 = mini_ast.BinaryExpression(e1, oper_sec, e2)

        return e1

    def parse_secExpr(self):
        e1 = self.parse_priExpr()
        token = self.token_current()
        while token.type == mini_scanner.TK_OPERATOR and token.val in ['(',')']:
            oper_pri = token.val
            self.token_accept_any()
            e2 = self.parse_priExpr()
            token = self.token_current()
            e1 = mini_ast.BinaryExpression(e1, oper_pri, e2)

        return e1

    def parse_priExpr(self):
        """ 
            PriExpr          :== IntegerExprssion| VnameExprssion | '(' Expr ')' | CallExpression
            IntegerExprssion :== Value | '-'Value
            VnameExprssion   :== Variable
            CallExpression   :== Identifier '(' PassedArgs ')'
            PassedArgs :== Identifier (',' Identifier )* | Value (',' Value )*


        """

        token = self.token_current()
        next_token = self.token_next()
        if token.type == mini_scanner.TK_OPERATOR and token.val in ['-'] and next_token.type == mini_scanner.TK_INTLITERAL:
            e1 = mini_ast.IntegerExpression(token.val + str(next_token.val))
            self.token_accept_any()
            self.token_accept_any()
        elif  token.type == mini_scanner.TK_INTLITERAL:
                e1 = mini_ast.IntegerExpression(token.val)
                self.token_accept_any()
        elif token.type == mini_scanner.TK_IDENTIFIER:
            if next_token.type == mini_scanner.TK_LPAREN:
                ident = token.val
                self.token_accept_any()
                self.token_accept_any()
                expr = self.parse_passedArgs()
                self.token_accept(mini_scanner.TK_RPAREN)
                e1 = mini_ast.CallExpression(ident, expr)
            else:
                e1 = mini_ast.VnameExpression(mini_ast.Vname(token.val))
                self.token_accept_any()
        elif token.type == mini_scanner.TK_LPAREN:
            self.token_accept_any()
            e1 = self.parse_expr()
            self.token_accept(mini_scanner.TK_RPAREN)
        else:
            raise ParserError(self.curtoken.pos, self.curtoken.type)

        return e1


    def parse_type_denoter(self):
        """ Type-denoter ::=  Identifier """

        token = self.token_current()
        type_denoter = mini_ast.TypeDenoter(token.val)
        self.token_accept_any()
        return type_denoter

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
    exprs = ["""
                 let
                     var x: Integer;
                     var y: Integer;
                     func add(x: Integer, y: Integer): Integer
                         return x + y;
                 in
                    let
                         var a: Integer;
                    in
                        a := add(1, 2);
             """,
             """! scopes
                   let var x: Integer;
                       var y: Integer;
                   in
                       begin
                           x := 1;
                           y := 2;
                           let
                               var x: Integer;
                           in
                               x := y;
                           putint(x);
                       end
             """,
             """ ! Factorial_old
                 let var x: Integer;
                     var fact: Integer;
                 in
                   begin
                     getint(x);
                     if x = 0 then
                       putint(1);
                     else
                       begin
                         fact := 1;
                         while x > 0 do
                           begin
                             fact := fact * x;
                             x := x - 1;
                           end
                         putint(fact);
                       end
                   end
             """ ,
             """! Factorial_new
                let
                    var x: Integer;
                    var fact: Integer;
                    func factorial(x: Integer): Integer
                        begin
                            if x = 0 then
                                putint(1);
                            else
                                begin
                                    fact:=1;
                                    while x > 0 do
                                        begin
                                            fact := fact * x;
                                            x := x - 1;
                                        end
                                    putint(fact);
                                end
                        end
                in
                    begin
                        x := getint(x);
                        factorial(x);
                    end
            """,
            """
            ! isprime
            let var x: Integer;

               	    var half: Integer;
                    var half1: Integer;
                    var half2: Integer;
                    var i: Integer;
                    var count: Integer;

                in
                  begin
                    getint(x);
                    half := (x / 2) + 1;
                    half1 := half + 1;
                    half2 := half1 + 1;
                    i := 2;
                    count := 2;

                    while i < half do
                      if (x \ i) then
                        i := i + 1;
                      else
                        i := half2;

                    if (i = half) then
                      putint(1);
                    else
                      putint(0);

                  end
             """
            ]

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

    
