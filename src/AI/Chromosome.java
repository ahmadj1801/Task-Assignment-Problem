package AI;

import java.util.ArrayList;
import java.util.Random;

public class Chromosome implements Cloneable{

    private int fitness_value;
    private int num_people, num_tasks;
    private int[] chromosome;

    public Chromosome(){}

    public Chromosome(int num_people, int num_tasks, int[] chromosome){
        this.num_people = num_people;
        this.num_tasks = num_tasks;
        this.chromosome = chromosome;
    }

    public Chromosome(int num_people, int num_tasks){
        this.num_people = num_people;
        this.num_tasks = num_tasks;
        this.chromosome = new int[this.num_tasks];
        ArrayList<Integer> used = new ArrayList<>();

        //Random person per task
        int i=0;
        //used is initially empty
        while(used.size()!=chromosome.length){
            //Random generator
            Random rng = new Random();
            //Get a person number 0 to n-1
            int person = rng.nextInt(this.num_people);
            //check if person is assigned
            if(!used.contains(person)){
                //if not assigned, then assign task
                used.add(person);
                chromosome[i] = person;
                //increase i for next task
                i++;
            }
        }
    }

    public int get_num_people(){return this.num_people;}
    public int get_num_tasks(){return this.num_tasks;}
    public int[] get_chromosome(){
        return this.chromosome.clone();
    }
    public int get_fitness_value(){return this.fitness_value;}

    public void calculate_fitness(){
        int sum = 0;
        for(int i =0; i<chromosome.length; i++){
            sum += Main.times [chromosome[i]][i];
        }
        this.fitness_value = sum;
    }

    public void set_chromosome(int[] chromosome){this.chromosome = chromosome;}

    @Override
    public boolean equals(Object obj){
        Chromosome c = (Chromosome)obj;
        int count = 0;
        for(int i=0;i<chromosome.length;i++){
            if(c.get_chromosome()[i]==chromosome[i]){
                count++;
            }
        }
        if(count == chromosome.length){
            return true;
        }else{
            return false;
        }
    }

    public String toString(){
        String s = "";
        for(int i =0; i<this.chromosome.length;i++){
            s += "Task "+ (i+1) + ": \t" +
                    "Person " + (char)(this.chromosome[i] + 65) + " With Quality "+
                    Main.times[this.chromosome[i]][i] +"\n";
        }
        s += "Maximised Quality = " + this.fitness_value;
        return s;
    }
    public Object clone() throws CloneNotSupportedException{
        return super.clone();
    }

}
