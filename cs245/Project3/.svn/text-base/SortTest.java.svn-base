//https://cs.usfca.edu/svn/yxu66/cs245

import java.util.Random;

public class SortTest {

	public static void main(String[] args) {
		int i, j;
		int[] ints;
		Integer[] Integers;
		long startTime, endTime;
		double duration;
		
		int[] sizes = { 5, 20, 50, 100, 200, 300, 400, 500, 1000, 2000, 3000, 4000, 5000, 10000, 50000, 100000 };

		Sort sorter = new Sort();
		Random randomGenerator = new Random();

		for (i = 0; i < sizes.length; i++) {
			int size = sizes[i];
			System.out.println("Result on array size: " + size);
			int[] intsI = new int[size];
			Integer[] IntegersI = new Integer[size];
			int NUMITER = 100000 / size;
			for (j = 0; j < size; j++) {
				intsI[j] = Math.abs(randomGenerator.nextInt());
				IntegersI[j] = intsI[j];
			}
			LLNode head0 = new LLNode(intsI[0]);
			LLNode head1 = new LLNode(intsI[0]);
			LLNode tmp0 = head0;
			LLNode tmp1 = head1;
			for (j = 1; j < size; j++) {
				LLNode n0 = new LLNode(intsI[j]);
				tmp0.setNext(n0);
				tmp0 = tmp0.next();
				LLNode n1 = new LLNode(intsI[j]);
				tmp1.setNext(n1);
				tmp1 = tmp1.next();
			}
			//Insertion Sort
			Integers = IntegersI.clone();
			startTime = System.currentTimeMillis();
			for (j = 0; j < NUMITER; j++) {
				sorter.insertionSort(Integers, 0, size - 1, false);
			}
			endTime = System.currentTimeMillis();
			duration = ((double) (endTime - startTime)) / NUMITER;
			System.out.println("Insertion Sort | Reverse = False | Duration = " + duration);
			
			//Selection Sort
			Integers = IntegersI.clone();
			startTime = System.currentTimeMillis();
			for (j = 0; j < NUMITER; j++) {
				sorter.selectionSort(Integers, 0, size - 1, false);
			}
			endTime = System.currentTimeMillis();
			duration = ((double) (endTime - startTime)) / NUMITER;
			System.out.println("Selection Sort | Reverse = False | Duration = " + duration);
			
			//Shell Sort
			Integers = IntegersI.clone();
			startTime = System.currentTimeMillis();
			for (j = 0; j < NUMITER; j++) {
				sorter.shellSort(Integers, 0, size - 1, false);
			}
			endTime = System.currentTimeMillis();
			duration = ((double) (endTime - startTime)) / NUMITER;
			System.out.println("Shell Sort | Reverse = False | Duration = " + duration);
			
			//Bucket Sort
			ints = intsI.clone();
			startTime = System.currentTimeMillis();
			for (j = 0; j < NUMITER; j++) {
				sorter.bucketSort(ints, 0, size - 1, false);
			}
			endTime = System.currentTimeMillis();
			duration = ((double) (endTime - startTime)) / NUMITER;
			System.out.println("Bucket Sort | Reverse = False | Duration = " + duration);
			
			//Heap Sort
			Integers = IntegersI.clone();
			startTime = System.currentTimeMillis();
			for (j = 0; j < NUMITER; j++) {
				sorter.heapSort(Integers, 0, size - 1, false);
			}
			endTime = System.currentTimeMillis();
			duration = ((double) (endTime - startTime)) / NUMITER;
			System.out.println("Heap Sort | Reverse = False | Duration = " + duration);
			
			//Radix Sort
			ints = intsI.clone();
			startTime = System.currentTimeMillis();
			for (j = 0; j < NUMITER; j++) {
				sorter.radixSort(ints, 0, size - 1, false);
			}
			endTime = System.currentTimeMillis();
			duration = ((double) (endTime - startTime)) / NUMITER;
			System.out.println("Radix Sort | Reverse = False | Duration = " + duration);
			
			//Merge Sort
			Integers = IntegersI.clone();
			startTime = System.currentTimeMillis();
			for (j = 0; j < NUMITER; j++) {
				sorter.mergeSort(Integers, 0, size - 1, false);
			}
			endTime = System.currentTimeMillis();
			duration = ((double) (endTime - startTime)) / NUMITER;
			System.out.println("Merge Sort | Reverse = False | Duration = " + duration);
			
			//Quick Sort
			Integers = IntegersI.clone();
			startTime = System.currentTimeMillis();
			for (j = 0; j < NUMITER; j++) {
				sorter.quickSort(Integers, 0, size - 1, false);
			}
			endTime = System.currentTimeMillis();
			duration = ((double) (endTime - startTime)) / NUMITER;
			System.out.println("Quick Sort | Reverse = False | Duration = " + duration);
			
			//Merge Sort LL
			startTime = System.currentTimeMillis();
			for (j = 0; j < NUMITER; j++) {
				sorter.mergeSortLL(head0, false);
			}
			endTime = System.currentTimeMillis();
			duration = ((double) (endTime - startTime)) / NUMITER;
			System.out.println("Merge Sort LL | Reverse = False | Duration = " + duration);
			
			//Insertion Sort LL
			startTime = System.currentTimeMillis();
			for (j = 0; j < NUMITER; j++) {
				sorter.insertionSortLL(head1, false);
			}
			endTime = System.currentTimeMillis();
			duration = ((double) (endTime - startTime)) / NUMITER;
			System.out.println("Insertion Sort LL | Reverse = False | Duration = " + duration);
			
			//Optimized Quick Sort
			Integers = IntegersI.clone();
			startTime = System.currentTimeMillis();
			for (j = 0; j < NUMITER; j++) {
				sorter.optimziedQuickSort(Integers, 0, size - 1, false);
			}
			endTime = System.currentTimeMillis();
			duration = ((double) (endTime - startTime)) / NUMITER;
			System.out.println("Optimized Quick Sort | Reverse = False | Duration = " + duration);
			System.out.println();
			
			//Insertion Sort(reversed)
			Integers = IntegersI.clone();
			startTime = System.currentTimeMillis();
			for (j = 0; j < NUMITER; j++) {
				sorter.insertionSort(Integers, 0, size - 1, true);
			}
			endTime = System.currentTimeMillis();
			duration = ((double) (endTime - startTime)) / NUMITER;
			System.out.println("Insertion Sort | Reverse = True | Duration = " + duration);
			
			//Selection Sort(reversed)
			Integers = IntegersI.clone();
			startTime = System.currentTimeMillis();
			for (j = 0; j < NUMITER; j++) {
				sorter.selectionSort(Integers, 0, size - 1, true);
			}
			endTime = System.currentTimeMillis();
			duration = ((double) (endTime - startTime)) / NUMITER;
			System.out.println("Selection Sort | Reverse = True | Duration = " + duration);
	
			//ShellSort(reversed)
			Integers = IntegersI.clone();
			startTime = System.currentTimeMillis();
			for (j = 0; j < NUMITER; j++) {
				sorter.shellSort(Integers, 0, size - 1, true);
			}
			endTime = System.currentTimeMillis();
			duration = ((double) (endTime - startTime)) / NUMITER;
			System.out.println("Shell Sort | Reverse = True | Duration = " + duration);
			
			//Bucket Sort(reversed)
			ints = intsI.clone();
			startTime = System.currentTimeMillis();
			for (j = 0; j < NUMITER; j++) {
				sorter.bucketSort(ints, 0, size - 1, true);
			}
			endTime = System.currentTimeMillis();
			duration = ((double) (endTime - startTime)) / NUMITER;
			System.out.println("Bucket Sort | Reverse = True | Duration = " + duration);
			
			//Heap Sort(reversed)
			Integers = IntegersI.clone();
			startTime = System.currentTimeMillis();
			for (j = 0; j < NUMITER; j++) {
				sorter.heapSort(Integers, 0, size - 1, true);
			}
			endTime = System.currentTimeMillis();
			duration = ((double) (endTime - startTime)) / NUMITER;
			System.out.println("Heap Sort | Reverse = True | Duration = " + duration);
			
			//Radix Sort(reversed)
			ints = intsI.clone();
			startTime = System.currentTimeMillis();
			for (j = 0; j < NUMITER; j++) {
				sorter.radixSort(ints, 0, size - 1, true);
			}
			endTime = System.currentTimeMillis();
			duration = ((double) (endTime - startTime)) / NUMITER;
			System.out.println("Radix Sort | Reverse = True | Duration = " + duration);
			
			//Merge Sort(reversed)
			Integers = IntegersI.clone();
			startTime = System.currentTimeMillis();
			for (j = 0; j < NUMITER; j++) {
				sorter.mergeSort(Integers, 0, size - 1, true);
			}
			endTime = System.currentTimeMillis();
			duration = ((double) (endTime - startTime)) / NUMITER;
			System.out.println("Merge Sort | Reverse = True | Duration = " + duration);
			
			//Quick Sort(reversed)
			Integers = IntegersI.clone();
			startTime = System.currentTimeMillis();
			for (j = 0; j < NUMITER; j++) {
				sorter.quickSort(Integers, 0, size - 1, true);
			}
			endTime = System.currentTimeMillis();
			duration = ((double) (endTime - startTime)) / NUMITER;
			System.out.println("Quick Sort | Reverse = True | Duration = " + duration);
			
			//Merge Sort LL
			startTime = System.currentTimeMillis();
			for (j = 0; j < NUMITER; j++) {
				sorter.mergeSortLL(head0, true);
			}
			endTime = System.currentTimeMillis();
			duration = ((double) (endTime - startTime)) / NUMITER;
			System.out.println("Merge Sort LL | Reverse = True | Duration = " + duration);
			
			//Insertion Sort LL
			startTime = System.currentTimeMillis();
			for (j = 0; j < NUMITER; j++) {
				sorter.insertionSortLL(head1, true);
			}
			endTime = System.currentTimeMillis();
			duration = ((double) (endTime - startTime)) / NUMITER;
			System.out.println("Insertion Sort LL | Reverse = True | Duration = " + duration);
			
			//Optimized Quick Sort(reversed)
			Integers = IntegersI.clone();
			startTime = System.currentTimeMillis();
			for (j = 0; j < NUMITER; j++) {
				sorter.optimziedQuickSort(Integers, 0, size - 1, true);
			}
			endTime = System.currentTimeMillis();
			duration = ((double) (endTime - startTime)) / NUMITER;
			System.out.println("Optimized Quick Sort | Reverse = True | Duration = " + duration);
			System.out.println("==================");
		}
	}

}
