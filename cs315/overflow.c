/* Largest unsigned int is 
 *
 *    2^32 - 1 = 0xffffffff
 *
 * Largest positive signed int is
 *
 *    2^31 - 1 = 0x7fffffff
 *
 * Largest negative signed int (in absolute value) is
 *
 *    -2^31 = 0x8000000
 */
#include <stdio.h>


int main(void) {
   unsigned x = 0xffffffff;
   unsigned y = 0x1;
   unsigned z = 0x0;
   unsigned w;

   int a = 0x7fffffff;
   int b = 0x1;
   int c = 0x80000000;
   int d;

   int e = 10001;
   int f = 10011;

   printf("sizeof(unsigned) = %d\n", (int) sizeof(unsigned));
   w = x+y;
   printf("x + y = 0x%x = %u\n", w, w);
   w = z-y;
   printf("z - y = 0x%x = %u\n\n", w, w);

   printf("sizeof(int) = %d\n", (int) sizeof(int));
   d = a+b;
   printf("a + b = 0x%x = %d\n", d, d);
   d = c-b;
   printf("c - b = 0x%x = %d\n", d, d);

   g = e+f;
   printf("e + f = 0x%x = %d\n", d, d);
   return 0;
}