import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

public class GeneticAlgorithm
{
    HashMap<String, Lesson> lessonsMap;
    HashMap<String, Teacher> teachersMap;

    private ArrayList<Chromosome> population; // population with chromosomes
    private ArrayList<Integer> occurrences; // list with chromosomes (indices) based on fitness score

    int grade = 9;
    int day = 5;
    int slot = 7;

    GeneticAlgorithm(HashMap<String, Lesson> lessonsMap, HashMap<String, Teacher> teachersMap)
    {
        this.lessonsMap = lessonsMap;
        this.teachersMap = teachersMap;

        this.population = null;
        this.occurrences = null;
    }

    Chromosome run(int populationSize, double mutationProbability, int maxSteps, int totalFitness, int minFitness)
    {

        this.initializePopulation(populationSize);

        Random r = new Random();
        for(int step = 0; step < maxSteps; step++)
        {
            ArrayList<Chromosome> newPopulation = new ArrayList<>();
            for(int i = 0; i < populationSize / 2; i++)
            {
                int xIndex = this.occurrences.get(r.nextInt(this.occurrences.size()));
                Chromosome xParent = this.population.get(xIndex);

                int yIndex = this.occurrences.get(r.nextInt(this.occurrences.size()));
                while(xIndex == yIndex){ yIndex = this.occurrences.get(r.nextInt(this.occurrences.size()));}
                Chromosome yParent = this.population.get(yIndex);

                Chromosome[] children = this.reproduce(xParent, yParent);

                if(r.nextDouble() < mutationProbability)
                {
                    children[0].mutate();
                    children[1].mutate();
                }

                newPopulation.add(children[0]);
                newPopulation.add(children[1]);
            }
            this.population = new ArrayList<>(newPopulation);

            this.population.sort(Collections.reverseOrder());

            if(this.population.get(0).getFitness() >= minFitness){
                System.out.println("\nMin fitness: " + minFitness);
                System.out.println("Current fitness: " + this.population.get(0).getFitness());
                return this.population.get(0);
            }

            System.out.println(this.population.get(0).getFitness());

            this.updateOccurrences();
        }

        System.out.println("\nMin fitness: " + minFitness);
        System.out.println("Current fitness: " + this.population.get(0).getFitness());

        return this.population.get(0);
    }


    void initializePopulation(int populationSize)
    {
        this.population = new ArrayList<>();
        for(int i = 0; i < populationSize; i++)
        {
            this.population.add(new Chromosome(lessonsMap, teachersMap));
        }
        this.updateOccurrences();
    }


    void updateOccurrences()
    {
        this.occurrences = new ArrayList<>();
        for(int i = 0; i < this.population.size(); i++)
        {
            for(int j = 0; j < this.population.get(i).getFitness(); j++)
            {
                this.occurrences.add(i);
            }
        }
    }


    Chromosome[] reproduce(Chromosome x, Chromosome y)
    {
        Random r = new Random();

        int choice = r.nextInt(2);

        Lesson_Teacher[][][] firstChild = new Lesson_Teacher[grade][day][slot];
        Lesson_Teacher[][][] secondChild = new Lesson_Teacher[grade][day][slot];

        //intescect by day
        if(choice==0) {

            for (int g = 0; g < grade; g++) {
                int intersectionDay = r.nextInt(day);
                for (int d = 0; d < intersectionDay; d++) {
                    for (int s = 0; s < slot; s++) {
                        firstChild[g][d][s] = x.getGenes()[g][d][s];
                        secondChild[g][d][s] = y.getGenes()[g][d][s];
                    }
                }
                for (int d = intersectionDay; d < day; d++) {
                    for (int s = 0; s < slot; s++) {
                        firstChild[g][d][s] = y.getGenes()[g][d][s];
                        secondChild[g][d][s] = x.getGenes()[g][d][s];
                    }
                }
            }

        }else if(choice==1){

            for (int g = 0; g < grade; g++) {
                for (int d = 0; d < day; d++) {
                    int intersectionSlot = r.nextInt(slot);
                    for (int s = 0; s < intersectionSlot; s++) {
                        firstChild[g][d][s] = x.getGenes()[g][d][s];
                        secondChild[g][d][s] = y.getGenes()[g][d][s];
                    }
                    for (int s = intersectionSlot; s < slot; s++) {
                        firstChild[g][d][s] = y.getGenes()[g][d][s];
                        secondChild[g][d][s] = x.getGenes()[g][d][s];
                    }
                }
            }

        }

        //TODO how and where will i reproduce them

        return new Chromosome[] {new Chromosome(firstChild, lessonsMap, teachersMap), new Chromosome(secondChild, lessonsMap, teachersMap)};
    }
}
