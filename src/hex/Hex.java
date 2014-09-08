package hex;

/**
 *
 * @author Ljubo Raicevic <rljubo90@gmail.com>
 */
public class Hex {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Table table = new Table(7);
        Player first = new PlayerHuman();
        Player second = new PlayerHuman();
        
        Game g = new Game(table, first, second);
        g.play();
    }
    
}
