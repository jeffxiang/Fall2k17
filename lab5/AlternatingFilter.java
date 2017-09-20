import java.util.Iterator;
import utils.Filter;

/** A kind of Filter that lets through every other VALUE element of
 *  its input sequence, starting with the first.
 *  @author Jeff Xiang
 */
class AlternatingFilter<Value> extends Filter<Value> {
    private boolean _valid;
    /** A filter of values from INPUT that lets through every other
     *  value. */
    AlternatingFilter(Iterator<Value> input) {
        super(input);
        _valid = false;

    }

    @Override
    protected boolean keep() {
        this._valid = !this._valid;
        return this._valid;
    }

}
