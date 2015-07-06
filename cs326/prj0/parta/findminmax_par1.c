/* findminmax_seq.c - find the min and max values in a random array
 *
 * usage: ./findminmax <seed> <arraysize> <nprocs>
 *
 */
#include<string.h>
#include<stdio.h>
#include<stdlib.h>
#include<unistd.h>
#include<sys/types.h>
#include"measure.h"



/* a struct used to pass results to caller */
struct results {
    int min;
    int max;
};

/* given an array of ints and the size of array, find min and max values */
struct results find_min_and_max(int *subarray, int n) {
    int i, min, max;
    min = max = subarray[0];
    struct results r;

    for (i = 1; i < n; i++) {
        if (subarray[i] < min) {
            min = subarray[i];
        }
        if (subarray[i] > max) {
            max = subarray[i];
        }
    }

    r.min = min;
    r.max = max;
    return r;
}

int main(int argc, char **argv) {
    int *array;
    int *subarray;
    int arraysize = 0;
    int subarrsize = 0;
    int seed;
    int processorsize=0;
    char randomstate[8];
    struct results subr;
    char file[processorsize];
    int i;
    int status;
    pid_t pid;
    FILE *fw;
    FILE *fr;


    /* process command line arguments */
    if (argc != 4) {
        printf("usage: ./findminmax <seed> <arraysize> <nprocs>\n");
        return 1;
    }

    seed = atoi(argv[1]);
    arraysize = atoi(argv[2]);
    processorsize = atoi(argv[3]);
    subarrsize = arraysize / processorsize;

    /* allocate array and populate with random values */
    array = (int *) malloc(sizeof(int) * arraysize);
    subarray = (int *) malloc(sizeof(int) * subarrsize);

    initstate(seed, randomstate, 8);

    for (i = 0; i < arraysize; i++) {
        array[i] = random();
    }


    /* parallel Min and Max Verison 1 (results communicated via files) */
    mtf_measure_begin();
    for ( i = 0; i < processorsize; i++) {
        pid = fork();
        if (pid == -1) {
            printf("Error in generating processors!");
            exit(1);
        }
        else if (pid == 0) {
            subarray = array + i*subarrsize;
            subr = find_min_and_max(subarray, subarrsize);

            sprintf(file,"text%d.txt",i);
            fw = fopen(file, "w");
            if (fw == NULL) {
                printf("Error opening file!\n");
                return 1;
            }
            fprintf(fw, "%d\n%d", subr.min, subr.max);
            fclose(fw);
            exit(0);
        }
    }

    for (i = 0; i < processorsize; i++) {
        wait(&status);
    }

    int tmpmin,tmpmax, min, max;
    for (i = 0; i < processorsize; i++) {
        sprintf(file,"text%d.txt",i);
        fr = fopen(file, "r");
        if (fr == NULL) {
            printf("Error in opening file!\n");
            return 1;
        }

        while (!feof(fr)) {
            fscanf(fr, "%d\n%d", &tmpmin, &tmpmax);
            if (i == 0 ){
                min = tmpmin;
                max = tmpmax;
            }
            else {
                if (tmpmin < min) {
                    min = tmpmin;
                }
                else if (tmpmax > max) {
                    max = tmpmax;
                }
            }
        }
        fclose(fr);
        remove(file);
    }


    free(array);
    mtf_measure_end();

    printf("Execution time: ");
    mtf_measure_print_seconds(1);

    printf("min = %d, max = %d\n", min, max);

    return 0;
}
