package galles;

public abstract class ElemIterator implements java.util.Iterator<MatrixElem>
{
    public abstract boolean iteratingRow();
    public abstract boolean iteratingCol();
    public abstract int nonIteratingIndex();
    public abstract MatrixElem next();
    public abstract boolean hasNext();
    public void remove()
    {
        throw new UnsupportedOperationException();
    }
}
