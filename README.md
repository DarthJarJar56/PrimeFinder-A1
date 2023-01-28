# Assignment #1 - Multithreaded Sieve of Eratosthenes

### Introduction
A prime number is a number that fulfills the condition of having no multiples other than 1 and itself. For example, the number $17$ is prime because its factors are only $[1, 17]$. Finding a list of prime numbers can be an important task in some areas in Information Technology and mathematics. 

### Naive approaches
Unfortunately, the majority of algorithms used to find a list of prime numbers are inefficient. A simple, common algorithm runs in $O(n^2)$ time in which we iterate over the range $[2 .. n]$ and for each $i$, run an $O(n)$ `isPrime()` function, resulting in an $O(n^2)$ algorithm. 
```java
public class NaivePrime
{
	public static void main(String [] args)
	{
		int n = 1000;
		for (int i = 2; i <= n; i++)
			if (isPrime(i))
				System.out.print(i + ", ");
	}
	
	public static boolean isPrime(int check)
	{
		if (check <= 1) return false;
		for (int i = 2; i < check; i++)
			if (check % i == 0)
				return false;
		return true;
	}
}
```

### A slightly better solution
We can slightly improve this algorithm by modifying the `isPrime()` function to run in $O(\sqrt n)$ rather than $O(n)$, making the total runtime $O(n \times \sqrt n)$. By changing the upper bound of the for loop to $\sqrt n$ instead of $n$. That would look like this:
```java
public static boolean isPrime(int n)
{
	if (n <= 1) return false;
	for (int i = 2; i <= Math.sqrt(n); i++)
		if (n % i == 0)
			return false;
	return true;
}
```
However, even this isn't the most efficient prime finding algorithm. There is a more efficient algorithm out there to find the prime numbers less than or equal to some number $n$. 

### The Sieve of Eratosthenes
We can further improve the runtime of finding all prime numbers less than or equal to $n$ by using an algorithm known as the Sieve of Eratosthenes. This is an algorithm that utilizes a boolean array of flags to mark the numbers that are prime or composite. To do this:
1) We start at index 2 of the array and mark all multiples of 2 as false. 
2) Move to the next unmarked  index and repeat the process
3) Repeat steps 2-3 until we reach the upper bound of $\sqrt n$

We would implement this algorithm as follows:
```java
import java.util*;

public class Sieve
{
	public static void main(String [] args)
	{
		int n = 10000;
		boolean [] flags = new boolean[n+1];
		Arrays.fill(flags, true);
		
		for (int i = 2; i <= Math.sqrt(n); i++)
			if (flags[i] == true)
				for (int j = i*i; j <= n; j += i)
					flags[j] = false;
		
		for (int i = 2; i <= n; i++)
			if (flags[i])
				System.out.print(i + ", ");	
	}
} 
```
The runtime of the Sieve of Eratosthenes algorithm is $O(nlog(log(n)))$.

## Multithreaded solution
We can utilize multithreading to divide the workload of this algorithm to 8 threads. In Java, we create a thread by instantiating a `Sieve` class and then passing it into the constructor of Java's `Thread` class. We can set this up in such a way that the `Sieve` class finds the prime numbers in a given range $[m...n]$ and then spin up 8 threads with roughly equal workload to concurrently complete the task of finding prime numbers up to $10^8$. We would need to calculate a range factor that would be $ceil(n / 8)$, meaning when we start, the low of the range would be `i` and the high of the range would be `i + rangeFactor`. The for loop would need to increment `i` by `i + rangeFactor` to generate the next range low. We could set it up as follows:
```java
int n = 10000;
int rangeFactor = (int)Math.ceil((double)n / 8);

for (int i = 2; i <= n; i = i + rangeFactor + 1)
{
	int high = i + rangeFactor;
	if (high > n) high = n; // prevents going over the limit
	Sieve sieveThread = new Sieve(i, high);
	Thread t = new Thread(sieveThread);
	t.start();
}
```
At this time, the runtime of this setup when $n = 10^8$ is `1.736s` or `1736ms`. When using the other, less efficient, algorithms for finding prime numbers in a range, the runtime is **drastically** longer. Using the $O(n^2)$ algorithm takes `6.887s` or `6887ms` for $n = 10^6$, over 6x longer than the efficient Sieve algorithm, where $n$ is 100x larger. 

In the file `PrimeFinder.java`, the approach described above dividing the workload between 8 threads is implemented. In order to evaluate and test this program, I compared the results against a standard Sieve application across several multiples of 10. Starting from $n = 10^3$ up until $n = 10^8$, I matched the results from the well known Sieve algorithm with my multithreaded solution. 