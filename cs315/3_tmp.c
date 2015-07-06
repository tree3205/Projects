
#include <stdlib.h>
#include <stdio.h>

const int MAX = 50;

int main( ) {
    int x = -7;
    int y = 2;
    int rem = x / y;

    char array1[50] = "It's Some Computer Scientists'";
    char array2[50] = "It's Mathematicians'";

    if (rem < 0)
            printf("%s\n", array1);
    else
            printf("%s\n", array2);

    return 0;
}
