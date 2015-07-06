# codegen_test.py - test the scanner code

import unittest

import scanner
import parser
import codegen

test_1 = """
let var x: Integer 
in 
    x := 0
"""

test_1_bytecode = "[(LOAD_CONST, 0), (STORE_FAST, 'x'), (LOAD_CONST, None), (RETURN_VALUE, None)]"

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

test_2_bytecode = "[(LOAD_CONST, 7), (STORE_FAST, 'm'), (LOAD_CONST, 2), (LOAD_FAST, 'm'), (BINARY_MULTIPLY, None), (LOAD_FAST, 'm'), (BINARY_MULTIPLY, None), (STORE_FAST, 'n'),  (LOAD_GLOBAL, 'putint'), (LOAD_FAST, 'n'), (CALL_FUNCTION, 1), (POP_TOP, None), (LOAD_CONST, None), (RETURN_VALUE, None)]"

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

test_3_bytecode = "[(LOAD_GLOBAL, 'getint'), (LOAD_FAST, 'x'), (CALL_FUNCTION, 1), (POP_TOP, None), (LOAD_CONST, 1), (STORE_FAST, 'fact'), (SETUP_LOOP, <byteplay.Label object at 0x7f61dd8cd2d0>), (<byteplay.Label object at 0x7f61dd8d7ad0>, None), (LOAD_FAST, 'x'), (LOAD_CONST, 0), (COMPARE_OP, '>'), (POP_JUMP_IF_FALSE, <byteplay.Label object at 0x7f61dd8d7a90>), (LOAD_FAST, 'fact'), (LOAD_FAST, 'x'), (BINARY_MULTIPLY, None), (STORE_FAST, 'fact'), (LOAD_FAST, 'x'), (LOAD_CONST, 1), (BINARY_SUBTRACT, None), (STORE_FAST, 'x'), (JUMP_ABSOLUTE, <byteplay.Label object at 0x7f61dd8d7ad0>), (<byteplay.Label object at 0x7f61dd8d7a90>, None), (POP_BLOCK, None), (<byteplay.Label object at 0x7f61dd8cd2d0>, None), (LOAD_GLOBAL, 'putint'), (LOAD_FAST, 'fact'), (CALL_FUNCTION, 1), (POP_TOP, None), (LOAD_CONST, None), (RETURN_VALUE, None)]"


class CodegenTest(unittest.TestCase):

    def test_1(self):
        s = scanner.Scanner(test_1)
        p = parser.Parser(s.scan())
        tree = p.parse()
        c = codegen.CodeGen(tree)
        bytecode = str(c.generate())
        self.assertEqual(bytecode, test_1_bytecode)

    def test_2(self):
        s = scanner.Scanner(test_2)
        p = parser.Parser(s.scan())
        tree = p.parse()
        c = codegen.CodeGen(tree)
        bytecode = str(c.generate())
        self.assertEqual(bytecode, test_2_bytecode)

#    def test_3(self):
#        s = scanner.Scanner(test_3)
#        p = parser.Parser(s.scan())
#        tree = str(p.parse())
#        c = codegen.CodeGen(tree)
#        bytecode = c.generate()
#        self.assertEqual(bytecode, test_3_bytecode)

if __name__ == '__main__':
    unittest.main()
    
