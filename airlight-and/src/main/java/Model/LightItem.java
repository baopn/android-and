package Model;

public class LightItem {

    private int on_code;
    private int off_code;
    private int x;
    private int y;
    private int width;
    private int height;
    private float rotation;
    private String type;
    private boolean state;

    public LightItem(int on_code, int x, int y, float rotation)
    {
        this.on_code = on_code;
        this.x = x;
        this.y = y;
        this.rotation = rotation;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }
    public int getOn_code() {
        return on_code;
    }

    public void setOn_Code(int code) {
        this.on_code = code;
    }
    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
    public int getOff_code() {
        return off_code;
    }

    public void setOff_code(int off_code) {
        this.off_code = off_code;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}
