/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package waterdirect;

/**
 *
 * @author arman
 */
public class PopulationMember {

    static int i;
    static int j;
    Individual individual;
    int selectionProbability;
    int fitness;
    int distanceFromB;

    public PopulationMember(Individual individual) {
        this.individual = individual;
        fitness = GeneticAlgorithm.Fitness(individual);
        distanceFromB = GeneticAlgorithm.FindDistance(i, j);
    }

}
