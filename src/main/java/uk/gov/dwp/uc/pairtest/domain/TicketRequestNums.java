package uk.gov.dwp.uc.pairtest.domain;

public class TicketRequestNums {

    private int adultTicketsNum;
    private int childTicketsNum;
    private int infantTicketsNum;
    private int totalTicketsNum;

    public TicketRequestNums() {
    }

    public TicketRequestNums(int adultTicketsNum, int childTicketsNum, int infantTicketsNum) {
        this.adultTicketsNum = adultTicketsNum;
        this.childTicketsNum = childTicketsNum;
        this.infantTicketsNum = infantTicketsNum;
    }

    public int getAdultTicketsNum() {
        return adultTicketsNum;
    }

    public void setAdultTicketsNum(int adultTicketsNum) {
        this.adultTicketsNum = adultTicketsNum;
    }

    public int getChildTicketsNum() {
        return childTicketsNum;
    }

    public void setChildTicketsNum(int childTicketsNum) {
        this.childTicketsNum = childTicketsNum;
    }

    public int getInfantTicketsNum() {
        return infantTicketsNum;
    }

    public void setInfantTicketsNum(int infantTicketsNum) {
        this.infantTicketsNum = infantTicketsNum;
    }

    public int getTotalTicketsNum() {
        return this.infantTicketsNum + this.adultTicketsNum + this.childTicketsNum;
    }

    public void setTotalTicketsNum(int totalTicketsNum) {
        this.totalTicketsNum = totalTicketsNum;
    }
}
