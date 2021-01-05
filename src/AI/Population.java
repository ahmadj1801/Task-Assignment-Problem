package AI;

import java.util.ArrayList;

public class Population{

    int generation_number;
    int population_size;
    ArrayList<Chromosome> population;
    int num_people;
    int num_tasks;
    int sum_fitness;

    public Population(){}

    public Population(int generation, int population_size, int persons, int tasks){
        this.generation_number = generation;
        this.population_size = population_size;
        this.population = new ArrayList<>();
        this.num_people = persons;
        this.num_tasks = tasks;

        if(this.generation_number==0) {
            while (this.population.size() < this.population_size) {
                //Create new chromosome...
                Chromosome c = new Chromosome(num_people, num_tasks);
                //Add if not present in population
                this.add_chromosome(c);
            }
        }
    }

    public boolean check_present(Chromosome chromosome){
        for (Chromosome c : this.population) {
            if (chromosome.equals(c)) {
                //it is already present
                return true;
            }
        }
        return false;
    }

    public void add_chromosome(Chromosome chromosome){
        if (!this.check_present(chromosome)) {
            this.population.add(chromosome);
        }
    }

    public void evaluate_fitness(){
        //Does population have chromosomes?
        if(population != null){
            //For each chromosome in the population
            int sum = 0;
            for(Chromosome chromosome: population){
                //Calculate the fitness value for the chromosome
                chromosome.calculate_fitness();
                sum += chromosome.get_fitness_value();

            }
            this.sum_fitness = sum;
        }
    }

    public Chromosome get_maximiser(){
        Chromosome maximiser = this.population.get(0);
        for(Chromosome c: population){
            if(c.get_fitness_value()>maximiser.get_fitness_value()){
                maximiser = c;
            }
        }
        return maximiser;
    }

}
