/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package waterdirect;

import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author arman
 */
public class GeneticAlgorithm {

    static int maxFitness;

    public static Individual GeneticAlgorithm(Population population, int iterationLimit, int mutationProbability) {
        Individual x;
        Individual y;
        Individual child;
        int i = 0;
        PopulationMember totalBest = population.best;
        PopulationMember nearestToB = population.nearest;
        while (i < iterationLimit) {
            System.out.println("generation: " + i);
            Population newPopulation = new Population();
            for (int k = 0; k < Population.populationSize; k++) {
                x = FitnessBasedRandomSelection(population);
                y = FitnessBasedRandomSelection(population);
                child = Reproduce(x, y);
                child = Mutate(child, mutationProbability);
                newPopulation.Add(child);
            }
            population = newPopulation;
            if (population.nearest.distanceFromB < nearestToB.distanceFromB) {
                nearestToB = population.nearest;
            }
            if (totalBest == null && population.best != null) {
                totalBest = population.best;
            } else {
                if (totalBest != null && population.best != null) {
                    if (population.best.fitness > totalBest.fitness) {
                        totalBest = population.best;
                    }
                }
            }
            i++;
        }
        if (totalBest != null) {
            return totalBest.individual;
        } else {
            return nearestToB.individual;
        }
    }

    public static Population initializePopulation() {
        int populationSize = Population.populationSize;
        Population basePopulation = new Population();
        Individual base = new Individual(WaterDirect.map);
        basePopulation.Add(base);
        for (int k = 0; k < populationSize - 1; k++) {
            basePopulation.Add(Mutate(base, 70));
        }
        return basePopulation;
    }

