#!/usr/bin/env python
#
# Scanner for Mini Triangle

import cStringIO as StringIO
import string

# Token Constants

TK_IDENTIFIER = 0
TK_INTLITERAL = 1
TK_OPERATOR   = 2
TK_BEGIN      = 3  # begin
TK_CONST      = 4  # const
TK_DO         = 5  # do
TK_ELSE       = 6  # else
TK_END        = 7  # end
TK_IF         = 8  # if
TK_IN         = 9  # in
TK_LET        = 10 # let
TK_THEN       = 11 # then
TK_VAR        = 12 # var
TK_WHILE      = 13 # while
TK_SEMICOLON  = 14 # ;
TK_COLON      = 15 # :
TK_BECOMES    = 16 # :=
TK_IS         = 17 # ~
TK_LPAREN     = 18 # (
TK_RPAREN     = 19 # )
TK_EOT        = 20 # end of text
TK_FUNCDEF    = 21 # func
TK_RETURN     = 22 # return
TK_COMMA      = 23 # ,

TOKENS = {TK_IDENTIFIER: 'IDENTIFIER',
          TK_INTLITERAL: 'INTLITERAL',
          TK_OPERATOR:   'OPERATOR',
          TK_BEGIN:      'BEGIN',
          TK_CONST:      'CONST',
          TK_DO:         'DO',
          TK_ELSE:       'ELSE',
          TK_END:        'END',
          TK_IF:         'IF',
          TK_IN:         'IN',
          TK_LET:        'LET',
          TK_THEN:       'THEN',
          TK_VAR:        'VAR',
          TK_WHILE:      'WHILE',
          TK_SEMICOLON:  'SEMICOLON',
          TK_COLON:      'COLON',
          TK_BECOMES:    'BECOMES',
          TK_IS:         'IS',
          TK_LPAREN:     'LPAREN',
          TK_RPAREN:     'RPAREN',
          TK_EOT:        'EOT',
          TK_FUNCDEF:    'FUNCDEF',
          TK_RETURN:     'RETURN',
          TK_COMMA:      'COMMA'}

class Token(object):
    """ A simple Token structure.
        
        Contains the token type, value and position. 
    """
    def __init__(self, type, val, pos):
        self.type = type
        self.val = val
        self.pos = pos

    def __str__(self):
        return '(%s(%s) at %s)' % (TOKENS[self.type], self.val, self.pos)

    def __repr__(self):
        return self.__str__()


class ScannerError(Exception):
    """ Scanner error exception.

        pos: position in the input text where the error occurred.
    """
    def __init__(self, pos, char):
        self.pos = pos
        self.char = char

    def __str__(self):
        return 'ScannerError at pos = %d, char = %s' % (self.pos, self.char)

