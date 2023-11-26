package uk.ac.wlv.socialchat;

public class Message {
    private int id;
    private String message;
    private String imageUri;

    private boolean isSelected;

    public Message(int id, String message) {
        this.id = id;
        this.message = message;
    }

    // Getters and setters
    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

}
