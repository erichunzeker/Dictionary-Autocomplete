public class DLBNode
{
    public char value;
    public DLBNode rightSib;
    public DLBNode child;

    public DLBNode()
    {

    }

    public DLBNode(char value)
    {
        this(value, null, null);
    }

    public DLBNode(char value, DLBNode rightSib, DLBNode child)
    {
        this.value = value;
        this.rightSib = rightSib;
        this.child = child;
    }
}