    public static Individual Reproduce(Individual x, Individual y) {
        int n = WaterDirect.n;
        Random rn = new Random();
        int c = rn.nextInt(n - 1) + 0;
        Cell[][] matrix = new Cell[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j <= c; j++) {
                matrix[i][j] = new Cell(x.map.matrix[i][j].type, x.map.matrix[i][j].direction, x.map.matrix[i][j].cost);
            }
            for (int j = c + 1; j < n; j++) {
                matrix[i][j] = new Cell(y.map.matrix[i][j].type, y.map.matrix[i][j].direction, y.map.matrix[i][j].cost);
            }
        }
        Map map = new Map(matrix);
        return new Individual(map);
    }

    public static Individual Mutate(Individual individual, int probability) {
        int n = WaterDirect.n;
        Cell[][] matrix = new Cell[n][n];
        Random rn = new Random();
        int random;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                matrix[i][j] = new Cell(individual.map.matrix[i][j].type, individual.map.matrix[i][j].direction, individual.map.matrix[i][j].cost);
                random = rn.nextInt(100) + 1;
                if (random <= probability) {
                    int temp = 3;
                    if (WaterDirect.map.matrix[i][j].type == 2) {
                        temp = 1;
                    }
                    int mutation = rn.nextInt(temp) + 0;
                    matrix[i][j].direction = mutation;
                }
            }
        }
        Individual mutated = new Individual(new Map(matrix));
        return mutated;
    }

    public static int Fitness(Individual individual) {
        int startFromLeft = FitnessStep1(individual);
        int startFromUp = FitnessStep2(individual);
        if (startFromLeft >= startFromUp) {
            return startFromLeft;
        }
        return startFromUp;
    }

    public static int FitnessStep1(Individual individual) {
        ArrayList<Cell> seenCells = new ArrayList<Cell>();
        Map problemMap = individual.map;
        int n = WaterDirect.n;
        maxFitness = (((n * n) - 2) * 5) + (2 * n * 5);
        int cost = 0;
        int currentRow = n - 1;
        int currentColumn = 1;
        EntranceDirection currentEntranceDirection = EntranceDirection.left;
        seenCells.add(problemMap.matrix[currentRow][currentColumn]);
        Cell currentCell;
        while (true) {
            currentCell = problemMap.matrix[currentRow][currentColumn];
            if (currentRow == 0 && currentColumn == n - 1) {
                break;
            }
            if (currentEntranceDirection == EntranceDirection.down) {
                if (currentCell.type == 2) {
                    if (currentCell.direction == 0) {
                        cost = cost + (FindDistance(currentRow, currentColumn) * 5);
                        break;
                    } else {
                        if (currentRow - 1 < 0 || seenCells.contains(problemMap.matrix[currentRow - 1][currentColumn])) {
                            cost = cost + (FindDistance(currentRow, currentColumn) * 5);
                            break;
                        } else {
                            cost = cost + currentCell.cost;
                            currentRow = currentRow - 1;
                            currentEntranceDirection = EntranceDirection.down;
                            seenCells.add(problemMap.matrix[currentRow][currentColumn]);
                        }
                    }
                } else {
                    if (currentCell.direction == 0) {
                        if (currentColumn + 1 >= n || seenCells.contains(problemMap.matrix[currentRow][currentColumn + 1])) {
                            cost = cost + (FindDistance(currentRow, currentColumn) * 5);
                            break;
                        } else {
                            cost = cost + currentCell.cost;
                            currentColumn = currentColumn + 1;
                            currentEntranceDirection = EntranceDirection.left;
                            seenCells.add(problemMap.matrix[currentRow][currentColumn]);
                        }
                    }
                    if (currentCell.direction == 1) {
                        cost = cost + (FindDistance(currentRow, currentColumn) * 5);
                        break;
                    }
                    if (currentCell.direction == 2) {
                        cost = cost + (FindDistance(currentRow, currentColumn) * 5);
                        break;
                    }
                    if (currentCell.direction == 3) {
                        if (currentColumn - 1 < 0 || seenCells.contains(problemMap.matrix[currentRow][currentColumn - 1])) {
                            cost = cost + (FindDistance(currentRow, currentColumn) * 5);
                            break;
                        } else {
                            cost = cost + currentCell.cost;
                            currentColumn = currentColumn - 1;
                            currentEntranceDirection = EntranceDirection.right;
                            seenCells.add(problemMap.matrix[currentRow][currentColumn]);
                        }
                    }
                }
            } else {
                if (currentEntranceDirection == EntranceDirection.up) {
                    if (currentCell.type == 2) {
                        if (currentCell.direction == 0) {
                            cost = cost + (FindDistance(currentRow, currentColumn) * 5);
                            break;
                        } else {
                            if ((currentRow == n - 2 && currentColumn == 0) || currentRow + 1 >= n || seenCells.contains(problemMap.matrix[currentRow + 1][currentColumn])) {
                                cost = cost + (FindDistance(currentRow, currentColumn) * 5);
                                break;
                            } else {
                                cost = cost + currentCell.cost;
                                currentRow = currentRow + 1;
                                currentEntranceDirection = EntranceDirection.up;
                                seenCells.add(problemMap.matrix[currentRow][currentColumn]);
                            }
                        }
                    } else {
                        if (currentCell.direction == 0) {
                            cost = cost + (FindDistance(currentRow, currentColumn) * 5);
                            break;
                        }
                        if (currentCell.direction == 1) {
                            if ((currentRow == n - 1 && currentColumn == 1) || currentColumn - 1 < 0 || seenCells.contains(problemMap.matrix[currentRow][currentColumn - 1])) {
                                cost = cost + (FindDistance(currentRow, currentColumn) * 5);
                                break;
                            } else {
                                cost = cost + currentCell.cost;
                                currentColumn = currentColumn - 1;
                                currentEntranceDirection = EntranceDirection.right;
                                seenCells.add(problemMap.matrix[currentRow][currentColumn]);
                            }
                        }
                        if (currentCell.direction == 2) {
                            if (currentColumn + 1 >= n || seenCells.contains(problemMap.matrix[currentRow][currentColumn + 1])) {
                                cost = cost + (FindDistance(currentRow, currentColumn) * 5);
                                break;
                            } else {
                                cost = cost + currentCell.cost;
                                currentColumn = currentColumn + 1;
                                currentEntranceDirection = EntranceDirection.left;
                                seenCells.add(problemMap.matrix[currentRow][currentColumn]);
                            }
                        }
                        if (currentCell.direction == 3) {
                            cost = cost + (FindDistance(currentRow, currentColumn) * 5);
                            break;
                        }
                    }
                } else {
                    if (currentEntranceDirection == EntranceDirection.left) {
                        if (currentCell.type == 2) {
                            if (currentCell.direction == 0) {
                                if (currentColumn + 1 >= n || seenCells.contains(problemMap.matrix[currentRow][currentColumn + 1])) {
                                    cost = cost + (FindDistance(currentRow, currentColumn) * 5);
                                    break;
                                } else {
                                    cost = cost + currentCell.cost;
                                    currentColumn = currentColumn + 1;
                                    currentEntranceDirection = EntranceDirection.left;
                                    seenCells.add(problemMap.matrix[currentRow][currentColumn]);
                                }

                            } else {
                                cost = cost + (FindDistance(currentRow, currentColumn) * 5);
                                break;
                            }
                        } else {
                            if (currentCell.direction == 0) {
                                cost = cost + (FindDistance(currentRow, currentColumn) * 5);
                                break;
                            }
                            if (currentCell.direction == 1) {
                                if (currentRow - 1 < 0 || seenCells.contains(problemMap.matrix[currentRow - 1][currentColumn])) {
                                    cost = cost + (FindDistance(currentRow, currentColumn) * 5);
                                    break;
                                } else {
                                    cost = cost + currentCell.cost;
                                    currentRow = currentRow - 1;
                                    currentEntranceDirection = EntranceDirection.down;
                                    seenCells.add(problemMap.matrix[currentRow][currentColumn]);
                                }
                            }
                            if (currentCell.direction == 2) {
                                cost = cost + (FindDistance(currentRow, currentColumn) * 5);
                                break;
                            }
                            if (currentCell.direction == 3) {
                                if (currentRow + 1 >= n || seenCells.contains(problemMap.matrix[currentRow + 1][currentColumn])) {
                                    cost = cost + (FindDistance(currentRow, currentColumn) * 5);
                                    break;
                                } else {
                                    cost = cost + currentCell.cost;
                                    currentRow = currentRow + 1;
                                    currentEntranceDirection = EntranceDirection.up;
                                    seenCells.add(problemMap.matrix[currentRow][currentColumn]);
                                }
                            }
                        }
                    } else {
                        if (currentEntranceDirection == EntranceDirection.right) {
                            if (currentCell.type == 2) {
                                if (currentCell.direction == 0) {
                                    if ((currentRow == n - 1 && currentColumn == 1) || currentColumn - 1 < 0 || seenCells.contains(problemMap.matrix[currentRow][currentColumn - 1])) {
                                        cost = cost + (FindDistance(currentRow, currentColumn) * 5);
                                        break;
                                    } else {
                                        cost = cost + currentCell.cost;
                                        currentColumn = currentColumn - 1;
                                        currentEntranceDirection = EntranceDirection.right;
                                        seenCells.add(problemMap.matrix[currentRow][currentColumn]);
                                    }

                                } else {
                                    cost = cost + (FindDistance(currentRow, currentColumn) * 5);
                                    break;
                                }
                            } else {
                                if (currentCell.direction == 0) {
                                    if (currentRow + 1 >= n || seenCells.contains(problemMap.matrix[currentRow + 1][currentColumn])) {
                                        cost = cost + (FindDistance(currentRow, currentColumn) * 5);
                                        break;
                                    } else {
                                        cost = cost + currentCell.cost;
                                        currentRow = currentRow + 1;
                                        currentEntranceDirection = EntranceDirection.up;
                                        seenCells.add(problemMap.matrix[currentRow][currentColumn]);
                                    }

                                }
                                if (currentCell.direction == 1) {
                                    cost = cost + (FindDistance(currentRow, currentColumn) * 5);
                                    break;
                                }
                                if (currentCell.direction == 2) {
                                    if (currentRow - 1 < 0 || seenCells.contains(problemMap.matrix[currentRow - 1][currentColumn])) {
                                        cost = cost + (FindDistance(currentRow, currentColumn) * 5);
                                        break;
                                    } else {
                                        cost = cost + currentCell.cost;
                                        currentRow = currentRow - 1;
                                        currentEntranceDirection = EntranceDirection.down;
                                        seenCells.add(problemMap.matrix[currentRow][currentColumn]);
                                    }
                                }
                                if (currentCell.direction == 3) {
                                    cost = cost + (FindDistance(currentRow, currentColumn) * 5);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        return maxFitness - cost;

    }

    public static int FitnessStep2(Individual individual) {
        ArrayList<Cell> seenCells = new ArrayList<Cell>();
        Map problemMap = individual.map;
        int n = WaterDirect.n;
        maxFitness = (((n * n) - 2) * 5) + (2 * n * 5);
        int cost = 0;
        int currentRow = n - 2;
        int currentColumn = 0;
        EntranceDirection currentEntranceDirection = EntranceDirection.down;
        seenCells.add(problemMap.matrix[currentRow][currentColumn]);
        Cell currentCell;
        while (true) {
            currentCell = problemMap.matrix[currentRow][currentColumn];
            if (currentRow == 0 && currentColumn == n - 1) {
                break;
            }
            if (currentEntranceDirection == EntranceDirection.down) {
                if (currentCell.type == 2) {
                    if (currentCell.direction == 0) {
                        cost = cost + (FindDistance(currentRow, currentColumn) * 5);
                        break;
                    } else {
                        if (currentRow - 1 < 0 || seenCells.contains(problemMap.matrix[currentRow - 1][currentColumn])) {
                            cost = cost + (FindDistance(currentRow, currentColumn) * 5);
                            break;
                        } else {
                            cost = cost + currentCell.cost;
                            currentRow = currentRow - 1;
                            currentEntranceDirection = EntranceDirection.down;
                            seenCells.add(problemMap.matrix[currentRow][currentColumn]);
                        }
                    }
                } else {
                    if (currentCell.direction == 0) {
                        if (currentColumn + 1 >= n || seenCells.contains(problemMap.matrix[currentRow][currentColumn + 1])) {
                            cost = cost + (FindDistance(currentRow, currentColumn) * 5);
                            break;
                        } else {
                            cost = cost + currentCell.cost;
                            currentColumn = currentColumn + 1;
                            currentEntranceDirection = EntranceDirection.left;
                            seenCells.add(problemMap.matrix[currentRow][currentColumn]);
                        }
                    }
                    if (currentCell.direction == 1) {
                        cost = cost + (FindDistance(currentRow, currentColumn) * 5);
                        break;
                    }
                    if (currentCell.direction == 2) {
                        cost = cost + (FindDistance(currentRow, currentColumn) * 5);
                        break;
                    }
                    if (currentCell.direction == 3) {
                        if (currentColumn - 1 < 0 || seenCells.contains(problemMap.matrix[currentRow][currentColumn - 1])) {
                            cost = cost + (FindDistance(currentRow, currentColumn) * 5);
                            break;
                        } else {
                            cost = cost + currentCell.cost;
                            currentColumn = currentColumn - 1;
                            currentEntranceDirection = EntranceDirection.right;
                            seenCells.add(problemMap.matrix[currentRow][currentColumn]);
                        }
                    }
                }
            } else {
                if (currentEntranceDirection == EntranceDirection.up) {
                    if (currentCell.type == 2) {
                        if (currentCell.direction == 0) {
                            cost = cost + (FindDistance(currentRow, currentColumn) * 5);
                            break;
                        } else {
                            if ((currentRow == n - 2 && currentColumn == 0) || currentRow + 1 >= n || seenCells.contains(problemMap.matrix[currentRow + 1][currentColumn])) {
                                cost = cost + (FindDistance(currentRow, currentColumn) * 5);
                                break;
                            } else {
                                cost = cost + currentCell.cost;
                                currentRow = currentRow + 1;
                                currentEntranceDirection = EntranceDirection.up;
                                seenCells.add(problemMap.matrix[currentRow][currentColumn]);
                            }
                        }
                    } else {
                        if (currentCell.direction == 0) {
                            cost = cost + (FindDistance(currentRow, currentColumn) * 5);
                            break;
                        }
                        if (currentCell.direction == 1) {
                            if ((currentRow == n - 1 && currentColumn == 1) || currentColumn - 1 < 0 || seenCells.contains(problemMap.matrix[currentRow][currentColumn - 1])) {
                                cost = cost + (FindDistance(currentRow, currentColumn) * 5);
                                break;
                            } else {
                                cost = cost + currentCell.cost;
                                currentColumn = currentColumn - 1;
                                currentEntranceDirection = EntranceDirection.right;
                                seenCells.add(problemMap.matrix[currentRow][currentColumn]);
                            }
                        }
                        if (currentCell.direction == 2) {
                            if (currentColumn + 1 >= n || seenCells.contains(problemMap.matrix[currentRow][currentColumn + 1])) {
                                cost = cost + (FindDistance(currentRow, currentColumn) * 5);
                                break;
                            } else {
                                cost = cost + currentCell.cost;
                                currentColumn = currentColumn + 1;
                                currentEntranceDirection = EntranceDirection.left;
                                seenCells.add(problemMap.matrix[currentRow][currentColumn]);
                            }
                        }
                        if (currentCell.direction == 3) {
                            cost = cost + (FindDistance(currentRow, currentColumn) * 5);
                            break;
                        }
                    }
                } else {
                    if (currentEntranceDirection == EntranceDirection.left) {
                        if (currentCell.type == 2) {
                            if (currentCell.direction == 0) {
                                if (currentColumn + 1 >= n || seenCells.contains(problemMap.matrix[currentRow][currentColumn + 1])) {
                                    cost = cost + (FindDistance(currentRow, currentColumn) * 5);
                                    break;
                                } else {
                                    cost = cost + currentCell.cost;
                                    currentColumn = currentColumn + 1;
                                    currentEntranceDirection = EntranceDirection.left;
                                    seenCells.add(problemMap.matrix[currentRow][currentColumn]);
                                }

                            } else {
                                cost = cost + (FindDistance(currentRow, currentColumn) * 5);
                                break;
                            }
                        } else {
                            if (currentCell.direction == 0) {
                                cost = cost + (FindDistance(currentRow, currentColumn) * 5);
                                break;
                            }
                            if (currentCell.direction == 1) {
                                if (currentRow - 1 < 0 || seenCells.contains(problemMap.matrix[currentRow - 1][currentColumn])) {
                                    cost = cost + (FindDistance(currentRow, currentColumn) * 5);
                                    break;
                                } else {
                                    cost = cost + currentCell.cost;
                                    currentRow = currentRow - 1;
                                    currentEntranceDirection = EntranceDirection.down;
                                    seenCells.add(problemMap.matrix[currentRow][currentColumn]);
                                }
                            }
                            if (currentCell.direction == 2) {
                                cost = cost + (FindDistance(currentRow, currentColumn) * 5);
                                break;
                            }
                            if (currentCell.direction == 3) {
                                if (currentRow + 1 >= n || seenCells.contains(problemMap.matrix[currentRow + 1][currentColumn])) {
                                    cost = cost + (FindDistance(currentRow, currentColumn) * 5);
                                    break;
                                } else {
                                    cost = cost + currentCell.cost;
                                    currentRow = currentRow + 1;
                                    currentEntranceDirection = EntranceDirection.up;
                                    seenCells.add(problemMap.matrix[currentRow][currentColumn]);
                                }
                            }
                        }
                    } else {
                        if (currentEntranceDirection == EntranceDirection.right) {
                            if (currentCell.type == 2) {
                                if (currentCell.direction == 0) {
                                    if ((currentRow == n - 1 && currentColumn == 1) || currentColumn - 1 < 0 || seenCells.contains(problemMap.matrix[currentRow][currentColumn - 1])) {
                                        cost = cost + (FindDistance(currentRow, currentColumn) * 5);
                                        break;
                                    } else {
                                        cost = cost + currentCell.cost;
                                        currentColumn = currentColumn - 1;
                                        currentEntranceDirection = EntranceDirection.right;
                                        seenCells.add(problemMap.matrix[currentRow][currentColumn]);
                                    }

                                } else {
                                    cost = cost + (FindDistance(currentRow, currentColumn) * 5);
                                    break;
                                }
                            } else {
                                if (currentCell.direction == 0) {
                                    if (currentRow + 1 >= n || seenCells.contains(problemMap.matrix[currentRow + 1][currentColumn])) {
                                        cost = cost + (FindDistance(currentRow, currentColumn) * 5);
                                        break;
                                    } else {
                                        cost = cost + currentCell.cost;
                                        currentRow = currentRow + 1;
                                        currentEntranceDirection = EntranceDirection.up;
                                        seenCells.add(problemMap.matrix[currentRow][currentColumn]);
                                    }

                                }
                                if (currentCell.direction == 1) {
                                    cost = cost + (FindDistance(currentRow, currentColumn) * 5);
                                    break;
                                }
                                if (currentCell.direction == 2) {
                                    if (currentRow - 1 < 0 || seenCells.contains(problemMap.matrix[currentRow - 1][currentColumn])) {
                                        cost = cost + (FindDistance(currentRow, currentColumn) * 5);
                                        break;
                                    } else {
                                        cost = cost + currentCell.cost;
                                        currentRow = currentRow - 1;
                                        currentEntranceDirection = EntranceDirection.down;
                                        seenCells.add(problemMap.matrix[currentRow][currentColumn]);
                                    }
                                }
                                if (currentCell.direction == 3) {
                                    cost = cost + (FindDistance(currentRow, currentColumn) * 5);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        return maxFitness - cost;

    }

    public static int FindDistance(int i, int j) {
        PopulationMember.i = i;
        PopulationMember.j = j;
        int n = WaterDirect.n;
        int result;
        result = i + (n - 1 - j);
        return result;
    }

    public static Individual FitnessBasedRandomSelection(Population population) {
        Random rn = new Random();
        int totalFitness = population.totalFitness;
        int random = rn.nextInt(totalFitness) + 1;
        for (int i = 0; i < population.array.size(); i++) {
            PopulationMember populationMember = population.array.get(i);
            if (random <= populationMember.selectionProbability) {
                return populationMember.individual;
            }
        }
        System.out.println("error in FitnessBasedRandomSelection");
        System.exit(0);
        return null;
    }
}
