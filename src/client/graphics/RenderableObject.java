package client.graphics;

import java.awt.*;

public interface RenderableObject {
    public void draw(Graphics2D p);
    public double getX();
    public double getY();
}