class Scanner(object):
    """Implement a scanner for the following token grammar
    
       Token           ::= EOT | IDENTIFIER | Integer-Literal| Comment | OPERATOR | Keywords | Symbol

       IDENTIFIER      ::= Letter (Letter|Digit)*
       Integer-Literal ::= Digit (Digit*)
       Comment         ::= '!' Graphic* <eol>
       OPERATOR        ::= '+' | '-' | '*' | '/' | '<' | '>' | '=' | ' \ '
       Digit           ::= [0..9]
       Letter          ::= [a..zA..Z]
       Keywords         ::= 'if' | 'then' | 'else' | 'while' | 'do' | 'let' | 'in' | 'begin' | 'end' | 'const' | 'var' | 'func' | 'return'
       Symbol          ::= ';' | ':' | ':=' | '~' | '(' | ')' | ','
       Separator       ::=  '!' Graphic* <eol> | <space> | <eol>    

       

       Token     ::=  Letter (Letter | Digit)* | Digit Digit* |
                   '+' | '-' | '*' | '/' | '<' | '>' | '=' | '\'
                   ':' ('=' | <empty>) | ';' | '~' | '(' | ')' | <eot>
    """

    def __init__(self, input):
        # Use StringIO to treat input string like a file.
        self.inputstr = StringIO.StringIO(input)
        self.eot = False   # Are we at the end of the input text?
        self.pos = 0       # Position in the input text
        self.char = ''     # The current character from the input text
        self.char_take()   # Fill self.char with the first character

    def scan(self):
        """Main entry point to scanner object.

        Return a list of Tokens.
        """

        self.tokens = []
        while 1:
            token = self.scan_token()
            self.tokens.append(token)
            if token.type == TK_EOT:
                break
        return self.tokens
    
    def scan_token(self):
        """Scan a single token from input text."""

        c = self.char_current()
        token = None
        
        while not self.char_eot():
            # Skip the space
            if c.isspace():
                self.char_take()
                c = self.char_current() 
                continue
            # Distinguish comment
            if c == '!':
                self.char_take()
                while self.char_current() != '\n' and not self.char_eot():
                    self.char_take()
                c = self.char_current()
                continue
            # Distinguish ':' and ':= '
            elif c == ':':
                pos = self.char_pos()
                self.char_take()
                c = self.char_current()
                if c == '=':
                    token = Token(TK_BECOMES, 0, pos)
                else:
                    token = Token(TK_COLON, 0, pos)
                self.char_take()
                break
            # Distinguish Integer-Literal
            elif c.isdigit():
                token = self.scan_int()
                break
            # Distinguish Identifier, keyword 
            elif c.isalnum():
                token = self.scan_letter()
                break
            # Distinguish Operator
            elif c == '+' or c == '-' or c == '*' or c == '/' or c == '<' or c == '>' or c == '=' or c == '\\':
                token = Token(TK_OPERATOR, str(self.char_current()), self.char_pos())
                self.char_take()
                break
            # Distinguish ';'
            elif c == ';':
                token = Token(TK_SEMICOLON, 0, self.char_pos())
                self.char_take()
                break
            # Distinguish '~'
            elif c == '~':
                token = Token(TK_IS, 0, self.char_pos())
                self.char_take()
                break
            # Distinguish '('
            elif c == '(':
                token = Token(TK_LPAREN, 0, self.char_pos())
                self.char_take()
                break
            # Distinguish ')''
            elif c == ')':
                token = Token(TK_RPAREN, 0, self.char_pos())
                self.char_take()
                break
            elif c == ',':
                token = Token(TK_COMMA, 0, self.char_pos())
                self.char_take()
                break
            else:
                raise ScannerError(self.char_pos(), self.char_current())
      
        if token is not None:
            return token
           
        if self.char_eot():
            return(Token(TK_EOT, 0, self.char_pos()))

    def scan_int(self):
        """Integer-Literal :== Digit (Digit*)"""
        
        pos = self.char_pos()
        numlist = [self.char_take()]

        while self.char_current().isdigit():
            numlist.append(self.char_take())
        
        return Token(TK_INTLITERAL, int(string.join(numlist ,'')), pos)

    def scan_letter(self):
        """IDENTIFIER :== Letter (Letter*) | Letter (Digit*)
           Keyword    :== 'if' | 'then' | 'else' | 'while' | 'do' | 'let' | 'in' | 'begin' | 'end' | 'const' | 'var' | 'func' | 'return' """
        pos = self.char_pos()
        letterlist = [self.char_take()]
        wordlist = []

        while self.char_current().isalnum(): 
            letterlist.append(self.char_take()) 
            wordlist = [str(string.join(letterlist , ''))]  
            for i in wordlist:
                if i == 'begin':
                    return Token(TK_BEGIN, 0, pos)
                    break
                elif i == 'const':
                    return Token(TK_CONST, 0, pos)
                    break
                elif i == 'do':
                    return Token(TK_DO, 0, pos)
                    break
                elif i == 'end':
                    return Token(TK_END, 0, pos)
                    break
                elif i == 'else':
                    return Token(TK_ELSE, 0, pos)
                    break
                elif i == 'if':
                    return Token(TK_IF, 0, pos)
                    break
                elif i == 'in':
                    return Token(TK_IN, 0, pos)
                    break
                elif i == 'let':
                    return Token(TK_LET, 0, pos)
                    break
                elif i == 'then':
                    return Token(TK_THEN, 0, pos)
                    break
                elif i == 'var':
                    return Token(TK_VAR, 0, pos)
                    break
                elif i == 'while':
                    return Token(TK_WHILE, 0, pos)
                    break
                elif i == 'func':
                    return Token(TK_FUNCDEF, 0, pos)
                    break
                elif i == 'return':
                    return Token(TK_RETURN, 0, pos)
                    break


        return  Token(TK_IDENTIFIER, str(string.join(letterlist , '')), pos)    
        
    def char_current(self):
        """Return in the current input character."""

        return self.char

    def char_take(self):
        """Consume the current character and read the next character 
        from the input text.

        Update self.char, self.eot, and self.pos
        """

        char_prev = self.char
        
        self.char = self.inputstr.read(1)
        if self.char == '':
            self.eot = True

        self.pos += 1
        
        return char_prev
        
    def char_pos(self):
        """Return the position of the *current* character in the input text."""

        return self.pos - 1
        
    def char_eot(self):
        """Determine if we are at the end of the input text."""

        return self.eot

    
        

if __name__ == '__main__':
    exprs = ['1',
             '723',
             '1 + 1',
             '3 - 2',
             '9 * 10',
             '(1)',
             '1 + 2 * (3 + 4)',
             '(1 + 2) * ((3 * 10) / 5)',
             '1 + &',
             '1 -  5 * 3',
             'if a := 5',
             'if b = 6',
             'aion33',
             'b4',
             'cc',
             'begin Command end',
             '797* %$#@',
             ]

    for exp in exprs:
        print '=============='
        print exp
        scanner = Scanner(exp)
        try:
            tokens = scanner.scan()
            print tokens
        except ScannerError as e:
            print e