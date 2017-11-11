import org.junit.Test;
import static org.junit.Assert.*;

public class RBTTest {

    @Test
    public void testBuildRedBlackTree() {
        BTree testBTree = new BTree();
        testBTree.root = new BTree.TwoThreeFourNode(2, 4, 8);
        BTree.TwoThreeFourNode child0 = new BTree.TwoThreeFourNode(0, 1);
        BTree.TwoThreeFourNode child1 = new BTree.TwoThreeFourNode(3);
        BTree.TwoThreeFourNode child2 = new BTree.TwoThreeFourNode(5, 6, 7);
        BTree.TwoThreeFourNode child3 = new BTree.TwoThreeFourNode(9, 10);
        testBTree.root.setChildAt(0, child0);
        testBTree.root.setChildAt(1, child1);
        testBTree.root.setChildAt(2, child2);
        testBTree.root.setChildAt(3, child3);
        RedBlackTree rbtesttree = new RedBlackTree(testBTree);
        RedBlackTree rbtesttree2 = new RedBlackTree();
        rbtesttree2.insert(0);
        rbtesttree2.insert(1);
        rbtesttree2.insert(2);
        rbtesttree2.insert(3);
        rbtesttree2.insert(4);
        rbtesttree2.insert(6);
        rbtesttree2.insert(7);
        rbtesttree2.insert(8);
        rbtesttree2.insert(5);
    }
}
