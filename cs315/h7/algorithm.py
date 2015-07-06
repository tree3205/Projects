import sys

def sub(num1, num2):

   minu = store_num(num1)
   subt = store_num(num2)

   digit_minu = len(minu)
   digit_subt = len(subt)

   max_digits = 0
   if (digit_minu >= digit_subt):
      max_digits = digit_minu
   else:
      max_digits = digit_subt

   update = minu;
   diff = []
   for digit in range(0, max_digits):
      if (update[digit] < subt[digit]):
         update[digit] += 10
         i = digit + 1
         while (update[i] == 0):
            update[i] = 9
            i += 1
         
         update[i] -=1
      
      diff[digit] = update[digit] - subt[digit]

def store_num(num):
   num_array = []
   tmp_num = str(num)
   for s in tmp_num:
      num_array.append(int(s))
   num_array.reverse()

   return num_array


if __name__ == '__main__' :
   num1 = sys.argv[1]
   num2 = sys.argv[2]
   sub(num1, num2)

