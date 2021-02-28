package main.java.ga;

@FunctionalInterface
public interface Mutation {

    Chromosome mutate(Chromosome chromosome);

}
