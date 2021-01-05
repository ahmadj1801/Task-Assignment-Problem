package AI;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main {

    public static final double CROSSOVER_RATE = 0.85;
    public static final double MUTATION_RATE = 0.15;

    public static int[][] times;
    public static int num_people ;
    public static int num_tasks ;

    public static void read_file(String path, int num_spaces){
        try {
            String spaces ="";
            for(int i =0; i<num_spaces; i++){
                spaces+= " ";
            }
            Scanner file = new Scanner(new File(path));
            num_people = Integer.parseInt(file.nextLine());
            num_tasks = Integer.parseInt(file.nextLine());
            times = new int[num_people][num_tasks];
            int person = 0;
            while(file.hasNextLine()){
                String line = file.nextLine();
                String[] data = line.split(spaces);
                for(int i=0;i<data.length;i++){
                    times[person][i] = Integer.parseInt(data[i].trim());
                }
                person++;
            }
        }catch(IOException i){
            JOptionPane.showMessageDialog(null,"The file you are looking " +
                    "for cannot be read!!!","File Not Found",JOptionPane.WARNING_MESSAGE);
            System.exit(0);
        }
    }

    public static Chromosome crossover(Chromosome parent1, Chromosome parent2, int parent_case){
        int start, end;
        int[] parent1_chromosome = parent1.get_chromosome();
        int[] parent2_chromosome = parent2.get_chromosome();
        int[] child_chromosome = new int[parent1_chromosome.length];
        ArrayList<Integer> child_contains = new ArrayList<>();

        for(int i =0; i<child_chromosome.length;i++){
            child_chromosome[i] = -1;
        }

        Random rng = new Random();

        start = rng.nextInt(parent1_chromosome.length - 1) ; //dont want to choose the last index, hence -1
        end = start + rng.nextInt(parent1_chromosome.length - start - 1) + 1; //at least 1 higher than start

        if(parent_case == 1){
            //take from Parent 1
            for(int i=start; i<=end; i++){
                child_contains.add(parent1_chromosome[i]);
                child_chromosome[i] = parent1_chromosome[i];
            }

            //take from parent 2
            for(int i =0; i<parent2_chromosome.length;i++){
                int num = parent2_chromosome[i];
                if(!child_contains.contains(num)){
                    child_contains.add(num);
                    for(int j=0; j<child_chromosome.length;j++){
                        if(child_chromosome[j]==-1){
                            child_chromosome[j] = num;
                            break;
                        }
                    }

                }
            }

        }else{
            //take from Parent 2
            for(int i=start; i<=end; i++){
                child_contains.add(parent2_chromosome[i]);
                child_chromosome[i] = parent2_chromosome[i];
            }

            //take from parent 1
            for(int i =0; i<parent1_chromosome.length;i++){
                if(!child_contains.contains( parent1_chromosome[i])){
                    child_contains.add( parent1_chromosome[i]);
                    for(int j=0; j<child_chromosome.length;j++){
                        if(child_chromosome[j]==-1){
                            child_chromosome[j] =  parent1_chromosome[i];
                            break;
                        }
                    }

                }
            }
        }

        return new Chromosome(parent1.get_num_people(), parent1.get_num_tasks(), child_chromosome.clone());
    }



    public static void mutate(Chromosome chromosome){
        int restriction = chromosome.get_chromosome().length/3;
        Random rng = new Random();
        int reorder = rng.nextInt(restriction)+1;
        int[] arr = chromosome.get_chromosome();
        for(int i =0; i<reorder; i++){
            int p1 = rng.nextInt(chromosome.get_chromosome().length);
            int p2 = rng.nextInt(chromosome.get_chromosome().length);
            int temp = arr[p1];
            arr[p1] = arr[p2];
            arr[p2] = temp;

        }
        chromosome.set_chromosome(arr);
    }

    public static Chromosome roulette_wheel_selection(Population population){

        Random rng = new Random();

        int benchmark = rng.nextInt(population.sum_fitness);
        int p = 0;
        for(Chromosome c:population.population){
            p = p + c.get_fitness_value();
            if(p>=benchmark){
                return c;
            }
        }

        return null;
    }

    public static void main(String[] args) throws CloneNotSupportedException{
        Set<Integer> previous_highest = new LinkedHashSet<>();
        //Read in input.txt
        //read_file();
        int file_option = Integer.parseInt(JOptionPane.showInputDialog(null, "Would you like to" +
                " use the default file or use an external file?\nPlease select an option below:\n1. Use default file\n" +
                "2. Use external file"));

        if(file_option == 1){
            read_file("input.txt", 2);
        }else{
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
            chooser.addChoosableFileFilter(new FileNameExtensionFilter("txt files", ".txt"));
            int result = chooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                System.out.println("Selected file: " + file.getAbsolutePath());
                int num_spaces = Integer.parseInt(JOptionPane.showInputDialog(null, "How many spaces " +
                        "separate the numbers in the text file?\nPlease insert a number 1, 2 etc"));
                read_file(file.getAbsolutePath(), num_spaces);
            }
        }
        //Confirmation of successful read
        JOptionPane.showMessageDialog(null,"File Successfully Read!!!\nNote: For large inputs " +
                        "the algorithm may take a few seconds", "Success",
                JOptionPane.INFORMATION_MESSAGE);

        //Create initial population
        Population population = new Population(0,  num_tasks * 10
                , num_people, num_tasks);

        //evaluate population fitness
        population.evaluate_fitness();

        //Get best chromosome
        Chromosome current_maximiser =(Chromosome) population.get_maximiser().clone();
        int gen = population.generation_number;
        previous_highest.add(current_maximiser.get_fitness_value());

        //Best for how many generations?
        int maximiser_count = 0;

        //test if optimal(Same maximiser value for 10 generations) or max generation is done
        while(maximiser_count<= (100 * num_tasks )) {
            System.out.println("Generation "+population.generation_number );
            System.out.println("Generation Fittest: "+ population.get_maximiser().get_fitness_value() );
            System.out.println("Current Optimal Solution: " + current_maximiser.get_fitness_value()+"\n");
            Chromosome parent1, parent2;

            Population new_population = new Population(population.generation_number+1,
                    num_tasks * 10, num_people, num_tasks);

            Random rng = new Random();

            while(new_population.population.size()<new_population.population_size){

                parent1 = roulette_wheel_selection(population);
                parent2 = roulette_wheel_selection(population);
                double prob = rng.nextDouble();//Double.parseDouble(df.format(rng.nextDouble()));
                if(prob>CROSSOVER_RATE){
                    new_population.add_chromosome(parent1);
                    new_population.add_chromosome(parent2);
                }else{
                    //Within crossover rate
                    Chromosome child1 = crossover(parent1, parent2, 1);
                    Chromosome child2 = crossover(parent1,parent2, 2);
                    new_population.add_chromosome(child1);
                    new_population.add_chromosome(child2);
                    //Within Mutation rate
                    if(prob<MUTATION_RATE){
                        Chromosome c1 = (Chromosome)child1.clone();
                        Chromosome c2 = (Chromosome)child2.clone();
                        mutate(c1);
                        mutate(c2);
                        new_population.add_chromosome(c1);
                        new_population.add_chromosome(c2);
                    }
                }
            }

            population= new_population;

            //calculate fitness of new population
            population.evaluate_fitness();

            //Best Chromosome in new population
            Chromosome chromosome = population.get_maximiser();

            //check if new max > old max
            if(current_maximiser.get_fitness_value() < chromosome.get_fitness_value()){
                //yes bigger - set 0
                maximiser_count = 0;
                //update control variable
                current_maximiser =(Chromosome) chromosome.clone();
                gen = population.generation_number;
                previous_highest.add(current_maximiser.get_fitness_value());

            }else{
                //no smaller - increase count
                maximiser_count++;
            }
        }

        System.out.println("Best Output found @ Generation " + gen);
        System.out.println(current_maximiser.toString());
        System.out.println("Previous Maximiser Values: " + previous_highest.toString());

        JOptionPane.showMessageDialog(null, "Optimal Solution Located @ Generation "+gen+ ":\n"+
                current_maximiser.toString() + "\nSub-Optimal fitness values encountered on the way to the solution: "+
                previous_highest.toString());
    }

}
