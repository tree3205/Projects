package cs601.webmail.managers;


public class Pagination {

    private int totalRecords;
    private int pageSize;
    private int pageNo;
    private String type;
    public String previous;
    public String next;
    public String previouspre;
    public String nextnext;
    public String pageExist;

    private static Pagination instance;
    public static Boolean debug = false;

    public static synchronized Pagination instance() {
        if( instance == null ) {
            instance = new Pagination();
        }
        return instance;
    }
    /**
     * Get total page number
     * @return
     */
    public int getTotalPages(){
        return(totalRecords+pageSize-1)/pageSize;
    }
    /**
     * Get first page
     * @return
     */
    public int getTopPageNo(){
        return 1;
    }

    /**
     * Get previous page
     * @return
     */
    public int getPreviousPageNo(){
        if(pageNo-1<getTopPageNo()){
            return Integer.MIN_VALUE;
        }
        return pageNo-1;
    }
    /**
     * Get previous page
     * @return
     */
    public int getPreviousPreviousPageNo(){
        if(pageNo-2<getTopPageNo()){
            return Integer.MIN_VALUE;
        }
        return pageNo-2;
    }
    /**
     * Get next page
     * @return
     */
    public int getNextPageNo(){
        if(pageNo+1>getBottomPageNo()){
            return Integer.MAX_VALUE;
        }
        return pageNo+1;
    }
    /**
     * Get next page
     * @return
     */
    public int getNextNextPageNo(){
        if(pageNo+2>getBottomPageNo()){
            return Integer.MAX_VALUE;
        }
        return pageNo+2;
    }
    /**
     * Get end page
     * @return
     */
    public int getBottomPageNo(){
        return getTotalPages();
    }
    /**
     * Get total records
     * @return
     */
    public int getTotalRecords() {
        return totalRecords;
    }
    /**
     * Get display records's number
     * @return
     */
    public int getPageSize() {
        return pageSize;
    }
    /**
     * Get current page
     * @return
     */
    public int getPageNo() {
        return pageNo;
    }

    public String getPrevious() {
        return previous;
    }

    public String getPreviousPre() {
        return previouspre;
    }

    public String getNext() {
        return next;
    }

    public String getNextNext() {
        return nextnext;
    }

    public String getPageExist() {
        return pageExist;
    }

    public String getType() {
        return type;
    }

    public void getPagination(int pageNum) {
        if (getTotalPages() == 0) {
            pageExist = null;
        }
        else {
            pageExist = "exist";
            // special case: only one page
            if (getTotalPages() == 1 && pageNum == 1) {
                setPageNo(1);
            }
            else if(pageNum == getBottomPageNo()){
                setPageNo(pageNum - 1);
            }
            else if(pageNum == getTopPageNo()) {
                setPageNo(pageNum+1);
            }
            else {
                setPageNo(pageNum);
            }

            if (getPreviousPageNo() == Integer.MIN_VALUE) {
                previous = null;
            } else {
                previous = "true";
            }

            if (getNextPageNo() == Integer.MAX_VALUE) {
                next = null;
            } else {
                next = "true";
            }
            if (getPreviousPreviousPageNo() == Integer.MIN_VALUE) {
                previouspre = null;
            } else {
                previouspre = "true";
            }

            if (getNextNextPageNo() == Integer.MAX_VALUE) {
                nextnext = null;
            } else {
                nextnext = "true";
            }
        }
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public void setType(String type) {
        this.type = type;
    }
}
