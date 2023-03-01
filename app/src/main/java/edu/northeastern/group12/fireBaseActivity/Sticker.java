package edu.northeastern.group12.fireBaseActivity;

public class Sticker implements Comparable<Sticker> {
    private int imageId;
    private String fromUser;
    private String toUser;
    private long sendTimeEpochSecond;
    private String receivedTimeEpochSecond;

    public Sticker(int imageId, String fromUser, String toUser, long sendTimeEpochSecond) {
        this.imageId = imageId;
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.sendTimeEpochSecond = sendTimeEpochSecond;
    }

    public Sticker() {

    }

    public String getFromUser() {
        return fromUser;
    }

    public String getToUser() {
        return toUser;
    }

    public long getSendTimeEpochSecond() {
        return sendTimeEpochSecond;
    }

    public int compareTo(Sticker other) {
        return Long.compare(this.sendTimeEpochSecond, other.getSendTimeEpochSecond());
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    @Override
    public String toString() {
        return "Sticker{" +
                "imageId=" + imageId +
                ", fromUser='" + fromUser + '\'' +
                ", toUser='" + toUser + '\'' +
                ", sendTimeEpochSecond='" + sendTimeEpochSecond + '\'' +
                ", receivedTimeEpochSecond='" + receivedTimeEpochSecond + '\'' +
                '}';
    }
}
