package simpledb;

import java.util.*;

/**
 * The Join operator implements the relational join operation.
 */
public class Join extends Operator {

    private static final long serialVersionUID = 1L;
    
    private JoinPredicate _p;
    private DbIterator _child1;
    private DbIterator _child2;
    private Tuple CurrentTuple2 = null;

    /**
     * Constructor. Accepts to children to join and the predicate to join them
     * on
     * 
     * @param p
     *            The predicate to use to join the children
     * @param child1
     *            Iterator for the left(outer) relation to join
     * @param child2
     *            Iterator for the right(inner) relation to join
     */
    public Join(JoinPredicate p, DbIterator child1, DbIterator child2) {
        // some code goes here
    	_p = p;
    	_child1 = child1;
    	_child2 = child2;
    }

    public JoinPredicate getJoinPredicate() {
        // some code goes here
        return _p;
    }

    /**
     * @return
     *       the field name of join field1. Should be quantified by
     *       alias or table name.
     * */
    public String getJoinField1Name() {
        // some code goes here
        return _child1.getTupleDesc().getFieldName(_p.getField1());
    }

    /**
     * @return
     *       the field name of join field2. Should be quantified by
     *       alias or table name.
     * */
    public String getJoinField2Name() {
        // some code goes here
        return _child2.getTupleDesc().getFieldName(_p.getField2());
    }

    /**
     * @see simpledb.TupleDesc#merge(TupleDesc, TupleDesc) for possible
     *      implementation logic.
     */
    public TupleDesc getTupleDesc() {
        // some code goes here
        return TupleDesc.merge(_child1.getTupleDesc(), _child2.getTupleDesc());
    }

    public void open() throws DbException, NoSuchElementException,
            TransactionAbortedException {
        // some code goes here
    	_child1.open();
    	_child2.open();
    	super.open();
    	CurrentTuple2 = null;
    }

    public void close() {
        // some code goes here
    	_child1.close();
    	_child2.close();
    	super.close();
    	CurrentTuple2 = null;
    }

    public void rewind() throws DbException, TransactionAbortedException {
        // some code goes here
    	_child1.rewind();
    	_child2.rewind();
    	CurrentTuple2 = null;
    }

    /**
     * Returns the next tuple generated by the join, or null if there are no
     * more tuples. Logically, this is the next tuple in r1 cross r2 that
     * satisfies the join predicate. There are many possible implementations;
     * the simplest is a nested loops join.
     * <p>
     * Note that the tuples returned from this particular implementation of Join
     * are simply the concatenation of joining tuples from the left and right
     * relation. Therefore, if an equality predicate is used there will be two
     * copies of the join attribute in the results. (Removing such duplicate
     * columns can be done with an additional projection operator if needed.)
     * <p>
     * For example, if one tuple is {1,2,3} and the other tuple is {1,5,6},
     * joined on equality of the first column, then this returns {1,2,3,1,5,6}.
     * 
     * @return The next matching tuple.
     * @see JoinPredicate#filter
     */
    protected Tuple fetchNext() throws TransactionAbortedException, DbException {
        // some code goes here
    	
    	Tuple tuple1 = null;
    	boolean FirstTime = true;
    	while(_child2.hasNext() || _child1.hasNext())
    	{
    		if(CurrentTuple2 == null)
    			CurrentTuple2 = _child2.next();
    		if(FirstTime)	
    			FirstTime = false;
    		else
    			CurrentTuple2 = _child2.next(); 
    		
    		while(_child1.hasNext())
    		{
    			tuple1 = _child1.next();
    			if(_p.filter(tuple1, CurrentTuple2))
    			{
    				TupleDesc _TupleDesc = getTupleDesc(); 
    				Tuple result = new Tuple(_TupleDesc);
    				int TupleLength1 = _child1.getTupleDesc().numFields();
    				for(int i=0; i<TupleLength1; i++)
    					result.setField(i, tuple1.getField(i));
    				for(int i=0; i<_child2.getTupleDesc().numFields();i++)
    					result.setField(i+TupleLength1, CurrentTuple2.getField(i));
    				return result;
    			}
    		}
    		if(_child2.hasNext())
    			_child1.rewind();
    	}
        return null;
    }

    @Override
    public DbIterator[] getChildren() {
        // some code goes here
    	DbIterator[] result = {_child1, _child2};
        return result;
    }

    @Override
    public void setChildren(DbIterator[] children) {
        // some code goes here
    	_child1 = children[0];
    	_child2 = children[1];
    }

}