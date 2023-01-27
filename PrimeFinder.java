import java.util.*;
import java.io.*;

public class PrimeFinder
{
    // Create a global array list to store all found prime numbers
    // Keep a running long sum for all found primes
    static ArrayList<Integer> primes = new ArrayList<>();
    static long sum = 0;

    // Main method
    public static void main(String [] args)
    {
        // Create an array list for threads so we can loop through them and join them after the work is done
        ArrayList<Thread> threads = new ArrayList<>();

        // Read in the max value to search for primes
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        sc.close();


        // We want 8 threads, so we should determine the sizes of the ranges that each thread will work on
        int rangeFactor = (int)Math.ceil((double)n / 8);
        long start = System.currentTimeMillis();

        // Ignoring 0 and 1, use i as the low and i + rangeFactor as the high for each threads workload
        for (int i = 2; i <= n; i = i + rangeFactor + 1)
        {
            int high = i + rangeFactor;
            if (high > n) high = n; // don't go over the limit

            // Create the thread and start it, add it to the thread list for later usage
            Sieve sieveThread = new Sieve(i, high);
            Thread t = new Thread(sieveThread);
            t.start();
            threads.add(t);
        }

        // Go through and join each thread (wait until they're done)
        try
        {
            for (Thread th : threads)
                th.join();
        }
        catch (Exception e)
        {}

        // sort the list of primes because items got added in a possibly random order
        Collections.sort(primes);
        long elapsed = System.currentTimeMillis() - start; // stop the clock!
        double secs = (double)elapsed / 1000;

        try
        {
            // Set up file writer to display the output
            BufferedWriter file = new BufferedWriter(new FileWriter("primes.txt"));
            file.write(secs + "s" + " or " + elapsed + "ms" + " ");
            
            // Write the amount of primes and the sum of all primes found
            file.write(primes.size() + " " + sum + "\n");

            // loop backwards through the sorted list and print the top 10 primes
            file.write("Top 10: ");
            for (int i = primes.size() - 1; i > primes.size() - 11; i--)
                file.write(primes.get(i) + ", ");

            file.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    static class Sieve implements Runnable
    {
        private int low, high;
        public Sieve(int lo, int hi)
        {
            this.low = lo;
            this.high = hi;
        }

        // Sieve of Eratosthenes implementation
        @Override
        public void run()
        {
            // Declare an array of flags for each number from 2 to high
            boolean [] nums = new boolean[high + 1];
            Arrays.fill(nums, true);
            // starting from 2 to sqrt(high), mark all multiples (aka non-primes)
            for (int i = 2; i*i <= high; i++)
            {
                // If this is still currently marked as prime, go through its multiples and flag them as composite
                if (nums[i] == true)
                {
                    for (int j = i*i; j <= high; j += i)
                    {
                        nums[j] = false; // j can't be prime
                    }
                }
            }

            // Finally, starting from the low in the range to the high, print all the numbers that are prime
            for (int i = low; i <= high; i++)
            {
                if (nums[i])
                {
                    sum += i; // add to total sum of all primes
                    primes.add(i);
                }
            }
        }
    }
}