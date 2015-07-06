# scanner_test.py - test the scanner code

import unittest

import scanner

test_1 = """
let var x: Integer 
in 
    x := 0
"""

test_1_tokens = '[(LET(0) at 1), (VAR(0) at 5), (IDENTIFIER(x) at 9), (COLON(0) at 10), \
(IDENTIFIER(Integer) at 12), (IN(0) at 21), (IDENTIFIER(x) at 29), (BECOMES(0) at 31), \
(INTLITERAL(0) at 34), (EOT(0) at 36)]'

test_2 = """
! This is a comment.
let
    const m ~ 7;
    var n: Integer
in
    begin
        n := 2 * m * m;
        putint(n)
    end
"""

test_2_tokens = '[(LET(0) at 22), (CONST(0) at 30), (IDENTIFIER(m) at 36), (IS(0) at 38), \
(INTLITERAL(7) at 40), (SEMICOLON(0) at 41), (VAR(0) at 47), (IDENTIFIER(n) at 51), \
(COLON(0) at 52), (IDENTIFIER(Integer) at 54), (IN(0) at 62), (BEGIN(0) at 69), \
(IDENTIFIER(n) at 83), (BECOMES(0) at 85), (INTLITERAL(2) at 88), (OPERATOR(*) at 90), \
(IDENTIFIER(m) at 92), (OPERATOR(*) at 94), (IDENTIFIER(m) at 96), (SEMICOLON(0) at 97), \
(IDENTIFIER(putint) at 107), (LPAREN(0) at 113), (IDENTIFIER(n) at 114), (RPAREN(0) at 115), \
(END(0) at 121), (EOT(0) at 125)]'

class ScannerTest(unittest.TestCase):

    def test_1(self):
        s = scanner.Scanner(test_1)
        tokens = s.scan()
        self.assertEqual(str(tokens), test_1_tokens)

    def test_2(self):
        s = scanner.Scanner(test_2)
        tokens = s.scan()
        self.assertEqual(str(tokens), test_2_tokens)


if __name__ == '__main__':
    unittest.main()