import dis

class testcode(object):
	

	def if_command():
		if 3 > 2:
			b = 4
		else:
			a = 3


	def while_command():
		while 3 > 2:
			a = 3


if __name__ == '__main__':
	dis.dis(if_command)	
	dis.dis(while_command)
		

		