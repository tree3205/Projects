# parser: single-Declaration add:func Identifier '(' Identifier ':' Typedenoter')' ':' Typedenoter sigle-Command
# primary-Expression change: Identifier ( *empty*) |'(' Expression ')' )
# single-Command add: return Expression
parse_single_Command:
if token.type == scanncer.TK_RETURN

parse_primary_expression:
if token.type == scanncer.Identifier:
	...
	if next.type == scanncer.TK_LEFTPRAN:
		ident = token.val
		self.token.accecpy_any()
		expr = self.parse_expression()
		self.token_accept(scanncer.TK_RIGHTPRAN)
		cmd = ast.Call-Expression(ident, expr)

parse_single_Command:
if token.type == scanncer.TK_FUNCDEF:
 	accecpy_any
 	token_accept(TK_Identifier)
 	...
 	
# ast add Return-Command:
# def Return-Command(Command):
# ast add Function-Declaration:
# def Function-Declaration(Declaration):
# 	def _init_(self.ident_name, dient_argname, ident_argtype, type_denoter, command):
# ast add Call-Expression




#scanncer: add TK_FUNCDEF: 'FUNCDEF' :func ; TK_FUNCDEF  = 21
#		   add TK_RETURN
