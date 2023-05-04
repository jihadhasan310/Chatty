package dm2e.jihad.chatty.model;

public class Messege {
    String message;
    String senderid, reciverid;
    long timeStamp;

    // importante este constructor si lo elimna da una excepcion
    public  Messege(){}



    public Messege(String message, String senderUID, String reciverUid, long timeStamp) {
        this.message = message;
        this.senderid = senderUID;
        this.reciverid = reciverUid;
        this.timeStamp = timeStamp;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getSenderid() {
        return senderid;
    }
    public String getReciverid() {
        return reciverid;
    }
    public long getTimeStamp() {
        return timeStamp;
    }
    public void setSenderid(String senderid) {
        this.senderid = senderid;
    }
    public void setReciverid(String reciverid) {
        this.reciverid = reciverid;
    }
    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

}
