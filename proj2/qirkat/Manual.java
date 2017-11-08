package qirkat;

import static qirkat.PieceColor.*;
import static qirkat.Command.Type.*;

/** A Player that receives its moves from its Game's getMoveCmnd method.
 *  @author Jeff Xiang
 */
class Manual extends Player {

    /** A Player that will play MYCOLOR on GAME, taking its moves from
     *  GAME. */
    Manual(Game game, PieceColor myColor) {
        super(game, myColor);
        _prompt = myColor + ": ";
    }

    @Override
    Move myMove() {
        Game mygame = this.game();
        Command mycommand = mygame.getMoveCmnd(_prompt);
        System.out.println(mycommand);
        if (mycommand.commandType() == PIECEMOVE) {
            return Move.parseMove(mycommand.operands()[0]);
        }
        return null;
    }

    @Override
    /** Returns true because I am manual. */
    boolean isManual() {
        return true;
    }

    /** Identifies the player serving as a source of input commands. */
    private String _prompt;
}

