

class CodeGen(object):
	"""docstring for CodeGen"""

	def __init__(self, tree):
		self.tree = tree
		self.code = []
		self.evl = {}

	def generated():
		pass

	def gen_command(self, tree):
		if type(tree) is ast.ExpeCommand:
			self.gen_expr(tree.expr)
		elif


	def gen_expr(self,tree):
		
		if type(tree) is ast.ExprVariable:
			self.code.append((LOAD_FAST, tree.name))
		elif type(tree) is ast.ExprValue:
			self.codeappend(LOAD_CONST, tree.value)
		elif type(tree) is ast.ExpeBinary:
			self.gen_expr(tree.exp1)
			self.gen_expr(tree.exp2)
			op = tree.oper
			if op == '+':
				self.code.append((BINARY_ADD,None))
			if op == '-':
				self.code.append((BINARY_MINUS,None))
			if op == '*':
				self.code.append((BINARY_MULTIPLY,None))
			if op == '/':
				self.code.append((BINARY_DIVIDE,None))
