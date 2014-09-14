package hex;

/**
 *
 * @author Ljubo Raicevic <rljubo90@gmail.com>
 */
public class MCSimulationMove {
    private final Coordinate coordinate;
    private final Double probability;

    public MCSimulationMove(Coordinate coordinate, Double simulationsWon) {
        this.coordinate = coordinate;
        this.probability = simulationsWon;
    }

    public Coordinate getCoordinates() {
        return coordinate;
    }

    public Double getProbability() {
        return probability;
    }
}
