class GenNode {
    private GenNode leftchild_;
    private GenNode rightsib_;
    private Object element_;


    public GenNode(Object element) {
	element_ = element;
    }

    public GenNode(Object element, GenNode leftchild, GenNode rightsib) {
	element_ = element;
	leftchild_ = leftchild;
	rightsib_ = rightsib;
    }

    GenNode leftchild() {
	return leftchild_;
    }

    GenNode rightsib() {
	return rightsib_;
    }

    Object element() {
	return element_;
    }

    void setLeftchild(GenNode leftchild) {
	leftchild_ = leftchild;
    }

    void setRightsib(GenNode rightsib) {
	rightsib_ = rightsib;
    }

    void setElement(GenNode element) {
	element_ = element;
    }
}