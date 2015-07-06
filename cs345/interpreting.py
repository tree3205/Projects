import scanner
import parser
import ast 

class EvalError(Exception):
	"""EvalError"""

	def __init__(self,tree,expected):
		self.tree = tree
		self.expected = expected
		
	def __str__(self):
		return 'Error at ast node: %s expected: %s'%(str(self.tree),str(self.expected))

class Evaluator(object):
	
	def __init__(self,tree):
		self.tree = tree
        self.env = []
    
    def lookup_env(self, name):
        for e in self.env[::-1]:
            if name in e:
                return e[name]
        raise NameError(name)
            
            
    def upadate_env(self, nemw, value):
        for e in self.env[::-1]:
            if name in e:
                e[name][1] = value
                return
        raise NameError(name)
        
    def add_env(self, name, type, value):
        e = self.env[-1]
        e[name] = [type, value]
    

	def run(self):
		if type(self.tree) is not ast.Program:
			raise EvalError(self.ast, ast.Program)
		if type(self.tree.command) is not ast.LetCommand:
			raise EvalError(self.tree.command, ast.LetProgram))

		return self.eval_command(self.ast.command)

	def eval_command(self, tree):
		if type(tree) is ast.LetCommand:
			return self.eval_Let_command(tree)
		elif type(tree) is ast.SequentialCommand:
			return self.eval_Seqentiao_command(tree)
		elif type(tree) is ast.AssignCommand:
			return self.eval_Assign_command(tree)
		elif type(tree) is ast.CallCommand:
			return self.eval_Call_command(tree)
		elif type(tree) is ast.WhileCommand:
			return self.eval_While_command(tree)
	
		else
			raise EvalError(tree, ast.Command)
			# add class Command:
			#	pass
			# to ast.py

	def eval_Let_command(self,tree):
        # self.env.append({})
		self.eval_declaration(tree.declaration)
		self.eval_command(tree.command)
        #self.env.pop()
    
	def eval_Seqentiao_command(self,tree):
		self.eval_command(tree.command1)
		self.eval_command(tree.command2)

	def eval_Assign_command(self,tree):
		e1 = self.eval_expression(tree.expression)
        self.update_env[tree.variable, identifier, e1]
        
	def eval_Call_command(self,tree):
		e1 = self.eval_expression(tree.expression)
		func = tree.identifier
		if func == 'putint':
			print e1

		elif func == 'getint' and type(tree.expression) is ast.VnameExpression:
			v = input()
            
			name = tree.expression.variable.identifier
			self.update_env(name, value)
		else
			raise EvalError(tree, 'putint')

	def eval_While_command():
		expr  = tree.expression
		cmd = tree.command

		while True:
			expr_val = self.eval_expression(expr)
			if  not expr_val:
				break
			self.eval_command(cmd)

    def eval_if_command(self, tree):
        exp1 = tree.expression
        ...
        
        
        

	def  eval_declaration(self,tree):
		
		if type(tree) is ast.VarDeclaration:
			#self.env[tree.identifier] = [tree.type_denoter.identifier, None]
            self.add_env(tree.identifier, tree.tpe_denoter.identifier,None)
            return
		elif type(tree) is ast.ConstDeclaration:
			self.env[tree.identifier] = ['Integer', self.eval_expression(tree.expression)]

		elif type(tree) is ast.SequentialDeclaration:
			self.eval_declaration(tree.decl1)
			self.eval_declaration(tree.decl2)
		else
			raise EvalError(tree, ast.Declaration)
			# add class Declaration:
			#	pass
			# to ast.py	

	def eval_expression(self,tree):
		
		if type(tree) is ast.IntegerExpression:
			return tree.value
		elif type(tree) is ast.VnameExpression:
			return self.env[tree.variable.identifier][1]
		elif type(tree) is ast.UnaryExpression:
			if tree.operator =='-':
				return -(self.eval_expression(tree.expression))
			elif tree.operator =='+':
				return (self.eval_expression(tree.expression))
			else:
				raise EvalError(tree, ['-','+'])
		elif type(tree) is ast.BinaryExpression:
			e1 = self.eval_expression(tree.expr1)
			e2 = self.eval_expression(tree.expr2)
            if tree.oper =='=':
                return e1 = e2
            else:
                val = eval('%d %s'%(e1, tree.operator, e2))
			""" need to implement the eval class which is used to caculate??"""
					
		else
			raise EvalError(tree, ast.Expression)
			# add class Expression:
			#	pass
			# to ast.py



if __name__ == '__main__':
	progs = ["""let
					var x: Integer;
					var y: Integer;
					var z: Integer
				in 
					begin
						x := 1;
						y := 2;
						z := x+y
						putint(z)
					end
			 """]

	for prog in progs:
		print '================'
		print prog

		scanner_obj = scanner.Scanner(prog)

		 try:
            tokens = scanner.scan()
            print tokens
        except scanner.ScannerError as e:
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


	    evaluator_obj = Evaluator(tree)
	    evaluator_obj.run()
	    print evaluator_obj.env
	
#used for CodeGen:
#cg = CodeGen(tree)



