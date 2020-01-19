/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package waterdirect;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import static waterdirect.GeneticAlgorithm.FindDistance;

/**
 *
 * @author arman
 */
public class WaterDirect {

    public static int flag1 = 0;
    public static int flag2 = 0;
    public static int n;
    public static Map map;
    static int cost1 = -1;
    static int cost2 = -1;
    static float averageCost;
    static int near1;
    static int near2;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        Scanner s = new Scanner(System.in);
        System.out.println("enter the address of input file : ");
        String path = s.next();
        loadFromFile(path);
        Population.populationSize = 20000;
        int mutationRate = 10;
        int GenerationNumber = 25;
        System.out.println("population Size :" + Population.populationSize);
        System.out.println("mutation rate :" + mutationRate);
        System.out.println("Generation number :" + GenerationNumber);
        Individual result = GeneticAlgorithm.GeneticAlgorithm(GeneticAlgorithm.initializePopulation(), GenerationNumber, mutationRate);
        PrintPath(GetPath(result));
    }

    public static Map loadFromFile(String path) throws FileNotFoundException, IOException {
        float totalCost = 0;
        FileReader fr = new FileReader(path);
        BufferedReader br = new BufferedReader(fr);
        String number = br.readLine();
        String numberArray[] = number.split(" ");
        int N = Integer.valueOf(numberArray[2]);
        Cell[][] matrix = new Cell[N][N];
        String line;
        String[] lineArray;
        for (int i = 0; i <= N - 1; i++) {
            line = br.readLine();
            while (line.equals("")) {
                line = br.readLine();
            }
            lineArray = line.split(" ");
            for (int j = 0; j <= N - 1; j++) {
                if ((i == 0 && j == N - 1) || (i == N - 1 && j == 0)) {
                    matrix[i][j] = new Cell(-1, -1, -1);
                } else {
                    matrix[i][j] = new Cell(Integer.valueOf(String.valueOf(lineArray[j].charAt(1))), Integer.valueOf(String.valueOf(lineArray[j].charAt(3))), Integer.valueOf(String.valueOf(lineArray[j].charAt(5))));
                    totalCost = totalCost + matrix[i][j].cost;
                }
            }
        }
        float totalnumber = n;
        averageCost = totalCost / totalnumber;
        map = new Map(matrix);
        n = N;
        return map;
    }

    public static void PrintPath(ArrayList<Cell> path) {
        int cost = 0;
        System.out.println("NearOptimal path from A to B : ");
        for (int i = 0; i < path.size(); i++) {
            cost = cost + path.get(i).cost;
            System.out.print("( " + path.get(i).type + "," + path.get(i).direction + "," + path.get(i).cost + ") ");
        }
        System.out.println("cost : " + cost);
    }

    public static ArrayList<Cell> GetPath(Individual individual) {
        ArrayList<Cell> startFromLeft = GetPathStep1(individual);
        ArrayList<Cell> startFromUp = GetPathStep2(individual);
        if (flag1 == 1 && flag2 == 1) {
            if (cost1 < cost2) {
                return startFromLeft;
            } else {
                return startFromUp;
            }
        }
        if (flag1 == 1) {
            return startFromLeft;
        }
        if (flag2 == 1) {
            return startFromUp;
        }
        if (near1 < near2) {
            return startFromLeft;
        }
        return startFromUp;
    }

    public static ArrayList<Cell> GetPathStep1(Individual individual) {
        ArrayList<Cell> seenCells = new ArrayList<Cell>();
        Map problemMap = individual.map;
        int n = WaterDirect.n;
        int maxFitness = (((n * n) - 2) * 5) + (2 * n * 5);
        int cost = 0;
        int currentRow = n - 1;
        int currentColumn = 1;
        EntranceDirection currentEntranceDirection = EntranceDirection.left;
        seenCells.add(problemMap.matrix[currentRow][currentColumn]);
        Cell currentCell;
        while (true) {
            currentCell = problemMap.matrix[currentRow][currentColumn];
            if (currentRow == 0 && currentColumn == n - 1) {
                cost1 = cost;
                seenCells.remove(seenCells.size() - 1);
                flag1 = 1;
                return seenCells;
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
        flag1 = 0;
        cost1 = cost;
        near1 = FindDistance(currentRow, currentColumn);
        return seenCells;
    }

    public static ArrayList<Cell> GetPathStep2(Individual individual) {
        ArrayList<Cell> seenCells = new ArrayList<Cell>();
        Map problemMap = individual.map;
        int n = WaterDirect.n;
        int maxFitness = (((n * n) - 2) * 5) + (2 * n * 5);
        int cost = 0;
        int currentRow = n - 2;
        int currentColumn = 0;
        EntranceDirection currentEntranceDirection = EntranceDirection.down;
        seenCells.add(problemMap.matrix[currentRow][currentColumn]);
        Cell currentCell;
        while (true) {
            currentCell = problemMap.matrix[currentRow][currentColumn];
            if (currentRow == 0 && currentColumn == n - 1) {
                seenCells.remove(seenCells.size() - 1);
                cost2 = cost;
                flag2 = 1;
                return seenCells;
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
        near2 = FindDistance(currentRow, currentColumn);
        cost2 = cost;
        flag2 = 0;
        return seenCells;
    }

}
