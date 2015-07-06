# scanner_test.py - test the scanner code

import unittest

import scanner
import parser

test_1 = """
let var x: Integer 
in 
    x := 0
"""

test_1_tree = 'Program(LetCommand(VarDeclaration(x,TypeDonoter(Integer)),AssignCommand(Vname(x),IntegerExpression(0))))'

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

test_2_tree = 'Program(LetCommand(SequentialDeclaration(ConstDeclaration(m,IntegerExpression(7)),VarDeclaration(n,TypeDonoter(Integer))),SequentialCommand(AssignCommand(Vname(n),BinaryExpression(BinaryExpression(IntegerExpression(2),*,VnameExpression(Vname(m))),*,VnameExpression(Vname(m)))),CallCommand(putint,VnameExpression(Vname(n))))))'

test_3 = """
! Factorial
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
  end
"""

test_3_tree = 'Program(LetCommand(SequentialDeclaration(VarDeclaration(x,TypeDonoter(Integer)),VarDeclaration(fact,TypeDonoter(Integer))),SequentialCommand(SequentialCommand(SequentialCommand(CallCommand(getint,VnameExpression(Vname(x))),AssignCommand(Vname(fact),IntegerExpression(1))),WhileCommand(BinaryExpression(VnameExpression(Vname(x)),>,IntegerExpression(0)),SequentialCommand(AssignCommand(Vname(fact),BinaryExpression(VnameExpression(Vname(fact)),*,VnameExpression(Vname(x)))),AssignCommand(Vname(x),BinaryExpression(VnameExpression(Vname(x)),-,IntegerExpression(1)))))),CallCommand(putint,VnameExpression(Vname(fact))))))'

class ParserTest(unittest.TestCase):

    def test_1(self):
        s = scanner.Scanner(test_1)
        p = parser.Parser(s.scan())
        tree = str(p.parse())
        self.assertEqual(tree, test_1_tree)

    def test_2(self):
        s = scanner.Scanner(test_2)
        p = parser.Parser(s.scan())
        tree = str(p.parse())
        self.assertEqual(tree, test_2_tree)

    def test_3(self):
        s = scanner.Scanner(test_3)
        p = parser.Parser(s.scan())
        tree = str(p.parse())
        self.assertEqual(tree, test_3_tree)

if __name__ == '__main__':
    unittest.main()
    