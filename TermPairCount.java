/*
names: Trevor Greenside, Thomas Hughes, Matthew Lee
asgn: Homework 11
course: CPSC 435 - 01
date due: 17 April 2019
*/

import java.io.IOException;
import java.util.*;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.join.*;
import org.apache.hadoop.util.*;

public class TermPairCount 
{
    
    /*
    1: method MAP(docid id, doc d)
    2:      A = new ArrayList;
    3:      for each concept c in d //this loop finds a list of unique CIDs
    4:      if(A.contains(c) = false)
    5:          A.add(c);
    6:      end for
    7:      SORT(A);
    8:      for each concept c_i in A; 0 ≤ i ≤ A.length() − 1
    9:          EMIT(concept c_i, one);
    10:         for each concept c_j in A, i < j ≤ A.length() - 1
    11:             EMIT(concept {c_i: c_j}, one);
    12:         end for
    13:     end for
    14: end method
    */

    public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, IntArrayWritable, IntWritable> 
    {
        private final static IntWritable one = new IntWritable(1);
        private QuickSort sorter = new QuickSort();
 
        public void map(
            LongWritable docId, /* document id */
            Text document, /* document text */
            OutputCollector<IntArrayWritable, IntWritable> output, /* output <K,V> collector to our reducer */
            Reporter reporter)
        throws IOException 
        {
            // Tokenize document
            StringTokenizer tokenizer = new StringTokenizer(document.toString());
            
            // Set of concepts
            HashSet<Integer> conceptSet = new HashSet<Integer>();
            
            // Loop over tokens, add tokens that are integers into 'conceptSet'
            while (tokenizer.hasMoreTokens())
            {
                String conceptStr = tokenizer.nextToken();
                
                try {
                    conceptSet.add(Integer.parseInt(conceptStr));
                } catch (NumberFormatException e) continue;
            }
            
            // Convert conceptSet to ArrayList and sort
            ArrayList<Integer> concepts = new ArrayList<Integer>(conceptSet);
            concepts = sorter.quicksort(concepts);
            
            // Loop and add each concept and pairs of concepts to the output collector
            for (int i = 0; i < concepts.size(); i++) {
                Integer c_i = concepts.get(i);
                
                output.collect(new IntArrayWritable(c_i), one);
                
                for (int j = i; j < concepts.size(); j++) {
                    output.collect(new IntArrayWritable(c_i, c_j), one);
                }
            }
        }
    }

    public static class Reduce extends MapReduceBase implements Reducer<Text, IntWritable, IntArrayWritable, IntWritable> 
    {
    
        public void reduce(
            IntArrayWritable key, /* key from our mapper */
            Iterator<IntWritable> values, /* values from our mapper */
            OutputCollector<IntArrayWritable, IntWritable> output, /* output collector <K,V> to file output */
            Reporter reporter)
        throws IOException 
        {
            int sum = 0;
            while (values.hasNext()) 
            {
                sum += values.next().get();
            }
            output.collect(key, new IntWritable(sum));
        }
    }
    
    public static class IntArrayWritable extends ArrayWritable {
        
        /**
         * Create empty IntArrayWritable
         */
        public IntArrayWritable() {
            super(IntWritable.class);
        }

        @Override
        public IntWritable[] get() {
            return (IntWritable[]) super.get();
        }
        
        /**
         * Create IntArrayWritable from Integer array.
         */
        public IntArrayWritable(Integer[] values) {
            super(IntWritable.class);
            IntWritable[] writableValues = new IntWritable[values.length];
            for (int i = 0; i < values.length; i++) {
                writableValues[i] = new IntWritable(values[i]);
            }
            set(writableValues);
        }
        
        /**
         * Create single-element IntArrayWritable.
         */
        public IntArrayWritable(Integer value) {
            super(IntWritable.class);
            IntWritable[] writableValues = new IntWritable[1];
            writableValues[0] = new IntWritable(value);
            set(writableValues);
        }
        
        /**
         * Create two-element IntArrayWritable.
         */
        public IntArrayWritable(Integer value1, Integer value2) {
            super(IntWritable.class);
            IntWritable[] writableValues = new IntWritable[2];
            writableValues[0] = new IntWritable(value1);
            writableValues[1] = new IntWritable(value2);
            set(writableValues);
        }
    }

    public static void main(String[] args) throws Exception 
    {
        JobConf conf = new JobConf(TermPairCount.class);

        conf.setJobName("termpaircount");
        
        // Set input/output parameters
        conf.setOutputKeyClass(IntArrayWritable.class);
        conf.setOutputValueClass(IntWritable.class);
        
        // Set Classes
        conf.setMapperClass(Map.class);
        conf.setCombinerClass(Reduce.class);
        conf.setReducerClass(Reduce.class);
        
        // Set input/output formats
        conf.setInputFormat(TextInputFormat.class);
        conf.setOutputFormat(TextOutputFormat.class);
        
        FileInputFormat.setInputPaths(conf, new Path(args[0]));
        FileOutputFormat.setOutputPath(conf, new Path(args[1]));

        JobClient.runJob(conf);
   }
}