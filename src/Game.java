import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * UNO Game
 */
public class Game {
    private static final int numberOfCardsForEachPlayer = 7;
    private Table table;

    /**
     * Star a new Game
     */
    public Game(int numberOfPlayers) {
        table = new Table();
        divideCards();
    }

    /**
     * Put a card on
     *
     * @param cardToPut card to put
     * @param putterPlayer who want to put a card
     * @return Is card putted
     */
    public boolean putCard(Card cardToPut, Player putterPlayer) {
        if(checkPuttedCard(cardToPut, putterPlayer)) {
            cardToPut.ability();
            if(table.getMiddleCard().getSign() == Sign.wildColor || table.getMiddleCard().getSign() == Sign.wildDraw)
                table.getMiddleCard().design = table.getMiddleCard().colorToPaint(table.getMiddleCard().getColor()) +  Card.BORDER + "\n" + table.getMiddleCard().signToShape(table.getMiddleCard().getSign()) + "\n" + Card.BORDER + Card.RESET;;
            table.getCards().add(table.getMiddleCard());
            table.setMiddleCard(cardToPut);
            putterPlayer.getCards().remove(cardToPut);
            return true;
        }
        else {
            if(!putterPlayer.isSystem()) {
                System.out.println("You Can't Put This Card");
            }
            return false;
        }

    }

    /**
     * Check if you can put the selected card.
     *
     * @param cardToPut card to put on.
     * @return can put or not.
     */
    private boolean checkPuttedCard(Card cardToPut, Player putterPlayer) {
        Card middleCard = table.getMiddleCard();
        if(putterPlayer.getHaveToPut() != null){
            if(cardToPut.getSign() == putterPlayer.getHaveToPut()){
                putterPlayer.setHaveToPut(null);
                return true;
            } else {
                return false;
            }
        }
        if(cardToPut.getSign() == Sign.wildColor)
            return true;
        if(middleCard.getSign() == cardToPut.getSign())
            return true;
        if(middleCard.getColor() == cardToPut.getColor())
            return true;
        if(cardToPut.getSign() == Sign.wildDraw)
            return checkWildDrawPutting(middleCard, putterPlayer);
        return false;
    }

    /**
     * Check if you can put another card instead of WildDraw Card , you can't put it on.
     *
     * @param middleCard the middle cart of game
     * @param putterPlayer who want to put a card.
     * @return can put or not.
     */
    private boolean checkWildDrawPutting(Card middleCard, Player putterPlayer){
        for (Card cardToCheck:putterPlayer.getCards()) {
            if(cardToCheck.getSign() != Sign.wildDraw){
                if(checkPuttedCard(cardToCheck, putterPlayer))
                    return false;
            }
        }
        return true;
    }

    /**
     * Take a card from table
     *
     * @param player who want to take a card
     */
    public void takeCard(Player player) {
        Card aCard = selectRandomCard(table.getCards());
        player.getCards().add(aCard);
        table.getCards().remove(aCard);
    }

    /**
     * Select a Random Card from the given Card Source
     *
     * @param cardSource card source
     * @return selected card
     */
    private Card selectRandomCard (ArrayList<Card> cardSource){
        return cardSource.get(ThreadLocalRandom.current().nextInt(0 ,  cardSource.size()));
    }

    /**
     * divide cards for players at first of game
     */
    public void divideCards() {
        for (Player player : table.getPlayers()) {
            for (int i = 0; i < numberOfCardsForEachPlayer; i++) {
                takeCard(player);
            }
        }
        Card middleCard;
        do {
            middleCard = selectRandomCard(table.getCards());
        } while(!checkFirstMiddleCard(middleCard));
        table.setMiddleCard(middleCard);
        middleCard.ability();
    }

    /**
     * Check for first middle Card
     *
     * @param cardToPutFirst first card to put
     * @return can put or not.
     */
    private boolean checkFirstMiddleCard(Card cardToPutFirst) {
        return cardToPutFirst.getSign() != Sign.wildDraw && cardToPutFirst.getSign() != Sign.wildColor && cardToPutFirst.getSign() != Sign.draw;
    }

    /**
     * Show score of players when game ends
     */
    public void playersScore(){
        int i = 1;
        for (Player player:getTable().getPlayers()){
            int playerScore = 0;
            for (Card card:player.getCards()){
                playerScore += card.getPoint();
            }
            System.out.println("Score of Player " + i++ + " is : " + playerScore);
        }
    }

    /**
     * AI Put a Card
     *
     * @param player player turn
     */
    public void systemPlay(Player player) {
        for (Card card:player.getCards()){
            if(putCard(card, player)){
                return;
            }
        }
        takeCard(player);
        putCard(player.getCards().get(player.getCards().size()-1), player);
    }

    /**
     * Check End of Game
     *
     * @return is game end
     */
    public boolean endGame() {
        for (Player player:table.getPlayers()) {
            if (player.getCards().size() == 0) {
                return true;
            }
        }
        return false;
    }

    public Table getTable() {
        return table;
    }
}
