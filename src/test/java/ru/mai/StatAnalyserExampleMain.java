package ru.mai;

import com.github.javaparser.ParseProblemException;
import mai.student.CodeComparer;
import mai.student.tokenizers.java17.JavaTokenizer;
import mai.student.tokenizers.java17.MissingTypeException;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

public class StatAnalyserExampleMain {

    private static final String TYPE_ARRAY = "Array";
    private static final int LOG_FILE_SIZE = 1024 * 1024 * 256;

    public static void main(String[] args) {

        class StatisticCollector {
            public String file1;
            public String file2;
            public long size1;
            public long size2;
            public double result;

            public StatisticCollector() {
                file1 = "";
                file2 = "";
                result = 0;
            }

            public StatisticCollector(String file1, String file2, long size1, long size2, double result) {
                this.file1 = file1;
                this.file2 = file2;
                this.size1 = size1;
                this.size2 = size2;
                this.result = result;
            }
        }

        class StaticComparator implements Comparator<StatisticCollector> {
            public int compare(StatisticCollector stat1, StatisticCollector stat2) {
                return (int) (stat1.result * 1000000 - stat2.result * 1000000);
            }
        }

        class ExerciseStat {
            public String ex;
            public double total;
            public int clones;
            public double per;
            public double avrSize;

            public ExerciseStat(String ex, double total, int clones, double per, double avrSize) {
                this.ex = ex;
                this.total = total;
                this.clones = clones;
                this.per = per;
                this.avrSize = avrSize;
            }
        }

        class ExerciseComparator implements Comparator<ExerciseStat> {
            public int compare(ExerciseStat stat1, ExerciseStat stat2) {
                return (int) (stat1.avrSize * 1000000 - stat2.avrSize * 1000000);
            }
        }

        class ExerciseComparatorPer implements Comparator<ExerciseStat> {
            public int compare(ExerciseStat stat1, ExerciseStat stat2) {
                return (int) (stat1.per * 1000000 - stat2.per * 1000000);
            }
        }

        class GroupStatistic {
            public String group;
            public double total;
            public int clones;
            public double per;

            public GroupStatistic(String group, double total, int clones, double per) {
                this.group = group;
                this.total = total;
                this.clones = clones;
                this.per = per;
            }
        }

        File dir = new File("..\\..\\..\\Бакалавр\\ДИПЛОМ\\diplomTesting\\Определение порога");

        if (dir.isDirectory()) {
            try {
//                FileHandler fh = new FileHandler("CodeComparerLogger.txt", LOG_FILE_SIZE, 5, true);
                CodeComparer.setLoggerHandler(null);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }


//            double min = 1;
//
//            for (File f1 : dir.listFiles()) {
//                for (File f2: dir.listFiles()) {
//                    if (!f1.isFile() || !f2.isFile() || f1.equals(f2)) {
//                        continue;
//                    }
//
//                    ArrayList<Path> paths1 = new ArrayList<>();
//                    paths1.add(f1.toPath());
//
//                    ArrayList<Path> paths2 = new ArrayList<>();
//                    paths2.add(f2.toPath());
//
//                    CodeComparer codeComparer = new CodeComparer(paths1, paths2);
//                    codeComparer.compare();
//                    System.out.println(f1.getName() + " и " + f2.getName() + " " + codeComparer.getResult());
//
//                    if (min > codeComparer.getResult() && codeComparer.getResult() > 0) {
//                        min = codeComparer.getResult();
//                    }
//                }
//            }
//
//            System.out.println("Минимальная уникальность " + min);


//            // Сбор статистике по работам за 2021
//            HashMap<String, ArrayList<StatisticCollector>> statistic = new HashMap<>();
//

//            // TODO: run through all works
            int parseProblemCounter = 0;
            for (File f1 : dir.listFiles()) {
                String name1 = f1.getName().split("_")[0];
                String assignment1 = f1.getName().split("_")[1];

                if (f1.isDirectory() || f1.getName().endsWith(".java_errorReport.txt") ||
                        f1.getName().endsWith(".zip_errorReport.txt")) {
                    continue;
                }

//                if (!statistic.containsKey(assignment1)) {
//                    statistic.put(assignment1, new ArrayList<>());
//                }

                for (File f2 : dir.listFiles()) {
                    String name2 = f2.getName().split("_")[0];
                    String assignment2 = f2.getName().split("_")[1];

                    if (f2.isDirectory() || f2.getName().endsWith(".java_errorReport.txt") ||
                            !assignment1.equals(assignment2) || f2.getName().endsWith(".zip_errorReport.txt")) {
                        continue;
                    }

                    System.out.println(name1 + name2 + "\n" + assignment1);
                    try {
                        CodeComparer codeComparer = new CodeComparer();
                        codeComparer.setFirstProgram(f1.toPath());
                        codeComparer.setSecondProgram(f2.toPath());
                        System.out.println("!!!! TIME\n\tPreprocessing: " + JavaTokenizer.totalPreprocessingTime +
                                "\n\tTokenizing: " + JavaTokenizer.totalTokenizingTime);
                        codeComparer.compare();
                        System.out.println(codeComparer.getResult());
//                        System.out.println(codeComparer.isCheckPassed());


//                        statistic.get(assignment1).add(new StatisticCollector(name1, name2,
//                                (new File(paths1.get(0).toString())).length(),
//                                (new File(paths2.get(0).toString())).length(), codeComparer.getResult()));
                    } catch (Exception e) {
                        if (e instanceof ParseProblemException) {
                            parseProblemCounter += 1;
                        } else if (!(e instanceof MissingTypeException)) {
                            System.out.println(e.getMessage());
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
            System.out.println("Parse problems: " + parseProblemCounter);
//            // TODO: end

//
//            // Подсчет среднего размера задания
//            double[] averageSize = new double[statistic.size()];
//            int sizeIndex = 0;
//            for (Map.Entry stat : statistic.entrySet()) {
//
//                double total = 0;
//                int counter = 0;
//                for (StatisticCollector s : (ArrayList<StatisticCollector>) stat.getValue()) {
//                    total += s.size1 + s.size2;
//                    counter += 2;
//                }
//
//                averageSize[sizeIndex] = total / counter;
//                ++sizeIndex;
//            }
//
//            // ...
//            int counter = 0;
//            ArrayList<HashSet<String>> clones = new ArrayList<>();
//            int index = 0;
//            for (Map.Entry stat : statistic.entrySet()) {
//                clones.add(new HashSet<>());
//
//                // Запись статистики
//                try(FileWriter writer = new FileWriter("Статистика/" + stat.getKey() + "_stat.txt", false)) {
//                    ArrayList<StatisticCollector> data = (ArrayList<StatisticCollector>) stat.getValue();
//                    Collections.sort(data, new StaticComparator());
//
//                    for (StatisticCollector s : data) {
//                        writer.write(s.file1 + "\t" + s.file2 + "\t" + s.result + "\n");
//                    }
//                    writer.flush();
//                } catch (Exception e) {
//                    System.out.println(e.getMessage());
//                }
//
//                // Подсчет плагиата
//                double THRESGOLD = 0.0000000000000000000000000001;
//                ArrayList<StatisticCollector> data = (ArrayList<StatisticCollector>) stat.getValue();
//                Collections.sort(data, new StaticComparator());
//
//                for (StatisticCollector s : data) {
//                    if (s.file1.equals(s.file2) || s.result >= THRESGOLD) {
//                        continue;
//                    }
////                    System.out.println(s.file1 + " " + s.file2);
//                    clones.get(index).add(s.file1);
//                    clones.get(index).add(s.file2);
////                    System.out.println(clones.get(index).size());
//                }
//                ++index;
//            }
//
////            for (HashSet<String> var : clones) {
//////                for (String str : var) {
//////                    System.out.print(str + " ");
//////                }
//////                System.out.println("\n");
////                counter += var.size();
////            }
//
//            // Подсчет статистики по упражнениям
//            ArrayList<ExerciseStat> exerciseStats = new ArrayList<>();
//            int i = 0;
//            for (Map.Entry stat : statistic.entrySet()) {
//                double total = Math.ceil(Math.sqrt(((ArrayList<StatisticCollector>) stat.getValue()).size()));
//                counter += total;
//                exerciseStats.add(new ExerciseStat((String) stat.getKey(), total, clones.get(i).size(),
//                        (clones.get(i).size() / total), averageSize[i]));
////                System.out.println(stat.getKey() + "\ttotal: " + total + "\tclones: " + clones.get(i).size() + "\t%: " +
////                        (clones.get(i).size() / total) + "\tavrSize: " + averageSize[i]);
//                ++i;
//            }
//
//            Collections.sort(exerciseStats, new ExerciseComparatorPer());
////            Collections.sort(exerciseStats, new ExerciseComparator());
//            // Print ExStat
////            for (ExerciseStat ex : exerciseStats) {
////                System.out.println(ex.ex + "\ttotal: " + ex.total + "\tclones: " + ex.clones + "\t%: " +
////                        ex.per + "\tavrSize: " + ex.avrSize);
////            }
//
//            // Группировака статистики по размеру
////            ArrayList<GroupStatistic> groupStatistics = new ArrayList<>();
////            long startSize = 0;
////            long step = 500;
////
////            double total = 0;
////            int clonesAmount = 0;
////            for (int j = 0; j < exerciseStats.size(); ++j) {
////                if (exerciseStats.get(j).avrSize >= startSize && exerciseStats.get(j).avrSize < startSize + step) {
////                  total += exerciseStats.get(j).total;
////                  clonesAmount += exerciseStats.get(j).clones;
////                }  else {
////                    if (total != 0) {
////                        groupStatistics.add(new GroupStatistic(startSize + "-" + (startSize + step),
////                                total, clonesAmount, clonesAmount / total));
////                        total = 0;
////                        clonesAmount = 0;
////                    }
////
////                    startSize += step;
////                    --j;
////                }
////            }
////            if (total != 0) {
////                groupStatistics.add(new GroupStatistic(startSize + "-" + (startSize + step),
////                        total, clonesAmount, clonesAmount / total));
////            }
//
//            // Группировака статистики по доли заимствования
//            ArrayList<GroupStatistic> groupStatistics = new ArrayList<>();
//            ArrayList<ArrayList<ExerciseStat>> listOfEx = new ArrayList<>();
//            double startPer = 0;
//            double step = 0.05;
//
//            ArrayList<ExerciseStat> temp = new ArrayList<>();
//
//            System.out.println(exerciseStats.size());
//            double total = 0;
//            int clonesAmount = 0;
//            for (int j = 0; j < exerciseStats.size(); ++j) {
//                if (exerciseStats.get(j).per >= startPer && exerciseStats.get(j).per < startPer + step) {
//                    total += exerciseStats.get(j).total;
//                    clonesAmount += exerciseStats.get(j).clones;
//                    temp.add(exerciseStats.get(j));
//                }  else {
//                    if (total != 0) {
//                        groupStatistics.add(new GroupStatistic(startPer + "-" + (startPer + step),
//                                Math.ceil(total), clonesAmount, clonesAmount / total));
//                        listOfEx.add(temp);
//
//                        temp = new ArrayList<>();
//
//                        total = 0;
//                        clonesAmount = 0;
//                    }
//
//                    startPer += step;
//                    --j;
//                }
//            }
//            if (total != 0) {
//                groupStatistics.add(new GroupStatistic(startPer + "-" + (startPer + step),
//                        Math.ceil(total), clonesAmount, clonesAmount / total));
//                listOfEx.add(temp);
//            }
//
//            // Print Group stat
//            int groupInd = 0;
//            for (GroupStatistic gStat : groupStatistics) {
//                if (gStat.total == 0) {
//                    continue;
//                }
//                System.out.println(gStat.group + "\ttotal: " + gStat.total + "\tclones: " + gStat.clones + "\t%: " +
//                        gStat.per);
//
//                for (ExerciseStat exS : listOfEx.get(groupInd)) {
//                    System.out.println("\t\t" + exS.ex + " : " + exS.avrSize);
//                }
//                ++groupInd;
//            }
//            System.out.println(counter);


            // TODO: check my files
//            String myTest = "testfiles/ru/mai/";
//            try {
//                ArrayList<Path> paths1 = new ArrayList<>();
//                paths1.add(Path.of(myTest + "file1.java"));
//                paths1.add(Path.of(myTest + "file2.java"));
//                paths1.add(Path.of(myTest + "file3.java"));
//                paths1.add(Path.of(myTest + "file4.java"));
//                paths1.add(Path.of(myTest + "file5.java"));
//
//                ArrayList<Path> paths2 = new ArrayList<>();
//                paths2.add(Path.of(myTest + "file1.java"));
//                paths2.add(Path.of(myTest + "file2.java"));
//                paths2.add(Path.of(myTest + "file3.java"));
//                paths2.add(Path.of(myTest + "file4.java"));
//                paths2.add(Path.of(myTest + "file5.java"));
//
//                CodeComparer codeComparer = new CodeComparer(paths1, paths2);
//                System.out.println(JavaTokenizer.totalTime);
//
////                codeComparer.compare();
////                System.out.println(codeComparer.getResult() + " " + codeComparer.isCheckPassed());
//            } catch (Exception e) {
//                System.out.println(e);
//                throw new RuntimeException(e);
//            }
            // TODO: end my test

//            try {
//                ArrayList<Path> paths1 = new ArrayList<>();
//                paths1.add(Path.of("Тестовые файлы/multyFileExample/Test1.java"));
//
//                ArrayList<Path> paths2 = new ArrayList<>();
//                paths2.add(Path.of("Тестовые файлы/multyFileExample/Test2.java"));
//
//                CodeComparer codeComparer = new CodeComparer(paths1, paths2);
//                codeComparer.compare();
//                System.out.println(codeComparer.getResult());
//
//                paths1.clear();
//                paths1.add(Path.of("Тестовые файлы/multyFileExample/Test1.java"));
//
//                paths2.clear();
//                paths2.add(Path.of("Тестовые файлы/multyFileExample/Test3.java"));
//
//                codeComparer = new CodeComparer(paths1, paths2);
//                codeComparer.compare();
//                System.out.println(codeComparer.getResult());
//
//                paths1.clear();
//                paths1.add(Path.of("Тестовые файлы/multyFileExample/Test2.java"));
//
//                paths2.clear();
//                paths2.add(Path.of("Тестовые файлы/multyFileExample/Test3.java"));
//
//                codeComparer = new CodeComparer(paths1, paths2);
//                codeComparer.compare();
//                System.out.println(codeComparer.getResult());
//            } catch (Exception e) {
//                System.out.println(e.getMessage());
//            }

//            System.out.println(codeComparer.getResult());
        }
    }


}