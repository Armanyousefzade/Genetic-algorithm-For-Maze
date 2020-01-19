/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package waterdirect;

import java.util.ArrayList;

/**
 *
 * @author arman
 */
public class Population {

    int totalFitness = 0;
    static int populationSize;
    PopulationMember best = null;
    PopulationMember nearest = null;
    ArrayList<PopulationMember> array = new ArrayList<>();

    public void Add(Individual individual) {
        PopulationMember populationMember = new PopulationMember(individual);
        totalFitness = totalFitness + populationMember.fitness;
        populationMember.selectionProbability = totalFitness;
        array.add(populationMember);
        WaterDirect.GetPath(individual);
        if (WaterDirect.flag1 == 0 && WaterDirect.flag2 == 0) {
            if (nearest == null) {
                nearest = populationMember;
            } else {
                if (populationMember.distanceFromB < nearest.distanceFromB) {
                    nearest = populationMember;
                }
            }
        }
        if (best == null && !(WaterDirect.flag1 == 0 && WaterDirect.flag2 == 0)) {
            best = populationMember;
        } else {
            if (!(WaterDirect.flag1 == 0 && WaterDirect.flag2 == 0) && populationMember.fitness > best.fitness) {
                best = populationMember;
            }
        }
    }

}
